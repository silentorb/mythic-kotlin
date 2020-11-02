package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.nodeNameText

fun onGraphEditingCommand(commandType: Any, transform: EditorGraphTransform): GraphEditCommandsHandler =
    { editor, commandTypes, graph ->
      if (commandTypes.contains(commandType))
        transform(editor, graph)
      else
        graph
    }

val defaultNodeNamePattern = Regex("^node(\\d+)$")

fun newNodeKey(graph: Graph): String {
  val keys = getTripleKeys(graph)
  val takenNumbers = keys
      .mapNotNull {
        defaultNodeNamePattern.matchEntire(it)
      }
      .map { it.groupValues[1].toInt() }
  val number = (takenNumbers.maxOrNull() ?: 0) + 1
  return "node$number"
}

val onAddNode = onGraphEditingCommand(EditorCommands.addNode) { editor, graph ->
  val state = editor.state
  if (state.selection.size != 1)
    graph
  else {
    val selected = state.selection.first()
    val key = nodeNameText.get()
    graph + Entry(key, Properties.parent, selected)
  }
}

val onRenameNode = onGraphEditingCommand(EditorCommands.renameNode) { editor, graph ->
  val state = editor.state
  if (state.selection.size != 1)
    graph
  else {
    val selected = state.selection.first()
    val key = nodeNameText.get()
    graph.map {
      if (it.source == selected)
        it.copy(source = key)
      else if (it.target == selected)
        it.copy(target = key)
      else
        it
    }
  }
}

val onDeleteNode = onGraphEditingCommand(EditorCommands.deleteNode) { editor, graph ->
  val state = editor.state
  val selection = state.selection
  graph.filter { !selection.contains(it.source) }
}

fun updateSceneGraph(commandTypes: List<Any>, editor: Editor): Graph? {
  val initialGraph = editor.graph ?: getActiveEditorGraph(editor)
  return if (initialGraph == null)
    null
  else {
    listOf(
        onAddNode,
        onDeleteNode,
        onRenameNode,
    )
        .fold(initialGraph) { graph, handler ->
          handler(editor, commandTypes, graph)
        }
  }
}
