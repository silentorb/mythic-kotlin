package silentorb.mythic.ent.scenery

import silentorb.mythic.ent.*
import silentorb.mythic.scenery.SceneProperties

fun expandInstance(library: ExpansionLibrary, node: Key, target: Key): LooseGraph {
  val definition = library.graphs[target]
  return if (definition == null)
    setOf()
  else {
    val roots = getGraphRoots(definition)
    val (rootEntries, withoutRoots) = definition
        .partition { roots.contains(it.source) }

    val inheritedProperties = rootEntries.map { it.copy(source = node) }
    val transposed = transposeNamespace(withoutRoots.toSet(), node)
    val expanded = expandInstances(library, transposed)
    val newRoots = filterByPropertyValue(transposed, SceneProperties.parent, roots.first())
    inheritedProperties + expanded - newRoots + newRoots.map { root -> root.copy(target = node) }
  }
}

fun expandInstances(library: ExpansionLibrary, instances: LooseGraph, accumulator: Graph): Graph =
    if (instances.none())
      accumulator
    else {
      val node = instances.first().source
      val instanceTypesEntries = instances
          .filter { it.source == node }

      val instanceTypes = instanceTypesEntries.map { it.target as Key }

      val initial: LooseGraph = listOf()
      val additions = instanceTypes
          .fold(initial) { a, b ->
            a + expandInstance(library, node, b)
          }

      // Make sure accumulator is added to additions and not the other way around
      // so that local properties override inherited properties
      val graph = additions.toSet() + accumulator
      val nextInstances = instances - instanceTypesEntries
      expandInstances(library, nextInstances, graph)
    }

fun expandExpansions(library: ExpansionLibrary, instances: LooseGraph, accumulator: LooseGraph): LooseGraph =
    if (instances.none())
      accumulator
    else {
      val node = instances.first().source
      val instanceTypesEntries = instances
          .filter { it.source == node }

      val instanceTypes = instanceTypesEntries.map { it.target as Key }

      val expander = library.expanders.entries.firstOrNull { (expander, _) ->
        instanceTypes.contains(expander)
      }?.value

      val expanded = if (expander == null)
        accumulator
      else
        expander(library, accumulator, node)

      val nextInstances = instances - instanceTypesEntries

      // Make sure expansions are added after everything else so they can override the others
      expandExpansions(library, nextInstances, expanded)
    }

fun expandInstances(library: ExpansionLibrary, graph: Graph): Graph {
  val instances = filterByProperty<Key>(graph, SceneProperties.type)
      .filter { library.graphs.containsKey(it.target) }

  val instanced = expandInstances(library, instances, graph)

  val expansions = filterByProperty<Key>(instanced, SceneProperties.type)
      .filter { library.expanders.containsKey(it.target) }

  return expandExpansions(library, expansions, instanced).toSet()
}

fun expandInstances(library: ExpansionLibrary, name: String): Graph =
    expandInstances(library,  library.graphs[name]!!)
