package silentorb.mythic.bloom

import silentorb.mythic.haft.DeviceIndexes
import silentorb.mythic.platforming.InputEvent
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.toVector2i

class DeleteMe

val deleteMe = DeleteMe()

fun foldBoxLogic(logicInput: LogicInput, boxes: List<OffsetBox>) =
    boxes.fold(mapOf<String, Any>()) { a, b -> a + getBoxLogicState(logicInput, b) }

fun getBoxLogicState(logicInput: LogicInput, box: OffsetBox): BloomState {
  val logic = box.child.logic
  val stateAdditions = if (logic != null)
    logic(logicInput, box)
  else
    mapOf()

  return stateAdditions + foldBoxLogic(logicInput, box.boxes)
}

fun updateBloomLogic(logicInput: LogicInput, boxes: List<OffsetBox>): BloomState {
  val nextState = foldBoxLogic(logicInput, boxes)
  return (logicInput.state + nextState)
      .filterValues { it != deleteMe }
}

fun composeLogic(vararg logic: LogicModule): LogicModule = { input, box ->
  logic.fold(mapOf()) { a, b -> a + b(input, box) }
}

fun composeLogic(logicModules: Collection<LogicModule>): LogicModule = { input, box ->
  logicModules.fold(mapOf()) { a, b -> a + b(input, box) }
}

fun composeLogicNotNull(vararg logic: LogicModule?): LogicModule =
    composeLogic(logic.filterNotNull())

fun onDrag(key: String, handler: (Vector2i?) -> BloomState): LogicModule = { input, box ->
  val isDragging = input.state.containsKey(key)
  if (isDragging) {
    val offset = if (input.isLeftMouseDown)
      (input.deviceStates.last().mousePosition - input.deviceStates.first().mousePosition).toVector2i()
    else
      null

    val value: Any = offset ?: deleteMe

    mapOf(key to value) + handler(offset)
  } else {
    if (input.isLeftMouseDownStarted && input.isMouseOver(box))
      mapOf(key to Vector2i.zero)
    else
      mapOf()
  }
//  handler(Vector2i.zero)
}

fun onHover(logicModule: LogicModule): LogicModule = { input, box ->
  if (input.isMouseOver(box))
    logicModule(input, box)
  else
    mapOf()
}

fun onLeftClick(logicModule: LogicModule): LogicModule = onHover { input, box ->
  if (input.isLeftMouseClick)
    logicModule(input, box)
  else
    mapOf()
}

fun onActivate(logicModule: LogicModule): LogicModule = composeLogic(
    onLeftClick(logicModule),
    { input, box ->
      val focusIndex = input.state[menuItemIndexKey] as? Int
      val isActivatePressed = input.isPressed(DeviceIndexes.keyboard, 257) ||
          input.isPressed(DeviceIndexes.gamepad, 100)
      if (box.child.attributes[menuItemIndexKey] == focusIndex && isActivatePressed)
        logicModule(input, box)
      else
        mapOf()
    }
)

fun onInputEvent(device: Int, handler: (InputEvent) -> BloomState): LogicModule = { input, box ->
  input.deviceStates
      .last()
      .events
      .filter { it.device == device }
      .fold(mapOf()) { a, b -> a + handler(b) }
}
