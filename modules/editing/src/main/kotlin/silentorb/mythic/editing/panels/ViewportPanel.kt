package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.drawMainMenuBar
import silentorb.mythic.editing.components.drawMenuBar
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Vector4i

const val defaultViewportId = "viewport"

fun viewportMenus(): Commands =
    drawMenuBar(listOf(
        MenuItem("View", items = listOf(
            MenuItem("View Front", "$keypadKey 1", EditorCommands.viewFront),
            MenuItem("View Back", "Ctrl+$keypadKey 1", EditorCommands.viewBack),
            MenuItem("View Right", "$keypadKey 3", EditorCommands.viewRight),
            MenuItem("View Left", "Ctrl+$keypadKey 3", EditorCommands.viewLeft),
            MenuItem("View Top", "$keypadKey 7", EditorCommands.viewTop),
            MenuItem("View Bottom", "Ctrl+$keypadKey 7", EditorCommands.viewBottom),
            MenuItem("Toggle Projection", "$keypadKey 5", EditorCommands.toggleProjectionMode),
            MenuItem("Center on Selection", numpadPeriodKey, EditorCommands.centerOnSelection),
        ))
    ))

fun drawViewportPanel(editor: Editor): Commands {
  ImGui.begin("Viewport", ImGuiWindowFlags.MenuBar or ImGuiWindowFlags.NoBackground)
  val menuCommands = viewportMenus()
  val viewport = Vector4i(
      ImGui.getWindowPosX().toInt(), ImGui.getWindowPosY().toInt(),
      ImGui.getWindowSizeX().toInt(), ImGui.getWindowSizeY().toInt()
  )
  ImGui.end()

  val viewportCommands = if (viewport != editor.viewportBoundsMap[defaultViewportId])
    listOf(Command(EditorCommands.setViewportBounds, mapOf(defaultViewportId to viewport)))
  else
    listOf()
  return viewportCommands + menuCommands
}
