package silentorb.mythic.bloom

import silentorb.mythic.drawing.Canvas
import silentorb.mythic.drawing.globalFonts
import silentorb.mythic.glowing.cropStack
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.toVector2i
import silentorb.mythic.typography.IndexedTextStyle
import silentorb.mythic.typography.TextConfiguration
import silentorb.mythic.typography.calculateTextDimensions
import silentorb.mythic.typography.resolveTextStyle

fun textDepiction(style: IndexedTextStyle, content: String): Depiction = { b, c ->
  val position = b.position
  c.drawText(position, style, content)
}

fun label(style: IndexedTextStyle, content: String): Box {
  val config = TextConfiguration(content, Vector2(), resolveTextStyle(globalFonts(), style))
  val dimensions = calculateTextDimensions(config)
  return Box(
      name = if (content.length < 32) content else content.substring(0, 32),
      dimensions = dimensions.toVector2i(),
      depiction = textDepiction(style, content)
  )
}

fun clipBox(bounds: Bounds, depiction: Depiction): Depiction = { b, c ->
  val viewport = c.flipViewport(toVector4i(bounds))
  cropStack(viewport) {
    depiction(b, c)
  }
}

fun solidBackground(backgroundColor: Vector4): Depiction = { b: Bounds, canvas: Canvas ->
  drawFill(b, canvas, backgroundColor)
  drawBorder(b, canvas, Vector4(0f, 0f, 0f, 1f))
}
