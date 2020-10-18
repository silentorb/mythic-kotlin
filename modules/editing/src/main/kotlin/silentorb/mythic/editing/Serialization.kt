package silentorb.mythic.editing

import silentorb.mythic.resource_loading.getUrlPath
import silentorb.mythic.resource_loading.listFiles
import silentorb.mythic.spatial.serialization.loadJsonResource
import silentorb.mythic.spatial.serialization.saveJsonResource
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Path
import java.util.*

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


fun loadGraph(path: String): Graph =
    loadJsonResource<GraphFile>(path).nodes

fun saveGraph(path: String, graph: Graph) =
    saveJsonResource(path, GraphFile(nodes = graph))

fun loadGraphLibrary(directoryPath: String): GraphLibrary {
  val rootPath = getUrlPath(directoryPath)
  val resourcesPath = rootPath.getRoot().resolve(rootPath.subpath(0, rootPath.nameCount - Path.of(directoryPath).nameCount))
  val files = listFiles(rootPath)
  val library = files.associate { filePath ->
    val name = filePath.fileName.toString().dropLast(".json".length)
    val relativePath = resourcesPath.relativize(filePath)
    val graph = loadGraph(relativePath.toString())
    name to graph
  }
  return library
}
