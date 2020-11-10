package silentorb.mythic.editing.lookinglass

import silentorb.mythic.editing.*
import silentorb.mythic.ent.*
import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.lookinglass.*
import silentorb.mythic.scenery.*
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.typography.IndexedTextStyle

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
      meshes = groupProperty<String>(Properties.mesh)(graph),
      textures = groupProperty<String>(Properties.texture)(graph),
      translation = groupProperty<Vector3>(Properties.translation)(graph),
      rotation = groupProperty<Vector3>(Properties.rotation)(graph),
      scale = groupProperty<Vector3>(Properties.scale)(graph),
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

fun nodesToElements(meshes: ModelMeshMap, selection: NodeSelection, graphs: GraphLibrary, graph: Graph): List<ElementGroup> {
  val tree = getSceneTree(graph)
  val nodes = getTripleKeys(graph)
      .plus(tree.values)

  return nodes.flatMap { node -> nodeToElements(meshes, selection, graphs, graph, node) }
}

fun nodeToElements(meshes: ModelMeshMap, selection: NodeSelection, graphs: GraphLibrary, graph: Graph, node: Key): List<ElementGroup> {
  val isSelected = selection.contains(node)
  val mesh = getValue<Key>(graph, node, Properties.mesh)
  val type = getValue<Key>(graph, node, Properties.type)
  val text3d = getValue<String>(graph, node, Properties.text3d)
  val light = getValue<String>(graph, node, Properties.light)
  val collisionShape = if (isSelected)
    getValue<String>(graph, node, Properties.collisionShape)
  else
    null

  return if (type != null) {
    val subGraph = graphs[type]
    if (subGraph == null || subGraph == graph)
      listOf()
    else {
      val instanceTransform = silentorb.mythic.ent.spatial.getTransform(graph, node)
      nodesToElements(meshes, selection, graphs, subGraph)
          .map { group ->
            group.copy(
                meshes = group.meshes.map { meshElement ->
                  meshElement.copy(
                      transform = instanceTransform * meshElement.transform
                  )
                },
                textBillboards = group.textBillboards.map { textBillboard ->
                  textBillboard.copy(
                      position = instanceTransform.translation() + textBillboard.position
                  )
                }
            )
          }
    }
  } else if (mesh == null && text3d == null && light == null && collisionShape == null)
    listOf()
  else {
    val transform = silentorb.mythic.ent.spatial.getTransform(graph, node)
    val meshElements = if (mesh != null) {
      val texture = getValue<Key>(graph, node, Properties.texture)
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

    val collisionMeshes = if (collisionShape != null) {
      val meshShape = meshes[mesh]?.bounds
      val collisionTransform = if (meshShape == null)
        transform
      else
        transform.scale(Vector3(meshShape.x / 2f, meshShape.y / 2f, meshShape.height / 2f))

      listOf(
          MeshElement(
              mesh = "cube",
              material = Material(color = Vector4(1f), shading = false, drawMethod = DrawMethod.lineLoop),
              transform = collisionTransform
          )
      )
    } else
      listOf()

    val textBillboards = if (text3d != null)
      listOf(
          TextBillboard(text3d, transform.translation(), IndexedTextStyle(
              0,
              22,
              color = Vector4(1f)
          ))
      )
    else
      listOf()

    val lights = if (light != null)
      listOf(
          Light(
              type = LightType.valueOf(light),
              range = getValue<Float>(graph, node, Properties.range) ?: 1f,
              offset = transform.translation(),
              direction = Vector3.unit,
              color = hexColorStringToVector4(getValue<String>(graph, node, Properties.rgba) ?: "#ffffffff"),
          )
      )
    else
      listOf()

    listOf(
        ElementGroup(
            textBillboards = textBillboards,
            meshes = meshElements + collisionMeshes,
            lights = lights,
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

fun sceneFromEditorGraph(meshes: ModelMeshMap, editor: Editor, lightingConfig: LightingConfig, viewport: Key): GameScene {
  val graph = getActiveEditorGraph(editor) ?: newGraph()
//  val data = newSerialElementData(graph)
  val camera = cameraRigToCamera(editor.state.cameras[viewport] ?: CameraRig())

  val layers = listOf(
      SceneLayer(
          elements = nodesToElements(meshes, editor.state.nodeSelection, editor.graphLibrary, graph),
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
