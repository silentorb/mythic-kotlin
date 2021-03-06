package silentorb.mythic.imaging.texturing

import silentorb.imp.core.*
import silentorb.mythic.imaging.common.GetSample2d
import silentorb.mythic.imaging.common.GetSample3d
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import silentorb.mythic.spatial.Vector4
import java.nio.FloatBuffer

typealias Sampler = (Float, Float) -> Float

const val texturingPath = "silentorb.mythic.generation.texturing"

typealias RgbColor = Vector3i
typealias RgbaColor = Vector4
typealias GrayscaleColor = Float

// Type Keys
val rgbColorType = newTypePair(PathKey(texturingPath, "RgbColor"))
val newRgbSignature = CompleteSignature(
    parameters = listOf(
        CompleteParameter("red", intType),
        CompleteParameter("green", intType),
        CompleteParameter("blue", intType)
    ),
    output = rgbColorType
)
val newRgbTypeHash = convertCompleteSignature(newRgbSignature).hashCode()

val rgbBitmapType = newTypePair(PathKey(texturingPath, "RgbBitmap"))
val grayscaleBitmapType = newTypePair(PathKey(texturingPath, "GrayscaleBitmap"))
val absoluteDimensionsType = newTypePair(PathKey(texturingPath, "Dimensions"))
val relativeDimensionsType = newTypePair(PathKey(texturingPath, "RelativeDimensions"))

val floatSampler2dType = newTypePair(PathKey(texturingPath, "FloatSampler"))
val rgbSampler2dType = newTypePair(PathKey(texturingPath, "RgbSampler"))

typealias FloatSampler2d = GetSample2d<Float>
typealias DistanceSample = Pair<Any, Float>
typealias DistanceSampler = (Vector3) -> DistanceSample

const val anonymousSampler = 0

typealias RgbSampler = GetSample2d<Vector3>
typealias AnySampler = GetSample2d<Any>

data class Bitmap(
    val buffer: FloatBuffer,
    val channels: Int,
    val dimensions: Vector2i
)
