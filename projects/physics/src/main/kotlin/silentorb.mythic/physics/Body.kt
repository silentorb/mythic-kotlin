package silentorb.mythic.physics

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3

const val voidNodeId = -1L

interface SimpleBody {
  val position: Vector3
}

data class HingeConstraint(
    val pivot: Vector3,
    val axis: Vector3
)

data class Body(
    val position: Vector3,
    val velocity: Vector3 = Vector3.zero,
    val orientation: Quaternion = Quaternion(),
    val scale: Vector3 = Vector3.unit,
    val parent: Id? = null,
    val isKinetic: Boolean = false,
    val localTransform: Matrix = Matrix.identity,
    val absoluteTransform: Matrix? = null,
    val parentTransform: Matrix = Matrix.identity,
)

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
    Matrix.identity.translate(body.position).rotate(body.orientation)

fun updateInheritedBodyTransforms(bodies: Table<Body>): Table<Body> =
    bodies.mapValues { (_, body) ->
      if (body.parent == null)
        body
      else {
        val parent = bodies[body.parent]
        if (parent == null)
          body
        else {
          val parentTransform = getBodyTransform(parent)
          val absoluteTransform = parentTransform * body.localTransform
          body.copy(
              localTransform = body.localTransform,
              parentTransform = parentTransform,
              absoluteTransform = absoluteTransform,
              position = absoluteTransform.translation(),
              orientation = Quaternion().fromUnnormalized(absoluteTransform),
              scale = absoluteTransform.getScale(),
          )
        }
      }
    }
