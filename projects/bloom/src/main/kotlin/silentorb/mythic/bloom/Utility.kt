package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i
import kotlin.math.max

fun floorDimensions(dimensions: Vector2i): Vector2i =
    if (dimensions.x < 0 || dimensions.y < 0)
      Vector2i(max(0, dimensions.x), max(0, dimensions.y))
    else
      dimensions
