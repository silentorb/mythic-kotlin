package silentorb.mythic.editing.updating

import silentorb.mythic.editing.general.MouseState
import silentorb.mythic.editing.main.*
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.ent.scenery.getAbsoluteNodeTransform
import silentorb.mythic.happenings.Command
import silentorb.mythic.scenery.ProjectionType
import silentorb.mythic.spatial.*

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
  val selection = getNodeSelection(editor)
  val graph = getTransformedActiveEditorGraph(editor)
  return if (selection.any() && graph != null) {
    val nodeLocation = getAbsoluteNodeTransform(graph, selection.first()).translation()
    val pivot = getCameraPivot(camera)
    val pivotToCameraOffset = camera.location - pivot
    val nextLocation = nodeLocation + pivotToCameraOffset
    camera.copy(
        location = nextLocation
    )
  } else
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

fun updateCameraMouseAction(mouseAction: MouseAction, mouseOffset: Vector2, camera: CameraRig): CameraRig =
    when (mouseAction) {
      MouseAction.pan -> updateCameraPanning(mouseOffset, camera)
      MouseAction.orbit -> updateCameraOrbiting(mouseOffset, camera)
      else -> camera
    }

fun updateCamera(editor: Editor, mouse: MouseState, commands: List<Command>, viewport: String, camera: CameraRig): CameraRig {
  val isInBounds = isInViewportBounds(editor, mouse.position, viewport)

  val lookOffset = if (editor.flyThrough && mouse.offset != Vector2.zero)
    -mouse.offset * 4f / (editor.viewportBoundsMap[defaultViewportId]?.zw()?.toVector2() ?: Vector2.zero)
  else
    Vector2.zero

  return applyCameraPresets(editor, commands, camera)
      ?: if (editor.mouseAction != MouseAction.none && editor.mouseActionViewport == viewport)
        updateCameraMouseAction(editor.mouseAction, mouse.offset, camera)
      else
        updateFlyThroughCamera(mouse.offset, commands, camera, isInBounds, lookOffset)
}
