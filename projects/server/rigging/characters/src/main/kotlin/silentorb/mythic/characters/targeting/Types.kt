package silentorb.mythic.characters.targeting

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table

const val toggleTargetingCommand = "toggleTargeting"

typealias TargetTable = Table<Id>

typealias GetAvailableTargets = (Id) -> List<Id>
typealias AutoSelectTarget = (Id, List<Id>) -> Id
