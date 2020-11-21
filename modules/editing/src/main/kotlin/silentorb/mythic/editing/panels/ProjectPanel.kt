package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.flag.ImGuiMouseButton
import imgui.flag.ImGuiTreeNodeFlags
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

fun getDragType(type: FileItemType): String =
    if (type == FileItemType.file)
      DraggingTypes.file
    else
      DraggingTypes.folder

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

  if (item.parent != null) {
    dragSource(getDragType(item.type), item) {
      ImGui.text(item.name)
    }
  }

  val dragCommands = if (item.type == FileItemType.folder) {
    val onDrag = DragTarget({ payload ->
      val source = payload as? FileItem
      if (source == null)
        false
      else
        !isDerivativePath(source.fullPath, item.fullPath) && item.fullPath != source.parent
    }) { payload ->
      listOf(Command(EditorCommands.moveFileItem, (payload as FileItem).fullPath to item.fullPath))
    }
    dragTargets(mapOf(
        DraggingTypes.file to onDrag,
        DraggingTypes.folder to onDrag,
    ))
  } else
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

fun renderProject(editor: Editor): PanelResponse =
    panel(editor, "Project", Contexts.project, ::projectMenus) {
      panelBackground()
      val items = editor.fileItems.values
      val root = items.firstOrNull { it.parent == null }
      if (root == null)
        listOf()
      else
        renderProjectTree(items, root, editor.state.fileSelection)
    }
