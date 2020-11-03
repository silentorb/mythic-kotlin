package silentorb.mythic.editing.components

import silentorb.mythic.editing.EditorCommands
import silentorb.mythic.editing.MenuItem
import silentorb.mythic.editing.keypadKey
import silentorb.mythic.happenings.Commands

fun mainMenus(): Commands =
    drawMainMenuBar(listOf(
        MenuItem("Edit", items = listOf(
            MenuItem("Add Node", "Ctrl+A", EditorCommands.addNodeWithNameDialog),
            MenuItem("Rename Node", "Ctrl+R", EditorCommands.renameNodeWithNameDialog),
            MenuItem("Delete Node", "Del", EditorCommands.deleteNode),
            MenuItem("Assign Mesh", "Shift+M", EditorCommands.assignMesh),
            MenuItem("Assign Texture", "Shift+T", EditorCommands.assignTexture),
        )),
        MenuItem("View", items = listOf(
            MenuItem("View Front", "$keypadKey 1", EditorCommands.viewFront),
            MenuItem("View Back", "Ctrl+$keypadKey 1", EditorCommands.viewBack),
            MenuItem("View Right", "$keypadKey 3", EditorCommands.viewRight),
            MenuItem("View Left", "Ctrl+$keypadKey 3", EditorCommands.viewLeft),
            MenuItem("View Top", "$keypadKey 7", EditorCommands.viewTop),
            MenuItem("View Bottom", "Ctrl+$keypadKey 7", EditorCommands.viewBottom),
            MenuItem("Toggle Projection", "$keypadKey 5", EditorCommands.toggleProjectionMode),
        ))
    ))
