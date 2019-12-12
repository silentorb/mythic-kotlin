package mythic.glowing

import org.lwjgl.opengl.GL30.*

class Renderbuffer() {
  val id = glGenRenderbuffers()
  private var disposed = false

  init {
    glBindRenderbuffer(GL_RENDERBUFFER, id)
  }

  fun dispose() {
    if (disposed)
      return

    glDeleteRenderbuffers(id)
    disposed = true
  }
}
