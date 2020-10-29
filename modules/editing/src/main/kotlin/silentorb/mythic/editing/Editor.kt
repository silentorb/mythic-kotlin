package silentorb.mythic.editing

import imgui.ImGui
import silentorb.mythic.editing.components.drawMainMenuBar
import silentorb.mythic.editing.components.drawMenuItems
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.editing.panels.drawPropertiesPanel
import silentorb.mythic.editing.panels.drawViewportPanel
import silentorb.mythic.editing.panels.renderTree
import silentorb.mythic.happenings.Commands

fun drawEditor(editor: Editor): Pair<Editor, Commands> {
  val graphId = getActiveEditorGraphId(editor)
  val graph = editor.graphLibrary[graphId]
  val state = editor.state

  val menuCommands = drawMainMenuBar(listOf(
      MenuItem("Edit", items = listOf(
          MenuItem("Add Node", "Ctrl+A", EditorCommands.addNode),
          MenuItem("Assign Mesh", "Shift+M", EditorCommands.assignMesh),
          MenuItem("Assign Texture", "Shift+T", EditorCommands.assignTexture),
      ))
  ))

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
  ) to menuCommands
}
