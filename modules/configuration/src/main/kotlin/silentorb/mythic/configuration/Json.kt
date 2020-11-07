package silentorb.mythic.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

val getJsonObjectMapper = getObjectMapper {
  val mapper = ObjectMapper()
  val module = KotlinModule()
  mapper.registerModule(module)
  mapper.registerModule(getAfterburnerModule())
  mapper.enable(SerializationFeature.INDENT_OUTPUT)
  mapper
}

inline fun <reified T> loadJsonFile(stream: InputStream): T {
  val result = getJsonObjectMapper().readValue(stream, T::class.java)
  return result
}

inline fun <reified T> loadJsonFile(path: String): T =
    loadJsonFile(FileInputStream(path))

fun <T> saveJsonFile(path: String, data: T) {
  Files.newBufferedWriter(Paths.get(path)).use {
    getJsonObjectMapper().writeValue(it, data)
  }
}
