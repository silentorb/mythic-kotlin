package silentorb.mythic.editing

enum class SpatialTransformType {
  translate,
  rotate,
  scale,
}

enum class Axis {
  x,
  y,
  z,
}

data class SpatialTransformOperation(
    val type: SpatialTransformType,
    val axis: Set<Axis>
) {
  init {
    assert(axis.any())
  }
}
