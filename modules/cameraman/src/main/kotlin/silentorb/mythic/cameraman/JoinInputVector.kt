package silentorb.mythic.cameraman

import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3

fun joinInputVector(commands: Commands, commandMap: Map<String, Vector3>, limit: Float = 1f): Vector3? {
  val forces = commands.mapNotNull {
    val vector = commandMap[it.type]
    if (vector != null && it.value as? Float ?: 0f > 0f)
      vector * it.value as Float
    else
      null
  }
  if (forces.isEmpty())
    return null

  val offset = forces.reduce { a, b -> a + b }
  return if (offset == Vector3.zero)
    Vector3.zero
  else {
    if (offset.length() > limit)
      offset.normalize()
    else
      offset
  }
}

fun characterMovementVector(commands: Commands, orientation: Quaternion): Vector3? {
  val offsetVector = joinInputVector(commands, playerMoveMap)
  return if (offsetVector != null) {
    val offset = orientation * offsetVector
    offset
  } else {
    null
  }
}
