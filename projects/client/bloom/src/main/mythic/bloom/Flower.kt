package mythic.bloom

import mythic.bloom.next.Box
import mythic.bloom.next.Flower
import mythic.bloom.next.Seed

fun maxBounds(a: Bounds, b: Bounds): Bounds {
  val x1 = Math.min(a.position.x, b.position.x)
  val y1 = Math.min(a.position.y, b.position.y)
  val x2 = Math.max(a.position.x + a.dimensions.x, b.position.x + b.dimensions.x)
  val y2 = Math.max(a.position.y + a.dimensions.y, b.position.y + b.dimensions.y)
  return Bounds(x1, y1, x2 - x1, y2 - y1)
}

fun depict(depiction: StateDepiction): Flower = { seed ->
  Box(
      bounds = Bounds(dimensions = seed.dimensions),
      depiction = depiction(seed),
      name = "depiction"
  )
}

fun depict(name: String, depiction: StateDepiction): Flower = { seed ->
  Box(
      bounds = Bounds(dimensions = seed.dimensions),
      depiction = depiction(seed),
      name = name
  )
}

fun depict(depiction: Depiction): Flower =
    depict { s: Seed -> depiction }

fun depict(name: String, depiction: Depiction): Flower =
    depict(name) { s: Seed -> depiction }

typealias StateDepiction = (Seed) -> Depiction

inline fun <reified T> existingOrNewState(crossinline initializer: () -> T): (Any?) -> T = { state ->
  if (state is T)
    state
  else
    initializer()
}

typealias BagGetter<T> = (StateBag) -> T

inline fun <reified T> existingOrNewState(key: String, crossinline initializer: () -> T): BagGetter<T> = { bag ->
  val value = bag[key]
  if (value is T)
    value
  else
    initializer()
}

