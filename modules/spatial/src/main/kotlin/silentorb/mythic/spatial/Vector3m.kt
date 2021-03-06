package silentorb.mythic.spatial

import org.joml.internal.Options
import org.joml.internal.Runtime
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.text.DecimalFormat
import java.text.NumberFormat

class Vector3m : Vector2fMinimal {

  /**
   * The x component of the vector.
   */
  override var x: Float = 0.toFloat()

  /**
   * The y component of the vector.
   */
  override var y: Float = 0.toFloat()

  /**
   * The z component of the vector.
   */
  var z: Float = 0.toFloat()

  /**
   * Create a new [Vector3m] of <tt>(0, 0, 0)</tt>.
   */
  constructor() {}

  /**
   * Create a new [Vector3m] and initialize all three components with the given value.
   *
   * @param d
   * the value of all three components
   */
  constructor(d: Float) : this(d, d, d) {}

  /**
   * Create a new [Vector3m] with the given component values.
   *
   * @param x
   * the value of x
   * @param y
   * the value of y
   * @param z
   * the value of z
   */
  constructor(x: Float, y: Float, z: Float) {
    this.x = x
    this.y = y
    this.z = z
  }

  /**
   * Create a new [Vector3m] with the same values as `v`.
   *
   * @param v
   * the [Vector3m] to copy the values from
   */
  constructor(v: Vector3m) : this(v.x, v.y, v.z) {}

  constructor(v: Vector3) : this(v.x, v.y, v.z) {}

  /**
   * Create a new [Vector3m] with the first two components from the
   * given `v` and the given `z`
   *
   * @param v
   * the [Vector2f] to copy the values from
   * @param z
   * the z component
   */
  constructor(v: Vector2f, z: Float) : this(v.x, v.y, z) {}

  private fun thisOrNew(): Vector3m {
    return this
  }

  /**
   * Set the x, y and z components to match the supplied vector.
   *
   * @param v
   * contains the values of x, y and z to set
   * @return this
   */
  fun set(v: Vector3m): Vector3m {
    return set(v.x, v.y, v.z)
  }

  /**
   * Set the first two components from the given `v`
   * and the z component from the given `z`
   *
   * @param v
   * the [Vector2f] to copy the values from
   * @param z
   * the z component
   * @return this
   */
  operator fun set(v: Vector2f, z: Float): Vector3m {
    return set(v.x, v.y, z)
  }

  /**
   * Set the x, y, and z components to the supplied value.
   *
   * @param d
   * the value of all three components
   * @return this
   */
  fun set(d: Float): Vector3m {
    return set(d, d, d)
  }

  /**
   * Set the x, y and z components to the supplied values.
   *
   * @param x
   * the x component
   * @param y
   * the y component
   * @param z
   * the z component
   * @return this
   */
  operator fun set(x: Float, y: Float, z: Float): Vector3m {
    this.x = x
    this.y = y
    this.z = z
    return this
  }


  /**
   * Set the value of the specified component of this vector.
   *
   * @param component
   * the component whose value to set, within <tt>[0..2]</tt>
   * @param value
   * the value to set
   * @return this
   * @throws IllegalArgumentException if `component` is not within <tt>[0..2]</tt>
   */
  @Throws(IllegalArgumentException::class)
  fun setComponent(component: Int, value: Float): Vector3m {
    when (component) {
      0 -> x = value
      1 -> y = value
      2 -> z = value
      else -> throw IllegalArgumentException()
    }
    return this
  }

  /**
   * Subtract the supplied vector from this one and store the result in `this`.
   *
   * @param v
   * the vector to subtract
   * @return a vector holding the result
   */
  fun sub(v: Vector3m): Vector3m {
    return sub(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#sub(Vector3m, Vector3m)
     */
  fun sub(v: Vector3m, dest: Vector3m): Vector3m {
    dest.x = x - v.x
    dest.y = y - v.y
    dest.z = z - v.z
    return dest
  }

  /**
   * Decrement the components of this vector by the given values.
   *
   * @param x
   * the x component to subtract
   * @param y
   * the y component to subtract
   * @param z
   * the z component to subtract
   * @return a vector holding the result
   */
  fun sub(x: Float, y: Float, z: Float): Vector3m {
    return sub(x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#sub(float, float, float, Vector3m)
     */
  fun sub(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m {
    dest.x = this.x - x
    dest.y = this.y - y
    dest.z = this.z - z
    return dest
  }

  /**
   * Add the supplied vector to this one.
   *
   * @param v
   * the vector to add
   * @return a vector holding the result
   */
  fun add(v: Vector3m): Vector3m {
    return add(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#add(Vector3m, Vector3m)
     */
  fun add(v: Vector3m, dest: Vector3m): Vector3m {
    dest.x = x + v.x
    dest.y = y + v.y
    dest.z = z + v.z
    return dest
  }

  /**
   * Increment the components of this vector by the given values.
   *
   * @param x
   * the x component to add
   * @param y
   * the y component to add
   * @param z
   * the z component to add
   * @return a vector holding the result
   */
  fun add(x: Float, y: Float, z: Float): Vector3m {
    return add(x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#add(float, float, float, Vector3m)
     */
  fun add(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m {
    dest.x = this.x + x
    dest.y = this.y + y
    dest.z = this.z + z
    return dest
  }

  /**
   * Add the component-wise multiplication of `a * b` to this vector.
   *
   * @param a
   * the first multiplicand
   * @param b
   * the second multiplicand
   * @return a vector holding the result
   */
  fun fma(a: Vector3m, b: Vector3m): Vector3m {
    return fma(a, b, thisOrNew())
  }

  /**
   * Add the component-wise multiplication of `a * b` to this vector.
   *
   * @param a
   * the first multiplicand
   * @param b
   * the second multiplicand
   * @return a vector holding the result
   */
  fun fma(a: Float, b: Vector3m): Vector3m {
    return fma(a, b, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#fma(Vector3m, Vector3m, Vector3m)
     */
  fun fma(a: Vector3m, b: Vector3m, dest: Vector3m): Vector3m {
    dest.x = x + a.x * b.x
    dest.y = y + a.y * b.y
    dest.z = z + a.z * b.z
    return dest
  }

  /* (non-Javadoc)
     * @see Vector3m#fma(float, Vector3m, Vector3m)
     */
  fun fma(a: Float, b: Vector3m, dest: Vector3m): Vector3m {
    dest.x = x + a * b.x
    dest.y = y + a * b.y
    dest.z = z + a * b.z
    return dest
  }

  /**
   * Multiply this Vector3m component-wise by another Vector3m.
   *
   * @param v
   * the vector to multiply by
   * @return a vector holding the result
   */
  fun mul(v: Vector3m): Vector3m {
    return mul(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#mul(Vector3m, Vector3m)
     */
  fun mul(v: Vector3m, dest: Vector3m): Vector3m {
    dest.x = x * v.x
    dest.y = y * v.y
    dest.z = z * v.z
    return dest
  }

  /**
   * Divide this Vector3m component-wise by another Vector3m.
   *
   * @param v
   * the vector to divide by
   * @return a vector holding the result
   */
  operator fun div(v: Vector3m): Vector3m {
    return div(v.x, v.y, v.z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#div(Vector3m, Vector3m)
     */
  fun div(v: Vector3m, dest: Vector3m): Vector3m {
    dest.x = x / v.x
    dest.y = y / v.y
    dest.z = z / v.z
    return dest
  }

  /* (non-Javadoc)
     * @see Vector3m#mulProject(Matrix4f, Vector3m)
     */
  fun mulProject(mat: Matrix4f, dest: Vector3m): Vector3m {
    val invW = 1.0f / (mat.m03() * x + mat.m13() * y + mat.m23() * z + mat.m33())
    val rx = (mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30()) * invW
    val ry = (mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31()) * invW
    val rz = (mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32()) * invW
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /**
   * Multiply the given matrix `mat` with this Vector3m, perform perspective division.
   *
   *
   * This method uses <tt>w=1.0</tt> as the fourth vector component.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @return a vector holding the result
   */
  fun mulProject(mat: Matrix4f): Vector3m {
    return mulProject(mat, thisOrNew())
  }

  /**
   * Multiply the given 4x4 matrix `mat` with `this`.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>1.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @return a vector holding the result
   */
  fun mulPosition(mat: Matrix4f): Vector3m {
    return mulPosition(mat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#mulPosition(Matrix4f, Vector3m)
     */
  fun mulPosition(mat: Matrix4f, dest: Vector3m): Vector3m {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30()
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31()
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32()
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /**
   * Multiply the transpose of the given 4x4 matrix `mat` with `this`.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>1.0</tt>.
   *
   * @param mat
   * the matrix whose transpose to multiply this vector by
   * @return a vector holding the result
   */
  fun mulTransposePosition(mat: Matrix4f): Vector3m {
    return mulTransposePosition(mat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#mulTransposePosition(Matrix4f, Vector3m)
     */
  fun mulTransposePosition(mat: Matrix4f, dest: Vector3m): Vector3m {
    val rx = mat.m00() * x + mat.m01() * y + mat.m02() * z + mat.m03()
    val ry = mat.m10() * x + mat.m11() * y + mat.m12() * z + mat.m13()
    val rz = mat.m20() * x + mat.m21() * y + mat.m22() * z + mat.m23()
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /**
   * Multiply the given 4x4 matrix `mat` with `this` and return the *w* component
   * of the resulting 4D vector.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>1.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @return the *w* component of the resulting 4D vector after multiplication
   */
  fun mulPositionW(mat: Matrix4f): Float {
    return mulPositionW(mat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#mulPositionW(Matrix4f, Vector3m)
     */
  fun mulPositionW(mat: Matrix4f, dest: Vector3m): Float {
    val w = mat.m03() * x + mat.m13() * y + mat.m23() * z + mat.m33()
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30()
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31()
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32()
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return w
  }

  /**
   * Multiply the given 4x4 matrix `mat` with `this`.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>0.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @return a vector holding the result
   */
  fun mulDirection(mat: Matrix4f): Vector3m {
    return mulDirection(mat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#mulDirection(Matrix4f, Vector3m)
     */
  fun mulDirection(mat: Matrix4f, dest: Vector3m): Vector3m {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /**
   * Multiply the transpose of the given 4x4 matrix `mat` with `this`.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>0.0</tt>.
   *
   * @param mat
   * the matrix whose transpose to multiply this vector by
   * @return a vector holding the result
   */
  fun mulTransposeDirection(mat: Matrix4f): Vector3m {
    return mulTransposeDirection(mat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#mulTransposeDirection(Matrix4f, Vector3m)
     */
  fun mulTransposeDirection(mat: Matrix4f, dest: Vector3m): Vector3m {
    val rx = mat.m00() * x + mat.m01() * y + mat.m02() * z
    val ry = mat.m10() * x + mat.m11() * y + mat.m12() * z
    val rz = mat.m20() * x + mat.m21() * y + mat.m22() * z
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /**
   * Multiply all components of this [Vector3m] by the given scalar
   * value.
   *
   * @param scalar
   * the scalar to multiply this vector by
   * @return a vector holding the result
   */
  fun mul(scalar: Float): Vector3m {
    return mul(scalar, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#mul(float, Vector3m)
     */
  fun mul(scalar: Float, dest: Vector3m): Vector3m {
    dest.x = x * scalar
    dest.y = y * scalar
    dest.z = z * scalar
    return dest
  }

  /**
   * Multiply the components of this Vector3m by the given scalar values and store the result in `this`.
   *
   * @param x
   * the x component to multiply this vector by
   * @param y
   * the y component to multiply this vector by
   * @param z
   * the z component to multiply this vector by
   * @return a vector holding the result
   */
  fun mul(x: Float, y: Float, z: Float): Vector3m {
    return mul(x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#mul(float, float, float, Vector3m)
     */
  fun mul(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m {
    dest.x = this.x * x
    dest.y = this.y * y
    dest.z = this.z * z
    return dest
  }

  /**
   * Divide all components of this [Vector3m] by the given scalar
   * value.
   *
   * @param scalar
   * the scalar to divide by
   * @return a vector holding the result
   */
  operator fun div(scalar: Float): Vector3m {
    return div(scalar, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#div(float, Vector3m)
     */
  fun div(scalar: Float, dest: Vector3m): Vector3m {
    val inv = 1.0f / scalar
    dest.x = x * inv
    dest.y = y * inv
    dest.z = z * inv
    return dest
  }

  /**
   * Divide the components of this Vector3m by the given scalar values and store the result in `this`.
   *
   * @param x
   * the x component to divide this vector by
   * @param y
   * the y component to divide this vector by
   * @param z
   * the z component to divide this vector by
   * @return a vector holding the result
   */
  fun div(x: Float, y: Float, z: Float): Vector3m {
    return div(x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#div(float, float, float, Vector3m)
     */
  fun div(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m {
    dest.x = this.x / x
    dest.y = this.y / y
    dest.z = this.z / z
    return dest
  }

  /* (non-Javadoc)
     * @see Vector3m#rotationTo(float, float, float, Quaternion)
     */
  fun rotationTo(toDirX: Float, toDirY: Float, toDirZ: Float, dest: Quaternion): Quaternion {
    return dest.rotationTo(x, y, z, toDirX, toDirY, toDirZ)
  }

  /**
   * Rotate this vector the specified radians around the given rotation axis.
   *
   * @param angle
   * the angle in radians
   * @param x
   * the x component of the rotation axis
   * @param y
   * the y component of the rotation axis
   * @param z
   * the z component of the rotation axis
   * @return a vector holding the result
   */
  fun rotateAxis(angle: Float, x: Float, y: Float, z: Float): Vector3m {
    return rotateAxis(angle, x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#rotateAxis(float, float, float, float, Vector3m)
     */
  fun rotateAxis(angle: Float, aX: Float, aY: Float, aZ: Float, dest: Vector3m): Vector3m {
    val hangle = angle * 0.5f
    val sinAngle = Math.sin(hangle.toDouble()).toFloat()
    val qx = aX * sinAngle
    val qy = aY * sinAngle
    val qz = aZ * sinAngle
    val qw = Math.cosFromSin(sinAngle.toDouble(), hangle.toDouble()).toFloat()
    val w2 = qw * qw
    val x2 = qx * qx
    val y2 = qy * qy
    val z2 = qz * qz
    val zw = qz * qw
    val xy = qx * qy
    val xz = qx * qz
    val yw = qy * qw
    val yz = qy * qz
    val xw = qx * qw
    val nx = (w2 + x2 - z2 - y2) * x + (-zw + xy - zw + xy) * y + (yw + xz + xz + yw) * z
    val ny = (xy + zw + zw + xy) * x + (y2 - z2 + w2 - x2) * y + (yz + yz - xw - xw) * z
    val nz = (xz - yw + xz - yw) * x + (yz + yz + xw + xw) * y + (z2 - y2 - x2 + w2) * z
    dest.x = nx
    dest.y = ny
    dest.z = nz
    return dest
  }

  /**
   * Rotate this vector the specified radians around the X axis.
   *
   * @param angle
   * the angle in radians
   * @return a vector holding the result
   */
  fun rotateX(angle: Float): Vector3m {
    return rotateX(angle, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#rotateX(float, Vector3m)
     */
  fun rotateX(angle: Float, dest: Vector3m): Vector3m {
    val sin = Math.sin(angle.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val y = this.y * cos - this.z * sin
    val z = this.y * sin + this.z * cos
    dest.x = this.x
    dest.y = y
    dest.z = z
    return dest
  }

  /**
   * Rotate this vector the specified radians around the Y axis.
   *
   * @param angle
   * the angle in radians
   * @return a vector holding the result
   */
  fun rotateY(angle: Float): Vector3m {
    return rotateY(angle, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#rotateY(float, Vector3m)
     */
  fun rotateY(angle: Float, dest: Vector3m): Vector3m {
    val sin = Math.sin(angle.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val x = this.x * cos + this.z * sin
    val z = -this.x * sin + this.z * cos
    dest.x = x
    dest.y = this.y
    dest.z = z
    return dest
  }

  /**
   * Rotate this vector the specified radians around the Z axis.
   *
   * @param angle
   * the angle in radians
   * @return a vector holding the result
   */
  fun rotateZ(angle: Float): Vector3m {
    return rotateZ(angle, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#rotateZ(float, Vector3m)
     */
  fun rotateZ(angle: Float, dest: Vector3m): Vector3m {
    val sin = Math.sin(angle.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val x = this.x * cos - this.y * sin
    val y = this.x * sin + this.y * cos
    dest.x = x
    dest.y = y
    dest.z = this.z
    return dest
  }

  /* (non-Javadoc)
     * @see Vector3m#lengthSquared()
     */
  fun lengthSquared(): Float {
    return x * x + y * y + z * z
  }

  /* (non-Javadoc)
     * @see Vector3m#length()
     */
  fun length(): Float {
    return Math.sqrt(lengthSquared().toDouble()).toFloat()
  }

  /**
   * Normalize this vector.
   *
   * @return a vector holding the result
   */
  fun normalize(): Vector3m {
    return normalize(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#normalize(Vector3m)
     */
  fun normalize(dest: Vector3m): Vector3m {
    val invLength = 1.0f / length()
    dest.x = x * invLength
    dest.y = y * invLength
    dest.z = z * invLength
    return dest
  }

  /**
   * Scale this vector to have the given length.
   *
   * @param length
   * the desired length
   * @return a vector holding the result
   */
  fun normalize(length: Float): Vector3m {
    return normalize(length, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#normalize(float, Vector3m)
     */
  fun normalize(length: Float, dest: Vector3m): Vector3m {
    val invLength = 1.0f / length() * length
    dest.x = x * invLength
    dest.y = y * invLength
    dest.z = z * invLength
    return dest
  }

  /**
   * Set this vector to be the cross product of itself and `v`.
   *
   * @param v
   * the other vector
   * @return a vector holding the result
   */
  fun cross(v: Vector3m): Vector3m {
    return cross(v, thisOrNew())
  }

  /**
   * Set this vector to be the cross product of itself and <tt>(x, y, z)</tt>.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @param z
   * the z component of the other vector
   * @return a vector holding the result
   */
  fun cross(x: Float, y: Float, z: Float): Vector3m {
    return cross(x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#cross(Vector3m, Vector3m)
     */
  fun cross(v: Vector3m, dest: Vector3m): Vector3m {
    val rx = y * v.z - z * v.y
    val ry = z * v.x - x * v.z
    val rz = x * v.y - y * v.x
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see Vector3m#cross(float, float, float, Vector3m)
     */
  fun cross(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m {
    val rx = this.y * z - this.z * y
    val ry = this.z * x - this.x * z
    val rz = this.x * y - this.y * x
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see Vector3m#distance(Vector3m)
     */
  fun distance(v: Vector3m): Float {
    return distance(v.x, v.y, v.z)
  }

  /* (non-Javadoc)
     * @see Vector3m#distance(float, float, float)
     */
  fun distance(x: Float, y: Float, z: Float): Float {
    val dx = this.x - x
    val dy = this.y - y
    val dz = this.z - z
    return Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()
  }

  /* (non-Javadoc)
     * @see Vector3m#distanceSquared(Vector3m)
     */
  fun distanceSquared(v: Vector3m): Float {
    return distanceSquared(v.x, v.y, v.z)
  }

  /* (non-Javadoc)
     * @see Vector3m#distanceSquared(float, float, float)
     */
  fun distanceSquared(x: Float, y: Float, z: Float): Float {
    val dx = this.x - x
    val dy = this.y - y
    val dz = this.z - z
    return dx * dx + dy * dy + dz * dz
  }

  /* (non-Javadoc)
     * @see Vector3m#dot(Vector3m)
     */
  fun dot(v: Vector3m): Float {
    return dot(v.x, v.y, v.z)
  }

  /* (non-Javadoc)
     * @see Vector3m#dot(float, float, float)
     */
  fun dot(x: Float, y: Float, z: Float): Float {
    return this.x * x + this.y * y + this.z * z
  }

  /* (non-Javadoc)
     * @see Vector3m#angleCos(Vector3m)
     */
  fun angleCos(v: Vector3m): Float {
    val length1Sqared = (x * x + y * y + z * z).toDouble()
    val length2Sqared = (v.x * v.x + v.y * v.y + v.z * v.z).toDouble()
    val dot = (x * v.x + y * v.y + z * v.z).toDouble()
    return (dot / Math.sqrt(length1Sqared * length2Sqared)).toFloat()
  }

  /* (non-Javadoc)
     * @see Vector3m#angle(Vector3m)
     */
  fun angle(v: Vector3m): Float {
    var cos = angleCos(v)
    // This is because sometimes cos goes above 1 or below -1 because of lost precision
    cos = if (cos < 1) cos else 1f
    cos = if (cos > -1) cos else -1f
    return Math.acos(cos.toDouble()).toFloat()
  }

  /**
   * Set the components of this vector to be the component-wise minimum of this and the other vector.
   *
   * @param v
   * the other vector
   * @return a vector holding the result
   */
  fun min(v: Vector3m): Vector3m {
    return min(v, thisOrNew())
  }

  fun min(v: Vector3m, dest: Vector3m): Vector3m {
    dest.x = if (x < v.x) x else v.x
    dest.y = if (y < v.y) y else v.y
    dest.z = if (z < v.z) z else v.z
    return this
  }

  /**
   * Set the components of this vector to be the component-wise maximum of this and the other vector.
   *
   * @param v
   * the other vector
   * @return a vector holding the result
   */
  fun max(v: Vector3m): Vector3m {
    return max(v, thisOrNew())
  }

  fun max(v: Vector3m, dest: Vector3m): Vector3m {
    dest.x = if (x > v.x) x else v.x
    dest.y = if (y > v.y) y else v.y
    dest.z = if (z > v.z) z else v.z
    return this
  }

  /**
   * Set all components to zero.
   *
   * @return a vector holding the result
   */
  fun zero(): Vector3m {
    return thisOrNew().set(0f, 0f, 0f)
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
    return "(" + formatter.format(x.toDouble()) + ", " + formatter.format(y.toDouble()) + ", " + formatter.format(z.toDouble()) + ")"
  }

  @Throws(IOException::class)
  fun writeExternal(out: ObjectOutput) {
    out.writeFloat(x)
    out.writeFloat(y)
    out.writeFloat(z)
  }

  @Throws(IOException::class, ClassNotFoundException::class)
  fun readExternal(`in`: ObjectInput) {
    x = `in`.readFloat()
    y = `in`.readFloat()
    z = `in`.readFloat()
  }

  /**
   * Negate this vector.
   *
   * @return a vector holding the result
   */
  fun negate(): Vector3m {
    return negate(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#negate(Vector3m)
     */
  fun negate(dest: Vector3m): Vector3m {
    dest.x = -x
    dest.y = -y
    dest.z = -z
    return dest
  }

  /**
   * Set `this` vector's components to their respective absolute values.
   *
   * @return a vector holding the result
   */
  fun absolute(): Vector3m {
    return absolute(thisOrNew())
  }

  /*
     * (non-Javadoc)
     * @see Vector3m#absolute(Vector3m)
     */
  fun absolute(dest: Vector3m): Vector3m {
    dest.x = Math.abs(this.x)
    dest.y = Math.abs(this.y)
    dest.z = Math.abs(this.z)
    return dest
  }

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
    val other = obj as Vector3m?
    if (java.lang.Float.floatToIntBits(x) != java.lang.Float.floatToIntBits(other!!.x))
      return false
    if (java.lang.Float.floatToIntBits(y) != java.lang.Float.floatToIntBits(other.y))
      return false
    return if (java.lang.Float.floatToIntBits(z) != java.lang.Float.floatToIntBits(other.z)) false else true
  }

  /**
   * Reflect this vector about the given `normal` vector.
   *
   * @param normal
   * the vector to reflect about
   * @return a vector holding the result
   */
  fun reflect(normal: Vector3m): Vector3m {
    return reflect(normal, thisOrNew())
  }

  /**
   * Reflect this vector about the given normal vector.
   *
   * @param x
   * the x component of the normal
   * @param y
   * the y component of the normal
   * @param z
   * the z component of the normal
   * @return a vector holding the result
   */
  fun reflect(x: Float, y: Float, z: Float): Vector3m {
    return reflect(x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#reflect(Vector3m, Vector3m)
     */
  fun reflect(normal: Vector3m, dest: Vector3m): Vector3m {
    return reflect(normal.x, normal.y, normal.z, dest)
  }

  /* (non-Javadoc)
     * @see Vector3m#reflect(float, float, float, Vector3m)
     */
  fun reflect(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m {
    val dot = this.dot(x, y, z)
    dest.x = this.x - (dot + dot) * x
    dest.y = this.y - (dot + dot) * y
    dest.z = this.z - (dot + dot) * z
    return dest
  }

  /* (non-Javadoc)
     * @see Vector3m#smoothStep(Vector3m, float, Vector3m)
     */
  fun smoothStep(v: Vector3m, t: Float, dest: Vector3m): Vector3m {
    val t2 = t * t
    val t3 = t2 * t
    dest.x = (x + x - v.x - v.x) * t3 + (3.0f * v.x - 3.0f * x) * t2 + x * t + x
    dest.y = (y + y - v.y - v.y) * t3 + (3.0f * v.y - 3.0f * y) * t2 + y * t + y
    dest.z = (z + z - v.z - v.z) * t3 + (3.0f * v.z - 3.0f * z) * t2 + z * t + z
    return dest
  }

  /* (non-Javadoc)
     * @see Vector3m#hermite(Vector3m, Vector3m, Vector3m, float, Vector3m)
     */
  fun hermite(t0: Vector3m, v1: Vector3m, t1: Vector3m, t: Float, dest: Vector3m): Vector3m {
    val t2 = t * t
    val t3 = t2 * t
    dest.x = (x + x - v1.x - v1.x + t1.x + t0.x) * t3 + (3.0f * v1.x - 3.0f * x - t0.x - t0.x - t1.x) * t2 + x * t + x
    dest.y = (y + y - v1.y - v1.y + t1.y + t0.y) * t3 + (3.0f * v1.y - 3.0f * y - t0.y - t0.y - t1.y) * t2 + y * t + y
    dest.z = (z + z - v1.z - v1.z + t1.z + t0.z) * t3 + (3.0f * v1.z - 3.0f * z - t0.z - t0.z - t1.z) * t2 + z * t + z
    return dest
  }

  /**
   * Linearly interpolate `this` and `other` using the given interpolation factor `t`
   * and store the result in `this`.
   *
   *
   * If `t` is <tt>0.0</tt> then the result is `this`. If the interpolation factor is `1.0`
   * then the result is `other`.
   *
   * @param other
   * the other vector
   * @param t
   * the interpolation factor between 0.0 and 1.0
   * @return a vector holding the result
   */
  fun lerp(other: Vector3m, t: Float): Vector3m {
    return lerp(other, t, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#lerp(Vector3m, float, Vector3m)
     */
  fun lerp(other: Vector3m, t: Float, dest: Vector3m): Vector3m {
    dest.x = x + (other.x - x) * t
    dest.y = y + (other.y - y) * t
    dest.z = z + (other.z - z) * t
    return dest
  }

  /* (non-Javadoc)
     * @see Vector3m#get(int)
     */
  @Throws(IllegalArgumentException::class)
  fun get(component: Int): Float {
    when (component) {
      0 -> return x
      1 -> return y
      2 -> return z
      else -> throw IllegalArgumentException()
    }
  }

  /* (non-Javadoc)
     * @see Vector3m#maxComponent()
     */
  fun maxComponent(): Int {
    val absX = Math.abs(x)
    val absY = Math.abs(y)
    val absZ = Math.abs(z)
    if (absX >= absY && absX >= absZ) {
      return 0
    } else if (absY >= absZ) {
      return 1
    }
    return 2
  }

  /* (non-Javadoc)
     * @see Vector3m#minComponent()
     */
  fun minComponent(): Int {
    val absX = Math.abs(x)
    val absY = Math.abs(y)
    val absZ = Math.abs(z)
    if (absX < absY && absX < absZ) {
      return 0
    } else if (absY < absZ) {
      return 1
    }
    return 2
  }

  /* (non-Javadoc)
     * @see Vector3m#orthogonalize(Vector3m, Vector3m)
     */
  fun orthogonalize(v: Vector3m, dest: Vector3m): Vector3m {
    /*
         * http://lolengine.net/blog/2013/09/21/picking-orthogonal-vector-combing-coconuts
         */
    val rx: Float
    val ry: Float
    val rz: Float
    if (Math.abs(v.x) > Math.abs(v.z)) {
      rx = -v.y
      ry = v.x
      rz = 0.0f
    } else {
      rx = 0.0f
      ry = -v.z
      rz = v.y
    }
    val invLen = 1.0f / Math.sqrt((rx * rx + ry * ry + rz * rz).toDouble()).toFloat()
    dest.x = rx * invLen
    dest.y = ry * invLen
    dest.z = rz * invLen
    return dest
  }

  /**
   * Transform `this` vector so that it is orthogonal to the given vector `v` and normalize the result.
   *
   *
   * Reference: [Gram–Schmidt process](https://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process)
   *
   * @param v
   * the reference vector which the result should be orthogonal to
   * @return a vector holding the result
   */
  fun orthogonalize(v: Vector3m): Vector3m {
    return orthogonalize(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector3m#orthogonalizeUnit(Vector3m, Vector3m)
     */
  fun orthogonalizeUnit(v: Vector3m, dest: Vector3m): Vector3m {
    return orthogonalize(v, dest)
  }

  /**
   * Transform `this` vector so that it is orthogonal to the given unit vector `v` and normalize the result.
   *
   *
   * The vector `v` is assumed to be a [unit][.normalize] vector.
   *
   *
   * Reference: [Gram–Schmidt process](https://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process)
   *
   * @param v
   * the reference unit vector which the result should be orthogonal to
   * @return a vector holding the result
   */
  fun orthogonalizeUnit(v: Vector3m): Vector3m {
    return orthogonalizeUnit(v, thisOrNew())
  }

  override operator fun minus(v: Vector2fMinimal): Vector2 =
      Vector2(x - v.x, y - v.y)

  operator fun minus(v: Vector3m): Vector3m =
      Vector3m(x - v.x, y - v.y, z - v.z)

  companion object {

    private val serialVersionUID = 1L
  }

  override fun xy(): Vector2 = Vector2(x, y)
}
