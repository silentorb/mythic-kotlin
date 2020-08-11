package silentorb.mythic.fathom.misc

import silentorb.mythic.fathom.mergeDistanceFunctions
import silentorb.mythic.scenery.CompositeShape
import silentorb.mythic.scenery.Shading
import silentorb.mythic.spatial.Vector3

fun newShading(color: Vector3) =
    Shading(
        color = color,
        opacity = 1f,
        specular = 0.8f,
        glow = 0f
    )

fun mergeModelFunctions(models: List<ModelFunction>): ModelFunction {
  val collisions = models.mapNotNull { it.collision }
  val collision = if (collisions.size > 1)
    CompositeShape(
        shapes = collisions
    )
  else
    collisions.firstOrNull()

  val getDistance = mergeDistanceFunctions(models.map {
    val result: DistanceFunction = { origin ->
        it.hashCode() to it.form(origin).second
    }
      result
  })
  val shadingMap = models.associate { it.hashCode() to it.shading }
  val shading: ShadingFunction = { origin ->
    val (id, _) = getDistance(origin)
    val shader = shadingMap[id]!!
    shader(origin)
  }
  return ModelFunction(
      form = getDistance,
      shading = shading,
      collision = collision
  )
}
