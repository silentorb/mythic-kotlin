package silentorb.mythic.imaging.texturing.filters

import silentorb.mythic.spatial.Vector3i
import silentorb.imp.core.*
import silentorb.imp.execution.Arguments
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.FunctionImplementation
import silentorb.mythic.ent.mappedCache
import silentorb.mythic.imaging.texturing.*
import silentorb.mythic.imaging.texturing.drawing.drawingFunctions
import silentorb.mythic.imaging.texturing.math.mathFunctions
import silentorb.mythic.randomly.Dice
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector3
import java.nio.FloatBuffer

typealias SolidColor = Vector3

fun mix(first: Vector3, second: Vector3, weight: Float): Vector3 =
    first * (1f - weight) + second * weight

fun mix(first: Float, second: Float, weight: Float): Float =
    first * (1f - weight) + second * weight

data class BufferInfo<T>(
    val depth: Int,
    val setter: (FloatBuffer, T) -> Unit
)

fun convertColor(value: Vector3): Vector3i =
    Vector3i(
        (value.x * 255).toInt(),
        (value.y * 255).toInt(),
        (value.z * 255).toInt()
    )

val solidColor: FunctionImplementation = withBuffer("dimensions", withBitmapBuffer) { arguments ->
  val color = arguments["color"]!! as SolidColor
  { _, _ -> color }
}

val coloredCheckers: FunctionImplementation = withBuffer("dimensions", withBitmapBuffer) { arguments ->
  val first = arguments["firstColor"]!! as SolidColor
  val second = arguments["secondColor"]!! as SolidColor
  coloredCheckerPattern(first, second)
}

fun colorizeValue(arguments: Arguments): (Float) -> Vector3 {
  val first = rgbIntToFloat(arguments["firstColor"]!! as RgbColor)
  val second = rgbIntToFloat(arguments["secondColor"]!! as RgbColor)
  return { unit ->
    first * (1f - unit) + second * unit
  }
}

fun floatBufferArgument(arguments: Arguments, name: String): FloatBuffer {
  val result = arguments[name]!! as Bitmap
  result.buffer.rewind()
  return result.buffer
}

val mixBitmaps: FunctionImplementation = withBuffer("first", withBitmapBuffer) { arguments ->
  val degree = arguments["degree"]!! as Float
  val first = floatBufferArgument(arguments, "first")
  val second = floatBufferArgument(arguments, "second")
  ;
  { _, _ ->
    first.getVector3() * (1f - degree) + second.getVector3() * degree
  }
}

fun clip(threshold: Float, value: Float): Float =
    if (value >= threshold)
      1f
    else
      0f

fun flipClip(threshold: Float, value: Float): Float =
    if (value >= threshold)
      0f
    else
      1f

val voronoiBoundaryOperator: FunctionImplementation = withBuffer("dimensions", withGrayscaleBuffer) { arguments ->
  val dice = Dice(1)
  val length = 10
  val grid = newAnchorGrid(dice, length, 10)
  val nearestCells = mappedCache(getNearestCells(grid, 2))
  voronoi(length, nearestCells, voronoiBoundaries(0.05f * grid.length.toFloat()))
}

// Function Keys
val coloredCheckersKey = PathKey(texturingPath, "coloredCheckers")
val colorizeKey = PathKey(texturingPath, "colorize")
val mixBitmapsKey = PathKey(texturingPath, "mixBitmaps")
val mixGrayscalesKey = PathKey(texturingPath, "mixGrayscales")
val voronoiBoundariesKey = PathKey(texturingPath, "voronoiBoundaries")

fun completeTexturingFunctions() = listOf(
    CompleteFunction(
        path = rgbColorKey,
        signature = Signature(
            parameters = listOf(
                Parameter("red", intType),
                Parameter("green", intType),
                Parameter("blue", intType)
            ),
            output = rgbColorType
        ),
        implementation = { arguments ->
            Vector3i(arguments["red"] as Int, arguments["green"] as Int, arguments["blue"] as Int)
        }
    ),
    CompleteFunction(
        path = absoluteDimensionsKey,
        signature = Signature(
            parameters = listOf(
                Parameter("width", intType),
                Parameter("height", intType)
            ),
            output = absoluteDimensionsType
        ),
        implementation = { arguments ->
          Vector2i(arguments["width"] as Int, arguments["height"] as Int)
        }
    ),
    CompleteFunction(
        path = coloredCheckersKey,
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", absoluteDimensionsType),
                Parameter("firstColor", rgbColorType),
                Parameter("secondColor", rgbColorType)
            ),
            output = rgbBitmapType
        ),
        implementation = coloredCheckers
    ),
    CompleteFunction(
        path = PathKey(texturingPath, "Bitmap"),
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", absoluteDimensionsType),
                Parameter("color", rgbColorType)
            ),
            output = rgbBitmapType
        ),
        implementation = withBuffer("dimensions", withBitmapBuffer) { arguments ->
          val color = arguments["color"]!! as SolidColor
          { _, _ -> color }
        }
    ),
    CompleteFunction(
        path = PathKey(texturingPath, "Bitmap"),
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", absoluteDimensionsType),
                Parameter("value", floatType)
            ),
            output = grayscaleBitmapType
        ),
        implementation = withBuffer("dimensions", withGrayscaleBuffer) { arguments ->
          val value = arguments["value"]!! as Float
          { _, _ -> value }
        }
    ),
    CompleteFunction(
        path = colorizeKey,
        signature = Signature(
            parameters = listOf(
                Parameter("sampler", floatSampler2dType),
                Parameter("firstColor", rgbColorType),
                Parameter("secondColor", rgbColorType)
            ),
            output = rgbSampler2dType
        ),
        implementation = { arguments ->
          val sampler = arguments["sampler"]!! as FloatSampler2d
          val colorize = colorizeValue(arguments)
          ;
          { x: Float, y: Float ->
            colorize(sampler(x, y))
          }
        }
    ),
    CompleteFunction(
        path = PathKey(texturingPath, "mask"),
        signature = Signature(
            parameters = listOf(
                Parameter("first", rgbSampler2dType),
                Parameter("second", rgbSampler2dType),
                Parameter("mask", floatSampler2dType)
            ),
            output = rgbSampler2dType
        ),
        implementation = { arguments ->
          val first = arguments["first"]!! as RgbSampler
          val second = arguments["second"]!! as RgbSampler
          val mask = arguments["mask"]!! as FloatSampler2d
          ;
          { x: Float, y: Float ->
            val degree = mask(x, y)
            first(x, y) * (1f - degree) + second(x, y) * degree
          }
        }
    ),
    CompleteFunction(
        path = mixBitmapsKey,
        signature = Signature(
            parameters = listOf(
                Parameter("degree", floatType),
                Parameter("first", rgbBitmapType),
                Parameter("second", grayscaleBitmapType)
            ),
            output = rgbBitmapType
        ),
        implementation = mixBitmaps
    ),
    seamlessColoredNoiseFunction,
    noiseFunction,
    relativeDimensionsFunction,
    CompleteFunction(
        path = voronoiBoundariesKey,
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", absoluteDimensionsType)
            ),
            output = grayscaleBitmapType
        ),
        implementation = voronoiBoundaryOperator
    )
)
    .plus(drawingFunctions())
    .plus(mathFunctions())
    .plus(checkersFunctions())
    .plus(distortionFunctions())
    .plus(blurFunctions())

fun completeTexturingAliases() =
    noiseAliases()
        .plus(checkersAliases())
