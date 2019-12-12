package configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

private var globalJsonMapper: ObjectMapper? = null

fun getJsonObjectMapper(): ObjectMapper {
  if (globalJsonMapper == null) {
    val mapper = ObjectMapper()
    val module = KotlinModule()
    mapper.registerModule(module)
    mapper.registerModule(getAfterburnerModule())
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    globalJsonMapper = mapper
  }

  return globalJsonMapper!!
}

inline fun <reified T> loadJsonFile(stream: InputStream): T {
  val result = getJsonObjectMapper().readValue(stream, T::class.java)
  return result
}

fun <T> saveJsonFile(path: String, data: T) {
  Files.newBufferedWriter(Paths.get(path)).use {
    getJsonObjectMapper().writeValue(it, data)
  }
}