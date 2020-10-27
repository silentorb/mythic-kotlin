package silentorb.mythic.cameraman

import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3

object CameramanCommands {
  const val lookLeft = "lookLeft"
  const val lookRight = "lookRight"
  const val lookUp = "lookUp"
  const val lookDown = "lookDown"

  const val moveUp = "moveUp"
  const val moveDown = "moveDown"
  const val moveLeft = "moveLeft"
  const val moveRight = "moveRight"

  const val switchView = "switchView"
}

val playerMoveMap = mapOf(
    CameramanCommands.moveLeft to Vector3(-1f, 0f, 0f),
    CameramanCommands.moveRight to Vector3(1f, 0f, 0f),
    CameramanCommands.moveUp to Vector3(0f, 1f, 0f),
    CameramanCommands.moveDown to Vector3(0f, -1f, 0f)
)

val lookMap = mapOf(
    CameramanCommands.lookLeft to Vector3(0f, 0f, 1f),
    CameramanCommands.lookRight to Vector3(0f, 0f, -1f),
    CameramanCommands.lookUp to Vector3(0f, 1f, 0f),
    CameramanCommands.lookDown to Vector3(0f, -1f, 0f)
)

fun characterMovementVector(commands: Commands, orientation: Quaternion): Vector3? {
  val offsetVector = joinInputVector(commands, playerMoveMap)
  return if (offsetVector != null) {
    val offset = orientation * offsetVector
    offset
  } else {
    null
  }
}
