package silentorb.mythic.lookinglass

import silentorb.mythic.glowing.*
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

fun serializeShadingColor(shading: Shading): Float =
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
    listOf<Float>() +

        toList(sample.location) +
        serializeShadingColor(sample.shading) +
        sample.size +
        bytesToFloat(
            sample.level.toByte(),
            normalizedFloatToSignedByte(sample.normal.x),
            normalizedFloatToSignedByte(sample.normal.y),
            normalizedFloatToSignedByte(sample.normal.z)
        ) +
        listOf()

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
  val index = lodRanges.indexOfLast { it > distance }
  return if (index == -1)
    0
  else
    max(0, index)
}

fun getVolumeOffsets(partitioning: SamplePartitioning): List<Int> {
  val realCounts = partitioning
      .flatten()

  val (offsets) = realCounts
      .fold(Pair(listOf<Int>(), 0)) { (a, b), c ->
        val offset = b + c
        Pair(a + b, offset)
      }

  return offsets
}

fun newSampledModel(vertexSchema: VertexSchema, lodRanges: LodRanges, levels: Int, initialPoints: List<SamplePoint>): SampledModel {
  val (partitioning, points) = partitionSamples(levels, initialPoints)
  val vertices = points
      .flatMap(::toFloatList)
      .toFloatArray()

  val mesh = GeneralMesh(
      vertexSchema = vertexSchema,
      primitiveType = PrimitiveType.points,
      vertexBuffer = newVertexBuffer(vertexSchema).load(createFloatBuffer(vertices)),
      count = vertices.size / vertexSchema.floatSize
  )

  return SampledModel(
      mesh = mesh,
      partitioning = partitioning,
      offsets = getVolumeOffsets(partitioning),
      levels = levels,
      lodRanges = lodRanges
  )
}
