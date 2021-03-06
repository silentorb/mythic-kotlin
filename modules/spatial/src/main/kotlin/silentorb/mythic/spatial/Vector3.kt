package silentorb.mythic.spatial

import java.text.NumberFormat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private val _zero = Vector3()
private val _unit = Vector3(1f, 1f, 1f)

data class Vector3(
    override val x: Float = 0f,
    override val y: Float = 0f,
    override val z: Float = 0f
) : Vector2fMinimal, Vector3int {

  constructor(d: Float) : this(d, d, d) {}

  constructor(v: Vector3m) : this(v.x, v.y, v.z) {}

  constructor(v: Vector3int) : this(v.x, v.y, v.z) {}

  constructor(v: Vector2f, z: Float) : this(v.x, v.y, z) {}

  constructor(v: Vector2, z: Float = 0f) : this(v.x, v.y, z) {}

  constructor(items: List<Float>) : this(items[0], items[1], items[2])

  init {
//    assert(!this.x.isNaN())
  }

  companion object {
    val zero: Vector3 = _zero
    val unit: Vector3 = _unit
    val up: Vector3 = Vector3(0f, 0f, 1f)
  }

  override operator fun minus(v: Vector2fMinimal): Vector2 = Vector2(x - v.x, y - v.y)

  override fun hashCode(): Int {
    val prime = 31
    var result = 1
    result = prime * result + java.lang.Float.floatToIntBits(x)
    result = prime * result + java.lang.Float.floatToIntBits(y)
    result = prime * result + java.lang.Float.floatToIntBits(z)
    return result
  }

  override fun equals(obj: Any?): Boolean {
    if (this === obj)
      return true
    if (obj == null)
      return false
    if (javaClass != obj.javaClass)
      return false
    val other = obj as Vector3?
    if (java.lang.Float.floatToIntBits(x) != java.lang.Float.floatToIntBits(other!!.x))
      return false
    if (java.lang.Float.floatToIntBits(y) != java.lang.Float.floatToIntBits(other.y))
      return false
    return if (java.lang.Float.floatToIntBits(z) != java.lang.Float.floatToIntBits(other.z)) false else true
  }

  operator fun plus(v: Vector3): Vector3 = Vector3(x + v.x, y + v.y, z + v.z)
  operator fun plus(v: Float): Vector3 = Vector3(x + v, y + v, z + v)
  operator fun times(other: Float): Vector3 = Vector3(x * other, y * other, z * other)
  operator fun times(other: Vector3): Vector3 = Vector3(x * other.x, y * other.y, z * other.z)
  operator fun minus(other: Vector3m) = Vector3m(x - other.x, y - other.y, z - other.z)
  operator fun minus(v: Vector3): Vector3 = Vector3(x - v.x, y - v.y, z - v.z)
  operator fun minus(v: Float) = Vector3(x - v, y - v, z - v)
  operator fun div(v: Float) = Vector3(x / v, y / v, z / v)
  operator fun div(v: Vector3) = Vector3(x / v.x, y / v.y, z / v.z)

  fun cross(v: Vector3): Vector3 =
      Vector3(y * v.z - z * v.y,
          z * v.x - x * v.z,
          x * v.y - y * v.x
      )

  fun distance(other: Vector3): Float {
    val dx = this.x - other.x
    val dy = this.y - other.y
    val dz = this.z - other.z
    return Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()
  }

  fun dot(other: Vector3): Float {
    return x * other.x + y * other.y + z * other.z
  }

  fun lengthSquared(): Float {
    return x * x + y * y + z * z
  }

  fun length(): Float {
    return Math.sqrt(lengthSquared().toDouble()).toFloat()
  }

  fun normalize(): Vector3 {
    val invLength = 1.0f / length()
    return Vector3(x * invLength, y * invLength, z * invLength)
  }

  fun transform(m: Matrix) = Vector3(m.transform(Vector4(x, y, z, 1f)).xyz)

  operator fun unaryMinus() = Vector3(-x, -y, -z)

  override fun xy(): Vector2 = Vector2(x, y)

  override fun toString(): String {
//    return Runtime.formatNumbers(toString(Options.NUMBER_FORMAT))
    return "(${x}, ${y}, ${z})"
  }

  fun toString(formatter: NumberFormat): String {
    return "(" + formatter.format(x.toDouble()) + ", " + formatter.format(y.toDouble()) + ", " + formatter.format(z.toDouble()) + ")"
  }

  fun roughlyEquals(margin: Float, value: Vector3): Boolean =
      x >= value.x - margin && x <= value.x + margin
          && y >= value.y - margin && y <= value.y + margin
          && z >= value.z - margin && z <= value.z + margin

  fun roughlyEquals(value: Vector3): Boolean =
      x >= value.x - lowResEpsilon && x <= value.x + lowResEpsilon
          && y >= value.y - lowResEpsilon && y <= value.y + lowResEpsilon
          && z >= value.z - lowResEpsilon && z <= value.z + lowResEpsilon

  operator fun get(i: Int): Float =
      when (i) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw Error("Invalid index $i")
      }

  fun absolute() = Vector3(abs(x), abs(y), abs(z))

  fun max(b: Float) =
      Vector3(
          max(x, b),
          max(y, b),
          max(z, b)
      )

  fun min(b: Float) =
      Vector3(
          min(x, b),
          min(y, b),
          min(z, b)
      )
}

fun getNormal(a: Vector3, b: Vector3, c: Vector3) =
    (c - b).cross(a - b).normalize()

fun getNormal(vertices: List<Vector3>) =
    (vertices[2] - vertices[1]).cross(vertices[0] - vertices[1]).normalize()

fun arrayToVector3(items: Array<Float>): Vector3 =
    Vector3(items[0], items[1], items[2])

fun listToVector3(items: List<Float>): Vector3 =
    Vector3(items[0], items[1], items[2])

fun toList(value: Vector3): List<Float> =
    listOf(
        value.x,
        value.y,
        value.z
    )

fun maxScalar(value: Vector3): Float =
    max(max(value.x, value.y), value.z)
