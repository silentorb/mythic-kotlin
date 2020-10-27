package silentorb.mythic.editing

import silentorb.mythic.cameraman.characterMovementVector
import silentorb.mythic.cameraman.defaultLookMomentumAxis
import silentorb.mythic.cameraman.updateFirstPersonFacingRotation
import silentorb.mythic.cameraman.updateLookVelocityFirstPerson
import silentorb.mythic.haft.GAMEPAD_AXIS_TRIGGER_LEFT
import silentorb.mythic.haft.GAMEPAD_AXIS_TRIGGER_RIGHT
import silentorb.mythic.happenings.CharacterCommand
import silentorb.mythic.platforming.InputEvent
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3

const val simulationFps = 60
const val simulationDelta = 1f / simulationFps.toFloat()

data class CameraRig(
    val location: Vector3 = Vector3.zero,
    val rotation: Vector2 = Vector2.zero,
    val lookVelocity: Vector2 = Vector2.zero
) {
  val orientation:Quaternion get() = Quaternion()
      .rotateZ(rotation.x)
      .rotateY(-rotation.y)
}

//fun getFlyThroughCameraState(initialize: () -> FlyThroughCameraState = ::defaultFlyThroughState): FlyThroughCameraState {
//  if (flyThroughCameraState == null) {
//    flyThroughCameraState = initialize()
//  }
//
//  return flyThroughCameraState!!
//}

//fun newFlyThroughCamera(location: Vector3, orientation: Quaternion): Camera =
//    Camera(
//        ProjectionType.perspective,
//        location,
//        orientation,
//        defaultAngle
//    )

//fun newFlyThroughCamera(initialize: () -> FlyThroughCameraState): Camera {
//  val state = getFlyThroughCameraState(initialize)
//  return newFlyThroughCamera(state.location, state.facingOrientation)
//}

fun flyThroughOrientation(camera: CameraRig, commands: List<CharacterCommand>): Vector2 {
  val firstPersonLookVelocity = updateLookVelocityFirstPerson(commands, defaultLookMomentumAxis(), camera.lookVelocity)
  return updateFirstPersonFacingRotation(camera.rotation, null, firstPersonLookVelocity * 20f, simulationDelta)
}

fun updateFlyThroughCamera(commands: List<CharacterCommand>, events: List<InputEvent>, camera: CameraRig): CameraRig {
//  val commands = mapGameCommands(clientState.players, clientState.commands)
  val lookVelocity = updateLookVelocityFirstPerson(commands, defaultLookMomentumAxis(), camera.lookVelocity)
  val rotation = updateFirstPersonFacingRotation(camera.rotation, null, lookVelocity * 20f, simulationDelta)
  val movementVector = characterMovementVector(commands, camera.orientation)
  val movementOffset = if (movementVector != null)
    movementVector * 12f * simulationDelta
  else
    Vector3.zero

//  val deviceEvents = clientState.input.deviceStates.flatMap { it.events }
  val zSpeed = 15f
  val zOffset = if (events.any { it.index == GAMEPAD_AXIS_TRIGGER_LEFT }) {
    Vector3(0f, 0f, -zSpeed * simulationDelta)
  } else if (events.any { it.index == GAMEPAD_AXIS_TRIGGER_RIGHT }) {
    Vector3(0f, 0f, zSpeed* simulationDelta)
  } else
    Vector3.zero

  return camera.copy(
      location = camera.location + movementOffset + zOffset,
      rotation = rotation,
      lookVelocity = lookVelocity,
  )
}
