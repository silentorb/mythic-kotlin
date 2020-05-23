package silentorb.mythic.imaging.fathoming.sampling

import silentorb.mythic.imaging.fathoming.surfacing.GridBounds
import silentorb.mythic.imaging.fathoming.surfacing.snapToSurfaceIncludingNormal
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.toVector3
import kotlin.math.abs

fun sampleFunction(config: SamplingConfig, bounds: GridBounds): List<SamplePoint> {
  val resolution = config.resolution
  val sampleRange = 1.5f / resolution
  val getDistance = config.getDistance
  val getColor = config.getColor
  val stepDimensions = (bounds.end - bounds.start) * config.resolution
  val sampleCount = stepDimensions.x * stepDimensions.y * stepDimensions.z
  val sliceSize = stepDimensions.x * stepDimensions.y
  val start = bounds.start.toVector3()
  val pointSize = 5f / resolution

  return (0 until sampleCount).mapNotNull { i ->
    val z = i / sliceSize
    val zRemainder = i - sliceSize * z
    val y = zRemainder / stepDimensions.x
    val x = zRemainder - y * stepDimensions.x

    val startingLocation = start + Vector3(x.toFloat(), y.toFloat(), z.toFloat()) / resolution.toFloat()
    val startingDistance = getDistance(startingLocation)
    if (abs(startingDistance) > sampleRange)
      null
    else {
      val (location, normal) = snapToSurfaceIncludingNormal(getDistance, startingLocation)
      SamplePoint(
          location = location,
          normal = normal,
          color = getColor(location),
          size = pointSize
      )
    }
  }
}
