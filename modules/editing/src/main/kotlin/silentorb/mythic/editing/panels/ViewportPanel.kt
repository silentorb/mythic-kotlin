package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiMouseButton
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.drawMenuBar
import silentorb.mythic.editing.components.getShortcutForContext
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector4i

const val defaultViewportId = "viewport"

fun viewportMenus(getShortcut: GetShortcut): Commands =
    drawMenuBar(getShortcut, listOf(
        MenuItem("View", items = listOf(
            MenuItem("View Front", EditorCommands.viewFront),
            MenuItem("View Back", EditorCommands.viewBack),
            MenuItem("View Right", EditorCommands.viewRight),
            MenuItem("View Left", EditorCommands.viewLeft),
            MenuItem("View Top", EditorCommands.viewTop),
            MenuItem("View Bottom", EditorCommands.viewBottom),
            MenuItem("Toggle Projection", EditorCommands.toggleProjectionMode),
            MenuItem("Center on Selection", EditorCommands.centerOnSelection),
            MenuItem("Draw Wireframe", EditorCommands.renderingModeWireframe),
            MenuItem("Draw Full", EditorCommands.renderingModeFull),
        ))
    ))

fun drawViewportPanel(editor: Editor): Commands {
  ImGui.begin("Viewport", ImGuiWindowFlags.MenuBar or ImGuiWindowFlags.NoBackground)
  val menuCommands = viewportMenus(getShortcutForContext(editor.bindings, Contexts.viewport))
  val viewport = Vector4i(
      ImGui.getWindowPosX().toInt(), ImGui.getWindowPosY().toInt(),
      ImGui.getWindowSizeX().toInt(), ImGui.getWindowSizeY().toInt()
  )

  val mousePositionReference = ImVec2()
  ImGui.getMousePos(mousePositionReference)
  val mousePosition = Vector2i(
      mousePositionReference.x.toInt() - viewport.x,
      mousePositionReference.y.toInt() - viewport.y
  )

  val clickCommands = if (
      ImGui.isMouseClicked(ImGuiMouseButton.Left) &&
      mousePosition.x > 0 &&
      mousePosition.y > 0 &&
      mousePosition.x < viewport.z &&
      mousePosition.y < viewport.w
  )
    listOf(Command(EditorCommands.startNodeSelect, mousePosition))
  else
    listOf()

  ImGui.end()
  val viewportCommands = if (viewport != editor.viewportBoundsMap[defaultViewportId])
    listOf(Command(EditorCommands.setViewportBounds, mapOf(defaultViewportId to viewport)))
  else
    listOf()
  return viewportCommands + menuCommands + clickCommands
}
