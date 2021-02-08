package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.Graph
import silentorb.mythic.happenings.Commands

fun undoEdit(history: EditHistory): EditHistory {
  val (pastAndPresent, future) = history

  return if (pastAndPresent.size < 2)
    history
  else
    EditHistory(
        pastAndPresent = pastAndPresent.dropLast(1),
        future = pastAndPresent.takeLast(1) + future,
    )
}

fun redoEdit(history: EditHistory): EditHistory {
  val (pastAndPresent, future) = history

  return if (future.none())
    history
  else
    EditHistory(
        pastAndPresent = pastAndPresent + future.take(1),
        future = future.drop(1),
    )
}

fun appendHistory(graph: Graph, nodeSelection: NodeSelection, maxDepth: Int, history: EditHistory): EditHistory =
    EditHistory(
        pastAndPresent = history.pastAndPresent
            .plus(
                Snapshot(
                    graph = graph.toSet(),
                    nodeSelection = nodeSelection,
                )
            )
            .takeLast(maxDepth),
        future = listOf(),
    )

fun shouldAppendHistory(nextGraph: Graph, nodeSelection: NodeSelection, history: EditHistory): Boolean {
  val pastAndPresent = history.pastAndPresent
  val last = pastAndPresent.lastOrNull()
  if (last?.graph != nextGraph)
    return true

  val nextToLast = pastAndPresent.dropLast(1).lastOrNull()

  // Append just a node selection change as long as the previous change was not just a node selection change
  return nodeSelection != last.nodeSelection && nextToLast?.graph != last.graph
}

fun updateHistory(
    nextGraph: Graph,
    nodeSelection: NodeSelection,
    commands: Commands,
    maxDepth: Int,
    history: EditHistory
): EditHistory =
    when {
      commands.any { it.type == EditorCommands.undo } -> undoEdit(history)
      commands.any { it.type == EditorCommands.redo } -> redoEdit(history)
      shouldAppendHistory(nextGraph, nodeSelection, history) ->
        appendHistory(nextGraph, nodeSelection, maxDepth, history)

      else -> history
    }

fun updateHistory(
    nextGraph: Graph?,
    nodeSelection: NodeSelection,
    graphId: Key?,
    commands: Commands,
    maxDepth: Int,
    history: HistoryMap
): HistoryMap =
    if (nextGraph == null || graphId == null)
      history
    else {
      val previousHistory = history[graphId] ?: EditHistory()
      val nextHistory = updateHistory(nextGraph, nodeSelection, commands, maxDepth, previousHistory)
      history.plus(graphId to nextHistory)
    }
