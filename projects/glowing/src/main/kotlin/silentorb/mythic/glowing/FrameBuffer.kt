package silentorb.mythic.glowing

import silentorb.mythic.spatial.Vector2i
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_LINEAR
import org.lwjgl.opengl.GL11.GL_NEAREST
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL32
import org.lwjgl.opengl.GL32.glFramebufferTexture

class FrameBuffer() {
  val id = glGenFramebuffers()
  private var disposed = false

  init {
    globalState.setFrameBuffer(id)
  }

  fun blitToScreen(sourceDimensions: Vector2i, targetDimensions: Vector2i, smooth: Boolean) {
    globalState.readFramebuffer = id
    globalState.drawFramebuffer = 0
    glBlitFramebuffer(
        0, 0, sourceDimensions.x, sourceDimensions.y,
        0, 0, targetDimensions.x, targetDimensions.y,
        GL_COLOR_BUFFER_BIT,
        if (smooth) GL_LINEAR else GL_NEAREST
    )
  }

  fun dispose() {
    if (disposed)
      return

    glDeleteFramebuffers(id)
    disposed = true
  }

  fun activateDraw() {
    globalState.drawFramebuffer = id
  }
}

data class OffscreenBuffer(
    val frameBuffer: FrameBuffer,
    val colorTexture: Texture,
    val depthTexture: Texture?
)

fun applyOffscreenBuffer(buffer: OffscreenBuffer, windowDimensions: Vector2i, smooth: Boolean) {
  buffer.frameBuffer.blitToScreen(Vector2i(buffer.colorTexture.width, buffer.colorTexture.height), windowDimensions, smooth)
}

fun prepareScreenFrameBuffer(windowWidth: Int, windowHeight: Int, withDepth: Boolean): OffscreenBuffer {
  val dimensions = Vector2i(windowWidth, windowHeight)
  val framebuffer = FrameBuffer()
  val textureAttributes = TextureAttributes(
      repeating = false,
      smooth = false,
      storageUnit = TextureStorageUnit.unsigned_byte
  )
  val colorTexture = Texture(dimensions.x, dimensions.y, textureAttributes)
  glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, colorTexture.id, 0)
  glDrawBuffers(GL_COLOR_ATTACHMENT0)

  val depthTexture = if (withDepth) {
    val depthTexture = Texture(dimensions.x, dimensions.y, textureAttributes.copy(format = TextureFormat.depth, storageUnit = TextureStorageUnit.float))
    glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthTexture.id, 0)
    depthTexture
  } else
    null

  val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
  if (status != GL_FRAMEBUFFER_COMPLETE)
    throw Error("Error creating framebuffer.")

  if (withDepth) {
    clearDepth() // Initialize the depth texture (the pixels of which are undefined until this)
  }
  globalState.setFrameBuffer(0)
  return OffscreenBuffer(framebuffer, colorTexture, depthTexture)
}
