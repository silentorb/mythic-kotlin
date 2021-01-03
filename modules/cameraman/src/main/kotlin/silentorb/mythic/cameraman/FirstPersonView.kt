package silentorb.mythic.cameraman

import silentorb.mythic.spatial.*

fun updateFirstPersonFacingRotation(facingRotation: Vector2, mouseLookOffset: Vector2?, lookVelocity: Vector2, delta: Float): Vector2 {
  val next = if (mouseLookOffset != null)
    facingRotation + mouseLookOffset * Vector2(2f, 1.3f)
  else
    facingRotation + fpCameraRotation(lookVelocity, delta)

  val verticalMax = 0.9f * Pi / 2f
  return Vector2(
      normalizeRadialAngle(next.x),
      minMax(next.y, -verticalMax, verticalMax)
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
}
