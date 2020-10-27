package silentorb.mythic.cameraman

import silentorb.mythic.happenings.CommandName
import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Vector3

fun joinInputVector(commands: Commands, commandMap: Map<CommandName, Vector3>): Vector3? {
  val forces = commands.mapNotNull {
    val vector = commandMap[it.type]
    if (vector != null && it.value > 0)
      vector * it.value
    else
      null
  }
  if (forces.isEmpty())
    return null

  val offset = forces.reduce { a, b -> a + b }
  return if (offset == Vector3.zero)
    Vector3.zero
  else {
    if (offset.length() > 1f)
      offset.normalize()
    else
      offset
  }
}
