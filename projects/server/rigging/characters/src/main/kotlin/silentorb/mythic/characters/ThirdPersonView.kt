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
import kotlin.math.abs
import kotlin.math.min

const val targetCameraDistance = 5f
const val cameraObstaclePadding = 0.01f

fun getCameraOrientation(rotation: Vector2): Quaternion =
    Quaternion()
        .rotateZ(rotation.x)
        .rotateY(rotation.y)

fun getCameraPivotLocation(actorLocation: Vector3): Vector3 =
    actorLocation + Vector3(0f, 0f, 1f)

fun getCameraLocation(bodyPosition: Vector3, orientation: Quaternion, distance: Float): Vector3 =
    getCameraPivotLocation(bodyPosition) + orientation * Vector3(-distance, 0f, 0f)

fun adjustCameraDistance(dynamicsWorld: btDiscreteDynamicsWorld, cameraCollisionMask: Int,
                         bodyPosition: Vector3, orientation: Quaternion, delta: Float, distance: Float): Float {
  val rayStart = getCameraPivotLocation(bodyPosition)
  val rayEnd = rayStart + orientation * Vector3(-targetCameraDistance * 2f, 0f, 0f)
  val hit = firstRayHit(dynamicsWorld, rayStart, rayEnd, cameraCollisionMask)
  val newDistance = if (hit != null) {
    hit.hitPoint.distance(rayStart) - cameraObstaclePadding
  } else {
    targetCameraDistance
  }
  return if (newDistance < distance)
    newDistance
  else if (newDistance == distance)
    distance
  else {
    val difference = newDistance - distance
    distance + min(difference * 2f * delta, difference)
  }
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
  val orientationVelocity = if (target == null) {
    updateLookVelocity(commands, Vector2(4f, 2f), camera.orientationVelocity)
  } else
    Vector2.zero

  val normalPivotLocation = getCameraPivotLocation(body.position)
  val pivotDestination = if (target == null)
    normalPivotLocation
  else
    getCenter(normalPivotLocation, target)

  val newLocation = if (camera.pivotLocation != pivotDestination) {
    val locationDifference = pivotDestination - camera.pivotLocation
    val locationDifferenceLength = locationDifference.length()
    val locationChangeDistance = min(10f, locationDifferenceLength) * 2f * delta
    val locationOffset = locationDifference.normalize() * locationChangeDistance
    camera.pivotLocation + locationOffset
  } else
    camera.pivotLocation

  // Not currently supporting aggregating movement events from multiple sources
  assert(movements.size < 2)

  val movement = if (target == null)
    movements.firstOrNull()
  else
    null

  val facingDestination = if (movement != null) {
    getHorizontalLookAtAngle(movement.offset)
  } else if (target != null) {
    getHorizontalLookAtAngle(target - body.position)
  } else
    null

  val initialRotation = if (orientationVelocity.x != 0f || orientationVelocity.y != 0f) {
    val pitch = minMax(camera.rotation.y - orientationVelocity.y * delta, -1.0f, 1.4f)
    val yaw = camera.rotation.x + orientationVelocity.x * delta
    Vector2(yaw, pitch)
  } else
    camera.rotation

  val idealRotationX = if (target == null)
    characterRig.facingRotation.z
  else
    atan(target.xy() - newLocation.xy())

  val idealRotationY = 0.3f
  val isAlignedWithIdeal = initialRotation.x == idealRotationX && initialRotation.y == idealRotationY
  val rotationAlignment = if (movement != null && !isAlignedWithIdeal) {
    val initialGapX = getAngleCourse(initialRotation.x, idealRotationX)
    val gapY = getAngleCourse(initialRotation.y, idealRotationY)
    val isCleanlyReversed = abs(initialGapX) > Pi * 0.8f
    val gapX = if (isCleanlyReversed)
      0f
    else
      initialGapX

    val maxChange = 0.5f * delta
    if (gapX == 0f && gapY == 0f)
      Vector2.zero
    else {
      val gapVector = Vector2(gapX, gapY)
      val changeLength = minMax(gapVector.length(), -maxChange, maxChange)
//      if (changeLength < maxChange && !isCleanlyReversed)
//        Vector2(idealRotationX, idealRotationY)
//      else {
      gapVector.normalize() * changeLength
//      }
    }
  } else if (!isAlignedWithIdeal && target != null) {
    val gapX = getAngleCourse(initialRotation.x, idealRotationX)
    val gapY = getAngleCourse(initialRotation.y, idealRotationY)
    val maxChange = 4f * delta
    val gapVector = Vector2(gapX, gapY)
//    val gapVector = Vector2(
//        if (abs(gapX) < Pi * 0.01f) 0f else gapX,
//        if (abs(gapY) < Pi * 0.01f) 0f else gapY
//    )
    if (gapVector.x == 0f && gapVector.y == 0f)
      Vector2.zero
    else {
      val changeLength = min(gapVector.length(), maxChange) * 0.9f
//    if (changeLength < maxChange)
//      Vector2(idealRotationX, idealRotationY)
//    else {
      assert(!(gapVector.normalize() * changeLength).x.isNaN())
      gapVector.normalize() * changeLength
//      gapVector
    }
//    }
  } else
    Vector2.zero

  val rotation = initialRotation + rotationAlignment
  assert(!rotation.x.isNaN())
  return camera.copy(
      rotation = rotation,
      orientationVelocity = orientationVelocity,
      pivotLocation = newLocation,
      distance = adjustCameraDistance(dynamicsWorld, cameraCollisionMask, body.position, getCameraOrientation(rotation), delta, camera.distance),
      facingDestination = facingDestination
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
  val facingDestination = thirdPersonRig.facingDestination
  return if (facingDestination != null && facingDestination != facing) {
    val facingGap = getAngleCourse(facing, facingDestination)
    val maxFacingChange = 18f * delta
    val change = minMax(facingGap, -maxFacingChange, maxFacingChange)
//    println("$change $facingGap ${movement.offset} ${movement.offset.length()}")
    Vector3(0f, 0f, facing + change
    )
  } else
    facingRotation
}

fun newThirdPersonRig(actorLocation: Vector3, actorFacingZ: Float): ThirdPersonRig {
  return ThirdPersonRig(
      pivotLocation = getCameraPivotLocation(actorLocation),
      rotation = Vector2(0f, actorFacingZ),
      distance = targetCameraDistance,
      facingDestination = actorFacingZ
  )
}
