package silentorb.mythic.haft

import silentorb.mythic.platforming.PlatformInput

typealias DeviceTypeMap = Map<Int, Int>

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
