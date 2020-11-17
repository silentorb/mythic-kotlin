package silentorb.mythic.editing.lookinglass

import silentorb.mythic.drawing.flipViewport
import silentorb.mythic.editing.*
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.ent.*
import silentorb.mythic.ent.scenery.getSceneTree
import silentorb.mythic.ent.scenery.nodesToElements
import silentorb.mythic.lookinglass.*
import silentorb.mythic.platforming.WindowInfo
import silentorb.mythic.scenery.*
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3

data class SerialElementData(
    val parents: SceneTree,
    val meshes: Map<Key, String>,
    val textures: Map<Key, String>,
    val translation: Map<Key, Vector3>,
    val rotation: Map<Key, Vector3>,
    val scale: Map<Key, Vector3>
)

fun newSerialElementData(graph: Graph): SerialElementData {
  val tree = getSceneTree(graph)
  return SerialElementData(
      parents = tree,
      meshes = mapByProperty(graph, SceneProperties.mesh),
      textures = mapByProperty(graph, SceneProperties.texture),
      translation = mapByProperty(graph, SceneProperties.translation),
      rotation = mapByProperty(graph, SceneProperties.rotation),
      scale = mapByProperty(graph, SceneProperties.scale),
  )
}

fun getTransform(data: SerialElementData, node: Key): Matrix {
  val translation = data.translation[node] ?: Vector3.zero
  val rotation = data.rotation[node] ?: Vector3.zero
  val scale = data.scale[node] ?: Vector3.unit
  val localTransform = Matrix.identity
      .translate(translation)
      .rotateZ(rotation.z)
      .rotateY(rotation.y)
      .rotateX(rotation.x)
      .scale(scale)

  val parent = data.parents[node]
  return if (parent != null)
    getTransform(data, parent) * localTransform
  else
    localTransform
}

fun cameraRigToCamera(camera: CameraRig): Camera =
    Camera(
        projectionType = camera.projection,
        position = camera.location,
        orientation = camera.orientation,
        angleOrZoom = if (camera.projection == ProjectionType.perspective)
          45f
        else
          getOrthoZoom(camera),
    )

fun sceneFromEditorGraph(meshShapes: Map<String, Shape>, editor: Editor, lightingConfig: LightingConfig, viewport: Key): GameScene {
  val graph = getActiveEditorGraph(editor) ?: newGraph()
  val camera = cameraRigToCamera(editor.state.cameras[viewport] ?: CameraRig())

  val layers = listOf(
      SceneLayer(
          elements = nodesToElements(meshShapes, editor.graphLibrary, graph),
          useDepth = true
      ),
  )
  val elementLights = layers.flatMap { layer ->
    layer.elements.flatMap { it.lights }
  }
  return GameScene(
      main = Scene(
          camera = camera,
          lights = elementLights,
          lightingConfig = lightingConfig
      ),
      layers = layers,
      filters = listOf()
  )
}

fun renderEditor(renderer: Renderer, windowInfo: WindowInfo, editor: Editor, lightingConfig: LightingConfig): SelectionQuery? {
  prepareRender(renderer, windowInfo)
  val viewport = editor.viewportBoundsMap.values.firstOrNull()
  val selectionQuery = if (viewport != null) {
    val adjustedViewport = flipViewport(windowInfo.dimensions.y, viewport)
    val scene = sceneFromEditorGraph(getMeshShapes(renderer), editor, lightingConfig, defaultViewportId)
    val sceneRenderer = createSceneRenderer(renderer, scene, adjustedViewport)
    val selectionQuery = if (editor.selectionQuery != null) {
      renderer.glow.operations.clearScreen()

      null
    } else
      null
    val filters = prepareRender(sceneRenderer, scene)
    renderSceneLayers(sceneRenderer, sceneRenderer.camera, scene.layers)
    renderEditorSelection(editor, sceneRenderer)
    applyFilters(sceneRenderer, filters)
    selectionQuery
  } else
    null

  renderEditorGui()
  finishRender(renderer, windowInfo)
  return selectionQuery
}
