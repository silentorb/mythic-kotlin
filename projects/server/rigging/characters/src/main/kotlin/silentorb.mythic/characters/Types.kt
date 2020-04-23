package silentorb.mythic.characters

import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3

enum class ViewMode {
  firstPerson,
  thirdPerson
}

data class HoverCamera(
    var pitch: Float = -0.4f,
    var yaw: Float = 0f,
    var distance: Float = 7f
)

data class CharacterRig(
    val facingRotation: Vector3 = Vector3(),
    val isActive: Boolean,
    val groundDistance: Float = 0f,
    val lookVelocity: Vector2 = Vector2(),
    val maxSpeed: Float,
    val turnSpeed: Vector2,
    val viewMode: ViewMode,
    val hoverCamera: HoverCamera = HoverCamera()
) {
  val facingQuaternion: Quaternion
    get() = Quaternion()
        .rotateZ(facingRotation.z)
        .rotateY(-facingRotation.y)

  val facingVector: Vector3
    get() = getFacingVector(facingQuaternion)
}
