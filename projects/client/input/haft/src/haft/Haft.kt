package haft

import silentorb.mythic.debugging.debugLog
import mythic.platforming.InputEvent
import mythic.spatial.Vector2

val DEBUG_INPUT = System.getenv("DEBUG_INPUT") != null
val DEBUG_INPUT_COUNTS = System.getenv("DEBUG_INPUT_COUNTS") != null

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
    val value: Float = 0f
)

fun  simpleCommand(type: Any, target: Long = 0): HaftCommand =
    HaftCommand(
        type = type,
        target = target,
        value = 1f
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

const val MouseMovementLeft = 5
const val MouseMovementRight = 6
const val MouseMovementUp = 7
const val MouseMovementDown = 8

fun newInputDeviceState() =
    InputDeviceState(
        events = listOf(),
        mousePosition = Vector2()
    )

fun applyMouseAxis(device: Int, value: Float, firstIndex: Int, secondIndex: Int, scale: Float) =
    if (value > 0)
      InputEvent(device, firstIndex, value * scale)
    else if (value < 0)
      InputEvent(device, secondIndex, -value * scale)
    else
      null

fun applyMouseMovement(device: Int, mouseOffset: Vector2): List<InputEvent> =
    listOfNotNull(
        applyMouseAxis(device, mouseOffset.x, MouseMovementRight, MouseMovementLeft, 1f),
        applyMouseAxis(device, mouseOffset.y, MouseMovementDown, MouseMovementUp, 1f)
    )

typealias BindingSourceTarget = Long
typealias BindingSource = (InputEvent) -> Triple<Binding, BindingSourceTarget, Boolean>?

fun matches(event: InputEvent): (InputEvent) -> Boolean = { other ->
  event.device == other.device && event.index == other.index
}

fun mapEventsToCommands(deviceStates: List<InputDeviceState>, getBinding: BindingSource): HaftCommands {
  if (DEBUG_INPUT_COUNTS) {
    val counts = deviceStates.map { it.events.size }
    if (counts.any { it > 0 })
      debugLog("Event counts: ${counts.joinToString(" ")}")
  }
  return deviceStates.last().events
      .mapNotNull { event ->
        val bindingPair = getBinding(event)
        if (bindingPair != null) {
          val (binding, target, isStroke) = bindingPair
          if (!isStroke || deviceStates.dropLast(1).last().events.none(matches(event))) {
            if (DEBUG_INPUT)
              debugLog("Haft Command: isStroke $isStroke ${binding.command} $target ${event.value}")
            HaftCommand(
                type = binding.command,
                target = target,
                value = event.value
            )
          } else
            null
        } else
          null
      }
}

fun getBindingSimple(bindings: List<Binding>): BindingSource = { event ->
  val binding = bindings.firstOrNull {
    val values = DeviceIndex.values()
    it.device == values[Math.min(2, event.device)] && it.trigger == event.index
  }
  if (binding != null)
    Triple(binding, 0, true)
  else
    null
}

fun  applyCommands(commands: HaftCommands, actions: Map<Any, (HaftCommand) -> Unit>) {
  commands.filter({ actions.containsKey(it.type) })
      .forEach({ actions[it.type]!!(it) })
}

fun  createBindings(device: DeviceIndex, bindings: Map<Int, Any>) =
    bindings.map({ Binding(device, it.key, it.value) })

fun  isActive(commands: List<HaftCommand>, commandType: Any): Boolean =
    commands.any { it.type == commandType }

fun  isActive(commands: List<HaftCommand>): (Any) -> Boolean =
    { commandType -> haft.isActive(commands, commandType) }

fun  getCommand(commands: List<HaftCommand>, commandType: Any) =
    commands.first { it.type == commandType }
