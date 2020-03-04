package silentorb.mythic.imaging.operators

import org.lwjgl.BufferUtils
import silentorb.imp.execution.Arguments
import silentorb.imp.execution.FunctionImplementation
import silentorb.mythic.imaging.Bitmap
import silentorb.mythic.imaging.put
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector3
import java.nio.ByteBuffer
import java.nio.FloatBuffer

fun allocateFloatTextureBuffer(length: Int): FloatBuffer =
    BufferUtils.createFloatBuffer(length * length * 3)

fun allocateByteTextureBuffer(length: Int): ByteBuffer =
    BufferUtils.createByteBuffer(length * length * 3)

fun allocateFloatBuffer(size: Int): FloatBuffer =
    BufferUtils.createFloatBuffer(size)

fun fillBuffer(depth: Int, dimensions: Vector2i, action: (FloatBuffer) -> Unit): Bitmap {
//  val buffer = BufferUtils.createFloatBuffer(dimensions.x * dimensions.y * depth)
//  val buffer = bufferCache(id, dimensions.x * dimensions.y * depth)
  val buffer = allocateFloatBuffer(dimensions.x * dimensions.y * depth)
  action(buffer)
  buffer.rewind()
  return Bitmap(
      dimensions = dimensions,
      channels = depth,
      buffer = buffer
  )
}

fun <T> withBuffer(dimensionsField: String, bufferInfo: BufferInfo<T>, function: (Arguments) -> GetPixel<T>): FunctionImplementation =
    { arguments ->
      val dimensionsArgument = arguments[dimensionsField]!!
      val dimensions: Vector2i = if (dimensionsArgument is Vector2i)
        dimensionsArgument
      else if (dimensionsArgument is Bitmap)
        dimensionsArgument.dimensions
      else throw Error("Invalid dimensions argument $dimensionsArgument")

      fillBuffer(bufferInfo.depth, dimensions) { buffer ->
        val getter = function(arguments)
        for (y in 0 until dimensions.y) {
          for (x in 0 until dimensions.x) {
            val value = getter(x.toFloat() / dimensions.x, 1f - y.toFloat() / dimensions.y)
            bufferInfo.setter(buffer, value)
          }
        }
      }
    }

val withBitmapBuffer = BufferInfo<Vector3>(3) { buffer, value ->
  buffer.put(value)
}

val withGrayscaleBuffer = BufferInfo<Float>(1) { buffer, value ->
  buffer.put(value)
}
