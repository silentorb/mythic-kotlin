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
            val addition = expandInstance(library, node, b)
            mergeGraphs(library.schema, addition, a, accumulator)
          }

      // Make sure accumulator is added to additions and not the other way around
      // so that local properties override inherited properties
      val graph = additions.toSet() + accumulator
      val nextInstances = instances - instanceTypesEntries
      expandGraphInstances(library, nextInstances, graph)
    }

fun expandExpansions(library: ExpansionLibrary, instances: Graph, accumulator: Graph): Graph =
    if (instances.none())
      accumulator
    else {
      val node = instances.first().source
      val instanceTypesEntries = instances
          .filter { it.source == node }

      val nextInstances = instances - instanceTypesEntries

      // Make sure expansions are added after everything else so they can override the others
      expandExpansions(library, nextInstances, accumulator)
    }

fun expandGraphInstances(library: ExpansionLibrary, graph: Graph): Graph {
  val instances = filterByProperty(graph, SceneProperties.type)
      .filter { library.graphs.containsKey(it.target) }

  val instanced = expandGraphInstances(library, instances, graph)
  val expansions = filterByProperty(instanced, SceneProperties.type)
  return HashedList.from(expandExpansions(library, expansions, instanced))
}

fun expandGraphInstances(library: ExpansionLibrary, name: String): Graph =
    expandGraphInstances(library, library.graphs[name]!!)
