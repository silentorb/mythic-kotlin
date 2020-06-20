package silentorb.mythic.sculpting

import silentorb.mythic.spatial.Vector3

fun getVerticesCenter(vertices: List<Vector3>): Vector3 {
  if (vertices.isEmpty())
    return Vector3()

  var result = Vector3()
  for (vertex in vertices) {
    result += vertex
  }
  return result / vertices.size.toFloat()
}
