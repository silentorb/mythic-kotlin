package mythic.sculpting

import mythic.spatial.Vector3

private fun pythonCode(meshName: String, vertexClause: String, indexClause: String): String {
  return """
import bpy
mesh = bpy.data.meshes.new("$meshName")
obj = bpy.data.objects.new("$meshName", mesh)
bpy.context.scene.objects.link(obj)
vertices = [$vertexClause]
indices = [$indexClause]
mesh.from_pydata(vertices, [], indices)
mesh.update(calc_edges=True)
  """.trimIndent()
}

private fun createVertexClause(vertices: List<Vector3>) =
    vertices
        .map { "(${it.x}, ${it.y}, ${it.z})" }
        .joinToString(", ")

private fun createFaceIndexClause(indices: List<Int>): String {
  val stringIndices = indices
      .map { it.toString() }
      .joinToString(", ")
  return "($stringIndices)"
}

fun serializeFaces(faces: List<ImmutableFace>): String {
  val vertices = faces.flatMap { it.vertices }.distinct()
  val vertexClause = createVertexClause(vertices)

  val indexClause = faces.joinToString(", ") { face ->
    createFaceIndexClause(face.vertices.map { vertices.indexOf(it) })
  }

  val meshName = "mesh" + faces.hashCode()
  return pythonCode(meshName, vertexClause, indexClause)
}

fun serializeFace(face: ImmutableFace): String = serializeFaces(listOf(face))

fun serializeFace(vertices: List<Vector3>): String {
  val vertexClause = createVertexClause(vertices)
  val indexClause = createFaceIndexClause(vertices.mapIndexed { i, _ -> i })

  val meshName = "mesh" + vertices.hashCode()
  return pythonCode(meshName, vertexClause, indexClause)
}
