package silentorb.mythic.characters

import silentorb.mythic.ent.reflectProperties
import silentorb.mythic.happenings.CommandName

object CharacterRigCommands {
  const val lookLeft = "lookLeft"
  const val lookRight = "lookRight"
  const val lookUp = "lookUp"
  const val lookDown = "lookDown"

  const val moveUp = "moveUp"
  const val moveDown = "moveDown"
  const val moveLeft = "moveLeft"
  const val moveRight = "moveRight"
}

val characterRigCommands = reflectProperties<CommandName>(CharacterRigCommands)
