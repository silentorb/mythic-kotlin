package silentorb.mythic.spatial.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import silentorb.mythic.configuration.getAfterburnerModule
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

class Vector4Serializer @JvmOverloads constructor(vc: Class<Vector4>? = null) : StdSerializer<Vector4>(vc) {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: Vector4, gen: JsonGenerator, provider: SerializerProvider) {
    gen.writeStartArray()
    gen.writeNumber(value.x)
    gen.writeNumber(value.y)
    gen.writeNumber(value.z)
    gen.writeNumber(value.w)
    gen.writeEndArray()
  }
}

class Vector3Deserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<Vector3>(vc) {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Vector3 {
    val node = jp.codec.readTree<ArrayNode>(jp)
    return Vector3(
        parseFloat(node.get(0)),
        parseFloat(node.get(1)),
        parseFloat(node.get(2))
    )
  }
}

class Vector3Serializer @JvmOverloads constructor(vc: Class<Vector3>? = null) : StdSerializer<Vector3>(vc) {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: Vector3, gen: JsonGenerator, provider: SerializerProvider) {
    gen.writeStartArray()
    gen.writeNumber(value.x)
    gen.writeNumber(value.y)
    gen.writeNumber(value.z)
    gen.writeEndArray()
  }
}

class Vector3iDeserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<Vector3i>(vc) {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Vector3i {
    val node = jp.getCodec().readTree<ArrayNode>(jp)
    return Vector3i(
        parseInt(node.get(0)),
        parseInt(node.get(1)),
        parseInt(node.get(2))
    )
  }
}

class QuaternionDeserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<Quaternion>(vc) {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Quaternion {
    val node = jp.getCodec().readTree<ArrayNode>(jp)
    return Quaternion(
        parseFloat(node.get(0)),
        parseFloat(node.get(1)),
        parseFloat(node.get(2)),
        parseFloat(node.get(3))
    )
  }
}

class QuaternionSerializer @JvmOverloads constructor(vc: Class<Quaternion>? = null) : StdSerializer<Quaternion>(vc) {

  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(value: Quaternion, gen: JsonGenerator, provider: SerializerProvider) {
    gen.writeStartArray()
    gen.writeNumber(value.x)
    gen.writeNumber(value.y)
    gen.writeNumber(value.z)
    gen.writeNumber(value.w)
    gen.writeEndArray()
  }
}

fun initializeSpatialObjectMapper(mapper: ObjectMapper): ObjectMapper {
  val module = KotlinModule()

  module.addDeserializer(Vector3::class.java, Vector3Deserializer(null))
  module.addDeserializer(Vector4::class.java, Vector4Deserializer(null))
  module.addDeserializer(Vector3i::class.java, Vector3iDeserializer(null))
  module.addDeserializer(Quaternion::class.java, QuaternionDeserializer(null))

//  module.addSerializer(Vector3::class.java, Vector3Serializer(null))
//  module.addSerializer(Vector4::class.java, Vector4Serializer(null))
//  module.addSerializer(Quaternion::class.java, QuaternionSerializer(null))

  mapper.registerModule(module)
  mapper.registerModule(getAfterburnerModule())
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  return mapper
}
