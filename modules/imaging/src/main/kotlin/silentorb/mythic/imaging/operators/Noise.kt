package silentorb.mythic.imaging.operators

import silentorb.imp.core.*
import silentorb.imp.execution.Arguments
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.FunctionImplementation
import silentorb.imp.execution.TypeAlias
import silentorb.mythic.imaging.*
import thirdparty.noise.OpenSimplexNoise
import java.nio.ByteBuffer

tailrec fun noiseIteration(octaves: List<Pair<Float, Float>>, x: Float, y: Float, algorithm: GetPixel<Float>, step: Int, output: Float): Float {
  return if (step >= octaves.size)
    output
  else {
    val (frequency, amplitude) = octaves[step]
    val nextOutput = output + (algorithm(x * frequency, y * frequency) * 0.5f + 0.5f) * amplitude
    noiseIteration(octaves, x, y, algorithm, step + 1, nextOutput)
  }
}

data class NoiseOctaves(
    val octaves: List<Pair<Float, Float>>,
    val amplitudeMax: Float
)

fun detailToOctaves(detail: Int): Int =
    when {
      detail == 0 -> 1
      detail < 15 -> 2
      detail < 30 -> 3
      detail < 40 -> 4
      detail < 60 -> 5
      detail < 85 -> 6
      detail < 99 -> 7
      else -> 8
    }

fun getNoiseOctaves(arguments: Arguments): NoiseOctaves {
  val scale = (arguments["scale"] as Int).toFloat() / 300f
  val detail = arguments["detail"] as Int
  val octaveCount = detailToOctaves(detail)
  val amplitudeMod = detail.toFloat() / 100f
  val (octaves) = (0 until octaveCount)
      .fold(Triple(listOf<Pair<Float, Float>>(), 1f, 1f)) { (accumulator, amplitude, frequency), b ->
        val octave = Pair(frequency / scale, amplitude)
        Triple(accumulator.plus(octave), amplitude * amplitudeMod, frequency * 2f)
      }
  val amplitudeMax = octaves.fold(0f) { a, b -> a + b.second }

  return NoiseOctaves(
      octaves = octaves,
      amplitudeMax = amplitudeMax
  )
}

fun noise(arguments: Arguments, algorithm: GetPixel<Float>): GetPixel<Float> {
  val (octaves, amplitudeMax) = getNoiseOctaves(arguments)
  return { x, y ->
    //    var rawValue = 0f
//    for ((frequency, amplitude) in octaves) {
//    rawValue += (algorithm(x * frequency, y * frequency) * 0.5f + 0.5f) * amplitude
//  }
    val rawValue = noiseIteration(octaves, x, y, algorithm, 0, 0f)
//    val rawValue = octaves.fold(0f) { a, (frequency, amplitude) ->
//      a + (algorithm(x * frequency, y * frequency) * 0.5f + 0.5f) * amplitude
//    }
    rawValue / amplitudeMax
//    minMax(rawValue, 0f, 1f)
  }
}

fun nonTilingOpenSimplex2D(seed: Long = 1L): GetPixel<Float> {
  val generator = OpenSimplexNoise(seed)
  return { x, y ->
//    0.5f
    generator.eval(x, y)
  }
}

//val noiseOctaveKey = PathKey(texturingPath, "NoiseOctave")
val noiseDetailKey = PathKey(texturingPath, "NoiseDetail")
val noiseScaleKey = PathKey(texturingPath, "NoiseScale")
val noiseVariationKey = PathKey(texturingPath, "NoiseVariation")

val coloredNoiseSignature = Signature(
    parameters = listOf(
        Parameter("dimensions", absoluteDimensionsKey),
        Parameter("scale", noiseScaleKey),
        Parameter("detail", noiseDetailKey),
        Parameter("firstColor", rgbColorKey),
        Parameter("secondColor", rgbColorKey)
    ),
    output = rgbBitmapKey
)

val noiseSignature = Signature(
    parameters = listOf(
        Parameter("scale", noiseScaleKey),
        Parameter("detail", noiseDetailKey),
        Parameter("variation", noiseVariationKey)
    ),
    output = floatSamplerKey
)

val noiseFunction = CompleteFunction(
    path = PathKey(texturingPath, "noise"),
    signature = noiseSignature,
    implementation = { arguments ->
      val variation = arguments ["variation"] as Int
      noise(arguments, nonTilingOpenSimplex2D(variation.toLong()))
//      val depth = 1
//      val buffer = ByteBuffer.allocate(dimensions.x * dimensions.y * depth * 4).asFloatBuffer()
//      for (y in 0 until dimensions.y) {
//        for (x in 0 until dimensions.x) {
//          val value = getNoise(x.toFloat() / dimensions.x, 1f - y.toFloat() / dimensions.y)
//          buffer.put(value)
//        }
//      }
//      buffer.rewind()
//      Bitmap(
//          dimensions = dimensions,
//          channels = depth,
//          buffer = buffer
//      )
    }
)

val seamlessColoredNoiseFunction = CompleteFunction(
    path = PathKey(texturingPath, "seamlessColoredNoise"),
    signature = coloredNoiseSignature,
    implementation = withBuffer("dimensions", withBitmapBuffer) { arguments ->
      val getNoise = noise(arguments, nonTilingOpenSimplex2D())
      val colorize = colorizeValue(arguments)
      ;
      { x, y ->
        if (x < 0.75f && y <= 0.75f) {
          colorize(getNoise(x, y))
        } else {
          val value = getNoise(x, y)

          val otherX = getNoise(x - 1f, y)
          val weightX = java.lang.Float.max(0f, (x - 0.75f) * 4f)

          val firstMix = mix(value, otherX, weightX)

          val value2 = getNoise(x - 1f, y - 1f)
          val otherY2 = mix(getNoise(x, y - 1f), value2, weightX)

          val weightY = java.lang.Float.max(0f, (y - 0.75f) * 4f)

          colorize(mix(firstMix, otherY2, weightY))
        }
      }
    }
)

fun noiseAliases() =
    listOf(
//        TypeAlias(
//            path = noiseOctaveKey,
//            alias = intKey,
//            numericConstraint = newNumericConstraint(1, 8)
//        ),
        TypeAlias(
            path = noiseScaleKey,
            alias = intKey,
            numericConstraint = newNumericConstraint(1, 100)
        ),
        TypeAlias(
            path = noiseDetailKey,
            alias = intKey,
            numericConstraint = newNumericConstraint(0, 100)
        ),
        TypeAlias(
            path = noiseVariationKey,
            alias = intKey,
            numericConstraint = newNumericConstraint(1, 1000)
        )
    )
