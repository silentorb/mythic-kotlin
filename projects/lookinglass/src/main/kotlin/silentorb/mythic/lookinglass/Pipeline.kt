package silentorb.mythic.lookinglass

import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import silentorb.mythic.drawing.getStaticCanvasDependencies
import silentorb.mythic.glowing.*
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

//  renderer.buffers.color.buffer = renderer.buffers.color.buffer
//      ?: BufferUtils.createByteBuffer(dimensions.x * dimensions.y * 3)
//
//  renderer.buffers.depth.buffer = renderer.buffers.depth.buffer
//      ?: BufferUtils.createFloatBuffer(dimensions.x * dimensions.y)
  renderer.deferred = updateDeferredShading(renderer, dimensions)
  renderer.glow.state.viewport = Vector4i(0, 0, dimensions.x, dimensions.y)
  renderer.glow.state.depthEnabled = true
  renderer.offscreenBuffer.frameBuffer.activate()
  renderer.glow.operations.clearScreen()
}

//fun applyRenderBuffer(renderer: Renderer, dimensions: Vector2i) {
//  updateTextureBuffer(dimensions, renderer.buffers.color) {
//    TextureAttributes(
//        repeating = false,
//        smooth = false,
//        storageUnit = TextureStorageUnit.unsignedByte
//    )
//  }
//
//  updateTextureBuffer(dimensions, renderer.buffers.depth) {
//    TextureAttributes(
//        repeating = false,
//        smooth = false,
//        storageUnit = TextureStorageUnit.float,
//        format = TextureFormat.depth
//    )
//  }
//
//  renderer.shaders.screenTexture.activate(Vector2(1f))
//  val canvasDependencies = getStaticCanvasDependencies()
//  activateTextures(listOf(renderer.buffers.color.texture!!, renderer.buffers.depth.texture!!))
//  canvasDependencies.meshes.image.draw(DrawMethod.triangleFan)
//}

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

fun activateOffscreenRendering(renderer: SceneRenderer) {
  val glow = renderer.renderer.glow
  val offscreenBuffer = renderer.renderer.offscreenBuffer
  val dimensions = Vector2i(offscreenBuffer.colorTexture.width, offscreenBuffer.colorTexture.height)
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

fun applyFilters(renderer: SceneRenderer, filters: List<ScreenFilter>) {
  globalState.cullFaces = false
  globalState.viewport = renderer.viewport
  globalState.depthEnabled = false

  val offscreenBuffer = renderer.renderer.offscreenBuffer
  activateTextures(listOf(offscreenBuffer.colorTexture, offscreenBuffer.depthTexture!!))

  for (filter in filters) {
    applyFrameBufferTexture(renderer, filter)
  }

  if (renderer.renderer.multisampler != null) {
    renderer.renderer.multisampler.frameBuffer.activateDraw()
  } else {
    globalState.setFrameBuffer(0)
  }

  applyOffscreenBuffer(renderer.offscreenBuffer, renderer.windowInfo.dimensions, false)
}

fun getScreenScale(renderer: SceneRenderer): Vector2 {
  val dimensions = renderer.windowInfo.dimensions
  return Vector2(dimensions.x.toFloat(), dimensions.y.toFloat()) / renderer.viewport.zw.toVector2()
}
