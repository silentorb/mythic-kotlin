package silentorb.mythic.scenery

import silentorb.mythic.spatial.Vector3

data class Shading(
    val color: Vector3,
    val opacity: Float,
    val glow: Float,
    val specular: Float
)
