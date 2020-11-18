package silentorb.mythic.editing.components

import silentorb.mythic.editing.EditorCommands
import silentorb.mythic.editing.GetShortcut
import silentorb.mythic.editing.MenuItem
import silentorb.mythic.happenings.Commands

fun mainMenus(getShortcut: GetShortcut): Commands =
    drawMainMenuBar(getShortcut, listOf(
        MenuItem("Edit", items = listOf(
            MenuItem("Undo", EditorCommands.undo),
            MenuItem("Redo", EditorCommands.redo),
        )),
    ))
