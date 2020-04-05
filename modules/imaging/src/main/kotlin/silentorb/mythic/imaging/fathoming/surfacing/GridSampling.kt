package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.imaging.fathoming.DistanceFunction
import silentorb.mythic.imaging.fathoming.calculateNormal
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
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

tailrec fun snapToSurface(getDistance: DistanceFunction, tolerance: Float, normal: Vector3, position: Vector3, distance: Float, step: Int): Vector3 {
  return if (abs(distance) <= tolerance || step > 5)
    position
  else {
    val newPosition = position - normal * distance
    val newDistance = getDistance(newPosition)
//    println("$step $newDistance     $newPosition")
    snapToSurface(getDistance, tolerance, normal, newPosition, newDistance, step + 1)
  }
}

fun sampleCellGrid(config: SurfacingConfig, center: Vector3, start: Vector3, dimensions: Vector3i, subCellRange: Float): CellSample {
  val getDistance = config.getDistance
  val subStep = config.cellSize / config.subCells
  val sampleCount = dimensions.x * dimensions.y * dimensions.z
  val sliceSize = dimensions.x * dimensions.y
  val snapTolerance = subStep * 0.09f

  val samples = (0 until sampleCount).map { i ->
    val z = i / sliceSize
    val zRemainder = i - sliceSize * z
    val y = zRemainder / dimensions.x
    val x = zRemainder - y * dimensions.x

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
      val position = snapToSurface(getDistance, snapTolerance, normal, cellCenter, distance, 1)
      SubSample(
          center = cellCenter,
          position = position,
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

fun getCellStart(bounds: GridBounds): (Int) -> Vector3i {
  val dimensions = bounds.end - bounds.start
  val sliceSize = dimensions.x * dimensions.y
//  val start = bounds.start.toVector3() + config.cellSize / 2f
  return { i ->
    val z = i / sliceSize
    val zRemainder = i - sliceSize * z
    val y = zRemainder / dimensions.x
    val x = zRemainder - y * dimensions.x
    bounds.start + Vector3i(x, y, z)
  }
}

fun gridToDimensions(config: SurfacingConfig, get: (Int) -> Vector3i): (Int) -> Vector3 {
  val cellSize = config.cellSize
  return { i ->
    val offset = get(i)
    offset.toVector3() * cellSize
  }
}

fun getActiveCells(getCellStart: (Int) -> Vector3, config: SurfacingConfig, cellCount: Int): BooleanArray {
  val cellSize = config.cellSize
  val halfCell = cellSize / 2f
  val maxCellRange = Vector3(halfCell).length()
  val cells = BooleanArray(cellCount)
  for (i in 0 until cellCount) {
    val center = getCellStart(i) + halfCell
    // Skip cells that have no geometry
    val rangeSample = config.getDistance(center)
    cells[i] = abs(rangeSample) <= maxCellRange
  }
  return cells
}

fun sampleCellGrids(config: SurfacingConfig, bounds: GridBounds): (Int) -> CellSample? {
  val dimensions = bounds.end - bounds.start
  val cellCount = dimensions.x * dimensions.y * dimensions.z
  val cellSize = config.cellSize
  val subCells = config.subCells
  val halfCell = cellSize / 2f
  val maxCellRange = Vector3(halfCell).length()
  val subCellRange = maxCellRange / (config.subCells / 2f)
  val cellGridStart = getCellStart(bounds)
  val cellDecimalStart = gridToDimensions(config, cellGridStart)
  val activeCells = getActiveCells(cellDecimalStart, config, cellCount)
  fun cellActivityOffset(index: Int) = if (activeCells.getOrElse(index) { false }) 1 else 0
  val subStep = config.cellSize / config.subCells
  return { i ->
    // Skip cells that have no geometry
    if (activeCells[i]) {
      val gridStart = cellGridStart(i)
//      val activityStartOffset = Vector3i(
//          cellActivityOffset(i - 1),
//          cellActivityOffset(i - dimensions.x),
//          cellActivityOffset(i - dimensions.y * dimensions.z)
//      )
//      val activityEndOffset = Vector3i(
//          cellActivityOffset(i + 1),
//          cellActivityOffset(i + dimensions.x),
//          cellActivityOffset(i + dimensions.y * dimensions.z)
//      )
      val subCellDimensions = Vector3i(subCells + 2)// + activityStartOffset + activityEndOffset
      val initialStart = gridStart.toVector3() * cellSize
      val center = initialStart + halfCell
      val start = initialStart - subStep
      sampleCellGrid(config, center, start, subCellDimensions, subCellRange)
    } else
      null
  }
}
