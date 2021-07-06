package silentorb.mythic.glowing

import org.lwjgl.opengl.GL30.*

class RenderBuffer {
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
