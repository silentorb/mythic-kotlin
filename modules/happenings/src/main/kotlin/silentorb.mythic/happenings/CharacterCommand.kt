package silentorb.mythic.happenings

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.singleValueCache

fun filterCommands(id: Id, commands: Commands) =
    commands.filter { it.target == id }

val filterCharacterCommandsFromEvents = singleValueCache<Events, List<Command>> { events ->
  events.filterIsInstance<Command>()
}
