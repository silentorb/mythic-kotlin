package silentorb.mythic.editing

import imgui.ImGui
import imgui.flag.ImGuiMouseButton
import org.lwjgl.glfw.GLFW
import silentorb.mythic.haft.Bindings
import silentorb.mythic.haft.DeviceIndexes
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.haft.mapInputToCommands
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

fun mapCommands(bindings: Bindings, deviceStates: List<InputDeviceState>): Commands =
    mapInputToCommands(setOf(), bindings, deviceStates)

fun getImGuiCommands(editor: Editor): Commands {
  return listOfNotNull(
      if (editor.operation != null && editor.operation.type != OperationType.connecting &&
          ImGui.isMouseClicked(ImGuiMouseButton.Left))
        Command(EditorCommands.commitOperation)
      else
        null,
  )
}

fun isTabPressed(deviceState: InputDeviceState): Boolean =
    deviceState.events.any { it.device == DeviceIndexes.keyboard && it.index == GLFW.GLFW_KEY_TAB }
