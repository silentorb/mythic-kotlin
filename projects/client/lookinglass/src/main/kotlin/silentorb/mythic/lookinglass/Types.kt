package silentorb.mythic.lookinglass

import silentorb.mythic.breeze.AnimationName
import silentorb.mythic.breeze.Bones
import silentorb.mythic.breeze.SkeletonAnimation
import silentorb.mythic.drawing.createDrawingEffects
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.meshes.VertexSchemas
import silentorb.mythic.lookinglass.meshes.createVertexSchemas
import silentorb.mythic.lookinglass.shading.*
import silentorb.mythic.lookinglass.texturing.AsyncTextureLoader
import silentorb.mythic.lookinglass.texturing.DeferredTexture
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
    override var width: Int = 800,
    override var height: Int = 600,
    override var fullscreen: Boolean = false,
    override var windowedFullscreen: Boolean = false, // Whether fullscreen uses windowed fullscreen
    override var vsync: Boolean = true,
    override var multisamples: Int = 0,
    var depthOfField: Boolean = false,
    var textureAntialiasing: TextureAntialiasing = TextureAntialiasing.trilinear
) : PlatformDisplayConfig

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
  var renderColor: ByteTextureBuffer = ByteTextureBuffer()
  var renderDepth: FloatTextureBuffer = FloatTextureBuffer()
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
