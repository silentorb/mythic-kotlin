package silentorb.mythic.editing

import silentorb.mythic.haft.Bindings
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.haft.mapInputToCommands
import silentorb.mythic.happenings.Commands

fun mapCommands(bindings: Bindings, deviceStates: List<InputDeviceState>): Commands =
    mapInputToCommands(setOf(), bindings, deviceStates)
