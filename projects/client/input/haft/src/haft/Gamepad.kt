package haft

val maxAxisCount = 100

val GAMEPAD_AXIS_LEFT_LEFT = 0
val GAMEPAD_AXIS_LEFT_RIGHT = 1
val GAMEPAD_AXIS_LEFT_UP = 2
val GAMEPAD_AXIS_LEFT_DOWN = 3

val GAMEPAD_AXIS_RIGHT_LEFT = 4
val GAMEPAD_AXIS_RIGHT_RIGHT = 5
val GAMEPAD_AXIS_RIGHT_UP = 6
val GAMEPAD_AXIS_RIGHT_DOWN = 7

val GAMEPAD_AXIS_TRIGGER_LEFT = 8
val GAMEPAD_AXIS_TRIGGER_RIGHT = 9

val GAMEPAD_BUTTON_A = 100
val GAMEPAD_BUTTON_B = 101
val GAMEPAD_BUTTON_X = 102
val GAMEPAD_BUTTON_Y = 103
val GAMEPAD_BUTTON_LEFT_BUMPER = 104
val GAMEPAD_BUTTON_RIGHT_BUMPER = 105
val GAMEPAD_BUTTON_BACK = 106
val GAMEPAD_BUTTON_START = 107
//val GAMEPAD_BUTTON_GUIDE = 108 // THE GLFW Enums seem to be wrong about this button, at least with XBox controllers.
val GAMEPAD_BUTTON_LEFT_THUMB = 108
val GAMEPAD_BUTTON_RIGHT_THUMB = 109
val GAMEPAD_BUTTON_DPAD_UP = 110
val GAMEPAD_BUTTON_DPAD_RIGHT = 111
val GAMEPAD_BUTTON_DPAD_DOWN = 112
val GAMEPAD_BUTTON_DPAD_LEFT = 113

typealias GamepadDeviceId = Int
typealias GamepadSlots = List<Int?>

fun updateGamepadSlots(gamepads: List<GamepadDeviceId>, previousMap: GamepadSlots): GamepadSlots {
  var newDevices = gamepads.filter { !previousMap.contains(it) }
  return previousMap
      .map { value ->
        if (value != null) {
          if (gamepads.contains(value))
            value
          else
            null
        } else if (newDevices.size > 0) {
          val result = newDevices.first()
          newDevices = newDevices.drop(1)
          result
        } else
          null
      }

//  val result = mutableListOf<Int?>(previousMap.size)
//  var newDevices = gamepads.filter { !previousMap.contains(it) }
//  for (i in 0 until previousMap.size) {
//    val value = previousMap[i]
//    result[i] = value
//    if (value != null) {
//      if (!gamepads.contains(value))
//        result[i] = null
//    } else if (newDevices.size > 0) {
//      result[i] = newDevices.first()
//      newDevices = newDevices.drop(1)
//    }
//  }
//  return result

//  val (existing, added) = previousMap.partition { gamepads.contains(it) }
}

