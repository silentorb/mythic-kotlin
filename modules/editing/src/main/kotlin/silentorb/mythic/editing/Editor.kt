package silentorb.mythic.editing

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags

fun drawEditor() {
  if (ImGui.beginMainMenuBar()) {
    if (ImGui.beginMenu("Edit")) {
      ImGui.menuItem("Foo", "F")
      ImGui.menuItem("Kung Foo", "K")
      ImGui.endMenu()
    }
    ImGui.endMainMenuBar()
  }

  ImGui.begin("Editor", ImGuiWindowFlags.MenuBar)
  ImGui.button("Hello!")
  ImGui.end()
}
