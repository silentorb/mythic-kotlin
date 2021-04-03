package silentorb.mythic.lookinglass.deferred

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL30.*
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.*
import silentorb.mythic.lookinglass.shading.*
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.toVector2

data class DeferredShading(
    val frameBuffer: FrameBuffer,
    var albedo: Texture,
    var position: Texture,
    var normal: Texture,
) {
  fun dispose() {
    frameBuffer.dispose()
    albedo.dispose()
    position.dispose()
    normal.dispose()
  }
}

const val deferredScreenVertex = """
layout(location = 0) in vec3 position;
out int instanceId;
uniform vec2 dimensions;
$sceneHeader
$lightingHeader

out vec4 fragmentPosition;
out vec2 screenDimensions;

void main() {
  instanceId = gl_InstanceID;
  screenDimensions = dimensions;
  Light light = section.lights[instanceId];
  vec4 position4 = vec4(position * light.direction.w + light.position, 1.0);
  gl_Position = scene.cameraTransform * position4;
  fragmentPosition = position4;
}
"""

const val extractAlbedoAndGlow = """
  vec4 albedoAndGlow = texture($deferredAlbedoKey, texCoords);
  vec3 albedo = albedoAndGlow.rgb;
  float glow = albedoAndGlow.a;
"""

const val deferredShadingFragment = """
flat in int instanceId;
in vec4 fragmentPosition;
in vec2 screenDimensions;
out vec4 outputColor;
uniform sampler2D $deferredAlbedoKey;
uniform sampler2D $deferredPositionKey;
uniform sampler2D $deferredNormalKey;
uniform vec4 inputColor;
$sceneHeader
$lightingCode

void main()
{
  Light light = section.lights[instanceId];
  vec2 texCoords = gl_FragCoord.xy / screenDimensions;
$extractAlbedoAndGlow
  vec3 position = texture($deferredPositionKey, texCoords).xyz;
  vec3 normal = texture($deferredNormalKey, texCoords).xyz;
  float inverseGlow = (1.0 - glow);
  vec3 rgb = processLight(light, vec4(albedo, 1.0), normal, scene.cameraDirection, position);
//  outputColor = vec4(rgb * inverseGlow, 1.0);
  outputColor = vec4(rgb, 1.0);
}

"""
const val deferredAmbientFragment = """
in vec2 texCoords;
out vec4 outputColor;
uniform float ambientValue;
uniform sampler2D $deferredAlbedoKey;

void main()
{
$extractAlbedoAndGlow
  outputColor = vec4(albedo * (ambientValue + glow), 1.0);
}
"""

class AmbientScreenShader(val program: ShaderProgram) {
  private val ambientProperty = FloatProperty(program, "ambientValue")
  private val scaleProperty = Vector2Property(program, "scale")

  init {
    routeTexture(program, deferredAlbedoKey, 0)
  }

  fun activate(scale: Vector2, ambientValue: Float) {
    scaleProperty.setValue(scale)
    ambientProperty.setValue(ambientValue)
    program.activate()
  }
}

fun newAmbientScreenShader() =
    AmbientScreenShader(ShaderProgram(screenVertex, deferredAmbientFragment))

class DeferredScreenShader(val program: ShaderProgram, uniformBuffers: UniformBuffers) {
  private val dimensionsProperty = Vector2Property(program, "dimensions")
  val sceneProperty = bindUniformBuffer(UniformBufferId.SceneUniform, program, uniformBuffers.scene)
  val sectionProperty = bindUniformBuffer(UniformBufferId.SectionUniform, program, uniformBuffers.section)

  init {
    routeTexture(program, deferredAlbedoKey, 0)
    routeTexture(program, deferredPositionKey, 1)
    routeTexture(program, deferredNormalKey, 2)
  }

  fun activate(dimensions: Vector2) {
    dimensionsProperty.setValue(dimensions)
    program.activate()
  }
}

fun newDeferredScreenShader(uniformBuffers: UniformBuffers) =
    DeferredScreenShader(ShaderProgram(deferredScreenVertex, deferredShadingFragment), uniformBuffers)

fun newDeferredShading(dimensions: Vector2i, depthTexture: Texture): DeferredShading {
  val frameBuffer = FrameBuffer()
  val albedo = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT0, TextureFormat.rgba, TextureStorageUnit.unsignedByte)
  val position = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT1, TextureFormat.rgba16f, TextureStorageUnit.float)
  val normal = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT2, TextureFormat.rgba16f, TextureStorageUnit.float)
  val attachments = BufferUtils.createIntBuffer(3)
  attachments.put(GL_COLOR_ATTACHMENT0)
  attachments.put(GL_COLOR_ATTACHMENT1)
  attachments.put(GL_COLOR_ATTACHMENT2)
  attachments.flip()
  glDrawBuffers(attachments)
  glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture.id, 0)
  val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
  if (status != GL_FRAMEBUFFER_COMPLETE)
    throw Error("Error creating framebuffer.")

  globalState.setFrameBuffer(0)
  return DeferredShading(
      frameBuffer = frameBuffer,
      albedo = albedo,
      position = position,
      normal = normal,
  )
}

fun updateDeferredShading(renderer: Renderer, dimensions: Vector2i): DeferredShading? {
  val deferred = renderer.deferred
  return if (renderer.options.shadingMode == ShadingMode.deferred) {
    if (deferred == null || dimensions.x != deferred.albedo.width || dimensions.y != deferred.albedo.height)
      newDeferredShading(dimensions, renderer.offscreenBuffer.depthTexture!!)
    else
      deferred
  } else
    null
}

fun applyDeferredShading(renderer: SceneRenderer, sphereMesh: GeneralMesh) {
  val deferred = renderer.renderer.deferred!!
  debugMarkPass(true, "Applied Shading") {
    renderer.offscreenBuffer.frameBuffer.activate()
    deferred.albedo.activate(GL_TEXTURE0)
    deferred.position.activate(GL_TEXTURE1)
    deferred.normal.activate(GL_TEXTURE2)
    globalState.depthEnabled = false
    val dimensions = renderer.windowInfo.dimensions.toVector2()
    val ambient = renderer.scene.lightingConfig.ambient
    applyFrameBufferTexture(renderer) { shaders, scale -> shaders.deferredAmbientShading.activate(scale, ambient) }
    val shader = renderer.renderer.shaders.deferredShading
    shader.activate(dimensions)
    globalState.cullFaces = true
    globalState.blendEnabled = true
    globalState.blendFunction = GL_ONE to GL_ONE
    globalState.cullFaceSides = GL_FRONT
    drawMeshInstanced(sphereMesh, DrawMethod.triangleFan, renderer.scene.lights.size)
    globalState.depthEnabled = true
    globalState.blendEnabled = false
    globalState.cullFaceSides = GL_BACK
  }
}
