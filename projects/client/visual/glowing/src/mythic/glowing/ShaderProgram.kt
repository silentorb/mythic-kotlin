package mythic.glowing

import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*

enum class ShaderType {
  vertex,
  fragment
}

class ShaderProgram(val vertexShader: VertexShader, val fragmentShader: FragmentShader) {
  var id: Int

  init {
    id = glCreateProgram()
    glAttachShader(id, vertexShader.id)
    glAttachShader(id, fragmentShader.id)
    glBindAttribLocation(id, 0, "position")
    glBindFragDataLocation(id, 0, "color")
    glLinkProgram(id)
    val linked = glGetProgrami(id, GL_LINK_STATUS)
    val programLog = glGetProgramInfoLog(id)
    if (programLog!!.trim({ it <= ' ' }).isNotEmpty())
      throw Error(programLog)

    if (linked == 0)
      throw Error("Could not link program.")
  }

  constructor(vertexCode: String, fragmentCode: String) :
      this(VertexShader(vertexCode), FragmentShader(fragmentCode))

  fun activate() {
    globalState.shaderProgram = id
  }
}