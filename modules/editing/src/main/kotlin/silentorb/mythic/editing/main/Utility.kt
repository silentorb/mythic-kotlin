package silentorb.mythic.editing.main

import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.GraphLibrary
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.PropertiesSerialization
import silentorb.mythic.ent.scenery.ExpansionLibrary
import silentorb.mythic.scenery.ProjectionType
import silentorb.mythic.scenery.SceneProperties
import silentorb.mythic.spatial.*

fun getLatestSnapshot(editor: Editor): Snapshot? =
    editor.history[editor.persistentState.graph]?.pastAndPresent?.lastOrNull()

fun getPreviousSnapshot(editor: Editor): Snapshot? =
    editor.history[editor.persistentState.graph]?.pastAndPresent?.dropLast(1)?.lastOrNull()

fun getNextSnapshot(editor: Editor): Snapshot? =
    editor.history[editor.persistentState.graph]?.future?.firstOrNull()

fun getLatestGraph(editor: Editor): Graph? =
    getLatestSnapshot(editor)?.graph

fun getSceneState(editor: Editor): SceneState? =
    editor.persistentState.sceneStates[editor.persistentState.graph]

fun getViewports(editor: Editor) =
    getSceneState(editor)?.viewports

fun getRenderingMode(editor: Editor, viewport: Key = defaultViewportId): RenderingMode =
    editor.persistentState.renderingModes[viewport] ?: RenderingMode.lit

fun isManyToMany(editor: Editor, property: String): Boolean =
    editor.enumerations.schema[property]?.manyToMany == true

fun getEditorViewport(editor: Editor, viewport: Key?): ViewportState? {
  val viewports = getViewports(editor)
  return if (viewports != null)
    viewports[viewport]
  else
    null
}

fun getNodeSelection(editor: Editor): NodeSet =
    getSceneState(editor)?.nodeSelection ?: setOf()

fun getNodeSelection(state: EditorPersistentState): NodeSet =
    state.sceneStates[state.graph]?.nodeSelection ?: setOf()

fun getEditorCamera(editor: Editor, viewport: Key?): CameraRig? =
    getEditorViewport(editor, viewport)?.camera

fun getActiveEditorGraphKey(editor: Editor): String? =
    editor.persistentState.graph

fun getActiveEditorGraph(editor: Editor): Graph? =
    editor.staging ?: getLatestGraph(editor) ?: editor.graphLibrary[editor.persistentState.graph]

fun getTransformedActiveEditorGraph(editor: Editor): Graph? {
  val graph = getActiveEditorGraph(editor)
  return if (graph != null)
    editor.enumerations.graphTransform(graph)
  else
    graph
}

fun getActiveSceneState(editor: Editor): SceneState =
    editor.persistentState.sceneStates[editor.persistentState.graph] ?: SceneState()

fun getExpansionLibrary(editor: Editor) =
    ExpansionLibrary(
        graphs = editor.graphLibrary,
        schema = editor.enumerations.schema,
    )

fun defaultViewports() =
    mapOf(
        defaultViewportId to ViewportState(
            camera = CameraRig(location = Vector3(-10f, 0f, 0f)),
        )
    )

fun defaultEditorState() =
    EditorPersistentState()

fun axisMask(axis: Set<Axis>): List<Float> =
    (0 until 3).map { index ->
      if (axis.any { it.ordinal == index })
        1f
      else
        0f
    }

fun transformPoint(transform: Matrix, dimensions: Vector2, offset: Vector2): ScreenTransform = { point ->
  val sample = transformToScreenIncludingBehind(transform, point)
//  sample * Vector2(1f, -2f) * dimensions + offset
  Vector2(sample.x + 1f, 1f - sample.y) / 2f * dimensions + offset
}

fun sceneFileNameWithoutExtension(fileName: String): String =
    fileName.replace(sceneFileExtension, "")

fun isSceneFile(fileName: String): Boolean =
    fileName.substring(fileName.length - sceneFileExtension.length) == sceneFileExtension

fun getSceneFiles(editor: Editor): Sequence<FileItem> =
    editor.fileItems
        .values
        .asSequence()
        .filter { it.type == FileItemType.file && isSceneFile(it.name) }

// This function will recursively scans loaded graphs but does no graph loading
// so it does not recursively scan unloaded graphs.
// It's assumed that this function will be called multiple times between loading, where each
// pass includes more missing graphs until the unloaded set is loaded
tailrec fun getGraphDependencies(
    graphLibrary: GraphLibrary,
    graphs: Set<Key>,
    accumulator: Set<Key> = setOf()
): Set<Key> =
    if (graphs.none())
      accumulator
    else {
      val dependencies = graphs
          .flatMap { graphId ->
            val graph = graphLibrary[graphId]
            if (graph == null)
              listOf()
            else
              graph
                  .filter { it.property == SceneProperties.type }
                  .map { it.target as Key }
          }
          .toSet()

      val nextGraphs = dependencies - accumulator
      val nextAccumulator = accumulator + dependencies

      getGraphDependencies(graphLibrary, nextGraphs, nextAccumulator)
    }

fun createProjectionMatrix(camera: CameraRig, dimensions: Vector2i, distance: Float = 1000f): Matrix =
    if (camera.projection == ProjectionType.perspective)
      createPerspectiveMatrix(dimensions, camera.angle, 0.01f, distance)
    else
      createOrthographicMatrix(dimensions, getOrthoZoom(camera), 0.01f, distance)

fun extractPropertiesSerialization(properties: PropertyDefinitions): PropertiesSerialization =
    properties
        .filterValues { it.serialization != null }
        .mapValues { (_, property) ->
          property.serialization!!
        }

fun defaultGraphEditors(): GraphEditors = mapOf()
