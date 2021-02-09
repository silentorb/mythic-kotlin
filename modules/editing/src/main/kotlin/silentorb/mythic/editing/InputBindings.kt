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

fun defineMenus(items: Map<String, Map<String, String>>): KeystrokeBindings =
    items.entries.flatMap { (context, children) ->
      children.entries.map { (command, shortcut) ->
        ContextCommand(context, command) to shortcut
      }
    }
        .associate { it }

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

    ContextCommand(Contexts.global, EditorCommands.undo) to "Ctrl+Z",
    ContextCommand(Contexts.global, EditorCommands.redo) to "Ctrl+Shift+Z",
    ContextCommand(Contexts.global, EditorCommands.playGame) to "F5",
    ContextCommand(Contexts.global, EditorCommands.playScene) to "F6",
) + defineMenus(
    mapOf(
        Contexts.viewport to mapOf(
            EditorCommands.viewFront to "$keypadKey 1",
            EditorCommands.viewBack to "Ctrl+$keypadKey 1",
            EditorCommands.viewRight to "$keypadKey 3",
            EditorCommands.viewLeft to "Ctrl+$keypadKey 3",
            EditorCommands.viewTop to "$keypadKey 7",
            EditorCommands.viewBottom to "Ctrl+$keypadKey 7",
            EditorCommands.toggleProjectionMode to "$keypadKey 5",
            EditorCommands.centerOnSelection to "F",
            EditorCommands.toggleFlythroughMode to "Shift+F",

            // These will probably need modifier keys eventually
            EditorCommands.renderingModeWireframe to "1",
            EditorCommands.renderingModeFlat to "2",
            EditorCommands.renderingModeLit to "3",
            EditorCommands.deleteNode to "Del",
            EditorCommands.startConnecting to "Ctrl+J",
            EditorCommands.duplicateNode to "Ctrl+D",
            EditorCommands.startTranslating to "G",
            EditorCommands.startRotating to "R",
            EditorCommands.startScaling to "S",
            EditorCommands.restrictAxisX to "X",
            EditorCommands.restrictAxisY to "Y",
            EditorCommands.restrictAxisZ to "Z",
        )
    )
)
