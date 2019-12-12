package mythic.drawing

import mythic.glowing.DrawMethod
import mythic.glowing.VertexSchema
import mythic.glowing.globalState
import mythic.spatial.Matrix
import mythic.spatial.Vector2
import mythic.typography.TextConfiguration
import mythic.typography.TextPackage
import mythic.typography.prepareText
import org.joml.Vector2i
import org.lwjgl.opengl.GL11.*

fun getUnitScaling(dimensions: Vector2i) =
    if (dimensions.x < dimensions.y)
      Vector2(1f, dimensions.x.toFloat() / dimensions.y)
    else
      Vector2(dimensions.y.toFloat() / dimensions.x, 1f)

fun prepareTextMatrix(pixelsToScalar: Matrix, position: Vector2) =
    Matrix()
    .mul(pixelsToScalar)
    .translate(position.x, position.y, 0f)

fun renderText(config: TextConfiguration, effect: ColoredImageShader, textPackage: TextPackage, transform: Matrix) {
//  val position = config.position
//  val scale = config.size * 0.1f
//  val scale = 1f
//      .scale(scale, scale, 1f)

  effect.activate(transform, config.style.color, config.style.font.texture)

  globalState.blendEnabled = true
  globalState.blendFunction = Pair(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
//  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
//  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
  textPackage.mesh.draw(DrawMethod.triangleFan)
}

fun drawTextRaw(config: TextConfiguration, effect: ColoredImageShader, vertexSchema: VertexSchema, transform: Matrix) {
  val textPackage = prepareText(config, vertexSchema)
  if (textPackage != null) {
//    val transform = prepareTextMatrix(transform, config.position)
    renderText(config, effect, textPackage, transform)
    textPackage.mesh.dispose()
  }
}
