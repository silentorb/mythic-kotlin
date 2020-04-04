package silentorb.mythic.lookinglass

import silentorb.mythic.spatial.Vector4

enum class MaterialPrimitiveType {
  points,
  lines,
  polygons
}

data class Material(
    val color: Vector4? = null,
    val glow: Float = 0f,
    val texture: String? = null,
    val shading: Boolean,
    val primitiveType: MaterialPrimitiveType = MaterialPrimitiveType.polygons
)
