package silentorb.mythic.imaging.fathoming.sampling

import silentorb.mythic.imaging.fathoming.surfacing.GridBounds
import silentorb.mythic.imaging.fathoming.surfacing.snapToSurfaceIncludingNormal
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.toVector3
import kotlin.math.abs

fun samplePoint(config: SamplingConfig, start: Vector3): SamplePoint? {
  val resolution = config.resolution
  val sampleRange = 1.5f / resolution
  val getDistance = config.getDistance
  val getColor = config.getColor
  val pointSize = config.pointSize / resolution

  val startingDistance = getDistance(start)
  return if (abs(startingDistance) > sampleRange)
    null
  else {
    val (location, normal) = snapToSurfaceIncludingNormal(getDistance, start)
    val rgb = getColor(location)
    SamplePoint(
        location = location,
        normal = normal,
        color = Vector4(rgb.x, rgb.y, rgb.z, 1f),
        size = pointSize
    )
  }
}

fun indexToVector3i(sliceSize: Int, xSize: Int, index: Int): Vector3i {
  val z = index / sliceSize
  val zRemainder = index - sliceSize * z
  val y = zRemainder / xSize
  val x = zRemainder - y * xSize
  return Vector3i(x, y, z)
}

fun sampleFunction(config: SamplingConfig, bounds: GridBounds): List<SamplePoint> {
  val boundsDimensions = bounds.end - bounds.start

  // Sometimes intermediate sdf changes can cause massive spikes in bounding size
  if (boundsDimensions.x > 100 || boundsDimensions.y > 100 || boundsDimensions.z > 100)
    return listOf()

  val resolution = config.resolution
  val stepDimensions = boundsDimensions * config.resolution
  val sampleCount = stepDimensions.x * stepDimensions.y * stepDimensions.z
  val sliceSize = stepDimensions.x * stepDimensions.y
  val start = bounds.start.toVector3()

  return (0 until sampleCount).mapNotNull { index ->
    val offset = indexToVector3i(sliceSize, stepDimensions.x, index).toVector3()
    val startingLocation = start + offset / resolution.toFloat()
    samplePoint(config, startingLocation)
  }
}

data class CellSampler(
    val cellCount: Int,
    val sampler: (Int) -> List<SamplePoint>
)

fun sampleCells(config: SamplingConfig, bounds: GridBounds, cellDivisions: Int = 1): CellSampler {
  val unitDimensions = bounds.end - bounds.start
  val subDimensions = unitDimensions * cellDivisions
  val cellCount = subDimensions.x * subDimensions.y * subDimensions.z
  val cellsSliceSize = subDimensions.x * subDimensions.y
  val cellsSizeX = subDimensions.x

  val cellLength = config.resolution
  val cellSliceSize = cellLength * cellLength
  val cellSampleCount = cellLength * cellLength * cellLength
  val resolution = config.resolution.toFloat()
  val start = bounds.start.toVector3()

  return CellSampler(cellCount) { step ->
    val cellStart = start + indexToVector3i(cellsSliceSize, cellsSizeX, step).toVector3()
    (0 until cellSampleCount).mapNotNull { index ->
      val offset = indexToVector3i(cellSliceSize, cellLength, index).toVector3()
      samplePoint(config, cellStart + offset / resolution)
    }
  }
}
