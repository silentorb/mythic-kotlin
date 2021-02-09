package silentorb.mythic.shapemeshes

import silentorb.mythic.scenery.*
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.createArcZ

data class IntermediateMesh(
    val vertices: List<Vector3>,
    val triangles: List<Int>
)

private val boxLength = 1f

private val boxVertices = listOf(
    Vector3(boxLength, boxLength, boxLength),
    Vector3(boxLength, -boxLength, boxLength),
    Vector3(-boxLength, -boxLength, boxLength),
    Vector3(-boxLength, boxLength, boxLength),

    Vector3(boxLength, boxLength, -boxLength),
    Vector3(boxLength, -boxLength, -boxLength),
    Vector3(-boxLength, -boxLength, -boxLength),
    Vector3(-boxLength, boxLength, -boxLength)
)

private fun square(a: Int, b: Int, c: Int, d: Int): List<Int> =
    listOf(a, b, c, a, c, d)

private fun box(halfExtents: Vector3) =
    IntermediateMesh(
        vertices = boxVertices.map { it * halfExtents },
        triangles = listOf(
            square(0, 1, 2, 3), //  top
            square(3, 2, 6, 7),
            square(2, 1, 5, 6),
            square(1, 0, 4, 5),
            square(0, 3, 7, 4),
            square(4, 7, 6, 5) // bottom
        ).flatten()
    )

private fun cylinder(shape: Cylinder): IntermediateMesh {
  val count = 8
  val arc = createArcZ(shape.radius, 8, offset = 1f)
  val wrap = { i: Int -> i % count }
  val pieSlice = { middleIndex: Int, offset: Int ->
    (0 until count).flatMap { i ->
      listOf(middleIndex, i + offset, offset + wrap(i + 1)).reversed()
    }
  }
  return IntermediateMesh(
      vertices = listOf(
          Vector3(0f, 0f, 1f),
          Vector3(0f, 0f, -1f)
      )
          .plus(arc)
          .plus(arc.map { it.copy(z = -1f) }),
      triangles = pieSlice(0, 2)
          .plus(pieSlice(1, 2 + count))
          .plus((0 until count).flatMap {
            val i = it + 2
            val nextColumn = wrap(i + 1)
            square(i, i + count, nextColumn + count, nextColumn)
          })
  )
}

private fun offsetShapeVertices(shape: ShapeTransform): IntermediateMesh {
  val mesh = getShapeVertices(shape.shape)
  return mesh.copy(
      vertices = mesh.vertices.map { it.transform(shape.transform) }
  )
}

private fun compositeShapeVertices(shape: CompositeShape): IntermediateMesh {
  val meshes = shape.shapes.map(::getShapeVertices)
  val vertices = meshes.flatMap { it.vertices }
  val triangles = meshes
      .fold(Pair(0, listOf<Int>())) { (vertexCount, indices), mesh ->
        Pair(vertexCount + mesh.vertices.size, indices.plus(mesh.triangles.map { it + vertexCount }))
      }
      .second

  return IntermediateMesh(
      vertices = vertices,
      triangles = triangles
  )
}

private fun meshShapeVertices(shape: MeshShape): IntermediateMesh {
  val triangles = shape.triangles.reversed()
  return IntermediateMesh(
      vertices = triangles,
      triangles = triangles.mapIndexed { index, _ -> index }
  )
}

fun getShapeVertices(shape: Shape): IntermediateMesh =
    when (shape) {

      is Box -> box(shape.halfExtents)

      is Cylinder -> cylinder(shape)

      is ShapeTransform -> offsetShapeVertices(shape)

      is CompositeShape -> compositeShapeVertices(shape)

      is MeshShape -> meshShapeVertices(shape)

      is Sphere -> box(Vector3(shape.radius))

      else -> throw Error("Not implemented")
    }
