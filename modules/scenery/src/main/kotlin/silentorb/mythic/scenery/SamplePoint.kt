package silentorb.mythic.scenery

import silentorb.mythic.spatial.Vector3

data class SamplePoint(
    val location: Vector3,
    val shading: Shading,
    val normal: Vector3,
    val size: Float
)
