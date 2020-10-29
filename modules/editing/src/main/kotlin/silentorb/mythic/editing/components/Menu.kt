package silentorb.mythic.editing.components

import imgui.ImGui
import silentorb.mythic.editing.MenuItem
import silentorb.mythic.happenings.Command

fun drawMenuItems(items: List<MenuItem>): List<Command> {
  return items.flatMap { item ->
    if (ImGui.menuItem(item.label, item.shortcut)) {
      if (item.items != null)
        drawMenuItems(item.items)
      else if (item.command != null)
        listOf(Command(type = item.command))
      else
        listOf()
    } else
      listOf()
  }
}

fun drawMenu(label: String, items: List<MenuItem>): List<Command> =
    if (ImGui.beginMenu(label)) {
      val result = drawMenuItems(items)
      ImGui.endMenu()
      result
    } else
      listOf()

fun drawMainMenuBar(items: List<MenuItem>): List<Command> =
    if (ImGui.beginMainMenuBar()) {
      val result = items
          .flatMap { drawMenu(it.label, it.items!!) }
      ImGui.endMainMenuBar()
      result
    } else
      listOf()
