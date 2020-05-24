package silentorb.mythic.lookinglass.drawing

import org.lwjgl.opengl.GL11
import silentorb.mythic.breeze.MultiAnimationPart
import silentorb.mythic.breeze.transformAnimatedSkeleton
import silentorb.mythic.glowing.PrimitiveType
import silentorb.mythic.glowing.drawMesh
import silentorb.mythic.glowing.globalState
import silentorb.mythic.spatial.*
import silentorb.mythic.lookinglass.*
import silentorb.mythic.lookinglass.meshes.Lod
import silentorb.mythic.lookinglass.meshes.Primitive
import silentorb.mythic.lookinglass.shading.ObjectShaderConfig
import silentorb.mythic.lookinglass.shading.ShaderFeatureConfig
import silentorb.mythic.lookinglass.shading.populateBoneBuffer
import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.MeshName

fun renderVolume(renderer: SceneRenderer, lod: Lod, location: Vector3, transform: Matrix) {
  val orientationTransform = getRotationMatrix(transform)
  val distance = renderer.camera.position.distance(location)
  val mesh = lod.entries.last { it.key <= distance }.value
  val effect = renderer.getShader(mesh.vertexSchema, ShaderFeatureConfig(
      shading = true,
      pointSize = true
  ))

  val config = ObjectShaderConfig(
      nearPlaneHeight = getNearPlaneHeight(renderer.viewport, renderer.camera.angleOrZoom),
      normalTransform = orientationTransform,
      transform = transform
  )

  globalState.vertexProgramPointSizeEnabled = true
//  globalState.pointSprite = true
  effect.activate(config)
  drawMesh(mesh, GL11.GL_POINTS)
}

fun renderElement(renderer: SceneRenderer, primitive: Primitive, material: Material, transform: Matrix, isAnimated: Boolean) {
  val orientationTransform = getRotationMatrix(transform)
  val texture = renderer.textures[material.texture]

  if (material.texture != null && texture == null) {
    val debugMissingTexture = 0
  }

  val config = ObjectShaderConfig(
      transform,
      color = material.color,
      glow = material.glow,
      normalTransform = orientationTransform,
      texture = texture
  )
  val effect = renderer.getShader(primitive.mesh.vertexSchema, ShaderFeatureConfig(
      skeleton = isAnimated,
      texture = texture != null,
      shading = material.shading
  ))

  effect.activate(config)
  drawMesh(primitive.mesh, material.drawMethod)
}

fun armatureTransforms(armature: Armature, group: ElementGroup): List<Matrix> =
    if (group.animations.size == 1) {
      val animation = group.animations.first()
      transformAnimatedSkeleton(armature.bones, armature.animations[animation.animationId]!!, animation.timeOffset)
    } else {
      val animations = group.animations.mapNotNull { animation ->
        val definition = armature.animations[animation.animationId]
        if (definition != null)
          MultiAnimationPart(
              animation = definition,
              strength = animation.strength,
              timeOffset = animation.timeOffset
          )
        else
          null
      }
      transformAnimatedSkeleton(armature.bones, animations)
    }

fun getElementTransform(element: MeshElement, primitive: Primitive, transforms: List<Matrix>?): Matrix {
  if (primitive.name == "pumpkin-head") {
    val k = 0
  }
  return if (primitive.transform != null)
    element.transform * primitive.transform
  else if (primitive.parentBone != null && transforms != null)
    element.transform * transforms[primitive.parentBone] * Matrix.identity.rotateX(-Pi / 2f)
  else
    element.transform
}

private fun useMesh(meshes: ModelMeshMap, MeshName: MeshName, action: (ModelMesh) -> Unit) {
  val mesh = meshes[MeshName]
  if (mesh == null) {
    val debugMeshNotFound = 0
  } else {
    action(mesh)
  }
}

fun renderMeshElement(renderer: SceneRenderer, element: MeshElement, armature: Armature? = null, transforms: List<Matrix>? = null) {
  val meshes = renderer.meshes
  useMesh(meshes, element.mesh) { mesh ->
    if (mesh.particleLod.any()) {
      renderVolume(renderer, mesh.particleLod, element.location, element.transform)
    } else {
      for (primitive in mesh.primitives) {
        val transform = getElementTransform(element, primitive, transforms)
        val materal = element.material ?: primitive.material
        val isAnimated = armature != null && primitive.isAnimated
        renderElement(renderer, primitive, materal, transform, isAnimated)
      }
    }
  }
}

fun renderElementGroup(renderer: SceneRenderer, camera: Camera, group: ElementGroup) {
  val armature = renderer.armatures[group.armature]
  val transforms = if (armature != null)
    armatureTransforms(armature, group)
  else
    null

  if (transforms != null) {
    populateBoneBuffer(renderer.uniformBuffers.bone, armature!!.transforms, transforms)
  }

  for (element in group.meshes) {
    renderMeshElement(renderer, element, armature, transforms)
  }

  if (armature != null) {
    for ((socketName, element) in group.attachments) {
      val bone = armature.sockets[socketName]
      if (bone == null) {
        val debugMissingBone = 0
      } else {
        val meshes = renderer.meshes
        useMesh(meshes, element.mesh) { mesh ->
          for (primitive in mesh.primitives) {
            val transform = element.transform * transforms!![bone]
            val materal = element.material ?: primitive.material
            renderElement(renderer, primitive, materal, transform, false)
          }
        }
      }
    }
  }

  if (group.billboards.any()) {
    renderBillboard(renderer.renderer, camera, group.billboards)
  }

  for (text in group.textBillboards) {
    drawText(renderer, text.content, text.position, text.style, text.depthOffset)
  }
}
