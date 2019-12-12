package mythic.sculpting

import mythic.spatial.BoundingBox
import mythic.spatial.Vector3
import mythic.spatial.Vector3m
import org.joml.plus

typealias EdgeExplorer = (EdgeReference) -> EdgeReference?

fun gatherEdges(explore: EdgeExplorer, edge: EdgeReference): List<EdgeReference> {
  val result = mutableListOf<EdgeReference>()
  var current = edge
  var i = 0
  do {
    result.add(current)
    val next = explore(current)
    if (next == null)
      break

    current = next
    if (i++ > 20)
      break

  } while (current != edge)

  return result
}

val edgeLoopNext: EdgeExplorer = { edge ->
  if (edge.next!!.otherEdgeReferences.size == 0)
    null
  else
    edge.next!!.otherEdgeReferences[0].next!!
}

val edgeLoopReversedNext: EdgeExplorer = { edge ->
  if (edge.previous!!.otherEdgeReferences.size == 0)
    null
  else
    edge.previous!!.otherEdgeReferences[0].previous!!
}

fun getEdgeLoop(edge: EdgeReference): List<EdgeReference> = gatherEdges(edgeLoopNext, edge)
fun getEdgeLoopReversed(edge: EdgeReference): List<EdgeReference> = gatherEdges(edgeLoopReversedNext, edge)

fun getEdgesCenter(edges: List<EdgeReference>) =
    edges.map { it.first }.reduce { a, b -> a + b } / edges.size.toFloat()


fun getVerticesCenter(vertices: List<Vector3>): Vector3 {
  if (vertices.isEmpty())
    return Vector3()

  var result = Vector3()
  for (vertex in vertices) {
    result += vertex
  }
  return result / vertices.size.toFloat()
}

fun getBounds(vertices: List<Vector3>): BoundingBox {
  return BoundingBox(
      Vector3m(
          vertices.minBy { it.x }!!.x,
          vertices.minBy { it.y }!!.y,
          vertices.minBy { it.z }!!.z
      ),
      Vector3m(
          vertices.maxBy { it.x }!!.x,
          vertices.maxBy { it.y }!!.y,
          vertices.maxBy { it.z }!!.z
      )
  )
}

