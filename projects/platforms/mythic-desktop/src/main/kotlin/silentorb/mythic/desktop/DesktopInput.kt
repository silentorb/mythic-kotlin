package silentorb.mythic.desktop

import silentorb.mythic.spatial.Vector2
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWScrollCallback
import silentorb.mythic.haft.*
import silentorb.mythic.platforming.*

val GamepadIndices = GLFW_JOYSTICK_1..GLFW_JOYSTICK_LAST

fun enumerateActiveGamepadIds(): List<Int> =
    GamepadIndices
        .filter { glfwJoystickPresent(it) }

//val deadZone = 0.15f
val deadZone = 0.25f

fun getGamepadAxes(device: Int, axisDirIndex: Int): Float {
  val axes = glfwGetJoystickAxes(device)
  return if (axes == null)
    0f
  else if (axisDirIndex < GAMEPAD_AXIS_TRIGGER_LEFT) {
    val axisIndex = axisDirIndex / 2
    val value = axes[axisIndex]

//  println(axisDirIndex.toString() + ", " + axisIndex + ", " + value + ", " + (axisDirIndex % 2))
    if (axisDirIndex % 2 == 1)
      if (value > deadZone) value else 0f
    else
      if (value < -deadZone) -value else 0f
  } else {
    val index = axisDirIndex - 4
    if (axes.capacity() <= index)
      0f
    else {
      val value = axes[index]
      if (value > deadZone) value else 0f
    }
  }
}

private val gamepadInputSource = { device: Int, trigger: Int ->
  if (trigger < GAMEPAD_BUTTON_A)
    getGamepadAxes(device, trigger)
  else {
    val buttons = glfwGetJoystickButtons(device)
    if (buttons != null && buttons[trigger - GAMEPAD_BUTTON_A].toInt() == GLFW_PRESS)
      1f
    else
      0f
  }
}

fun getKeyboardEvents(window: Long): List<InputEvent> {
  return keyboardKeys.mapNotNull { key ->
    if (glfwGetKey(window, key) == GLFW_PRESS)
      InputEvent(
          device = Devices.keyboard,
          index = key,
          value = 1f
      )
    else
      null
  }
}

fun getGamepadEvents(): List<InputEvent> {
  val gamepads = enumerateActiveGamepadIds()
  return gamepads.flatMap { gamepad ->
    (GAMEPAD_AXIS_LEFT_LEFT..GAMEPAD_BUTTON_DPAD_LEFT).mapNotNull { button ->
      val value = gamepadInputSource(gamepad, button)
      if (value != 0f)
        InputEvent(
            device = gamepad + Devices.gamepadFirst,
            index = button,
            value = value
        )
      else
        null
    }
  }
}

private var mouseScrollYBuffer: Float = 0f
private var mouseScrollY: Float = 0f

private fun mouseInputSource(window: Long) = { key: Int ->
  if (key < MouseCommands.skip) {
    if (glfwGetMouseButton(window, key) == GLFW_PRESS)
      1f
    else
      0f
  } else if (key == MouseCommands.scrollUp) {
    if (mouseScrollY > 0)
      mouseScrollY
    else
      0f
  } else if (key == MouseCommands.scrollDown) {
    if (mouseScrollY < 0)
      -mouseScrollY
    else
      0f
  } else {
    0f
  }
}

fun getMouseEvents(window: Long): List<InputEvent> {
  val getValue = mouseInputSource(window)
  return listOf(
      GLFW_MOUSE_BUTTON_1,
      GLFW_MOUSE_BUTTON_2,
      GLFW_MOUSE_BUTTON_3,
      MouseCommands.scrollDown,
      MouseCommands.scrollUp,
  )
      .mapNotNull { button ->
        val value = getValue(button)
        if (value != 0f)
          InputEvent(
              device = Devices.mouse,
              index = button,
              value = value
          )
        else
          null
      }
}

class DesktopInput(val window: Long) : PlatformInput {

  init {
    glfwSetInputMode(window, GLFW_STICKY_KEYS, 1)
    glfwSetScrollCallback(window, GLFWScrollCallback.create { window, xoffset, yoffset ->
      mouseScrollYBuffer = yoffset.toFloat()
    })
  }

  override fun update() {
    mouseScrollY = mouseScrollYBuffer
    mouseScrollYBuffer = 0f
  }

//  override fun getGamepads(): List<Gamepad> =
//      enumerateActiveGamepadIds()
//          .map { Gamepad(it, glfwGetJoystickName(it)) }
//
//  override val KeyboardInputSource = { key: Int ->
//    if (glfwGetKey(window, key) == GLFW_PRESS) {
//      1f
//    } else
//      0f
//  }
//
//  override val GamepadInputSource = gamepadInputSource
//
//  override val MouseInputSource = mouseInputSource(window)

  override fun getMousePosition(): Vector2 {
    val tempX = DoubleArray(1)
    val tempY = DoubleArray(1)
    glfwGetCursorPos(window, tempX, tempY)
    return Vector2(tempX[0].toFloat(), tempY[0].toFloat())
  }

  override fun setMouseVisibility(value: Boolean) {
    val mode = if (value) GLFW_CURSOR_NORMAL else GLFW_CURSOR_DISABLED
    glfwSetInputMode(window, GLFW_CURSOR, mode)
  }

  override fun getEvents(): List<InputEvent> {
    return getKeyboardEvents(window)
        .plus(getMouseEvents(window))
        .plus(getGamepadEvents())
  }
}
