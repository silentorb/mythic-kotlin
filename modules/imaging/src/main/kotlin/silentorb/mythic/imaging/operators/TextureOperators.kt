package silentorb.mythic.imaging.operators

import silentorb.mythic.spatial.Vector3i
import silentorb.imp.core.*
import silentorb.imp.execution.Arguments
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.FunctionImplementation
import silentorb.mythic.ent.mappedCache
import silentorb.mythic.imaging.*
import silentorb.mythic.imaging.drawing.drawingFunctions
import silentorb.mythic.imaging.math.mathFunctions
import silentorb.mythic.randomly.Dice
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector3
import java.nio.FloatBuffer

typealias SolidColor = Vector3

typealias TextureFunction = (Vector2i) -> FunctionImplementation

//val bufferCache = { id:size: Int ->
//  mappedCache<Id, FloatBuffer> { id2 -> singleValueCache(::allocateFloatBuffer)(size) }(id)
//}

fun mix(first: Vector3, second: Vector3, weight: Float): Vector3 =
    first * (1f - weight) + second * weight

fun mix(first: Float, second: Float, weight: Float): Float =
    first * (1f - weight) + second * weight

data class BufferInfo<T>(
    val depth: Int,
    val setter: (FloatBuffer, T) -> Unit
)

typealias GetPixel<T> = (Float, Float) -> T

//val withGrayscaleBuffer = fillBuffer<Float>(1) { buffer, value ->
//  buffer.put(value)
//}

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
  checkerPattern(first, second)
}

val grayscaleCheckers: FunctionImplementation = withBuffer("dimensions", withGrayscaleBuffer) { _ ->
  checkerOp(0f, 1f)
}

fun colorizeValue(arguments: Arguments): (Float) -> Vector3 {
  val first = arguments["firstColor"]!! as SolidColor
  val second = arguments["secondColor"]!! as SolidColor
  return { unit ->
    first * (1f - unit) + second * unit
  }
}

val colorizeOperator: FunctionImplementation = withBuffer("grayscale", withBitmapBuffer) { arguments ->
  val grayscale = arguments["grayscale"]!! as Bitmap
  grayscale.buffer.rewind()
  val first = arguments["firstColor"]!! as SolidColor
  val second = arguments["secondColor"]!! as SolidColor
  val colorize = colorizeValue(arguments)
  ;
  { _, _ ->
    colorize(grayscale.buffer.get())
  }
}

fun floatBufferArgument(arguments: Arguments, name: String): FloatBuffer {
  val result = arguments[name]!! as Bitmap
  result.buffer.rewind()
  return result.buffer
}

val maskOperator: FunctionImplementation = withBuffer("first", withBitmapBuffer) { arguments ->
  val first = floatBufferArgument(arguments, "first")
  val second = floatBufferArgument(arguments, "second")
  val mask = floatBufferArgument(arguments, "mask")
  ;
  { _, _ ->
    val degree = mask.get()
    first.getVector3() * (1f - degree) + second.getVector3() * degree
  }
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

//val mixGrayscales: FunctionImplementation = withBuffer(withGrayscaleBuffer) { arguments ->
//  val weights = arguments["weights"]!! as List<Float>
//  val buffers = weights.mapIndexed { index, value ->
//    Pair(floatBufferArgument(arguments, (index + 1).toString()), value)
//  }
////  val first = floatBufferArgument(arguments, "first")
////  val second = floatBufferArgument(arguments, "second")
//  val k = 0
//  { x, y ->
//    buffers.fold(0f) { a, b -> a + b.first.get() * b.second }
////    first.get() * (1f - degrees) + second.get() * degrees
//  }
//}

//val noiseSource = OpenSimplexNoiseKotlin(1)

//fun simpleNoise(scale: Float): ScalarTextureAlgorithm =
//    { x, y ->
//      noiseSource.eval(x * scale, y * scale)
//    }

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

val newSolidColor: FunctionImplementation = { arguments ->
  Vector3(arguments["red"] as Float, arguments["green"] as Float, arguments["blue"] as Float)
}

// Function Keys
val coloredCheckersKey = PathKey(texturingPath, "coloredCheckers")
val colorizeKey = PathKey(texturingPath, "colorize")
val checkersKey = PathKey(texturingPath, "checkers")
val mixBitmapsKey = PathKey(texturingPath, "mixBitmaps")
val mixGrayscalesKey = PathKey(texturingPath, "mixGrayscales")
val voronoiBoundariesKey = PathKey(texturingPath, "voronoiBoundaries")

fun completeTexturingFunctions() = listOf(
    CompleteFunction(
        path = rgbColorKey,
        signature = Signature(
            parameters = listOf(
                Parameter("red", floatKey),
                Parameter("green", floatKey),
                Parameter("blue", floatKey)
            ),
            output = rgbColorKey
        ),
        implementation = newSolidColor
    ),
    CompleteFunction(
        path = absoluteDimensionsKey,
        signature = Signature(
            parameters = listOf(
                Parameter("width", intKey),
                Parameter("height", intKey)
            ),
            output = absoluteDimensionsKey
        ),
        implementation = { arguments ->
          Vector2i(arguments["width"] as Int, arguments["height"] as Int)
        }
    ),
    CompleteFunction(
        path = coloredCheckersKey,
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", absoluteDimensionsKey),
                Parameter("firstColor", rgbColorKey),
                Parameter("secondColor", rgbColorKey)
            ),
            output = rgbBitmapKey
        ),
        implementation = coloredCheckers
    ),
    CompleteFunction(
        path = PathKey(texturingPath, "Bitmap"),
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", absoluteDimensionsKey),
                Parameter("color", rgbColorKey)
            ),
            output = rgbBitmapKey
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
                Parameter("dimensions", absoluteDimensionsKey),
                Parameter("value", floatKey)
            ),
            output = grayscaleBitmapKey
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
                Parameter("grayscale", grayscaleBitmapKey),
                Parameter("firstColor", rgbColorKey),
                Parameter("secondColor", rgbColorKey)
            ),
            output = rgbBitmapKey
        ),
        implementation = colorizeOperator
    ),
    CompleteFunction(
        path = checkersKey,
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", absoluteDimensionsKey)
            ),
            output = rgbBitmapKey
        ),
        implementation = grayscaleCheckers
    ),
    CompleteFunction(
        path = PathKey(texturingPath, "mask"),
        signature = Signature(
            parameters = listOf(
                Parameter("first", rgbBitmapKey),
                Parameter("second", rgbBitmapKey),
                Parameter("mask", grayscaleBitmapKey)
            ),
            output = rgbBitmapKey
        ),
        implementation = withBuffer("first", withBitmapBuffer) { arguments ->
          val first = floatBufferArgument(arguments, "first")
          val second = floatBufferArgument(arguments, "second")
          val mask = floatBufferArgument(arguments, "mask")
          ;
          { _, _ ->
            val degree = mask.get()
            first.getVector3() * (1f - degree) + second.getVector3() * degree
          }
        }
    ),
    CompleteFunction(
        path = mixBitmapsKey,
        signature = Signature(
            parameters = listOf(
                Parameter("degree", floatKey),
                Parameter("first", rgbBitmapKey),
                Parameter("second", grayscaleBitmapKey)
            ),
            output = rgbBitmapKey
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
                Parameter("dimensions", absoluteDimensionsKey)
            ),
            output = grayscaleBitmapKey
        ),
        implementation = voronoiBoundaryOperator
    )
)
    .plus(drawingFunctions())
    .plus(mathFunctions())

fun completeTexturingAliases() =
    noiseAliases()
