package silentorb.mythic.fathom

import silentorb.mythic.fathom.surfacing.snapToSurface
import silentorb.mythic.imaging.texturing.FloatSampler3d
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3
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

fun deformer3dSampler(first: DistanceFunction, deformer: FloatSampler3d): DistanceFunction =
    { origin ->
      val distance = first(origin)
      val location = snapToSurface(first, origin)
      distance + deformer(location)
    }

fun times3dSampler(first: DistanceFunction, constant: Float): DistanceFunction =
    { origin ->
      first(origin) * constant
    }
