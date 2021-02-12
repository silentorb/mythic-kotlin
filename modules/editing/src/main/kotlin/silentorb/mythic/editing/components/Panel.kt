package silentorb.mythic.editing.components

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.editing.Editor
import silentorb.mythic.editing.MenuChannel
import silentorb.mythic.editing.MenuDefinition
import silentorb.mythic.editing.PanelResponse
import silentorb.mythic.happenings.Commands

fun panel(
    editor: Editor,
    title: String,
    context: String,
    flags: Int = ImGuiWindowFlags.None,
    body: () -> Commands
): PanelResponse {
  val menu = editor.enumerations.menus[context]
  val menuFlags = if (menu != null)
    ImGuiWindowFlags.MenuBar
  else
    ImGuiWindowFlags.None

  ImGui.begin(title, flags or menuFlags)

  val menuCommands = if (menu != null) {
    drawMenuBar(newMenuChannel(editor, context), menu)
  } else
    listOf()

  val bodyCommands = body()

  val contextResponse = if (ImGui.isWindowFocused())
    context
  else
    null
  ImGui.end()

  val commands = bodyCommands + menuCommands

  return contextResponse to commands
}
