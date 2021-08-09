package silentorb.mythic.editing.updating

import silentorb.mythic.editing.main.*
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.getGraphKeys
import silentorb.mythic.happenings.handleCommands
import silentorb.mythic.scenery.SceneProperties

fun updateNodeSelection(editor: Editor, nextGraph: Graph?) = handleCommands<NodeSet> { command, selection ->
  val graph = getActiveEditorGraph(editor)
  if (graph != null && nextGraph != null) {
    when (command.type) {
      EditorCommands.setNodeSelection -> command.value as NodeSet
      EditorCommands.addNode, EditorCommands.renameNode, EditorCommands.duplicateNode, EditorCommands.pasteNode -> {
        val newNodes = getGraphKeys(nextGraph) - getGraphKeys(graph)
        if (newNodes.any())
          newNodes
        else
          selection
      }

      EditorCommands.deleteNode -> {
        val deletedNodes = getGraphKeys(graph) - getGraphKeys(nextGraph)
        if (deletedNodes.any()) {
          val firstRemainingParent = graph.firstOrNull {
            deletedNodes.contains(it.source) && it.property == SceneProperties.parent && !deletedNodes.contains(it.target)
          }?.target as String?
          if (firstRemainingParent != null)
            setOf(firstRemainingParent)
          else
            selection
        } else
          selection
      }

      EditorCommands.undo -> getPreviousSnapshot(editor)?.nodeSelection ?: selection
      EditorCommands.redo -> getNextSnapshot(editor)?.nodeSelection ?: selection

      else -> selection
    }
  } else
    selection
}
