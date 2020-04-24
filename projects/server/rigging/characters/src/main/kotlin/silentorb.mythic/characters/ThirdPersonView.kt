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
  val hoverCamera = characterRig.hoverCamera ?: HoverCamera(0f, 0f, 5f, facing)
  val yaw = hoverCamera.yaw + lookVelocity.x * delta
  val pitch = hoverCamera.pitch - lookVelocity.y * delta

  // Not currently supporting aggregating movement events from multiple sources
  assert(movements.size < 2)

  val movement = movements.firstOrNull()

  val facingDestination = if (movement != null)
    getHorizontalLookAtAngle(movement.offset)
  else
    hoverCamera.facingDestination

  val turnVelocity = if (movement != null) {
    val facingGap = getAngleCourse(facing, facingDestination)
    val maxFacingChange = min(0.3f, abs(hoverCamera.turnVelocity) + 0.02f)
    val change = minMax(facingGap, -maxFacingChange, maxFacingChange)
//    val k = transitionAxis(0.03f, 0.06f, hoverCamera.turnVelocity, rawChange)
    println("$change $facingGap ${movement.offset.length()}")
    change
  } else
    0f

  return characterRig.copy(
      lookVelocity = lookVelocity,
      hoverCamera = hoverCamera.copy(
          yaw = yaw,
          pitch = pitch,
          facingDestination = facingDestination,
          turnVelocity = turnVelocity
      ),
      facingRotation = if (movement != null)
        Vector3(0f, 0f, facing + turnVelocity
        )
      else
        characterRig.facingRotation
  )
}
