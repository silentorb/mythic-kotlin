package silentorb.mythic.editing

import imgui.ImColor
import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.spatial.Vector4i

private val nodeSelection: MutableSet<String> = mutableSetOf()

fun renderTree(graph: Graph, id: String, node: Node) {
  val selected = nodeSelection.contains(id)
  val children = graph.filter { it.value.parent == id }

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
      println("Removed $id")
      nodeSelection.remove(id)
    } else {
      println("Added $id")
      nodeSelection.add(id)
    }
  }
  if (isOpen) {
    for (child in children) {
      renderTree(graph, child.key, child.value)
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

fun drawEditor(state:  Editor): Editor {
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

  val graph = mapOf(
      "Node 1" to Node(
          type = "foo",
      ),
      "Node 2" to Node(
          type = "foo",
          parent = "Node 1"
      )
  )
  val rootNodes = graph.filter { it.value.parent == null }
  assert(rootNodes.size == 1)
  val (rootId, rootRecord) = rootNodes.entries.first()
  renderTree(graph, rootId, rootRecord)
  ImGui.end()

  ImGui.begin("Viewport", ImGuiWindowFlags.MenuBar or ImGuiWindowFlags.NoBackground)
  val viewport = Vector4i(
      ImGui.getWindowPosX().toInt(), ImGui.getWindowPosY().toInt(),
      ImGui.getWindowSizeX().toInt(), ImGui.getWindowSizeY().toInt()
  )
  ImGui.end()

  return state.copy(
      viewport = viewport
  )
}
