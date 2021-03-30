package silentorb.mythic.lookinglass

import silentorb.mythic.drawing.getStaticCanvasDependencies
import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.glowing.activateTextures
import silentorb.mythic.glowing.globalState
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

fun getDisplayConfigFilters(options: DisplayOptions): List<ScreenFilter> =
    if (options.depthOfField)
      listOf<ScreenFilter>(
          { shaders, scale -> shaders.depthOfField.activate(scale) }
      )
    else
      listOf()
