package silentorb.mythic.spatial

import org.joml.Math
import org.joml.Vector2ic
import org.joml.Vector3ic
import org.joml.internal.Options
import org.joml.internal.Runtime
import java.io.Externalizable
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.text.NumberFormat

class Vector4i : Externalizable {
  /**
   * The x component of the vector.
   */
  var x = 0

  /**
   * The y component of the vector.
   */
  var y = 0

  /**
   * The z component of the vector.
   */
  var z = 0

  /**
   * The w component of the vector.
   */
  var w: Int

  /**
   * Create a new [Vector4i] of `(0, 0, 0, 1)`.
   */
  constructor() {
    w = 1
  }

  /**
   * Create a new [Vector4i] with the first three components from the
   * given `v` and the given `w`.
   *
   * @param v the [Vector3ic]
   * @param w the w component
   */
  constructor(v: Vector3ic, w: Int) : this(v.x(), v.y(), v.z(), w) {}

  /**
   * Create a new [Vector4i] with the first two components from the
   * given `v` and the given `z`, and `w`.
   *
   * @param v the [Vector2ic]
   * @param z the z component
   * @param w the w component
   */
  constructor(v: Vector2ic, z: Int, w: Int) : this(v.x, v.y, z, w) {}

  /**
   * Create a new [Vector4i] and initialize all four components with the
   * given value.
   *
   * @param s scalar value of all four components
   */
  constructor(s: Int) : this(s, s, s, s) {}

  /**
   * Create a new [Vector4i] with the given component values.
   *
   * @param x the x component
   * @param y the y component
   * @param z the z component
   * @param w the w component
   */
  constructor(x: Int, y: Int, z: Int, w: Int) {
    this.x = x
    this.y = y
    this.z = z
    this.w = w
  }

  private fun thisOrNew(): Vector4i {
    return this
  }

  /**
   * Set the value of the specified component of this vector.
   *
   * @param component the component whose value to set, within <tt>[0..3]</tt>
   * @param value     the value to set
   * @return this
   * @throws IllegalArgumentException if `component` is not within <tt>[0..3]</tt>
   */
  @Throws(IllegalArgumentException::class)
  fun setComponent(component: Int, value: Int): Vector4i {
    when (component) {
      0 -> x = value
      1 -> y = value
      2 -> z = value
      3 -> w = value
      else -> throw IllegalArgumentException()
    }
    return this
  }

  /**
   * Subtract the supplied vector from this one.
   *
   * @param v the vector to subtract
   * @return a vector holding the result
   */
  fun sub(v: Vector4ic): Vector4i {
    return sub(v, thisOrNew())
  }

  /**
   * Subtract <tt>(x, y, z, w)</tt> from this.
   *
   * @param x the x component to subtract
   * @param y the y component to subtract
   * @param z the z component to subtract
   * @param w the w component to subtract
   * @return a vector holding the result
   */
  fun sub(x: Int, y: Int, z: Int, w: Int): Vector4i {
    return sub(x, y, z, w, thisOrNew())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#sub(org.joml.Vector4ic, org.joml.Vector4i)
     */
  fun sub(v: Vector4ic, dest: Vector4i): Vector4i {
    return sub(v.x(), v.y(), v.z(), v.w(), dest)
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#sub(int, int, int, int, org.joml.Vector4i)
     */
  fun sub(x: Int, y: Int, z: Int, w: Int, dest: Vector4i): Vector4i {
    dest.x = this.x - x
    dest.y = this.y - y
    dest.z = this.z - z
    dest.w = this.w - w
    return dest
  }

  /**
   * Add the supplied vector to this one.
   *
   * @param v the vector to add
   * @return a vector holding the result
   */
  fun add(v: Vector4ic): Vector4i {
    return add(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#add(org.joml.Vector4ic, org.joml.Vector4i)
     */
  fun add(v: Vector4ic, dest: Vector4i): Vector4i {
    return add(v.x(), v.y(), v.z(), v.w(), dest)
  }

  /**
   * Increment the components of this vector by the given values.
   *
   * @param x the x component to add
   * @param y the y component to add
   * @param z the z component to add
   * @param w the w component to add
   * @return a vector holding the result
   */
  fun add(x: Int, y: Int, z: Int, w: Int): Vector4i {
    return add(x, y, z, w, thisOrNew())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#add(int, int, int, int, org.joml.Vector4i)
     */
  fun add(x: Int, y: Int, z: Int, w: Int, dest: Vector4i): Vector4i {
    dest.x = this.x + x
    dest.y = this.y + y
    dest.z = this.z + z
    dest.w = this.w + w
    return dest
  }

  /**
   * Multiply this Vector4i component-wise by another Vector4i.
   *
   * @param v the other vector
   * @return a vector holding the result
   */
  fun mul(v: Vector4ic): Vector4i {
    return mul(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#mul(org.joml.Vector4ic, org.joml.Vector4i)
     */
  fun mul(v: Vector4ic, dest: Vector4i): Vector4i {
    dest.x = x * v.x()
    dest.y = y * v.y()
    dest.z = z * v.z()
    dest.w = w * v.w()
    return dest
  }

  /**
   * Divide this Vector4i component-wise by another Vector4i.
   *
   * @param v the vector to divide by
   * @return a vector holding the result
   */
  operator fun div(v: Vector4ic): Vector4i {
    return div(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#div(org.joml.Vector4ic, org.joml.Vector4i)
     */
  fun div(v: Vector4ic, dest: Vector4i): Vector4i {
    dest.x = x / v.x()
    dest.y = y / v.y()
    dest.z = z / v.z()
    dest.w = w / v.w()
    return dest
  }

  /**
   * Multiply all components of this [Vector4i] by the given scalar
   * value.
   *
   * @param scalar the scalar to multiply by
   * @return a vector holding the result
   */
  fun mul(scalar: Float): Vector4i {
    return mul(scalar, thisOrNew())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#mul(float, org.joml.Vector4i)
     */
  fun mul(scalar: Float, dest: Vector4i): Vector4i {
    dest.x = (x * scalar).toInt()
    dest.y = (y * scalar).toInt()
    dest.z = (z * scalar).toInt()
    dest.w = (w * scalar).toInt()
    return dest
  }

  /**
   * Divide all components of this [Vector4i] by the given scalar value.
   *
   * @param scalar the scalar to divide by
   * @return a vector holding the result
   */
  operator fun div(scalar: Int): Vector4i {
    return div(scalar.toFloat(), thisOrNew())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#div(float, org.joml.Vector4i)
     */
  fun div(scalar: Float, dest: Vector4i): Vector4i {
    dest.x = (x / scalar).toInt()
    dest.y = (y / scalar).toInt()
    dest.z = (z / scalar).toInt()
    dest.w = (w / scalar).toInt()
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#lengthSquared()
     */
  fun lengthSquared(): Long {
    return (x * x + y * y + z * z + w * w).toLong()
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#length()
     */
  fun length(): Double {
    return Math.sqrt(lengthSquared().toDouble())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#distance(org.joml.Vector4i)
     */
  fun distance(v: Vector4ic): Double {
    return distance(v.x(), v.y(), v.z(), v.w())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#distance(int, int, int, int)
     */
  fun distance(x: Int, y: Int, z: Int, w: Int): Double {
    return Math.sqrt(distanceSquared(x, y, z, w).toDouble())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#distanceSquared(org.joml.Vector4ic)
     */
  fun distanceSquared(v: Vector4ic): Int {
    return distanceSquared(v.x(), v.y(), v.z(), v.w())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#distanceSquared(int, int, int, int)
     */
  fun distanceSquared(x: Int, y: Int, z: Int, w: Int): Int {
    val dx = this.x - x
    val dy = this.y - y
    val dz = this.z - z
    val dw = this.w - w
    return dx * dx + dy * dy + dz * dz + dw * dw
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#dot(org.joml.Vector4ic)
     */
  fun dot(v: Vector4ic): Int {
    return x * v.x() + y * v.y() + z * v.z() + w * v.w()
  }

  /**
   * Negate this vector.
   *
   * @return a vector holding the result
   */
  fun negate(): Vector4i {
    return negate(thisOrNew())
  }

  /* (non-Javadoc)
     * @see org.joml.Vector4ic#negate(org.joml.Vector4i)
     */
  fun negate(dest: Vector4i): Vector4i {
    dest.x = -x
    dest.y = -y
    dest.z = -z
    dest.w = -w
    return dest
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
   * @param formatter the [NumberFormat] used to format the vector components with
   * @return the string representation
   */
  fun toString(formatter: NumberFormat): String {
    return "(" + formatter.format(x.toLong()) + " " + formatter.format(y.toLong()) + " " + formatter.format(z.toLong()) + " " + formatter.format(w.toLong()) + ")"
  }

  @Throws(IOException::class)
  override fun writeExternal(out: ObjectOutput) {
    out.writeInt(x)
    out.writeInt(y)
    out.writeInt(z)
    out.writeInt(w)
  }

  @Throws(IOException::class, ClassNotFoundException::class)
  override fun readExternal(`in`: ObjectInput) {
    x = `in`.readInt()
    y = `in`.readInt()
    z = `in`.readInt()
    w = `in`.readInt()
  }

  /**
   * Set the components of this vector to be the component-wise minimum of this and the other vector.
   *
   * @param v the other vector
   * @return a vector holding the result
   */
  fun min(v: Vector4ic): Vector4i {
    return min(v, thisOrNew())
  }

  fun min(v: Vector4ic, dest: Vector4i): Vector4i {
    dest.x = if (x < v.x()) x else v.x()
    dest.y = if (y < v.y()) y else v.y()
    dest.z = if (z < v.z()) z else v.z()
    dest.w = if (w < v.w()) w else v.w()
    return this
  }

  /**
   * Set the components of this vector to be the component-wise maximum of this and the other vector.
   *
   * @param v the other vector
   * @return a vector holding the result
   */
  fun max(v: Vector4ic): Vector4i {
    return max(v, thisOrNew())
  }

  fun max(v: Vector4ic, dest: Vector4i): Vector4i {
    dest.x = if (x > v.x()) x else v.x()
    dest.y = if (y > v.y()) y else v.y()
    dest.z = if (z > v.z()) z else v.z()
    dest.w = if (w > v.w()) w else v.w()
    return this
  }

  override fun hashCode(): Int {
    val prime = 31
    var result = 1
    result = prime * result + x
    result = prime * result + y
    result = prime * result + z
    result = prime * result + w
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
    val other = obj as Vector4i
    if (x != other.x) {
      return false
    }
    if (y != other.y) {
      return false
    }
    if (z != other.z) {
      return false
    }
    return if (w != other.w) {
      false
    } else true
  }

  companion object {
    private const val serialVersionUID = 1L
  }
}
