package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i
import kotlin.math.max

typealias BloomKey = String

fun clippedDimensions(parent: Vector2i, childPosition: Vector2i, childDimensions: Vector2i): Vector2i {
  return if (childPosition.x + childDimensions.x > parent.x ||
      childPosition.y + childDimensions.y > parent.y)
    Vector2i(
        x = Math.min(childDimensions.x, parent.x - childPosition.x),
        y = Math.min(childDimensions.y, parent.y - childPosition.y)
    )
  else
    childDimensions
}

fun moveBounds(offset: Vector2i, container: Vector2i): (Bounds) -> Bounds = { child ->
  val newPosition = child.position + offset
  val newDimensions = clippedDimensions(container, newPosition, child.dimensions)

  child.copy(
      position = newPosition,
      dimensions = newDimensions
  )
}

typealias Positioner = (Vector2i) -> Int

typealias PlanePositioner = (PlaneMap) -> Positioner

typealias ReversePositioner = (Vector2i, Vector2i) -> Int

typealias Aligner = (Int, Int) -> Int

typealias ReversePlanePositioner = (PlaneMap) -> ReversePositioner

val centered: Aligner = { parent, child ->
  max(0, (parent - child) / 2)
}

val justifiedStart: Aligner = { _, _ ->
  0
}

fun percentage(value: Float): Aligner = { parent, _ ->
  (parent.toFloat() * value).toInt()
}

fun axisMargin(plane: Plane, all: Int = 0, left: Int = all, top: Int = all, bottom: Int = all, right: Int = all): (LengthFlower) -> LengthFlower = { child ->
  { length ->
    val sizeOffset = Vector2i(left + right, top + bottom)
    val relativeSizeOffset = plane(sizeOffset)
    val box = child(length - relativeSizeOffset.x)
    Box(
        dimensions = box.dimensions + sizeOffset,
        boxes = listOf(
            OffsetBox(
                child = box,
                offset = Vector2i(left, top)
            )
        )
    )
  }
}

fun lengthToFlower(plane: Plane): (LengthFlower) -> Flower = { lengthFlower ->
  { dimensions ->
    val length = plane(dimensions).x
    lengthFlower(length)
  }
}

fun boxMargin(all: Int = 0, left: Int = all, top: Int = all, bottom: Int = all, right: Int = all): (Box) -> Box = { box ->
  val sizeOffset = Vector2i(left + right, top + bottom)
  Box(
      dimensions = box.dimensions + sizeOffset,
      boxes = listOf(
          OffsetBox(
              child = box,
              offset = Vector2i(left, top)
          )
      )
  )
}

fun centered(box: Box): Flower = { dimensions ->
  val offset = Vector2i(
      centered(dimensions.x, box.dimensions.x),
      centered(dimensions.y, box.dimensions.y)
  )
  Box(
      dimensions = dimensions,
      boxes = listOf(OffsetBox(box, offset))
  )
}

fun alignToLengthFlower(aligner: Aligner): (Plane) -> (Box) -> LengthFlower = { plane ->
  { box ->
    { length ->
      val relativeBoxDimensions = plane(box.dimensions)
      val offset = plane(
          Vector2i(
              aligner(length, relativeBoxDimensions.x),
              relativeBoxDimensions.y
          )
      )

      // Currently doesn't support negative numbers
      assert(offset.x >= 0)
      assert(offset.y >= 0)

      Box(
          dimensions = box.dimensions + offset,
          boxes = listOf(OffsetBox(box, offset))
      )
    }
  }
}

fun align(horizontal: Aligner, vertical: Aligner, flower: Flower): Flower =
    { dimensions ->
      val box = flower(dimensions)
      val offset = Vector2i(
          horizontal(dimensions.x, box.dimensions.x),
          vertical(dimensions.y, box.dimensions.y)
      )

      // Currently doesn't support negative numbers
      assert(offset.x >= 0)
      assert(offset.y >= 0)

      Box(
          dimensions = box.dimensions + offset,
          boxes = listOf(OffsetBox(box, offset))
      )
    }

fun align(horizontal: Aligner, vertical: Aligner, box: Box): Flower =
    align(horizontal, vertical, box.asFlower())

val centeredAxis = alignToLengthFlower(centered)

fun percentageAxis(plane: Plane, value: Float) = alignToLengthFlower(percentage(value))(plane)

val reverseJustifiedStart: ReversePlanePositioner = { plane ->
  { _, _ ->
    0
  }
}

val reverseJustifiedEnd: ReversePlanePositioner = { plane ->
  { parent, child ->
    plane.x(parent) - plane.x(child)
  }
}

fun fixedReverse(value: Int): ReversePlanePositioner = { plane ->
  { _, _ ->
    value
  }
}
