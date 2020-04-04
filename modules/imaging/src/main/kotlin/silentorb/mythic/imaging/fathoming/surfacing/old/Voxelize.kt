package silentorb.mythic.imaging.fathoming.surfacing.old

import silentorb.mythic.imaging.fathoming.DistanceFunction
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import java.nio.FloatBuffer

fun voxelize(sampler: DistanceFunction, dimensions: Vector3i, depth: Int, scale: Float): FloatBuffer {
  val buffer = FloatBuffer.allocate(dimensions.x * dimensions.y * dimensions.z * depth)
  val half = dimensions / 2
  val startX = -half.x
  val startY = -half.y
  val startZ = -half.z
  for (z in startX until half.z) {
    for (y in startY until half.y) {
      for (x in startZ until half.x) {
        val distance = sampler(Vector3(x.toFloat() * scale, y.toFloat() * scale, z.toFloat() * scale))
        val value = if (distance <= scale)
          1f - distance / scale
        else
          0f
        buffer.put(value)
      }
    }
  }
  return buffer
}
