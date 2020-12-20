package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.getGraphKeys
import silentorb.mythic.ent.scenery.gatherChildren
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.haft.getMouseOffset
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.happenings.handleCommands
import silentorb.mythic.happenings.onSetCommand
import silentorb.mythic.scenery.SceneProperties
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.toVector2i

val updateFileSelection = handleCommands<NodeSelection> { command, selection ->
  when (command.type) {
    EditorCommands.setFileSelection -> command.value as NodeSelection
    else -> selection
  }
}

fun gatherSelectionHierarchy(graph: Graph, selection: NodeSelection): Graph {
  val selectionAndChildren = selection + gatherChildren(graph, selection)
  return graph
      .filter {
        selectionAndChildren.contains(it.source) &&
            !(it.property == SceneProperties.parent && !selection.contains(it.target))
      }
      .toSet()
}

fun updateClipboard(editor: Editor) = handleCommands<Graph?> { command, clipboard ->
  when (command.type) {
    EditorCommands.copyNode -> {
      val selection = getNodeSelection(editor)
      val graph = getActiveEditorGraph(editor)
      if (graph == null)
        clipboard
      else {
        val duplicated = gatherSelectionHierarchy(graph, selection)
        val keys = getGraphKeys(duplicated)
        val oldParentReferences = duplicated
            .filter { entry -> entry.property == SceneProperties.parent && !keys.contains(entry.target) }

        duplicated - oldParentReferences
      }
    }
    else -> clipboard
  }
}

val updateRenderingMode = handleCommands<RenderingMode> { command, renderingMode ->
  when (command.type) {
    EditorCommands.renderingModeFlat -> RenderingMode.flat
    EditorCommands.renderingModeLit -> RenderingMode.lit
    EditorCommands.renderingModeWireframe -> RenderingMode.wireframe
    else -> renderingMode
  }
}

fun updateViewport(editor: Editor, commands: Commands, mouseOffset: Vector2, viewport: ViewportState, isInBounds: Boolean): ViewportState {
  return viewport.copy(
      camera = updateCamera(editor, mouseOffset, commands, viewport.camera, isInBounds),
      renderingMode = updateRenderingMode(commands, viewport.renderingMode)
  )
}

fun updateSceneStates(commands: Commands, editor: Editor, graph: Graph?, mousePosition: Vector2i, mouseOffset: Vector2): SceneStates =
    if (graph == null)
      editor.state.sceneStates
    else {
      val graphId = editor.state.graph!!
      val viewports = (getViewports(editor) ?: defaultViewports())
          .mapValues { (key, viewport) ->
            val bounds = editor.viewportBoundsMap[key]
            val isInBounds = if (bounds == null)
              false
            else
              isInBounds(mousePosition, bounds)

            updateViewport(editor, commands, mouseOffset, viewport, isInBounds)
          }

      val nextNodeSelection = updateNodeSelection(editor, graph)(commands, getNodeSelection(editor))
      val previous = editor.state.sceneStates[graphId] ?: SceneState()
      val next = previous.copy(
          viewports = viewports,
          nodeSelection = nextNodeSelection,
      )
      editor.state.sceneStates + (graphId to next)
    }

fun updateEditorState(commands: Commands, editor: Editor, graph: Graph?, mousePosition: Vector2i, mouseOffset: Vector2): EditorState {
  val state = editor.state
  return state.copy(
      graph = onSetCommand(commands, EditorCommands.setActiveGraph, state.graph),
      sceneStates = updateSceneStates(commands, editor, graph, mousePosition, mouseOffset),
      fileSelection = updateFileSelection(commands, state.fileSelection)
  )
}

fun updateSelectionQuery(editor: Editor, commands: Commands): SelectionQuery? {
  val selectionCommand = commands
      .firstOrNull { it.type == EditorCommands.startNodeSelect || it.type == EditorCommands.startNodeDrillDown }

  val previousSelectionQuery = editor.selectionQuery
  return if (selectionCommand != null && commands.none { it.type == EditorCommands.commitOperation })
    SelectionQuery(
        position = selectionCommand.value as Vector2i,
        command = selectionCommand
    )
  else if (previousSelectionQuery != null && previousSelectionQuery.response == null)
    previousSelectionQuery // Still waiting for a response from the rendering code
  else
    null
}

fun getNextGraph(editor: Editor, commands: Commands): Graph? {
  val commandTypes = commands.map { it.type }
  return if (commandTypes.contains(EditorCommands.setActiveGraph)) {
    editor.graphLibrary[commands.first { it.type == EditorCommands.setActiveGraph }.value]
  } else if (commandTypes.contains(EditorCommands.commitOperation) && editor.staging != null)
    editor.staging
  else if (!editor.graphLibrary.containsKey(editor.state.graph))
    null
  else if (editor.staging != null)
    getLatestGraph(editor)
  else {
    val activeGraph = getActiveEditorGraph(editor)
    if (activeGraph == null)
      activeGraph
    else
      updateSceneGraph(editor)(commands, activeGraph)
  }
}

fun updateEditorFromCommands(previousMousePosition: Vector2, mouseOffset: Vector2, commands: Commands, editor: Editor): Editor {
  val commandTypes = commands.map { it.type }
  val nextGraph = getNextGraph(editor, commands)
  val nextOperation = updateOperation(commandTypes, editor)
  val nextStaging = updateStaging(editor, previousMousePosition, mouseOffset, commandTypes, nextOperation)
  val mousePosition = (previousMousePosition + mouseOffset).toVector2i()
  val nextState = updateEditorState(commands, editor, nextGraph, mousePosition, mouseOffset)
  val nextHistory = updateHistory(nextGraph, getNodeSelection(nextState), nextState.graph, commands, editor.maxHistory, editor.history)

  return editor.copy(
      state = nextState,
      operation = nextOperation,
      staging = nextStaging,
      graphLibrary = updateSceneCaching(editor),
      viewportBoundsMap = onSetCommand(commands, EditorCommands.setViewportBounds, editor.viewportBoundsMap),
      fileItems = updateProject(commands, editor),
      clipboard = updateClipboard(editor)(commands, editor.clipboard),
      history = nextHistory,
      selectionQuery = updateSelectionQuery(editor, commands),
  )
}

fun getQuerySelectionCommands(editor: Editor): Commands {
  val request = editor.selectionQuery
  val response = request?.response
  return if (request != null && response != null)
    if (request.command?.type == EditorCommands.startNodeDrillDown)
      listOf(Command(EditorCommands.setActiveGraph, response.selectedObject))
    else
      listOf(Command(EditorCommands.setNodeSelection, setOfNotNull(response.selectedObject)))
  else
    listOf()
}

fun prepareEditorUpdate(deviceStates: List<InputDeviceState>, editor: Editor): Commands {
  val externalCommands = if (isCtrlDown() || isAltDown() || isShiftDown())
    listOf()
  else
    mapCommands(defaultEditorBindings(), deviceStates) + getQuerySelectionCommands(editor)

  val guiCommands = defineEditorGui(editor, deviceStates)
  return externalCommands + guiCommands
}

fun updateEditor(deviceStates: List<InputDeviceState>, commands: Commands, editor: Editor): Editor {
  return if (isImGuiFieldActive())
    editor
  else {
    val previousMousePosition = deviceStates.dropLast(1).last().mousePosition
    updateEditorFromCommands(previousMousePosition, getMouseOffset(deviceStates), commands, editor)
  }
}
