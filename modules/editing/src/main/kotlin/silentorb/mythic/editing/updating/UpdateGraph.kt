package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.nameText
import silentorb.mythic.ent.*
import silentorb.mythic.ent.scenery.getGraphRoots
import silentorb.mythic.ent.scenery.removeNodesAndChildren
import silentorb.mythic.happenings.handleCommands
import silentorb.mythic.scenery.SceneProperties

fun duplicateNode(graph: Graph, node: Key): Graph {
  val parent = getNodeValue<String>(graph, node, SceneProperties.parent)
  return if (parent == null)
    graph
  else {
    val selected = gatherSelectionHierarchy(graph, setOf(node)) +
        Entry(node, SceneProperties.parent, parent)

    mergeGraphsWithRenaming(graph, selected)
  }
}

fun updateSceneGraph(editor: Editor) = handleCommands<Graph> { command, graph ->
  val selection = getNodeSelection(editor)

  when (command.type) {

    EditorCommands.addNode -> {
      if (selection.size != 1)
        graph
      else {
        val selected = selection.first()
        val key = nameText.get()
        graph + Entry(key, SceneProperties.parent, selected)
      }
    }

    EditorCommands.pasteNode -> {
      val clipboard = editor.clipboard
      if (clipboard == null)
        graph
      else {
        val nodeSelection = selection.first()
        when (clipboard.type) {
          ClipboardDataTypes.scene -> {
            if (selection.size != 1)
              graph
            else {
              val data = clipboard.data as Graph
              val newClipboardGraph = prepareGraphForMerging(graph, data)
              val roots = getGraphRoots(newClipboardGraph)
              val glue = roots.map { Entry(it, SceneProperties.parent, nodeSelection) }
              val result = graph + newClipboardGraph + glue
              result
            }
          }
          ClipboardDataTypes.properties -> {
            val data = clipboard.data as Graph
            val properties = data.map { it.property }.toSet()
            val withoutProperties = graph
                .filter { !(selection.contains(it.source) && properties.contains(it.property)) }

            val newEntries = selection.flatMap { node ->
              data.map { it.copy(source = node) }
            }

            withoutProperties + newEntries
          }
          else -> graph
        }
      }
    }

    EditorCommands.duplicateNode -> {
      if (selection.size != 1)
        graph
      else {
        selection
            .fold(graph, ::duplicateNode)
      }
    }

    EditorCommands.renameNode -> {
      if (selection.size != 1)
        graph
      else {
        val selected = selection.first()
        val key = command.value as? String ?: nameText.get()
        renameNode(graph, selected, key)
      }
    }

    EditorCommands.moveFileItem -> {
      val selected = getSelectedFileItem(editor)
      val (from, to) = command.value as Pair<String, String>
      val previous = getFileName(from).split('.').first()
      val next = getFileName(to).split('.').first()
      if (selected == null || previous == next)
        graph
      else {
        graph
            .map { entry ->
              if (entry.property == SceneProperties.type && entry.target == previous)
                entry.copy(target = next)
              else
                entry
            }
            .toSet()
      }
    }

    EditorCommands.deleteNode -> {
      removeNodesAndChildren(graph, selection)
          .toSet()
    }

    EditorCommands.setGraphValue -> {
      val newEntry = command.value as Entry
      if (isManyToMany(editor, newEntry.property))
        graph + newEntry
      else
        replaceValues(graph, listOf(newEntry))
    }

    EditorCommands.replaceGraphValue -> {
      val (previous, next) = command.value as Pair<Entry, Entry>
      (graph - previous) + next
    }

    EditorCommands.removeGraphValue -> {
      val entry = command.value as Entry
      graph - listOf(entry)
    }

    else -> {
      val handler = editor.enumerations.graphEditors[command.type]
      if (handler != null)
        handler(editor, command, graph)
      else
        graph
    }
  }
}
