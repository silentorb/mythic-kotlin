package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.nameText
import silentorb.mythic.ent.*
import silentorb.mythic.ent.scenery.getAbsoluteNodeTransform
import silentorb.mythic.ent.scenery.getGraphRoots
import silentorb.mythic.ent.scenery.getLocalNodeTransform
import silentorb.mythic.ent.scenery.removeNodesAndChildren
import silentorb.mythic.happenings.handleCommands
import silentorb.mythic.scenery.SceneProperties
import silentorb.mythic.spatial.Matrix

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

// Only returns the needed change entries, not the whole graph
fun transformNode(graph: Graph, node: Key, transform: Matrix): Graph {
  val previous = getLocalNodeTransform(graph, node)
  val next = transform * previous
  return listOfNotNull(
      if (next.translation() != previous.translation())
        Entry(node, SceneProperties.translation, next.translation())
      else
        null,
      if (next.rotation() != previous.rotation())
        Entry(node, SceneProperties.rotation, next.rotation())
      else
        null,
      if (next.getScale() != previous.getScale())
        Entry(node, SceneProperties.scale, next.getScale())
      else
        null,
  )
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
            val newEntries = selection
                .flatMap { node ->
                  data.map { it.copy(source = node) }
                }
            mergeGraphs(editor.enumerations.schema, graph, newEntries)
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

    EditorCommands.moveNode -> {
      val pair = command.value as? Pair<*, *>
      val node = pair?.first as? String
      val newParent = pair?.second as? String
      if (node == null || newParent == null)
        graph
      else {
        val oldParent = getNodeValue<String>(graph, node, SceneProperties.parent)!!
        val oldParentTransform = getAbsoluteNodeTransform(graph, oldParent)
        val newParentTransform = getAbsoluteNodeTransform(graph, newParent)
        val transform = oldParentTransform * newParentTransform.invert()
        val transformEntries = transformNode(graph, node, transform)
        val changes = listOf(Entry(node, SceneProperties.parent, newParent)) + transformEntries
        replaceValues(graph, changes)
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
