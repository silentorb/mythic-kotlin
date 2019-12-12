/*
 * (C) Copyright 2015-2018 Richard Greenlees

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package org.joml

import mythic.spatial.Vector3
import org.joml.internal.MemUtil
import org.joml.internal.Options
import org.joml.internal.Runtime

import java.io.Externalizable
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * Contains the definition of a Vector comprising 4 floats and associated
 * transformations.
 *
 * @author Richard Greenlees
 * @author Kai Burjack
 */
class Vector4f : Externalizable, Vector4fc {

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
   * The w component of the vector.
   */
  override var w: Float = 0.toFloat()

  /**
   * Create a new [Vector4f] of `(0, 0, 0, 1)`.
   */
  constructor() {
    this.w = 1.0f
  }

  /**
   * Create a new [Vector4f] with the same values as `v`.
   *
   * @param v
   * the [Vector4fc] to copy the values from
   */
  constructor(v: Vector4fc) {
    if (v is Vector4f) {
      MemUtil.INSTANCE.copy(v, this)
    } else {
      this.x = v.x
      this.y = v.y
      this.z = v.z
      this.w = v.w
    }
  }

  /**
   * Create a new [Vector4f] with the first three components from the
   * given `v` and the given `w`.
   *
   * @param v
   * the [Vector3fc]
   * @param w
   * the w component
   */
  constructor(v: Vector3fc, w: Float) : this(v.x, v.y, v.z, w) {}
  constructor(v: Vector3, w: Float) : this(v.x, v.y, v.z, w) {}

  /**
   * Create a new [Vector4f] with the first two components from the
   * given `v` and the given `z`, and `w`.
   *
   * @param v
   * the [Vector2fc]
   * @param z
   * the z component
   * @param w
   * the w component
   */
  constructor(v: Vector2fc, z: Float, w: Float) : this(v.x, v.y, z, w) {}

  /**
   * Create a new [Vector4f] with the first two components from the
   * given `v` and the given `z`, and `w`.
   *
   * @param v
   * the [Vector2ic]
   * @param z
   * the z component
   * @param w
   * the w component
   */
  constructor(v: Vector2ic, z: Float, w: Float) : this(v.x.toFloat(), v.y.toFloat(), z, w) {}

  /**
   * Create a new [Vector4f] and initialize all four components with the given value.
   *
   * @param d
   * the value of all four components
   */
  constructor(d: Float) {
    MemUtil.INSTANCE.broadcast(d, this)
  }

  /**
   * Create a new [Vector4f] with the given component values.
   *
   * @param x
   * the x component
   * @param y
   * the y component
   * @param z
   * the z component
   * @param w
   * the w component
   */
  constructor(x: Float, y: Float, z: Float, w: Float) {
    this.x = x
    this.y = y
    this.z = z
    this.w = w
  }

  /**
   * Create a new [Vector4f] and read this vector from the supplied [ByteBuffer]
   * at the current buffer [position][ByteBuffer.position].
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   *
   * In order to specify the offset into the ByteBuffer at which
   * the vector is read, use [.Vector4f], taking
   * the absolute position as parameter.
   *
   * @param buffer
   * values will be read in <tt>x, y, z, w</tt> order
   * @see .Vector4f
   */
  constructor(buffer: ByteBuffer) : this(buffer.position(), buffer) {}

  /**
   * Create a new [Vector4f] and read this vector from the supplied [ByteBuffer]
   * starting at the specified absolute buffer position/index.
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   * @param index
   * the absolute position into the ByteBuffer
   * @param buffer
   * values will be read in <tt>x, y, z, w</tt> order
   */
  constructor(index: Int, buffer: ByteBuffer) {
    MemUtil.INSTANCE.get(this, index, buffer)
  }

  /**
   * Create a new [Vector4f] and read this vector from the supplied [FloatBuffer]
   * at the current buffer [position][FloatBuffer.position].
   *
   *
   * This method will not increment the position of the given FloatBuffer.
   *
   *
   * In order to specify the offset into the FloatBuffer at which
   * the vector is read, use [.Vector4f], taking
   * the absolute position as parameter.
   *
   * @param buffer
   * values will be read in <tt>x, y, z, w</tt> order
   * @see .Vector4f
   */
  constructor(buffer: FloatBuffer) : this(buffer.position(), buffer) {}

  /**
   * Create a new [Vector4f] and read this vector from the supplied [FloatBuffer]
   * starting at the specified absolute buffer position/index.
   *
   *
   * This method will not increment the position of the given FloatBuffer.
   *
   * @param index
   * the absolute position into the FloatBuffer
   * @param buffer
   * values will be read in <tt>x, y, z, w</tt> order
   */
  constructor(index: Int, buffer: FloatBuffer) {
    MemUtil.INSTANCE.get(this, index, buffer)
  }

  private fun thisOrNew(): Vector4f {
    return this
  }

  /**
   * Set this [Vector4f] to the values of the given `v`.
   *
   * @param v
   * the vector whose values will be copied into this
   * @return this
   */
  fun set(v: Vector4fc): Vector4f {
    if (v is Vector4f) {
      MemUtil.INSTANCE.copy(v, this)
    } else {
      this.x = v.x
      this.y = v.y
      this.z = v.z
      this.w = v.w
    }
    return this
  }

  /**
   * Set the first three components of this to the components of
   * `v` and the last component to `w`.
   *
   * @param v
   * the [Vector3fc] to copy
   * @param w
   * the w component
   * @return this
   */
  operator fun set(v: Vector3fc, w: Float): Vector4f {
    return set(v.x, v.y, v.z, w)
  }

  /**
   * Sets the first two components of this to the components of given `v`
   * and last two components to the given `z`, and `w`.
   *
   * @param v
   * the [Vector2fc]
   * @param z
   * the z component
   * @param w
   * the w component
   * @return this
   */
  operator fun set(v: Vector2fc, z: Float, w: Float): Vector4f {
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
  operator fun set(v: Vector2ic, z: Float, w: Float): Vector4f {
    return set(v.x.toFloat(), v.y.toFloat(), z, w)
  }

  /**
   * Set the x, y, z, and w components to the supplied value.
   *
   * @param d
   * the value of all four components
   * @return this
   */
  fun set(d: Float): Vector4f {
    MemUtil.INSTANCE.broadcast(d, this)
    return this
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
  operator fun set(x: Float, y: Float, z: Float, w: Float): Vector4f {
    this.x = x
    this.y = y
    this.z = z
    this.w = w
    return this
  }

  /**
   * Read this vector from the supplied [ByteBuffer] at the current
   * buffer [position][ByteBuffer.position].
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   *
   * In order to specify the offset into the ByteBuffer at which
   * the vector is read, use [.set], taking
   * the absolute position as parameter.
   *
   * @param buffer
   * values will be read in <tt>x, y, z, w</tt> order
   * @return this
   * @see .set
   */
  fun set(buffer: ByteBuffer): Vector4f {
    return set(buffer.position(), buffer)
  }

  /**
   * Read this vector from the supplied [ByteBuffer] starting at the specified
   * absolute buffer position/index.
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   * @param index
   * the absolute position into the ByteBuffer
   * @param buffer
   * values will be read in <tt>x, y, z, w</tt> order
   * @return this
   */
  operator fun set(index: Int, buffer: ByteBuffer): Vector4f {
    MemUtil.INSTANCE.get(this, index, buffer)
    return this
  }

  /**
   * Read this vector from the supplied [FloatBuffer] at the current
   * buffer [position][FloatBuffer.position].
   *
   *
   * This method will not increment the position of the given FloatBuffer.
   *
   *
   * In order to specify the offset into the FloatBuffer at which
   * the vector is read, use [.set], taking
   * the absolute position as parameter.
   *
   * @param buffer
   * values will be read in <tt>x, y, z, w</tt> order
   * @return this
   * @see .set
   */
  fun set(buffer: FloatBuffer): Vector4f {
    return set(buffer.position(), buffer)
  }

  /**
   * Read this vector from the supplied [FloatBuffer] starting at the specified
   * absolute buffer position/index.
   *
   *
   * This method will not increment the position of the given FloatBuffer.
   *
   * @param index
   * the absolute position into the FloatBuffer
   * @param buffer
   * values will be read in <tt>x, y, z, w</tt> order
   * @return this
   */
  operator fun set(index: Int, buffer: FloatBuffer): Vector4f {
    MemUtil.INSTANCE.get(this, index, buffer)
    return this
  }

  /**
   * Set the values of this vector by reading 4 float values from off-heap memory,
   * starting at the given address.
   *
   *
   * This method will throw an [UnsupportedOperationException] when JOML is used with `-Djoml.nounsafe`.
   *
   *
   * *This method is unsafe as it can result in a crash of the JVM process when the specified address range does not belong to this process.*
   *
   * @param address
   * the off-heap memory address to read the vector values from
   * @return this
   */
  fun setFromAddress(address: Long): Vector4f {
    if (Options.NO_UNSAFE)
      throw UnsupportedOperationException("Not supported when using joml.nounsafe")
    val unsafe = MemUtil.INSTANCE as MemUtil.MemUtilUnsafe
    unsafe.get(this, address)
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
  fun setComponent(component: Int, value: Float): Vector4f {
    when (component) {
      0 -> x = value
      1 -> y = value
      2 -> z = value
      3 -> w = value
      else -> throw IllegalArgumentException()
    }
    return this
  }

  /* (non-Javadoc)
     * @see Vector4fc#get(java.nio.FloatBuffer)
     */
  override fun get(buffer: FloatBuffer): FloatBuffer {
    return get(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Vector4fc#get(int, java.nio.FloatBuffer)
     */
  override fun get(index: Int, buffer: FloatBuffer): FloatBuffer {
    MemUtil.INSTANCE.put(this, index, buffer)
    return buffer
  }

  /* (non-Javadoc)
     * @see Vector4fc#get(java.nio.ByteBuffer)
     */
  override fun get(buffer: ByteBuffer): ByteBuffer {
    return get(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Vector4fc#get(int, java.nio.ByteBuffer)
     */
  override fun get(index: Int, buffer: ByteBuffer): ByteBuffer {
    MemUtil.INSTANCE.put(this, index, buffer)
    return buffer
  }

  override fun getToAddress(address: Long): Vector4fc {
    if (Options.NO_UNSAFE)
      throw UnsupportedOperationException("Not supported when using joml.nounsafe")
    val unsafe = MemUtil.INSTANCE as MemUtil.MemUtilUnsafe
    unsafe.put(this, address)
    return this
  }

  /**
   * Subtract the supplied vector from this one.
   *
   * @param v
   * the vector to subtract
   * @return a vector holding the result
   */
  fun sub(v: Vector4fc): Vector4f {
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
  fun sub(x: Float, y: Float, z: Float, w: Float): Vector4f {
    return sub(x, y, z, w, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#sub(Vector4fc, Vector4f)
     */
  override fun sub(v: Vector4fc, dest: Vector4f): Vector4f {
    return sub(v.x, v.y, v.z, v.w, dest)
  }

  /* (non-Javadoc)
     * @see Vector4fc#sub(float, float, float, float, Vector4f)
     */
  override fun sub(x: Float, y: Float, z: Float, w: Float, dest: Vector4f): Vector4f {
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
  fun add(v: Vector4fc): Vector4f {
    return add(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#add(Vector4fc, Vector4f)
     */
  override fun add(v: Vector4fc, dest: Vector4f): Vector4f {
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
  fun add(x: Float, y: Float, z: Float, w: Float): Vector4f {
    return add(x, y, z, w, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#add(float, float, float, float, Vector4f)
     */
  override fun add(x: Float, y: Float, z: Float, w: Float, dest: Vector4f): Vector4f {
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
  fun fma(a: Vector4fc, b: Vector4fc): Vector4f {
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
  fun fma(a: Float, b: Vector4fc): Vector4f {
    return fma(a, b, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#fma(Vector4fc, Vector4fc, Vector4f)
     */
  override fun fma(a: Vector4fc, b: Vector4fc, dest: Vector4f): Vector4f {
    dest.x = x + a.x * b.x
    dest.y = y + a.y * b.y
    dest.z = z + a.z * b.z
    dest.w = w + a.w * b.w
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4fc#fma(float, Vector4fc, Vector4f)
     */
  override fun fma(a: Float, b: Vector4fc, dest: Vector4f): Vector4f {
    dest.x = x + a * b.x
    dest.y = y + a * b.y
    dest.z = z + a * b.z
    dest.w = w + a * b.w
    return dest
  }

  /**
   * Multiply this Vector4f component-wise by another Vector4f.
   *
   * @param v
   * the other vector
   * @return a vector holding the result
   */
  fun mul(v: Vector4fc): Vector4f {
    return mul(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#mul(Vector4fc, Vector4f)
     */
  override fun mul(v: Vector4fc, dest: Vector4f): Vector4f {
    dest.x = x * v.x
    dest.y = y * v.y
    dest.z = z * v.z
    dest.w = w * v.w
    return dest
  }

  /**
   * Divide this Vector4f component-wise by another Vector4f.
   *
   * @param v
   * the vector to divide by
   * @return a vector holding the result
   */
  operator fun div(v: Vector4fc): Vector4f {
    return div(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#div(Vector4fc, Vector4f)
     */
  override fun div(v: Vector4fc, dest: Vector4f): Vector4f {
    dest.x = x / v.x
    dest.y = y / v.y
    dest.z = z / v.z
    dest.w = w / v.w
    return dest
  }

  /**
   * Multiply the given matrix mat with this Vector4f and store the result in
   * `this`.
   *
   * @param mat
   * the matrix to multiply the vector with
   * @return a vector holding the result
   */
  fun mul(mat: Matrix4fc): Vector4f {
    return mul(mat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#mul(Matrix4fc, Vector4f)
     */
  override fun mul(mat: Matrix4fc, dest: Vector4f): Vector4f {
    return if (mat.properties() and Matrix4fc.PROPERTY_AFFINE != 0) mulAffine(mat, dest) else mulGeneric(mat, dest)
  }

  /* (non-Javadoc)
     * @see Vector4fc#mulAffine(Matrix4fc, Vector4f)
     */
  override fun mulAffine(mat: Matrix4fc, dest: Vector4f): Vector4f {
    val rx = mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30() * w
    val ry = mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31() * w
    val rz = mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32() * w
    dest.x = rx
    dest.y = ry
    dest.z = rz
    dest.w = w
    return dest
  }

  private fun mulGeneric(mat: Matrix4fc, dest: Vector4f): Vector4f {
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
   * Multiply the given matrix mat with this Vector4f and store the result in
   * `this`.
   *
   * @param mat
   * the matrix to multiply the vector with
   * @return a vector holding the result
   */
  fun mul(mat: Matrix4x3fc): Vector4f {
    return mul(mat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#mul(org.joml.Matrix4x3fc, Vector4f)
     */
  override fun mul(mat: Matrix4x3fc, dest: Vector4f): Vector4f {
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
     * @see Vector4fc#mulProject(Matrix4fc, Vector4f)
     */
  override fun mulProject(mat: Matrix4fc, dest: Vector4f): Vector4f {
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
   * Multiply the given matrix `mat` with this Vector4f, perform perspective division.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @return a vector holding the result
   */
  fun mulProject(mat: Matrix4fc): Vector4f {
    return mulProject(mat, thisOrNew())
  }

  /**
   * Multiply all components of this [Vector4f] by the given scalar
   * value.
   *
   * @param scalar
   * the scalar to multiply by
   * @return a vector holding the result
   */
  fun mul(scalar: Float): Vector4f {
    return mul(scalar, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#mul(float, Vector4f)
     */
  override fun mul(scalar: Float, dest: Vector4f): Vector4f {
    dest.x = x * scalar
    dest.y = y * scalar
    dest.z = z * scalar
    dest.w = w * scalar
    return dest
  }

  /**
   * Multiply the components of this Vector4f by the given scalar values and store the result in `this`.
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
  fun mul(x: Float, y: Float, z: Float, w: Float): Vector4f {
    return mul(x, y, z, w, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#mul(float, float, float, float, Vector4f)
     */
  override fun mul(x: Float, y: Float, z: Float, w: Float, dest: Vector4f): Vector4f {
    dest.x = this.x * x
    dest.y = this.y * y
    dest.z = this.z * z
    dest.w = this.w * w
    return dest
  }

  /**
   * Divide all components of this [Vector4f] by the given scalar
   * value.
   *
   * @param scalar
   * the scalar to divide by
   * @return a vector holding the result
   */
  operator fun div(scalar: Float): Vector4f {
    return div(scalar, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#div(float, Vector4f)
     */
  override fun div(scalar: Float, dest: Vector4f): Vector4f {
    val inv = 1.0f / scalar
    dest.x = x * inv
    dest.y = y * inv
    dest.z = z * inv
    dest.w = w * inv
    return dest
  }

  /**
   * Divide the components of this Vector4f by the given scalar values and store the result in `this`.
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
  fun div(x: Float, y: Float, z: Float, w: Float): Vector4f {
    return div(x, y, z, w, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#div(float, float, float, float, Vector4f)
     */
  override fun div(x: Float, y: Float, z: Float, w: Float, dest: Vector4f): Vector4f {
    dest.x = this.x / x
    dest.y = this.y / y
    dest.z = this.z / z
    dest.w = this.w / w
    return dest
  }

  /**
   * Rotate this vector by the given quaternion `quat` and store the result in `this`.
   *
   * @see Quaternionf.transform
   * @param quat
   * the quaternion to rotate this vector
   * @return a vector holding the result
   */
  fun rotate(quat: Quaternionfc): Vector4f {
    return rotate(quat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#rotate(Quaternionf, Vector4f)
     */
  override fun rotate(quat: Quaternionfc, dest: Vector4f): Vector4f {
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
  fun rotateAbout(angle: Float, x: Float, y: Float, z: Float): Vector4f {
    return rotateAxis(angle, x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#rotateAxis(float, float, float, float, Vector4f)
     */
  override fun rotateAxis(angle: Float, aX: Float, aY: Float, aZ: Float, dest: Vector4f): Vector4f {
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
  fun rotateX(angle: Float): Vector4f {
    return rotateX(angle, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#rotateX(float, Vector4f)
     */
  override fun rotateX(angle: Float, dest: Vector4f): Vector4f {
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
  fun rotateY(angle: Float): Vector4f {
    return rotateY(angle, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#rotateY(float, Vector4f)
     */
  override fun rotateY(angle: Float, dest: Vector4f): Vector4f {
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
  fun rotateZ(angle: Float): Vector4f {
    return rotateZ(angle, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#rotateZ(float, Vector4f)
     */
  override fun rotateZ(angle: Float, dest: Vector4f): Vector4f {
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
     * @see Vector4fc#lengthSquared()
     */
  override fun lengthSquared(): Float {
    return x * x + y * y + z * z + w * w
  }

  /* (non-Javadoc)
     * @see Vector4fc#length()
     */
  override fun length(): Float {
    return Math.sqrt(lengthSquared().toDouble()).toFloat()
  }

  /**
   * Normalizes this vector.
   *
   * @return a vector holding the result
   */
  fun normalize(): Vector4f {
    return normalize(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#normalize(Vector4f)
     */
  override fun normalize(dest: Vector4f): Vector4f {
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
  fun normalize(length: Float): Vector4f {
    return normalize(length, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#normalize(float, Vector4f)
     */
  override fun normalize(length: Float, dest: Vector4f): Vector4f {
    val invLength = 1.0f / length() * length
    dest.x = x * invLength
    dest.y = y * invLength
    dest.z = z * invLength
    dest.w = w * invLength
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4fc#normalize3(Vector4f)
     */
  @JvmOverloads
  fun normalize3(dest: Vector4f = thisOrNew()): Vector4f {
    val invLength = 1.0f / Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    dest.x = x * invLength
    dest.y = y * invLength
    dest.z = z * invLength
    dest.w = w * invLength
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4fc#distance(Vector4fc)
     */
  override fun distance(v: Vector4fc): Float {
    return distance(v.x, v.y, v.z, v.w)
  }

  /* (non-Javadoc)
     * @see Vector4fc#distance(float, float, float, float)
     */
  override fun distance(x: Float, y: Float, z: Float, w: Float): Float {
    val dx = this.x - x
    val dy = this.y - y
    val dz = this.z - z
    val dw = this.w - w
    return Math.sqrt((dx * dx + dy * dy + dz * dz + dw * dw).toDouble()).toFloat()
  }

  /* (non-Javadoc)
     * @see Vector4fc#dot(Vector4fc)
     */
  override fun dot(v: Vector4fc): Float {
    return x * v.x + y * v.y + z * v.z + w * v.w
  }

  /* (non-Javadoc)
     * @see Vector4fc#dot(float, float, float, float)
     */
  override fun dot(x: Float, y: Float, z: Float, w: Float): Float {
    return this.x * x + this.y * y + this.z * z + this.w * w
  }

  /* (non-Javadoc)
     * @see Vector4fc#angleCos(Vector4fc)
     */
  override fun angleCos(v: Vector4fc): Float {
    val length1Sqared = (x * x + y * y + z * z + w * w).toDouble()
    val length2Sqared = (v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w).toDouble()
    val dot = (x * v.x + y * v.y + z * v.z + w * v.w).toDouble()
    return (dot / Math.sqrt(length1Sqared * length2Sqared)).toFloat()
  }

  /* (non-Javadoc)
     * @see Vector4fc#angle(Vector4fc)
     */
  override fun angle(v: Vector4fc): Float {
    var cos = angleCos(v)
    // This is because sometimes cos goes above 1 or below -1 because of lost precision
    cos = if (cos < 1) cos else 1f
    cos = if (cos > -1) cos else -1f
    return Math.acos(cos.toDouble()).toFloat()
  }

  /**
   * Set all components to zero.
   *
   * @return a vector holding the result
   */
  fun zero(): Vector4f {
    val dest = thisOrNew()
    MemUtil.INSTANCE.zero(dest)
    return dest
  }

  /**
   * Negate this vector.
   *
   * @return a vector holding the result
   */
  fun negate(): Vector4f {
    return negate(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#negate(Vector4f)
     */
  override fun negate(dest: Vector4f): Vector4f {
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
   * @param formatter
   * the [NumberFormat] used to format the vector components with
   * @return the string representation
   */
  fun toString(formatter: NumberFormat): String {
    return "(" + formatter.format(x.toDouble()) + " " + formatter.format(y.toDouble()) + " " + formatter.format(z.toDouble()) + " " + formatter.format(w.toDouble()) + ")"
  }

  @Throws(IOException::class)
  override fun writeExternal(out: ObjectOutput) {
    out.writeFloat(x)
    out.writeFloat(y)
    out.writeFloat(z)
    out.writeFloat(w)
  }

  @Throws(IOException::class, ClassNotFoundException::class)
  override fun readExternal(`in`: ObjectInput) {
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
  fun min(v: Vector4fc): Vector4f {
    return min(v, thisOrNew())
  }

  override fun min(v: Vector4fc, dest: Vector4f): Vector4f {
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
  fun max(v: Vector4fc): Vector4f {
    return max(v, thisOrNew())
  }

  override fun max(v: Vector4fc, dest: Vector4f): Vector4f {
    dest.x = if (x > v.x) x else v.x
    dest.y = if (y > v.y) y else v.y
    dest.z = if (z > v.z) z else v.z
    dest.w = if (w > v.w) w else v.w
    return this
  }

  override fun hashCode(): Int {
    val prime = 31
    var result = 1
    result = prime * result + java.lang.Float.floatToIntBits(w)
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
    val other = obj as Vector4f?
    if (java.lang.Float.floatToIntBits(w) != java.lang.Float.floatToIntBits(other!!.w))
      return false
    if (java.lang.Float.floatToIntBits(x) != java.lang.Float.floatToIntBits(other.x))
      return false
    if (java.lang.Float.floatToIntBits(y) != java.lang.Float.floatToIntBits(other.y))
      return false
    return if (java.lang.Float.floatToIntBits(z) != java.lang.Float.floatToIntBits(other.z)) false else true
  }

  /* (non-Javadoc)
     * @see Vector4fc#smoothStep(Vector4fc, float, Vector4f)
     */
  override fun smoothStep(v: Vector4fc, t: Float, dest: Vector4f): Vector4f {
    val t2 = t * t
    val t3 = t2 * t
    dest.x = (x + x - v.x - v.x) * t3 + (3.0f * v.x - 3.0f * x) * t2 + x * t + x
    dest.y = (y + y - v.y - v.y) * t3 + (3.0f * v.y - 3.0f * y) * t2 + y * t + y
    dest.z = (z + z - v.z - v.z) * t3 + (3.0f * v.z - 3.0f * z) * t2 + z * t + z
    dest.w = (w + w - v.w - v.w) * t3 + (3.0f * v.w - 3.0f * w) * t2 + w * t + w
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4fc#hermite(Vector4fc, Vector4fc, Vector4fc, float, Vector4f)
     */
  override fun hermite(t0: Vector4fc, v1: Vector4fc, t1: Vector4fc, t: Float, dest: Vector4f): Vector4f {
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
  fun lerp(other: Vector4fc, t: Float): Vector4f {
    return lerp(other, t, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector4fc#lerp(Vector4fc, float, Vector4f)
     */
  override fun lerp(other: Vector4fc, t: Float, dest: Vector4f): Vector4f {
    dest.x = x + (other.x - x) * t
    dest.y = y + (other.y - y) * t
    dest.z = z + (other.z - z) * t
    dest.w = w + (other.w - w) * t
    return dest
  }

  /* (non-Javadoc)
     * @see Vector4fc#get(int)
     */
  @Throws(IllegalArgumentException::class)
  override fun get(component: Int): Float {
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
