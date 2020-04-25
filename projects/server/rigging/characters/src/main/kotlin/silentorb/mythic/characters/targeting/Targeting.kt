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

fun newTargetingEvents(previousTargetings: Table<Targeting>, events: Events,
                       getGetAvailableTargets: GetAvailableTargets, autoSelectTarget: AutoSelectTarget): Events {
  val toggleEvents = getTargetingToggles(events)

  val (obsoleteTargetings, newTargetings) = toggleEvents
      .partition { previousTargetings.containsKey(it) }

  val deletions = obsoleteTargetings.map { StopTargeting(it) }
  val additions = newTargetings.mapNotNull { actor ->
    val availableTargets = getGetAvailableTargets(actor)
    if (availableTargets.none())
      null
    else {
      val target = autoSelectTarget(actor, availableTargets)
      ChangeTargeting(
          actor = actor,
          target = target
      )
    }
  }

  val continuing = previousTargetings.minus(toggleEvents)
  val modifications = continuing
      .mapNotNull { (actor, targeting) ->
        val availableTargets = getGetAvailableTargets(actor)
        if (availableTargets.none())
          StopTargeting(actor)
        else if (!availableTargets.contains(targeting.target)) {
          val target = autoSelectTarget(actor, availableTargets)
          ChangeTargeting(
              actor = actor,
              target = target
          )
        } else
          null
      }

  return additions + deletions + modifications
}

// This groups together modified and new targetings instead of handling them separately
fun modifiedTargetings(events: Events): List<ChangeTargeting> {
  return events.filterIsInstance<ChangeTargeting>()
}

fun targetingEventsToRecords(events: List<ChangeTargeting>): Table<Targeting> =
    events.associate { Pair(it.actor, Targeting(it.target)) }

fun updateTargetings(events: Events, targetings: Table<Targeting>): Table<Targeting> =
    targetings + targetingEventsToRecords(modifiedTargetings(events))

fun getObsoleteTargetings(events: Events): List<Id> {
  return events
      .filterIsInstance<StopTargeting>()
      .map { it.actor }
}

fun pruneTargetings(events: Events, targetings: Table<Targeting>): Table<Targeting> =
    targetings.minus(getObsoleteTargetings(events))
