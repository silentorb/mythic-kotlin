package silentorb.mythic.imaging

import silentorb.imp.core.PathKey

typealias Sampler = (Float, Float) -> Float

const val texturingPath = "silentorb.mythic.generation.texturing"

// Type Keys
val solidColorKey = PathKey(texturingPath, "SolidColor")
val transparentColorKey = PathKey(texturingPath, "TransparentColor")

val solidColorBitmapKey = PathKey(texturingPath, "SolidColorBitmap")
val transparentColorBitmapKey = PathKey(texturingPath, "TransparentColorBitmap")
val grayscaleBitmapKey = PathKey(texturingPath, "GrayscaleBitmap")
val dimensionsKey = PathKey(texturingPath, "Dimensions")
