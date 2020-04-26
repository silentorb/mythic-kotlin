package silentorb.mythic.characters

import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.happenings.CharacterCommand
import silentorb.mythic.happenings.Commands
import silentorb.mythic.happenings.Events
import silentorb.mythic.physics.Body
import silentorb.mythic.physics.firstRayHit
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.minMax
import kotlin.math.min

const val targetCameraDistance = 5f
const val cameraObstaclePadding = 0.3f

fun hoverCameraAnchorPosition(actorLocation: Vector3): Vector3 =
    actorLocation + Vector3(0f, 0f, 1f)

fun getCameraDestination(actorLocation: Vector3): Vector3 =
    actorLocation + Vector3(0f, 0f, 1f)

fun getCameraLocation(bodyPosition: Vector3, orientation: Quaternion, distance: Float): Vector3 =
    hoverCameraAnchorPosition(bodyPosition) + orientation * Vector3(-distance, 0f, 0f)

fun adjustCameraDistance(dynamicsWorld: btDiscreteDynamicsWorld, cameraCollisionMask: Int,
                         bodyPosition: Vector3, orientation: Quaternion): Float {
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
    camera: ThirdPersonRig
): ThirdPersonRig {
//  return camera
  val orientationVelocity = if (target == null) {
    updateLookVelocity(commands, Vector2(4f, 2f), camera.orientationVelocity)
  } else
    Vector2.zero

  val pivotDestination = getCameraDestination(body.position)
  val newLocation = if (camera.pivotLocation != pivotDestination) {
    val locationDifference = pivotDestination - camera.pivotLocation
    val locationDifferenceLength = locationDifference.length()
    val locationChangeDistance = min(10f, locationDifferenceLength) * 2f * delta
    val locationOffset = locationDifference.normalize() * locationChangeDistance
    camera.pivotLocation + locationOffset
  } else
    camera.pivotLocation

  val orientation = if (orientationVelocity.x != 0f || orientationVelocity.y != 0f) {
    val pitch = minMax(camera.orientation.angleX - orientationVelocity.y * delta, -1.0f, 1.5f)
    println(camera.orientation.angleZ)
    Quaternion()
        .rotateZ(camera.orientation.angleZ + orientationVelocity.x * delta)
        .rotateY(pitch)
  } else
    camera.orientation
  // Not currently supporting aggregating movement events from multiple sources
  assert(movements.size < 2)

//  val movement = if (target == null)
//    movements.firstOrNull()
//  else
//    null

//  val facingDestination = if (movement != null) {
//    Quaternion.lookAt((movement.offset).normalize())
//  } else if (target != null) {
//    Quaternion.lookAt((target - body.position).normalize())
//  } else
//    null

//  println(camera.orientation)
  return camera.copy(
      orientation = orientation,
      orientationVelocity = orientationVelocity,
      pivotLocation = newLocation,
      distance = adjustCameraDistance(dynamicsWorld, cameraCollisionMask, body.position, orientation)
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
  return facingRotation
//  return if (thirdPersonRig.orientationDestination != facing) {
//    val facingGap = getAngleCourse(facing, thirdPersonRig.orientationDestination)
//    val maxFacingChange = 0.3f
//    val change = minMax(facingGap, -maxFacingChange, maxFacingChange)
////    println("$change $facingGap ${movement.offset} ${movement.offset.length()}")
//    Vector3(0f, 0f, facing + change
//    )
//  } else
//    facingRotation
}

fun newThirdPersonRig(actorLocation: Vector3, actorFacingZ: Float): ThirdPersonRig {
  return ThirdPersonRig(
      pivotLocation = getCameraDestination(actorLocation),
      orientation = -Quaternion().rotateZ(actorFacingZ),
      distance = targetCameraDistance
  )
}
