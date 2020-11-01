package silentorb.mythic.fathom.sampling

import silentorb.mythic.fathom.misc.getNormal
import silentorb.mythic.fathom.surfacing.GridBounds
import silentorb.mythic.fathom.surfacing.snapToSurfaceIncludingNormal
import silentorb.mythic.imaging.texturing.anonymousSampler
import silentorb.mythic.spatial.Vector2
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

fun samplePoint(config: SamplingConfig, levelRanges: List<Float>, cellLength: Float, level: Int, start: Vector3): List<SamplePoint> {
  val sampleRange = levelRanges[level]
  val getDistance = config.getDistance
  val getShading = config.getShading
  val (_, startingDistance) = getDistance(start)
  val absDistance = abs(startingDistance)

  return if (absDistance > sampleRange) {
    if (level == 0 && startingDistance < -sampleRange) {
      val location = start
      val normal = getNormal(getDistance, start)
      val shading = getShading(location)
      listOf(
          SamplePoint(
              location = location,
              normal = normal,
              shading = shading,
              size = config.pointSizeScale * cellLength * 2f,
              level = level
          )
      )
    } else
      listOf()
  } else {
    val newPointList = if (absDistance > sampleRange / 2f)
      listOf()
    else {
      val (id, location, normal) = snapToSurfaceIncludingNormal(getDistance, start)
      if (location.distance(start) > cellLength / 2f)
        listOf()
      else {
        val shading = getShading(location)
        listOf(
            SamplePoint(
                location = location,
                normal = normal,
                shading = shading,
                size = config.pointSizeScale * cellLength,
                level = level
            )
        )
      }
    }
    if (level < config.levels - 1) {
      val nextResolution = cellLength / 2f
      val nextLevel = level + 1
      newPointList + subdivisionTemplate.flatMap { offset ->
        samplePoint(config, levelRanges, nextResolution, nextLevel, start + offset * nextResolution)
      }
    } else {
      newPointList
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

fun sampleCells(config: SamplingConfig, bounds: GridBounds): CellSampler {
  val boundsDimensions = bounds.end - bounds.start

  // Sometimes intermediate sdf changes can cause massive spikes in bounding size
  if (boundsDimensions.x > 100 || boundsDimensions.y > 100 || boundsDimensions.z > 100)
    return CellSampler(0) { listOf() }

  val stepDimensions = boundsDimensions * config.resolution
  val sampleCount = stepDimensions.x * stepDimensions.y * stepDimensions.z
  val sliceSize = stepDimensions.x * stepDimensions.y
  val start = bounds.start.toVector3()
  val initialScale = config.resolution.toFloat()

  val levelRanges = (0 until config.levels - 1)
      .fold(listOf(1f / initialScale)) { a, b ->
        a + a.last() / 2f
      }
      .map { Vector2(it).length() }

  return CellSampler(sampleCount) { index ->
    val offset = indexToVector3i(sliceSize, stepDimensions.x, index).toVector3()
    val startingLocation = start + offset / initialScale
    samplePoint(config, levelRanges, 1f / initialScale, 0, startingLocation)
  }
}

fun sampleForm(config: SamplingConfig, bounds: GridBounds): List<SamplePoint> {
  val (sampleCount, sampler) = sampleCells(config, bounds)
  return (0 until sampleCount).flatMap(sampler)
}

data class CellSampler(
    val cellCount: Int,
    val sampler: (Int) -> List<SamplePoint>
)
