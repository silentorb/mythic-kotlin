package silentorb.mythic.fathom.sampling

import silentorb.mythic.fathom.surfacing.GridBounds
import silentorb.mythic.fathom.surfacing.snapToSurfaceIncludingNormal
import silentorb.mythic.scenery.SamplePoint
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import silentorb.mythic.spatial.toVector3
import kotlin.math.abs

val subdivisionTemplate: List<Vector3> = (-1..1 step 2).flatMap { z ->
  (-1..1 step 2).flatMap { y ->
    (-1..1 step 2).map { x ->
      Vector3(x.toFloat(), y.toFloat(), z.toFloat()) / 2f
    }
  }
}

fun samplePoint(config: SamplingConfig, scale: Float, level: Int, start: Vector3): List<SamplePoint> {
  val sampleRange = 1.5f * scale
  val getDistance = config.getDistance
  val getShading = config.getShading

  val startingDistance = getDistance(start)
  return if (abs(startingDistance) > sampleRange)
    listOf()
  else {
    val (snappedLocation, normal) = snapToSurfaceIncludingNormal(getDistance, start)
    val normalOffsetStep = config.levelOffsetRange / config.levels.toFloat()
    val scalarNormalOffset = -config.levelOffsetRange / 2f + level * normalOffsetStep
    val location = snappedLocation + normal * scalarNormalOffset
    val shading = getShading(location)
    val point = SamplePoint(
        location = location,
        normal = normal,
        shading = shading,
        size = config.pointSize * scale,
        level = level
    )
    if (level < config.levels - 1) {
      val nextResolution = scale / 2f
      val nextLevel = level + 1
      listOf(point) + subdivisionTemplate.flatMap { offset ->
        samplePoint(config, nextResolution, nextLevel, start + offset * nextResolution)
      }
    } else {
      listOf(point)
    }
  }
}

fun indexToVector3i(sliceSize: Int, xSize: Int, index: Int): Vector3i {
  val z = index / sliceSize
  val zRemainder = index - sliceSize * z
  val y = zRemainder / xSize
  val x = zRemainder - y * xSize
  return Vector3i(x, y, z)
}

fun sampleForm(config: SamplingConfig, startingResolution: Int, bounds: GridBounds): List<SamplePoint> {
  val boundsDimensions = bounds.end - bounds.start

  // Sometimes intermediate sdf changes can cause massive spikes in bounding size
  if (boundsDimensions.x > 100 || boundsDimensions.y > 100 || boundsDimensions.z > 100)
    return listOf()

  val stepDimensions = boundsDimensions * startingResolution
  val sampleCount = stepDimensions.x * stepDimensions.y * stepDimensions.z
  val sliceSize = stepDimensions.x * stepDimensions.y
  val start = bounds.start.toVector3()

  return (0 until sampleCount).flatMap { index ->
    val offset = indexToVector3i(sliceSize, stepDimensions.x, index).toVector3()
    val startingLocation = start + offset / startingResolution.toFloat()
    samplePoint(config, 1f / startingResolution.toFloat(), 0, startingLocation)
  }
}

data class CellSampler(
    val cellCount: Int,
    val sampler: (Int) -> List<SamplePoint>
)

//fun sampleCells(config: SamplingConfig, bounds: GridBounds, cellDivisions: Int = 1): CellSampler {
//  val unitDimensions = bounds.end - bounds.start
//  val subDimensions = unitDimensions * cellDivisions
//  val cellCount = subDimensions.x * subDimensions.y * subDimensions.z
//  val cellsSliceSize = subDimensions.x * subDimensions.y
//  val cellsSizeX = subDimensions.x
//
//  val cellLength = config.resolution
//  val cellSliceSize = cellLength * cellLength
//  val cellSampleCount = cellLength * cellLength * cellLength
//  val resolution = config.resolution.toFloat()
//  val start = bounds.start.toVector3()
//
//  return CellSampler(cellCount) { step ->
//    val cellStart = start + indexToVector3i(cellsSliceSize, cellsSizeX, step).toVector3()
//    (0 until cellSampleCount).mapNotNull { index ->
//      val offset = indexToVector3i(cellSliceSize, cellLength, index).toVector3()
//      samplePoint(config, cellStart + offset / resolution)
//    }
//  }
//}
