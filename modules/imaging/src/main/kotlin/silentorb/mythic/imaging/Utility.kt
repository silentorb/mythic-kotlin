package silentorb.mythic.imaging

import org.lwjgl.BufferUtils
import silentorb.mythic.imaging.operators.allocateFloatBuffer
import silentorb.mythic.spatial.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import java.nio.FloatBuffer

fun ByteBuffer.put(color: Vector3i) {
  this.put(color.x.toByte())
  this.put(color.y.toByte())
  this.put(color.z.toByte())
}

fun FloatBuffer.put(color: MutableVector3) {
  this.put(color.x)
  this.put(color.y)
  this.put(color.z)
}

fun FloatBuffer.put(color: Vector3) {
  this.put(color.x)
  this.put(color.y)
  this.put(color.z)
}

fun FloatBuffer.getVector3() =
    Vector3(
        this.get(),
        this.get(),
        this.get()
    )

enum class BufferedImageType(val value: Int, val channels: Int) {
  grayscale(BufferedImage.TYPE_BYTE_GRAY, 1),
  rgb(BufferedImage.TYPE_3BYTE_BGR, 3)
}

fun getBufferedImageTypeByChannels(channels: Int): BufferedImageType =
    BufferedImageType.values().firstOrNull { it.channels == channels }
        ?: throw Error("Unsupported bitmap channel count ${channels}")

fun bitmapToBufferedImage(bitmap: Bitmap): BufferedImage {
  val buffer = bitmap.buffer
  val dimensions = bitmap.dimensions
  val type = getBufferedImageTypeByChannels(bitmap.channels)
  buffer.rewind()
  val array = IntArray(buffer.capacity())
  for (i in 0 until buffer.capacity()) {
    array[i] = (buffer.get() * 255).toInt()
  }
  val image = BufferedImage(dimensions.x, dimensions.y, type.value)
  image.raster.setPixels(0, 0, dimensions.x, dimensions.y, array)
  return image
}

data class SamplerAwtWriter(
    val depth: Int,
    val write: (Float, Float, IntArray, Int) -> Int
)

fun newFloatSampleWriter(sampler: FloatSampler) =
    SamplerAwtWriter(depth = 1) { x, y, array, i ->
      val sample = sampler(x, y)
      array[i] = (sample * 255).toInt()
      i + 1
    }

fun newRgbSampleWriter(sampler: RgbSampler) =
    SamplerAwtWriter(depth = 3) { x, y, array, i ->
      val sample = sampler(x, y)
      array[i] = (sample.x * 255).toInt()
      array[i + 1] = (sample.y * 255).toInt()
      array[i + 2] = (sample.z * 255).toInt()
      i + 3
    }

fun rgbSamplerToBufferedImage(fullDimensions: Vector2i, samplerAwtWriter: SamplerAwtWriter, offset: Vector2i, tileSize: Vector2i): BufferedImage {
  val depth = samplerAwtWriter.depth
  val type = getBufferedImageTypeByChannels(depth)
  val size = tileSize.x * tileSize.y * depth
  val array = IntArray(size)
  var i = 0
  val end = offset + tileSize
  for (y in offset.y until end.y) {
    for (x in offset.x until end.x) {
      i = samplerAwtWriter.write(x.toFloat() / fullDimensions.x, y.toFloat() / fullDimensions.y, array, i)
    }
  }
  val image = BufferedImage(tileSize.x, tileSize.y, type.value)
  image.raster.setPixels(0, 0, tileSize.x, tileSize.y, array)
  return image
}

fun bufferedImageToBitmap(dimensions: Vector2i, depth: Int, image: BufferedImage): Bitmap {
  val buffer = allocateFloatBuffer(dimensions.x * dimensions.y * depth)
  val array = IntArray(buffer.capacity())
  image.raster.getPixels(0, 0, dimensions.x, dimensions.y, array)
  for (i in 0 until buffer.capacity()) {
    buffer.put(array[i] / 255f)
  }
  buffer.rewind()
  return Bitmap(
      buffer = buffer,
      dimensions = dimensions,
      channels = depth
  )
}

fun toAwtColor(color: Vector3) =
    Color(color.x, color.y, color.z)

fun rgbFloatToBytes(input: FloatBuffer, output: ByteBuffer) {
  input.rewind()
  (1..input.capacity()).forEach {
    val value = input.get()
    output.put((value * 255).toByte())
  }
  output.rewind()
}

fun rgbFloatToBytes(buffer: FloatBuffer): ByteBuffer {
  val byteBuffer = BufferUtils.createByteBuffer(buffer.capacity())
  buffer.rewind()
  (1..buffer.capacity()).forEach {
    val value = buffer.get()
    byteBuffer.put((value * 255).toByte())
  }
  byteBuffer.rewind()
  return byteBuffer
}

fun grayscaleTextureToBytes(buffer: FloatBuffer): ByteBuffer {
  val byteBuffer = BufferUtils.createByteBuffer(buffer.capacity() * 3)
  buffer.rewind()
  (1..buffer.capacity()).forEach {
    val value = (buffer.get() * 255).toByte()
    byteBuffer.put(value)
    byteBuffer.put(value)
    byteBuffer.put(value)
  }
  byteBuffer.rewind()
  return byteBuffer
}
