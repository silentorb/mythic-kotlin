package silentorb.mythic.lookinglass

import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.Light
import silentorb.mythic.scenery.LightingConfig

data class Scene(
    val camera: Camera,
    val lights: List<Light> = listOf(),
    val lightingConfig: LightingConfig
)

data class GameScene(
    val main: Scene,
    val layers: SceneLayers,
    val filters: List<ScreenFilter>
)
