package silentorb.mythic.imaging.texturing.filters

import silentorb.imp.core.Parameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.Signature
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.imaging.texturing.*
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector3


private fun commonDistortionParameters() = listOf(
    Parameter("radius", zeroToOneHundredKey)
)

val blurSignatureRgb = Signature(
    parameters = commonDistortionParameters()
        .plus(Parameter("source", rgbSampler2dKey)),
    output = rgbSampler2dKey
)

val blurSignatureFloat = Signature(
    parameters = commonDistortionParameters()
        .plus(Parameter("source", floatSampler2dKey)),
    output = floatSampler2dKey
)

fun newBlurConvolutionMatrix(radius: Int): FloatArray {
  val radiusPlus = (radius + 1).toFloat()
  val halfConvolution = (0 until radius).map { it.toFloat() / radiusPlus }
  return halfConvolution
      .plus(1f)
      .plus(halfConvolution.reversed())
      .toFloatArray()
}

fun blurHorizontalRgb(dimensions: Vector2i, source: FloatArray, destination: FloatArray, convolution: FloatArray, radius: Int) {
  val weightSum = convolution.sum()
  for (y in 0 until dimensions.y) {
    for (x in 0 until dimensions.x) {
      var accumulatorR = 0f
      var accumulatorG = 0f
      var accumulatorB = 0f
      for (offset in convolution.indices) {
        val offsetX = (dimensions.x + x + offset - radius) % dimensions.x
        val offsetIndex = (offsetX + y * dimensions.x) * 3
        val weight = convolution[offset]
        accumulatorR += source[offsetIndex] * weight
        accumulatorG += source[offsetIndex + 1] * weight
        accumulatorB += source[offsetIndex + 2] * weight
      }
      val index = (x + y * dimensions.x) * 3
      destination[index] = accumulatorR / weightSum
      destination[index + 1] = accumulatorG / weightSum
      destination[index + 2] = accumulatorB / weightSum
    }
  }
}

fun blurVerticalRgb(dimensions: Vector2i, source: FloatArray, destination: FloatArray, convolution: FloatArray, radius: Int) {
  val weightSum = convolution.sum()
  for (x in 0 until dimensions.x) {
    for (y in 0 until dimensions.y) {
      var accumulatorR = 0f
      var accumulatorG = 0f
      var accumulatorB = 0f
      for (offset in convolution.indices) {
        val offsetY = (dimensions.y + y + offset - radius) % dimensions.y
        val offsetIndex = (x + offsetY * dimensions.x) * 3
        val weight = convolution[offset]
        accumulatorR += source[offsetIndex] * weight
        accumulatorG += source[offsetIndex + 1] * weight
        accumulatorB += source[offsetIndex + 2] * weight
      }
      val index = (x + y * dimensions.x) * 3
      destination[index] = accumulatorR / weightSum
      destination[index + 1] = accumulatorG / weightSum
      destination[index + 2] = accumulatorB / weightSum
    }
  }
}

fun blurFunctions() = listOf(
    CompleteFunction(
        path = PathKey(texturingPath, "gaussianBlur"),
        signature = blurSignatureRgb,
        implementation = { arguments ->
          val sampler = arguments["source"] as RgbSampler
          val dimensions = arguments["__globalDimensions"] as Vector2i
          val scalar = (arguments["radius"] as Int).toFloat() / 100f
          val radius = (dimensions.x * scalar * scalar).toInt()
          val size = dimensions.x * dimensions.y * 3
          val source = FloatArray(size)
          rgbSamplerToArray(sampler, source, dimensions)
          val destination = FloatArray(size)
          val convolution = newBlurConvolutionMatrix(radius)
          blurHorizontalRgb(dimensions, source, destination, convolution, radius)
          blurVerticalRgb(dimensions, destination, source, convolution, radius)
          ;
          { x: Float, y: Float ->
            val intX = (x * dimensions.x).toInt()
            val intY = (y * dimensions.y).toInt()
            val index = (intX + intY * dimensions.x) * 3
            Vector3(source[index], source[index + 1], source[index + 2])
          }
        }
    ),
    CompleteFunction(
        path = PathKey(texturingPath, "gaussianBlur"),
        signature = blurSignatureFloat,
        implementation = { arguments ->
          val sampler = arguments["source"] as RgbSampler
          val dimensions = arguments["dimensions"] as Vector2i
          val radius = dimensions.x * (arguments["radius"] as Int) / 100
          val size = dimensions.x * dimensions.y * 3
          val source = FloatArray(size)
          rgbSamplerToArray(sampler, source, dimensions)
          ;
          { x: Float, y: Float ->
            val intX = (x * dimensions.x).toInt()
            val intY = (y * dimensions.y).toInt()
            val index = (intX + intY * dimensions.x) * 3
            Vector3(source[index], source[index + 1], source[index + 2])
          }
        }
    )
)
