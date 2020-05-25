package silentorb.mythic.imaging.texturing.drawing

import silentorb.imp.core.*
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.imaging.texturing.*
import silentorb.mythic.imaging.texturing.math.vector2Type
import silentorb.mythic.spatial.Matrix3
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3

const val drawingPath = "silentorb.mythic.generation.drawing"

val shapesType = newTypePair(PathKey(drawingPath, "Shapes"))

val newRectangleFunction = CompleteFunction(
    path = PathKey(drawingPath, "rectangle"),
    signature = CompleteSignature(
        parameters = listOf(
            CompleteParameter("dimensions", relativeDimensionsType)
        ),
        output = shapesType
    ),
    implementation = { arguments ->
      val dimensions = arguments["dimensions"] as Vector2
      newRectangle(dimensions)
    }
)

val rgbColorFillShapeFunction = CompleteFunction(
    path = PathKey(drawingPath, "colorFill"),
    signature = CompleteSignature(
        parameters = listOf(
            CompleteParameter("color", rgbColorType),
            CompleteParameter("shapes", shapesType)
        ),
        output = shapesType
    ),
    implementation = { arguments ->
      val color = arguments["color"] as RgbColor
      val shapes = arguments["shapes"] as Shapes
      shapes.copy(
          rgbFills = shapes.ids
              .associateWith { color }
      )
    }
)

val grayscaleFillShapeFunction = CompleteFunction(
    path = PathKey(drawingPath, "grayscaleFill"),
    signature = CompleteSignature(
        parameters = listOf(
            CompleteParameter("value", floatType),
            CompleteParameter("shapes", shapesType)
        ),
        output = shapesType
    ),
    implementation = { arguments ->
      val value = arguments["value"] as Float
      val shapes = arguments["shapes"] as Shapes
      shapes.copy(
          grayscaleFills = shapes.ids
              .associateWith { value }
      )
    }
)

val grayscaleStrokeShapeFunction = CompleteFunction(
    path = PathKey(drawingPath, "grayscaleStroke"),
    signature = CompleteSignature(
        parameters = listOf(
            CompleteParameter("value", floatType),
            CompleteParameter("width", floatType),
            CompleteParameter("shapes", shapesType)
        ),
        output = shapesType
    ),
    implementation = { arguments ->
      val value = arguments["value"] as Float
      val width = arguments["width"] as Float
      val shapes = arguments["shapes"] as Shapes
      shapes.copy(
          grayscaleStrokes = shapes.ids
              .associateWith { GrayscaleStroke(width = width, value = value) }
      )
    }
)

val rgbStrokeShapeFunction = CompleteFunction(
    path = PathKey(drawingPath, "rgbStroke"),
    signature = CompleteSignature(
        parameters = listOf(
            CompleteParameter("color", rgbColorType),
            CompleteParameter("width", floatType),
            CompleteParameter("shapes", shapesType)
        ),
        output = shapesType
    ),
    implementation = { arguments ->
      val color = arguments["color"] as Vector3
      val width = arguments["width"] as Float
      val shapes = arguments["shapes"] as Shapes
      shapes.copy(
          rgbStrokes = shapes.ids
              .associateWith { RgbStroke(width = width, color = color) }
      )
    }
)

val translateShapeFunction = CompleteFunction(
    path = PathKey(drawingPath, "translate"),
    signature = CompleteSignature(
        parameters = listOf(
            CompleteParameter("offset", vector2Type),
            CompleteParameter("shapes", shapesType)
        ),
        output = shapesType
    ),
    implementation = { arguments ->
      val offset = arguments["offset"] as Vector2
      val shapes = arguments["shapes"] as Shapes
      val modifiedTransforms = shapes.transforms.mapValues { (_, transform) ->
        transform.translate(offset)
      }
      val newTransformIds = shapes.ids.minus(shapes.transforms.keys)
      val newTransforms = if (newTransformIds.any()) {
        val transform = Matrix3().translate(offset)
        newTransformIds.associateWith { transform }
      } else
        mapOf()
      shapes.copy(
          transforms = modifiedTransforms.plus(newTransforms)
      )
    }
)

fun rasterizeShapesFunction(bitmapType: TypePair) = CompleteFunction(
    path = PathKey(drawingPath, "rasterizeShapes"),
    signature = CompleteSignature(
        parameters = listOf(
            CompleteParameter("bitmap", bitmapType),
            CompleteParameter("shapes", shapesType)
        ),
        output = bitmapType
    ),
    implementation = { arguments ->
      val bitmap = arguments["bitmap"] as Bitmap
      val shapes = arguments["shapes"] as Shapes
      rasterizeShapes(shapes, bitmap)
    }
)

fun drawingFunctions() = listOf(
    grayscaleFillShapeFunction,
    newRectangleFunction,
    grayscaleStrokeShapeFunction,
    rgbStrokeShapeFunction,
    rasterizeShapesFunction(rgbBitmapType),
    rasterizeShapesFunction(grayscaleBitmapType),
    rgbColorFillShapeFunction,
    translateShapeFunction
)
