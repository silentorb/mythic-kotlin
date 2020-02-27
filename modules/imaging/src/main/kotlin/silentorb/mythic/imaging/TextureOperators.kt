package silentorb.mythic.imaging

import org.joml.Vector3i
import org.lwjgl.BufferUtils
import silentorb.imp.core.*
import silentorb.imp.execution.Arguments
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.FunctionImplementation
import silentorb.imp.execution.partitionFunctions
import silentorb.mythic.ent.mappedCache
import silentorb.mythic.randomly.Dice
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.minMax
import thirdparty.noise.OpenSimplexNoise
import java.nio.ByteBuffer
import java.nio.FloatBuffer

typealias SolidColor = Vector3

typealias TextureFunction = (Vector2i) -> FunctionImplementation

fun allocateFloatTextureBuffer(length: Int): FloatBuffer =
    BufferUtils.createFloatBuffer(length * length * 3)

fun allocateByteTextureBuffer(length: Int): ByteBuffer =
    BufferUtils.createByteBuffer(length * length * 3)

fun allocateFloatBuffer(size: Int): FloatBuffer =
    BufferUtils.createFloatBuffer(size)

//val bufferCache = { id:size: Int ->
//  mappedCache<Id, FloatBuffer> { id2 -> singleValueCache(::allocateFloatBuffer)(size) }(id)
//}

fun fillBuffer(depth: Int, dimensions: Vector2i, action: (FloatBuffer) -> Unit): Bitmap {
//  val buffer = BufferUtils.createFloatBuffer(dimensions.x * dimensions.y * depth)
//  val buffer = bufferCache(id, dimensions.x * dimensions.y * depth)
  val buffer = allocateFloatBuffer(dimensions.x * dimensions.y * depth)
  action(buffer)
  buffer.rewind()
  return Bitmap(
      dimensions = dimensions,
      channels = depth,
      buffer = buffer
  )
}

data class BufferInfo<T>(
    val depth: Int,
    val setter: (FloatBuffer, T) -> Unit
)

data class Bitmap(
    val buffer: FloatBuffer,
    val channels: Int,
    val dimensions: Vector2i
)

typealias GetPixel<T> = (Float, Float) -> T

fun <T> withBuffer(dimensionsField: String, bufferInfo: BufferInfo<T>, function: (Arguments) -> GetPixel<T>): FunctionImplementation =
    { arguments ->
      val dimensionsArgument = arguments[dimensionsField]!!
      val dimensions: Vector2i = if (dimensionsArgument is Vector2i)
        dimensionsArgument
      else if (dimensionsArgument is Bitmap)
        dimensionsArgument.dimensions
      else throw Error("Invalid dimensions argument $dimensionsArgument")

      fillBuffer(bufferInfo.depth, dimensions) { buffer ->
        val getter = function(arguments)
        for (y in 0 until dimensions.y) {
          for (x in 0 until dimensions.x) {
            val value = getter(x.toFloat() / dimensions.x, 1f - y.toFloat() / dimensions.y)
            bufferInfo.setter(buffer, value)
          }
        }
      }
    }

val withBitmapBuffer = BufferInfo<Vector3>(3) { buffer, value ->
  buffer.put(value)
}

val withGrayscaleBuffer = BufferInfo<Float>(1) { buffer, value ->
  buffer.put(value)
}

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
  val grayscale = arguments["grayscale"]!! as FloatBuffer
  grayscale.rewind()
  val first = arguments["firstColor"]!! as SolidColor
  val second = arguments["secondColor"]!! as SolidColor
  val colorize = colorizeValue(arguments)
  ;
  { _, _ ->
    colorize(grayscale.get())
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

fun noise(arguments: Arguments): GetPixel<Float> {
  val scale = (arguments["scale"] as Float? ?: 10f)
  val roughness = (arguments["roughness"] as Float? ?: 0.8f)
  val octaveCount = arguments["octaves"]!! as Int? ?: 1
  val generator = OpenSimplexNoise(1)
  val amplitudeMod = roughness
  val (octaves) = (0 until octaveCount)
      .fold(Triple(listOf<Pair<Float, Float>>(), 1f, 1f)) { (accumulator, amplitude, frequency), b ->
        val octave = Pair(frequency / scale, amplitude)
        Triple(accumulator.plus(octave), amplitude * amplitudeMod, frequency * 2f)
      }
  val amplitudeMax = octaves.fold(0f) { a, b -> a + b.second }
  return { x, y ->
    val rawValue = octaves.fold(0f) { a, (frequency, amplitude) ->
      a + (generator.eval((x * frequency).toDouble(), (y * frequency).toDouble()).toFloat() * 0.5f + 0.5f) * amplitude
    }
    rawValue / amplitudeMax
//    minMax(rawValue, 0f, 1f)
  }
}

val simpleNoiseOperator: FunctionImplementation = withBuffer("dimensions", withGrayscaleBuffer) { arguments ->
  val getNoise = noise(arguments)
  ;
  { x, y ->
    getNoise(x, y)
  }
}

val colorizedNoiseOperator: FunctionImplementation = withBuffer("dimensions", withBitmapBuffer) { arguments ->
  val getNoise = noise(arguments)
  val colorize = colorizeValue(arguments)
  ;
  { x, y ->
    colorize(getNoise(x, y))
  }
}

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

val newDimensions: FunctionImplementation = { arguments ->
  Vector2i(arguments["width"] as Int, arguments["height"] as Int)
}

const val texturingPath = "silentorb.mythic.generation.texturing"

// Type Keys
val solidColorKey = PathKey(texturingPath, "SolidColor")
val transparentColorKey = PathKey(texturingPath, "TransparentColor")

val solidColorBitmapKey = PathKey(texturingPath, "SolidColorBitmap")
val transparentColorBitmapKey = PathKey(texturingPath, "TransparentColorBitmap")
val grayscaleBitmapKey = PathKey(texturingPath, "GrayscaleBitmap")
val dimensionsKey = PathKey(texturingPath, "Dimensions")

// Function Keys
val coloredCheckersKey = PathKey(texturingPath, "coloredCheckers")
val fromSolidColorKey = PathKey(texturingPath, "solidColorToBitmap")
val colorizeKey = PathKey(texturingPath, "colorize")
val checkersKey = PathKey(texturingPath, "checkers")
val maskKey = PathKey(texturingPath, "mask")
val mixBitmapsKey = PathKey(texturingPath, "mixBitmaps")
val mixGrayscalesKey = PathKey(texturingPath, "mixGrayscales")
val perlinNoiseGrayscaleKey = PathKey(texturingPath, "perlinNoiseGrayscale")
val perlinNoiseColorKey = PathKey(texturingPath, "perlinNoiseColor")
val voronoiBoundariesKey = PathKey(texturingPath, "voronoiBoundaries")

fun completeTexturingFunctions() = listOf(
    CompleteFunction(
        path = solidColorKey,
        signature = Signature(
            parameters = listOf(
                Parameter("red", floatKey),
                Parameter("green", floatKey),
                Parameter("blue", floatKey)
            ),
            output = solidColorKey
        ),
        implementation = newSolidColor
    ),
    CompleteFunction(
        path = dimensionsKey,
        signature = Signature(
            parameters = listOf(
                Parameter("width", intKey),
                Parameter("height", intKey)
            ),
            output = dimensionsKey
        ),
        implementation = newDimensions
    ),
    CompleteFunction(
        path = coloredCheckersKey,
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", dimensionsKey),
                Parameter("firstColor", solidColorKey),
                Parameter("secondColor", solidColorKey)
            ),
            output = solidColorBitmapKey
        ),
        implementation = coloredCheckers
    ),
    CompleteFunction(
        path = fromSolidColorKey,
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", dimensionsKey),
                Parameter("color", solidColorKey)
            ),
            output = solidColorBitmapKey
        ),
        implementation = solidColor
    ),
    CompleteFunction(
        path = colorizeKey,
        signature = Signature(
            parameters = listOf(
                Parameter("grayscale", grayscaleBitmapKey),
                Parameter("firstColor", solidColorKey),
                Parameter("secondColor", solidColorKey)
            ),
            output = solidColorBitmapKey
        ),
        implementation = colorizeOperator
    ),
    CompleteFunction(
        path = checkersKey,
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", dimensionsKey)
            ),
            output = solidColorBitmapKey
        ),
        implementation = grayscaleCheckers
    ),
    CompleteFunction(
        path = maskKey,
        signature = Signature(
            parameters = listOf(
                Parameter("first", solidColorBitmapKey),
                Parameter("second", solidColorBitmapKey),
                Parameter("mask", grayscaleBitmapKey)
            ),
            output = solidColorBitmapKey
        ),
        implementation = maskOperator
    ),
    CompleteFunction(
        path = mixBitmapsKey,
        signature = Signature(
            parameters = listOf(
                Parameter("degree", floatKey),
                Parameter("first", solidColorBitmapKey),
                Parameter("second", grayscaleBitmapKey)
            ),
            output = solidColorBitmapKey
        ),
        implementation = mixBitmaps
    ),
    CompleteFunction(
        path = perlinNoiseGrayscaleKey,
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", dimensionsKey),
                Parameter("scale", floatKey),
                Parameter("octaves", intKey)
            ),
            output = grayscaleBitmapKey
        ),
        implementation = simpleNoiseOperator
    ),
    CompleteFunction(
        path = perlinNoiseColorKey,
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", dimensionsKey),
                Parameter("scale", floatKey),
                Parameter("octaves", intKey),
                Parameter("roughness", floatKey),
                Parameter("firstColor", solidColorKey),
                Parameter("secondColor", solidColorKey)
            ),
            output = solidColorBitmapKey
        ),
        implementation = colorizedNoiseOperator
    ),
    CompleteFunction(
        path = voronoiBoundariesKey,
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", dimensionsKey)
            ),
            output = grayscaleBitmapKey
        ),
        implementation = voronoiBoundaryOperator
    )
)

fun texturingLibrary() =
    partitionFunctions(completeTexturingFunctions())
