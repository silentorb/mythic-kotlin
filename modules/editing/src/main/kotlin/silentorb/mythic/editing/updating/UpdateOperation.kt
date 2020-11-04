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

data class TransformOperationArguments(
    val value: Vector3,
    val start: Vector3,
    val end: Vector3,
    val center: Vector3,
    val axis: Set<Axis>
)

typealias MouseTransformHandler = (TransformOperationArguments) -> Vector3

fun mouseTransform(property: String, handler: MouseTransformHandler): (Vector2, Vector2, Editor, Graph) -> Graph =
    { previousMousePosition, mouseOffset, editor, graph ->
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
            val globalObjectLocation = getTransform(graph, node).translation()
            val distance = globalObjectLocation.distance(camera.location)
            val mouseStart = previousMousePosition - viewport.xy().toVector2()
            val cameraTransform = createProjectionMatrix(camera, viewport.zw(), distance) * viewTransform
            val start = unproject(cameraTransform, viewport.toVector4(), mouseStart, 1f)
            val end = unproject(cameraTransform, viewport.toVector4(), mouseStart + mouseOffset, 1f)
            val value = getValue<Vector3>(graph, node, property) ?: Vector3.zero
            val newValue = handler(TransformOperationArguments(value, start, end, globalObjectLocation, data.axis))
            Entry(node, property, newValue)
          }
          replaceValues(graph, newEntries)
        }
      }
    }

val updateTranslation = mouseTransform(Properties.translation) { (value, start, end, center, axis) ->
  val offset = end - start
  val newValue = value + offset
  if (axis.any())
    newValue * Vector3(axisMask(axis))
  else
    newValue
}

val updateRotation = mouseTransform(Properties.rotation) { (value, start, end, center, axis) ->
  val a = Quaternion().lookAlong((start - center).normalize(), Vector3.up)
  val b = Quaternion().lookAlong((end - center).normalize(), Vector3.up)
  val diff=  a.difference(b)
  val orientation = Quaternion()
      .rotateZ(value.z)
      .rotateY(value.y)
      .rotateX(value.x)
  val newOrientation = orientation * diff
  val newValue = newOrientation.getAngles()
  if (axis.any())
    newValue // * Vector3(axisMask(axis))
  else
    newValue
}

fun updateStaging(editor: Editor, previousMousePosition: Vector2, mouseOffset: Vector2, commandTypes: List<Any>): Graph? {
  val graph = editor.staging
  val operation = editor.operation
  return if (graph == null || operation == null)
    null
  else {
    when (operation.type) {
      OperationType.translate -> updateTranslation(previousMousePosition, mouseOffset, editor, graph)
      OperationType.rotate -> updateRotation(previousMousePosition, mouseOffset, editor, graph)
//      OperationType.scale -> updateScaling(previousMousePosition, mouseOffset, editor, graph)
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
