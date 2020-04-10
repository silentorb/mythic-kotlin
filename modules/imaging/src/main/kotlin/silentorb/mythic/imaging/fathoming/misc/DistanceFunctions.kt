package silentorb.mythic.imaging.fathoming

import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.unaryMinus
import kotlin.math.max
import kotlin.math.min

fun sphere(radius: Float): DistanceFunction =
    { origin ->
      origin.distance(Vector3.zero) - radius
    }

fun cube(bounds: Vector3): DistanceFunction {
  val halfBounds = bounds / 2f
  return { origin ->
    val q = origin.absolute() - halfBounds
    q.max(0f).length() + min(max(q.x, max(q.y, q.z)), 0f)
  }
}

fun translate(offset: Vector3, function: DistanceFunction): DistanceFunction =
    { origin ->
      function(origin - offset)
    }

fun rotate(quaternion: Quaternion, function: DistanceFunction): DistanceFunction {
  val opposite = -quaternion
  return { origin ->
    val rotated = opposite.transform(origin)
    function(rotated)
  }
}
