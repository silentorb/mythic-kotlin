package silentorb.mythic.lookinglass.shading

import silentorb.mythic.scenery.SamplePoint
import silentorb.mythic.scenery.Shading
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.toList
import java.nio.ByteBuffer

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
