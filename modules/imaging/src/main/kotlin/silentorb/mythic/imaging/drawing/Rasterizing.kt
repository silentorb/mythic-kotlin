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
import java.awt.geom.GeneralPath
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

fun getPolygon(shapes: Shapes, id: Id): Shape {
  val points = shapes.pointLists[id]!!
  assert(points.size > 1)
  val polygon = GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size)
  val first = points.first()
  polygon.moveTo(first.x, first.y)
  points.drop(1).forEach { point ->
    polygon.lineTo(point.x, point.y)
  }
  return polygon
}

fun rasterizeShape(canvas: Graphics2D, shapes: Shapes): (Id) -> Unit = { id ->
  val function = shapes.functions[id]!!
  val rasterize = when (function) {
    ShapeFunction.polygon -> ::getPolygon
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
        pointLists = shapes.pointLists.mapValues { (_, points) ->
          points.map { point ->
            point * dimensions / 100f
          }
        }
    )

fun transformShapes(dimensions: Vector2, shapes: Shapes): Shapes =
    shapes.copy(
        pointLists = shapes.pointLists.mapValues { (id, points) ->
          val transform = shapes.transforms[id]
          if (transform == null)
            points
          else {
            points.map { point ->
              transform.transform(point)
            }
          }
        }
    )

fun rasterizeShapes(dimensions: Vector2i, shapes: Shapes, channels: Int): Bitmap {
  val image = BufferedImage(dimensions.x, dimensions.y, getBufferedImageTypeByChannels(channels).value)
  val canvas = image.createGraphics()
  val scaledShapes = scaleShapes(dimensions.toVector2(), shapes)
  shapes.functions.keys.forEach(rasterizeShape(canvas, scaledShapes))
  return bufferedImageToBitmap(dimensions, channels, image)
}
