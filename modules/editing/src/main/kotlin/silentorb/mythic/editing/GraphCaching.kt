package silentorb.mythic.editing

import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.GraphLibrary
import silentorb.mythic.ent.newGraph
import silentorb.mythic.ent.scenery.ExpansionLibrary
import silentorb.mythic.ent.scenery.convertToNodeTransforms
import silentorb.mythic.ent.scenery.expandGraphInstances
import silentorb.mythic.ent.singleValueCache

fun loadImmediateDependencies(editor: Editor, library: GraphLibrary, graphId: String): GraphLibrary {
  val dependencies = getGraphDependencies(library, setOf(graphId)).plus(graphId) - library.keys
  return dependencies
      .associateWith { loadGraph(editor, it) }
      .filter { it.value != null } as GraphLibrary
}

tailrec fun loadAllDependencies(editor: Editor, graphId: String, accumulator: GraphLibrary = editor.graphLibrary): GraphLibrary {
  val loaded = loadImmediateDependencies(editor, accumulator, graphId)
  return if (loaded.none())
    accumulator
  else
    loadAllDependencies(editor, graphId, accumulator + loaded)
}

fun updateSceneCaching(editor: Editor): GraphLibrary {
  val graph = getActiveEditorGraph(editor)
  val library = editor.graphLibrary
  val graphId = editor.persistentState.graph
  return if (graph == null) {
    if (graphId == null)
      library
    else {
      val loadedGraph = loadGraph(editor, graphId)
      if (loadedGraph == null)
        library
      else
        library + mapOf(graphId to loadedGraph)
    }
  } else if (graphId != null) {
    val update = if (graph != library[graphId])
      mapOf(graphId to graph)
    else
      mapOf()

    loadAllDependencies(editor, graphId, library) + update

  } else
    library
}

val graphCache = singleValueCache<Triple<GraphTransform, ExpansionLibrary, Graph>, Graph> { (transform, library, graph) ->
  expandGraphInstances(library, transform(convertToNodeTransforms(graph)))
}

fun getCachedGraph(editor: Editor): Graph {
  val startingGraph = getActiveEditorGraph(editor) ?: newGraph()
  return graphCache(Triple(editor.enumerations.graphTransform, getExpansionLibrary(editor), startingGraph))
}
