package silentorb.mythic.editing.updating

import silentorb.mythic.editing.*
import silentorb.mythic.ent.Entry
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.getGraphValue
import silentorb.mythic.ent.replaceValues
import silentorb.mythic.ent.scenery.getNodeTransform
import silentorb.mythic.scenery.SceneProperties
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
        val selection = editor.state.nodeSelection
        val viewportPair = editor.viewportBoundsMap.entries
            .firstOrNull { (_, viewport) ->
              isInBounds(previousMousePosition.toVector2i(), viewport)
            }

        val camera = getEditorCamera(editor, viewportPair?.key)
        if (viewportPair == null || camera == null)
          graph
        else {
          val viewport = viewportPair.value
          val viewTransform = createViewMatrix(camera.location, camera.orientation)
          val data = editor.operation!!.data as SpatialTransformState
          val newEntries = selection.map { node ->
            val globalObjectLocation = getNodeTransform(graph, node).translation()
            val distance = globalObjectLocation.distance(camera.location)
            val mouseStart = previousMousePosition - viewport.xy().toVector2()
            val cameraTransform = createProjectionMatrix(camera, viewport.zw(), distance) * viewTransform
            val start = unproject(cameraTransform, viewport.toVector4(), mouseStart, 1f)
            val end = unproject(cameraTransform, viewport.toVector4(), mouseStart + mouseOffset, 1f)
            val value = getGraphValue<Vector3>(graph, node, property) ?: Vector3.zero
            val newValue = handler(TransformOperationArguments(value, start, end, globalObjectLocation, data.axis))
            Entry(node, property, newValue)
          }
          replaceValues(graph, newEntries)
        }
      }
    }

fun updateTranslation(previousMousePosition: Vector2, mouseOffset: Vector2, editor: Editor, graph: Graph): Graph =
    if (mouseOffset == Vector2.zero)
      graph
    else {
      val selection = editor.state.nodeSelection
      val viewportPair = editor.viewportBoundsMap.entries
          .firstOrNull { (_, viewport) ->
            isInBounds(previousMousePosition.toVector2i(), viewport)
          }

      val camera = getEditorCamera(editor, viewportPair?.key)
      if (viewportPair == null || camera == null)
        graph
      else {
        val viewport = viewportPair.value
        val viewTransform = createViewMatrix(camera.location, camera.orientation)
        val data = editor.operation!!.data as SpatialTransformState
        val newEntries = selection.mapNotNull { node ->
          val value = getGraphValue<Vector3>(graph, node, SceneProperties.translation) ?: Vector3.zero
          val globalObjectTransform = getNodeTransform(graph, node)
          val globalObjectLocation = globalObjectTransform.translation()
          val distance = globalObjectLocation.distance(camera.location)
          val mouseStart = previousMousePosition - viewport.xy().toVector2()
          val cameraTransform = createProjectionMatrix(camera, viewport.zw(), distance) * viewTransform
          val start = unproject(cameraTransform, viewport.toVector4(), mouseStart, 1f)
          val end = unproject(cameraTransform, viewport.toVector4(), mouseStart + mouseOffset, 1f)
          val offset = end - start
          val finalOffset = if (data.axis.any()) {
            val vector = offset * Vector3(axisMask(data.axis))
            if (vector.x == 0f && vector.y == 0f && vector.z == 0f)
              Vector3.zero
            else
              vector.normalize() * offset.length()
          } else
            offset

          val newValue = value + finalOffset
//          println("$mouseOffset $offset $newValue")

          if (finalOffset == Vector3.zero)
            null
          else
            Entry(node, SceneProperties.translation, newValue)
        }
        replaceValues(graph, newEntries)
      }
    }

fun updateRotation(previousMousePosition: Vector2, mouseOffset: Vector2, editor: Editor, graph: Graph) =
    if (mouseOffset == Vector2.zero)
      graph
    else {
      val selection = editor.state.nodeSelection
      val viewportPair = editor.viewportBoundsMap.entries.firstOrNull { (_, viewport) ->
        isInBounds(previousMousePosition.toVector2i(), viewport)
      }
      val camera = getEditorCamera(editor, viewportPair?.key)
      if (viewportPair == null || camera == null)
        graph
      else {
        val viewport = viewportPair.value
        val viewTransform = createViewMatrix(camera.location, camera.orientation)
        val data = editor.operation!!.data as SpatialTransformState
        val newEntries = selection.map { node ->
          val objectCenter = getNodeTransform(graph, node).translation()
          val mouseStart = previousMousePosition - viewport.xy().toVector2()
          val cameraTransform = createProjectionMatrix(camera, viewport.zw()) * viewTransform
          val center = transformPoint(cameraTransform, viewport.zw().toVector2(), Vector2.zero)(objectCenter)
          val angle = getAngle(mouseStart - center, mouseStart + mouseOffset - center)
//          println(" $mouseOffset ${mouseStart - center} ${mouseStart + mouseOffset - center} $angle")
          val value = getGraphValue<Vector3>(graph, node, SceneProperties.rotation) ?: Vector3.zero
          val lookat = getCameraLookat(camera)
          val offsetOrientation = Quaternion().rotateAxis(angle, lookat)
//        val orientation = Quaternion()//.rotateZYX(value.z, value.y, value.x)
//            .rotateX(value.x)
//            .rotateZ(value.z)
//            .rotateY(value.y)
//        if (orientation.getAngles() !=  value) {
//          val k = 0
//        }
//        val newValue = value + offsetOrientation.getAngles()
          val offsetAngles = when (data.axis.firstOrNull()) {
            Axis.x -> Vector3(offsetOrientation.angleX, 0f, 0f)
            Axis.y -> Vector3(0f, offsetOrientation.angleY, 0f)
            Axis.z -> Vector3(0f, 0f, offsetOrientation.angleZ)
            else -> offsetOrientation.getAngles()
          }
          val newValue = normalizeRadialAngles(value + offsetAngles)
//        val newValue = (orientation * offsetOrientation).getAngles()
//        if (data.axis.any())
//          newValue // * Vector3(axisMask(axis))
//        else
//          newValue
          Entry(node, SceneProperties.rotation, newValue)
        }
        replaceValues(graph, newEntries)
      }
    }

fun updateScaling(previousMousePosition: Vector2, mouseOffset: Vector2, editor: Editor, graph: Graph): Graph =
    if (mouseOffset == Vector2.zero)
      graph
    else {
      val selection = editor.state.nodeSelection
      val viewportPair = editor.viewportBoundsMap.entries.firstOrNull { (_, viewport) ->
        isInBounds(previousMousePosition.toVector2i(), viewport)
      }
      val camera = getEditorCamera(editor, viewportPair?.key)
      if (viewportPair == null || camera == null)
        graph
      else {
        val viewport = viewportPair.value
        val viewTransform = createViewMatrix(camera.location, camera.orientation)
        val data = editor.operation!!.data as SpatialTransformState
        val newEntries = selection.map { node ->
          val objectCenter = getNodeTransform(graph, node).translation()
          val mouseStart = previousMousePosition - viewport.xy().toVector2()
          val cameraTransform = createProjectionMatrix(camera, viewport.zw()) * viewTransform
          val center = transformPoint(cameraTransform, viewport.zw().toVector2(), Vector2.zero)(objectCenter)
          val scalarOffset = (mouseStart + mouseOffset).distance(center) - mouseStart.distance(center)
          val value = getGraphValue<Vector3>(graph, node, SceneProperties.scale) ?: Vector3.unit
          val mask = if (data.axis.any())
            Vector3(axisMask(data.axis))
          else
            Vector3.unit

          val mod = Vector3.unit + mask * scalarOffset * 0.02f

          val newValue = value * mod

          Entry(node, SceneProperties.scale, newValue)
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
      OperationType.rotate -> updateRotation(previousMousePosition, mouseOffset, editor, graph)
      OperationType.scale -> updateScaling(previousMousePosition, mouseOffset, editor, graph)
      else -> graph
    }
  }
}

fun updateStaging(editor: Editor, previousMousePosition: Vector2, mouseOffset: Vector2, commandTypes: List<Any>, nextOperation: Operation?): Graph? =
    if (nextOperation == null || commandTypes.contains(EditorCommands.cancelOperation))
      null
    else if (editor.staging == null || editor.operation != nextOperation)
      getLatestGraph(editor)
    else
      updateStaging(editor, previousMousePosition, mouseOffset, commandTypes)
