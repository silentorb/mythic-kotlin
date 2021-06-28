package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

fun depictBox(dimensions: Vector2i, depiction: Depiction): Box =
    Box(
        name = "depiction",
        dimensions = dimensions,
        depiction = depiction,
    )

fun depict(depiction: StateDepiction): Flower = { seed ->
  Box(
      name = "depiction",
      dimensions = seed.dimensions,
      depiction = depiction(seed)
  )
}

fun depict(depiction: Depiction): Flower =
    depict { s: Seed -> depiction }

typealias StateDepiction = (Seed) -> Depiction

inline fun <reified T> existingOrNewState(crossinline initializer: () -> T): (Any?) -> T = { state ->
  if (state is T)
    state
  else
    initializer()
}
