package silentorb.mythic.editing

import imgui.ImColor
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.spatial.Vector4i

fun drawEditor(): EditorResult {
  if (ImGui.beginMainMenuBar()) {
    if (ImGui.beginMenu("Edit")) {
      ImGui.menuItem("Foo", "F")
      ImGui.menuItem("Kung Foo", "K")
      ImGui.endMenu()
    }
    ImGui.endMainMenuBar()
  }

  ImGui.setNextWindowBgAlpha(0f)
  ImGui.dockSpaceOverViewport()

  ImGui.begin("Tree", ImGuiWindowFlags.MenuBar)
  val drawList = ImGui.getWindowDrawList()
  drawList.addRectFilled(
      ImGui.getWindowPosX(),
      ImGui.getWindowPosY(),
      ImGui.getWindowPosX() + ImGui.getWindowSizeX(),
      ImGui.getWindowPosY() + ImGui.getWindowSizeY(),
      ImColor.intToColor(64, 64, 64, 255)
  )
  ImGui.button("Hello!")
  ImGui.end()

  ImGui.begin("Viewport", ImGuiWindowFlags.MenuBar or ImGuiWindowFlags.NoBackground)
  val viewport = Vector4i(
      ImGui.getWindowPosX().toInt(), ImGui.getWindowPosY().toInt(),
      ImGui.getWindowSizeX().toInt(), ImGui.getWindowSizeY().toInt()
  )
  ImGui.end()

  return EditorResult(
      viewport = viewport
  )
}
