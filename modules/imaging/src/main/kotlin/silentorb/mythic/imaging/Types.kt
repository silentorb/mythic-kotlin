package silentorb.mythic.imaging

import silentorb.imp.core.PathKey
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import java.nio.FloatBuffer

typealias Sampler = (Float, Float) -> Float

const val texturingPath = "silentorb.mythic.generation.texturing"

typealias RgbColor = Vector3
typealias RgbaColor = Vector4
typealias GrayscaleColor = Float

// Type Keys
val rgbColorKey = PathKey(texturingPath, "RgbColor")
val transparentColorKey = PathKey(texturingPath, "RgbaColor")

val rgbBitmapKey = PathKey(texturingPath, "RgbBitmap")
val transparentColorBitmapKey = PathKey(texturingPath, "RgbaBitmap")
val grayscaleBitmapKey = PathKey(texturingPath, "GrayscaleBitmap")
val absoluteDimensionsKey = PathKey(texturingPath, "Dimensions")
val relativeDimensionsKey = PathKey(texturingPath, "RelativeDimensions")

data class Bitmap(
    val buffer: FloatBuffer,
    val channels: Int,
    val dimensions: Vector2i
)
