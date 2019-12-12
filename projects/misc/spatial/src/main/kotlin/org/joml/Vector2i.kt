/*
 * (C) Copyright 2015-2018 Richard Greenlees
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package org.joml

import org.joml.internal.MemUtil
import org.joml.internal.Options
import org.joml.internal.Runtime

import java.io.Externalizable
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.text.DecimalFormat
import java.text.NumberFormat

private val _zero = Vector2i()

/**
 * Represents a 2D vector with single-precision.
 *
 * @author RGreenlees
 * @author Kai Burjack
 * @author Hans Uhlig
 */
class Vector2i : Externalizable, Vector2ic {

  /**
   * The x component of the vector.
   */
  override var x: Int = 0
  /**
   * The y component of the vector.
   */
  override var y: Int = 0

  /**
   * Create a new [Vector2i] and initialize its components to zero.
   */
  constructor() {}

  /**
   * Create a new [Vector2i] and initialize both of its components with
   * the given value.
   *
   * @param s
   * the value of both components
   */
  constructor(s: Int) {
    this.x = s
    this.y = s
  }

  /**
   * Create a new [Vector2i] and initialize its components to the given values.
   *
   * @param x
   * the x component
   * @param y
   * the y component
   */
  constructor(x: Int, y: Int) {
    this.x = x
    this.y = y
  }

  /**
   * Create a new [Vector2i] and read this vector from the supplied
   * [ByteBuffer] at the current buffer
   * [position][ByteBuffer.position].
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   *
   * In order to specify the offset into the ByteBuffer at which the vector is
   * read, use [.Vector2i], taking the absolute
   * position as parameter.
   *
   * @see .Vector2i
   * @param buffer
   * values will be read in <tt>x, y</tt> order
   */
  constructor(buffer: ByteBuffer) : this(buffer.position(), buffer) {}

  /**
   * Create a new [Vector2i] and read this vector from the supplied
   * [ByteBuffer] starting at the specified absolute buffer
   * position/index.
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   * @param index
   * the absolute position into the ByteBuffer
   * @param buffer
   * values will be read in <tt>x, y</tt> order
   */
  constructor(index: Int, buffer: ByteBuffer) {
    MemUtil.INSTANCE.get(this, index, buffer)
  }

  /**
   * Create a new [Vector2i] and read this vector from the supplied
   * [IntBuffer] at the current buffer
   * [position][IntBuffer.position].
   *
   *
   * This method will not increment the position of the given IntBuffer.
   *
   *
   * In order to specify the offset into the IntBuffer at which the vector is
   * read, use [.Vector2i], taking the absolute position
   * as parameter.
   *
   * @see .Vector2i
   * @param buffer
   * values will be read in <tt>x, y</tt> order
   */
  constructor(buffer: IntBuffer) : this(buffer.position(), buffer) {}

  /**
   * Create a new [Vector2i] and read this vector from the supplied
   * [IntBuffer] starting at the specified absolute buffer
   * position/index.
   *
   *
   * This method will not increment the position of the given IntBuffer.
   *
   * @param index
   * the absolute position into the IntBuffer
   * @param buffer
   * values will be read in <tt>x, y</tt> order
   */
  constructor(index: Int, buffer: IntBuffer) {
    MemUtil.INSTANCE.get(this, index, buffer)
  }

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

  /**
   * Read this vector from the supplied [ByteBuffer] at the current
   * buffer [position][ByteBuffer.position].
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   *
   * In order to specify the offset into the ByteBuffer at which the vector is
   * read, use [.set], taking the absolute position as
   * parameter.
   *
   * @see .set
   * @param buffer
   * values will be read in <tt>x, y</tt> order
   * @return this
   */
  fun set(buffer: ByteBuffer): Vector2i {
    return set(buffer.position(), buffer)
  }

  /**
   * Read this vector from the supplied [ByteBuffer] starting at the
   * specified absolute buffer position/index.
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
  operator fun set(index: Int, buffer: ByteBuffer): Vector2i {
    MemUtil.INSTANCE.get(this, index, buffer)
    return this
  }

  /**
   * Read this vector from the supplied [IntBuffer] at the current
   * buffer [position][IntBuffer.position].
   *
   *
   * This method will not increment the position of the given IntBuffer.
   *
   *
   * In order to specify the offset into the IntBuffer at which the vector is
   * read, use [.set], taking the absolute position as
   * parameter.
   *
   * @see .set
   * @param buffer
   * values will be read in <tt>x, y</tt> order
   * @return this
   */
  fun set(buffer: IntBuffer): Vector2i {
    return set(buffer.position(), buffer)
  }

  /**
   * Read this vector from the supplied [IntBuffer] starting at the
   * specified absolute buffer position/index.
   *
   *
   * This method will not increment the position of the given IntBuffer.
   *
   * @param index
   * the absolute position into the IntBuffer
   * @param buffer
   * values will be read in <tt>x, y</tt> order
   * @return this
   */
  operator fun set(index: Int, buffer: IntBuffer): Vector2i {
    MemUtil.INSTANCE.get(this, index, buffer)
    return this
  }

  /**
   * Set the values of this vector by reading 2 integer values from off-heap memory,
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
  fun setFromAddress(address: Long): Vector2i {
    if (Options.NO_UNSAFE)
      throw UnsupportedOperationException("Not supported when using joml.nounsafe")
    val unsafe = MemUtil.INSTANCE as MemUtil.MemUtilUnsafe
    unsafe.get(this, address)
    return this
  }

  /* (non-Javadoc)
     * @see Vector2ic#get(int)
     */
  @Throws(IllegalArgumentException::class)
  override fun get(component: Int): Int {
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
  fun setComponent(component: Int, value: Int): Vector2i {
    when (component) {
      0 -> x = value
      1 -> y = value
      else -> throw IllegalArgumentException()
    }
    return this
  }

  /* (non-Javadoc)
     * @see Vector2ic#get(java.nio.ByteBuffer)
     */
  override fun get(buffer: ByteBuffer): ByteBuffer {
    return get(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Vector2ic#get(int, java.nio.ByteBuffer)
     */
  override fun get(index: Int, buffer: ByteBuffer): ByteBuffer {
    MemUtil.INSTANCE.put(this, index, buffer)
    return buffer
  }

  /* (non-Javadoc)
     * @see Vector2ic#get(java.nio.IntBuffer)
     */
  override fun get(buffer: IntBuffer): IntBuffer {
    return get(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Vector2ic#get(int, java.nio.IntBuffer)
     */
  override fun get(index: Int, buffer: IntBuffer): IntBuffer {
    MemUtil.INSTANCE.put(this, index, buffer)
    return buffer
  }

  override fun getToAddress(address: Long): Vector2ic {
    if (Options.NO_UNSAFE)
      throw UnsupportedOperationException("Not supported when using joml.nounsafe")
    val unsafe = MemUtil.INSTANCE as MemUtil.MemUtilUnsafe
    unsafe.put(this, address)
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
  fun sub(v: Vector2ic): Vector2i {
    return sub(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2ic#sub(Vector2ic, Vector2i)
     */
  override fun sub(v: Vector2ic, dest: Vector2i): Vector2i {
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
     * @see Vector2ic#sub(int, int, Vector2i)
     */
  override fun sub(x: Int, y: Int, dest: Vector2i): Vector2i {
    dest.x = this.x - x
    dest.y = this.y - y
    return dest
  }

  /* (non-Javadoc)
     * @see Vector2ic#lengthSquared()
     */
  override fun lengthSquared(): Long {
    return (x * x + y * y).toLong()
  }

  /* (non-Javadoc)
     * @see Vector2ic#length()
     */
  override fun length(): Double {
    return Math.sqrt(lengthSquared().toDouble())
  }

  /* (non-Javadoc)
     * @see Vector2ic#distance(Vector2ic)
     */
  override fun distance(v: Vector2ic): Double {
    return Math.sqrt(distanceSquared(v).toDouble())
  }

  /* (non-Javadoc)
     * @see Vector2ic#distance(int, int)
     */
  override fun distance(x: Int, y: Int): Double {
    return Math.sqrt(distanceSquared(x, y).toDouble())
  }

  /* (non-Javadoc)
     * @see Vector2ic#distanceSquared(Vector2ic)
     */
  override fun distanceSquared(v: Vector2ic): Long {
    val dx = this.x - v.x
    val dy = this.y - v.y
    return (dx * dx + dy * dy).toLong()
  }

  /* (non-Javadoc)
     * @see Vector2ic#distanceSquared(int, int)
     */
  override fun distanceSquared(x: Int, y: Int): Long {
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
  fun add(v: Vector2ic): Vector2i {
    return add(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2ic#add(Vector2ic, Vector2i)
     */
  override fun add(v: Vector2ic, dest: Vector2i): Vector2i {
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
     * @see Vector2ic#add(int, int, Vector2i)
     */
  override fun add(x: Int, y: Int, dest: Vector2i): Vector2i {
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
     * @see Vector2ic#mul(int, Vector2i)
     */
  override fun mul(scalar: Int, dest: Vector2i): Vector2i {
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
  fun mul(v: Vector2ic): Vector2i {
    return mul(v, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Vector2ic#mul(Vector2ic, Vector2i)
     */
  override fun mul(v: Vector2ic, dest: Vector2i): Vector2i {
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
     * @see Vector2ic#mul(int, int, Vector2i)
     */
  override fun mul(x: Int, y: Int, dest: Vector2i): Vector2i {
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
  override fun writeExternal(out: ObjectOutput) {
    out.writeInt(x)
    out.writeInt(y)
  }

  @Throws(IOException::class, ClassNotFoundException::class)
  override fun readExternal(`in`: ObjectInput) {
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
     * @see Vector2ic#negate(Vector2i)
     */
  override fun negate(dest: Vector2i): Vector2i {
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
  fun min(v: Vector2ic): Vector2i {
    return min(v, thisOrNew())
  }

  override fun min(v: Vector2ic, dest: Vector2i): Vector2i {
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
  fun max(v: Vector2ic): Vector2i {
    return max(v, thisOrNew())
  }

  override fun max(v: Vector2ic, dest: Vector2i): Vector2i {
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
    return "(" + formatter.format(x.toLong()) + " " + formatter.format(y.toLong()) + ")"
  }

  companion object {
    val zero: Vector2i get() = _zero
    private val serialVersionUID = 1L
  }

}
