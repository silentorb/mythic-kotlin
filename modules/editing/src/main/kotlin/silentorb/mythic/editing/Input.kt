package silentorb.mythic.editing

import imgui.ImGui
import imgui.flag.ImGuiMouseButton
import silentorb.mythic.haft.Bindings
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.haft.mapInputToCommands
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

fun mapCommands(bindings: Bindings, deviceStates: List<InputDeviceState>): Commands =
    mapInputToCommands(setOf(), bindings, deviceStates)

fun getImGuiCommands(editor: Editor): Commands {
  return listOfNotNull(
      if (editor.operation != null && ImGui.isMouseClicked(ImGuiMouseButton.Left))
        Command(EditorCommands.commitOperation)
      else
        null,
  )
}
