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
const val cameraObstaclePadding = 0.1f

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

  val idealRotation = (characterRig.facingRotation.z + Pi) % Pi * 2f
  val rotation = if (orientationVelocity.x != 0f || orientationVelocity.y != 0f) {
    val pitch = minMax(camera.rotation.y - orientationVelocity.y * delta, -1.0f, 1.5f)
    val yaw = camera.rotation.x + orientationVelocity.x * delta
    Vector2(yaw, pitch)
  } else if (movement != null && facingDestination != idealRotation) {
    val gap = getAngleCourse(characterRig.facingRotation.z, idealRotation)
    if (abs(gap) > Pi * 0.8f)
      camera.rotation
    else {
      val maxChange = 0.3f * delta
      val rotationAlignment = minMax(gap, -maxChange, maxChange)
      camera.rotation.copy(
          x = camera.rotation.x + rotationAlignment
      )
    }
  } else
    camera.rotation

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
