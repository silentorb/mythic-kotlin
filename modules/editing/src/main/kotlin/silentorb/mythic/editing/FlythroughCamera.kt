package silentorb.mythic.editing

import marloth.clienting.ClientState
import marloth.clienting.rendering.defaultAngle
import marloth.integration.misc.mapGameCommands
import silentorb.mythic.characters.rigs.*
import silentorb.mythic.haft.GAMEPAD_AXIS_TRIGGER_LEFT
import silentorb.mythic.haft.GAMEPAD_AXIS_TRIGGER_RIGHT
import silentorb.mythic.happenings.CharacterCommand
import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.ProjectionType
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import simulation.updating.simulationDelta

data class FlyThroughCamera(
    val location: Vector3 = Vector3.zero,
    val facingRotation: Vector2 = Vector2()
) {
  val facingOrientation:Quaternion get() = Quaternion()
      .rotateZ(facingRotation.x)
      .rotateY(-facingRotation.y)
}

fun defaultFlyThroughState() =
    FlyThroughCamera(
        location = Vector3.zero
    )

//fun getFlyThroughCameraState(initialize: () -> FlyThroughCameraState = ::defaultFlyThroughState): FlyThroughCameraState {
//  if (flyThroughCameraState == null) {
//    flyThroughCameraState = initialize()
//  }
//
//  return flyThroughCameraState!!
//}

fun newFlyThroughCamera(location: Vector3, orientation: Quaternion): Camera =
    Camera(
        ProjectionType.perspective,
        location,
        orientation,
        defaultAngle
    )

//fun newFlyThroughCamera(initialize: () -> FlyThroughCameraState): Camera {
//  val state = getFlyThroughCameraState(initialize)
//  return newFlyThroughCamera(state.location, state.facingOrientation)
//}

fun flyThroughOrientation(camera: FlyThroughCamera, commands: List<CharacterCommand>): Vector2 {
  val firstPersonLookVelocity = updateLookVelocityFirstPerson(commands, defaultGamepadMomentumAxis(), rig.firstPersonLookVelocity)
  return updateFirstPersonFacingRotation(camera.facingRotation, null, firstPersonLookVelocity * 20f, simulationDelta)
}

fun updateFlyThroughCamera(clientState: ClientState, camera: FlyThroughCamera): FlyThroughCamera {
//  val state = getFlyThroughCameraState()
  val commands = mapGameCommands(clientState.players, clientState.commands)
  val facingRotation = flyThroughOrientation(camera, commands)
  val movement = characterMovement(commands, camera, null, 0L)
  if (movement != null) {
    camera.location = camera.location + movement.offset * 12f * simulationDelta
  }
  val deviceEvents = clientState.input.deviceStates.flatMap { it.events }
  val zSpeed = 15f
  val zOffset = if (deviceEvents.any { it.index == GAMEPAD_AXIS_TRIGGER_LEFT }) {
    Vector3(0f, 0f, -zSpeed * simulationDelta)
  } else if (deviceEvents.any { it.index == GAMEPAD_AXIS_TRIGGER_RIGHT }) {
    Vector3(0f, 0f, zSpeed* simulationDelta)
  } else
    Vector3.zero

  return camera.copy(
      location = location + zOffset
  )
//  state.location = state.

  state.rig = state.rig.copy(
      facingRotation = facingRotation,
      facingOrientation = characterRigOrentation(facingRotation)
  )
}
