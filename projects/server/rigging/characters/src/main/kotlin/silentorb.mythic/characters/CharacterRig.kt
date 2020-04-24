package silentorb.mythic.characters

import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import silentorb.mythic.spatial.times
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.physics.*
import silentorb.mythic.spatial.*
import silentorb.mythic.happenings.Commands
import silentorb.mythic.happenings.filterCommands
import silentorb.mythic.ent.firstFloatSortedBy
import kotlin.math.abs
import kotlin.math.min

const val defaultCharacterRadius = 0.3f
const val defaultCharacterHeight = 2.5f
const val characterGroundBuffer = 0.02f

const val groundedLinearDamping = 0.9f
const val airLinearDamping = 0f
const val airControlReduction = 0.4f

const val maxFootStepHeight = 0.6f

fun maxPositiveLookVelocityXChange() = 0.06f
fun maxNegativeLookVelocityXChange() = 0.15f

fun maxPositiveLookVelocityYChange() = 0.04f
fun maxNegativeLookVelocityYChange() = 0.08f

fun maxMoveVelocityChange() = 1f

fun lookSensitivity() = Vector2(.7f, .7f)

fun getFacingVector(orientation: Quaternion) =
    orientation * Vector3(1f, 0f, 0f)

data class AbsoluteOrientationForce(
    val body: Id,
    val orientation: Quaternion
)

fun footOffsets(radius: Float): List<Vector3> =
    createArcZ(radius + 0.9f, 8)

private const val noHitValue = 1f

private fun castFootStepRay(dynamicsWorld: btDiscreteDynamicsWorld, bodyPosition: Vector3, footHeight: Float,
                            shapeHeight: Float): (Vector3) -> Float? {
  val basePosition = bodyPosition + Vector3(0f, 0f, -shapeHeight / 2f + footHeight)
  val rayLength = footHeight * 2f
  val endOffset = Vector3(0f, 0f, -rayLength)
  return { it: Vector3 ->
    val start = basePosition + it
    val end = start + endOffset
    val result = castCollisionRay(dynamicsWorld, start, end)
    if (result != null)
      start.z - result.hitPoint.z
    else
      null
  }
}

data class CharacterRigHand(
    val characterRig: CharacterRig,
    val body: Body,
    val collisionObject: CollisionObject
)

fun updateCharacterStepHeight(bulletState: BulletState, hand: CharacterRigHand): Float {
  val body = hand.body
  val collisionObject = hand.collisionObject
  val shape = collisionObject.shape
  val radius = shape.radius
  val footHeight = maxFootStepHeight
  val offsets = footOffsets(radius).plus(Vector3.zero)
  val cast = castFootStepRay(bulletState.dynamicsWorld, body.position, footHeight, shape.height)
  val centerDistance = cast(Vector3.zero)
  if (centerDistance == null) {
    return noHitValue
  } else {
    val distances = offsets
        .mapNotNull(cast)
        .plus(centerDistance)

    return if (distances.any()) {
      val distance = distances.firstFloatSortedBy { it }
      distance - footHeight
    } else
      noHitValue
  }
}

fun isGrounded(characterRig: CharacterRig) = characterRig.groundDistance <= characterGroundBuffer

fun updateCharacterRigBulletBody(bulletState: BulletState): (Id, CharacterRig) -> Unit = { id, characterRig ->
  val groundDistance = characterRig.groundDistance
  val btBody = bulletState.dynamicBodies[id]!!
  val isGrounded = isGrounded(characterRig)
  if (isGrounded && (groundDistance < -0.01f || groundDistance > 0.1f)) {
    val stepHeight = -groundDistance

    if (stepHeight < 0f) {
      val k = 0
    }
    val transitionStepHeight = min(0.015f, stepHeight)
//    println("$groundDistance $transitionStepHeight")
    btBody.translate(toGdxVector3(Vector3(0f, 0f, transitionStepHeight)))
  }

  val linearDamping = if (isGrounded)
    groundedLinearDamping
  else
    airLinearDamping

  val gravity = if (isGrounded)
    com.badlogic.gdx.math.Vector3.Zero
  else
    staticGravity()

  btBody.setDamping(linearDamping, 0f)
  btBody.gravity = gravity
}

fun updateCharacterRigBulletBodies(bulletState: BulletState, characterRigs: Table<CharacterRig>) {
  characterRigs.forEach(updateCharacterRigBulletBody(bulletState))
}

fun characterOrientationZ(characterRig: CharacterRig) =
    Quaternion().rotateZ(characterRig.facingRotation.z - Pi / 2)

fun hoverCameraOrientationZ(characterRig: CharacterRig) =
    Quaternion().rotateZ(characterRig.hoverCamera!!.yaw - Pi / 2)

fun updateCharacterRigGroundedDistance(bulletState: BulletState, hand: CharacterRigHand): (CharacterRig) -> CharacterRig = { characterRig ->
  characterRig.copy(
      groundDistance = updateCharacterStepHeight(bulletState, hand)
  )
}

fun interpolateCharacterRigs(scalar: Float, first: Table<CharacterRig>, second: Table<CharacterRig>) =
    interpolateTables(scalar, first, second) { s, a, b ->
      a.copy(
          facingRotation = interpolate(s, a.facingRotation, b.facingRotation)
      )
    }
