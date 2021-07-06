package silentorb.mythic.lookinglass.shading

import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.ShadingMode
import silentorb.mythic.lookinglass.deferred.*
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

class LightingFeature(program: ShaderProgram, sectionBuffer: UniformBuffer) {
  val sectionProperty = bindUniformBuffer(UniformBufferId.SectionUniform, program, sectionBuffer)
}

class SkeletonFeature(program: ShaderProgram, boneBuffer: UniformBuffer) {
  val boneTransformsProperty = bindUniformBuffer(UniformBufferId.BoneTransforms, program, boneBuffer)
}

fun populateBoneBuffer(boneBuffer: UniformBuffer, originalTransforms: List<Matrix>, transforms: List<Matrix>): UniformBuffer {
  val bytes = createBoneTransformBuffer(originalTransforms, transforms)
  boneBuffer.load(bytes)
  return boneBuffer
}

data class ShaderFeatureConfig(
    val pointSize: Boolean = false,
    val shading: ShadingMode = ShadingMode.none,
    val skeleton: Boolean = false,
    val texture: Boolean = false,
    val colored: Boolean = false,
    val instanced: Boolean = false,
    val animatedTexture: Boolean = false, // Requires `texture` == true
    val deferredBlending: Boolean = false,
)

data class ObjectShaderConfig(
    val transform: Matrix = Matrix.identity,
    val texture: Texture? = null,
    val color: Vector4? = null,
    val glow: Float = 0f,
    val normalTransform: Matrix? = null,
    val boneBuffer: UniformBuffer? = null,
    val textureScale: Vector2? = null,
    val nearPlaneHeight: Float? = null, // Used for scaling point size
    val lodOpacityLevels: List<Float>? = null,
    val screenDimensions: Vector2? = null,
)

fun generateShaderProgram(vertexSchema: VertexSchema, featureConfig: ShaderFeatureConfig): ShaderProgram {
  val vertexShader = generateVertexCode(featureConfig)(vertexSchema)
  val fragmentShader = generateFragmentShader(featureConfig)
  return ShaderProgram(vertexShader, fragmentShader)
}

val debugGpuModelTransformCost = getDebugBoolean("DEBUG_GPU_MODEL_TRANSFORM_COST")

// This code is used roughly  measure the cost of transforming models
// It's purpose is to weigh the cost of passing model transforms
// It does not weigh the cost of applying model transforms in the vertex shader
// The last use of this code demonstrated that passing model transforms had no discernable impact
// on performance
fun setModelTransformWithDebugging(config: ObjectShaderConfig, perspective: PerspectiveFeature) {
  if (!getDebugBoolean("DISABLE_RENDER_MODEL_TRANSFORMS"))
    when {
      getDebugBoolean("RENDER_FIXED_MODEL_TRANSFORMS") -> {
        perspective.modelTransform.setValue(Matrix.identity.translate(20f, 0f, 0f))
      }
      // Used to verify that setting the same matrix repeatedly is not somehow getting optimized away
      getDebugBoolean("RENDER_FIXED_MODEL_TRANSFORMS_SLOW") -> {
        for (i in 0 until 100) {
          perspective.modelTransform.setValue(Matrix.identity.translate(20f, 0f, 0f))
        }
      }
      else -> perspective.modelTransform.setValue(config.transform)
    }
}

class GeneralPerspectiveShader(buffers: UniformBuffers, vertexSchema: VertexSchema, featureConfig: ShaderFeatureConfig) {
  val program = generateShaderProgram(vertexSchema, featureConfig)
  val perspective: PerspectiveFeature = PerspectiveFeature(program)
  val coloring: ColoringFeature = ColoringFeature(program)
  val textureScale = if (featureConfig.animatedTexture) Vector2Property(program, "uniformTextureScale") else null
  val instanceProperty = if (featureConfig.instanced) bindUniformBuffer(UniformBufferId.InstanceUniform, program, buffers.instance) else null
  val sceneProperty = bindUniformBuffer(UniformBufferId.SceneUniform, program, buffers.scene)
  val lighting: LightingFeature? = if (featureConfig.shading == ShadingMode.forward) LightingFeature(program, buffers.section) else null
  val skeleton: SkeletonFeature? = if (featureConfig.skeleton) SkeletonFeature(program, buffers.bone) else null
  val nearPlaneHeight: FloatProperty? = if (featureConfig.pointSize) FloatProperty(program, "nearPlaneHeight") else null
  val lodOpacityLevels: FloatArrayProperty? = if (featureConfig.pointSize) FloatArrayProperty(program, "lodOpacityLevels") else null
  val normalTransformProperty = if (featureConfig.shading != ShadingMode.none) MatrixProperty(program, "normalTransform") else null
  val glowProperty = if (featureConfig.shading != ShadingMode.none) FloatProperty(program, "glow") else null
  val dimensionsProperty = if (featureConfig.deferredBlending) Vector2Property(program, "dimensions") else null

  init {
    if (featureConfig.deferredBlending) {
      routeDeferredBufferTextures(program)
    }
  }

  // IntelliJ will flag this use of inline as a warning, but using inline here
  // causes the JVM to optimize away the ObjectShaderConfig allocation and significantly
  // reduces the amount of objects created each frame.
  // A similar optimization may eventually be applied by the JIT, but this way the optimization is applied immediately
  inline fun activate(config: ObjectShaderConfig) {
    program.activate()

    if (debugGpuModelTransformCost) {
      setModelTransformWithDebugging(config, perspective)
    } else {
      perspective.modelTransform.setValue(config.transform)
    }

    coloring.colorProperty.setValue(config.color ?: Vector4(1f))
    glowProperty?.setValue(config.glow)
    normalTransformProperty?.setValue(config.normalTransform ?: Matrix.identity)

    if (config.texture != null) {
      config.texture.activate()
    }

    if (textureScale != null && config.textureScale != null) {
      textureScale.setValue(config.textureScale)
    }

    if (nearPlaneHeight != null && config.nearPlaneHeight != null) {
      nearPlaneHeight.setValue(config.nearPlaneHeight)
    }

    if (lodOpacityLevels != null && config.lodOpacityLevels != null) {
      lodOpacityLevels.setValue(config.lodOpacityLevels)
    }

    if (dimensionsProperty != null && config.screenDimensions != null) {
      dimensionsProperty.setValue(config.screenDimensions)
    }
  }
}

data class Shaders(
    val depthOfField: DepthScreenShader,
    val screenColor: ScreenColorShader,
    val screenDesaturation: DepthScreenShader,
    val screenTexture: DepthScreenShader,
    val deferredShading: DeferredScreenShader,
    val deferredAmbientShading: AmbientScreenShader,
)

data class UniformBuffers(
    val instance: UniformBuffer,
    val scene: UniformBuffer,
    val section: UniformBuffer,
    val bone: UniformBuffer
)

fun createShaders(uniformBuffers: UniformBuffers): Shaders {
  return Shaders(
      depthOfField = DepthScreenShader(ShaderProgram(screenVertex, depthOfFieldFragment)),
      screenColor = ScreenColorShader(ShaderProgram(screenVertex, screenColorFragment)),
      screenDesaturation = DepthScreenShader(ShaderProgram(screenVertex, screenDesaturation)),
      screenTexture = DepthScreenShader(ShaderProgram(screenVertex, screenTextureFragment)),
      deferredShading = newDeferredScreenShader(uniformBuffers),
      deferredAmbientShading = newAmbientScreenShader(),
  )
}
