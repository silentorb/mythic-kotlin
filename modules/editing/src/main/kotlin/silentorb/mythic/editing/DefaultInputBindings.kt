package silentorb.mythic.editing

import org.lwjgl.glfw.GLFW
import silentorb.mythic.cameraman.CameramanCommands
import silentorb.mythic.haft.*

fun defaultGamepadBindings() = mapOf(
    GAMEPAD_AXIS_LEFT_UP to CameramanCommands.moveForward,
    GAMEPAD_AXIS_LEFT_DOWN to CameramanCommands.moveBackwards,
    GAMEPAD_AXIS_LEFT_LEFT to CameramanCommands.moveLeft,
    GAMEPAD_AXIS_LEFT_RIGHT to CameramanCommands.moveRight,

    GAMEPAD_AXIS_TRIGGER_LEFT to CameramanCommands.moveUp,
    GAMEPAD_AXIS_TRIGGER_RIGHT to CameramanCommands.moveDown,

    GAMEPAD_AXIS_RIGHT_UP to CameramanCommands.lookUp,
    GAMEPAD_AXIS_RIGHT_DOWN to CameramanCommands.lookDown,
    GAMEPAD_AXIS_RIGHT_LEFT to CameramanCommands.lookLeft,
    GAMEPAD_AXIS_RIGHT_RIGHT to CameramanCommands.lookRight,
)

fun defaultKeyboardBindings() = mapOf(
    GLFW.GLFW_KEY_G to EditorCommands.startTranslating,
    GLFW.GLFW_KEY_X to EditorCommands.restrictAxisX,
    GLFW.GLFW_KEY_Y to EditorCommands.restrictAxisY,
    GLFW.GLFW_KEY_Z to EditorCommands.restrictAxisZ,
    GLFW.GLFW_KEY_ESCAPE to EditorCommands.cancelOperation,
)

fun defaultEditorBindings(): Bindings =
    createBindings(DeviceIndexes.keyboard, defaultKeyboardBindings()) +
        createBindings(DeviceIndexes.gamepad, defaultGamepadBindings())
