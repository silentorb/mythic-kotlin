package silentorb.mythic.lookinglass

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.GL_BACK
import org.lwjgl.opengl.GL11.glDrawBuffer
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.glBlitFramebuffer
import silentorb.mythic.drawing.getStaticCanvasDependencies
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.deferred.updateDeferredShading
import silentorb.mythic.platforming.WindowInfo
import silentorb.mythic.spatial.*

fun prepareRender(renderer: Renderer, windowInfo: WindowInfo) {
  val dimensions = windowInfo.dimensions
  if (renderer.multisampler != null) {
    renderer.multisampler.frameBuffer.activateDraw()
  }
  renderer.deferred = updateDeferredShading(renderer, dimensions)
  renderer.glow.state.viewport = Vector4i(0, 0, dimensions.x, dimensions.y)
  renderer.glow.state.depthEnabled = true
  renderer.glow.operations.clearScreen()
  renderer.buffers.color.buffer = renderer.buffers.color.buffer
      ?: BufferUtils.createByteBuffer(dimensions.x * dimensions.y * 3)

  renderer.buffers.depth.buffer = renderer.buffers.depth.buffer
      ?: BufferUtils.createFloatBuffer(dimensions.x * dimensions.y)
}

fun applyRenderBuffer(renderer: Renderer, dimensions: Vector2i) {
  updateTextureBuffer(dimensions, renderer.buffers.color) {
    TextureAttributes(
        repeating = false,
        smooth = false,
        storageUnit = TextureStorageUnit.unsignedByte
    )
  }

  updateTextureBuffer(dimensions, renderer.buffers.depth) {
    TextureAttributes(
        repeating = false,
        smooth = false,
        storageUnit = TextureStorageUnit.float,
        format = TextureFormat.depth
    )
  }

  renderer.shaders.screenTexture.activate(Vector2(1f))
  val canvasDependencies = getStaticCanvasDependencies()
  activateTextures(listOf(renderer.buffers.color.texture!!, renderer.buffers.depth.texture!!))
  canvasDependencies.meshes.image.draw(DrawMethod.triangleFan)
}

fun finishRender(renderer: Renderer, windowInfo: WindowInfo) {
  if (renderer.multisampler != null) {
    val width = windowInfo.dimensions.x
    val height = windowInfo.dimensions.y
    renderer.glow.state.drawFramebuffer = 0
    renderer.multisampler.frameBuffer.activateRead()
    glDrawBuffer(GL_BACK)                       // Set the back buffer as the draw buffer
    glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST)
  }
}

fun activeOffscreenRendering(renderer: SceneRenderer) {
  val glow = renderer.renderer.glow
  val offscreenBuffer = renderer.renderer.offscreenBuffers.first()
  val dimensions = Vector2i(offscreenBuffer.colorTexture.width, offscreenBuffer.colorTexture.height)
  glow.state.setFrameBuffer(offscreenBuffer.frameBuffer.id)
  glow.state.viewport = Vector4i(0, 0, renderer.viewport.z, renderer.viewport.w)
}

fun activeDirectRendering(renderer: SceneRenderer) {
  val glow = renderer.renderer.glow
  glow.state.viewport = renderer.viewport
}

fun prepareRender(renderer: SceneRenderer, scene: Scene): List<ScreenFilter> {
  val filters = getDisplayConfigFilters(renderer.renderer.options).plus(scene.filters)
  globalState.lineThickness = 2f
  return filters
}

fun applyFilters(renderer: SceneRenderer, filters: List<ScreenFilter>) {
  globalState.cullFaces = false
  globalState.viewport = renderer.viewport

  val offscreenBuffer = renderer.renderer.offscreenBuffers.first()
  activateTextures(listOf(offscreenBuffer.colorTexture, offscreenBuffer.depthTexture!!))

  for (filter in filters.dropLast(1)) {
//      globalState.setFrameBuffer(renderer.renderer.offscreenBuffers.first().framebuffer.id)
    applyFrameBufferTexture(renderer, filter)
  }

  if (renderer.renderer.multisampler != null) {
    renderer.renderer.multisampler.frameBuffer.activateDraw()
  }
  else {
    globalState.setFrameBuffer(0)
  }

  if (filters.any()) {
    applyFrameBufferTexture(renderer, filters.last())
  }
}

fun applyFrameBufferTexture(renderer: SceneRenderer, filter: ScreenFilter) {
  val canvasDependencies = getStaticCanvasDependencies()
  val dimensions = renderer.windowInfo.dimensions
  val scale = Vector2(dimensions.x.toFloat(), dimensions.y.toFloat()) / renderer.viewport.zw.toVector2()
  filter(renderer.renderer.shaders, scale)
  canvasDependencies.meshes.imageGl.draw(DrawMethod.triangleFan)
}
