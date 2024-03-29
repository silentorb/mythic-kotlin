package silentorb.mythic.lookinglass.drawing

import silentorb.mythic.breeze.MultiAnimationPart
import silentorb.mythic.breeze.transformAnimatedSkeleton
import silentorb.mythic.glowing.drawMesh
import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.*
import silentorb.mythic.lookinglass.meshes.Primitive
import silentorb.mythic.lookinglass.ShadingMode
import silentorb.mythic.lookinglass.shading.ObjectShaderConfig
import silentorb.mythic.lookinglass.shading.ShaderFeatureConfig
import silentorb.mythic.lookinglass.shading.populateBoneBuffer
import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.MeshName
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Pi
import silentorb.mythic.spatial.getRotationMatrix
import silentorb.mythic.spatial.toVector2

fun renderElement(renderer: SceneRenderer, primitive: Primitive, material: Material, transform: Matrix,
                  isAnimated: Boolean,
                  shadingMode: ShadingMode) {
  val orientationTransform = getRotationMatrix(transform)
  val texture = renderer.textures[material.texture]
  val mergedShadingMode = if (material.shading)
    shadingMode
  else
    ShadingMode.none

  if (material.texture != null && texture == null) {
    val debugMissingTexture = 0
  }

  val deferredBlending = mergedShadingMode == ShadingMode.deferred && material.containsTransparency

  val config = ObjectShaderConfig(
      transform,
      color = material.color,
      glow = material.glow,
      normalTransform = orientationTransform,
      texture = texture,
      screenDimensions = if (deferredBlending) renderer.windowInfo.dimensions.toVector2() else null,
  )

  val effect = renderer.getShader(primitive.mesh.vertexSchema, ShaderFeatureConfig(
      skeleton = isAnimated,
      texture = texture != null && primitive.mesh.vertexSchema.attributes.any { it.name == "uv" },
      shading = mergedShadingMode,
      colored = primitive.material.coloredVertices || material.coloredVertices,
      deferredBlending = deferredBlending,
  ))

  effect.activate(config)
  globalState.cullFaces = !material.doubleSided
  drawMesh(primitive.mesh, material.drawMethod)
}

fun armatureTransforms(armature: Armature, group: ElementGroup): List<Matrix>? =
    if (group.animations.size == 1) {
      val elementAnimation = group.animations.first()
      val animation = armature.animations[elementAnimation.animationId] ?: armature.animations.values.firstOrNull()
      if (animation != null)
        transformAnimatedSkeleton(armature.bones, animation, elementAnimation.timeOffset)
      else
        null
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
      if (animations.any())
        transformAnimatedSkeleton(armature.bones, animations)
      else
        null
    }

fun getElementTransform(elementTransform: Matrix, primitive: Primitive, transforms: List<Matrix>?): Matrix {
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
                      shadingMode: ShadingMode,
                      armature: Armature? = null,
                      transforms: List<Matrix>? = null) {
  val meshes = renderer.meshes
  useMesh(meshes, mesh) { mesh ->
    if (mesh.sampledModel == null) {
      for (primitive in mesh.primitives) {
        val transform = getElementTransform(transform, primitive, transforms)
        val materal = material ?: primitive.material
        val isAnimated = armature != null && primitive.isAnimated
        renderElement(renderer, primitive, materal, transform, isAnimated, shadingMode)
      }
    }
  }
}

fun renderMeshElement(renderer: SceneRenderer, element: MeshElement, shadingMode: ShadingMode, armature: Armature? = null,
                      transforms: List<Matrix>? = null) {
  renderMeshElement(renderer, element.mesh, element.transform, element.material, shadingMode, armature, transforms)
}

fun renderElementGroup(renderer: SceneRenderer, camera: Camera, group: ElementGroup, shadingMode: ShadingMode) {
  val armature = renderer.armatures[group.armature]
  val transforms = if (armature != null)
    armatureTransforms(armature, group)
  else
    null

  if (transforms != null) {
    populateBoneBuffer(renderer.uniformBuffers.bone, armature!!.transforms, transforms)
  }

  for (element in group.meshes) {
    renderMeshElement(renderer, element, shadingMode, armature, transforms)
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
            renderElement(renderer, primitive, materal, transform, false, shadingMode)
          }
        }
      }
    }
  }

  if (group.billboards.any()) {
    renderBillboard(renderer.renderer, camera, group.billboards, shadingMode)
  }

  for (text in group.textBillboards) {
    drawText(renderer, text.content, text.position, text.style, text.depthOffset)
  }
}

fun renderElementGroups(renderer: SceneRenderer, camera: Camera, groups: Collection<ElementGroup>, shadingMode: ShadingMode) {
  for (group in groups) {
    renderElementGroup(renderer, camera, group, shadingMode)
  }
}

fun renderElementGroups(renderer: SceneRenderer, groups: Collection<ElementGroup>, shadingMode: ShadingMode) =
    renderElementGroups(renderer, renderer.camera, groups, shadingMode)
