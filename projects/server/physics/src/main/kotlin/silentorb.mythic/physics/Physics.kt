package silentorb.mythic.physics

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.toMutableMatrix
import com.badlogic.gdx.math.Vector3 as GdxVector3

// TODO: Migrate to LWJGL Bullet Bindings if it ever seems a little more used and documented.
// libGDX is inefficient.

data class BulletState(
    var dynamicsWorld: btDiscreteDynamicsWorld,
    var dynamicBodies: Map<Id, btRigidBody>,
    var staticBodies: Map<Id, btCollisionObject>,
//    var collisionObjectMap: Map<Int, Id>,
    var isMapSynced: Boolean = false
)

fun toGdxVector3(vector: Vector3) = GdxVector3(vector.x, vector.y, vector.z)
fun toVector3(vector: GdxVector3) = Vector3(vector.x, vector.y, vector.z)

// TODO: This could be optimized but half of the need for optimization is using libGDX in the first place.
// It would probably be best to wait until libGDX Bullet is replaced.
fun toGdxMatrix4(matrix: Matrix): Matrix4 {
  val result = Matrix4()
  result.set(floatArrayOf(
      matrix.m00,
      matrix.m01,
      matrix.m02,
      matrix.m03,
      matrix.m10,
      matrix.m11,
      matrix.m12,
      matrix.m13,
      matrix.m20,
      matrix.m21,
      matrix.m22,
      matrix.m23,
      matrix.m30,
      matrix.m31,
      matrix.m32,
      matrix.m33
  ))
  return result
}

private var isBulletInitialized = false

fun staticGravity() = GdxVector3(0f, 0f, -10f)

fun newBulletState(): BulletState {
  if (!isBulletInitialized) {
    Bullet.init()
    isBulletInitialized = true
  }

  val collisionConfig = btDefaultCollisionConfiguration()

  ///use the default collision dispatcher. For parallel processing you can use a diffent dispatcher (see Extras/BulletMultiThreaded)
  val dispatcher = btCollisionDispatcher(collisionConfig)

  ///btDbvtBroadphase is a good general purpose broadphase. You can also try out btAxis3Sweep.
  val broadphase = btDbvtBroadphase()

  ///the default constraint solver. For parallel processing you can use a different solver (see Extras/BulletMultiThreaded)
  val solver = btSequentialImpulseConstraintSolver()

  val dynamicsWorld = btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig)
  dynamicsWorld.gravity = staticGravity()

  return BulletState(
      dynamicsWorld = dynamicsWorld,
      dynamicBodies = mapOf(),
      staticBodies = mapOf()
  )
}

fun releaseBulletState(bulletState: BulletState) {
  bulletState.dynamicsWorld.release()
  bulletState.dynamicBodies = mapOf()
}

data class BaseRayCastResult(
    val collisionObject: Id,
    val hitPoint: Vector3
)

fun firstRayHit(dynamicsWorld: btDiscreteDynamicsWorld, start: Vector3, end: Vector3): BaseRayCastResult? {
  val start2 = toGdxVector3(start)
  val end2 = toGdxVector3(end)
  val callback = ClosestRayResultCallback(start2, end2)
  dynamicsWorld.collisionWorld.rayTest(start2, end2, callback)
  val hasHit = callback.hasHit()
  val result = if (hasHit) {
    val collisionObject = callback.collisionObject
    val collisionObjectId = collisionObject.userData as Id
    val hitPoint = com.badlogic.gdx.math.Vector3()
    callback.getHitPointWorld(hitPoint)
    BaseRayCastResult(
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

data class LinearImpulse(
    val body: Id,
    val offset: Vector3
)

data class PhysicsDeck(
    val bodies: Table<Body>,
    val collisionShapes: Table<CollisionObject>,
    val dynamicBodies: Table<DynamicBody>
)

data class PhysicsWorld(
    val bulletState: BulletState,
    val deck: PhysicsDeck
)

fun updateBulletPhysics(linearForces: List<LinearImpulse>): (PhysicsWorld) -> PhysicsWorld = { world ->
  val bulletState = world.bulletState
  syncNewBodies(world, bulletState)
  syncRemovedBodies(world, bulletState)
//  updateCharacterRigs() used to be here and still may need to be in some fashion
  applyImpulses(bulletState, linearForces)
  bulletState.dynamicsWorld.stepSimulation(1f / 60f, 10)
  syncWorldToBullet(bulletState)(world)
}
