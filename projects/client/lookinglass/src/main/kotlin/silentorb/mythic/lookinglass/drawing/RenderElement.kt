package silentorb.mythic.lookinglass.drawing

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import silentorb.mythic.breeze.MultiAnimationPart
import silentorb.mythic.breeze.transformAnimatedSkeleton
import silentorb.mythic.glowing.drawMesh
import silentorb.mythic.glowing.globalState
import silentorb.mythic.spatial.*
import silentorb.mythic.lookinglass.*
import silentorb.mythic.lookinglass.meshes.Primitive
import silentorb.mythic.lookinglass.shading.*
import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.MeshName

fun renderVolume(renderer: SceneRenderer, sampledModel: SampledModel, location: Vector3, transform: Matrix) {
  val camera = renderer.camera
  val orientationTransform = getRotationMatrix(transform)
  val distance = camera.position.distance(location)
  val levels = sampledModel.levels
  val lodRanges = listOf(
//      50f,
//      40f,
//      30f,
      20f,
      10f
  )
  val lodLevel = getLodLevel(lodRanges, levels, distance)
  val mesh = sampledModel.mesh
  val effect = renderer.getShader(mesh.vertexSchema, ShaderFeatureConfig(
      shading = true,
      pointSize = true
  ))

  val lodTransitionScalar: Float = if (lodLevel < lodRanges.size - 1) {
    val lower = lodRanges[lodLevel + 1]
    val upper = lodRanges[lodLevel]
    val gap = upper - lower
    val current = distance - lower
    current / gap
  } else
    1f

  val lodOpacityLevels = (0 until levels)
      .map { level ->
        if (level == lodLevel + 1)
          lodTransitionScalar
        else
          1f
      }

  val config = ObjectShaderConfig(
      nearPlaneHeight = getNearPlaneHeight(renderer.viewport, camera.angleOrZoom),
      normalTransform = orientationTransform,
      transform = transform,
      lodOpacityLevels = lodOpacityLevels
  )

  globalState.vertexProgramPointSizeEnabled = true

  val visibleSides = getVisibleSides((-camera.lookAt).transform(orientationTransform))

  val realCounts = sampledModel.partitioning
      .flatten()

  val (offsets) = realCounts
      .fold(Pair(listOf<Int>(), 0)) { (a, b), c ->
        val offset = b + c
        Pair(a + b, offset)
      }

  val counts = sampledModel.partitioning
      .mapIndexed { sideIndex, levelCounts ->
        if (visibleSides.contains(NormalSide.values()[sideIndex]))
          levelCounts.mapIndexed { level, count ->
            if (level <= lodLevel + 1)
              count
            else
              0
          }
        else
          levelCounts.map { 0 }
      }
      .flatten()
      .toIntArray()

  effect.activate(config)

//  val memoryCounts = BufferUtils.createIntBuffer(counts.size)
//  memoryCounts.put(counts)
//  memoryCounts.rewind()
//
//  val memoryOffsets = BufferUtils.createIntBuffer(offsets.size)
//  memoryOffsets.put(offsets.toIntArray())
//  memoryOffsets.rewind()

//  drawMesh(mesh, GL11.GL_POINTS)
//  globalState.blendFunction = Pair(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA)
  mesh.vertexBuffer.activate()
//  GL14.nglMultiDrawArrays(GL11.GL_POINTS, memAddress(memoryOffsets), memAddress(memoryCounts), counts.size)
  GL14.glMultiDrawArrays(GL11.GL_POINTS, offsets.toIntArray(), counts)
//  GL14.glMultiDrawArrays(GL11.GL_POINTS, intArrayOf(0), intArrayOf(mesh.count!!))
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
    if (mesh.sampledModel != null) {
      renderVolume(renderer, mesh.sampledModel, element.location, element.transform)
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
