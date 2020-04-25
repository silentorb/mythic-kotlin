package silentorb.mythic.characters

import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.happenings.CharacterCommand
import silentorb.mythic.happenings.Commands
import silentorb.mythic.happenings.Events
import silentorb.mythic.physics.Body
import silentorb.mythic.physics.firstRayHit
import silentorb.mythic.spatial.*

const val targetCameraDistance = 7f
const val cameraObstaclePadding = 0.3f

fun getHoverCameraOrientation(yaw: Float, pitch: Float): Quaternion =
    Quaternion()
        .rotateZ(yaw)
        .rotateY(pitch)

fun getHoverCameraOrientation(thirdPersonRig: ThirdPersonRig): Quaternion =
    getHoverCameraOrientation(thirdPersonRig.yaw, thirdPersonRig.pitch)

fun hoverCameraAnchorPosition(bodyPosition: Vector3): Vector3 =
    bodyPosition + Vector3(0f, 0f, 1f)

fun getHoverCameraPosition(bodyPosition: Vector3, thirdPersonRig: ThirdPersonRig, orientation: Quaternion): Vector3 =
    hoverCameraAnchorPosition(bodyPosition) + orientation * Vector3(-thirdPersonRig.distance, 0f, 0f)

fun adjustCameraDistance(dynamicsWorld: btDiscreteDynamicsWorld, cameraCollisionMask: Int,
                         bodyPosition: Vector3, yaw: Float, pitch: Float): Float {
  val orientation = getHoverCameraOrientation(yaw, pitch)
  val rayStart = hoverCameraAnchorPosition(bodyPosition)
  val rayEnd = rayStart + orientation * Vector3(-targetCameraDistance * 2f, 0f, 0f)
  val hit = firstRayHit(dynamicsWorld, rayStart, rayEnd, cameraCollisionMask)
  return if (hit != null) {
    val distance = hit.hitPoint.distance(rayStart)
    distance - cameraObstaclePadding
  } else
    targetCameraDistance
}

fun updateThirdPersonCamera(
    dynamicsWorld: btDiscreteDynamicsWorld,
    cameraCollisionMask: Int,
    commands: Commands,
    movements: List<CharacterRigMovement>,
    delta: Float,
    body: Body,
    characterRig: CharacterRig,
    target: Vector3?,
    thirdPersonRig: ThirdPersonRig
): ThirdPersonRig {
  val lookVelocity = updateLookVelocity(commands, Vector2(4f, 2f), thirdPersonRig.lookVelocity)
  val facing = characterRig.facingRotation.z
  val yaw = thirdPersonRig.yaw + lookVelocity.x * delta
  val pitch = minMax(thirdPersonRig.pitch - lookVelocity.y * delta, -1.0f, 1.5f)

  // Not currently supporting aggregating movement events from multiple sources
  assert(movements.size < 2)

  val movement = movements.firstOrNull()

  val facingDestinationCandidate = if (movement != null) {
    getHorizontalLookAtAngle(movement.offset)
  } else
    thirdPersonRig.facingDestination

  val facingDestination = if (movement != null) {
    val range = Pi / 4f
    if (getAngleGap(facingDestinationCandidate, facing) < range ||
        getAngleGap(facingDestinationCandidate, thirdPersonRig.facingDestinationCandidate) < range) {
      facingDestinationCandidate
    } else {
      thirdPersonRig.facingDestination
    }
  } else
    thirdPersonRig.facingDestination

  return thirdPersonRig.copy(
      yaw = yaw,
      pitch = pitch,
      lookVelocity = lookVelocity,
      facingDestination = facingDestination,
      facingDestinationCandidate = facingDestinationCandidate,
      distance = adjustCameraDistance(dynamicsWorld, cameraCollisionMask, body.position, yaw, pitch)
  )
}

fun updateThirdPersonCamera(
    dynamicsWorld: btDiscreteDynamicsWorld,
    cameraCollisionMask: Int,
    events: Events,
    bodies: Table<Body>,
    characterRigs: Table<CharacterRig>,
    targets: Table<Id>,
    delta: Float
): (Id, ThirdPersonRig) -> ThirdPersonRig {
  val allCommands = events
      .filterIsInstance<CharacterCommand>()

  val allMovements = events
      .filterIsInstance<CharacterRigMovement>()

  return { id, thirdPersonRig ->
    val body = bodies[id]!!
    val characterRig = characterRigs[id]!!
    val target = bodies[targets[id]]?.position
    val commands = allCommands.filter { it.target == id }
    val movements = allMovements.filter { it.actor == id }

    updateThirdPersonCamera(dynamicsWorld, cameraCollisionMask, commands, movements, delta, body, characterRig, target, thirdPersonRig)
  }
}

fun updateThirdPersonFacingRotation(facingRotation: Vector3, thirdPersonRig: ThirdPersonRig,
                                    delta: Float): Vector3 {
  val facing = facingRotation.z

  return if (thirdPersonRig.facingDestination != facing) {
    val facingGap = getAngleCourse(facing, thirdPersonRig.facingDestination)
    val maxFacingChange = 0.3f
    val change = minMax(facingGap, -maxFacingChange, maxFacingChange)
//    println("$change $facingGap ${movement.offset} ${movement.offset.length()}")
    Vector3(0f, 0f, facing + change
    )
  } else
    facingRotation
}
