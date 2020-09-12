package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

typealias FixedChildArranger = (Vector2i) -> List<Bounds>
typealias FlowerContainer = (List<Flower>) -> Flower
typealias FlowerContainerWrapper = (FlowerContainer) -> FlowerContainer
typealias SimpleBoxContainer = (List<Box>) -> Box

tailrec fun arrangeSimpleListItems(plane: Plane, spacing: Int, boxes: List<DimensionBox>, length: Int = 0, accumulator: List<Int> = listOf()): List<Int> =
    if (boxes.none())
      accumulator
    else {
      val childDimensions = plane(boxes.first().dimensions)
      val nextLength = length + childDimensions.x + spacing
      arrangeSimpleListItems(plane, spacing, boxes.drop(1), nextLength, accumulator + length)
    }

//fun arrangeListItems(plane: Plane, spacing: Int, boxes: List<Box>): List<Box> {
//  val lengths = arrangeSimpleListItems(plane, spacing, boxes)
//  return boxes.zip(lengths) { child, length ->
//    child.copy(
//        bounds = child.bounds.copy(
//            position = plane(Vector2i(length, 0))
//        )
//    )
//  }
//}

fun wrapArrangedSimpleBoxItems(plane: Plane, children: List<Box>, lengths: List<Int>): List<OffsetBox> =
    children.zip(lengths) { child, length -> OffsetBox(child, plane(Vector2i(length, 0))) }

//fun getListLength(plane: Plane, boxes: Boxes): Int =
//    boxes.maxOfOrNull { plane(it.bounds.end).x } ?: 0

fun getListBreadth(plane: Plane, boxes: List<DimensionBox>): Int =
    boxes.maxOfOrNull { plane(it.dimensions).y } ?: 0

fun boxList(plane: Plane, spacing: Int = 0, name: String = "list"): SimpleBoxContainer = { children ->
  val lengths = arrangeSimpleListItems(plane, spacing, children)
  val boxes = wrapArrangedSimpleBoxItems(plane, children, lengths)
  val length = lengths.maxOrNull() ?: 0
  val breadth = getListBreadth(plane, boxes.map { it.child })

  Box(
      name = name,
      dimensions = plane(Vector2i(length, breadth)),
      boxes = boxes
  )
}

fun breadthList(plane: Plane, spacing: Int = 0, name: String = "list"): (List<SimpleLengthFlower>) -> SimpleLengthFlower = { children ->
  { breadth ->
    val initialBoxes = children.map { it(breadth) }
    val lengths = arrangeSimpleListItems(plane, spacing, initialBoxes)
    val boxes = wrapArrangedSimpleBoxItems(plane, initialBoxes, lengths)
    val length = lengths.maxOrNull() ?: 0
//    val breadth = getListBreadth(plane, boxes)

    Box(
        name = name,
        dimensions = plane(Vector2i(length, breadth)),
        boxes = boxes
    )
  }
}

fun horizontalList(spacing: Int = 0, name: String = "horizontalList"): SimpleBoxContainer =
    boxList(horizontalPlane, spacing, name)

//fun verticalList(spacing: Int = 0, name: String = "verticalList"): (List<Box>) -> Box =
//    boxList(verticalPlane, spacing, name)

enum class FlexType {
  stretch,
  fixed,
}

interface FlexItem

data class BoxFlexItem(
    val box: Box
) : FlexItem

data class FlowerFlexItem(
    val flower: Flower
) : FlexItem

fun flex(box: Box): FlexItem =
    BoxFlexItem(box)

fun flex(flower: Flower): FlexItem =
    FlowerFlexItem(flower)

//fun flexList(plane: Plane, spacing: Int = 0, name: String = "flexList"): (List<FlexItem>) -> Flower = { items ->
//  { dimensions ->
//    val relativeBounds = plane(dimensions)
//    val totalLength = relativeBounds.x
//    val breadth = relativeBounds.y
//    val inputBoxes = items.mapNotNull { (it as? BoxFlexItem)?.box }
//    val fixedLength = inputBoxes.sumBy { plane(it.bounds.dimensions).x }
////    val fixedBreadth = inputBoxes.sumBy { plane(it.bounds.dimensions).y }
//    val totalSpacing = spacing * (items.size - 1)
//    val reserved = fixedLength + totalSpacing
//    val remaining = totalLength - reserved
//    val stretchCount = items.size - inputBoxes.size
//    val stretchRation = remaining / stretchCount
//    val stretchItemBounds = plane(Vector2i(stretchRation, breadth))
//
//    val boxes = items.map { item ->
//      when (item) {
//        is BoxFlexItem -> item.box
//        is FlowerFlexItem -> item.flower(stretchItemBounds)
//        else -> throw Error()
//      }
//    }
//    val arrangedBoxes = arrangeListItems(plane, spacing, boxes)
//    Box(
//        name = name,
//        bounds = Bounds(
//            dimensions = dimensions
//        ),
//        boxes = arrangedBoxes
//    )
//  }
//}

interface LengthFlexItem {
  fun getBox(length: Int): Box
  fun getBoxOrNull(): Box?
}

data class LengthBoxFlexItem(
    val box: Box
) : LengthFlexItem {
  override fun getBox(length: Int): Box = box
  override fun getBoxOrNull(): Box? = box
}

data class LengthFlowerFlexItem(
    val flower: SimpleLengthFlower
) : LengthFlexItem {
  override fun getBox(length: Int): Box = flower(length)
  override fun getBoxOrNull(): Box? = null
}

fun flex2(box: Box): LengthFlexItem =
    LengthBoxFlexItem(box)

fun flex2(flower: SimpleLengthFlower): LengthFlexItem =
    LengthFlowerFlexItem(flower)

fun flexList(plane: Plane, spacing: Int = 0, name: String = "flexList", children: List<LengthFlexItem>): SimpleLengthFlower {
  return { length ->
    val inputBoxes = children.mapNotNull { it.getBoxOrNull() }
    if (inputBoxes.size == children.size)
      boxList(plane, spacing, name)(inputBoxes)
    else {
      val totalLength = length
      val fixedLength = inputBoxes.sumBy { plane(it.dimensions).x }
      val breadth = inputBoxes.sumBy { plane(it.dimensions).y }
      val totalSpacing = spacing * (children.size - 1)
      val reserved = fixedLength + totalSpacing
      val remaining = totalLength - reserved
      val stretchCount = children.size - inputBoxes.size
      val stretchRation = remaining / stretchCount
      val boxes = children.map { it.getBox(stretchRation) }
      val arrangedBoxes = wrapArrangedSimpleBoxItems(plane, boxes, arrangeSimpleListItems(plane, spacing, boxes))
      Box(
          name = name,
          dimensions = plane(Vector2i(length, breadth)),
          boxes = arrangedBoxes
      )
    }
  }
}
