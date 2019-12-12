package silentorb.mythic.scenery

enum class CameraMode {
  firstPerson,
  topDown,
  thirdPerson
}

data class Scene(
    val camera: Camera,
    val lights: List<Light> = listOf()
)

data class BillboardDetails(
    val text: String,
    val cooldown: Float? = null
)
