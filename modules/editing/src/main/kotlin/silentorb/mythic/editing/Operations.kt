package silentorb.mythic.editing

enum class OperationType {
  translate,
  rotate,
  scale,
}

enum class Axis {
  x,
  y,
  z,
}

data class Operation(
    val type: OperationType,
    val data: Any
)

data class SpatialTransformState(
    val axis: Set<Axis> = setOf()
)
