package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import silentorb.mythic.editing.components.*
import silentorb.mythic.editing.general.MenuTree
import silentorb.mythic.editing.main.*
import silentorb.mythic.ent.*
import silentorb.mythic.ent.scenery.getSceneTree
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.scenery.SceneProperties

fun sceneTreeMenus(): List<MenuTree> =
    listOf(
        MenuTree("Edit", key = Menus.edit, items = listOf(
            MenuTree("Add Node", EditorCommands.addNodeWithNameDialog),
            MenuTree("Rename Node", EditorCommands.renameNodeWithNameDialog),
            MenuTree("Delete Node", EditorCommands.deleteNode),
            MenuTree("Copy Node", EditorCommands.copyNode),
            MenuTree("Paste Node", EditorCommands.pasteNode),
            MenuTree("Duplicate Node", EditorCommands.duplicateNode),
        ))
    )

fun sceneTreeDragSource(graph: Graph, node: Key): Commands =
    dragTargets(mapOf(
        DraggingTypes.file to DragTarget({ payload ->
          val sourcePath = payload as? FileItem
          if (sourcePath == null)
            false
          else
            sourcePath.type == FileItemType.file
        }) { payload ->
          val source = payload as FileItem
          val typeName = source.name.split(".").first()
          val key = uniqueNodeName(getGraphKeys(graph), typeName)
          listOf(
              Command(EditorCommands.setGraphValue, Entry(key, SceneProperties.parent, node)),
              Command(EditorCommands.setGraphValue, Entry(key, SceneProperties.type, typeName)),
              Command(EditorCommands.setNodeSelection, setOf(key)),
          )
        },
        DraggingTypes.node to DragTarget({ payload ->
          payload is Key && payload != node
        }) { payload ->
          val source = payload as Key
          listOf(
              Command(EditorCommands.moveNode, source to node),
          )
        }
    ))

fun renderTree(graph: Graph, tree: SceneTree, node: String, selection: NodeSet): Commands {
  val selected = selection.contains(node)
  val children = tree.filter { it.value == node }
  val flags = newTreeFlags(selected) or leafFlags(children.any()) or ImGuiTreeNodeFlags.DefaultOpen

  val isOpen = ImGui.treeNodeEx("Tree-$node", flags, node)
  val selectionCommands = getSelectionCommands(EditorCommands.setNodeSelection, selection, node)
  val hasParent = tree.containsKey(node)

  if (hasParent) {
    dragSource(DraggingTypes.node, node) {
      ImGui.text(node)
    }
  }

  val dragCommands = sceneTreeDragSource(graph, node)

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
    panel(editor, "Node Tree", Contexts.nodes) {
      panelBackground()

      if (graph != null) {
        val tree = getSceneTree(graph)
        val rootNodes = getGraphKeys(graph)
            .plus(tree.values)
            .minus(tree.keys)

//        assert(rootNodes.size == 1)
        rootNodes.flatMap { root ->
          renderTree(graph, tree, root, getNodeSelection(editor))
        }

      } else
        listOf()
    }
