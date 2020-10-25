package silentorb.mythic.editing

import imgui.ImColor
import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.spatial.Vector4i

private val nodeSelection: MutableSet<String> = mutableSetOf()

fun renderTree(tree: SceneTree, id: String) {
  val selected = nodeSelection.contains(id)
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
      (1 shl 11)

  val isOpen = ImGui.treeNodeEx("Tree-$id", flags, id)
  if (ImGui.isItemClicked()) {
    if (selected) {
      nodeSelection.remove(id)
    } else {
      nodeSelection.clear()
      nodeSelection.add(id)
    }
  }
  if (isOpen) {
    for (child in children) {
      renderTree(tree, child.key)
    }
    ImGui.treePop()
  }
}

fun panelBackground() {
  val drawList = ImGui.getWindowDrawList()
  drawList.addRectFilled(
      ImGui.getWindowPosX(),
      ImGui.getWindowPosY(),
      ImGui.getWindowPosX() + ImGui.getWindowSizeX(),
      ImGui.getWindowPosY() + ImGui.getWindowSizeY(),
      ImColor.intToColor(64, 64, 64, 255)
  )
}

fun drawEditor(editor: Editor): Editor {
  val graph = getActiveEditorGraph(editor)

  if (ImGui.beginMainMenuBar()) {
    if (ImGui.beginMenu("Edit")) {
      ImGui.menuItem("Foo", "F")
      ImGui.menuItem("Kung Foo", "K")
      ImGui.endMenu()
    }
    ImGui.endMainMenuBar()
  }

  ImGui.setNextWindowBgAlpha(0f)
  ImGui.dockSpaceOverViewport()

  ImGui.begin("Tree", ImGuiWindowFlags.MenuBar)
  panelBackground()

  if (graph != null) {
    val tree = getSceneTree(graph)
    val rootNodes = getTripleKeys(graph)
        .plus(tree.values)
        .minus(tree.keys)
    assert(rootNodes.size == 1)
    val rootId = rootNodes.first()
    renderTree(tree, rootId)
  }
  ImGui.end()

  ImGui.begin("Viewport", ImGuiWindowFlags.MenuBar or ImGuiWindowFlags.NoBackground)
  val viewport = Vector4i(
      ImGui.getWindowPosX().toInt(), ImGui.getWindowPosY().toInt(),
      ImGui.getWindowSizeX().toInt(), ImGui.getWindowSizeY().toInt()
  )
  ImGui.end()

  return editor.copy(
      viewport = viewport
  )
}
