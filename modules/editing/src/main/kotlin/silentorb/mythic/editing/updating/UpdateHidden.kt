package silentorb.mythic.editing.updating

import silentorb.mythic.editing.Editor
import silentorb.mythic.editing.EditorCommands
import silentorb.mythic.editing.NodeSet
import silentorb.mythic.editing.getActiveEditorGraph
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
