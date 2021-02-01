package silentorb.mythic.ent

typealias Key = String

data class GenericEntry<T>(
    val source: T,
    val property: Key,
    val target: Any
)

data class ContextKey(
    val context: Any,
    val name: Any
)

typealias Entry = GenericEntry<String>

typealias Graph = Set<Entry>
typealias GraphLibrary = Map<String, Graph>
typealias ListGraph = List<Entry>
typealias AnyEntry = GenericEntry<Any>
typealias AnyGraph = Collection<AnyEntry>
typealias GraphStores = Map<String, GraphStore>

typealias LooseGraph = Collection<Entry>

typealias GenericGraph<T> = Collection<GenericEntry<T>>

data class PropertyInfo(
    val manyToMany: Boolean = false,
    val type: Any? = null
)

typealias PropertySchema = Map<String, PropertyInfo>

fun newGraph(): Graph = setOf()

data class GraphFile(
    val graph: List<List<Any>>
)

fun getGraphKeys(graph: LooseGraph) =
    graph
        .map { it.source }
        .toSet()

inline fun <reified T> filterByPropertyStrict(graph: LooseGraph, relationship: Key): ListGraph =
    graph.filter { it.property == relationship && it.target is T }

fun filterByProperty(graph: LooseGraph, relationship: Key): ListGraph =
    graph.filter { it.property == relationship }

inline fun <reified T> filterByPropertyValue(graph: LooseGraph, relationship: Key, value: T): ListGraph =
    graph.filter { it.property == relationship && it.target == value }

inline fun <reified T> mapByProperty(graph: LooseGraph, relationship: Key): Map<Key, T> =
    graph
        .filter { it.property == relationship && it.target is T }
        .associate { it.source to it.target as T }

fun getProperties(graph: LooseGraph, key: String): Graph =
    graph
        .filter { it.source == key }
        .toSet()

fun <T> getPropertyValues(graph: LooseGraph, key: String, property: Key): List<T> =
    graph
        .filter { it.source == key && it.property == property }
        .map { it.target as T }

inline fun <reified T> getGraphValue(graph: LooseGraph, key: String, property: Key): T? =
    graph.firstOrNull { it.source == key && it.property == property }?.target as T?

inline fun <reified T> getGraphValues(graph: LooseGraph, key: String, property: Key): List<T> =
    graph.filter { it.source == key && it.property == property }.map { it.target as T }

fun replaceValues(graph: LooseGraph, additional: LooseGraph): Graph =
    graph.filter { entry ->
      additional.none { it.source == entry.source && it.property == entry.property }
    }
        .plus(additional)
        .toSet()

fun renameNode(graph: LooseGraph, previous: Key, next: Key): Graph =
    graph.map {
      when {
        it.source == previous -> it.copy(source = next)
        it.target == previous -> it.copy(target = next)
        else -> it
      }
    }
        .toSet()

fun uniqueNodeName(keys: Set<Key>, name: String): String {
  return if (!keys.contains(name))
    name
  else {
    val numberPattern = Regex("^\\d+$")
    val withoutTrailingNumbersMatch = Regex("^(.*?)\\d+$").find(name)
    val baseName = withoutTrailingNumbersMatch?.groups?.get(1)?.value ?: name
    val takenNumbers = keys
        .mapNotNull { id ->
          if (id.length > baseName.length && id.substring(0, baseName.length) == baseName) {
            val numberText = id.substring(baseName.length)
            if (numberText.matches(numberPattern))
              numberText.toInt()
            else
              null
          } else
            null
        }
    val number = (takenNumbers.maxOrNull() ?: 0) + 1
    "$baseName$number"
  }
}

fun renameNodes(duplicates: List<String>, allKeys: Set<String>, graph: LooseGraph): LooseGraph =
    if (duplicates.none())
      graph
    else {
      val next = duplicates.first()
      val newName = uniqueNodeName(allKeys, next)
      val nextGraph = renameNode(graph, next, newName)
      renameNodes(duplicates.drop(1), allKeys + newName, nextGraph)
    }

fun mergeGraphsWithRenaming(primary: LooseGraph, secondary: LooseGraph): LooseGraph {
  val primaryKeys = getGraphKeys(primary)
  val secondaryKeys = getGraphKeys(secondary)
  val duplicates = primaryKeys.intersect(secondaryKeys).toList()
  val allKeys = primaryKeys + secondaryKeys
//  val updatedSecondary = duplicates
//      .fold(secondary) { a, b -> renameNode(a, b, uniqueNodeName(allKeys, b)) }

  val updatedSecondary = renameNodes(duplicates, allKeys, secondary)
  return primary + updatedSecondary
}
typealias SerializationMethod = (Any) -> Any

data class Serialization(
    val load: SerializationMethod,
    val save: SerializationMethod,
)

typealias PropertiesSerialization = Map<String, Serialization>
