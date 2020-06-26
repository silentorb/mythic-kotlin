package silentorb.mythic.lookinglass.shading

import silentorb.mythic.glowing.VertexSchema


private const val weightHeader = """
layout (std140) uniform BoneTransforms {
  mat4[128] boneTransforms;
};
"""

private const val weightOperations = """
  vec3 position3 = vec3(0.0);

  for (int i = 0; i < 4; ++i) {
    int boneIndex = int(joints[i]);
    float strength = weights[i];
    position3 += (boneTransforms[boneIndex] * position4).xyz * strength;
  }
  position4 = vec4(position3, 1.0);
"""

private const val shadingHeader = """
uniform mat4 normalTransform;
out vec4 fragmentPosition;
out vec3 fragmentNormal;
"""

private const val textureHeader = """
out vec2 textureCoordinates;
"""

private const val shadingOperations = """
  fragmentPosition = modelPosition;
  fragmentNormal = normalize((normalTransform * vec4(normal, 1.0)).xyz);
"""

private const val pointSizeHeader = """
uniform float nearPlaneHeight;
out vec4 fragmentColor;
out uint fragmentLevel;
"""

private const val pointSizeOutput = """
  gl_PointSize = (nearPlaneHeight * pointSize) / gl_Position.w;
  fragmentColor = color;
"""

private fun textureOperations(config: ShaderFeatureConfig) =
    if (config.animatedTexture && config.instanced)
      "textureCoordinates = uv * uniformTextureScale + instanceSection.instances[gl_InstanceID].textureOffset;"
    else if (config.texture)
      "textureCoordinates = uv;"
    else
      ""

private fun instanceHeader(instanced: Boolean) =
    if (instanced)
      instancedParticleHeader
    else
      "uniform mat4 modelTransform;"

private fun instanceOperations(instanced: Boolean) =
    if (instanced) {
      """
  Instance instance = instanceSection.instances[gl_InstanceID];
  mat4 modelTransform = instance.position;
  fragmentColor = instance.color;
"""
    } else ""

private fun mainVertex(config: ShaderFeatureConfig): String {
  return """
${instanceHeader(config.instanced)}
${if (config.shading) shadingHeader else ""}
${if (config.texture) textureHeader else ""}
${if (config.animatedTexture) "uniform vec2 uniformTextureScale;" else ""}
${if (config.skeleton) weightHeader else ""}
${if (config.pointSize) pointSizeHeader else ""}

void main() {
  vec4 position4 = vec4(position, 1.0);
${instanceOperations(config.instanced)}
 ${if (config.skeleton) weightOperations else ""}
 vec4 modelPosition = modelTransform * position4;
  gl_Position = scene.cameraTransform * modelPosition;
${if (config.shading) shadingOperations else ""}
${if (config.pointSize) pointSizeOutput else ""}
${textureOperations(config)}
}
"""
}

fun shaderFieldType(size: Int): String =
    when(size) {
      1 -> "float"
      else -> "vec$size"
    }

fun generateInputsHeader(vertexSchema: VertexSchema): String =
    vertexSchema.attributes.mapIndexed { i, it ->
      "layout(location = ${i}) in ${shaderFieldType(it.count)} ${it.name};"
    }.joinToString("\n")

fun generateVertexCode(config: ShaderFeatureConfig): (VertexSchema) -> String = { vertexSchema ->
  val inputHeader = generateInputsHeader(vertexSchema)
  inputHeader + sceneHeader + mainVertex(config)
}
