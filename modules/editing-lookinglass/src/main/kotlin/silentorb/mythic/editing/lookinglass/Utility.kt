package silentorb.mythic.editing.lookinglass

import silentorb.mythic.editing.Editor
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.scenery.gatherChildren
import silentorb.mythic.lookinglass.ElementGroup
import silentorb.mythic.lookinglass.Material
import silentorb.mythic.lookinglass.SceneRenderer
import silentorb.mythic.lookinglass.drawing.renderMeshElement
import silentorb.mythic.spatial.Matrix

typealias MeshNodes = List<Pair<Key, Matrix>>

fun getSelectionMeshes(editor: Editor, graph: Graph, node: Key): List<ElementGroup> {
  val nodes = gatherChildren(graph, node) + node
//  val subGraph = graph.filter { nodes.contains(it.source) }
  return nodesToElements(editor, graph, nodes)
}

fun renderMeshNodes(sceneRenderer: SceneRenderer, material: Material, meshNodes: MeshNodes) {
  for ((mesh, transform) in meshNodes) {
    renderMeshElement(sceneRenderer, mesh, transform, material)
  }
}

fun setElementGroupMaterial(material: Material, elementGroups: Collection<ElementGroup>) =
    elementGroups.map { group ->
      group.copy(
          meshes = group.meshes.map { mesh ->
            mesh.copy(
                material = material
            )
          }
      )
    }
