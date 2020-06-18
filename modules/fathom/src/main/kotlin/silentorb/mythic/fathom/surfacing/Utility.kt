package silentorb.mythic.fathom.surfacing

import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import silentorb.mythic.spatial.toVector3

fun isInsideBounds(bounds: DecimalBounds, position: Vector3) =
    position.x >= bounds.start.x &&
        position.y >= bounds.start.y &&
        position.z >= bounds.start.z &&
        position.x < bounds.end.x &&
        position.y < bounds.end.y &&
        position.z < bounds.end.z

fun getBoundsDimensions(bounds: GridBounds): Vector3i =
    bounds.end - bounds.start

fun getBoundsCellCount(bounds: GridBounds): Int {
  val dimensions = getBoundsDimensions(bounds)
  return dimensions.x * dimensions.y * dimensions.z
}

fun toDecimalBounds(scale: Float, bounds: GridBounds) =
    DecimalBounds(
        start = bounds.start.toVector3() * scale,
        end = bounds.end.toVector3() * scale
    )

fun edgeContains(edge: Edge, vertex: Vector3): Boolean =
    edge.first == vertex || edge.second == vertex

fun getEdgeVector(edge: Edge): Vector3 =
    (edge.second - edge.first).normalize()

fun getEdgeVertices(edge: Edge): List<Vector3> =
    listOf(edge.first, edge.second)

fun replaceEdgeVertex(edge: Edge, old: Vector3, replacement: Vector3): Edge =
    if (edge.first == old)
      Edge(replacement, edge.second)
    else if (edge.second == old)
      Edge(edge.first, replacement)
    else
      edge

fun getOtherVertex(edge: Edge, vertex: Vector3): Vector3 =
    if (vertex == edge.first)
      edge.second
    else
      edge.first

fun getFaceNormal(face: VertexFace): Vector3 {
  val second = face[1]
  val a = (second - face[0]).normalize()
  val b = (face[2] - second).normalize()
  return a.cross(b)
}

fun edgesMatch(a: Edge, b: Edge): Boolean =
    a == b || (a.second == b.first && a.first == b.second)
