package silentorb.mythic.lookinglass

import silentorb.mythic.breeze.AnimationName
import silentorb.mythic.breeze.Bones
import silentorb.mythic.breeze.SkeletonAnimation
import silentorb.mythic.drawing.createDrawingEffects
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.deferred.DeferredShading
import silentorb.mythic.lookinglass.meshes.VertexSchemas
import silentorb.mythic.lookinglass.pipeline.OffscreenBuffer
import silentorb.mythic.lookinglass.shading.*
import silentorb.mythic.platforming.PlatformDisplayConfig
import silentorb.mythic.platforming.WindowMode
import silentorb.mythic.scenery.ArmatureName
import silentorb.mythic.scenery.MeshName
import silentorb.mythic.scenery.Shape
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.typography.FontSet
import java.nio.ByteBuffer
import java.nio.FloatBuffer

enum class TextureAntialiasing {
  none,
  bilinear,
  trilinear
}

enum class ShadingMode {
  deferred,
  forward,
  none,
}

data class DisplayOptions(
    val fullscreenResolution: Vector2i = Vector2i(1920, 1080),
    val windowedResolution: Vector2i? = null,
    val fullscreen: Boolean = false,
    val windowMode: WindowMode = WindowMode.windowed,
    val vsync: Boolean = true,
    val multisamples: Int = 0,
    val depthOfField: Boolean = false,
    val shadingMode: ShadingMode = ShadingMode.deferred,
    val textureAntialiasing: TextureAntialiasing = TextureAntialiasing.trilinear
)

fun toPlatformDisplayConfig(display: DisplayOptions) =
    PlatformDisplayConfig(
        fullscreenDimensions = display.fullscreenResolution,
        windowedDimensions = display.windowedResolution,
        windowMode = display.windowMode,
        vsync = display.vsync,
        multisamples = display.multisamples
    )

data class Multisampler(
    val frameBuffer: FrameBuffer,
    val renderBuffer: RenderBuffer
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

data class RenderBuffers(
    val color: ByteTextureBuffer = ByteTextureBuffer(),
    val depth: FloatTextureBuffer = FloatTextureBuffer(),
)

data class Renderer(
    val glow: Glow,
    val options: DisplayOptions,
    val multisampler: Multisampler?,
    val meshes: MutableMap<MeshName, ModelMesh>,
    val armatures: Map<ArmatureName, Armature>,
    val vertexSchemas: VertexSchemas,
    val fonts: List<FontSet>,
    var offscreenBuffer: OffscreenBuffer? = null,
    val textures: DynamicTextureLibrary = mutableMapOf()
) {
  val uniformBuffers = UniformBuffers(
      instance = UniformBuffer(instanceBufferSize),
      scene = UniformBuffer(sceneBufferSize),
      section = UniformBuffer(sectionBufferSize),
      bone = UniformBuffer(boneBufferSize)
  )
  val shaders: Shaders = createShaders(uniformBuffers)
  val shaderCache: ShaderCache = mutableMapOf()
  val getShader = getCachedShader(uniformBuffers, shaderCache)
  val drawing = createDrawingEffects()
  val dynamicMesh = MutableSimpleMesh(vertexSchemas.flat)
  private var internalDeferred: DeferredShading? = null

  var deferred: DeferredShading?
    get() = internalDeferred
    set(value) {
      if (internalDeferred != value) {
        internalDeferred?.dispose()
      }
      internalDeferred = value
    }
}

typealias TextureInfoMap = Map<String, TextureAttributes>

data class ResourceInfo(
    val meshShapes: Map<String, Shape>,
    val textures: TextureInfoMap,
)
