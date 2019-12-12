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

import mythic.spatial.Vector2
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
 * Represents a 2D vector with single-precision.
 *
 * @author RGreenlees
 * @author Kai Burjack
 */
class Vector2f : Externalizable, Vector2fc {

  /**
   * The x component of the vector.
   */
 override var x: Float = 0.toFloat()
  /**
   * The y component of the vector.
   */
  override  var y: Float = 0.toFloat()

  /**
   * Create a new [Vector2f] and initialize its components to zero.
   */
  constructor() {}

  /**
   * Create a new [Vector2f] and initialize both of its components with the given value.
   *
   * @param d
   * the value of both components
   */
  constructor(d: Float) : this(d, d) {}

  /**
   * Create a new [Vector2f] and initialize its components to the given values.
   *
   * @param x
   * the x component
   * @param y
   * the y component
   */
  constructor(x: Float, y: Float) {
    this.x = x
    this.y = y
  }

  /**
   * Create a new [Vector2f] and initialize its components to the one of the given vector.
   *
   * @param v
   * the [Vector2fc] to copy the values from
   */
  constructor(v: Vector2fc) {
    x = v.x
    y = v.y
  }

  /**
   * Create a new [Vector2f] and read this vector from the supplied [ByteBuffer]
   * at the current buffer [position][ByteBuffer.position].
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   *
   * In order to specify the offset into the ByteBuffer at which
   * the vector is read, use [.Vector2f], taking
   * the absolute position as parameter.
   *
   * @param buffer
   * values will be read in <tt>x, y</tt> order
   * @see .Vector2f
   */
  constructor(buffer: ByteBuffer) : this(buffer.position(), buffer) {}

  /**
   * Create a new [Vector2f] and read this vector from the supplied [ByteBuffer]
   * starting at the specified absolute buffer position/index.
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   * @param index
   * the absolute position into the ByteBuffer
   * @param buffer values will be read in <tt>x, y</tt> order
   */
  constructor(index: Int, buffer: ByteBuffer) {
    MemUtil.INSTANCE.get(this, index, buffer)
  }

  /**
   * Create a new [Vector2f] and read this vector from the supplied [FloatBuffer]
   * at the current buffer [position][FloatBuffer.position].
   *
   *
   * This method will not increment the position of the given FloatBuffer.
   *
   *
   * In order to specify the offset into the FloatBuffer at which
   * the vector is read, use [.Vector2f], taking
   * the absolute position as parameter.
   *
   * @param buffer
   * values will be read in <tt>x, y</tt> order
   * @see .Vector2f
   */
  constructor(buffer: FloatBuffer) : this(buffer.position(), buffer) {}

  /**
   * Create a new [Vector2f] and read this vector from the supplied [FloatBuffer]
   * starting at the specified absolute buffer position/index.
   *
   *
   * This method will not increment the position of the given FloatBuffer.
   *
   * @param index
   * the absolute position into the FloatBuffer
   * @param buffer
   * values will be read in <tt>x, y</tt> order
   */
  constructor(index: Int, buffer: FloatBuffer) {
    MemUtil.INSTANCE.get(this, index, buffer)
  }

  private fun thisOrNew(): Vector2f {
    return this
  }

  /**
   * Set the x and y components to the supplied value.
   *
   * @param d
   * the value of both components
   * @return this
   */
  fun set(d: Float): Vector2f {
    return set(d, d)
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
  operator fun set(x: Float, y: Float): Vector2f {
    this.x = x
    this.y = y
    return this
  }

  /**
   * Set this [Vector2f] to the values of v.
   *
   * @param v
   * the vector to copy from
   * @return this
   */
  fun set(v: Vector2fc): Vector2f {
    return set(v.x, v.y)
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
   * values will be read in <tt>x, y</tt> order
   * @return this
   * @see .set
   */
  fun set(buffer: ByteBuffer): Vector2f {
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
   * values will be read in <tt>x, y</tt> order
   * @return this
   */
  operator fun set(index: Int, buffer: ByteBuffer): Vector2f {
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
   * values will be read in <tt>x, y</tt> order
   * @return this
   * @see .set
   */
  fun set(buffer: FloatBuffer): Vector2f {
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
   * values will be read in <tt>x, y</tt> order
   * @return this
   */
  operator fun set(index: Int, buffer: FloatBuffer): Vector2f {
    MemUtil.INSTANCE.get(this, index, buffer)
    return this
  }

  /**
   * Set the values of this vector by reading 2 float values from off-heap memory,
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
  fun setFromAddress(address: Long): Vector2f {
    if (Options.NO_UNSAFE)
      throw UnsupportedOperationException("Not supported when using joml.nounsafe")
    val unsafe = MemUtil.INSTANCE as MemUtil.MemUtilUnsafe
    unsafe.get(this, address)
    return this
  }

  /* (non-Javadoc)
     * @see Vector2fc#get(int)
     */
  @Throws(IllegalArgumentException::class)
  override fun get(component: Int): Float {
    when (component) {
      0 -> return x
      1 -> return y
      else -> throw IllegalArgumentException()
    }
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
  fun setComponent(component: Int, value: Float): Vector2f {
    when (component) {
      0 -> x = value
      1 -> y = value
      else -> throw IllegalArgumentException()
    }
    return this
  }

  /* (non-Javadoc)
     * @see Vector2fc#get(java.nio.ByteBuffer)
     */
  override fun get(buffer: ByteBuffer): ByteBuffer {
    return get(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Vector2fc#get(int, java.nio.ByteBuffer)
     */
  override fun get(index: Int, buffer: ByteBuffer): ByteBuffer {
    MemUtil.INSTANCE.put(this, index, buffer)
    return buffer
  }

  /* (non-Javadoc)
     * @see Vector2fc#get(java.nio.FloatBuffer)
     */
  override fun get(buffer: FloatBuffer): FloatBuffer {
    return get(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Vector2fc#get(int, java.nio.FloatBuffer)
     */
  override fun get(index: Int, buffer: FloatBuffer): FloatBuffer {
    MemUtil.INSTANCE.put(this, index, buffer)
    return buffer
  }

  override fun getToAddress(address: Long): Vector2fc {
    if (Options.NO_UNSAFE)
      throw UnsupportedOperationException("Not supported when using joml.nounsafe")
    val unsafe = MemUtil.INSTANCE as MemUtil.MemUtilUnsafe
    unsafe.put(this, address)
    return this
  }

  /**
   * Set this vector to be one of its perpendicular vectors.
   *
   * @return this
   */
  fun perpendicular(): Vector2f {
    return set(y, x * -1)
  }

  /**
   * Subtract `v` from this vector.
   *
   * @param v
   * the vector to subtract
   * @return a vector holding the result
   */
  fun sub(v: Vector2fc): Vector2f {
    return sub(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#sub(Vector2fc, Vector2f)
     */
  override fun sub(v: Vector2fc, dest: Vector2f): Vector2f {
    dest.x = x - v.x
    dest.y = y - v.y
    return dest
  }

  /**
   * Subtract <tt>(x, y)</tt> from this vector.
   *
   * @param x
   * the x component to subtract
   * @param y
   * the y component to subtract
   * @return a vector holding the result
   */
  fun sub(x: Float, y: Float): Vector2f {
    return sub(x, y, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#sub(float, float, Vector2f)
     */
  override fun sub(x: Float, y: Float, dest: Vector2f): Vector2f {
    dest.x = this.x - x
    dest.y = this.y - y
    return dest
  }

  /* (non-Javadoc)
     * @see Vector2fc#dot(Vector2fc)
     */
  override fun dot(v: Vector2fc): Float {
    return x * v.x + y * v.y
  }

  /* (non-Javadoc)
     * @see Vector2fc#angle(Vector2fc)
     */
  override fun angle(v: Vector2fc): Float {
    val dot = x * v.x + y * v.y
    val det = x * v.y - y * v.x
    return Math.atan2(det.toDouble(), dot.toDouble()).toFloat()
  }

  /* (non-Javadoc)
     * @see Vector2fc#length()
     */
  override fun length(): Float {
    return Math.sqrt((x * x + y * y).toDouble()).toFloat()
  }

  /* (non-Javadoc)
     * @see Vector2fc#lengthSquared()
     */
  override fun lengthSquared(): Float {
    return x * x + y * y
  }

  /* (non-Javadoc)
     * @see Vector2fc#distance(Vector2fc)
     */
  override fun distance(v: Vector2fc): Float {
    return distance(v.x, v.y)
  }

  /* (non-Javadoc)
     * @see Vector2fc#distanceSquared(Vector2fc)
     */
  //    public float distanceSquared(Vector2fc v) {
  //        return distanceSquared(v.x, v.y);
  //    }

  /* (non-Javadoc)
     * @see Vector2fc#distance(float, float)
     */
  override fun distance(x: Float, y: Float): Float {
    val dx = this.x - x
    val dy = this.y - y
    return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
  }

  /* (non-Javadoc)
     * @see Vector2fc#distanceSquared(float, float)
     */
  fun distanceSquared(x: Float, y: Float): Float {
    val dx = this.x - x
    val dy = this.y - y
    return dx * dx + dy * dy
  }

  /**
   * Normalize this vector.
   *
   * @return a vector holding the result
   */
  fun normalize(): Vector2f {
    return normalize(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#normalize(Vector2f)
     */
  override fun normalize(dest: Vector2f): Vector2f {
    val invLength = (1.0 / Math.sqrt((x * x + y * y).toDouble())).toFloat()
    dest.x = x * invLength
    dest.y = y * invLength
    return dest
  }

  /**
   * Scale this vector to have the given length.
   *
   * @param length
   * the desired length
   * @return a vector holding the result
   */
  fun normalize(length: Float): Vector2f {
    return normalize(length, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#normalize(float, Vector2f)
     */
  override fun normalize(length: Float, dest: Vector2f): Vector2f {
    val invLength = (1.0 / Math.sqrt((x * x + y * y).toDouble())).toFloat() * length
    dest.x = x * invLength
    dest.y = y * invLength
    return dest
  }

  /**
   * Add `v` to this vector.
   *
   * @param v
   * the vector to add
   * @return a vector holding the result
   */
  fun add(v: Vector2fc): Vector2f {
    return add(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#add(Vector2fc, Vector2f)
     */
  override fun add(v: Vector2fc, dest: Vector2f): Vector2f {
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
  fun add(x: Float, y: Float): Vector2f {
    return add(x, y, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#add(float, float, Vector2f)
     */
  override fun add(x: Float, y: Float, dest: Vector2f): Vector2f {
    dest.x = this.x + x
    dest.y = this.y + y
    return dest
  }

  /**
   * Set all components to zero.
   *
   * @return a vector holding the result
   */
  fun zero(): Vector2f {
    return thisOrNew().set(0f, 0f)
  }

  @Throws(IOException::class)
  override fun writeExternal(out: ObjectOutput) {
    out.writeFloat(x)
    out.writeFloat(y)
  }

  @Throws(IOException::class, ClassNotFoundException::class)
  override fun readExternal(`in`: ObjectInput) {
    x = `in`.readFloat()
    y = `in`.readFloat()
  }

  /**
   * Negate this vector.
   *
   * @return a vector holding the result
   */
  fun negate(): Vector2f {
    return negate(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#negate(Vector2f)
     */
  override fun negate(dest: Vector2f): Vector2f {
    dest.x = -x
    dest.y = -y
    return dest
  }

  /**
   * Multiply the components of this vector by the given scalar.
   *
   * @param scalar
   * the value to multiply this vector's components by
   * @return a vector holding the result
   */
  fun mul(scalar: Float): Vector2f {
    return mul(scalar, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#mul(float, Vector2f)
     */
  override fun mul(scalar: Float, dest: Vector2f): Vector2f {
    dest.x = x * scalar
    dest.y = y * scalar
    return dest
  }

  /**
   * Multiply the components of this Vector2f by the given scalar values and store the result in `this`.
   *
   * @param x
   * the x component to multiply this vector by
   * @param y
   * the y component to multiply this vector by
   * @return a vector holding the result
   */
  fun mul(x: Float, y: Float): Vector2f {
    return mul(x, y, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#mul(float, float, Vector2f)
     */
  override fun mul(x: Float, y: Float, dest: Vector2f): Vector2f {
    dest.x = this.x * x
    dest.y = this.y * y
    return dest
  }

  /**
   * Multiply this Vector2f component-wise by another Vector2f.
   *
   * @param v
   * the vector to multiply by
   * @return a vector holding the result
   */
  fun mul(v: Vector2fc): Vector2f {
    return mul(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#mul(Vector2fc, Vector2f)
     */
  override fun mul(v: Vector2fc, dest: Vector2f): Vector2f {
    dest.x = x * v.x
    dest.y = y * v.y
    return dest
  }

  /**
   * Multiply the given 3x2 matrix `mat` with `this`.
   *
   *
   * This method assumes the <tt>z</tt> component of `this` to be <tt>1.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @return a vector holding the result
   */
  fun mulPosition(mat: Matrix3x2fc): Vector2f {
    return mulPosition(mat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#mulPosition(org.joml.Matrix3x2fc, Vector2f)
     */
  override fun mulPosition(mat: Matrix3x2fc, dest: Vector2f): Vector2f {
    dest[mat.m00() * x + mat.m10() * y + mat.m20()] = mat.m01() * x + mat.m11() * y + mat.m21()
    return dest
  }

  /**
   * Multiply the given 3x2 matrix `mat` with `this`.
   *
   *
   * This method assumes the <tt>z</tt> component of `this` to be <tt>0.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @return a vector holding the result
   */
  fun mulDirection(mat: Matrix3x2fc): Vector2f {
    return mulDirection(mat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#mulDirection(org.joml.Matrix3x2fc, Vector2f)
     */
  override fun mulDirection(mat: Matrix3x2fc, dest: Vector2f): Vector2f {
    dest[mat.m00() * x + mat.m10() * y] = mat.m01() * x + mat.m11() * y
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
  fun lerp(other: Vector2fc, t: Float): Vector2f {
    return lerp(other, t, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#lerp(Vector2fc, float, Vector2f)
     */
  override fun lerp(other: Vector2fc, t: Float, dest: Vector2f): Vector2f {
    dest.x = x + (other.x - x) * t
    dest.y = y + (other.y - y) * t
    return dest
  }

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
    val other = obj as Vector2f?
    if (java.lang.Float.floatToIntBits(x) != java.lang.Float.floatToIntBits(other!!.x))
      return false
    return if (java.lang.Float.floatToIntBits(y) != java.lang.Float.floatToIntBits(other.y)) false else true
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
    return "(" + formatter.format(x.toDouble()) + " " + formatter.format(y.toDouble()) + ")"
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
  fun fma(a: Vector2fc, b: Vector2fc): Vector2f {
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
  fun fma(a: Float, b: Vector2fc): Vector2f {
    return fma(a, b, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2fc#fma(Vector2fc, Vector2fc, Vector2f)
     */
  override fun fma(a: Vector2fc, b: Vector2fc, dest: Vector2f): Vector2f {
    dest.x = x + a.x * b.x
    dest.y = y + a.y * b.y
    return dest
  }

  /* (non-Javadoc)
     * @see Vector2fc#fma(float, Vector2fc, Vector2f)
     */
  override fun fma(a: Float, b: Vector2fc, dest: Vector2f): Vector2f {
    dest.x = x + a * b.x
    dest.y = y + a * b.y
    return dest
  }

  /**
   * Set the components of this vector to be the component-wise minimum of this and the other vector.
   *
   * @param v
   * the other vector
   * @return a vector holding the result
   */
  fun min(v: Vector2fc): Vector2f {
    return min(v, thisOrNew())
  }

  override fun min(v: Vector2fc, dest: Vector2f): Vector2f {
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
  fun max(v: Vector2fc): Vector2f {
    return max(v, thisOrNew())
  }

  override fun max(v: Vector2fc, dest: Vector2f): Vector2f {
    dest.x = if (x > v.x) x else v.x
    dest.y = if (y > v.y) y else v.y
    return this
  }

  override operator fun minus(v: Vector2fMinimal): Vector2 =
      Vector2(x - v.x, y - v.y)

  companion object {

    private val serialVersionUID = 1L
  }

  override fun xy(): Vector2 = Vector2(x, y)
}
