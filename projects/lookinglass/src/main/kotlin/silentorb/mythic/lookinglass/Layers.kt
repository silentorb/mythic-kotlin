package silentorb.mythic.lookinglass

import silentorb.mythic.glowing.clearDepth
import silentorb.mythic.glowing.debugMarkPass
import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.deferred.processDeferredShading
import silentorb.mythic.lookinglass.drawing.renderElementGroups
import silentorb.mythic.lookinglass.drawing.renderVolumes
import silentorb.mythic.scenery.Camera

data class SceneLayer(
    val elements: ElementGroups,
    val useDepth: Boolean,
    val resetDepth: Boolean = false,
    val attributes: Set<String> = setOf(),
    val shadingMode: ShadingMode = ShadingMode.forward,
)

typealias SceneLayers = List<SceneLayer>

// Temporary callback mechanism while experimenting with a hybrid rendering system
typealias OnRenderScene = (SceneRenderer, Camera, SceneLayer) -> Unit

fun layerLightingMode(options: DisplayOptions, layer: SceneLayer): ShadingMode {
  val deferred = layer.shadingMode == ShadingMode.deferred &&
      options.shadingMode == ShadingMode.deferred

  return if (deferred)
    ShadingMode.deferred
  else
    ShadingMode.forward
}

fun renderSceneLayer(renderer: SceneRenderer, camera: Camera, layer: SceneLayer, callback: OnRenderScene? = null) {
  val shadingMode = layerLightingMode(renderer.renderer.options, layer)
  debugMarkPass(shadingMode == ShadingMode.deferred, "Deferred Rendering") {

    val previousDepthEnabled = globalState.depthEnabled
    globalState.depthEnabled = layer.useDepth
    if (renderer.offscreenRendering) {
      activeOffscreenRendering(renderer)
    } else {
      activeDirectRendering(renderer)
    }

    if (layer.resetDepth)
      clearDepth()

    if (shadingMode == ShadingMode.deferred) {
      val deferred = renderer.renderer.deferred!!
      deferred.frameBuffer.activateDraw()
    }

    renderElementGroups(renderer, camera, layer.elements, shadingMode)

    if (callback != null) {
      callback(renderer, camera, layer)
    }

    renderVolumes(renderer, layer.elements, shadingMode)

    if (shadingMode == ShadingMode.deferred) {
      processDeferredShading(renderer)
    }

    globalState.depthEnabled = previousDepthEnabled
  }
}

fun renderSceneLayers(renderer: SceneRenderer, camera: Camera, layers: SceneLayers, callback: OnRenderScene? = null) {
  for (layer in layers) {
    renderSceneLayer(renderer, camera, layer, callback)
  }
}
