package silentorb.mythic.editing.components

import silentorb.mythic.editing.EditorCommands
import silentorb.mythic.editing.MenuItem
import silentorb.mythic.happenings.Commands

fun mainMenus(): Commands =
    drawMainMenuBar(listOf(
        MenuItem("Edit", items = listOf(
            MenuItem("Add Node", "Ctrl+A", EditorCommands.addNodeWithNameDialog),
            MenuItem("Rename Node", "Ctrl+R", EditorCommands.renameNodeWithNameDialog),
            MenuItem("Delete Node", "Del", EditorCommands.deleteNode),
            MenuItem("Assign Mesh", "Shift+M", EditorCommands.assignMesh),
            MenuItem("Assign Texture", "Shift+T", EditorCommands.assignTexture),
        ))
    ))
