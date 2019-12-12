package mythic.bloom.next

import mythic.bloom.*
import org.joml.Vector2i

typealias LogicModule = (LogicArgs) -> StateBag

data class LogicArgs(
    val inputState: HistoricalInputState,
    val events: List<AnyEvent>,
    val bag: StateBag
)

fun Flower.plusOnClick(vararg events: AnyEvent): Flower = { seed ->
  val box = this(seed)
  box.copy(
      onClick = box.onClick.plus(events)
  )
}

fun getClicked(inputState: HistoricalInputState, boxes: List<Box>) =
    boxes.filter { box ->
      val visibleBounds = visibleBounds(box)
      visibleBounds != null && isClickInside(visibleBounds, inputState)
    }

fun getClickedLazy(inputState: HistoricalInputState, boxes: Sequence<Box>): List<Box> =
    if (isClick()(inputState))
      getClicked(inputState, boxes.toList())
    else
      listOf()

fun flattenAllBoxes(box: Box): List<Box> =
    listOf(box).plus(box.boxes.flatMap(::flattenAllBoxes))

fun combineModules(modules: List<LogicModule>): LogicModule = { args ->
  modules.fold(mapOf()) { a, b ->
    a.plus(b(args))
  }
}

fun combineModules(vararg modules: LogicModule): LogicModule =
    combineModules(modules.toList())

fun gatherEventBoxes(box: Box): List<Box> {
  val children = box.boxes.flatMap(::flattenAllBoxes)
  return if (box.onClick.any())
    listOf(box).plus(children)
  else
    children
}

fun gatherBoxEvents(inputState: HistoricalInputState, box: Box): List<AnyEvent> {
  val boxes = gatherEventBoxes(box)
  val activatedBoxes = getClicked(inputState, boxes)
  return activatedBoxes.flatMap { it.onClick }
}

fun <T> logicModule(initialState: Any, key: String, transform: (LogicArgs) -> (T) -> T): LogicModule =
    { args ->
      val state = args.bag[key] ?: initialState
      val result = transform(args)(state as T) as Any
      mapOf(key to result)
    }

fun newBloomState() =
    BloomState(
        bag = mapOf(),
        input = InputState(
            mousePosition = Vector2i(),
            mouseButtons = listOf(ButtonState.up),
            events = listOf()
        )
    )
