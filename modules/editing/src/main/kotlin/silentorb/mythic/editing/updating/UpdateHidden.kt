package silentorb.mythic.editing.updating

import silentorb.mythic.editing.main.Editor
import silentorb.mythic.editing.main.EditorCommands
import silentorb.mythic.editing.main.NodeSet
import silentorb.mythic.editing.main.getActiveEditorGraph
import silentorb.mythic.happenings.handleCommands

fun updateHidden(editor: Editor, selection: NodeSet) = handleCommands<NodeSet> { command, hidden ->
  val graph = getActiveEditorGraph(editor)
  if (graph != null) {
    when (command.type) {
      EditorCommands.hide -> hidden + selection
      EditorCommands.unhide -> setOf()
      else -> hidden
    }
  } else
    hidden
}
