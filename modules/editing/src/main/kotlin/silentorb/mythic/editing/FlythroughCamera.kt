package silentorb.mythic.editing

import com.fasterxml.jackson.annotation.JsonIgnore
import silentorb.mythic.cameraman.*
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.happenings.Command
import silentorb.mythic.scenery.ProjectionType
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.getYawAndPitch

const val simulationFps = 60
const val simulationDelta = 1f / simulationFps.toFloat()

data class CameraRig(
    val location: Vector3 = Vector3.zero,
    val rotation: Vector2 = Vector2.zero,
    val lookVelocity: Vector2 = Vector2.zero,
    val pivotDistance: Float = 10f,
    val projection: ProjectionType = ProjectionType.perspective,
) {
  @get:JsonIgnore
  val orientation: Quaternion
    get() = Quaternion()
        .rotateZ(rotation.x)
        .rotateY(-rotation.y)
}

fun getCameraPivot(camera: CameraRig): Vector3 {
  val pivotOffset = Vector3(camera.pivotDistance, 0f, 0f)
  return camera.location + camera.orientation.transform(pivotOffset)
}

fun getCameraLookat(camera: CameraRig): Vector3 =
    camera.orientation.transform(Vector3(1f, 0f, 0f))

fun updateCameraOrbiting(mouseOffset: Vector2, camera: CameraRig): CameraRig {
  return if (mouseOffset == Vector2.zero)
    camera
  else {
    val pivotOffset = Vector3(camera.pivotDistance, 0f, 0f)
    val pivot = getCameraPivot(camera)
    val reverseRotation = getYawAndPitch(camera.location - pivot)

    val orientationOffset = Quaternion()
        .rotateZ(reverseRotation.x - mouseOffset.x * 0.03f)
        .rotateY(-reverseRotation.y - mouseOffset.y * 0.02f)

    val nextLocation = orientationOffset.transform(pivotOffset) + pivot

    val nextRotation = getYawAndPitch(pivot - nextLocation)
    return camera.copy(
        location = nextLocation,
        rotation = nextRotation,
        lookVelocity = Vector2.zero
    )
  }
}

fun updateCameraPanning(mouseOffset: Vector2, camera: CameraRig): CameraRig {
  return if (mouseOffset == Vector2.zero)
    camera
  else {
    val strength = 3f * simulationDelta
    val horizontalOffset = camera.orientation.transform(Vector3(0f, strength, 0f)) * mouseOffset.x
    val verticalOffset = camera.orientation.transform(Vector3(0f, 0f, strength)) * mouseOffset.y
    val nextLocation = camera.location + horizontalOffset + verticalOffset

    return camera.copy(
        location = nextLocation,
        lookVelocity = Vector2.zero
    )
  }
}

fun zoomCamera(camera: CameraRig, newPivotDistance: Float): CameraRig {
  val distanceOffset = camera.pivotDistance - newPivotDistance
  val pivot = getCameraPivot(camera)
  val lookAt = (pivot - camera.location).normalize()
  val offset = lookAt * distanceOffset
  return camera.copy(
      location = camera.location + offset,
      pivotDistance = camera.pivotDistance - distanceOffset,
  )
}

fun getOrthoZoom(camera: CameraRig): Float =
    camera.pivotDistance * 0.45f

fun flyThroughKeyboardCommands(deviceStates:List<InputDeviceState>, mouseOffset: Vector2): List<Command> {
  val horizontalScale = 10f
  val verticalScale = 10f
  val lookCommands = listOfNotNull(
      if (mouseOffset.x < 0f) Command(CameramanCommands.lookLeft, -mouseOffset.x * horizontalScale) else null,
      if (mouseOffset.x > 0f) Command(CameramanCommands.lookRight, mouseOffset.x * horizontalScale) else null,
      if (mouseOffset.y < 0f) Command(CameramanCommands.lookUp, -mouseOffset.y * verticalScale) else null,
      if (mouseOffset.y > 0f) Command(CameramanCommands.lookDown, mouseOffset.y * verticalScale) else null,
  )
  if (lookCommands.any()){
    val k = 0
    println(lookCommands)
  }
  return lookCommands + mapCommands(keyboardFlyThroughBindings(), deviceStates)
}

fun updateFlyThroughCamera(mouseOffset: Vector2, commands: List<Command>, camera: CameraRig, isInBounds: Boolean): CameraRig {
  return when {
    commands.any { it.type == EditorCommands.zoomIn } && isInBounds -> zoomCamera(camera, camera.pivotDistance * 0.7f)
    commands.any { it.type == EditorCommands.zoomOut } && isInBounds -> zoomCamera(camera, camera.pivotDistance * 1.3f + 0.1f)
    isMouseDown(1) && isInBounds && isShiftDown() -> updateCameraPanning(mouseOffset, camera)
    isMouseDown(1) && isInBounds -> updateCameraOrbiting(mouseOffset, camera)
    else -> {
      val lookVelocity = updateLookVelocityFirstPerson(commands, defaultLookMomentumAxis(), camera.lookVelocity)
      val rotation = updateFirstPersonFacingRotation(camera.rotation, null, lookVelocity, simulationDelta)

      val movementVector = characterMovementVector(commands, camera.orientation)
      val movementOffset = if (movementVector != null)
        movementVector * 12f * simulationDelta
      else
        Vector3.zero

      val zSpeed = 15f
      val zOffset = if (commands.any { it.type == CameramanCommands.moveUp }) {
        Vector3(0f, 0f, -zSpeed * simulationDelta)
      } else if (commands.any { it.type == CameramanCommands.moveDown }) {
        Vector3(0f, 0f, zSpeed * simulationDelta)
      } else
        Vector3.zero

      return camera.copy(
          location = camera.location + movementOffset + zOffset,
          rotation = rotation,
          lookVelocity = lookVelocity,
      )
    }
  }
}
