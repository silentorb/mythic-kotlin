package silentorb.metaview.common

import silentorb.metaview.common.views.ValueDisplayMap
import silentorb.metaview.common.views.newImage
import silentorb.mythic.imaging.rgbFloatToBytes
import silentorb.mythic.imaging.grayscaleTextureToBytes
import org.joml.Vector2i
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer

val defaultBitmap: (Int) -> FloatBuffer = { length ->
  BufferUtils.createFloatBuffer(length * length * 3)
}

val defaultGrayscale: (Int) -> FloatBuffer = { length ->
  BufferUtils.createFloatBuffer(length * length)
}

fun fillerTypeValues(length: Int): ValueMap = mapOf(
    bitmapType to defaultBitmap,
    grayscaleType to defaultGrayscale,
    depthsType to defaultGrayscale,
    positionsType to defaultBitmap,
    normalsType to defaultBitmap
).mapValues { { it.value(length) } }

fun textureValueDisplays(dimensions: Vector2i): ValueDisplayMap = mapOf(
    bitmapType to ::rgbFloatToBytes,
    grayscaleType to ::grayscaleTextureToBytes
).mapValues { (_, toBytes) ->
  { value: Any ->
    val buffer = value as FloatBuffer
    val output = BufferUtils.createByteBuffer(buffer.capacity())
    newImage(dimensions, toBytes(buffer))
  }
}.plus(mapOf(
    multiType to { value: Any ->
      val map = value as Map<String, Any>
      val buffer = map["color"] as FloatBuffer
      newImage(dimensions, rgbFloatToBytes(buffer))
    }
))
