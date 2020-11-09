package silentorb.mythic.editing.components

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import silentorb.mythic.editing.Id
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

fun updateSelectionToggle(selection: Set<Id>, id: Id) =
    if (ImGui.isItemClicked()) {
      if (selection.contains(id)) {
        selection - id
      } else {
        setOf(id)
      }
    } else
      selection

fun getSelectionCommands(selectCommandType: Any, selection: Set<Id>, id: Id): Commands =
    if (ImGui.isItemClicked()) {
      val newSelection = setOf(id)
      listOf(Command(selectCommandType, value = newSelection))
    } else
      listOf()
