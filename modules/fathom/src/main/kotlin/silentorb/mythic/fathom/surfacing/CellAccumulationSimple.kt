package silentorb.mythic.fathom.surfacing

fun aggregateCellsSimple(config: SurfacingConfig, cells: List<Edges>): Edges {
  val distanceTolerance = getDistanceTolerance(config) * 1f
  val flattened = cells.flatten()
  val merged = mergeNearbyEdgeVertices(distanceTolerance, flattened)
  val reduced = removeDuplicateEdges(merged)
  val unified = unifyLinearEdges(reduced)
//  val reduced2 = removeDuplicateEdges(unified)
  val withoutDangling = removeDanglingEdges(unified)
  return withoutDangling
}
