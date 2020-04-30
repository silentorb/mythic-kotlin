package silentorb.mythic.happenings

import silentorb.mythic.ent.Id

data class UseAction(
    val actor: Id,
    val action: Id,
    val deferredEvents: Map<String, GameEvent> = mapOf()
) : GameEvent
