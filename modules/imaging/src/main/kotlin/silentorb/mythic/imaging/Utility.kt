package silentorb.mythic.imaging

import silentorb.mythic.spatial.MutableVector3
import silentorb.mythic.spatial.Vector3
import org.joml.Vector3i
import org.lwjgl.BufferUtils
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
