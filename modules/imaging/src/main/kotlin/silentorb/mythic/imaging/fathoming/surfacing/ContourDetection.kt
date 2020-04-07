package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.imaging.fathoming.DistanceFunction
import silentorb.mythic.imaging.fathoming.getNormal
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import silentorb.mythic.spatial.getCenter
import silentorb.mythic.spatial.lineIntersectsSphere
import kotlin.math.abs

fun getVariance(first: Vector3, second: Vector3) =
    (-first.dot(second) + 1f) / 2f

fun getWeightedMiddle(getDistance: DistanceFunction, a: SubSample, b: SubSample, position: Vector3): Vector3 {
  val normal = getNormal(getDistance, position)
  val strengthA = getVariance(normal, a.normal)
  val strengthB = getVariance(normal, b.normal)
  val weightScale = strengthA + strengthB
  val weightA = strengthA / weightScale
  val weightB = strengthB / weightScale
  val middle = a.position * weightA + b.position * weightB
  val result = snapToSurface(getDistance, middle)
  return result
}

fun refineMiddle(getDistance: DistanceFunction, a: SubSample, b: SubSample): Vector3 {
  val middle = getCenter(a.position, b.position)
  val position = snapToSurface(getDistance, middle)
  return getWeightedMiddle(getDistance, a, b, position)
}

fun diffSamples(getDistance: DistanceFunction, samples: Array<SubSample?>, first: Int, second: Int): Contour? {
  val a = samples[first]
  val b = samples[second]
  return if (a != null && b != null && a.normal != b.normal) {
    val position = refineMiddle(getDistance, a, b)
    Contour(
        strength = getVariance(a.normal, b.normal),
        direction = a.normal.cross(b.normal).normalize(),
        position = position,
        normal = getNormal(getDistance, position),
        firstSample = a,
        secondSample = b
    )
  } else
    null
}

fun diffSampleGrid(getDistance: DistanceFunction, samples: Array<SubSample?>, gridLength: Int): (Vector3i, Int) -> Contours = { axis, offset ->
  val subLengths = Vector3i(gridLength - 2) + axis
  val sampleCount = subLengths.x * subLengths.y * subLengths.z
  val start = Vector3i(1) - axis
  val end = start + subLengths
  val buffer = ArrayList<Contour>(sampleCount)
//  println(" -- $axis")
  for (z in start.z until end.z) {
    for (y in start.y until end.y) {
      for (x in start.x until end.x) {
        val first = x + y * gridLength + z * gridLength * gridLength
        val second = first + offset
//        println("$x $y $z ${first.toString().padStart(3, ' ')} ${second.toString().padStart(3, ' ')}")
        val sample = diffSamples(getDistance, samples, first, second)
        if (sample != null) {
          buffer.add(sample)
        }
      }
    }
  }
  buffer.toList()
//  (0 until sampleCount)
//      .mapNotNull { i ->
//        val z = start.z + i / sliceSize
//        val zRemainder = i - sliceSize * z
//        val y = start.y + zRemainder / subLengths.x
//        val x = start.x + zRemainder - y * subLengths.x
//        val first = x + y * gridLength + z * gridLength * gridLength
//        diffSamples(getDistance, samples, first, first + offset)
//      }
}

// With the way contour diffs are only generated along axis, the cell corners have a slight blindspot
// which can benefit from diagonal contour diffs
fun getCellCornerContours(getDistance: DistanceFunction, grid: CellSample, gridLength: Int): Contours {
  return listOf(
      Vector3i(0, 0, 0),
      Vector3i(1, 1, 1),
      Vector3i(0, 1, 1),
      Vector3i(1, 0, 1),
      Vector3i(1, 0, 0),
      Vector3i(1, 1, 0)
  )
      .mapNotNull { base ->
        val a = (base * (gridLength - 3)) + 1
        val c = a + base + base - 1
        val first = a.x + a.y * gridLength + a.z * gridLength * gridLength
        val second = c.x + c.y * gridLength + c.z * gridLength * gridLength
        diffSamples(getDistance, grid.samples, first, second)
      }
}

fun newContourGrid(getDistance: DistanceFunction, grid: CellSample, gridLength: Int): Contours {
  val diff = diffSampleGrid(getDistance, grid.samples, gridLength)
  val x = diff(Vector3i(1, 0, 0), 1)
  val y = diff(Vector3i(0, 1, 0), gridLength)
  val z = diff(Vector3i(0, 0, 1), gridLength * gridLength)
  val w = getCellCornerContours(getDistance, grid, gridLength)
  return x + y + z + w
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
  return sampleLength * 2f// * 0.5f
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
          lineIntersectsSphere(base.position, base.direction, contour.position, distanceTolerance) //&&
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
