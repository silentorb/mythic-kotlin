package silentorb.mythic.editing

import imgui.ImGui
import silentorb.mythic.editing.components.mainMenus
import silentorb.mythic.editing.components.newNodeNameDialog
import silentorb.mythic.editing.components.renameNodeDialog
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.editing.panels.drawPropertiesPanel
import silentorb.mythic.editing.panels.drawViewportPanel
import silentorb.mythic.editing.panels.renderTree
import silentorb.mythic.editing.updating.appendHistory
import silentorb.mythic.happenings.Commands

fun drawEditor(editor: Editor): Pair<Editor, Commands> {
  val graph = editor.staging ?: editor.graph
  val state = editor.state

  val menuCommands = mainMenus()

  ImGui.setNextWindowBgAlpha(0f)
  ImGui.dockSpaceOverViewport()

  drawEditor3dElements(editor)

  val nextSelection = renderTree(editor, graph)
  val viewport = drawViewportPanel();
  val propertiesCommands= drawPropertiesPanel(editor, graph)
//  val nextGraphLibrary = incorporateGraphIntoLibrary(editor, nextGraph)

  val dialogCommands = newNodeNameDialog(menuCommands) + renameNodeDialog(editor)(menuCommands)
  val imGuiCommands = getImGuiCommands(editor)
  return editor.copy(
      state.copy(
          selection = nextSelection,
      ),
      viewportBoundsMap = mapOf(defaultViewportId to viewport),
//      history = appendHistory(editor.history, nextGraph),
  ) to menuCommands + dialogCommands + imGuiCommands + propertiesCommands
}
