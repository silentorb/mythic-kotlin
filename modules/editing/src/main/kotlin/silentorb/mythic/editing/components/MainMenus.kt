package silentorb.mythic.editing.components

import silentorb.mythic.editing.EditorCommands
import silentorb.mythic.editing.MenuTree

fun mainMenus(): List<MenuTree> =
    listOf(
        MenuTree("Edit", items = listOf(
            MenuTree("Undo", EditorCommands.undo),
            MenuTree("Redo", EditorCommands.redo),
        )),
        MenuTree("Game", items = listOf(
            MenuTree("Play Game", EditorCommands.playGame),
            MenuTree("Play Scene", EditorCommands.playScene),
        )),
    )
