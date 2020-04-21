package silentorb.mythic.lookinglass

import silentorb.mythic.drawing.getStaticCanvasDependencies
import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.glowing.activateTextures
import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.drawing.armatureTransforms
import silentorb.mythic.lookinglass.shading.Shaders
import silentorb.mythic.spatial.*

typealias ScreenFilter = (Shaders, Vector2) -> Unit

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

//fun renderArmatures(renderer: GameSceneRenderer) {
//  globalState.depthEnabled = false
//  renderer.scene.opaqueElementGroups.filter { it.armature != null }
//      .forEach { group ->
//        val armature = renderer.renderer.renderer.armatures[group.armature]!!
//        drawSkeleton(renderer.renderer, armature, armatureTransforms(armature, group), group.meshes.first().transform)
//      }
//  globalState.depthEnabled = true
//}

fun getDisplayConfigFilters(config: DisplayConfig): List<ScreenFilter> =
    if (config.depthOfField)
      listOf<ScreenFilter>(
          { shaders, scale -> shaders.depthOfField.activate(scale) }
      )
    else
      listOf()

fun prepareRender(renderer: SceneRenderer, filters: List<ScreenFilter>) {
  globalState.depthEnabled = true
  val glow = renderer.renderer.glow
  if (filters.any()) {
    val offscreenBuffer = renderer.renderer.offscreenBuffers.first()
    val dimensions = Vector2i(offscreenBuffer.colorTexture.width, offscreenBuffer.colorTexture.height)
    glow.state.setFrameBuffer(offscreenBuffer.framebuffer.id)
    glow.state.viewport = Vector4i(0, 0, renderer.viewport.z, renderer.viewport.w)
  } else {
    glow.state.viewport = renderer.viewport
  }
//    glow.operations.clearScreen()
}

fun finishRender(renderer: SceneRenderer, filters: List<ScreenFilter>) {
  globalState.cullFaces = false
  globalState.viewport = renderer.viewport

  for (filter in filters.dropLast(1)) {
//      globalState.setFrameBuffer(renderer.renderer.offscreenBuffers.first().framebuffer.id)
    applyFrameBufferTexture(renderer, filter)
  }

  globalState.setFrameBuffer(0)

  if (filters.any()) {
    applyFrameBufferTexture(renderer, filters.last())
  }
}

fun applyFrameBufferTexture(renderer: SceneRenderer, filter: ScreenFilter) {
  val canvasDependencies = getStaticCanvasDependencies()
  val offscreenBuffer = renderer.renderer.offscreenBuffers.first()
  val config = renderer.renderer.config
  val scale = Vector2(config.width.toFloat(), config.height.toFloat()) / renderer.viewport.zw.toVector2()
  filter(renderer.renderer.shaders, scale)
  activateTextures(listOf(offscreenBuffer.colorTexture, offscreenBuffer.depthTexture!!))
  canvasDependencies.meshes.image.draw(DrawMethod.triangleFan)
}
