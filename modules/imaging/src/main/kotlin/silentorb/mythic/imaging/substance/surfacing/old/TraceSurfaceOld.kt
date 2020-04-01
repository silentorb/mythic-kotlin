package silentorb.mythic.imaging.substance.surfacing.old

import silentorb.mythic.imaging.substance.calculateNormal
import silentorb.mythic.imaging.substance.surfacing.SurfacingConfig
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3

data class LongestLineCandidate(
    val distance: Float,
    val position: Vector3
)

// Used to precalculate the first half of the normal projection algorithm
fun getNormalRotation(normal: Vector3): Quaternion =
    Quaternion().rotationTo(Vector3(0f, 0f, 1f), normal)

// Used to execute the second half of the normal projection algorithm
fun projectFromNormalRotation(normalRotation: Quaternion, point: Vector2) =
    normalRotation.transform(Vector3(point.x, point.y, 0f))
/*
// Casts a ray along the surface of a volumetric shape and stops when the ray gets too far away from the surface
// If the origin of the ray is along a sharp edge or point, it is possible there to be no valid edge in this direction
tailrec fun castSurfaceRay(config: SurfacingConfig, origin: Vector3,
                           direction: Vector3, distance: Float, candidate: Vector3): Vector3 {
  val newDistance = distance + config.stride
  val position = origin + direction * newDistance
  val sample = config.getDistance(position)
  return if (sample > config.tolerance || sample < -config.tolerance) {
    candidate
  } else {
    castSurfaceRay(config, origin, direction, newDistance, position)
  }
}

tailrec fun tracePolygonBounds(config: SurfacingConfig,
                               origin: Vector3,
                               normalRotation: Quaternion,
                               directions: List<Vector2>,
                               candidates: List<Vector3>): List<Vector3> {
  return if (directions.none()) {
    candidates
  } else {
    val direction = projectFromNormalRotation(normalRotation, directions.first())
    val candidate = castSurfaceRay(config, origin, direction, 0f, origin)
    val newCandidates = candidates.plus(candidate)
    tracePolygonBounds(config, origin, normalRotation, directions.drop(1), newCandidates)
  }
}

// Following shape contours, finds the longest possible edge within a specified tolerance along any orthogonal direction
fun tracePolygonBounds(config: SurfacingConfig, origin: Vector3): List<Vector3> {
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

  val normal = calculateNormal(config.getDistance, origin)
  val normalRotation = getNormalRotation(normal)

  return tracePolygonBounds(config, origin, normalRotation, initialDirections, listOf())
}

fun createStartingPolygon(config: SurfacingConfig, origin: Vector3): Mesh {

}

fun traceSurface(config: SurfacingConfig, origin: Vector3) {
  var mesh = createStartingPolygon(config, origin)
  val incompleteEdgeCount = 1
  while(incompleteEdgeCount > 0) {
    // Get next edge

    // Get concave connected edges

    // Get approximately orthoganal vertices filtering out already connected vertices



  }
}
*/
