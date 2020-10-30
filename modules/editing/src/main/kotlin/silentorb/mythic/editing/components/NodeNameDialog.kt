package silentorb.mythic.editing.components

import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiKey
import imgui.type.ImString
import silentorb.mythic.editing.EditorCommands
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

var nodeNameText = ImString()

fun nodeNameDialog(title: String, triggerCommand: Any, nextCommand: Any): (Commands) -> Commands = { commands ->
  if (commands.any { it.type == triggerCommand } && !ImGui.isPopupOpen(title)) {
    nodeNameText.set("")
    ImGui.openPopup(title)
  }

  ImGui.setNextItemWidth(100f)
  if (ImGui.beginPopupModal(title)) {
    ImGui.setKeyboardFocusHere()
    val pressedEnter = ImGui.inputText("Name", nodeNameText, ImGuiInputTextFlags.EnterReturnsTrue)
    ImGui.separator()
    if (ImGui.button("Cancel") || ImGui.isKeyPressed(ImGui.getKeyIndex(ImGuiKey.Escape))) {
      nodeNameText.set("")
      ImGui.closeCurrentPopup()
    }
    ImGui.sameLine()
    val result = if (ImGui.button("OK") || pressedEnter) {
      ImGui.closeCurrentPopup()
      listOf(Command(nextCommand))
    } else
      listOf()
    ImGui.endPopup()
    result
  } else
    listOf()
}

val newNodeNameDialog = nodeNameDialog(
    "New Node",
    EditorCommands.addNodeWithNameDialog,
    EditorCommands.addNode
)

val renameNodeDialog = nodeNameDialog(
    "Rename Node",
    EditorCommands.renameNodeWithNameDialog,
    EditorCommands.renameNode
)
