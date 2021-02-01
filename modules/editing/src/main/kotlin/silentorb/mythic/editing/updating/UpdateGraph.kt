package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.nameText
import silentorb.mythic.ent.*
import silentorb.mythic.ent.scenery.gatherChildren
import silentorb.mythic.ent.scenery.getGraphRoots
import silentorb.mythic.happenings.handleCommands
import silentorb.mythic.scenery.SceneProperties

fun duplicateNode(graph: LooseGraph, node: Key): LooseGraph {
  val parent = getGraphValue<String>(graph, node, SceneProperties.parent)
  return if (parent == null)
    graph
  else {
    val selected = gatherSelectionHierarchy(graph, setOf(node)) +
        Entry(node, SceneProperties.parent, parent)

    mergeGraphsWithRenaming(graph, selected)
  }
}

fun updateSceneGraph(editor: Editor) = handleCommands<LooseGraph> { command, graph ->
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
      if (selection.size != 1 || clipboard == null)
        graph
      else {
        val selected = selection.first()
        val roots = getGraphRoots(clipboard)
        val glue = roots.map { Entry(it, SceneProperties.parent, selected) }
        val result = mergeGraphsWithRenaming(graph, clipboard) + glue
        result
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
        val key = nameText.get()
        renameNode(graph, selected, key)
      }
    }

    EditorCommands.moveFileItem -> {
      val selected = getSelectedFileItem(editor)
      val (from, to) = command.value as Pair<String, String>
      val previous = getBaseName(from).split('.').first()
      val next = getBaseName(to).split('.').first()
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
      val selectionAndChildren = gatherChildren(graph, selection)
      graph
          .filter { !selectionAndChildren.contains(it.source) }
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

    else -> graph
  }
}
