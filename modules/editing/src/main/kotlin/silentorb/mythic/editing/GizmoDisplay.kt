package silentorb.mythic.editing

import imgui.ImColor
import imgui.ImDrawList
import imgui.ImGui
import silentorb.mythic.spatial.*

typealias ScreenTransform = (Vector3) -> Vector2

fun transformPoint(transform: Matrix, dimensions: Vector2, offset: Vector2): ScreenTransform = { point ->
  transformToScreen(transform, point)!! * Vector2(1f, -2f) * dimensions + offset
}

fun drawCompass(transform: ScreenTransform, drawList: ImDrawList) {
  val colors = listOf(
      ImColor.intToColor(255, 0, 0, 255),
      ImColor.intToColor(0, 255, 0, 255),
      ImColor.intToColor(0, 0, 255, 255),
  )
  val vectors = listOf(
      Vector3(1f, 0f, 0f),
      Vector3(0f, 1f, 0f),
      Vector3(0f, 0f, 1f),
  )
  val start = transform(Vector3.zero)
  val black = ImColor.intToColor(0, 0, 0, 255)

  for (i in 0 until 3) {
    val end = transform(vectors[i] * 2f)
    drawList.addLine(start.x, start.y, end.x, end.y, colors[i], 3f)
  }
  drawList.addCircleFilled(start.x, start.y, 3f, ImColor.intToColor(128, 128, 128, 255))

  for (i in 0 until 3) {
    val end = transform(vectors[i] * 2f)
    drawList.addCircleFilled(end.x, end.y, 9f, colors[i])
    drawList.addText(end.x - 4f, end.y - 6f, black, ('X' + i).toString())
  }
}

fun drawEditor3dElements(editor: Editor, viewport: Vector4i, camera: CameraRig) {
  val drawList = ImGui.getBackgroundDrawList()
  drawList.addCircle(viewport.x.toFloat() + viewport.z.toFloat() * 0.5f,
      viewport.y.toFloat() + viewport.w.toFloat() * 0.5f,
      100f, ImColor.intToColor(64, 64, 64, 255), 32, 2f)

  val dimensions = viewport.zw()
  val viewTransform = createViewMatrix(Vector3.zero, camera.orientation)
  val orthoTransform = createOrthographicMatrix(dimensions, 45f, 0.01f, 1000f) * viewTransform
  val compassPadding = 50
  val compassOffset = Vector2i(viewport.x + compassPadding, viewport.y + viewport.w - compassPadding)
  val compassTransform = transformPoint(orthoTransform, dimensions.toVector2(), compassOffset.toVector2())
  drawCompass(compassTransform, drawList)
}

fun drawEditor3dElements(editor: Editor) {
  for ((key, viewport) in editor.viewportBoundsMap) {
    val camera = editor.state.cameras[key]
    if (camera != null) {
      drawEditor3dElements(editor, viewport, camera)
    }
  }
}
