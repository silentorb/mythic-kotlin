package silentorb.mythic.randomly

import silentorb.mythic.spatial.Vector2
import java.util.*

class Dice(val seed: Long? = null) {
  val random = Random(seed ?: System.currentTimeMillis())

  fun getInt(min: Int, max: Int) =
      if (min == max)
        min
      else
        min + random.nextInt(1 + max - min)

  fun getInt(max: Int) = getInt(0, max)

  fun getInt(value: IntRange) = getInt(value.first, value.last)

  fun getFloat() = random.nextFloat()

  fun getFloat(min: Float, max: Float) = min + getFloat() * (max - min)

  fun getFloat(max: Float) = getFloat(0f, max)

  fun get(max: Vector2) = Vector2(getFloat(max.x), getFloat(max.y))

  fun <T> takeOne(collection: Collection<T>): T {
    assert(collection.isNotEmpty())
    return if (collection.size == 1)
      collection.first()
    else
      collection.toList()[random.nextInt(collection.size)] // TODO: Optimize away the toList call
  }

  fun <T> takeOneOrNull(collection: Collection<T>): T? =
      if (collection.none())
        null
      else
        takeOne(collection)

  fun <T> take(list: Collection<T>, count: Int): List<T> {
    assert(count <= list.size)
    val result = mutableListOf<T>()
    val options = list.toMutableList()
    for (i in 1..count) {
      val item = takeOne(options)
      options.remove(item)
      result.add(item)
    }
    return result
  }

//  fun <T> take(list: Set<T>, count: Int): Set<T> {
//    assert(count <= list.size)
//    if (count == 0)
//      return setOf()
//
//    val result = mutableSetOf<T>()
//    val options = list.toMutableList()
//    for (i in 1..count) {
//      val item = takeOne(options)
//      options.remove(item)
//      result.add(item)
//    }
//    return result
//  }

  fun getBoolean(): Boolean = getInt(0, 1) == 1

  fun <T> shuffle(list: List<T>): List<T> {
    val result = mutableListOf<T>()
    val pool = list.toMutableList()
    val count = list.size
    for (i in 0 until count) {
      val index = random.nextInt(pool.size)
      result.add(pool[index])
      pool.removeAt(index)
    }
    return result
  }

  fun <T> shuffle(set: Set<T>): List<T> = shuffle(set.toList())
}
