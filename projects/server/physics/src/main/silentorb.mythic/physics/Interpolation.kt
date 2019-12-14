package silentorb.mythic.physics

import silentorb.mythic.ent.Table

fun <T> interpolateTables(scalar: Float, first: Table<T>, second: Table<T>, action: (Float, T, T) -> T): Table<T> =
    first.keys.plus(second.keys).associateWith { key ->
      val a = first[key]
      val b = second[key]
      if (a != null && b != null)
        action(scalar, a, b)
      else
        a ?: b!!
    }
