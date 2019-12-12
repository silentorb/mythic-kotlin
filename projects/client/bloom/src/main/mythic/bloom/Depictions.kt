package mythic.bloom

import mythic.bloom.next.Box
import mythic.bloom.next.Flower
import mythic.drawing.Canvas
import mythic.drawing.globalFonts
import mythic.glowing.cropStack
import mythic.spatial.Vector2
import mythic.spatial.Vector4
import mythic.spatial.toVector2i
import mythic.typography.IndexedTextStyle
import mythic.typography.TextConfiguration
import mythic.typography.calculateTextDimensions
import mythic.typography.resolveTextStyle

fun textDepiction(style: IndexedTextStyle, content: String): Depiction = { b, c ->
  val position = b.position
  c.drawText(position, style, content)
}

fun label(style: IndexedTextStyle, content: String): Flower = { seed ->
  val config = TextConfiguration(content, Vector2(), resolveTextStyle(globalFonts(), style))
  val dimensions = calculateTextDimensions(config)
  Box(
      bounds = Bounds(
          dimensions = dimensions.toVector2i()
      ),
      depiction = textDepiction(style, content),
      name = if (content.length < 32) content else content.substring(0, 32)
  )
}

fun clipBox(bounds: Bounds, depiction: Depiction): Depiction = { b, c ->
  val viewport = c.flipViewport(bounds.toVector4i())
  cropStack(viewport) {
    depiction(b, c)
  }
}

fun solidBackground(backgroundColor: Vector4): Depiction = { b: Bounds, canvas: Canvas ->
  drawFill(b, canvas, backgroundColor)
  drawBorder(b, canvas, Vector4(0f, 0f, 0f, 1f))
}
