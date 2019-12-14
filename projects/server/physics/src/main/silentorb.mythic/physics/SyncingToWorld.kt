package silentorb.mythic.physics

import com.badlogic.gdx.math.Matrix4
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3

fun syncWorldToBullet(bulletState: BulletState): (PhysicsWorld) -> PhysicsWorld = { world ->
  val quat = com.badlogic.gdx.math.Quaternion()
  val deck = world.deck
  world.copy(
      bulletState = bulletState,
      deck = deck.copy(
          bodies = deck.bodies.mapValues { (key, body) ->
            val btBody = bulletState.dynamicBodies[key]
            if (btBody == null)
              body
            else {
              val worldTransform = btBody.worldTransform
              val transform = worldTransform.getValues()
              worldTransform.getRotation(quat)
              body.copy(
                  position = Vector3(transform[Matrix4.M03], transform[Matrix4.M13], transform[Matrix4.M23]),
                  orientation = Quaternion(quat.x, quat.y, quat.z, quat.w),
                  velocity = toVector3(btBody.linearVelocity)
              )
            }
          }
      )
  )
}
