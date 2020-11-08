package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.happenings.Commands
import silentorb.mythic.happenings.handleCommands
import java.io.File

fun getSelectedFileItem(editor: Editor): FileItem? {
  val selected = editor.state.fileSelection.firstOrNull()
  return if (selected == null)
    null
  else
    editor.fileItems[selected]
}

fun handleProjectCommands(editor: Editor) = handleCommands<FileItems> { command, items ->
  when (command.type) {
    EditorCommands.newFile -> {
      val selected = getSelectedFileItem(editor)
      if (selected == null)
        items
      else {
        val name = (command.value as String) + sceneFileExtension
        val fullPath = selected.fullPath + "/" + name
        val newItem = FileItem(
            type = FileItemType.file,
            name = name,
            fullPath = fullPath,
            parent = selected.fullPath
        )
        val absolutePath = resolveProjectFilePath(editor, fullPath)
        File(absolutePath).writeText("{\"graph\":[[\"root\",\"\",\"\"]]}\n")
        items + (newItem.fullPath to newItem)
      }
    }
    else -> items
  }
}

fun updateProject(commands: Commands, editor: Editor): FileItems {
  val items = editor.fileItems
  return handleProjectCommands(editor)(commands, items)
}
