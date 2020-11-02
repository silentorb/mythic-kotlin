package silentorb.mythic.editing

import silentorb.mythic.spatial.*

object Lab {
  @JvmStatic
  fun main(args: Array<String>) {
    System.setProperty("joml.format", "false")
    val viewport = Vector4i(0,0, 200, 100)
    val viewTransform = createViewMatrix(Vector3(-10f, 0f, 0f), Quaternion())
    val cameraTransform = createPerspectiveMatrix(viewport.zw(), 45f, 0.001f, 10f) * viewTransform
    val point = Vector2(150f, 50f)
    val result = cameraTransform.unproject(point.x,
        viewport.w - point.y, 1f - 0.001f, viewport.toVector4())
    println(result)
  }
}
