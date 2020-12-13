package silentorb.mythic.ent.scenery

import silentorb.mythic.ent.*
import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.lookinglass.ElementGroup
import silentorb.mythic.lookinglass.Material
import silentorb.mythic.lookinglass.MeshElement
import silentorb.mythic.lookinglass.TextBillboard
import silentorb.mythic.scenery.Light
import silentorb.mythic.scenery.LightType
import silentorb.mythic.scenery.SceneProperties
import silentorb.mythic.scenery.Shape
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.typography.IndexedTextStyle

fun nodesToElements(meshesShapes: Map<String, Shape>, graphs: GraphLibrary, graph: Graph): List<ElementGroup> {
  val tree = getSceneTree(graph)
  val nodes = getGraphKeys(graph)
      .plus(tree.values)

  return nodes.flatMap { node -> nodeToElements(meshesShapes, graphs, graph, node) }
}

fun getGraphElementMaterial(graph: Graph, node: Key): Material? {
  val texture = getGraphValue<Key>(graph, node, SceneProperties.texture)
  return if (texture != null)
    Material(texture = texture, shading = true)
  else
    null
}

fun instanceToElements(meshesShapes: Map<String, Shape>, graphs: GraphLibrary, graph: Graph, node: Key, subGraph: Graph): List<ElementGroup> {
  val instanceTransform = getNodeTransform(graph, node)
  return nodesToElements(meshesShapes, graphs, subGraph)
      .map { group ->
        group.copy(
            meshes = group.meshes
                .map { meshElement ->
                  meshElement.copy(
                      // TODO: For some reason the matrix integration is needing to be backwards from the highlight pass
                      transform = instanceTransform * meshElement.transform
                  )
                },
            textBillboards = group.textBillboards
                .map { textBillboard ->
                  textBillboard.copy(
                      position = instanceTransform.translate(textBillboard.position).translation()
                  )
                }
        )
      }
}

fun nodeToElements(meshesShapes: Map<String, Shape>, graphs: GraphLibrary, graph: Graph, node: Key): List<ElementGroup> {
  val isSelected = false
  val mesh = getGraphValue<Key>(graph, node, SceneProperties.mesh)
  val type = getGraphValue<Key>(graph, node, SceneProperties.type)
  val text3d = getGraphValue<String>(graph, node, SceneProperties.text3d)
  val light = getGraphValue<String>(graph, node, SceneProperties.light)
  val collisionShape = if (isSelected)
    getGraphValue<String>(graph, node, SceneProperties.collisionShape)
  else
    null

  val subGraph = graphs[type]

  val instancedElements = if (subGraph != null && subGraph != graph)
    instanceToElements(meshesShapes, graphs, graph, node, subGraph)
  else
    listOf()

  val localElements = if (mesh == null && text3d == null && light == null && collisionShape == null)
    listOf()
  else {
    val inheritedProperties = if (subGraph != null && subGraph != graph) {
      val subgraphRoot = getGraphRoots(subGraph).first()
      subGraph
          .filter { it.source == subgraphRoot }
          .map { it.copy(source = node) }
          .toSet()
    } else
      setOf()

    val combinedGraph = inheritedProperties + graph
    val transform = getNodeTransform(combinedGraph, node)
    val meshElements = if (mesh != null) {
      val material = getGraphElementMaterial(combinedGraph, node)

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

    listOf(
        ElementGroup(
            textBillboards = textBillboards,
            meshes = meshElements + collisionMeshes,
            lights = lights,
        )
    )
  }
  return localElements + instancedElements
}
