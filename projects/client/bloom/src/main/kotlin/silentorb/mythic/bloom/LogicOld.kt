package silentorb.mythic.bloom

import silentorb.mythic.bloom.next.*
import silentorb.mythic.spatial.Vector2i

data class BloomState(
    val resourceBag: StateBag,
    val input: InputState
)

data class HistoricalBloomState(
    val resourceBag: StateBag,
    val input: HistoricalInputState
)

data class LogicBundle(
    val state: HistoricalBloomState,
    val bounds: Bounds,
    val visibleBounds: Bounds? // Null means the box is completely clipped and not visible
)

typealias LogicModuleOld = (LogicBundle) -> StateBagMods

typealias LogicModuleTransform = (LogicModuleOld) -> LogicModuleOld

fun logicWrapper(wrapper: (LogicBundle, StateBagMods) -> StateBagMods): LogicModuleTransform = { logicModule ->
  { bundle ->
    val result = logicModule(bundle)
    wrapper(bundle, result)
  }
}

fun visibleBounds(box: Box): Bounds? =
    box.bounds

fun gatherLogicBoxes(box: Box): List<Box> {
  val localList = if (box.logic != null)
    listOf(box)
  else
    listOf()

  return localList.plus(box.boxes.flatMap { gatherLogicBoxes(it) })
}

fun updateStateBag(rootBox: Box, state: HistoricalBloomState): StateBag {
  val logicBoxes = gatherLogicBoxes(rootBox)
  val active = logicBoxes.mapNotNull { box ->
    box.logic!!(LogicBundle(state, box.bounds, visibleBounds(box)))
  }
  if (active.any()) {
    val k = 0
  }
  val result = active
      .flatMap { it.entries }
      .associate { it.toPair() }
  return result
}

fun updateBloomState(logic: LogicModule, box: Box, previousState: BloomState,
                     currentInput: InputState): Pair<BloomState, List<AnyEvent>> {
  val inputState = HistoricalInputState(
      previous = previousState.input,
      current = currentInput
  )
  val historicalState = HistoricalBloomState(
      input = inputState,
      resourceBag = previousState.resourceBag
  )

  val newBag = updateStateBag(box, historicalState)
  val events = gatherBoxEvents(inputState, box)

  val args = LogicArgs(
      inputState = inputState,
      events = events,
      bag = previousState.resourceBag
  )

  val secondBag = logic(args)

  return Pair(BloomState(
      input = currentInput,
      resourceBag = newBag.plus(secondBag)
  ), events)
}
