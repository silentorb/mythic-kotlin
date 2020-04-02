package silentorb.mythic.imaging.substance.surfacing

import silentorb.mythic.imaging.substance.calculateNormal
import silentorb.mythic.imaging.substance.surfacing.old.getNormalRotation
import silentorb.mythic.imaging.substance.surfacing.old.projectFromNormalRotation
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import silentorb.mythic.spatial.toVector3

data class SubSample(
    val position: Vector3,
    val normal: Vector3,
    val distance: Float
)

data class CellSample(
    val samples: Array<SubSample?>,

    // Currently these variables are only used for debugging and testing
    val center: Vector3,
    val origin: Vector3,
    val normal: Vector3
)

fun sampleCellGrid(config: SurfacingConfig, center: Vector3, centerDistance: Float): CellSample {
  val getDistance = config.getDistance
  val subCells = config.subCells
  val subStep = config.cellSize / config.subCells
  val normal = calculateNormal(getDistance, center)
  val origin = center + normal * -centerDistance
  val normalRotation = getNormalRotation(normal)
  val left = projectFromNormalRotation(normalRotation, Vector2(-1f, 0f))
  val up = projectFromNormalRotation(normalRotation, Vector2(0f, -1f))
  val down = -up
  val right = -left
  val cellHalf = config.cellSize / 2
  val start = origin + left * cellHalf + up * cellHalf
  val sampleCount = subCells * subCells
  val bounds = DecimalBounds(
      start = center - cellHalf,
      end = center + cellHalf
  )

  val samples = (0 until sampleCount).map { i ->
    val y = i / subCells
    val x = i - y * subCells
    val position = start +
        right * subStep * x.toFloat() +
        down * subStep * y.toFloat()

    if (isInsideBounds(bounds, position)) {
      SubSample(
          position = position,
          normal = calculateNormal(getDistance, position),
          distance = getDistance(position)
      )
    } else
      null
  }.toTypedArray()

  return CellSample(
      samples = samples,
      normal = normal,
      center = center,
      origin = origin
  )
}

fun sampleCellGrids(config: SurfacingConfig, bounds: GridBounds): (Int) -> CellSample? {
  val dimensions = bounds.end - bounds.start
  val cellSize = config.cellSize
  val halfCell = cellSize / 2f
  val cellCount = dimensions.x * dimensions.y * dimensions.z
  val maxCellRange = Vector3(halfCell).length()
  val sliceSize = dimensions.x * dimensions.y
  val start = bounds.start.toVector3() + config.cellSize / 2f
  return { i ->
    val z = i / sliceSize
    val zRemainder = i - sliceSize * z
    val y = zRemainder / dimensions.x
    val x = zRemainder - y * dimensions.x
    val stepOffset = Vector3(x.toFloat(), y.toFloat(), z.toFloat()) * cellSize
    val center = start + stepOffset

    // Skip cells that have no geometry
    val rangeSample = config.getDistance(center)
    if (rangeSample <= maxCellRange) {
      sampleCellGrid(config, center, rangeSample)
    } else
      null
  }
}

fun sampleAllCellGrids(config: SurfacingConfig, bounds: GridBounds): Array<CellSample?> {
  val dimensions = bounds.end - bounds.start
  val cellCount = dimensions.x * dimensions.y * dimensions.z
  return (0 until cellCount)
      .map(sampleCellGrids(config, bounds))
      .toTypedArray()
}

fun traceContours(config: SurfacingConfig, bounds: GridBounds): ContourMesh {
  val grid = sampleAllCellGrids(config, bounds)
  return ContourMesh(
      vertices = listOf(),
      edges = listOf()
  )
}
