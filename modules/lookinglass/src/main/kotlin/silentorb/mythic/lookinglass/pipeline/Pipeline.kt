package silentorb.mythic.lookinglass.pipeline

import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import silentorb.mythic.drawing.getStaticCanvasDependencies
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.*
import silentorb.mythic.lookinglass.deferred.updateDeferredShading
import silentorb.mythic.platforming.WindowInfo
import silentorb.mythic.spatial.*

fun newFrameBufferTexture(dimensions: Vector2i, attachment: Int, format: TextureFormat,
                          storage: TextureStorageUnit): Texture {
  val texture = newTexture(dimensions.x, dimensions.y, TextureAttributes(
      format = format,
      storageUnit = storage,
      smooth = false,
  ))
  glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, texture.id, 0)
  return texture
}

fun prepareRender(renderer: Renderer, windowInfo: WindowInfo) {
  val dimensions = windowInfo.dimensions
  if (renderer.multisampler != null) {
    renderer.multisampler.frameBuffer.activateDraw()
  }

  // Deferred depth from offscreenBuffer so offscreenBuffer needs to be updated first
  val offscreenBuffer = getOrCreateOffscreenBuffer(renderer, windowInfo.dimensions, true)
  renderer.deferred = updateDeferredShading(renderer, dimensions)

  renderer.glow.state.viewport = Vector4i(0, 0, dimensions.x, dimensions.y)
  renderer.glow.state.depthEnabled = true
  offscreenBuffer.frameBuffer.activate()
  renderer.glow.operations.clearScreen()
}

fun finishRender(renderer: Renderer, windowInfo: WindowInfo) {
  if (renderer.multisampler != null) {
    val width = windowInfo.dimensions.x
    val height = windowInfo.dimensions.y
    renderer.glow.state.drawFramebuffer = 0
    renderer.multisampler.frameBuffer.activateRead()
    glDrawBuffer(GL_BACK) // Set the back buffer as the draw buffer
    glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST)
  }

  // This may not matter for normal rendering, but if the current read framebuffer
  // is not set back to the default backbuffer and is still pointing to an offscreen buffer when swapping,
  // screen capture applications will grab the intermiate image in the offscreen buffer
  // instead of the final image in the back buffer.
  // Also resetting the write buffer here for good measure.
  globalState.setFrameBuffer(0)
}

fun getOrCreateOffscreenBuffer(renderer: Renderer, dimensions: Vector2i, withDepth: Boolean): OffscreenBuffer {
  val existing = renderer.offscreenBuffer

  // Eventually may add checking to see if withDepth changed as well
  return if (existing != null && existing.dimensions == dimensions)
    existing
  else { // Either new or dimensions changed
    val frameBufferId = existing?.frameBuffer?.id
    val isRead = globalState.readFramebuffer == frameBufferId
    val isWrite = globalState.drawFramebuffer == frameBufferId
    if (existing != null) {
      disposeOffscreenBuffer(existing)
    }
    val buffer = prepareScreenFrameBuffer(dimensions.x, dimensions.y, withDepth)
    renderer.offscreenBuffer = buffer
    if (isRead) buffer.frameBuffer.activateRead()
    if (isWrite) buffer.frameBuffer.activateDraw()
    buffer
  }
}

fun getOrCreateOffscreenBuffer(renderer: SceneRenderer): OffscreenBuffer =
    getOrCreateOffscreenBuffer(renderer.renderer, renderer.windowInfo.dimensions, true)

fun activateOffscreenRendering(renderer: SceneRenderer) {
  val glow = renderer.renderer.glow
  val offscreenBuffer = getOrCreateOffscreenBuffer(renderer)
  glow.state.setFrameBuffer(offscreenBuffer.frameBuffer.id)
  glow.state.viewport = Vector4i(0, 0, renderer.viewport.z, renderer.viewport.w)
}

fun activateDirectRendering(renderer: SceneRenderer) {
  val glow = renderer.renderer.glow
  glow.state.viewport = renderer.viewport
}

fun prepareRender(renderer: SceneRenderer, scene: Scene): List<ScreenFilter> {
  val filters = getDisplayConfigFilters(renderer.renderer.options).plus(scene.filters)
  globalState.lineThickness = 2f
  return filters
}

fun applyFrameBufferTexture(renderer: SceneRenderer, filter: ScreenFilter) {
  val canvasDependencies = getStaticCanvasDependencies()
  val scale = getScreenScale(renderer)
  filter(renderer.renderer.shaders, scale)
  canvasDependencies.meshes.imageGl.draw(DrawMethod.triangleFan)
}

fun applyRenderedBuffers(renderer: Renderer, windowInfo: WindowInfo) {
  val offscreenBuffer = renderer.offscreenBuffer
  if (offscreenBuffer != null) {
    if (renderer.multisampler != null) {
      renderer.multisampler.frameBuffer.activateDraw()
    } else {
      globalState.setFrameBuffer(0)
    }

    applyOffscreenBuffer(offscreenBuffer, windowInfo.dimensions, false)
  }
}

fun applyFilters(renderer: SceneRenderer, filters: List<ScreenFilter>) {
  globalState.cullFaces = false
  globalState.viewport = renderer.viewport
  globalState.depthEnabled = false

  val offscreenBuffer = renderer.renderer.offscreenBuffer
  if (offscreenBuffer != null) {
    activateTextures(listOf(offscreenBuffer.colorTexture, offscreenBuffer.depthTexture!!))

    for (filter in filters) {
      applyFrameBufferTexture(renderer, filter)
    }
    applyRenderedBuffers(renderer.renderer, renderer.windowInfo)
  }
}

fun getScreenScale(renderer: SceneRenderer): Vector2 {
  val dimensions = renderer.windowInfo.dimensions
  return Vector2(dimensions.x.toFloat(), dimensions.y.toFloat()) / renderer.viewport.zw.toVector2()
}
