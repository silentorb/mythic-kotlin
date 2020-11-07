package silentorb.mythic.editing

import imgui.ImGui
import silentorb.mythic.editing.components.mainMenus
import silentorb.mythic.editing.components.newNodeNameDialog
import silentorb.mythic.editing.components.renameNodeDialog
import silentorb.mythic.editing.panels.*
import silentorb.mythic.happenings.Commands

fun drawEditor(editor: Editor): Pair<Editor, Commands> {
  val graph = getActiveEditorGraph(editor)
  val state = editor.state

  val menuCommands = mainMenus()

  ImGui.setNextWindowBgAlpha(0f)
  ImGui.dockSpaceOverViewport()

  drawEditor3dElements(editor)

  val panelCommands = renderTree(editor, graph) +
      renderProject(editor) +
      drawPropertiesPanel(editor, graph) +
      getImGuiCommands(editor)

  val viewport = drawViewportPanel()

  val dialogCommands = newNodeNameDialog(menuCommands) + renameNodeDialog(editor)(menuCommands)
  return editor.copy(
      viewportBoundsMap = mapOf(defaultViewportId to viewport),
  ) to menuCommands + dialogCommands + panelCommands
}
