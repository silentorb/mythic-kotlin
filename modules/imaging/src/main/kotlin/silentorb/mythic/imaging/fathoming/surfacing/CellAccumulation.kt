package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.getCenter
import kotlin.math.abs

data class MergeConfig(
    val distanceTolerance: Float,
    val axis: Int,
    val boundaryRange: Float,
    val cellSize: Float
)

data class EdgeUnion(
    val firstVertex: Vector3,
    val secondVertex: Vector3,
    val firstEdges: Edges,
    val secondEdges: Edges
)

fun mergeEdges(distanceTolerance: Float, firstEdges: Edges, secondEdges: Edges, firstVertices: List<Vector3>, secondVertices: List<Vector3>): Edges {
  val clumps = secondVertices
      .mapNotNull { b ->
        // TODO: This code may eventually need better neighbor selection for when there are multiple options
        val a = firstVertices.firstOrNull { it.distance(b) < distanceTolerance }
        if (a != null)
          Pair(a, b)
        else
          null
      }

  val groupedEdges = clumps
      .map { (a, b) ->
        val first = firstEdges.filter { edgeContains(it, a) }
        val second = secondEdges.filter { edgeContains(it, b) }
        assert(first.any() && second.any())
        if (first.size > 1 || second.size > 1)
          throw Error("Not yet supported")

        EdgeUnion(
            firstVertex = a,
            secondVertex = b,
            firstEdges = first,
            secondEdges = second
        )
      }

  val (singleEdges, divergentEdges) = groupedEdges
      .partition { union ->
        val firstVector = getEdgeVector(union.firstEdges.first())
        val secondVector = getEdgeVector(union.secondEdges.first())
        val dot = firstVector.dot(secondVector)
        abs(dot) > 0.8f
      }

  val mergedEdges = singleEdges
      .map { union ->
        val a = getEdgeVertices(union.firstEdges.first()).first { it != union.firstVertex }
        val b = getEdgeVertices(union.secondEdges.first()).first { it != union.secondVertex }
        Edge(a, b)
      }

  val adjustedEdges = divergentEdges
      .flatMap { union ->
        val middle = getCenter(union.firstVertex, union.secondVertex)
        val a = union.firstEdges.map { edge -> replaceEdgeVertex(edge, union.firstVertex) }
        val b = union.secondEdges.map { edge -> replaceEdgeVertex(edge, union.secondVertex) }
        a + b
      }

  val newEdges = adjustedEdges.plus(mergedEdges)
  val firstRemovedEdges = groupedEdges.flatMap { it.firstEdges }
  val secondRemovedEdges = groupedEdges.flatMap { it.secondEdges }

  val firstResult = firstEdges.minus(firstRemovedEdges)
  val secondResult = secondEdges.minus(secondRemovedEdges)
  val result = firstResult.plus(secondResult).plus(newEdges)
  return result
}

fun mergeCells(config: MergeConfig, boundary: Float, first: Edges, second: Edges, firstVertices: List<Vector3>, secondVertices: List<Vector3>): Edges {
  val firstBoundary = boundary - config.boundaryRange
  val secondBoundary = boundary + config.boundaryRange
  val firstCandidates = firstVertices
      .filter { it[config.axis] > firstBoundary }

  val secondCandidates = secondVertices
      .filter { it[config.axis] < secondBoundary }

  return mergeEdges(config.distanceTolerance, first, second, firstCandidates, secondCandidates)
}

tailrec fun accumulateRow(
    config: MergeConfig,
    cells: List<Edges>,
    boundary: Float,
    previousCellVertices: List<Vector3>,
    accumulator: Edges
): Edges =
    if (cells.none())
      accumulator
    else {
      val next = cells.first()
      val nextCellVertices = getVerticesFromEdges(next)
      val merged = mergeCells(config, boundary, accumulator, next, previousCellVertices, nextCellVertices)
      accumulateRow(config, cells.drop(1), boundary + config.cellSize, nextCellVertices, merged)
    }

fun accumulateRows(mergeConfig: MergeConfig, bounds: GridBounds, groups: List<Edges>, rowCount: Int): List<Edges> {
  val axis = mergeConfig.axis
  val dimensions = getBoundsDimensions(bounds)
  val rowLength = dimensions[axis]
  val cellSize = mergeConfig.cellSize
  val firstDivision = bounds.start[axis] * cellSize + cellSize

  return (0 until rowCount)
      .map { i ->
        val rowStart = i * rowLength
        val row = groups.subList(rowStart, rowStart + rowLength)
        assert(row.size == rowLength)
        val first = row.first()
        accumulateRow(mergeConfig, row.drop(1), firstDivision, getVerticesFromEdges(first), first)
      }
}

fun accumulateFloors(mergeConfig: MergeConfig, bounds: GridBounds, floors: List<Edges>): Edges {
  val axis = mergeConfig.axis
  val cellSize = mergeConfig.cellSize
  val firstDivision = bounds.start[axis] * cellSize + cellSize
  val first = floors.first()
  return accumulateRow(mergeConfig, floors.drop(1), firstDivision, getVerticesFromEdges(first), first)
}

fun aggregateCells(config: SurfacingConfig, bounds: GridBounds, cells: List<Edges>): Edges {
  val cellSize = config.cellSize
  val subCellSize = (cellSize / config.subCells)
  val mergeConfig = MergeConfig(
      distanceTolerance = subCellSize * 2f,
      axis = 0,
      boundaryRange = subCellSize * 2f,
      cellSize = cellSize
  )

  val dimensions = getBoundsDimensions(bounds)
  val rowCount = dimensions.y * dimensions.z
  val rows = accumulateRows(mergeConfig, bounds, cells, rowCount)
  val floors = accumulateRows(mergeConfig.copy(axis = 1), bounds, rows, dimensions.z)
  val result = accumulateFloors(mergeConfig.copy(axis = 2), bounds, floors)
  return result
}
