package silentorb.mythic.editing

import imgui.ImGui
import silentorb.mythic.editing.components.mainMenus
import silentorb.mythic.editing.components.newNodeNameDialog
import silentorb.mythic.editing.components.renameNodeDialog
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.editing.panels.drawPropertiesPanel
import silentorb.mythic.editing.panels.drawViewportPanel
import silentorb.mythic.editing.panels.renderTree
import silentorb.mythic.editing.updating.incorporateGraphIntoLibrary
import silentorb.mythic.happenings.Commands

fun drawEditor(editor: Editor): Pair<Editor, Commands> {
  val graph = getActiveEditorGraph(editor)
  val state = editor.state

  val menuCommands = mainMenus()

  ImGui.setNextWindowBgAlpha(0f)
  ImGui.dockSpaceOverViewport()

  val nextSelection = renderTree(editor, graph)
  val viewport = drawViewportPanel();
  val (nextGraph, propertiesCommands) = drawPropertiesPanel(editor, graph)
  val nextGraphLibrary = incorporateGraphIntoLibrary(editor, nextGraph)

  val dialogCommands = newNodeNameDialog(menuCommands) + renameNodeDialog(editor)(menuCommands)

  return editor.copy(
      state.copy(
          viewportBoundsMap = mapOf(defaultViewportId to viewport),
          selection = nextSelection,
      ),
      graphLibrary = nextGraphLibrary,
  ) to menuCommands + dialogCommands
}
