package silentorb.mythic.imaging

import silentorb.mythic.spatial.MutableVector3
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import org.lwjgl.BufferUtils
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

fun bitmapToBufferedImage(bitmap: Bitmap): BufferedImage {
  val buffer = bitmap.buffer
  val dimensions = bitmap.dimensions
  val type = when (bitmap.channels) {
    1 -> BufferedImage.TYPE_BYTE_GRAY
    3 -> BufferedImage.TYPE_3BYTE_BGR
    else -> throw Error("Unsupported bitmap channel count ${bitmap.channels}")
  }
  buffer.rewind()
  val array = IntArray(buffer.capacity())
  for (i in 0 until buffer.capacity()) {
    array[i] = (buffer.get() * 255).toInt()
  }
  val image = BufferedImage(dimensions.x, dimensions.y, type)
  image.raster.setPixels(0, 0, dimensions.x, dimensions.y, array)
  return image
}

fun rgbFloatToBytes(input: FloatBuffer, output: ByteBuffer){
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
