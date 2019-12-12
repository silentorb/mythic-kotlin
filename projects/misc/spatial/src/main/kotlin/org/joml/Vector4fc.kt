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
 * Interface to a read-only view of a 4-dimensional vector of single-precision floats.
 *
 * @author Kai Burjack
 */
interface Vector4fc {

  /**
   * @return the value of the x component
   */
  var x: Float

  /**
   * @return the value of the y component
   */
  var y: Float

  /**
   * @return the value of the z component
   */
  var z: Float

  /**
   * @return the value of the w component
   */
  var w: Float

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
   * will receive the values of this vector in <tt>x, y, z, w</tt> order
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
   * will receive the values of this vector in <tt>x, y, z, w</tt> order
   * @return the passed in buffer
   */
  operator fun get(index: Int, buffer: FloatBuffer): FloatBuffer

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
   * will receive the values of this vector in <tt>x, y, z, w</tt> order
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
   * will receive the values of this vector in <tt>x, y, z, w</tt> order
   * @return the passed in buffer
   */
  operator fun get(index: Int, buffer: ByteBuffer): ByteBuffer

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
  fun getToAddress(address: Long): Vector4fc

  /**
   * Subtract the supplied vector from this one and store the result in `dest`.
   *
   * @param v
   * the vector to subtract from `this`
   * @param dest
   * will hold the result
   * @return dest
   */
  fun sub(v: Vector4fc, dest: Vector4f): Vector4f

  /**
   * Subtract <tt>(x, y, z, w)</tt> from this and store the result in `dest`.
   *
   * @param x
   * the x component to subtract
   * @param y
   * the y component to subtract
   * @param z
   * the z component to subtract
   * @param w
   * the w component to subtract
   * @param dest
   * will hold the result
   * @return dest
   */
  fun sub(x: Float, y: Float, z: Float, w: Float, dest: Vector4f): Vector4f

  /**
   * Add the supplied vector to this one and store the result in `dest`.
   *
   * @param v
   * the vector to add
   * @param dest
   * will hold the result
   * @return dest
   */
  fun add(v: Vector4fc, dest: Vector4f): Vector4f

  /**
   * Increment the components of this vector by the given values and store the result in `dest`.
   *
   * @param x
   * the x component to add
   * @param y
   * the y component to add
   * @param z
   * the z component to add
   * @param w
   * the w component to add
   * @param dest
   * will hold the result
   * @return dest
   */
  fun add(x: Float, y: Float, z: Float, w: Float, dest: Vector4f): Vector4f

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
  fun fma(a: Vector4fc, b: Vector4fc, dest: Vector4f): Vector4f

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
  fun fma(a: Float, b: Vector4fc, dest: Vector4f): Vector4f

  /**
   * Multiply this Vector4f component-wise by another Vector4f and store the result in `dest`.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(v: Vector4fc, dest: Vector4f): Vector4f

  /**
   * Divide this Vector4f component-wise by another Vector4f and store the result in `dest`.
   *
   * @param v
   * the vector to divide by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun div(v: Vector4fc, dest: Vector4f): Vector4f

  /**
   * Multiply the given matrix mat with this Vector4f and store the result in
   * `dest`.
   *
   * @param mat
   * the matrix to multiply the vector with
   * @param dest
   * the destination vector to hold the result
   * @return dest
   */
  fun mul(mat: Matrix4fc, dest: Vector4f): Vector4f

  /**
   * Multiply the given affine matrix mat with this Vector4f and store the result in
   * `dest`.
   *
   * @param mat
   * the affine matrix to multiply the vector with
   * @param dest
   * the destination vector to hold the result
   * @return dest
   */
  fun mulAffine(mat: Matrix4fc, dest: Vector4f): Vector4f

  /**
   * Multiply the given matrix mat with this Vector4f and store the result in
   * `dest`.
   *
   * @param mat
   * the matrix to multiply the vector with
   * @param dest
   * the destination vector to hold the result
   * @return dest
   */
  fun mul(mat: Matrix4x3fc, dest: Vector4f): Vector4f

  /**
   * Multiply the given matrix `mat` with this Vector4f, perform perspective division
   * and store the result in `dest`.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulProject(mat: Matrix4fc, dest: Vector4f): Vector4f

  /**
   * Multiply all components of this [Vector4f] by the given scalar
   * value and store the result in `dest`.
   *
   * @param scalar
   * the scalar to multiply by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(scalar: Float, dest: Vector4f): Vector4f

  /**
   * Multiply the components of this Vector4f by the given scalar values and store the result in `dest`.
   *
   * @param x
   * the x component to multiply by
   * @param y
   * the y component to multiply by
   * @param z
   * the z component to multiply by
   * @param w
   * the w component to multiply by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(x: Float, y: Float, z: Float, w: Float, dest: Vector4f): Vector4f

  /**
   * Divide all components of this [Vector4f] by the given scalar
   * value and store the result in `dest`.
   *
   * @param scalar
   * the scalar to divide by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun div(scalar: Float, dest: Vector4f): Vector4f

  /**
   * Divide the components of this Vector4f by the given scalar values and store the result in `dest`.
   *
   * @param x
   * the x component to divide by
   * @param y
   * the y component to divide by
   * @param z
   * the z component to divide by
   * @param w
   * the w component to divide by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun div(x: Float, y: Float, z: Float, w: Float, dest: Vector4f): Vector4f

  /**
   * Rotate this vector by the given quaternion `quat` and store the result in `dest`.
   *
   * @see Quaternionf.transform
   * @param quat
   * the quaternion to rotate this vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotate(quat: Quaternionfc, dest: Vector4f): Vector4f

  /**
   * Rotate this vector the specified radians around the given rotation axis and store the result
   * into `dest`.
   *
   * @param angle
   * the angle in radians
   * @param aX
   * the x component of the rotation axis
   * @param aY
   * the y component of the rotation axis
   * @param aZ
   * the z component of the rotation axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateAxis(angle: Float, aX: Float, aY: Float, aZ: Float, dest: Vector4f): Vector4f

  /**
   * Rotate this vector the specified radians around the X axis and store the result
   * into `dest`.
   *
   * @param angle
   * the angle in radians
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateX(angle: Float, dest: Vector4f): Vector4f

  /**
   * Rotate this vector the specified radians around the Y axis and store the result
   * into `dest`.
   *
   * @param angle
   * the angle in radians
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateY(angle: Float, dest: Vector4f): Vector4f

  /**
   * Rotate this vector the specified radians around the Z axis and store the result
   * into `dest`.
   *
   * @param angle
   * the angle in radians
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateZ(angle: Float, dest: Vector4f): Vector4f

  /**
   * Return the length squared of this vector.
   *
   * @return the length squared
   */
  fun lengthSquared(): Float

  /**
   * Return the length of this vector.
   *
   * @return the length
   */
  fun length(): Float

  /**
   * Normalizes this vector and store the result in `dest`.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun normalize(dest: Vector4f): Vector4f

  /**
   * Scale this vector to have the given length and store the result in `dest`.
   *
   * @param length
   * the desired length
   * @param dest
   * will hold the result
   * @return dest
   */
  fun normalize(length: Float, dest: Vector4f): Vector4f

  /**
   * Return the distance between `this` vector and `v`.
   *
   * @param v
   * the other vector
   * @return the euclidean distance
   */
  fun distance(v: Vector4fc): Float

  /**
   * Return the distance between `this` vector and <tt>(x, y, z, w)</tt>.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @param z
   * the z component of the other vector
   * @param w
   * the w component of the other vector
   * @return the euclidean distance
   */
  fun distance(x: Float, y: Float, z: Float, w: Float): Float

  /**
   * Compute the dot product (inner product) of this vector and `v`
   * .
   *
   * @param v
   * the other vector
   * @return the dot product
   */
  fun dot(v: Vector4fc): Float

  /**
   * Compute the dot product (inner product) of this vector and <tt>(x, y, z, w)</tt>.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @param z
   * the z component of the other vector
   * @param w
   * the w component of the other vector
   * @return the dot product
   */
  fun dot(x: Float, y: Float, z: Float, w: Float): Float

  /**
   * Return the cosine of the angle between this vector and the supplied vector. Use this instead of `Math.cos(angle(v))`.
   *
   * @see .angle
   * @param v
   * the other vector
   * @return the cosine of the angle
   */
  fun angleCos(v: Vector4fc): Float

  /**
   * Return the angle between this vector and the supplied vector.
   *
   * @see .angleCos
   * @param v
   * the other vector
   * @return the angle, in radians
   */
  fun angle(v: Vector4fc): Float

  /**
   * Negate this vector and store the result in `dest`.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun negate(dest: Vector4f): Vector4f

  /**
   * Set the components of `dest` to be the component-wise minimum of this and the other vector.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun min(v: Vector4fc, dest: Vector4f): Vector4f

  /**
   * Set the components of `dest` to be the component-wise maximum of this and the other vector.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun max(v: Vector4fc, dest: Vector4f): Vector4f

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
  fun lerp(other: Vector4fc, t: Float, dest: Vector4f): Vector4f

  /**
   * Compute a smooth-step (i.e. hermite with zero tangents) interpolation
   * between `this` vector and the given vector `v` and
   * store the result in `dest`.
   *
   * @param v
   * the other vector
   * @param t
   * the interpolation factor, within <tt>[0..1]</tt>
   * @param dest
   * will hold the result
   * @return dest
   */
  fun smoothStep(v: Vector4fc, t: Float, dest: Vector4f): Vector4f

  /**
   * Compute a hermite interpolation between `this` vector and its
   * associated tangent `t0` and the given vector `v`
   * with its tangent `t1` and store the result in
   * `dest`.
   *
   * @param t0
   * the tangent of `this` vector
   * @param v1
   * the other vector
   * @param t1
   * the tangent of the other vector
   * @param t
   * the interpolation factor, within <tt>[0..1]</tt>
   * @param dest
   * will hold the result
   * @return dest
   */
  fun hermite(t0: Vector4fc, v1: Vector4fc, t1: Vector4fc, t: Float, dest: Vector4f): Vector4f

  /**
   * Get the value of the specified component of this vector.
   *
   * @param component
   * the component, within <tt>[0..3]</tt>
   * @return the value
   * @throws IllegalArgumentException if `component` is not within <tt>[0..3]</tt>
   */
  @Throws(IllegalArgumentException::class)
  operator fun get(component: Int): Float

}
