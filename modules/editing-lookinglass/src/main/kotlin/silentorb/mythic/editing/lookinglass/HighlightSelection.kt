package silentorb.mythic.editing.lookinglass

import silentorb.mythic.editing.Editor
import silentorb.mythic.editing.getCachedGraph
import silentorb.mythic.editing.getNodeSelection
import silentorb.mythic.lookinglass.SceneRenderer
import silentorb.mythic.lookinglass.drawing.renderHighlight
import silentorb.mythic.spatial.Vector4

val selectionColor = Vector4(0.9f, 0.9f, 0.4f, 1f)

fun renderEditorSelection(editor: Editor, sceneRenderer: SceneRenderer) {
  val selection = getNodeSelection(editor)
  val graph = getCachedGraph(editor)
  for (node in selection) {
    val elementGroups = getSelectionMeshes(editor, graph, graph, node)
    renderHighlight(sceneRenderer, elementGroups, selectionColor)
  }
}
