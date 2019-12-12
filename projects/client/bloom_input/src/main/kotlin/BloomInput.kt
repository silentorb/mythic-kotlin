import haft.DeviceIndex
import haft.InputDeviceState
import mythic.bloom.BloomId
import mythic.bloom.ButtonState
import mythic.platforming.PlatformInput
import mythic.platforming.mouseDeviceIndex
import mythic.spatial.Vector2
import mythic.spatial.toVector2i

data class GeneralCommandState(
    val commands: List<Any>,
    val mousePosition: Vector2,
    val mouseOffset: Vector2
)

data class PlayerDevice(
    val player: BloomId,
    val device: DeviceIndex
)

typealias DeviceMap = Map<Int, PlayerDevice>

fun newBloomInputState(deviceState: InputDeviceState) =
    mythic.bloom.InputState(
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
