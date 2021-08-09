package silentorb.mythic.editing.main

enum class OperationType {
  translate,
  rotate,
  scale,
  connecting,
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
