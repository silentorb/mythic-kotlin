/*
 * (C) Copyright 2015-2018 JOML

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
 * Interface to a read-only view of a quaternion of single-precision floats.
 *
 * @author Kai Burjack
 */
interface Quaternionfc {

  /**
   * @return the first component of the vector part
   */
  var x: Float

  /**
   * @return the second component of the vector part
   */
  var y: Float

  /**
   * @return the third component of the vector part
   */
  var z: Float

  /**
   * @return the real/scalar part of the quaternion
   */
  var w: Float

  /**
   * Normalize this quaternion and store the result in `dest`.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun normalize(dest: Quaternionf): Quaternionf

  /**
   * Add the quaternion <tt>(x, y, z, w)</tt> to this quaternion and store the result in `dest`.
   *
   * @param x
   * the x component of the vector part
   * @param y
   * the y component of the vector part
   * @param z
   * the z component of the vector part
   * @param w
   * the real/scalar component
   * @param dest
   * will hold the result
   * @return dest
   */
  fun add(x: Float, y: Float, z: Float, w: Float, dest: Quaternionf): Quaternionf

  /**
   * Add `q2` to this quaternion and store the result in `dest`.
   *
   * @param q2
   * the quaternion to add to this
   * @param dest
   * will hold the result
   * @return dest
   */
  fun add(q2: Quaternionfc, dest: Quaternionf): Quaternionf

  /**
   * Return the angle in radians represented by this quaternion rotation.
   *
   * @return the angle in radians
   */
  fun angle(): Float

  /**
   * Set the given destination matrix to the rotation represented by `this`.
   *
   * @see Matrix3f.set
   * @param dest
   * the matrix to write the rotation into
   * @return the passed in destination
   */
  operator fun get(dest: Matrix3f): Matrix3f

  /**
   * Set the given destination matrix to the rotation represented by `this`.
   *
   * @see Matrix3d.set
   * @param dest
   * the matrix to write the rotation into
   * @return the passed in destination
   */
  operator fun get(dest: Matrix3d): Matrix3d

  /**
   * Set the given destination matrix to the rotation represented by `this`.
   *
   * @see Matrix4f.set
   * @param dest
   * the matrix to write the rotation into
   * @return the passed in destination
   */
  operator fun get(dest: Matrix4f): Matrix4f

  /**
   * Set the given destination matrix to the rotation represented by `this`.
   *
   * @see Matrix4d.set
   * @param dest
   * the matrix to write the rotation into
   * @return the passed in destination
   */
  operator fun get(dest: Matrix4d): Matrix4d

  /**
   * Set the given destination matrix to the rotation represented by `this`.
   *
   * @see Matrix4x3f.set
   * @param dest
   * the matrix to write the rotation into
   * @return the passed in destination
   */
  operator fun get(dest: Matrix4x3f): Matrix4x3f

  /**
   * Set the given destination matrix to the rotation represented by `this`.
   *
   * @see Matrix4x3d.set
   * @param dest
   * the matrix to write the rotation into
   * @return the passed in destination
   */
  operator fun get(dest: Matrix4x3d): Matrix4x3d

  /**
   * Set the given [AxisAngle4f] to represent the rotation of
   * `this` quaternion.
   *
   * @param dest
   * the [AxisAngle4f] to set
   * @return the passed in destination
   */
  operator fun get(dest: AxisAngle4f): AxisAngle4f

  /**
   * Set the given [Quaterniond] to the values of `this`.
   *
   * @see Quaterniond.set
   * @param dest
   * the [Quaterniond] to set
   * @return the passed in destination
   */
  operator fun get(dest: Quaterniond): Quaterniond

  /**
   * Set the given [Quaternionf] to the values of `this`.
   *
   * @param dest
   * the [Quaternionf] to set
   * @return the passed in destination
   */
  operator fun get(dest: Quaternionf): Quaternionf

  /**
   * Store the 3x3 float matrix representation of `this` quaternion in column-major order into the given [ByteBuffer].
   *
   *
   * This is equivalent to calling: `this.get(new Matrix3f()).get(dest)`
   *
   * @param dest
   * the destination buffer
   * @return dest
   */
  fun getAsMatrix3f(dest: ByteBuffer): ByteBuffer

  /**
   * Store the 3x3 float matrix representation of `this` quaternion in column-major order into the given [FloatBuffer].
   *
   *
   * This is equivalent to calling: `this.get(new Matrix3f()).get(dest)`
   *
   * @param dest
   * the destination buffer
   * @return dest
   */
  fun getAsMatrix3f(dest: FloatBuffer): FloatBuffer

  /**
   * Store the 4x4 float matrix representation of `this` quaternion in column-major order into the given [ByteBuffer].
   *
   *
   * This is equivalent to calling: `this.get(new Matrix4f()).get(dest)`
   *
   * @param dest
   * the destination buffer
   * @return dest
   */
  fun getAsMatrix4f(dest: ByteBuffer): ByteBuffer

  /**
   * Store the 4x4 float matrix representation of `this` quaternion in column-major order into the given [FloatBuffer].
   *
   *
   * This is equivalent to calling: `this.get(new Matrix4f()).get(dest)`
   *
   * @param dest
   * the destination buffer
   * @return dest
   */
  fun getAsMatrix4f(dest: FloatBuffer): FloatBuffer

  /**
   * Store the 4x3 float matrix representation of `this` quaternion in column-major order into the given [ByteBuffer].
   *
   *
   * This is equivalent to calling: `this.get(new Matrix4x3f()).get(dest)`
   *
   * @param dest
   * the destination buffer
   * @return dest
   */
  fun getAsMatrix4x3f(dest: ByteBuffer): ByteBuffer

  /**
   * Store the 4x3 float matrix representation of `this` quaternion in column-major order into the given [FloatBuffer].
   *
   *
   * This is equivalent to calling: `this.get(new Matrix4x3f()).get(dest)`
   *
   * @param dest
   * the destination buffer
   * @return dest
   */
  fun getAsMatrix4x3f(dest: FloatBuffer): FloatBuffer

  /**
   * Multiply this quaternion by `q` and store the result in `dest`.
   *
   *
   * If <tt>T</tt> is `this` and <tt>Q</tt> is the given
   * quaternion, then the resulting quaternion <tt>R</tt> is:
   *
   *
   * <tt>R = T * Q</tt>
   *
   *
   * So, this method uses post-multiplication like the matrix classes, resulting in a
   * vector to be transformed by <tt>Q</tt> first, and then by <tt>T</tt>.
   *
   * @param q
   * the quaternion to multiply `this` by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(q: Quaternionfc, dest: Quaternionf): Quaternionf

  /**
   * Multiply this quaternion by the quaternion represented via <tt>(qx, qy, qz, qw)</tt> and store the result in `dest`.
   *
   *
   * If <tt>T</tt> is `this` and <tt>Q</tt> is the given
   * quaternion, then the resulting quaternion <tt>R</tt> is:
   *
   *
   * <tt>R = T * Q</tt>
   *
   *
   * So, this method uses post-multiplication like the matrix classes, resulting in a
   * vector to be transformed by <tt>Q</tt> first, and then by <tt>T</tt>.
   *
   * @param qx
   * the x component of the quaternion to multiply `this` by
   * @param qy
   * the y component of the quaternion to multiply `this` by
   * @param qz
   * the z component of the quaternion to multiply `this` by
   * @param qw
   * the w component of the quaternion to multiply `this` by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(qx: Float, qy: Float, qz: Float, qw: Float, dest: Quaternionf): Quaternionf

  /**
   * Pre-multiply this quaternion by `q` and store the result in `dest`.
   *
   *
   * If <tt>T</tt> is `this` and <tt>Q</tt> is the given quaternion, then the resulting quaternion <tt>R</tt> is:
   *
   *
   * <tt>R = Q * T</tt>
   *
   *
   * So, this method uses pre-multiplication, resulting in a vector to be transformed by <tt>T</tt> first, and then by <tt>Q</tt>.
   *
   * @param q
   * the quaternion to pre-multiply `this` by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun premul(q: Quaternionfc, dest: Quaternionf): Quaternionf

  /**
   * Pre-multiply this quaternion by the quaternion represented via <tt>(qx, qy, qz, qw)</tt> and store the result in `dest`.
   *
   *
   * If <tt>T</tt> is `this` and <tt>Q</tt> is the given quaternion, then the resulting quaternion <tt>R</tt> is:
   *
   *
   * <tt>R = Q * T</tt>
   *
   *
   * So, this method uses pre-multiplication, resulting in a vector to be transformed by <tt>T</tt> first, and then by <tt>Q</tt>.
   *
   * @param qx
   * the x component of the quaternion to multiply `this` by
   * @param qy
   * the y component of the quaternion to multiply `this` by
   * @param qz
   * the z component of the quaternion to multiply `this` by
   * @param qw
   * the w component of the quaternion to multiply `this` by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun premul(qx: Float, qy: Float, qz: Float, qw: Float, dest: Quaternionf): Quaternionf

  /**
   * Transform the given vector by this quaternion.
   * This will apply the rotation described by this quaternion to the given vector.
   *
   * @param vec
   * the vector to transform
   * @return vec
   */
  fun transform(vec: Vector3m): Vector3m

  /**
   * Transform the vector <tt>(1, 0, 0)</tt> by this quaternion.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformPositiveX(dest: Vector3m): Vector3m

  /**
   * Transform the vector <tt>(1, 0, 0)</tt> by this quaternion.
   *
   *
   * Only the first three components of the given 4D vector are modified.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformPositiveX(dest: Vector4f): Vector4f

  /**
   * Transform the vector <tt>(1, 0, 0)</tt> by this unit quaternion.
   *
   *
   * This method is only applicable when `this` is a unit quaternion.
   *
   *
   * Reference: [https://de.mathworks.com/](https://de.mathworks.com/help/aerotbx/ug/quatrotate.html?requestedDomain=true)
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformUnitPositiveX(dest: Vector3m): Vector3m

  /**
   * Transform the vector <tt>(1, 0, 0)</tt> by this unit quaternion.
   *
   *
   * Only the first three components of the given 4D vector are modified.
   *
   *
   * This method is only applicable when `this` is a unit quaternion.
   *
   *
   * Reference: [https://de.mathworks.com/](https://de.mathworks.com/help/aerotbx/ug/quatrotate.html?requestedDomain=true)
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformUnitPositiveX(dest: Vector4f): Vector4f

  /**
   * Transform the vector <tt>(0, 1, 0)</tt> by this quaternion.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformPositiveY(dest: Vector3m): Vector3m

  /**
   * Transform the vector <tt>(0, 1, 0)</tt> by this quaternion.
   *
   *
   * Only the first three components of the given 4D vector are modified.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformPositiveY(dest: Vector4f): Vector4f

  /**
   * Transform the vector <tt>(0, 1, 0)</tt> by this unit quaternion.
   *
   *
   * This method is only applicable when `this` is a unit quaternion.
   *
   *
   * Reference: [https://de.mathworks.com/](https://de.mathworks.com/help/aerotbx/ug/quatrotate.html?requestedDomain=true)
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformUnitPositiveY(dest: Vector3m): Vector3m

  /**
   * Transform the vector <tt>(0, 1, 0)</tt> by this unit quaternion.
   *
   *
   * Only the first three components of the given 4D vector are modified.
   *
   *
   * This method is only applicable when `this` is a unit quaternion.
   *
   *
   * Reference: [https://de.mathworks.com/](https://de.mathworks.com/help/aerotbx/ug/quatrotate.html?requestedDomain=true)
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformUnitPositiveY(dest: Vector4f): Vector4f

  /**
   * Transform the vector <tt>(0, 0, 1)</tt> by this quaternion.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformPositiveZ(dest: Vector3m): Vector3m

  /**
   * Transform the vector <tt>(0, 0, 1)</tt> by this quaternion.
   *
   *
   * Only the first three components of the given 4D vector are modified.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformPositiveZ(dest: Vector4f): Vector4f

  /**
   * Transform the vector <tt>(0, 0, 1)</tt> by this unit quaternion.
   *
   *
   * This method is only applicable when `this` is a unit quaternion.
   *
   *
   * Reference: [https://de.mathworks.com/](https://de.mathworks.com/help/aerotbx/ug/quatrotate.html?requestedDomain=true)
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformUnitPositiveZ(dest: Vector3m): Vector3m

  /**
   * Transform the vector <tt>(0, 0, 1)</tt> by this unit quaternion.
   *
   *
   * Only the first three components of the given 4D vector are modified.
   *
   *
   * This method is only applicable when `this` is a unit quaternion.
   *
   *
   * Reference: [https://de.mathworks.com/](https://de.mathworks.com/help/aerotbx/ug/quatrotate.html?requestedDomain=true)
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transformUnitPositiveZ(dest: Vector4f): Vector4f

  /**
   * Transform the given vector by this quaternion.
   * This will apply the rotation described by this quaternion to the given vector.
   *
   *
   * Only the first three components of the given 4D vector are being used and modified.
   *
   * @param vec
   * the vector to transform
   * @return vec
   */
  fun transform(vec: Vector4f): Vector4f

  /**
   * Transform the given vector by this quaternion and store the result in `dest`.
   * This will apply the rotation described by this quaternion to the given vector.
   *
   * @param vec
   * the vector to transform
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transform(vec: Vector3fc, dest: Vector3m): Vector3m

  /**
   * Transform the given vector <tt>(x, y, z)</tt> by this quaternion and store the result in `dest`.
   * This will apply the rotation described by this quaternion to the given vector.
   *
   * @param x
   * the x coordinate of the vector to transform
   * @param y
   * the y coordinate of the vector to transform
   * @param z
   * the z coordinate of the vector to transform
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transform(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m

  /**
   * Transform the given vector by this quaternion and store the result in `dest`.
   * This will apply the rotation described by this quaternion to the given vector.
   *
   *
   * Only the first three components of the given 4D vector are being used and set on the destination.
   *
   * @param vec
   * the vector to transform
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transform(vec: Vector4fc, dest: Vector4f): Vector4f

  /**
   * Transform the given vector <tt>(x, y, z)</tt> by this quaternion and store the result in `dest`.
   * This will apply the rotation described by this quaternion to the given vector.
   *
   * @param x
   * the x coordinate of the vector to transform
   * @param y
   * the y coordinate of the vector to transform
   * @param z
   * the z coordinate of the vector to transform
   * @param dest
   * will hold the result
   * @return dest
   */
  fun transform(x: Float, y: Float, z: Float, dest: Vector4f): Vector4f

  /**
   * Invert this quaternion and store the [normalized][.normalize] result in `dest`.
   *
   *
   * If this quaternion is already normalized, then [.conjugate] should be used instead.
   *
   * @see .conjugate
   * @param dest
   * will hold the result
   * @return dest
   */
  fun invert(dest: Quaternionf): Quaternionf

  /**
   * Divide `this` quaternion by `b` and store the result in `dest`.
   *
   *
   * The division expressed using the inverse is performed in the following way:
   *
   *
   * <tt>dest = this * b^-1</tt>, where <tt>b^-1</tt> is the inverse of `b`.
   *
   * @param b
   * the [Quaternionfc] to divide this by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun div(b: Quaternionfc, dest: Quaternionf): Quaternionf

  /**
   * Conjugate this quaternion and store the result in `dest`.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun conjugate(dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the cartesian base unit axes,
   * called the euler angles using rotation sequence <tt>XYZ</tt> and store the result in `dest`.
   *
   *
   * This method is equivalent to calling: <tt>rotateX(angleX, dest).rotateY(angleY).rotateZ(angleZ)</tt>
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   * @param angleX
   * the angle in radians to rotate about the x axis
   * @param angleY
   * the angle in radians to rotate about the y axis
   * @param angleZ
   * the angle in radians to rotate about the z axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateXYZ(angleX: Float, angleY: Float, angleZ: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the cartesian base unit axes,
   * called the euler angles, using the rotation sequence <tt>ZYX</tt> and store the result in `dest`.
   *
   *
   * This method is equivalent to calling: <tt>rotateZ(angleZ, dest).rotateY(angleY).rotateX(angleX)</tt>
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   * @param angleZ
   * the angle in radians to rotate about the z axis
   * @param angleY
   * the angle in radians to rotate about the y axis
   * @param angleX
   * the angle in radians to rotate about the x axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateZYX(angleZ: Float, angleY: Float, angleX: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the cartesian base unit axes,
   * called the euler angles, using the rotation sequence <tt>YXZ</tt> and store the result in `dest`.
   *
   *
   * This method is equivalent to calling: <tt>rotateY(angleY, dest).rotateX(angleX).rotateZ(angleZ)</tt>
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   * @param angleY
   * the angle in radians to rotate about the y axis
   * @param angleX
   * the angle in radians to rotate about the x axis
   * @param angleZ
   * the angle in radians to rotate about the z axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateYXZ(angleY: Float, angleX: Float, angleZ: Float, dest: Quaternionf): Quaternionf

  /**
   * Get the euler angles in radians in rotation sequence <tt>XYZ</tt> of this quaternion and store them in the
   * provided parameter `eulerAngles`.
   *
   * @param eulerAngles
   * will hold the euler angles in radians
   * @return the passed in vector
   */
  fun getEulerAnglesXYZ(eulerAngles: Vector3m): Vector3m

  /**
   * Return the square of the length of this quaternion.
   *
   * @return the length
   */
  fun lengthSquared(): Float

  /**
   * Interpolate between `this` [unit][.normalize] quaternion and the specified
   * `target` [unit][.normalize] quaternion using spherical linear interpolation using the specified interpolation factor `alpha`,
   * and store the result in `dest`.
   *
   *
   * This method resorts to non-spherical linear interpolation when the absolute dot product of `this` and `target` is
   * below <tt>1E-6f</tt>.
   *
   *
   * Reference: [http://fabiensanglard.net](http://fabiensanglard.net/doom3_documentation/37725-293747_293747.pdf)
   *
   * @param target
   * the target of the interpolation, which should be reached with <tt>alpha = 1.0</tt>
   * @param alpha
   * the interpolation factor, within <tt>[0..1]</tt>
   * @param dest
   * will hold the result
   * @return dest
   */
  fun slerp(target: Quaternionfc, alpha: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply scaling to this quaternion, which results in any vector transformed by the quaternion to change
   * its length by the given `factor`, and store the result in `dest`.
   *
   * @param factor
   * the scaling factor
   * @param dest
   * will hold the result
   * @return dest
   */
  fun scale(factor: Float, dest: Quaternionf): Quaternionf

  /**
   * Integrate the rotation given by the angular velocity `(vx, vy, vz)` around the x, y and z axis, respectively,
   * with respect to the given elapsed time delta `dt` and add the differentiate rotation to the rotation represented by this quaternion
   * and store the result into `dest`.
   *
   *
   * This method pre-multiplies the rotation given by `dt` and `(vx, vy, vz)` by `this`, so
   * the angular velocities are always relative to the local coordinate system of the rotation represented by `this` quaternion.
   *
   *
   * This method is equivalent to calling: `rotateLocal(dt * vx, dt * vy, dt * vz, dest)`
   *
   *
   * Reference: [http://physicsforgames.blogspot.de/](http://physicsforgames.blogspot.de/2010/02/quaternions.html)
   *
   * @see .rotateLocal
   * @param dt
   * the delta time
   * @param vx
   * the angular velocity around the x axis
   * @param vy
   * the angular velocity around the y axis
   * @param vz
   * the angular velocity around the z axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun integrate(dt: Float, vx: Float, vy: Float, vz: Float, dest: Quaternionf): Quaternionf

  /**
   * Compute a linear (non-spherical) interpolation of `this` and the given quaternion `q`
   * and store the result in `dest`.
   *
   *
   * Reference: [http://fabiensanglard.net](http://fabiensanglard.net/doom3_documentation/37725-293747_293747.pdf)
   *
   * @param q
   * the other quaternion
   * @param factor
   * the interpolation factor. It is between 0.0 and 1.0
   * @param dest
   * will hold the result
   * @return dest
   */
  fun nlerp(q: Quaternionfc, factor: Float, dest: Quaternionf): Quaternionf

  /**
   * Compute linear (non-spherical) interpolations of `this` and the given quaternion `q`
   * iteratively and store the result in `dest`.
   *
   *
   * This method performs a series of small-step nlerp interpolations to avoid doing a costly spherical linear interpolation, like
   * [slerp][.slerp],
   * by subdividing the rotation arc between `this` and `q` via non-spherical linear interpolations as long as
   * the absolute dot product of `this` and `q` is greater than the given `dotThreshold` parameter.
   *
   *
   * Thanks to <tt>@theagentd</tt> at [http://www.java-gaming.org/](http://www.java-gaming.org/) for providing the code.
   *
   * @param q
   * the other quaternion
   * @param alpha
   * the interpolation factor, between 0.0 and 1.0
   * @param dotThreshold
   * the threshold for the dot product of `this` and `q` above which this method performs another iteration
   * of a small-step linear interpolation
   * @param dest
   * will hold the result
   * @return dest
   */
  fun nlerpIterative(q: Quaternionfc, alpha: Float, dotThreshold: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to this quaternion that maps the given direction to the positive Z axis, and store the result in `dest`.
   *
   *
   * Because there are multiple possibilities for such a rotation, this method will choose the one that ensures the given up direction to remain
   * parallel to the plane spanned by the `up` and `dir` vectors.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   *
   * Reference: [http://answers.unity3d.com](http://answers.unity3d.com/questions/467614/what-is-the-source-code-of-quaternionlookrotation.html)
   *
   * @see .lookAlong
   * @param dir
   * the direction to map to the positive Z axis
   * @param up
   * the vector which will be mapped to a vector parallel to the plane
   * spanned by the given `dir` and `up`
   * @param dest
   * will hold the result
   * @return dest
   */
  fun lookAlong(dir: Vector3fc, up: Vector3fc, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to this quaternion that maps the given direction to the positive Z axis, and store the result in `dest`.
   *
   *
   * Because there are multiple possibilities for such a rotation, this method will choose the one that ensures the given up direction to remain
   * parallel to the plane spanned by the <tt>up</tt> and <tt>dir</tt> vectors.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   *
   * Reference: [http://answers.unity3d.com](http://answers.unity3d.com/questions/467614/what-is-the-source-code-of-quaternionlookrotation.html)
   *
   * @param dirX
   * the x-coordinate of the direction to look along
   * @param dirY
   * the y-coordinate of the direction to look along
   * @param dirZ
   * the z-coordinate of the direction to look along
   * @param upX
   * the x-coordinate of the up vector
   * @param upY
   * the y-coordinate of the up vector
   * @param upZ
   * the z-coordinate of the up vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun lookAlong(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` that rotates the <tt>fromDir</tt> vector to point along <tt>toDir</tt> and
   * store the result in `dest`.
   *
   *
   * Since there can be multiple possible rotations, this method chooses the one with the shortest arc.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   *
   * Reference: [stackoverflow.com](http://stackoverflow.com/questions/1171849/finding-quaternion-representing-the-rotation-from-one-vector-to-another#answer-1171995)
   *
   * @param fromDirX
   * the x-coordinate of the direction to rotate into the destination direction
   * @param fromDirY
   * the y-coordinate of the direction to rotate into the destination direction
   * @param fromDirZ
   * the z-coordinate of the direction to rotate into the destination direction
   * @param toDirX
   * the x-coordinate of the direction to rotate to
   * @param toDirY
   * the y-coordinate of the direction to rotate to
   * @param toDirZ
   * the z-coordinate of the direction to rotate to
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateTo(fromDirX: Float, fromDirY: Float, fromDirZ: Float, toDirX: Float, toDirY: Float, toDirZ: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` that rotates the `fromDir` vector to point along `toDir` and
   * store the result in `dest`.
   *
   *
   * Because there can be multiple possible rotations, this method chooses the one with the shortest arc.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   * @see .rotateTo
   * @param fromDir
   * the starting direction
   * @param toDir
   * the destination direction
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateTo(fromDir: Vector3fc, toDir: Vector3fc, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the basis unit axes of the
   * cartesian space and store the result in `dest`.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   * @param angleX
   * the angle in radians to rotate about the x axis
   * @param angleY
   * the angle in radians to rotate about the y axis
   * @param angleZ
   * the angle in radians to rotate about the z axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotate(angleX: Float, angleY: Float, angleZ: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the basis unit axes of the
   * local coordinate system represented by this quaternion and store the result in `dest`.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `R * Q`. So when transforming a
   * vector `v` with the new quaternion by using `R * Q * v`, the
   * rotation represented by `this` will be applied first!
   *
   * @param angleX
   * the angle in radians to rotate about the local x axis
   * @param angleY
   * the angle in radians to rotate about the local y axis
   * @param angleZ
   * the angle in radians to rotate about the local z axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateLocal(angleX: Float, angleY: Float, angleZ: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the x axis
   * and store the result in `dest`.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   * @see .rotate
   * @param angle
   * the angle in radians to rotate about the x axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateX(angle: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the y axis
   * and store the result in `dest`.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   * @see .rotate
   * @param angle
   * the angle in radians to rotate about the y axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateY(angle: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the z axis
   * and store the result in `dest`.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   * @see .rotate
   * @param angle
   * the angle in radians to rotate about the z axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateZ(angle: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the local x axis
   * and store the result in `dest`.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `R * Q`. So when transforming a
   * vector `v` with the new quaternion by using `R * Q * v`, the
   * rotation represented by `this` will be applied first!
   *
   * @param angle
   * the angle in radians to rotate about the local x axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateLocalX(angle: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the local y axis
   * and store the result in `dest`.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `R * Q`. So when transforming a
   * vector `v` with the new quaternion by using `R * Q * v`, the
   * rotation represented by `this` will be applied first!
   *
   * @param angle
   * the angle in radians to rotate about the local y axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateLocalY(angle: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the local z axis
   * and store the result in `dest`.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `R * Q`. So when transforming a
   * vector `v` with the new quaternion by using `R * Q * v`, the
   * rotation represented by `this` will be applied first!
   *
   * @param angle
   * the angle in radians to rotate about the local z axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateLocalZ(angle: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the specified axis
   * and store the result in `dest`.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   * @param angle
   * the angle in radians to rotate about the specified axis
   * @param axisX
   * the x coordinate of the rotation axis
   * @param axisY
   * the y coordinate of the rotation axis
   * @param axisZ
   * the z coordinate of the rotation axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateAxis(angle: Float, axisX: Float, axisY: Float, axisZ: Float, dest: Quaternionf): Quaternionf

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the specified axis
   * and store the result in `dest`.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   * @see .rotateAxis
   * @param angle
   * the angle in radians to rotate about the specified axis
   * @param axis
   * the rotation axis
   * @param dest
   * will hold the result
   * @return dest
   */
  fun rotateAxis(angle: Float, axis: Vector3fc, dest: Quaternionf): Quaternionf

  /**
   * Compute the difference between `this` and the `other` quaternion
   * and store the result in `dest`.
   *
   *
   * The difference is the rotation that has to be applied to get from
   * `this` rotation to `other`. If <tt>T</tt> is `this`, <tt>Q</tt>
   * is `other` and <tt>D</tt> is the computed difference, then the following equation holds:
   *
   *
   * <tt>T * D = Q</tt>
   *
   *
   * It is defined as: <tt>D = T^-1 * Q</tt>, where <tt>T^-1</tt> denotes the [inverse][.invert] of <tt>T</tt>.
   *
   * @param other
   * the other quaternion
   * @param dest
   * will hold the result
   * @return dest
   */
  fun difference(other: Quaternionf, dest: Quaternionf): Quaternionf

  /**
   * Obtain the direction of <tt>+X</tt> before the rotation transformation represented by `this` quaternion is applied.
   *
   *
   * This method is equivalent to the following code:
   * <pre>
   * Quaternionf inv = new Quaternionf(this).invert();
   * inv.transform(dir.set(1, 0, 0));
  </pre> *
   *
   * @param dir
   * will hold the direction of <tt>+X</tt>
   * @return dir
   */
  fun positiveX(dir: Vector3m): Vector3m

  /**
   * Obtain the direction of <tt>+X</tt> before the rotation transformation represented by `this` *normalized* quaternion is applied.
   * The quaternion *must* be [normalized][.normalize] for this method to work.
   *
   *
   * This method is equivalent to the following code:
   * <pre>
   * Quaternionf inv = new Quaternionf(this).conjugate();
   * inv.transform(dir.set(1, 0, 0));
  </pre> *
   *
   * @param dir
   * will hold the direction of <tt>+X</tt>
   * @return dir
   */
  fun normalizedPositiveX(dir: Vector3m): Vector3m

  /**
   * Obtain the direction of <tt>+Y</tt> before the rotation transformation represented by `this` quaternion is applied.
   *
   *
   * This method is equivalent to the following code:
   * <pre>
   * Quaternionf inv = new Quaternionf(this).invert();
   * inv.transform(dir.set(0, 1, 0));
  </pre> *
   *
   * @param dir
   * will hold the direction of <tt>+Y</tt>
   * @return dir
   */
  fun positiveY(dir: Vector3m): Vector3m

  /**
   * Obtain the direction of <tt>+Y</tt> before the rotation transformation represented by `this` *normalized* quaternion is applied.
   * The quaternion *must* be [normalized][.normalize] for this method to work.
   *
   *
   * This method is equivalent to the following code:
   * <pre>
   * Quaternionf inv = new Quaternionf(this).conjugate();
   * inv.transform(dir.set(0, 1, 0));
  </pre> *
   *
   * @param dir
   * will hold the direction of <tt>+Y</tt>
   * @return dir
   */
  fun normalizedPositiveY(dir: Vector3m): Vector3m

  /**
   * Obtain the direction of <tt>+Z</tt> before the rotation transformation represented by `this` quaternion is applied.
   *
   *
   * This method is equivalent to the following code:
   * <pre>
   * Quaternionf inv = new Quaternionf(this).invert();
   * inv.transform(dir.set(0, 0, 1));
  </pre> *
   *
   * @param dir
   * will hold the direction of <tt>+Z</tt>
   * @return dir
   */
  fun positiveZ(dir: Vector3m): Vector3m

  /**
   * Obtain the direction of <tt>+Z</tt> before the rotation transformation represented by `this` *normalized* quaternion is applied.
   * The quaternion *must* be [normalized][.normalize] for this method to work.
   *
   *
   * This method is equivalent to the following code:
   * <pre>
   * Quaternionf inv = new Quaternionf(this).conjugate();
   * inv.transform(dir.set(0, 0, 1));
  </pre> *
   *
   * @param dir
   * will hold the direction of <tt>+Z</tt>
   * @return dir
   */
  fun normalizedPositiveZ(dir: Vector3m): Vector3m

}
