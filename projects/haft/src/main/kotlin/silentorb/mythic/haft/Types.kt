package silentorb.mythic.haft

import silentorb.mythic.debugging.getDebugString
import silentorb.mythic.platforming.InputEvent
import silentorb.mythic.platforming.PlatformInput
import silentorb.mythic.spatial.Vector2

val DEBUG_INPUT_COUNTS = getDebugString("DEBUG_INPUT_COUNTS") != null

object DeviceIndexes {
  const val keyboard = 0
  const val mouse = 1
  const val gamepad = 2
}

data class InputDeviceState(
    val events: List<InputEvent>,
    val mousePosition: Vector2
)

data class Binding(
    val device: Int,
    val index: Int,
    val command: Any,
)

typealias Bindings = List<Binding>

typealias InputProfiles = List<Bindings>

fun newInputDeviceState(input: PlatformInput) =
    InputDeviceState(
        events = listOf(),
        mousePosition = input.getMousePosition(),
    )

typealias BindingSourceTarget = Long
typealias BindingSource = (InputEvent) -> Triple<Binding, BindingSourceTarget, Boolean>?
