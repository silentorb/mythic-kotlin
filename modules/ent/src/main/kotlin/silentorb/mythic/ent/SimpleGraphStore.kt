package silentorb.mythic.ent

data class SimpleGraphStore(
    val collection: Set<AnyEntry> = setOf(),
    val manyToManyProperties: Set<String> = setOf()
) : GraphStore {
  override fun filterByProperty(property: String): List<AnyEntry> =
      collection.filter { it.property == property }

  override fun <V> filterByPropertyAndValue(property: String, value: V): List<AnyEntry> =
      collection.filter { it.property == property && it.target == value }

  override fun filterByEntity(entity: Any): List<AnyEntry> =
      collection.filter { it.source == entity }

  override fun filterByEntityAndProperty(entity: Any, property: String): List<AnyEntry> =
      collection.filter { it.source == entity && it.property == property }

  override fun firstOrNullByProperty(property: String): AnyEntry? =
      collection.firstOrNull { it.property == property }

  override fun <V> firstOrNullByPropertyAndValue(property: String, value: V): AnyEntry? =
      collection.firstOrNull { it.property == property && it.target == value }

  override fun firstOrNullByEntity(entity: Any): AnyEntry? =
      collection.firstOrNull { it.source == entity }

  override fun firstOrNullByEntityAndProperty(entity: Any, property: String): AnyEntry? =
      collection.firstOrNull { it.source == entity && it.property == property }

  override fun toCollection(): Collection<AnyEntry> =
      collection

  override fun plus(value: AnyEntry): GraphStore =
      this.copy(collection = collection.plusElement(value))

  override fun plus(value: Collection<AnyEntry>): GraphStore =
      this.copy(collection = collection + value)

  override fun minus(value: AnyEntry): GraphStore =
      this.copy(collection = collection - value)

  override fun minus(value: Collection<AnyEntry>): GraphStore =
      this.copy(collection = collection - value)
}
