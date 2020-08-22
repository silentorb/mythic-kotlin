package silentorb.mythic.fathom.misc

import silentorb.mythic.fathom.mergeDistanceFunctions
import silentorb.mythic.fathom.mergeDistanceFunctionsTrackingIds
import silentorb.mythic.scenery.CompositeShape
import silentorb.mythic.scenery.Shading
import silentorb.mythic.scenery.mergeShapes
import silentorb.mythic.spatial.Vector3

fun newShading(color: Vector3) =
    Shading(
        color = color,
        opacity = 1f,
        specular = 0.8f,
        glow = 0f
    )

fun mergeShadingFunctions(models: Collection<ModelFunction>, form: DistanceFunction): ShadingFunction {
  val shadingMap = models.associate { it.hashCode() to it.shading }
  return { origin ->
    val (id, _) = form(origin)
    val shader = shadingMap[id]!!
    shader(origin)
  }
}

fun mergeModelFunctions(models: List<ModelFunction>): ModelFunction {
  val collisions = models.mapNotNull { it.collision }
  val collision = mergeShapes(collisions)

  val getDistance = mergeDistanceFunctionsTrackingIds(models)
  val shading = mergeShadingFunctions(models, getDistance)
  return ModelFunction(
      form = getDistance,
      shading = shading,
      collision = collision
  )
}
