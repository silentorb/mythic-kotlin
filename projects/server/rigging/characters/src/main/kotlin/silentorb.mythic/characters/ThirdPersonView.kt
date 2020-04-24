package silentorb.mythic.characters

import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.*
import kotlin.math.abs
import kotlin.math.min

fun getHoverCameraOrientation(hoverCamera: HoverCamera): Quaternion =
    Quaternion()
        .rotateZ(hoverCamera.yaw)
        .rotateY(hoverCamera.pitch)

fun getHoverCameraPosition(bodyPosition: Vector3, hoverCamera: HoverCamera, orientation: Quaternion): Vector3 =
    bodyPosition + Vector3(0f, 0f, 1f) + orientation * Vector3(-hoverCamera.distance, 0f, 0f)

fun updateThirdPersonCamera(commands: Commands, movements: List<CharacterRigMovement>, delta: Float,
                            characterRig: CharacterRig): CharacterRig {
  val lookVelocity = updateLookVelocity(commands, Vector2(4f, 2f), characterRig.lookVelocity)
  val facing = characterRig.facingRotation.z
  val hoverCamera = characterRig.hoverCamera ?: HoverCamera(0f, 0f, 5f, facing, facing)
  val yaw = hoverCamera.yaw + lookVelocity.x * delta
  val pitch = hoverCamera.pitch - lookVelocity.y * delta

  // Not currently supporting aggregating movement events from multiple sources
  assert(movements.size < 2)

  val movement = movements.firstOrNull()

  val facingDestinationCandidate = if (movement != null) {
    getHorizontalLookAtAngle(movement.offset)
  } else
    hoverCamera.facingDestination

  val facingDestination = if (movement != null) {
    val range = Pi / 4f
    if (getAngleGap(facingDestinationCandidate, facing) < range ||
        getAngleGap(facingDestinationCandidate, hoverCamera.facingDestinationCandidate) < range) {
      facingDestinationCandidate
    } else {
      hoverCamera.facingDestination
    }
  } else
    hoverCamera.facingDestination

  val facingRotation = if (movement != null) {
    val facingGap = getAngleCourse(facing, facingDestination)
    val maxFacingChange = 0.3f
    val change = minMax(facingGap, -maxFacingChange, maxFacingChange)
//    println("$change $facingGap ${movement.offset} ${movement.offset.length()}")
    Vector3(0f, 0f, facing + change
    )
  } else
    characterRig.facingRotation

  return characterRig.copy(
      lookVelocity = lookVelocity,
      hoverCamera = hoverCamera.copy(
          yaw = yaw,
          pitch = pitch,
          facingDestination = facingDestination,
          facingDestinationCandidate = facingDestinationCandidate
      ),
      facingRotation = facingRotation
  )
}
