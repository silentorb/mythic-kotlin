package silentorb.mythic.fathom.surfacing

tailrec fun removeDuplicateEdges(edges: Edges, accumulator: Edges): Edges =
    if (edges.none())
      accumulator
    else {
      val next = edges.first()
      val remaining = edges.drop(1)
      val matches = edges.filter { edgesMatch(next, it) }
      removeDuplicateEdges(remaining.minus(matches), accumulator.plus(next))
    }

fun removeDuplicateEdges(edges: Edges): Edges =
    removeDuplicateEdges(edges, listOf())
