package silentorb.mythic.lookinglass.deferred

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL30.*
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.ShadingMode
import silentorb.mythic.lookinglass.Renderer
import silentorb.mythic.lookinglass.SceneRenderer
import silentorb.mythic.lookinglass.applyFrameBufferTexture
import silentorb.mythic.lookinglass.shading.*
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector2i

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

val deferredShadingFragment = """
in vec2 texCoords;
out vec4 output_color;
uniform sampler2D $deferredAlbedoKey;
uniform sampler2D $deferredPositionKey;
uniform sampler2D $deferredNormalKey;
uniform vec4 inputColor;
$sceneHeader
$lightingHeader

void main()
{
  vec3 albedo = texture($deferredAlbedoKey, texCoords).rgb;
  vec3 position = texture($deferredPositionKey, texCoords).xyz;
  vec3 normal = texture($deferredNormalKey, texCoords).xyz;
  float glow = 0.0;
  vec3 lightResult = processLights(vec4(1.0), normal, scene.cameraDirection, position, glow);
  vec3 rgb = albedo * lightResult;
  output_color = vec4(rgb, 1.0);
}
"""

class DeferredScreenShader(val program: ShaderProgram, uniformBuffers: UniformBuffers) {
  private val scaleProperty = Vector2Property(program, "scale")
  val sceneProperty = bindUniformBuffer(UniformBufferId.SceneUniform, program, uniformBuffers.scene)
  val sectionProperty = bindUniformBuffer(UniformBufferId.SectionUniform, program, uniformBuffers.section)

  init {
    routeTexture(program, deferredAlbedoKey, 0)
    routeTexture(program, deferredPositionKey, 1)
    routeTexture(program, deferredNormalKey, 2)
  }

  fun activate(scale: Vector2) {
    scaleProperty.setValue(scale)

    program.activate()
  }
}

fun newFrameBufferTexture(dimensions: Vector2i, attachment: Int, format: TextureFormat,
                          storage: TextureStorageUnit): Texture {
  val texture = Texture(dimensions.x, dimensions.y, TextureAttributes(
      format = format,
      storageUnit = storage,
      smooth = false,
  ))
  glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, texture.id, 0)
  return texture
}

fun newDeferredShading(dimensions: Vector2i): DeferredShading {
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
  val depth = glGenRenderbuffers()
  glBindRenderbuffer(GL_RENDERBUFFER, depth)
  glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, dimensions.x, dimensions.y)
  glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depth)
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
      newDeferredShading(dimensions)
    else
      deferred
  } else
    null
}

fun applyDeferredShading(renderer: SceneRenderer) {
  val deferred = renderer.renderer.deferred!!
  debugMarkPass(true, "Applied Shading") {
    globalState.setFrameBuffer(0)
    deferred.albedo.activate(GL_TEXTURE0)
    deferred.position.activate(GL_TEXTURE1)
    deferred.normal.activate(GL_TEXTURE2)
    applyFrameBufferTexture(renderer) { shaders, scale -> shaders.deferredShading.activate(scale) }
  }
}
