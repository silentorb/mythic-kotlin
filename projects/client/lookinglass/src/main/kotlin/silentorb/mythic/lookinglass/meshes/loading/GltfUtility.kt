package silentorb.mythic.lookinglass.meshes.loading

import silentorb.mythic.lookinglass.getResourceStream
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import org.lwjgl.BufferUtils
import java.io.DataInputStream
import java.nio.ByteBuffer

fun loadGltfByteBuffer(directoryPath: String, info: GltfInfo): ByteBuffer {
  val inputStream = getResourceStream(directoryPath + "/" + info.buffers[0].uri)
  val dataStream = DataInputStream(inputStream)
  val size = info.buffers[0].byteLength
  val buffer = BufferUtils.createByteBuffer(size)
  dataStream.use {
    for (i in 0 until size)
//    while (dataStream.available() > 0) {
      buffer.put(dataStream.readByte())
//    }
  }

  return buffer
}

typealias BufferIterator = (ByteBuffer, Int, (Int) -> Unit) -> Unit

val iterateBytes = { buffer: ByteBuffer, count: Int, action: (Int) -> Unit ->
  for (i in 0 until count) {
    val value = buffer.get().toInt() // and 0xFF
    action(value)
  }
}

val iterateShorts = { buffer: ByteBuffer, count: Int, action: (Int) -> Unit ->
  val intBuffer = buffer.asShortBuffer()
  for (i in 0 until count) {
    val value = intBuffer.get().toInt()// and 0xFF
    action(value)
  }
}

fun getFloats(buffer: ByteBuffer, offset: Int, count: Int): List<Float> {
  buffer.position(offset)
  val valueBuffer = buffer.asFloatBuffer()
  val result = mutableListOf<Float>()
  for (i in 0 until count) {
    val value = valueBuffer.get()// and 0xFF
    result.add(value)
  }
  return result.toList()
}

fun getVector3List(buffer: ByteBuffer, offset: Int, count: Int): List<Vector3> {
  buffer.position(offset)
  val valueBuffer = buffer.asFloatBuffer()
  val result = mutableListOf<Vector3>()
  for (i in 0 until count) {
    result.add(Vector3(
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get()
    ))
  }
  return result.toList()
}

fun getQuaternions(buffer: ByteBuffer, offset: Int, count: Int): List<Quaternion> {
  buffer.position(offset)
  val valueBuffer = buffer.asFloatBuffer()
  val result = mutableListOf<Quaternion>()
  for (i in 0 until count) {
    result.add(Quaternion(
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get()
    ))
  }
  return result.toList()
}

fun getMatrices(buffer: ByteBuffer, offset: Int, count: Int): List<Matrix> {
  buffer.position(offset)
  val valueBuffer = buffer.asFloatBuffer()
  val result = mutableListOf<Matrix>()
  for (i in 0 until count) {
    result.add(Matrix(
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get(),
        valueBuffer.get()
    ))
  }
  return result.toList()
}

fun selectBufferIterator(componentType: Int): BufferIterator =
    when (componentType) {
      ComponentType.UnsignedByte.value -> iterateBytes
      ComponentType.UnsignedShort.value -> iterateShorts
      else -> throw Error("Not implemented.")
    }

fun loadQuaternion(value: Vector4?) =
    if (value != null)
      Quaternion(value.x, value.y, value.z, value.w)
    else
      Quaternion()

fun getVector3(buffer: ByteBuffer) =
    Vector3(
        buffer.getFloat(),
        buffer.getFloat(),
        buffer.getFloat()
    )

fun getVector4(buffer: ByteBuffer) =
    Vector4(
        buffer.getFloat(),
        buffer.getFloat(),
        buffer.getFloat(),
        buffer.getFloat()
    )

fun getOffset(info: GltfInfo, accessorIndex: Int): Int {
  val accessor = info.accessors[accessorIndex]
  val bufferView = info.bufferViews[accessor.bufferView]
  return bufferView.byteOffset
}

fun parseFloat(value: Any?): Float =
    if (value is Int)
      value.toFloat()
    else if (value is Double)
      value.toFloat()
    else
      throw Error("Not supported")

fun getComponentIntValue(buffer: ByteBuffer, componentType: Int): Int {
  return when (componentType) {
    ComponentType.UnsignedByte.value -> buffer.get().toInt() and 0xFF
    ComponentType.UnsignedShort.value -> buffer.short.toInt() and 0xFF
    ComponentType.UnsignedInt.value -> buffer.int and 0xFF
    else -> throw Error("Not implemented.")
  }
}

fun getComponentByteSize(componentType: Int): Int {
  return when (componentType) {
    ComponentType.UnsignedByte.value -> 1
    ComponentType.UnsignedShort.value -> 2
    ComponentType.UnsignedInt.value -> 4
    ComponentType.Float.value -> 4
    else -> throw Error("Not implemented.")
  }
}

@Suppress("UNCHECKED_CAST")
fun parseVector3(source: Any?): Vector3 {
  val dimensions = source as List<Double>
  return Vector3(dimensions[0].toFloat(), dimensions[1].toFloat(), dimensions[2].toFloat())
}
