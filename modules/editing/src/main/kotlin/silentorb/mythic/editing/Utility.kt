package silentorb.mythic.editing

fun getActiveEditorGraph(editor: Editor): Graph? =
    editor.state.graphLibrary[editor.state.graph]
