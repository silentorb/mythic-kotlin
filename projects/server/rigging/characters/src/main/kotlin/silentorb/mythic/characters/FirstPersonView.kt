package silentorb.mythic.characters

import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.minMax

fun updateFirstPersonFacingRotation(facingRotation: Vector3, lookVelocity: Vector2, delta: Float): Vector3 {
  val next = facingRotation + fpCameraRotation(lookVelocity, delta)
  return Vector3(
      0f,
      minMax(next.y, -1.1f, 1.1f),
      next.z
  )
}
