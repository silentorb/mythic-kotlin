package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.nameText
import silentorb.mythic.happenings.Commands

fun onGraphEditingCommand(commandType: Any, transform: EditorGraphTransform): GraphEditCommandsHandler =
    { editor, commandTypes, graph ->
      if (commandTypes.contains(commandType))
        transform(editor, graph)
      else
        graph
    }


fun uniqueNodeName(graph: Graph, name: String): String {
  val keys = getTripleKeys(graph)
  return if (!keys.contains(name))
    name
  else {
    val numberPattern = Regex("^\\d+$")
    val takenNumbers = keys
        .mapNotNull { id ->
          if (id.substring(0, name.length) == name) {
            val numberText = id.substring(name.length)
            if (numberText.matches(numberPattern))
              numberText.toInt()
            else
              null
          } else
            null
        }
    val number = (takenNumbers.maxOrNull() ?: 0) + 1
    "$name$number"
  }
}

val onAddNode = onGraphEditingCommand(EditorCommands.addNode) { editor, graph ->
  val state = editor.state
  if (state.nodeSelection.size != 1)
    graph
  else {
    val selected = state.nodeSelection.first()
    val key = nameText.get()
    graph + Entry(key, Properties.parent, selected)
  }
}

val onRenameNode = onGraphEditingCommand(EditorCommands.renameNode) { editor, graph ->
  val state = editor.state
  if (state.nodeSelection.size != 1)
    graph
  else {
    val selected = state.nodeSelection.first()
    val key = nameText.get()
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
  val selection = state.nodeSelection
  val selectionAndChildren = gatherChildren(graph, selection)
  graph.filter { !selectionAndChildren.contains(it.source) }
}

fun updateSceneGraph(commands: Commands, editor: Editor): Graph? {
  val commandTypes = commands.map { it.type }
  val initialGraph = getActiveEditorGraph(editor)
  return if (initialGraph == null)
    null
  else {
    val graph2 = listOf(
        onAddNode,
        onDeleteNode,
        onRenameNode,
    )
        .fold(initialGraph) { graph, handler ->
          handler(editor, commandTypes, graph)
        }

    val graphChanges = commands
        .filter { it.type == EditorCommands.setGraphValue }
        .map { it.value as Entry }

    if (graphChanges.any())
      replaceValues(graph2, graphChanges)
    else
      graph2
  }
}

const val maxGraphHistory = 30

fun appendHistory(history: List<Graph>, graph: Graph?): GraphHistory =
    if (graph != null && graph != history.last())
      history.takeLast(maxGraphHistory).plusElement(graph)
    else
      history
