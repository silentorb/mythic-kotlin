package silentorb.mythic.fathom.surfacing.old.marching

import silentorb.mythic.fathom.misc.DistanceFunction
import silentorb.mythic.fathom.surfacing.GridBounds
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.toVector3

fun voxelize(sampler: DistanceFunction, bounds: GridBounds, depth: Int, voxelsPerUnit: Int): FloatArray {
  val scale = 1f / voxelsPerUnit.toFloat()
  val dimensions = (bounds.end - bounds.start) * voxelsPerUnit
  val buffer = FloatArray(dimensions.x * dimensions.y * dimensions.z * depth)
  var i = 0
  val start = bounds.start.toVector3()
  for (z in 0 until dimensions.z) {
    for (y in 0 until dimensions.y) {
      for (x in 0 until dimensions.x) {
        val location = Vector3(x.toFloat(), y.toFloat(), z.toFloat()) * scale + start
        val distance = sampler(location)
        val value = if (distance <= scale)
          1f - distance / scale
        else
          0f
        buffer[i++] = value
      }
    }
  }
  return buffer
}
