package silentorb.mythic.imaging.fathoming.surfacing.old

import silentorb.mythic.spatial.Vector3
import java.lang.Float.min
import java.lang.Math.pow
import java.util.*
import kotlin.math.abs

typealias SymmetricMatrix = FloatArray

fun newSymmetricMatrix(): SymmetricMatrix =
    FloatArray(10)

fun newSymmetricMatrix(a: Float, b: Float, c: Float, d: Float) =
    floatArrayOf(
        a * a, a * b, a * c, a * d,
        b * b, b * c, b * d,
        c * c, c * d,
        d * d
    )

fun addSymmetricMatrices(a: SymmetricMatrix, b: SymmetricMatrix): SymmetricMatrix =
    floatArrayOf(
        a[0] + b[0], a[1] + b[1], a[2] + b[2], a[3] + b[3],
        a[4] + b[4], a[5] + b[5], a[6] + b[6],
        a[7] + b[7], a[8] + b[8],
        a[9] + b[9]
    )

fun det(
    m: SymmetricMatrix,
    a11: Int, a12: Int, a13: Int,
    a21: Int, a22: Int, a23: Int,
    a31: Int, a32: Int, a33: Int): Float =
    m[a11] * m[a22] * m[a33] + m[a13] * m[a21] * m[a32] + m[a12] * m[a23] * m[a31] -
        m[a13] * m[a22] * m[a31] - m[a11] * m[a23] * m[a32] - m[a12] * m[a21] * m[a33]

fun barycentric(p: Vector3, a: Vector3, b: Vector3, c: Vector3): Vector3 {
  val v0: Vector3 = b - a
  val v1: Vector3 = c - a
  val v2: Vector3 = p - a
  val d00 = v0.dot(v0)
  val d01 = v0.dot(v1)
  val d11 = v1.dot(v1)
  val d20 = v2.dot(v0)
  val d21 = v2.dot(v1)
  val denom = d00 * d11 - d01 * d01
  val v = (d11 * d20 - d01 * d21) / denom
  val w = (d00 * d21 - d01 * d20) / denom
  val u = 1.0f - v - w
  return Vector3(u, v, w)
}

fun interpolate(p: Vector3, a: Vector3, b: Vector3, c: Vector3, attrs: Array<Vector3>): Vector3 {
  val bary: Vector3 = barycentric(p, a, b, c)
  return attrs[0] * bary.x + attrs[1] * bary.y + attrs[2] * bary.z
}

private const val NONE = 0
private const val NORMAL = 2
private const val TEXCOORD = 4
private const val COLOR = 8

data class Triangle(
    val indices: IntArray, // 3
    var deleted: Boolean = false,
//    val dirty: Boolean,
    val attributes: Int = 0,
    var normal: Vector3 = Vector3.zero,
    val uvs: ArrayList<Vector3> = arrayListOf()
//    val material: Int
)

data class Vertex(
    var position: Vector3,
    var start: Int = 0,
    var count: Int = 0,
    var q: SymmetricMatrix = newSymmetricMatrix(),
    var border: Int = 0
)

fun newVertex(position: Vector3) =
    Vertex(
        position = position,
        start = 0,
        count = 0,
        q = newSymmetricMatrix(),
        border = 0
    )

typealias Vertices = List<Vertex>

data class Reference(
    var index: Int,
    var vertex: Int
)

data class Mesh(
    var vertices: Vertices,
    var triangles: Array<Triangle>
//    val indices: IntArray,
//    val dirty: BooleanArray,
//    val deleted: BooleanArray,
)

//fun newMesh(vertices: FloatArray, indices: IntArray, vertexFloatSize: Int): Mesh {
//  val triangleCount = vertices.size / vertexFloatSize
//
//  return Mesh(
//      vertices = (0 until triangleCount).map { i ->
//        newVertex(Vector3(vertices[i + 1], vertices[i + 2], vertices[i + 3]))
//      },
//      indices = indices,
////      dirty = BooleanArray(triangleCount),
////      deleted = BooleanArray(triangleCount),
//      references = arrayListOf()
//  )
//}

fun pruneTriangles(triangles: Array<Triangle>): Array<Triangle> =
    triangles
        .filter { !it.deleted }
        .toTypedArray()

fun buildReferences(mesh: Mesh, references: ArrayList<Reference>) {
  val vertices = mesh.vertices
  val triangles = mesh.triangles

  for (vertex in vertices) {
    vertex.start = 0
    vertex.count = 0
  }

  for (triangle in triangles) {
    for (index in triangle.indices) {
      ++vertices[index].count
    }
  }

  var start = 0
  for (vertex in vertices) {
    vertex.start = start
    start += vertex.count
    vertex.count = 0
  }

  references.ensureCapacity(triangles.size * 3)
  // Not efficient at the moment but just trying to get things working.
  // In the long run this code is extremely convoluted and after
  // refactoring such hacks won't be needed
  for (i in references.size until triangles.size * 3) {
    references.add(Reference(0, 0))
  }
  triangles.forEachIndexed { i, triangle ->
    triangle.indices.forEachIndexed { j, index ->
      val vertex = vertices[index]
      val reference = references[vertex.start + vertex.count]
      reference.index = i
      reference.vertex = j
      vertex.count++
    }
  }
}

fun updateMesh(mesh: Mesh, references: ArrayList<Reference>) {
  mesh.triangles = pruneTriangles(mesh.triangles)

  buildReferences(mesh, references)
}

fun setBorders(mesh: Mesh, references: ArrayList<Reference>) {
  val vertices = mesh.vertices
  val triangles = mesh.triangles

  for (vertex in vertices) {
    vertex.border = 0
  }
  val vertexCount = ArrayList<Int>()
  val vertexIds = ArrayList<Int>()

  for (vertex in vertices) {
    vertexCount.clear()
    vertexIds.clear()
    for (j in 0 until vertex.count) {
      val k = references[vertex.start + j].index
      val triangle = triangles[k]
      for (index in triangle.indices) {
        var offset = 0
        while (offset < vertexCount.size) {
          if (vertexIds[offset] == index)
            break
          offset++
        }
        if (offset == vertexCount.size) {
          vertexCount.add(1)
          vertexIds.add(index)
        } else {
          vertexCount[offset]++
        }
      }
    }

    for (j in 0 until vertexCount.size) {
      if (vertexCount[j] == 1) {
        vertices[vertexIds[j]].border = 1
      }
    }
  }
}

fun initializeMesh(mesh: Mesh, references: ArrayList<Reference>, errors: FloatArray) {
  val vertices = mesh.vertices
  val triangles = mesh.triangles

  for (vertex in vertices) {
    vertex.q = newSymmetricMatrix()
  }

  for (triangle in triangles) {
    val p = triangle.indices
        .map { vertices[it].position }

    val n = (p[1] - p[0]).cross(p[2] - p[0]).normalize()
    triangle.normal = n
    for (index in triangle.indices) {
      val vertex = vertices[index]
      vertex.q = addSymmetricMatrices(vertex.q, newSymmetricMatrix(n.x, n.y, n.z, -n.dot(p[0])))
    }
  }

  triangles.forEachIndexed { i, triangle ->
    val k = i * 4
    for (j in 0 until 3) {
      errors[k + j] = calculateError(vertices, triangle.indices[j], triangle.indices[(j + 1) % 3]).second
    }
    errors[k + 3] = min(errors[k], min(errors[k + 1], errors[k + 2]))
  }

  buildReferences(mesh, references)
  setBorders(mesh, references)
}

fun vertexError(q: SymmetricMatrix, x: Float, y: Float, z: Float): Float =
    q[0] * x * x + 2 * q[1] * x * y + 2 * q[2] * x * z + 2 * q[3] * x + q[4] * y * y +
        2 * q[5] * y * z + 2 * q[6] * y + q[7] * z * z + 2 * q[8] * z + q[9]

fun vertexError(q: SymmetricMatrix, value: Vector3): Float =
    vertexError(q, value.x, value.y, value.z)

fun calculateError(vertices: Vertices, id_v1: Int, id_v2: Int): Pair<Vector3?, Float> {
  val firstVertex = vertices[id_v1]
  val secondVertex = vertices[id_v2]
  val q = addSymmetricMatrices(firstVertex.q, secondVertex.q)
  val border: Boolean = (firstVertex.border and secondVertex.border) != 0
  val det = det(q, 0, 1, 2, 1, 4, 5, 2, 5, 7)
  return if (det != 0f && !border) {
    val vector = Vector3(
        x = -1 / det * det(q, 1, 2, 3, 4, 5, 6, 5, 7, 8),
        y = -1 / det * det(q, 0, 2, 3, 1, 5, 6, 2, 7, 8),
        z = -1 / det * det(q, 0, 1, 3, 1, 4, 6, 2, 5, 8)
    )
    Pair(vector, vertexError(q, vector))
  } else {
    val vertex1 = vertices[id_v1].position
    val vertex2 = vertices[id_v2].position
    val vertex3 = (vertex1 + vertex2) / 2f
    val error1 = vertexError(q, vertex1)
    val error2 = vertexError(q, vertex2)
    val error3 = vertexError(q, vertex3)
    val error = min(error1, min(error2, error3))
    val point = when (error) {
      error1 -> vertex1
      error2 -> vertex2
      error3 -> vertex3
      else -> null
    }
    Pair(point, error)
  }
}

fun flipped(
    mesh: Mesh, position: Vector3,
    index: Int, vertex: Vertex,
    references: ArrayList<Reference>, deleted: BooleanArray
): Boolean {
  for (k in 0 until vertex.count) {
    val triangleIndex = references[vertex.start + k].index
    if (deleted[triangleIndex])
      continue

    val something = references[vertex.start + k].vertex
    val triangle = mesh.triangles[triangleIndex]
    val firstId = triangle.indices[(something + 1) % 3]
    val secondId = triangle.indices[(something + 2) % 3]
    if (firstId == index || secondId == index) {
      deleted[k] = true
      continue
    }
    val d1 = (mesh.vertices[firstId].position - position).normalize()
    val d2 = (mesh.vertices[secondId].position - position).normalize()
    if (abs(d1.dot(d2)) > 0.999)
      return true

    val normal = d1.cross(d2).normalize()
    deleted[k] = false
    if (normal.dot(triangle.normal) < 0.2f)
      return true
  }
  return false
}

fun updateTriangles(mesh: Mesh, references: ArrayList<Reference>, dirty: BooleanArray, errors: FloatArray, index: Int, vertex: Vertex, deleted: BooleanArray): Int {
  var localDeletionCount = 0
  for (k in 0 until vertex.count) {
    val reference = references[vertex.start + k]
    val triangle = mesh.triangles[reference.index]
    if (triangle.deleted)
      continue

    if (deleted[k]) {
      triangle.deleted = true
      ++localDeletionCount
    }

    triangle.indices[reference.vertex] = index
    dirty[k] = true
    val errorOffset = k * 4
    val indices = triangle.indices
    val error1 = calculateError(mesh.vertices, indices[0], indices[1]).second
    val error2 = calculateError(mesh.vertices, indices[1], indices[2]).second
    val error3 = calculateError(mesh.vertices, indices[2], indices[0]).second
    errors[errorOffset + 0] = error1
    errors[errorOffset + 1] = error2
    errors[errorOffset + 2] = error3
    errors[errorOffset + 3] = min(error1, min(error2, error3))
    references.add(reference)
  }
  return localDeletionCount
}

fun compactMesh(mesh: Mesh): Mesh {
  val triangles = mesh.triangles
  val vertices = mesh.vertices

  for (vertex in mesh.vertices) {
    vertex.count = 0
  }

  val survivingTriangles = pruneTriangles(triangles)

  for (triangle in survivingTriangles) {
    for (index in triangle.indices) {
      vertices[index].count = 1
    }
  }

  var survivingVertexCount = 0
  for (vertex in vertices) {
    if (vertex.count > 0) {
      vertex.start = survivingVertexCount
      vertices[survivingVertexCount].position = vertex.position
      survivingVertexCount++
    }
  }

  for (triangle in survivingTriangles) {
    for (j in 0 until 3) {
      triangle.indices[j] = vertices[triangle.indices[j]].start
    }
  }

  return Mesh(
      triangles = survivingTriangles,
      vertices = vertices.take(survivingVertexCount)
  )
}

fun simplifyMesh(targetCount: Int, aggressiveness: Float, mesh: Mesh): Mesh {
  val vertices = mesh.vertices
  val triangles = mesh.triangles
  val triangleCount = mesh.triangles.size
  val references = ArrayList<Reference>()
  val dirty = BooleanArray(triangleCount)
  var deletedTriangleCount = 0
  val deleted0 = BooleanArray(triangleCount)
  val deleted1 = BooleanArray(triangleCount)

  val errors = FloatArray(triangleCount * 4)

  initializeMesh(mesh, references, errors)
  for (iteration in 0 until 100) {
    if (triangleCount - deletedTriangleCount <= targetCount)
      break

    if (iteration != 0 && iteration and 5 == 0) {
      updateMesh(mesh, references)
//      deleted0.fill(false)
//      deleted1.fill(false)
    }

    Arrays.fill(dirty, false)

    val threshold = 0.000000001f * pow(iteration.toDouble() + 3, aggressiveness.toDouble()).toFloat()

    for (i in 0 until triangleCount) {
      val triangle = triangles[i]
      val errorOffset = i * 4
      if (triangle.deleted || dirty[i] || errors[errorOffset + 3] > threshold)
        continue

      for (j in 0 until 3) {
        if (errors[errorOffset + j] < threshold) {
          val index0 = triangle.indices[j]
          val vertex0 = vertices[index0]
          val index1 = triangle.indices[(j + 1) % 3]
          val vertex1 = vertices[index1]

          if (vertex0.border != vertex1.border)
            continue

          val (point) = calculateError(vertices, index0, index1)
          if (point != null) {
            if (flipped(mesh, point, index1, vertex0, references, deleted0))
              continue

            if (flipped(mesh, point, index0, vertex1, references, deleted1))
              continue

            // TODO: Update uvs

            vertex0.position = point
            vertex0.q = addSymmetricMatrices(vertex1.q, vertex0.q)
            val start = references.size
            deletedTriangleCount += updateTriangles(mesh, references, dirty, errors, index0, vertex0, deleted0)
            deletedTriangleCount += updateTriangles(mesh, references, dirty, errors, index0, vertex1, deleted1)

            val count = references.size - start
            if (count <= vertex0.count) {
              if (count > 0) {
//                System.arraycopy(references, vertex0.start, references, start, count)
                for (s in (0 until count)) {
                  references[start + s] = references[vertex0.start + s]
                }
              }
            } else {
              vertex0.start = start
              vertex0.count = count
              break
            }

            if (triangleCount - deletedTriangleCount <= targetCount)
              break
          }
        }
      }
    }
  }

  return compactMesh(mesh)
}
