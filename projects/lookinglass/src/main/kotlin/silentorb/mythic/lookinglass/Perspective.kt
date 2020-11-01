package silentorb.mythic.lookinglass

import silentorb.mythic.spatial.*
import silentorb.mythic.scenery.Camera
import silentorb.mythic.scenery.ProjectionType
import silentorb.mythic.spatial.Vector2i

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

fun createCameraMatrix(dimensions: Vector2i, camera: Camera): Matrix {
  val projection = if (camera.projectionType == ProjectionType.orthographic)
    createOrthographicMatrix(dimensions, camera.angleOrZoom, camera.nearClip, camera.farClip)
  else
    createPerspectiveMatrix(dimensions, camera.angleOrZoom, camera.nearClip, camera.farClip)

  val view = createViewMatrix(camera.position, camera.orientation)
  return projection * view
}

fun createCameraEffectsData(dimensions: Vector2i, camera: Camera) =
    CameraEffectsData(
        createCameraMatrix(dimensions, camera),
//        if (camera.lookAt != null)
//          camera.lookAt!! - camera.position
//        else
          camera.orientation * Vector3(1f, 0f, 0f)
    )
