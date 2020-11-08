package marloth.integration.editing

import silentorb.mythic.editing.*
import silentorb.mythic.lookinglass.*
import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.LightingConfig
import silentorb.mythic.scenery.ProjectionType
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.typography.IndexedTextStyle

data class SerialElementData(
    val parents: SceneTree,
    val meshes: Map<Id, String>,
    val textures: Map<Id, String>,
    val translation: Map<Id, Vector3>,
    val rotation: Map<Id, Vector3>,
    val scale: Map<Id, Vector3>
)

fun newSerialElementData(graph: Graph): SerialElementData {
  val tree = getSceneTree(graph)
  return SerialElementData(
      parents = tree,
      meshes = groupProperty<String>(Properties.mesh)(graph),
      textures = groupProperty<String>(Properties.texture)(graph),
      translation = groupProperty<Vector3>(Properties.translation)(graph),
      rotation = groupProperty<Vector3>(Properties.rotation)(graph),
      scale = groupProperty<Vector3>(Properties.scale)(graph),
  )
}

fun getTransform(data: SerialElementData, node: Id): Matrix {
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


fun nodesToElements(graphs: GraphLibrary, graph: Graph): List<ElementGroup> {
  val tree = getSceneTree(graph)
  val nodes = getTripleKeys(graph)
      .plus(tree.values)

  return nodes.flatMap { node -> nodeToElements(graphs, graph, node) }
}

fun nodeToElements(graphs: GraphLibrary, graph: Graph, node: Id): List<ElementGroup> {
  val mesh = getValue<Id>(graph, node, Properties.mesh)
  val type = getValue<Id>(graph, node, Properties.type)
  val text3d = getValue<String>(graph, node, Properties.text3d)

  return if (type != null) {
    val subGraph = graphs[type]
    if (subGraph == null || subGraph == graph)
      listOf()
    else {
      val instanceTransform = getTransform(graph, node)
      nodesToElements(graphs, subGraph)
          .map { group ->
            group.copy(
                meshes = group.meshes.map { meshElement ->
                  meshElement.copy(
                      transform = instanceTransform * meshElement.transform
                  )
                }
            )
          }
    }
  } else if (mesh == null && text3d == null)
    listOf()
  else {
    val transform = getTransform(graph, node)
    val meshes = if (mesh != null) {
      val texture = getValue<Id>(graph, node, Properties.texture)
      val material = if (texture != null)
        Material(texture = texture, shading = true)
      else
        null

      listOf(
          MeshElement(
              mesh = mesh,
              material = material,
              transform = transform
          )
      )
    } else
      listOf()

    val textBillboards = if (text3d != null)
      listOf(TextBillboard(text3d, transform.translation(), IndexedTextStyle(
          0,
          22,
          color = Vector4(1f)
      )))
    else
      listOf()

    listOf(
        ElementGroup(
            textBillboards = textBillboards,
            meshes = meshes,
        )
    )
  }
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

fun sceneFromEditorGraph(meshes: ModelMeshMap, editor: Editor, lightingConfig: LightingConfig, viewport: Id): GameScene {
  val graph = getActiveEditorGraph(editor) ?: listOf()
//  val data = newSerialElementData(graph)
  val camera = cameraRigToCamera(editor.state.cameras[viewport] ?: CameraRig())

  val layers = listOf(
      SceneLayer(
          elements = nodesToElements(editor.graphLibrary, graph),
          useDepth = true
      ),
  )
  return GameScene(
      main = Scene(
          camera = camera,
          lights = listOf(),
          lightingConfig = lightingConfig
      ),
      layers = layers,
      filters = listOf()
  )
}
