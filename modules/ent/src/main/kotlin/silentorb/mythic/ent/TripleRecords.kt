package silentorb.mythic.ent

typealias Key = String

data class Entry(
    val source: Key,
    val property: Key,
    val target: Any
)

typealias Graph = List<Entry>
typealias GraphLibrary = Map<String, Graph>

data class GraphFile(
    val graph: List<List<Any>>
)

fun getTripleKeys(graph: Graph) =
    graph
        .map { it.source }
        .toSet()

inline fun <reified T> groupProperty(relationship: Key, graph: Graph): Map<Key, T> =
    graph.filter { it.property == relationship && it.target is T }
        .associate { it.source to it.target as T }


inline fun <reified T> groupProperty(relationship: Key): (Graph) -> Map<Key, T> = { graph ->
  groupProperty(relationship, graph)
}

fun getProperties(graph: Graph, key: String): List<Entry> =
    graph.filter { it.source == key }

fun <T> getPropertyValues(graph: Graph, key: String, property: Key): List<T> =
    graph
        .filter { it.source == key && it.property == property }
        .map { it.target as T }

inline fun <reified T> getValue(graph: Graph, key: String, property: Key): T? =
    graph.firstOrNull { it.source == key && it.property == property }?.target as T?

fun replaceValues(graph: Graph, additional: Graph): Graph =
    graph.filter { entry ->
      additional.none { it.source == entry.source && it.property == entry.property }
    }
        .plus(additional)
