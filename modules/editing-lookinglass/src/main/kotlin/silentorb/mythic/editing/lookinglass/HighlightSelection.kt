package silentorb.mythic.editing.lookinglass

import org.lwjgl.opengl.GL11.*
import silentorb.mythic.editing.Editor
import silentorb.mythic.editing.getCachedGraph
import silentorb.mythic.editing.getNodeSelection
import silentorb.mythic.glowing.clearStencil
import silentorb.mythic.glowing.globalState
import silentorb.mythic.glowing.withoutFrontDrawing
import silentorb.mythic.lookinglass.Material
import silentorb.mythic.lookinglass.SceneRenderer
import silentorb.mythic.lookinglass.drawing.renderElementGroups
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.Vector4i

val selectionColor = Vector4(0.9f, 0.9f, 0.4f, 1f)

fun renderEditorSelection(editor: Editor, sceneRenderer: SceneRenderer) {
  val selection = getNodeSelection(editor)
  val graph = getCachedGraph(editor)
  for (node in selection) {
    val elementGroups = getSelectionMeshes(editor, graph, graph, node)
    if (elementGroups.any()) {
      val material = Material(
          shading = false,
          color = selectionColor,
      )
      val groups = setElementGroupMaterial(material, elementGroups)
      globalState.depthEnabled = false
      val viewport = globalState.viewport
      val offset = viewport.xy()
      val dimensions = viewport.zw()

      glStencilMask(0xFF)
      clearStencil()
      globalState.stencilTest = true

      glStencilFunc(GL_ALWAYS, 1, 0xFF)
      glStencilOp(GL_KEEP, GL_KEEP, GL_INCR)
      glStencilMask(0xFF)
      withoutFrontDrawing {
        renderElementGroups(sceneRenderer, groups)
      }

      glStencilMask(0x00)
      glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP)
      glStencilFunc(GL_EQUAL, 0, 0xFF)

      for (y in -1..1 step 2) {
        for (x in -1..1 step 2) {
          globalState.viewport = Vector4i(offset.x + x, offset.y + y, dimensions.x, dimensions.y)
          renderElementGroups(sceneRenderer, groups)
        }
      }
      globalState.depthEnabled = true
      globalState.stencilTest = false
      globalState.viewport = viewport
    }
  }
}
