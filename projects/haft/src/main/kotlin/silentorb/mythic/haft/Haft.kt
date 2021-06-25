package silentorb.mythic.haft

import silentorb.mythic.debugging.debugLog
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.platforming.InputEvent

fun matches(event: InputEvent): (InputEvent) -> Boolean = { other ->
  event.device == other.device && event.index == other.index
}

fun mapEventsToCommandsOld(deviceStates: List<InputDeviceState>, getBinding: BindingSource): Commands {
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
            if (getDebugBoolean("DEBUG_INPUT"))
              debugLog("Haft Command: isStroke $isStroke ${binding.command} $target ${event.value}")
            Command(
                type = binding.command,
                value = event.value,
                target = target,
                device = event.device
            )
          } else
            null
        } else
          null
      }
}

fun mapInputToCommands(strokes: Set<Any>, bindings: Bindings, deviceStates: List<InputDeviceState>): Commands {
  if (DEBUG_INPUT_COUNTS) {
    val counts = deviceStates.map { it.events.size }
    if (counts.any { it > 0 })
      debugLog("Event counts: ${counts.joinToString(" ")}")
  }
  val previousEvents = deviceStates.dropLast(1).last().events

  return deviceStates.last().events
      .mapNotNull { event ->
        val binding = bindings.firstOrNull { it.device == event.device && it.trigger == event.index }
        if (binding != null) {
          val isStroke = strokes.contains(binding.command)
          if (!isStroke || previousEvents.none(matches(event))) {
            if (getDebugBoolean("DEBUG_INPUT")) {
              debugLog("Haft Command: isStroke $isStroke ${binding.command} ${event.value}")
            }
            Command(
                type = binding.command,
                value = event.value,
                target = 0L,
                device = event.device
            )
          } else
            null
        } else
          null
      }
}

fun createBindings(device: Int, bindings: Map<Int, Any>) =
    bindings.map { Binding(device, it.key, it.value) }
