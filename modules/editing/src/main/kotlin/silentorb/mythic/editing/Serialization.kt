package silentorb.mythic.editing

import silentorb.mythic.spatial.serialization.loadJsonResource
import silentorb.mythic.spatial.serialization.saveJsonResource
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UncheckedIOException
import java.util.*

fun loadGraph(path: String): Graph =
    loadJsonResource(path)

fun saveGraph(path: String, graph: Graph) =
    saveJsonResource(path, graph)

fun loadFromResources(fileName: String): ByteArray? {
  return try {
    Objects.requireNonNull(Thread.currentThread().contextClassLoader.getResourceAsStream(fileName)).use { `is` ->
      ByteArrayOutputStream().use { buffer ->
        val data = ByteArray(16384)
        var nRead: Int = 0
        while (`is`.read(data, 0, data.size).also { nRead = it } != -1) {
          buffer.write(data, 0, nRead)
        }
        buffer.toByteArray()
      }
    }
  } catch (e: IOException) {
    throw UncheckedIOException(e)
  }
}
