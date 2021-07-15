package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiMouseButton
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.gizmoMenuToggleState
import silentorb.mythic.editing.components.panel
import silentorb.mythic.editing.general.MenuTree
import silentorb.mythic.editing.general.isShiftDown
import silentorb.mythic.happenings.Command
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector4i

const val defaultViewportId = "viewport"

fun renderingModeState(mode: RenderingMode): GetMenuItemState = { editor ->
  editor.persistentState.renderingModes[defaultViewportId] == mode
}

fun viewportMenus(): List<MenuTree> =
    listOf(
        MenuTree("Camera", key = Menus.camera, items = listOf(
            MenuTree("View Front", EditorCommands.viewFront),
            MenuTree("View Back", EditorCommands.viewBack),
            MenuTree("View Right", EditorCommands.viewRight),
            MenuTree("View Left", EditorCommands.viewLeft),
            MenuTree("View Top", EditorCommands.viewTop),
            MenuTree("View Bottom", EditorCommands.viewBottom),
            MenuTree("Toggle Projection", EditorCommands.toggleProjectionMode),
            MenuTree("Center on Selection", EditorCommands.centerOnSelection),
        )),
        MenuTree("Display", key = Menus.display, items = listOf(
            MenuTree("Draw Wireframe", EditorCommands.renderingModeWireframe,
                getState = renderingModeState(RenderingMode.wireframe)
            ),
            MenuTree("Draw Flat", EditorCommands.renderingModeFlat,
                getState = renderingModeState(RenderingMode.flat)
            ),
            MenuTree("Draw Lit", EditorCommands.renderingModeLit,
                getState = renderingModeState(RenderingMode.lit)
            ),
            MenuTree("Collision", command = Command(EditorCommands.toggleGizmoVisibility, GizmoTypes.collision),
                getState = gizmoMenuToggleState(GizmoTypes.collision)
            ),
        )),
    )

fun drawViewportPanel(editor: Editor): PanelResponse =
    panel(editor, "Viewport", Contexts.viewport, ImGuiWindowFlags.NoBackground) {
      val menuHeight = 35
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

      val clickCommands = if (isInBounds)
        if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left))
          listOf(Command(EditorCommands.startNodeDrillDown, mousePosition))
        else if (ImGui.isMouseClicked(ImGuiMouseButton.Left))
          if (editor.operation?.type == OperationType.connecting)
            listOf(Command(EditorCommands.trySelectJoint, mousePosition))
          else if (isShiftDown())
            listOf(Command(EditorCommands.startNodeSelectToggle, mousePosition))
          else
            listOf(Command(EditorCommands.startNodeSelectReplace, mousePosition))
        else
          listOf()
      else
        listOf()

      val viewportCommands = if (viewport != editor.viewportBoundsMap[defaultViewportId])
        listOf(Command(EditorCommands.setViewportBounds, mapOf(defaultViewportId to viewport)))
      else
        listOf()

      if (editor.operation != null) {
        val ki = ImGui.getCursorPosY()
        ImGui.setCursorPos(100f, viewport.w - 50f)
        ImGui.text(editor.operation.type.name.capitalize())
      }

      val camera = getEditorCamera(editor, defaultViewportId)
      if (camera != null) {
        ImGui.setCursorPos(viewport.z - 100f, 50f)
        ImGui.text(camera.projection.name.capitalize())
      }

      viewportCommands + clickCommands
    }
