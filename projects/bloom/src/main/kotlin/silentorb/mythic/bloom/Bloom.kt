package silentorb.mythic.bloom

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import silentorb.mythic.bloom.old.getAttributeBoolean
import silentorb.mythic.drawing.Canvas
import silentorb.mythic.glowing.withCropping
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
) {
  val left: Int get() = position.x
  val top: Int get() = position.y
  val right: Int get() = position.x + dimensions.x
  val bottom: Int get() = position.y + dimensions.y
}

fun toVector4i(bounds: Bounds) = Vector4i(bounds.position.x, bounds.position.y, bounds.dimensions.x, bounds.dimensions.y)

fun mergeDimensions(boxes: List<Box>): Vector2i =
    Vector2i(
        boxes.maxOfOrNull { it.dimensions.x } ?: 0,
        boxes.maxOfOrNull { it.dimensions.y } ?: 0,
    )

typealias Depiction = (Bounds, Canvas) -> Unit
typealias StateBag = Map<String, Any>
typealias StateBagMods = StateBag?

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
    withCropping(viewport) {
      renderChildren()
    }
  } else
    renderChildren()
}

fun enableBloomBlending() {
  globalState.blendEnabled = true
  globalState.blendFunction = Pair(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
}

fun renderLayout(box: Box, canvas: Canvas, debug: Boolean = false) {
  val current = getGLBounds(GL_VIEWPORT)
  if (current.z == 0)
    return

  debugMarkPass(debug, "Bloom GUI Pass") {
    globalState.depthEnabled = false

    // Bloom's faces aren't all facing the same direction.
    // Maybe that is okay because it allows for efficient H/V flipping of 2D elements
    globalState.cullFaces = false

    renderBox(canvas, box, Vector2i.zero)
  }
}
