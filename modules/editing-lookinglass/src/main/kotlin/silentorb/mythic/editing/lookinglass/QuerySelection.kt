package silentorb.mythic.editing.lookinglass

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.GL_INCR_WRAP
import silentorb.mythic.editing.Editor
import silentorb.mythic.editing.SelectionQuery
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.getGraphKeys
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.Material
import silentorb.mythic.lookinglass.SceneRenderer
import silentorb.mythic.lookinglass.flipY
import silentorb.mythic.spatial.Vector4i

fun plumbPixelDepth(sceneRenderer: SceneRenderer, editor: Editor, selectionQuery: SelectionQuery, graph: Graph): Key? {
  val pixelPositionX = selectionQuery.position.x + sceneRenderer.viewport.x
  val pixelPositionY = flipY(sceneRenderer.viewport.w, selectionQuery.position.y) + sceneRenderer.viewport.y
  val crop = Vector4i(pixelPositionX, pixelPositionY, 1, 1)

  return withCropping(crop) {
    withoutFrontDrawing {
      val nodes = getGraphKeys(graph)
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
        val meshNodes = getSelectionMeshes(editor, graph, node)
        if (meshNodes.any()) {
          renderMeshNodes(sceneRenderer, material, meshNodes)
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
