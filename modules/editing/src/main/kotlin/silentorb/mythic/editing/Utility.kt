package silentorb.mythic.editing

fun getActiveEditorGraph(editor: Editor): Graph? =
    editor.graphLibrary[editor.graph]
