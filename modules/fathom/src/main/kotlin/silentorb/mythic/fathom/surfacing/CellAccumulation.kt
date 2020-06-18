package silentorb.mythic.fathom.surfacing

import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.getCenter
import kotlin.math.abs

data class MergeConfig(
    val distanceTolerance: Float,
    val axis: Int,
    val boundaryRange: Float,
    val cellSize: Float
)

data class Clump(
    val first: Vector3,
    val second: Vector3,
    val middle: Vector3
)

fun getClumps(distanceTolerance: Float, firstVertices: List<Vector3>, secondVertices: List<Vector3>): List<Clump> =
    secondVertices
        .mapNotNull { b ->
          // TODO: This code may eventually need better neighbor selection for when there are multiple options
          val a = firstVertices.firstOrNull { it.distance(b) < distanceTolerance }
          if (a != null)
            Clump(a, b, getCenter(a, b))
          else
            null
        }

fun synchronizeEdges(vertexMap: List<Pair<Vector3, Vector3>>, edges: Edges): Edges =
    vertexMap.fold(edges) { accumulator, (from, to) ->
      accumulator.map { replaceEdgeVertex(it, from, to) }
    }

fun synchronizeEdges(clumps: List<Clump>, firstEdges: Edges, secondEdges: Edges): Pair<Edges, Edges> {
  val synchronizedFirstEdges = synchronizeEdges(clumps.map { Pair(it.first, it.middle) }, firstEdges)
  val synchronizedSecondEdges = synchronizeEdges(clumps.map { Pair(it.second, it.middle) }, secondEdges)
  return Pair(synchronizedFirstEdges, synchronizedSecondEdges)
}

fun getDuplicates(firstEdges: Edges, secondEdges: Edges): Edges =
    secondEdges
        .filter { second ->
          firstEdges.any { edgesMatch(second, it) }
        }

fun withoutDuplicates(comparison: Edges, pruning: Edges): Edges {
  val duplicates = getDuplicates(comparison, pruning)
  if (duplicates.any()) {
    val k = 0
  }
  return pruning.minus(duplicates)
}

fun mergeEdges(sharedVertices: List<Vector3>, firstEdges: Edges, secondEdges: Edges): Edges {
  val edgesNeedingUnifying = sharedVertices
      .mapNotNull { vertex ->
        val first = firstEdges.filter { edgeContains(it, vertex) }
        val second = secondEdges.filter { edgeContains(it, vertex) }
        if (first.size == 1 && second.size == 1) {
          val a = first.first()
          val b = second.first()
          val firstVector = getEdgeVector(a)
          val secondVector = getEdgeVector(b)
          val dot = firstVector.dot(secondVector)
          if (abs(dot) > 0.8f)
            Triple(a, b, vertex)
          else
            null
        } else
          null
      }

  val unifiedEdges = edgesNeedingUnifying
      .map { (first, second, middle) ->
        val a = getOtherVertex(first, middle)
        val b = getOtherVertex(second, middle)
        Edge(a, b)
      }

  val firstResult = firstEdges.minus(edgesNeedingUnifying.map { it.first })
  val secondResult = secondEdges.minus(edgesNeedingUnifying.map { it.second })
  val result = firstResult.plus(secondResult).plus(unifiedEdges)
  assert(result.size <= firstEdges.size + secondEdges.size)
  return result
}

fun mergeCells(config: MergeConfig, boundary: Float, first: Edges, second: Edges, firstVertices: List<Vector3>, secondVertices: List<Vector3>): Edges {
  val firstBoundary = boundary - config.boundaryRange
  val secondBoundary = boundary + config.boundaryRange
  val firstCandidates = firstVertices
      .filter { it[config.axis] > firstBoundary }

  val secondCandidates = secondVertices
      .filter { it[config.axis] < secondBoundary }

  val clumps = getClumps(config.distanceTolerance, firstCandidates, secondCandidates)
  val (synced1, synced2) = synchronizeEdges(clumps, first, second)
  val result = mergeEdges(clumps.map { it.middle }, synced1, withoutDuplicates(synced1, synced2))
  return result
}

fun accumulateRow(
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
      distanceTolerance = subCellSize * 2.5f,
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
