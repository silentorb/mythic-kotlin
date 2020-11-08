package silentorb.mythic.editing

typealias Id = String

data class Entry(
    val source: Id,
    val property: Id,
    val target: Any
)

typealias Graph = List<Entry>

data class GraphFile(
    val graph: List<List<Any>>
)

fun getTripleKeys(graph: Graph) =
    graph
        .map { it.source }
        .toSet()

inline fun <reified T> groupProperty(relationship: Id, graph: Graph): Map<Id, T> =
    graph.filter { it.property == relationship && it.target is T }
        .associate { it.source to it.target as T }


inline fun <reified T> groupProperty(relationship: Id): (Graph) -> Map<Id, T> = { graph ->
  groupProperty(relationship, graph)
}

fun getProperties(graph: Graph, key: String): List<Entry> =
    graph.filter { it.source == key }

fun <T> getPropertyValues(graph: Graph, key: String, property: Id): List<T> =
    graph
        .filter { it.source == key && it.property == property }
        .map { it.target as T }

inline fun <reified T> getValue(graph: Graph, key: String, property: Id): T? =
    graph.firstOrNull { it.source == key && it.property == property }?.target as T?

fun replaceValues(graph: Graph, additional: Graph): Graph =
    graph.filter { entry ->
      additional.none { it.source == entry.source && it.property == entry.property }
    }
        .plus(additional)
