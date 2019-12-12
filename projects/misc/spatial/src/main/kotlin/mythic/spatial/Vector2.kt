package mythic.spatial

import org.joml.*
import java.text.NumberFormat

data class Vector2(
    override val x: Float = 0f,
    override val y: Float = 0f
) : Vector2fMinimal {

  constructor(d: Float) : this(d, d) {}

  constructor(v: Vector2fc) : this(v.x, v.y) {}

  override operator fun minus(v: Vector2fMinimal): Vector2 =
      Vector2(x - v.x, y - v.y)

  operator fun minus(v: Vector2): Vector2 =
      Vector2(x - v.x, y - v.y)

  override fun hashCode(): Int {
    val prime = 31
    var result = 1
    result = prime * result + java.lang.Float.floatToIntBits(x)
    result = prime * result + java.lang.Float.floatToIntBits(y)
    return result
  }

  override fun equals(obj: Any?): Boolean {
    if (this === obj)
      return true
    if (obj == null)
      return false
    if (javaClass != obj.javaClass)
      return false
    val other = obj as Vector2?
    if (java.lang.Float.floatToIntBits(x) != java.lang.Float.floatToIntBits(other!!.x))
      return false
    if (java.lang.Float.floatToIntBits(y) != java.lang.Float.floatToIntBits(other.y))
      return false
    return java.lang.Float.floatToIntBits(y) == java.lang.Float.floatToIntBits(other.y)
  }

  operator fun plus(v: Vector2): Vector2 = Vector2(x + v.x, y + v.y)
  operator fun plus(v: Float): Vector2 = Vector2(x + v, y + v)
  operator fun times(other: Float): Vector2 = Vector2(x * other, y * other)
  operator fun times(other: Vector2): Vector2 = Vector2(x * other.x, y * other.y)
//  operator fun minus(other: Vector2fc) = Vector2(x - other.x, y - other.y)
  operator fun minus(v: Float) = Vector2(x - v, y - v)
  operator fun div(v: Float) = Vector2(x / v, y / v)
  operator fun div(v: Vector2) = Vector2(x / v.x, y / v.y)

  fun distance(other: Vector2): Float {
    val dx = this.x - other.x
    val dy = this.y - other.y
    return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
  }

  fun dot(other: Vector2): Float {
    return x * other.x + y * other.y
  }

  fun lengthSquared(): Float {
    return x * x + y * y
  }

  fun length(): Float {
    return Math.sqrt(lengthSquared().toDouble()).toFloat()
  }

  fun normalize(): Vector2 {
    val invLength = 1.0f / length()
    return Vector2(x * invLength, y * invLength)
  }

//  fun transform(m: Matrix) = Vector2(m.transform(Vector4(x, y, 1f)).xyz)

  operator fun unaryMinus() = Vector2(-x, -y)

  override fun xy(): Vector2 = Vector2(x, y)

  override fun toString(): String {
//    return Runtime.formatNumbers(toString(Options.NUMBER_FORMAT))
    return "(${x}, ${y})"
  }

  fun toString(formatter: NumberFormat): String {
    return "(" + formatter.format(x.toDouble()) + ", " + formatter.format(y.toDouble()) + ")"
  }

//  fun roughlyEquals(margin: Float, value: Vector2): Boolean =
//      x >= value.x - margin && x <= value.x + margin
//          && y >= value.y - margin && y <= value.y + margin
//          && z >= value.z - margin && z <= value.z + margin
//
//  fun roughlyEquals(value: Vector2): Boolean =
//      x >= value.x - epsilon && x <= value.x + epsilon
//          && y >= value.y - epsilon && y <= value.y + epsilon
//          && z >= value.z - epsilon && z <= value.z + epsilon
}
