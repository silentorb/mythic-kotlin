package silentorb.mythic.editing.updating

import silentorb.mythic.editing.Editor
import silentorb.mythic.editing.EditorCommands

val onStartTranslating = onGraphEditingCommand(EditorCommands.startTranslating) { editor, graph ->
  val state = editor.state
  val selection = state.selection
  graph.filter { !selection.contains(it.source) }
}

fun updateOperation(editor: Editor, commandType: Any, operation: Any?): Any? {
  return when (commandType) {
//    EditorCommands.startTranslating->
//      if(operation != null)
//        operation
//    else
    else -> operation
  }
}

fun updateOperation(commandTypes: List<Any>, editor: Editor): Any? =
    commandTypes.fold(editor.operation) { a, commandType -> updateOperation(editor, commandType, a) }
