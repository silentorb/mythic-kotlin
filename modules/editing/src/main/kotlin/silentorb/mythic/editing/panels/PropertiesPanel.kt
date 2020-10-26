package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.editing.panelBackground

fun drawPropertiesPanel() {
  ImGui.begin("Properties", ImGuiWindowFlags.MenuBar)
  panelBackground()
  ImGui.end()
}
