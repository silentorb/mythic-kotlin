package silentorb.mythic.fathom.misc

data class ModelFunction(
    val form: DistanceFunction,
    val shading: ShadingFunction,
    val collision: CollisionFunction?
)
