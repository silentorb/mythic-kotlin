package silentorb.mythic.commanding

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.reflectProperties

typealias CommandName = String

data class CharacterCommand(
    val type: CommandName,
    val target: Id,
    val value: Float = 1f
)

typealias Commands = List<CharacterCommand>

class CommonCharacterCommands {
  companion object {
    const val equipSlot0 = "equipSlot0"
    const val equipSlot1 = "equipSlot1"
    const val equipSlot2 = "equipSlot2"
    const val equipSlot3 = "equipSlot3"

    const val lookLeft = "lookLeft"
    const val lookRight = "lookRight"
    const val lookUp = "lookUp"
    const val lookDown = "lookDown"

    const val cameraLookLeft = "cameraLookLeft"
    const val cameraLookRight = "cameraLookRight"
    const val cameraLookUp = "cameraLookUp"
    const val cameraLookDown = "cameraLookDown"

    const val moveUp = "moveUp"
    const val moveDown = "moveDown"
    const val moveLeft = "moveLeft"
    const val moveRight = "moveRight"

    const val interactPrimary = "interactPrimary"
    const val interactSecondary = "interactSecondary"
    const val stopInteracting = "stopInteracting"

    const val jump = "jump"
    const val ability = "ability"
    const val duck = "duck"
    const val run = "run"

    const val switchView = "switchView"
    const val joinGame = "joinGame"
  }
}

val commonCharacterCommands = reflectProperties<CommandName>(CommonCharacterCommands)

// Normally triggered by GUI events and does not need to be bound to device buttons

val gameStrokes = setOf(
    CommonCharacterCommands.equipSlot0,
    CommonCharacterCommands.equipSlot1,
    CommonCharacterCommands.equipSlot2,
    CommonCharacterCommands.equipSlot3,
    CommonCharacterCommands.switchView,
    CommonCharacterCommands.joinGame,
    CommonCharacterCommands.interactPrimary
)

fun filterCommands(id: Id, commands: Commands) =
    commands.filter({ it.target == id })
