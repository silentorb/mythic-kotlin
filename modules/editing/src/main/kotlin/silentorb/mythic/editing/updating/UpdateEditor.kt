package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.activeFieldId
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.getGraphKeys
import silentorb.mythic.ent.scenery.gatherChildren
import silentorb.mythic.ent.scenery.getNodeTransform
import silentorb.mythic.ent.scenery.nodeAttributes
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

fun <T> toggleKey(set: Set<T>, key: T): Set<T> =
    if (set.contains(key))
      set - key
    else
      set + key

val updateVisibleWidgetTypes = handleCommands<Set<String>> { command, value ->
  when (command.type) {
    EditorCommands.toggleCollisionDisplay -> toggleKey(value, WidgetTypes.collision)
    else -> value
  }
}

fun updateViewport(editor: Editor, commands: Commands, mousePosition: Vector2i, mouseOffset: Vector2, viewport: String, viewportState: ViewportState): ViewportState {
  return viewportState.copy(
      camera = updateCamera(editor, mousePosition, mouseOffset, commands, viewport, viewportState.camera),
  )
}

fun isInViewportBounds(editor: Editor, mousePosition: Vector2i, viewport: String): Boolean {
  val bounds = editor.viewportBoundsMap[viewport]
  return if (bounds == null)
    false
  else
    isInBounds(mousePosition, bounds)
}

fun mouseViewport(editor: Editor, mousePosition: Vector2i): String? =
    editor.viewportBoundsMap.keys.firstOrNull { isInViewportBounds(editor, mousePosition, it) }

fun updateSceneStates(commands: Commands, editor: Editor, graph: Graph?, mousePosition: Vector2i, mouseOffset: Vector2): SceneStates =
    if (graph == null)
      editor.persistentState.sceneStates
    else {
      val graphId = editor.persistentState.graph!!
      val viewports = (getViewports(editor) ?: defaultViewports())
          .mapValues { (key, viewport) ->
            updateViewport(editor, commands, mousePosition, mouseOffset, key, viewport)
          }

      val nextNodeSelection = updateNodeSelection(editor, graph)(commands, getNodeSelection(editor))
      val previous = editor.persistentState.sceneStates[graphId] ?: SceneState()
      val next = previous.copy(
          viewports = viewports,
          nodeSelection = nextNodeSelection,
      )
      editor.persistentState.sceneStates + (graphId to next)
    }

fun updateEditorState(commands: Commands, editor: Editor, graph: Graph?, mousePosition: Vector2i, mouseOffset: Vector2): EditorPersistentState {
  val state = editor.persistentState
  val renderingMode = updateRenderingMode(commands, getRenderingMode(editor))
  return state.copy(
      graph = onSetCommand(commands, EditorCommands.setActiveGraph, state.graph),
      sceneStates = updateSceneStates(commands, editor, graph, mousePosition, mouseOffset),
      fileSelection = updateFileSelection(commands, state.fileSelection),
      renderingModes = state.renderingModes + (defaultViewportId to renderingMode),
      visibleWidgetTypes = updateVisibleWidgetTypes(commands, state.visibleWidgetTypes),
  )
}

fun updateSelectionQuery(editor: Editor, commands: Commands): SelectionQuery? {
  val selectionCommand = commands
      .firstOrNull {
        it.type == EditorCommands.startNodeSelectReplace ||
            it.type == EditorCommands.startNodeSelectToggle ||
            it.type == EditorCommands.startNodeDrillDown
      }

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

fun getNextGraph(editor: Editor, staging: Graph?, commands: Commands): Graph? {
  val commandTypes = commands.map { it.type }
  return if (commandTypes.contains(EditorCommands.setActiveGraph)) {
    editor.graphLibrary[commands.first { it.type == EditorCommands.setActiveGraph }.value]
  } else if (commandTypes.contains(EditorCommands.commitOperation) && editor.staging != null)
    staging
  else if (!editor.graphLibrary.containsKey(editor.persistentState.graph))
    null
  else if (staging != null)
    getLatestGraph(editor)
  else {
    val activeGraph = getActiveEditorGraph(editor)
    if (activeGraph == null)
      activeGraph
    else
      updateSceneGraph(editor)(commands, activeGraph)
  }
}

fun updateMouseAction(isInBounds: Boolean, mouseAction: MouseAction): MouseAction {
  val mouse = isMouseDown(1)
  return if (mouseAction == MouseAction.none)
    if (isInBounds && mouse)
      if (isShiftDown())
        MouseAction.pan
      else
        MouseAction.orbit
    else
      MouseAction.none
  else if (mouse)
    mouseAction
  else
    MouseAction.none
}

fun onTrySelectJoint(editor: Editor, mousePosition: Vector2, commands: Commands): Commands =
    if (commands.any { it.type == EditorCommands.trySelectJoint }) {
      val camera = getEditorCamera(editor, defaultViewportId)
      val viewport = editor.viewportBoundsMap[defaultViewportId]
      if (camera != null && viewport != null) {
        val transform = getStandardPointTransform(viewport, camera)
        val graph = getCachedGraph(editor)
        val joints = nodeAttributes(graph, CommonEditorAttributes.joint)
        val hit = joints
            .firstOrNull { joint ->
              val location = getNodeTransform(graph, joint).translation()
              val point = transform(location)
              mousePosition.distance(point) < 6f
            }
        if (hit != null) {
          if (editor.selectedJoint == null)
            listOf(Command(EditorCommands.selectJoint, hit))
          else
            listOf(
                Command(EditorCommands.connectJoints, hit),
                Command(EditorCommands.commitOperation),
            )
        } else
          listOf()
      } else
        listOf()
    } else
      listOf()

fun getNextSelectedJoint(commandType: Any, commands: Commands): Key? =
    commands.firstOrNull { it.type == commandType }?.value as? Key

fun updateEditorFromCommands(previousMousePosition: Vector2, mouseOffset: Vector2, commands: Commands, editor: Editor): Editor {
  val nextStaging = updateStaging(editor, previousMousePosition, mouseOffset, commands, editor.operation)
  val nextOperation = updateOperation(commands, editor.operation)
  val nextGraph = getNextGraph(editor, nextStaging, commands)
  val mousePosition = (previousMousePosition + mouseOffset).toVector2i()
  val nextState = updateEditorState(commands, editor, nextGraph, mousePosition, mouseOffset)
  val nextHistory = updateHistory(nextGraph, getNodeSelection(nextState), nextState.graph, commands, editor.maxHistory, editor.history)
  val mouseViewport = mouseViewport(editor, mousePosition)
  val mouseAction = updateMouseAction(mouseViewport != null, editor.mouseAction)
  val mouseActionViewport = if (mouseAction == editor.mouseAction)
    editor.mouseActionViewport
  else
    mouseViewport

  val flyThrough = if (commands.any { it.type == EditorCommands.toggleFlythroughMode })
    !editor.flyThrough
  else
    editor.flyThrough

  val selectedJoint = if (editor.operation?.type != OperationType.connecting)
    null
  else
    editor.selectedJoint ?: getNextSelectedJoint(EditorCommands.selectJoint, commands)

  return editor.copy(
      persistentState = nextState,
      operation = nextOperation,
      staging = nextStaging,
      graphLibrary = updateSceneCaching(editor),
      viewportBoundsMap = onSetCommand(commands, EditorCommands.setViewportBounds, editor.viewportBoundsMap),
      fileItems = updateProject(commands, editor),
      clipboard = updateClipboard(editor)(commands, editor.clipboard),
      history = nextHistory,
      selectionQuery = updateSelectionQuery(editor, commands),
      flyThrough = flyThrough,
      mouseActionViewport = mouseActionViewport,
      mouseAction = mouseAction,
      selectedJoint = selectedJoint,
      previousActiveField = activeFieldId,
  )
}

fun getQuerySelectionCommands(editor: Editor): Commands {
  val request = editor.selectionQuery
  val response = request?.response
  return if (response != null) {
    val node = response.selectedObject
    when (request.command?.type) {
      EditorCommands.startNodeDrillDown -> listOf(Command(EditorCommands.setActiveGraph, node))
      EditorCommands.startNodeSelectReplace -> listOf(Command(EditorCommands.setNodeSelection, setOfNotNull(node)))
      EditorCommands.startNodeSelectToggle -> {
        if (node == null)
          listOf()
        else {
          val selection = getNodeSelection(editor)
          val nextSelection = if (selection.contains(node))
            selection - node
          else
            selection + node
          listOf(Command(EditorCommands.setNodeSelection, nextSelection))
        }
      }
      else -> listOf()
    }
  } else
    listOf()
}

fun prepareEditorUpdate(deviceStates: List<InputDeviceState>, editor: Editor): Commands {
//  val externalCommands = if (isCtrlDown() || isAltDown() || isShiftDown())
//    listOf()
//  else
//    mapCommands(defaultEditorBindings(), deviceStates) + getQuerySelectionCommands(editor)

  val externalCommands = mapCommands(defaultEditorBindings(), deviceStates) + getQuerySelectionCommands(editor)

  val guiCommands = defineEditorGui(editor, deviceStates)
  return if (editor.flyThrough)
    externalCommands + guiCommands
        .filter { it.type == EditorCommands.toggleFlythroughMode }
  else
    externalCommands + guiCommands
}

fun updateEditor(deviceStates: List<InputDeviceState>, commands: Commands, editor: Editor): Editor {
  return if (isImGuiFieldActive() && editor.previousActiveField == activeFieldId)//!isTabPressed(deviceStates.last()))
    editor.copy(
        previousActiveField = activeFieldId,
    )
  else {
    val previousMousePosition = deviceStates.dropLast(1).last().mousePosition
    val mouseOffset = getMouseOffset(deviceStates)
    val additionalFlythroughCommands = if (editor.flyThrough)
      flyThroughModeCommands(deviceStates)
    else
      listOf()

    val jointSelectCommands = onTrySelectJoint(editor, deviceStates.last().mousePosition, commands)
    val finalCommands = commands + additionalFlythroughCommands + jointSelectCommands
    updateEditorFromCommands(previousMousePosition, mouseOffset, finalCommands, editor)
  }
}
