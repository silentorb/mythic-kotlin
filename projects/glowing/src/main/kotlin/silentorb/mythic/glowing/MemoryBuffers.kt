package silentorb.mythic.glowing

import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import java.nio.IntBuffer

fun createFloatBuffer(values: FloatArray): FloatBuffer {
  val buffer = BufferUtils.createFloatBuffer(values.size)
  for (value in values) {
    buffer.put(value)
  }
  buffer.flip()
  return buffer
}

fun createIntBuffer(value: Int): IntBuffer {
  val buffer = BufferUtils.createIntBuffer(1)
  buffer.put(value)
  buffer.flip()
  return buffer
}

fun createIntBuffer(values: List<Int>): IntBuffer {
  val buffer = BufferUtils.createIntBuffer(values.size)
  for (value in values) {
    buffer.put(value)
  }
  buffer.flip()
  return buffer
}
