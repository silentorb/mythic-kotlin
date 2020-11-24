package silentorb.mythic.editing.lookinglass

import silentorb.mythic.editing.Editor
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.getGraphValue
import silentorb.mythic.ent.mapByProperty
import silentorb.mythic.ent.scenery.expandInstances
import silentorb.mythic.ent.scenery.getNodeTransform
import silentorb.mythic.lookinglass.Material
import silentorb.mythic.lookinglass.SceneRenderer
import silentorb.mythic.lookinglass.drawing.renderMeshElement
import silentorb.mythic.scenery.SceneProperties
import silentorb.mythic.spatial.Matrix

typealias MeshNodes = List<Pair<Key, Matrix>>

fun getSelectionMeshes(editor: Editor, graph: Graph, node: Key): List<Pair<Key, Matrix>> {
  val type = getGraphValue<Key>(graph, node, SceneProperties.instance)
  val subGraph = if (type != null && editor.graphLibrary.containsKey(type))
    expandInstances(editor.graphLibrary, editor.graphLibrary[type]!!)
  else
    null

  val localMesh = getGraphValue<Key>(graph, node, SceneProperties.mesh)

  val subGraphMeshNodes = if (subGraph != null)
    mapByProperty<Key>(subGraph, SceneProperties.mesh)
  else
    mapOf()

  val selfMeshNodes = if (localMesh != null)
    mapOf(node to localMesh)
  else
    mapOf()

  val localTransform = getNodeTransform(graph, node)
  return selfMeshNodes
      .map { (_, mesh) ->
        mesh to localTransform
      }
      .plus(
          subGraphMeshNodes
              .map { (key, mesh) ->
                // TODO: For some reason the matrix integration is needing to be backwards from the main rendering pass
                mesh to localTransform * getNodeTransform(subGraph!!, key)
              }
      )
}

fun renderMeshNodes(sceneRenderer: SceneRenderer, material: Material, meshNodes: MeshNodes) {
  for ((mesh, transform) in meshNodes) {
    renderMeshElement(sceneRenderer, mesh, transform, material)
  }
}
