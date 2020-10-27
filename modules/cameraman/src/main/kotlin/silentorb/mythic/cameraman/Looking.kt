package silentorb.mythic.cameraman

import silentorb.mythic.happenings.CommandName
import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.minMax

data class MomentumConfig(
    val positiveIncrement: Float,
    val maxVelocity: Float,
    val negativeIncrement: Float
)

data class MomentumAxis(
    val horizontal: MomentumConfig,
    val vertical: MomentumConfig
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
    applyLookForce(lookMap, turnSpeed, commands)

fun applyLookForce(momentumAxis: MomentumAxis, commands: Commands): Vector2 {
  val offset3 = joinInputVector(commands, lookMap)
  return if (offset3 != null) {
    val offset2 = Vector2(offset3.z, offset3.y)
    offset2 * Vector2(momentumAxis.horizontal.maxVelocity, momentumAxis.vertical.maxVelocity)
  } else
    Vector2()
}

fun fpCameraRotation(velocity: Vector2, delta: Float): Vector2 =
    if (velocity.y != 0f || velocity.x != 0f) {
      velocity * delta
    } else
      Vector2.zero

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

fun updateLookVelocityFirstPerson(commands: Commands, momentumAxis: MomentumAxis, lookVelocity: Vector2): Vector2 {
  val lookForce = applyLookForce(momentumAxis, commands)
  return Vector2(
      transitionAxis(momentumAxis.horizontal.negativeIncrement, momentumAxis.horizontal.positiveIncrement, lookVelocity.x, lookForce.x),
      transitionAxis(momentumAxis.vertical.negativeIncrement, momentumAxis.vertical.positiveIncrement, lookVelocity.y, lookForce.y)
  )
}

fun updateLookVelocityThirdPerson(commands: Commands, turnSpeed: Vector2, momentumAxis: MomentumAxis,
                                  lookVelocity: Vector2): Vector2 {
  val lookForce = characterLookForce(turnSpeed, commands)
  return Vector2(
      transitionAxis(momentumAxis.horizontal.negativeIncrement, momentumAxis.horizontal.positiveIncrement, lookVelocity.x, lookForce.x),
      transitionAxis(momentumAxis.vertical.negativeIncrement, momentumAxis.vertical.positiveIncrement, lookVelocity.y, lookForce.y)
  )
}

fun defaultLookMomentumAxis() =
    MomentumAxis(
        horizontal = MomentumConfig(
            positiveIncrement = 0.1f,
            maxVelocity = 3.5f,
            negativeIncrement = 0.3f
        ),
        vertical = MomentumConfig(
            positiveIncrement = 0.1f,
            maxVelocity = 2f,
            negativeIncrement = 0.15f
        )
    )
