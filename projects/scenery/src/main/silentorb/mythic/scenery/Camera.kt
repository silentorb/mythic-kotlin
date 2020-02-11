package silentorb.mythic.scenery

import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3

enum class ProjectionType {
  orthographic,
  perspective
}

data class Camera(
    val projectionType: ProjectionType,
    val position: Vector3,
    val orientation: Quaternion,
    val angleOrZoom: Float,
    val nearClip: Float = 0.01f,
    val farClip: Float = 1000f,
    val lookAt: Vector3? = null
)
