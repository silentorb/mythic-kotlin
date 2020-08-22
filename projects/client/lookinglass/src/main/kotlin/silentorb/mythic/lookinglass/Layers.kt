package silentorb.mythic.lookinglass

import silentorb.mythic.glowing.clearDepth
import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.drawing.renderElementGroup
import silentorb.mythic.lookinglass.drawing.renderVolumes
import silentorb.mythic.scenery.Camera

data class SceneLayer(
    val elements: ElementGroups,
    val useDepth: Boolean,
    val resetDepth: Boolean = false
)

typealias SceneLayers = List<SceneLayer>

// Temporary callback mechanism while experimenting with a hybrid rendering system
typealias OnRenderScene = (SceneRenderer, Camera, SceneLayer) -> Unit

fun renderSceneLayer(renderer: SceneRenderer, camera: Camera, layer: SceneLayer, callback: OnRenderScene? = null) {
  val previousDepthEnabled = globalState.depthEnabled
  globalState.depthEnabled = layer.useDepth
  if (layer.resetDepth)
    clearDepth()

  for (group in layer.elements) {
    renderElementGroup(renderer, camera, group)
  }

  if (callback != null) {
    callback(renderer, camera, layer)
  }

  renderVolumes(renderer, layer.elements)
  globalState.depthEnabled = previousDepthEnabled
}

fun renderSceneLayers(renderer: SceneRenderer, camera: Camera, layers: SceneLayers, callback: OnRenderScene? = null) {
  for (layer in layers) {
    renderSceneLayer(renderer, camera, layer, callback)
  }
}
