package silentorb.mythic.imaging.operators

import silentorb.imp.core.*
import silentorb.imp.execution.Arguments
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.FunctionImplementation
import silentorb.imp.execution.TypeAlias
import silentorb.mythic.imaging.*
import silentorb.mythic.spatial.Vector3
import thirdparty.noise.OpenSimplexNoise

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

fun getNoiseOctaves(arguments: Arguments): NoiseOctaves {
  val scale = (arguments["scale"] as Float? ?: 10f)
  val roughness = (arguments["roughness"] as Float? ?: 0.8f)
  val octaveCount = arguments["octaves"]!! as Int? ?: 1
  val amplitudeMod = roughness
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

fun nonTilingOpenSimplex2D(): GetPixel<Float> {
  val generator = OpenSimplexNoise(1)
  return { x, y ->
//    0.5f
    generator.eval(x, y)
  }
}

val simpleNoiseOperator: FunctionImplementation = withBuffer("dimensions", withGrayscaleBuffer) { arguments ->
  val getNoise = noise(arguments, nonTilingOpenSimplex2D())
  ;
  { x, y ->
    getNoise(x, y)
  }
}

val noiseOctaveKey = PathKey(texturingPath, "NoiseOctave")

val coloredNoiseSignature = Signature(
    parameters = listOf(
        Parameter("dimensions", absoluteDimensionsKey),
        Parameter("scale", floatKey),
        Parameter("octaves", noiseOctaveKey),
        Parameter("roughness", floatKey),
        Parameter("firstColor", rgbColorKey),
        Parameter("secondColor", rgbColorKey)
    ),
    output = rgbBitmapKey
)

val coloredNoiseFunctionNested = CompleteFunction(
    path = PathKey(texturingPath, "coloredNoise"),
    signature = coloredNoiseSignature,
    implementation = withBuffer("dimensions", withBitmapBuffer) { arguments ->
      val getNoise = noise(arguments, nonTilingOpenSimplex2D())
      val colorize = colorizeValue(arguments)
      ;
      { x, y ->
        colorize(getNoise(x, y))
      }
    }
)

val coloredNoiseFunction = CompleteFunction(
    path = PathKey(texturingPath, "coloredNoise"),
    signature = coloredNoiseSignature,
    implementation = { arguments ->
      val testValue = NoiseNative.test()
//      val getNoise = noise(arguments, nonTilingOpenSimplex2D())
      val getNoise = nonTilingOpenSimplex2D()
      val colorize = colorizeValue(arguments)
      val dimensions = dimensionsFromArguments(arguments, "dimensions")
      val depth = 3
      val (octaves, amplitudeMax) = getNoiseOctaves(arguments)
      val buffer = allocateFloatBuffer(dimensions.x * dimensions.y * depth)
      for (octave in octaves) {
        for (y in 0 until dimensions.y) {
          for (x in 0 until dimensions.x) {
            val value = getNoise(x.toFloat() / dimensions.x, 1f - y.toFloat() / dimensions.y)
            buffer.put(value)
          }
        }
        buffer.rewind()
      }
      Bitmap(
          dimensions = dimensions,
          channels = depth,
          buffer = buffer
      )
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
        TypeAlias(
            path = noiseOctaveKey,
            alias = intKey,
            numericConstraint = newNumericConstraint(0, 20)
        )
    )
