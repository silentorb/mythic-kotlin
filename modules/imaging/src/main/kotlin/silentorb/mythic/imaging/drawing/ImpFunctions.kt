package silentorb.mythic.imaging.drawing

import silentorb.imp.core.Parameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.Signature
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.FunctionImplementation
import silentorb.mythic.imaging.relativeDimensionsKey
import silentorb.mythic.imaging.solidColorBitmapKey
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector2i

const val drawingPath = "silentorb.mythic.generation.drawing"

val shapesKey = PathKey(drawingPath, "Shapes")

val newSquareImplementation: FunctionImplementation = { arguments ->
  val dimensions = arguments["dimensions"] as Vector2
    val id = 1
    newShapes()
        .copy(
            ids = listOf(id),
            functions = mapOf(id to ShapeFunction.rectangle),
            dimensions = mapOf(id to dimensions)
        )
}

val rasterizeShapesImplementation: FunctionImplementation = { arguments ->
  val dimensions = arguments["dimensions"] as Vector2i
    throw Error("Not implemented")
//  rasterizeShapes(dimensions)
}

val newRectangleFunction = CompleteFunction(
    path = PathKey(drawingPath, "newRectangle"),
    signature = Signature(
        parameters = listOf(
            Parameter("dimensions", relativeDimensionsKey)
        ),
        output = shapesKey
    ),
    implementation = newSquareImplementation
)

val rasterizeShapesFunction = CompleteFunction(
    path = PathKey(drawingPath, "rasterizeShapes"),
    signature = Signature(
        parameters = listOf(
            Parameter("dimensions", relativeDimensionsKey),
            Parameter("shapes", shapesKey)
        ),
        output = solidColorBitmapKey
    ),
    implementation = rasterizeShapesImplementation
)
