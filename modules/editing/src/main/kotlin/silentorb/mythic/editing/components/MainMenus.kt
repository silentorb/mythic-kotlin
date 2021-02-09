package silentorb.mythic.editing.components

import silentorb.mythic.editing.EditorCommands
import silentorb.mythic.editing.GetShortcut
import silentorb.mythic.editing.MenuChannel
import silentorb.mythic.editing.MenuItem
import silentorb.mythic.happenings.Commands

fun mainMenus(channel: MenuChannel): Commands =
    drawMainMenuBar(channel, listOf(
        MenuItem("Edit", items = listOf(
            MenuItem("Undo", EditorCommands.undo),
            MenuItem("Redo", EditorCommands.redo),
        )),
        MenuItem("Game", items = listOf(
            MenuItem("Play Game", EditorCommands.playGame),
            MenuItem("Play Scene", EditorCommands.playScene),
        )),
    ))
