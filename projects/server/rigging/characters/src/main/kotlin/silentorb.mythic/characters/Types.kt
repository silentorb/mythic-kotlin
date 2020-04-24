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

data class HoverCamera(
    val pitch: Float,
    val yaw: Float,
    val distance: Float,
    val facingDestination: Float,
    val facingDestinationCandidate: Float // Delays facing changes for one frame to filter out brief input aberrations
)

data class CharacterRig(
    val facingRotation: Vector3 = Vector3(),
    val isActive: Boolean,
    val groundDistance: Float = 0f,
    val lookVelocity: Vector2 = Vector2(),
    val maxSpeed: Float,
    val turnSpeed: Vector2,
    val viewMode: ViewMode,
    val hoverCamera: HoverCamera?
) {
  val facingQuaternion: Quaternion
    get() = Quaternion()
        .rotateZ(facingRotation.z)
        .rotateY(-facingRotation.y)

  val facingVector: Vector3
    get() = getFacingVector(facingQuaternion)
}

data class CharacterRigMovement(
    val actor: Id,
    val offset: Vector3
) : GameEvent
