package silentorb.mythic.editing.updating

import silentorb.mythic.editing.CameraRig
import silentorb.mythic.editing.EditorCommands
import silentorb.mythic.editing.getCameraPivot
import silentorb.mythic.editing.updateFlyThroughCamera
import silentorb.mythic.happenings.Command
import silentorb.mythic.scenery.ProjectionType
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.getYawAndPitch

fun applyCameraPreset(camera: CameraRig, lookat: Vector3): CameraRig {
  val pivot = getCameraPivot(camera)
  val location = pivot - lookat * camera.pivotDistance
  val rotation = getYawAndPitch(lookat)
  return camera.copy(
      location = location,
      rotation = rotation,
  )
}

fun toggleProjectionMode(camera: CameraRig): CameraRig =
    camera.copy(
        projection = if (camera.projection == ProjectionType.perspective)
          ProjectionType.orthographic
        else
          ProjectionType.perspective
    )

fun applyCameraPresets(commandType: Any, camera: CameraRig): CameraRig? {
  return when (commandType) {
    EditorCommands.viewFront -> applyCameraPreset(camera, Vector3(0f, 1f, 0f))
    EditorCommands.viewBack -> applyCameraPreset(camera, Vector3(0f, -1f, 0f))
    EditorCommands.viewRight -> applyCameraPreset(camera, Vector3(-1f, 0f, 0f))
    EditorCommands.viewLeft -> applyCameraPreset(camera, Vector3(1f, 0f, 0f))
    EditorCommands.viewTop -> applyCameraPreset(camera, Vector3(0f, 0f, -1f))
    EditorCommands.viewBottom -> applyCameraPreset(camera, Vector3(0f, 0f, 1f))
    EditorCommands.toggleProjectionMode -> toggleProjectionMode(camera)
    else -> null
  }
}

fun applyCameraPresets(commands: List<Command>, camera: CameraRig): CameraRig? =
    commands
        .mapNotNull { applyCameraPresets(it.type, camera) }
        .firstOrNull()

fun updateCamera(mouseOffset: Vector2, commands: List<Command>, camera: CameraRig): CameraRig {
  return applyCameraPresets(commands, camera)
      ?: updateFlyThroughCamera(mouseOffset, commands, camera)
}
