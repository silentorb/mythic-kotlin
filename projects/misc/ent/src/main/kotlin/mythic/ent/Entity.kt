package mythic.ent

typealias Id = Long
typealias Table<T> = Map<Id, T>

typealias IdSource = () -> Id

fun <T : WithId> entityMap(list: Collection<T>): Map<Id, T> =
    list.associate { Pair(it.id, it) }

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
