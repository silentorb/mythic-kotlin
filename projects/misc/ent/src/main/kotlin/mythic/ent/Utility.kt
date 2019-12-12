package mythic.ent

fun <T, N> firstSortedBy(comparison: (N, N) -> Boolean): (Sequence<T>, (T) -> N) -> T = { list, accessor ->
  var result = list.first()
  var value = accessor(result)
  for (item in list.asSequence().drop(1)) {
    val nextValue = accessor(item)
    if (comparison(nextValue, value)) {
      result = item
      value = nextValue
    }
  }
  result
}

fun <T> Collection<T>.firstFloatSortedBy(accessor: (T) -> Float): T =
    firstSortedBy<T, Float> { a, b -> a < b }(this.asSequence(), accessor)

fun <T> Collection<T>.firstFloatSortedByDescending(accessor: (T) -> Float): T =
    firstSortedBy<T, Float> { a, b -> a > b }(this.asSequence(), accessor)

fun <T> Collection<T>.firstIntSortedBy(accessor: (T) -> Int): T =
    firstSortedBy<T, Int> { a, b -> a < b }(this.asSequence(), accessor)

fun <T> Collection<T>.firstIntSortedByDescending(accessor: (T) -> Int): T =
    firstSortedBy<T, Int> { a, b -> a > b }(this.asSequence(), accessor)

fun <T> Sequence<T>.firstFloatSortedBy(accessor: (T) -> Float): T =
    firstSortedBy<T, Float> { a, b -> a < b }(this, accessor)

fun <T, B> Collection<T>.firstNotNull(mapper: (T) -> B?): B? {
  for (item in this) {
    val result = mapper(item)
    if (result != null)
      return result
  }
  return null
}

fun <T> replaceSingle(collection: Collection<T>, newItem: T, matches: (T, T) -> Boolean) =
    collection.map {
      if (matches(it, newItem))
        newItem
      else
        it
    }

fun <T> transformNotNull(value: T?, action: (T) -> T): T? =
    if (value != null)
      action(value)
    else
      null

fun <T> pass(value: T): T = value

fun <T> pipe2(initial: T, steps: List<(T) -> T>): T =
    steps.fold(initial) { a, b -> b(a) }

fun <T> pipe(vararg steps: (T) -> T): (T) -> T = {
  steps.fold(it) { a, b -> b(a) }
}

fun <T> pipe2(steps: List<(T) -> T>): (T) -> T = {
  steps.fold(it) { a, b -> b(a) }
}

fun <T> peek(transform: (T) -> (T) -> T): (T) -> T = {
  transform(it)(it)
}

fun <T> transformIf(condition: (T) -> Boolean, transform: (T) -> T): (T) -> T = {
  if (condition(it))
    transform(it)
  else
    it
}

fun <T, A> ifNotNull(getter: (T) -> A?, transform: (A) -> (T) -> T): (T) -> T = {
  val value = getter(it)
  if (value != null)
    transform(value)(it)
  else
    it
}

fun <T> transformIf(condition: (T) -> Boolean, transform: (T) -> T, elseTransform: (T) -> T): (T) -> T = {
  if (condition(it))
    transform(it)
  else
    elseTransform(it)
}

fun <T> replace(collection: Collection<T>, condition: (T) -> Boolean, transform: (T) -> T) =
    collection.map { item ->
      if (condition(item))
        transform(item)
      else
        item
    }


fun <T> replaceIndex(collection: Collection<T>, index: Int, newValue: T) =
    collection.mapIndexed { i, item ->
      if (i == index)
        newValue
      else
        item
    }
