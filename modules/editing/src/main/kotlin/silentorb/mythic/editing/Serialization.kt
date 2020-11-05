package silentorb.mythic.editing

import silentorb.mythic.configuration.loadYamlFile
import silentorb.mythic.configuration.saveYamlFile
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.resource_loading.getUrlPath
import silentorb.mythic.resource_loading.listFiles
import silentorb.mythic.spatial.serialization.loadJsonResource
import silentorb.mythic.spatial.serialization.saveJsonResource
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Path
import java.util.*

const val defaultConfigFilePath = "editor.yaml"

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
    loadJsonResource<GraphFile>(path).graph
        .map { Entry(it[0].toString(), it[1].toString(), it[2]) }

fun saveGraph(path: String, graph: Graph) =
    saveJsonResource(path, GraphFile(graph = graph.map { listOf(it.source, it.property, it.target) }))

fun loadGraphLibrary(directoryPath: String): GraphLibrary {
  val rootPath = getUrlPath(directoryPath)
  val resourcesPath = rootPath.getRoot().resolve(rootPath.subpath(0, rootPath.nameCount - Path.of(directoryPath).nameCount))
  val files = listFiles(rootPath)
  val library = files.associate { filePath ->
    val name = filePath.fileName.toString().dropLast(".json".length)
    val relativePath = resourcesPath.relativize(filePath)
    val graph = loadGraph(relativePath.toString().replace("\\", "/"))
    name to graph
  }
  return library
}

fun loadEditorState(filePath: String = defaultConfigFilePath): EditorState? =
    loadYamlFile(filePath)

fun loadEditorStateOrDefault(filePath: String = defaultConfigFilePath): EditorState =
    loadEditorState(filePath) ?: defaultEditorState()

fun checkSaveEditorState(previous: EditorState?, next: EditorState?, filePath: String = defaultConfigFilePath) {
  if (next != null && previous != next && !getDebugBoolean("DISABLE_EDITOR_SAVE")) {
    saveYamlFile(filePath, next)
  }
}

fun checkSaveGraph(editor: Editor, previous: Graph?, next: Graph?) {
  if (next != null && previous != next) {
    editor.graphLibrary
    saveGraph(filePath, next)
  }
}

fun checkSaveEditor(previous: Editor?, next: Editor?, filePath: String = defaultConfigFilePath) {
  checkSaveEditorState(previous?.state, next?.state, filePath)

}
