package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

typealias FixedChildArranger = (Vector2i) -> List<Bounds>
typealias FlowerContainer = (List<Flower>) -> Flower
typealias FlowerContainerWrapper = (FlowerContainer) -> FlowerContainer
typealias BoxFlowerContainer = (List<Box>) -> Flower
typealias BoxContainer = (List<Box>) -> Box
typealias BoxContainerWrapper = (BoxContainer) -> BoxContainer

fun boxToFlower(box: Box): Flower = { box }

fun flowerToBox(flower: Flower): Box = flower(Vector2i.zero)

fun flowersToBoxes(flowers: List<Flower>): List<Box> = flowers.map(::flowerToBox)

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

fun horizontalList(spacing: Int = 0, name: String = "horizontalList"): (List<Box>) -> Box =
    list(horizontalPlane, spacing, name)

fun verticalList(spacing: Int = 0, name: String = "verticalList"): (List<Box>) -> Box =
    list(verticalPlane, spacing, name)

enum class FlexType {
  stretch,
  fixed,
}

data class FlexItem(
    val flower: Flower,
    val type: FlexType = FlexType.fixed
)

val flexStretch: (Flower) -> FlexItem = { flower ->
  FlexItem(flower, FlexType.stretch)
}

fun flexList(plane: Plane, spacing: Int = 0, name: String = "flexList"): (List<FlexItem>) -> Flower = { items ->
  { dimensions ->
//    val totalSpacing = (items.size - 1) * spacing
    val firstPass = items.map { item ->
      if (item.type == FlexType.fixed) {
        item.flower(dimensions)
      } else
        null
    }
    val lengths = firstPass.map { if (it != null) plane(it.bounds.end).x else null }
    val otherLength = firstPass.filterNotNull().map { plane(it.bounds.end).y }.maxOrNull()
    assert(otherLength != null)
    val resolvedDimensions = plane(Vector2i(0, otherLength!!)) + plane(Vector2i(plane(dimensions).x, 0))
    val boundsList = fixedLengthArranger(plane, spacing, lengths)(resolvedDimensions)
    val boxes = firstPass.zip(boundsList.zip(items)) { box, (bounds, item) ->
      if (box != null) {
        box.copy(
            bounds = bounds
        )
      } else {
        val newBox = item.flower(bounds.dimensions)
        newBox.copy(
            bounds = bounds
        )
      }
    }
    Box(
        name = name,
        bounds = Bounds(
            dimensions = resolvedDimensions
        ),
        boxes = boxes
    )
  }
}
