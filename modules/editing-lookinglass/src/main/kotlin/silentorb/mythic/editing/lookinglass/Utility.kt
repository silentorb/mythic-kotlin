package silentorb.mythic.editing.lookinglass

import silentorb.mythic.editing.Editor
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.scenery.withNodeChildren
import silentorb.mythic.lookinglass.ElementGroup
import silentorb.mythic.lookinglass.ShadingMode
import silentorb.mythic.lookinglass.Material
import silentorb.mythic.lookinglass.SceneRenderer
import silentorb.mythic.lookinglass.drawing.renderMeshElement
import silentorb.mythic.spatial.Matrix

typealias MeshNodes = List<Pair<Key, Matrix>>

fun getSelectionMeshes(editor: Editor, childGraph: Graph, graph: Graph, node: Key): List<ElementGroup> {
  val nodes = withNodeChildren(childGraph, node)
  return nodesToElements(editor, graph, nodes)
}

fun renderMeshNodes(sceneRenderer: SceneRenderer, material: Material, meshNodes: MeshNodes, shadingMode: ShadingMode) {
  for ((mesh, transform) in meshNodes) {
    renderMeshElement(sceneRenderer, mesh, transform, material, shadingMode)
  }
}
