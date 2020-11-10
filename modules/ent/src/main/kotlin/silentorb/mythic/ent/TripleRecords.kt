package silentorb.mythic.ent

typealias Key = String

data class Entry(
    val source: Key,
    val property: Key,
    val target: Any
)

typealias Graph = Set<Entry>
typealias GraphLibrary = Map<String, Graph>

typealias LooseGraph = Collection<Entry>

fun newGraph(): Graph = setOf()

data class GraphFile(
    val graph: List<List<Any>>
)

fun getTripleKeys(graph: LooseGraph) =
    graph
        .map { it.source }
        .toSet()

inline fun <reified T> groupProperty(relationship: Key, graph: LooseGraph): Map<Key, T> =
    graph.filter { it.property == relationship && it.target is T }
        .associate { it.source to it.target as T }

inline fun <reified T> groupProperty(relationship: Key): (Graph) -> Map<Key, T> = { graph ->
  groupProperty(relationship, graph)
}

fun getProperties(graph: LooseGraph, key: String): Graph =
    graph
        .filter { it.source == key }
        .toSet()

fun <T> getPropertyValues(graph: LooseGraph, key: String, property: Key): List<T> =
    graph
        .filter { it.source == key && it.property == property }
        .map { it.target as T }

inline fun <reified T> getValue(graph: LooseGraph, key: String, property: Key): T? =
    graph.firstOrNull { it.source == key && it.property == property }?.target as T?

fun replaceValues(graph: LooseGraph, additional: LooseGraph): Graph =
    graph.filter { entry ->
      additional.none { it.source == entry.source && it.property == entry.property }
    }
        .plus(additional)
        .toSet()

fun firstOrNullWithAttribute(graph: LooseGraph, attribute: String) =
    graph.firstOrNull { it.property == "attribute" && it.target == attribute }?.target as Key?
