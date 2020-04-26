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

fun characterRigOrentation(facingRotation: Vector3) =
    Quaternion()
        .rotateZ(facingRotation.z)
        .rotateY(-facingRotation.y)

data class CharacterRig(
    val facingRotation: Vector3 = Vector3(),
    val facingOrientation: Quaternion,
//    val isActive: Boolean,
    val groundDistance: Float = 0f,
    val maxSpeed: Float,
    val firstPersonLookVelocity: Vector2 = Vector2(),
    val turnSpeed: Vector2
) {
  val facingVector: Vector3
    get() = getFacingVector(facingOrientation)
}

data class ThirdPersonRig(
    val pivotLocation: Vector3,
    val rotation: Vector2,
    val orientationVelocity: Vector2 = Vector2.zero,
    val distance: Float,
    val facingDestination: Float?
)

data class CharacterRigMovement(
    val actor: Id,
    val offset: Vector3
) : GameEvent
