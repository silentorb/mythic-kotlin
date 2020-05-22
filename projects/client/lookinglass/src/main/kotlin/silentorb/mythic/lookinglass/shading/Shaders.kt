package silentorb.mythic.lookinglass.shading

import silentorb.mythic.glowing.*
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector4

class PerspectiveFeature(program: ShaderProgram) {
  val modelTransform = MatrixProperty(program, "modelTransform")
}

class ColoringFeature(program: ShaderProgram) {
  val colorProperty = Vector4Property(program, "uniformColor")
}

enum class UniformBufferId {
  InstanceUniform,
  SceneUniform,
  SectionUniform,
  BoneTransforms
}

fun bindUniformBuffer(id: UniformBufferId, program: ShaderProgram, buffer: UniformBuffer): UniformBufferProperty {
  val index = id.ordinal + 1
  return UniformBufferProperty(program, id.name, index, buffer)
}

class ShadingFeature(program: ShaderProgram, sectionBuffer: UniformBuffer) {
  val normalTransformProperty = MatrixProperty(program, "normalTransform")
  val glowProperty = FloatProperty(program, "glow")
  val sectionProperty = bindUniformBuffer(UniformBufferId.SectionUniform, program, sectionBuffer)
}

class SkeletonFeature(program: ShaderProgram, boneBuffer: UniformBuffer) {
  val boneTransformsProperty = bindUniformBuffer(UniformBufferId.BoneTransforms, program, boneBuffer)
}

fun populateBoneBuffer(boneBuffer: UniformBuffer, originalTransforms: List<Matrix>, transforms: List<Matrix>): UniformBuffer {
  val bytes = createBoneTransformBuffer(originalTransforms, transforms)
  boneBuffer.load(bytes)
  checkError("sending bone transforms")
  return boneBuffer
}

data class ShaderFeatureConfig(
    val pointSize: Boolean = false,
    val shading: Boolean = false,
    val skeleton: Boolean = false,
    val texture: Boolean = false,
    val instanced: Boolean = false,
    val animatedTexture: Boolean = false // Requires `texture` == true
)

data class ObjectShaderConfig(
    val transform: Matrix = Matrix.identity,
    val texture: Texture? = null,
    val color: Vector4? = null,
    val glow: Float = 0f,
    val normalTransform: Matrix? = null,
    val boneBuffer: UniformBuffer? = null,
    val textureScale: Vector2? = null,
    val nearPlaneHeight: Float? = null // Used for scaling point size
)

fun generateShaderProgram(vertexSchema: VertexSchema, featureConfig: ShaderFeatureConfig): ShaderProgram {
  val vertexShader = generateVertexCode(featureConfig)(vertexSchema)
  val fragmentShader = generateFragmentShader(featureConfig)
  return ShaderProgram(vertexShader, fragmentShader)
}

class GeneralPerspectiveShader(buffers: UniformBuffers, vertexSchema: VertexSchema, featureConfig: ShaderFeatureConfig) {
  val program = generateShaderProgram(vertexSchema, featureConfig)
  val perspective: PerspectiveFeature = PerspectiveFeature(program)
  val coloring: ColoringFeature = ColoringFeature(program)
  val textureScale = if (featureConfig.animatedTexture) Vector2Property(program, "uniformTextureScale") else null
  val instanceProperty = if (featureConfig.instanced) bindUniformBuffer(UniformBufferId.InstanceUniform, program, buffers.instance) else null
  val sceneProperty = bindUniformBuffer(UniformBufferId.SceneUniform, program, buffers.scene)
  val shading: ShadingFeature? = if (featureConfig.shading) ShadingFeature(program, buffers.section) else null
  val skeleton: SkeletonFeature? = if (featureConfig.skeleton) SkeletonFeature(program, buffers.bone) else null
  val nearPlaneHeight: FloatProperty? = if (featureConfig.pointSize) FloatProperty(program, "nearPlaneHeight") else null

  // IntelliJ will flag this use of inline as a warning, but using inline here
  // causes the JVM to optimize away the ObjectShaderConfig allocation and significantly
  // reduces the amount of objects created each frame.
  inline fun activate(config: ObjectShaderConfig) {
    program.activate()

    perspective.modelTransform.setValue(config.transform)
    coloring.colorProperty.setValue(config.color ?: Vector4(1f))

    if (shading != null) {
      shading.glowProperty.setValue(config.glow)
      shading.normalTransformProperty.setValue(config.normalTransform ?: Matrix.identity)
    }

    if (config.texture != null) {
      config.texture.activate()
    }

    if (textureScale != null && config.textureScale != null) {
      textureScale.setValue(config.textureScale)
    }

    if (nearPlaneHeight != null && config.nearPlaneHeight != null) {
      nearPlaneHeight.setValue(config.nearPlaneHeight)
    }
  }
}

data class Shaders(
    val depthOfField: DepthScreenShader,
    val screenColor: ScreenColorShader,
    val screenDesaturation: DepthScreenShader,
    val screenTexture: DepthScreenShader
)

data class UniformBuffers(
    val instance: UniformBuffer,
    val scene: UniformBuffer,
    val section: UniformBuffer,
    val bone: UniformBuffer
)

fun createShaders(): Shaders {
  return Shaders(
      depthOfField = DepthScreenShader(ShaderProgram(screenVertex, depthOfFieldFragment)),
      screenColor = ScreenColorShader(ShaderProgram(screenVertex, screenColorFragment)),
      screenDesaturation = DepthScreenShader(ShaderProgram(screenVertex, screenDesaturation)),
      screenTexture = DepthScreenShader(ShaderProgram(screenVertex, screenTextureFragment))
  )
}
