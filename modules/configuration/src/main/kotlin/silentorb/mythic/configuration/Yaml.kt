package silentorb.mythic.configuration

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

private var afterburnerModule: AfterburnerModule? = null

fun getAfterburnerModule(): AfterburnerModule {
  if (afterburnerModule == null) {
    afterburnerModule = AfterburnerModule()
    afterburnerModule!!.setUseValueClassLoader(false)
  }
  return afterburnerModule!!
}

val getYamlObjectMapper = getObjectMapper {
  val mapper = YAMLMapper()
  mapper.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false)
  mapper.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true)
  mapper.registerModule(KotlinModule())
  mapper.registerModule(getAfterburnerModule())
  mapper
}

inline fun <reified T> loadYamlFile(path: String): T? {
  if (File(path).isFile) {
    val mapper = getYamlObjectMapper()
    return Files.newBufferedReader(Paths.get(path)).use {
      mapper.readValue(it, T::class.java)
    }
  }

  return null
}

fun getResourceStream(name: String): InputStream? {
  val classloader = Thread.currentThread().contextClassLoader
  return classloader.getResourceAsStream(name)
}

inline fun <reified T> loadYamlResource(path: String): T {
  val content = getResourceStream(path)
  return getYamlObjectMapper().readValue(content, T::class.java)
}

inline fun <reified T> loadYamlResource(path: String, typeref: TypeReference<T>): T {
  val content = getResourceStream(path)
  return getYamlObjectMapper().readValue(content, typeref)
}

fun <T> saveYamlFile(mapper: ObjectMapper, path: String, data: T) {
  Files.newBufferedWriter(Paths.get(path)).use {
    mapper.writeValue(it, data)
  }
}

fun <T> saveYamlFile(path: String, config: T) {
  saveYamlFile(getYamlObjectMapper(), path, config)
}
