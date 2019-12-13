package silentorb.mythic.lookinglass.meshes.loading

import java.io.File
import java.io.PrintWriter
import java.nio.ByteBuffer

fun getComponentValue(buffer: ByteBuffer, componentType: Int): Any {
  return when (componentType) {
    ComponentType.UnsignedByte.value -> buffer.get().toInt() and 0xFF
    ComponentType.UnsignedShort.value -> buffer.short.toInt() and 0xFF
    ComponentType.UnsignedInt.value -> buffer.int and 0xFF
    ComponentType.Float.value -> buffer.float
    else -> throw Error("Not implemented.")
  }
}

private val componentCountMap = mapOf(
    AccessorType.SCALAR to 1,
    AccessorType.VEC2 to 2,
    AccessorType.VEC2 to 2,
    AccessorType.VEC3 to 3,
    AccessorType.VEC4 to 4,
    AccessorType.MAT4 to 16
)

private fun log(out: PrintWriter, text: Any, position: Int) {
  val line = " " + position.toString().padStart(7, ' ') + " " + text
  out.println(line)
//    println(line)
}

fun logBuffer(buffer: ByteBuffer, info: GltfInfo) {
  File("gltf-data.txt").printWriter().use { out ->
    info.accessors.forEachIndexed { index, accessor ->
      val bufferView = info.bufferViews[accessor.bufferView]
      out.println("Accessor " + index + " " + accessor.name)

      for (i in 0 until accessor.count) {
        buffer.position(bufferView.byteOffset + accessor.byteOffset + i * bufferView.byteStride)
        val componentCount = componentCountMap[accessor.type]!!
        val position = buffer.position()
        val values = (0 until componentCount).map {
          getComponentValue(buffer, accessor.componentType).toString()
        }
        val valueString = values.joinToString(", ")
        log(out, valueString, position)
      }
    }
  }
}
