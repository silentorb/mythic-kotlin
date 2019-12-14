package silentorb.mythic.characters

import org.joml.Vector2fMinimal
import silentorb.mythic.commanding.CommandName
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.getAngle
import silentorb.mythic.commanding.CommonCharacterCommands
import silentorb.mythic.commanding.Commands

val playerMoveMap = mapOf(
    CommonCharacterCommands.moveLeft to Vector3(-1f, 0f, 0f),
    CommonCharacterCommands.moveRight to Vector3(1f, 0f, 0f),
    CommonCharacterCommands.moveUp to Vector3(0f, 1f, 0f),
    CommonCharacterCommands.moveDown to Vector3(0f, -1f, 0f)
)

fun joinInputVector(commands: Commands, commandMap: Map<CommandName, Vector3>): Vector3? {
  val forces = commands.mapNotNull {
    val vector = commandMap[it.type]
    if (vector != null && it.value > 0)
      vector * it.value
    else
      null
  }
  if (forces.isEmpty())
    return null

  val offset = forces.reduce { a, b -> a + b }
  return if (offset == Vector3.zero)
    Vector3.zero
  else {
    if (offset.length() > 1f)
      offset.normalize()
    else
      offset
  }
}

fun getLookAtAngle(lookAt: Vector2fMinimal) =
    getAngle(Vector2(1f, 0f), lookAt.xy())
