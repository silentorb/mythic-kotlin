package silentorb.mythic.editing

import silentorb.mythic.spatial.Vector4i

typealias SceneTree = Map<Id, Id>

typealias GraphLibrary = Map<String, Graph>


data class Typeface(
    val name: String,
    val path: String,
    val size: Float
)

data class Editor(
    val isActive: Boolean = false,
    val graphLibrary: GraphLibrary = mapOf(),
    val graph: String? = null,
    val selection: Set<String> = setOf(),
    val viewport: Vector4i? = null
)

fun isActive(editor: Editor?) =
    editor?.isActive == true

fun getActiveEditorGraph(editor: Editor): Graph? =
    editor.graphLibrary[editor.graph]
