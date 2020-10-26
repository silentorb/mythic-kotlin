package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.spatial.Vector4i

fun drawViewportPanel(): Vector4i {
  ImGui.begin("Viewport", ImGuiWindowFlags.MenuBar or ImGuiWindowFlags.NoBackground)
  val viewport = Vector4i(
      ImGui.getWindowPosX().toInt(), ImGui.getWindowPosY().toInt(),
      ImGui.getWindowSizeX().toInt(), ImGui.getWindowSizeY().toInt()
  )
  ImGui.end()
  return viewport
}
