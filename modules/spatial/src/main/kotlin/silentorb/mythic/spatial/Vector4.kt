package silentorb.mythic.spatial

import org.joml.Matrix4x3fc
import org.joml.Vector2ic
import org.joml.Vector3f
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.text.NumberFormat

data class Vector4(
    var x: Float = 0.toFloat(),
    var y: Float = x,
    var z: Float = x,
    var w: Float = x
) {

  private fun thisOrNew(): Vector4 {
    return this
  }

  /**
   * Set the first three components of this to the components of
   * `v` and the last component to `w`.
   *
   * @param v
   * the [Vector3f] to copy
   * @param w
   * the w component
   * @return this
   */
  operator fun set(v: Vector3f, w: Float): Vector4 {
    return set(v.x, v.y, v.z, w)
  }

  /**
   * Sets the first two components of this to the components of given `v`
   * and last two components to the given `z`, and `w`.
   *
   * @param v
   * the [Vector2f]
   * @param z
   * the z component
   * @param w
   * the w component
   * @return this
   */
  operator fun set(v: Vector2f, z: Float, w: Float): Vector4 {
    return set(v.x, v.y, z, w)
  }

  /**
   * Sets the first two components of this to the components of given `v`
   * and last two components to the given `z`, and `w`.
   *
   * @param v
   * the [Vector2ic]
   * @param z
   * the z component
   * @param w
   * the w component
   * @return this
   */
  operator fun set(v: Vector2ic, z: Float, w: Float): Vector4 {
    return set(v.x.toFloat(), v.y.toFloat(), z, w)
  }

  /**
   * Set the x, y, z, and w components to the supplied values.
   *
   * @param x
   * the x component
   * @param y
   * the y component
   * @param z
   * the z component
   * @param w
   * the w component
   * @return this
   */
  operator fun set(x: Float, y: Float, z: Float, w: Float): Vector4 {
    this.x = x
    this.y = y
    this.z = z
    this.w = w
    return this
  }

  /**
   * Set the value of the specified component of this vector.
   *
   * @param component
   * the component whose value to set, within <tt>[0..3]</tt>
   * @param value
   * the value to set
   * @return this
   * @throws IllegalArgumentException if `component` is not within <tt>[0..3]</tt>
   */
  @Throws(IllegalArgumentException::class)
  fun setComponent(component: Int, value: Float): Vector4 {
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
   * @param v
   * the vector to subtract
   * @return a vector holding the result
   */
  fun sub(v: Vector4): Vector4 {
    return sub(v, thisOrNew())
  }

  /**
   * Subtract <tt>(x, y, z, w)</tt> from this.
   *
   * @param x
   * the x component to subtract
   * @param y
   * the y component to subtract
   * @param z
   * the z component to subtract
   * @param w
   * the w component to subtract
   * @return a vector holding the result
   */
  fun sub(x: Float, y: Float, z: Float, w: Float): Vector4 {
    return sub(x, y, z, w, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#sub(Vector4, Vector4)
     */
  fun sub(v: Vector4, dest: Vector4): Vector4 {
    return sub(v.x, v.y, v.z, v.w, dest)
  }

  /* (non-Javadoc)
     * @see Vector4#sub(float, float, float, float, Vector4)
     */
  fun sub(x: Float, y: Float, z: Float, w: Float, dest: Vector4): Vector4 {
    dest.x = this.x - x
    dest.y = this.y - y
    dest.z = this.z - z
    dest.w = this.w - w
    return dest
  }

  /**
   * Add the supplied vector to this one.
   *
   * @param v
   * the vector to add
   * @return a vector holding the result
   */
  fun add(v: Vector4): Vector4 {
    return add(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#add(Vector4, Vector4)
     */
  fun add(v: Vector4, dest: Vector4): Vector4 {
    dest.x = x + v.x
    dest.y = y + v.y
    dest.z = z + v.z
    dest.w = w + v.w
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
   * @param w
   * the w component to add
   * @return a vector holding the result
   */
  fun add(x: Float, y: Float, z: Float, w: Float): Vector4 {
    return add(x, y, z, w, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#add(float, float, float, float, Vector4)
     */
  fun add(x: Float, y: Float, z: Float, w: Float, dest: Vector4): Vector4 {
    dest.x = this.x + x
    dest.y = this.y + y
    dest.z = this.z + z
    dest.w = this.w + w
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
  fun fma(a: Vector4, b: Vector4): Vector4 {
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
  fun fma(a: Float, b: Vector4): Vector4 {
    return fma(a, b, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#fma(Vector4, Vector4, Vector4)
     */
  fun fma(a: Vector4, b: Vector4, dest: Vector4): Vector4 {
    dest.x = x + a.x * b.x
    dest.y = y + a.y * b.y
    dest.z = z + a.z * b.z
    dest.w = w + a.w * b.w
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4#fma(float, Vector4, Vector4)
     */
  fun fma(a: Float, b: Vector4, dest: Vector4): Vector4 {
    dest.x = x + a * b.x
    dest.y = y + a * b.y
    dest.z = z + a * b.z
    dest.w = w + a * b.w
    return dest
  }

  /**
   * Multiply this Vector4 component-wise by another Vector4.
   *
   * @param v
   * the other vector
   * @return a vector holding the result
   */
  fun mul(v: Vector4): Vector4 {
    return mul(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#mul(Vector4, Vector4)
     */
  fun mul(v: Vector4, dest: Vector4): Vector4 {
    dest.x = x * v.x
    dest.y = y * v.y
    dest.z = z * v.z
    dest.w = w * v.w
    return dest
  }

  /**
   * Divide this Vector4 component-wise by another Vector4.
   *
   * @param v
   * the vector to divide by
   * @return a vector holding the result
   */
  operator fun div(v: Vector4): Vector4 {
    return div(v, thisOrNew())
  }

  operator fun times(value: Float): Vector4 =
      Vector4(x * value, y * value, z * value, w * value)

  /* (non-Javadoc)
   * @see Vector4#div(Vector4, Vector4)
   */
  fun div(v: Vector4, dest: Vector4): Vector4 {
    dest.x = x / v.x
    dest.y = y / v.y
    dest.z = z / v.z
    dest.w = w / v.w
    return dest
  }

  /**
   * Multiply the given matrix mat with this Vector4 and store the result in
   * `this`.
   *
   * @param mat
   * the matrix to multiply the vector with
   * @return a vector holding the result
   */
  fun mul(mat: Matrix4f): Vector4 {
    return mul(mat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#mul(Matrix4f, Vector4)
     */
  fun mul(mat: Matrix4f, dest: Vector4): Vector4 {
    return if (mat.properties() and Matrix4f.PROPERTY_AFFINE != 0) mulAffine(mat, dest) else mulGeneric(mat, dest)
  }

  /* (non-Javadoc)
     * @see Vector4#mulAffine(Matrix4f, Vector4)
     */
  fun mulAffine(mat: Matrix4f, dest: Vector4): Vector4 {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30() * w
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31() * w
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32() * w
    dest.x = rx
    dest.y = ry
    dest.z = rz
    dest.w = w
    return dest
  }

  private fun mulGeneric(mat: Matrix4f, dest: Vector4): Vector4 {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30() * w
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31() * w
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32() * w
    val rw = mat.m03() * x + mat.m13() * y + mat.m23() * z + mat.m33() * w
    dest.x = rx
    dest.y = ry
    dest.z = rz
    dest.w = rw
    return dest
  }

  /**
   * Multiply the given matrix mat with this Vector4 and store the result in
   * `this`.
   *
   * @param mat
   * the matrix to multiply the vector with
   * @return a vector holding the result
   */
  fun mul(mat: Matrix4x3fc): Vector4 {
    return mul(mat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#mul(org.joml.Matrix4x3fc, Vector4)
     */
  fun mul(mat: Matrix4x3fc, dest: Vector4): Vector4 {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30() * w
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31() * w
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32() * w
    dest.x = rx
    dest.y = ry
    dest.z = rz
    dest.w = w
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4#mulProject(Matrix4f, Vector4)
     */
  fun mulProject(mat: Matrix4f, dest: Vector4): Vector4 {
    val invW = 1.0f / (mat.m03() * x + mat.m13() * y + mat.m23() * z + mat.m33() * w)
    val rx = (mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30()) * invW
    val ry = (mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31()) * invW
    val rz = (mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32()) * invW
    dest.x = rx
    dest.y = ry
    dest.z = rz
    dest.w = 1.0f
    return dest
  }

  /**
   * Multiply the given matrix `mat` with this Vector4, perform perspective division.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @return a vector holding the result
   */
  fun mulProject(mat: Matrix4f): Vector4 {
    return mulProject(mat, thisOrNew())
  }

  /**
   * Multiply all components of this [Vector4] by the given scalar
   * value.
   *
   * @param scalar
   * the scalar to multiply by
   * @return a vector holding the result
   */
  fun mul(scalar: Float): Vector4 {
    return mul(scalar, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#mul(float, Vector4)
     */
  fun mul(scalar: Float, dest: Vector4): Vector4 {
    dest.x = x * scalar
    dest.y = y * scalar
    dest.z = z * scalar
    dest.w = w * scalar
    return dest
  }

  /**
   * Multiply the components of this Vector4 by the given scalar values and store the result in `this`.
   *
   * @param x
   * the x component to multiply by
   * @param y
   * the y component to multiply by
   * @param z
   * the z component to multiply by
   * @param w
   * the w component to multiply by
   * @return a vector holding the result
   */
  fun mul(x: Float, y: Float, z: Float, w: Float): Vector4 {
    return mul(x, y, z, w, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#mul(float, float, float, float, Vector4)
     */
  fun mul(x: Float, y: Float, z: Float, w: Float, dest: Vector4): Vector4 {
    dest.x = this.x * x
    dest.y = this.y * y
    dest.z = this.z * z
    dest.w = this.w * w
    return dest
  }

  /**
   * Divide all components of this [Vector4] by the given scalar
   * value.
   *
   * @param scalar
   * the scalar to divide by
   * @return a vector holding the result
   */
  operator fun div(scalar: Float): Vector4 {
    return div(scalar, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#div(float, Vector4)
     */
  fun div(scalar: Float, dest: Vector4): Vector4 {
    val inv = 1.0f / scalar
    dest.x = x * inv
    dest.y = y * inv
    dest.z = z * inv
    dest.w = w * inv
    return dest
  }

  /**
   * Divide the components of this Vector4 by the given scalar values and store the result in `this`.
   *
   * @param x
   * the x component to divide by
   * @param y
   * the y component to divide by
   * @param z
   * the z component to divide by
   * @param w
   * the w component to divide by
   * @return a vector holding the result
   */
  fun div(x: Float, y: Float, z: Float, w: Float): Vector4 {
    return div(x, y, z, w, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#div(float, float, float, float, Vector4)
     */
  fun div(x: Float, y: Float, z: Float, w: Float, dest: Vector4): Vector4 {
    dest.x = this.x / x
    dest.y = this.y / y
    dest.z = this.z / z
    dest.w = this.w / w
    return dest
  }

  /**
   * Rotate this vector by the given quaternion `quat` and store the result in `this`.
   *
   * @see Quaternion.transform
   * @param quat
   * the quaternion to rotate this vector
   * @return a vector holding the result
   */
  fun rotate(quat: Quaternion): Vector4 {
    return rotate(quat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#rotate(Quaternion, Vector4)
     */
  fun rotate(quat: Quaternion, dest: Vector4): Vector4 {
    return quat.transform(this, dest)
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
  fun rotateAbout(angle: Float, x: Float, y: Float, z: Float): Vector4 {
    return rotateAxis(angle, x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#rotateAxis(float, float, float, float, Vector4)
     */
  fun rotateAxis(angle: Float, aX: Float, aY: Float, aZ: Float, dest: Vector4): Vector4 {
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
  fun rotateX(angle: Float): Vector4 {
    return rotateX(angle, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#rotateX(float, Vector4)
     */
  fun rotateX(angle: Float, dest: Vector4): Vector4 {
    val sin = Math.sin(angle.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val y = this.y * cos - this.z * sin
    val z = this.y * sin + this.z * cos
    dest.x = this.x
    dest.y = y
    dest.z = z
    dest.w = this.w
    return dest
  }

  /**
   * Rotate this vector the specified radians around the Y axis.
   *
   * @param angle
   * the angle in radians
   * @return a vector holding the result
   */
  fun rotateY(angle: Float): Vector4 {
    return rotateY(angle, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#rotateY(float, Vector4)
     */
  fun rotateY(angle: Float, dest: Vector4): Vector4 {
    val sin = Math.sin(angle.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val x = this.x * cos + this.z * sin
    val z = -this.x * sin + this.z * cos
    dest.x = x
    dest.y = this.y
    dest.z = z
    dest.w = this.w
    return dest
  }

  /**
   * Rotate this vector the specified radians around the Z axis.
   *
   * @param angle
   * the angle in radians
   * @return a vector holding the result
   */
  fun rotateZ(angle: Float): Vector4 {
    return rotateZ(angle, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#rotateZ(float, Vector4)
     */
  fun rotateZ(angle: Float, dest: Vector4): Vector4 {
    val sin = Math.sin(angle.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val x = this.x * cos - this.y * sin
    val y = this.x * sin + this.y * cos
    dest.x = x
    dest.y = y
    dest.z = this.z
    dest.w = this.w
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4#lengthSquared()
     */
  fun lengthSquared(): Float {
    return x * x + y * y + z * z + w * w
  }

  /* (non-Javadoc)
     * @see Vector4#length()
     */
  fun length(): Float {
    return Math.sqrt(lengthSquared().toDouble()).toFloat()
  }

  /**
   * Normalizes this vector.
   *
   * @return a vector holding the result
   */
  fun normalize(): Vector4 {
    return normalize(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#normalize(Vector4)
     */
  fun normalize(dest: Vector4): Vector4 {
    val invLength = 1.0f / length()
    dest.x = x * invLength
    dest.y = y * invLength
    dest.z = z * invLength
    dest.w = w * invLength
    return dest
  }

  /**
   * Scale this vector to have the given length.
   *
   * @param length
   * the desired length
   * @return a vector holding the result
   */
  fun normalize(length: Float): Vector4 {
    return normalize(length, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#normalize(float, Vector4)
     */
  fun normalize(length: Float, dest: Vector4): Vector4 {
    val invLength = 1.0f / length() * length
    dest.x = x * invLength
    dest.y = y * invLength
    dest.z = z * invLength
    dest.w = w * invLength
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4#normalize3(Vector4)
     */
  @JvmOverloads
  fun normalize3(dest: Vector4 = thisOrNew()): Vector4 {
    val invLength = 1.0f / Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    dest.x = x * invLength
    dest.y = y * invLength
    dest.z = z * invLength
    dest.w = w * invLength
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4#distance(Vector4)
     */
  fun distance(v: Vector4): Float {
    return distance(v.x, v.y, v.z, v.w)
  }

  /* (non-Javadoc)
     * @see Vector4#distance(float, float, float, float)
     */
  fun distance(x: Float, y: Float, z: Float, w: Float): Float {
    val dx = this.x - x
    val dy = this.y - y
    val dz = this.z - z
    val dw = this.w - w
    return Math.sqrt((dx * dx + dy * dy + dz * dz + dw * dw).toDouble()).toFloat()
  }

  /* (non-Javadoc)
     * @see Vector4#dot(Vector4)
     */
  fun dot(v: Vector4): Float {
    return x * v.x + y * v.y + z * v.z + w * v.w
  }

  /* (non-Javadoc)
     * @see Vector4#dot(float, float, float, float)
     */
  fun dot(x: Float, y: Float, z: Float, w: Float): Float {
    return this.x * x + this.y * y + this.z * z + this.w * w
  }

  /* (non-Javadoc)
     * @see Vector4#angleCos(Vector4)
     */
  fun angleCos(v: Vector4): Float {
    val length1Sqared = (x * x + y * y + z * z + w * w).toDouble()
    val length2Sqared = (v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w).toDouble()
    val dot = (x * v.x + y * v.y + z * v.z + w * v.w).toDouble()
    return (dot / Math.sqrt(length1Sqared * length2Sqared)).toFloat()
  }

  /* (non-Javadoc)
     * @see Vector4#angle(Vector4)
     */
  fun angle(v: Vector4): Float {
    var cos = angleCos(v)
    // This is because sometimes cos goes above 1 or below -1 because of lost precision
    cos = if (cos < 1) cos else 1f
    cos = if (cos > -1) cos else -1f
    return Math.acos(cos.toDouble()).toFloat()
  }

  /**
   * Negate this vector.
   *
   * @return a vector holding the result
   */
  fun negate(): Vector4 {
    return negate(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#negate(Vector4)
     */
  fun negate(dest: Vector4): Vector4 {
    dest.x = -x
    dest.y = -y
    dest.z = -z
    dest.w = -w
    return dest
  }

  /**
   * Return a string representation of this vector by formatting the vector components with the given [NumberFormat].
   *
   * @param formatter
   * the [NumberFormat] used to format the vector components with
   * @return the string representation
   */
  fun toString(formatter: NumberFormat): String {
    return "(" + formatter.format(x.toDouble()) + " " + formatter.format(y.toDouble()) + " " + formatter.format(z.toDouble()) + " " + formatter.format(w.toDouble()) + ")"
  }

  @Throws(IOException::class)
  fun writeExternal(out: ObjectOutput) {
    out.writeFloat(x)
    out.writeFloat(y)
    out.writeFloat(z)
    out.writeFloat(w)
  }

  @Throws(IOException::class, ClassNotFoundException::class)
  fun readExternal(`in`: ObjectInput) {
    x = `in`.readFloat()
    y = `in`.readFloat()
    z = `in`.readFloat()
    w = `in`.readFloat()
  }

  /**
   * Set the components of this vector to be the component-wise minimum of this and the other vector.
   *
   * @param v
   * the other vector
   * @return a vector holding the result
   */
  fun min(v: Vector4): Vector4 {
    return min(v, thisOrNew())
  }

  fun min(v: Vector4, dest: Vector4): Vector4 {
    dest.x = if (x < v.x) x else v.x
    dest.y = if (y < v.y) y else v.y
    dest.z = if (z < v.z) z else v.z
    dest.w = if (w < v.w) w else v.w
    return this
  }

  /**
   * Set the components of this vector to be the component-wise maximum of this and the other vector.
   *
   * @param v
   * the other vector
   * @return a vector holding the result
   */
  fun max(v: Vector4): Vector4 {
    return max(v, thisOrNew())
  }

  fun max(v: Vector4, dest: Vector4): Vector4 {
    dest.x = if (x > v.x) x else v.x
    dest.y = if (y > v.y) y else v.y
    dest.z = if (z > v.z) z else v.z
    dest.w = if (w > v.w) w else v.w
    return this
  }

  /* (non-Javadoc)
     * @see Vector4#smoothStep(Vector4, float, Vector4)
     */
  fun smoothStep(v: Vector4, t: Float, dest: Vector4): Vector4 {
    val t2 = t * t
    val t3 = t2 * t
    dest.x = (x + x - v.x - v.x) * t3 + (3.0f * v.x - 3.0f * x) * t2 + x * t + x
    dest.y = (y + y - v.y - v.y) * t3 + (3.0f * v.y - 3.0f * y) * t2 + y * t + y
    dest.z = (z + z - v.z - v.z) * t3 + (3.0f * v.z - 3.0f * z) * t2 + z * t + z
    dest.w = (w + w - v.w - v.w) * t3 + (3.0f * v.w - 3.0f * w) * t2 + w * t + w
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4#hermite(Vector4, Vector4, Vector4, float, Vector4)
     */
  fun hermite(t0: Vector4, v1: Vector4, t1: Vector4, t: Float, dest: Vector4): Vector4 {
    val t2 = t * t
    val t3 = t2 * t
    dest.x = (x + x - v1.x - v1.x + t1.x + t0.x) * t3 + (3.0f * v1.x - 3.0f * x - t0.x - t0.x - t1.x) * t2 + x * t + x
    dest.y = (y + y - v1.y - v1.y + t1.y + t0.y) * t3 + (3.0f * v1.y - 3.0f * y - t0.y - t0.y - t1.y) * t2 + y * t + y
    dest.z = (z + z - v1.z - v1.z + t1.z + t0.z) * t3 + (3.0f * v1.z - 3.0f * z - t0.z - t0.z - t1.z) * t2 + z * t + z
    dest.w = (w + w - v1.w - v1.w + t1.w + t0.w) * t3 + (3.0f * v1.w - 3.0f * w - t0.w - t0.w - t1.w) * t2 + w * t + w
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
  fun lerp(other: Vector4, t: Float): Vector4 {
    return lerp(other, t, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4#lerp(Vector4, float, Vector4)
     */
  fun lerp(other: Vector4, t: Float, dest: Vector4): Vector4 {
    dest.x = x + (other.x - x) * t
    dest.y = y + (other.y - y) * t
    dest.z = z + (other.z - z) * t
    dest.w = w + (other.w - w) * t
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4#get(int)
     */
  @Throws(IllegalArgumentException::class)
  fun get(component: Int): Float {
    when (component) {
      0 -> return x
      1 -> return y
      2 -> return z
      3 -> return w
      else -> throw IllegalArgumentException()
    }
  }

  companion object {

    private val serialVersionUID = 1L
  }

}

/**
 * Normalize this vector by computing only the norm of <tt>(x, y, z)</tt>.
 *
 * @return a vector holding the result
 */

fun newVector4(xyz: Vector3, w: Float) =
    Vector4(xyz.x, xyz.y, xyz.z, w)

fun toList(value: Vector4): List<Float> =
    listOf(
        value.x,
        value.y,
        value.z,
        value.w
    )

fun grayscaleVector4(value: Float, alpha: Float) =
    Vector4(value, value, value, alpha)
