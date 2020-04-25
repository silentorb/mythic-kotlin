package silentorb.mythic.characters

import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.minMax

fun updateFirstPersonCamera(commands: Commands, delta: Float): (CharacterRig) -> CharacterRig = { characterRig ->
  val lookVelocity = updateLookVelocity(commands, characterRig.turnSpeed * lookSensitivity(), characterRig.firstPersonLookVelocity)
  val facingRotation = characterRig.facingRotation + fpCameraRotation(lookVelocity, delta)

  characterRig.copy(
      firstPersonLookVelocity = lookVelocity,
      facingRotation = Vector3(
          0f,
          minMax(facingRotation.y, -1.1f, 1.1f),
          facingRotation.z
      )
  )
}

fun updateFirstPersonFacingRotation(facingRotation: Vector3, lookVelocity: Vector2, delta: Float): Vector3 {
  val next = facingRotation + fpCameraRotation(lookVelocity, delta)
  return Vector3(
      0f,
      minMax(next.y, -1.1f, 1.1f),
      next.z
  )
}
