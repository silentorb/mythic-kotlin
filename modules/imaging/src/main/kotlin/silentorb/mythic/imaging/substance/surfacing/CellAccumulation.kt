package silentorb.mythic.imaging.substance.surfacing

// This function assumes that vertices of each mesh already have a distance of at least minDistance between them
fun mergeContourMeshes(distanceTolerance: Float): (ContourMesh, ContourMesh) -> ContourMesh = { first, second ->
  val clumps = second.vertices.mapNotNull { b ->
    // TODO: This code may eventually better neighbor selection when there are multiple options
    val a = first.vertices
        .filter { it.distance(b) < distanceTolerance }
        .firstOrNull()
    if (a != null)
      Pair(b, a)
    else
      null
  }
      .associate { it }

  val secondVertices = second.vertices
      .minus(clumps.keys)

  val secondEdges = if (clumps.none())
    second.edges
  else
    second.edges.map { edge ->
      edge.copy(
          first = clumps[edge.first] ?: edge.first,
          second = clumps[edge.second] ?: edge.second
      )
    }

  ContourMesh(
      vertices = first.vertices.plus(secondVertices),
      edges = first.edges.plus(secondEdges)
  )
}
