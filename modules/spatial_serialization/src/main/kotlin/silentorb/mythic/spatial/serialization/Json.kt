package silentorb.mythic.spatial.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import silentorb.mythic.configuration.getJsonObjectMapper
import silentorb.mythic.configuration.getObjectMapper
import silentorb.mythic.resource_loading.getResourceStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

val getJsonObjectMapper = getObjectMapper {
  initializeSpatialObjectMapper(ObjectMapper())
}

inline fun <reified T> loadSpatialJsonFile(stream: InputStream): T {
  val result = getJsonObjectMapper().readValue(stream, T::class.java)
  return result
}

inline fun <reified T> loadSpatialJsonFile(path: String): T =
    loadSpatialJsonFile(FileInputStream(path))

inline fun <reified T> loadSpatialJsonResource(path: String): T {
  try {
    return getResourceStream(path).use { content ->
      val result = silentorb.mythic.spatial.serialization.getJsonObjectMapper().readValue(content, T::class.java)
      result
    }
  } catch (e: Throwable) {
    throw Error("Could not load JSON resource $path")
  }
}

inline fun <reified T> saveJsonResource(path: String, record: T) {
  try {
    Files.newBufferedWriter(Paths.get(path)).use {
      getJsonObjectMapper().writeValue(it, record)
    }
  } catch (e: Throwable) {
    throw Error("Could not save JSON resource $path")
  }
}

inline fun <reified T> saveSpatialJsonFile(path: String, data: T) {
  Files.newBufferedWriter(Paths.get(path)).use {
    getJsonObjectMapper().writeValue(it, data)
  }
}
