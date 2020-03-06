package silentorb.mythic.imaging

import silentorb.mythic.spatial.MutableVector3
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import org.lwjgl.BufferUtils
import silentorb.mythic.imaging.operators.allocateFloatBuffer
import silentorb.mythic.spatial.Vector2i
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
    BufferedImageType.values().firstOrNull { it.channels == channels } ?:
    throw Error("Unsupported bitmap channel count ${channels}")

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
