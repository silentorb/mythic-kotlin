package silentorb.metaview.common

import javafx.application.Platform
import silentorb.mythic.ent.pass
import silentorb.mythic.ent.pipe

fun <T> appLogic(transformListeners: List<StateTransformListener<T>>,
                 sideEffectListeners: List<SideEffectStateListener<T>>,
                 initialState: T): Pair<(Event) -> Unit, () -> T> {
  var state = initialState

  val emit: (Event) -> Unit = { event ->
    Platform.runLater {
      val previousState = state
//      val focus = getFocus(root)
//      val newState = updateAppState(engine, nodeDefinitions, commonUpdater, historyUpdater, getFocus(root), event)(state)
//      val nextState = stateUpdater(previousState, event)
//      val graph = newState.common.graph
      val transformChange = StateTransformChange(
          previous = previousState,
          event = event
      )
      val transforms = transformListeners.map { it(transformChange) }
      state = pipe(transforms)(state)

      val change = StateChange(
          previous = previousState,
          next = state,
          event = event
      )

      sideEffectListeners.forEach { it(change) }

      /*
      val values = if (graph != null) {
        val defaultValues = fillerTypeValues(textureLength)
        executeSanitized(nodeDefinitions, defaultValues, engine, graph)
      } else
        mapOf()
      updateGraphView(newState.common, values)
      updatePreviewView(newState.common, values)
      if (newState.common.graphNames.size != previousState.common.graphNames.size || newState.domain != previousState.domain)
        updateTextureListView(newState.common)

      if (!event.preview) {
        state = newState
        if (state.common.graph != null && ((state.common.gui.activeGraph != null && state.common.gui.activeGraph == previousState.common.gui.activeGraph) || (state.common.graph != previousState.common.graph && previousState.common.graph != null))) {
          saveJsonFile(texturePath(state.common, state.common.gui.activeGraph!!), state.common.graph!!)
        }

        if (state.common.gui != previousState.common.gui || state.texturing != previousState.texturing) {
          saveConfig(state)
        }

        if (state.common.gui.activeGraph != previousState.common.gui.activeGraph || state.common.gui.graphInteraction.nodeSelection != previousState.common.gui.graphInteraction.nodeSelection) {
          updatePropertiesView(state.common)
        }

        if (event.type != HistoryEvent.undo && event.type != HistoryEvent.redo && newState.common.graph != previousState.common.graph) {
          // maxHistory + 1 because the current state is stored in history, taking up one slot for a state that isn't actually a historical record
          // This way, when maxHistory is set to 10 then the user can actually undo 10 times instead of 9.
          history = history.plusBounded(newState).take(maxHistory + 1)
          future = listOf() // Creating new history entries erases any possible forks
        }
      }
      */
    }
  }

  return Pair(emit, { state })
}

//fun <T> notPreview(listener: SideEffectStateListener<T>): SideEffectStateListener<T> = { change ->
//  if (!change.event.preview)
//    listener(change)
//}

fun <T> notPreview(listener: StateTransformListener<T>): StateTransformListener<T> = { change ->
  if (!change.event.preview)
    listener(change)
  else
    ::pass
}
