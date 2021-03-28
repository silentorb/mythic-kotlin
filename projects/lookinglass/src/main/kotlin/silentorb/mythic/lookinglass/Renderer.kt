package silentorb.mythic.lookinglass

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE
import org.lwjgl.opengl.GL32.glTexImage2DMultisample
import silentorb.mythic.drawing.Canvas
import silentorb.mythic.drawing.getStaticCanvasDependencies
import silentorb.mythic.drawing.getUnitScaling
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.deferred.DeferredRenderer
import silentorb.mythic.lookinglass.deferred.newDeferredRenderer
import silentorb.mythic.lookinglass.shading.EffectsData
import silentorb.mythic.lookinglass.shading.createSceneBuffer
import silentorb.mythic.lookinglass.texturing.DynamicTextureLibrary
import silentorb.mythic.platforming.WindowInfo
import silentorb.mythic.scenery.Light
import silentorb.mythic.scenery.LightingConfig
import silentorb.mythic.spatial.*
import java.nio.FloatBuffer

fun gatherEffectsData(dimensions: Vector2i, lights: List<Light>, cameraEffectsData: CameraEffectsData): EffectsData {
  return EffectsData(
      cameraEffectsData,
      toMatrix(MutableMatrix().ortho(0.0f, dimensions.x.toFloat(), 0.0f, dimensions.y.toFloat(), 0f, 100f)),
      lights
  )
}

fun getPlayerViewports(playerCount: Int, dimensions: Vector2i): List<Vector4i> {
  val half = dimensions / 2
  return when (playerCount) {
    0, 1 -> listOf(Vector4i(0, 0, dimensions.x, dimensions.y))
    2 -> listOf(
        Vector4i(0, 0, half.x, dimensions.y),
        Vector4i(half.x, 0, half.x, dimensions.y)
    )
    3 -> listOf(
        Vector4i(0, 0, dimensions.x / 2, dimensions.y),
        Vector4i(half.x, half.y, half.x, half.y),
        Vector4i(half.x, 0, half.x, half.y)
    )
    4 -> listOf(
        Vector4i(0, 0, half.x, half.y),
        Vector4i(0, half.y, half.x, half.y),
        Vector4i(half.x, half.y, half.x, half.y),
        Vector4i(half.x, 0, half.x, half.y)
    )
    else -> throw Error("Not supported")
  }
}

fun createMultiSampler(glow: Glow, width: Int, height: Int, multisamples: Int): Multisampler {
  val texture = Texture(width, height, null, { width: Int, height: Int, buffer: FloatBuffer? ->
    glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, multisamples, GL_RGB, width, height, true)
  }, TextureTarget.multisample)

  val framebuffer = FrameBuffer()
  glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, texture.id, 0)

  val renderbuffer = Renderbuffer()
  glRenderbufferStorageMultisample(GL_RENDERBUFFER, multisamples, GL_DEPTH24_STENCIL8, width, height);
  glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderbuffer.id);

  checkError("Initializing multisampled framebuffer.")

  val status = glCheckFramebufferStatus(GL_DRAW_FRAMEBUFFER)
  if (status != GL_FRAMEBUFFER_COMPLETE)
    throw Error("Error creating multisample framebuffer.")

  return Multisampler(
      frameBuffer = framebuffer,
      renderbuffer = renderbuffer
  )
}

fun updateTextureBuffer(dimensions: Vector2i, buffer: ByteTextureBuffer, attributes: () -> TextureAttributes) {
  if (buffer.texture == null) {
    buffer.texture = Texture(dimensions.x, dimensions.y, attributes())
  }
  buffer.texture!!.update(buffer.buffer!!)
}

fun updateTextureBuffer(dimensions: Vector2i, buffer: FloatTextureBuffer, attributes: () -> TextureAttributes) {
  if (buffer.texture == null) {
    buffer.texture = Texture(dimensions.x, dimensions.y, attributes())
  }
  buffer.texture!!.update(buffer.buffer!!)
}

fun textureAttributesFromConfig(options: DisplayOptions) =
    TextureAttributes(
        repeating = true,
        mipmap = options.textureAntialiasing == TextureAntialiasing.trilinear,
        smooth = options.textureAntialiasing != TextureAntialiasing.none,
        storageUnit = TextureStorageUnit.unsigned_byte
    )

fun gatherChildLights(meshes: ModelMeshMap, groups: ElementGroups): List<Light> {
  return groups.flatMap { group ->
    group.meshes.flatMap { meshElement ->
      val mesh = meshes[meshElement.mesh]!!
      mesh.lights.map { light ->
        light.copy(
            offset = light.offset.transform(meshElement.transform)
        )
      }
    }
  }
}

data class GraphicState(
    val textures: DynamicTextureLibrary = mutableMapOf()
)

fun updateShaders(renderer: Renderer, lightingConfig: LightingConfig, lights: List<Light>, dimensions: Vector2i, cameraEffectsData: CameraEffectsData) {
  val effectsData = gatherEffectsData(dimensions, lights, cameraEffectsData)
  updateLights(lightingConfig, effectsData.lights, renderer.uniformBuffers.section)
  renderer.uniformBuffers.scene.load(createSceneBuffer(effectsData))
}

fun createSceneRenderer(renderer: Renderer, windowInfo: WindowInfo, scene: Scene, viewport: Vector4i): SceneRenderer {
  val dimensions = Vector2i(viewport.z, viewport.w)
  val cameraEffectsData = createCameraEffectsData(dimensions, scene.camera)
  updateShaders(renderer, scene.lightingConfig, scene.lights, dimensions, cameraEffectsData)
  return SceneRenderer(viewport, renderer, scene.camera, cameraEffectsData, windowInfo)
}

fun gatherSceneLights(meshes: ModelMeshMap, scene: GameScene): List<Light> {
  return scene.main.lights
//      .plus(gatherChildLights(meshes, scene.opaqueElementGroups))
}

fun createSceneRenderer(renderer: Renderer, windowInfo: WindowInfo, scene: GameScene, viewport: Vector4i): SceneRenderer {
  val minimalScene = scene.main.copy(
      lights = gatherSceneLights(renderer.meshes, scene)
  )
  return createSceneRenderer(renderer, windowInfo, minimalScene, viewport)
}

fun updateDeferredRenderer(renderer: Renderer, dimensions: Vector2i): DeferredRenderer? {
  val deferred = renderer.deferred
  return if (renderer.options.lightingMode == LightingMode.deferred) {
    if (deferred == null || dimensions.x != deferred.albedo.width || dimensions.y != deferred.albedo.height)
      newDeferredRenderer(dimensions)
    else
      deferred
  } else
    null
}

fun prepareRender(renderer: Renderer, windowInfo: WindowInfo) {
  val dimensions = windowInfo.dimensions
  if (renderer.multisampler != null) {
    renderer.multisampler.frameBuffer.activateDraw()
  }
  renderer.deferred = updateDeferredRenderer(renderer, dimensions)
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
        storageUnit = TextureStorageUnit.unsigned_byte
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
    renderer.glow.state.readFramebuffer = renderer.multisampler.frameBuffer.id
    glDrawBuffer(GL_BACK)                       // Set the back buffer as the draw buffer
    glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST)
  }
}

fun createCanvas(renderer: Renderer, custom: Map<String, Any>, dimensions: Vector2i): Canvas {
  val unitScaling = getUnitScaling(dimensions)
  return Canvas(
      renderer.drawing,
      unitScaling,
      renderer.fonts,
      custom,
      dimensions
  )
}

fun <T> renderContainer(renderer: Renderer, windowInfo: WindowInfo, action: () -> T): T {
  prepareRender(renderer, windowInfo)
  val result = action()
  finishRender(renderer, windowInfo)
  return result
}
