package silentorb.mythic.editing.components

import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImString
import silentorb.mythic.editing.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

var nameText = ImString()

typealias DialogCommandsEmitter = (String) -> Commands

fun emitDialogCommand(nextCommand: Any): DialogCommandsEmitter = { text ->
  listOf(Command(nextCommand, text))
}

fun nameDialog(title: String, triggerCommand: Any, nextCommands: DialogCommandsEmitter, initialValue: () -> String?): (Commands) -> Commands = { commands ->
  if (commands.any { it.type == triggerCommand } && !ImGui.isPopupOpen(title)) {
    val value = initialValue()
    if (value != null) {
      nameText.set(initialValue())
      ImGui.openPopup(title)
    }
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
      nextCommands(nameText.get())
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
    emitDialogCommand(EditorCommands.addNode)
) { "" }

fun renameNodeDialog(editor: Editor) = nameDialog(
    "Rename Node",
    EditorCommands.renameNodeWithNameDialog,
    emitDialogCommand(EditorCommands.renameNode)
) {
  editor.state.nodeSelection.firstOrNull()
}

val newFileNameDialog = nameDialog(
    "New File",
    EditorCommands.newFileWithNameDialog,
    emitDialogCommand(EditorCommands.newFile)
) { "" }

val newFolderNameDialog = nameDialog(
    "New Folder",
    EditorCommands.newFolderWithNameDialog,
    emitDialogCommand(EditorCommands.newFolder)
) { "" }

fun renameFileItemDialog(editor: Editor) = nameDialog(
    "Rename",
    EditorCommands.renameFileItemWithNameDialog,
    { text ->
      val selected = editor.fileItems[editor.state.fileSelection.firstOrNull()]
      if (selected == null)
        listOf()
      else
        listOf(Command(EditorCommands.moveFileItem, selected.fullPath to (selected.parent + "/" + text)))
    }
) {
  editor.fileItems[editor.state.fileSelection.firstOrNull()]?.name
}
