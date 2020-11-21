package silentorb.mythic.editing.components

import imgui.ImGui
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

typealias DragHandler = (Any) -> Commands
typealias DragCondition = (Any) -> Boolean

data class DragTarget(
    val condition: DragCondition = { true },
    val handle: DragHandler,
)

typealias DragTargets = Map<String, DragTarget>

// Needed because the used Java ImGui bindings do not implement ImGui's isDelivery() method
var globalDragPayload: Any? = null

fun dragSource(dragType: String, payload: Any, depiction: () -> Unit) {
  if (ImGui.beginDragDropSource()) {
    globalDragPayload = payload
    ImGui.setDragDropPayloadObject(dragType, payload)
    depiction()
    ImGui.endDragDropSource()
  }
}

fun dragTargets(typeMap: DragTargets): Commands =
    if (ImGui.beginDragDropTarget()) {
      val result = typeMap
          .mapNotNull { (dragType, target) ->
            val payload = globalDragPayload
            if (payload == null || !target.condition(payload))
              null
            else {
              val imGuiPayload = ImGui.acceptDragDropPayloadObject(dragType)
              if (imGuiPayload != null) {
                globalDragPayload = null
                target.handle(payload)
              } else
                null
            }
          }
          .flatten()

      ImGui.endDragDropTarget()
      result
    } else
      listOf()
