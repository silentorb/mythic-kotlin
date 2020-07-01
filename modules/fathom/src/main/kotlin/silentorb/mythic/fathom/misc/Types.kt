package silentorb.mythic.fathom.misc

import silentorb.mythic.scenery.Shape

data class ModelFunction(
    val form: DistanceFunction,
    val shading: ShadingFunction,
    val collision: Shape?
)
