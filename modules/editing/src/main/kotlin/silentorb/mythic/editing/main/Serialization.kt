package silentorb.mythic.editing.main

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import silentorb.mythic.configuration.loadJsonFile
import silentorb.mythic.configuration.loadYamlFile
import silentorb.mythic.configuration.saveYamlFile
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.ent.*
import silentorb.mythic.resource_loading.listFilesAndFoldersRecursive
import silentorb.mythic.resource_loading.scanResources
import silentorb.mythic.spatial.serialization.loadSpatialJsonResource
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

const val defaultConfigFilePath = "editor.yaml"

fun serializeGraph(propertyDefinitions: PropertyDefinitions, graph: Graph) =
    graph.map {
      val serialization = propertyDefinitions[it.property]?.serialization
      val value = if (serialization != null)
        serialization.save(it.target)
      else
        it.target

      listOf(it.source, it.property, value)
    }

fun deserializeGraph(propertiesSerialization: PropertiesSerialization, file: GraphFile) =
    file.graph
        .map {
          val property = it[1].toString()
          val serialization = propertiesSerialization[property]
          val value = if (serialization != null)
            serialization.load(it[2])
          else
            it[2]

          Entry(it[0].toString(), property, value)
        }
        .toSet()

fun loadGraph(propertiesSerialization: PropertiesSerialization, path: String): Graph =
    deserializeGraph(propertiesSerialization, loadJsonFile(path))

fun loadGraphResource(propertiesSerialization: PropertiesSerialization, path: String): Graph =
    deserializeGraph(propertiesSerialization, loadSpatialJsonResource(path))

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

fun loadGraphLibrary(propertiesSerialization: PropertiesSerialization, directoryPath: String): GraphLibrary {
  val files = scanResources(directoryPath, listOf(".scene"))
  val library: GraphLibrary = files.associate { filePath ->
    val name = filePath.fileName.toString().dropLast(sceneFileExtension.length)
    val graph = loadGraphResource(propertiesSerialization, filePath.toString())
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
      FileItemType.folder
    else
      FileItemType.file

    path to newFileItem(path, type)
  }
      .minus("")

  return fileItems
}

fun loadEditorState(filePath: String = defaultConfigFilePath): EditorPersistentState? =
    loadYamlFile(filePath)

fun loadEditorStateOrDefault(filePath: String = defaultConfigFilePath): EditorPersistentState =
    loadEditorState(filePath) ?: defaultEditorState()

fun checkSaveEditorState(previous: EditorPersistentState?, next: EditorPersistentState?, filePath: String = defaultConfigFilePath) {
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
    loadGraph(editor.enumerations.propertiesSerialization, filePath)
}

fun checkSaveGraph(previous: Editor, next: Editor) {
  if (getDebugBoolean("EDITOR_DISABLE_SAVE"))
    return

  val graphName = next.persistentState.graph
  val nextGraph = next.graphLibrary[graphName]
  val previousGraph = previous.graphLibrary[graphName]
  if (graphName != null && previousGraph != null && nextGraph != null && previousGraph != nextGraph) {
    val filePath = getGraphFilePath(next, graphName)
    if (filePath != null) {
      saveGraph(next.enumerations.propertyDefinitions, filePath, nextGraph)
    }
  }
}

fun checkSaveEditor(previous: Editor?, next: Editor?, filePath: String = defaultConfigFilePath) {
  checkSaveEditorState(previous?.persistentState, next?.persistentState, filePath)
  if (previous != null && next != null) {
    checkSaveGraph(previous, next)
  }
}
