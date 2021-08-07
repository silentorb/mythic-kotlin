package silentorb.mythic.physics

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btHingeConstraint
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState
import silentorb.mythic.ent.*
import silentorb.mythic.ent.scenery.getShape
import silentorb.mythic.ent.scenery.getAbsoluteNodeTransform
import silentorb.mythic.sculpting.ImmutableFace
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Pi
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.getCenter
import silentorb.mythic.scenery.*
import com.badlogic.gdx.math.Vector3 as GdxVector3

// TODO: Cache the usage of this function
fun createBulletStaticMesh(vertices: List<Vector3>, scale: Vector3 = Vector3.unit): btBvhTriangleMeshShape {
  val triangleMesh = btTriangleMesh()
  for (i in vertices.indices step 3) {
    triangleMesh.addTriangle(
        toGdxVector3(vertices[i] * scale),
        toGdxVector3(vertices[i + 1] * scale),
        toGdxVector3(vertices[i + 2] * scale)
    )
  }
  return btBvhTriangleMeshShape(triangleMesh, true)
}

fun createCollisionShape(shape: Shape, scale: Vector3): btCollisionShape {
  return when (shape) {
    is ShapeTransform -> {
      val parent = btCompoundShape()
      parent.addChildShape(toGdxMatrix4(shape.transform), createCollisionShape(shape.shape, Vector3.unit))
      parent.localScaling = toGdxVector3(scale)
      parent
    }
    is Box -> btBoxShape(toGdxVector3(shape.halfExtents * scale))
    is Sphere -> btSphereShape(shape.radius * scale.x)
    is Capsule -> btCapsuleShapeZ(shape.radius * scale.x, (shape.height - shape.radius * 2f) * scale.z)
    is Cylinder -> btCylinderShapeZ(GdxVector3(shape.radius * scale.x / 2f, shape.radius * scale.y / 2f, shape.height * scale.z / 2f))
    is CompositeShape -> {
      val parent = btCompoundShape()
      for (child in shape.shapes) {
        parent.addChildShape(Matrix4(), createCollisionShape(child, scale))
      }
      parent
    }
    is Cylinder -> btCylinderShapeZ(toGdxVector3(Vector3(shape.radius * scale.x, shape.radius * scale.y, shape.height * scale.z * 0.5f)))

    is MeshShape -> createBulletStaticMesh(shape.triangles, scale / 2f)
    else -> throw Error("Not supported")
  }
}

fun createBulletDynamicObject(transform: Matrix, dynamicBody: DynamicBody, shape: btCollisionShape, rotationalInertia: Boolean,
                              isSolid: Boolean = true): btRigidBody {
  val localInertia = com.badlogic.gdx.math.Vector3(0f, 0f, 0f)
  if (rotationalInertia)
    shape.calculateLocalInertia(dynamicBody.mass, localInertia)

  val myMotionState = btDefaultMotionState(toGdxMatrix4(transform))
  val rbInfo = btRigidBody.btRigidBodyConstructionInfo(dynamicBody.mass, myMotionState, shape, localInertia)
  val btBody = btRigidBody(rbInfo)
  btBody.activationState = CollisionConstants.DISABLE_DEACTIVATION
  btBody.friction = dynamicBody.friction
  if (!isSolid) {
    btBody.collisionFlags = btBody.collisionFlags or btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
  }
  rbInfo.release()
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
  btBody.worldTransform = toGdxMatrix4(Matrix.identity.translate(center))
  return btBody
}

fun createStaticBody(transform: Matrix, shape: btCollisionShape): btCollisionObject {
  val btBody = btCollisionObject()
  btBody.collisionShape = shape
  btBody.worldTransform = toGdxMatrix4(transform)
  return btBody
}

fun createGhostBody(transform: Matrix, shape: btCollisionShape): btCollisionObject {
  val btBody = btCollisionObject()
  btBody.collisionShape = shape
  btBody.worldTransform = toGdxMatrix4(transform)
//  val j = btBody.collisionFlags
  btBody.collisionFlags = btBody.collisionFlags or btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
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
//  val j = btBody.collisionFlags
  btBody.collisionFlags = btBody.collisionFlags or btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
  return btBody
}

fun initializeHinge(bulletState: BulletState, bulletBody: btRigidBody, hingeInfo: HingeConstraint, body: Body) {
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

fun getNodeCollisionObject(meshShapes: Map<String, Shape>, graph: Graph, node: Key): CollisionObject? {
  val shapeDefinition = getShape(meshShapes, graph, node)
  val groups = getNodeValue<Int>(graph, node, SceneProperties.collisionGroups)
  return if (shapeDefinition != null && groups != null) {
    val mask = getNodeValue<Int>(graph, node, SceneProperties.collisionMask) ?: 2 or 4
    CollisionObject(
        shape = shapeDefinition,
        groups = groups,
        mask = mask,
    )
  } else
    null
}

fun syncStaticGeometry(graph: Graph, meshShapes: Map<String, Shape>, bulletState: BulletState) {
  val collisionShapes = graph.filter { it.property == SceneProperties.collisionShape }
  val newStaticBodies = collisionShapes
      .mapNotNull { (node) ->
        val collisionObject = getNodeCollisionObject(meshShapes, graph, node)
        if (collisionObject == null)
          null
        else {
          val fullTransform = getAbsoluteNodeTransform(graph, node)
          val scale = fullTransform.getScale()
          val transform = fullTransform.scale(Vector3.unit / scale)
          val shape = createCollisionShape(collisionObject.shape, scale)
          val isSolid = true
          val bulletCollisionObject = if (isSolid)
            createStaticBody(transform, shape)
          else
            createGhostBody(transform, shape)

          bulletCollisionObject.userData = node
          bulletState.dynamicsWorld.addCollisionObject(bulletCollisionObject, 17, collisionObject.mask)
          Pair(node, bulletCollisionObject)
        }
      }

  bulletState.staticBodies = bulletState.staticBodies + newStaticBodies
}

fun syncNewBodies(world: PhysicsWorld, bulletState: BulletState) {
  val deck = world.deck

  val newDynamicBodies = deck.dynamicBodies
      .filterKeys { key ->
        !bulletState.dynamicBodies.containsKey(key) && deck.collisionObjects.contains(key) && !bulletState.kineticBodies.containsKey(key)
      }
      .map { (key, dynamicBody) ->
        val body = deck.bodies[key]!!
        val collisionObject = deck.collisionObjects[key]!!
        val shape = createCollisionShape(collisionObject.shape, body.scale)
        val hingeInfo = dynamicBody.hinge
        val bulletBody = createBulletDynamicObject(getBodyTransform(body), dynamicBody, shape, hingeInfo != null, collisionObject.isSolid)

        if (hingeInfo != null) {
          initializeHinge(bulletState, bulletBody, hingeInfo, body)
        }
        bulletBody.userData = key
        bulletBody.linearVelocity = toGdxVector3(body.velocity)
        bulletState.dynamicsWorld.addRigidBody(bulletBody, collisionObject.groups, collisionObject.mask)

        // Per-object bullet gravity needs to be set after the body is added to the world or the gravity is ignored.
        // (Either a design flaw or bug...)
        if (!dynamicBody.gravity)
          bulletBody.gravity = toGdxVector3(Vector3.zero)

        Pair(key, bulletBody)
      }

  val (newKineticBodies, newStaticBodies) = deck.collisionObjects
      .filterKeys { key ->
        !deck.dynamicBodies.containsKey(key) && !bulletState.staticBodies.containsKey(key) && !bulletState.kineticBodies.containsKey(key)
      }
      .map { (key, shapeDefinition) ->
        val body = deck.bodies[key]!!
        val shape = createCollisionShape(shapeDefinition.shape, body.scale)
        val collisionObject = if (shapeDefinition.isSolid)
          createStaticBody(body, shape)
        else
          createGhostBody(body, shape)

        collisionObject.userData = key
        bulletState.dynamicsWorld.addCollisionObject(collisionObject, shapeDefinition.groups, shapeDefinition.mask)
        Pair(key, collisionObject)
      }
      .partition { (id, _) -> deck.bodies[id]!!.isKinetic }

  bulletState.dynamicBodies = bulletState.dynamicBodies + newDynamicBodies
  bulletState.staticBodies = bulletState.staticBodies + newStaticBodies
  bulletState.kineticBodies = bulletState.kineticBodies + newKineticBodies
}

fun syncRemovedBodies(deck: PhysicsDeck, bulletState: BulletState) {
  val removedDynamic = bulletState.dynamicBodies
      .filterValues { it.userData is Id && !deck.bodies.containsKey(it.userData) }

  for (body in removedDynamic.values) {
    bulletState.dynamicsWorld.removeRigidBody(body)
    body.release()
  }
  bulletState.dynamicBodies = bulletState.dynamicBodies.minus(removedDynamic.keys)

  val removedStatic = bulletState.staticBodies
      .filterValues { it.userData is Id && !deck.bodies.containsKey(it.userData) }

  for (body in removedStatic.values) {
    bulletState.dynamicsWorld.removeCollisionObject(body)
    body.release()
  }
  bulletState.staticBodies = bulletState.staticBodies.minus(removedStatic.keys)

  val removedKinetic = bulletState.kineticBodies
      .filterValues { it.userData is Id && !deck.bodies.containsKey(it.userData) }

  for (body in removedKinetic.values) {
    bulletState.dynamicsWorld.removeCollisionObject(body)
    body.release()
  }
  bulletState.kineticBodies = bulletState.kineticBodies.minus(removedKinetic.keys)
}

fun applyImpulses(bulletState: BulletState, linearForces: List<LinearImpulse>) {
  for (force in linearForces) {
    val btBody = bulletState.dynamicBodies[force.body]!!
    btBody.applyCentralImpulse(toGdxVector3(force.offset))
  }
}

fun applyBodyChanges(bulletState: BulletState, previous: Table<Body>, next: Table<Body>) {
  val changes = next.filter { (id, body) ->
    val other = previous[id]
    other != null && other != body
  }
  for ((id, body) in changes) {
    val btBody = bulletState.dynamicBodies[id]
    if (btBody != null) {
      btBody.worldTransform = toGdxMatrix4(getBodyTransform(body))
      btBody.linearVelocity = toGdxVector3(body.velocity)
    } else {
      val btBody2 = bulletState.kineticBodies[id]
      if (btBody2 != null) {
        btBody2.worldTransform = toGdxMatrix4(getBodyTransform(body))
      }
    }
  }
}
