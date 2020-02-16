package silentorb.mythic.bloom.next

import silentorb.mythic.bloom.Bounds
import silentorb.mythic.bloom.PlanePositioner
import silentorb.mythic.bloom.horizontalPlaneMap
import silentorb.mythic.bloom.verticalPlaneMap
import silentorb.mythic.spatial.Vector2i

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
