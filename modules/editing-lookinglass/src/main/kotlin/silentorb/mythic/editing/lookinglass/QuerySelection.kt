package silentorb.mythic.editing.lookinglass

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.GL_INCR_WRAP
import silentorb.mythic.editing.main.Editor
import silentorb.mythic.editing.main.SelectionQuery
import silentorb.mythic.editing.main.getActiveEditorGraph
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.getGraphKeys
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.*
import silentorb.mythic.lookinglass.drawing.renderElementGroups
import silentorb.mythic.scenery.SceneProperties
import silentorb.mythic.spatial.Vector4i

fun plumbPixelDepth(sceneRenderer: SceneRenderer, editor: Editor, selectionQuery: SelectionQuery, expandedGraph: Graph): Key? {
  val pixelPositionX = selectionQuery.position.x + sceneRenderer.viewport.x
  val pixelPositionY = flipY(sceneRenderer.viewport.w, selectionQuery.position.y) + sceneRenderer.viewport.y
  val crop = Vector4i(pixelPositionX, pixelPositionY, 1, 1)
  globalState.depthEnabled = true
  clearDepth()

  val graph = getActiveEditorGraph(editor)

  return if (graph == null)
    null
  else
    withCropping(crop) {
      withoutFrontDrawing {
        val nodes = getGraphKeys(graph)
        val childGraph = expandedGraph
            .filter { !(it.property == SceneProperties.parent && nodes.contains(it.source)) }

        val material = Material(
            shading = false,
            color = selectionColor,
        )
        var hit: Key? = null

        glStencilMask(0xFF)
        clearStencil()
        globalState.stencilTest = true
        glStencilFunc(GL_ALWAYS, 1, 0xFF)
        glStencilOp(GL_KEEP, GL_KEEP, GL_INCR_WRAP)

        var lastSample = 0

        for (node in nodes) {
          val elementGroups = getSelectionMeshes(editor, childGraph, expandedGraph, node)
          if (elementGroups.any()) {
            val groups = setElementGroupMaterial(material, elementGroups)
            renderElementGroups(sceneRenderer, groups, ShadingMode.none)
            val pixels = intArrayOf(0)
            glReadPixels(pixelPositionX, pixelPositionY, 1, 1, GL_STENCIL_INDEX, GL_UNSIGNED_INT, pixels)
            val sample = pixels.first()
            if (sample > lastSample) {
              hit = node
              lastSample = sample
              if (lastSample > 128) {
                clearStencil()
                lastSample = 0
              }
            }
          }
        }
        globalState.stencilTest = false
        glStencilMask(0x00)
        clearDepth()
        hit
      }
    }
}
