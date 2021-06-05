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

fun nodesToElements(resourceInfo: ResourceInfo, graph: Graph, nodes: Collection<String>): List<ElementGroup> {
  return nodes.flatMap { node -> nodeToElements(resourceInfo, graph, node) }
}

fun nodesToElements(resourceInfo: ResourceInfo, graph: Graph): List<ElementGroup> {
  val nodes = getElementNodes(graph)
  return nodesToElements(resourceInfo, graph, nodes)
}

tailrec fun getInheritedTexture(graph: Graph, node: Key): String? {
  val texture = getNodeValue<Key>(graph, node, SceneProperties.texture)
  return if (texture != null)
    texture
  else {
    val parent = getNodeValue<Key>(graph, node, SceneProperties.parent)
    if (parent == null)
      null
    else
      getInheritedTexture(graph, parent)
  }
}

fun nodeToElements(resourceInfo: ResourceInfo, graph: Graph, node: Key): List<ElementGroup> {
  val isSelected = false
  val mesh = getNodeValue<Key>(graph, node, SceneProperties.mesh)
  val text3d = getNodeValue<String>(graph, node, SceneProperties.text3d)
  val light = getNodeValue<String>(graph, node, SceneProperties.light)
  val isBillboard = graph.contains(Entry(node, SceneProperties.type, SceneTypes.billboard))
  val texture = getInheritedTexture(graph, node)
  val collisionShape = if (isSelected)
    getNodeValue<String>(graph, node, SceneProperties.collisionShape)
  else
    null

  return if (mesh != null || text3d != null || light != null || collisionShape != null || isBillboard) {
    val transform = getAbsoluteNodeTransform(graph, node)
    val meshElements = if (mesh != null) {
      val material = if (texture != null)
        Material(
            texture = texture,
            shading = true,
            containsTransparency = textureContainsTransparency(resourceInfo, texture),
            doubleSided = nodeHasAttribute(graph, node, SceneTypes.doubleSided),
        )
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
      val meshShape = resourceInfo.meshShapes[mesh]
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
              range = getNodeValue<Float>(graph, node, SceneProperties.range) ?: 1f,
              offset = transform.translation(),
              direction = Vector3.unit,
              color = hexColorStringToVector4(getNodeValue<String>(graph, node, SceneProperties.rgba) ?: "#ffffffff"),
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
