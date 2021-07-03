package silentorb.mythic.bloom

import silentorb.mythic.drawing.Canvas
import silentorb.mythic.drawing.globalFonts
import silentorb.mythic.glowing.withCropping
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.toVector2i
import silentorb.mythic.typography.IndexedTextStyle
import silentorb.mythic.typography.TextConfiguration
import silentorb.mythic.typography.calculateTextDimensions
import silentorb.mythic.typography.resolveTextStyle

fun textDepiction(style: IndexedTextStyle, content: String, maxWidth: Int = 0): Depiction = { b, c ->
  val position = b.position
  c.drawText(position, style, content, maxWidth.toFloat())
}

fun label(style: IndexedTextStyle, content: String, maxWidth: Int = 0): Box {
  val textStyle = resolveTextStyle(globalFonts(), style)
  val dimensionsContent = if (content.isEmpty())
    "b"
  else
    content

  val dimensionsConfig = TextConfiguration(
      content = dimensionsContent,
      position = Vector2.zero,
      style = textStyle,
      maxWidth = maxWidth.toFloat(),
  )

  val dimensions = calculateTextDimensions(dimensionsConfig)

  return Box(
      name = if (content.length < 32) content else content.substring(0, 32),
      dimensions = dimensions.toVector2i(),
      depiction = textDepiction(style, content, maxWidth)
  )
}

fun textBox(style: IndexedTextStyle, content: String): Flower = { seed ->
  label(style, content, seed.dimensions.x)
}

fun clipBox(bounds: Bounds, depiction: Depiction): Depiction = { b, c ->
  val viewport = c.flipViewport(toVector4i(bounds))
  withCropping(viewport) {
    depiction(b, c)
  }
}

fun solidBackground(backgroundColor: Vector4): Depiction = { b: Bounds, canvas: Canvas ->
  drawFill(b, canvas, backgroundColor)
  drawBorder(b, canvas, Vector4(0f, 0f, 0f, 1f), 2f)
}

fun composeDepictions(vararg depictions: Depiction): Depiction = { b, c ->
  for (depiction in depictions) {
    depiction(b, c)
  }
}
