package silentorb.mythic.lookinglass.drawing

import silentorb.mythic.breeze.MultiAnimationPart
import silentorb.mythic.breeze.transformAnimatedSkeleton
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.glowing.drawMesh
import silentorb.mythic.lookinglass.*
import silentorb.mythic.lookinglass.meshes.Primitive
import silentorb.mythic.lookinglass.LightingMode
import silentorb.mythic.lookinglass.shading.ObjectShaderConfig
import silentorb.mythic.lookinglass.shading.ShaderFeatureConfig
import silentorb.mythic.lookinglass.shading.populateBoneBuffer
import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.MeshName
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Pi
import silentorb.mythic.spatial.getRotationMatrix

fun renderElement(renderer: SceneRenderer, primitive: Primitive, material: Material, transform: Matrix,
                  isAnimated: Boolean,
                  lightingMode: LightingMode) {
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
      texture = texture != null && primitive.mesh.vertexSchema.attributes.any { it.name == "uv" },
      lighting = lightingMode,
      colored = primitive.material.coloredVertices || material.coloredVertices
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

fun getElementTransform(elementTransform: Matrix, primitive: Primitive, transforms: List<Matrix>?): Matrix {
  if (primitive.name == "pumpkin-head") {
    val k = 0
  }
  return if (primitive.transform != null)
    elementTransform * primitive.transform
  else if (primitive.parentBone != null && transforms != null)
    elementTransform * transforms[primitive.parentBone] * Matrix.identity.rotateX(-Pi / 2f)
  else
    elementTransform
}

fun getElementTransform(element: MeshElement, primitive: Primitive, transforms: List<Matrix>?): Matrix =
    getElementTransform(element.transform, primitive, transforms)

private fun useMesh(meshes: ModelMeshMap, MeshName: MeshName, action: (ModelMesh) -> Unit) {
  val mesh = meshes[MeshName]
  if (mesh == null) {
    val debugMeshNotFound = 0
  } else {
    action(mesh)
  }
}

fun renderMeshElement(renderer: SceneRenderer, mesh: String, transform: Matrix, material: Material?,
                      lightingMode: LightingMode,
                      armature: Armature? = null,
                      transforms: List<Matrix>? = null) {
  val meshes = renderer.meshes
  useMesh(meshes, mesh) { mesh ->
    if (mesh.sampledModel == null) {
      for (primitive in mesh.primitives) {
        val transform = getElementTransform(transform, primitive, transforms)
        val materal = material ?: primitive.material
        val isAnimated = armature != null && primitive.isAnimated
        renderElement(renderer, primitive, materal, transform, isAnimated, lightingMode)
      }
    }
  }
}

fun renderMeshElement(renderer: SceneRenderer, element: MeshElement, lightingMode: LightingMode, armature: Armature? = null,
                      transforms: List<Matrix>? = null) {
  renderMeshElement(renderer, element.mesh, element.transform, element.material, lightingMode, armature, transforms)
}

fun renderElementGroup(renderer: SceneRenderer, camera: Camera, group: ElementGroup, lightingMode: LightingMode) {
  val armature = renderer.armatures[group.armature]
  val transforms = if (armature != null)
    armatureTransforms(armature, group)
  else
    null

  if (transforms != null) {
    populateBoneBuffer(renderer.uniformBuffers.bone, armature!!.transforms, transforms)
  }

  for (element in group.meshes) {
    renderMeshElement(renderer, element, lightingMode, armature, transforms)
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
            renderElement(renderer, primitive, materal, transform, false, lightingMode)
          }
        }
      }
    }
  }

  if (group.billboards.any()) {
    renderBillboard(renderer.renderer, camera, group.billboards, lightingMode)
  }

  for (text in group.textBillboards) {
    drawText(renderer, text.content, text.position, text.style, text.depthOffset)
  }
}

fun renderElementGroups(renderer: SceneRenderer, camera: Camera, groups: Collection<ElementGroup>, lightingMode: LightingMode) {
  for (group in groups) {
    renderElementGroup(renderer, camera, group, lightingMode)
  }
}

fun renderElementGroups(renderer: SceneRenderer, groups: Collection<ElementGroup>, lightingMode: LightingMode) =
    renderElementGroups(renderer, renderer.camera, groups, lightingMode)
