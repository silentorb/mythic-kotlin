package silentorb.mythic.physics

import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import silentorb.mythic.ent.Id
import silentorb.mythic.spatial.Vector3

data class RaycastResult(
    val distance: Float,
    val collisionObject: Id
)

fun castCollisionRay(dynamicsWorld: btDiscreteDynamicsWorld, start: Vector3, end: Vector3): RaycastResult? {
  val callback = firstRayHit(dynamicsWorld, start, end)
  return if (callback.hasHit()) {
    val collisionObject = callback.collisionObject
    val collisionObjectId = collisionObject.userData as Id
    val hitPoint = com.badlogic.gdx.math.Vector3()
    callback.getHitPointWorld(hitPoint)
    val distance = start.z - hitPoint.z
//    println(" $collisionObjectId $distance")
    return RaycastResult(
        distance = distance,
        collisionObject = collisionObjectId
    )
  } else
    null
}
