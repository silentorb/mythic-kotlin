package silentorb.mythic.happenings

import silentorb.mythic.ent.Id

data class UseAction(
    val actor: Id,
    val action: Id
) : GameEvent
