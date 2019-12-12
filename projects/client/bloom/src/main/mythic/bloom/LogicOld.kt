package mythic.bloom

import mythic.bloom.next.*
import org.joml.Vector2i

data class BloomState(
    val bag: StateBag,
    val input: InputState
)

data class HistoricalBloomState(
    val bag: StateBag,
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

fun updateBloomState(modules: List<LogicModule>, box: Box, previousState: BloomState,
                     currentInput: InputState): Pair<BloomState, List<AnyEvent>> {
  val inputState = HistoricalInputState(
      previous = previousState.input,
      current = currentInput
  )
  val historicalState = HistoricalBloomState(
      input = inputState,
      bag = previousState.bag
  )

  val newBag = updateStateBag(box, historicalState)
  val events = gatherBoxEvents(inputState, box)

  val args = LogicArgs(
      inputState = inputState,
      events = events,
      bag = previousState.bag
  )

  val secondBag = combineModules(modules)(args)

  return Pair(BloomState(
      input = currentInput,
      bag = newBag.plus(secondBag)
  ), events)
}

fun persist(key: String): LogicModuleOld = { bundle ->
  val flowerState = bundle.state.bag[key]
  if (flowerState != null)
    mapOf(key to flowerState)
  else
    null
}

fun persist(key: String, logicModule: LogicModuleOld): LogicModuleOld = { bundle ->
  val visibleBounds = bundle.visibleBounds
  if (visibleBounds != null)
    logicModule(bundle)
  else {
    val flowerState = bundle.state.bag[key]
    if (flowerState != null)
      mapOf(key to flowerState)
    else
      null
  }
}

fun isInBounds(position: Vector2i, bounds: Bounds): Boolean =
    position.x >= bounds.position.x &&
        position.x < bounds.position.x + bounds.dimensions.x &&
        position.y >= bounds.position.y &&
        position.y < bounds.position.y + bounds.dimensions.y

fun logic(logicModule: LogicModuleOld): Flower = { seed ->
  Box(
      bounds = Bounds(dimensions = seed.dimensions),
      logic = logicModule
  )
}

infix fun LogicModuleOld.combineLogic(b: LogicModuleOld): LogicModuleOld = { bundle ->
  val first = this(bundle)
  val second = b(bundle)
  if (first != null) {
    if (second != null) {
      first.plus(second)
    } else {
      first
    }
  } else if (second != null) {
    second
  } else {
    null
  }
}
