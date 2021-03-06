package silentorb.mythic.glowing

import org.lwjgl.opengl.GL20.*

abstract class ShaderBase(code: String, type: Int) {
  val id: Int

  init {
    id = glCreateShader(type)
    checkError("Error creating shader.")
    glShaderSource(id, "#version 430\n" + code)
    checkError("Error loading shader code.")
    glCompileShader(id)
    val compiled = glGetShaderi(id, GL_COMPILE_STATUS)
    val shaderLog = glGetShaderInfoLog(id)
    if (shaderLog.trim { it <= ' ' }.isNotEmpty())
      throw Error(shaderLog)

    if (compiled == 0)
      throw Error("Could not compile shader.")
  }
}
