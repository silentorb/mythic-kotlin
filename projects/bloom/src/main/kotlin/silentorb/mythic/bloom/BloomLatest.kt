package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

typealias AnyEvent = Any

val emptyBox = Box(
    dimensions = Vector2i()
)

val emptyFlower: Flower = { emptyBox }

fun compose(flowers: List<Flower>): Flower = { seed ->
  Box(
      dimensions = seed.dimensions,
      boxes = flowers.map { OffsetBox(it(seed)) }
  )
}

fun compose(vararg flowers: Flower): Flower = { seed ->
  Box(
      dimensions = seed.dimensions,
      boxes = flowers.map { OffsetBox(it(seed)) }
  )
}

fun compose(vararg boxes: Box): Box =
    Box(
        dimensions = mergeDimensions(boxes.toList()),
        boxes = boxes.map { OffsetBox(it, Vector2i.zero) }
    )

infix fun Flower.plusFlower(second: Flower): Flower =
    compose(this, second)

infix fun Box.depictBehind(depiction: Depiction): Box {
  val boxDepiction = this.depiction
  return this.copy(
      depiction = { b, c ->
        depiction(b, c)
        if (boxDepiction != null)
          boxDepiction(b, c)
      }
  )
}

fun <T> depictBehind(depiction: Depiction, flower: BoxSource<T>): BoxSource<T> = { dimensions ->
  flower(dimensions) depictBehind depiction
}

inline infix fun <reified T> BoxSource<T>.depictBehind(noinline depiction: Depiction): BoxSource<T> =
    depictBehind(depiction, this)

