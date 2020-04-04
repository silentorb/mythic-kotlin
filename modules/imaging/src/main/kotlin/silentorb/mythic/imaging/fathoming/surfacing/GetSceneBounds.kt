package silentorb.mythic.imaging.fathoming.surfacing

import silentorb.mythic.imaging.fathoming.DistanceFunction
import silentorb.mythic.spatial.*

// The results of this function can  be imprecisely smaller than the actual bounds and can use a little padding
// This is due to measuring the bounds of a cube using arcs
fun getSceneDecimalBounds(getDistance: DistanceFunction): DecimalBounds {
  val originDistance = 100000f
  val baseVectors = arrayOf(
      Vector3(1f, 0f, 0f),
      Vector3(0f, 1f, 0f),
      Vector3(0f, 0f, 1f)
  )
  val (first, second) = (listOf(-1f, 1f))
      .map { facing ->
        listToVector3(baseVectors
            .map { vector ->
              val origin = vector * facing * originDistance
              val distance = getDistance(origin)
              (originDistance - distance) * facing
            }
        )
      }

  return DecimalBounds(
      start = first,
      end = second
  )
}

fun getSceneGridBounds(getDistance: DistanceFunction, cellSize: Float): GridBounds {
  val decimalBounds = getSceneDecimalBounds(getDistance)
  return GridBounds(
      start = toVector3iRoundedDown(decimalBounds.start / cellSize),
      end = toVector3iRoundedUp(decimalBounds.end / cellSize)
  )
}
