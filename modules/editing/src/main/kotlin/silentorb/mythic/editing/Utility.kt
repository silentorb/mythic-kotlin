package silentorb.mythic.editing

import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.spatial.Vector3

fun getActiveEditorGraphId(editor: Editor): Id? {
  val graph = editor.state.graph
  return if (editor.graphLibrary.containsKey(graph))
    graph
  else
    editor.graphLibrary.keys.firstOrNull()
}

fun getActiveEditorGraph(editor: Editor): Graph? =
    editor.graphLibrary[getActiveEditorGraphId(editor)]

fun defaultEditorState() =
    EditorState(
        cameras = mapOf(defaultViewportId to CameraRig(location = Vector3(-10f, 0f, 0f))),
    )
