package silentorb.mythic.bloom.old

import silentorb.mythic.bloom.*
import silentorb.mythic.drawing.Canvas
import silentorb.mythic.spatial.Vector2i

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

fun lengthToFlower(plane: Plane): (LengthFlower) -> Flower = { lengthFlower ->
  { dimensions ->
    val length = plane(dimensions).x
    lengthFlower(length)
  }
}

fun crop(bounds: Bounds, canvas: Canvas, action: () -> Unit) = canvas.crop(toVector4i(bounds), action)

fun onClickPersisted(key: String, logicModule: LogicModuleOld): LogicModuleOld = { bundle ->
  val visibleBounds = bundle.visibleBounds
  if (visibleBounds != null && isClickInside(visibleBounds, bundle.state.input))
    logicModule(bundle)
  else {
    val flowerState = bundle.state.resourceBag[key]
    if (flowerState != null)
      mapOf(key to flowerState)
    else
      null
  }
}

//fun maxBounds(a: Bounds, b: Bounds): Bounds {
//  val x1 = Math.min(a.position.x, b.position.x)
//  val y1 = Math.min(a.position.y, b.position.y)
//  val x2 = Math.max(a.position.x + a.dimensions.x, b.position.x + b.dimensions.x)
//  val y2 = Math.max(a.position.y + a.dimensions.y, b.position.y + b.dimensions.y)
//  return Bounds(x1, y1, x2 - x1, y2 - y1)
//}
typealias LogicModuleOld = (LogicBundle) -> StateBagMods
