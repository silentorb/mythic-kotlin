package silentorb.mythic.lookinglass

import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.drawing.renderElementGroup
import silentorb.mythic.scenery.Camera

data class SceneLayer(
    val elements: ElementGroups,
    val useDepth: Boolean
)

typealias SceneLayers = List<SceneLayer>

fun renderSceneLayer(renderer: Renderer, camera: Camera, layer: SceneLayer) {
  val previousDepthEnabled = globalState.depthEnabled
  globalState.depthEnabled = layer.useDepth
  for (group in layer.elements) {
    renderElementGroup(renderer, camera, group)
  }
  globalState.depthEnabled = previousDepthEnabled
}

fun renderSceneLayers(renderer: Renderer, camera: Camera, layers: SceneLayers) {
  for (layer in layers) {
    renderSceneLayer(renderer, camera, layer)
  }
}
