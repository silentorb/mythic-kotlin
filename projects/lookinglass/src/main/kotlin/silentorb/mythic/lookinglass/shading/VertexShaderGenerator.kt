package silentorb.mythic.lookinglass.shading

import org.lwjgl.opengl.GL11.*
import silentorb.mythic.glowing.VertexAttribute
import silentorb.mythic.glowing.VertexSchema
import silentorb.mythic.lookinglass.ShadingMode

const val screenDimensionsOutHeader = """out vec2 screenDimensions;
uniform vec2 dimensions;
"""

const val screenDimensionsApplication = "screenDimensions = dimensions;"

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
uniform float lodOpacityLevels[$maxLodLevels];
uniform float nearPlaneHeight;
out vec4 fragmentColor;
"""

private const val pointSizeOutput = """
  gl_PointSize = (nearPlaneHeight * pointSize) / gl_Position.w;
  fragmentColor = color * vec4(1.0, 1.0, 1.0, lodOpacityLevels[level]);
"""

private const val coloredHeader = """
out vec4 fragmentColor;
"""

private const val coloredOutput = """
  fragmentColor = color;
"""

private fun textureOperations(config: ShaderFeatureConfig) =
    if (config.animatedTexture && config.instanced)
      "textureCoordinates = uv * uniformTextureScale + instanceSection.instances[gl_InstanceID].textureOffset;"
    else if (config.texture)
      "textureCoordinates = uv;"
    else
      null

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
    }
    else
      null

private fun mainVertex(config: ShaderFeatureConfig): String {
  return listOfNotNull(
      instanceHeader(config.instanced),
      if (config.shading != ShadingMode.none) shadingHeader else null,
      if (config.texture) textureHeader else null,
      if (config.animatedTexture) "uniform vec2 uniformTextureScale;" else null,
      if (config.skeleton) weightHeader else null,
      if (config.pointSize) pointSizeHeader else null,
      if (config.colored) coloredHeader else null,
      if (config.deferredBlending) screenDimensionsOutHeader else null,
      "\nvoid main() {",
      "  vec4 position4 = vec4(position, 1.0);",
      instanceOperations(config.instanced),
      if (config.skeleton) weightOperations else null,
      "vec4 modelPosition = modelTransform * position4;",
      "gl_Position = scene.cameraTransform * modelPosition;",
      if (config.shading != ShadingMode.none) shadingOperations else null,
      if (config.pointSize) pointSizeOutput else null,
      if (config.colored) coloredOutput else null,
      if (config.deferredBlending) screenDimensionsApplication else null,
      textureOperations(config),
      "}\n",
  ).joinToString("\n")
}

fun shaderFieldType(attribute: VertexAttribute): String =
    if (attribute.elementType == GL_FLOAT || attribute.normalize)
      when (attribute.count) {
        1 -> "float"
        else -> "vec${attribute.count}"
      }
    else {
      when (attribute.elementType) {
        GL_BYTE -> "int"
        GL_UNSIGNED_BYTE -> "uint"
        else -> throw Error("Not implemented")
      }
    }

fun generateInputsHeader(vertexSchema: VertexSchema): String =
    vertexSchema.attributes.mapIndexed { i, it ->
      "layout(location = ${i}) in ${shaderFieldType(it)} ${it.name};"
    }.joinToString("\n")

fun generateVertexCode(config: ShaderFeatureConfig): (VertexSchema) -> String = { vertexSchema ->
  val inputHeader = generateInputsHeader(vertexSchema)
  inputHeader + sceneHeader + mainVertex(config)
}
