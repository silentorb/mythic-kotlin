package silentorb.mythic.editing

import silentorb.mythic.cameraman.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3

const val simulationFps = 60
const val simulationDelta = 1f / simulationFps.toFloat()

data class CameraRig(
    val location: Vector3 = Vector3.zero,
    val rotation: Vector2 = Vector2.zero,
    val lookVelocity: Vector2 = Vector2.zero
) {
  val orientation:Quaternion get() = Quaternion()
      .rotateZ(rotation.x)
      .rotateY(-rotation.y)
}

fun updateFlyThroughCamera(commands: List<Command>, camera: CameraRig): CameraRig {
  val lookVelocity = updateLookVelocityFirstPerson(commands, defaultLookMomentumAxis(), camera.lookVelocity)
  val rotation = updateFirstPersonFacingRotation(camera.rotation, null, lookVelocity, simulationDelta)
  val movementVector = characterMovementVector(commands, camera.orientation)
  val movementOffset = if (movementVector != null)
    movementVector * 12f * simulationDelta
  else
    Vector3.zero

  val zSpeed = 15f
  val zOffset = if (commands.any { it.type == CameramanCommands.moveUp }) {
    Vector3(0f, 0f, -zSpeed * simulationDelta)
  } else if (commands.any { it.type == CameramanCommands.moveDown }) {
    Vector3(0f, 0f, zSpeed* simulationDelta)
  } else
    Vector3.zero

  return camera.copy(
      location = camera.location + movementOffset + zOffset,
      rotation = rotation,
      lookVelocity = lookVelocity,
  )
}
