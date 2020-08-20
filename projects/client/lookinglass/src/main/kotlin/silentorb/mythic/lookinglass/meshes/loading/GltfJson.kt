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
import silentorb.mythic.resource_loading.getResourceStream
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import silentorb.mythic.spatial.Vector4
import java.io.IOException

fun parseFloat(node: JsonNode): Float =
    node.floatValue()

fun parseInt(node: JsonNode): Int =
    node.intValue()

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

class Vector3iDeserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<Vector3i>(vc) {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Vector3i {
    val node = jp.getCodec().readTree<ArrayNode>(jp)
    val result = Vector3i(
        parseInt(node.get(0)),
        parseInt(node.get(1)),
        parseInt(node.get(2))
    )
    return result
  }
}

class QuaternionDeserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<Quaternion>(vc) {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Quaternion {
    val node = jp.getCodec().readTree<ArrayNode>(jp)
    val result = Quaternion(
        parseFloat(node.get(0)),
        parseFloat(node.get(1)),
        parseFloat(node.get(2)),
        parseFloat(node.get(3))
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
    module.addDeserializer(Vector3i::class.java, Vector3iDeserializer(null))
    module.addDeserializer(Quaternion::class.java, QuaternionDeserializer(null))
    mapper.registerModule(module)
    mapper.registerModule(getAfterburnerModule())
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    globalMapper = mapper
  }

  return globalMapper!!
}

inline fun <reified T> loadJsonResource(path: String): T {
  try {
    return getResourceStream(path).use { content ->
      val result = getObjectMapper().readValue(content, T::class.java)
      result
    }
  } catch (e: Throwable) {
    throw Error("Could not load JSON resource $path")
  }
}
