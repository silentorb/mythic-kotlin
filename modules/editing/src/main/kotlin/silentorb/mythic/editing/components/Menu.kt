package silentorb.mythic.editing.components

import imgui.ImGui
import silentorb.mythic.editing.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

fun drawMenuItem(getShortcut: GetShortcut): (MenuItem) -> List<Command> = { item ->
  val command = item.command
  val shortcut = if (command != null) getShortcut(command) else null
  if (ImGui.menuItem(item.label, shortcut) && command != null) {
    listOf(Command(type = command))
  } else
    listOf()
}

fun drawMenuItems(getShortcut: GetShortcut, items: List<MenuItem>): List<Command> =
    items.flatMap(drawMenuItem(getShortcut))

fun drawMenu(getShortcut: GetShortcut): (MenuItem) -> List<Command> = { item ->
  if (ImGui.beginMenu(item.label)) {
    val result = drawMenuItems(getShortcut, item.items ?: listOf())
    ImGui.endMenu()
    result
  } else
    listOf()
}

fun drawMainMenuBar(getShortcut: GetShortcut, items: List<MenuItem>): MenuResponse =
    if (ImGui.beginMainMenuBar()) {
      val result = items.flatMap(drawMenu(getShortcut))
      ImGui.endMainMenuBar()
      result
    } else
      listOf()

fun drawMenuBar(getShortcut: GetShortcut, items: List<MenuItem>): MenuResponse =
    if (ImGui.beginMenuBar()) {
      val result = items.flatMap(drawMenu(getShortcut))
      ImGui.endMenuBar()
      result
    } else
      listOf()

fun getShortcutForContext(bindings: KeystrokeBindings, context: String): GetShortcut = { command ->
  bindings[ContextCommand(context, command)]
}
