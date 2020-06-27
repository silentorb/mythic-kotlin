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

fun renderSceneLayer(renderer: SceneRenderer, camera: Camera, layer: SceneLayer) {
  val previousDepthEnabled = globalState.depthEnabled
  globalState.depthEnabled = layer.useDepth
  if (layer.resetDepth)
    clearDepth()

  for (group in layer.elements) {
    renderElementGroup(renderer, camera, group)
  }

  renderVolumes(renderer, layer.elements)
  globalState.depthEnabled = previousDepthEnabled
}

fun renderSceneLayers(renderer: SceneRenderer, camera: Camera, layers: SceneLayers) {
  for (layer in layers) {
    renderSceneLayer(renderer, camera, layer)
  }
}
