package silentorb.mythic.editing

import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4i

data class Node(
    val type: String? = null,
    val location: Vector3 = Vector3.zero,
    val orientation: Quaternion = Quaternion.zero,
    val scale: Vector3 = Vector3.unit,
    val parent: String? = null
)

typealias Graph = Map<String, Node>

typealias GraphLibrary = Map<String, Graph>

data class GraphFile(
    val nodes: Graph
)

data class Typeface(
    val name: String,
    val path: String,
    val size: Float
)

data class Editor(
    val isActive: Boolean = false,
    val graph: Graph = mapOf(),
    val selection: Set<String> = setOf(),
    val viewport: Vector4i? = null
)

fun isActive(editor: Editor?) =
    editor?.isActive == true
