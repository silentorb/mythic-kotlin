package silentorb.mythic.bloom

typealias DualLayout = (Seed, Flower) -> Pair<Box, Bounds>

//fun layoutDimensions(width: PlanePositioner? = null, height: PlanePositioner? = null): DualLayout = { dimensions, flower ->
//  val childDimensions = Vector2i(
//      if (width != null) width(horizontalPlaneMap)(dimensions) else dimensions.x,
//      if (height != null) height(verticalPlaneMap)(dimensions) else dimensions.y
//  )
//  val childSeed = childDimensions
//  val childBox = flower(childSeed)
//  val finalBounds = Bounds(
//      dimensions = Vector2i(
//          if (width != null) childDimensions.x else childBox.dimensions.x,
//          if (height != null) childDimensions.y else childBox.dimensions.y
//      )
//  )
//  Pair(childBox, finalBounds)
//}