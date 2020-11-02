package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.haft.getMouseOffset
import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Vector2

fun incorporateGraphIntoLibrary(editor: Editor, nextGraph: Graph?): GraphLibrary {
  val graphId = getActiveEditorGraphId(editor)
  return if (graphId != null && nextGraph != null)
    editor.graphLibrary + (graphId to nextGraph)
  else
    editor.graphLibrary
}

//fun updateGraphLibrary(commandTypes: List<Any>, editor: Editor): GraphLibrary {
//  val nextGraph = updateSceneGraph(commandTypes, editor)
//  return incorporateGraphIntoLibrary(editor, nextGraph)
//}

fun updateSelection(commandTypes: List<Any>, editor: Editor, nextGraph: Graph?): NodeSelection {
  val selection = editor.state.selection
  val graph = editor.graph
  return if (graph != null && nextGraph != null) {
    when {

      commandTypes.contains(EditorCommands.addNode) || commandTypes.contains(EditorCommands.renameNode) -> {
        val newNodes = getTripleKeys(nextGraph) - getTripleKeys(graph)
        if (newNodes.any())
          newNodes
        else
          selection
      }

      commandTypes.contains(EditorCommands.deleteNode) -> {
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

fun updateEditorFromCommands(mouseOffset: Vector2, commands: Commands, editor: Editor): Editor {
  val commandTypes = commands.map { it.type }
  val cameras = editor.state.cameras
      .mapValues { (_, camera) ->
        updateFlyThroughCamera(mouseOffset, commands, camera)
      }

  val nextGraph = if (commandTypes.contains(EditorCommands.commitOperation) && editor.staging != null)
    editor.staging
  else
    updateSceneGraph(commandTypes, editor)

  val nextSelection = updateSelection(commandTypes, editor, nextGraph)
  val nextOperation = updateOperation(commandTypes, editor)
  val nextStaging = updateStaging(editor, mouseOffset, commandTypes, nextOperation)
  return editor.copy(
      state = editor.state.copy(
          cameras = cameras,
          selection = nextSelection,
      ),
      operation = nextOperation,
      staging = nextStaging,
      graph = nextGraph,
//      history = appendHistory(editor.history, nextGraph),
  )
}

fun updateEditor(deviceStates: List<InputDeviceState>, editor: Editor): Editor {
  val externalCommands = mapCommands(defaultEditorBindings(), deviceStates)
  val (nextEditor, guiCommands) = defineEditorGui(editor)
  val commands = externalCommands + guiCommands
  return updateEditorFromCommands(getMouseOffset(deviceStates), commands, nextEditor)
}
