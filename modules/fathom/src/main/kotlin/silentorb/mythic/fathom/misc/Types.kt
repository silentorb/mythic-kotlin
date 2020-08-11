package silentorb.mythic.fathom.misc

import silentorb.mythic.scenery.Shading
import silentorb.mythic.scenery.Shape
import silentorb.mythic.spatial.Vector3

data class ModelFunction(
    val form: DistanceFunction,
    val shading: (Vector3) -> Shading,
    val collision: Shape?
)
