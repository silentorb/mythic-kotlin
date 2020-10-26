package marloth.integration.editing

import silentorb.mythic.editing.*
import silentorb.mythic.lookinglass.*
import silentorb.mythic.lookinglass.Scene
import silentorb.mythic.scenery.LightingConfig

data class SerialElementData(
    val tree: SceneTree,
    val nodes: Set<Id>,
    val meshes: Map<Id, String>,
    val textures: Map<Id, String>
)

fun newSerialElementData(graph: Graph): SerialElementData {
  val tree = getSceneTree(graph)
  val nodes = getTripleKeys(graph)
      .plus(tree.values)

  return SerialElementData(
      tree = tree,
      nodes = nodes,
      meshes = groupProperty<String>(Properties.mesh)(graph),
      textures = groupProperty<String>(Properties.texture)(graph)
  )
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

    ElementGroup(
        meshes = listOf(
            MeshElement(
                mesh = mesh,
                material = material,
            )
        )
    )
  }
}

fun sceneFromEditorGraph(meshes: ModelMeshMap, editor: Editor, lightingConfig: LightingConfig): GameScene {
  val camera = newFlyThroughCamera { defaultFlyThroughState() }
  val graph = getActiveEditorGraph(editor) ?: listOf()
  val data = newSerialElementData(graph)

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
