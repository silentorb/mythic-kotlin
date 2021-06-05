package silentorb.mythic.ent

// Caches graph hash so it doesn't need to be recalculated when checked for caching
class GraphWrapper(val value: Graph) {
  val graphHash: Int = value.hashCode()

  override fun hashCode(): Int {
    return graphHash
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as GraphWrapper
    if (graphHash != other.graphHash) return false
    return true
  }
}
