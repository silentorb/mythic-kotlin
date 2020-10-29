package silentorb.mythic.editing

import silentorb.mythic.haft.InputDeviceState

fun updateEditor(deviceStates: List<InputDeviceState>, previous: Editor): EditorState {
  val commands = mapCommands(defaultEditorBindings(), deviceStates)
  val cameras = previous.state.cameras
      .mapValues { (_, camera) ->
        updateFlyThroughCamera(commands, camera)
      }
  return previous.state.copy(
      cameras = cameras
  )
}
