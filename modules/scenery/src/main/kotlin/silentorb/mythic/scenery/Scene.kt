package silentorb.mythic.scenery

enum class CameraMode {
  firstPerson,
  topDown,
  thirdPerson
}

data class Scene(
    val camera: Camera,
    val lights: List<Light> = listOf(),
    val lightingConfig: LightingConfig
)

data class BillboardDetails(
    val text: String,
    val cooldown: Float? = null
)
