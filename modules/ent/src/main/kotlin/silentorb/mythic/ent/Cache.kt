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

// Only stores one value at a time.  Using a different input value overwrites the previously stored output value.
fun <Input, Output> singleValueCache(source: (Input) -> Output): (Input) -> Output {
  var lastInput: Int? = null
  var lastOutput: Output? = null
  return { input ->
    val hashCode = input.hashCode()
    if (hashCode == lastInput)
      lastOutput!!
    else {
      lastInput = hashCode
      val newOutput = source(input)
      lastOutput = newOutput
      newOutput
    }
  }
}