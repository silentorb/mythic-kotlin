package silentorb.mythic.scenery

import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4

enum class LightType(val value: Int) {
  point(1),
  spot(2)
}

data class Light(
    val type: LightType,
    val color: Vector4, // w is brightness
    val offset: Vector3,
    val direction: Vector3? = null,
    val range: Float
)
