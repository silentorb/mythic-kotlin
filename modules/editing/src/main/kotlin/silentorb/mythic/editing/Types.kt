package silentorb.mythic.editing

import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4i

data class Node(
    val type: String,
    val location: Vector3 = Vector3.zero,
    val orientation: Quaternion = Quaternion.zero,
    val scale: Vector3 = Vector3.unit,
    val parent: String? = null
)

data class Graph(
    val nodes: Map<String, Node>
)

data class Typeface(
    val name: String,
    val path: String,
    val size: Float
)

data class EditorResult(
    val viewport: Vector4i?
)
