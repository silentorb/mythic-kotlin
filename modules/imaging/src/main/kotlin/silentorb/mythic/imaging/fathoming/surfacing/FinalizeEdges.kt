package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.spatial.Vector3

data class FarthestPointsResult(
    val first: Vector3,
    val second: Vector3,
    val distance: Float
)

tailrec fun getFarthestPoints(points: List<Vector3>, farthest: FarthestPointsResult): FarthestPointsResult =
    if (points.none())
      farthest
    else {
      val next = points.first()
      val nextFarthest = listOf(
          farthest,
          FarthestPointsResult(farthest.first, next, next.distance(farthest.first)),
          FarthestPointsResult(farthest.second, next, next.distance(farthest.second))
      )
          .sortedByDescending { it.distance }
          .first()

      getFarthestPoints(points.drop(1), nextFarthest)
    }

fun getFarthestPoints(points: List<Vector3>): FarthestPointsResult {
  val first = points.first()
  val second = points[1]
  return getFarthestPoints(points.drop(2), FarthestPointsResult(first, second, first.distance(second)))
}

fun lineAggregateToEdge(line: LineAggregate): Edge? {
  if (line.size < 2)
    throw Error("A line aggregate must have at least two samples to be converted to an edge.")

  val farthest = getFarthestPoints(line.map { it.position })
  return if (farthest.first == farthest.second)
    null
  else
    Edge(
        farthest.first,
        farthest.second
    )
}

fun lineAggregatesToEdges(config: SurfacingConfig, lines: LineAggregates): Edges {
  val distanceTolerance = getDistanceTolerance(config)
  val edges = lines
      .filter { it.size > 1 }
      .mapNotNull(::lineAggregateToEdge)

  return mergeNearbyEdgeVertices(distanceTolerance, edges)
}
