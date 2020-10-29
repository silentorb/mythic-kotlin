package silentorb.mythic.configuration

import com.fasterxml.jackson.databind.ObjectMapper

fun getObjectMapper(constructor: () -> ObjectMapper): () -> ObjectMapper {
  var mapper: ObjectMapper? = null

  return {
    if (mapper == null) {
      mapper = constructor()
    }
    mapper!!
  }
}
