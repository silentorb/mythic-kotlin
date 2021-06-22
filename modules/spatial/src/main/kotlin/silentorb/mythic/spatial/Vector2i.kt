package silentorb.mythic.spatial

import org.joml.Math
import org.joml.internal.Options
import org.joml.internal.Runtime
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.text.DecimalFormat
import java.text.NumberFormat

private val _zero = Vector2i()
private val _unit = Vector2i(1, 1)

/**
 * Represents a 2D vector with single-precision.
 *
 * @author RGreenlees
 * @author Kai Burjack
 * @author Hans Uhlig
 */
data class Vector2i(
    var x: Int = 0,
    var y: Int = x
) {

  /**
   * Create a new [Vector2i] and initialize its components to the given values.
   *
   * @param x
   * the x component
   * @param y
   * the y component
   */

  private fun thisOrNew(): Vector2i {
    return this
  }

  /**
   * Set the x and y components to the supplied value.
   *
   * @param s
   * scalar value of both components
   * @return this
   */
  fun set(s: Int): Vector2i {
    return set(s, s)
  }

  /**
   * Set the x and y components to the supplied values.
   *
   * @param x
   * the x component
   * @param y
   * the y component
   * @return this
   */
  operator fun set(x: Int, y: Int): Vector2i {
    this.x = x
    this.y = y
    return this
  }

  operator fun get(i: Int): Int =
      when (i) {
        0 -> x
        1 -> y
        else -> throw IllegalArgumentException()
      }
  /**
   * Set the value of the specified component of this vector.
   *
   * @param component
   * the component whose value to set, within <tt>[0..1]</tt>
   * @param value
   * the value to set
   * @return this
   * @throws IllegalArgumentException if `component` is not within <tt>[0..1]</tt>
   */
  @Throws(IllegalArgumentException::class)
  fun setComponent(component: Int, value: Int): Vector2i {
    when (component) {
      0 -> x = value
      1 -> y = value
      else -> throw IllegalArgumentException()
    }
    return this
  }

  /**
   * Subtract the supplied vector from this one and store the result in
   * `this`.
   *
   * @param v
   * the vector to subtract
   * @return a vector holding the result
   */
  fun sub(v: Vector2i): Vector2i {
    return sub(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2i#sub(Vector2i, Vector2i)
     */
  fun sub(v: Vector2i, dest: Vector2i): Vector2i {
    dest.x = x - v.x
    dest.y = y - v.y
    return dest
  }

  /**
   * Decrement the components of this vector by the given values.
   *
   * @param x
   * the x component to subtract
   * @param y
   * the y component to subtract
   * @return a vector holding the result
   */
  fun sub(x: Int, y: Int): Vector2i {
    return sub(x, y, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2i#sub(int, int, Vector2i)
     */
  fun sub(x: Int, y: Int, dest: Vector2i): Vector2i {
    dest.x = this.x - x
    dest.y = this.y - y
    return dest
  }

  /* (non-Javadoc)
     * @see Vector2i#lengthSquared()
     */
  fun lengthSquared(): Long {
    return (x * x + y * y).toLong()
  }

  /* (non-Javadoc)
     * @see Vector2i#length()
     */
  fun length(): Double {
    return Math.sqrt(lengthSquared().toDouble())
  }

  /* (non-Javadoc)
     * @see Vector2i#distance(Vector2i)
     */
  fun distance(v: Vector2i): Double {
    return Math.sqrt(distanceSquared(v).toDouble())
  }

  /* (non-Javadoc)
     * @see Vector2i#distance(int, int)
     */
  fun distance(x: Int, y: Int): Double {
    return Math.sqrt(distanceSquared(x, y).toDouble())
  }

  /* (non-Javadoc)
     * @see Vector2i#distanceSquared(Vector2i)
     */
  fun distanceSquared(v: Vector2i): Long {
    val dx = this.x - v.x
    val dy = this.y - v.y
    return (dx * dx + dy * dy).toLong()
  }

  /* (non-Javadoc)
     * @see Vector2i#distanceSquared(int, int)
     */
  fun distanceSquared(x: Int, y: Int): Long {
    val dx = this.x - x
    val dy = this.y - y
    return (dx * dx + dy * dy).toLong()
  }

  /**
   * Add `v` to this vector.
   *
   * @param v
   * the vector to add
   * @return a vector holding the result
   */
  fun add(v: Vector2i): Vector2i {
    return add(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2i#add(Vector2i, Vector2i)
     */
  fun add(v: Vector2i, dest: Vector2i): Vector2i {
    dest.x = x + v.x
    dest.y = y + v.y
    return dest
  }

  /**
   * Increment the components of this vector by the given values.
   *
   * @param x
   * the x component to add
   * @param y
   * the y component to add
   * @return a vector holding the result
   */
  fun add(x: Int, y: Int): Vector2i {
    return add(x, y, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2i#add(int, int, Vector2i)
     */
  fun add(x: Int, y: Int, dest: Vector2i): Vector2i {
    dest.x = this.x + x
    dest.y = this.y + y
    return dest
  }

  /**
   * Multiply all components of this [Vector2i] by the given scalar
   * value.
   *
   * @param scalar
   * the scalar to multiply this vector by
   * @return a vector holding the result
   */
  fun mul(scalar: Int): Vector2i {
    return mul(scalar, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2i#mul(int, Vector2i)
     */
  fun mul(scalar: Int, dest: Vector2i): Vector2i {
    dest.x = x * scalar
    dest.y = y * scalar
    return dest
  }

  /**
   * Add the supplied vector by this one.
   *
   * @param v
   * the vector to multiply
   * @return a vector holding the result
   */
  fun mul(v: Vector2i): Vector2i {
    return mul(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2i#mul(Vector2i, Vector2i)
     */
  fun mul(v: Vector2i, dest: Vector2i): Vector2i {
    dest.x = x * v.x
    dest.y = y * v.y
    return dest
  }

  /**
   * Multiply the components of this vector by the given values.
   *
   * @param x
   * the x component to multiply
   * @param y
   * the y component to multiply
   * @return a vector holding the result
   */
  fun mul(x: Int, y: Int): Vector2i {
    return mul(x, y, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2i#mul(int, int, Vector2i)
     */
  fun mul(x: Int, y: Int, dest: Vector2i): Vector2i {
    dest.x = this.x * x
    dest.y = this.y * y
    return dest
  }

  /**
   * Set all components to zero.
   *
   * @return a vector holding the result
   */

  @Throws(IOException::class)
  fun writeExternal(out: ObjectOutput) {
    out.writeInt(x)
    out.writeInt(y)
  }

  @Throws(IOException::class, ClassNotFoundException::class)
  fun readExternal(`in`: ObjectInput) {
    x = `in`.readInt()
    y = `in`.readInt()
  }

  /**
   * Negate this vector.
   *
   * @return a vector holding the result
   */
  fun negate(): Vector2i {
    return negate(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2i#negate(Vector2i)
     */
  fun negate(dest: Vector2i): Vector2i {
    dest.x = -x
    dest.y = -y
    return dest
  }

  /**
   * Set the components of this vector to be the component-wise minimum of this and the other vector.
   *
   * @param v
   * the other vector
   * @return a vector holding the result
   */
  fun min(v: Vector2i): Vector2i {
    return min(v, thisOrNew())
  }

  fun min(v: Vector2i, dest: Vector2i): Vector2i {
    dest.x = if (x < v.x) x else v.x
    dest.y = if (y < v.y) y else v.y
    return this
  }

  /**
   * Set the components of this vector to be the component-wise maximum of this and the other vector.
   *
   * @param v
   * the other vector
   * @return a vector holding the result
   */
  fun max(v: Vector2i): Vector2i {
    return max(v, thisOrNew())
  }

  fun max(v: Vector2i, dest: Vector2i): Vector2i {
    dest.x = if (x > v.x) x else v.x
    dest.y = if (y > v.y) y else v.y
    return this
  }

  override fun hashCode(): Int {
    val prime = 31
    var result = 1
    result = prime * result + x
    result = prime * result + y
    return result
  }

 override fun equals(obj: Any?): Boolean {
    if (this === obj) {
      return true
    }
    if (obj == null) {
      return false
    }
    if (javaClass != obj.javaClass) {
      return false
    }
    val other = obj as Vector2i?
    if (x != other!!.x) {
      return false
    }
    return if (y != other.y) {
      false
    } else true
  }

  /**
   * Return a string representation of this vector.
   *
   *
   * This method creates a new [DecimalFormat] on every invocation with the format string "<tt>0.000E0;-</tt>".
   *
   * @return the string representation
   */
  override fun toString(): String {
    return Runtime.formatNumbers(toString(Options.NUMBER_FORMAT))
  }

  /**
   * Return a string representation of this vector by formatting the vector components with the given [NumberFormat].
   *
   * @param formatter
   * the [NumberFormat] used to format the vector components with
   * @return the string representation
   */
  fun toString(formatter: NumberFormat): String {
    return "($x $y)"
  }

  companion object {
    val zero: Vector2i get() = _zero
    val unit: Vector2i get() = _unit
    private val serialVersionUID = 1L
  }

  operator fun times(other: Vector2i): Vector2i = Vector2i(x * other.x, y * other.y)
  operator fun div(other: Vector2i): Vector2i = Vector2i(x / other.x, y / other.y)
  operator fun plus(other: Vector2i): Vector2i = Vector2i(x + other.x, y + other.y)
  operator fun minus(other: Vector2i): Vector2i = Vector2i(x - other.x, y - other.y)
  operator fun minus(other: Int): Vector2i = Vector2i(x - other, y - other)

  operator fun times(other: Int): Vector2i = Vector2i(x * other, y * other)
  operator fun plus(other: Int): Vector2i = Vector2i(x + other, y + other)
}
