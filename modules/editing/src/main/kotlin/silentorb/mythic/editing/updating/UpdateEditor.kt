package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.haft.getMouseOffset
import silentorb.mythic.happenings.Commands
import silentorb.mythic.happenings.handleCommands
import silentorb.mythic.spatial.Vector2

//fun incorporateGraphIntoLibrary(editor: Editor, nextGraph: Graph?): GraphLibrary {
//  val graphId = getActiveEditorGraphId(editor)
//  return if (graphId != null && nextGraph != null)
//    editor.graphLibrary + (graphId to nextGraph)
//  else
//    editor.graphLibrary
//}

//fun updateGraphLibrary(commandTypes: List<Any>, editor: Editor): GraphLibrary {
//  val nextGraph = updateSceneGraph(commandTypes, editor)
//  return incorporateGraphIntoLibrary(editor, nextGraph)
//}

fun updateNodeSelection(editor: Editor, nextGraph: Graph?) = handleCommands<NodeSelection> { command, selection ->
  val graph = editor.graph
  if (graph != null && nextGraph != null) {
    when (command.type) {
      EditorCommands.setNodeSelection -> command.value as NodeSelection
      EditorCommands.addNode, EditorCommands.renameNode -> {
        val newNodes = getTripleKeys(nextGraph) - getTripleKeys(graph)
        if (newNodes.any())
          newNodes
        else
          selection
      }

      EditorCommands.deleteNode -> {
        val deletedNodes = getTripleKeys(graph) - getTripleKeys(nextGraph)
        if (deletedNodes.any()) {
          val firstRemainingParent = graph.firstOrNull {
            deletedNodes.contains(it.source) && it.property == Properties.parent && !deletedNodes.contains(it.target)
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

fun updateEditorState(commands: Commands, editor: Editor, graph: Graph?, mouseOffset: Vector2): EditorState {
  val state = editor.state
  val cameras = state.cameras
      .mapValues { (_, camera) ->
        updateCamera(editor, mouseOffset, commands, camera)
      }

  val nextNodeSelection = updateNodeSelection(editor, graph)(commands, state.nodeSelection)
  val setGraphCommand = commands.firstOrNull { it.type == EditorCommands.setActiveGraph }
  val nextGraph = if (setGraphCommand != null)
    setGraphCommand.value as Id
  else
    state.graph

  return state.copy(
      graph = nextGraph,
      cameras = cameras,
      nodeSelection = nextNodeSelection,
      fileSelection = updateFileSelection(commands, state.fileSelection)
  )
}

fun updateEditorFromCommands(previousMousePosition: Vector2, mouseOffset: Vector2, commands: Commands, editor: Editor): Editor {
  val commandTypes = commands.map { it.type }
  val nextGraph = if (commandTypes.contains(EditorCommands.commitOperation) && editor.staging != null)
    editor.staging
  else if (!editor.graphLibrary.containsKey(editor.state.graph))
    null
  else if (editor.staging != null)
    editor.graph
  else
    updateSceneGraph(commands, editor)

  val nextOperation = updateOperation(commandTypes, editor)
  val nextStaging = updateStaging(editor, previousMousePosition, mouseOffset, commandTypes, nextOperation)
  val nextState = updateEditorState(commands, editor, nextGraph, mouseOffset)
  return editor.copy(
      state = nextState,
      operation = nextOperation,
      staging = nextStaging,
      graph = nextGraph,
      graphLibrary = updateSceneCaching(editor),
//      history = appendHistory(editor.history, nextGraph),
  )
}

fun updateEditor(deviceStates: List<InputDeviceState>, editor: Editor): Editor {
  val externalCommands = mapCommands(defaultEditorBindings(), deviceStates)
  val (nextEditor, guiCommands) = defineEditorGui(editor)
  val commands = externalCommands + guiCommands
  val previousMousePosition = deviceStates.dropLast(1).last().mousePosition
  return updateEditorFromCommands(previousMousePosition, getMouseOffset(deviceStates), commands, nextEditor)
}
