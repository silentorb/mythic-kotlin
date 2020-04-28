package silentorb.mythic.physics

import com.badlogic.gdx.physics.bullet.collision.AllHitsRayResultCallback
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import silentorb.mythic.ent.Id
import silentorb.mythic.spatial.Vector3

data class RayCastResult(
    val collisionObject: Id,
    val hitPoint: Vector3
)

fun firstRayHit(dynamicsWorld: btDiscreteDynamicsWorld, start: Vector3, end: Vector3, mask: Int = -1): RayCastResult? {
  val start2 = toGdxVector3(start)
  val end2 = toGdxVector3(end)
  val callback = ClosestRayResultCallback(start2, end2)
  callback.collisionFilterGroup = -1
  if (mask != -1)
    callback.collisionFilterMask = mask

  dynamicsWorld.collisionWorld.rayTest(start2, end2, callback)
  val hasHit = callback.hasHit()
  val result = if (hasHit) {
    val collisionObject = callback.collisionObject
    val collisionObjectId = collisionObject.userData as Id
    val hitPoint = com.badlogic.gdx.math.Vector3()
    callback.getHitPointWorld(hitPoint)
    RayCastResult(
        collisionObject = collisionObjectId,
        hitPoint = toVector3(hitPoint)
    )
  } else
    null

  callback.dispose()

  return result
}

// ClosestNotMeRayResultCallback is REAAAALLY slow.  Must be horribly unoptimized.
fun firstRayHitNotMe(dynamicsWorld: btDiscreteDynamicsWorld, start: Vector3, end: Vector3, collisionObject: btCollisionObject): ClosestNotMeRayResultCallback {
  val start2 = toGdxVector3(start)
  val end2 = toGdxVector3(end)
  val callback = ClosestNotMeRayResultCallback(collisionObject)
  dynamicsWorld.collisionWorld.rayTest(start2, end2, callback)
  return callback
}

fun allRayHits(dynamicsWorld: btDiscreteDynamicsWorld, start: Vector3, end: Vector3): AllHitsRayResultCallback {
  val callback = AllHitsRayResultCallback(com.badlogic.gdx.math.Vector3.Zero, com.badlogic.gdx.math.Vector3.Z)
  dynamicsWorld.collisionWorld.rayTest(toGdxVector3(start), toGdxVector3(end), callback)
  return callback
}
