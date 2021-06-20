package silentorb.mythic.ent.scenery

import silentorb.mythic.ent.*
import silentorb.mythic.scenery.*
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.maxScalar
import kotlin.math.max

fun getLocalNodeTranslation(graph: Graph, node: Key): Vector3 =
    getNodeValue<Vector3>(graph, node, SceneProperties.translation) ?: Vector3.zero

fun getTranslationRotationMatrix(graph: Graph, node: Key): Matrix {
  val translation = getLocalNodeTranslation(graph, node)
  val rotation = getNodeValue<Vector3>(graph, node, SceneProperties.rotation) ?: Vector3.zero
  return Matrix.identity
      .translate(translation)
      .rotateZ(rotation.z)
      .rotateY(rotation.y)
      .rotateX(rotation.x)
}

fun getNodeScale(graph: Graph, node: Key): Vector3 =
    getNodeValue<Vector3>(graph, node, SceneProperties.scale) ?: Vector3.unit

fun getLocalNodeTransform(graph: Graph, node: Key): Matrix {
  val scale = getNodeScale(graph, node)
  return getTranslationRotationMatrix(graph, node)
      .scale(scale)
}

fun getAbsoluteNodeTransform(graph: Graph, node: Key): Matrix {
  val localTransform = getLocalNodeTransform(graph, node)
  val parent = getNodeValue<Key>(graph, node, SceneProperties.parent)
  return if (parent != null)
    getAbsoluteNodeTransform(graph, parent) * localTransform
  else
    localTransform
}

// Still applies scaling of parent objects, just not the local scale
fun getNodeTransformWithoutScale(graph: Graph, node: Key): Matrix {
  val localTransform = getTranslationRotationMatrix(graph, node)

  val parent = getNodeValue<Key>(graph, node, SceneProperties.parent)
  return if (parent != null)
    getAbsoluteNodeTransform(graph, parent) * localTransform
  else
    localTransform
}

fun toSpatialEntries(matrix: Matrix, node: Key): AnyGraph {
  val translation = matrix.translation()
  val rotation = matrix.rotation()
  val scale = matrix.getScale()
  return setOfNotNull(
      if (translation != Vector3.zero) AnyEntry(node, SceneProperties.translation, translation) else null,
      if (rotation != Vector3.zero) AnyEntry(node, SceneProperties.rotation, rotation) else null,
      if (scale != Vector3.unit) AnyEntry(node, SceneProperties.scale, scale) else null,
  )
}

tailrec fun getNodeChildren(graph: Graph, nodes: Collection<Key>, accumulator: Set<Key> = setOf()): Collection<Key> {
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
    getNodeChildren(graph, next, nextAccumulator)
}

fun getNodeChildren(graph: Graph, node: Key): Collection<Key> =
    getNodeChildren(graph, listOf(node)) - node

fun withNodeChildren(graph: Graph, node: Key): Collection<Key> =
    getNodeChildren(graph, listOf(node))

fun removeNodesAndChildren(graph: Graph, nodes: Collection<Key>): Graph {
  val withChildren = getNodeChildren(graph, nodes)
  return graph
      .filter { !withChildren.contains(it.source) }
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

fun getGraphRoots(graph: Graph): Set<Key> =
    getGraphKeys(graph)
        .filter { key -> graph.none { it.source == key && it.property == SceneProperties.parent } }
        .toSet()

fun getShape(meshShapes: Map<Key, Shape>, graph: Graph, node: Key): Shape? {
  val shapeType = getNodeValue<Key>(graph, node, SceneProperties.collisionShape)
  return if (shapeType == null)
    null
  else {
    val mesh = getNodeValue<Key>(graph, node, SceneProperties.mesh)
    return when (shapeType) {
      "cylinder" -> {
        val radius = getNodeValue<Float>(graph, node, SceneProperties.radius)
        val height = getNodeValue<Float>(graph, node, SceneProperties.height)
        if (radius != null && height != null)
          Cylinder(radius, height)
        else {
          val scale = getNodeScale(graph, node)
          Cylinder(max(scale.x, scale.y), scale.z / 2f)
        }
      }
      "sphere" -> {
        val radius = getNodeValue<Float>(graph, node, SceneProperties.radius)
        if (radius != null)
          Sphere(radius)
        else {
          val scale = getNodeScale(graph, node)
          Sphere(maxScalar(scale) / 2f)
        }
      }
      "composite" -> {
        val children = getNodeChildren(graph, node)
            .mapNotNull { child ->
              val childShape = getShape(meshShapes, graph, child)
              if (childShape == null)
                null
              else {
                val localTransform = getLocalNodeTransform(graph, child)
                if (localTransform != Matrix.identity)
                  ShapeTransform(localTransform, childShape)
                else
                  childShape
              }
            }

        CompositeShape(shapes = children)
      }
      else -> meshShapes[mesh] ?: Box(Vector3.unit / 2f)
    }
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

fun getNodeColor(graph: Graph, node: Key): Vector4? {
  val color = getNodeValue<String>(graph, node, SceneProperties.rgba)
  return if (color != null)
    hexColorStringToVector4(color)
  else
    null
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

fun <T> groupNodesWithCertainAttributes(graph: Graph, attributes: Collection<String>): Map<String, Set<T>> =
    graph
        .filter { it.property == SceneProperties.type && attributes.contains(it.target) }
        .groupBy { it.source }
        .mapValues { (_, entries) -> entries.map { it.target as T }.toSet() }

fun getNodesWithAttribute(graph: Graph, attribute: String): List<Key> =
    graph
        .filter { it.property == SceneProperties.type && it.target == attribute }
        .map { it.source }

fun getNodeAttributes(graph: Graph, node: String): List<Key> =
    graph
        .filter { it.property == SceneProperties.type && it.source == node }
        .map { it.target as Key }

fun nodeHasAttribute(graph: Graph, node: Key, attribute: String): Boolean =
    graph.any { it.source == node && it.property == SceneProperties.type && it.target == attribute }

fun nodeHasAttribute(graph: Graph, attribute: String): (Key) -> Boolean = { node ->
  nodeHasAttribute(graph, node, attribute)
}

fun anyNodeHasAttribute(graph: Graph, attribute: String): Boolean =
    graph.any { it.property == SceneProperties.type && it.target == attribute }
