package silentorb.mythic.fathom.surfacing.old

import silentorb.mythic.fathom.misc.DistanceFunction
import silentorb.mythic.fathom.surfacing.GridBounds
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import silentorb.mythic.spatial.toVector3

fun voxelize(sampler: DistanceFunction, bounds: GridBounds, voxelsPerUnit: Int): Map<Vector3i, Float> {
  val scale = 1f / voxelsPerUnit.toFloat()
  val dimensions = (bounds.end - bounds.start) * voxelsPerUnit
  val start = bounds.start.toVector3()
  return (0 until dimensions.z).flatMap { z ->
    (0 until dimensions.y).flatMap { y ->
      (0 until dimensions.x).mapNotNull { x ->
        val location = Vector3(x.toFloat(), y.toFloat(), z.toFloat()) * scale + start
        val distance = sampler(location)
        if (distance <= scale)
          Vector3i(x, y, z) to 1f - distance / scale
        else
          null
      }
    }
  }
      .associate { it }
}
