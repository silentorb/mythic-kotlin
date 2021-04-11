package silentorb.mythic.ent

typealias Id = Long

const val emptyId = 0L

typealias IdSource = () -> Id

data class SharedNextId(
    var value: Id,
) {
  fun source(): IdSource = { value++ }
}

fun newIdSource(initialValue: Id): IdSource {
  var nextId: Id = initialValue
  return { nextId++ }
}

fun <K, V, O> mapEntry(transform: (K, V) -> O): (Map.Entry<K, V>) -> O = { entry ->
  transform(entry.key, entry.value)
}

fun <K, V, O> mapEntryValue(transform: (V) -> O): (Map.Entry<K, V>) -> O = { entry ->
  transform(entry.value)
}
