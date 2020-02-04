package silentorb.metaview.texturing

import silentorb.metaview.common.StateTransformListener
import silentorb.metaview.common.eventTypeSwitch
import silentorb.mythic.ent.pass

typealias TexturingTransform = (TexturingState) -> TexturingState

fun setTilePreview(value: Boolean): TexturingTransform = { state ->
  state.copy(
      tilePreview = value
  )
}

fun updateTexturingState(event: TexturingEvent, data: Any): TexturingTransform =
    when (event) {
      TexturingEvent.setTilePreview -> setTilePreview(data as Boolean)
      else -> ::pass
    }

val texturingListener: StateTransformListener<TexturingState> = eventTypeSwitch(::updateTexturingState)
