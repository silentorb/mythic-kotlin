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

import mythic.spatial.Vector3m
import java.nio.ByteBuffer
import java.nio.FloatBuffer

/**
 * Interface to a read-only view of a 3-dimensional vector of single-precision floats.
 *
 * @author Kai Burjack
 */
interface Vector3fc {

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
   * @see .get
   * @param buffer
   * will receive the values of this vector in <tt>x, y, z</tt> order
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
   * will receive the values of this vector in <tt>x, y, z</tt> order
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
   * @see .get
   * @param buffer
   * will receive the values of this vector in <tt>x, y, z</tt> order
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
   * will receive the values of this vector in <tt>x, y, z</tt> order
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
  fun getToAddress(address: Long): Vector3fc

  /**
   * Subtract the supplied vector from this one and store the result in `dest`.
   *
   * @param v
   * the vector to subtract
   * @param dest
   * will hold the result
   * @return dest
   */
  fun sub(v: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Decrement the components of this vector by the given values and store the result in `dest`.
   *
   * @param x
   * the x component to subtract
   * @param y
   * the y component to subtract
   * @param z
   * the z component to subtract
   * @param dest
   * will hold the result
   * @return dest
   */
  fun sub(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m

  /**
   * Add the supplied vector to this one and store the result in `dest`.
   *
   * @param v
   * the vector to add
   * @param dest
   * will hold the result
   * @return dest
   */
  fun add(v: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Increment the components of this vector by the given values and store the result in `dest`.
   *
   * @param x
   * the x component to add
   * @param y
   * the y component to add
   * @param z
   * the z component to add
   * @param dest
   * will hold the result
   * @return dest
   */
  fun add(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m

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
  fun fma(a: Vector3fc, b: Vector3fc, dest: Vector3m): Vector3m

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
  fun fma(a: Float, b: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Multiply this Vector3m component-wise by another Vector3m and store the result in `dest`.
   *
   * @param v
   * the vector to multiply by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(v: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Divide this Vector3m component-wise by another Vector3m and store the result in `dest`.
   *
   * @param v
   * the vector to divide by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun div(v: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Multiply the given matrix `mat` with this Vector3m, perform perspective division
   * and store the result in `dest`.
   *
   *
   * This method uses <tt>w=1.0</tt> as the fourth vector component.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulProject(mat: Matrix4fc, dest: Vector3m): Vector3m

  /**
   * Multiply the given matrix with this Vector3m and store the result in `dest`.
   *
   * @param mat
   * the matrix
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(mat: Matrix3fc, dest: Vector3m): Vector3m

  /**
   * Multiply the given matrix with this Vector3m and store the result in `dest`.
   *
   * @param mat
   * the matrix
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(mat: Matrix3dc, dest: Vector3m): Vector3m

  /**
   * Multiply the given matrix `mat` with `this` by assuming a
   * third row in the matrix of <tt>(0, 0, 1)</tt> and store the result in `dest`.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(mat: Matrix3x2fc, dest: Vector3m): Vector3m

  /**
   * Multiply the transpose of the given matrix with this Vector3m and store the result in `dest`.
   *
   * @param mat
   * the matrix
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulTranspose(mat: Matrix3fc, dest: Vector3m): Vector3m

  /**
   * Multiply the given 4x4 matrix `mat` with `this` and store the
   * result in `dest`.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>1.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulPosition(mat: Matrix4fc, dest: Vector3m): Vector3m

  /**
   * Multiply the given 4x3 matrix `mat` with `this` and store the
   * result in `dest`.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>1.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulPosition(mat: Matrix4x3fc, dest: Vector3m): Vector3m

  /**
   * Multiply the transpose of the given 4x4 matrix `mat` with `this` and store the
   * result in `dest`.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>1.0</tt>.
   *
   * @param mat
   * the matrix whose transpose to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulTransposePosition(mat: Matrix4fc, dest: Vector3m): Vector3m

  /**
   * Multiply the given 4x4 matrix `mat` with `this`, store the
   * result in `dest` and return the *w* component of the resulting 4D vector.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>1.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @param dest
   * will hold the <tt>(x, y, z)</tt> components of the resulting vector
   * @return the *w* component of the resulting 4D vector after multiplication
   */
  fun mulPositionW(mat: Matrix4fc, dest: Vector3m): Float

  /**
   * Multiply the given 4x4 matrix `mat` with `this` and store the
   * result in `dest`.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>0.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulDirection(mat: Matrix4dc, dest: Vector3m): Vector3m

  /**
   * Multiply the given 4x4 matrix `mat` with `this` and store the
   * result in `dest`.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>0.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulDirection(mat: Matrix4fc, dest: Vector3m): Vector3m

  /**
   * Multiply the given 4x3 matrix `mat` with `this` and store the
   * result in `dest`.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>0.0</tt>.
   *
   * @param mat
   * the matrix to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulDirection(mat: Matrix4x3fc, dest: Vector3m): Vector3m

  /**
   * Multiply the transpose of the given 4x4 matrix `mat` with `this` and store the
   * result in `dest`.
   *
   *
   * This method assumes the <tt>w</tt> component of `this` to be <tt>0.0</tt>.
   *
   * @param mat
   * the matrix whose transpose to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mulTransposeDirection(mat: Matrix4fc, dest: Vector3m): Vector3m

  /**
   * Multiply all components of this [Vector3m] by the given scalar
   * value and store the result in `dest`.
   *
   * @param scalar
   * the scalar to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(scalar: Float, dest: Vector3m): Vector3m

  /**
   * Multiply the components of this Vector3m by the given scalar values and store the result in `dest`.
   *
   * @param x
   * the x component to multiply this vector by
   * @param y
   * the y component to multiply this vector by
   * @param z
   * the z component to multiply this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m

  /**
   * Divide all components of this [Vector3m] by the given scalar
   * value and store the result in `dest`.
   *
   * @param scalar
   * the scalar to divide by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun div(scalar: Float, dest: Vector3m): Vector3m

  /**
   * Divide the components of this Vector3m by the given scalar values and store the result in `dest`.
   *
   * @param x
   * the x component to divide this vector by
   * @param y
   * the y component to divide this vector by
   * @param z
   * the z component to divide this vector by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun div(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m

  /**
   * Rotate this vector by the given quaternion `quat` and store the result in `dest`.
   *
   * @see Quaternionfc.transform
   * @param quat
   * the quaternion to rotate this vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotate(quat: Quaternionfc, dest: Vector3m): Vector3m

  /**
   * Compute the quaternion representing a rotation of `this` vector to point along `toDir`
   * and store the result in `dest`.
   *
   *
   * Because there can be multiple possible rotations, this method chooses the one with the shortest arc.
   *
   * @see Quaternionf.rotationTo
   * @param toDir
   * the destination direction
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotationTo(toDir: Vector3fc, dest: Quaternionf): Quaternionf

  /**
   * Compute the quaternion representing a rotation of `this` vector to point along <tt>(toDirX, toDirY, toDirZ)</tt>
   * and store the result in `dest`.
   *
   *
   * Because there can be multiple possible rotations, this method chooses the one with the shortest arc.
   *
   * @see Quaternionf.rotationTo
   * @param toDirX
   * the x coordinate of the destination direction
   * @param toDirY
   * the y coordinate of the destination direction
   * @param toDirZ
   * the z coordinate of the destination direction
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotationTo(toDirX: Float, toDirY: Float, toDirZ: Float, dest: Quaternionf): Quaternionf

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
  fun rotateAxis(angle: Float, aX: Float, aY: Float, aZ: Float, dest: Vector3m): Vector3m

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
  fun rotateX(angle: Float, dest: Vector3m): Vector3m

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
  fun rotateY(angle: Float, dest: Vector3m): Vector3m

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
  fun rotateZ(angle: Float, dest: Vector3m): Vector3m

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
   * Normalize this vector and store the result in `dest`.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun normalize(dest: Vector3m): Vector3m

  /**
   * Scale this vector to have the given length and store the result in `dest`.
   *
   * @param length
   * the desired length
   * @param dest
   * will hold the result
   * @return dest
   */
  fun normalize(length: Float, dest: Vector3m): Vector3m

  /**
   * Compute the cross product of this vector and `v` and store the result in `dest`.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun cross(v: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Compute the cross product of this vector and <tt>(x, y, z)</tt> and store the result in `dest`.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @param z
   * the z component of the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun cross(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m

  /**
   * Return the distance between this Vector and `v`.
   *
   * @param v
   * the other vector
   * @return the distance
   */
  fun distance(v: Vector3fc): Float

  /**
   * Return the distance between `this` vector and <tt>(x, y, z)</tt>.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @param z
   * the z component of the other vector
   * @return the euclidean distance
   */
  fun distance(x: Float, y: Float, z: Float): Float

  /**
   * Return the square of the distance between this vector and `v`.
   *
   * @param v
   * the other vector
   * @return the squared of the distance
   */
  fun distanceSquared(v: Vector3fc): Float

  /**
   * Return the square of the distance between `this` vector and <tt>(x, y, z)</tt>.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @param z
   * the z component of the other vector
   * @return the square of the distance
   */
  fun distanceSquared(x: Float, y: Float, z: Float): Float

  /**
   * Return the dot product of this vector and the supplied vector.
   *
   * @param v
   * the other vector
   * @return the dot product
   */
  fun dot(v: Vector3fc): Float

  /**
   * Return the dot product of this vector and the vector <tt>(x, y, z)</tt>.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @param z
   * the z component of the other vector
   * @return the dot product
   */
  fun dot(x: Float, y: Float, z: Float): Float

  /**
   * Return the cosine of the angle between this vector and the supplied vector. Use this instead of Math.cos(this.angle(v)).
   *
   * @see .angle
   * @param v
   * the other vector
   * @return the cosine of the angle
   */
  fun angleCos(v: Vector3fc): Float

  /**
   * Return the angle between this vector and the supplied vector.
   *
   * @see .angleCos
   * @param v
   * the other vector
   * @return the angle, in radians
   */
  fun angle(v: Vector3fc): Float

  /**
   * Set the components of `dest` to be the component-wise minimum of this and the other vector.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun min(v: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Set the components of `dest` to be the component-wise maximum of this and the other vector.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun max(v: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Negate this vector and store the result in `dest`.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun negate(dest: Vector3m): Vector3m

  /**
   * Compute the absolute values of the individual components of `this` and store the result in `dest`.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun absolute(dest: Vector3m): Vector3m

  /**
   * Reflect this vector about the given `normal` vector and store the result in `dest`.
   *
   * @param normal
   * the vector to reflect about
   * @param dest
   * will hold the result
   * @return dest
   */
  fun reflect(normal: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Reflect this vector about the given normal vector and store the result in `dest`.
   *
   * @param x
   * the x component of the normal
   * @param y
   * the y component of the normal
   * @param z
   * the z component of the normal
   * @param dest
   * will hold the result
   * @return dest
   */
  fun reflect(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m

  /**
   * Compute the half vector between this and the other vector and store the result in `dest`.
   *
   * @param other
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun half(other: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Compute the half vector between this and the vector <tt>(x, y, z)</tt>
   * and store the result in `dest`.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @param z
   * the z component of the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun half(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m

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
  fun smoothStep(v: Vector3fc, t: Float, dest: Vector3m): Vector3m

  /**
   * Compute a hermite interpolation between `this` vector with its
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
  fun hermite(t0: Vector3fc, v1: Vector3fc, t1: Vector3fc, t: Float, dest: Vector3m): Vector3m

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
  fun lerp(other: Vector3fc, t: Float, dest: Vector3m): Vector3m

  /**
   * Get the value of the specified component of this vector.
   *
   * @param component
   * the component, within <tt>[0..2]</tt>
   * @return the value
   * @throws IllegalArgumentException if `component` is not within <tt>[0..2]</tt>
   */
  @Throws(IllegalArgumentException::class)
  operator fun get(component: Int): Float

  /**
   * Determine the component with the biggest absolute value.
   *
   * @return the component index, within <tt>[0..2]</tt>
   */
  fun maxComponent(): Int

  /**
   * Determine the component with the smallest (towards zero) absolute value.
   *
   * @return the component index, within <tt>[0..2]</tt>
   */
  fun minComponent(): Int

  /**
   * Transform `this` vector so that it is orthogonal to the given vector `v`, normalize the result and store it into `dest`.
   *
   *
   * Reference: [Gram–Schmidt process](https://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process)
   *
   * @param v
   * the reference vector which the result should be orthogonal to
   * @param dest
   * will hold the result
   * @return dest
   */
  fun orthogonalize(v: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Transform `this` vector so that it is orthogonal to the given unit vector `v`, normalize the result and store it into `dest`.
   *
   *
   * The vector `v` is assumed to be a [unit][.normalize] vector.
   *
   *
   * Reference: [Gram–Schmidt process](https://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process)
   *
   * @param v
   * the reference unit vector which the result should be orthogonal to
   * @param dest
   * will hold the result
   * @return dest
   */
  fun orthogonalizeUnit(v: Vector3fc, dest: Vector3m): Vector3m

}
