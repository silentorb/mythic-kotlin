package silentorb.mythic.imaging.filters

import silentorb.imp.core.*
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.TypeAlias
import silentorb.mythic.imaging.floatSamplerKey
import silentorb.mythic.imaging.texturingPath

val CheckersIterations = PathKey(texturingPath, "CheckersIterations")

fun checkerPattern(iterationsX: Int, iterationsY: Int): GetPixel<Float> {
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
                Parameter("iterationsX", intKey),
                Parameter("iterationsY", intKey)
            ),
            output = floatSamplerKey
        ),
        implementation = { arguments ->
          val iterationsX = arguments["iterationsX"] as Int
          val iterationsY = arguments["iterationsY"] as Int
          checkerPattern(iterationsX, iterationsY)
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
