package silentorb.mythic.editing

import imgui.ImGui
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.editing.panels.drawPropertiesPanel
import silentorb.mythic.editing.panels.drawViewportPanel
import silentorb.mythic.editing.panels.renderTree

fun drawEditor(editor: Editor): Editor {
  val graphId = getActiveEditorGraphId(editor)
  val graph = editor.graphLibrary[graphId]
  val state = editor.state

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
  val nextGraph = drawPropertiesPanel(editor, graph)
  val nextGraphLibrary = if (graphId != null && nextGraph != null)
    editor.graphLibrary + (graphId to nextGraph)
  else
    editor.graphLibrary

  return editor.copy(
      state.copy(
          viewportBoundsMap = mapOf(defaultViewportId to viewport),
          selection = nextSelection,
      ),
      graphLibrary = nextGraphLibrary,
  )
}
