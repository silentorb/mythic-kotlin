package mythic.bloom

import mythic.bloom.next.Box
import mythic.drawing.Canvas
import mythic.glowing.cropStack
import mythic.glowing.debugMarkPass
import mythic.glowing.getGLBounds
import mythic.glowing.globalState
import mythic.spatial.Vector4
import mythic.spatial.toVector2
import org.joml.Vector2i
import org.joml.Vector4i
import org.joml.plus
import org.lwjgl.opengl.GL11

data class Bounds(
    val position: Vector2i = Vector2i(),
    val dimensions: Vector2i
) {
  constructor(x: Int, y: Int, width: Int, height: Int) :
      this(Vector2i(x, y), Vector2i(width, height))

  constructor(values: Vector4i) :
      this(Vector2i(values.x, values.y), Vector2i(values.z, values.w))

  val left: Int get() = position.x
  val top: Int get() = position.y
  val right: Int get() = position.x + dimensions.x
  val bottom: Int get() = position.y + dimensions.y

  val end: Vector2i get() = position + dimensions

  fun toVector4i() = Vector4i(position.x, position.y, dimensions.x, dimensions.y)

  companion object {
    fun fromEnds(left: Int, top: Int, right: Int, bottom: Int) =
        Bounds(left, top, right - left, bottom - top)
  }
}

fun mergeBounds(list: List<Bounds>): Bounds {
  if (list.none())
    return Bounds(0, 0, 0, 0)

  val left = list.minBy { it.left }!!.left
  val right = list.maxBy { it.right }!!.right
  val top = list.minBy { it.top }!!.top
  val bottom = list.maxBy { it.bottom }!!.bottom
  return Bounds(
      x = left,
      y = top,
      width = right - left,
      height = bottom - top
  )
}

val emptyBounds = Bounds(0, 0, 0, 0)

typealias Depiction = (Bounds, Canvas) -> Unit
typealias StateBag = Map<String, Any>
typealias StateBagMods = StateBag?

fun crop(bounds: Bounds, canvas: Canvas, action: () -> Unit) = canvas.crop(bounds.toVector4i(), action)

fun listBounds(plane: Plane, padding: Int, dimensions: Vector2i, lengths: List<Int>): List<Bounds> {
  var progress = 0
  val otherLength = plane(dimensions).y

  return lengths.mapIndexed { i, length ->
    val b = Bounds(
        plane(Vector2i(progress, 0)),
        plane(Vector2i(length, otherLength))
    )
    progress += length
    if (i != lengths.size - 1)
      progress += padding

    b
  }
}

typealias LengthArrangement = (dimensions: Vector2i, lengths: List<Int>) -> List<Bounds>

fun lengthArranger(plane: Plane, padding: Int): LengthArrangement = { dimensions, lengths: List<Int> ->
  listBounds(plane, padding, dimensions, lengths)
}

fun fixedLengthArranger(plane: Plane, spacing: Int, lengths: List<Int?>): FixedChildArranger = { dimensions ->
  val totalPadding = spacing * (lengths.size - 1)
  val boundLength = plane(dimensions).x - totalPadding
  lengthArranger(plane, spacing)(dimensions, resolveLengths(boundLength, lengths))
}

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

fun toAbsoluteBounds(parentOffset: Vector2i, box: Box): Box {
  val localOffset = parentOffset + box.bounds.position
  return box.copy(
      bounds = box.bounds.copy(
          position = localOffset
      ),
      boxes = box.boxes.map { toAbsoluteBounds(localOffset, it) }
  )
}

fun renderBox(canvas: Canvas, box: Box, debug: Boolean = false) {
  val depiction = box.depiction
  if (depiction != null) {
    debugMarkPass(debug, box.name) {
      depiction(box.bounds, canvas)
    }
  }

  val renderChildren = {
    for (child in box.boxes) {
      renderBox(canvas, child)
    }
  }

  if (box.clipBounds) {
    val viewport = canvas.flipViewport(box.bounds.toVector4i())
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
    renderBox(canvas, box)
  }
}

fun centeredPosition(bounds: Bounds, contentDimensions: Vector2i): Vector2i {
  val dimensions = bounds.dimensions
  return bounds.position + Vector2i(
      centeredPosition(horizontalPlane, dimensions, contentDimensions.x),
      centeredPosition(verticalPlane, dimensions, contentDimensions.y)
  )
}

fun centeredBounds(bounds: Bounds, contentDimensions: Vector2i): Bounds {
  return Bounds(
      centeredPosition(bounds, contentDimensions) + bounds.position,
      contentDimensions
  )
}
