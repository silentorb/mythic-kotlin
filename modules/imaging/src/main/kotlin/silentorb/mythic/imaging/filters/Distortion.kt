package silentorb.mythic.imaging.filters

import silentorb.imp.core.Parameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.Signature
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.FunctionImplementation
import silentorb.mythic.imaging.AnySampler
import silentorb.mythic.imaging.floatSamplerKey
import silentorb.mythic.imaging.rgbSamplerKey
import silentorb.mythic.imaging.texturingPath

private fun commonDistortionParameters() = listOf(
    Parameter("strength", zeroToOneHundredKey),
    Parameter("scale", zeroToOneHundredKey),
    Parameter("detail", noiseDetailKey),
    Parameter("variation", noiseVariationKey)
)

val distortSignatureRgb = Signature(
    parameters = commonDistortionParameters()
        .plus(Parameter("source", rgbSamplerKey)),
    output = rgbSamplerKey
)

val distortSignatureFloat = Signature(
    parameters = commonDistortionParameters()
        .plus(Parameter("source", floatSamplerKey)),
    output = floatSamplerKey
)

val distortionImplementation: FunctionImplementation = { arguments ->
  val variation = (arguments["variation"] as Int).toLong()
  val horizontalNoise = noise(arguments, nonTilingOpenSimplex2D(variation))
  val verticalNoise = noise(arguments, nonTilingOpenSimplex2D(variation + 1))
  val strength = (arguments["strength"] as Int).toFloat() / 100f
  val sampler = arguments["source"] as AnySampler
  ;
  { x: Float, y: Float ->
    val newX = (x + horizontalNoise(x, y) * strength) % 1f
    val newY = (y + verticalNoise(x, y) * strength) % 1f
    sampler(newX, newY)
  }
}

fun distortionFunctions() = listOf(
    CompleteFunction(
        path = PathKey(texturingPath, "distort"),
        signature = distortSignatureRgb,
        implementation = distortionImplementation
    ),
    CompleteFunction(
        path = PathKey(texturingPath, "distort"),
        signature = distortSignatureFloat,
        implementation = distortionImplementation
    )
)
