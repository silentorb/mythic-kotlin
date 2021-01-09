package silentorb.mythic.editing.lookinglass

import silentorb.mythic.editing.Editor
import silentorb.mythic.editing.getExpansionLibrary
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.getGraphValue
import silentorb.mythic.ent.mapByProperty
import silentorb.mythic.ent.scenery.expandInstances
import silentorb.mythic.ent.scenery.gatherChildren
import silentorb.mythic.ent.scenery.getNodeTransform
import silentorb.mythic.lookinglass.Material
import silentorb.mythic.lookinglass.SceneRenderer
import silentorb.mythic.lookinglass.drawing.renderMeshElement
import silentorb.mythic.scenery.SceneProperties
import silentorb.mythic.spatial.Matrix

typealias MeshNodes = List<Pair<Key, Matrix>>

fun getSelectionMeshes(editor: Editor, graph: Graph, node: Key): List<Pair<Key, Matrix>> {
  val nodes = gatherChildren(graph, node) + node
  val subGraph = graph.filter { nodes.contains(it.source) }

  return mapByProperty<Key>(subGraph, SceneProperties.mesh)
      .map { (key, mesh) ->
        mesh to getNodeTransform(graph, key)
      }
}

fun renderMeshNodes(sceneRenderer: SceneRenderer, material: Material, meshNodes: MeshNodes) {
  for ((mesh, transform) in meshNodes) {
    renderMeshElement(sceneRenderer, mesh, transform, material)
  }
}
