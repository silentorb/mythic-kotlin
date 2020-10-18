package silentorb.mythic.haft

import silentorb.mythic.debugging.debugLog
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.platforming.InputEvent

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
            if (getDebugBoolean("DEBUG_INPUT"))
              debugLog("Haft Command: isStroke $isStroke ${binding.command} $target ${event.value}")
            HaftCommand(
                type = binding.command,
                target = target,
                value = event.value,
                device = event.device
            )
          } else
            null
        } else
          null
      }
}

fun createBindings(device: DeviceIndex, bindings: Map<Int, Any>) =
    bindings.map { Binding(device, it.key, it.value) }
