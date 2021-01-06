package silentorb.mythic.editing.lookinglass

import silentorb.mythic.drawing.flipViewport
import silentorb.mythic.editing.*
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.ent.*
import silentorb.mythic.ent.scenery.*
import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.lookinglass.*
import silentorb.mythic.platforming.WindowInfo
import silentorb.mythic.scenery.*
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4

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

fun cameraRigToCamera(camera: CameraRig): Camera =
    Camera(
        projectionType = camera.projection,
        position = if (camera.projection == ProjectionType.perspective)
          camera.location
        else
          camera.location + (camera.orientation.transform(Vector3(-50f, 0f, 0f))),
        orientation = camera.orientation,
        angleOrZoom = if (camera.projection == ProjectionType.perspective)
          70f
        else
          getOrthoZoom(camera),
    )

val elementsCache = singleValueCache<Graph, List<ElementGroup>>()

fun sceneFromEditorGraph(meshShapes: Map<String, Shape>, editor: Editor, lightingConfig: LightingConfig, viewport: Key): GameScene {
  val graph = getCachedGraph(editor)
  val camera = cameraRigToCamera(getEditorCamera(editor, viewport) ?: CameraRig())

  val initialElements = elementsCache(graph) { nodesToElements(meshShapes, editor.graphLibrary, graph) }

  val elements = if (getRenderingMode(editor) == RenderingMode.wireframe) {
    val wireframeMaterial = Material(
        shading = false,
        color = Vector4(0.9f),
        drawMethod = DrawMethod.lineLoop,
    )
    initialElements +
        initialElements
            .map { elementGroup ->
              elementGroup.copy(
                  meshes = elementGroup.meshes
                      .map { meshElement ->
                        meshElement.copy(
                            material = wireframeMaterial,
                        )
                      }
              )
            }
  } else
    initialElements

  val layers = listOf(
      SceneLayer(
          elements = elements,
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
    val sceneRenderer = createSceneRenderer(renderer, windowInfo, scene, adjustedViewport)
    val previousSelectionQuery = editor.selectionQuery
    val graph = getCachedGraph(editor)
    val filters = prepareRender(sceneRenderer, scene)
    val nextSelectionQuery = if (previousSelectionQuery != null) {
      val selectedObject = plumbPixelDepth(sceneRenderer, editor, previousSelectionQuery, graph)
      previousSelectionQuery.copy(
          response = SelectionQueryResponse(
              selectedObject = selectedObject
          )
      )
    } else
      null

    renderSceneLayers(sceneRenderer, sceneRenderer.camera, scene.layers)
    renderEditorSelection(editor, sceneRenderer)
    applyFilters(sceneRenderer, filters)
    nextSelectionQuery
  } else
    null

  renderEditorGui()
  finishRender(renderer, windowInfo)
  return selectionQuery
}
