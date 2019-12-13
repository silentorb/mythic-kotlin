package silentorb.mythic.lookinglass.meshes.loading

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import silentorb.mythic.configuration.getAfterburnerModule
import silentorb.mythic.lookinglass.getResourceStream
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import java.io.IOException

fun parseFloat(node: JsonNode): Float =
      node.floatValue()

class Vector4Deserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<Vector4>(vc) {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Vector4 {
    val node = jp.getCodec().readTree<ArrayNode>(jp)
    val result = Vector4(
        parseFloat(node.get(0)),
        parseFloat(node.get(1)),
        parseFloat(node.get(2)),
        parseFloat(node.get(3))
    )
    return result
  }
}

class Vector3Deserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<Vector3>(vc) {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Vector3 {
    val node = jp.getCodec().readTree<ArrayNode>(jp)
    val result = Vector3(
        parseFloat(node.get(0)),
        parseFloat(node.get(1)),
        parseFloat(node.get(2))
    )
    return result
  }
}

private var globalMapper: ObjectMapper? = null

fun getObjectMapper(): ObjectMapper {
  if (globalMapper == null) {
    val mapper = ObjectMapper()
    val module = KotlinModule()
    module.addDeserializer(Vector3::class.java, Vector3Deserializer(null))
    module.addDeserializer(Vector4::class.java, Vector4Deserializer(null))
    mapper.registerModule(module)
    mapper.registerModule(getAfterburnerModule())
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    globalMapper = mapper
  }

  return globalMapper!!
}

inline fun <reified T> loadJsonResource(path: String): T {
  try {
    val content = getResourceStream(path)
    val result = getObjectMapper().readValue(content, T::class.java)
    return result
  } catch (e: Throwable) {
    throw Error("Could not load JSON resource $path")
  }
}
