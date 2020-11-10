package silentorb.mythic.ent.spatial

import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.getValue
import silentorb.mythic.scenery.Properties
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3

fun getTransform(graph: Graph, node: Key): Matrix {
  val translation = getValue<Vector3>(graph, node, Properties.translation) ?: Vector3.zero
  val rotation = getValue<Vector3>(graph, node, Properties.rotation) ?: Vector3.zero
  val scale = getValue<Vector3>(graph, node, Properties.scale) ?: Vector3.unit
  val localTransform = Matrix.identity
      .translate(translation)
      .rotateZ(rotation.z)
      .rotateY(rotation.y)
      .rotateX(rotation.x)
      .scale(scale)

  val parent = getValue<Key>(graph, node, Properties.parent)
  return if (parent != null)
    getTransform(graph, parent) * localTransform
  else
    localTransform
}
