package silentorb.mythic.lookinglass

import silentorb.mythic.glowing.clearDepth
import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.drawing.renderElementGroups
import silentorb.mythic.lookinglass.drawing.renderVolumes
import silentorb.mythic.scenery.Camera

data class SceneLayer(
    val elements: ElementGroups,
    val useDepth: Boolean,
    val resetDepth: Boolean = false,
    val attributes: Set<String> = setOf(),
    val lightingMode: LightingMode = LightingMode.forward,
)

typealias SceneLayers = List<SceneLayer>

// Temporary callback mechanism while experimenting with a hybrid rendering system
typealias OnRenderScene = (SceneRenderer, Camera, SceneLayer) -> Unit

fun renderSceneLayer(renderer: SceneRenderer, camera: Camera, layer: SceneLayer, callback: OnRenderScene? = null) {
  val previousDepthEnabled = globalState.depthEnabled
  globalState.depthEnabled = layer.useDepth
  if (layer.resetDepth)
    clearDepth()

  val deferred = layer.lightingMode == LightingMode.deferred &&
      renderer.renderer.options.lightingMode == LightingMode.deferred

  val lightingMode = if (deferred)
    LightingMode.deferred
  else
    LightingMode.forward

  renderElementGroups(renderer, camera, layer.elements, lightingMode)

  if (callback != null) {
    callback(renderer, camera, layer)
  }

  renderVolumes(renderer, layer.elements, lightingMode)
  globalState.depthEnabled = previousDepthEnabled
}

fun renderSceneLayers(renderer: SceneRenderer, camera: Camera, layers: SceneLayers, callback: OnRenderScene? = null) {
  for (layer in layers) {
    renderSceneLayer(renderer, camera, layer, callback)
  }
}
