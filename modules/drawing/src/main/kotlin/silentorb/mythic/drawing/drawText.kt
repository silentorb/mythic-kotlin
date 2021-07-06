package silentorb.mythic.drawing

import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.glowing.VertexSchema
import silentorb.mythic.glowing.globalState
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.typography.TextConfiguration
import silentorb.mythic.typography.TextPackage
import silentorb.mythic.typography.prepareText
import silentorb.mythic.spatial.Vector2i
import org.lwjgl.opengl.GL11.*

fun getUnitScaling(dimensions: Vector2i) =
    if (dimensions.x < dimensions.y)
      Vector2(1f, dimensions.x.toFloat() / dimensions.y)
    else
      Vector2(dimensions.y.toFloat() / dimensions.x, 1f)

fun prepareTextMatrix(pixelsToScalar: Matrix, position: Vector2) =
    Matrix.identity
    .mul(pixelsToScalar)
    .translate(position.x, position.y, 0f)

fun renderText(config: TextConfiguration, effect: ColoredImageShader, textPackage: TextPackage, transform: Matrix) {
  effect.activate(transform, config.style.color, config.style.font.texture)

  globalState.blendEnabled = true
  globalState.blendFunction = Pair(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
  textPackage.mesh.draw(DrawMethod.triangleFan)
}

fun drawTextRaw(config: TextConfiguration, effect: ColoredImageShader, vertexSchema: VertexSchema, transform: Matrix) {
  val textPackage = prepareText(config, vertexSchema)
  if (textPackage != null) {
    renderText(config, effect, textPackage, transform)
    textPackage.mesh.dispose()
  }
}
