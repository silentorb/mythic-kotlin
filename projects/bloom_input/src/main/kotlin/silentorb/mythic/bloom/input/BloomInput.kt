package silentorb.mythic.bloom.input

import silentorb.mythic.bloom.ButtonState
import silentorb.mythic.bloom.InputState
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.platforming.Devices
import silentorb.mythic.spatial.toVector2i

fun newBloomInputState(deviceState: InputDeviceState) =
    InputState(
        mousePosition = deviceState.mousePosition.toVector2i(),
        mouseButtons = listOf(
            if (deviceState.events.any { it.device == Devices.mouse && it.index == 0 })
              ButtonState.down
            else
              ButtonState.up
        ),
        events = listOf()
    )
