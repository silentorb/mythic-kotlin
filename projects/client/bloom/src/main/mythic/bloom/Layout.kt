package mythic.bloom

import org.joml.Vector2i
import org.joml.plus

typealias BloomId = Long
typealias BloomKey = String

data class FlatBox(
    val bounds: Bounds,
    val depiction: Depiction? = null,
    val clipBounds: Bounds? = null,
    val handler: Any? = null,
    val logic: LogicModuleOld? = null
)

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

typealias ReversePositioner = (Vector2i, Bounds, Bounds) -> Int

typealias ReversePlanePositioner = (PlaneMap) -> ReversePositioner

val centered: ReversePlanePositioner = { plane ->
  { parent, _, child ->
    (plane.x(parent) - plane.x(child.dimensions)) / 2
  }
}

fun fixedReverse(value: Int): ReversePlanePositioner = { plane ->
  { _, _, _ ->
    value
  }
}

fun percentage(value: Float): PlanePositioner = { plane ->
  { parent ->
    (plane.x(parent).toFloat() * value).toInt()
  }
}
