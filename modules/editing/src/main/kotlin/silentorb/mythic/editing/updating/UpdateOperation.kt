package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3

fun startOperation(previous: Operation?, type: OperationType): Operation? =
    if (previous?.type == type)
      previous
    else
      Operation(
          type = type,
          data = SpatialTransformState()
      )

fun updateOperation(commandType: Any, operation: Operation?): Operation? {
  return when (commandType) {
    EditorCommands.startTranslating -> startOperation(operation, OperationType.translate)
    EditorCommands.startRotating -> startOperation(operation, OperationType.rotate)
    EditorCommands.startScaling -> startOperation(operation, OperationType.scale)
    EditorCommands.cancelOperation -> null
    EditorCommands.commitOperation -> null
    else -> operation
  }
}

fun updateOperation(commandTypes: List<Any>, editor: Editor): Operation? =
    commandTypes.fold(editor.operation) { a, commandType -> updateOperation(commandType, a) }

fun updateStaging(editor: Editor, mouseOffset: Vector2, commandTypes: List<Any>): Graph? {
  val graph = editor.staging
  val operation = editor.operation
  return if (graph == null || operation == null)
    null
  else {
    when (operation.type) {
      OperationType.translate -> {
        if (mouseOffset != Vector2.zero) {
          val selection = editor.state.selection
          // TODO: Get camera projection to convert mouse offset to 3d offset
          val newEntries = selection.map { node ->
            val value = getValue<Vector3>(graph, node, Properties.translation) ?: Vector3.zero
            // TODO: Apply offset to value
            Entry(node, Properties.translation, value)
          }
          graph + newEntries
        } else
          graph
      }
      else -> graph
    }
  }
}

fun updateStaging(editor: Editor, mouseOffset: Vector2, commandTypes: List<Any>, nextOperation: Operation?): Graph? =
    if (nextOperation == null)
      null
    else if (editor.staging == null || editor.operation?.type != nextOperation.type)
      editor.graph
    else
      updateStaging(editor, mouseOffset, commandTypes)
