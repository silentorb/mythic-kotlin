package mythic.glowing

import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30

class VertexArrayObject() {
  val id = GL30.glGenVertexArrays()

  companion object {

    fun createInterwoven(schema: VertexSchema): VertexArrayObject {
      val result = VertexArrayObject()
      var offset = 0L
      var i = 0
      globalState.vertexArrayObject = result.id

      for (attribute in schema.attributes) {
        glVertexAttribPointer(i, attribute.size, GL_FLOAT, false, schema.floatSize * 4, offset)
        glEnableVertexAttribArray(i)
        checkError("binding vbo buffer data")
        i++
        offset += attribute.size * 4
      }
      return result
    }

    fun createNonInterleaved(schema: VertexSchema): VertexArrayObject {
      val result = VertexArrayObject()
      var offset = 0L
      var i = 0
      globalState.vertexArrayObject = result.id

      for (attribute in schema.attributes) {
        glVertexAttribPointer(i, attribute.size, GL_FLOAT, false, schema.floatSize * 4, offset)
        glEnableVertexAttribArray(i)
        i++
        offset += attribute.size * 4
      }
      return result
    }
  }

  fun activate() {
    globalState.vertexArrayObject = id
  }
}
