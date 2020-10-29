package silentorb.mythic.editing

import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.happenings.Commands

fun updateEditorFromCommands(commands: Commands, editor: Editor): Editor {
  val cameras = editor.state.cameras
      .mapValues { (_, camera) ->
        updateFlyThroughCamera(commands, camera)
      }
  return editor.copy(
      state = editor.state.copy(
          cameras = cameras
      )
  )
}

fun updateEditor(deviceStates: List<InputDeviceState>, editor: Editor): Editor {
  val externalCommands = mapCommands(defaultEditorBindings(), deviceStates)
  val (nextEditor, guiCommands) = defineEditorGui(editor)
  val commands = externalCommands + guiCommands
  return updateEditorFromCommands(commands, nextEditor)
}
