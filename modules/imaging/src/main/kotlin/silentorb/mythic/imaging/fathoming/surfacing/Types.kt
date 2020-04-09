package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.imaging.fathoming.DistanceFunction
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i

data class SurfacingConfig(
    val getDistance: DistanceFunction,
    val normalTolerance: Float,
    val cellSize: Float,
    val subCells: Int // The number of subcells along a single dimensions of a cell
)

data class DecimalBounds(
    val start: Vector3,
    val end: Vector3
)

data class GridBounds(
    val start: Vector3i,
    val end: Vector3i
) {
  fun pad(amount: Int) =
      GridBounds(
          start = start - amount,
          end = end + amount
      )
}

data class Contour(
    val strength: Float,
    val direction: Vector3,
    val position: Vector3,
    val normal: Vector3,
    val firstSample: SubSample,
    val secondSample: SubSample
)

typealias Contours = List<Contour>

typealias LineAggregate = List<Contour>
typealias LineAggregates = List<LineAggregate>

data class Edge(
    val first: Vector3,
    val second: Vector3
) {
  init {
    if (first == second) {
      val k = 0
    }
  }
}

typealias Edges = List<Edge>

data class ContourMesh(
    val vertices: List<Vector3>,
    val edges: Edges
)

typealias IndexedFace = List<Int>

typealias VertexFace = List<Vector3>
