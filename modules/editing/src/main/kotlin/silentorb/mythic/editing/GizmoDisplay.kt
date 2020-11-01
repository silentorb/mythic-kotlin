package silentorb.mythic.editing

import imgui.ImColor
import imgui.ImDrawList
import imgui.ImGui
import silentorb.mythic.spatial.*

typealias ScreenTransform = (Vector3) -> Vector2

fun transformPoint(transform: Matrix, dimensions: Vector2, offset: Vector2): ScreenTransform = { point ->
  transformToScreen(transform, point)!! * Vector2(1f, -1f) * dimensions + offset
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

  for (i in 0 until 3) {
    val end = transform(vectors[i] * 1f)
    drawList.addLine(start.x, start.y, end.x, end.y, colors[i])
    val textPoint = transform(vectors[i] * 1.1f)
    drawList.addText(textPoint.x - 5f, textPoint.y - 5f, colors[i], ('X' + i).toString())
  }
}

fun drawEditor3dElements(editor: Editor, viewport: Vector4i, camera: CameraRig) {
  val drawList = ImGui.getBackgroundDrawList()
  drawList.addCircle(viewport.x.toFloat() + viewport.z.toFloat() * 0.5f,
      viewport.y.toFloat() + viewport.w.toFloat() * 0.5f,
      100f, ImColor.intToColor(64, 64, 64, 255), 32, 2f)

  val dimensions = viewport.zw()
  val viewTransform = createViewMatrix(camera.location, camera.orientation)
  val orthoTransform = createPerspectiveMatrix(dimensions, 45f, 0.01f, 1000f) * viewTransform
  val compassPadding = 40
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
