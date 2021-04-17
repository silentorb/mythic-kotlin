package silentorb.mythic.lookinglass

import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.glowing.clearDepth
import silentorb.mythic.glowing.debugMarkPass
import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.deferred.applyDeferredShading
import silentorb.mythic.lookinglass.drawing.renderElementGroups
import silentorb.mythic.lookinglass.drawing.renderVolumes
import silentorb.mythic.scenery.Camera

enum class DepthMode {
  none,
  local,
  global
}

data class SceneLayer(
    val elements: ElementGroups = listOf(),
    val depth: DepthMode? = null,
    val attributes: Set<String> = setOf(),
    val shadingMode: ShadingMode? = null,
    val children: List<SceneLayer> = listOf(),
)

typealias SceneLayers = List<SceneLayer>

// Temporary callback mechanism while experimenting with a hybrid rendering system
typealias OnRenderScene = (SceneRenderer, Camera, SceneLayer) -> Unit

fun layerLightingMode(options: DisplayOptions, layer: SceneLayer): ShadingMode {
  if (getDebugBoolean("NO_SHADING"))
    return ShadingMode.none

  val deferred = layer.shadingMode == ShadingMode.deferred &&
      options.shadingMode == ShadingMode.deferred

  return if (deferred)
    ShadingMode.deferred
  else
    ShadingMode.forward
}

fun renderSceneLayer(renderer: SceneRenderer, camera: Camera, layer: SceneLayer, parent: SceneLayer? = null,
                     callback: OnRenderScene? = null) {
  val parentShadingMode = parent?.shadingMode
  val shadingMode = if (layer.shadingMode == null)
    parentShadingMode ?: ShadingMode.none
  else
    layerLightingMode(renderer.options, layer)

  val manageDeferred = shadingMode == ShadingMode.deferred && shadingMode != parentShadingMode
  val previousDepthEnabled = globalState.depthEnabled
  val depthMode = layer.depth
  debugMarkPass(manageDeferred && getDebugBoolean("MARK_DEFERRED_RENDERING"),
      "Deferred Rendering") {
    if (depthMode != null)
      globalState.depthEnabled = depthMode != DepthMode.none

    if (renderer.offscreenRendering) {
      activeOffscreenRendering(renderer)
    } else {
      activeDirectRendering(renderer)
    }

    if (manageDeferred) {
      val deferred = renderer.renderer.deferred!!
      deferred.frameBuffer.activate()
      renderer.renderer.glow.operations.clearScreen()
      globalState.blendEnabled = false
    }

    if (layer.children.any()) {
      for (child in layer.children) {
        renderSceneLayer(renderer, camera, child, layer, callback)
      }
    } else {
      renderElementGroups(renderer, camera, layer.elements, shadingMode)
      renderVolumes(renderer, layer.elements, shadingMode)
    }

    if (callback != null) {
      callback(renderer, camera, layer)
    }
  }

  if (manageDeferred) {
    val sphereMesh = renderer.meshes["sphere"]!!.primitives.first().mesh
    applyDeferredShading(renderer, sphereMesh)
  }

  if (depthMode != null)
    globalState.depthEnabled = previousDepthEnabled
}

fun renderSceneLayers(renderer: SceneRenderer, camera: Camera, layers: SceneLayers, callback: OnRenderScene? = null) {
  for (layer in layers) {
    renderSceneLayer(renderer, camera, layer, null, callback)
  }
}
