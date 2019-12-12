package mythic.bloom.next

import mythic.bloom.Bounds
import mythic.bloom.PlanePositioner
import mythic.bloom.horizontalPlaneMap
import mythic.bloom.verticalPlaneMap
import org.joml.Vector2i

typealias DualLayout = (Seed, Flower) -> Pair<Box, Bounds>

fun layoutDimensions(width: PlanePositioner? = null, height: PlanePositioner? = null): DualLayout = { seed, flower ->
  val childDimensions = Vector2i(
      if (width != null) width(horizontalPlaneMap)(seed.dimensions) else seed.dimensions.x,
      if (height != null) height(verticalPlaneMap)(seed.dimensions) else seed.dimensions.y
  )
  val childSeed = seed.copy(
      dimensions = childDimensions
  )
  val childBox = flower(childSeed)
  val finalBounds = Bounds(
      dimensions = Vector2i(
          if (width != null) childDimensions.x else childBox.bounds.dimensions.x,
          if (height != null) childDimensions.y else childBox.bounds.dimensions.y
      )
  )
  Pair(childBox, finalBounds)
}
