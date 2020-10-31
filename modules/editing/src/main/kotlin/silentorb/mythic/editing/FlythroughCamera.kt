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
    val orientation: Quaternion = Quaternion(),
    val lookVelocity: Vector2 = Vector2.zero,
    val pivotDistance: Float = 10f,
) {
}

fun updateCameraOrbiting(mouseOffset: Vector2, camera: CameraRig): CameraRig {
  val pivot = camera.location + camera.orientation.transform(Vector3(camera.pivotDistance, 0f, 0f))
  val orientationOffset = Quaternion()
      .rotateZ(-mouseOffset.x * 0.02f)
      .rotateY(mouseOffset.y * 0.03f)
  val nextLocation = orientationOffset.transform(camera.location - pivot) + pivot

  val nextOrientation = Quaternion.lookAt((pivot - nextLocation).normalize())
  return camera.copy(
      location = nextLocation,
      orientation = nextOrientation,
      lookVelocity = Vector2.zero
  )
}

fun updateFlyThroughCamera(mouseOffset: Vector2, commands: List<Command>, camera: CameraRig): CameraRig {
  return if (isAltDown())
    updateCameraOrbiting(mouseOffset, camera)
  else {
    val lookVelocity = updateLookVelocityFirstPerson(commands, defaultLookMomentumAxis(), camera.lookVelocity)
    val orientation = updateFirstPersonFacingRotation(camera.orientation, null, lookVelocity, simulationDelta)

    val movementVector = characterMovementVector(commands, camera.orientation)
    val movementOffset = if (movementVector != null)
      movementVector * 12f * simulationDelta
    else
      Vector3.zero

    val zSpeed = 15f
    val zOffset = if (commands.any { it.type == CameramanCommands.moveUp }) {
      Vector3(0f, 0f, -zSpeed * simulationDelta)
    } else if (commands.any { it.type == CameramanCommands.moveDown }) {
      Vector3(0f, 0f, zSpeed * simulationDelta)
    } else
      Vector3.zero

    return camera.copy(
        location = camera.location + movementOffset + zOffset,
        orientation = orientation,
        lookVelocity = lookVelocity,
    )
  }
}
