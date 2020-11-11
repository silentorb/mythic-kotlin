package silentorb.mythic.editing.lookinglass

import silentorb.mythic.editing.*
import silentorb.mythic.ent.*
import silentorb.mythic.ent.scenery.getSceneTree
import silentorb.mythic.ent.scenery.nodesToElements
import silentorb.mythic.lookinglass.*
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
      meshes = mapByProperty(graph, Properties.mesh),
      textures = mapByProperty(graph, Properties.texture),
      translation = mapByProperty(graph, Properties.translation),
      rotation = mapByProperty(graph, Properties.rotation),
      scale = mapByProperty(graph, Properties.scale),
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
          elements = nodesToElements(meshShapes, editor.state.nodeSelection, editor.graphLibrary, graph),
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
