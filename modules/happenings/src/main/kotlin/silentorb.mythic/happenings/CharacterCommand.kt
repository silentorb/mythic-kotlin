package silentorb.mythic.happenings

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.singleValueCache

typealias CommandName = String

data class CharacterCommand(
    val type: CommandName,
    val target: Id,
    val value: Float = 1f,
    val device: Int = 0
) : GameEvent

typealias Commands = List<CharacterCommand>

fun filterCommands(id: Id, commands: Commands) =
    commands.filter({ it.target == id })

val filterCharacterCommandsFromEvents = singleValueCache<Events, List<CharacterCommand>> { events ->
  events.filterIsInstance<CharacterCommand>()
}
