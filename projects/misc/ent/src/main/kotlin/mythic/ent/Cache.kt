package mythic.ent

private val cacheMap: MutableMap<Int, Pair<Int, Any>> = mutableMapOf()

fun <I, O> functionCache(func: (I) -> O): (I) -> O {
  var entry: Pair<Int, O>? = null

  return { input ->
    val localEntry = entry
    if (localEntry != null && localEntry.first == input.hashCode() && localEntry.second != null) {
      localEntry.second
    } else {
      val key = input.hashCode()
      val output = func(input)
      entry = Pair(key, output)
      output
    }
  }
}

fun <Key, Value> mappedCache(source: (Key) -> Value): (Key) -> Value {
  val cache: MutableMap<Key, Value> = mutableMapOf()
  return { key ->
    val existing = cache[key]
    if (existing != null)
      existing
    else {
      val newEntry = source(key)
      cache[key] = newEntry
      newEntry
    }
  }
}

// Only stores one value at a time.  Using a different key overwrites the previously mappedCache value.
fun <Key, Value> singleCache(source: (Key) -> Value): (Key) -> Value {
  var lastKey: Key? = null
  var value: Value? = null
  return { key ->
    if (key == lastKey)
      value!!
    else {
      lastKey = key
      val newValue = source(key)
      value = newValue
      newValue
    }
  }
}