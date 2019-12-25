package silentorb.mythic.lookinglass

import silentorb.mythic.spatial.*
import org.joml.*
import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.ProjectionType

data class CameraEffectsData(
    val transform: Matrix,
    val direction: Vector3
)

fun createViewMatrix(position: Vector3, lookAt: Vector3): Matrix {
  return toMatrix(
      MutableMatrix()
          .setLookAt(position, lookAt, Vector3(0f, 0f, 1f))
  )
}

fun createViewMatrix(position: Vector3, orientation: Quaternion): Matrix {
//  val forward = Quaternion(orientation) * Vector3(1f, 0f, 0f)
//  val lookAt = position + forward
//  return createViewMatrix(position, lookAt)
  return Matrix.identity
      //      .rotateZ(-Pi / 2f)
      .rotateZ(Pi / 2f)
      .rotateY(Pi / 2f)
      .rotate(-orientation)
      .translate(-position)
}

//fun createViewMatrix(position: Vector3, orientation: Quaternion): Matrix {
//  val forward = Quaternion(-0.271f, 0.271f, 0.653f, 0.653f) * Vector3(1f, 0f, 0f)
//  val look_at = position + forward
//  return Matrix.identity.setLookAt(Vector3(1f, -10.70593596f, 10.707852f), Vector3(), Vector3(0f, 0f, 1f))
//
////  return lookAt3(position, look_at, Vector3(0f, 0f, 1f), Matrix.identity)
//}

fun getAspectRatio(dimensions: Vector2i): Float {
  return dimensions.x.toFloat() / dimensions.y.toFloat()
}

fun createPerspectiveMatrix(dimensions: Vector2i, angle: Float, nearClip: Float, farClip: Float): Matrix {
  val ratio = getAspectRatio(dimensions)
  val radians = Math.toRadians(angle.toDouble()).toFloat()
  return toMatrix(MutableMatrix()
      .setPerspective(radians, ratio, nearClip, farClip))
}

fun createOrthographicMatrix(dimensions: Vector2i, zoom: Float, nearClip: Float, farClip: Float): Matrix {
  val ratio = getAspectRatio(dimensions)
  return toMatrix(MutableMatrix()
      .setOrtho(-1f * zoom, 1f * zoom, -1f * zoom, 1f * zoom, nearClip, farClip))
}

fun createCameraMatrix(dimensions: Vector2i, camera: Camera): Matrix {
  val projection = if (camera.projectionType == ProjectionType.orthographic)
    createOrthographicMatrix(dimensions, camera.angleOrZoom, camera.nearClip, camera.farClip)
  else
    createPerspectiveMatrix(dimensions, camera.angleOrZoom, camera.nearClip, camera.farClip)

  val lookAt = camera.lookAt
  val view = if (lookAt == null)
    createViewMatrix(camera.position, camera.orientation)
  else
    createViewMatrix(camera.position, lookAt)

  return projection * view
}

fun createCameraEffectsData(dimensions: Vector2i, camera: Camera) =
    CameraEffectsData(
        createCameraMatrix(dimensions, camera),
        if (camera.lookAt != null)
          camera.lookAt!! - camera.position
        else
          camera.orientation * Vector3(1f, 0f, 0f)
    )
