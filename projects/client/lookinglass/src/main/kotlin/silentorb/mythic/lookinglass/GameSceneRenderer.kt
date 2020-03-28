package silentorb.mythic.lookinglass

import silentorb.mythic.drawing.getStaticCanvasDependencies
import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.glowing.activateTextures
import silentorb.mythic.glowing.globalState
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector4i
import silentorb.mythic.lookinglass.drawing.armatureTransforms
import silentorb.mythic.lookinglass.shading.Shaders

typealias ScreenFilter = (Shaders) -> Unit

fun drawSkeleton(renderer: SceneRenderer, armature: Armature, transforms: List<Matrix>, modelTransform: Matrix) {
  armature.bones
      .filter { it.parent != -1 }
      .forEach { bone ->
        val a = Vector3().transform(modelTransform * transforms[bone.index])
        val b = Vector3().transform(modelTransform * transforms[bone.parent])
        val white = Vector4(1f)
        renderer.drawLine(a, b, white, 2f)
      }
}

fun renderArmatures(renderer: GameSceneRenderer) {
  globalState.depthEnabled = false
  renderer.scene.opaqueElementGroups.filter { it.armature != null }
      .forEach { group ->
        val armature = renderer.renderer.renderer.armatures[group.armature]!!
        drawSkeleton(renderer.renderer, armature, armatureTransforms(armature, group), group.meshes.first().transform)
      }
  globalState.depthEnabled = true
}

fun getDisplayConfigFilters(config: DisplayConfig): List<ScreenFilter> =
    if (config.depthOfField)
      listOf<ScreenFilter>(
          { it.depthOfField.activate() }
      )
    else
      listOf()

data class GameSceneRenderer(
    val scene: GameScene,
    val renderer: SceneRenderer
) {

  fun prepareRender(filters: List<ScreenFilter>) {
    globalState.depthEnabled = true
    val glow = renderer.renderer.glow
    if (filters.any()) {
      val offscreenBuffer = renderer.renderer.offscreenBuffers.first()
      val dimensions = Vector2i(offscreenBuffer.colorTexture.width, offscreenBuffer.colorTexture.height)
      glow.state.setFrameBuffer(offscreenBuffer.framebuffer.id)
      glow.state.viewport = Vector4i(0, 0, dimensions.x, dimensions.y)
    }
    glow.state.viewport = renderer.viewport
//    glow.operations.clearScreen()
  }

  fun finishRender(dimensions: Vector2i, filters: List<ScreenFilter>) {
    globalState.cullFaces = false
    globalState.viewport = Vector4i(0, 0, dimensions.x, dimensions.y)

    for (filter in filters.dropLast(1)) {
//      globalState.setFrameBuffer(renderer.renderer.offscreenBuffers.first().framebuffer.id)
      applyFrameBufferTexture(filter)
    }

    globalState.setFrameBuffer(0)

    if (filters.any()) {
      applyFrameBufferTexture(filters.last())
    }
  }

  fun applyFrameBufferTexture(filter: ScreenFilter) {
    val canvasDependencies = getStaticCanvasDependencies()
    val offscreenBuffer = renderer.renderer.offscreenBuffers.first()
    filter(renderer.renderer.shaders)
    activateTextures(listOf(offscreenBuffer.colorTexture, offscreenBuffer.depthTexture!!))
    canvasDependencies.meshes.image.draw(DrawMethod.triangleFan)
  }
}
