package silentorb.mythic.fathom.sampling

import silentorb.mythic.fathom.misc.DistanceFunction
import silentorb.mythic.fathom.misc.ShadingFunction

data class SamplingConfig(
    val getDistance: DistanceFunction,
    val getShading: ShadingFunction,
    val pointSizeScale: Float,
    val resolution: Int,
    val levels: Int = 1
)

