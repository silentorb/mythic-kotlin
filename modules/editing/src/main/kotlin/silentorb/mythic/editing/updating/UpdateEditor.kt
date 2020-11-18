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

fun updateNodeSelection(editor: Editor, nextGraph: Graph?) = handleCommands<NodeSelection> { command, selection ->
  val graph = editor.graph
  if (graph != null && nextGraph != null) {
    when (command.type) {
      EditorCommands.setNodeSelection -> command.value as NodeSelection
      EditorCommands.addNode, EditorCommands.renameNode, EditorCommands.duplicateNode, EditorCommands.pasteNode -> {
        val newNodes = getGraphKeys(nextGraph) - getGraphKeys(graph)
        if (newNodes.any())
          newNodes
        else
          selection
      }

      EditorCommands.deleteNode -> {
        val deletedNodes = getGraphKeys(graph) - getGraphKeys(nextGraph)
        if (deletedNodes.any()) {
          val firstRemainingParent = graph.firstOrNull {
            deletedNodes.contains(it.source) && it.property == SceneProperties.parent && !deletedNodes.contains(it.target)
          }?.target as String?
          if (firstRemainingParent != null)
            setOf(firstRemainingParent)
          else
            selection
        } else
          selection
      }

      else -> selection
    }
  } else
    selection
}

val updateFileSelection = handleCommands<NodeSelection> { command, selection ->
  when (command.type) {
    EditorCommands.setFileSelection -> command.value as NodeSelection
    else -> selection
  }
}

fun gatherSelectionHierarchy(graph: Graph, selection: NodeSelection): Graph {
  val selectionAndChildren = gatherChildren(graph, selection)
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
      val selection = editor.state.nodeSelection
      val graph = getActiveEditorGraph(editor)
      if (graph == null)
        clipboard
      else {
        gatherSelectionHierarchy(graph, selection)
      }
    }
    else -> clipboard
  }
}

fun updateEditorState(commands: Commands, editor: Editor, graph: Graph?, mouseOffset: Vector2): EditorState {
  val state = editor.state
  val cameras = state.cameras
      .mapValues { (_, camera) ->
        updateCamera(editor, mouseOffset, commands, camera)
      }

  val nextNodeSelection = updateNodeSelection(editor, graph)(commands, state.nodeSelection)

  return state.copy(
      graph = onSetCommand(commands, EditorCommands.setActiveGraph, state.graph),
      cameras = cameras,
      nodeSelection = nextNodeSelection,
      fileSelection = updateFileSelection(commands, state.fileSelection)
  )
}

fun updateSelectionQuery(editor: Editor, commands: Commands): SelectionQuery? {
  val selectionCommand = commands.firstOrNull { it.type == EditorCommands.startNodeSelect }
  val previousSelectionQuery = editor.selectionQuery
  return if (selectionCommand != null && commands.none { it.type == EditorCommands.commitOperation })
    SelectionQuery(
        position = selectionCommand.value as Vector2i
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
    editor.graph
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
  val nextState = updateEditorState(commands, editor, nextGraph, mouseOffset)

  return editor.copy(
      state = nextState,
      operation = nextOperation,
      staging = nextStaging,
      graph = nextGraph,
      graphLibrary = updateSceneCaching(editor),
      viewportBoundsMap = onSetCommand(commands, EditorCommands.setViewportBounds, editor.viewportBoundsMap),
      fileItems = updateProject(commands, editor),
      clipboard = updateClipboard(editor)(commands, editor.clipboard),
//      history = appendHistory(editor.history, nextGraph),
      selectionQuery = updateSelectionQuery(editor, commands),
  )
}

fun getQuerySelectionCommands(editor: Editor): Commands {
  val queryResponse = editor.selectionQuery?.response
  return if (queryResponse != null)
    listOf(Command(EditorCommands.setNodeSelection, setOfNotNull(queryResponse.selectedObject)))
  else
    listOf()
}

fun updateEditor(deviceStates: List<InputDeviceState>, editor: Editor): Editor {
  val externalCommands = if (isCtrlDown() || isAltDown() || isShiftDown())
    listOf()
  else
    mapCommands(defaultEditorBindings(), deviceStates) + getQuerySelectionCommands(editor)

  val guiCommands = defineEditorGui(editor)
  return if (isImGuiFieldActive())
    editor
  else {
    val commands = externalCommands + guiCommands
    val previousMousePosition = deviceStates.dropLast(1).last().mousePosition
    updateEditorFromCommands(previousMousePosition, getMouseOffset(deviceStates), commands, editor)
  }
}
