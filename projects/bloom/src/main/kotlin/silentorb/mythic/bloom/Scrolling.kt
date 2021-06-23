package silentorb.mythic.bloom

import silentorb.mythic.haft.DeviceIndexes
import silentorb.mythic.haft.MouseCommands
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.minMax
import kotlin.math.max

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

fun findNestedBox(box: OffsetBox, predicate: (OffsetBox) -> Boolean): OffsetBox? =
    if (predicate(box))
      box
    else
      box.boxes
          .mapNotNull {
            val result = findNestedBox(it, predicate)
            result?.copy(
                offset = result.offset + box.offset
            )
          }
          .firstOrNull()

fun scrollableY(key: String, content: Flower): Flower = { seed ->
  val box = content(seed)
  val spacing = 10
  val inset = scrollbarWidth + spacing
  val c = if (box.dimensions.y == 0) 1 else box.dimensions.y
  val v = seed.dimensions.y
  val scrollBarHeight = seed.dimensions.y * v / c
  val dragKey = "${key}_drag"
  val isDragging = seed.state.containsKey(dragKey)
  val pageIncrement = seed.dimensions.y / 8
  val scrollMax = box.dimensions.y - seed.dimensions.y
  val constrainOffset = { offset: Int -> minMax(0, scrollMax, offset) }
  val initialContentOffset = seed.state[key] as? Int ?: 0
  val initialClippedContentOffset = constrainOffset(initialContentOffset)

  val focusIndex = getFocusIndex(seed.state)
  val focusedBox = if (getFocusIndex(seed.previousState) != focusIndex)
    findNestedBox(OffsetBox(box)) { b ->
      b.child.attributes[menuItemIndexKey] == focusIndex
    }
  else
    null

  val contentOffset = if (focusedBox != null) {
    val upperHidden = initialClippedContentOffset - focusedBox.bounds.top
    val lowerHidden = focusedBox.bounds.bottom - seed.dimensions.y - initialClippedContentOffset
    val pageAdustment = (pageIncrement.toFloat() * 3.5f).toInt()
    if (upperHidden > 0) {
      val adjustment = max(upperHidden, pageAdustment)
      constrainOffset(initialContentOffset - adjustment)
    } else if (lowerHidden > 0) {
      val adjustment = max(lowerHidden, pageAdustment)
      constrainOffset(initialContentOffset + adjustment)
    } else
      initialContentOffset
  } else
    initialContentOffset

  val logic = composeLogic(
      listOfNotNull(
          if (!isDragging)
            onHover(
                onInputEvent(DeviceIndexes.mouse) { event ->
                  when (event.index) {
                    MouseCommands.scrollDown -> mapOf(key to constrainOffset(contentOffset + pageIncrement))
                    MouseCommands.scrollUp -> mapOf(key to constrainOffset(contentOffset - pageIncrement))
                    else -> mapOf()
                  }
                }
            )
          else
            null,
          if (contentOffset != initialContentOffset)
            ({ _, _ -> mapOf(key to contentOffset) })
          else
            null
      )
  )

  val clippedContentOffset = constrainOffset(contentOffset)
  val scrollBarOffset = clippedContentOffset * v / c

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
      logic = logic,
  )
      .addAttributes(clipBoundsKey to true)
}
