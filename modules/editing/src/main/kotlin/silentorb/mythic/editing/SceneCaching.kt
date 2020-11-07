package silentorb.mythic.editing

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
  } else if (graphId != null && graph != library[graphId])
    library + mapOf(graphId to graph)
  else
    library
}
