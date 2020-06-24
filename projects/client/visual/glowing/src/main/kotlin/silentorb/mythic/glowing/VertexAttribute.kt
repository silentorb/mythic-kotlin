package silentorb.mythic.glowing

import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE

fun elementTypeByteSize(elementType: Int): Int =
    when (elementType) {
      GL_FLOAT -> 4
      GL_UNSIGNED_BYTE -> 1
      else -> throw Error("Not supported")
    }

class VertexAttribute(
    val name: String,
    val count: Int,
    val elementType: Int
) {
  val byteSize = count * elementTypeByteSize(elementType)
}

fun floatVertexAttribute(name: String, count: Int) =
    VertexAttribute(
        name = name,
        count = count,
        elementType = GL_FLOAT
    )
