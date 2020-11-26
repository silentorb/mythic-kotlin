package silentorb.mythic.editing

import imgui.ImGui
import silentorb.mythic.editing.components.*
import silentorb.mythic.editing.panels.drawPropertiesPanel
import silentorb.mythic.editing.panels.drawViewportPanel
import silentorb.mythic.editing.panels.renderProject
import silentorb.mythic.editing.panels.renderTree
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.happenings.Commands

fun drawEditor(editor: Editor, deviceStates: List<InputDeviceState>): Commands {
  val graph = getActiveEditorGraph(editor)
  val menuCommands = mainMenus(getShortcutForContext(editor.bindings, Contexts.global))

  ImGui.setNextWindowBgAlpha(0f)
  ImGui.dockSpaceOverViewport()

  drawEditor3dElements(editor)

  val panelResponses =
      listOf(
          renderTree(editor, graph),
          renderProject(editor),
          drawPropertiesPanel(editor, graph),
          drawViewportPanel(editor),
      )

  val panelMenuCommands = panelResponses.flatMap { it.second }
  val context = panelResponses.fold(Contexts.global) { a, b -> b.first ?: a }

  val shortcutCommands = getShortcutCommands(editor.bindings, context, deviceStates)

  val menuAndPanelCommands = menuCommands + panelMenuCommands + shortcutCommands

  val dialogCommands = listOf(
      newNodeNameDialog,
      renameNodeDialog(editor),
      renameFileItemDialog(editor),
      newFileNameDialog,
      newFolderNameDialog
  )
      .flatMap { it(menuAndPanelCommands) }


  return menuAndPanelCommands + dialogCommands + getImGuiCommands(editor)
}
