package silentorb.mythic.lookinglass

import silentorb.mythic.drawing.drawTextRaw
import silentorb.mythic.drawing.getStaticCanvasDependencies
import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.glowing.OffscreenBuffer
import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.shading.*
import silentorb.mythic.lookinglass.texturing.DynamicTextureLibrary
import silentorb.mythic.platforming.WindowInfo
import silentorb.mythic.scenery.ArmatureName
import silentorb.mythic.scenery.Camera
import silentorb.mythic.spatial.*
import silentorb.mythic.typography.*

data class SceneRenderer(
    val viewport: Vector4i,
    val renderer: Renderer,
    val camera: Camera,
    val cameraEffectsData: CameraEffectsData,
    val windowInfo: WindowInfo,
    val offscreenRendering: Boolean,
    val scene: Scene,
    val options: DisplayOptions,
) {

  val effects: Shaders
    get() = renderer.shaders

  val flat: GeneralPerspectiveShader
    get() = renderer.getShader(renderer.vertexSchemas.flat, ShaderFeatureConfig())

  fun drawLine(start: Vector3, end: Vector3, color: Vector4, thickness: Float = 1f) {
    globalState.lineThickness = thickness
    renderer.dynamicMesh.load(listOf(start.x, start.y, start.z, end.x, end.y, end.z))

    flat.activate(ObjectShaderConfig(transform = Matrix.identity, color = color))
    renderer.dynamicMesh.draw(DrawMethod.lines)
  }

  fun drawLines(values: List<Float>, color: Vector4, thickness: Float = 1f) {
    globalState.lineThickness = thickness
    renderer.dynamicMesh.load(values)

    flat.activate(ObjectShaderConfig(color = color))
    renderer.dynamicMesh.draw(DrawMethod.lines)
  }

  fun drawPoint(position: Vector3, color: Vector4, size: Float = 1f) {
    globalState.pointSize = size
    renderer.dynamicMesh.load(listOf(position.x, position.y, position.z))
    flat.activate(ObjectShaderConfig(color = color))
    renderer.dynamicMesh.draw(DrawMethod.points)
  }

  fun drawSolidFace(vertices: List<Vector3>, color: Vector4) {
    renderer.dynamicMesh.load(vertices.flatMap { listOf(it.x, it.y, it.z) })

    flat.activate(ObjectShaderConfig(color = color))
    renderer.dynamicMesh.draw(DrawMethod.triangleFan)
  }

  fun drawOutlinedFace(vertices: List<Vector3>, color: Vector4, thickness: Float = 1f) {
    globalState.lineThickness = thickness
    renderer.dynamicMesh.load(vertices.flatMap { listOf(it.x, it.y, it.z) })

    flat.activate(ObjectShaderConfig(color = color))
    renderer.dynamicMesh.draw(DrawMethod.lines)
  }

  fun drawCircle(position: Vector3, radius: Float, method: DrawMethod) {
    val resources = getStaticCanvasDependencies()
    val mesh = resources.meshes.circle
    val transform = toMatrix(MutableMatrix()
        .billboardSpherical(position, camera.position, Vector3(0f, 0f, 1f)))
        .scale(radius)
    flat.activate(ObjectShaderConfig(
        transform = transform,
        color = Vector4(0.5f, 0.5f, 0f, 0.4f)
    ))

    mesh.draw(method)
  }

  val armatures: Map<ArmatureName, Armature>
    get() = renderer.armatures

  val meshes: ModelMeshMap
    get() = renderer.meshes

  val offscreenBuffer: OffscreenBuffer
    get() = renderer.offscreenBuffer

  val uniformBuffers: UniformBuffers
    get() = renderer.uniformBuffers

  val textures: DynamicTextureLibrary
    get() = renderer.textures

  val getShader: ShaderGetter
    get() = renderer.getShader
}

fun drawText(renderer: SceneRenderer, content: String, position: Vector3, style: TextStyle, depthOffset: Float = 0f) {
  val dimensions = Vector2i(renderer.viewport.z, renderer.viewport.w)
  val point = transformToScreen(renderer.cameraEffectsData.transform, position)
  if (point != null) {
    val config = TextConfiguration(content, point, style)
    val pixelsToScalar = Matrix.identity.scale(1f / dimensions.x, 1f / dimensions.y, 1f)
//    val transform = prepareTextMatrix(pixelsToScalar, pos2)
    val rawPoint = renderer.cameraEffectsData.transform * Vector4(position.x, position.y, position.z, 1f)

    // I don't understand why the pixelsToScalar part is needed.
    // It looks like I'm just multiplying by the dimensions and then dividing by the dimensions, but somehow it makes a difference.
    val transform = Matrix.identity
        .mul(pixelsToScalar)
        .translate((point.x + 1f) / 2f * dimensions.x, (1f - point.y) / 2f * dimensions.y, (rawPoint.z + depthOffset) / rawPoint.w)

    drawTextRaw(
        config,
        renderer.renderer.drawing.coloredImage,
        renderer.renderer.vertexSchemas.drawing.image,
        transform
    )
  }
}

fun drawText(renderer: SceneRenderer, content: String, position: Vector3, style: IndexedTextStyle, depthOffset: Float = 0f) =
    drawText(renderer, content, position, resolveTextStyle(renderer.renderer.fonts, style), depthOffset)

fun drawSolidFace(renderer: Renderer, vertices: List<Float>, color: Vector4) {
  renderer.dynamicMesh.load(vertices)
  val flat = renderer.getShader(renderer.vertexSchemas.flat, ShaderFeatureConfig())
  flat.activate(ObjectShaderConfig(color = color))
  renderer.dynamicMesh.draw(DrawMethod.triangleFan)
}

fun drawSolidFaceVertex(renderer: Renderer, vertices: List<Vector3>, color: Vector4) {
  return drawSolidFace(renderer, vertices.flatMap { listOf(it.x, it.y, it.z) }, color)
}
