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
import java.nio.FloatBuffer

/**
 * Interface to a read-only view of a 2-dimensional vector of single-precision floats.
 *
 * @author Kai Burjack
 */
interface Vector2fc : Vector2fMinimal {

  /**
   * @return the value of the x component
   */
  override var x: Float

  /**
   * @return the value of the y component
   */
  override var y: Float

  /**
   * Store this vector into the supplied [ByteBuffer] at the current
   * buffer [position][ByteBuffer.position].
   *
   *
   * This method will not increment the position of the given ByteBuffer.
   *
   *
   * In order to specify the offset into the ByteBuffer at which
   * the vector is stored, use [.get], taking
   * the absolute position as parameter.
   *
   * @param buffer
   * will receive the values of this vector in <tt>x, y</tt> order
   * @return the passed in buffer
   * @see .get
   */
  operator fun get(buffer: ByteBuffer): ByteBuffer

  /**
   * Store this vector into the supplied [ByteBuffer] starting at the specified
   * absolute buffer position/index.
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
   * Store this vector into the supplied [FloatBuffer] at the current
   * buffer [position][FloatBuffer.position].
   *
   *
   * This method will not increment the position of the given FloatBuffer.
   *
   *
   * In order to specify the offset into the FloatBuffer at which
   * the vector is stored, use [.get], taking
   * the absolute position as parameter.
   *
   * @param buffer
   * will receive the values of this vector in <tt>x, y</tt> order
   * @return the passed in buffer
   * @see .get
   */
  operator fun get(buffer: FloatBuffer): FloatBuffer

  /**
   * Store this vector into the supplied [FloatBuffer] starting at the specified
   * absolute buffer position/index.
   *
   *
   * This method will not increment the position of the given FloatBuffer.
   *
   * @param index
   * the absolute position into the FloatBuffer
   * @param buffer
   * will receive the values of this vector in <tt>x, y</tt> order
   * @return the passed in buffer
   */
  operator fun get(index: Int, buffer: FloatBuffer): FloatBuffer

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
  fun getToAddress(address: Long): Vector2fc

  /**
   * Subtract `v` from `this` vector and store the result in `dest`.
   *
   * @param v
   * the vector to subtract
   * @param dest
   * will hold the result
   * @return dest
   */
  fun sub(v: Vector2fc, dest: Vector2f): Vector2f

  /**
   * Subtract <tt>(x, y)</tt> from this vector and store the result in `dest`.
   *
   * @param x
   * the x component to subtract
   * @param y
   * the y component to subtract
   * @param dest
   * will hold the result
   * @return dest
   */
  fun sub(x: Float, y: Float, dest: Vector2f): Vector2f

  /**
   * Return the dot product of this vector and `v`.
   *
   * @param v
   * the other vector
   * @return the dot product
   */
  fun dot(v: Vector2fc): Float

  /**
   * Return the angle between this vector and the supplied vector.
   *
   * @param v
   * the other vector
   * @return the angle, in radians
   */
  fun angle(v: Vector2fc): Float

  /**
   * Return the length of this vector.
   *
   * @return the length
   */
  fun length(): Float

  /**
   * Return the length squared of this vector.
   *
   * @return the length squared
   */
  fun lengthSquared(): Float

  /**
   * Return the distance between this and `v`.
   *
   * @param v
   * the other vector
   * @return the distance
   */
  fun distance(v: Vector2fc): Float

  /**
   * Return the distance squared between this and `v`.
   *
   * @param v
   * the other vector
   * @return the distance squared
   */
  //    float distanceSquared(Vector2fc v);

  /**
   * Return the distance between `this` vector and <tt>(x, y)</tt>.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @return the euclidean distance
   */
  fun distance(x: Float, y: Float): Float

  /**
   * Return the distance squared between `this` vector and <tt>(x, y)</tt>.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @return the euclidean distance squared
   */
  //    float distanceSquared(float x, float y);

  /**
   * Normalize this vector and store the result in `dest`.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun normalize(dest: Vector2f): Vector2f

  /**
   * Scale this vector to have the given length and store the result in `dest`.
   *
   * @param length
   * the desired length
   * @param dest
   * will hold the result
   * @return dest
   */
  fun normalize(length: Float, dest: Vector2f): Vector2f

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
  fun add(v: Vector2fc, dest: Vector2f): Vector2f

  /**
   * Increment the components of this vector by the given values and store the result in `dest`.
   *
   * @param x
   * the x component to add
   * @param y
   * the y component to add
   * @param dest
   * will hold the result
   * @return dest
   */
  fun add(x: Float, y: Float, dest: Vector2f): Vector2f

  /**
   * Negate this vector and store the result in `dest`.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun negate(dest: Vector2f): Vector2f

  /**
   * Multiply the components of this vector by the given scalar and store the result in `dest`.
   *
   * @param scalar
   * the value to multiply this vector's components by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(scalar: Float, dest: Vector2f): Vector2f

  /**
   * Multiply the components of this Vector2f by the given scalar values and store the result in `dest`.
   *
   * @param x
   * the x component to multiply this vector by
   * @param y
   * the y component to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(x: Float, y: Float, dest: Vector2f): Vector2f

  /**
   * Multiply this Vector2f component-wise by another Vector2f and store the result in `dest`.
   *
   * @param v
   * the vector to multiply by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(v: Vector2fc, dest: Vector2f): Vector2f

  /**
   * Multiply the given 3x2 matrix `mat` with `this` and store the
   * result in `dest`.
   *
   *
   * This method assumes the <tt>z</tt> component of `this` to be <tt>1.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulPosition(mat: Matrix3x2fc, dest: Vector2f): Vector2f

  /**
   * Multiply the given 3x2 matrix `mat` with `this` and store the
   * result in `dest`.
   *
   *
   * This method assumes the <tt>z</tt> component of `this` to be <tt>0.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulDirection(mat: Matrix3x2fc, dest: Vector2f): Vector2f

  /**
   * Linearly interpolate `this` and `other` using the given interpolation factor `t`
   * and store the result in `dest`.
   *
   *
   * If `t` is <tt>0.0</tt> then the result is `this`. If the interpolation factor is `1.0`
   * then the result is `other`.
   *
   * @param other
   * the other vector
   * @param t
   * the interpolation factor between 0.0 and 1.0
   * @param dest
   * will hold the result
   * @return dest
   */
  fun lerp(other: Vector2fc, t: Float, dest: Vector2f): Vector2f

  /**
   * Add the component-wise multiplication of `a * b` to this vector
   * and store the result in `dest`.
   *
   * @param a
   * the first multiplicand
   * @param b
   * the second multiplicand
   * @param dest
   * will hold the result
   * @return dest
   */
  fun fma(a: Vector2fc, b: Vector2fc, dest: Vector2f): Vector2f

  /**
   * Add the component-wise multiplication of `a * b` to this vector
   * and store the result in `dest`.
   *
   * @param a
   * the first multiplicand
   * @param b
   * the second multiplicand
   * @param dest
   * will hold the result
   * @return dest
   */
  fun fma(a: Float, b: Vector2fc, dest: Vector2f): Vector2f

  /**
   * Set the components of `dest` to be the component-wise minimum of this and the other vector.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun min(v: Vector2fc, dest: Vector2f): Vector2f

  /**
   * Set the components of `dest` to be the component-wise maximum of this and the other vector.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun max(v: Vector2fc, dest: Vector2f): Vector2f

  /**
   * Get the value of the specified component of this vector.
   *
   * @param component
   * the component, within <tt>[0..1]</tt>
   * @return the value
   * @throws IllegalArgumentException if `component` is not within <tt>[0..1]</tt>
   */
  @Throws(IllegalArgumentException::class)
  operator fun get(component: Int): Float

}
