package mythic.bloom.next

import mythic.bloom.*
import org.joml.Vector2i
import org.joml.minus
import org.joml.plus

private val emptyBoxList: List<Box> = listOf()

val emptyBounds = Bounds(0, 0, 0, 0)

typealias AnyEvent = Any

data class Box(
    val name: String = "",
    val bounds: Bounds,
    val boxes: List<Box> = emptyBoxList,
    val depiction: Depiction? = null,
    val clipBounds: Boolean = false,
    val logic: LogicModuleOld? = null,
    val onClick: List<AnyEvent> = listOf()
)

data class Seed(
    val bag: StateBag = mapOf(),
    val dimensions: Vector2i,
    val clipBounds: Bounds? = null
)

typealias Flower = (Seed) -> Box

typealias FlowerWrapper = (Flower) -> Flower

typealias ForwardLayout = (Vector2i) -> Bounds

typealias ReverseLayout = (Vector2i, Bounds, Bounds) -> Bounds

val emptyBox = Box(
    bounds = Bounds(
        dimensions = Vector2i()
    )
)

val emptyFlower: Flower = { emptyBox }

fun div(name: String = "",
        forward: ForwardLayout = forwardPass,
        reverse: ReverseLayout = reversePass,
        depiction: Depiction? = null,
        logic: LogicModuleOld? = null): FlowerWrapper = { flower ->
  { seed ->
    if (name == "dialog") {
      val k = 0
    }
    val bounds = forward(seed.dimensions)
    val childSeed = seed.copy(
        dimensions = bounds.dimensions
    )
    val childBox = flower(childSeed)
    val finalBounds = reverse(seed.dimensions, bounds, childBox.bounds)

    Box(
        name = name,
        bounds = finalBounds,
        boxes = listOf(childBox),
        depiction = depiction,
        logic = logic
    )
  }
}

fun div(name: String = "",
        layout: DualLayout,
        depiction: Depiction? = null,
        logic: LogicModuleOld? = null): FlowerWrapper = { flower ->
  { seed ->
    val (childBox, bounds) = layout(seed, flower)
    Box(
        name = name,
        bounds = bounds,
        boxes = listOf(childBox),
        depiction = depiction,
        logic = logic
    )
  }
}

fun compose(flowers: List<Flower>): Flower = { seed ->
  Box(
      bounds = Bounds(dimensions = seed.dimensions),
      boxes = flowers.map { it(seed) }
  )
}

fun compose(vararg flowers: Flower): Flower = { seed ->
  Box(
      bounds = Bounds(dimensions = seed.dimensions),
      boxes = flowers.map { it(seed) }
  )
}

infix fun Flower.plusFlower(second: Flower): Flower =
    compose(this, second)

fun dependentBoundsTransform(transform: (Vector2i, Bounds, Bounds) -> Vector2i): ReverseLayout = { parent, bounds, child ->
  val offset = transform(parent, bounds, child)
  moveBounds(offset, bounds.dimensions)(child)
}

fun fixed(value: Int): PlanePositioner = { plane -> { value } }

infix fun Flower.plusLogic(logic: LogicModuleOld): Flower = { seed ->
  val box = this(seed)
  val newLogic = if (box.logic == null)
    logic
  else
    box.logic combineLogic logic

  box.copy(
      logic = newLogic
  )
}

fun ForwardLayout.plus2(other: ForwardLayout): ForwardLayout = { container ->
  val a = this(container)
  val b = other(a.dimensions)
  Bounds(
      position = a.position + b.position,
      dimensions = b.dimensions
  )
}

val shrinkWrap: ReversePlanePositioner = { plane ->
  { parent, bounds, child ->
    plane.x(child.dimensions)
  }
}

fun margin(all: Int = 0, left: Int = all, top: Int = all, bottom: Int = all, right: Int = all): FlowerWrapper = div(
    layout = { seed, flower ->
      val sizeOffset = Vector2i(left + right, top + bottom)
      val childSeed = seed.copy(
          dimensions = seed.dimensions - sizeOffset
      )
      val childBox = flower(childSeed)
      val finalBounds = Bounds(
          dimensions = childBox.bounds.dimensions + sizeOffset
      )
      val finalChildBox = childBox.copy(
          bounds = childBox.bounds.copy(
              position = childBox.bounds.position + Vector2i(left, top)
          )
      )
      Pair(finalChildBox, finalBounds)
    },
    name = "margin"
)

fun padding2(all: Int = 0, left: Int = all, top: Int = all, bottom: Int = all, right: Int = all): FlowerWrapper = div(
    forward = fixedOffset(left, top),
    reverse = { _, bounds, child ->
      bounds.copy(
          dimensions = child.dimensions + Vector2i(left + right, top + bottom)
      )
    }
)

fun padding(all: Int = 0, left: Int = all, top: Int = all, bottom: Int = all, right: Int = all): FlowerWrapper = { flower ->
  { seed ->
    val paddingWidth = left + right
    val paddingHeight = top + bottom
    val paddingDimensions = Vector2i(paddingWidth, paddingHeight)
    val childDimensions = seed.dimensions - paddingDimensions
    val childSeed = seed.copy(
        dimensions = childDimensions
    )
    val box = flower(childSeed)
    val offsetBox = box.copy(
        bounds = box.bounds.copy(
            position = box.bounds.position + Vector2i(left, top)
        )
    )
    Box(
        name = "padding",
        bounds = Bounds(dimensions = box.bounds.dimensions + paddingDimensions),
        boxes = listOf(offsetBox)
    )
  }
}

fun accumulatedBounds(boxes: List<Box>): Bounds {
  assert(boxes.any())
  val start = boxes.first().bounds.position
  val end = boxes.sortedByDescending { it.bounds.end.y }.first().bounds.end
  return Bounds(start, end - start)
}

fun depictBehind2(depiction: Depiction): (Flower) -> Flower = { flower ->
  { seed ->
    val box = flower(seed)
    val boxDepiction = box.depiction
    box.copy(
        depiction = { b, c ->
          depiction(b, c)
          if (boxDepiction != null)
            boxDepiction(b, c)
        }
    )
  }
}

infix fun Flower.depictBehind(depiction: Depiction): Flower =
    depictBehind2(depiction)(this)

fun breakReverse(child: Flower): Flower {
  return { seed ->
    val box = child(seed)
    Box(
        name = "breakReverse",
        bounds = Bounds(dimensions = Vector2i()),
        boxes = listOf(box)
    )
  }
}
