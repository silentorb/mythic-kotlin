package silentorb.mythic.characters

import silentorb.mythic.happenings.CommandName
import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.minMax

data class MomentumConfig(
    val attack: Float,
    val sustain: Float,
    val release: Float
)

data class MomentumConfig2(
    val yaw: MomentumConfig,
    val pitch: MomentumConfig
)

operator fun MomentumConfig.times(value: Float): MomentumConfig =
    MomentumConfig(attack * value, sustain * value, release * value)

operator fun MomentumConfig2.times(value: Float): MomentumConfig2 =
    MomentumConfig2(yaw * value, pitch * value)

operator fun MomentumConfig2.times(value: Vector2): MomentumConfig2 =
    MomentumConfig2(yaw * value.x, pitch * value.y)

val firstPersonLookMap = mapOf(
    CharacterRigCommands.lookLeft to Vector3(0f, 0f, 1f),
    CharacterRigCommands.lookRight to Vector3(0f, 0f, -1f),
    CharacterRigCommands.lookUp to Vector3(0f, 1f, 0f),
    CharacterRigCommands.lookDown to Vector3(0f, -1f, 0f)
)

fun applyLookForce(lookMap: Map<CommandName, Vector3>, turnSpeed: Vector2, commands: Commands): Vector2 {
  val offset3 = joinInputVector(commands, lookMap)
  return if (offset3 != null) {
    val offset2 = Vector2(offset3.z, offset3.y)
    offset2 * turnSpeed
  } else
    Vector2()
}

fun characterLookForce(turnSpeed: Vector2, commands: Commands): Vector2 =
    applyLookForce(firstPersonLookMap, turnSpeed, commands)

fun fpCameraRotation(velocity: Vector2, delta: Float): Vector3 {
  val deltaVelocity = velocity * delta
  return if (velocity.y != 0f || velocity.x != 0f) {
    Vector3(0f, deltaVelocity.y, deltaVelocity.x)
  } else
    Vector3.zero
}

fun transitionAxis(negativeMaxChange: Float, positiveMaxChange: Float, current: Float, target: Float): Float {
  return if (target == current)
    current
  else {
    val (minOffset, maxOffset) = if (current == 0f)
      Pair(positiveMaxChange, positiveMaxChange)
    else if (current > 0f)
      Pair(negativeMaxChange, positiveMaxChange)
    else
      Pair(positiveMaxChange, negativeMaxChange)
    minMax(target, current - minOffset, current + maxOffset)
  }
}

data class AxisVelocityLimits(
    val positiveX: Float,
    val negativeX: Float,
    val positiveY: Float,
    val negativeY: Float
)

fun updateLookVelocity(commands: Commands, turnSpeed: Vector2, velocityLimits: AxisVelocityLimits,
                       lookVelocity: Vector2): Vector2 {
  val lookForce = characterLookForce(turnSpeed, commands)
  return Vector2(
      transitionAxis(velocityLimits.negativeX, velocityLimits.positiveX, lookVelocity.x, lookForce.x),
      transitionAxis(velocityLimits.negativeY, velocityLimits.positiveY, lookVelocity.y, lookForce.y)
  )
}

fun updateLookVelocity(commands: Commands, turnSpeed: Vector2, lookVelocity: Vector2): Vector2 {
  val lookForce = characterLookForce(turnSpeed, commands)
  return Vector2(
      transitionAxis(maxNegativeLookVelocityXChange(), maxPositiveLookVelocityXChange(), lookVelocity.x, lookForce.x),
      transitionAxis(maxNegativeLookVelocityYChange(), maxPositiveLookVelocityYChange(), lookVelocity.y, lookForce.y)
  )
}

//fun updateCharacterRigFacing(
//    dynamicsWorld: btDiscreteDynamicsWorld,
//    cameraCollisionMask: Int,
//    bodies: Table<Body>,
//    id: Id,
//    commands: Commands,
//    movements: List<CharacterRigMovement>,
//    target: Vector3,
//    delta: Float
//): (CharacterRig) -> CharacterRig = { characterRig ->
//  if (characterRig.viewMode == ViewMode.firstPerson)
//    updateFirstPersonCamera(commands, delta)(characterRig)
//  else
//    updateThirdPersonCamera(dynamicsWorld, cameraCollisionMask, bodies[id]!!, commands, movements, delta, characterRig, target)
//}
