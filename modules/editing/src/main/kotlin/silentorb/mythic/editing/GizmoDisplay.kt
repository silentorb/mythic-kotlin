package silentorb.mythic.editing

import imgui.ImColor
import imgui.ImGui
import silentorb.mythic.spatial.*

fun drawEditor3dElements(editor: Editor, viewport: Vector4i) {
  val drawList = ImGui.getBackgroundDrawList()
  drawList.addCircle(viewport.x.toFloat() + viewport.z.toFloat() * 0.5f,
      viewport.y.toFloat() + viewport.w.toFloat() * 0.5f,
      100f, ImColor.intToColor(64, 64, 64, 255), 32, 2f)
}

fun drawEditor3dElements(editor: Editor) {
  for (viewport in editor.viewportBoundsMap.values) {
    drawEditor3dElements(editor, viewport)
  }
}
