package silentorb.mythic.editing

import com.fasterxml.jackson.annotation.JsonIgnore
import silentorb.mythic.cameraman.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.spatial.*

const val simulationFps = 60
const val simulationDelta = 1f / simulationFps.toFloat()

data class CameraRig(
    val location: Vector3 = Vector3.zero,
    val rotation: Vector2 = Vector2.zero,
    val lookVelocity: Vector2 = Vector2.zero,
    val pivotDistance: Float = 10f,
) {
  @get:JsonIgnore
  val orientation: Quaternion
    get() = Quaternion()
        .rotateZ(rotation.x)
        .rotateY(-rotation.y)
//  get() =
//  Quaternion.lookAt((Vector3.zero - location).normalize())
}

fun updateCameraOrbiting(mouseOffset: Vector2, camera: CameraRig): CameraRig {
  val pivotOffset = Vector3(camera.pivotDistance, 0f, 0f)
//  val pivot = camera.location + camera.orientation.transform(pivotOffset)
  val pivot = Vector3.zero
  val reverseRotation = getYawAndPitch(camera.location - pivot)
  val a = getYawAndPitch((Vector3(-10f, 0f, 5f) - pivot))
  val b = getYawAndPitch((Vector3(10f, 0f, 5f) - pivot))

  val orientationOffset = Quaternion()
      .rotateZ(reverseRotation.x - mouseOffset.x * 0.03f)
      .rotateY(-reverseRotation.y - mouseOffset.y * 0.025f)

  val nextLocation = orientationOffset.transform(pivotOffset) + pivot

  val nextRotation = getYawAndPitch(pivot - nextLocation)
  return camera.copy(
      location = nextLocation,
      rotation = nextRotation,
      lookVelocity = Vector2.zero
  )
}

fun updateFlyThroughCamera(mouseOffset: Vector2, commands: List<Command>, camera: CameraRig): CameraRig {
  return if (isAltDown())
    updateCameraOrbiting(mouseOffset, camera)
  else {
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
      Vector3(0f, 0f, zSpeed * simulationDelta)
    } else
      Vector3.zero

    return camera.copy(
        location = camera.location + movementOffset + zOffset,
        rotation = rotation,
        lookVelocity = lookVelocity,
    )
  }
}
