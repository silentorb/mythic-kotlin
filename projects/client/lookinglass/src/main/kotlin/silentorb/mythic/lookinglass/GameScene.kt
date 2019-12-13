package silentorb.mythic.lookinglass

import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.Light
import silentorb.mythic.scenery.Scene

data class GameScene(
    val main: Scene,
    val opaqueElementGroups: ElementGroups,
    val transparentElementGroups: ElementGroups,
    val background: ElementGroups,
    val filters: List<ScreenFilter>
) {

  val camera: Camera
    get() = main.camera

  val lights: List<Light>
    get() = main.lights
}
