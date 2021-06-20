package silentorb.mythic.ent.scenery

import silentorb.mythic.ent.*
import silentorb.mythic.lookinglass.*
import silentorb.mythic.scenery.*
import silentorb.mythic.spatial.Matrix
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

fun getNodeLight(graph: Graph, node: Key, transform: Matrix): Light? {
  val light = getNodeValue<String>(graph, node, SceneProperties.light)
  return if (light != null)
    Light(
        type = LightType.valueOf(light),
        range = getNodeValue<Float>(graph, node, SceneProperties.range) ?: 1f,
        offset = transform.translation(),
        direction = Vector3.unit,
        color = getNodeColor(graph, node) ?: Vector4(1f),
    )
  else
    null
}

fun getNodeMaterial(resourceInfo: ResourceInfo, graph: Graph, node: Key): Material? {
  val texture = getInheritedTexture(graph, node)
  val color = getNodeColor(graph, node)
  return if (texture != null || color != null)
    Material(
        texture = texture,
        color = color,
        shading = true,
        containsTransparency = if (texture != null) textureContainsTransparency(resourceInfo, texture) else false,
        doubleSided = nodeHasAttribute(graph, node, SceneTypes.doubleSided),
    )
  else
    null
}

fun nodeToElements(resourceInfo: ResourceInfo, graph: Graph, node: Key): List<ElementGroup> {
  val mesh = getNodeValue<Key>(graph, node, SceneProperties.mesh)
  val text3d = getNodeValue<String>(graph, node, SceneProperties.text3d)
  val transform = getAbsoluteNodeTransform(graph, node)
  val light = getNodeLight(graph, node, transform)
  val isBillboard = graph.contains(Entry(node, SceneProperties.type, SceneTypes.billboard))
  val material = getNodeMaterial(resourceInfo, graph, node)

  return if (mesh != null || text3d != null || light != null || isBillboard) {
    val meshElements = if (mesh != null) {
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
      listOf(
          TextBillboard(text3d, transform.translation(), IndexedTextStyle(
              0,
              22,
              color = Vector4(1f)
          ))
      )
    else
      listOf()

    val lights = listOfNotNull(light)

    val texture = material?.texture

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
            meshes = meshElements,
            lights = lights,
        )
    )
  } else
    listOf()
}
