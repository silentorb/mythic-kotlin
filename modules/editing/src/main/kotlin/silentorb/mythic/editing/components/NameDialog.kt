package silentorb.mythic.editing.components

import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImString
import silentorb.mythic.editing.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

var nameText = ImString()

fun nameDialog(title: String, triggerCommand: Any, nextCommand: Any, initialValue: () -> String): (Commands) -> Commands = { commands ->
  if (commands.any { it.type == triggerCommand } && !ImGui.isPopupOpen(title)) {
    nameText.set(initialValue())
    ImGui.openPopup(title)
  }

  ImGui.setNextItemWidth(100f)
  if (ImGui.beginPopupModal(title)) {
    ImGui.setKeyboardFocusHere()
    val pressedEnter = ImGui.inputText("Name", nameText, ImGuiInputTextFlags.EnterReturnsTrue)
    checkActiveInputType(InputType.text)
    ImGui.separator()
    if (ImGui.button("Cancel") || isEscapePressed()) {
      nameText.set("")
      ImGui.closeCurrentPopup()
    }
    ImGui.sameLine()
    val result = if (ImGui.button("OK") || pressedEnter) {
      ImGui.closeCurrentPopup()
      activeInputType = null
      listOf(Command(nextCommand, nameText.get()))
    } else
      listOf()
    ImGui.endPopup()
    result
  } else
    listOf()
}

val newNodeNameDialog = nameDialog(
    "New Node",
    EditorCommands.addNodeWithNameDialog,
    EditorCommands.addNode
) { "" }

fun renameNodeDialog(editor: Editor) = nameDialog(
    "Rename Node",
    EditorCommands.renameNodeWithNameDialog,
    EditorCommands.renameNode
) {
  editor.state.nodeSelection.firstOrNull() ?: ""
}

val newFileNameDialog = nameDialog(
    "New File",
    EditorCommands.newFileWithNameDialog,
    EditorCommands.newFile
) { "" }

val newFolderNameDialog = nameDialog(
    "New Folder",
    EditorCommands.newFolderWithNameDialog,
    EditorCommands.newFolder
) { "" }
