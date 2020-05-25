package silentorb.mythic.imaging.texturing.filters

import silentorb.imp.core.*
import silentorb.imp.execution.Arguments
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.TypeAlias
import silentorb.mythic.imaging.common.GetSample2d
import silentorb.mythic.imaging.common.GetSample3d
import silentorb.mythic.imaging.texturing.*
import silentorb.mythic.spatial.Vector3
import thirdparty.noise.OpenSimplexNoise

tailrec fun noiseIteration(octaves: List<Octave>, x: Float, y: Float, algorithm: GetSample2d<Float>, step: Int, output: Float): Float {
  return if (step >= octaves.size)
    output
  else {
    val (frequency, amplitude) = octaves[step]
    val nextOutput = output + (algorithm(x * frequency, y * frequency) * 0.5f + 0.5f) * amplitude
    noiseIteration(octaves, x, y, algorithm, step + 1, nextOutput)
  }
}

tailrec fun noiseIteration(octaves: List<Octave>, location: Vector3, algorithm: GetSample3d<Float>, step: Int, output: Float): Float {
  return if (step >= octaves.size)
    output
  else {
    val (frequency, amplitude) = octaves[step]
    val nextOutput = output + (algorithm(location * frequency) * 0.5f + 0.5f) * amplitude
    noiseIteration(octaves, location, algorithm, step + 1, nextOutput)
  }
}

data class Octave(
    val frequency: Float,
    val amplitude: Float
)

data class NoiseOctaves(
    val octaves: List<Octave>,
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
      .fold(Triple(listOf<Octave>(), 1f, 1f)) { (accumulator, amplitude, frequency), b ->
        val octave = Octave(frequency / scale, amplitude)
        Triple(accumulator.plus(octave), amplitude * amplitudeMod, frequency * 2f)
      }
  val amplitudeMax = octaves.fold(0f) { a, b -> a + b.amplitude }

  return NoiseOctaves(
      octaves = octaves,
      amplitudeMax = amplitudeMax
  )
}

fun noise2d(arguments: Arguments, algorithm: GetSample2d<Float>): GetSample2d<Float> {
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

fun noise3d(arguments: Arguments, algorithm: FloatSampler3d): FloatSampler3d {
  val (octaves, amplitudeMax) = getNoiseOctaves(arguments)
  return { location: Vector3 ->
    //    var rawValue = 0f
//    for ((frequency, amplitude) in octaves) {
//    rawValue += (algorithm(x * frequency, y * frequency) * 0.5f + 0.5f) * amplitude
//  }
    val rawValue = noiseIteration(octaves, location, algorithm, 0, 0f)
//    val rawValue = octaves.fold(0f) { a, (frequency, amplitude) ->
//      a + (algorithm(x * frequency, y * frequency) * 0.5f + 0.5f) * amplitude
//    }
    rawValue / amplitudeMax
//    minMax(rawValue, 0f, 1f)
  }
}

fun nonTilingOpenSimplex2D(seed: Long = 1L): GetSample2d<Float> {
  val generator = OpenSimplexNoise(seed)
  return { x, y ->
//    0.5f
    generator.eval(x, y)
  }
}

fun nonTilingOpenSimplex3D(seed: Long = 1L): GetSample3d<Float> {
  val generator = OpenSimplexNoise(seed)
  return { location: Vector3 ->
//    0.5f
    generator.eval(location.x.toDouble(), location.y.toDouble(), location.z.toDouble()).toFloat()
  }
}

//val noiseOctaveKey = PathKey(texturingPath, "NoiseOctave")
val zeroToOneHundredType = newTypePair(PathKey(texturingPath, "NoiseDetail"))
val oneToOneHundredType = newTypePair(PathKey(texturingPath, "ZeroToOneHundred"))
val noiseVariationType = newTypePair(PathKey(texturingPath, "NoiseVariation"))

val coloredNoiseSignature = CompleteSignature(
    parameters = listOf(
        CompleteParameter("dimensions", absoluteDimensionsType),
        CompleteParameter("scale", oneToOneHundredType),
        CompleteParameter("detail", zeroToOneHundredType),
        CompleteParameter("firstColor", rgbColorType),
        CompleteParameter("secondColor", rgbColorType)
    ),
    output = rgbBitmapType
)

val noiseSignature = CompleteSignature(
    parameters = listOf(
        CompleteParameter("scale", oneToOneHundredType),
        CompleteParameter("detail", zeroToOneHundredType),
        CompleteParameter("variation", noiseVariationType)
    ),
    output = floatSampler2dType
)

val noiseFunction = CompleteFunction(
    path = PathKey(texturingPath, "noise"),
    signature = noiseSignature,
    implementation = { arguments ->
      val variation = arguments["variation"] as Int
      noise2d(arguments, nonTilingOpenSimplex2D(variation.toLong()))
    }
)

val seamlessColoredNoiseFunction = CompleteFunction(
    path = PathKey(texturingPath, "seamlessColoredNoise"),
    signature = coloredNoiseSignature,
    implementation = withBuffer("dimensions", withBitmapBuffer) { arguments ->
      val getNoise = noise2d(arguments, nonTilingOpenSimplex2D())
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
//            alias = intType,
//            numericConstraint = newNumericConstraint(1, 8)
//        ),
        TypeAlias(
            path = oneToOneHundredType.hash,
            alias = intType.hash,
            numericConstraint = newNumericConstraint(1, 100)
        ),
        TypeAlias(
            path = zeroToOneHundredType.hash,
            alias = intType.hash,
            numericConstraint = newNumericConstraint(0, 100)
        ),
        TypeAlias(
            path = noiseVariationType.hash,
            alias = intType.hash,
            numericConstraint = newNumericConstraint(1, 1000)
        )
    )
