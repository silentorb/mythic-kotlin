package silentorb.mythic.fathom.sampling

import silentorb.mythic.scenery.Shading
import silentorb.mythic.spatial.Vector3

data class SamplePoint(
    val location: Vector3,
    val shading: Shading,
    val normal: Vector3,
    val size: Float,
    val level: Int
)
