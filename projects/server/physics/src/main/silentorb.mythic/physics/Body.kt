package silentorb.mythic.physics

import silentorb.mythic.ent.Id
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3

const val voidNodeId = -1L

interface SimpleBody {
  val position: Vector3
  val nearestNode: Id
}

data class HingeConstraint(
    val pivot: Vector3,
    val axis: Vector3
)

data class Body(
    override val position: Vector3,
    val velocity: Vector3 = Vector3.zero,
    val orientation: Quaternion = Quaternion(),
    val scale: Vector3 = Vector3.unit,
    override val nearestNode: Id = voidNodeId
) : SimpleBody

data class DynamicBody(
    val gravity: Boolean,
    val mass: Float,
    val resistance: Float,
    val friction: Float = 0.5f,
    val hinge: HingeConstraint? = null
)

fun isMoving(body: Body) =
    body.velocity != Vector3.zero

fun getBodyTransform(body: Body) =
    Matrix().translate(body.position).rotate(body.orientation)
