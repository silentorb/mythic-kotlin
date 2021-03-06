package silentorb.mythic.fathom.surfacing.old

import silentorb.mythic.fathom.misc.DistanceFunction
import silentorb.mythic.spatial.Vector3

// TODO: Modify direction with each iteration to hone in on geometry and ensure a surface is hit
tailrec fun findSurfacingStart(getDistance: DistanceFunction, tolerance: Float, origin: Vector3, direction: Vector3): Vector3 {
  val (_, distance) = getDistance(origin)
  return when {
    distance < -tolerance -> throw Error("Could not find surfacing start")
    distance <= tolerance -> origin
    else -> findSurfacingStart(getDistance, tolerance, origin + direction * distance, direction)
  }
}
