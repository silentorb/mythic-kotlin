package silentorb.mythic.characters

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.happenings.CommandName
import silentorb.mythic.happenings.CommonCharacterCommands
import silentorb.mythic.happenings.Commands
import silentorb.mythic.happenings.filterCommands
import silentorb.mythic.physics.Body
import silentorb.mythic.physics.LinearImpulse
import silentorb.mythic.physics.PhysicsDeck
import silentorb.mythic.spatial.*

val playerMoveMap = mapOf(
    CommonCharacterCommands.moveLeft to Vector3(-1f, 0f, 0f),
    CommonCharacterCommands.moveRight to Vector3(1f, 0f, 0f),
    CommonCharacterCommands.moveUp to Vector3(0f, 1f, 0f),
    CommonCharacterCommands.moveDown to Vector3(0f, -1f, 0f)
)

fun joinInputVector(commands: Commands, commandMap: Map<CommandName, Vector3>): Vector3? {
  val forces = commands.mapNotNull {
    val vector = commandMap[it.type]
    if (vector != null && it.value > 0)
      vector * it.value
    else
      null
  }
  if (forces.isEmpty())
    return null

  val offset = forces.reduce { a, b -> a + b }
  return if (offset == Vector3.zero)
    Vector3.zero
  else {
    if (offset.length() > 1f)
      offset.normalize()
    else
      offset
  }
}

fun getHorizontalLookAtAngle(lookAt: Vector2fMinimal) =
    getAngle(Vector2(1f, 0f), lookAt.xy())

fun getVerticalLookAtAngle(lookAt: Vector3) =
    getAngle(Vector2(1f, 0f), Vector2(lookAt.xy().length(), lookAt.z))

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

fun allCharacterMovements(deck: PhysicsDeck, characterRigs: Table<CharacterRig>, commands: Commands): List<LinearImpulse> =
    characterRigs
        .filter { characterRigs[it.key]!!.isActive }
        .mapNotNull { characterMovementFp(filterCommands(it.key, commands), it.value, it.key, deck.bodies[it.key]!!) }
