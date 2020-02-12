package silentorb.mythic.imaging

import silentorb.mythic.ent.mappedCache
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.toVector2
import silentorb.mythic.spatial.toVector2i
import org.joml.Vector2i
import silentorb.mythic.randomly.Dice
import silentorb.mythic.spatial.plus

data class AnchorGrid(
    val length: Int,
    val cells: List<Vector2?>
)

fun newAnchorGrid(dice: Dice, length: Int, roughAnchorCount: Int): AnchorGrid {

  val cellCount = length * length
  val cellLength = 1f / length.toFloat()
  val cellAnchorChance = roughAnchorCount.toFloat() / cellCount.toFloat()
  val cells = Array<Vector2?>(cellCount) { null }

  val minDistance = cellLength / 2f

  // Note: minDistance is Manhattan, not direct

  // Ensure that even if a cell is completely surrounded by neighbor anchors pushing against its boundaries,
  // the cell can still hold an anchor in its middle.
  // This is an extreme case to be worried about, but I've found that in generation code randomness needs
  // to be completely controlled or endless woes and surprises follow.
//  assert(minDistance < length.toFloat() / 2f)

  var i = 0

  val getRange = { step: Int, previous: Float?, next: Float? ->
    val paddedStart = if (previous != null)
      Math.max(0f, previous + minDistance - 1f)
    else
      0f

    val paddedEnd = if (next != null)
      Math.min(1f, next - minDistance + 1f)
    else
      1f

    dice.getFloat(paddedStart, paddedEnd)
  }

  val left = { j: Int ->
    if (j == 0)
      cellCount - 1
    else
      j - 1
  }

  val right = { j: Int ->
    if (j >= cellCount - 1)
      cellCount - 1
    else
      j + 1
  }

  val above = { j: Int ->
    if (j + length < cellCount)
      j + length
    else
      j + length - cellCount
  }

  val below = { j: Int ->
    if (j >= length)
      j - length
    else
      j + cellCount - length
  }

  val getX = { getter: (Int) -> Int ->
    cells[getter(i)]?.x
  }

  val getY = { getter: (Int) -> Int ->
    cells[getter(i)]?.y
  }

  for (y in 0 until length) {
    for (x in 0 until length) {
      val hasAnchor = dice.getFloat() < cellAnchorChance
      if (hasAnchor) {
        val anchor = Vector2(
            getRange(x, getX(left), getX(right)),
            getRange(y, getY(below), getY(above))
        )
        cells[i] = anchor
      }
      ++i
    }
  }
  return AnchorGrid(
      cells = cells.toList(),
      length = length
  )
}

// Faster than a full modulus
fun singleModulus(x: Int, divisor: Int): Int =
    when {
      x < 0 -> x + divisor
      x >= divisor -> x - divisor
      else -> x
    }

fun anchorGridCell(grid: AnchorGrid): (Vector2i) -> Vector2? = { input ->
  val cellCount = grid.length * grid.length
  val x = singleModulus(input.x, grid.length)
  val i = input.y * grid.length + x
  val i2 = if (i < 0)
    i + cellCount
  else if (i >= cellCount)
    i - cellCount
  else
    i

  grid.cells[i2]
}

private fun newOffsets(step: Int): List<Vector2i> {
  // Forms a boundary like:
  //
  //   ###
  //   # #
  //   ###

  val short = step - 1
  val fullRange = (-step..step)
  val shortRange = (-short..short)
  return fullRange.map { Vector2i(it, -step) } +
      shortRange.map { Vector2i(-step, it) } +
      shortRange.map { Vector2i(step, it) } +
      fullRange.map { Vector2i(it, step) }
}

typealias CellsSource = (Vector2i) -> List<Vector2>

fun getNearestCells(grid: AnchorGrid, minimumCount: Int): CellsSource {
  val offsets = mappedCache(::newOffsets)
//  val cell = mappedCache(anchorGridCell(grid))
  return { i ->
    var step = 1

    val cellOffset = { point: Vector2i ->
      val fineOffset = anchorGridCell(grid)(point)
      if (fineOffset != null) {
        val roughOffset = point.toVector2()
        val m = roughOffset + fineOffset
        m
      } else
        null
    }

    var cells = listOfNotNull(cellOffset(i))

    val gatherCells = { s: Int ->
      cells +
          offsets(s)
              .mapNotNull {
                cellOffset(it + i)
              }.toList()
    }

    do {
      if (step > grid.length / 2)
        throw Error("Anchor grid does not have enough cells")

      cells = gatherCells(step)
      ++step
    } while (cells.size < minimumCount)
    gatherCells(step)
  }
}

fun manhattanDistance(a: Vector2): (Vector2) -> Float = { b ->
  Math.abs(a.x - b.x) + Math.abs(a.y - b.y)
}

typealias VoronoiApp = (Vector2, List<Vector2>) -> Float

fun voronoiBoundaries(thickness: Float): VoronoiApp = { input, options ->
  val nearestPair = options.sortedBy { it.distance(input) }.take(2)
  val gap = Math.abs(nearestPair[0].distance(input) - nearestPair[1].distance(input))
  val result = if (gap < thickness / 2f)
    0f
  else
    1f

  result
}

fun voronoi(length: Int, nearestCells: CellsSource, app: VoronoiApp): Sampler = { x, y ->
  val input = Vector2(x, y) * length.toFloat()
  val i = input.toVector2i()
  val options = nearestCells(i)
  app(input, options)
}
