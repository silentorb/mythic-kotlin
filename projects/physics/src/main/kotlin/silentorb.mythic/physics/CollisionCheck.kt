package silentorb.mythic.physics

import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3

fun checkCollision(dynamicsWorld: btDiscreteDynamicsWorld, collisionObject: CollisionObject, transform: Matrix): Boolean {
  val shape = createCollisionShape(collisionObject.shape, Vector3.unit)
//  val ghost = btPairCachingGhostObject()
//  ghost.collisionShape = shape
//  ghost.worldTransform = toGdxMatrix4(transform)
////  val j = btBody.collisionFlags
//  ghost.collisionFlags = ghost.collisionFlags or btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
//  val ghost = createGhostBody(transform, shape)
//  dynamicsWorld.addCollisionObject(ghost, collisionObject.groups, collisionObject.mask)
//  val hit = ghost.numOverlappingObjects > 0
//  val resultCallback = ContactResultCallback()
//  dynamicsWorld.contactTest(ghost, resultCallback)
  return  false
//  resultCallback.
//  dynamicsWorld.removeCollisionObject(ghost)
//  ghost.release()
//  return hit
}
