package silentorb.mythic.editing

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import silentorb.mythic.configuration.loadJsonFile
import silentorb.mythic.configuration.loadYamlFile
import silentorb.mythic.configuration.saveYamlFile
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.resource_loading.getUrlPath
import silentorb.mythic.resource_loading.listFiles
import silentorb.mythic.resource_loading.listFilesAndFoldersRecursive
import silentorb.mythic.resource_loading.listFilesRecursive
import silentorb.mythic.spatial.serialization.loadSpatialJsonResource
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

const val defaultConfigFilePath = "editor.yaml"

fun loadFromResources(fileName: String): ByteArray? {
  return try {
    Objects.requireNonNull(Thread.currentThread().contextClassLoader.getResourceAsStream(fileName)).use { `is` ->
      ByteArrayOutputStream().use { buffer ->
        val data = ByteArray(16384)
        var nRead: Int
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

fun serializeGraph(propertyDefinitions: PropertyDefinitions, graph: Graph) =
    graph.map {
      val serialization = propertyDefinitions[it.property]?.serialization
      val value = if (serialization != null)
        serialization.save(it.target)
      else
        it.target

      listOf(it.source, it.property, value)
    }

fun deserializeGraph(propertyDefinitions: PropertyDefinitions, file: GraphFile) =
    file.graph
        .map {
          val property = it[1].toString()
          val serialization = propertyDefinitions[property]?.serialization
          val value = if (serialization != null)
            serialization.load(it[2])
          else
            it[2]

          Entry(it[0].toString(), property, value)
        }

fun loadGraph(propertyDefinitions: PropertyDefinitions, path: String): Graph =
    deserializeGraph(propertyDefinitions, loadJsonFile(path))

fun loadGraphResource(propertyDefinitions: PropertyDefinitions, path: String): Graph =
    deserializeGraph(propertyDefinitions, loadSpatialJsonResource(path))

fun saveGraph(propertyDefinitions: PropertyDefinitions, path: String, graph: Graph) =
    Files.newBufferedWriter(Paths.get(path)).use { stream ->
      val jsonFactory = JsonFactory()
      jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
      val mapper = ObjectMapper(jsonFactory)
      val module = KotlinModule()
      mapper.registerModule(module)

      val data = GraphFile(graph = serializeGraph(propertyDefinitions, graph))
      val entries = data.graph
          .distinct()
          .sortedBy { it[0].toString() }

      stream.write("{")
      stream.newLine()
      stream.write("\"graph\" : [")
      stream.newLine()

      for (entry in entries.dropLast(1)) {
        mapper.writeValue(stream, entry)
        stream.write(",")
        stream.newLine()
      }

      if (entries.any()) {
        mapper.writeValue(stream, entries.last())
        stream.newLine()
      }

      stream.write("]")
      stream.newLine()
      stream.write("}")
      stream.newLine()
    }

fun loadGraphLibrary(propertyDefinitions: PropertyDefinitions, directoryPath: String): GraphLibrary {
  val rootPath = getUrlPath(directoryPath)
  val resourcesPath = rootPath.getRoot().resolve(rootPath.subpath(0, rootPath.nameCount - Path.of(directoryPath).nameCount))
  val files = listFilesRecursive(rootPath)
  val library: GraphLibrary = files.associate { filePath ->
    val name = filePath.fileName.toString().dropLast(sceneFileExtension.length)
    val relativePath = resourcesPath.relativize(filePath)
    val path = relativePath.toString().replace("\\", "/")
    val graph = loadGraphResource(propertyDefinitions, path)
    name to graph
  }
  return library
}

fun loadProjectTree(rootPath: Path, rootName: String): FileItems {
  val files = listFilesAndFoldersRecursive(rootPath)
  val fileItems = files.associate { filePath ->
    val relativePath = rootPath.relativize(filePath)
    val path = relativePath.toString().replace("\\", "/")
    val type = if (File(filePath.toString()).isDirectory)
      FileItemType.directory
    else
      FileItemType.file

    path to newFileItem(path, type)
  }
      .minus("")

  return fileItems
}

fun loadEditorState(filePath: String = defaultConfigFilePath): EditorState? =
    loadYamlFile(filePath)

fun loadEditorStateOrDefault(filePath: String = defaultConfigFilePath): EditorState =
    loadEditorState(filePath) ?: defaultEditorState()

fun checkSaveEditorState(previous: EditorState?, next: EditorState?, filePath: String = defaultConfigFilePath) {
  if (next != null && previous != next && !getDebugBoolean("EDITOR_DISABLE_SAVE")) {
    saveYamlFile(filePath, next)
  }
}

fun resolveProjectFilePath(editor: Editor, path: String): String =
    editor.projectPath.resolve(Path.of(path)).toString()

fun getGraphFilePath(editor: Editor, graphName: String): String? {
  val fullFileName = "$graphName$sceneFileExtension"
  val options = editor.fileItems.values.filter { it.name == fullFileName }
  assert(options.size < 2)
  val path = options.firstOrNull()?.fullPath
  return if (path == null)
    null
  else
    resolveProjectFilePath(editor, path)
}

fun loadGraph(editor: Editor, graphName: String): Graph? {
  val filePath = getGraphFilePath(editor, graphName)
  return if (filePath == null)
    null
  else
    loadGraph(editor.propertyDefinitions, filePath)
}

fun checkSaveGraph(previous: Editor, next: Editor) {
  if (getDebugBoolean("EDITOR_DISABLE_SAVE"))
    return

  val graphName = next.state.graph
  val nextGraph = next.graphLibrary[graphName]
  val previousGraph = previous.graphLibrary[graphName]
  if (graphName != null && previousGraph != null && nextGraph != null && previousGraph != nextGraph) {
    val filePath = getGraphFilePath(next, graphName)
    if (filePath != null) {
      saveGraph(next.propertyDefinitions, filePath, nextGraph)
    }
  }
}

fun checkSaveEditor(previous: Editor?, next: Editor?, filePath: String = defaultConfigFilePath) {
  checkSaveEditorState(previous?.state, next?.state, filePath)
  if (previous != null && next != null) {
    checkSaveGraph(previous, next)
  }
}
