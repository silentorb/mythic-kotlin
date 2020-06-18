package silentorb.mythic.fathom.surfacing

import silentorb.mythic.spatial.Vector3

fun vertexList(edge: Edge): List<Vector3> =
    listOf(edge.first, edge.second)

fun getVerticesFromEdges(edges: List<Edge>): List<Vector3> =
    edges.flatMap(::vertexList)
        .distinct()
