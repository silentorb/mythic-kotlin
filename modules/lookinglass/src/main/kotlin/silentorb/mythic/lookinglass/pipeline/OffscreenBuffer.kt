package silentorb.mythic.lookinglass.pipeline

import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL32
import org.lwjgl.opengl.GL32.glFramebufferTexture
import silentorb.mythic.glowing.*
import silentorb.mythic.spatial.Vector2i

data class OffscreenBuffer(
    val frameBuffer: FrameBuffer,
    val colorTexture: Texture,
    val depthTexture: Texture?,
) {
  val dimensions: Vector2i get() = colorTexture.dimensions
}

fun applyOffscreenBuffer(buffer: OffscreenBuffer, windowDimensions: Vector2i, smooth: Boolean) {
  buffer.frameBuffer.blitToScreen(Vector2i(buffer.colorTexture.width, buffer.colorTexture.height), windowDimensions, smooth)
}

fun newDepthTexture(textureAttributes: TextureAttributes, dimensions: Vector2i): Texture {
  val attributes = textureAttributes.copy(
      format = TextureFormat.depthStencil,
      storageUnit = TextureStorageUnit.unsignedInt24_8
  )
  val depthTexture = newTexture(dimensions.x, dimensions.y, attributes)
  glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, depthTexture.id, 0)
  return depthTexture
}

fun prepareScreenFrameBuffer(windowWidth: Int, windowHeight: Int, withDepth: Boolean): OffscreenBuffer {
  val dimensions = Vector2i(windowWidth, windowHeight)
  val framebuffer = FrameBuffer()
  val textureAttributes = TextureAttributes(
      repeating = false,
      smooth = false,
      storageUnit = TextureStorageUnit.unsignedByte
  )
  val colorTexture = newTexture(dimensions.x, dimensions.y, textureAttributes)
  glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, colorTexture.id, 0)
  glDrawBuffers(GL_COLOR_ATTACHMENT0)

  val depthTexture = if (withDepth)
    newDepthTexture(textureAttributes, dimensions)
  else
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

fun disposeOffscreenBuffer(offscreenBuffer: OffscreenBuffer) {
  offscreenBuffer.frameBuffer.dispose()
  offscreenBuffer.colorTexture.dispose()
  offscreenBuffer.depthTexture?.dispose()
}
