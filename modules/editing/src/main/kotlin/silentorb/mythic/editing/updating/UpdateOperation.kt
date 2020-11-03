package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.spatial.*

fun startOperation(type: OperationType, previous: Operation?): Operation? =
    if (previous?.type == type)
      previous
    else
      Operation(
          type = type,
          data = SpatialTransformState()
      )

fun restrictAxis(axis: Axis, previous: Operation?): Operation? =
    if (previous == null || !(previous.data is SpatialTransformState))
      previous
    else
      previous.copy(
          data = SpatialTransformState(
              axis = setOf(axis)
          )
      )

fun updateOperation(commandType: Any, operation: Operation?): Operation? {
  return when (commandType) {
    EditorCommands.startTranslating -> startOperation(OperationType.translate, operation)
    EditorCommands.startRotating -> startOperation(OperationType.rotate, operation)
    EditorCommands.startScaling -> startOperation(OperationType.scale, operation)
    EditorCommands.restrictAxisX -> restrictAxis(Axis.x, operation)
    EditorCommands.restrictAxisY -> restrictAxis(Axis.y, operation)
    EditorCommands.restrictAxisZ -> restrictAxis(Axis.z, operation)
    EditorCommands.cancelOperation -> null
    EditorCommands.commitOperation -> null
    else -> operation
  }
}

fun updateOperation(commandTypes: List<Any>, editor: Editor): Operation? =
    commandTypes.fold(editor.operation) { a, commandType -> updateOperation(commandType, a) }

fun isInBounds(position: Vector2i, bounds: Vector4i): Boolean =
    position.x >= bounds.x &&
        position.x < bounds.x + bounds.z &&
        position.y >= bounds.y &&
        position.y < bounds.y + bounds.w

fun unproject(cameraTransform: Matrix, viewport: Vector4, point: Vector2, distance: Float): Vector3 {
  return cameraTransform.unproject(point.x,
      viewport.w - point.y, distance, viewport)
}

fun updateTranslation(previousMousePosition: Vector2, mouseOffset: Vector2, editor: Editor, graph: Graph): Graph =
    if (mouseOffset == Vector2.zero)
      graph
    else {
      val selection = editor.state.selection
      val viewportPair = editor.viewportBoundsMap.entries.firstOrNull { (_, viewport) ->
        isInBounds(previousMousePosition.toVector2i(), viewport)
      }
      val camera = editor.state.cameras[viewportPair?.key]
      if (viewportPair == null || camera == null)
        graph
      else {
        val viewport = viewportPair.value
        val viewTransform = createViewMatrix(camera.location, camera.orientation)
        val data = editor.operation!!.data as SpatialTransformState
        val newEntries = selection.map { node ->
          val value = getValue<Vector3>(graph, node, Properties.translation) ?: Vector3.zero
          val globalObjectTransform = getTransform(graph, node)
          val globalObjectLocation = globalObjectTransform.translation()
          val distance = globalObjectLocation.distance(camera.location)
          val mouseStart = previousMousePosition - viewport.xy().toVector2()
          val cameraTransform = createPerspectiveMatrix(viewport.zw(), 45f, 0.01f, distance) * viewTransform
          val start = unproject(cameraTransform, viewport.toVector4(), mouseStart, 1f)
          val end = unproject(cameraTransform, viewport.toVector4(), mouseStart + mouseOffset, 1f)
          val offset = end - start
          val newValue = value + offset
          val constrained = if (data.axis.any())
            newValue * Vector3(axisMask(data.axis))
          else
            newValue

//          println("$mouseOffset $offset $newValue")

          Entry(node, Properties.translation, constrained)
        }
        replaceValues(graph, newEntries)
      }
    }

fun updateStaging(editor: Editor, previousMousePosition: Vector2, mouseOffset: Vector2, commandTypes: List<Any>): Graph? {
  val graph = editor.staging
  val operation = editor.operation
  return if (graph == null || operation == null)
    null
  else {
    when (operation.type) {
      OperationType.translate -> updateTranslation(previousMousePosition, mouseOffset, editor, graph)
      else -> graph
    }
  }
}

fun updateStaging(editor: Editor, previousMousePosition: Vector2, mouseOffset: Vector2, commandTypes: List<Any>, nextOperation: Operation?): Graph? =
    if (nextOperation == null || commandTypes.contains(EditorCommands.cancelOperation))
      null
    else if (editor.staging == null || editor.operation != nextOperation)
      editor.graph
    else
      updateStaging(editor, previousMousePosition, mouseOffset, commandTypes)
