package silentorb.mythic.imaging.fathoming.sampling

import silentorb.mythic.imaging.fathoming.ColorFunction
import silentorb.mythic.imaging.fathoming.DistanceFunction
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4

data class SamplingConfig(
    val getDistance: DistanceFunction,
    val getColor: ColorFunction,
    val resolution: Int // Length of samples per cell (Each cell having a length of 1f)
)

data class SamplePoint(
    val location: Vector3,
    val color: Vector4,
    val normal: Vector3,
    val size: Float
)
