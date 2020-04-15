package silentorb.mythic.bloom.input

import silentorb.mythic.haft.DeviceIndex
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.bloom.BloomId
import silentorb.mythic.bloom.ButtonState
import silentorb.mythic.platforming.PlatformInput
import silentorb.mythic.platforming.mouseDeviceIndex
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.toVector2i
import silentorb.mythic.bloom.InputState

data class GeneralCommandState(
    val commands: List<Any>,
    val mousePosition: Vector2,
    val mouseOffset: Vector2
)

typealias DeviceMap = Map<Int, DeviceIndex>

fun newBloomInputState(deviceState: InputDeviceState) =
    InputState(
        mousePosition = deviceState.mousePosition.toVector2i(),
        mouseButtons = listOf(
            if (deviceState.events.any { it.device == mouseDeviceIndex && it.index == 0 })
              ButtonState.down
            else
              ButtonState.up
        ),
        events = listOf()
    )

fun updateInputDeviceState(input: PlatformInput): InputDeviceState {
  input.update()
  return InputDeviceState(
      events = input.getEvents(),
      mousePosition = input.getMousePosition()
  )
}

fun updateInputDeviceStates(input: PlatformInput, deviceStates: List<InputDeviceState>): List<InputDeviceState> {
  val newDeviceState = updateInputDeviceState(input)
  return listOf(deviceStates.last(), newDeviceState)
}
