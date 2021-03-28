package silentorb.mythic.lookinglass.deferred

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import silentorb.mythic.glowing.*
import silentorb.mythic.spatial.Vector2i

data class DeferredRenderer(
    val frameBuffer: FrameBuffer,
    var albedo: Texture,
    var position: Texture,
    var normal: Texture,
) {
  fun dispose() {
    frameBuffer.dispose()
    albedo.dispose()
    position.dispose()
    normal.dispose()
  }
}

fun newFrameBufferTexture(dimensions: Vector2i, attachment: Int): Texture {
  val texture = Texture(dimensions.x, dimensions.y, TextureAttributes(
      format = TextureFormat.rgba, // alpha isn't always used but some devices prefer 4x float frame buffers
      storageUnit = TextureStorageUnit.float,
      smooth = false,
  ))
  glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, texture.id, 0)
  return texture
}

fun newDeferredRenderer(dimensions: Vector2i): DeferredRenderer {
  val frameBuffer = FrameBuffer()
  val albedo = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT0)
  val position = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT0)
  val normal = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT0)
  val attachments = BufferUtils.createIntBuffer(3)
  attachments.put(GL_COLOR_ATTACHMENT0)
  attachments.put(GL_COLOR_ATTACHMENT1)
  attachments.put(GL_COLOR_ATTACHMENT2)
  glDrawBuffers(attachments)
  return DeferredRenderer(
      frameBuffer = frameBuffer,
      albedo = albedo,
      position = position,
      normal = normal,
  )
}
