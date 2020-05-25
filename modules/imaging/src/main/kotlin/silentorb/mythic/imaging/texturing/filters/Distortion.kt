package silentorb.mythic.imaging.texturing.filters

import silentorb.imp.core.CompleteParameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.CompleteSignature
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.FunctionImplementation
import silentorb.mythic.imaging.texturing.AnySampler
import silentorb.mythic.imaging.texturing.floatSampler2dType
import silentorb.mythic.imaging.texturing.rgbSampler2dType
import silentorb.mythic.imaging.texturing.texturingPath

private fun commonDistortionParameters() = listOf(
    CompleteParameter("strength", zeroToOneHundredType),
    CompleteParameter("scale", oneToOneHundredType),
    CompleteParameter("detail", zeroToOneHundredType),
    CompleteParameter("variation", noiseVariationType)
)

val distortSignatureRgb = CompleteSignature(
    parameters = commonDistortionParameters()
        .plus(CompleteParameter("source", rgbSampler2dType)),
    output = rgbSampler2dType
)

val distortSignatureFloat = CompleteSignature(
    parameters = commonDistortionParameters()
        .plus(CompleteParameter("source", floatSampler2dType)),
    output = floatSampler2dType
)

val distortionImplementation: FunctionImplementation = { arguments ->
  val variation = (arguments["variation"] as Int).toLong()
  val horizontalNoise = noise2d(arguments, nonTilingOpenSimplex2D(variation))
  val verticalNoise = noise2d(arguments, nonTilingOpenSimplex2D(variation + 1))
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
