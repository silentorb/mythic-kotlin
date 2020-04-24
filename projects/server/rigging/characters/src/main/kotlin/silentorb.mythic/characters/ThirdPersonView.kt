package silentorb.mythic.characters

import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import silentorb.mythic.happenings.Commands
import silentorb.mythic.physics.Body
import silentorb.mythic.physics.firstRayHit
import silentorb.mythic.spatial.*

const val targetCameraDistance = 7f
const val cameraObstaclePadding = 0.01f

fun getHoverCameraOrientation(hoverCamera: HoverCamera): Quaternion =
    Quaternion()
        .rotateZ(hoverCamera.yaw)
        .rotateY(hoverCamera.pitch)

fun hoverCameraAnchorPosition(bodyPosition: Vector3): Vector3 =
    bodyPosition + Vector3(0f, 0f, 1f)

fun getHoverCameraPosition(bodyPosition: Vector3, hoverCamera: HoverCamera, orientation: Quaternion): Vector3 =
    hoverCameraAnchorPosition(bodyPosition) + orientation * Vector3(-hoverCamera.distance, 0f, 0f)

fun adjustCameraDistance(dynamicsWorld: btDiscreteDynamicsWorld, cameraCollisionMask: Int,
                         bodyPosition: Vector3, hoverCamera: HoverCamera): Float {
  val orientation = getHoverCameraOrientation(hoverCamera)
  val rayStart = hoverCameraAnchorPosition(bodyPosition)
  val rayEnd = rayStart + orientation * Vector3(-targetCameraDistance * 2f, 0f, 0f)
  val hit = firstRayHit(dynamicsWorld, rayStart, rayEnd, cameraCollisionMask)
  return if (hit != null) {
    val distance = hit.hitPoint.distance(rayStart)
    distance - 0.5f
  } else
    targetCameraDistance
}

fun updateThirdPersonCamera(dynamicsWorld: btDiscreteDynamicsWorld, cameraCollisionMask: Int,
                            body: Body,
                            commands: Commands,
                            movements: List<CharacterRigMovement>, delta: Float,
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

  val nextCamera = hoverCamera.copy(
      yaw = yaw,
      pitch = pitch,
      facingDestination = facingDestination,
      facingDestinationCandidate = facingDestinationCandidate
  )

  return characterRig.copy(
      lookVelocity = lookVelocity,
      hoverCamera = nextCamera.copy(
          distance = adjustCameraDistance(dynamicsWorld, cameraCollisionMask, body.position, hoverCamera)
      ),
      facingRotation = facingRotation
  )
}
