package silentorb.mythic.lookinglass

import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.Light
import silentorb.mythic.scenery.LightingConfig

data class Scene(
    val camera: Camera,
    val lights: List<Light> = listOf(),
    val lightingConfig: LightingConfig,
    val layers: SceneLayers,
    val filters: List<ScreenFilter>,
)
