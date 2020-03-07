package silentorb.mythic.imaging.drawing

import silentorb.mythic.imaging.Bitmap
import silentorb.mythic.imaging.bufferedImageToBitmap
import silentorb.mythic.imaging.getBufferedImageTypeByChannels
import silentorb.mythic.imaging.toAwtColor
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.toVector2
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

fun getRectangle(shapes: Shapes, id: Id): Shape {
  val shapeDimensions = shapes.dimensions[id]!!
  return Rectangle2D.Float(0f, 0f, shapeDimensions.x, shapeDimensions.y)
}

fun rasterizeShape(canvas: Graphics2D, shapes: Shapes): (Id) -> Unit = { id ->
  val function = shapes.functions[id]!!
  val rasterize = when (function) {
    ShapeFunction.rectangle -> ::getRectangle
  }
  val shape = rasterize(shapes, id)
  val fill = shapes.rgbFills[id]
  if (fill != null) {
    canvas.paint = toAwtColor(fill)
    canvas.fill(shape)
  }
  val stroke = shapes.strokes[id]
  if (stroke != null) {
    canvas.paint = toAwtColor(stroke.color)
    canvas.stroke = BasicStroke(stroke.width)
    canvas.draw(shape)
  }
}

fun scaleShapes(dimensions: Vector2, shapes: Shapes): Shapes =
    shapes.copy(
        dimensions = shapes.dimensions.mapValues { (_, value) -> value * dimensions / 100f }
    )

fun rasterizeShapes(dimensions: Vector2i, shapes: Shapes, channels: Int): Bitmap {
  val image = BufferedImage(dimensions.x, dimensions.y, getBufferedImageTypeByChannels(channels).value)
  val canvas = image.createGraphics()
  val scaledShapes = scaleShapes(dimensions.toVector2(), shapes)
  shapes.functions.keys.forEach(rasterizeShape(canvas, scaledShapes))
  return bufferedImageToBitmap(dimensions, channels, image)
}
