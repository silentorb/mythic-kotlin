package silentorb.mythic.cameraman

import silentorb.mythic.cameraman.fpCameraRotation
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.minMax
import silentorb.mythic.spatial.normalizeRadialAngle

fun updateFirstPersonFacingRotation(facingRotation: Vector2, mouseLookOffset: Vector2?, lookVelocity: Vector2, delta: Float): Vector2 {
  val next = if (mouseLookOffset != null)
    facingRotation + mouseLookOffset * Vector2(2f, 1.3f)
  else
    facingRotation + fpCameraRotation(lookVelocity, delta)

  return Vector2(
      normalizeRadialAngle(next.x),
      minMax(next.y, -1.1f, 1.1f)
  )
}

fun updateFirstPersonFacingRotation(facingRotation: Quaternion, mouseLookOffset: Vector2?, lookVelocity: Vector2, delta: Float): Quaternion {
  val offset = if (mouseLookOffset != null)
    mouseLookOffset * Vector2(2f, 1.3f)
  else
    fpCameraRotation(lookVelocity, delta)

  return facingRotation * Quaternion()
      .rotateZ(offset.x)
      .rotateY(offset.y)

//  return Vector2(
//      normalizeRadialAngle(next.x),
//      minMax(next.y, -1.1f, 1.1f)
//  )
}
