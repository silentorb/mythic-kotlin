package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.flag.ImGuiDragDropFlags
import imgui.flag.ImGuiMouseButton
import imgui.flag.ImGuiTreeNodeFlags
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

fun projectMenus(getShortcut: GetShortcut): Commands =
    drawMenuBar(getShortcut, listOf(
        MenuItem("File", items = listOf(
            MenuItem("New File", EditorCommands.newFileWithNameDialog),
            MenuItem("New Folder", EditorCommands.newFolderWithNameDialog),
        ))
    ))

fun renderProjectTree(items: Collection<FileItem>, item: FileItem, selection: NodeSelection): Commands {
  val id = item.fullPath
  val selected = selection.contains(id)
  val children = items.filter { it.parent == id }
  val flags = newTreeFlags(selected) or
      when {
        item.type == FileItemType.file -> ImGuiTreeNodeFlags.Leaf
        children.none() -> ImGuiTreeNodeFlags.Bullet
        else -> ImGuiTreeNodeFlags.None
      }

  val isOpen = ImGui.treeNodeEx("File-Tree-$id", flags, item.name)
  val dragCommands = if (item.parent != null && ImGui.beginDragDropSource()) {
    ImGui.setDragDropPayloadObject(DragPayloadTypes.fileItem, item.fullPath)
    ImGui.text(item.name)
    ImGui.endDragDropSource()
    listOf()
  } else if (ImGui.beginDragDropTarget()) {
    val payload = ImGui.acceptDragDropPayloadObject(DragPayloadTypes.fileItem)
    if (payload != null) {
      listOf(Command(EditorCommands.moveFileItem, payload to item.fullPath))
    }
    else
      listOf()
  }
  else
    listOf()

  val selectionCommands = getSelectionCommands(EditorCommands.setFileSelection, selection, id)
  val activateCommands: Commands = if (ImGui.isItemClicked(ImGuiMouseButton.Left) && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left))
    listOf(Command(EditorCommands.setActiveGraph, value = sceneFileNameWithoutExtension(item.name)))
  else
    listOf()

  val childCommands = mutableListOf<Command>()
  if (isOpen) {
    for (child in children.sortedBy { it.name }) {
      childCommands += renderProjectTree(items, child, selection)
    }
    ImGui.treePop()
  }

  return selectionCommands + activateCommands + childCommands + dragCommands
}

fun renderProject(editor: Editor): Commands {
  ImGui.begin("Project", ImGuiWindowFlags.MenuBar)
  panelBackground()
  val menuCommands = projectMenus(getShortcutForContext(editor.bindings, Contexts.project))

  val items = editor.fileItems.values
  val root = items.firstOrNull { it.parent == null }
  val commands = if (root == null)
    listOf()
  else
    renderProjectTree(items, root, editor.state.fileSelection)

  ImGui.end()

  return menuCommands + commands
}
