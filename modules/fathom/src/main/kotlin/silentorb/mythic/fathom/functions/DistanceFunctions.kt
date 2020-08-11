package silentorb.mythic.fathom

import silentorb.imp.core.CompleteParameter
import silentorb.imp.core.CompleteSignature
import silentorb.imp.core.PathKey
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.fathom.misc.DistanceFunction
import silentorb.mythic.fathom.misc.distanceFunctionType
import silentorb.mythic.fathom.misc.fathomPath
import silentorb.mythic.fathom.surfacing.snapToSurface
import silentorb.mythic.imaging.texturing.DistanceSample
import silentorb.mythic.imaging.texturing.DistanceSampler
import silentorb.mythic.imaging.texturing.anonymousSampler
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.minMax
import kotlin.math.max
import kotlin.math.min

fun sphere(radius: Float): DistanceFunction =
    { origin ->
      anonymousSampler to origin.distance(Vector3.zero) - radius
    }

fun cube(bounds: Vector3): DistanceFunction {
  val halfBounds = bounds / 2f
  return { origin ->
    val q = origin.absolute() - halfBounds
    anonymousSampler to q.max(0f).length() + min(max(q.x, max(q.y, q.z)), 0f)
  }
}

fun capsule(radius: Float, height: Float): DistanceFunction =
    { origin ->
      val z = origin.z - minMax(origin.z, 0f, height)
      anonymousSampler to origin.copy(z = z).length() - radius
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

fun deformer3dSampler(first: DistanceFunction, deformer: DistanceSampler): DistanceFunction =
    { origin ->
      val (id, distance) = first(origin)
      val location = snapToSurface(first, origin)
      id to distance + deformer(location).second
    }

fun times3dSampler(first: DistanceFunction, constant: Float): DistanceFunction =
    { origin ->
      val (id, sample) = first(origin)
      id to sample * constant
    }

fun min(vararg values: DistanceSample): DistanceSample =
    values
        .reduce { a, b ->
          if (a.second <= b.second)
            a
          else
            b
        }

fun min(values: List<DistanceSample>): DistanceSample =
    min(*values.toTypedArray())

fun max(vararg values: DistanceSample): DistanceSample =
    values
        .reduce { a, b ->
          if (a.second >= b.second)
            a
          else
            b
        }

fun max(values: List<DistanceSample>): DistanceSample =
    max(*values.toTypedArray())

fun mergeDistanceFunctions(values: List<DistanceFunction>): DistanceFunction {
  val result: DistanceFunction = { location ->
    min(values.map { it(location) })
  }
  return result
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
          mergeDistanceFunctions(values)
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
            max(a, b.copy(second = -b.second))
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
                .reduce { a, b -> max(a, b) }
          }
          result
        }
    )
)
