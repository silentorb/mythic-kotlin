package silentorb.mythic.glowing

import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30

class VertexArrayObject() {
  val id = GL30.glGenVertexArrays()

  companion object {

    fun createInterwoven(schema: VertexSchema): VertexArrayObject {
      val result = VertexArrayObject()
      val stride = schema.byteSize
      var offset = 0L
      var i = 0
      globalState.vertexArrayObject = result.id

      for (attribute in schema.attributes) {
        glVertexAttribPointer(i, attribute.count, attribute.elementType, true, stride, offset)
        glEnableVertexAttribArray(i)
        checkError("binding vbo buffer data")
        i++
        offset += attribute.byteSize
      }
      return result
    }

    fun createNonInterleaved(schema: VertexSchema): VertexArrayObject {
      val result = VertexArrayObject()
      val stride = schema.byteSize
      var offset = 0L
      var i = 0
      globalState.vertexArrayObject = result.id

      for (attribute in schema.attributes) {
        glVertexAttribPointer(i, attribute.count, attribute.elementType, true, stride, offset)
        glEnableVertexAttribArray(i)
        i++
        offset += attribute.byteSize
      }
      return result
    }
  }

  fun activate() {
    globalState.vertexArrayObject = id
  }
}
