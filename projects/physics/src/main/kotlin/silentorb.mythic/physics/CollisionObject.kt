package silentorb.mythic.physics

import silentorb.mythic.ent.Id
import silentorb.mythic.scenery.Shape
import silentorb.mythic.spatial.Vector3

data class CollisionObject(
    val shape: Shape,
    val groups: Int,
    val mask: Int,
    val isSolid: Boolean = true
)

data class Collision(
    val first: Any,
    val second: Any,
    val hitPoint: Vector3? = null
)typealias Collisions = List<Collision>
