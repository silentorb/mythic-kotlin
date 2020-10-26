package silentorb.mythic.lookinglass.shading

import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.glowing.VertexSchema

data class ShaderKey(
    val vertexSchema: VertexSchema,
    val config: ShaderFeatureConfig
)

typealias ShaderCache = MutableMap<ShaderKey, GeneralPerspectiveShader>

typealias ShaderGetter = (VertexSchema, ShaderFeatureConfig) -> GeneralPerspectiveShader

fun getCachedShader(buffers: UniformBuffers, cache: ShaderCache): ShaderGetter = { vertexSchema, config ->
  // Not sure if the JVM can optimize away the allocation of a key simply for lookup.
  // If not, this implementation will probably need to be refactored away from using a Pair-like structure.
  val key = ShaderKey(vertexSchema, config)
  val shader = cache[key]
  if (shader!= null)
    shader
  else {
    if (getDebugBoolean("LOG_SHADER_CREATION")) {
      println("New Shader Created")
    }
    val newShader = GeneralPerspectiveShader(buffers, vertexSchema, config)
    cache[key] = newShader
    newShader
  }
}
