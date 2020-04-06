package silentorb.mythic.spatial

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

private val _zero = Vector3i()
private val _unit = Vector3i(1, 1, 1)

data class Vector3i(
    val x: Int = 0,
    val y: Int = x,
    val z: Int = x
) {
  companion object {
    val zero: Vector3i = _zero
    val unit: Vector3i = _unit
  }

  operator fun plus(value: Vector3i): Vector3i = Vector3i(x + value.x, y + value.y, z + value.z)
  operator fun plus(v: Int) = Vector3i(x + v, y + v, z + v)
  operator fun minus(value: Vector3i): Vector3i = Vector3i(x - value.x, y - value.y, z - value.z)
  operator fun minus(v: Int) = Vector3i(x - v, y - v, z - v)
  operator fun unaryMinus() = Vector3i(-x, -y, -z)
  operator fun times(value: Vector3i): Vector3i = Vector3i(x * value.x, y * value.y, z * value.z)
  operator fun times(value: Int): Vector3i = Vector3i(x * value, y * value, z * value)
  operator fun div(value: Int): Vector3i = Vector3i(x / value, y / value, z / value)

  operator fun get(i: Int): Int =
      when (i) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw Error("Invalid index $i")
      }
}

fun toVector3iRounded(value: Vector3): Vector3i =
    Vector3i(
        value.x.roundToInt(),
        value.y.roundToInt(),
        value.z.roundToInt()
    )

fun toVector3iRoundedUp(value: Vector3): Vector3i =
    Vector3i(
        ceil(value.x).toInt(),
        ceil(value.y).toInt(),
        ceil(value.z).toInt()
    )

fun toVector3iRoundedDown(value: Vector3): Vector3i =
    Vector3i(
        floor(value.x).toInt(),
        floor(value.y).toInt(),
        floor(value.z).toInt()
    )
