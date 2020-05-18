package silentorb.mythic.imaging.texturing

import silentorb.imp.core.PathKey
import silentorb.mythic.imaging.common.GetSample2d
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
val rgbColorKey = PathKey(texturingPath, "RgbColor")
val rgbColorType = rgbColorKey.hashCode()
val transparentColorKey = PathKey(texturingPath, "RgbaColor")

val rgbBitmapKey = PathKey(texturingPath, "RgbBitmap")
val rgbBitmapType = rgbBitmapKey.hashCode()
val transparentColorBitmapKey = PathKey(texturingPath, "RgbaBitmap")
val grayscaleBitmapKey = PathKey(texturingPath, "GrayscaleBitmap")
val grayscaleBitmapType = grayscaleBitmapKey.hashCode()
val absoluteDimensionsKey = PathKey(texturingPath, "Dimensions")
val absoluteDimensionsType = absoluteDimensionsKey.hashCode()
val relativeDimensionsKey = PathKey(texturingPath, "RelativeDimensions")
val relativeDimensionsType = relativeDimensionsKey.hashCode()

val floatSampler2dKey = PathKey(texturingPath, "FloatSampler")
val floatSampler2dType = floatSampler2dKey.hashCode()
val rgbSampler2dKey = PathKey(texturingPath, "RgbSampler")
val rgbSampler2dType = rgbSampler2dKey.hashCode()

typealias FloatSampler = GetSample2d<Float>

typealias RgbSampler = GetSample2d<Vector3>
typealias AnySampler = GetSample2d<Any>

data class Bitmap(
    val buffer: FloatBuffer,
    val channels: Int,
    val dimensions: Vector2i
)
