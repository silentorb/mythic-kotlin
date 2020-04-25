package silentorb.mythic.characters

import silentorb.mythic.ent.Id
import silentorb.mythic.happenings.GameEvent
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3

enum class ViewMode {
  firstPerson,
  thirdPerson
}

data class CharacterRig(
    val facingRotation: Vector3 = Vector3(),
//    val isActive: Boolean,
    val groundDistance: Float = 0f,
    val maxSpeed: Float,
    val firstPersonLookVelocity: Vector2 = Vector2(),
    val turnSpeed: Vector2
) {
  val facingQuaternion: Quaternion
    get() = Quaternion()
        .rotateZ(facingRotation.z)
        .rotateY(-facingRotation.y)

  val facingVector: Vector3
    get() = getFacingVector(facingQuaternion)
}

data class ThirdPersonRig(
    val pitch: Float,
    val yaw: Float,
    val lookVelocity: Vector2 = Vector2(),
    val distance: Float,
    val facingDestination: Float,
    val facingDestinationCandidate: Float // Delays facing changes for one frame to filter out brief input aberrations
)

data class CharacterRigMovement(
    val actor: Id,
    val offset: Vector3
) : GameEvent
