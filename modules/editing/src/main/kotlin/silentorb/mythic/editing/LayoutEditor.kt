package silentorb.mythic.editing

import imgui.ImGui
import silentorb.mythic.editing.components.*
import silentorb.mythic.editing.panels.drawPropertiesPanel
import silentorb.mythic.editing.panels.drawViewportPanel
import silentorb.mythic.editing.panels.renderProject
import silentorb.mythic.editing.panels.renderTree
import silentorb.mythic.happenings.Commands

fun drawEditor(editor: Editor): Commands {
  val graph = getActiveEditorGraph(editor)
  val menuCommands = mainMenus(getShortcutForContext(editor.bindings, Contexts.global))

  ImGui.setNextWindowBgAlpha(0f)
  ImGui.dockSpaceOverViewport()

  drawEditor3dElements(editor)

  val menuAndPanelCommands = menuCommands +
      renderTree(editor, graph) +
      renderProject(editor) +
      drawPropertiesPanel(editor, graph) +
      getImGuiCommands(editor) +
      drawViewportPanel(editor)

  val dialogCommands = listOf(
      newNodeNameDialog,
      renameNodeDialog(editor),
      newFileNameDialog,
      newFolderNameDialog
  )
      .flatMap { it(menuAndPanelCommands) }

  return menuAndPanelCommands + dialogCommands
}
