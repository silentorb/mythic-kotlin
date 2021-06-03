package silentorb.mythic.ent.scenery

import silentorb.mythic.ent.*
import silentorb.mythic.scenery.SceneProperties

fun expandInstance(library: ExpansionLibrary, node: Key, target: Key): Graph {
  val definition = library.graphs[target]
  return if (definition == null)
    setOf()
  else {
    val expanded1 = expandGraphInstances(library, definition)
    val roots = getGraphRoots(expanded1)
    val (rootEntries, withoutRoots) = expanded1
        .partition { roots.contains(it.source) }

    val inheritedProperties = rootEntries.map { it.copy(source = node) }
    val transposed = transposeNamespace(withoutRoots.toSet(), node)
    val expanded2 = expandGraphInstances(library, transposed)
    val newRoots = filterByPropertyValue(transposed, SceneProperties.parent, roots.first())
    inheritedProperties + expanded2 - newRoots + newRoots.map { root -> root.copy(target = node) }
  }
}

fun expandGraphInstances(library: ExpansionLibrary, instances: Graph, accumulator: Graph): Graph =
    if (instances.none())
      accumulator
    else {
      val node = instances.first().source
      val instanceTypesEntries = instances
          .filter { it.source == node }

      val instanceTypes = instanceTypesEntries.map { it.target as Key }

      val initial: Graph = listOf()
      val additions = instanceTypes
          .fold(initial) { a, b ->
            a + expandInstance(library, node, b)
          }

      // Make sure accumulator is added to additions and not the other way around
      // so that local properties override inherited properties
      val graph = additions.toSet() + accumulator
      val nextInstances = instances - instanceTypesEntries
      expandGraphInstances(library, nextInstances, graph)
    }

fun expand(library: ExpansionLibrary, accumulator: Graph, node: Key, type: Key, expander: Expander): Graph {
  val result = expander(library, accumulator, node)
  return if (result == null)
    accumulator
  else
    result - Entry(node, SceneProperties.type, type)
}

fun expandExpansions(library: ExpansionLibrary, instances: Graph, accumulator: Graph): Graph =
    if (instances.none())
      accumulator
    else {
      val node = instances.first().source
      val instanceTypesEntries = instances
          .filter { it.source == node }

      val instanceTypes = instanceTypesEntries.map { it.target as Key }

      val expander = library.expanders.entries
          .firstOrNull { (expander, _) ->
            instanceTypes.contains(expander)
          }

      val expanded = if (expander == null)
        accumulator
      else
        expand(library, accumulator, node, expander.key, expander.value)

      val nextInstances = instances - instanceTypesEntries

      // Make sure expansions are added after everything else so they can override the others
      expandExpansions(library, nextInstances, expanded ?: accumulator)
    }

fun expandGraphInstances(library: ExpansionLibrary, graph: Graph): Graph {
  val instances = filterByProperty(graph, SceneProperties.type)
      .filter { library.graphs.containsKey(it.target) }

  val instanced = expandGraphInstances(library, instances, graph)

  val expansions = filterByProperty(instanced, SceneProperties.type)
      .filter { library.expanders.containsKey(it.target) }

  return expandExpansions(library, expansions, instanced).toSet()
}

fun expandGraphInstances(graphLibrary: GraphLibrary, graph: Graph): Graph =
    expandGraphInstances(ExpansionLibrary(graphLibrary), graph)

fun expandGraphInstances(library: ExpansionLibrary, name: String): Graph =
    expandGraphInstances(library, library.graphs[name]!!)
