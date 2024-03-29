package silentorb.mythic.editing.lookinglass

import silentorb.mythic.drawing.flipViewport
import silentorb.mythic.editing.general.renderEditorGui
import silentorb.mythic.editing.main.*
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.ent.*
import silentorb.mythic.ent.scenery.*
import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.glowing.globalState
import silentorb.mythic.lookinglass.*
import silentorb.mythic.lookinglass.pipeline.applyFilters
import silentorb.mythic.lookinglass.pipeline.finishRender
import silentorb.mythic.lookinglass.pipeline.prepareRender
import silentorb.mythic.platforming.WindowInfo
import silentorb.mythic.scenery.*
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4

fun cameraRigToCamera(camera: CameraRig): Camera =
    Camera(
        projectionType = camera.projection,
        position = if (camera.projection == ProjectionType.perspective)
          camera.location
        else
          camera.location + (camera.orientation.transform(Vector3(-50f, 0f, 0f))),
        orientation = camera.orientation,
        angleOrZoom = if (camera.projection == ProjectionType.perspective)
          camera.angle
        else
          getOrthoZoom(camera),
    )

val elementsCache = singleValueCache<Pair<Graph, Set<String>>, List<ElementGroup>>()

fun prepareDynamicDepictions(depictions: EditorDepictionMap, graph: Graph, nodes: Collection<String>): List<ElementGroup> =
    nodes.mapNotNull { node ->
      val typeEntries = graph.filter { it.source == node && it.property == SceneProperties.type }
      val depictionKey = typeEntries.firstOrNull { depictions.keys.contains(it.target as Key) }?.target
      val depiction = depictions[depictionKey]
      if (depiction != null)
        depiction(graph, node)
      else
        null
    }

fun collisionElements(editor: Editor, graph: Graph, nodes: Collection<String>) =
    if (editor.persistentState.visibleGizmoTypes.contains(GizmoTypes.collision)) {
      val collisionNodes = filterByProperty(graph, SceneProperties.collisionGroups)
          .distinct()
          .filter { nodes.contains(it.source) }

      listOf(
          ElementGroup(
              meshes = collisionNodes.map {
                val shape = getShape(editor.enumerations.resourceInfo.meshShapes, graph, it.source)
                val transform = getAbsoluteNodeTransform(graph, it.source)
                if (shape != null) {
                  shapeToMeshes(editor.enumerations.resourceInfo.meshShapes, shape, transform)
                } else
                  listOf(
                      MeshElement(
                          mesh = "cube",
                          material = Material(color = Vector4(1f), shading = false, drawMethod = DrawMethod.lineLoop),
                          transform = transform
                      )
                  )
              }
                  .flatten()
          )
      )
    } else
      listOf()

fun nodesToElements(editor: Editor, graph: Graph, nodes: Collection<String>) =
    nodesToElements(editor.enumerations.resourceInfo, graph, nodes) +
        collisionElements(editor, graph, nodes) +
        prepareDynamicDepictions(editor.enumerations.depictions, graph, nodes)

fun sceneFromEditorGraph(meshShapes: Map<String, Shape>, editor: Editor, lightingConfig: LightingConfig, viewport: Key): Scene {
  val graph = getCachedGraph(editor)
  val camera = cameraRigToCamera(getEditorCamera(editor, viewport) ?: CameraRig())

  val initialElements = elementsCache(graph to editor.persistentState.visibleGizmoTypes) {
    val nodes = getElementNodes(graph)
    nodesToElements(editor, graph, nodes)
  }

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

  val (particleGroups, solidGroups) = elements
      .partition { group -> group.billboards.any() }

  val layers = listOf(
      SceneLayer(
          elements = solidGroups,
          depth = DepthMode.global
      ),
      SceneLayer(
          elements = particleGroups.sortedByDescending { it.billboards.first().position.distance(camera.position) },
          depth = DepthMode.global
      ),
  )
  val elementLights = layers.flatMap { layer ->
    layer.elements.flatMap { it.lights }
  }
  return Scene(
      camera = camera,
      lights = elementLights,
      lightingConfig = lightingConfig,
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
    val sceneRenderer = createSceneRenderer(renderer, windowInfo, scene, adjustedViewport).copy(
        options = renderer.options.copy(
            shadingMode = ShadingMode.forward,
        )
    )
    val previousSelectionQuery = editor.selectionQuery
    val expandedGraph = getCachedGraph(editor)
    val filters = prepareRender(sceneRenderer, scene)
    renderSceneLayers(sceneRenderer, sceneRenderer.camera, scene.layers)
    applyFilters(sceneRenderer, filters)
    finishRender(renderer, windowInfo)
    globalState.setFrameBuffer(0)

    val nextSelectionQuery = if (previousSelectionQuery != null) {
      val selectedObject = plumbPixelDepth(sceneRenderer, editor, previousSelectionQuery, expandedGraph)
      previousSelectionQuery.copy(
          response = SelectionQueryResponse(
              selectedObject = selectedObject
          )
      )
    } else
      null

    renderEditorSelection(editor, sceneRenderer)
    nextSelectionQuery
  } else
    null

  renderEditorGui()
  return selectionQuery
}
