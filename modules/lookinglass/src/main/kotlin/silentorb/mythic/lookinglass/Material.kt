package silentorb.mythic.lookinglass

import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.spatial.Vector4

enum class MaterialPrimitiveType {
  points,
  lines,
  polygons
}

data class Material(
    val shading: Boolean,
    val color: Vector4? = null,
    val glow: Float = 0f,
    val texture: String? = null,
    val coloredVertices: Boolean = false,
    val drawMethod: DrawMethod = DrawMethod.triangleFan,
    val containsTransparency: Boolean = false,
    val doubleSided: Boolean = false,
)
