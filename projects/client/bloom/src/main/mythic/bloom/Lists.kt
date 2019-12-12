package mythic.bloom

import mythic.bloom.next.Box
import mythic.bloom.next.Flower
import mythic.bloom.next.Seed
import org.joml.Vector2i
import org.joml.minus
import org.joml.plus

typealias FixedChildArranger = (Vector2i) -> List<Bounds>
typealias ParentFlower = (List<Flower>) -> Flower

typealias PreparedChildren<T> = Triple<Collection<T>, List<Flower>, List<Bounds>>

fun applyBounds(bag: StateBag, children: List<Flower>, bounds: List<Bounds>): List<Box> =
    bounds.zip(children) { b, child -> child(Seed(bag, b.dimensions)).boxes }
        .flatten()

fun applyBounds(arranger: FixedChildArranger): (List<Flower>) -> Flower = { flowers ->
  { seed ->
    Box(
        boxes = applyBounds(seed.bag, flowers, arranger(seed.dimensions)),
        bounds = emptyBounds
    )
  }
}

fun list(plane: Plane, spacing: Int = 0, drawReversed: Boolean = false, name: String = "list"): (List<Flower>) -> Flower = { children ->
  { seed ->
    var lastOffset = 0
    var otherLength = 0
    if (name == "dialog-list") {
      val k = 0
    }
    val boxes = children.mapIndexed { i, flower ->
      val offsetVector = plane(Vector2i(lastOffset, 0))
      val localSeed = seed.copy(
          dimensions = seed.dimensions - offsetVector
      )
      val initialBox = flower(localSeed)
      val box = initialBox.copy(
          bounds = initialBox.bounds.copy(
              position = initialBox.bounds.position + offsetVector
          )
      )
      val childDimensions = plane(box.bounds.end)
      if (childDimensions.y > otherLength)
        otherLength = childDimensions.y

      lastOffset = childDimensions.x + if (i != children.size - 1) spacing else 0

      box
    }
    if (name == "dialog-list") {
      val k = 0
    }
    Box(
        name = name,
        boxes = if (drawReversed) boxes.reversed() else boxes,
        bounds = Bounds(
            dimensions = plane(Vector2i(lastOffset, otherLength))
        )
    )
  }
}

enum class FlexType {
  stretch,
  fixed,
}

data class FlexItem(
    val flower: Flower,
    val type: FlexType = FlexType.fixed
)

fun flexList(plane: Plane, spacing: Int = 0, name: String = "flexList"): (List<FlexItem>) -> Flower = { items ->
  { seed ->
    val totalSpacing = (items.size - 1) * spacing
    val firstPass = items.map { item ->
      if (item.type == FlexType.fixed) {
        item.flower(seed)
      } else
        null
    }
    val lengths = firstPass.map { if (it != null) plane(it.bounds.end).x else null }
    val otherLength = firstPass.filterNotNull().map { plane(it.bounds.end).y }.max()!!
    val resolvedDimensions = plane(Vector2i(0, otherLength)) + plane(Vector2i(plane(seed.dimensions).x, 0))
    val boundsList = fixedLengthArranger(plane, spacing, lengths)(resolvedDimensions)
    val boxes = firstPass.zip(boundsList.zip(items)) { box, (bounds, item) ->
      if (box != null) {
        box.copy(
            bounds = bounds
        )
      } else {
        val newBox = item.flower(seed.copy(dimensions = bounds.dimensions))
        newBox.copy(
            bounds = bounds
        )
      }
    }
    Box(
        name = name,
        boxes = boxes,
        bounds = Bounds(
            dimensions = resolvedDimensions
        )
    )
  }
}

fun fixedList(plane: Plane, spacing: Int, lengths: List<Int?>): ParentFlower = { flowers ->
  { seed ->
    val boundsList = fixedLengthArranger(plane, spacing, lengths)(seed.dimensions)
    Box(
        name = "fixedList",
        bounds = Bounds(dimensions = seed.dimensions),
        boxes = flowers.zip(boundsList) { flower, bounds ->
          val box = flower(seed.copy(dimensions = bounds.dimensions))
          box.copy(
              bounds = box.bounds.copy(
                  position = bounds.position + box.bounds.position
              )
          )
        }
    )
  }
}
