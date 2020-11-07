package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.flag.ImGuiMouseButton
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.getSelectionCommands
import silentorb.mythic.editing.components.newTreeFlags
import silentorb.mythic.editing.components.panelBackground
import silentorb.mythic.editing.components.updateSelectionToggle
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

fun renderProjectTree(items: Collection<FileItem>, item: FileItem, selection: NodeSelection): Commands {
  val id = item.fullPath
  val selected = selection.contains(id)
  val children = items.filter { it.parent == id }
  val flags = newTreeFlags(selected, children.any())

  val isOpen = ImGui.treeNodeEx("File-Tree-$id", flags, item.name)
  val selectionCommands = getSelectionCommands(EditorCommands.setFileSelection, selection, id)
  val activateCommands: Commands = if (ImGui.isItemClicked(ImGuiMouseButton.Left) && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left))
    listOf(Command(EditorCommands.setActiveGraph, value = item.name.replace(".json", "")))
  else
    listOf()

  val childCommands = mutableListOf<Command>()
  if (isOpen) {
    for (child in children) {
      childCommands += renderProjectTree(items, child, selection)
    }
    ImGui.treePop()
  }

  return selectionCommands + activateCommands + childCommands
}

fun renderProject(editor: Editor): Commands {
  ImGui.begin("Project")
  panelBackground()

  val items = editor.fileItems.values
  val root = items.firstOrNull { it.parent == null }
  val commands = if (root == null)
    listOf()
  else
    renderProjectTree(items, root, editor.state.fileSelection)

  ImGui.end()

  return commands
}
