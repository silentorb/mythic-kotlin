package silentorb.mythic.imaging.operators

import silentorb.imp.core.*
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.TypeAlias
import silentorb.mythic.imaging.absoluteDimensionsKey
import silentorb.mythic.imaging.grayscaleBitmapKey
import silentorb.mythic.imaging.texturingPath
import silentorb.mythic.spatial.Vector2i

val CheckersIterations = PathKey(texturingPath, "CheckersIterations")

fun checkerPattern(iterationsX: Int, iterationsY: Int, dimensions: Vector2i): GetPixel<Float> {
  val scaleX = iterationsX.toFloat() * 2f
  val scaleY = iterationsY.toFloat() * 2f
  return { x: Float, y: Float ->
    val x2 = (scaleX * x).toInt()
    val y2 = (scaleY * y).toInt()
    ((x2 + y2) % 2).toFloat()
  }
}

fun checkersFunctions() = listOf(
    CompleteFunction(
        path = PathKey(texturingPath, "checkers"),
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", absoluteDimensionsKey),
                Parameter("iterationsX", intKey),
                Parameter("iterationsY", intKey)
            ),
            output = grayscaleBitmapKey
        ),
        implementation = withBuffer("dimensions", withGrayscaleBuffer) { arguments ->
          val iterationsX = arguments["iterationsX"] as Int
          val iterationsY = arguments["iterationsY"] as Int
          val dimensions = arguments["dimensions"] as Vector2i
          checkerPattern(iterationsX, iterationsY, dimensions)
        }
    )
)

fun checkersAliases() = listOf(
    TypeAlias(
        path = CheckersIterations,
        alias = intKey,
        numericConstraint = newNumericConstraint(1, 64)
    )
)
