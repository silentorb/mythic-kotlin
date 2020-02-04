package silentorb.metaview.common

import silentorb.metahub.core.Engine
import silentorb.mythic.ent.pass

data class StateTransformChange<T>(
    val previous: T,
    val event: Event
)

data class StateChange<T>(
    val previous: T,
    val next: T,
    val event: Event
)

// Modifies immutable state based on events
typealias StateTransformListener<T> = (StateTransformChange<T>) -> (T) -> T

// Modifies mutable state based on events
typealias SideEffectStateListener<T> = (StateChange<T>) -> Unit

fun onGraphChanged(nodeDefinitions: NodeDefinitionMap, defaultValues: ValueMap, engine: Engine): StateTransformListener<CommonState> = { change ->
  val previous = change.previous
  { state ->
    if (state.graph != null && (state.graph != previous.graph || change.event.type == CommonEvent.refresh)) {
      val values = executeSanitized(nodeDefinitions, defaultValues, engine, state.graph)
      state.copy(
          outputValues = values
      )
    } else
      state
  }
}

fun <T> stateTransformListener(transform: (T) -> T): StateTransformListener<T> = { change ->
  { state ->
    transform(state)
  }
}

inline fun <reified EventType, T> eventTypeSwitch(crossinline transform: (EventType, Any) -> (T) -> T): StateTransformListener<T> = { change ->
  val eventType = change.event.type
  if (eventType is EventType)
    transform(eventType, change.event.data)
  else
    ::pass
}

fun <A, B> wrapStateListener(get: (A) -> B, set: (A, B) -> A): (StateTransformListener<B>) -> StateTransformListener<A> = { listener ->
  { change ->
    { state ->
      val commonChange = StateTransformChange(
          previous = get(change.previous),
          event = change.event
      )
      set(state, listener(commonChange)(get(state)))
    }
  }
}

fun <A, B> wrapSideEffectListener(get: (A) -> B): (SideEffectStateListener<B>) -> SideEffectStateListener<A> = { listener ->
  { change ->
    val commonChange = StateChange(
        next = get(change.next),
        previous = get(change.previous),
        event = change.event
    )
    listener(commonChange)
  }
}
