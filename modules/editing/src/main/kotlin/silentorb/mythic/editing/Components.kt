package silentorb.mythic.editing

import imgui.ImColor
import imgui.ImGui

fun panelBackground() {
  val drawList = ImGui.getWindowDrawList()
  drawList.addRectFilled(
      ImGui.getWindowPosX(),
      ImGui.getWindowPosY(),
      ImGui.getWindowPosX() + ImGui.getWindowSizeX(),
      ImGui.getWindowPosY() + ImGui.getWindowSizeY(),
      ImColor.intToColor(64, 64, 64, 255)
  )
}
