package mythic.glowing

import org.lwjgl.opengl.GL11.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer

fun getErrorInfo(error: Int): String {
  when (error) {
    GL_INVALID_OPERATION ->
      return "GL_INVALID_OPERATION"

    GL_INVALID_VALUE ->
      return "GL_INVALID_VALUE"

    GL_INVALID_ENUM ->
      return "GL_INVALID_ENUM"
  }

  return error.toString()
}

fun checkError(message: String) {
  val error = glGetError()
  if (error != GL_NO_ERROR) {
    val info = getErrorInfo(error)
    throw Error("OpenGL Error " + info + " while " + message)
  }
}

class BufferCustodian(val buffer: ByteBuffer) {
  private var bufferInitialized = false

  fun finish() {
    if (!bufferInitialized) {
      while (buffer.position() != buffer.capacity()) {
        buffer.put(1)
      }
      bufferInitialized = true
    }
//    buffer.position(buffer.capacity() - 1)
    buffer.rewind()
    assert(buffer.limit() == buffer.capacity())
  }
}

class FloatBufferCustodian(val buffer: FloatBuffer) {
  private var bufferInitialized = false

  fun finish() {
    if (!bufferInitialized) {
      while (buffer.position() != buffer.capacity()) {
        buffer.put(1f)
      }
      bufferInitialized = true
    }
//    buffer.position(buffer.capacity() - 1)
    buffer.rewind()
    assert(buffer.limit() == buffer.capacity())
  }
}
