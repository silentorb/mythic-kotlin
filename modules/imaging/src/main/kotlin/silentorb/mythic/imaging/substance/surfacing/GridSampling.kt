package silentorb.mythic.imaging.substance.surfacing

import silentorb.mythic.imaging.substance.calculateNormal
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.toVector3
import kotlin.math.abs

data class SubSample(
    val position: Vector3,
    val center: Vector3,
    val normal: Vector3,
    val distance: Float
)

data class CellSample(
    val samples: Array<SubSample?>,

    // Currently these variables are only used for debugging and testing
    val center: Vector3
)

fun sampleCellGrid(config: SurfacingConfig, center: Vector3, subCellRange: Float): CellSample {
  val getDistance = config.getDistance
  val subCells = config.subCells
  val subStep = config.cellSize / config.subCells
  val cellHalf = config.cellSize / 2
  val start = center - cellHalf
  val sampleCount = subCells * subCells * subCells
  val sliceSize = subCells * subCells

  val samples = (0 until sampleCount).map { i ->
    val z = i / sliceSize
    val zRemainder = i - sliceSize * z
    val y = zRemainder / subCells
    val x = zRemainder - y * subCells

    val cellCenter = start + Vector3(
        x.toFloat() * subStep,
        y.toFloat() * subStep,
        z.toFloat() * subStep
    )

    val distance = getDistance(cellCenter)
    if (abs(distance) > subCellRange)
      null
    else {
      val normal = calculateNormal(getDistance, cellCenter)
      SubSample(
          center = cellCenter,
          position = cellCenter - normal * distance,
          normal = normal,
          distance = distance
      )
    }
  }.toTypedArray()

  return CellSample(
      samples = samples,
      center = center
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
  val subCellRange = maxCellRange / config.subCells
  return { i ->
    val z = i / sliceSize
    val zRemainder = i - sliceSize * z
    val y = zRemainder / dimensions.x
    val x = zRemainder - y * dimensions.x
    val stepOffset = Vector3(x.toFloat(), y.toFloat(), z.toFloat()) * cellSize
    val center = start + stepOffset

    // Skip cells that have no geometry
    val rangeSample = config.getDistance(center)
    if (abs(rangeSample) <= maxCellRange) {
      sampleCellGrid(config, center, subCellRange)
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
