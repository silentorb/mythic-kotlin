package silentorb.mythic.ent.scenery

import silentorb.mythic.ent.*
import silentorb.mythic.scenery.Box
import silentorb.mythic.scenery.SceneProperties
import silentorb.mythic.scenery.Shape
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4

fun getTranslationRotationMatrix(graph: Graph, node: Key): Matrix {
  val translation = getGraphValue<Vector3>(graph, node, SceneProperties.translation) ?: Vector3.zero
  val rotation = getGraphValue<Vector3>(graph, node, SceneProperties.rotation) ?: Vector3.zero
  return Matrix.identity
      .translate(translation)
      .rotateZ(rotation.z)
      .rotateY(rotation.y)
      .rotateX(rotation.x)
}

fun getNodeScale(graph: Graph, node: Key): Vector3 =
    getGraphValue<Vector3>(graph, node, SceneProperties.scale) ?: Vector3.unit

fun getNodeTransform(graph: Graph, node: Key): Matrix {
  val scale = getNodeScale(graph, node)
  val localTransform = getTranslationRotationMatrix(graph, node)
      .scale(scale)

  val parent = getGraphValue<Key>(graph, node, SceneProperties.parent)
  return if (parent != null)
    getNodeTransform(graph, parent) * localTransform
  else
    localTransform
}

// Still applies scaling of parent objects, just not the local scale
fun getNodeTransformWithoutScale(graph: Graph, node: Key): Matrix {
  val localTransform = getTranslationRotationMatrix(graph, node)

  val parent = getGraphValue<Key>(graph, node, SceneProperties.parent)
  return if (parent != null)
    getNodeTransform(graph, parent) * localTransform
  else
    localTransform
}

tailrec fun gatherChildren(graph: Graph, nodes: Set<Key>, accumulator: Set<Key> = setOf()): Set<Key> {
  val next = nodes
      .flatMap { node ->
        graph.filter { it.property == SceneProperties.parent && it.target == node }
      }
      .map { it.source }
      .toSet()

  val nextAccumulator = accumulator + nodes
  return if (next.none())
    nextAccumulator
  else
    gatherChildren(graph, next, nextAccumulator)
}

fun renameNode(graph: Graph, previous: Key, next: Key): Graph =
    graph.map {
      if (it.source == previous)
        it.copy(source = next)
      else if (it.property == SceneProperties.parent && it.target == previous)
        it.copy(target = next)
      else
        it
    }
        .toSet()

fun transposeNamespace(graph: Graph, parent: Key): Graph {
  val keys = getGraphKeys(graph)
  return keys.fold(graph) { a, b ->
    renameNode(a, b, "$parent.$b")
  }
}

fun getGraphRoots(graph: LooseGraph): Set<Key> =
    getGraphKeys(graph)
        .filter { key -> graph.none { it.source == key && it.property == SceneProperties.parent } }
        .toSet()

fun expandInstance(graphs: GraphLibrary, key: Key, target: Key): LooseGraph {
  val definition = graphs[target]
  return if (definition == null)
    setOf()
  else {
    val transposed = transposeNamespace(definition, key)
    val expanded = expandInstances(graphs, transposed)
    val roots = getGraphRoots(transposed)
    assert(roots.any())
    expanded + roots.map { root -> Entry(root, SceneProperties.parent, key) }
  }
}

fun expandInstances(graphs: GraphLibrary, instances: LooseGraph, accumulator: Graph): Graph =
    if (instances.none())
      accumulator
    else {
      val (key, _, target) = instances.first()
      val additions = expandInstance(graphs, key, target as Key)
      expandInstances(graphs, instances.drop(1), accumulator + additions)
    }

fun expandInstances(graphs: GraphLibrary, graph: Graph): Graph {
  val instances = filterByProperty<Key>(graph, SceneProperties.instance)
      .filter { graphs.containsKey(it.target) }

  return expandInstances(graphs, instances, graph)
}

fun expandInstances(graphs: GraphLibrary, name: String): Graph =
    expandInstances(graphs, graphs[name]!!)

fun getShape(meshShapeMap: Map<Key, Shape>, graph: Graph, node: Key): Shape? {
  val shapeType = getGraphValue<Key>(graph, node, SceneProperties.collisionShape)
  return if (shapeType == null)
    null
  else {
    val mesh = getGraphValue<Key>(graph, node, SceneProperties.mesh)
    val meshBounds = meshShapeMap[mesh]
    return meshBounds ?: Box(Vector3.unit / 2f)
  }
}

fun hexColorStringToVector4(value: String): Vector4 {
  assert(value.length == 9)
  val red = value.substring(1, 3).toInt(16).toFloat()
  val green = value.substring(3, 5).toInt(16).toFloat()
  val blue = value.substring(5, 7).toInt(16).toFloat()
  val alpha = value.substring(7, 9).toInt(16).toFloat()
  return Vector4(red, green, blue, alpha) / 255f
}

//fun vector4toHexColorString(value: Vector4): String {
//  val temp = (value * 255f).toVector4i()
//  val red = temp.x shl 32
//  val green = temp.y shl 16
//  val blue = temp.z shl 8
//  val alpha = temp.w shl 0
//  return red + green + blue + alpha
//}

fun arrayToHexColorString(values: FloatArray): String {
  val red = Integer.toHexString((values[0] * 255f).toInt())
  val green = Integer.toHexString((values[1] * 255f).toInt())
  val blue = Integer.toHexString((values[2] * 255f).toInt())
  val alpha = Integer.toHexString((values[3] * 255f).toInt())
  return "#$red$green$blue$alpha"
}

fun getSceneTree(graph: Graph): Map<Key, Key> =
    mapByProperty(graph, SceneProperties.parent)
