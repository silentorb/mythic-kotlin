package silentorb.mythic.bloom

import silentorb.mythic.bloom.next.isInBounds
import silentorb.mythic.spatial.Vector2i

enum class ButtonState {
  down,
  up
}

enum class BloomEvent {
  activate,
  back,
  up,
  down,
  left,
  right,
  pageUp,
  pageDown
}

data class InputState(
    val mousePosition: Vector2i,
    val mouseButtons: List<ButtonState>,
    val events: List<BloomEvent>
)

data class HistoricalInputState(
    val previous: InputState,
    val current: InputState
)

fun isClick(button: Int): (HistoricalInputState) -> Boolean = {
  it.previous.mouseButtons[button] == ButtonState.up
      && it.current.mouseButtons[button] == ButtonState.down
}

fun isClick() = isClick(0)

fun isClickInside(bounds: Bounds, inputState: HistoricalInputState) =
    isClick()(inputState) && isInBounds(inputState.current.mousePosition, bounds)

fun onClickPersisted(key: String, logicModule: LogicModuleOld): LogicModuleOld = { bundle ->
  val visibleBounds = bundle.visibleBounds
  if (visibleBounds != null && isClickInside(visibleBounds, bundle.state.input))
    logicModule(bundle)
  else {
    val flowerState = bundle.state.resourceBag[key]
    if (flowerState != null)
      mapOf(key to flowerState)
    else
      null
  }
}
