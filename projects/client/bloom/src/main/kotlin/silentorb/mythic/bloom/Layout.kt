package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i
import kotlin.math.max

typealias BloomKey = String

fun boxToFlower(box: Box): Flower = { box }

fun flowerToBox(flower: Flower): Box = flower(Vector2i.zero)

fun flowersToBoxes(flowers: List<Flower>): List<Box> = flowers.map(::flowerToBox)

fun resolveLengths(boundLength: Int, lengths: List<Int?>): List<Int> {
  val exacts = lengths.filterNotNull()
  val total = exacts.sum()

  if (exacts.size == lengths.size) {
    if (total != boundLength)
      throw Error("Could not stretch or shrink to fit bounds")

    return exacts
  } else {
    val stretchCount = lengths.size - exacts.size
    val stretchLength = (boundLength - total) / stretchCount
    return lengths.map { if (it != null) it else stretchLength }
  }
}

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

//val centered: ReversePlanePositioner = { plane ->
//  { parent, child ->
//    center(plane.x(child), plane.x(parent))
//  }
//}

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

fun lengthToFlower(plane: Plane): (LengthFlower) -> Flower = { flower ->
  { dimensions ->
    flower(plane(dimensions).x)
  }
}

fun centeredAxis(plane: Plane): (Box) -> LengthFlower = { box ->
  { length ->
    val relativeBoxDimensions = plane(box.dimensions)
    val offset = plane(
        Vector2i(
            centered(length, relativeBoxDimensions.x),
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

val justifiedEnd: ReversePlanePositioner = { plane ->
  { parent, child ->
    plane.x(parent) - plane.x(child)
  }
}

val justifiedStart: ReversePlanePositioner = { plane ->
  { _, _ ->
    0
  }
}

fun fixedReverse(value: Int): ReversePlanePositioner = { plane ->
  { _, _ ->
    value
  }
}

fun percentage(value: Float): PlanePositioner = { plane ->
  { parent ->
    (plane.x(parent).toFloat() * value).toInt()
  }
}
