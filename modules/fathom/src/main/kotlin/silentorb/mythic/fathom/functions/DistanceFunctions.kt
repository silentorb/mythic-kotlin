package silentorb.mythic.fathom

import silentorb.imp.core.CompleteParameter
import silentorb.imp.core.CompleteSignature
import silentorb.imp.core.PathKey
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.fathom.misc.DistanceFunction
import silentorb.mythic.fathom.misc.distanceFunctionType
import silentorb.mythic.fathom.misc.fathomPath
import silentorb.mythic.fathom.misc.shapeType
import silentorb.mythic.fathom.surfacing.snapToSurface
import silentorb.mythic.imaging.texturing.FloatSampler3d
import silentorb.mythic.scenery.CompositeShape
import silentorb.mythic.scenery.Shape
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

fun distanceFunctions() = listOf(
    CompleteFunction(
        path = PathKey(fathomPath, "+"),
        signature = CompleteSignature(
            isVariadic = true,
            parameters = listOf(
                CompleteParameter("values", distanceFunctionType)
            ),
            output = distanceFunctionType
        ),
        implementation = { arguments ->
          val values = arguments["values"] as List<DistanceFunction>
          val result: DistanceFunction = { location ->
            values
                .map { it(location) }
                .reduce(::min)
          }
          result
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "-"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("first", distanceFunctionType),
                CompleteParameter("second", distanceFunctionType)
            ),
            output = distanceFunctionType
        ),
        implementation = { arguments ->
          val first = arguments["first"] as DistanceFunction
          val second = arguments["second"] as DistanceFunction
          val result: DistanceFunction = { location ->
            val a = first(location)
            val b = second(location)
            max(a, -b)
          }
          result
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "intersect"),
        signature = CompleteSignature(
            isVariadic = true,
            parameters = listOf(
                CompleteParameter("values", distanceFunctionType)
            ),
            output = distanceFunctionType
        ),
        implementation = { arguments ->
          val values = arguments["values"] as List<DistanceFunction>
          val result: DistanceFunction = { location ->
            values
                .map { it(location) }
                .reduce(::max)
          }
          result
        }
    )
)
