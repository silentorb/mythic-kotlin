package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.flag.ImGuiMouseButton
import imgui.flag.ImGuiTreeNodeFlags
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

fun projectMenus(): List<MenuTree> =
    listOf(
        MenuTree("File", key = Menus.file, items = listOf(
            MenuTree("New File", EditorCommands.newFileWithNameDialog),
            MenuTree("New Folder", EditorCommands.newFolderWithNameDialog),
            MenuTree("Rename", EditorCommands.renameFileItemWithNameDialog),
        ))
    )

fun getDragType(type: FileItemType): String =
    if (type == FileItemType.file)
      DraggingTypes.file
    else
      DraggingTypes.folder

fun renderProjectTree(persistentExpansions: Set<String>,
                      items: Collection<FileItem>, item: FileItem, selection: NodeSelection): Commands {
  val id = item.fullPath
  val selected = selection.contains(id)
  val children = items.filter { it.parent == id }
  val persistentFlag = if (persistentExpansions.contains(item.fullPath))
    ImGuiTreeNodeFlags.DefaultOpen
  else
    ImGuiTreeNodeFlags.None

  val flags = newTreeFlags(selected) or persistentFlag or
      when {
        item.type == FileItemType.file -> ImGuiTreeNodeFlags.Leaf
        children.none() -> ImGuiTreeNodeFlags.Bullet
        else -> ImGuiTreeNodeFlags.None
      }

  val isOpen = ImGui.treeNodeEx("File-Tree-$id", flags, item.name)

  val toggleCommands = if (ImGui.isItemToggledOpen())
    listOf(Command(EditorCommands.treeNodeExpansionToggled, value = item.fullPath))
  else
    listOf()

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
      val source = (payload as FileItem).fullPath
      val destination = item.fullPath + "/" + getFileName(source)
      listOf(Command(EditorCommands.moveFileItem, source to destination))
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
    val sorted = children.sortedBy { getFileName(it.baseName) }
    for (child in sorted) {
      childCommands += renderProjectTree(persistentExpansions, items, child, selection)
    }
    ImGui.treePop()
  }

  return toggleCommands + selectionCommands + activateCommands + childCommands + dragCommands
}

fun renderProject(editor: Editor): PanelResponse =
    panel(editor, "Project", Contexts.project) {
      panelBackground()
      val items = editor.fileItems.values
      val roots = items
          .filter { it.parent == null }
          .sortedBy { it.name }

      val persistentState = editor.persistentState
      roots.fold(listOf()) { commands, root ->
        commands + renderProjectTree(persistentState.expandedProjectTreeNodes, items, root, persistentState.fileSelection)
      }
    }
