package silentorb.mythic.editing.components

import imgui.ImGui
import silentorb.mythic.editing.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

fun drawMenuItem(channel: MenuChannel): (MenuItem) -> List<Command> = { item ->
  val command = item.command
  val shortcut = (if (command != null) channel.getShortcut(command) else null) ?: ""
  val getMenuItemState = item.getState
  val expanded = if (getMenuItemState != null)
    ImGui.menuItem(item.label, shortcut, getMenuItemState(channel.editor))
  else
    ImGui.menuItem(item.label, shortcut)

  if (expanded && command != null) {
    listOf(Command(type = command))
  } else
    listOf()
}

fun drawMenuItems(channel: MenuChannel, items: List<MenuItem>): List<Command> =
    items.flatMap(drawMenuItem(channel))

fun drawMenu(channel: MenuChannel): (MenuItem) -> List<Command> = { item ->
  if (ImGui.beginMenu(item.label)) {
    val result = drawMenuItems(channel, item.items ?: listOf())
    ImGui.endMenu()
    result
  } else
    listOf()
}

fun drawMainMenuBar(channel: MenuChannel, items: List<MenuItem>): MenuResponse =
    if (ImGui.beginMainMenuBar()) {
      val result = items.flatMap(drawMenu(channel))
      ImGui.endMainMenuBar()
      result
    } else
      listOf()

fun drawMenuBar(channel: MenuChannel, items: List<MenuItem>): MenuResponse =
    if (ImGui.beginMenuBar()) {
      val result = items.flatMap(drawMenu(channel))
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
    )
