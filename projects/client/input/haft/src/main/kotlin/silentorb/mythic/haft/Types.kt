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
    val value: Float = 0f,
    val device: Int
)

fun simpleCommand(type: Any, device: Int, target: Long = 0): HaftCommand =
    HaftCommand(
        type = type,
        target = target,
        value = 1f,
        device = device
    )

data class Gamepad(val id: Int, val name: String)

typealias HaftCommands = List<HaftCommand>

typealias CommandHandler = (HaftCommand) -> Unit

//typealias InputTriggerState = Map<Binding, TriggerState?>

typealias Bindings = List<Binding>

typealias ScalarInputSource = (trigger: Int) -> Float

typealias MultiDeviceScalarInputSource = (device: Int, trigger: Int) -> Float

typealias InputProfiles = List<Bindings>

val disconnectedScalarInputSource: ScalarInputSource = { 0f }

fun newInputDeviceState() =
    InputDeviceState(
        events = listOf(),
        mousePosition = Vector2()
    )

typealias BindingSourceTarget = Long
typealias BindingSource = (InputEvent) -> Triple<Binding, BindingSourceTarget, Boolean>?
