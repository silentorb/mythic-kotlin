package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.imaging.fathoming.DistanceFunction
import silentorb.mythic.imaging.fathoming.calculateNormal
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import silentorb.mythic.spatial.getCenter
import silentorb.mythic.spatial.lineIntersectsSphere
import kotlin.math.abs

fun getVariance(first: Vector3, second: Vector3) =
    (-first.dot(second) + 1f) / 2f

fun refineMiddle(getDistance: DistanceFunction, a: SubSample, b: SubSample): Vector3 {
  val middle = getCenter(a.position, b.position)
  val normal = calculateNormal(getDistance, middle)
  val distance = getDistance(middle)
  val position = middle - normal * distance
  return position
}

fun diffSamples(getDistance: DistanceFunction, samples: Array<SubSample?>, first: Int, second: Int): Contour? {
  val a = samples[first]
  val b = samples[second]
  return if (a != null && b != null && a.normal != b.normal) {
    Contour(
        strength = getVariance(a.normal, b.normal),
        direction = a.normal.cross(b.normal).normalize(),
        position = refineMiddle(getDistance, a, b),
        firstSample = a,
        secondSample = b
    )
  } else
    null
}

fun diffSampleGrid(getDistance: DistanceFunction, samples: Array<SubSample?>, gridLength: Int): (Vector3i, Int) -> Contours = { axis, offset ->
  val subLengths = Vector3i(gridLength) - axis
  val sliceSize = subLengths.x * subLengths.y
  val sampleCount = subLengths.x * subLengths.y * subLengths.z
  (0 until sampleCount)
      .mapNotNull { i ->
        val z = i / sliceSize
        val zRemainder = i - sliceSize * z
        val y = zRemainder / subLengths.x
        val x = zRemainder - y * subLengths.x
        val first = x + y * gridLength + z * gridLength * gridLength
        diffSamples(getDistance, samples, first, first + offset)
      }
}

fun newContourGrid(getDistance: DistanceFunction, grid: CellSample, gridLength: Int): Contours {
  val diff = diffSampleGrid(getDistance, grid.samples, gridLength)
  val x = diff(Vector3i(1, 0, 0), 1)
  val y = diff(Vector3i(0, 1, 0), gridLength)
  val z = diff(Vector3i(0, 0, 1), gridLength * gridLength)
  return x + y + z
}

fun isolateContours(tolerance: Float, neighbors: Contours) =
    neighbors
        .filter { it.strength > tolerance }

//fun isolateContours(tolerance: Float, contourGrid: ContourGrid): Contours =
//    isolateContours(tolerance, contourGrid.x)
//        .plus(isolateContours(tolerance, contourGrid.y)
//        )

fun getDistanceTolerance(config: SurfacingConfig): Float {
  val sampleLength = config.cellSize / config.subCells
  return sampleLength * 2.5f// * 0.5f
//  val squared = sampleLength * sampleLength
//  return sqrt(squared + squared)
}

tailrec fun detectEdges(distanceTolerance: Float, contours: Contours, pivots: Contours, lines: LineAggregates): LineAggregates {
  return if (contours.none())
    lines
  else {
    val base = contours.first()
    val remainingContours = contours.drop(1)
    val matches = remainingContours
        .filter { contour ->
          lineIntersectsSphere(base.position, base.direction, contour.position, distanceTolerance) &&
              abs(base.direction.dot(contour.direction)) > 0.8f
        }

    val alignedMatches = matches
//        .filter { contour ->
//          val strength = getVariance(base.direction, contour.direction)
//          strength < normalTolerance
//        }

    val pivotMatches = pivots
        .filter { contour ->
          lineIntersectsSphere(base.position, base.direction, contour.position, distanceTolerance)
        }

    val nextContours = remainingContours
        .minus(alignedMatches)

    val allMatches = matches.plus(pivotMatches)

    val nextLines = if (allMatches.any()) {
      val newLine = listOf(base).plus(allMatches)
      lines.plusElement(newLine)
    } else
      lines

    val nextPivots = if (allMatches.none())
      pivots.plus(base)
    else
      pivots

    detectEdges(distanceTolerance, nextContours, nextPivots, nextLines)
  }
}

fun detectEdges(config: SurfacingConfig, contours: Contours, pivots: Contours): LineAggregates {
  val distanceTolerance = getDistanceTolerance(config)
  return detectEdges(distanceTolerance, contours, pivots, listOf())
}
