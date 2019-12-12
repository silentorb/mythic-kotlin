package randomly

import mythic.spatial.Vector2
import java.util.*

class Dice(private val seed: Long? = null) {
  private val random = Random(seed ?: System.currentTimeMillis())

  fun getInt(min: Int, max: Int) = min + random.nextInt(1 + max - min)

  fun getInt(max: Int) = getInt(0, max)

  fun getInt(value: IntRange) = getInt(value.first, value.last)

  fun getFloat() = random.nextFloat()

  fun getFloat(min: Float, max: Float) = min + getFloat() * (max - min)

  fun getFloat(max: Float) = getFloat(0f, max)

  fun get(max: Vector2) = Vector2(getFloat(max.x), getFloat(max.y))

  fun <T> takeOne(list: List<T>): T {
    assert(list.isNotEmpty())
    return if (list.size == 1)
      list.first()
    else
      list[random.nextInt(list.size)]
  }

  fun <T> takeOne(set: Set<T>): T {
    assert(set.isNotEmpty())
    return if (set.size == 1)
      set.first()
    else
      set.toList()[random.nextInt(set.size)] // TODO: Optimize away the toList call
  }

  fun <T> take(list: List<T>, count: Int): List<T> {
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

  fun <T> take(list: Set<T>, count: Int): Set<T> {
    assert(count <= list.size)
    val result = mutableSetOf<T>()
    val options = list.toMutableList()
    for (i in 1..count) {
      val item = takeOne(options)
      options.remove(item)
      result.add(item)
    }
    return result
  }

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

  companion object {
    val global = Dice()
  }
}
