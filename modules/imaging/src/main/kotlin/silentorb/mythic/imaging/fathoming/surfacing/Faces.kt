package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.imaging.fathoming.DistanceFunction
import silentorb.mythic.imaging.fathoming.getNormal
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.getCenter
import kotlin.math.abs

typealias FirstEdgeUses = Map<Edge, List<Edge>>
typealias SecondEdgeUses = Set<Edge>

data class VertexInfo(
    val index: Int,
    val edges: Edges
)

tailrec fun getFace(
    previousEdge: Edge,
    position: Vector3,
    vertices: Map<Vector3, VertexInfo>,
    firstEdgeUses: FirstEdgeUses,
    secondEdgeUses: SecondEdgeUses,
    normal: Vector3?,
    faceVertices: VertexFace,
    faceEdges: Edges
): Pair<VertexFace, Edges> {
  assert(faceVertices.any())
  val vertex = vertices[position]!!
  return if (position == faceVertices.first()) {
    assert(faceVertices.size > 2)
    Pair(faceVertices, faceEdges)
  } else {
    val options = vertex.edges
        .minus(previousEdge)
        .filter { option ->
          !secondEdgeUses.contains(option) && firstEdgeUses[option]?.contains(previousEdge)?.not() ?: true
        }
    assert(options.any())
    val nextNormal = if (options.size == 1)
      normal
    else if (faceVertices.size < 3)
      null
    else {
      normal ?: (faceVertices[1] - faceVertices[0]).normalize().cross((faceVertices[2] - faceVertices[1]).normalize())
    }
    val nextEdge = if (options.size == 1)
      options.first()
    else if (faceVertices.size < 3)
      options.first()
    else
      options.maxBy { option ->
        val otherVertex = getOtherVertex(option, position)
        val a = (faceVertices[1] - faceVertices[0]).normalize()
        val b = (otherVertex - faceVertices.last()).normalize()
        val optionNormal = a.cross(b)
        val dot = optionNormal.dot(nextNormal!!)
        abs(dot)
      }!!

    val nextFaceVertices = faceVertices.plus(position)
    val nextPosition = getOtherVertex(nextEdge, position)
    val nextFaceEdges = faceEdges.plus(nextEdge)
    getFace(nextEdge, nextPosition, vertices, firstEdgeUses, secondEdgeUses, nextNormal, nextFaceVertices, nextFaceEdges)
  }
}

fun getFace(
    next: Edge,
    vertices: Map<Vector3, VertexInfo>,
    firstEdgeUses: FirstEdgeUses,
    secondEdgeUses: SecondEdgeUses
): Triple<VertexFace?, FirstEdgeUses, SecondEdgeUses> {
  val (firstFace, faceEdges1) = if (!secondEdgeUses.contains(next))
    getFace(next, next.second, vertices, firstEdgeUses, secondEdgeUses, null, listOf(next.first), listOf(next))
  else
    Pair<VertexFace?, Edges>(null, listOf())

  val firstEdgeUses2 = firstEdgeUses.plus(
      faceEdges1.associateWith { faceEdges1 }
  )

  val secondEdgeUses2 = secondEdgeUses.plus(
      faceEdges1.filter { firstEdgeUses.containsKey(it) }
  )

  return Triple(firstFace, firstEdgeUses2, secondEdgeUses2)
}

tailrec fun accumulateFaces(
    edges: Edges,
    vertices: Map<Vector3, VertexInfo>,
    firstEdgeUses: FirstEdgeUses,
    secondEdgeUses: SecondEdgeUses,
    faces: List<VertexFace>
): List<VertexFace> =
    if (edges.none())
      faces
    else {
      val next = edges.first()
      val (firstFace, firstEdgeUses2, secondEdgeUses2) =
          getFace(next, vertices, firstEdgeUses, secondEdgeUses)

      val (secondFace, firstEdgeUses3, secondEdgeUses3) =
          getFace(next, vertices, firstEdgeUses2, secondEdgeUses2)

      val nextFaces = faces.plus(listOfNotNull(firstFace, secondFace))
      accumulateFaces(edges.drop(1), vertices, firstEdgeUses3, secondEdgeUses3, nextFaces)
    }

fun getFaces(edges: Edges, vertices: List<Vector3>): List<VertexFace> {
  val vertexEdges = vertices
      .mapIndexed { index, position ->
        val vertex = VertexInfo(
            index = index,
            edges = edges.filter { edgeContains(it, position) }
        )
        Pair(position, vertex)
      }
      .associate { it }

  return accumulateFaces(edges, vertexEdges, mapOf(), setOf(), listOf())
}

fun isFaceNormalCorrect(getDistance: DistanceFunction, face: VertexFace): Boolean {
  val faceNormal = getFaceNormal(face)
  val center = getCenter(face)
  val distanceNormal = getNormal(getDistance, center)
  val dot = distanceNormal.dot(faceNormal)
  return dot > 0
}

fun correctFaceNormals(getDistance: DistanceFunction, faces: List<VertexFace>): List<VertexFace> =
    faces.map { face ->
      if (isFaceNormalCorrect(getDistance, face))
        face
      else
        face.reversed()
    }

fun getAlignedFaces(getDistance: DistanceFunction, edges: Edges, vertices: List<Vector3>): List<VertexFace> {
  val faces = getFaces(edges, vertices)
  return correctFaceNormals(getDistance, faces)
}

fun vertexFacesToIndexFaces(vertices: Array<Vector3>, faces: List<VertexFace>): List<IndexedFace> {
  val vertexMap = vertices
      .mapIndexed { index, position ->
        Pair(position, index)
      }
      .associate { it }

  return faces.map { face ->
    face.map { vertex ->
      vertexMap[vertex]!!
    }
  }
}
