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
import kotlin.math.min

const val targetCameraDistance = 7f
const val cameraObstaclePadding = 0.3f

fun hoverCameraAnchorPosition(bodyPosition: Vector3): Vector3 =
    bodyPosition + Vector3(0f, 0f, 1f)

fun getCameraDestination(bodyPosition: Vector3, thirdPersonRig: ThirdPersonRig, orientation: Quaternion): Vector3 =
    hoverCameraAnchorPosition(bodyPosition) + orientation * Vector3(-7f, 0f, 0f)

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
  val pivotVelocity = if (target == null) {
    updateLookVelocity(commands, Vector2(4f, 2f), camera.pivotVelocity)
  } else
    Vector2.zero
//    Vector2()
  val newPivot = Quaternion(camera.pivot).rotateZ(pivotVelocity.x).rotateY(pivotVelocity.y)

  val anchorLocation = body.position + Vector3(0f, 0f, 1f)
  val idealDistance = 4f
  val locationDestination = anchorLocation + camera.pivot * Vector3(idealDistance, 0f, 0f)
  val newLocation = if (camera.location != anchorLocation) {
    val locationDifference = locationDestination - camera.location
    val locationDifferenceLength = locationDifference.length()
    val locationChangeDistance = min(10f, locationDifferenceLength) * 2f * delta
    val locationOffset = locationDifference.normalize() * locationChangeDistance
    val moved = camera.location + locationOffset
    val movedVector = moved - anchorLocation
    if (movedVector.length() < idealDistance)
      anchorLocation + movedVector.normalize() * idealDistance
    else
      moved
  } else
    camera.location

  val newOrientation = if (anchorLocation != newLocation) {
//    val transitionScale = locationChangeDistance / locationDifferenceLength
//    val orientationDestination = Quaternion.lookAt((anchorLocation - locationDestination).normalize())
//    Quaternion(camera.orientation).slerp(orientationDestination, transitionScale)
    Quaternion.lookAt(anchorLocation - newLocation)
  } else
    camera.orientation

  assert(!newOrientation.x.isNaN())
//  val yaw = camera.orientation.angleZ + lookVelocity.x * delta
//  val pitch = minMax(camera.orientation.angleX - lookVelocity.y * delta, -1.0f, 1.5f)
//  val orientation = Quaternion()
//      .rotateZ(yaw)
//      .rotateY(pitch)

  // Not currently supporting aggregating movement events from multiple sources
  assert(movements.size < 2)

  val movement = if (target == null)
    movements.firstOrNull()
  else
    null

  val facingDestination = if (movement != null) {
    Quaternion.lookAt((movement.offset).normalize())
  } else if (target != null) {
    Quaternion.lookAt((target - body.position).normalize())
  } else
    null

  return camera.copy(
      orientation = newOrientation,
      pivotVelocity = pivotVelocity,
      location = newLocation,
      pivot = newPivot
//      distance = adjustCameraDistance(dynamicsWorld, cameraCollisionMask, body.position, orientation)
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
