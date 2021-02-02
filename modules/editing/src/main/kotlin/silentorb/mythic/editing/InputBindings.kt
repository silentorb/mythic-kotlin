package silentorb.mythic.editing

import org.lwjgl.glfw.GLFW
import silentorb.mythic.cameraman.CameramanCommands
import silentorb.mythic.haft.*

fun defaultGamepadBindings() = mapOf(
    GAMEPAD_AXIS_LEFT_UP to CameramanCommands.moveForward,
    GAMEPAD_AXIS_LEFT_DOWN to CameramanCommands.moveBackward,
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
    GLFW.GLFW_KEY_R to EditorCommands.startRotating,
    GLFW.GLFW_KEY_S to EditorCommands.startScaling,
    GLFW.GLFW_KEY_X to EditorCommands.restrictAxisX,
    GLFW.GLFW_KEY_Y to EditorCommands.restrictAxisY,
    GLFW.GLFW_KEY_Z to EditorCommands.restrictAxisZ,
    GLFW.GLFW_KEY_ESCAPE to EditorCommands.cancelOperation,
)

fun keyboardFlyThroughBindings() =
    createBindings(DeviceIndexes.keyboard,
        mapOf(
            GLFW.GLFW_KEY_W to CameramanCommands.moveForward,
            GLFW.GLFW_KEY_S to CameramanCommands.moveBackward,
            GLFW.GLFW_KEY_A to CameramanCommands.moveLeft,
            GLFW.GLFW_KEY_D to CameramanCommands.moveRight,

            GLFW.GLFW_KEY_SPACE to CameramanCommands.moveUp,
            GLFW.GLFW_KEY_LEFT_CONTROL to CameramanCommands.moveDown,
            GLFW.GLFW_KEY_ESCAPE to EditorCommands.toggleFlythroughMode,
            GLFW.GLFW_KEY_F to EditorCommands.toggleFlythroughMode,
        )
    )

fun defaultMouseBindings() = mapOf(
    MOUSE_SCROLL_DOWN to EditorCommands.zoomOut,
    MOUSE_SCROLL_UP to EditorCommands.zoomIn,
)

fun defaultEditorBindings(): Bindings =
    createBindings(DeviceIndexes.keyboard, defaultKeyboardBindings()) +
        createBindings(DeviceIndexes.gamepad, defaultGamepadBindings()) +
        createBindings(DeviceIndexes.mouse, defaultMouseBindings())

fun defaultEditorMenuKeystrokes(): KeystrokeBindings = mapOf(
    ContextCommand(Contexts.nodes, EditorCommands.addNodeWithNameDialog) to "Ctrl+A",
    ContextCommand(Contexts.nodes, EditorCommands.renameNodeWithNameDialog) to "Ctrl+R",
    ContextCommand(Contexts.nodes, EditorCommands.deleteNode) to "Del",
    ContextCommand(Contexts.nodes, EditorCommands.copyNode) to "Ctrl+C",
    ContextCommand(Contexts.nodes, EditorCommands.pasteNode) to "Ctrl+V",
    ContextCommand(Contexts.nodes, EditorCommands.duplicateNode) to "Ctrl+D",

    ContextCommand(Contexts.project, EditorCommands.newFileWithNameDialog) to "Ctrl+N",
    ContextCommand(Contexts.project, EditorCommands.newFolderWithNameDialog) to "Ctrl+Shift+N",
    ContextCommand(Contexts.project, EditorCommands.deleteFileItem) to "Del",
    ContextCommand(Contexts.project, EditorCommands.duplicateFile) to "Ctrl+D",
    ContextCommand(Contexts.project, EditorCommands.renameFileItemWithNameDialog) to "Ctrl+R",

    ContextCommand(Contexts.viewport, EditorCommands.viewFront) to "$keypadKey 1",
    ContextCommand(Contexts.viewport, EditorCommands.viewBack) to "Ctrl+$keypadKey 1",
    ContextCommand(Contexts.viewport, EditorCommands.viewRight) to "$keypadKey 3",
    ContextCommand(Contexts.viewport, EditorCommands.viewLeft) to "Ctrl+$keypadKey 3",
    ContextCommand(Contexts.viewport, EditorCommands.viewTop) to "$keypadKey 7",
    ContextCommand(Contexts.viewport, EditorCommands.viewBottom) to "Ctrl+$keypadKey 7",
    ContextCommand(Contexts.viewport, EditorCommands.toggleProjectionMode) to "$keypadKey 5",
    ContextCommand(Contexts.viewport, EditorCommands.centerOnSelection) to "F",
    ContextCommand(Contexts.viewport, EditorCommands.toggleFlythroughMode) to "Shift+F",
    ContextCommand(Contexts.viewport, EditorCommands.deleteNode) to "Del",
    ContextCommand(Contexts.viewport, EditorCommands.startConnecting) to "Ctrl+J",

    // These will probably need modifier keys eventually
    ContextCommand(Contexts.viewport, EditorCommands.renderingModeWireframe) to "1",
    ContextCommand(Contexts.viewport, EditorCommands.renderingModeFlat) to "2",
    ContextCommand(Contexts.viewport, EditorCommands.renderingModeLit) to "3",

    ContextCommand(Contexts.global, EditorCommands.undo) to "Ctrl+Z",
    ContextCommand(Contexts.global, EditorCommands.redo) to "Ctrl+Shift+Z",
    ContextCommand(Contexts.global, EditorCommands.playGame) to "F5",
    ContextCommand(Contexts.global, EditorCommands.playScene) to "F6",
)
