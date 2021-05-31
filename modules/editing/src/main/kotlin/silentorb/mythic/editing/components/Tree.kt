package silentorb.mythic.editing.components

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import silentorb.mythic.editing.isCtrlDown
import silentorb.mythic.editing.isShiftDown
import silentorb.mythic.ent.Key
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

fun leafFlags(hasChildren: Boolean): Int =
    if (hasChildren)
      ImGuiTreeNodeFlags.None
    else
      ImGuiTreeNodeFlags.Leaf

fun newTreeFlags(selected: Boolean): Int {
  val selectionFlags = if (selected)
    ImGuiTreeNodeFlags.Selected
  else
    ImGuiTreeNodeFlags.None

  return selectionFlags or
      ImGuiTreeNodeFlags.OpenOnArrow or
      ImGuiTreeNodeFlags.OpenOnDoubleClick or
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
      val newSelection = if (isShiftDown())
        selection + key
      else if (isCtrlDown())
        if (!selection.contains(key))
          selection + key
        else if (selection.size > 1)
          selection - key
        else
          selection
      else
        setOf(key)

      if (newSelection == selection)
        listOf()
      else
        listOf(Command(selectCommandType, value = newSelection))
    } else
      listOf()
