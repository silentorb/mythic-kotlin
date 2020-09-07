package silentorb.mythic.bloom.next

import silentorb.mythic.bloom.*
import silentorb.mythic.spatial.Vector2i

typealias LogicModule = (LogicArgs) -> StateBag

data class LogicArgs(
    val inputState: HistoricalInputState,
    val events: List<AnyEvent>,
    val bag: StateBag
)

inline fun <reified T> getBagEntry(bag: StateBag, key: String, initialValue: () -> T): T {
  val existing = bag[key]
  return if (existing != null && existing is T)
    existing
  else
    initialValue()
}

fun getClicked(inputState: HistoricalInputState, boxes: List<Box>) =
    boxes.filter { box ->
      val visibleBounds = visibleBounds(box)
      visibleBounds != null && isClickInside(visibleBounds, inputState)
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

fun gatherEventBoxes(box: Box): List<Box> {
  val children = box.boxes.flatMap(::flattenAllBoxes)
  return if (box.onClick.any())
    listOf(box).plus(children)
  else
    children
}

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

fun gatherBoxEvents(inputState: HistoricalInputState, box: Box): List<AnyEvent> {
  val boxes = gatherEventBoxes(box)
  val activatedBoxes = getClicked(inputState, boxes)
  return activatedBoxes.flatMap { it.onClick }
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
