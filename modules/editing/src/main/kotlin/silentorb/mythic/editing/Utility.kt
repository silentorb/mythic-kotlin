package silentorb.mythic.editing

fun isActive(editor: Editor?) =
    editor?.isActive == true

fun getActiveEditorGraph(editor: Editor): Graph? =
    editor.graphLibrary[editor.graph]
