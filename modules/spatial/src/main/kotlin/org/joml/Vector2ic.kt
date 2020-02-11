/*
 * (C) Copyright 2016-2018 JOML

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

import java.nio.ByteBuffer
import java.nio.IntBuffer

/**
 * Interface to a read-only view of a 2-dimensional vector of integers.
 *
 * @author Kai Burjack
 */
interface Vector2ic {

  /**
   * @return the value of the x component
   */
  var x: Int

  /**
   * @return the value of the y component
   */
  var y: Int

  /**
   * Store this vector into the supplied [ByteBuffer] at the current
   * buffer [position][ByteBuffer.position].
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   *
   * In order to specify the offset into the ByteBuffer at which the vector is
   * stored, use [.get], taking the absolute position
   * as parameter.
   *
   * @see .get
   * @param buffer
   * will receive the values of this vector in <tt>x, y</tt> order
   * @return the passed in buffer
   */
  operator fun get(buffer: ByteBuffer): ByteBuffer

  /**
   * Store this vector into the supplied [ByteBuffer] starting at the
   * specified absolute buffer position/index.
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   * @param index
   * the absolute position into the ByteBuffer
   * @param buffer
   * will receive the values of this vector in <tt>x, y</tt> order
   * @return the passed in buffer
   */
  operator fun get(index: Int, buffer: ByteBuffer): ByteBuffer

  /**
   * Store this vector into the supplied [IntBuffer] at the current
   * buffer [position][IntBuffer.position].
   *
   *
   * This method will not increment the position of the given IntBuffer.
   *
   *
   * In order to specify the offset into the IntBuffer at which the vector is
   * stored, use [.get], taking the absolute position as
   * parameter.
   *
   * @see .get
   * @param buffer
   * will receive the values of this vector in <tt>x, y</tt> order
   * @return the passed in buffer
   */
  operator fun get(buffer: IntBuffer): IntBuffer

  /**
   * Store this vector into the supplied [IntBuffer] starting at the
   * specified absolute buffer position/index.
   *
   *
   * This method will not increment the position of the given IntBuffer.
   *
   * @param index
   * the absolute position into the IntBuffer
   * @param buffer
   * will receive the values of this vector in <tt>x, y</tt> order
   * @return the passed in buffer
   */
  operator fun get(index: Int, buffer: IntBuffer): IntBuffer

  /**
   * Store this vector at the given off-heap memory address.
   *
   *
   * This method will throw an [UnsupportedOperationException] when JOML is used with `-Djoml.nounsafe`.
   *
   *
   * *This method is unsafe as it can result in a crash of the JVM process when the specified address range does not belong to this process.*
   *
   * @param address
   * the off-heap address where to store this vector
   * @return this
   */
  fun getToAddress(address: Long): Vector2ic

  /**
   * Subtract the supplied vector from this one and store the result in
   * `dest`.
   *
   * @param v
   * the vector to subtract
   * @param dest
   * will hold the result
   * @return dest
   */
  fun sub(v: Vector2ic, dest: Vector2i): Vector2i

  /**
   * Decrement the components of this vector by the given values and store the
   * result in `dest`.
   *
   * @param x
   * the x component to subtract
   * @param y
   * the y component to subtract
   * @param dest
   * will hold the result
   * @return dest
   */
  fun sub(x: Int, y: Int, dest: Vector2i): Vector2i

  /**
   * Return the length squared of this vector.
   *
   * @return the length squared
   */
  fun lengthSquared(): Long

  /**
   * Return the length of this vector.
   *
   * @return the length
   */
  fun length(): Double

  /**
   * Return the distance between this Vector and `v`.
   *
   * @param v
   * the other vector
   * @return the distance
   */
  fun distance(v: Vector2ic): Double

  /**
   * Return the distance between `this` vector and <tt>(x, y)</tt>.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @return the euclidean distance
   */
  fun distance(x: Int, y: Int): Double

  /**
   * Return the square of the distance between this vector and `v`.
   *
   * @param v
   * the other vector
   * @return the squared of the distance
   */
  fun distanceSquared(v: Vector2ic): Long

  /**
   * Return the square of the distance between `this` vector and
   * <tt>(x, y)</tt>.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @return the square of the distance
   */
  fun distanceSquared(x: Int, y: Int): Long

  /**
   * Add the supplied vector to this one and store the result in
   * `dest`.
   *
   * @param v
   * the vector to add
   * @param dest
   * will hold the result
   * @return dest
   */
  fun add(v: Vector2ic, dest: Vector2i): Vector2i

  /**
   * Increment the components of this vector by the given values and store the
   * result in `dest`.
   *
   * @param x
   * the x component to add
   * @param y
   * the y component to add
   * @param dest
   * will hold the result
   * @return dest
   */
  fun add(x: Int, y: Int, dest: Vector2i): Vector2i

  /**
   * Multiply all components of this [Vector2ic] by the given scalar
   * value and store the result in `dest`.
   *
   * @param scalar
   * the scalar to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(scalar: Int, dest: Vector2i): Vector2i

  /**
   * Multiply the supplied vector by this one and store the result in
   * `dest`.
   *
   * @param v
   * the vector to multiply
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(v: Vector2ic, dest: Vector2i): Vector2i

  /**
   * Multiply the components of this vector by the given values and store the
   * result in `dest`.
   *
   * @param x
   * the x component to multiply
   * @param y
   * the y component to multiply
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(x: Int, y: Int, dest: Vector2i): Vector2i

  /**
   * Negate this vector and store the result in `dest`.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun negate(dest: Vector2i): Vector2i

  /**
   * Set the components of `dest` to be the component-wise minimum of this and the other vector.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun min(v: Vector2ic, dest: Vector2i): Vector2i

  /**
   * Set the components of `dest` to be the component-wise maximum of this and the other vector.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun max(v: Vector2ic, dest: Vector2i): Vector2i

  /**
   * Get the value of the specified component of this vector.
   *
   * @param component
   * the component, within <tt>[0..1]</tt>
   * @return the value
   * @throws IllegalArgumentException if `component` is not within <tt>[0..1]</tt>
   */
  @Throws(IllegalArgumentException::class)
  operator fun get(component: Int): Int

}
