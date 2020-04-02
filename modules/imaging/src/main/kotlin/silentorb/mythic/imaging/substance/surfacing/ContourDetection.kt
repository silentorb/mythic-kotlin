package silentorb.mythic.imaging.substance.surfacing

import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.getCenter
import silentorb.mythic.spatial.lineIntersectsSphere
import kotlin.math.sqrt

fun getVariance(first: Vector3, second: Vector3) =
    (-first.dot(second) + 1f) / 2f

fun diffSamples(samples: Array<SubSample?>, first: Int, second: Int): Contour? {
  val a = samples[first]
  val b = samples[second]
  return if (a != null && b != null) {
    Contour(
        strength = getVariance(a.normal, b.normal),
        direction = a.normal.cross(b.normal).normalize(),
        position = getCenter(a.position, b.position),
        firstSample = a,
        secondSample = b
    )
  } else
    null
}

fun diffSamples(samples: Array<SubSample?>, neighborhoodLength: Int, gridLength: Int, offset: Int) =
    (0 until neighborhoodLength * neighborhoodLength)
        .map { i ->
          val y = i / neighborhoodLength
          val x = i - y * neighborhoodLength
          val first = x + y * gridLength
          diffSamples(samples, first, first + offset)
        }

fun newContourGrid(grid: CellSample, gridLength: Int): ContourGrid {
  val neighborhoodLength = (gridLength - 1)
  val horizontal = diffSamples(grid.samples, neighborhoodLength, gridLength, 1)
  val vertical = diffSamples(grid.samples, neighborhoodLength, gridLength, gridLength)
  return ContourGrid(
      gridLength = gridLength,
      neighborhoodLength = neighborhoodLength,
      horizontal = horizontal,
      vertical = vertical
  )
}

fun isolateContours(tolerance: Float, neighbors: PossibleContours) =
    neighbors
        .filterNotNull()
        .filter { it.strength > tolerance }

fun isolateContours(tolerance: Float, contourGrid: ContourGrid): Contours =
    isolateContours(tolerance, contourGrid.horizontal)
        .plus(isolateContours(tolerance, contourGrid.vertical)
        )

fun getDistanceTolerance(config: SurfacingConfig): Float {
  val sampleLength = config.cellSize / config.subCells
  val squared = sampleLength * sampleLength
  return sqrt(squared + squared)
}

tailrec fun detectEdges(distanceTolerance: Float, normalTolerance: Float, contours: Contours, lines: LineAggregates): LineAggregates {
  return if (contours.none())
    lines
  else {
    val base = contours.first()
    val remainingContours = contours.drop(1)
    val matches = remainingContours
        .filter { contour ->
          lineIntersectsSphere(base.position, base.direction, contour.position, distanceTolerance)
//          val vector = (base.position - contour.position).normalize()
//          val dot = vector.dot(base.direction)
//          abs(dot) > tolerance
//          val strength = getVariance(vector, base.direction)
//          strength < tolerance * 2f
        }

    val alignedMatches = matches
        .filter { contour ->
          val strength = getVariance(base.direction, contour.direction)
          strength < normalTolerance
        }

    val nextContours = remainingContours
        .minus(alignedMatches)

    val newLine = listOf(base).plus(matches)

    val nextLines = lines.plusElement(newLine)
    detectEdges(distanceTolerance, normalTolerance, nextContours, nextLines)
  }
}

fun detectEdges(config: SurfacingConfig, contours: Contours): LineAggregates {
  val distanceTolerance = getDistanceTolerance(config)
  return detectEdges(distanceTolerance, 0.2f, contours, listOf())
}
