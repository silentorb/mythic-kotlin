package silentorb.mythic.editing.components

import imgui.ImGui
import silentorb.mythic.editing.MenuItem
import silentorb.mythic.editing.isShortcutPressed
import silentorb.mythic.happenings.Command

fun pollMenuItem(item: MenuItem): List<Command> =
    if (item.shortcut != null && item.command != null && isShortcutPressed(item.shortcut))
      listOf(Command(type = item.command))
    else
      listOf()

fun pollMenuItems(items: List<MenuItem>): List<Command> =
    items.flatMap(::pollMenuItem)

fun pollMenu(item: MenuItem): List<Command> =
    pollMenuItems(item.items ?: listOf())

fun drawMenuItem(item: MenuItem): List<Command> =
    if (ImGui.menuItem(item.label, item.shortcut) || item.shortcut != null && isShortcutPressed(item.shortcut)) {
      if (item.command != null)
        listOf(Command(type = item.command))
      else
        listOf()
    } else
      listOf()

fun drawMenuItems(items: List<MenuItem>): List<Command> =
    items.flatMap(::drawMenuItem)

fun drawMenu(item: MenuItem): List<Command> =
    if (ImGui.beginMenu(item.label)) {
      val result = drawMenuItems(item.items ?: listOf())
      ImGui.endMenu()
      result
    } else
      pollMenuItems(item.items ?: listOf())

fun drawMainMenuBar(items: List<MenuItem>): List<Command> =
    if (ImGui.beginMainMenuBar()) {
      val result = items.flatMap(::drawMenu)
      ImGui.endMainMenuBar()
      result
    } else
      items.flatMap(::pollMenu)

fun drawMenuBar(items: List<MenuItem>): List<Command> =
    if (ImGui.beginMenuBar()) {
      val result = items.flatMap(::drawMenu)
      ImGui.endMenuBar()
      result
    } else
      items.flatMap(::pollMenu)
