package silentorb.mythic.characters

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
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
    val groundDistance: Float = 0f,
    val maxSpeed: Float,
    val firstPersonLookVelocity: Vector2 = Vector2(),
    val turnSpeed: Vector2,
    val viewMode: ViewMode
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

object Freedom {
  const val none = 0
  const val walking = 1
  const val turning = 2
  const val orbiting = 4
  const val acting = 8

  const val all = -1
}

typealias Freedoms = Int

typealias FreedomTable = Table<Freedoms>

fun hasFreedom(freedoms: Freedoms, freedom: Freedoms): Boolean =
    freedoms and freedom != 0
