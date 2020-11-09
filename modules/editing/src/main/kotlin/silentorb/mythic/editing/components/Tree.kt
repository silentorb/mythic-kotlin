package silentorb.mythic.editing.components

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import silentorb.mythic.ent.Key
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

fun newTreeFlags(selected: Boolean, hasChildren: Boolean): Int {
  val selectionFlags = if (selected)
    ImGuiTreeNodeFlags.Selected
  else
    ImGuiTreeNodeFlags.None

  val leafFlags = if (hasChildren)
    ImGuiTreeNodeFlags.None
  else
    ImGuiTreeNodeFlags.Leaf

  return selectionFlags or leafFlags or
      ImGuiTreeNodeFlags.OpenOnArrow or
      ImGuiTreeNodeFlags.OpenOnDoubleClick or
      ImGuiTreeNodeFlags.DefaultOpen or
      (1 shl 11)
}

fun updateSelectionToggle(selection: Set<Key>, key: Key) =
    if (ImGui.isItemClicked()) {
      if (selection.contains(key)) {
        selection - key
      } else {
        setOf(key)
      }
    } else
      selection

fun getSelectionCommands(selectCommandType: Any, selection: Set<Key>, key: Key): Commands =
    if (ImGui.isItemClicked()) {
      val newSelection = setOf(key)
      listOf(Command(selectCommandType, value = newSelection))
    } else
      listOf()
