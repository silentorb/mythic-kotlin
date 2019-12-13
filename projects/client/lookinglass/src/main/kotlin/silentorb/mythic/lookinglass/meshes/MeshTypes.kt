package silentorb.mythic.lookinglass.meshes

import silentorb.mythic.sculpting.EdgeReference
import silentorb.mythic.spatial.Vector3

typealias EdgeGroup = Map<EdgeReference, Float>
typealias VertexGroup = Map<Vector3, Float>

data class MeshInfo(
    val vertexGroups: List<VertexGroup> = listOf(),
    val edgeGroups: List<EdgeGroup> = listOf()
)
