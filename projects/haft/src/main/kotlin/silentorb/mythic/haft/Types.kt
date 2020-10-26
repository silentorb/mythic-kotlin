package silentorb.mythic.haft

import silentorb.mythic.debugging.getDebugString
import silentorb.mythic.platforming.InputEvent
import silentorb.mythic.spatial.Vector2

val DEBUG_INPUT_COUNTS = getDebugString("DEBUG_INPUT_COUNTS") != null

enum class DeviceIndex {
  keyboard,
  mouse,
  gamepad
}

data class InputDeviceState(
    val events: List<InputEvent>,
    val mousePosition: Vector2
)

data class Binding(
    val device: DeviceIndex,
    val trigger: Int,
    val command: Any
)

data class HaftCommand(
    val type: Any,
    val target: Long = 0,
    val value: Any? = 0f,
    val device: Int
)

typealias HaftCommands = List<HaftCommand>

typealias Bindings = List<Binding>

typealias InputProfiles = List<Bindings>

fun newInputDeviceState() =
    InputDeviceState(
        events = listOf(),
        mousePosition = Vector2()
    )

typealias BindingSourceTarget = Long
typealias BindingSource = (InputEvent) -> Triple<Binding, BindingSourceTarget, Boolean>?
