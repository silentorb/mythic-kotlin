package silentorb.mythic.glowing

import org.lwjgl.opengl.GL11.*

fun elementTypeByteSize(elementType: Int): Int =
    when (elementType) {
      GL_FLOAT -> 4
      GL_UNSIGNED_BYTE, GL_BYTE -> 1
      else -> throw Error("Not supported")
    }

class VertexAttribute(
    val name: String,
    val count: Int,
    val elementType: Int,
    val normalize: Boolean
) {
  val byteSize = count * elementTypeByteSize(elementType)
}

fun floatVertexAttribute(name: String, count: Int) =
    VertexAttribute(
        name = name,
        count = count,
        elementType = GL_FLOAT,
        normalize = false // Doesn't matter true or false for GL_FLOAT
    )
