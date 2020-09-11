package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

typealias FixedChildArranger = (Vector2i) -> List<Bounds>
typealias FlowerContainer = (List<Flower>) -> Flower
typealias FlowerContainerWrapper = (FlowerContainer) -> FlowerContainer
typealias BoxFlowerContainer = (List<Box>) -> Flower
typealias BoxContainer = (List<Box>) -> Box
typealias BoxContainerWrapper = (BoxContainer) -> BoxContainer

tailrec fun arrangeListItems(plane: Plane, spacing: Int, boxes: List<Box>, length: Int = 0, accumulator: List<Box> = listOf()): List<Box> =
    if (boxes.none())
      accumulator
    else {
      val offsetVector = plane(Vector2i(length, 0))
      val next = boxes.first()
      val updatedNext = next
          .copy(
              bounds = next.bounds.copy(
                  position = next.bounds.position + offsetVector
              )
          )
      val childDimensions = plane(next.bounds.dimensions)
      val nextLength = length + childDimensions.x + spacing
      arrangeListItems(plane, spacing, boxes.drop(1), nextLength, accumulator + updatedNext)
    }

fun getListLength(plane: Plane, boxes: Boxes): Int =
    boxes.maxOfOrNull { plane(it.bounds.end).x } ?: 0

fun getListBreadth(plane: Plane, boxes: Boxes): Int =
    boxes.maxOfOrNull { plane(it.bounds.dimensions).y } ?: 0

fun list(plane: Plane, spacing: Int = 0, name: String = "list"): BoxContainer = { children ->
  val boxes = arrangeListItems(plane, spacing, children)
  val length = getListLength(plane, boxes)
  val breadth = getListBreadth(plane, boxes)

  Box(
      name = name,
      bounds = Bounds(
          dimensions = plane(Vector2i(length, breadth))
      ),
      boxes = boxes
  )
}

inline fun <reified T : PlaneMap> list(spacing: Int = 0, name: String = "list", children: List<LengthFlower<T>>): LengthFlower<T> {
  return { length ->
    val plane = getPlane<T>()
    val boxes = arrangeListItems(plane, spacing, children.map { it(length) })
//    val length = getListLength(plane, boxes)
    val breadth = getListBreadth(plane, boxes)

    Box(
        name = name,
        bounds = Bounds(
            dimensions = plane(Vector2i(length, breadth))
        ),
        boxes = boxes
    )
  }
}

fun horizontalList(spacing: Int = 0, name: String = "horizontalList"): (List<Box>) -> Box =
    list(horizontalPlane, spacing, name)

fun verticalList(spacing: Int = 0, name: String = "verticalList"): (List<Box>) -> Box =
    list(verticalPlane, spacing, name)

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

fun flexList(plane: Plane, spacing: Int = 0, name: String = "flexList"): (List<FlexItem>) -> Flower = { items ->
  { dimensions ->
    val relativeBounds = plane(dimensions)
    val totalLength = relativeBounds.x
    val breadth = relativeBounds.y
    val inputBoxes = items.mapNotNull { (it as? BoxFlexItem)?.box }
    val fixedLength = inputBoxes.sumBy { plane(it.bounds.dimensions).x }
//    val fixedBreadth = inputBoxes.sumBy { plane(it.bounds.dimensions).y }
    val totalSpacing = spacing * (items.size - 1)
    val reserved = fixedLength + totalSpacing
    val remaining = totalLength - reserved
    val stretchCount = items.size - inputBoxes.size
    val stretchRation = remaining / stretchCount
    val stretchItemBounds = plane(Vector2i(stretchRation, breadth))

    val boxes = items.map { item ->
      when (item) {
        is BoxFlexItem -> item.box
        is FlowerFlexItem -> item.flower(stretchItemBounds)
        else -> throw Error()
      }
    }
    val arrangedBoxes = arrangeListItems(plane, spacing, boxes)
    Box(
        name = name,
        bounds = Bounds(
            dimensions = dimensions
        ),
        boxes = arrangedBoxes
    )
  }
}

interface LengthFlexItem<T> {
  fun getBox(length: Int): Box
  fun getBoxOrNull(): Box?
}

data class LengthBoxFlexItem<T>(
    val box: Box
) : LengthFlexItem<T> {
  override fun getBox(length: Int): Box = box
  override fun getBoxOrNull(): Box? = box
}

data class LengthFlowerFlexItem<T>(
    val flower: LengthFlower<T>
) : LengthFlexItem<T> {
  override fun getBox(length: Int): Box = flower(length)
  override fun getBoxOrNull(): Box? = null
}

fun <T> flex2(box: Box): LengthFlexItem<T> =
    LengthBoxFlexItem(box)

fun <T> flex2(flower: LengthFlower<T>): LengthFlexItem<T> =
    LengthFlowerFlexItem(flower)

inline fun <reified T : PlaneMap> flexList(spacing: Int = 0, name: String = "flexList", children: List<LengthFlexItem<T>>): LengthFlower<T> {
  return { length ->
    val plane = getPlane<T>()
    val inputBoxes = children.mapNotNull { it.getBoxOrNull() }
    if (inputBoxes.size == children.size)
      list(plane, spacing, name)(inputBoxes)
    else {
      val totalLength = length
      val fixedLength = inputBoxes.sumBy { plane(it.bounds.dimensions).x }
      val breadth = inputBoxes.sumBy { plane(it.bounds.dimensions).y }
      val totalSpacing = spacing * (children.size - 1)
      val reserved = fixedLength + totalSpacing
      val remaining = totalLength - reserved
      val stretchCount = children.size - inputBoxes.size
      val stretchRation = remaining / stretchCount
      val boxes = children.map { it.getBox(stretchRation) }
      val arrangedBoxes = arrangeListItems(plane, spacing, boxes)
      Box(
          name = name,
          bounds = Bounds(
              dimensions = plane(Vector2i(length, breadth))
          ),
          boxes = arrangedBoxes
      )
    }
  }
}
