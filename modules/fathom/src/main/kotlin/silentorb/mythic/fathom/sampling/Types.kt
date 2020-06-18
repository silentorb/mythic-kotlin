package silentorb.mythic.fathom.sampling

import silentorb.mythic.fathom.RgbColorFunction
import silentorb.mythic.fathom.DistanceFunction
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4

data class SamplingConfig(
    val getDistance: DistanceFunction,
    val getColor: RgbColorFunction,
    val resolution: Int, // Length of samples per cell (Each cell having a length of 1f)
    val pointSize: Float
)

data class SamplePoint(
    val location: Vector3,
    val color: Vector4,
    val normal: Vector3,
    val size: Float
)
