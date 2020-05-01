package silentorb.mythic.haft

import silentorb.mythic.debugging.debugLog
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.debugging.getDebugString
import silentorb.mythic.platforming.InputEvent
import silentorb.mythic.spatial.Vector2

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

fun getBindingSimple(bindings: List<Binding>, strokes: Set<Any>): BindingSource = { event ->
  val binding = bindings.firstOrNull {
    val values = DeviceIndex.values()
    it.device == values[Math.min(2, event.device)] && it.trigger == event.index
  }
  if (binding != null)
    Triple(binding, 0, strokes.contains(binding.command))
  else
    null
}

fun applyCommands(commands: HaftCommands, actions: Map<Any, (HaftCommand) -> Unit>) {
  commands.filter { actions.containsKey(it.type) }
      .forEach { actions[it.type]!!(it) }
}

fun createBindings(device: DeviceIndex, bindings: Map<Int, Any>) =
    bindings.map { Binding(device, it.key, it.value) }

fun isActive(commands: List<HaftCommand>, commandType: Any): Boolean =
    commands.any { it.type == commandType }

fun isActive(commands: List<HaftCommand>): (Any) -> Boolean =
    { commandType -> isActive(commands, commandType) }

fun getCommand(commands: List<HaftCommand>, commandType: Any) =
    commands.first { it.type == commandType }
