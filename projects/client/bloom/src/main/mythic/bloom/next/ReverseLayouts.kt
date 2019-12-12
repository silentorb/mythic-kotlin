package mythic.bloom.next

import mythic.bloom.*
import org.joml.Vector2i

val reversePass: ReverseLayout = { _, bounds, _ -> bounds }

operator fun ReverseLayout.plus(other: ReverseLayout): ReverseLayout = { parent, bounds, child ->
  val a = this(parent, bounds, child)
  other(parent, a, child)
}

fun reverseOffset(left: ReversePlanePositioner? = null,
                  top: ReversePlanePositioner? = null): ReverseLayout = { parent, bounds, child ->
  bounds.copy(
      position = Vector2i(
          if (left != null) left(horizontalPlaneMap)(parent, bounds, child) else bounds.position.x,
          if (top != null) top(verticalPlaneMap)(parent, bounds, child) else bounds.position.y
      )
  )
}

fun reverseDimensions(width: ReversePlanePositioner? = null,
                      height: ReversePlanePositioner? = null): ReverseLayout = { parent, bounds, child ->
  bounds.copy(
      dimensions = Vector2i(
          if (width != null) width(horizontalPlaneMap)(parent, bounds, child) else bounds.dimensions.x,
          if (height != null) height(verticalPlaneMap)(parent, bounds, child) else bounds.dimensions.y
      )
  )
}

val shrink: ReverseLayout = { parent, bounds, child ->
  bounds.copy(
      dimensions = child.dimensions
  )
}

val shrinkVertical: ReverseLayout = { parent, bounds, child ->
  bounds.copy(
      dimensions = Vector2i(bounds.dimensions.x, child.dimensions.y)
  )
}
