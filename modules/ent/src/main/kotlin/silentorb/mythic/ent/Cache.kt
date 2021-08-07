package silentorb.mythic.ent

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

fun <Key, Value> mappedCache(limit: Int, source: (Key) -> Value): (Key) -> Value {
  val cache: MutableMap<Key, Value> = mutableMapOf()
  return { key ->
    val existing = cache[key]
    if (existing != null)
      existing
    else {
      if (limit > 0 && cache.size >= limit) {
        cache.entries
            .take(limit / 10)
            .forEach { cache.remove(it.key) }
      }
      val newEntry = source(key)
      cache[key] = newEntry
      newEntry
    }
  }
}

fun <Key, Value> mappedCache(source: (Key) -> Value,): (Key) -> Value =
    mappedCache(0, source)

// Only stores one value at a time.  Using a different input value overwrites the previously stored output value.
// Is not thread safe!
fun <Artifact, Output> singleValueCache(): (Artifact, (Artifact) -> Output) -> Output {
  var lastInput: Int? = null
  var lastOutput: Output? = null
  return { artifact, source ->
    val hashCode = artifact.hashCode()
    if (hashCode == lastInput)
      lastOutput!!
    else {
      lastInput = hashCode
      val newOutput = source(artifact)
      lastOutput = newOutput
      newOutput
    }
  }
}

// Only stores one value at a time.  Using a different input value overwrites the previously stored output value.
// Is not thread safe
fun <Input, Output> singleValueCache(source: (Input) -> Output): (Input) -> Output {
  val cache = singleValueCache<Input, Output>()
  return { input ->
    cache(input) { source(input) }
  }
}
