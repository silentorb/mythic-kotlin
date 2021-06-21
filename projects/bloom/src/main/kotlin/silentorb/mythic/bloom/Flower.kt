package silentorb.mythic.bloom

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
