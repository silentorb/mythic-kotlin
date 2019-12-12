package mythic.sculpting

import mythic.spatial.*
import kotlin.math.cos
import kotlin.math.sin


fun createArc(radius: Float, count: Int, sweep: Float = Pi * 2, offset: Float = 0f): Vertices3m {
  val vertices = ArrayList<Vector3m>(count)
  val increment = sweep / (count - 1)

  for (i in 0 until count) {
    val theta = increment * i + offset
    vertices.add(Vector3m(cos(theta) * radius, 0f, sin(theta) * radius))
  }
  if (sweep == Pi)
    vertices.last().x = 0f

  return vertices
}

fun createCircle(mesh: FlexibleMesh, radius: Float, count: Int): FlexibleFace {
  return mesh.createFace(createArc(radius, count))
}

fun createArcXY(radius: Float, count: Int, sweep: Float = Pi * 2): Vertices3m {
  val vertices = ArrayList<Vector3m>(count)
  val increment = sweep / (count - 1)

  for (i in 0 until count) {
    val theta = increment * i
    vertices.add(Vector3m(sin(theta) * radius, cos(theta) * radius, 0f))
  }
//  if (sweep == Pi)
//    vertices.last().x = 0f

  return vertices
}

fun createCircle2(mesh: FlexibleMesh, radius: Float, count: Int): FlexibleFace {
  return mesh.createFace(createArcXY(radius, count))
}

//fun createIncompleteCircle(mesh: FlexibleMesh, radius: Float, count: Int, take: Int): FlexibleFace {
//  return mesh.createFace(convertPath(createArcZ(radius, count)).take(take))
//}

fun createCylinder(mesh: FlexibleMesh, radius: Float, count: Int, length: Float): List<FlexibleFace> {
  val circle = createCircle2(mesh, radius, count)
  return listOf(circle).plus(
      extrudeBasic(mesh, circle, Matrix().translate(Vector3m(0f, 0f, length)))
  )
}

fun createSphere(mesh: FlexibleMesh, radius: Float, horizontalCount: Int, verticalCount: Int) =
    lathe(mesh, createArc(radius, verticalCount, Pi, Pi / 2), horizontalCount)
//    mesh.createFace(createArcZ(radius, verticalCount, Pi, -Pi / 2))

fun createIncompleteSphere(mesh: FlexibleMesh, radius: Float, horizontalCount: Int, verticalCount: Int, take: Int) =
    lathe(mesh, createArc(radius, verticalCount, Pi).take(take), horizontalCount)
/*
fun createCube(mesh: FlexibleMesh, size: Vector3): List<FlexibleFace> {
  val half = size * 0.5f
  val top = squareUp(mesh, Vector2(size.x, size.y), half.z)
  val bottom = squareDown(mesh, Vector2(size.x, size.y), -half.z)

  val top_vertices = top.vertices
  val initial_bottom_vertices = bottom.vertices
  val bottom_vertices = listOf(
      initial_bottom_vertices[0],
      initial_bottom_vertices[3],
      initial_bottom_vertices[2],
      initial_bottom_vertices[1]
  )

  val sides = (0..3).map { a ->
    val b = if (a > 2) 0 else a + 1
    mesh.createStitchedFace(listOf(
        top_vertices[b], top_vertices[a],
        bottom_vertices[a], bottom_vertices[b]
    ))
  }
  return listOf(top, bottom)
      .plus(listOf())
}

fun createCube(mesh: ImmutableMesh, size: Vector3): List<ImmutableFace> {
  val half = size * 0.5f
  val top = squareUp(mesh, Vector2(size.x, size.y), half.z)
  val bottom = squareDown(mesh, Vector2(size.x, size.y), -half.z)

  val top_vertices = top.vertices
  val initial_bottom_vertices = bottom.vertices
  val bottom_vertices = listOf(
      initial_bottom_vertices[0],
      initial_bottom_vertices[3],
      initial_bottom_vertices[2],
      initial_bottom_vertices[1]
  )

  val nextId = newIdSource(0)

  val sides = (0..3).map { a ->
    val b = if (a > 2) 0 else a + 1
    mesh.createStitchedFace(nextId, nextId(), listOf(
        top_vertices[b], top_vertices[a],
        bottom_vertices[a], bottom_vertices[b]
    ))
  }
  return listOf(top, bottom)
}

fun squareDown(mesh: FlexibleMesh, size: Vector2, z: Float): FlexibleFace {
  val half = size * 0.5f;
  return mesh.createStitchedFace(listOf(
      Vector3m(-half.x, -half.y, z),
      Vector3m(-half.x, half.y, z),
      Vector3m(half.x, half.y, z),
      Vector3m(half.x, -half.y, z)
  ))
}

fun squareDown(nextId: IdSource, mesh: ImmutableMesh, size: Vector2, z: Float): ImmutableFace {
  val half = size * 0.5f;
  return mesh.createStitchedFace(nextId, nextId(), listOf(
      Vector3(-half.x, -half.y, z),
      Vector3(-half.x, half.y, z),
      Vector3(half.x, half.y, z),
      Vector3(half.x, -half.y, z)
  ))
}

fun squareUp(mesh: FlexibleMesh, size: Vector2, z: Float): FlexibleFace {
  val half = size * 0.5f;
  return mesh.createStitchedFace(listOf(
      Vector3m(-half.x, -half.y, z),
      Vector3m(half.x, -half.y, z),
      Vector3m(half.x, half.y, z),
      Vector3m(-half.x, half.y, z)
  ))
}

fun squareUp(nextId: IdSource, mesh: ImmutableMesh, size: Vector2, z: Float): ImmutableFace {
  val half = size * 0.5f;
  return mesh.createStitchedFace(nextId, nextId(), listOf(
      Vector3(-half.x, -half.y, z),
      Vector3(half.x, -half.y, z),
      Vector3(half.x, half.y, z),
      Vector3(-half.x, half.y, z)
  ))
}

//fun createLines(mesh: FlexibleMesh, path: Vertices3m) {
//  for (i in 0 until path.size - 1) {
//    mesh.createEdge(path[i], path[i + 1])
//  }
//}
*/
