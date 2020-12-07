package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

typealias SimpleBoxContainer = (List<Box>) -> Box

tailrec fun arrangeListItems(plane: Plane, spacing: Int, boxes: List<Box>, length: Int = 0, accumulator: List<OffsetBox> = listOf()): List<OffsetBox> =
    if (boxes.none())
      accumulator
    else {
      val box = boxes.first()
      val childDimensions = plane(box.dimensions)
      val nextLength = length + childDimensions.x + spacing
      arrangeListItems(plane, spacing, boxes.drop(1), nextLength, accumulator + OffsetBox(box, plane(Vector2i(length, 0))))
    }

fun getListLength(plane: Plane, boxes: List<OffsetBox>): Int =
    boxes.maxOfOrNull { plane(it.offset + it.dimensions).x } ?: 0

fun getListBreadth(plane: Plane, boxes: List<DimensionBox>): Int =
    boxes.maxOfOrNull { plane(it.dimensions).y } ?: 0

fun boxList(plane: Plane, spacing: Int, vararg children: Box): Box {
  val boxes = arrangeListItems(plane, spacing, children.toList())
  val length = getListLength(plane, boxes)
  val breadth = getListBreadth(plane, boxes.map { it.child })

 return Box(
      name = "list",
      dimensions = plane(Vector2i(length, breadth)),
      boxes = boxes
  )
}

fun boxList2(plane: Plane, spacing: Int = 0, vararg children: Box) =
    boxList(plane, spacing, *children)

fun boxList(plane: Plane, spacing: Int = 0): SimpleBoxContainer = { children ->
  boxList(plane, spacing, *children.toTypedArray())
}

fun alignListItems(plane: Plane, align: Aligner): (Box) -> Box = { box ->
  val breadth = getListBreadth(plane, box.boxes.map { it.child })
  box.copy(
      boxes = box.boxes.map { childOffset ->
        val lengthOffset = plane(childOffset.offset).x
        val aligned = align(breadth, plane(childOffset.dimensions).y)
        childOffset.copy(
            offset = plane(Vector2i(lengthOffset, aligned))
        )
      }
  )
}

fun breadthList(plane: Plane, spacing: Int = 0): (List<LengthFlower>) -> LengthFlower = { children ->
  { breadth ->
    val initialBoxes = children.map { it(breadth) }
    val boxes = arrangeListItems(plane, spacing, initialBoxes)
    val length = getListLength(plane, boxes)
//    val breadth = getListBreadth(plane, boxes)

    Box(
        name = "list",
        dimensions = plane(Vector2i(length, breadth)),
        boxes = boxes
    )
  }
}

fun horizontalList(spacing: Int = 0): SimpleBoxContainer =
    boxList(horizontalPlane, spacing)

interface FlexItem {
  fun getBox(length: Int, breadth: Int): Box
  fun getBoxOrNull(): Box?
}

data class BoxFlexItem(
    val box: Box
) : FlexItem {
  override fun getBox(length: Int, breadth: Int): Box = box
  override fun getBoxOrNull(): Box? = box
}

data class LengthFlexItem(
    val flower: LengthFlower
) : FlexItem {
  override fun getBox(length: Int, breadth: Int): Box = flower(length)
  override fun getBoxOrNull(): Box? = null
}

data class FlowerFlexItem(
    val flower: Flower
) : FlexItem {
  override fun getBox(length: Int, breadth: Int): Box = flower(Vector2i(length, breadth))
  override fun getBoxOrNull(): Box? = null
}

fun flex(box: Box): FlexItem =
    BoxFlexItem(box)

fun flex(flower: LengthFlower): FlexItem =
    LengthFlexItem(flower)

fun flexFlower(flower: Flower): FlexItem =
    FlowerFlexItem(flower)

fun flexList(plane: Plane, spacing: Int = 0): (List<FlexItem>) -> LengthFlower = { children ->
  { length ->
    val inputBoxes = children.mapNotNull { it.getBoxOrNull() }
    if (inputBoxes.size == children.size)
      boxList(plane, spacing)(inputBoxes)
    else {
      val totalLength = length
      val fixedLength = inputBoxes.sumBy { plane(it.dimensions).x }
      val breadth = inputBoxes.sumBy { plane(it.dimensions).y }
      val totalSpacing = spacing * (children.size - 1)
      val reserved = fixedLength + totalSpacing
      val remaining = totalLength - reserved
      val stretchCount = children.size - inputBoxes.size
      val stretchRation = remaining / stretchCount
      val boxes = children.map { it.getBox(stretchRation, breadth) }
      val arrangedBoxes = arrangeListItems(plane, spacing, boxes)
      Box(
          name = "flexList",
          dimensions = plane(Vector2i(length, breadth)),
          boxes = arrangedBoxes
      )
    }
  }
}
