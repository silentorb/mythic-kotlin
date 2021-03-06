package silentorb.mythic.imaging.texturing.filters

import silentorb.imp.core.*
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.TypeAlias
import silentorb.mythic.imaging.common.GetSample2d
import silentorb.mythic.imaging.texturing.floatSampler2dType
import silentorb.mythic.imaging.texturing.texturingPath

val CheckersIterationsKey = PathKey(texturingPath, "CheckersIterations")
val CheckersIterationsType = CheckersIterationsKey.hashCode()

fun checkerPattern(iterationsX: Int, iterationsY: Int): GetSample2d<Float> {
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
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("iterationsX", oneToOneHundredType),
                CompleteParameter("iterationsY", oneToOneHundredType)
            ),
            output = floatSampler2dType
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
        path = CheckersIterationsType,
        alias = intType.hash,
        numericConstraint = newNumericConstraint(1, 64)
    )
)
