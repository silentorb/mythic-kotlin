package silentorb.mythic.editing.components

import imgui.ImGui
import silentorb.mythic.editing.general.ContextCommand
import silentorb.mythic.editing.general.GetShortcut
import silentorb.mythic.editing.general.KeystrokeBindings
import silentorb.mythic.editing.general.MenuChannel
import silentorb.mythic.editing.main.*
import silentorb.mythic.happenings.Command

fun getMenuItems(menus: ContextMenus, path: PathList): ContextMenus =
    menus
        .filterKeys { it.dropLast(1) == path }

fun drawMenuItem(channel: MenuChannel, item: MenuItem): List<Command> {
  val commandType = item.commandType
  val command = item.command
  val shortcut = (if (commandType != null) channel.getShortcut(commandType) else null) ?: ""
  val getMenuItemState = item.getState
  val expanded = if (getMenuItemState != null)
    ImGui.menuItem(item.label, shortcut, getMenuItemState(channel.editor))
  else
    ImGui.menuItem(item.label, shortcut)

  return if (expanded && (command != null || commandType != null)) {
    listOf(command ?: Command(type = commandType!!))
  } else
    listOf()
}

fun sortMenu(items: ContextMenus) =
    items.entries
        .sortedWith(compareBy({ it.value.weight }, { it.value.label }))

fun drawMenuItems(channel: MenuChannel, items: ContextMenus): List<Command> =
    sortMenu(items)
        .flatMap { drawMenuItem(channel, it.value) }

fun drawMenu(channel: MenuChannel, path: PathList, item: MenuItem): List<Command> =
    if (ImGui.beginMenu(item.label)) {
      val items = getMenuItems(channel.menus, path)
      val result = drawMenuItems(channel, items)
      ImGui.endMenu()
      result
    } else
      listOf()

fun drawMenu(channel: MenuChannel, items: ContextMenus) =
    sortMenu(items)
        .flatMap { drawMenu(channel, it.key, it.value) }

fun drawMainMenuBar(channel: MenuChannel, path: PathList): MenuResponse =
    if (ImGui.beginMainMenuBar()) {
      val items = getMenuItems(channel.menus, path)
      val result = drawMenu(channel, items)
      ImGui.endMainMenuBar()
      result
    } else
      listOf()

fun drawMenuBar(channel: MenuChannel, items: ContextMenus): MenuResponse =
    if (ImGui.beginMenuBar()) {
      val result = drawMenu(channel, items)
      ImGui.endMenuBar()
      result
    } else
      listOf()

fun getShortcutForContext(bindings: KeystrokeBindings, context: String): GetShortcut = { command ->
  bindings[ContextCommand(context, command)]
}

fun newMenuChannel(editor: Editor, context: String): MenuChannel =
    MenuChannel(
        getShortcut = getShortcutForContext(editor.bindings, context),
        editor = editor,
        menus = editor.enumerations.menus,
    )

fun gizmoMenuToggleState(gizmo: String): GetMenuItemState = { editor ->
  editor.persistentState.visibleGizmoTypes.contains(gizmo)
}
