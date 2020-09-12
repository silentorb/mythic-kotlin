package silentorb.mythic.bloom

import org.lwjgl.opengl.GL11
import silentorb.mythic.drawing.Canvas
import silentorb.mythic.glowing.cropStack
import silentorb.mythic.glowing.debugMarkPass
import silentorb.mythic.glowing.getGLBounds
import silentorb.mythic.glowing.globalState
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.Vector4i
import silentorb.mythic.spatial.toVector2

data class Bounds(
    val position: Vector2i,
    val dimensions: Vector2i
)

//data class Bounds(
//    val position: Vector2i = Vector2i(),
//    val dimensions: Vector2i
//) {
//  constructor(x: Int, y: Int, width: Int, height: Int) :
//      this(Vector2i(x, y), Vector2i(width, height))
//
//  constructor(values: Vector4i) :
//      this(Vector2i(values.x, values.y), Vector2i(values.z, values.w))
//
//  val left: Int get() = position.x
//  val top: Int get() = position.y
//  val right: Int get() = position.x + dimensions.x
//  val bottom: Int get() = position.y + dimensions.y
//
//  val end: Vector2i get() = position + dimensions
//
//  fun toVector4i() = Vector4i(position.x, position.y, dimensions.x, dimensions.y)
//
//  companion object {
//    fun fromEnds(left: Int, top: Int, right: Int, bottom: Int) =
//        Bounds(left, top, right - left, bottom - top)
//  }
//}

fun toVector4i(bounds: Bounds) = Vector4i(bounds.position.x, bounds.position.y, bounds.dimensions.x, bounds.dimensions.y)

fun mergeDimensions(boxes: List<Box>): Vector2i =
    Vector2i(
        boxes.maxOfOrNull { it.dimensions.x } ?: 0,
        boxes.maxOfOrNull { it.dimensions.y } ?: 0,
    )

//fun mergeBounds(list: List<Bounds>): Bounds {
//  if (list.none())
//    return Bounds(0, 0, 0, 0)
//
//  val left = list.minBy { it.left }!!.left
//  val right = list.maxBy { it.right }!!.right
//  val top = list.minBy { it.top }!!.top
//  val bottom = list.maxBy { it.bottom }!!.bottom
//  return Bounds(
//      x = left,
//      y = top,
//      width = right - left,
//      height = bottom - top
//  )
//}

//val emptyBounds = Bounds(0, 0, 0, 0)

typealias Depiction = (Bounds, Canvas) -> Unit
typealias StateBag = Map<String, Any>
typealias StateBagMods = StateBag?

fun crop(bounds: Bounds, canvas: Canvas, action: () -> Unit) = canvas.crop(toVector4i(bounds), action)

//fun listBounds(plane: Plane, padding: Int, dimensions: Vector2i, lengths: List<Int>): List<Bounds> {
//  var progress = 0
//  val otherLength = plane(dimensions).y
//
//  return lengths.mapIndexed { i, length ->
//    val b = Bounds(
//        plane(Vector2i(progress, 0)),
//        plane(Vector2i(length, otherLength))
//    )
//    progress += length
//    if (i != lengths.size - 1)
//      progress += padding
//
//    b
//  }
//}

typealias LengthArrangement = (dimensions: Vector2i, lengths: List<Int>) -> List<Bounds>

//fun lengthArranger(plane: Plane, padding: Int): LengthArrangement = { dimensions, lengths: List<Int> ->
//  listBounds(plane, padding, dimensions, lengths)
//}

//fun fixedLengthArranger(plane: Plane, spacing: Int, lengths: List<Int?>): FixedChildArranger = { dimensions ->
//  val totalPadding = spacing * (lengths.size - 1)
//  val boundLength = plane(dimensions).x - totalPadding
//  lengthArranger(plane, spacing)(dimensions, resolveLengths(boundLength, lengths))
//}

fun centeredPosition(boundsLength: Int, length: Int): Int =
    (boundsLength - length) / 2

fun centeredPosition(plane: Plane, bounds: Vector2i, length: Int?): Int =
    if (length == null)
      0
    else
      centeredPosition(plane(bounds).x, length)

fun drawBorder(bounds: Bounds, canvas: Canvas, color: Vector4, thickness: Float = 1f) {
  canvas.drawSquare(bounds.position.toVector2(), bounds.dimensions.toVector2(), canvas.outline(color, thickness))
}

data class LineStyle(
    val color: Vector4,
    val thickness: Float
)

fun drawBorder(bounds: Bounds, canvas: Canvas, style: LineStyle) {
  canvas.drawSquare(bounds.position.toVector2(), bounds.dimensions.toVector2(), canvas.outline(style.color, style.thickness))
}

fun drawFill(bounds: Bounds, canvas: Canvas, color: Vector4) {
  canvas.drawSquare(bounds.position.toVector2(), bounds.dimensions.toVector2(), canvas.solid(color))
}

fun toAbsoluteBoundsRecursive(box: Box, offset: Vector2i = Vector2i.zero): Box {
  return box.copy(
      boxes = box.boxes.map { offsetBox ->
        val localOffset = offset + offsetBox.offset
        offsetBox.copy(
            child = toAbsoluteBoundsRecursive(offsetBox.child, localOffset),
            offset = localOffset
        )
      }
  )
}

fun renderBox(canvas: Canvas, box: Box, offset: Vector2i, debug: Boolean = false) {
  val depiction = box.depiction
  if (depiction != null) {
    debugMarkPass(debug, box.name) {
      depiction(Bounds(position = offset, dimensions = box.dimensions), canvas)
    }
  }

  val renderChildren = {
    for (child in box.boxes) {
      renderBox(canvas, child.child, child.offset)
    }
  }

  if (getAttributeBoolean(box, clipBoundsKey)) {
    val viewport = canvas.flipViewport(toVector4i(Bounds(position = offset, dimensions = box.dimensions)))
    cropStack(viewport) {
      renderChildren()
    }
  } else
    renderChildren()
}

fun renderLayout(box: Box, canvas: Canvas, debug: Boolean = false) {
  val current = getGLBounds(GL11.GL_VIEWPORT)
  if (current.z == 0)
    return

  debugMarkPass(debug, "Bloom GUI Pass") {
    globalState.depthEnabled = false
    globalState.blendEnabled = true
    globalState.blendFunction = Pair(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    renderBox(canvas, box, Vector2i.zero)
  }
}

fun centeredPosition(bounds: Bounds, contentDimensions: Vector2i): Vector2i {
  val dimensions = bounds.dimensions
  return bounds.position + Vector2i(
      centeredPosition(horizontalPlane, dimensions, contentDimensions.x),
      centeredPosition(verticalPlane, dimensions, contentDimensions.y)
  )
}

//fun centeredBounds(bounds: Bounds, contentDimensions: Vector2i): Bounds {
//  return Bounds(
//      centeredPosition(bounds, contentDimensions) + bounds.position,
//      contentDimensions
//  )
//}
