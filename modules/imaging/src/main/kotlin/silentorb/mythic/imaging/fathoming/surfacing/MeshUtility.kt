package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.spatial.Vector3

fun vertexList(edge: SimpleEdge): List<Vector3> =
    listOf(edge.first, edge.second)

fun getVerticesFromEdges(edges: List<SimpleEdge>): List<Vector3> =
    edges.flatMap(::vertexList)
        .distinct()
