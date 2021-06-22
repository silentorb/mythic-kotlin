package silentorb.mythic.bloom

import silentorb.mythic.haft.DeviceIndexes
import silentorb.mythic.haft.MouseCommands
import silentorb.mythic.spatial.*

const val scrollbarWidth = 15

fun pruneClippedBoxes(dimensions: Vector2i, offset: Vector2i, boxes: List<OffsetBox>): List<OffsetBox> {
  val filtered = boxes
      .filter { box ->
        val result = offset.y + box.bounds.top <= dimensions.y && offset.y + box.bounds.bottom >= 0
        result
      }

  return filtered
      .map { box ->
        box.copy(
            child = box.child.copy(
                boxes = pruneClippedBoxes(dimensions, offset + box.bounds.position, box.boxes)
            )
        )
      }
}

fun pruneClippedBoxes(dimensions: Vector2i, offset: Vector2i, box: OffsetBox): OffsetBox =
    box.copy(
        child = box.child.copy(
            boxes = pruneClippedBoxes(dimensions, offset, box.boxes)
        )
    )

fun scrollableY(key: String, content: Flower): Flower = { seed ->
  val spacing = 10
  val inset = scrollbarWidth + spacing
  val box = content(seed)
  val c = if (box.dimensions.y == 0) 1 else box.dimensions.y
  val v = seed.dimensions.y
  val scrollBarHeight = seed.dimensions.y * v / c
  val contentOffset = seed.state[key] as? Int ?: 0
  val scrollMax = box.dimensions.y - seed.dimensions.y
  val clippedContentOffset = minMax(0, scrollMax, contentOffset)
  val scrollBarOffset = clippedContentOffset * v / c
  val dragKey = "${key}_drag"
  val isDragging = seed.state.containsKey(dragKey)
  val pageIncrement = seed.dimensions.y / 8

  Box(
      dimensions = Vector2i(box.dimensions.x + inset, seed.dimensions.y),
      boxes = listOf(
          pruneClippedBoxes(seed.dimensions, Vector2i(0, -clippedContentOffset),
          OffsetBox(
              child = box,
              offset = Vector2i(0, -clippedContentOffset)
              )
          ),
          OffsetBox(
              child = Box(
                  dimensions = Vector2i(scrollbarWidth, scrollBarHeight),
                  depiction = solidBackground(Vector4(0f, 0f, 0f, 0.9f)),
                  logic = composeLogic(
                      onDrag(dragKey) { offset ->
                        val value = if (offset == null)
                          clippedContentOffset
                        else
                          contentOffset + offset.y * c / v

                        mapOf(key to value)
                      },
                  )
              ),
              offset = Vector2i(box.dimensions.x + spacing, scrollBarOffset)
          )
      ),
      logic = if (!isDragging)
        onHover(
            onInputEvent(DeviceIndexes.mouse) { event ->
              when (event.index) {
                MouseCommands.scrollDown -> mapOf(key to minMax(0, scrollMax, contentOffset + pageIncrement))
                MouseCommands.scrollUp -> mapOf(key to minMax(0, scrollMax, contentOffset - pageIncrement))
                else -> mapOf()
              }
            }
        )
      else
        null
  )
      .addAttributes(clipBoundsKey to true)
}
