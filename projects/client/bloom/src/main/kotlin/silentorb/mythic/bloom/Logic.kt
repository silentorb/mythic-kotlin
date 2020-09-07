package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

typealias LogicModule = (LogicArgs) -> StateBag

data class LogicArgs(
    val inputState: HistoricalInputState,
    val events: List<AnyEvent>,
    val bag: StateBag
)

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

inline fun <reified T> getBagEntry(bag: StateBag, key: String, initialValue: () -> T): T {
  val existing = bag[key]
  return if (existing != null && existing is T)
    existing
  else
    initialValue()
}

fun flattenAllBoxes(box: Box): List<Box> =
    listOf(box).plus(box.boxes.flatMap(::flattenAllBoxes))

fun combineModules(modules: List<LogicModule>): LogicModule = { args ->
  modules.fold(mapOf()) { a, b ->
    a.plus(b(args))
  }
}

fun combineModules(vararg modules: LogicModule): LogicModule =
    combineModules(modules.toList())

val emptyLogic: LogicModule = { mapOf() }

fun isInBounds(position: Vector2i, bounds: Bounds): Boolean =
    position.x >= bounds.position.x &&
        position.x < bounds.position.x + bounds.dimensions.x &&
        position.y >= bounds.position.y &&
        position.y < bounds.position.y + bounds.dimensions.y

fun isInBounds(position: Vector2i, box: Box): Boolean {
  val visibleBounds = visibleBounds(box)
  return visibleBounds != null && isInBounds(position, visibleBounds)
}

fun hasAttributes(box: Box): Boolean =
    box.attributes.any()

fun getHoverBoxes(mousePosition: Vector2i, boxes: List<Box>): List<Box> =
    boxes.filter { box ->
      box.attributes.any() && isInBounds(mousePosition, box)
    }

fun newBloomState() =
    BloomState(
        resourceBag = mapOf(),
        input = InputState(
            mousePosition = Vector2i(),
            mouseButtons = listOf(ButtonState.up),
            events = listOf()
        )
    )
