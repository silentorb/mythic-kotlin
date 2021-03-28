package silentorb.mythic.lookinglass.drawing

import org.lwjgl.opengl.GL11
import silentorb.mythic.spatial.*
import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.glowing.debugMarkPass
import silentorb.mythic.glowing.drawMeshInstanced
import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.LightingMode
import silentorb.mythic.lookinglass.Renderer
import silentorb.mythic.lookinglass.TexturedBillboard
import silentorb.mythic.lookinglass.shading.*
import silentorb.mythic.scenery.Camera
import kotlin.math.roundToInt

fun renderBillboard(renderer: Renderer, camera: Camera, billboards: List<TexturedBillboard>, lightingMode: LightingMode) {
  val model = renderer.meshes["billboard"]!!
  val textures = renderer.textures
  val texture = textures[billboards.first().texture]
  if (texture == null)
    return

  debugMarkPass(true, "Particles") {
    globalState.blendEnabled = true
    globalState.blendFunction = Pair(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    val isTextureAnimated = texture.width != texture.height
    val textureScale = Vector2(texture.height.toFloat() / texture.width.toFloat(), 1f)

    val steps = (texture.width.toFloat() / texture.height.toFloat()).roundToInt().toFloat()

    val mesh = model.primitives.first().mesh
    val shader = renderer.getShader(mesh.vertexSchema, ShaderFeatureConfig(
        texture = true,
        instanced = true,
        animatedTexture = isTextureAnimated,
        lighting = lightingMode,
    ))
    shader.activate(ObjectShaderConfig(
        texture = texture,
        color = billboards.first().color,
        textureScale = textureScale
    ))

    renderer.uniformBuffers.instance.load(createInstanceBuffer { buffer ->
      for (billboard in billboards) {
        val transform = toMatrix(MutableMatrix()
            .billboardCylindrical(billboard.position, camera.position, Vector3(0f, 0f, 1f))
        )
            .scale(billboard.scale)
            .translate(-0.5f, -0.5f, 0f)
        writeMatrixToBuffer(buffer, transform)
        buffer.putVector4(billboard.color)
        buffer.putFloat(billboard.step.toFloat() / steps)
        buffer.putFloat(0f)
        padBuffer(buffer, 2)
      }
    })
    drawMeshInstanced(mesh, DrawMethod.triangleFan, billboards.size)
    globalState.blendEnabled = false
  }
}
