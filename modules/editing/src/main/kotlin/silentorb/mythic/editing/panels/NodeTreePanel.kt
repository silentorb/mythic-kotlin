package silentorb.mythic.editing.panels

import imgui.ImGui
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.getSelectionCommands
import silentorb.mythic.editing.components.newTreeFlags
import silentorb.mythic.editing.components.panelBackground
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.getGraphKeys
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

fun renderTree(tree: SceneTree, id: String, selection: NodeSelection): Commands {
  val selected = selection.contains(id)
  val children = tree.filter { it.value == id }
  val flags = newTreeFlags(selected, children.any())

  val isOpen = ImGui.treeNodeEx("Tree-$id", flags, id)
  val selectionCommands = getSelectionCommands(EditorCommands.setNodeSelection, selection, id)

  val childCommands = mutableListOf<Command>()
  if (isOpen) {
    for (child in children) {
      childCommands += renderTree(tree, child.key, selection)
    }
    ImGui.treePop()
  }

  return selectionCommands + childCommands
}

fun renderTree(editor: Editor, graph: Graph?): Commands {
  ImGui.begin("Node Tree")
  panelBackground()

  if (editor.state.graph != null) {
    ImGui.text(editor.state.graph)
    ImGui.separator()
  }

  val commands = if (graph != null) {
    val tree = getSceneTree(graph)
    val rootNodes = getGraphKeys(graph)
        .plus(tree.values)
        .minus(tree.keys)
    assert(rootNodes.size == 1)
    val rootId = rootNodes.first()
    renderTree(tree, rootId, editor.state.nodeSelection)
  } else
    listOf()

  ImGui.end()

  return commands
}
