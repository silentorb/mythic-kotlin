package silentorb.mythic.physics

import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import silentorb.mythic.ent.Id
import silentorb.mythic.spatial.Vector3

data class DistanceRaycastResult(
    val hitPoint: Vector3,
    val collisionObject: Id
)

fun castCollisionRay(dynamicsWorld: btDiscreteDynamicsWorld, start: Vector3, end: Vector3): DistanceRaycastResult? {
  val callback = firstRayHit(dynamicsWorld, start, end)
  return if (callback != null) {
//    println(" $collisionObjectId $distance")
    return DistanceRaycastResult(
        hitPoint = callback.hitPoint,
        collisionObject = callback.collisionObject
    )
  } else
    null
}
