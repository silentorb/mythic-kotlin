package mythic.bloom.next

import mythic.bloom.*
import org.joml.Vector2i

val forwardPass: ForwardLayout = { Bounds(dimensions = it) }

fun fixedOffset(offset: Vector2i): ForwardLayout = { container ->
  val newDimensions = clippedDimensions(container, offset, container)
  Bounds(
      position = offset,
      dimensions = newDimensions
  )
}

fun fixedOffset(left: Int = 0, top: Int = 0): ForwardLayout = fixedOffset(Vector2i(left, top))

fun forwardOffset(left: PlanePositioner? = null,
                  top: PlanePositioner? = null): ForwardLayout = { container ->
  val position = Vector2i(
      if (left != null) left(horizontalPlaneMap)(container) else 0,
      if (top != null) top(verticalPlaneMap)(container) else 0
  )

  Bounds(
      position = position,
      dimensions = container
  )
}

fun forwardDimensions(
    width: PlanePositioner? = null,
    height: PlanePositioner? = null): ForwardLayout = { container ->
  val dimensions = Vector2i(
      x = if (width != null) width(horizontalPlaneMap)(container) else container.x,
      y = if (height != null) height(verticalPlaneMap)(container) else container.y
  )

  Bounds(
      dimensions = dimensions
  )
}
