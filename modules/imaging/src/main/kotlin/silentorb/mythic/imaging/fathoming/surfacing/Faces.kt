package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.imaging.fathoming.DistanceFunction
import silentorb.mythic.imaging.fathoming.getNormal
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.getCenter
import kotlin.math.abs

data class Corner(
    val position: Vector3,
    val first: Edge,
    val second: Edge,
    val normal: Vector3
){
  init {
    assert(!normal.x.isNaN())
  }
}

typealias VertexEdgesMap = Map<Vector3, Edges>
typealias Corners = List<Corner>
typealias CornerMap = Map<Vector3, Corners>

fun getVertexEdgesMap(edges: Edges, vertices: List<Vector3>) =
    vertices
        .associateWith { position ->
          edges.filter { edgeContains(it, position) }
        }

fun getCornerNormal(first: Edge, second: Edge, b: Vector3): Vector3 {
  val a = getOtherVertex(first, b)
  val c = getOtherVertex(second, b)
  val d = (b - a).normalize()
  val e = (c - b).normalize()
  val result = d.cross(e)
  assert(!result.x.isNaN())
  return result
}

tailrec fun getVertexCorners(getDistance: DistanceFunction, position: Vector3, edges: Edges, accumulator: Corners): Corners =
    if (edges.none())
      accumulator
    else {
      val next = edges.first()
      val options = edges
          .minus(next)
          .map { option ->
            val nextVertices = getEdgeVertices(next)
            val center = getCenter(nextVertices.plus(getOtherVertex(option, position)))
            Pair(option, center)
          }

      val neighbors = if (options.size < 3)
        options
      else
        options
            .sortedBy { (_, center) ->
              getDistance(center)
            }
            .take(2)

      val nextAccumulator = accumulator
          .plus(
              neighbors.map { (neighbor, center) ->
                val faceNormal = getNormal(getDistance, center)
                val cornerNormal = getCornerNormal(next, neighbor, position)
                val dot = faceNormal.dot(cornerNormal)
                if (dot < 0)
                  Corner(position, next, neighbor, cornerNormal)
                else
                  Corner(position, neighbor, next, -cornerNormal)
              }
          )
      val nextEdges = edges
          .drop(1)
          .filter { edge -> nextAccumulator.count { edge == it.first || edge == it.second } < 2 }
      getVertexCorners(getDistance, position, nextEdges, nextAccumulator)
    }

fun getCorners(getDistance: DistanceFunction, vertexEdges: VertexEdgesMap): CornerMap {
  return vertexEdges
      .mapValues { (position, edges) ->
        getVertexCorners(getDistance, position, edges, listOf())
      }
}

tailrec fun accumulateFaceCorners(
    previousCorner: Corner,
    position: Vector3,
    cornerMap: CornerMap,
    faceCorners: Corners
): Corners {
  assert(faceCorners.any())
  val vertexEdges = cornerMap[position]!!
  return if (position == faceCorners.first().position) {
    assert(faceCorners.size > 2)
    faceCorners
  } else {
    val previousEdge = previousCorner.second
    val options = vertexEdges.filter { it.first == previousEdge }
    if (options.none()) {
      faceCorners
    } else {
      assert(options.any())
      val nextCorner = if (options.size == 1)
        options.first()
      else
        options.maxBy { option ->
          val dot = option.normal.dot(previousCorner.normal)
          abs(dot)
        }!!
      val nextFaceVertices = faceCorners.plus(nextCorner)
      val nextPosition = getOtherVertex(nextCorner.second, position)
      accumulateFaceCorners(nextCorner, nextPosition, cornerMap, nextFaceVertices)
    }
  }
}

fun removeUsedCorners(cornerMap: CornerMap, corners: Corners): CornerMap =
    corners.fold(cornerMap) { accumulator, corner ->
      val previous = accumulator[corner.position]!!
      accumulator.plus(Pair(corner.position, previous.minus(corner)))
    }

fun getFace(
    firstEdge: Edge,
    position: Vector3,
    cornerMap: CornerMap
): Pair<VertexFace?, CornerMap> {
  val corners = cornerMap[position]!!
  val corner = corners.firstOrNull { it.first == firstEdge }
  return if (corner == null)
    Pair(null, cornerMap)
  else {
    val nextPosition = getOtherVertex(corner.second, position)
    val faceCorners = accumulateFaceCorners(corner, nextPosition, cornerMap, listOf(corner))
    val updatedCornerMap = removeUsedCorners(cornerMap, faceCorners)
    val face = if (faceCorners.size > 2)
      faceCorners.map { it.position }
    else
      null
    Pair(face, updatedCornerMap)
  }
}

tailrec fun accumulateFaces(
    edges: Edges,
    corners: CornerMap,
    faces: List<VertexFace>
): List<VertexFace> =
    if (edges.none())
      faces
    else {
      val next = edges.first()
      val (firstFace, corners2) =
          getFace(next, next.second, corners)

      val (secondFace, corners3) =
          getFace(next, next.first, corners2)

      val nextFaces = faces.plus(listOfNotNull(firstFace, secondFace))
      accumulateFaces(edges.drop(1), corners3, nextFaces)
    }

fun getFaces(getDistance: DistanceFunction, edges: Edges, vertices: List<Vector3>): List<VertexFace> {
  val vertexEdges = getVertexEdgesMap(edges, vertices)
  val corners = getCorners(getDistance, vertexEdges)
  return accumulateFaces(edges, corners, listOf())
}

fun getFaces(getDistance: DistanceFunction, edges: Edges): List<VertexFace> {
  val vertexEdges = getVertexEdgesMap(edges, getVerticesFromEdges(edges))
  val corners = getCorners(getDistance, vertexEdges)
  return accumulateFaces(edges, corners, listOf())
}

//fun isFaceNormalCorrect(getDistance: DistanceFunction, face: VertexFace): Boolean {
//  val faceNormal = getFaceNormal(face)
//  val center = getCenter(face)
//  val distanceNormal = getNormal(getDistance, center)
//  val dot = distanceNormal.dot(faceNormal)
//  return dot > 0
//}

//fun correctFaceNormals(getDistance: DistanceFunction, faces: List<VertexFace>): List<VertexFace> =
//    faces.map { face ->
//      if (isFaceNormalCorrect(getDistance, face))
//        face
//      else
//        face.reversed()
//    }

//fun getAlignedFaces(getDistance: DistanceFunction, edges: Edges, vertices: List<Vector3>): List<VertexFace> {
//  val faces = getFaces(getDistance, edges, vertices)
//  return correctFaceNormals(getDistance, faces)
//}
