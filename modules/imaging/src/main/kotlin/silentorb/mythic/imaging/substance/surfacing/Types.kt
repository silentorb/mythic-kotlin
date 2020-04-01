package silentorb.mythic.imaging.substance.surfacing

import silentorb.mythic.imaging.substance.DistanceFunction
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i

data class SurfacingConfig(
    val getDistance: DistanceFunction,
    val tolerance: Float
)

data class DecimalBounds(
    val start: Vector3,
    val end: Vector3
)

data class GridBounds(
    val start: Vector3i,
    val end: Vector3i
)
