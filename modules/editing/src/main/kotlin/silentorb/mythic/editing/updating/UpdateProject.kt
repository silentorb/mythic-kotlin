package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.ent.uniqueNodeName
import silentorb.mythic.happenings.Commands
import silentorb.mythic.happenings.handleCommands
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun getSelectedFileItem(editor: Editor): FileItem? {
  val selected = editor.persistentState.fileSelection.firstOrNull()
  return if (selected == null)
    null
  else
    editor.fileItems[selected]
}

fun newFileItem(type: FileItemType, parentPath: String, name: String, items: FileItems): FileItems {
  val fullPath = "$parentPath/$name"
  val newItem = FileItem(
      type = type,
      name = name,
      fullPath = fullPath,
      parent = parentPath
  )
  return items + (newItem.fullPath to newItem)
}

fun handleProjectCommands(editor: Editor) = handleCommands<FileItems> { command, items ->
  when (command.type) {

    EditorCommands.newFile -> {
      val selected = getSelectedFileItem(editor)
      if (selected == null)
        items
      else {
        val key = command.value as String
        val name = key + sceneFileExtension
        val fullPath = selected.fullPath + "/" + name
        val absolutePath = resolveProjectFilePath(editor, fullPath)
        File(absolutePath).writeText("{\"graph\":[[\"$key\",\"\",\"\"]]}\n")
        newFileItem(FileItemType.file, selected.fullPath, name, items)
      }
    }

    EditorCommands.duplicateFile -> {
      val selected = getSelectedFileItem(editor)
      if (selected == null)
        items
      else {
        val parent =getParentPath(selected.fullPath)
        val siblings = File( resolveProjectFilePath(editor, parent)).listFiles()
            ?.map { it.name.split(".").first() }
            ?: listOf()

        val name = selected.name.split(".").first()
        val newName = uniqueNodeName(siblings, name) + sceneFileExtension
        val fullPath = parent + "/" + newName
        Files.copy(
            Path.of(resolveProjectFilePath(editor, selected.fullPath)),
            Path.of(resolveProjectFilePath(editor, fullPath))
        )
        newFileItem(FileItemType.file, parent, newName, items)
      }
    }

    EditorCommands.newFolder -> {
      val selected = getSelectedFileItem(editor)
      if (selected == null)
        items
      else {
        val name = command.value as String
        val fullPath = selected.fullPath + "/" + name
        val absolutePath = resolveProjectFilePath(editor, fullPath)
        File(absolutePath).mkdir()
        newFileItem(FileItemType.folder, selected.fullPath, name, items)
      }
    }

    EditorCommands.deleteFileItem -> {
      val selected = getSelectedFileItem(editor)
      if (selected == null)
        items
      else {
        Files.deleteIfExists(Paths.get(resolveProjectFilePath(editor, selected.fullPath)))
        items - selected.fullPath
      }
    }

    EditorCommands.moveFileItem -> {
      val (source, target) = command.value as Pair<String, String>
      val previous = items[source]
      if (previous == null)
        items
      else {
        val copied = copyRecursive(items, previous, target)
        val deleted = selectRecursive(items, previous.fullPath)
        Files.move(
            Paths.get(resolveProjectFilePath(editor, source)),
            Paths.get(resolveProjectFilePath(editor, target))
        )
        items + copied - deleted
      }
    }

    else -> items
  }
}

fun updateProject(commands: Commands, editor: Editor): FileItems {
  val items = editor.fileItems
  return handleProjectCommands(editor)(commands, items)
}
