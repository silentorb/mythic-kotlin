package silentorb.mythic.fathom.surfacing

tailrec fun removeDanglingEdges(edges: List<Edge>): Edges {
  val vertices = getVerticesFromEdges(edges)
  val counts = vertices.associateWith { vertex ->
    edges.count { edgeContains(it, vertex) }
  }
  val dangling = edges.filter { counts[it.first]!! == 1 || counts[it.second]!! == 1 }
  return if (dangling.none())
    edges
  else
    removeDanglingEdges(edges - dangling)
}
