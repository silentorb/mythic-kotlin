package silentorb.mythic.lookinglass.deferred

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL30
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

class DeferredScreenShader(val program: ShaderProgram) {
  private val scaleProperty = Vector2Property(program, "scale")

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

fun newFrameBufferTexture(dimensions: Vector2i, attachment: Int): Texture {
  val texture = Texture(dimensions.x, dimensions.y, TextureAttributes(
      format = TextureFormat.rgba, // alpha isn't always used but some devices prefer 4x float frame buffers
      storageUnit = TextureStorageUnit.float,
      smooth = false,
  ))
  glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, texture.id, 0)
  return texture
}

fun newDeferredShading(dimensions: Vector2i): DeferredShading {
  val frameBuffer = FrameBuffer()
  val albedo = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT0)
  val position = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT1)
  val normal = newFrameBufferTexture(dimensions, GL_COLOR_ATTACHMENT2)
  val attachments = BufferUtils.createIntBuffer(3)
  attachments.put(GL_COLOR_ATTACHMENT0)
  attachments.put(GL_COLOR_ATTACHMENT1)
  attachments.put(GL_COLOR_ATTACHMENT2)
  glDrawBuffers(attachments)
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

fun processDeferredShading(renderer: SceneRenderer) {
  val deferred = renderer.renderer.deferred!!
  debugMarkPass(true, "Applied Shading") {
    deferred.frameBuffer.activateRead()
    applyFrameBufferTexture(renderer) { shaders, scale -> shaders.deferredShading.activate(scale) }
  }
}
