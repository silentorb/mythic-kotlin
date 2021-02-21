package silentorb.mythic.editing

import imgui.ImColor
import imgui.ImDrawList
import imgui.ImGui
import imgui.ImVec2
import silentorb.mythic.ent.scenery.nodeAttributes
import silentorb.mythic.ent.scenery.getNodeTransform
import silentorb.mythic.spatial.*

typealias ScreenTransform = (Vector3) -> Vector2

fun axisColors() = listOf(
    ImColor.intToColor(255, 51, 82, 255),
    ImColor.intToColor(139, 220, 0, 255),
    ImColor.intToColor(40, 144, 255, 255),
)

fun gizmoPainterToggle(key: String, painter: GizmoPainter): GizmoPainter = { environment ->
  if (environment.editor.persistentState.visibleGizmoTypes.contains(key)) {
    painter(environment)
  }
}

fun drawAxisRails(axis: Axis, origin: Vector3, transform: ScreenTransform, drawList: ImDrawList) {
  val mask = axisMask(setOf(axis))
  val other = Vector3(mask.map { 1f - it }) * origin
  val halfLength = 10f
  val axisVector = Vector3(mask) * Vector3(halfLength)
  val center = transform(origin)
  val start = transform(other + axisVector)
  val end = transform(other - axisVector)
  drawList.addLine(center.x, center.y, start.x, start.y, axisColors()[axis.ordinal], 2f)
  drawList.addLine(center.x, center.y, end.x, end.y, axisColors()[axis.ordinal], 2f)
}

fun getStandardPointTransform(viewport: Vector4i, camera: CameraRig): ScreenTransform {
  val dimensions = viewport.zw()
  val viewTransform = createViewMatrix(camera.location, camera.orientation)
  val cameraTransform = createProjectionMatrix(camera, dimensions) * viewTransform
  val offset = viewport.xy()
  return transformPoint(cameraTransform, dimensions.toVector2(), offset.toVector2())
}

fun drawGizmoLine(drawList: ImDrawList, transform: ScreenTransform, start: Vector3, end: Vector3, color: Int, thickness: Float = 1f) {
  val a = transform(start)
  val b = transform(end)
  drawList.addLine(a.x, a.y, b.x, b.y, color, thickness)
}

fun drawGizmoSolidPolygon(drawList: ImDrawList, transform: ScreenTransform, points: Collection<Vector3>, color: Int) {
  val p = points.map {
    val point = transform(it)
    ImVec2(point.x, point.y)
  }
      .toTypedArray()

  drawList.addConvexPolyFilled(p, p.size, color)
}

fun drawSelectedObjectAnnotations(editor: Editor, transform: ScreenTransform, drawList: ImDrawList) {
  val selection = getNodeSelection(editor)
  val graph = getActiveEditorGraph(editor)
  val operation = editor.operation
  val data = operation?.data
  val node = selection.firstOrNull()
  if (node != null && graph != null) {
    val location = getNodeTransform(graph, node).translation()

    if (data != null && data is SpatialTransformState) {
      val axisList = data.axis

      for (axis in axisList) {
        drawAxisRails(axis, location, transform, drawList)
      }
    }

    val center = transform(location)
    val cb = 255
    drawList.addCircleFilled(center.x, center.y, 3f, ImColor.intToColor(cb, cb, cb, 128))
  }
}

fun drawCompass(transform: ScreenTransform, lookAt: Vector3, drawList: ImDrawList) {
  val colors = axisColors()
  val vectors = listOf(
      Vector3(1f, 0f, 0f),
      Vector3(0f, 1f, 0f),
      Vector3(0f, 0f, 1f),
  )
  val start = transform(Vector3.zero)
  val black = ImColor.intToColor(0, 0, 0, 255)
  val lineLength = 4f

  val indices = (0 until 3)
      .sortedByDescending { vectors[it].dot(lookAt) }

  drawList.addCircleFilled(start.x, start.y, 3f, ImColor.intToColor(128, 128, 128, 255))

  for (i in indices) {
    val end = transform(vectors[i] * lineLength)
    drawList.addLine(start.x, start.y, end.x, end.y, colors[i], 3f)
    drawList.addCircleFilled(end.x, end.y, 9f, colors[i])
    drawList.addText(end.x - 4f, end.y - 6f, black, ('X' + i).toString())
  }
}

fun drawCompass(viewport: Vector4i, camera: CameraRig, drawList: ImDrawList) {
  val dimensions = viewport.zw()
  val viewTransform = createViewMatrix(Vector3.zero, camera.orientation)
  val orthoTransform = createOrthographicMatrix(dimensions, 30f, 0.01f, 1000f) * viewTransform
  val compassPadding = 50
  val compassOffset = viewport.xy() + Vector2i(compassPadding, viewport.w - compassPadding) - viewport.zw() / 2
  val compassTransform = transformPoint(orthoTransform, dimensions.toVector2(), compassOffset.toVector2())
  val lookAt = getCameraLookat(camera)
  drawCompass(compassTransform, lookAt, drawList)
}

fun drawJoints(editor: Editor, transform: ScreenTransform, drawList: ImDrawList) {
  if (editor.operation?.type != OperationType.connecting)
    return

  val pointColor = ImColor.intToColor(255, 0, 255, 255)
  val selectionColor = ImColor.intToColor(255, 128, 255, 255)
  val graph = getCachedGraph(editor)
  val joints = nodeAttributes(graph, CommonEditorAttributes.joint)
  for (joint in joints) {
    val location = getNodeTransform(graph, joint).translation()
    val point = transform(location)
    val color = if (joint == editor.selectedJoint)
      selectionColor
    else
      pointColor

    drawList.addCircleFilled(point.x, point.y, 6f, color)
  }
}

fun drawEditor3dElements(editor: Editor, viewport: Vector4i, camera: CameraRig) {
  val drawList = ImGui.getBackgroundDrawList()

  val bounds = viewport.toVector4()
  drawList.pushClipRect(bounds.x, bounds.y, bounds.x + bounds.z, bounds.y + bounds.w)

  val transform = getStandardPointTransform(viewport, camera)
  drawSelectedObjectAnnotations(editor, transform, drawList)
  drawJoints(editor, transform, drawList)
  drawCompass(viewport, camera, drawList)

  val environment = GizmoEnvironment(
      editor = editor,
      viewport = viewport,
      camera = camera,
      transform = transform,
      drawList = drawList,
  )
  for (painter in editor.enumerations.gizmoPainters) {
    painter(environment)
  }

  drawList.popClipRect()
}

fun drawEditor3dElements(editor: Editor) {
  for ((key, viewport) in editor.viewportBoundsMap) {
    val camera = getEditorCamera(editor, key)
    if (camera != null) {
      drawEditor3dElements(editor, viewport, camera)
    }
  }
}
