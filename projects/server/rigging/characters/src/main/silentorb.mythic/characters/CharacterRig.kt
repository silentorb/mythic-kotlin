package silentorb.mythic.characters

import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import org.joml.times
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.physics.*
import silentorb.mythic.spatial.*
import silentorb.mythic.happenings.Commands
import silentorb.mythic.happenings.filterCommands
import silentorb.mythic.ent.firstFloatSortedBy
import kotlin.math.min

const val defaultCharacterRadius = 0.3f
const val defaultCharacterHeight = 1.2f
const val characterGroundBuffer = 0.02f

const val groundedLinearDamping = 0.9f
const val airLinearDamping = 0f
const val airControlReduction = 0.4f

const val maxFootStepHeight = 0.6f

fun maxPositiveLookVelocityChange() = 0.1f
fun maxNegativeLookVelocityChange() = 0.2f
fun maxMoveVelocityChange() = 1f

fun lookSensitivity() = Vector2(.7f, .7f)

data class CharacterRig(
    val facingRotation: Vector3 = Vector3(),
    val isActive: Boolean,
    val groundDistance: Float = 0f,
    val lookVelocity: Vector2 = Vector2(),
    val maxSpeed: Float,
    val turnSpeed: Vector2
) {
  val facingQuaternion: Quaternion
    get() = Quaternion()
        .rotateZ(facingRotation.z)
        .rotateY(-facingRotation.y)

  val facingVector: Vector3
    get() = facingQuaternion * Vector3(1f, 0f, 0f)
}

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
    castCollisionRay(dynamicsWorld, start, end)?.distance
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
    val transitionStepHeight = min(0.03f, stepHeight)
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

fun getMovementImpulseVector(baseSpeed: Float, velocity: Vector3, commandVector: Vector3): Vector3 {
  val rawImpulseVector = commandVector * 1.5f - velocity
  val finalImpulseVector = if (rawImpulseVector.length() > baseSpeed)
    rawImpulseVector.normalize() * baseSpeed
  else
    rawImpulseVector

  return finalImpulseVector
}

fun characterMovementFp(commands: Commands, characterRig: CharacterRig, id: Id, body: Body): LinearImpulse? {
  val offsetVector = joinInputVector(commands, playerMoveMap)
  return if (offsetVector != null) {
    val airControlMod = if (isGrounded(characterRig)) 1f else airControlReduction
    val direction = characterOrientationZ(characterRig) * offsetVector * airControlMod
    val baseSpeed = characterRig.maxSpeed
    val maxImpulseLength = baseSpeed
    val commandVector = direction * maxImpulseLength
    val horizontalVelocity = body.velocity.copy(z = 0f)
    val impulseVector = getMovementImpulseVector(baseSpeed, horizontalVelocity, commandVector)
    val finalImpulse = impulseVector * 5f
    LinearImpulse(body = id, offset = finalImpulse)
  } else {
    null
  }
}

fun transitionVector(maxChange: Float, current: Vector3, target: Vector3): Vector3 {
  val diff = target - current
  val diffLength = diff.length()
  return if (diffLength != 0f) {
    if (diffLength < maxChange)
      target
    else {
      val adjustment = if (diffLength > maxChange)
        diff.normalize() * maxChange
      else
        diff

      current + adjustment
    }
  } else
    current
}

fun transitionVector(negativeMaxChange: Float, positiveMaxChange: Float, current: Vector2, target: Vector2): Vector2 {
  val diff = target - current
  val diffLength = diff.length()
  val maxChange = if (current.length() < target.length())
    positiveMaxChange
  else
    negativeMaxChange

  return if (diffLength != 0f) {
    if (diffLength < maxChange)
      target
    else {
      val adjustment = if (diffLength > maxChange)
        diff.normalize() * maxChange
      else
        diff

      current + adjustment
    }
  } else
    current
}

fun updateCharacterRigFacing(commands: Commands, delta: Float): (CharacterRig) -> CharacterRig = { characterRig ->
  val lookForce = characterLookForce(characterRig, commands)
  val lookVelocity = transitionVector(maxNegativeLookVelocityChange(), maxPositiveLookVelocityChange(),
      characterRig.lookVelocity, lookForce)
  val facingRotation = characterRig.facingRotation + fpCameraRotation(lookVelocity, delta)

  characterRig.copy(
      lookVelocity = lookVelocity,
       facingRotation = Vector3(
          0f,
          minMax(facingRotation.y, -1.1f, 1.1f),
          facingRotation.z
      )
  )
}

fun updateCharacterRigGroundedDistance(bulletState: BulletState, hand: CharacterRigHand): (CharacterRig) -> CharacterRig = { characterRig ->
  characterRig.copy(
      groundDistance = updateCharacterStepHeight(bulletState, hand)
  )
}

fun allCharacterMovements(deck: PhysicsDeck, characterRigs: Table<CharacterRig>, commands: Commands): List<LinearImpulse> =
    characterRigs
        .filter { characterRigs[it.key]!!.isActive }
        .mapNotNull { characterMovementFp(filterCommands(it.key, commands), it.value, it.key, deck.bodies[it.key]!!) }

fun interpolateCharacterRigs(scalar: Float, first: Table<CharacterRig>, second: Table<CharacterRig>) =
    interpolateTables(scalar, first, second) { s, a, b ->
      a.copy(
          facingRotation = interpolate(s, a.facingRotation, b.facingRotation)
      )
    }