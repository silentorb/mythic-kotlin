package silentorb.mythic.lookinglass.shading

import silentorb.mythic.lookinglass.LodRanges
import silentorb.mythic.lookinglass.SamplePartitioning
import silentorb.mythic.scenery.SamplePoint
import silentorb.mythic.scenery.Shading
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.toList
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.max

fun normalizedFloatToUnsignedByte(value: Float): Byte =
    (value * 255).toByte()

fun normalizedFloatToSignedByte(value: Float): Byte =
    (value * 128).toByte()

fun bytesToFloat(vararg values: Byte): Float =
    ByteBuffer.wrap(
        byteArrayOf(*values)
            .reversedArray()
    )
        .float

fun serializeShading(shading: Shading): Float =
    bytesToFloat(
        normalizedFloatToUnsignedByte(shading.color.x),
        normalizedFloatToUnsignedByte(shading.color.y),
        normalizedFloatToUnsignedByte(shading.color.z),
        normalizedFloatToUnsignedByte(shading.opacity)
    )

fun serializeNormal(normal: Vector3): Float =
    bytesToFloat(
        normalizedFloatToSignedByte(normal.x),
        normalizedFloatToSignedByte(normal.y),
        normalizedFloatToSignedByte(normal.z),
        0
    )

fun toFloatList(sample: SamplePoint) =
    toList(sample.location) + listOf(sample.size) + serializeShading(sample.shading) + serializeNormal(sample.normal)

enum class NormalSide {
  x_plus,
  x_minus,
  y_plus,
  y_minus,
  z_plus,
  z_minus
}

fun getNormalSide(normal: Vector3): NormalSide {
  val x = abs(normal.x)
  val y = abs(normal.y)
  val z = abs(normal.z)
  return when {
    x >= y && x >= z -> if (normal.x >= 0f) NormalSide.x_plus else NormalSide.x_minus
    y >= x && y >= z -> if (normal.y >= 0f) NormalSide.y_plus else NormalSide.y_minus
    else -> if (normal.z >= 0f) NormalSide.z_plus else NormalSide.z_minus
  }
}

fun partitionSamples(levels: Int, samples: List<SamplePoint>): Pair<SamplePartitioning, List<SamplePoint>> {
  val groups = samples
      .groupBy { getNormalSide(it.normal) }

  val ranges = NormalSide.values()
      .map { side ->
        val sideSamples = groups.getOrElse(side) { listOf() }
        (0 until levels)
            .map { level ->
              sideSamples.filter { it.level == level }
            }
      }

  return Pair(
      ranges.map { side -> side.map { it.size } },
      ranges.flatMap { it.flatten() }
  )
}

fun getVisibleSides(facing: Vector3): List<NormalSide> {
  return listOf(
      if (facing.x > 0f) NormalSide.x_plus else NormalSide.x_minus,
      if (facing.y > 0f) NormalSide.y_plus else NormalSide.y_minus,
      if (facing.z > 0f) NormalSide.z_plus else NormalSide.z_minus
  )
}

fun getLodLevel(lodRanges: LodRanges, levels: Int, distance: Float): Int {
  val index = lodRanges.indexOfFirst { it > distance }
  return if (index == -1)
    0
  else
    max(0, levels - 1 - index)
}
