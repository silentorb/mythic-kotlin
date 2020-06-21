package silentorb.mythic.fathom.sampling

import silentorb.mythic.fathom.misc.DistanceFunction
import silentorb.mythic.fathom.misc.ShadingFunction

data class SamplingConfig(
    val getDistance: DistanceFunction,
    val getShading: ShadingFunction,
    val resolution: Int, // Length of samples per cell (Each cell having a length of 1f)
    val pointSize: Float
)

