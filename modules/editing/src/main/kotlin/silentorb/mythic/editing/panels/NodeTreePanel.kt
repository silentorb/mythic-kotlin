package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.panelBackground

fun renderTree(tree: SceneTree, id: String, selection: NodeSelection): NodeSelection {
  val selected = selection.contains(id)
  val children = tree.filter { it.value == id }

  val selectionFlags = if (selected)
    ImGuiTreeNodeFlags.Selected
  else
    ImGuiTreeNodeFlags.None

  val leafFlags = if (children.none())
    ImGuiTreeNodeFlags.Leaf
  else
    ImGuiTreeNodeFlags.None

  val flags = selectionFlags or leafFlags or
      ImGuiTreeNodeFlags.OpenOnArrow or
      ImGuiTreeNodeFlags.OpenOnDoubleClick or
      ImGuiTreeNodeFlags.DefaultOpen or
      (1 shl 11)

  val isOpen = ImGui.treeNodeEx("Tree-$id", flags, id)
  var nextSelection = if (ImGui.isItemClicked()) {
    if (selected) {
      selection - id
    } else {
      setOf(id)
    }
  }
  else
    selection

  if (isOpen) {
    for (child in children) {
      nextSelection = renderTree(tree, child.key, nextSelection)
    }
    ImGui.treePop()
  }

  return nextSelection
}

fun renderTree(editor: Editor, graph: Graph?): NodeSelection {
  ImGui.begin("Node Tree", ImGuiWindowFlags.MenuBar)
  panelBackground()

  val nextSelection = if (graph != null) {
    val tree = getSceneTree(graph)
    val rootNodes = getTripleKeys(graph)
        .plus(tree.values)
        .minus(tree.keys)
    assert(rootNodes.size == 1)
    val rootId = rootNodes.first()
    renderTree(tree, rootId, editor.state.selection)
  } else
    editor.state.selection

  ImGui.end()

  return nextSelection
}
