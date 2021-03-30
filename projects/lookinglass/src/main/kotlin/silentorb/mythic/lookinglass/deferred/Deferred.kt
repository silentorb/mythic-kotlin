package silentorb.mythic.lookinglass.deferred

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.ShadingMode
import silentorb.mythic.lookinglass.Renderer
import silentorb.mythic.lookinglass.SceneRenderer
import silentorb.mythic.lookinglass.applyFrameBufferTexture
import silentorb.mythic.spatial.Vector2i

data class DeferredShading(
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

fun newDeferredShading(dimensions: Vector2i): DeferredShading {
  val frameBuffer = FrameBuffer()
  val albedo = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT0)
  val position = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT0)
  val normal = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT0)
  val attachments = BufferUtils.createIntBuffer(3)
  attachments.put(GL_COLOR_ATTACHMENT0)
  attachments.put(GL_COLOR_ATTACHMENT1)
  attachments.put(GL_COLOR_ATTACHMENT2)
  glDrawBuffers(attachments)
  return DeferredShading(
      frameBuffer = frameBuffer,
      albedo = albedo,
      position = position,
      normal = normal,
  )
}

fun updateDeferredShading(renderer: Renderer, dimensions: Vector2i): DeferredShading? {
  val deferred = renderer.deferred
  return if (renderer.options.shadingMode == ShadingMode.deferred) {
    if (deferred == null || dimensions.x != deferred.albedo.width || dimensions.y != deferred.albedo.height)
      newDeferredShading(dimensions)
    else
      deferred
  } else
    null
}

fun processDeferredShading(renderer: SceneRenderer) {
  val deferred = renderer.renderer.deferred!!
  deferred.frameBuffer.activateRead()
  applyFrameBufferTexture(renderer) { shaders, scale -> shaders.deferredShading.activate(scale) }
}
