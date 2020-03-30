package silentorb.mythic.imaging.texturing.filters

import org.lwjgl.BufferUtils
import silentorb.imp.execution.Arguments
import silentorb.imp.execution.FunctionImplementation
import silentorb.mythic.imaging.common.GetSample2d
import silentorb.mythic.imaging.texturing.Bitmap
import silentorb.mythic.imaging.texturing.put
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

fun dimensionsFromArguments(arguments: Arguments, dimensionsField: String): Vector2i {
  val dimensionsArgument = arguments[dimensionsField]!!
  return if (dimensionsArgument is Vector2i)
    dimensionsArgument
  else if (dimensionsArgument is Bitmap)
    dimensionsArgument.dimensions
  else throw Error("Invalid dimensions argument $dimensionsArgument")
}

fun <T> withBuffer(dimensionsField: String, bufferInfo: BufferInfo<T>, function: (Arguments) -> GetSample2d<T>): FunctionImplementation =
    { arguments ->
      val dimensions = dimensionsFromArguments(arguments, dimensionsField)
      val depth = bufferInfo.depth
      val buffer = allocateFloatBuffer(dimensions.x * dimensions.y * depth)
      val getter = function(arguments)
      for (y in 0 until dimensions.y) {
        for (x in 0 until dimensions.x) {
          val value = getter(x.toFloat() / dimensions.x, 1f - y.toFloat() / dimensions.y)
          bufferInfo.setter(buffer, value)
        }
      }
      buffer.rewind()
      Bitmap(
          dimensions = dimensions,
          channels = depth,
          buffer = buffer
      )
    }

fun <T> samplerToBitmap(bufferInfo: BufferInfo<T>, dimensions: Vector2i, sampler: GetSample2d<T>): Bitmap {
  val depth = bufferInfo.depth
  val buffer = allocateFloatBuffer(dimensions.x * dimensions.y * depth)
  for (y in 0 until dimensions.y) {
    for (x in 0 until dimensions.x) {
      val value = sampler(x.toFloat() / dimensions.x, 1f - y.toFloat() / dimensions.y)
      bufferInfo.setter(buffer, value)
    }
  }
  buffer.rewind()
  return Bitmap(
      dimensions = dimensions,
      channels = depth,
      buffer = buffer
  )
}

val withBitmapBuffer = BufferInfo<Vector3>(3) { buffer, value ->
  buffer.put(value)
}

val withGrayscaleBuffer = BufferInfo<Float>(1) { buffer, value ->
  buffer.put(value)
}
