package silentorb.mythic.ent

class HashedList<T>(val value: Collection<T>) : Collection<T> by value {
  val hash = value.hashCode()

  override fun hashCode(): Int {
    return hash
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (value === other) return true
    if (javaClass != other?.javaClass) return false

    other as HashedList<*>

    if (hash != other.hash) return false
    if (value.size != other.size) return false
    if (!value.equals(other)) return false

    return true
  }

  companion object {

    // Prevents redundant wrapping
    fun <T> from(collection: Collection<T>) =
        if (collection is HashedList<T>)
          collection
        else
          HashedList(collection)
  }
}
