package silentorb.mythic.fathom.misc

import silentorb.mythic.spatial.Vector3

private const val normalStep = 0.001f

// , hook: SdfHook
fun getNormal(getDistance: DistanceFunction, position: Vector3): Vector3 {
  fun accumulateDimension(offset: Vector3) =
      getDistance(position + offset).second - getDistance(position - offset).second

//  (1..6).forEach { hook() }

  return Vector3(
      accumulateDimension(Vector3(0f + normalStep, 0f, 0f)),
      accumulateDimension(Vector3(0f, 0f + normalStep, 0f)),
      accumulateDimension(Vector3(0f, 0f, 0f + normalStep))
  ).normalize()
}
