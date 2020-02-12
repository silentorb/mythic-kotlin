package silentorb.mythic.physics

import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import silentorb.mythic.ent.Id
import silentorb.mythic.spatial.Vector3

data class DistanceRaycastResult(
    val distance: Float,
    val collisionObject: Id
)

fun castCollisionRay(dynamicsWorld: btDiscreteDynamicsWorld, start: Vector3, end: Vector3): DistanceRaycastResult? {
  val callback = firstRayHit(dynamicsWorld, start, end)
  return if (callback != null) {
    val distance = start.z - callback.hitPoint.z
//    println(" $collisionObjectId $distance")
    return DistanceRaycastResult(
        distance = distance,
        collisionObject = callback.collisionObject
    )
  } else
    null
}
