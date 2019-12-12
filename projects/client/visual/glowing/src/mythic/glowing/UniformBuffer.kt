package mythic.glowing

import org.lwjgl.opengl.ARBUniformBufferObject.GL_UNIFORM_BUFFER
import org.lwjgl.opengl.GL15.*
import java.nio.ByteBuffer

class UniformBuffer(val size: Int) {
  val id = glGenBuffers()
//  private var _size: Long = 0
  private val disposed = false

  init {
//    println("New Uniform Buffer   id: " + id + ", size: " + size)
  }

//  val size: Long
//    get() = _size

  fun load(data: ByteBuffer) {
    if (data.limit() > size)
      throw Error("Uniform buffer input data exceeds buffer size.")

    globalState.uniformBufferObject = id
    glBufferData(GL_UNIFORM_BUFFER, data, GL_DYNAMIC_DRAW)
    globalState.uniformBufferObject = 0
//    _size = data.limit().toLong()
  }

//  fun activate() {
//    globalState.uniformBufferObject = id
//  }

  fun dispose() {
    if (disposed)
      return

    if (globalState.uniformBufferObject == id)
      globalState.uniformBufferObject = 0

    glDeleteBuffers(id)
  }
}