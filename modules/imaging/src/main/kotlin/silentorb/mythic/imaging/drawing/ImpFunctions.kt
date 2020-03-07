package silentorb.mythic.imaging.drawing

import silentorb.imp.core.Parameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.Signature
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.FunctionImplementation
import silentorb.mythic.imaging.*
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector2i
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
      val id = 1
      newShapes()
          .copy(
              ids = listOf(id),
              functions = mapOf(id to ShapeFunction.rectangle),
              dimensions = mapOf(id to dimensions)
          )
    }
)

val rgbColorFillShapeFunction = CompleteFunction(
    path = PathKey(drawingPath, "colorFill"),
    signature = Signature(
        parameters = listOf(
            Parameter("color", solidColorKey),
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

val rasterizeShapesFunction = CompleteFunction(
    path = PathKey(drawingPath, "rasterizeShapes"),
    signature = Signature(
        parameters = listOf(
            Parameter("dimensions", absoluteDimensionsKey),
            Parameter("shapes", shapesKey)
        ),
        output = solidColorBitmapKey
    ),
    implementation = { arguments ->
      val dimensions = arguments["dimensions"] as Vector2i
      val shapes = arguments["shapes"] as Shapes
      rasterizeShapes(dimensions, shapes, 3)
    }
)

fun drawingFunctions() = listOf(
    newRectangleFunction,
    rasterizeShapesFunction,
    rgbColorFillShapeFunction
)
