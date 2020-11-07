package silentorb.mythic.editing

import imgui.ImColor
import imgui.ImDrawList
import imgui.ImGui
import silentorb.mythic.spatial.*

typealias ScreenTransform = (Vector3) -> Vector2

fun axisColors() = listOf(
    ImColor.intToColor(255, 51, 82, 255),
    ImColor.intToColor(139, 220, 0, 255),
    ImColor.intToColor(40, 144, 255, 255),
)

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

fun drawAxisConstraints(editor: Editor, viewport: Vector4i, camera: CameraRig, drawList: ImDrawList) {
  val selection = editor.state.nodeSelection
  val graph = getActiveEditorGraph(editor)
  val operation = editor.operation
  val data = operation?.data
  if (selection.any() && graph != null && data != null && data is SpatialTransformState) {
    val node = selection.first()
    val location = getTransform(graph, node).translation()
    val axisList = data.axis

    val dimensions = viewport.zw()
    val viewTransform = createViewMatrix(camera.location, camera.orientation)
    val cameraTransform = createProjectionMatrix(camera, dimensions) * viewTransform
    val offset = viewport.xy()
    val transform = transformPoint(cameraTransform, dimensions.toVector2(), offset.toVector2())

    for (axis in axisList) {
//      if (operation.type == OperationType.translate) {
      drawAxisRails(axis, location, transform, drawList)
//      }
    }
  }
}

fun drawCompass(transform: ScreenTransform, drawList: ImDrawList) {
  val colors = axisColors()
  val vectors = listOf(
      Vector3(1f, 0f, 0f),
      Vector3(0f, 1f, 0f),
      Vector3(0f, 0f, 1f),
  )
  val start = transform(Vector3.zero)
  val black = ImColor.intToColor(0, 0, 0, 255)
  val lineLength = 4f

  for (i in 0 until 3) {
    val end = transform(vectors[i] * lineLength)
    drawList.addLine(start.x, start.y, end.x, end.y, colors[i], 3f)
  }
  drawList.addCircleFilled(start.x, start.y, 3f, ImColor.intToColor(128, 128, 128, 255))

  for (i in 0 until 3) {
    val end = transform(vectors[i] * lineLength)
    drawList.addCircleFilled(end.x, end.y, 9f, colors[i])
    drawList.addText(end.x - 4f, end.y - 6f, black, ('X' + i).toString())
  }
}

fun drawEditor3dElements(editor: Editor, viewport: Vector4i, camera: CameraRig) {
  val drawList = ImGui.getBackgroundDrawList()
//  drawList.addCircle(viewport.x.toFloat() + viewport.z.toFloat() * 0.5f,
//      viewport.y.toFloat() + viewport.w.toFloat() * 0.5f,
//      100f, ImColor.intToColor(64, 64, 64, 255), 32, 2f)

  val dimensions = viewport.zw()
  val viewTransform = createViewMatrix(Vector3.zero, camera.orientation)
  val orthoTransform = createOrthographicMatrix(dimensions, 30f, 0.01f, 1000f) * viewTransform
  val compassPadding = 50
  val compassOffset = viewport.xy() + Vector2i(compassPadding, viewport.w - compassPadding) - viewport.zw() / 2
  val compassTransform = transformPoint(orthoTransform, dimensions.toVector2(), compassOffset.toVector2())

  val bounds = viewport.toVector4()
  drawList.pushClipRect(bounds.x, bounds.y, bounds.x + bounds.z, bounds.y + bounds.w)

  drawAxisConstraints(editor, viewport, camera, drawList)
  drawCompass(compassTransform, drawList)

  drawList.popClipRect()
}

fun drawEditor3dElements(editor: Editor) {
  for ((key, viewport) in editor.viewportBoundsMap) {
    val camera = editor.state.cameras[key]
    if (camera != null) {
      drawEditor3dElements(editor, viewport, camera)
    }
  }
}
