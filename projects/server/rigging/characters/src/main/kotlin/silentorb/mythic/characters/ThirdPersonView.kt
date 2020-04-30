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

fun getCameraLocation(pivotLocation: Vector3, orientation: Quaternion, distance: Float): Vector3 =
    pivotLocation + orientation * Vector3(-distance, 0f, 0f)

fun adjustCameraDistance(dynamicsWorld: btDiscreteDynamicsWorld, cameraCollisionMask: Int,
                         rayStart: Vector3, orientation: Quaternion, delta: Float, distance: Float): Float {
  val rayEnd = rayStart + orientation * Vector3(-targetCameraDistance * 2f, 0f, 0f)
  val hit = firstRayHit(dynamicsWorld, rayStart, rayEnd, cameraCollisionMask)
  val newDistance = if (hit != null) {
    min(targetCameraDistance, hit.hitPoint.distance(rayStart) - cameraObstaclePadding)
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

fun lookAtTargetOffset(location: Vector3, target: Vector3, rotation: Vector2, idealRotationY: Float, delta: Float): Vector2 {
  val targetRotationX = atan(target.xy() - location.xy())
  val rawGapX = getAngleCourse(rotation.x, targetRotationX)
  val gapY = getAngleCourse(rotation.y, idealRotationY - getVerticalLookAtAngle((target - location).normalize()))
  val margin = 0.4f
  val gapX = if (abs(rawGapX) < margin) 0f else rawGapX
  return if (gapX == 0f && gapY == 0f) {
    println("* ${rotation.x} $gapX $targetRotationX")
    Vector2.zero
  } else {
    val length = abs(gapX * gapX * gapX) * 2f * delta
    val changeX = minMax(gapX, -length, length)
    if (gapX != 0f)
      println("${rotation.x} $gapX $targetRotationX $changeX")

    Vector2(
        changeX,
        gapY * delta
    )
  }
}

fun updatePivotLocation(location: Vector3, destination: Vector3, delta: Float): Vector3 {
  val difference = destination - location
  val differenceLength = difference.length()
  val changeDistance = min(differenceLength, 0.1f * delta + differenceLength * 10f * delta)
  val offset = difference.normalize() * changeDistance
  return location + offset
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
    val limits = AxisVelocityLimits(
        positiveX = 0.2f,
        negativeX = 0.3f,
        positiveY = 0.1f,
        negativeY = 0.15f
    )
    updateLookVelocity(commands, Vector2(3.5f, 2f), limits, camera.orientationVelocity)
  } else
    Vector2.zero

  val pivotDestination = getCameraPivotLocation(body.position)
//  val pivotDestination = normalPivotLocation
//    normalPivotLocation
//  else
//    target
////    getCenter(normalPivotLocation, target)

  val newLocation = if (camera.pivotLocation != pivotDestination) {
    updatePivotLocation(camera.pivotLocation, pivotDestination, delta)
  } else
    camera.pivotLocation

  // Not currently supporting aggregating movement events from multiple sources
  assert(movements.size < 2)

  val movement = if (target == null)
    movements.firstOrNull()
  else
    null

  if (movements.any()) {
    val k = 0
  }

  val facingDestination = if (movement != null) {
    getHorizontalLookAtAngle(movement.offset)
  } else if (target != null) {
    getHorizontalLookAtAngle(target - body.position)
  } else
    null

  val initialRotation = if (orientationVelocity.x != 0f || orientationVelocity.y != 0f) {
    val pitch = minMax(camera.rotation.y - orientationVelocity.y * delta, -1.0f, 1.4f)
    val yaw = normalizeRadialAngle(camera.rotation.x + orientationVelocity.x * delta)
    Vector2(yaw, pitch)
  } else
    camera.rotation

  val idealRotationX = facingDestination ?: characterRig.facingRotation.z
  val idealRotationY = 0.4f
  val rotationAlignment = if (target != null)
    lookAtTargetOffset(newLocation, target, initialRotation, idealRotationY, delta)
  else if (movement != null && (initialRotation.x != idealRotationX || initialRotation.y != idealRotationY)) {
    Vector2.zero
//    val initialGapX = getAngleCourse(initialRotation.x, idealRotationX)
//    val gapY = getAngleCourse(initialRotation.y, idealRotationY)
//    val isCleanlyReversed = abs(initialGapX) > Pi * 0.8f
//    val gapX = if (isCleanlyReversed)
//      0f
//    else
//      initialGapX
//
//    if (gapX == 0f && gapY == 0f)
//      Vector2.zero
//    else {
//      val gapVector = Vector2(gapX, gapY)
//      val gapLength = gapVector.length()
//      val mod = gapLength * gapLength * delta
//      val changeLength = minMax(gapVector.length(), -mod, mod)
//      gapVector.normalize() * changeLength
//    }
  } else
    Vector2.zero

  val rotation = Vector2(
      normalizeRadialAngle(initialRotation.x + rotationAlignment.x),
      initialRotation.y + rotationAlignment.y
  )
  assert(!rotation.x.isNaN())
  return camera.copy(
      rotation = rotation,
      orientationVelocity = orientationVelocity,
      pivotLocation = newLocation,
      distance = adjustCameraDistance(dynamicsWorld, cameraCollisionMask, newLocation, getCameraOrientation(rotation), delta, camera.distance),
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
    freedomTable: Table<Freedoms>,
    delta: Float
): (Id, ThirdPersonRig) -> ThirdPersonRig {
  val allCommands = events
      .filterIsInstance<CharacterCommand>()

  val allMovements = events
      .filterIsInstance<CharacterRigMovement>()

  return { id, thirdPersonRig ->
    val freedoms = freedomTable[id] ?: Freedom.none
    val body = bodies[id]!!
    val characterRig = characterRigs[id]!!
    val target = bodies[targets[id]]?.position
    val commands = allCommands.filter { it.target == id }
    val movements = allMovements.filter { it.actor == id }
    if (hasFreedom(freedoms, Freedom.orbiting))
      updateThirdPersonCamera(dynamicsWorld, cameraCollisionMask, commands, movements, delta, body, characterRig, target, thirdPersonRig)
    else
      thirdPersonRig
  }
}

fun updateThirdPersonFacingRotation(facingRotation: Vector3, thirdPersonRig: ThirdPersonRig,
                                    delta: Float): Vector3 {
  val facing = facingRotation.z
  val facingDestination = thirdPersonRig.facingDestination
  return if (facingDestination != null && facingDestination != facing) {
    val facingGap = getAngleCourse(facing, facingDestination)
    val maxFacingChange = 0.3f * delta + facingGap * facingGap * 2f * delta
    val change = minMax(facingGap, -maxFacingChange, maxFacingChange)
//    println("$change $facingGap ${movement.offset} ${movement.offset.length()}")
    Vector3(0f, 0f, normalizeRadialAngle(facing + change))
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
