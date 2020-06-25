package silentorb.mythic.fathom.sampling

import silentorb.mythic.fathom.misc.DistanceFunction
import silentorb.mythic.fathom.misc.ShadingFunction

data class SamplingConfig(
    val getDistance: DistanceFunction,
    val getShading: ShadingFunction,
    val pointSize: Float,
    val levels: Int = 1,
    val levelOffsetRange: Float = 0.1f
)

