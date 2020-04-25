package silentorb.mythic.characters.targeting

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.happenings.CharacterCommand
import silentorb.mythic.happenings.Events

fun getTargetingToggles(events: Events): List<Id> =
    events
        .filterIsInstance<CharacterCommand>()
        .filter { it.type == toggleTargetingCommand }
        .map { it.target }

fun updateTargeting(previousTargets: TargetTable, toggleEvents: Set<Id>,
                  getGetAvailableTargets: GetAvailableTargets, autoSelectTarget: AutoSelectTarget): TargetTable {
  val (obsoleteTargets, newTargets) = toggleEvents
      .partition { previousTargets.containsKey(it) }

  val additions = newTargets.mapNotNull { actor ->
    val availableTargets = getGetAvailableTargets(actor)
    if (availableTargets.none())
      null
    else {
      val target = autoSelectTarget(actor, availableTargets)
      Pair(actor, target)
    }
  }
      .associate { it }

  val continuing = previousTargets.minus(toggleEvents)

  val modifications = continuing
      .mapNotNull { (actor, previousTarget) ->
        val availableTargets = getGetAvailableTargets(actor)
        if (availableTargets.none())
          null
        else {
          val newTarget = if (availableTargets.contains(previousTarget))
            previousTarget
          else
            autoSelectTarget(actor, availableTargets)

          Pair(actor, newTarget)
        }
      }

  return additions.minus(obsoleteTargets) + modifications
}
