package silentorb.mythic.editing.updating

import silentorb.mythic.editing.Editor
import silentorb.mythic.editing.EditorCommands
import silentorb.mythic.editing.components.nameText
import silentorb.mythic.editing.getBaseName
import silentorb.mythic.ent.*
import silentorb.mythic.ent.scenery.gatherChildren
import silentorb.mythic.ent.scenery.getGraphRoots
import silentorb.mythic.happenings.handleCommands
import silentorb.mythic.scenery.SceneProperties

fun duplicateNode(graph: Graph, node: Key): Graph {
  val parent = getGraphValue<String>(graph, node, SceneProperties.parent)
  return if (parent == null)
    graph
  else {
    val selected = gatherSelectionHierarchy(graph, setOf(node)) +
        Entry(node, SceneProperties.parent, parent)

    mergeGraphsWithRenaming(graph, selected)
  }
}

fun updateSceneGraph(editor: Editor) = handleCommands<Graph> { command, graph ->
  val state = editor.state
  val selection = state.nodeSelection

  when (command.type) {

    EditorCommands.addNode -> {
      if (state.nodeSelection.size != 1)
        graph
      else {
        val selected = state.nodeSelection.first()
        val key = nameText.get()
        graph + Entry(key, SceneProperties.parent, selected)
      }
    }

    EditorCommands.pasteNode -> {
      val clipboard = editor.clipboard
      if (state.nodeSelection.size != 1 || clipboard == null)
        graph
      else {
        val selected = state.nodeSelection.first()
        val roots = getGraphRoots(clipboard)
        val glue = roots.map { Entry(it, SceneProperties.parent, selected) }
        val result = mergeGraphsWithRenaming(graph, clipboard) + glue
        result
      }
    }

    EditorCommands.duplicateNode -> {
      if (state.nodeSelection.size != 1)
        graph
      else {
        state.nodeSelection
            .fold(graph, ::duplicateNode)
      }
    }

    EditorCommands.renameNode -> {
      if (state.nodeSelection.size != 1)
        graph
      else {
        val selected = state.nodeSelection.first()
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
      if (editor.enumerations.propertyDefinitions[newEntry.property]?.single == true)
        replaceValues(graph, listOf(newEntry))
      else
        graph + listOf(newEntry)
    }

    EditorCommands.removeGraphValue -> {
      val entry = command.value as Entry
      graph - listOf(entry)
    }

    else -> graph
  }
}
