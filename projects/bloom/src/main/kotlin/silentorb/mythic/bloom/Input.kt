package silentorb.mythic.bloom

import silentorb.mythic.bloom.old.isInBounds
import silentorb.mythic.haft.HaftCommand
import silentorb.mythic.happenings.Commands

fun onClick(handler: () -> Commands): InputHandler = { input, bounds ->
  if (input.commands.any { it.type == HaftCommand.leftMouseClick } && isInBounds(input.mousePosition, bounds))
    handler()
  else
    listOf()
}

fun commandsFromBoxes(boxes: List<OffsetBox>, input: Input): Commands =
    boxes
        .filter { it.child.handler != null }
        .flatMap { it.child.handler!!(input, it.bounds) }
