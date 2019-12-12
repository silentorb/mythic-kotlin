package mythic.glowing

import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL30.glDeleteVertexArrays
import java.nio.FloatBuffer

class VertexBuffer(private val vbo: Int, private val vao: VertexArrayObject) {
  private val disposed = false

  fun load(vertices: FloatBuffer): VertexBuffer {
    globalState.vertexBufferObject = vbo
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    return this
  }

  fun activate() {
    globalState.vertexArrayObject = vao.id
  }

  fun dispose() {
    if (disposed)
      return

    // There seems to be a multi-platform hardware bug where deleting the currently bound
    // vbo and then generating a new one results in corrupting the new vbo.
    if (globalState.vertexBufferObject == vbo)
      globalState.vertexBufferObject = 0

    if (globalState.vertexArrayObject == vao.id)
      globalState.vertexArrayObject = 0

    glDeleteVertexArrays(vao.id)
    glDeleteBuffers(vbo)
  }
}

fun newVertexBuffer(vertexSchema: VertexSchema, interleaved: Boolean = true): VertexBuffer {
  val vbo = glGenBuffers()
  globalState.vertexBufferObject = vbo
  val vao = if (interleaved)
    VertexArrayObject.createInterwoven(vertexSchema)
  else
    VertexArrayObject.createNonInterleaved(vertexSchema)

  checkError("binding vbo buffer data")
  return VertexBuffer(vbo, vao)
}
