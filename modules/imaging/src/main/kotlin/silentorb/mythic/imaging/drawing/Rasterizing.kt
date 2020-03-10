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

fun getPolygon(points: List<Vector2>): Shape {
  assert(points.size > 1)
  val polygon = GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size)
  val first = points.first()
  polygon.moveTo(first.x, first.y)
  points.drop(1).forEach { point ->
    polygon.lineTo(point.x, point.y)
  }
  return polygon
}

fun rasterizeShape(canvas: Graphics2D, shapes: Shapes, id: Id, shape: Shape) {
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

fun rasterizeShape(dimensions: Vector2, canvas: Graphics2D, shapes: Shapes): (Id) -> Unit = { id ->
  val function = shapes.functions[id]!!
  val rasterize = when (function) {
    ShapeFunction.polygon -> ::getPolygon
  }
  val points = shapes.pointLists[id]!!

  val rasterizeWithOffset = { offset: Vector2 ->
    val shape = rasterize(points.map { it + offset })
    rasterizeShape(canvas, shapes, id, shape)
  }

  rasterizeShape(canvas, shapes, id, rasterize(points))

  val offsetX = if (points.any { it.x < 0f })
    dimensions.x
  else if (points.any { it.x >= dimensions.x })
    -dimensions.x
  else
    0f

  val offsetY = if (points.any { it.y < 0f })
    dimensions.y
  else if (points.any { it.x >= dimensions.y })
    -dimensions.y
  else
    0f

  if (offsetX != 0f)
    rasterizeWithOffset(Vector2(offsetX, 0f))

  if (offsetY != 0f)
    rasterizeWithOffset(Vector2(0f, offsetY))

  if (offsetX != 0f && offsetY != 0f)
    rasterizeWithOffset(Vector2(offsetX, offsetY))
}

fun scaleShapes(dimensions: Vector2, shapes: Shapes): Shapes =
    shapes.copy(
        pointLists = shapes.pointLists.mapValues { (_, points) ->
          points.map { point ->
            point * dimensions / 100f
          }
        }
    )

fun transformShapes(shapes: Shapes): Shapes =
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
  val floatDimensions = dimensions.toVector2()
  val scaledShapes = scaleShapes(floatDimensions, transformShapes(shapes))
  shapes.functions.keys.forEach(rasterizeShape(floatDimensions, canvas, scaledShapes))
  return bufferedImageToBitmap(dimensions, channels, image)
}
