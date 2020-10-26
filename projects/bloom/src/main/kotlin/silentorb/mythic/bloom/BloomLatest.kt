package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

typealias AnyEvent = Any

val emptyBox = Box(
    dimensions = Vector2i()
)

fun compose(flowers: List<Flower>): Flower = { dimensions ->
  Box(
      dimensions = dimensions,
      boxes = flowers.map { OffsetBox(it(dimensions)) }
  )
}

fun compose(vararg flowers: Flower): Flower = { dimensions ->
  Box(
      dimensions = dimensions,
      boxes = flowers.map { OffsetBox(it(dimensions)) }
  )
}

fun compose(vararg boxes: Box): Box =
    Box(
        dimensions = mergeDimensions(boxes.toList()),
        boxes = boxes.map { OffsetBox(it, Vector2i.zero) }
    )

infix fun Flower.plusFlower(second: Flower): Flower =
    compose(this, second)

fun depictBehind2(depiction: Depiction): (Flower) -> Flower = { flower ->
  { dimensions ->
    val box = flower(dimensions)
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

