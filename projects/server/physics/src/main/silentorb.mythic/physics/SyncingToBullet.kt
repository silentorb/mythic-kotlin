package silentorb.mythic.physics

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btHingeConstraint
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState
import silentorb.mythic.ent.Id
import silentorb.mythic.sculpting.ImmutableFace
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Pi
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.getCenter
import silentorb.mythic.scenery.*

// TODO: Cache the usage of this function
fun createBulletStaticMesh(vertices: List<Vector3>): btBvhTriangleMeshShape {
  val triangleMesh = btTriangleMesh()
  for (i in vertices.indices step 3) {
    triangleMesh.addTriangle(toGdxVector3(vertices[i]), toGdxVector3(vertices[i + 1]), toGdxVector3(vertices[i + 2]))
  }
  return btBvhTriangleMeshShape(triangleMesh, true)
}

fun createCollisionShape(shape: Shape, scale: Vector3): btCollisionShape {
  return when (shape) {
    is ShapeTransform -> {
      val parent = btCompoundShape()
      parent.addChildShape(toGdxMatrix4(shape.transform), createCollisionShape(shape.shape, scale))
      parent
    }
    is Box -> btBoxShape(toGdxVector3(shape.halfExtents * scale))
    is Sphere -> btSphereShape(shape.radius * scale.x)
    is Capsule -> btCapsuleShapeZ(shape.radius * scale.x, (shape.height - shape.radius * 2f) * scale.z)
    is CompositeShape -> {
      val parent = btCompoundShape()
      for (child in shape.shapes) {
        parent.addChildShape(Matrix4(), createCollisionShape(child, scale))
      }
      parent
    }
    is Cylinder -> btCylinderShapeZ(toGdxVector3(Vector3(shape.radius * scale.x, shape.radius * scale.y, shape.height * scale.z * 0.5f)))

    is MeshShape -> createBulletStaticMesh(shape.triangles)
    else -> throw Error("Not supported")
  }
}

fun createBulletDynamicObject(body: Body, dynamicBody: DynamicBody, shape: btCollisionShape, rotationalInertia: Boolean): btRigidBody {
  val transform = getBodyTransform(body)
  val localInertia = com.badlogic.gdx.math.Vector3(0f, 0f, 0f)
  if (rotationalInertia)
    shape.calculateLocalInertia(dynamicBody.mass, localInertia)

  val myMotionState = btDefaultMotionState(toGdxMatrix4(transform))
  val rbInfo = btRigidBody.btRigidBodyConstructionInfo(dynamicBody.mass, myMotionState, shape, localInertia)
  val btBody = btRigidBody(rbInfo)
  btBody.activationState = CollisionConstants.DISABLE_DEACTIVATION
  btBody.friction = dynamicBody.friction
  return btBody
}

fun createStaticFaceBody(face: ImmutableFace): btCollisionObject {
  val triangleMesh = btTriangleMesh()
  val center = getCenter(face.vertices)
  val vertices = face.vertices.map { toGdxVector3(it - center) }
  for (i in (1..vertices.size - 2)) {
    triangleMesh.addTriangle(vertices[0], vertices[i], vertices[i + 1])
  }
  val shape = btBvhTriangleMeshShape(triangleMesh, true)
  val btBody = btCollisionObject()
  btBody.collisionShape = shape
  btBody.worldTransform = toGdxMatrix4(Matrix().translate(center))
  return btBody
}

fun createStaticBody(body: Body, shape: btCollisionShape): btCollisionObject {
  val btBody = btCollisionObject()
  btBody.collisionShape = shape
  btBody.worldTransform = toGdxMatrix4(getBodyTransform(body))
  return btBody
}

fun createGhostBody(body: Body, shape: btCollisionShape): btCollisionObject {
  val btBody = btCollisionObject()
  btBody.collisionShape = shape
  btBody.worldTransform = toGdxMatrix4(getBodyTransform(body))
  val j = btBody.collisionFlags
  btBody.collisionFlags = btBody.collisionFlags or btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
  return btBody
}

fun syncNewBodies(world: PhysicsWorld, bulletState: BulletState) {
  val deck = world.deck

  val newDynamicBodies = deck.dynamicBodies
      .filterKeys { key ->
        !bulletState.dynamicBodies.containsKey(key) && deck.collisionShapes.contains(key)
      }
      .map { (key, dynamicBody) ->
        val body = deck.bodies[key]!!
        val shape = createCollisionShape(deck.collisionShapes[key]!!.shape, body.scale)
        val hingeInfo = dynamicBody.hinge
        val bulletBody = createBulletDynamicObject(body, dynamicBody, shape, hingeInfo != null)
        if (hingeInfo != null) {
          val hinge = btHingeConstraint(bulletBody, toGdxVector3(hingeInfo.pivot * body.scale), toGdxVector3(hingeInfo.axis), true)
//          hinge.enableAngularMotor(true, 1f, 1f)
//          hinge.setLimit(-Pi * 0.2f, Pi  * 0.2f)
          val offset = Pi / 2f
//          hinge.setLimit(-Pi * 0.2f - offset, Pi  * 0.2f - offset)
          hinge.setLimit(-Pi, Pi)
          val j = hinge.hingeAngle

          bulletState.dynamicsWorld.addConstraint(hinge)
          hinge.setDbgDrawSize(5f)
//          bulletBody.setAngularVelocity(GdxVector3(0f, 0f, 1f))

        }
        bulletBody.userData = key
        bulletState.dynamicsWorld.addRigidBody(bulletBody)
        Pair(key, bulletBody)
      }

  val newStaticBodies = deck.collisionShapes
      .filterKeys { key ->
        !deck.dynamicBodies.containsKey(key) && !bulletState.staticBodies.containsKey(key)
      }
      .map { (key, shapeDefinition) ->
        val body = deck.bodies[key]!!
        val shape = createCollisionShape(shapeDefinition.shape, body.scale)
        val collisionObject = if (shapeDefinition.isSolid)
          createStaticBody(body, shape)
        else
          createGhostBody(body, shape)

        collisionObject.userData = key
        bulletState.dynamicsWorld.addCollisionObject(collisionObject)
        Pair(key, collisionObject)
      }

  bulletState.dynamicBodies = bulletState.dynamicBodies
      .plus(newDynamicBodies)

  bulletState.staticBodies = bulletState.staticBodies.plus(newStaticBodies)

  if (!bulletState.isMapSynced) {
    bulletState.isMapSynced = true
//    syncMapGeometryAndPhysics(world, bulletState)
  }
}

fun syncRemovedBodies(world: PhysicsWorld, bulletState: BulletState) {
  val removedDynamic = bulletState.dynamicBodies.filterValues { !world.deck.bodies.containsKey(it.userData as Id) }
  for (body in removedDynamic.values) {
    bulletState.dynamicsWorld.removeRigidBody(body)
  }
  bulletState.dynamicBodies = bulletState.dynamicBodies.minus(removedDynamic.keys)

  val removedStatic = bulletState.staticBodies.filterValues { !world.deck.bodies.containsKey(it.userData as Id) }
  for (body in removedStatic.values) {
    bulletState.dynamicsWorld.removeCollisionObject(body)
  }
  bulletState.staticBodies = bulletState.staticBodies.minus(removedStatic.keys)
}

fun applyImpulses(bulletState: BulletState, linearForces: List<LinearImpulse>) {
  for (force in linearForces) {
    val btBody = bulletState.dynamicBodies[force.body]!!
    btBody.applyCentralImpulse(toGdxVector3(force.offset))
  }
}
