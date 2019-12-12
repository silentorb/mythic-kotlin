package configuration

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

private var globalYamlMapper: YAMLMapper? = null
private var afterburnerModule: AfterburnerModule? = null

fun getAfterburnerModule(): AfterburnerModule {
  if (afterburnerModule == null) {
    afterburnerModule = AfterburnerModule()
    afterburnerModule!!.setUseValueClassLoader(false)
  }
  return afterburnerModule!!
}

fun getYamlObjectMapper(): YAMLMapper {
  if (globalYamlMapper == null) {
    val mapper = YAMLMapper()
    mapper.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false)
    mapper.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true)
    mapper.registerModule(KotlinModule())
    mapper.registerModule(getAfterburnerModule())
    globalYamlMapper = mapper
    return mapper
  }

  return globalYamlMapper!!
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

fun getResourceStream(name: String): InputStream {
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

fun <T> saveYamlFile(mapper: YAMLMapper, path: String, config: T) {
  Files.newBufferedWriter(Paths.get(path)).use {
    mapper.writeValue(it, config)
  }
}

fun <T> saveYamlFile(path: String, config: T) {
  saveYamlFile(getYamlObjectMapper(), path, config)
}

class ConfigManager<T>(private val path: String, private val config: T) {
  private var previous: String

  init {
    val mapper = getYamlObjectMapper()
    previous = mapper.writeValueAsString(config)
  }

  fun save() {
    val mapper = getYamlObjectMapper()
    val newString = mapper.writeValueAsString(config)
    if (newString != previous) {
      saveYamlFile(mapper, path, config)
      previous = newString
    }
  }
}
