package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i

fun isInsideBounds(bounds: DecimalBounds, position: Vector3) =
    position.x >= bounds.start.x &&
        position.y >= bounds.start.y &&
        position.z >= bounds.start.z &&
        position.x < bounds.end.x &&
        position.y < bounds.end.y &&
        position.z < bounds.end.z

fun getBoundsDimensions(bounds: GridBounds): Vector3i =
    bounds.end - bounds.start

fun getBoundsCellCount(bounds: GridBounds): Int {
  val dimensions = getBoundsDimensions(bounds)
  return dimensions.x * dimensions.y * dimensions.z
}
