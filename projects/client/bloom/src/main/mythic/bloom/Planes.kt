package mythic.bloom

import org.joml.Vector2i

interface PlaneMap {
  fun x(value: Vector2i): Int
  fun y(value: Vector2i): Int

  fun vector(first: Int, second: Int): Vector2i
  fun vector(value: Vector2i): Vector2i
}

class HorizontalPlane : PlaneMap {
  override fun x(value: Vector2i) = value.x
  override fun y(value: Vector2i) = value.y

  override fun vector(first: Int, second: Int): Vector2i = Vector2i(first, second)
  override fun vector(value: Vector2i): Vector2i = value
}

class VerticalPlane : PlaneMap {
  override fun x(value: Vector2i) = value.y
  override fun y(value: Vector2i) = value.x

  override fun vector(first: Int, second: Int): Vector2i = Vector2i(second, first)
  override fun vector(value: Vector2i): Vector2i = Vector2i(value.y, value.x)
}

val horizontalPlaneMap = HorizontalPlane()
val verticalPlaneMap = VerticalPlane()

typealias Plane = (Vector2i) -> Vector2i

fun normalizeBounds(plane: Plane): (Bounds) -> Bounds = { bounds ->
  Bounds(
      position = plane(bounds.position),
      dimensions = plane(bounds.dimensions)
  )
}

val horizontalPlane: Plane = { Vector2i(it.x, it.y) }
val verticalPlane: Plane = { Vector2i(it.y, it.x) }
