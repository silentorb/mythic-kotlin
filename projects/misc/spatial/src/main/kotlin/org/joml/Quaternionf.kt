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

import mythic.spatial.Pi
import mythic.spatial.Vector3m
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
 * Quaternion of 4 single-precision floats which can represent rotation and uniform scaling.
 *
 * @author Richard Greenlees
 * @author Kai Burjack
 */
class Quaternionf : Externalizable, Quaternionfc {

  /**
   * The first component of the vector part.
   */
 override var x: Float = 0.toFloat()
  /**
   * The second component of the vector part.
   */
  override  var y: Float = 0.toFloat()
  /**
   * The third component of the vector part.
   */
  override var z: Float = 0.toFloat()
  /**
   * The real/scalar part of the quaternion.
   */
  override var w: Float = 0.toFloat()

  /**
   * Create a new [Quaternionf] and initialize it with <tt>(x=0, y=0, z=0, w=1)</tt>,
   * where <tt>(x, y, z)</tt> is the vector part of the quaternion and <tt>w</tt> is the real/scalar part.
   */
  constructor() {
    this.w = 1.0f
  }

  /**
   * Create a new [Quaternionf] and initialize its components to the given values.
   *
   * @param x
   * the first component of the imaginary part
   * @param y
   * the second component of the imaginary part
   * @param z
   * the third component of the imaginary part
   * @param w
   * the real part
   */
  constructor(x: Float, y: Float, z: Float, w: Float) {
    this.x = x
    this.y = y
    this.z = z
    this.w = w
  }

  /**
   * Create a new [Quaternionf] and initialize its components to the same values as the given [Quaternionf].
   *
   * @param source
   * the [Quaternionf] to take the component values from
   */
  constructor(source: Quaternionf) {
    MemUtil.INSTANCE.copy(source, this)
  }

  /**
   * Create a new [Quaternionf] which represents the rotation of the given [AxisAngle4f].
   *
   * @param axisAngle
   * the [AxisAngle4f]
   */
  constructor(axisAngle: AxisAngle4f) {
    val sin = Math.sin(axisAngle.angle * 0.5).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), axisAngle.angle * 0.5).toFloat()
    x = axisAngle.x * sin
    y = axisAngle.y * sin
    z = axisAngle.z * sin
    w = cos
  }

  /**
   * Normalize this quaternion.
   *
   * @return this
   */
  fun normalize(): Quaternionf {
    val invNorm = (1.0 / Math.sqrt((x * x + y * y + z * z + w * w).toDouble())).toFloat()
    x *= invNorm
    y *= invNorm
    z *= invNorm
    w *= invNorm
    return this
  }

  /* (non-Javadoc)
     * @see Quaternionfc#normalize(Quaternionf)
     */
  override fun normalize(dest: Quaternionf): Quaternionf {
    val invNorm = (1.0 / Math.sqrt((x * x + y * y + z * z + w * w).toDouble())).toFloat()
    dest.x = x * invNorm
    dest.y = y * invNorm
    dest.z = z * invNorm
    dest.w = w * invNorm
    return dest
  }

  /**
   * Add the quaternion <tt>(x, y, z, w)</tt> to this quaternion.
   *
   * @param x
   * the x component of the vector part
   * @param y
   * the y component of the vector part
   * @param z
   * the z component of the vector part
   * @param w
   * the real/scalar component
   * @return this
   */
  fun add(x: Float, y: Float, z: Float, w: Float): Quaternionf {
    return add(x, y, z, w, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#add(float, float, float, float, Quaternionf)
     */
  override fun add(x: Float, y: Float, z: Float, w: Float, dest: Quaternionf): Quaternionf {
    dest.x = this.x + x
    dest.y = this.y + y
    dest.z = this.z + z
    dest.w = this.w + w
    return dest
  }

  /**
   * Add `q2` to this quaternion.
   *
   * @param q2
   * the quaternion to add to this
   * @return this
   */
  fun add(q2: Quaternionfc): Quaternionf {
    x += q2.x
    y += q2.y
    z += q2.z
    w += q2.w
    return this
  }

  /* (non-Javadoc)
     * @see Quaternionfc#add(Quaternionfc, Quaternionf)
     */
  override fun add(q2: Quaternionfc, dest: Quaternionf): Quaternionf {
    dest.x = x + q2.x
    dest.y = y + q2.y
    dest.z = z + q2.z
    dest.w = w + q2.w
    return dest
  }

  /**
   * Return the dot of this quaternion and `otherQuat`.
   *
   * @param otherQuat
   * the other quaternion
   * @return the dot product
   */
  fun dot(otherQuat: Quaternionf): Float {
    return this.x * otherQuat.x + this.y * otherQuat.y + this.z * otherQuat.z + this.w * otherQuat.w
  }

  /* (non-Javadoc)
     * @see Quaternionfc#angle()
     */
  override fun angle(): Float {
    val angle = (2.0 * Math.acos(w.toDouble())).toFloat()
    return if (angle <= Math.PI) angle else Pi + Pi - angle
  }

  /* (non-Javadoc)
     * @see Quaternionfc#get(org.joml.Matrix3f)
     */
  override fun get(dest: Matrix3f): Matrix3f {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#get(org.joml.Matrix3d)
     */
  override fun get(dest: Matrix3d): Matrix3d {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#get(Matrix4f)
     */
  override fun get(dest: Matrix4f): Matrix4f {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#get(org.joml.Matrix4d)
     */
  override fun get(dest: Matrix4d): Matrix4d {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#get(org.joml.Matrix4x3f)
     */
  override fun get(dest: Matrix4x3f): Matrix4x3f {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#get(org.joml.Matrix4x3d)
     */
  override fun get(dest: Matrix4x3d): Matrix4x3d {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#get(org.joml.AxisAngle4f)
     */
  override fun get(dest: AxisAngle4f): AxisAngle4f {
    var x = this.x
    var y = this.y
    var z = this.z
    var w = this.w
    if (w > 1.0f) {
      val invNorm = (1.0 / Math.sqrt((x * x + y * y + z * z + w * w).toDouble())).toFloat()
      x *= invNorm
      y *= invNorm
      z *= invNorm
      w *= invNorm
    }
    dest.angle = (2.0f * Math.acos(w.toDouble())).toFloat()
    var s = Math.sqrt(1.0 - w * w).toFloat()
    if (s < 0.001f) {
      dest.x = x
      dest.y = y
      dest.z = z
    } else {
      s = 1.0f / s
      dest.x = x * s
      dest.y = y * s
      dest.z = z * s
    }
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#get(org.joml.Quaterniond)
     */
  override fun get(dest: Quaterniond): Quaterniond {
    return dest.set(this)
  }

  /**
   * Set the given [Quaternionf] to the values of `this`.
   *
   * @see .set
   * @param dest
   * the [Quaternionf] to set
   * @return the passed in destination
   */
  override fun get(dest: Quaternionf): Quaternionf {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#getAsMatrix3f(java.nio.ByteBuffer)
     */
  override fun getAsMatrix3f(dest: ByteBuffer): ByteBuffer {
    MemUtil.INSTANCE.putMatrix3f(this, dest.position(), dest)
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#getAsMatrix3f(java.nio.FloatBuffer)
     */
  override fun getAsMatrix3f(dest: FloatBuffer): FloatBuffer {
    MemUtil.INSTANCE.putMatrix3f(this, dest.position(), dest)
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#getAsMatrix4f(java.nio.ByteBuffer)
     */
  override fun getAsMatrix4f(dest: ByteBuffer): ByteBuffer {
    MemUtil.INSTANCE.putMatrix4f(this, dest.position(), dest)
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#getAsMatrix4f(java.nio.FloatBuffer)
     */
  override fun getAsMatrix4f(dest: FloatBuffer): FloatBuffer {
    MemUtil.INSTANCE.putMatrix4f(this, dest.position(), dest)
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#getAsMatrix4x3f(java.nio.ByteBuffer)
     */
  override fun getAsMatrix4x3f(dest: ByteBuffer): ByteBuffer {
    MemUtil.INSTANCE.putMatrix4x3f(this, dest.position(), dest)
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#getAsMatrix4x3f(java.nio.FloatBuffer)
     */
  override fun getAsMatrix4x3f(dest: FloatBuffer): FloatBuffer {
    MemUtil.INSTANCE.putMatrix4x3f(this, dest.position(), dest)
    return dest
  }

  /**
   * Set this quaternion to the given values.
   *
   * @param x
   * the new value of x
   * @param y
   * the new value of y
   * @param z
   * the new value of z
   * @param w
   * the new value of w
   * @return this
   */
  operator fun set(x: Float, y: Float, z: Float, w: Float): Quaternionf {
    this.x = x
    this.y = y
    this.z = z
    this.w = w
    return this
  }

  /**
   * Set this quaternion to be a copy of q.
   *
   * @param q
   * the [Quaternionf] to copy
   * @return this
   */
  fun set(q: Quaternionfc): Quaternionf {
    if (q is Quaternionf)
      MemUtil.INSTANCE.copy(q, this)
    else {
      this.x = q.x
      this.y = q.y
      this.z = q.z
      this.w = q.w
    }
    return this
  }

  /**
   * Set this quaternion to a rotation equivalent to the given [AxisAngle4f].
   *
   * @param axisAngle
   * the [AxisAngle4f]
   * @return this
   */
  fun set(axisAngle: AxisAngle4f): Quaternionf {
    return setAngleAxis(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z)
  }

  /**
   * Set this quaternion to a rotation equivalent to the given [AxisAngle4d].
   *
   * @param axisAngle
   * the [AxisAngle4d]
   * @return this
   */
  fun set(axisAngle: AxisAngle4d): Quaternionf {
    return setAngleAxis(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z)
  }

  /**
   * Set this quaternion to a rotation equivalent to the supplied axis and
   * angle (in radians).
   *
   *
   * This method assumes that the given rotation axis <tt>(x, y, z)</tt> is already normalized
   *
   * @param angle
   * the angle in radians
   * @param x
   * the x-component of the normalized rotation axis
   * @param y
   * the y-component of the normalized rotation axis
   * @param z
   * the z-component of the normalized rotation axis
   * @return this
   */
  fun setAngleAxis(angle: Float, x: Float, y: Float, z: Float): Quaternionf {
    val s = Math.sin(angle * 0.5).toFloat()
    this.x = x * s
    this.y = y * s
    this.z = z * s
    this.w = Math.cosFromSin(s.toDouble(), angle * 0.5).toFloat()
    return this
  }

  /**
   * Set this quaternion to a rotation equivalent to the supplied axis and
   * angle (in radians).
   *
   *
   * This method assumes that the given rotation axis <tt>(x, y, z)</tt> is already normalized
   *
   * @param angle
   * the angle in radians
   * @param x
   * the x-component of the normalized rotation axis
   * @param y
   * the y-component of the normalized rotation axis
   * @param z
   * the z-component of the normalized rotation axis
   * @return this
   */
  fun setAngleAxis(angle: Double, x: Double, y: Double, z: Double): Quaternionf {
    val s = Math.sin(angle * 0.5)
    this.x = (x * s).toFloat()
    this.y = (y * s).toFloat()
    this.z = (z * s).toFloat()
    this.w = Math.cosFromSin(s, angle * 0.5).toFloat()
    return this
  }

  /**
   * Set this [Quaternionf] to a rotation of the given angle in radians about the supplied
   * axis, all of which are specified via the [AxisAngle4f].
   *
   * @see .rotationAxis
   * @param axisAngle
   * the [AxisAngle4f] giving the rotation angle in radians and the axis to rotate about
   * @return this
   */
  fun rotationAxis(axisAngle: AxisAngle4f): Quaternionf {
    return rotationAxis(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z)
  }

  /**
   * Set this quaternion to a rotation of the given angle in radians about the supplied axis.
   *
   * @param angle
   * the rotation angle in radians
   * @param axisX
   * the x-coordinate of the rotation axis
   * @param axisY
   * the y-coordinate of the rotation axis
   * @param axisZ
   * the z-coordinate of the rotation axis
   * @return this
   */
  fun rotationAxis(angle: Float, axisX: Float, axisY: Float, axisZ: Float): Quaternionf {
    val hangle = angle / 2.0f
    val sinAngle = Math.sin(hangle.toDouble()).toFloat()
    val invVLength = (1.0 / Math.sqrt((axisX * axisX + axisY * axisY + axisZ * axisZ).toDouble())).toFloat()

    x = axisX * invVLength * sinAngle
    y = axisY * invVLength * sinAngle
    z = axisZ * invVLength * sinAngle
    w = Math.cosFromSin(sinAngle.toDouble(), hangle.toDouble()).toFloat()

    return this
  }

  /**
   * Set this quaternion to a rotation of the given angle in radians about the supplied axis.
   *
   * @see .rotationAxis
   * @param angle
   * the rotation angle in radians
   * @param axis
   * the axis to rotate about
   * @return this
   */
  fun rotationAxis(angle: Float, axis: Vector3fc): Quaternionf {
    return rotationAxis(angle, axis.x, axis.y, axis.z)
  }

  /**
   * Set this quaternion to represent a rotation of the given angles in radians about the basis unit axes of the cartesian space.
   *
   * @param angleX
   * the angle in radians to rotate about the x axis
   * @param angleY
   * the angle in radians to rotate about the y axis
   * @param angleZ
   * the angle in radians to rotate about the z axis
   * @return this
   */
  fun rotation(angleX: Float, angleY: Float, angleZ: Float): Quaternionf {
    val thetaX = angleX * 0.5
    val thetaY = angleY * 0.5
    val thetaZ = angleZ * 0.5
    val thetaMagSq = thetaX * thetaX + thetaY * thetaY + thetaZ * thetaZ
    val s: Double
    if (thetaMagSq * thetaMagSq / 24.0f < 1E-8f) {
      w = (1.0 - thetaMagSq / 2.0).toFloat()
      s = 1.0 - thetaMagSq / 6.0
    } else {
      val thetaMag = Math.sqrt(thetaMagSq)
      val sin = Math.sin(thetaMag)
      s = sin / thetaMag
      w = Math.cosFromSin(sin, thetaMag).toFloat()
    }
    x = (thetaX * s).toFloat()
    y = (thetaY * s).toFloat()
    z = (thetaZ * s).toFloat()
    return this
  }

  /**
   * Set this quaternion to represent a rotation of the given radians about the x axis.
   *
   * @param angle
   * the angle in radians to rotate about the x axis
   * @return this
   */
  fun rotationX(angle: Float): Quaternionf {
    val sin = Math.sin(angle * 0.5).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle * 0.5).toFloat()
    w = cos
    x = sin
    y = 0.0f
    z = 0.0f
    return this
  }

  /**
   * Set this quaternion to represent a rotation of the given radians about the y axis.
   *
   * @param angle
   * the angle in radians to rotate about the y axis
   * @return this
   */
  fun rotationY(angle: Float): Quaternionf {
    val sin = Math.sin(angle * 0.5).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle * 0.5).toFloat()
    w = cos
    x = 0.0f
    y = sin
    z = 0.0f
    return this
  }

  /**
   * Set this quaternion to represent a rotation of the given radians about the z axis.
   *
   * @param angle
   * the angle in radians to rotate about the z axis
   * @return this
   */
  fun rotationZ(angle: Float): Quaternionf {
    val sin = Math.sin(angle * 0.5).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle * 0.5).toFloat()
    w = cos
    x = 0.0f
    y = 0.0f
    z = sin
    return this
  }

  private fun setFromUnnormalized(m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float, m20: Float, m21: Float, m22: Float) {
    var nm00 = m00
    var nm01 = m01
    var nm02 = m02
    var nm10 = m10
    var nm11 = m11
    var nm12 = m12
    var nm20 = m20
    var nm21 = m21
    var nm22 = m22
    val lenX = (1.0 / Math.sqrt((m00 * m00 + m01 * m01 + m02 * m02).toDouble())).toFloat()
    val lenY = (1.0 / Math.sqrt((m10 * m10 + m11 * m11 + m12 * m12).toDouble())).toFloat()
    val lenZ = (1.0 / Math.sqrt((m20 * m20 + m21 * m21 + m22 * m22).toDouble())).toFloat()
    nm00 *= lenX
    nm01 *= lenX
    nm02 *= lenX
    nm10 *= lenY
    nm11 *= lenY
    nm12 *= lenY
    nm20 *= lenZ
    nm21 *= lenZ
    nm22 *= lenZ
    setFromNormalized(nm00, nm01, nm02, nm10, nm11, nm12, nm20, nm21, nm22)
  }

  private fun setFromNormalized(m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float, m20: Float, m21: Float, m22: Float) {
    var t: Float
    val tr = m00 + m11 + m22
    if (tr >= 0.0f) {
      t = Math.sqrt((tr + 1.0f).toDouble()).toFloat()
      w = t * 0.5f
      t = 0.5f / t
      x = (m12 - m21) * t
      y = (m20 - m02) * t
      z = (m01 - m10) * t
    } else {
      if (m00 >= m11 && m00 >= m22) {
        t = Math.sqrt(m00 - (m11 + m22) + 1.0).toFloat()
        x = t * 0.5f
        t = 0.5f / t
        y = (m10 + m01) * t
        z = (m02 + m20) * t
        w = (m12 - m21) * t
      } else if (m11 > m22) {
        t = Math.sqrt(m11 - (m22 + m00) + 1.0).toFloat()
        y = t * 0.5f
        t = 0.5f / t
        z = (m21 + m12) * t
        x = (m10 + m01) * t
        w = (m20 - m02) * t
      } else {
        t = Math.sqrt(m22 - (m00 + m11) + 1.0).toFloat()
        z = t * 0.5f
        t = 0.5f / t
        x = (m02 + m20) * t
        y = (m21 + m12) * t
        w = (m01 - m10) * t
      }
    }
  }

  private fun setFromUnnormalized(m00: Double, m01: Double, m02: Double, m10: Double, m11: Double, m12: Double, m20: Double, m21: Double, m22: Double) {
    var nm00 = m00
    var nm01 = m01
    var nm02 = m02
    var nm10 = m10
    var nm11 = m11
    var nm12 = m12
    var nm20 = m20
    var nm21 = m21
    var nm22 = m22
    val lenX = 1.0 / Math.sqrt(m00 * m00 + m01 * m01 + m02 * m02)
    val lenY = 1.0 / Math.sqrt(m10 * m10 + m11 * m11 + m12 * m12)
    val lenZ = 1.0 / Math.sqrt(m20 * m20 + m21 * m21 + m22 * m22)
    nm00 *= lenX
    nm01 *= lenX
    nm02 *= lenX
    nm10 *= lenY
    nm11 *= lenY
    nm12 *= lenY
    nm20 *= lenZ
    nm21 *= lenZ
    nm22 *= lenZ
    setFromNormalized(nm00, nm01, nm02, nm10, nm11, nm12, nm20, nm21, nm22)
  }

  private fun setFromNormalized(m00: Double, m01: Double, m02: Double, m10: Double, m11: Double, m12: Double, m20: Double, m21: Double, m22: Double) {
    var t: Double
    val tr = m00 + m11 + m22
    if (tr >= 0.0) {
      t = Math.sqrt(tr + 1.0)
      w = (t * 0.5).toFloat()
      t = 0.5 / t
      x = ((m12 - m21) * t).toFloat()
      y = ((m20 - m02) * t).toFloat()
      z = ((m01 - m10) * t).toFloat()
    } else {
      if (m00 >= m11 && m00 >= m22) {
        t = Math.sqrt(m00 - (m11 + m22) + 1.0)
        x = (t * 0.5).toFloat()
        t = 0.5 / t
        y = ((m10 + m01) * t).toFloat()
        z = ((m02 + m20) * t).toFloat()
        w = ((m12 - m21) * t).toFloat()
      } else if (m11 > m22) {
        t = Math.sqrt(m11 - (m22 + m00) + 1.0).toFloat().toDouble()
        y = (t * 0.5).toFloat()
        t = 0.5 / t
        z = ((m21 + m12) * t).toFloat()
        x = ((m10 + m01) * t).toFloat()
        w = ((m20 - m02) * t).toFloat()
      } else {
        t = Math.sqrt(m22 - (m00 + m11) + 1.0).toFloat().toDouble()
        z = (t * 0.5).toFloat()
        t = 0.5 / t
        x = ((m02 + m20) * t).toFloat()
        y = ((m21 + m12) * t).toFloat()
        w = ((m01 - m10) * t).toFloat()
      }
    }
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   *
   * This method assumes that the first three columns of the upper left 3x3 submatrix are no unit vectors.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromUnnormalized(mat: Matrix4fc): Quaternionf {
    setFromUnnormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   *
   * This method assumes that the first three columns of the upper left 3x3 submatrix are no unit vectors.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromUnnormalized(mat: Matrix4x3fc): Quaternionf {
    setFromUnnormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   *
   * This method assumes that the first three columns of the upper left 3x3 submatrix are no unit vectors.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromUnnormalized(mat: Matrix4x3dc): Quaternionf {
    setFromUnnormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   *
   * This method assumes that the first three columns of the upper left 3x3 submatrix are unit vectors.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromNormalized(mat: Matrix4fc): Quaternionf {
    setFromNormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   *
   * This method assumes that the first three columns of the upper left 3x3 submatrix are unit vectors.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromNormalized(mat: Matrix4x3fc): Quaternionf {
    setFromNormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   *
   * This method assumes that the first three columns of the upper left 3x3 submatrix are unit vectors.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromNormalized(mat: Matrix4x3dc): Quaternionf {
    setFromNormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   *
   * This method assumes that the first three columns of the upper left 3x3 submatrix are no unit vectors.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromUnnormalized(mat: Matrix4dc): Quaternionf {
    setFromUnnormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   *
   * This method assumes that the first three columns of the upper left 3x3 submatrix are unit vectors.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromNormalized(mat: Matrix4dc): Quaternionf {
    setFromNormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   *
   * This method assumes that the first three columns of the upper left 3x3 submatrix are no unit vectors.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromUnnormalized(mat: Matrix3fc): Quaternionf {
    setFromUnnormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   *
   * This method assumes that the first three columns of the upper left 3x3 submatrix are unit vectors.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromNormalized(mat: Matrix3fc): Quaternionf {
    setFromNormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   *
   * This method assumes that the first three columns of the upper left 3x3 submatrix are no unit vectors.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromUnnormalized(mat: Matrix3dc): Quaternionf {
    setFromUnnormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the rotational component of the given matrix.
   *
   * @param mat
   * the matrix whose rotational component is used to set this quaternion
   * @return this
   */
  fun setFromNormalized(mat: Matrix3dc): Quaternionf {
    setFromNormalized(mat.m00(), mat.m01(), mat.m02(), mat.m10(), mat.m11(), mat.m12(), mat.m20(), mat.m21(), mat.m22())
    return this
  }

  /**
   * Set this quaternion to be a representation of the supplied axis and
   * angle (in radians).
   *
   * @param axis
   * the rotation axis
   * @param angle
   * the angle in radians
   * @return this
   */
  fun fromAxisAngleRad(axis: Vector3fc, angle: Float): Quaternionf {
    return fromAxisAngleRad(axis.x, axis.y, axis.z, angle)
  }

  /**
   * Set this quaternion to be a representation of the supplied axis and
   * angle (in radians).
   *
   * @param axisX
   * the x component of the rotation axis
   * @param axisY
   * the y component of the rotation axis
   * @param axisZ
   * the z component of the rotation axis
   * @param angle
   * the angle in radians
   * @return this
   */
  fun fromAxisAngleRad(axisX: Float, axisY: Float, axisZ: Float, angle: Float): Quaternionf {
    val hangle = angle / 2.0f
    val sinAngle = Math.sin(hangle.toDouble()).toFloat()
    val vLength = Math.sqrt((axisX * axisX + axisY * axisY + axisZ * axisZ).toDouble()).toFloat()
    x = axisX / vLength * sinAngle
    y = axisY / vLength * sinAngle
    z = axisZ / vLength * sinAngle
    w = Math.cosFromSin(sinAngle.toDouble(), hangle.toDouble()).toFloat()
    return this
  }

  /**
   * Set this quaternion to be a representation of the supplied axis and
   * angle (in degrees).
   *
   * @param axis
   * the rotation axis
   * @param angle
   * the angle in degrees
   * @return this
   */
  fun fromAxisAngleDeg(axis: Vector3fc, angle: Float): Quaternionf {
    return fromAxisAngleRad(axis.x, axis.y, axis.z, Math.toRadians(angle.toDouble()).toFloat())
  }

  /**
   * Set this quaternion to be a representation of the supplied axis and
   * angle (in degrees).
   *
   * @param axisX
   * the x component of the rotation axis
   * @param axisY
   * the y component of the rotation axis
   * @param axisZ
   * the z component of the rotation axis
   * @param angle
   * the angle in radians
   * @return this
   */
  fun fromAxisAngleDeg(axisX: Float, axisY: Float, axisZ: Float, angle: Float): Quaternionf {
    return fromAxisAngleRad(axisX, axisY, axisZ, Math.toRadians(angle.toDouble()).toFloat())
  }

  /**
   * Multiply this quaternion by `q`.
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
   * @return this
   */
  fun mul(q: Quaternionfc): Quaternionf {
    return mul(q, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#mul(Quaternionfc, Quaternionf)
     */
  override fun mul(q: Quaternionfc, dest: Quaternionf): Quaternionf {
    dest[w * q.x + x * q.w + y * q.z - z * q.y, w * q.y - x * q.z + y * q.w + z * q.x, w * q.z + x * q.y - y * q.x + z * q.w] = w * q.w - x * q.x - y * q.y - z * q.z
    return dest
  }

  /**
   * Multiply this quaternion by the quaternion represented via <tt>(qx, qy, qz, qw)</tt>.
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
   * @return this
   */
  fun mul(qx: Float, qy: Float, qz: Float, qw: Float): Quaternionf {
    set(w * qx + x * qw + y * qz - z * qy,
        w * qy - x * qz + y * qw + z * qx,
        w * qz + x * qy - y * qx + z * qw,
        w * qw - x * qx - y * qy - z * qz)
    return this
  }

  /* (non-Javadoc)
     * @see Quaternionfc#mul(float, float, float, float, Quaternionf)
     */
  override fun mul(qx: Float, qy: Float, qz: Float, qw: Float, dest: Quaternionf): Quaternionf {
    dest[w * qx + x * qw + y * qz - z * qy, w * qy - x * qz + y * qw + z * qx, w * qz + x * qy - y * qx + z * qw] = w * qw - x * qx - y * qy - z * qz
    return dest
  }

  /**
   * Pre-multiply this quaternion by `q`.
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
   * @return this
   */
  fun premul(q: Quaternionfc): Quaternionf {
    return premul(q, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#premul(Quaternionfc, Quaternionf)
     */
  override fun premul(q: Quaternionfc, dest: Quaternionf): Quaternionf {
    dest[q.w * x + q.x * w + q.y * z - q.z * y, q.w * y - q.x * z + q.y * w + q.z * x, q.w * z + q.x * y - q.y * x + q.z * w] = q.w * w - q.x * x - q.y * y - q.z * z
    return dest
  }

  /**
   * Pre-multiply this quaternion by the quaternion represented via <tt>(qx, qy, qz, qw)</tt>.
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
   * @return this
   */
  fun premul(qx: Float, qy: Float, qz: Float, qw: Float): Quaternionf {
    return premul(qx, qy, qz, qw, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#premul(float, float, float, float, Quaternionf)
     */
  override fun premul(qx: Float, qy: Float, qz: Float, qw: Float, dest: Quaternionf): Quaternionf {
    dest[qw * x + qx * w + qy * z - qz * y, qw * y - qx * z + qy * w + qz * x, qw * z + qx * y - qy * x + qz * w] = qw * w - qx * x - qy * y - qz * z
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#transform(Vector3m)
     */
  override fun transform(vec: Vector3m): Vector3m {
    return transform(vec.x, vec.y, vec.z, vec)
  }

  override fun transformPositiveX(dest: Vector3m): Vector3m {
    val w2 = this.w * this.w
    val x2 = this.x * this.x
    val y2 = this.y * this.y
    val z2 = this.z * this.z
    val zw = this.z * this.w
    val xy = this.x * this.y
    val xz = this.x * this.z
    val yw = this.y * this.w
    dest.x = w2 + x2 - z2 - y2
    dest.y = xy + zw + zw + xy
    dest.z = xz - yw + xz - yw
    return dest
  }

  override fun transformPositiveX(dest: Vector4f): Vector4f {
    val w2 = this.w * this.w
    val x2 = this.x * this.x
    val y2 = this.y * this.y
    val z2 = this.z * this.z
    val zw = this.z * this.w
    val xy = this.x * this.y
    val xz = this.x * this.z
    val yw = this.y * this.w
    dest.x = w2 + x2 - z2 - y2
    dest.y = xy + zw + zw + xy
    dest.z = xz - yw + xz - yw
    return dest
  }

  override fun transformUnitPositiveX(dest: Vector3m): Vector3m {
    val y2 = y * y
    val z2 = z * z
    val xy = x * y
    val xz = x * z
    val yw = y * w
    val zw = z * w
    dest.x = 1.0f - y2 - y2 - z2 - z2
    dest.y = xy + zw + xy + zw
    dest.z = xz - yw + xz - yw
    return dest
  }

  override fun transformUnitPositiveX(dest: Vector4f): Vector4f {
    val y2 = y * y
    val z2 = z * z
    val xy = x * y
    val xz = x * z
    val yw = y * w
    val zw = z * w
    dest.x = 1.0f - y2 - y2 - z2 - z2
    dest.y = xy + zw + xy + zw
    dest.z = xz - yw + xz - yw
    return dest
  }

  override fun transformPositiveY(dest: Vector3m): Vector3m {
    val w2 = this.w * this.w
    val x2 = this.x * this.x
    val y2 = this.y * this.y
    val z2 = this.z * this.z
    val zw = this.z * this.w
    val xy = this.x * this.y
    val yz = this.y * this.z
    val xw = this.x * this.w
    dest.x = -zw + xy - zw + xy
    dest.y = y2 - z2 + w2 - x2
    dest.z = yz + yz + xw + xw
    return dest
  }

  override fun transformPositiveY(dest: Vector4f): Vector4f {
    val w2 = this.w * this.w
    val x2 = this.x * this.x
    val y2 = this.y * this.y
    val z2 = this.z * this.z
    val zw = this.z * this.w
    val xy = this.x * this.y
    val yz = this.y * this.z
    val xw = this.x * this.w
    dest.x = -zw + xy - zw + xy
    dest.y = y2 - z2 + w2 - x2
    dest.z = yz + yz + xw + xw
    return dest
  }

  override fun transformUnitPositiveY(dest: Vector4f): Vector4f {
    val x2 = x * x
    val z2 = z * z
    val xy = x * y
    val yz = y * z
    val xw = x * w
    val zw = z * w
    dest.x = xy - zw + xy - zw
    dest.y = 1.0f - x2 - x2 - z2 - z2
    dest.z = yz + yz + xw + xw
    return dest
  }

  override fun transformUnitPositiveY(dest: Vector3m): Vector3m {
    val x2 = x * x
    val z2 = z * z
    val xy = x * y
    val yz = y * z
    val xw = x * w
    val zw = z * w
    dest.x = xy - zw + xy - zw
    dest.y = 1.0f - x2 - x2 - z2 - z2
    dest.z = yz + yz + xw + xw
    return dest
  }

  override fun transformPositiveZ(dest: Vector3m): Vector3m {
    val w2 = this.w * this.w
    val x2 = this.x * this.x
    val y2 = this.y * this.y
    val z2 = this.z * this.z
    val xz = this.x * this.z
    val yw = this.y * this.w
    val yz = this.y * this.z
    val xw = this.x * this.w
    dest.x = yw + xz + xz + yw
    dest.y = yz + yz - xw - xw
    dest.z = z2 - y2 - x2 + w2
    return dest
  }

  override fun transformPositiveZ(dest: Vector4f): Vector4f {
    val w2 = this.w * this.w
    val x2 = this.x * this.x
    val y2 = this.y * this.y
    val z2 = this.z * this.z
    val xz = this.x * this.z
    val yw = this.y * this.w
    val yz = this.y * this.z
    val xw = this.x * this.w
    dest.x = yw + xz + xz + yw
    dest.y = yz + yz - xw - xw
    dest.z = z2 - y2 - x2 + w2
    return dest
  }

  override fun transformUnitPositiveZ(dest: Vector4f): Vector4f {
    val x2 = x * x
    val y2 = y * y
    val xz = x * z
    val yz = y * z
    val xw = x * w
    val yw = y * w
    dest.x = xz + yw + xz + yw
    dest.y = yz + yz - xw - xw
    dest.z = 1.0f - x2 - x2 - y2 - y2
    return dest
  }

  override fun transformUnitPositiveZ(dest: Vector3m): Vector3m {
    val x2 = x * x
    val y2 = y * y
    val xz = x * z
    val yz = y * z
    val xw = x * w
    val yw = y * w
    dest.x = xz + yw + xz + yw
    dest.y = yz + yz - xw - xw
    dest.z = 1.0f - x2 - x2 - y2 - y2
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#transform(Vector4f)
     */
  override fun transform(vec: Vector4f): Vector4f {
    return transform(vec, vec)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#transform(Vector3fc, Vector3m)
     */
  override fun transform(vec: Vector3fc, dest: Vector3m): Vector3m {
    return transform(vec.x, vec.y, vec.z, dest)
  }

  fun transform(vec: Vector3, dest: Vector3m): Vector3m {
    return transform(vec.x, vec.y, vec.z, dest)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#transform(float, float, float, Vector3m)
     */
  override fun transform(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m {
    val w2 = this.w * this.w
    val x2 = this.x * this.x
    val y2 = this.y * this.y
    val z2 = this.z * this.z
    val zw = this.z * this.w
    val xy = this.x * this.y
    val xz = this.x * this.z
    val yw = this.y * this.w
    val yz = this.y * this.z
    val xw = this.x * this.w
    val m00 = w2 + x2 - z2 - y2
    val m01 = xy + zw + zw + xy
    val m02 = xz - yw + xz - yw
    val m10 = -zw + xy - zw + xy
    val m11 = y2 - z2 + w2 - x2
    val m12 = yz + yz + xw + xw
    val m20 = yw + xz + xz + yw
    val m21 = yz + yz - xw - xw
    val m22 = z2 - y2 - x2 + w2
    dest.x = m00 * x + m10 * y + m20 * z
    dest.y = m01 * x + m11 * y + m21 * z
    dest.z = m02 * x + m12 * y + m22 * z
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#transform(double, double, double, org.joml.Vector3d)
     */
  fun transform(x: Double, y: Double, z: Double, dest: Vector3d): Vector3d {
    val w2 = this.w * this.w
    val x2 = this.x * this.x
    val y2 = this.y * this.y
    val z2 = this.z * this.z
    val zw = this.z * this.w
    val xy = this.x * this.y
    val xz = this.x * this.z
    val yw = this.y * this.w
    val yz = this.y * this.z
    val xw = this.x * this.w
    val m00 = w2 + x2 - z2 - y2
    val m01 = xy + zw + zw + xy
    val m02 = xz - yw + xz - yw
    val m10 = -zw + xy - zw + xy
    val m11 = y2 - z2 + w2 - x2
    val m12 = yz + yz + xw + xw
    val m20 = yw + xz + xz + yw
    val m21 = yz + yz - xw - xw
    val m22 = z2 - y2 - x2 + w2
    dest.x = m00 * x + m10 * y + m20 * z
    dest.y = m01 * x + m11 * y + m21 * z
    dest.z = m02 * x + m12 * y + m22 * z
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#transform(Vector4fc, Vector4f)
     */
  override fun transform(vec: Vector4fc, dest: Vector4f): Vector4f {
    return transform(vec.x, vec.y, vec.z, dest)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#transform(float, float, float, Vector4f)
     */
  override fun transform(x: Float, y: Float, z: Float, dest: Vector4f): Vector4f {
    val w2 = this.w * this.w
    val x2 = this.x * this.x
    val y2 = this.y * this.y
    val z2 = this.z * this.z
    val zw = this.z * this.w
    val xy = this.x * this.y
    val xz = this.x * this.z
    val yw = this.y * this.w
    val yz = this.y * this.z
    val xw = this.x * this.w
    val m00 = w2 + x2 - z2 - y2
    val m01 = xy + zw + zw + xy
    val m02 = xz - yw + xz - yw
    val m10 = -zw + xy - zw + xy
    val m11 = y2 - z2 + w2 - x2
    val m12 = yz + yz + xw + xw
    val m20 = yw + xz + xz + yw
    val m21 = yz + yz - xw - xw
    val m22 = z2 - y2 - x2 + w2
    dest.x = m00 * x + m10 * y + m20 * z
    dest.y = m01 * x + m11 * y + m21 * z
    dest.z = m02 * x + m12 * y + m22 * z
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#invert(Quaternionf)
     */
  override fun invert(dest: Quaternionf): Quaternionf {
    val invNorm = 1.0f / (x * x + y * y + z * z + w * w)
    dest.x = -x * invNorm
    dest.y = -y * invNorm
    dest.z = -z * invNorm
    dest.w = w * invNorm
    return dest
  }

  /**
   * Invert this quaternion and [normalize][.normalize] it.
   *
   *
   * If this quaternion is already normalized, then [.conjugate] should be used instead.
   *
   * @see .conjugate
   * @return this
   */
  fun invert(): Quaternionf {
    return invert(this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#div(Quaternionfc, Quaternionf)
     */
  override fun div(b: Quaternionfc, dest: Quaternionf): Quaternionf {
    val invNorm = 1.0f / (b.x * b.x + b.y * b.y + b.z * b.z + b.w * b.w)
    val x = -b.x * invNorm
    val y = -b.y * invNorm
    val z = -b.z * invNorm
    val w = b.w * invNorm
    dest[this.w * x + this.x * w + this.y * z - this.z * y, this.w * y - this.x * z + this.y * w + this.z * x, this.w * z + this.x * y - this.y * x + this.z * w] = this.w * w - this.x * x - this.y * y - this.z * z
    return dest
  }

  /**
   * Divide `this` quaternion by `b`.
   *
   *
   * The division expressed using the inverse is performed in the following way:
   *
   *
   * <tt>this = this * b^-1</tt>, where <tt>b^-1</tt> is the inverse of `b`.
   *
   * @param b
   * the [Quaternionf] to divide this by
   * @return this
   */
  operator fun div(b: Quaternionfc): Quaternionf {
    return div(b, this)
  }

  /**
   * Conjugate this quaternion.
   *
   * @return this
   */
  fun conjugate(): Quaternionf {
    x = -x
    y = -y
    z = -z
    return this
  }

  /* (non-Javadoc)
     * @see Quaternionfc#conjugate(Quaternionf)
     */
  override fun conjugate(dest: Quaternionf): Quaternionf {
    dest.x = -x
    dest.y = -y
    dest.z = -z
    dest.w = w
    return dest
  }

  /**
   * Set this quaternion to the identity.
   *
   * @return this
   */
  fun identity(): Quaternionf {
    MemUtil.INSTANCE.identity(this)
    return this
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the cartesian base unit axes,
   * called the euler angles using rotation sequence <tt>XYZ</tt>.
   *
   *
   * This method is equivalent to calling: <tt>rotateX(angleX).rotateY(angleY).rotateZ(angleZ)</tt>
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
   * @return this
   */
  fun rotateXYZ(angleX: Float, angleY: Float, angleZ: Float): Quaternionf {
    return rotateXYZ(angleX, angleY, angleZ, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateXYZ(float, float, float, Quaternionf)
     */
  override fun rotateXYZ(angleX: Float, angleY: Float, angleZ: Float, dest: Quaternionf): Quaternionf {
    val sx = Math.sin(angleX * 0.5).toFloat()
    val cx = Math.cosFromSin(sx.toDouble(), angleX * 0.5).toFloat()
    val sy = Math.sin(angleY * 0.5).toFloat()
    val cy = Math.cosFromSin(sy.toDouble(), angleY * 0.5).toFloat()
    val sz = Math.sin(angleZ * 0.5).toFloat()
    val cz = Math.cosFromSin(sz.toDouble(), angleZ * 0.5).toFloat()

    val cycz = cy * cz
    val sysz = sy * sz
    val sycz = sy * cz
    val cysz = cy * sz
    val w = cx * cycz - sx * sysz
    val x = sx * cycz + cx * sysz
    val y = cx * sycz - sx * cysz
    val z = cx * cysz + sx * sycz
    // right-multiply
    dest[this.w * x + this.x * w + this.y * z - this.z * y, this.w * y - this.x * z + this.y * w + this.z * x, this.w * z + this.x * y - this.y * x + this.z * w] = this.w * w - this.x * x - this.y * y - this.z * z
    return dest
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the cartesian base unit axes,
   * called the euler angles, using the rotation sequence <tt>ZYX</tt>.
   *
   *
   * This method is equivalent to calling: <tt>rotateZ(angleZ).rotateY(angleY).rotateX(angleX)</tt>
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
   * @return this
   */
  fun rotateZYX(angleZ: Float, angleY: Float, angleX: Float): Quaternionf {
    return rotateZYX(angleZ, angleY, angleX, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateZYX(float, float, float, Quaternionf)
     */
  override fun rotateZYX(angleZ: Float, angleY: Float, angleX: Float, dest: Quaternionf): Quaternionf {
    val sx = Math.sin(angleX * 0.5).toFloat()
    val cx = Math.cosFromSin(sx.toDouble(), angleX * 0.5).toFloat()
    val sy = Math.sin(angleY * 0.5).toFloat()
    val cy = Math.cosFromSin(sy.toDouble(), angleY * 0.5).toFloat()
    val sz = Math.sin(angleZ * 0.5).toFloat()
    val cz = Math.cosFromSin(sz.toDouble(), angleZ * 0.5).toFloat()

    val cycz = cy * cz
    val sysz = sy * sz
    val sycz = sy * cz
    val cysz = cy * sz
    val w = cx * cycz + sx * sysz
    val x = sx * cycz - cx * sysz
    val y = cx * sycz + sx * cysz
    val z = cx * cysz - sx * sycz
    // right-multiply
    dest[this.w * x + this.x * w + this.y * z - this.z * y, this.w * y - this.x * z + this.y * w + this.z * x, this.w * z + this.x * y - this.y * x + this.z * w] = this.w * w - this.x * x - this.y * y - this.z * z
    return dest
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the cartesian base unit axes,
   * called the euler angles, using the rotation sequence <tt>YXZ</tt>.
   *
   *
   * This method is equivalent to calling: <tt>rotateY(angleY).rotateX(angleX).rotateZ(angleZ)</tt>
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
   * @return this
   */
  fun rotateYXZ(angleZ: Float, angleY: Float, angleX: Float): Quaternionf {
    return rotateYXZ(angleZ, angleY, angleX, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateYXZ(float, float, float, Quaternionf)
     */
  override fun rotateYXZ(angleY: Float, angleX: Float, angleZ: Float, dest: Quaternionf): Quaternionf {
    val sx = Math.sin(angleX * 0.5).toFloat()
    val cx = Math.cosFromSin(sx.toDouble(), angleX * 0.5).toFloat()
    val sy = Math.sin(angleY * 0.5).toFloat()
    val cy = Math.cosFromSin(sy.toDouble(), angleY * 0.5).toFloat()
    val sz = Math.sin(angleZ * 0.5).toFloat()
    val cz = Math.cosFromSin(sz.toDouble(), angleZ * 0.5).toFloat()

    val yx = cy * sx
    val yy = sy * cx
    val yz = sy * sx
    val yw = cy * cx
    val x = yx * cz + yy * sz
    val y = yy * cz - yx * sz
    val z = yw * sz - yz * cz
    val w = yw * cz + yz * sz
    // right-multiply
    dest[this.w * x + this.x * w + this.y * z - this.z * y, this.w * y - this.x * z + this.y * w + this.z * x, this.w * z + this.x * y - this.y * x + this.z * w] = this.w * w - this.x * x - this.y * y - this.z * z
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#getEulerAnglesXYZ(Vector3m)
     */
  override fun getEulerAnglesXYZ(eulerAngles: Vector3m): Vector3m {
    eulerAngles.x = Math.atan2(2.0 * (x * w - y * z), 1.0 - 2.0 * (x * x + y * y)).toFloat()
    eulerAngles.y = Math.asin(2.0 * (x * z + y * w)).toFloat()
    eulerAngles.z = Math.atan2(2.0 * (z * w - x * y), 1.0 - 2.0 * (y * y + z * z)).toFloat()
    return eulerAngles
  }

  /* (non-Javadoc)
     * @see Quaternionfc#lengthSquared()
     */
  override fun lengthSquared(): Float {
    return x * x + y * y + z * z + w * w
  }

  /**
   * Set this quaternion from the supplied euler angles (in radians) with rotation order XYZ.
   *
   *
   * This method is equivalent to calling: <tt>rotationX(angleX).rotateY(angleY).rotateZ(angleZ)</tt>
   *
   *
   * Reference: [this stackexchange answer](http://gamedev.stackexchange.com/questions/13436/glm-euler-angles-to-quaternion#answer-13446)
   *
   * @param angleX
   * the angle in radians to rotate about x
   * @param angleY
   * the angle in radians to rotate about y
   * @param angleZ
   * the angle in radians to rotate about z
   * @return this
   */
  fun rotationXYZ(angleX: Float, angleY: Float, angleZ: Float): Quaternionf {
    val sx = Math.sin(angleX * 0.5).toFloat()
    val cx = Math.cosFromSin(sx.toDouble(), angleX * 0.5).toFloat()
    val sy = Math.sin(angleY * 0.5).toFloat()
    val cy = Math.cosFromSin(sy.toDouble(), angleY * 0.5).toFloat()
    val sz = Math.sin(angleZ * 0.5).toFloat()
    val cz = Math.cosFromSin(sz.toDouble(), angleZ * 0.5).toFloat()

    val cycz = cy * cz
    val sysz = sy * sz
    val sycz = sy * cz
    val cysz = cy * sz
    w = cx * cycz - sx * sysz
    x = sx * cycz + cx * sysz
    y = cx * sycz - sx * cysz
    z = cx * cysz + sx * sycz

    return this
  }

  /**
   * Set this quaternion from the supplied euler angles (in radians) with rotation order ZYX.
   *
   *
   * This method is equivalent to calling: <tt>rotationZ(angleZ).rotateY(angleY).rotateX(angleX)</tt>
   *
   *
   * Reference: [this stackexchange answer](http://gamedev.stackexchange.com/questions/13436/glm-euler-angles-to-quaternion#answer-13446)
   *
   * @param angleX
   * the angle in radians to rotate about x
   * @param angleY
   * the angle in radians to rotate about y
   * @param angleZ
   * the angle in radians to rotate about z
   * @return this
   */
  fun rotationZYX(angleZ: Float, angleY: Float, angleX: Float): Quaternionf {
    val sx = Math.sin(angleX * 0.5).toFloat()
    val cx = Math.cosFromSin(sx.toDouble(), angleX * 0.5).toFloat()
    val sy = Math.sin(angleY * 0.5).toFloat()
    val cy = Math.cosFromSin(sy.toDouble(), angleY * 0.5).toFloat()
    val sz = Math.sin(angleZ * 0.5).toFloat()
    val cz = Math.cosFromSin(sz.toDouble(), angleZ * 0.5).toFloat()

    val cycz = cy * cz
    val sysz = sy * sz
    val sycz = sy * cz
    val cysz = cy * sz
    w = cx * cycz + sx * sysz
    x = sx * cycz - cx * sysz
    y = cx * sycz + sx * cysz
    z = cx * cysz - sx * sycz

    return this
  }

  /**
   * Set this quaternion from the supplied euler angles (in radians) with rotation order YXZ.
   *
   *
   * This method is equivalent to calling: <tt>rotationY(angleY).rotateX(angleX).rotateZ(angleZ)</tt>
   *
   *
   * Reference: [https://en.wikipedia.org](https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles)
   *
   * @param angleY
   * the angle in radians to rotate about y
   * @param angleX
   * the angle in radians to rotate about x
   * @param angleZ
   * the angle in radians to rotate about z
   * @return this
   */
  fun rotationYXZ(angleY: Float, angleX: Float, angleZ: Float): Quaternionf {
    val sx = Math.sin(angleX * 0.5).toFloat()
    val cx = Math.cosFromSin(sx.toDouble(), angleX * 0.5).toFloat()
    val sy = Math.sin(angleY * 0.5).toFloat()
    val cy = Math.cosFromSin(sy.toDouble(), angleY * 0.5).toFloat()
    val sz = Math.sin(angleZ * 0.5).toFloat()
    val cz = Math.cosFromSin(sz.toDouble(), angleZ * 0.5).toFloat()

    val x = cy * sx
    val y = sy * cx
    val z = sy * sx
    val w = cy * cx
    this.x = x * cz + y * sz
    this.y = y * cz - x * sz
    this.z = w * sz - z * cz
    this.w = w * cz + z * sz

    return this
  }

  /**
   * Interpolate between `this` [unit][.normalize] quaternion and the specified
   * `target` [unit][.normalize] quaternion using spherical linear interpolation using the specified interpolation factor `alpha`.
   *
   *
   * This method resorts to non-spherical linear interpolation when the absolute dot product of `this` and `target` is
   * below <tt>1E-6f</tt>.
   *
   * @param target
   * the target of the interpolation, which should be reached with <tt>alpha = 1.0</tt>
   * @param alpha
   * the interpolation factor, within <tt>[0..1]</tt>
   * @return this
   */
  fun slerp(target: Quaternionfc, alpha: Float): Quaternionf {
    return slerp(target, alpha, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#slerp(Quaternionfc, float, Quaternionf)
     */
  override fun slerp(target: Quaternionfc, alpha: Float, dest: Quaternionf): Quaternionf {
    val cosom = x * target.x + y * target.y + z * target.z + w * target.w
    val absCosom = Math.abs(cosom)
    val scale0: Float
    var scale1: Float
    if (1.0f - absCosom > 1E-6f) {
      val sinSqr = 1.0f - absCosom * absCosom
      val sinom = (1.0 / Math.sqrt(sinSqr.toDouble())).toFloat()
      val omega = Math.atan2((sinSqr * sinom).toDouble(), absCosom.toDouble()).toFloat()
      scale0 = (Math.sin((1.0 - alpha) * omega) * sinom).toFloat()
      scale1 = (Math.sin((alpha * omega).toDouble()) * sinom).toFloat()
    } else {
      scale0 = 1.0f - alpha
      scale1 = alpha
    }
    scale1 = if (cosom >= 0.0f) scale1 else -scale1
    dest.x = scale0 * x + scale1 * target.x
    dest.y = scale0 * y + scale1 * target.y
    dest.z = scale0 * z + scale1 * target.z
    dest.w = scale0 * w + scale1 * target.w
    return dest
  }

  /**
   * Apply scaling to this quaternion, which results in any vector transformed by this quaternion to change
   * its length by the given `factor`.
   *
   * @param factor
   * the scaling factor
   * @return this
   */
  fun scale(factor: Float): Quaternionf {
    return scale(factor, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#scale(float, Quaternionf)
     */
  override fun scale(factor: Float, dest: Quaternionf): Quaternionf {
    val sqrt = Math.sqrt(factor.toDouble()).toFloat()
    dest.x = sqrt * x
    dest.y = sqrt * y
    dest.z = sqrt * z
    dest.w = sqrt * w
    return this
  }

  /**
   * Set this quaternion to represent scaling, which results in a transformed vector to change
   * its length by the given `factor`.
   *
   * @param factor
   * the scaling factor
   * @return this
   */
  fun scaling(factor: Float): Quaternionf {
    val sqrt = Math.sqrt(factor.toDouble()).toFloat()
    this.x = 0.0f
    this.y = 0.0f
    this.z = 0.0f
    this.w = sqrt
    return this
  }

  /**
   * Integrate the rotation given by the angular velocity `(vx, vy, vz)` around the x, y and z axis, respectively,
   * with respect to the given elapsed time delta `dt` and add the differentiate rotation to the rotation represented by this quaternion.
   *
   *
   * This method pre-multiplies the rotation given by `dt` and `(vx, vy, vz)` by `this`, so
   * the angular velocities are always relative to the local coordinate system of the rotation represented by `this` quaternion.
   *
   *
   * This method is equivalent to calling: `rotateLocal(dt * vx, dt * vy, dt * vz)`
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
   * @return this
   */
  fun integrate(dt: Float, vx: Float, vy: Float, vz: Float): Quaternionf {
    return integrate(dt, vx, vy, vz, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#integrate(float, float, float, float, Quaternionf)
     */
  override fun integrate(dt: Float, vx: Float, vy: Float, vz: Float, dest: Quaternionf): Quaternionf {
    return rotateLocal(dt * vx, dt * vy, dt * vz, dest)
  }

  /**
   * Compute a linear (non-spherical) interpolation of `this` and the given quaternion `q`
   * and store the result in `this`.
   *
   * @param q
   * the other quaternion
   * @param factor
   * the interpolation factor. It is between 0.0 and 1.0
   * @return this
   */
  fun nlerp(q: Quaternionfc, factor: Float): Quaternionf {
    return nlerp(q, factor, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#nlerp(Quaternionfc, float, Quaternionf)
     */
  override fun nlerp(q: Quaternionfc, factor: Float, dest: Quaternionf): Quaternionf {
    val cosom = x * q.x + y * q.y + z * q.z + w * q.w
    val scale0 = 1.0f - factor
    val scale1 = if (cosom >= 0.0f) factor else -factor
    dest.x = scale0 * x + scale1 * q.x
    dest.y = scale0 * y + scale1 * q.y
    dest.z = scale0 * z + scale1 * q.z
    dest.w = scale0 * w + scale1 * q.w
    val s = (1.0 / Math.sqrt((dest.x * dest.x + dest.y * dest.y + dest.z * dest.z + dest.w * dest.w).toDouble())).toFloat()
    dest.x *= s
    dest.y *= s
    dest.z *= s
    dest.w *= s
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#nlerpIterative(Quaternionfc, float, float, Quaternionf)
     */
  override fun nlerpIterative(q: Quaternionfc, alpha: Float, dotThreshold: Float, dest: Quaternionf): Quaternionf {
    var q1x = x
    var q1y = y
    var q1z = z
    var q1w = w
    var q2x = q.x
    var q2y = q.y
    var q2z = q.z
    var q2w = q.w
    var dot = q1x * q2x + q1y * q2y + q1z * q2z + q1w * q2w
    var absDot = Math.abs(dot)
    if (1.0f - 1E-6f < absDot) {
      return dest.set(this)
    }
    var alphaN = alpha
    while (absDot < dotThreshold) {
      val scale0 = 0.5f
      val scale1 = if (dot >= 0.0f) 0.5f else -0.5f
      if (alphaN < 0.5f) {
        q2x = scale0 * q2x + scale1 * q1x
        q2y = scale0 * q2y + scale1 * q1y
        q2z = scale0 * q2z + scale1 * q1z
        q2w = scale0 * q2w + scale1 * q1w
        val s = (1.0 / Math.sqrt((q2x * q2x + q2y * q2y + q2z * q2z + q2w * q2w).toDouble())).toFloat()
        q2x *= s
        q2y *= s
        q2z *= s
        q2w *= s
        alphaN = alphaN + alphaN
      } else {
        q1x = scale0 * q1x + scale1 * q2x
        q1y = scale0 * q1y + scale1 * q2y
        q1z = scale0 * q1z + scale1 * q2z
        q1w = scale0 * q1w + scale1 * q2w
        val s = (1.0 / Math.sqrt((q1x * q1x + q1y * q1y + q1z * q1z + q1w * q1w).toDouble())).toFloat()
        q1x *= s
        q1y *= s
        q1z *= s
        q1w *= s
        alphaN = alphaN + alphaN - 1.0f
      }
      dot = q1x * q2x + q1y * q2y + q1z * q2z + q1w * q2w
      absDot = Math.abs(dot)
    }
    val scale0 = 1.0f - alphaN
    val scale1 = if (dot >= 0.0f) alphaN else -alphaN
    val resX = scale0 * q1x + scale1 * q2x
    val resY = scale0 * q1y + scale1 * q2y
    val resZ = scale0 * q1z + scale1 * q2z
    val resW = scale0 * q1w + scale1 * q2w
    val s = (1.0 / Math.sqrt((resX * resX + resY * resY + resZ * resZ + resW * resW).toDouble())).toFloat()
    dest.x = resX * s
    dest.y = resY * s
    dest.z = resZ * s
    dest.w = resW * s
    return dest
  }

  /**
   * Compute linear (non-spherical) interpolations of `this` and the given quaternion `q`
   * iteratively and store the result in `this`.
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
   * @return this
   */
  fun nlerpIterative(q: Quaternionfc, alpha: Float, dotThreshold: Float): Quaternionf {
    return nlerpIterative(q, alpha, dotThreshold, this)
  }

  /**
   * Apply a rotation to this quaternion that maps the given direction to the positive Z axis.
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
   * @return this
   */
  fun lookAlong(dir: Vector3fc, up: Vector3fc): Quaternionf {
    return lookAlong(dir.x, dir.y, dir.z, up.x, up.y, up.z, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#lookAlong(Vector3fc, Vector3fc, Quaternionf)
     */
  override fun lookAlong(dir: Vector3fc, up: Vector3fc, dest: Quaternionf): Quaternionf {
    return lookAlong(dir.x, dir.y, dir.z, up.x, up.y, up.z, dest)
  }

  /**
   * Apply a rotation to this quaternion that maps the given direction to the positive Z axis.
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
   * @see .lookAlong
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
   * @return this
   */
  fun lookAlong(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float): Quaternionf {
    return lookAlong(dirX, dirY, dirZ, upX, upY, upZ, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#lookAlong(float, float, float, float, float, float, Quaternionf)
     */
  override fun lookAlong(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float, dest: Quaternionf): Quaternionf {
    // Normalize direction
    val invDirLength = (1.0 / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble())).toFloat()
    val dirnX = -dirX * invDirLength
    val dirnY = -dirY * invDirLength
    val dirnZ = -dirZ * invDirLength
    // left = up x dir
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * dirnZ - upZ * dirnY
    leftY = upZ * dirnX - upX * dirnZ
    leftZ = upX * dirnY - upY * dirnX
    // normalize left
    val invLeftLength = (1.0 / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble())).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = dirnY * leftZ - dirnZ * leftY
    val upnY = dirnZ * leftX - dirnX * leftZ
    val upnZ = dirnX * leftY - dirnY * leftX

    /* Convert orthonormal basis vectors to quaternion */
    val x: Float
    val y: Float
    val z: Float
    val w: Float
    var t: Double
    val tr = (leftX + upnY + dirnZ).toDouble()
    if (tr >= 0.0) {
      t = Math.sqrt(tr + 1.0)
      w = (t * 0.5).toFloat()
      t = 0.5 / t
      x = ((dirnY - upnZ) * t).toFloat()
      y = ((leftZ - dirnX) * t).toFloat()
      z = ((upnX - leftY) * t).toFloat()
    } else {
      if (leftX > upnY && leftX > dirnZ) {
        t = Math.sqrt(1.0 + leftX - upnY.toDouble() - dirnZ.toDouble())
        x = (t * 0.5).toFloat()
        t = 0.5 / t
        y = ((leftY + upnX) * t).toFloat()
        z = ((dirnX + leftZ) * t).toFloat()
        w = ((dirnY - upnZ) * t).toFloat()
      } else if (upnY > dirnZ) {
        t = Math.sqrt(1.0 + upnY - leftX.toDouble() - dirnZ.toDouble())
        y = (t * 0.5).toFloat()
        t = 0.5 / t
        x = ((leftY + upnX) * t).toFloat()
        z = ((upnZ + dirnY) * t).toFloat()
        w = ((leftZ - dirnX) * t).toFloat()
      } else {
        t = Math.sqrt(1.0 + dirnZ - leftX.toDouble() - upnY.toDouble())
        z = (t * 0.5).toFloat()
        t = 0.5 / t
        x = ((dirnX + leftZ) * t).toFloat()
        y = ((upnZ + dirnY) * t).toFloat()
        w = ((upnX - leftY) * t).toFloat()
      }
    }
    /* Multiply */
    dest[this.w * x + this.x * w + this.y * z - this.z * y, this.w * y - this.x * z + this.y * w + this.z * x, this.w * z + this.x * y - this.y * x + this.z * w] = this.w * w - this.x * x - this.y * y - this.z * z
    return dest
  }

  /**
   * Set `this` quaternion to a rotation that rotates the <tt>fromDir</tt> vector to point along <tt>toDir</tt>.
   *
   *
   * Since there can be multiple possible rotations, this method chooses the one with the shortest arc.
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
   * @return this
   */
  fun rotationTo(fromDirX: Float, fromDirY: Float, fromDirZ: Float, toDirX: Float, toDirY: Float, toDirZ: Float): Quaternionf {
    x = fromDirY * toDirZ - fromDirZ * toDirY
    y = fromDirZ * toDirX - fromDirX * toDirZ
    z = fromDirX * toDirY - fromDirY * toDirX
    w = Math.sqrt(((fromDirX * fromDirX + fromDirY * fromDirY + fromDirZ * fromDirZ) * (toDirX * toDirX + toDirY * toDirY + toDirZ * toDirZ)).toDouble()).toFloat() + (fromDirX * toDirX + fromDirY * toDirY + fromDirZ * toDirZ)
    var invNorm = (1.0 / Math.sqrt((x * x + y * y + z * z + w * w).toDouble())).toFloat()
    if (java.lang.Float.isInfinite(invNorm)) {
      // Rotation is ambiguous: Find appropriate rotation axis (1. try toDir x +Z)
      x = toDirY
      y = -toDirX
      z = 0.0f
      w = 0.0f
      invNorm = (1.0 / Math.sqrt((x * x + y * y).toDouble())).toFloat()
      if (java.lang.Float.isInfinite(invNorm)) {
        // 2. try toDir x +X
        x = 0.0f
        y = toDirZ
        z = -toDirY
        w = 0.0f
        invNorm = (1.0 / Math.sqrt((y * y + z * z).toDouble())).toFloat()
      }
    }
    x *= invNorm
    y *= invNorm
    z *= invNorm
    w *= invNorm
    return this
  }

  /**
   * Set `this` quaternion to a rotation that rotates the `fromDir` vector to point along `toDir`.
   *
   *
   * Because there can be multiple possible rotations, this method chooses the one with the shortest arc.
   *
   * @see .rotationTo
   * @param fromDir
   * the starting direction
   * @param toDir
   * the destination direction
   * @return this
   */
  fun rotationTo(fromDir: Vector3fc, toDir: Vector3fc): Quaternionf {
    return rotationTo(fromDir.x, fromDir.y, fromDir.z, toDir.x, toDir.y, toDir.z)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateTo(float, float, float, float, float, float, Quaternionf)
     */
  override fun rotateTo(fromDirX: Float, fromDirY: Float, fromDirZ: Float, toDirX: Float, toDirY: Float, toDirZ: Float, dest: Quaternionf): Quaternionf {
    var x = fromDirY * toDirZ - fromDirZ * toDirY
    var y = fromDirZ * toDirX - fromDirX * toDirZ
    var z = fromDirX * toDirY - fromDirY * toDirX
    var w = Math.sqrt(((fromDirX * fromDirX + fromDirY * fromDirY + fromDirZ * fromDirZ) * (toDirX * toDirX + toDirY * toDirY + toDirZ * toDirZ)).toDouble()).toFloat() + (fromDirX * toDirX + fromDirY * toDirY + fromDirZ * toDirZ)
    var invNorm = (1.0 / Math.sqrt((x * x + y * y + z * z + w * w).toDouble())).toFloat()
    if (java.lang.Float.isInfinite(invNorm)) {
      // Rotation is ambiguous: Find appropriate rotation axis (1. try toDir x +Z)
      x = toDirY
      y = -toDirX
      z = 0.0f
      w = 0.0f
      invNorm = (1.0 / Math.sqrt((x * x + y * y).toDouble())).toFloat()
      if (java.lang.Float.isInfinite(invNorm)) {
        // 2. try toDir x +X
        x = 0.0f
        y = toDirZ
        z = -toDirY
        w = 0.0f
        invNorm = (1.0 / Math.sqrt((y * y + z * z).toDouble())).toFloat()
      }
    }
    x *= invNorm
    y *= invNorm
    z *= invNorm
    w *= invNorm
    /* Multiply */
    dest[this.w * x + this.x * w + this.y * z - this.z * y, this.w * y - this.x * z + this.y * w + this.z * x, this.w * z + this.x * y - this.y * x + this.z * w] = this.w * w - this.x * x - this.y * y - this.z * z
    return dest
  }

  /**
   * Apply a rotation to `this` that rotates the <tt>fromDir</tt> vector to point along <tt>toDir</tt>.
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
   * @see .rotateTo
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
   * @return this
   */
  fun rotateTo(fromDirX: Float, fromDirY: Float, fromDirZ: Float, toDirX: Float, toDirY: Float, toDirZ: Float): Quaternionf {
    return rotateTo(fromDirX, fromDirY, fromDirZ, toDirX, toDirY, toDirZ, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateTo(Vector3fc, Vector3fc, Quaternionf)
     */
  override fun rotateTo(fromDir: Vector3fc, toDir: Vector3fc, dest: Quaternionf): Quaternionf {
    return rotateTo(fromDir.x, fromDir.y, fromDir.z, toDir.x, toDir.y, toDir.z, dest)
  }

  /**
   * Apply a rotation to `this` that rotates the `fromDir` vector to point along `toDir`.
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
   * @return this
   */
  fun rotateTo(fromDir: Vector3fc, toDir: Vector3fc): Quaternionf {
    return rotateTo(fromDir.x, fromDir.y, fromDir.z, toDir.x, toDir.y, toDir.z, this)
  }

  fun rotateTo(fromDir: Vector3, toDir: Vector3): Quaternionf {
    return rotateTo(fromDir.x, fromDir.y, fromDir.z, toDir.x, toDir.y, toDir.z, this)
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the basis unit axes of the cartesian space.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `Q * R`. So when transforming a
   * vector `v` with the new quaternion by using `Q * R * v`, the
   * rotation added by this method will be applied first!
   *
   * @see .rotate
   * @param angleX
   * the angle in radians to rotate about the x axis
   * @param angleY
   * the angle in radians to rotate about the y axis
   * @param angleZ
   * the angle in radians to rotate about the z axis
   * @return this
   */
  fun rotate(angleX: Float, angleY: Float, angleZ: Float): Quaternionf {
    return rotate(angleX, angleY, angleZ, this)
  }

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
   * @see .rotate
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
  override fun rotate(angleX: Float, angleY: Float, angleZ: Float, dest: Quaternionf): Quaternionf {
    val thetaX = angleX * 0.5
    val thetaY = angleY * 0.5
    val thetaZ = angleZ * 0.5
    val thetaMagSq = thetaX * thetaX + thetaY * thetaY + thetaZ * thetaZ
    val s: Double
    val dqX: Double
    val dqY: Double
    val dqZ: Double
    val dqW: Double
    if (thetaMagSq * thetaMagSq / 24.0f < 1E-8f) {
      dqW = 1.0 - thetaMagSq / 2.0
      s = 1.0 - thetaMagSq / 6.0
    } else {
      val thetaMag = Math.sqrt(thetaMagSq)
      val sin = Math.sin(thetaMag)
      s = sin / thetaMag
      dqW = Math.cosFromSin(sin, thetaMag)
    }
    dqX = thetaX * s
    dqY = thetaY * s
    dqZ = thetaZ * s
    /* Pre-multiplication */
    //        dest.set((float) (dqW * x + dqX * w + dqY * z - dqZ * y),
    //                 (float) (dqW * y - dqX * z + dqY * w + dqZ * x),
    //                 (float) (dqW * z + dqX * y - dqY * x + dqZ * w),
    //                 (float) (dqW * w - dqX * x - dqY * y - dqZ * z));
    /* Post-multiplication (like matrices multiply) */
    dest[(w * dqX + x * dqW + y * dqZ - z * dqY).toFloat(), (w * dqY - x * dqZ + y * dqW + z * dqX).toFloat(), (w * dqZ + x * dqY - y * dqX + z * dqW).toFloat()] = (w * dqW - x * dqX - y * dqY - z * dqZ).toFloat()
    return dest
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the basis unit axes of the
   * local coordinate system represented by this quaternion.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `R * Q`. So when transforming a
   * vector `v` with the new quaternion by using `R * Q * v`, the
   * rotation represented by `this` will be applied first!
   *
   * @see .rotateLocal
   * @param angleX
   * the angle in radians to rotate about the local x axis
   * @param angleY
   * the angle in radians to rotate about the local y axis
   * @param angleZ
   * the angle in radians to rotate about the local z axis
   * @return this
   */
  fun rotateLocal(angleX: Float, angleY: Float, angleZ: Float): Quaternionf {
    return rotateLocal(angleX, angleY, angleZ, this)
  }

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
   * @see .rotateLocal
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
  override fun rotateLocal(angleX: Float, angleY: Float, angleZ: Float, dest: Quaternionf): Quaternionf {
    val thetaX = angleX * 0.5f
    val thetaY = angleY * 0.5f
    val thetaZ = angleZ * 0.5f
    val thetaMagSq = thetaX * thetaX + thetaY * thetaY + thetaZ * thetaZ
    val s: Float
    val dqX: Float
    val dqY: Float
    val dqZ: Float
    val dqW: Float
    if (thetaMagSq * thetaMagSq / 24.0f < 1E-8f) {
      dqW = 1.0f - thetaMagSq * 0.5f
      s = 1.0f - thetaMagSq / 6.0f
    } else {
      val thetaMag = Math.sqrt(thetaMagSq.toDouble()).toFloat()
      val sin = Math.sin(thetaMag.toDouble()).toFloat()
      s = sin / thetaMag
      dqW = Math.cosFromSin(sin.toDouble(), thetaMag.toDouble()).toFloat()
    }
    dqX = thetaX * s
    dqY = thetaY * s
    dqZ = thetaZ * s
    /* Pre-multiplication */
    dest[dqW * x + dqX * w + dqY * z - dqZ * y, dqW * y - dqX * z + dqY * w + dqZ * x, dqW * z + dqX * y - dqY * x + dqZ * w] = dqW * w - dqX * x - dqY * y - dqZ * z
    return dest
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the x axis.
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
   * @return this
   */
  fun rotateX(angle: Float): Quaternionf {
    return rotateX(angle, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateX(float, Quaternionf)
     */
  override fun rotateX(angle: Float, dest: Quaternionf): Quaternionf {
    val sin = Math.sin(angle * 0.5).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle * 0.5).toFloat()
    dest[w * sin + x * cos, y * cos + z * sin, z * cos - y * sin] = w * cos - x * sin
    return dest
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the y axis.
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
   * @return this
   */
  fun rotateY(angle: Float): Quaternionf {
    return rotateY(angle, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateY(float, Quaternionf)
     */
  override fun rotateY(angle: Float, dest: Quaternionf): Quaternionf {
    val sin = Math.sin(angle * 0.5).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle * 0.5).toFloat()
    dest[x * cos - z * sin, w * sin + y * cos, x * sin + z * cos] = w * cos - y * sin
    return dest
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the z axis.
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
   * @return this
   */
  fun rotateZ(angle: Float): Quaternionf {
    return rotateZ(angle, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateZ(float, Quaternionf)
     */
  override fun rotateZ(angle: Float, dest: Quaternionf): Quaternionf {
    val sin = Math.sin(angle * 0.5).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle * 0.5).toFloat()
    dest[x * cos + y * sin, y * cos - x * sin, w * sin + z * cos] = w * cos - z * sin
    return dest
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the local x axis.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `R * Q`. So when transforming a
   * vector `v` with the new quaternion by using `R * Q * v`, the
   * rotation represented by `this` will be applied first!
   *
   * @param angle
   * the angle in radians to rotate about the local x axis
   * @return this
   */
  fun rotateLocalX(angle: Float): Quaternionf {
    return rotateLocalX(angle, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateLocalX(float, Quaternionf)
     */
  override fun rotateLocalX(angle: Float, dest: Quaternionf): Quaternionf {
    val hangle = angle * 0.5f
    val s = Math.sin(hangle.toDouble()).toFloat()
    val c = Math.cosFromSin(s.toDouble(), hangle.toDouble()).toFloat()
    dest[c * x + s * w, c * y - s * z, c * z + s * y] = c * w - s * x
    return dest
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the local y axis.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `R * Q`. So when transforming a
   * vector `v` with the new quaternion by using `R * Q * v`, the
   * rotation represented by `this` will be applied first!
   *
   * @param angle
   * the angle in radians to rotate about the local y axis
   * @return this
   */
  fun rotateLocalY(angle: Float): Quaternionf {
    return rotateLocalY(angle, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateLocalY(float, Quaternionf)
     */
  override fun rotateLocalY(angle: Float, dest: Quaternionf): Quaternionf {
    val hangle = angle * 0.5f
    val s = Math.sin(hangle.toDouble()).toFloat()
    val c = Math.cosFromSin(s.toDouble(), hangle.toDouble()).toFloat()
    dest[c * x + s * z, c * y + s * w, c * z - s * x] = c * w - s * y
    return dest
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the local z axis.
   *
   *
   * If `Q` is `this` quaternion and `R` the quaternion representing the
   * specified rotation, then the new quaternion will be `R * Q`. So when transforming a
   * vector `v` with the new quaternion by using `R * Q * v`, the
   * rotation represented by `this` will be applied first!
   *
   * @param angle
   * the angle in radians to rotate about the local z axis
   * @return this
   */
  fun rotateLocalZ(angle: Float): Quaternionf {
    return rotateLocalZ(angle, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateLocalZ(float, Quaternionf)
     */
  override fun rotateLocalZ(angle: Float, dest: Quaternionf): Quaternionf {
    val hangle = angle * 0.5f
    val s = Math.sin(hangle.toDouble()).toFloat()
    val c = Math.cosFromSin(s.toDouble(), hangle.toDouble()).toFloat()
    dest[c * x - s * y, c * y + s * x, c * z + s * w] = c * w - s * z
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateAxis(float, float, float, float, Quaternionf)
     */
  override fun rotateAxis(angle: Float, axisX: Float, axisY: Float, axisZ: Float, dest: Quaternionf): Quaternionf {
    val hangle = angle / 2.0
    val sinAngle = Math.sin(hangle)
    val invVLength = 1.0 / Math.sqrt((axisX * axisX + axisY * axisY + axisZ * axisZ).toDouble())

    val rx = axisX.toDouble() * invVLength * sinAngle
    val ry = axisY.toDouble() * invVLength * sinAngle
    val rz = axisZ.toDouble() * invVLength * sinAngle
    val rw = Math.cosFromSin(sinAngle, hangle)

    dest[(w * rx + x * rw + y * rz - z * ry).toFloat(), (w * ry - x * rz + y * rw + z * rx).toFloat(), (w * rz + x * ry - y * rx + z * rw).toFloat()] = (w * rw - x * rx - y * ry - z * rz).toFloat()
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#rotateAxis(float, Vector3fc, Quaternionf)
     */
  override fun rotateAxis(angle: Float, axis: Vector3fc, dest: Quaternionf): Quaternionf {
    return rotateAxis(angle, axis.x, axis.y, axis.z, dest)
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the specified axis.
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
   * @return this
   */
  fun rotateAxis(angle: Float, axis: Vector3fc): Quaternionf {
    return rotateAxis(angle, axis.x, axis.y, axis.z, this)
  }

  /**
   * Apply a rotation to `this` quaternion rotating the given radians about the specified axis.
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
   * @param axisX
   * the x coordinate of the rotation axis
   * @param axisY
   * the y coordinate of the rotation axis
   * @param axisZ
   * the z coordinate of the rotation axis
   * @return this
   */
  fun rotateAxis(angle: Float, axisX: Float, axisY: Float, axisZ: Float): Quaternionf {
    return rotateAxis(angle, axisX, axisY, axisZ, this)
  }

  /**
   * Return a string representation of this quaternion.
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
   * Return a string representation of this quaternion by formatting the components with the given [NumberFormat].
   *
   * @param formatter
   * the [NumberFormat] used to format the quaternion components with
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
    val other = obj as Quaternionf?
    if (java.lang.Float.floatToIntBits(w) != java.lang.Float.floatToIntBits(other!!.w))
      return false
    if (java.lang.Float.floatToIntBits(x) != java.lang.Float.floatToIntBits(other.x))
      return false
    if (java.lang.Float.floatToIntBits(y) != java.lang.Float.floatToIntBits(other.y))
      return false
    return if (java.lang.Float.floatToIntBits(z) != java.lang.Float.floatToIntBits(other.z)) false else true
  }

  /**
   * Compute the difference between `this` and the `other` quaternion
   * and store the result in `this`.
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
   * @return this
   */
  fun difference(other: Quaternionf): Quaternionf {
    return difference(other, this)
  }

  /* (non-Javadoc)
     * @see Quaternionfc#difference(Quaternionf, Quaternionf)
     */
  override fun difference(other: Quaternionf, dest: Quaternionf): Quaternionf {
    val invNorm = 1.0f / (x * x + y * y + z * z + w * w)
    val x = -this.x * invNorm
    val y = -this.y * invNorm
    val z = -this.z * invNorm
    val w = this.w * invNorm
    dest[w * other.x + x * other.w + y * other.z - z * other.y, w * other.y - x * other.z + y * other.w + z * other.x, w * other.z + x * other.y - y * other.x + z * other.w] = w * other.w - x * other.x - y * other.y - z * other.z
    return dest
  }

  /* (non-Javadoc)
     * @see Quaternionfc#positiveX(Vector3m)
     */
  override fun positiveX(dir: Vector3m): Vector3m {
    val invNorm = 1.0f / (x * x + y * y + z * z + w * w)
    val nx = -x * invNorm
    val ny = -y * invNorm
    val nz = -z * invNorm
    val nw = w * invNorm
    val dy = ny + ny
    val dz = nz + nz
    dir.x = -ny * dy - nz * dz + 1.0f
    dir.y = nx * dy + nw * dz
    dir.z = nx * dz - nw * dy
    return dir
  }

  /* (non-Javadoc)
     * @see Quaternionfc#normalizedPositiveX(Vector3m)
     */
  override fun normalizedPositiveX(dir: Vector3m): Vector3m {
    val dy = y + y
    val dz = z + z
    dir.x = -y * dy - z * dz + 1.0f
    dir.y = x * dy - w * dz
    dir.z = x * dz + w * dy
    return dir
  }

  /* (non-Javadoc)
     * @see Quaternionfc#positiveY(Vector3m)
     */
  override fun positiveY(dir: Vector3m): Vector3m {
    val invNorm = 1.0f / (x * x + y * y + z * z + w * w)
    val nx = -x * invNorm
    val ny = -y * invNorm
    val nz = -z * invNorm
    val nw = w * invNorm
    val dx = nx + nx
    val dy = ny + ny
    val dz = nz + nz
    dir.x = nx * dy - nw * dz
    dir.y = -nx * dx - nz * dz + 1.0f
    dir.z = ny * dz + nw * dx
    return dir
  }

  /* (non-Javadoc)
     * @see Quaternionfc#normalizedPositiveY(Vector3m)
     */
  override fun normalizedPositiveY(dir: Vector3m): Vector3m {
    val dx = x + x
    val dy = y + y
    val dz = z + z
    dir.x = x * dy + w * dz
    dir.y = -x * dx - z * dz + 1.0f
    dir.z = y * dz - w * dx
    return dir
  }

  /* (non-Javadoc)
     * @see Quaternionfc#positiveZ(Vector3m)
     */
  override fun positiveZ(dir: Vector3m): Vector3m {
    val invNorm = 1.0f / (x * x + y * y + z * z + w * w)
    val nx = -x * invNorm
    val ny = -y * invNorm
    val nz = -z * invNorm
    val nw = w * invNorm
    val dx = nx + nx
    val dy = ny + ny
    val dz = nz + nz
    dir.x = nx * dz + nw * dy
    dir.y = ny * dz - nw * dx
    dir.z = -nx * dx - ny * dy + 1.0f
    return dir
  }

  /* (non-Javadoc)
     * @see Quaternionfc#normalizedPositiveZ(Vector3m)
     */
  override fun normalizedPositiveZ(dir: Vector3m): Vector3m {
    val dx = x + x
    val dy = y + y
    val dz = z + z
    dir.x = x * dz - w * dy
    dir.y = y * dz + w * dx
    dir.z = -x * dx - y * dy + 1.0f
    return dir
  }

  companion object {

    private val serialVersionUID = 1L

    /**
     * Interpolate between all of the quaternions given in `qs` via spherical linear interpolation using the specified interpolation factors `weights`,
     * and store the result in `dest`.
     *
     *
     * This method will interpolate between each two successive quaternions via [.slerp] using their relative interpolation weights.
     *
     *
     * This method resorts to non-spherical linear interpolation when the absolute dot product of any two interpolated quaternions is below <tt>1E-6f</tt>.
     *
     *
     * Reference: [http://gamedev.stackexchange.com/](http://gamedev.stackexchange.com/questions/62354/method-for-interpolation-between-3-quaternions#answer-62356)
     *
     * @param qs
     * the quaternions to interpolate over
     * @param weights
     * the weights of each individual quaternion in `qs`
     * @param dest
     * will hold the result
     * @return dest
     */
    fun slerp(qs: Array<Quaternionf>, weights: FloatArray, dest: Quaternionf): Quaternionfc {
      dest.set(qs[0])
      var w = weights[0]
      for (i in 1 until qs.size) {
        val w0 = w
        val w1 = weights[i]
        val rw1 = w1 / (w0 + w1)
        w += w1
        dest.slerp(qs[i], rw1)
      }
      return dest
    }

    /**
     * Interpolate between all of the quaternions given in `qs` via non-spherical linear interpolation using the
     * specified interpolation factors `weights`, and store the result in `dest`.
     *
     *
     * This method will interpolate between each two successive quaternions via [.nlerp]
     * using their relative interpolation weights.
     *
     *
     * Reference: [http://gamedev.stackexchange.com/](http://gamedev.stackexchange.com/questions/62354/method-for-interpolation-between-3-quaternions#answer-62356)
     *
     * @param qs
     * the quaternions to interpolate over
     * @param weights
     * the weights of each individual quaternion in `qs`
     * @param dest
     * will hold the result
     * @return dest
     */
    fun nlerp(qs: Array<Quaternionfc>, weights: FloatArray, dest: Quaternionf): Quaternionfc {
      dest.set(qs[0])
      var w = weights[0]
      for (i in 1 until qs.size) {
        val w0 = w
        val w1 = weights[i]
        val rw1 = w1 / (w0 + w1)
        w += w1
        dest.nlerp(qs[i], rw1)
      }
      return dest
    }

    /**
     * Interpolate between all of the quaternions given in `qs` via iterative non-spherical linear interpolation using the
     * specified interpolation factors `weights`, and store the result in `dest`.
     *
     *
     * This method will interpolate between each two successive quaternions via [.nlerpIterative]
     * using their relative interpolation weights.
     *
     *
     * Reference: [http://gamedev.stackexchange.com/](http://gamedev.stackexchange.com/questions/62354/method-for-interpolation-between-3-quaternions#answer-62356)
     *
     * @param qs
     * the quaternions to interpolate over
     * @param weights
     * the weights of each individual quaternion in `qs`
     * @param dotThreshold
     * the threshold for the dot product of each two interpolated quaternions above which [.nlerpIterative] performs another iteration
     * of a small-step linear interpolation
     * @param dest
     * will hold the result
     * @return dest
     */
    fun nlerpIterative(qs: Array<Quaternionf>, weights: FloatArray, dotThreshold: Float, dest: Quaternionf): Quaternionfc {
      dest.set(qs[0])
      var w = weights[0]
      for (i in 1 until qs.size) {
        val w0 = w
        val w1 = weights[i]
        val rw1 = w1 / (w0 + w1)
        w += w1
        dest.nlerpIterative(qs[i], rw1, dotThreshold)
      }
      return dest
    }
  }

}
