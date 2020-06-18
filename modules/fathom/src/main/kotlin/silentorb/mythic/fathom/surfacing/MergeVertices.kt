package silentorb.mythic.fathom.surfacing

import silentorb.mythic.spatial.Vector3

tailrec fun groupNearbyVertices(distanceTolerance: Float, remaining: List<Vector3>,
                                groups: List<List<Vector3>>): List<List<Vector3>> =
    if (remaining.size < 2)
      groups
    else {
      val next = remaining.first()
      val nearby = remaining.filter { it != next && it.distance(next) < distanceTolerance }
      val nextRemaining = remaining
          .drop(1)

      val nextGroups = if (nearby.any()) {
        val newGroup = listOf(next).plus(nearby)
//        if (groups.none { group -> group.containsAll(newGroup) })
        val overlaps = groups.filter { group -> group.any { newGroup.contains(it) } }
        if (overlaps.any())
          groups.minus(overlaps).plusElement(overlaps.flatten().plus(newGroup).distinct())
        else
          groups.plusElement(newGroup)
      } else
        groups

      groupNearbyVertices(distanceTolerance, nextRemaining, nextGroups)
    }

fun groupNearbyVertices(distanceTolerance: Float, edges: List<Edge>): List<List<Vector3>> =
    groupNearbyVertices(distanceTolerance, getVerticesFromEdges(edges), listOf())

fun mergeNearbyEdgeVertices(distanceTolerance: Float, edges: List<Edge>): List<Edge> {
  val clumps = groupNearbyVertices(distanceTolerance, edges)
  val vertexMap = clumps.flatMap { points ->
    val center = points.reduce { a, b -> a + b } / points.size.toFloat()
    points.map { point ->
      Pair(point, center)
    }
  }
      .associate { it }

  val result = edges.mapNotNull { edge ->
    val first = vertexMap[edge.first] ?: edge.first
    val second = vertexMap[edge.second] ?: edge.second
    if (first.distance(second) > distanceTolerance)
      edge.copy(
          first = vertexMap[edge.first] ?: edge.first,
          second = vertexMap[edge.second] ?: edge.second
      )
    else
      null
  }

  return result
}
