package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiMouseButton
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.drawMenuBar
import silentorb.mythic.editing.components.panel
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector4i

const val defaultViewportId = "viewport"

fun viewportMenus(getShortcut: GetShortcut): MenuResponse =
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
            MenuItem("Draw Flat", EditorCommands.renderingModeFlat),
            MenuItem("Draw Lit", EditorCommands.renderingModeLit),
        ))
    ))

fun drawViewportPanel(editor: Editor): PanelResponse =
    panel(editor, "Viewport", Contexts.viewport, ::viewportMenus, ImGuiWindowFlags.NoBackground) {
      val menuHeight = 0
      val viewport = Vector4i(
          ImGui.getWindowPosX().toInt(), ImGui.getWindowPosY().toInt() + menuHeight,
          ImGui.getWindowSizeX().toInt(), ImGui.getWindowSizeY().toInt() - menuHeight
      )

      val mousePositionReference = ImVec2()
      ImGui.getMousePos(mousePositionReference)
      val mousePosition = Vector2i(
          mousePositionReference.x.toInt() - viewport.x,
          mousePositionReference.y.toInt() - viewport.y
      )

      val isInBounds = mousePosition.x > 0 &&
          mousePosition.y > 0 &&
          mousePosition.x < viewport.z &&
          mousePosition.y < viewport.w

      val clickCommands =
          if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left) && isInBounds)
            listOf(Command(EditorCommands.startNodeDrillDown, mousePosition))
          else if (ImGui.isMouseClicked(ImGuiMouseButton.Left) && isInBounds)
            listOf(Command(EditorCommands.startNodeSelect, mousePosition))
          else
            listOf()

      val viewportCommands = if (viewport != editor.viewportBoundsMap[defaultViewportId])
        listOf(Command(EditorCommands.setViewportBounds, mapOf(defaultViewportId to viewport)))
      else
        listOf()

      if (editor.operation != null) {
        ImGui.setCursorPos(60f, viewport.y + viewport.z - 20f)
        ImGui.text(editor.operation.type.name)
      }
      viewportCommands + clickCommands
    }
