package silentorb.mythic.characters

import silentorb.mythic.happenings.CommandName
import silentorb.mythic.happenings.CommonCharacterCommands
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

private val gamepadLookSensitivity = Vector2(1f, 1f)

private val firstPersonLookMomentum = MomentumConfig2(
    MomentumConfig(1.7f, 4f, 1f),
    MomentumConfig(1f, 4f, 1f)
)

private val thirdPersonLookMomentum = MomentumConfig2(
    MomentumConfig(1.7f, 4f, 1f),
    MomentumConfig(1f, 4f, 1f)
)

val firstPersonLookMap = mapOf(
    CommonCharacterCommands.lookLeft to Vector3(0f, 0f, 1f),
    CommonCharacterCommands.lookRight to Vector3(0f, 0f, -1f),
    CommonCharacterCommands.lookUp to Vector3(0f, 1f, 0f),
    CommonCharacterCommands.lookDown to Vector3(0f, -1f, 0f)
)

val cameraLookMap = mapOf(
    CommonCharacterCommands.cameraLookLeft to Vector3(0f, 0f, 1f),
    CommonCharacterCommands.cameraLookRight to Vector3(0f, 0f, -1f),
    CommonCharacterCommands.cameraLookUp to Vector3(0f, 1f, 0f),
    CommonCharacterCommands.cameraLookDown to Vector3(0f, -1f, 0f)
)

fun applyLookForce(lookMap: Map<CommandName, Vector3>, character: CharacterRig, commands: Commands): Vector2 {
  val offset3 = joinInputVector(commands, lookMap)
  return if (offset3 != null) {
    val offset2 = Vector2(offset3.z, offset3.y)
    offset2 * lookSensitivity() * character.turnSpeed
  } else
    Vector2()
}

fun characterLookForce(character: CharacterRig, commands: Commands): Vector2 =
    applyLookForce(firstPersonLookMap, character, commands)

fun fpCameraRotation(velocity: Vector2, delta: Float): Vector3 {
  val deltaVelocity = velocity * delta
  return if (velocity.y != 0f || velocity.x != 0f) {
    Vector3(0f, deltaVelocity.y, deltaVelocity.x)
  } else
    Vector3()
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

fun updateCharacterRigFacing(commands: Commands, delta: Float): (CharacterRig) -> CharacterRig = { characterRig ->
  val lookForce = characterLookForce(characterRig, commands)
  val lookVelocity = Vector2(
      transitionAxis(maxNegativeLookVelocityXChange(), maxPositiveLookVelocityXChange(), characterRig.lookVelocity.x, lookForce.x),
      transitionAxis(maxNegativeLookVelocityYChange(), maxPositiveLookVelocityYChange(), characterRig.lookVelocity.y, lookForce.y)
  )
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

//fun updateTpCameraRotation(player: Player, character: CharacterRig, delta: Float): Vector3? {
//  val velocity = character.lookVelocity
//  val deltaVelocity = velocity * delta
//  return if (velocity.y != 0f || velocity.x != 0f) {
//    if (player.viewMode == ViewMode.firstPerson)
//      Vector3(0f, deltaVelocity.y, deltaVelocity.x)
//    else {
//      val hoverCamera = player.hoverCamera
//      hoverCamera.pitch += deltaVelocity.y
//      hoverCamera.yaw += deltaVelocity.y
//      val hoverPitchMin = -1.0f // Up
//      val hoverPitchMax = 0.0f // Down
//
//      if (hoverCamera.pitch > hoverPitchMax)
//        hoverCamera.pitch = hoverPitchMax
//
//      if (hoverCamera.pitch < hoverPitchMin)
//        hoverCamera.pitch = hoverPitchMin
//
//      null
////      println("p " + hoverCamera.pitch + ", y" + hoverCamera.yaw + " |  vp " + player.lookVelocity.y + ",vy " + player.lookVelocity.z)
//    }
//  } else
//    null
//}
