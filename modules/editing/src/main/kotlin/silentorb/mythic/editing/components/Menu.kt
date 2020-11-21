package silentorb.mythic.editing.components

import imgui.ImGui
import silentorb.mythic.editing.*
import silentorb.mythic.happenings.Command

//fun pollMenuItem(getShortcut: GetShortcut): (MenuItem) -> List<Command> = { item ->
//  val command = item.command
//  val shortcut = if (command!= null) getShortcut(command) else null
//  if (shortcut != null && command != null && isShortcutPressed(shortcut))
//    listOf(Command(type = command))
//  else
//    listOf()
//}
//
//fun pollMenuItems(getShortcut: GetShortcut): (List<MenuItem>) -> List<Command> = { items ->
//  items.flatMap(pollMenuItem(getShortcut))
//}
//
//fun pollMenu(getShortcut: GetShortcut): (MenuItem) -> List<Command> = { item ->
//  pollMenuItems(getShortcut)(item.items ?: listOf())
//}

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
//    pollMenuItems(getShortcut)(item.items ?: listOf())
}

fun drawMainMenuBar(getShortcut: GetShortcut, items: List<MenuItem>): List<Command> =
    if (ImGui.beginMainMenuBar()) {
      val result = items.flatMap(drawMenu(getShortcut))
      ImGui.endMainMenuBar()
      result
    } else
      listOf()
//      items.flatMap(pollMenu(getShortcut))

fun drawMenuBar(getShortcut: GetShortcut, items: List<MenuItem>): List<Command> =
    if (ImGui.beginMenuBar()) {
      val result = items.flatMap(drawMenu(getShortcut))
      ImGui.endMenuBar()
      result
    } else
      listOf()
//      items.flatMap(pollMenu(getShortcut))

fun getShortcutForContext(bindings: KeystrokeBindings, context: String): GetShortcut = { command ->
  bindings[ContextCommand(context, command)]
}
