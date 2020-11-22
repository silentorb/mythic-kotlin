package silentorb.mythic.editing.panels

import imgui.ImGui
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.*
import silentorb.mythic.ent.Entry
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.getGraphKeys
import silentorb.mythic.ent.scenery.getSceneTree
import silentorb.mythic.ent.uniqueNodeName
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.scenery.SceneProperties

fun nodeTreeMenus(getShortcut: GetShortcut): Commands =
    drawMenuBar(getShortcut, listOf(
        MenuItem("Edit", items = listOf(
            MenuItem("Add Node", EditorCommands.addNodeWithNameDialog),
            MenuItem("Rename Node", EditorCommands.renameNodeWithNameDialog),
            MenuItem("Delete Node", EditorCommands.deleteNode),
            MenuItem("Copy Node", EditorCommands.copyNode),
            MenuItem("Paste Node", EditorCommands.pasteNode),
            MenuItem("Duplicate Node", EditorCommands.duplicateNode),
        ))
    ))

fun renderTree(graph: Graph, tree: SceneTree, node: String, selection: NodeSelection): Commands {
  val selected = selection.contains(node)
  val children = tree.filter { it.value == node }
  val flags = newTreeFlags(selected) or leafFlags(children.any())

  val isOpen = ImGui.treeNodeEx("Tree-$node", flags, node)
  val selectionCommands = getSelectionCommands(EditorCommands.setNodeSelection, selection, node)

  val dragCommands = dragTargets(mapOf(
      DraggingTypes.file to DragTarget({ payload ->
        val sourcePath = payload as? FileItem
        if (sourcePath == null)
          false
        else
          sourcePath.type == FileItemType.file
      }) { payload ->
        val source = payload as? FileItem
        if (source == null)
          listOf()
        else {
          val typeName = source.name.split(".").first()
          val key = uniqueNodeName(getGraphKeys(graph), typeName)
          listOf(
              Command(EditorCommands.setGraphValue, Entry(key, SceneProperties.parent, node)),
              Command(EditorCommands.setGraphValue, Entry(key, SceneProperties.instance, typeName)),
          )
        }
      }
  ))

  val childCommands = mutableListOf<Command>()
  if (isOpen) {
    for (child in children) {
      childCommands += renderTree(graph, tree, child.key, selection)
    }
    ImGui.treePop()
  }

  return selectionCommands + childCommands + dragCommands
}

fun renderTree(editor: Editor, graph: Graph?): PanelResponse =
    panel(editor, "Node Tree", Contexts.nodes, ::nodeTreeMenus) {
      panelBackground()

      if (editor.state.graph != null) {
        ImGui.text(editor.state.graph)
        ImGui.separator()
      }

      if (graph != null) {
        val tree = getSceneTree(graph)
        val rootNodes = getGraphKeys(graph)
            .plus(tree.values)
            .minus(tree.keys)
        assert(rootNodes.size == 1)
        val rootId = rootNodes.first()
        renderTree(graph, tree, rootId, editor.state.nodeSelection)
      } else
        listOf()
    }