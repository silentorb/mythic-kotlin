package silentorb.metaview.texturing

enum class TexturingEvent{
  setTilePreview
}

data class TexturingState(
    val tilePreview: Boolean = false
)