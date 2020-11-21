package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.ent.scenery.getNodeTransform
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

fun centerOnSelection(editor: Editor, camera: CameraRig): CameraRig {
  val selection = editor.state.nodeSelection
  val graph = getActiveEditorGraph(editor)
  return if (selection.any() && graph != null){
    val nodeLocation = getNodeTransform(graph, selection.first()).translation()
    val pivot = getCameraPivot(camera)
    val pivotToCameraOffset = camera.location - pivot
    val nextLocation = nodeLocation + pivotToCameraOffset
    camera.copy(
        location = nextLocation
    )
  }
  else
    camera
}

fun applyCameraPresets(editor: Editor, commandType: Any, camera: CameraRig): CameraRig? {
  return when (commandType) {
    EditorCommands.viewFront -> applyCameraPreset(camera, Vector3(0f, 1f, 0f))
    EditorCommands.viewBack -> applyCameraPreset(camera, Vector3(0f, -1f, 0f))
    EditorCommands.viewRight -> applyCameraPreset(camera, Vector3(-1f, 0f, 0f))
    EditorCommands.viewLeft -> applyCameraPreset(camera, Vector3(1f, 0f, 0f))
    EditorCommands.viewTop -> applyCameraPreset(camera, Vector3(0f, 0f, -1f))
    EditorCommands.viewBottom -> applyCameraPreset(camera, Vector3(0f, 0f, 1f))
    EditorCommands.centerOnSelection -> centerOnSelection(editor, camera)
    EditorCommands.toggleProjectionMode -> toggleProjectionMode(camera)
    else -> null
  }
}

fun applyCameraPresets(editor: Editor, commands: List<Command>, camera: CameraRig): CameraRig? =
    commands
        .mapNotNull { applyCameraPresets(editor, it.type, camera) }
        .firstOrNull()

fun updateCamera(editor: Editor, mouseOffset: Vector2, commands: List<Command>, camera: CameraRig, isInBounds: Boolean): CameraRig {
  return applyCameraPresets(editor, commands, camera)
      ?: updateFlyThroughCamera(mouseOffset, commands, camera, isInBounds)
}
