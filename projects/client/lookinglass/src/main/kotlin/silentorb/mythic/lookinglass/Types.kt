package silentorb.mythic.lookinglass

import silentorb.mythic.breeze.AnimationName
import silentorb.mythic.breeze.Bones
import silentorb.mythic.breeze.SkeletonAnimation
import silentorb.mythic.drawing.createDrawingEffects
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.meshes.VertexSchemas
import silentorb.mythic.lookinglass.meshes.createVertexSchemas
import silentorb.mythic.lookinglass.shading.*
import silentorb.mythic.lookinglass.texturing.DynamicTextureLibrary
import silentorb.mythic.platforming.PlatformDisplayConfig
import silentorb.mythic.scenery.ArmatureName
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.typography.FontSet
import java.nio.ByteBuffer
import java.nio.FloatBuffer

enum class TextureAntialiasing {
  none,
  bilinear,
  trilinear
}

data class DisplayConfig(
    val width: Int = 800,
    val height: Int = 600,
    val fullscreen: Boolean = false,
    val windowedFullscreen: Boolean = false, // Whether fullscreen uses windowed fullscreen
    val vsync: Boolean = true,
    val multisamples: Int = 0,
    val depthOfField: Boolean = false,
    val textureAntialiasing: TextureAntialiasing = TextureAntialiasing.trilinear
)

fun toPlatformDisplayConfig(display: DisplayConfig) =
    PlatformDisplayConfig(
        width = display.width,
        height = display.height,
        fullscreen = display.fullscreen,
        windowedFullscreen = display.windowedFullscreen,
        vsync = display.vsync,
        multisamples = display.multisamples
    )

data class Multisampler(
    val framebuffer: Framebuffer,
    val renderbuffer: Renderbuffer
)

typealias AnimationDurationMap = Map<ArmatureName, Map<AnimationName, Float>>

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

data class Renderer(
    val glow: Glow,
    val config: DisplayConfig,
    val multisampler: Multisampler?,
    val meshes: ModelMeshMap,
    val armatures: Map<ArmatureName, Armature>,
    val vertexSchemas: VertexSchemas,
    val fonts: List<FontSet>,
    val offscreenBuffers: List<OffscreenBuffer>
) {
  val renderColor: ByteTextureBuffer = ByteTextureBuffer()
  val renderDepth: FloatTextureBuffer = FloatTextureBuffer()
  val uniformBuffers = UniformBuffers(
      instance = UniformBuffer(instanceBufferSize),
      scene = UniformBuffer(sceneBufferSize),
      section = UniformBuffer(sectionBufferSize),
      bone = UniformBuffer(boneBufferSize)
  )
  val shaders: Shaders = createShaders()
  val shaderCache: ShaderCache = mutableMapOf()
  val getShader = getCachedShader(uniformBuffers, shaderCache)
  val drawing = createDrawingEffects()
  val textures: DynamicTextureLibrary = mutableMapOf()
  val dynamicMesh = MutableSimpleMesh(vertexSchemas.flat)
}

//fun updateOffscreenBufferAllocations(renderer: Renderer, oldConfig: DisplayConfig?) {
//  val dimensionsChanged = oldConfig == null || renderer.config.width != oldConfig.width || renderer.config.height != oldConfig.height
//  if (renderer.config.multisamples == 0) {
//    val multisampler = renderer.multisampler
//    if (multisampler != null) {
//      multisampler.framebuffer.dispose()
//      multisampler.renderbuffer.dispose()
//      renderer.multisampler = null
//    }
//  } else if (renderer.config.multisamples != oldConfig?.multisamples || dimensionsChanged) {
//    renderer.multisampler = createMultiSampler(renderer.glow, renderer.config)
//  }
//}

fun emptyRenderer(config: DisplayConfig): Renderer =
    Renderer(
        glow = Glow(),
        config = config,
        meshes = mutableMapOf(),
        armatures = mapOf(),
        vertexSchemas = createVertexSchemas(),
        fonts = listOf(),
        multisampler = null,
        offscreenBuffers = listOf()
    )
