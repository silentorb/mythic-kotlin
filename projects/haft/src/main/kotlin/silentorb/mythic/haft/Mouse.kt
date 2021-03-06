package silentorb.mythic.haft

import silentorb.mythic.platforming.InputEvent
import silentorb.mythic.spatial.Vector2

const val MouseMovementLeft = 5
const val MouseMovementRight = 6
const val MouseMovementUp = 7
const val MouseMovementDown = 8

object MouseCommands {
  val button1 = 0
  val button2 = 1
  val button3 = 2
  val skip = 9
  val scrollDown = 10
  val scrollUp = 11

}

fun applyMouseAxis(device: Int, value: Float, firstIndex: Int, secondIndex: Int, scale: Float) =
    if (value > 0)
      InputEvent(device, firstIndex, value * scale)
    else if (value < 0)
      InputEvent(device, secondIndex, -value * scale)
    else
      null

fun getMouseOffset(deviceStates: List<InputDeviceState>): Vector2 =
    if (deviceStates.size < 2)
      Vector2.zero
    else
      deviceStates.last().mousePosition - deviceStates.dropLast(1).last().mousePosition

fun applyMouseMovement(device: Int, mouseOffset: Vector2): List<InputEvent> =
    listOfNotNull(
        applyMouseAxis(device, mouseOffset.x, MouseMovementRight, MouseMovementLeft, 1f),
        applyMouseAxis(device, mouseOffset.y, MouseMovementDown, MouseMovementUp, 1f)
    )

enum class HaftCommand {
  leftMouseClick
}
