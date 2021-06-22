package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

fun getAxisLengths(boxes: Map<Vector2i, Box>, count: Int, planeIndex: Int) =
    (0..count)
        .map { i ->
          boxes
              .filterKeys { it[planeIndex] == i }
              .maxByOrNull { it.value.dimensions[planeIndex] }?.value?.dimensions?.get(planeIndex) ?: 0
        }

fun tableFlower(cells: Map<Vector2i, Flower>, margins: Vector2i): Flower = { seed ->
  if (cells.none()) {
    emptyBox
  } else {
    val testSeed = seed.copy(dimensions = Vector2i.zero)
    val testBoxes = cells.mapValues { it.value(testSeed) }
    val countX = cells.keys.maxByOrNull { it.x }?.x ?: 0
    val countY = cells.keys.maxByOrNull { it.y }?.y ?: 0

    val lengthsX = getAxisLengths(testBoxes, countX, 0)
    val lengthsY = getAxisLengths(testBoxes, countY, 1)

    val offsetsX = arrangeLengths(margins.x, lengthsX)
    val offsetsY = arrangeLengths(margins.y, lengthsY)
    val lengthX = getListLength(offsetsX)
    val lengthY = getListLength(offsetsY)

    val boxes = cells.map { (cell, flower) ->
      val offsetX = offsetsX[cell.x]
      val offsetY = offsetsY[cell.y]
      val dimensions = Vector2i(offsetX.length, offsetY.length)
      OffsetBox(
          offset = Vector2i(offsetX.offset, offsetY.offset),
          child = flower(seed.copy(dimensions = dimensions)),
      )
    }

    Box(
        name = "table",
        dimensions = Vector2i(lengthX, lengthY),
        boxes = boxes
    )
//  Box(
//      dimensions = Vector2i(),
//      boxes = cells.map { (cell, flower) ->
//        OffsetBox(
//            offset = Vector2i.zero,
//            child = flower(seed),
//        )
//      }
//  )
  }
}
