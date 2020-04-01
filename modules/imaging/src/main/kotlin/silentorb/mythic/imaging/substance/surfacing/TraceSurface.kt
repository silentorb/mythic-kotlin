package silentorb.mythic.imaging.substance.surfacing

import silentorb.mythic.imaging.substance.DistanceFunction
import silentorb.mythic.imaging.substance.calculateNormal
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3

data class LongestLineCandidate(
    val distance: Float,
    val position: Vector3
)

fun getNormalRotation(normal: Vector3): Quaternion =
    Quaternion().rotationTo(Vector3(0f, 0f, 1f), normal)

fun projectFromNormalRotation(normalRotation: Quaternion, point: Vector2) =
    normalRotation.transform(Vector3(point.x, point.y, 0f))

tailrec fun castSurfaceRay(getDistance: DistanceFunction, tolerance: Float, stride: Float, origin: Vector3,
                           direction: Vector3, distance: Float, candidate: LongestLineCandidate?): LongestLineCandidate? {
  val newDistance = distance + stride
  val position = origin + direction * newDistance
  val sample = getDistance(position)
  return if (sample > tolerance || sample < tolerance) {
    candidate
  } else {
    val newCandidate = LongestLineCandidate(
        distance = newDistance,
        position = position
    )
    castSurfaceRay(getDistance, tolerance, stride, origin, direction, newDistance, newCandidate)
  }
}

tailrec fun findLongestLine(getDistance: DistanceFunction, tolerance: Float, stride: Float,
                            origin: Vector3,
                            normalRotation: Quaternion,
                            directions: List<Vector2>,
                            candidates: List<LongestLineCandidate>): LongestLineCandidate? {

  val direction = projectFromNormalRotation(normalRotation, directions.first())
  val candidate = castSurfaceRay(getDistance, tolerance, stride, origin, direction, 0f, null)
  val newCandidates = if (candidate == null)
    candidates
  else
    candidates
        .plus(candidate)
        .sortedByDescending { it.distance }
        .take(2)

  return if (directions.size < 2)
    candidates.firstOrNull()
  else
    findLongestLine(getDistance, tolerance, stride, origin, normalRotation, directions.drop(1), newCandidates)
}

fun findLongestLine(getDistance: DistanceFunction, tolerance: Float, stride: Float, origin: Vector3): Vector3? {
  val initialDirections = listOf(
      Vector2(1f, 0f),
      Vector2(0f, 1f),
      Vector2(-1f, 0f),
      Vector2(0f, -1f),

      Vector2(1f, 1f),
      Vector2(1f, -1f),
      Vector2(-1f, -1f),
      Vector2(-1f, 1f)
  )

  val normal = calculateNormal(getDistance, origin)
  val normalRotation = getNormalRotation(normal)

  return findLongestLine(getDistance, tolerance, stride, origin, normalRotation, initialDirections, listOf())
      ?.position
}
