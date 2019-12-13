package silentorb.mythic.lookinglass.drawing

import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.ElementGroups
import silentorb.mythic.lookinglass.Renderer
import silentorb.mythic.scenery.Camera

fun renderBackground(renderer: Renderer, camera: Camera, background: ElementGroups) {
  globalState.depthEnabled = false
  for (group in background) {
    renderElementGroup(renderer, camera, group)
  }
  globalState.depthEnabled = true
}
