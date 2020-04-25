package silentorb.mythic.characters.targeting

import silentorb.mythic.ent.Id
import silentorb.mythic.happenings.GameEvent

const val toggleTargetingCommand = "toggleTargeting"

data class Targeting(
    val target: Id
)

data class ChangeTargeting(
    val actor: Id,
    val target: Id
) : GameEvent

data class StopTargeting(
    val actor: Id
) : GameEvent

typealias GetAvailableTargets = (Id) -> List<Id>
typealias AutoSelectTarget = (Id, List<Id>) -> Id
