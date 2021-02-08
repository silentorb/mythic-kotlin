package silentorb.mythic.ent.scenery

import silentorb.mythic.ent.*
import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.lookinglass.*
import silentorb.mythic.scenery.*
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.typography.IndexedTextStyle

fun getElementNodes(graph: Graph): Set<String> {
  val tree = getSceneTree(graph)
  return getGraphKeys(graph)
      .plus(tree.values)
}

fun nodesToElements(meshShapes: Map<String, Shape>, graph: Graph, nodes: Collection<String>): List<ElementGroup> {
  return nodes.flatMap { node -> nodeToElements(meshShapes, graph, node) }
}

fun nodesToElements(meshShapes: Map<String, Shape>, graph: Graph): List<ElementGroup> {
  val nodes = getElementNodes(graph)
  return nodesToElements(meshShapes, graph, nodes)
}

fun getGraphElementMaterial(texture: String?): Material? =
    if (texture != null)
      Material(texture = texture, shading = true)
    else
      null

fun nodeToElements(meshesShapes: Map<String, Shape>, graph: Graph, node: Key): List<ElementGroup> {
  val isSelected = false
  val mesh = getGraphValue<Key>(graph, node, SceneProperties.mesh)
  val text3d = getGraphValue<String>(graph, node, SceneProperties.text3d)
  val light = getGraphValue<String>(graph, node, SceneProperties.light)
  val isBillboard = graph.contains(Entry(node, SceneProperties.type, SceneTypes.billboard))
  val texture = getGraphValue<Key>(graph, node, SceneProperties.texture)
  val collisionShape = if (isSelected)
    getGraphValue<String>(graph, node, SceneProperties.collisionShape)
  else
    null

  return if (mesh != null || text3d != null || light != null || collisionShape != null || isBillboard) {
    val transform = getNodeTransform(graph, node)
    val meshElements = if (mesh != null) {
      val material = getGraphElementMaterial(texture)

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
      val meshShape = meshesShapes[mesh]
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
              range = getGraphValue<Float>(graph, node, SceneProperties.range) ?: 1f,
              offset = transform.translation(),
              direction = Vector3.unit,
              color = hexColorStringToVector4(getGraphValue<String>(graph, node, SceneProperties.rgba) ?: "#ffffffff"),
          )
      )
    else
      listOf()

    val billboards = if (isBillboard && texture != null)
      listOf(
      TexturedBillboard(
          texture = texture,
          position = transform.translation(),
          color = Vector4(1f),
          scale = transform.getScale().x,
      )
      )
    else
      listOf()

    listOf(
        ElementGroup(
            billboards = billboards,
            textBillboards = textBillboards,
            meshes = meshElements + collisionMeshes,
            lights = lights,
        )
    )
  } else
    listOf()
}
