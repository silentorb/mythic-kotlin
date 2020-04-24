package silentorb.mythic.characters

import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.minMax

fun updateFirstPersonCamera(commands: Commands, delta: Float): (CharacterRig) -> CharacterRig = { characterRig ->
  val lookVelocity = updateLookVelocity(commands, characterRig.turnSpeed * lookSensitivity(), characterRig.lookVelocity)
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
