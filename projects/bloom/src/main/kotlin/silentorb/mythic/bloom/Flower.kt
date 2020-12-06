package silentorb.mythic.bloom

fun depict(depiction: StateDepiction): Flower = { dimensions ->
  Box(
      name = "depiction",
      dimensions = dimensions,
      depiction = depiction(dimensions)
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
