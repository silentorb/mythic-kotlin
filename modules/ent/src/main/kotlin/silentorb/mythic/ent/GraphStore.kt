package silentorb.mythic.ent

interface GraphStore {
  fun filterByProperty(property: String): List<AnyEntry>
  fun <V> filterByPropertyAndValue(property: String, value: V): List<Any>
  fun filterByEntity(entity: Any): List<AnyEntry>
  fun filterByEntityAndProperty(entity: Any, property: String): List<AnyEntry>

  fun firstOrNullByProperty(property: String): AnyEntry?
  fun <V> firstOrNullByPropertyAndValue(property: String, value: V): AnyEntry?
  fun firstOrNullByEntity(entity: Any): AnyEntry?
  fun firstOrNullByEntityAndProperty(entity: Any, property: String): AnyEntry?

  fun toCollection(): Collection<AnyEntry>

  operator fun plus(value: AnyEntry): GraphStore
  operator fun plus(value: AnyGraph): GraphStore
  operator fun minus(value: AnyEntry): GraphStore
  operator fun minus(value: AnyGraph): GraphStore
}

operator fun GraphStore.plus(value: GraphStore): GraphStore =
    this + value.toCollection()

operator fun GraphStore.minus(value: GraphStore): GraphStore =
    this - value.toCollection()
