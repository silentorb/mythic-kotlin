package silentorb.mythic.lookinglass.drawing

import silentorb.mythic.breeze.MultiAnimationPart
import silentorb.mythic.breeze.transformAnimatedSkeleton
import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.glowing.drawMesh
import silentorb.mythic.spatial.*
import silentorb.mythic.lookinglass.*
import silentorb.mythic.lookinglass.meshes.Primitive
import silentorb.mythic.lookinglass.shading.ObjectShaderConfig
import silentorb.mythic.lookinglass.shading.ShaderFeatureConfig
import silentorb.mythic.lookinglass.shading.populateBoneBuffer
import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.MeshName

fun renderElement(renderer: Renderer, primitive: Primitive, material: Material, transform: Matrix, isAnimated: Boolean) {
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
  drawMesh(primitive.mesh, DrawMethod.triangleFan)
}

fun armatureTransforms(armature: Armature, group: ElementGroup): List<Matrix> =
    if (group.animations.size == 1) {
      val animation = group.animations.first()
      transformAnimatedSkeleton(armature.bones, armature.animations[animation.animationId]!!, animation.timeOffset)
    } else {
      val animations = group.animations.map { animation ->
        MultiAnimationPart(
            animation = armature.animations[animation.animationId]!!,
            strength = animation.strength,
            timeOffset = animation.timeOffset
        )
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

fun renderMeshElement(renderer: Renderer, element: MeshElement, armature: Armature? = null, transforms: List<Matrix>? = null) {
  val meshes = renderer.meshes
  useMesh(meshes, element.mesh) { mesh ->
    for (primitive in mesh.primitives) {
      val transform = getElementTransform(element, primitive, transforms)
      val materal = element.material ?: primitive.material
      val isAnimated = armature != null && primitive.isAnimated
      renderElement(renderer, primitive, materal, transform, isAnimated)
    }
  }
}

fun renderElementGroup(renderer: Renderer, camera: Camera, group: ElementGroup) {
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
    renderBillboard(renderer, camera, group.billboards)
  }
}
