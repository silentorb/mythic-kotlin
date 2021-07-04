package silentorb.mythic.glowing

import silentorb.mythic.spatial.Vector2i
import org.lwjgl.opengl.GL11.GL_LINEAR
import org.lwjgl.opengl.GL11.GL_NEAREST
import org.lwjgl.opengl.GL30.*

class FrameBuffer() {
  val id = glGenFramebuffers()
  private var disposed = false

  init {
    globalState.setFrameBuffer(id)
  }

  fun blitToScreen(sourceDimensions: Vector2i, targetDimensions: Vector2i, smooth: Boolean) {
    activateRead()
    globalState.drawFramebuffer = 0
    glBlitFramebuffer(
        0, 0, sourceDimensions.x, sourceDimensions.y,
        0, 0, targetDimensions.x, targetDimensions.y,
        GL_COLOR_BUFFER_BIT,
        if (smooth) GL_LINEAR else GL_NEAREST
    )
  }

  fun deactivate() {
    if (globalState.drawFramebuffer == id) {
      globalState.drawFramebuffer = 0
    }
    if (globalState.readFramebuffer == id) {
      globalState.readFramebuffer = 0
    }
  }

  fun dispose() {
    if (disposed)
      return

    deactivate()
    glDeleteFramebuffers(id)
    disposed = true
  }

  fun activate() {
    globalState.setFrameBuffer(id)
  }

  fun activateDraw() {
    globalState.drawFramebuffer = id
  }

  fun activateRead() {
    globalState.readFramebuffer = id
  }
}

