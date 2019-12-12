
package mythic.spatial

import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.text.DecimalFormat
import java.text.NumberFormat

import org.joml.*
import org.joml.Math
import org.joml.internal.Options
import org.joml.internal.Runtime

/**
 * Contains the definition of a Vector comprising 3 floats and associated
 * transformations.
 *
 * @author Richard Greenlees
 * @author Kai Burjack
 */
class MutableVector3 : Vector3int {

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
  override var z: Float = 0.toFloat()

  /**
   * Create a new [MutableVector3] of <tt>(0, 0, 0)</tt>.
   */
  constructor() {}

  /**
   * Create a new [MutableVector3] and initialize all three components with the given value.
   *
   * @param d
   * the value of all three components
   */
  constructor(d: Float) : this(d, d, d) {}

  /**
   * Create a new [MutableVector3] with the given component values.
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
   * Create a new [MutableVector3] with the same values as `v`.
   *
   * @param v
   * the [MutableVector3] to copy the values from
   */
  constructor(v: MutableVector3) : this(v.x(), v.y(), v.z()) {}

  /**
   * Create a new [MutableVector3] with the same values as `v`.
   *
   * @param v
   * the [Vector3ic] to copy the values from
   */
  constructor(v: Vector3ic) : this(v.x().toFloat(), v.y().toFloat(), v.z().toFloat()) {}

  private fun thisOrNew(): MutableVector3 {
    return this
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#x()
     */
  fun x(): Float {
    return this.x
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#y()
     */
  fun y(): Float {
    return this.y
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#z()
     */
  fun z(): Float {
    return this.z
  }

  /**
   * Set the x, y and z components to match the supplied vector.
   *
   * @param v
   * contains the values of x, y and z to set
   * @return this
   */
  fun set(v: MutableVector3): MutableVector3 {
    return set(v.x(), v.y(), v.z())
  }

  fun set(v: Vector3): MutableVector3 {
    return set(v.x, v.y, v.z)
  }

  /**
   * Set the x, y and z components to match the supplied vector.
   *
   *
   * Note that due to the given vector `v` storing the components in double-precision,
   * there is the possibility to lose precision.
   *
   * @param v
   * contains the values of x, y and z to set
   * @return this
   */
  fun set(v: Vector3dc): MutableVector3 {
    return set(v.x().toFloat(), v.y().toFloat(), v.z().toFloat())
  }

  /**
   * Set the x, y and z components to match the supplied vector.
   *
   * @param v
   * contains the values of x, y and z to set
   * @return this
   */
  fun set(v: Vector3ic): MutableVector3 {
    return set(v.x().toFloat(), v.y().toFloat(), v.z().toFloat())
  }

  /**
   * Set the x, y, and z components to the supplied value.
   *
   * @param d
   * the value of all three components
   * @return this
   */
  fun set(d: Float): MutableVector3 {
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
  operator fun set(x: Float, y: Float, z: Float): MutableVector3 {
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
  fun setComponent(component: Int, value: Float): MutableVector3 {
    when (component) {
      0 -> x = value
      1 -> y = value
      2 -> z = value
      else -> throw IllegalArgumentException()
    }
    return this
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#sub(org.joml.MutableVector3, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun sub(v: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = x - v.x()
    dest.y = y - v.y()
    dest.z = z - v.z()
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#sub(float, float, float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun sub(x: Float, y: Float, z: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = this.x - x
    dest.y = this.y - y
    dest.z = this.z - z
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#add(org.joml.MutableVector3, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun add(v: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = x + v.x()
    dest.y = y + v.y()
    dest.z = z + v.z()
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#add(float, float, float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun add(x: Float, y: Float, z: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = this.x + x
    dest.y = this.y + y
    dest.z = this.z + z
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#fma(org.joml.MutableVector3, org.joml.MutableVector3, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun fma(a: MutableVector3, b: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = x + a.x() * b.x()
    dest.y = y + a.y() * b.y()
    dest.z = z + a.z() * b.z()
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#fma(float, org.joml.MutableVector3, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun fma(a: Float, b: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = x + a * b.x()
    dest.y = y + a * b.y()
    dest.z = z + a * b.z()
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mul(org.joml.MutableVector3, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mul(v: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = x * v.x()
    dest.y = y * v.y()
    dest.z = z * v.z()
    return dest
  }

  /**
   * Divide this MutableVector3 component-wise by another MutableVector3.
   *
   * @param v
   * the vector to divide by
   * @return a vector holding the result
   */
  operator fun div(v: MutableVector3): MutableVector3 {
    return div(v.x(), v.y(), v.z(), thisOrNew())
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#div(org.joml.MutableVector3, org.joml.MutableVector3)
     */
  fun div(v: MutableVector3, dest: MutableVector3): MutableVector3 {
    dest.x = x / v.x()
    dest.y = y / v.y()
    dest.z = z / v.z()
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mulProject(Matrix4fc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mulProject(mat: Matrix4fc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val invW = 1.0f / (mat.m03() * x + mat.m13() * y + mat.m23() * z + mat.m33())
    val rx = (mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30()) * invW
    val ry = (mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31()) * invW
    val rz = (mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32()) * invW
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mul(org.joml.Matrix3fc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mul(mat: Matrix3fc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mul(org.joml.Matrix3dc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mul(mat: Matrix3dc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z
    dest.x = rx.toFloat()
    dest.y = ry.toFloat()
    dest.z = rz.toFloat()
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mul(org.joml.Matrix3x2fc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mul(mat: Matrix3x2fc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z
    dest.x = rx
    dest.y = ry
    dest.z = z
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mulTranspose(org.joml.Matrix3fc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mulTranspose(mat: Matrix3fc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = mat.m00() * x + mat.m01() * y + mat.m02() * z
    val ry = mat.m10() * x + mat.m11() * y + mat.m12() * z
    val rz = mat.m20() * x + mat.m21() * y + mat.m22() * z
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mulPosition(Matrix4fc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mulPosition(mat: Matrix4fc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30()
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31()
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32()
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mulPosition(org.joml.Matrix4x3fc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mulPosition(mat: Matrix4x3fc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30()
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31()
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32()
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mulTransposePosition(Matrix4fc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mulTransposePosition(mat: Matrix4fc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = mat.m00() * x + mat.m01() * y + mat.m02() * z + mat.m03()
    val ry = mat.m10() * x + mat.m11() * y + mat.m12() * z + mat.m13()
    val rz = mat.m20() * x + mat.m21() * y + mat.m22() * z + mat.m23()
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mulPositionW(Matrix4fc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mulPositionW(mat: Matrix4fc, dest: MutableVector3 = thisOrNew()): Float {
    val w = mat.m03() * x + mat.m13() * y + mat.m23() * z + mat.m33()
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30()
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31()
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32()
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return w
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mulDirection(org.joml.Matrix4dc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mulDirection(mat: Matrix4dc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z
    dest.x = rx.toFloat()
    dest.y = ry.toFloat()
    dest.z = rz.toFloat()
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mulDirection(Matrix4fc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mulDirection(mat: Matrix4fc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mulDirection(org.joml.Matrix4x3fc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mulDirection(mat: Matrix4x3fc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mulTransposeDirection(Matrix4fc, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mulTransposeDirection(mat: Matrix4fc, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = mat.m00() * x + mat.m01() * y + mat.m02() * z
    val ry = mat.m10() * x + mat.m11() * y + mat.m12() * z
    val rz = mat.m20() * x + mat.m21() * y + mat.m22() * z
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mul(float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mul(scalar: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = x * scalar
    dest.y = y * scalar
    dest.z = z * scalar
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#mul(float, float, float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun mul(x: Float, y: Float, z: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = this.x * x
    dest.y = this.y * y
    dest.z = this.z * z
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#div(float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun div(scalar: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val inv = 1.0f / scalar
    dest.x = x * inv
    dest.y = y * inv
    dest.z = z * inv
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#div(float, float, float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun div(x: Float, y: Float, z: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = this.x / x
    dest.y = this.y / y
    dest.z = this.z / z
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#rotationTo(float, float, float, Quaternionf)
     */
  fun rotationTo(toDirX: Float, toDirY: Float, toDirZ: Float, dest: Quaternionf): Quaternionf {
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
  fun rotateAxis(angle: Float, x: Float, y: Float, z: Float): MutableVector3 {
    return rotateAxis(angle, x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#rotateAxis(float, float, float, float, org.joml.MutableVector3)
     */
  fun rotateAxis(angle: Float, aX: Float, aY: Float, aZ: Float, dest: MutableVector3): MutableVector3 {
    val hangle = angle * 0.5f
    val sinAngle = org.joml.Math.sin(hangle.toDouble()).toFloat()
    val qx = aX * sinAngle
    val qy = aY * sinAngle
    val qz = aZ * sinAngle
    val qw = org.joml.Math.cosFromSin(sinAngle.toDouble(), hangle.toDouble()).toFloat()
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

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#rotateX(float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun rotateX(angle: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val sin = org.joml.Math.sin(angle.toDouble()).toFloat()
    val cos = org.joml.Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val y = this.y * cos - this.z * sin
    val z = this.y * sin + this.z * cos
    dest.x = this.x
    dest.y = y
    dest.z = z
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#rotateY(float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun rotateY(angle: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val sin = org.joml.Math.sin(angle.toDouble()).toFloat()
    val cos = org.joml.Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val x = this.x * cos + this.z * sin
    val z = -this.x * sin + this.z * cos
    dest.x = x
    dest.y = this.y
    dest.z = z
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#rotateZ(float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun rotateZ(angle: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val sin = org.joml.Math.sin(angle.toDouble()).toFloat()
    val cos = org.joml.Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val x = this.x * cos - this.y * sin
    val y = this.x * sin + this.y * cos
    dest.x = x
    dest.y = y
    dest.z = this.z
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#lengthSquared()
     */
  fun lengthSquared(): Float {
    return x * x + y * y + z * z
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#length()
     */
  fun length(): Float {
    return org.joml.Math.sqrt(lengthSquared().toDouble()).toFloat()
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#normalize(org.joml.MutableVector3)
     */
  @JvmOverloads
  fun normalize(dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val invLength = 1.0f / length()
    dest.x = x * invLength
    dest.y = y * invLength
    dest.z = z * invLength
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#normalize(float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun normalize(length: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val invLength = 1.0f / length() * length
    dest.x = x * invLength
    dest.y = y * invLength
    dest.z = z * invLength
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#cross(org.joml.MutableVector3, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun cross(v: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = y * v.z() - z * v.y()
    val ry = z * v.x() - x * v.z()
    val rz = x * v.y() - y * v.x()
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#cross(float, float, float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun cross(x: Float, y: Float, z: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val rx = this.y * z - this.z * y
    val ry = this.z * x - this.x * z
    val rz = this.x * y - this.y * x
    dest.x = rx
    dest.y = ry
    dest.z = rz
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#distance(org.joml.MutableVector3)
     */
  fun distance(v: MutableVector3): Float {
    return distance(v.x(), v.y(), v.z())
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#distance(float, float, float)
     */
  fun distance(x: Float, y: Float, z: Float): Float {
    val dx = this.x - x
    val dy = this.y - y
    val dz = this.z - z
    return org.joml.Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#distanceSquared(org.joml.MutableVector3)
     */
  fun distanceSquared(v: MutableVector3): Float {
    return distanceSquared(v.x(), v.y(), v.z())
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#distanceSquared(float, float, float)
     */
  fun distanceSquared(x: Float, y: Float, z: Float): Float {
    val dx = this.x - x
    val dy = this.y - y
    val dz = this.z - z
    return dx * dx + dy * dy + dz * dz
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#dot(org.joml.MutableVector3)
     */
  fun dot(v: MutableVector3): Float {
    return dot(v.x(), v.y(), v.z())
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#dot(float, float, float)
     */
  fun dot(x: Float, y: Float, z: Float): Float {
    return this.x * x + this.y * y + this.z * z
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#angleCos(org.joml.MutableVector3)
     */
  fun angleCos(v: MutableVector3): Float {
    val length1Sqared = (x * x + y * y + z * z).toDouble()
    val length2Sqared = (v.x() * v.x() + v.y() * v.y() + v.z() * v.z()).toDouble()
    val dot = (x * v.x() + y * v.y() + z * v.z()).toDouble()
    return (dot / org.joml.Math.sqrt(length1Sqared * length2Sqared)).toFloat()
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#angle(org.joml.MutableVector3)
     */
  fun angle(v: MutableVector3): Float {
    var cos = angleCos(v)
    // This is because sometimes cos goes above 1 or below -1 because of lost precision
    cos = if (cos < 1) cos else 1f
    cos = if (cos > -1) cos else -1f
    return org.joml.Math.acos(cos.toDouble()).toFloat()
  }

  @JvmOverloads
  fun min(v: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = if (x < v.x()) x else v.x()
    dest.y = if (y < v.y()) y else v.y()
    dest.z = if (z < v.z()) z else v.z()
    return this
  }

  @JvmOverloads
  fun max(v: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = if (x > v.x()) x else v.x()
    dest.y = if (y > v.y()) y else v.y()
    dest.z = if (z > v.z()) z else v.z()
    return this
  }

  /**
   * Set all components to zero.
   *
   * @return a vector holding the result
   */
  fun zero(): MutableVector3 {
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
    return "(" + formatter.format(x.toDouble()) + " " + formatter.format(y.toDouble()) + " " + formatter.format(z.toDouble()) + ")"
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

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#negate(org.joml.MutableVector3)
     */
  @JvmOverloads
  fun negate(dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = -x
    dest.y = -y
    dest.z = -z
    return dest
  }

  /*
     * (non-Javadoc)
     * @see org.joml.MutableVector3#absolute(org.joml.MutableVector3)
     */
  @JvmOverloads
  fun absolute(dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = org.joml.Math.abs(this.x)
    dest.y = org.joml.Math.abs(this.y)
    dest.z = org.joml.Math.abs(this.z)
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
    val other = obj as MutableVector3?
    if (java.lang.Float.floatToIntBits(x) != java.lang.Float.floatToIntBits(other!!.x))
      return false
    if (java.lang.Float.floatToIntBits(y) != java.lang.Float.floatToIntBits(other.y))
      return false
    return if (java.lang.Float.floatToIntBits(z) != java.lang.Float.floatToIntBits(other.z)) false else true
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#reflect(org.joml.MutableVector3, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun reflect(normal: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    return reflect(normal.x(), normal.y(), normal.z(), dest)
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#reflect(float, float, float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun reflect(x: Float, y: Float, z: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    val dot = this.dot(x, y, z)
    dest.x = this.x - (dot + dot) * x
    dest.y = this.y - (dot + dot) * y
    dest.z = this.z - (dot + dot) * z
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#half(org.joml.MutableVector3, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun half(other: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    return half(other.x(), other.y(), other.z(), dest)
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#half(float, float, float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun half(x: Float, y: Float, z: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    return dest.set(this).add(x, y, z).normalize()
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#smoothStep(org.joml.MutableVector3, float, org.joml.MutableVector3)
     */
  fun smoothStep(v: MutableVector3, t: Float, dest: MutableVector3): MutableVector3 {
    val t2 = t * t
    val t3 = t2 * t
    dest.x = (x + x - v.x() - v.x()) * t3 + (3.0f * v.x() - 3.0f * x) * t2 + x * t + x
    dest.y = (y + y - v.y() - v.y()) * t3 + (3.0f * v.y() - 3.0f * y) * t2 + y * t + y
    dest.z = (z + z - v.z() - v.z()) * t3 + (3.0f * v.z() - 3.0f * z) * t2 + z * t + z
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#hermite(org.joml.MutableVector3, org.joml.MutableVector3, org.joml.MutableVector3, float, org.joml.MutableVector3)
     */
  fun hermite(t0: MutableVector3, v1: MutableVector3, t1: MutableVector3, t: Float, dest: MutableVector3): MutableVector3 {
    val t2 = t * t
    val t3 = t2 * t
    dest.x = (x + x - v1.x() - v1.x() + t1.x() + t0.x()) * t3 + (3.0f * v1.x() - 3.0f * x - t0.x() - t0.x() - t1.x()) * t2 + x * t + x
    dest.y = (y + y - v1.y() - v1.y() + t1.y() + t0.y()) * t3 + (3.0f * v1.y() - 3.0f * y - t0.y() - t0.y() - t1.y()) * t2 + y * t + y
    dest.z = (z + z - v1.z() - v1.z() + t1.z() + t0.z()) * t3 + (3.0f * v1.z() - 3.0f * z - t0.z() - t0.z() - t1.z()) * t2 + z * t + z
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#lerp(org.joml.MutableVector3, float, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun lerp(other: MutableVector3, t: Float, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    dest.x = x + (other.x() - x) * t
    dest.y = y + (other.y() - y) * t
    dest.z = z + (other.z() - z) * t
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#get(int)
     */
  @Throws(IllegalArgumentException::class)
  operator fun get(component: Int): Float {
    when (component) {
      0 -> return x
      1 -> return y
      2 -> return z
      else -> throw IllegalArgumentException()
    }
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#maxComponent()
     */
  fun maxComponent(): Int {
    val absX = org.joml.Math.abs(x)
    val absY = org.joml.Math.abs(y)
    val absZ = org.joml.Math.abs(z)
    if (absX >= absY && absX >= absZ) {
      return 0
    } else if (absY >= absZ) {
      return 1
    }
    return 2
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#minComponent()
     */
  fun minComponent(): Int {
    val absX = org.joml.Math.abs(x)
    val absY = org.joml.Math.abs(y)
    val absZ = org.joml.Math.abs(z)
    if (absX < absY && absX < absZ) {
      return 0
    } else if (absY < absZ) {
      return 1
    }
    return 2
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#orthogonalize(org.joml.MutableVector3, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun orthogonalize(v: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    /*
         * http://lolengine.net/blog/2013/09/21/picking-orthogonal-vector-combing-coconuts
         */
    val rx: Float
    val ry: Float
    val rz: Float
    if (org.joml.Math.abs(v.x()) > org.joml.Math.abs(v.z())) {
      rx = -v.y()
      ry = v.x()
      rz = 0.0f
    } else {
      rx = 0.0f
      ry = -v.z()
      rz = v.y()
    }
    val invLen = 1.0f / Math.sqrt((rx * rx + ry * ry + rz * rz).toDouble()).toFloat()
    dest.x = rx * invLen
    dest.y = ry * invLen
    dest.z = rz * invLen
    return dest
  }

  /* (non-Javadoc)
     * @see org.joml.MutableVector3#orthogonalizeUnit(org.joml.MutableVector3, org.joml.MutableVector3)
     */
  @JvmOverloads
  fun orthogonalizeUnit(v: MutableVector3, dest: MutableVector3 = thisOrNew()): MutableVector3 {
    return orthogonalize(v, dest)
  }

  companion object {

    private val serialVersionUID = 1L
  }

}
/**
 * Subtract the supplied vector from this one and store the result in `this`.
 *
 * @param v
 * the vector to subtract
 * @return a vector holding the result
 */
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
/**
 * Add the supplied vector to this one.
 *
 * @param v
 * the vector to add
 * @return a vector holding the result
 */
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
/**
 * Add the component-wise multiplication of `a * b` to this vector.
 *
 * @param a
 * the first multiplicand
 * @param b
 * the second multiplicand
 * @return a vector holding the result
 */
/**
 * Add the component-wise multiplication of `a * b` to this vector.
 *
 * @param a
 * the first multiplicand
 * @param b
 * the second multiplicand
 * @return a vector holding the result
 */
/**
 * Multiply this MutableVector3 component-wise by another MutableVector3.
 *
 * @param v
 * the vector to multiply by
 * @return a vector holding the result
 */
/**
 * Multiply the given matrix `mat` with this MutableVector3, perform perspective division.
 *
 *
 * This method uses <tt>w=1.0</tt> as the fourth vector component.
 *
 * @param mat
 * the matrix to multiply this vector by
 * @return a vector holding the result
 */
/**
 * Multiply the given matrix with this MutableVector3 and store the result in `this`.
 *
 * @param mat
 * the matrix
 * @return a vector holding the result
 */
/**
 * Multiply the given matrix with this MutableVector3 and store the result in `this`.
 *
 * @param mat
 * the matrix
 * @return a vector holding the result
 */
/**
 * Multiply the given matrix with this MutableVector3 and store the result in `this`.
 *
 * @param mat
 * the matrix
 * @return a vector holding the result
 */
/**
 * Multiply the transpose of the given matrix with this MutableVector3 store the result in `this`.
 *
 * @param mat
 * the matrix
 * @return a vector holding the result
 */
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
/**
 * Multiply the given 4x3 matrix `mat` with `this`.
 *
 *
 * This method assumes the <tt>w</tt> component of `this` to be <tt>1.0</tt>.
 *
 * @param mat
 * the matrix to multiply this vector by
 * @return a vector holding the result
 */
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
/**
 * Multiply the given 4x3 matrix `mat` with `this`.
 *
 *
 * This method assumes the <tt>w</tt> component of `this` to be <tt>0.0</tt>.
 *
 * @param mat
 * the matrix to multiply this vector by
 * @return a vector holding the result
 */
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
/**
 * Multiply all components of this [MutableVector3] by the given scalar
 * value.
 *
 * @param scalar
 * the scalar to multiply this vector by
 * @return a vector holding the result
 */
/**
 * Multiply the components of this MutableVector3 by the given scalar values and store the result in `this`.
 *
 * @param x
 * the x component to multiply this vector by
 * @param y
 * the y component to multiply this vector by
 * @param z
 * the z component to multiply this vector by
 * @return a vector holding the result
 */
/**
 * Divide all components of this [MutableVector3] by the given scalar
 * value.
 *
 * @param scalar
 * the scalar to divide by
 * @return a vector holding the result
 */
/**
 * Divide the components of this MutableVector3 by the given scalar values and store the result in `this`.
 *
 * @param x
 * the x component to divide this vector by
 * @param y
 * the y component to divide this vector by
 * @param z
 * the z component to divide this vector by
 * @return a vector holding the result
 */
/**
 * Rotate this vector the specified radians around the X axis.
 *
 * @param angle
 * the angle in radians
 * @return a vector holding the result
 */
/**
 * Rotate this vector the specified radians around the Y axis.
 *
 * @param angle
 * the angle in radians
 * @return a vector holding the result
 */
/**
 * Rotate this vector the specified radians around the Z axis.
 *
 * @param angle
 * the angle in radians
 * @return a vector holding the result
 */
/**
 * Normalize this vector.
 *
 * @return a vector holding the result
 */
/**
 * Scale this vector to have the given length.
 *
 * @param length
 * the desired length
 * @return a vector holding the result
 */
/**
 * Set this vector to be the cross product of itself and `v`.
 *
 * @param v
 * the other vector
 * @return a vector holding the result
 */
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
/**
 * Set the components of this vector to be the component-wise minimum of this and the other vector.
 *
 * @param v
 * the other vector
 * @return a vector holding the result
 */
/**
 * Set the components of this vector to be the component-wise maximum of this and the other vector.
 *
 * @param v
 * the other vector
 * @return a vector holding the result
 */
/**
 * Negate this vector.
 *
 * @return a vector holding the result
 */
/**
 * Set `this` vector's components to their respective absolute values.
 *
 * @return a vector holding the result
 */
/**
 * Reflect this vector about the given `normal` vector.
 *
 * @param normal
 * the vector to reflect about
 * @return a vector holding the result
 */
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
/**
 * Compute the half vector between this and the other vector.
 *
 * @param other
 * the other vector
 * @return a vector holding the result
 */
/**
 * Compute the half vector between this and the vector <tt>(x, y, z)</tt>.
 *
 * @param x
 * the x component of the other vector
 * @param y
 * the y component of the other vector
 * @param z
 * the z component of the other vector
 * @return a vector holding the result
 */
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
