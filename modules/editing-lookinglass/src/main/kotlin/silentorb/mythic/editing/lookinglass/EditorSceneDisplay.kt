package marloth.integration.editing

import silentorb.mythic.editing.*
import silentorb.mythic.lookinglass.*
import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.LightingConfig
import silentorb.mythic.scenery.ProjectionType
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3

data class SerialElementData(
    val parents: SceneTree,
    val nodes: Set<Id>,
    val meshes: Map<Id, String>,
    val textures: Map<Id, String>,
    val translation: Map<Id, Vector3>,
    val rotation: Map<Id, Vector3>,
    val scale: Map<Id, Vector3>
)

fun newSerialElementData(graph: Graph): SerialElementData {
  val tree = getSceneTree(graph)
  val nodes = getTripleKeys(graph)
      .plus(tree.values)

  return SerialElementData(
      parents = tree,
      nodes = nodes,
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

fun nodeToElement(data: SerialElementData, node: Id): ElementGroup? {
  val mesh = data.meshes[node]
  return if (mesh == null)
    null
  else {
    val texture = data.textures[node]
    val material = if (texture != null)
      Material(texture = texture, shading = true)
    else
      null

    val transform = getTransform(data, node)

    ElementGroup(
        meshes = listOf(
            MeshElement(
                mesh = mesh,
                material = material,
                transform = transform
            )
        )
    )
  }
}

fun cameraRigToCamera(rig: CameraRig): Camera =
    Camera(
        projectionType = ProjectionType.perspective,
        position = rig.location,
        orientation = rig.orientation,
        angleOrZoom = 45f,
    )

fun sceneFromEditorGraph(meshes: ModelMeshMap, editor: Editor, lightingConfig: LightingConfig, viewport: Id): GameScene {
  val graph = editor.graph ?: listOf()
  val data = newSerialElementData(graph)
  val camera = cameraRigToCamera(editor.state.cameras[viewport] ?: CameraRig())

  val layers = listOf(
      SceneLayer(
          elements = data.nodes.mapNotNull { node -> nodeToElement(data, node) },
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
