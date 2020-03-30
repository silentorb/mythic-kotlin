package silentorb.mythic.imaging.texturing.drawing

import silentorb.imp.core.Parameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.Signature
import silentorb.imp.core.floatKey
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.imaging.texturing.*
import silentorb.mythic.imaging.texturing.math.vector2Key
import silentorb.mythic.spatial.Matrix3
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3

const val drawingPath = "silentorb.mythic.generation.drawing"

val shapesKey = PathKey(drawingPath, "Shapes")

val newRectangleFunction = CompleteFunction(
    path = PathKey(drawingPath, "rectangle"),
    signature = Signature(
        parameters = listOf(
            Parameter("dimensions", relativeDimensionsKey)
        ),
        output = shapesKey
    ),
    implementation = { arguments ->
      val dimensions = arguments["dimensions"] as Vector2
      newRectangle(dimensions)
    }
)

val rgbColorFillShapeFunction = CompleteFunction(
    path = PathKey(drawingPath, "colorFill"),
    signature = Signature(
        parameters = listOf(
            Parameter("color", rgbColorKey),
            Parameter("shapes", shapesKey)
        ),
        output = shapesKey
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
    signature = Signature(
        parameters = listOf(
            Parameter("value", floatKey),
            Parameter("shapes", shapesKey)
        ),
        output = shapesKey
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
    signature = Signature(
        parameters = listOf(
            Parameter("value", floatKey),
            Parameter("width", floatKey),
            Parameter("shapes", shapesKey)
        ),
        output = shapesKey
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
    signature = Signature(
        parameters = listOf(
            Parameter("color", rgbColorKey),
            Parameter("width", floatKey),
            Parameter("shapes", shapesKey)
        ),
        output = shapesKey
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
    signature = Signature(
        parameters = listOf(
            Parameter("offset", vector2Key),
            Parameter("shapes", shapesKey)
        ),
        output = shapesKey
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

fun rasterizeShapesFunction(bitmapType: PathKey) = CompleteFunction(
    path = PathKey(drawingPath, "rasterizeShapes"),
    signature = Signature(
        parameters = listOf(
            Parameter("bitmap", bitmapType),
            Parameter("shapes", shapesKey)
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
    rasterizeShapesFunction(rgbBitmapKey),
    rasterizeShapesFunction(grayscaleBitmapKey),
    rgbColorFillShapeFunction,
    translateShapeFunction
)
