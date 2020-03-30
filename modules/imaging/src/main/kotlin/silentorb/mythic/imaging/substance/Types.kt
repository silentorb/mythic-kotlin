package silentorb.mythic.imaging.substance

import silentorb.mythic.spatial.Vector3i
import java.nio.FloatBuffer

data class VoxelMap(
    val depth: Int,
    val dimensions: Vector3i,
    val buffer: FloatBuffer
)
