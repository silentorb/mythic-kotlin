package silentorb.mythic.lookinglass

import silentorb.mythic.scenery.Light
import silentorb.mythic.scenery.Scene
import silentorb.mythic.scenery.TextureName
import silentorb.mythic.breeze.Bones
import silentorb.mythic.breeze.SkeletonAnimation
import silentorb.mythic.drawing.*
import silentorb.mythic.ent.Id
import silentorb.mythic.glowing.*
import silentorb.mythic.platforming.PlatformDisplayConfig
import silentorb.mythic.platforming.WindowInfo
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.typography.*
import org.joml.Vector2i
import org.joml.Vector4i
import org.joml.div
import org.joml.times
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE
import org.lwjgl.opengl.GL32.glTexImage2DMultisample
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.meshes.createVertexSchemas
import silentorb.mythic.lookinglass.shading.*
import silentorb.mythic.lookinglass.texturing.*
import silentorb.mythic.scenery.ArmatureName
import java.nio.ByteBuffer
import java.nio.FloatBuffer

enum class TextureAntialiasing {
  none,
  bilinear,
  trilinear
}

data class DisplayConfig(
    override var width: Int = 800,
    override var height: Int = 600,
    override var fullscreen: Boolean = false,
    override var windowedFullscreen: Boolean = false, // Whether fullscreen uses windowed fullscreen
    override var vsync: Boolean = true,
    override var multisamples: Int = 0,
    var depthOfField: Boolean = false,
    var textureAntialiasing: TextureAntialiasing = TextureAntialiasing.trilinear
) : PlatformDisplayConfig

fun gatherEffectsData(dimensions: Vector2i, lights: List<Light>, cameraEffectsData: CameraEffectsData): EffectsData {
  return EffectsData(
      cameraEffectsData,
      Matrix().ortho(0.0f, dimensions.x.toFloat(), 0.0f, dimensions.y.toFloat(), 0f, 100f),
      lights
  )
}

data class SectorMesh(
    val id: Id,
    val mesh: SimpleMesh,
    val textureIndex: List<TextureName>
)

data class WorldMesh(
    val sectors: List<SectorMesh>
)

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
    else -> throw Error("Not supported")
  }
}

//fun mapGameSceneRenderers(renderer: Renderer, scenes: List<GameScene>, windowInfo: WindowInfo): List<GameSceneRenderer> {
//  val viewports = getPlayerViewports(scenes.size, windowInfo.dimensions).iterator()
//  return scenes.map {
//    val viewport = viewports.next()
//    GameSceneRenderer(it, renderer.createSceneRenderer(it.main, viewport))
//  }
//}

const val defaultTextureScale = 1f

data class Multisampler(
    val framebuffer: Framebuffer,
    val renderbuffer: Renderbuffer
)

fun createMultiSampler(glow: Glow, config: PlatformDisplayConfig): Multisampler {
  val texture = Texture(config.width, config.height, null, { width: Int, height: Int, buffer: FloatBuffer? ->
    glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, config.multisamples, GL_RGB, width, height, true)
  }, TextureTarget.multisample)

  val framebuffer: Framebuffer
  val renderbuffer: Renderbuffer

  framebuffer = Framebuffer()
  glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, texture.id, 0)

  renderbuffer = Renderbuffer()
  glRenderbufferStorageMultisample(GL_RENDERBUFFER, config.multisamples, GL_DEPTH24_STENCIL8, config.width, config.height);
  glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderbuffer.id);

  checkError("Initializing multisampled framebuffer.")

  val status = glCheckFramebufferStatus(GL_DRAW_FRAMEBUFFER)
  if (status != GL_FRAMEBUFFER_COMPLETE)
    throw Error("Error creating multisample framebuffer.")

  return Multisampler(
      framebuffer = framebuffer,
      renderbuffer = renderbuffer
  )
}

typealias AnimationDurationMap = Map<ArmatureName, Map<AnimationName, Float>>

fun mapAnimationDurations(armatures: Map<ArmatureName, Armature>): AnimationDurationMap =
    armatures
        .mapValues { (_, armature) ->
          armature.animations.mapValues { it.value.duration }
        }

typealias AnimationMap = Map<AnimationName, SkeletonAnimation>

typealias SocketMap = Map<String, Int>

data class Armature(
    val id: ArmatureName,
    val bones: Bones,
    val animations: AnimationMap,
    val transforms: List<Matrix>,
    val sockets: SocketMap = mapOf()
)

data class ByteTextureBuffer(
    var texture: Texture? = null,
    var buffer: ByteBuffer? = null
)

data class FloatTextureBuffer(
    var texture: Texture? = null,
    var buffer: FloatBuffer? = null
)

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

fun textureAttributesFromConfig(config: DisplayConfig) =
    TextureAttributes(
        repeating = true,
        mipmap = config.textureAntialiasing == TextureAntialiasing.trilinear,
        smooth = config.textureAntialiasing != TextureAntialiasing.none,
        storageUnit = TextureStorageUnit.unsigned_byte
    )

fun gatherChildLights(meshes: ModelMeshMap, groups: ElementGroups): List<Light> {
  return groups.flatMap { group ->
    group.meshes.flatMap { meshElement ->
      val mesh = meshes[meshElement.mesh]!!
      mesh.lights.map { light ->
        light.copy(
            position = light.position.transform(meshElement.transform)
        )
      }
    }
  }
}

fun gatherSceneLights(meshes: ModelMeshMap, scene: GameScene): List<Light> {
  return scene.lights
      .plus(gatherChildLights(meshes, scene.opaqueElementGroups))
}

class Renderer(
    val config: DisplayConfig,
    fontSource: () -> List<FontSet>,
    val lightingConfig: LightingConfig,
    textures: List<DeferredTexture>
) {
  val glow = Glow()
  var renderColor: ByteTextureBuffer = ByteTextureBuffer()
  var renderDepth: FloatTextureBuffer = FloatTextureBuffer()
  val uniformBuffers = UniformBuffers(
      instance = UniformBuffer(instanceBufferSize),
      scene = UniformBuffer(sceneBufferSize),
      section = UniformBuffer(sectionBufferSize),
      bone = UniformBuffer(boneBufferSize)
  )
  val vertexSchemas = createVertexSchemas()
  val shaders: Shaders = createShaders()
  val shaderCache: ShaderCache = mutableMapOf()
  val getShader = getCachedShader(uniformBuffers, shaderCache)
  val drawing = createDrawingEffects()
  val meshes: ModelMeshMap
  val armatures: Map<ArmatureName, Armature>
  val animationDurations: AnimationDurationMap
  val textures: DynamicTextureLibrary = mutableMapOf()
  val textureLoader = AsyncTextureLoader(textures)
  val offscreenBuffers: List<OffscreenBuffer> = (0..0).map {
    prepareScreenFrameBuffer(config.width, config.height, true)
  }
  val multisampler: Multisampler?
  val dynamicMesh = MutableSimpleMesh(vertexSchemas.flat)
  val fonts = fontSource()

  init {
    glow.state.clearColor = Vector4(0f, 0f, 0f, 1f)
    multisampler = if (config.multisamples == 0)
      null
    else
      createMultiSampler(glow, config)

    val (loadedMeshes, loadedArmatures) = createMeshes(vertexSchemas)
    meshes = loadedMeshes
    armatures = loadedArmatures.associate { Pair(it.id, it) }
    animationDurations = mapAnimationDurations(armatures)
  }

  fun updateShaders(lights: List<Light>, dimensions: Vector2i, cameraEffectsData: CameraEffectsData) {
    val effectsData = gatherEffectsData(dimensions, lights, cameraEffectsData)
    updateLights(lightingConfig, effectsData.lights, uniformBuffers.section)
    uniformBuffers.scene.load(createSceneBuffer(effectsData))
  }

  fun createSceneRenderer(scene: Scene, viewport: Vector4i): SceneRenderer {
    val dimensions = Vector2i(viewport.z, viewport.w)
    val cameraEffectsData = createCameraEffectsData(dimensions, scene.camera)
    updateShaders(scene.lights, dimensions, cameraEffectsData)
    return SceneRenderer(viewport, this, scene.camera, cameraEffectsData)
  }

  fun prepareRender(windowInfo: WindowInfo) {
    updateAsyncTextureLoading(textureLoader, textures)
    if (multisampler != null) {
      multisampler.framebuffer.activateDraw()
    }
    glow.state.viewport = Vector4i(0, 0, windowInfo.dimensions.x, windowInfo.dimensions.y)
    glow.state.depthEnabled = true
    glow.operations.clearScreen()
    renderColor.buffer = renderColor.buffer
        ?: BufferUtils.createByteBuffer(windowInfo.dimensions.x * windowInfo.dimensions.y * 3)

    renderDepth.buffer = renderDepth.buffer
        ?: BufferUtils.createFloatBuffer(windowInfo.dimensions.x * windowInfo.dimensions.y)
  }

  fun applyRenderBuffer(dimensions: Vector2i) {
    updateTextureBuffer(dimensions, renderColor) {
      TextureAttributes(
          repeating = false,
          smooth = false,
          storageUnit = TextureStorageUnit.unsigned_byte
      )
    }

    updateTextureBuffer(dimensions, renderDepth) {
      TextureAttributes(
          repeating = false,
          smooth = false,
          storageUnit = TextureStorageUnit.float,
          format = TextureFormat.depth
      )
    }

    shaders.screenTexture.activate()
    val canvasDependencies = getStaticCanvasDependencies()
    activateTextures(listOf(renderColor.texture!!, renderDepth.texture!!))
    canvasDependencies.meshes.image.draw(DrawMethod.triangleFan)
  }

  fun finishRender(windowInfo: WindowInfo) {
    if (multisampler != null) {
      val width = windowInfo.dimensions.x
      val height = windowInfo.dimensions.y
      glow.state.drawFramebuffer = 0
      glow.state.readFramebuffer = multisampler.framebuffer.id
      glDrawBuffer(GL_BACK)                       // Set the back buffer as the draw buffer
      glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST)
    }
  }

}

fun createSceneRenderer(renderer: Renderer, scene: GameScene, viewport: Vector4i): SceneRenderer {
  val minimalScene = scene.main.copy(
      lights = gatherSceneLights(renderer.meshes, scene)
  )
  return renderer.createSceneRenderer(minimalScene, viewport)
}

fun rasterizeCoordinates(position: Vector3, cameraEffectsData: CameraEffectsData, dimensions: Vector2i): Vector2 {
  val modelTransform = Matrix()
      .translate(position)

  val transform2 = cameraEffectsData.transform * modelTransform
  val i = transform2.transform(Vector4(0f, 0f, 0f, 1f))
  return Vector2(((i.x + 1) / 2) * dimensions.x, (1 - ((i.y + 1) / 2)) * dimensions.y)
}

fun createCanvas(renderer: Renderer, dimensions: Vector2i): Canvas {
  val unitScaling = getUnitScaling(dimensions)
  return Canvas(
      renderer.drawing,
      unitScaling,
      renderer.fonts,
      dimensions
  )
}
