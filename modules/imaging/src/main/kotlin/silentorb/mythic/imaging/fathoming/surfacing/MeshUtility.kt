package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.spatial.Vector3

fun vertexList(edge: SimpleEdge): List<Vector3> =
    listOf(edge.first, edge.second)

fun getVerticesFromEdges(edges: List<SimpleEdge>): List<Vector3> =
    edges.flatMap(::vertexList)
        .distinct()

tailrec fun groupNearbyVertices(distanceTolerance: Float, remaining: List<Vector3>,
                                groups: List<List<Vector3>>): List<List<Vector3>> =
    if (remaining.size < 2)
      groups
    else {
      val next = remaining.first()
      val nearby = remaining.filter { it.distance(next) < distanceTolerance }
      val nextRemaining = remaining
          .drop(1)
          .minus(nearby)

      val nextGroups = if (nearby.any())
        groups.plusElement(listOf(next).plus(nearby))
      else
        groups

      groupNearbyVertices(distanceTolerance, nextRemaining, nextGroups)
    }

fun groupNearbyVertices(distanceTolerance: Float, edges: List<SimpleEdge>): List<List<Vector3>> =
    groupNearbyVertices(distanceTolerance, getVerticesFromEdges(edges), listOf())

fun mergeNearbyEdgeVertices(distanceTolerance: Float, edges: List<SimpleEdge>): List<SimpleEdge> {
  val clumps = groupNearbyVertices(distanceTolerance, edges)
  val vertexMap = clumps.flatMap { points ->
    val center = points.reduce { a, b -> a + b } / points.size.toFloat()
    points.map { point ->
      Pair(point, center)
    }
  }
      .associate { it }

  return edges.map { edge ->
    edge.copy(
        first = vertexMap[edge.first] ?: edge.first,
        second = vertexMap[edge.second] ?: edge.second
    )
  }
      .filter { it.first != it.second}
}
