package silentorb.mythic.cameraman

import silentorb.mythic.spatial.Vector3

object CameramanCommands {
  const val lookLeft = "lookLeft"
  const val lookRight = "lookRight"
  const val lookUp = "lookUp"
  const val lookDown = "lookDown"

  const val moveForward = "moveForward"
  const val moveBackwards = "moveBackwards"
  const val moveLeft = "moveLeft"
  const val moveRight = "moveRight"
  const val moveUp = "moveUp"
  const val moveDown = "moveDown"
}

val playerMoveMap = mapOf(
    CameramanCommands.moveLeft to Vector3(0f, 1f, 0f),
    CameramanCommands.moveRight to Vector3(0f, -1f, 0f),
    CameramanCommands.moveForward to Vector3(1f, 0f, 0f),
    CameramanCommands.moveBackwards to Vector3(-1f, 0f, 0f),
    CameramanCommands.moveUp to Vector3(0f, 0f, 1f),
    CameramanCommands.moveDown to Vector3(0f, 0f, -1f),
)

val lookMap = mapOf(
    CameramanCommands.lookLeft to Vector3(0f, 0f, 1f),
    CameramanCommands.lookRight to Vector3(0f, 0f, -1f),
    CameramanCommands.lookUp to Vector3(0f, 1f, 0f),
    CameramanCommands.lookDown to Vector3(0f, -1f, 0f)
)
