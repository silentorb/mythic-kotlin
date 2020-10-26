package silentorb.mythic.editing

import imgui.ImGui
import silentorb.mythic.editing.panels.drawPropertiesPanel
import silentorb.mythic.editing.panels.drawViewportPanel
import silentorb.mythic.editing.panels.renderTree

fun drawEditor(editor: Editor): Editor {
  val graph = getActiveEditorGraph(editor)

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

  val nextSelection = renderTree(editor, graph)
  val viewport = drawViewportPanel();
  drawPropertiesPanel()

  return editor.copy(
      viewport = viewport,
      selection = nextSelection
  )
}
