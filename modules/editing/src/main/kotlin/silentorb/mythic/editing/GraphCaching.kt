package silentorb.mythic.editing

import silentorb.mythic.ent.GraphLibrary

fun updateSceneCaching(editor: Editor): GraphLibrary {
  val graph = getActiveEditorGraph(editor)
  val library = editor.graphLibrary
  val graphId = editor.state.graph
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

    val dependencies = getGraphDependencies(library, setOf(graphId)) - library.keys
    val loaded = dependencies
        .associateWith { loadGraph(editor, it) }
        .filter { it.value != null } as GraphLibrary

    library + update + loaded
  } else
    library
}
