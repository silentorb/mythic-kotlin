package mythic.sculpting

import mythic.spatial.*
import org.joml.unaryMinus

typealias Vertices3m = List<Vector3m>
typealias Edges3m = List<FlexibleEdge>
typealias Faces3m = List<FlexibleFace>

fun skinLoop(mesh: FlexibleMesh, first: List<Vector3m>, second: List<Vector3m>): Faces3m {
  return (0 until first.size).map { a ->
    val b = if (a == first.size - 1) 0 else a + 1
    mesh.createStitchedFace(listOf(
        first[b], first[a],
        second[a], second[b]
    ))
  }
}

fun skin(mesh: FlexibleMesh, first: List<Vector3m>, second: List<Vector3m>) {
  val sides = (0 until first.size - 1).map { a ->
    val b = a + 1
    mesh.createStitchedFace(
        if (a == 0 && first[a] == first[b])
//          listOf(first[a], second[a], second[b])
          listOf(second[b], second[a], first[a])
        else if (second.size == 1) //a == first.size - 1 && second[a] == second[b])
//          listOf(first[b], first[a], second[0])
          listOf(second[0], first[a], first[b])
        else
//          listOf(first[b], first[a], second[a], second[b])
          listOf(second[b], second[a], first[a], first[b]).distinct()
    )
  }
}

fun extrudeBasic(mesh: FlexibleMesh, face: FlexibleFace, transform: Matrix): Faces3m {
  val newVertices = face.vertices
      .reversed()
      .map { it.transform(transform) }
  val secondFace = mesh.createFace(newVertices)
  val secondVertices = secondFace.vertices.reversed()
  return skinLoop(mesh, face.vertices, secondVertices)
}

inline fun nearly(value: Float, target: Float = 0f) =
    value < target + 0.000001f
        && value > target - 0.000001f

fun keepOrRotate(matrix: Matrix, input: Vector3m): Vector3m =
    if (nearly(input.x, 0f) && nearly(input.y, 0f))
      input
    else
      input.transform(matrix)

fun lathe(mesh: FlexibleMesh, path: List<Vector3m>, count: Int, sweep: Float = Pi * 2) {
  val increment = sweep / count
  var previous = path
  for (i in 1 until count) {
    val matrix = Matrix().rotateZ(i * increment)
    val next = path.map { keepOrRotate(matrix, it) }
    skin(mesh, previous, next)
    previous = next
  }
  skin(mesh, previous, path)
}

//fun interpolatePaths(firstPath: Vertices3m, secondPath: Vertices3m, weight: Float): Vertices3m {
//  val secondIterator = secondPath.iterator()
//  return firstPath.map { secondIterator.next() * weight + it * (1 - weight) }
//}
//
//fun interpolate(first: Float, second: Float, weight: Float)=
//

fun sawRange(x: Float) =
    Math.abs((x % 2) - 1)

fun sineRange(x: Float): Float =
    Math.sin((x * Pi * 2 - Pi * 0.5f).toDouble()).toFloat() * 0.5f + 0.5f

/* 0 >= i >= 1 */
fun bezierSample(i: Float, points: List<Vector2>) {

}

data class SwingInfo(
    val index: Int,
    val point: Vector3m,
    val scale: Vector3m,
    val rangeZ: Float
)
typealias Swings = List<Swing>

typealias SwingsOld = List<SwingInfo>

fun minOrOne(first: Float, second: Float): Float {
  val result = Math.min(first, second)
  return if (result == 0f)
    1f
  else
    result
}

fun createSwings2(firstPath: Vertices3m, secondPath: Vertices3m): SwingsOld {
  val secondIterator = secondPath.iterator()
  return firstPath
      .mapIndexed { i, it -> Pair(i, it) }
      .filter { it.second.x != 0f }
      .map {
        val point = it.second
        val other = secondIterator.next()
        val shortestX = minOrOne(point.x, other.x)
//    val lowestZ = minOrOne(it.z, other.z)
//    val highestZ = Math.max(it.z, other.z)
        val scale = Vector3m(point.x / shortestX, other.x / shortestX, 1f)
        val rangeZ = if (point.z == other.z)
          1f
        else {
          val widestX = Math.max(point.x, other.x)
          (other.z - point.z) / widestX
        }

        SwingInfo(it.first, Vector3m(shortestX, 0f, point.z), scale, rangeZ)
      }
}

fun createInterpolatedSwing(point: Vector3m, other: Vector3m): Swing {
  val shortestX = minOrOne(point.x, other.x)
//    val lowestZ = minOrOne(it.z, other.z)
//    val highestZ = Math.max(it.z, other.z)
  val scale = Vector3m(point.x / shortestX, other.x / shortestX, 1f)
  val rangeZ = if (point.z == other.z)
    1f
  else {
    val widestX = Math.max(point.x, other.x)
    (other.z - point.z) / widestX
  }

  val point2 = Vector3m(shortestX, 0f, point.z)
  return { matrix: Matrix ->
    val point3 = point2.transform(matrix)
    point3 * scale// + Vector3m(0f, 0f, swing.rangeZ * point.y)
  }
}

fun createSwings(firstPath: Vertices3m, secondPath: Vertices3m): Swings {
  val secondIterator = secondPath.iterator()
  return firstPath
      .mapIndexed { i, point ->
        val other = secondIterator.next()
        if (point.x == 0f && other.x == 0f) {
          val result = { _: Matrix -> point }
          result
        } else {
          createInterpolatedSwing(point, other)
        }
//        SwingInfo(it.first, Vector3m(shortestX, 0f, point.z), scale, rangeZ)
      }
}

fun mapPivots(path: Vertices3m, pivots: Vertices3m) =
    path.map {
      val pivot = pivots.firstOrNull { p -> p == it }
      if (pivot != null)
        pivot
      else
        it
    }

fun transformSwing(pivots: Vertices3m, matrix: Matrix, swing: SwingInfo): Vector3m {
  val pivot = pivots.firstOrNull { p -> p == swing.point }
  return if (pivot != null)
    pivot
  else {
    val point = swing.point.transform(matrix)
    point * swing.scale// + Vector3m(0f, 0f, swing.rangeZ * point.y)
  }
}

data class LatheCourse(
    val stepCount: Int,
    val wrap: Boolean,
    val transformer: (Int) -> Matrix
)

fun createLatheCourse(resolution: Int, sweep: Float = Pi * 2): LatheCourse {
  val count = (resolution * sweep / (Pi / 2)).toInt() + 1
  val increment = sweep / (count - 1)
  return LatheCourse(
      stepCount = count,
      transformer = { i: Int -> Matrix().rotateZ(i * increment) },
      wrap = sweep == Pi * 2
  )
}

//fun latheTwoPaths(mesh: FlexibleMesh, latheCourse: LatheCourse, firstPath: Vertices3m, secondPath: Vertices3m) {
////  val pivots = cloneVertices(firstPath.intersect(secondPath).filter { it.x == 0f })
//  val firstLastPath = mapPivots(firstPath, pivots)
//  var previous = firstLastPath
//  val swings = createSwings(firstPath, secondPath)
//  for (i in 1 until latheCourse.stepCount) {
//    val matrix = latheCourse.transformer(i)
//    val next = swings.map { transformSwing(pivots, matrix, it) }
//    skin(mesh, previous, next)
//    previous = next
//  }
//
//  if (latheCourse.wrap)
//    skin(mesh, previous, firstLastPath)
//}

typealias Swing = (globalTransform: Matrix) -> Vector3m

fun mapSwings(swings: Swings, globalTransform: Matrix) =
    swings.map { it(globalTransform) }

fun latheTwoPaths(mesh: FlexibleMesh, latheCourse: LatheCourse, firstPath: Vertices3m, secondPath: Vertices3m) {
  val swings = createSwings(firstPath, secondPath)
  val firstLastPath = mapSwings(swings, latheCourse.transformer(0))
  var previous = firstLastPath

  for (i in 1 until latheCourse.stepCount) {
    val matrix = latheCourse.transformer(i)
    val next = mapSwings(swings, matrix)
    skin(mesh, previous, next)
    previous = next
  }

  if (latheCourse.wrap)
    skin(mesh, previous, firstLastPath)
}

fun transformVertices(matrix: Matrix, vertices: Vertices3m): Vertices3m {
  for (vertex in vertices) {
    vertex.set(vertex.transform(matrix))
  }
  return vertices
}

fun transformFaces(matrix: Matrix, faces: Faces3m): Vertices3m {
  return transformVertices(matrix, distinctVertices(faces.flatMap { it.vertices }))
}

//fun transformVertices2D(matrix: Matrix, vertices: List<Vector2>) {
////  vertices.forEach { it.set(it.transform(matrix)) }
//  for (vertex in vertices) {
//    vertex.set(vertex.transform(matrix))
//  }
//}

fun distortedTranslatePosition(offset: Vector3m, vertices: List<Vector3m>) {
  transformVertices(Matrix().translate(offset), vertices)
}

fun distortedTranslatePosition(offset: Vector3m, mesh: FlexibleMesh) {
  transformVertices(Matrix().translate(offset), mesh.redundantVertices)
}

fun transformMesh(mesh: FlexibleMesh, matrix: Matrix) {
  transformVertices(matrix, mesh.distinctVertices)
}

fun translateMesh(mesh: FlexibleMesh, offset: Vector3) {
  transformVertices(Matrix().translate(offset), mesh.distinctVertices)
}

fun alignToFloor(vertices: List<Vector3m>, floor: Float = 0f) {
  val lowest = vertices.map { it.z }.sorted().first()
  distortedTranslatePosition(Vector3m(0f, 0f, floor - lowest), vertices)
}

fun alignToFloor(mesh: FlexibleMesh, floor: Float = 0f) {
  alignToFloor(mesh.distinctVertices, floor)
}

fun alignToCeiling(vertices: List<Vector3m>, ceiling: Float = 0f) {
  val highest = vertices.map { it.z }.sorted().last()
  distortedTranslatePosition(Vector3m(0f, 0f, ceiling - highest), vertices)
}

data class VerticalDimensions(
    val top: Float,
    val bottom: Float,
    val height: Float = top - bottom
)

fun getPathDimensions(path: Vertices3m): VerticalDimensions {
  val sorted = path.map { it.y }.sorted()
  val bottom = sorted.first()
  val top = sorted.last()
  return VerticalDimensions(top, bottom)
}

fun cloneVertices(vertices: Collection<Vector3m>): Vertices3m =
    vertices.map { Vector3m(it) }

fun flipVertical(vertices: Vertices3m): Vertices3m {
  val middle = vertices.map { it.z }.average().toFloat()
  return vertices.map { Vector3m(it.x, it.y, middle - (it.z - middle)) }
}

fun joinPaths(verticalGap: Float, first: Vertices3m, second: Vertices3m): Vertices3m {
  val firstCopy = cloneVertices(first)
  val secondCopy = cloneVertices(second)
  val half = verticalGap * 2
  alignToFloor(firstCopy, half)
  alignToCeiling(secondCopy, -half)
  return firstCopy.plus(secondCopy)
}

fun convertAsXZ(vertices: List<Vector2>) =
    vertices.map { Vector3m(it.x, 0f, it.y) }

fun setAnchor(anchor: Vector3m, vertices: Vertices3m) {
  distortedTranslatePosition(-anchor, vertices)
}

fun stitchEdges(a: EdgeReference, b: EdgeReference) {
  throw Error("Needs new code.")

//  a.edges.add(b)
//  b.edges.add(a)
//  b.first = a.second
//  b.second = a.first
//  b.next!!.first = b.second
//  b.previous!!.second = b.first
}

fun stitchEdgeLoops(firstLoop: List<EdgeReference>, secondLoop: List<EdgeReference>) {
  val secondIterator = secondLoop.listIterator()

  for (a in firstLoop) {
    val b = secondIterator.next()
    stitchEdges(a, b)
  }
}

fun mirrorAlongY(mesh: FlexibleMesh): Faces3m {
  val nearAxis = mesh.distinctVertices.filter { nearly(it.y) }
  nearAxis.forEach { it.y = 0f }
  val existingVertices = nearAxis.toMutableList()
  val newFaces = mesh.faces.toList().map { original ->
    val newVertices = original.vertices.map {
      val vertex = Vector3m(it.x, -it.y, it.z)
      val existing = existingVertices.find { it == vertex }
      if (existing != null) {
        existing
      } else {
        existingVertices.add(vertex)
        vertex
      }
    }.reversed()
    mesh.createStitchedFace(newVertices)
  }

  return newFaces
}
