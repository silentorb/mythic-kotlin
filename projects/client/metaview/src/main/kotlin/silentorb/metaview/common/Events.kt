package silentorb.metaview.common

enum class CommonEvent {
  addNode,
  connecting,
  deleteSelected,
  duplicateNode,
  inputValueChanged,
  insertNode,
  refresh,
  renameGraph,
  newGraph,
  selectInput,
  selectNode,
  setPreviewFinal,
  graphSelect,
}

enum class HistoryEvent {
  redo,
  undo
}

data class Event(
    val type: Any,
    val data: Any = 0,
    val preview: Boolean = false
)

typealias Emitter = (Event) -> Unit

data class Renaming(
    val previousName: String,
    val newName: String
)