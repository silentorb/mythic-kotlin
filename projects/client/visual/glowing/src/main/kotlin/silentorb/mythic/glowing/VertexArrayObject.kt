package silentorb.mythic.glowing

import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.glVertexAttribIPointer

class VertexArrayObject() {
  val id = GL30.glGenVertexArrays()

  companion object {

    fun create(schema: VertexSchema): VertexArrayObject {
      val result = VertexArrayObject()
      val stride = schema.byteSize
      var offset = 0L
      var i = 0
      globalState.vertexArrayObject = result.id

      for (attribute in schema.attributes) {
        if (attribute.elementType != GL_FLOAT && !attribute.normalize) {
          glVertexAttribIPointer(i, attribute.count, attribute.elementType, stride, offset)
        }
        else {
          glVertexAttribPointer(i, attribute.count, attribute.elementType, attribute.normalize, stride, offset)
        }
        glEnableVertexAttribArray(i)
        checkError("binding vbo buffer data")
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
