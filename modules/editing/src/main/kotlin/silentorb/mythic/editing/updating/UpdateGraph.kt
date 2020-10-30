package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.nodeNameText

typealias GraphEditHandler = (Editor, List<Any>, Graph) -> Graph

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

val onAddNode: GraphEditHandler = { editor, commandTypes, graph ->
  val state = editor.state
  if (commandTypes.contains(EditorCommands.addNode)) {
    if (state.selection.size != 1)
      graph
    else {
      val selected = state.selection.first()
      val key = nodeNameText.get()
      graph + Entry(key, Properties.parent, selected)
    }
  } else
    graph
}

val onDeleteNode: GraphEditHandler = { editor, commandTypes, graph ->
  val state = editor.state
  val selection = state.selection
  if (commandTypes.contains(EditorCommands.deleteNode))
    graph.filter { !selection.contains(it.source) }
  else
    graph
}

fun updateSceneGraph(commandTypes: List<Any>, editor: Editor): Graph? {
  val initialGraph = getActiveEditorGraph(editor)
  return if (initialGraph == null)
    null
  else {
    listOf(
        onAddNode,
        onDeleteNode,
    )
        .fold(initialGraph) { graph, handler ->
          handler(editor, commandTypes, graph)
        }
  }
}
