package silentorb.metaview.common

import silentorb.metahub.core.Graph

//val undo: CommonTransform = { state ->
//  val history = state.history
//  val future = state.future
//  if (history.size > 1) {
//    val newHistory = history.dropLast(1)
//    state.copy(
//        future = history.takeLast(1).plusBounded(future),
//        history = newHistory,
//        graph = history.last()
//    )
//  } else
//    state
//}
//
//val redo: CommonTransform = { state ->
//  val history = state.history
//  val future = state.future
//  if (future.any()) {
//    val newHistory = history.plusBounded(future.first())
//    state.copy(
//        future = future.drop(1),
//        history = newHistory,
//        graph = history.last()
//    )
//  } else
//    state
//}

data class HistoryPair(
    val history: List<Graph>,
    val future: List<Graph>
)

typealias HistoryFunction = (List<Graph>, List<Graph>) -> HistoryPair

val undo: HistoryFunction = { history, future ->
  HistoryPair(
      history = history.dropLast(1),
      future = history.takeLast(1).plus(future)
  )
}

val redo: HistoryFunction = { history, future ->
  HistoryPair(
      history = history.plus(future.first()),
      future = future.drop(1)
  )
}

fun historyFunction(f: HistoryFunction, condition: (CommonState) -> Boolean): CommonTransform = { state ->
  if (condition(state)) {
    val updated = f(state.history, state.future)
    state.copy(
        future = updated.future,
        history = updated.history,
        graph = updated.history.last()
    )
  } else
    state
}

fun updateHistory(eventType: HistoryEvent): CommonTransform =
    when (eventType) {
      HistoryEvent.undo -> historyFunction(undo) { it.history.size > 1 }
      HistoryEvent.redo -> historyFunction(redo) { it.future.any() }
    }

// This should usually be applied after any other graph changes in a state transform sequence
fun historyStateListener(maxHistory: Int): StateTransformListener<CommonState> = { change ->
  val event = change.event
  val previous = change.previous
  val eventType = event.type
  if (eventType is HistoryEvent) {
    updateHistory(eventType)
  } else { next ->
    val graph = next.graph
    if (!event.preview && !(eventType is HistoryEvent) && graph != null && graph != previous.graph) {
      // maxHistory + 1 because the current state is stored in history, taking up one slot
      // for a state that isn't actually a historical record.
      // This way, when maxHistory is set to 10 then the user can actually undo 10 times instead of 9.
      next.copy(
          history = next.history.plus(graph).take(maxHistory + 1),
          future = listOf() // Creating new history entries erases any possible forks
      )
    } else
      next
  }
}