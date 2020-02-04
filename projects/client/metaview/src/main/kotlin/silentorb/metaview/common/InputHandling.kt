package silentorb.metaview.common

import javafx.scene.Node
import javafx.scene.control.ChoiceDialog
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

typealias KeyHandler = (Emitter, CommonState) -> Unit

fun nodeFunctionDialog(nodeDefinitions: NodeDefinitionMap, title: String): String? {
  val choices = nodeDefinitions.keys
  val dialog = ChoiceDialog(choices.first(), choices)
  dialog.title = title
  dialog.contentText = "Function"
  dialog.headerText = null
  dialog.graphic = null
  val result = dialog.showAndWait()
  return if (!result.isEmpty)
    result.get()
  else null
}

fun addNodeDialog(nodeDefinitions: NodeDefinitionMap): KeyHandler = { emit, _ ->
  val name = nodeFunctionDialog(nodeDefinitions, "Add Node")
  if (name != null) {
    emit(Event(CommonEvent.addNode, name))
  }
}

fun insertNodeDialog(nodeDefinitions: NodeDefinitionMap): KeyHandler = { emit, state ->
  if (state.gui.graphInteraction.portSelection.any()) {
    val name = nodeFunctionDialog(nodeDefinitions, "Insert Node")
    if (name != null) {
      emit(Event(CommonEvent.insertNode, name))
    }
  }
}

val duplicateNodeHandler: KeyHandler = { emit, state ->
  val selections = state.gui.graphInteraction
  val node = selections.nodeSelection.firstOrNull() ?: selections.portSelection.firstOrNull()?.node
  if (node != null) {
    emit(Event(CommonEvent.duplicateNode, node))
  }
}

val keyEvents: Map<KeyCode, CommonEvent> = mapOf(
    KeyCode.C to CommonEvent.connecting
)

fun keyHandlers(nodeDefinitions: NodeDefinitionMap): Map<KeyCode, KeyHandler> = mapOf(
    KeyCode.A to addNodeDialog(nodeDefinitions),
    KeyCode.D to duplicateNodeHandler,
    KeyCode.I to insertNodeDialog(nodeDefinitions)
)

fun listenForKeypresses(nodeDefinitions: NodeDefinitionMap, node: Node, emit: Emitter, state: () -> CommonState) {
  node.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
    val type = keyEvents[event.code]
    if (type != null)
      emit(Event(type))
    else {
      val handler = keyHandlers(nodeDefinitions)[event.code]
      if (handler != null)
        handler(emit, state())
    }
  }
}
