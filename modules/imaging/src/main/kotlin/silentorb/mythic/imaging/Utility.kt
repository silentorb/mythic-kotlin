package silentorb.mythic.imaging

import org.lwjgl.BufferUtils
import silentorb.mythic.imaging.filters.allocateFloatBuffer
import silentorb.mythic.spatial.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.WritableRaster
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
    val write: (Float, Float, WritableRaster, Int, Int) -> Unit
)

fun newFloatSampleWriter(sampler: FloatSampler) =
    SamplerAwtWriter(depth = 1) { x, y, raster, intX, intY ->
      val sample = sampler(x, y)
      raster.setSample(intX, intY, 0, (sample * 255).toInt())
    }

fun newRgbSampleWriter(sampler: RgbSampler) =
    SamplerAwtWriter(depth = 3) { x, y, raster, intX, intY ->
      val sample = sampler(x, y)
      raster.setSample(intX, intY, 0, (sample.x * 255).toInt())
      raster.setSample(intX, intY, 1, (sample.y * 255).toInt())
      raster.setSample(intX, intY, 2, (sample.z * 255).toInt())
    }

fun newBufferedImage(width: Int, height: Int, depth: Int): BufferedImage {
  val type = getBufferedImageTypeByChannels(depth)
  return BufferedImage(width, height, type.value)
}

fun newBufferedImage(dimensions: Vector2i, depth: Int) =
    newBufferedImage(dimensions.x, dimensions.y, depth)

fun samplerToBufferedImage(samplerAwtWriter: SamplerAwtWriter, image: BufferedImage, fullDimensions: Vector2i, offset: Vector2i, tileSize: Vector2i) {
  val end = offset + tileSize
  val raster = image.raster
  for (y in offset.y until end.y) {
    for (x in offset.x until end.x) {
      samplerAwtWriter.write(x.toFloat() / fullDimensions.x, y.toFloat() / fullDimensions.y, raster, x - offset.x, y - offset.y)
    }
  }
}

fun rgbSamplerToArray(sampler: RgbSampler, image: FloatArray, dimensions: Vector2i) {
  var i = 0
  for (y in 0 until dimensions.y) {
    for (x in 0 until dimensions.x) {
      val sample = sampler(x.toFloat() / dimensions.x, y.toFloat() / dimensions.y)
      image[i++] = sample.x
      image[i++] = sample.y
      image[i++] = sample.z
    }
  }
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

fun toAwtColor(color: Vector3i) =
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

fun rgbIntToFloat(value: RgbColor): Vector3 =
    Vector3(
        value.x / 255f,
        value.y / 255f,
        value.z / 255f
    )
