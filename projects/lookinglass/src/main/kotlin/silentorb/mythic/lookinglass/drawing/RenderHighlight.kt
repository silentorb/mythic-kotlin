package silentorb.mythic.lookinglass.drawing

import org.lwjgl.opengl.GL11.*
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.*
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.Vector4i

fun renderHighlight(sceneRenderer: SceneRenderer, elementGroups: List<ElementGroup>, selectionColor: Vector4) {
  if (elementGroups.any()) {
    val material = Material(
        shading = false,
        color = selectionColor,
    )
    val groups = setElementGroupMaterial(material, elementGroups)
    withStack(mapOf(GlField.depthEnabled to false)) {
      val viewport = globalState.viewport
      val offset = viewport.xy()
      val dimensions = viewport.zw()

      glStencilMask(0xFF)
      clearStencil()
      globalState.stencilTest = true

      glStencilFunc(GL_ALWAYS, 1, 0xFF)
      glStencilOp(GL_KEEP, GL_KEEP, GL_INCR)
      glStencilMask(0xFF) // TODO: This line seems to be redundant
      withoutFrontDrawing {
        renderElementGroups(sceneRenderer, groups, ShadingMode.none)
      }

      glStencilMask(0x00)
      glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP)
      glStencilFunc(GL_EQUAL, 0, 0xFF)

      for (y in -1..1 step 2) {
        for (x in -1..1 step 2) {
          globalState.viewport = Vector4i(offset.x + x, offset.y + y, dimensions.x, dimensions.y)
          renderElementGroups(sceneRenderer, groups, ShadingMode.none)
        }
      }
      globalState.stencilTest = false
      globalState.viewport = viewport
    }
  }
}
