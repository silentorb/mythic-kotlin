package silentorb.mythic.bloom.old

import silentorb.mythic.bloom.*
import silentorb.mythic.spatial.*
import silentorb.mythic.spatial.Vector2i

//fun clipBox(clipBounds: Bounds): (FlatBox) -> FlatBox = { box ->
//  val depiction = if (box.depiction != null)
//    clipBox(clipBounds, box.depiction)
//  else
//    null
//
//  box.copy(
//      depiction = depiction,
//      clipBounds = clipBounds
//  )
//}

data class ScrollingState(
    val dragOrigin: Vector2i?,
    val offsetOrigin: Int,
    val offset: Int
)

val scrollingState = existingOrNewState {
  ScrollingState(
      dragOrigin = null,
      offsetOrigin = 0,
      offset = 0
  )
}

//fun extractOffset(key: String, input: (Vector2i) -> Flower): Flower = { dimensions ->
//  val state = scrollingState(dimensions.bag[key])
//  input(Vector2i(0, -state.withOffset))(dimensions)
//}

fun extractOffset(key: String, bag: StateBag): Vector2i {
  val state = scrollingState(bag[key])
  return Vector2i(0, -state.offset)
}

//fun scrollingInteraction(key: String, contentBounds: Bounds): LogicModuleOld = { (bloomState, bounds) ->
//  if (contentBounds.dimensions.y <= bounds.dimensions.y) {
//    null
//  } else {
//    val state = scrollingState(bloomState.resourceBag[key])
//    val input = bloomState.input
//    val currentButton = input.current.mouseButtons[0]
//    val previousButton = input.previous.mouseButtons[0]
////    val clip = minMax(0, contentBounds.dimensions.y - bounds.dimensions.y)
//
//    val (dragOrigin, offsetOrigin) = if (currentButton == ButtonState.down && previousButton == ButtonState.up
//        && isInBounds(input.current.mousePosition, bounds))
//      Pair(input.current.mousePosition, state.offset)
//    else if (currentButton == ButtonState.up)
//      Pair(null, state.offset) // Reclip the bounds in case the layout was changed independent of this code
//    else
//      Pair(state.dragOrigin, state.offsetOrigin)
//
//    val offset = if (dragOrigin != null) {
//      val mouseOffsetY = input.current.mousePosition.y - dragOrigin.y
//      val mod = offsetOrigin + mouseOffsetY * contentBounds.dimensions.y / bounds.dimensions.y
////    println(mod)
//      clip(mod)
//    } else
//      state.offset
//
//    val newState = ScrollingState(
//        dragOrigin = dragOrigin,
//        offsetOrigin = offsetOrigin,
//        offset = clip(offset)
//    )
//    mapOf(key to newState)
//  }
//}

//fun scrollBox(key: String, contentBounds: Bounds): Flower = { dimensions ->
//  val bounds = Bounds(dimensions = dimensions)
//  Box(
//      name = "scroll box",
//      bounds = bounds,
////      depiction = scrollbar(scrollingState(dimensions.bag[key]).offset, contentBounds.dimensions.y)
//  )
//}

//private fun pruneClippedBoxes(dimensions: Vector2i, offset: Vector2i, boxes: List<Box>): List<Box> {
//  return boxes.filter { box ->
//    val result = offset.y + box.bounds.top <= dimensions.y && offset.y + box.bounds.bottom >= 0
//    if (boxes.size > 16) {
//      val k = 0
//    }
//    if (!result) {
//      val k = 0
//    }
//    result
//  }
//      .map { box ->
//        box.copy(
//            boxes = pruneClippedBoxes(box.bounds.dimensions, offset + box.bounds.position, box.boxes)
//        )
//      }
//}

//private fun reverseFixedOffset(left: Int = 0, top: Int = 0): FlowerWrapper = div(
//    reverse = { _, bounds, child ->
//      bounds.copy(
//          position = Vector2i(left, top),
//          dimensions = child.dimensions
//      )
//    }
//)

fun scrolling(key: String): (Flower) -> Flower = { child ->
  { dimensions ->
//    val clippedDimensions = Vector2i(
//        dimensions.x - scrollbarWidth - 5,
//        dimensions.y
//    )
//    val innerSeed = clippedDimensions
    throw Error("No longer supported.  Needs updating to not use Seed.bag")
//    val offset = extractOffset(key, dimensions.bag)
//    val box = reverseFixedOffset(top = offset.y)(child)(innerSeed)
//    if (box.boxes.any()) {
//      val contentBounds = accumulatedBounds(box.boxes)
//      if (contentBounds.dimensions.y <= clippedDimensions.y) {
//        box
//      } else {
//        val result = scrollBox(key, contentBounds)(dimensions)
//        result.copy(
//            boxes = listOf(
//                Box(
//                    name = "clipped box",
//                    bounds = result.bounds.copy(
//                        dimensions = clippedDimensions
//                    ),
//                    boxes = pruneClippedBoxes(clippedDimensions, Vector2i(), listOf(box)),
//                    clipBounds = true
//                )
//            )
////              result.boxes.plus(box)
//        )
//      }
//
//    } else {
//      emptyBox
//    }
  }
}
