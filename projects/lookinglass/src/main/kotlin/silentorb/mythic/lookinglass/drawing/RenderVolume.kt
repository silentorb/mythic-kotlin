package silentorb.mythic.lookinglass.drawing

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.*
import silentorb.mythic.lookinglass.ShadingMode
import silentorb.mythic.lookinglass.shading.ObjectShaderConfig
import silentorb.mythic.lookinglass.shading.ShaderFeatureConfig
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.getRotationMatrix

data class BatchedVolume(
    val shaderConfig: ObjectShaderConfig,
    val sampledModel: SampledModel,
    val opaqueCounts: IntArray,
    val transparentCounts: IntArray
)

fun prepareRenderVolume(renderer: SceneRenderer, sampledModel: SampledModel, location: Vector3, transform: Matrix): BatchedVolume {
  val camera = renderer.camera
  val orientationTransform = getRotationMatrix(transform)
  val distance = camera.position.distance(location)
  val levels = sampledModel.levels
  val lodRanges = sampledModel.lodRanges
//  val lodRanges = listOf(
//      60f,
//      25f,
//      10f
//  )
//  val lodRanges = listOf(
//      15f,
//      10f,
//      5f
//  ).takeLast(sampledModel.levels)
  val lodLevel = getLodLevel(lodRanges, levels, distance)

  val lodTransitionScalar: Float = if (lodLevel < lodRanges.size - 1) {
    val lower = lodRanges[lodLevel + 1]
    val upper = lodRanges[lodLevel]
    if (distance > upper)
      0f
    else {
      val gap = upper - lower
      val current = distance - lower
      1f - current / gap
    }
  } else
    1f

  val lodOpacityLevels = (0 until levels)
      .map { level ->
        if (level == lodLevel + 1)
          lodTransitionScalar * 0.5f
        else if (level > 1f)
          0.2f
        else
          1f
      }

  val config = ObjectShaderConfig(
      nearPlaneHeight = getNearPlaneHeight(renderer.viewport, camera.angleOrZoom),
      normalTransform = orientationTransform,
      transform = transform,
      lodOpacityLevels = lodOpacityLevels
  )

  val visibleSides = getVisibleSides((camera.position - location).transform(orientationTransform.invert()))

  val opaqueCounts = (
      listOf(sampledModel.baseSize) +
          sampledModel.partitioning
              .mapIndexed { sideIndex, levelCounts ->
                if (visibleSides.contains(NormalSide.values()[sideIndex]))
                  levelCounts.mapIndexed { level, count ->
                    if (level <= lodLevel)
                      count
                    else
                      0
                  }
                else
                  levelCounts.map { 0 }
              }
              .flatten()
      )
      .toIntArray()

  val transparentCounts = (
      listOf(0) +
          sampledModel.partitioning
              .mapIndexed { sideIndex, levelCounts ->
                if (visibleSides.contains(NormalSide.values()[sideIndex]))
                  levelCounts.mapIndexed { level, count ->
                    if (level == lodLevel + 1 && lodTransitionScalar != 0f)
                      count
                    else
                      0
                  }
                else
                  levelCounts.map { 0 }
              }
              .flatten()
      )
      .toIntArray()

  return BatchedVolume(
      sampledModel = sampledModel,
      shaderConfig = config,
      opaqueCounts = opaqueCounts,
      transparentCounts = transparentCounts
  )
}

fun renderBatchedVolumes(renderer: SceneRenderer, volumes: List<BatchedVolume>, shading: ShadingMode) {
  val effect = renderer.getShader(renderer.renderer.vertexSchemas.shadedPoint, ShaderFeatureConfig(
      shading = shading,
      pointSize = true
  ))

  globalState.vertexProgramPointSizeEnabled = true

  for (volume in volumes) {
    effect.activate(volume.shaderConfig)
    volume.sampledModel.mesh.vertexBuffer.activate()
    GL14.glMultiDrawArrays(GL11.GL_POINTS, volume.sampledModel.offsets.toIntArray(), volume.opaqueCounts)
  }

  globalState.depthWrite = false
  for (volume in volumes) {
    effect.activate(volume.shaderConfig)
    volume.sampledModel.mesh.vertexBuffer.activate()
    GL14.glMultiDrawArrays(GL11.GL_POINTS, volume.sampledModel.offsets.toIntArray(), volume.transparentCounts)
  }
  globalState.depthWrite = true
}

fun renderVolumes(sceneRenderer: SceneRenderer, elements: ElementGroups, shadingMode: ShadingMode) {
  val volumes = elements.flatMap { group ->
    group.meshes.mapNotNull { element ->
      val mesh = sceneRenderer.meshes[element.mesh]
      if (mesh == null || mesh.sampledModel == null)
        null
      else {
        prepareRenderVolume(sceneRenderer, mesh.sampledModel, element.location, element.transform)
      }
    }
  }

  renderBatchedVolumes(sceneRenderer, volumes, shadingMode)
}
