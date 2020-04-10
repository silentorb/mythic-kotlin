package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.spatial.Vector3
import kotlin.math.abs

data class BrokenLine(
    val middle: Vector3,
    val first: Edge,
    val second: Edge
) {
  val edges = listOf(first, second)
}

typealias BrokenLines = List<BrokenLine>

tailrec fun accumulateNeighborLines(edge: Edge, endpoint: Vector3, lines: BrokenLines, accumulator: BrokenLines): Pair<BrokenLines, Vector3> =
    if (lines.none())
      Pair(accumulator, endpoint)
    else {
      val matches = lines.filter { line -> line.edges.contains(edge) }
      if (matches.none())
        Pair(accumulator, endpoint)
      else {
        assert(matches.size == 1)
        val match = matches.first()
        val nextEdge = match.edges.minus(edge).first()
        accumulateNeighborLines(nextEdge, getOtherVertex(nextEdge, match.middle), lines - match, accumulator + match)
      }
    }

tailrec fun groupLines(brokenLines: BrokenLines, accumulator: List<Edge>): List<Edge> =
    if (brokenLines.none())
      accumulator
    else {
      val next = brokenLines.first()
      val remaining = brokenLines.drop(1)
      val a = next.first
      val b = next.second
      val (firstNeighbors, start) = accumulateNeighborLines(a, getOtherVertex(a, next.middle), remaining, listOf())
      val (secondNeighbors, end) = accumulateNeighborLines(b, getOtherVertex(b, next.middle), remaining, listOf())
      val neighbors = firstNeighbors + secondNeighbors
      groupLines(remaining - neighbors, accumulator + Edge(start, end))
    }

fun unifyLinearEdges(allEdges: Edges): Edges {
  val vertices = getVerticesFromEdges(allEdges)
  val vertexEdgeMap = vertices
      .map { vertex ->
        Pair(vertex, allEdges.filter { edgeContains(it, vertex) })
      }

  val lines = vertexEdgeMap
      .filter { (_, edges) -> edges.size == 2 }

  val edgesNeedingUnifying = lines
      .mapNotNull { (vertex, edges) ->
        val a = edges[0]
        val b = edges[1]
        val firstVector = getEdgeVector(a)
        val secondVector = getEdgeVector(b)
        val dot = firstVector.dot(secondVector)
        if (abs(dot) > 0.8f)
          BrokenLine(vertex, a, b)
        else
          null
      }

  val unifiedEdges = groupLines(edgesNeedingUnifying, listOf())

  val oldEdges = edgesNeedingUnifying.flatMap { listOf(it.first, it.second) }

  return allEdges
      .minus(oldEdges)
      .plus(unifiedEdges)
}
