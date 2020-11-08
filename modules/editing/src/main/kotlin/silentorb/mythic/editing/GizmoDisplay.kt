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

fun drawSelectedObjectAnnotations(editor: Editor, viewport: Vector4i, camera: CameraRig, drawList: ImDrawList) {
  val selection = editor.state.nodeSelection
  val graph = getActiveEditorGraph(editor)
  val operation = editor.operation
  val data = operation?.data
  val node = selection.firstOrNull()
  if (node != null && graph != null) {
    val dimensions = viewport.zw()
    val viewTransform = createViewMatrix(camera.location, camera.orientation)
    val cameraTransform = createProjectionMatrix(camera, dimensions) * viewTransform
    val offset = viewport.xy()
    val transform = transformPoint(cameraTransform, dimensions.toVector2(), offset.toVector2())
    val location = getTransform(graph, node).translation()

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

  drawSelectedObjectAnnotations(editor, viewport, camera, drawList)
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
