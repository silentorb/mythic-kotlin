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

import mythic.spatial.Vector3m
import mythic.spatial.Vector3
import org.joml.internal.MemUtil
import org.joml.internal.Options

import java.io.Externalizable
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.text.DecimalFormat
import java.text.NumberFormat


/**
 * Contains the definition of a 4x4 matrix of floats, and associated functions to transform
 * it. The matrix is column-major to match OpenGL's interpretation, and it looks like this:
 *
 *
 * m00  m10  m20  m30<br></br>
 * m01  m11  m21  m31<br></br>
 * m02  m12  m22  m32<br></br>
 * m03  m13  m23  m33<br></br>
 *
 * @author Richard Greenlees
 * @author Kai Burjack
 */
class Matrix4f : Externalizable, Matrix4fc {

  internal var m00: Float = 0.toFloat()
  internal var m01: Float = 0.toFloat()
  internal var m02: Float = 0.toFloat()
  internal var m03: Float = 0.toFloat()
  internal var m10: Float = 0.toFloat()
  internal var m11: Float = 0.toFloat()
  internal var m12: Float = 0.toFloat()
  internal var m13: Float = 0.toFloat()
  internal var m20: Float = 0.toFloat()
  internal var m21: Float = 0.toFloat()
  internal var m22: Float = 0.toFloat()
  internal var m23: Float = 0.toFloat()
  internal var m30: Float = 0.toFloat()
  internal var m31: Float = 0.toFloat()
  internal var m32: Float = 0.toFloat()
  internal var m33: Float = 0.toFloat()

  internal var properties: Int = 0

  /* (non-Javadoc)
     * @see Matrix4fc#isAffine()
     */
  override val isAffine: Boolean
    get() = m03 == 0.0f && m13 == 0.0f && m23 == 0.0f && m33 == 1.0f

  /**
   * Create a new [Matrix4f] and set it to [identity][.identity].
   */
  constructor() {
    m00 = 1.0f
    m11 = 1.0f
    m22 = 1.0f
    m33 = 1.0f
    properties = Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_TRANSLATION or Matrix4fc.PROPERTY_ORTHONORMAL
  }

  /**
   * Create a new [Matrix4f] by setting its uppper left 3x3 submatrix to the values of the given [Matrix3fc]
   * and the rest to identity.
   *
   * @param mat
   * the [Matrix3fc]
   */
  constructor(mat: Matrix3fc) {
    if (mat is Matrix3f) {
      MemUtil.INSTANCE.copy3x3(mat, this)
    } else {
      set3x3Matrix3fc(mat)
    }
    m33 = 1.0f
    properties = Matrix4fc.PROPERTY_AFFINE.toInt()
  }

  /**
   * Create a new [Matrix4f] and make it a copy of the given matrix.
   *
   * @param mat
   * the [Matrix4fc] to copy the values from
   */
  constructor(mat: Matrix4fc) {
    if (mat is Matrix4f) {
      MemUtil.INSTANCE.copy(mat, this)
    } else {
      setMatrix4fc(mat)
    }
    properties = mat.properties()
  }

  /**
   * Create a new [Matrix4f] and set its upper 4x3 submatrix to the given matrix `mat`
   * and all other elements to identity.
   *
   * @param mat
   * the [Matrix4x3fc] to copy the values from
   */
  constructor(mat: Matrix4x3fc) {
    if (mat is Matrix4x3f) {
      MemUtil.INSTANCE.copy4x3(mat, this)
    } else {
      set4x3Matrix4x3fc(mat)
    }
    this.m33 = 1.0f
    properties = mat.properties() or Matrix4fc.PROPERTY_AFFINE
  }

  /**
   * Create a new [Matrix4f] and make it a copy of the given matrix.
   *
   *
   * Note that due to the given [Matrix4dc] storing values in double-precision and the constructed [Matrix4f] storing them
   * in single-precision, there is the possibility of losing precision.
   *
   * @param mat
   * the [Matrix4dc] to copy the values from
   */
  constructor(mat: Matrix4dc) {
    m00 = mat.m00().toFloat()
    m01 = mat.m01().toFloat()
    m02 = mat.m02().toFloat()
    m03 = mat.m03().toFloat()
    m10 = mat.m10().toFloat()
    m11 = mat.m11().toFloat()
    m12 = mat.m12().toFloat()
    m13 = mat.m13().toFloat()
    m20 = mat.m20().toFloat()
    m21 = mat.m21().toFloat()
    m22 = mat.m22().toFloat()
    m23 = mat.m23().toFloat()
    m30 = mat.m30().toFloat()
    m31 = mat.m31().toFloat()
    m32 = mat.m32().toFloat()
    m33 = mat.m33().toFloat()
    properties = mat.properties()
  }

  /**
   * Create a new 4x4 matrix using the supplied float values.
   *
   *
   * The matrix layout will be:<br></br><br></br>
   * m00, m10, m20, m30<br></br>
   * m01, m11, m21, m31<br></br>
   * m02, m12, m22, m32<br></br>
   * m03, m13, m23, m33
   *
   * @param m00
   * the value of m00
   * @param m01
   * the value of m01
   * @param m02
   * the value of m02
   * @param m03
   * the value of m03
   * @param m10
   * the value of m10
   * @param m11
   * the value of m11
   * @param m12
   * the value of m12
   * @param m13
   * the value of m13
   * @param m20
   * the value of m20
   * @param m21
   * the value of m21
   * @param m22
   * the value of m22
   * @param m23
   * the value of m23
   * @param m30
   * the value of m30
   * @param m31
   * the value of m31
   * @param m32
   * the value of m32
   * @param m33
   * the value of m33
   */
  constructor(m00: Float, m01: Float, m02: Float, m03: Float,
              m10: Float, m11: Float, m12: Float, m13: Float,
              m20: Float, m21: Float, m22: Float, m23: Float,
              m30: Float, m31: Float, m32: Float, m33: Float) {
    this._m00(m00)
    this._m01(m01)
    this._m02(m02)
    this._m03(m03)
    this._m10(m10)
    this._m11(m11)
    this._m12(m12)
    this._m13(m13)
    this._m20(m20)
    this._m21(m21)
    this._m22(m22)
    this._m23(m23)
    this._m30(m30)
    this._m31(m31)
    this._m32(m32)
    this._m33(m33)
    properties = 0
  }

  /**
   * Create a new [Matrix4f] by reading its 16 float components from the given [FloatBuffer]
   * at the buffer's current position.
   *
   *
   * That FloatBuffer is expected to hold the values in column-major order.
   *
   *
   * The buffer's position will not be changed by this method.
   *
   * @param buffer
   * the [FloatBuffer] to read the matrix values from
   */
  constructor(buffer: FloatBuffer) {
    MemUtil.INSTANCE.get(this, buffer.position(), buffer)
  }

  /**
   * Create a new [Matrix4f] and initialize its four columns using the supplied vectors.
   *
   * @param col0
   * the first column
   * @param col1
   * the second column
   * @param col2
   * the third column
   * @param col3
   * the fourth column
   */
  constructor(col0: Vector4fc, col1: Vector4fc, col2: Vector4fc, col3: Vector4fc) {
    if (col0 is Vector4f &&
        col1 is Vector4f &&
        col2 is Vector4f &&
        col3 is Vector4f) {
      MemUtil.INSTANCE.set(this, col0, col1, col2, col3)
    } else {
      setVector4fc(col0, col1, col2, col3)
    }
  }

  private fun thisOrNew(): Matrix4f {
    return this
  }

  internal fun _properties(properties: Int) {
    this.properties = properties
  }

  /**
   * Assume the given properties about this matrix.
   *
   *
   * Use one or multiple of 0, [Matrix4fc.PROPERTY_IDENTITY],
   * [Matrix4fc.PROPERTY_TRANSLATION], [Matrix4fc.PROPERTY_AFFINE],
   * [Matrix4fc.PROPERTY_PERSPECTIVE], [Matrix4fc.PROPERTY_ORTHONORMAL].
   *
   * @param properties
   * bitset of the properties to assume about this matrix
   * @return this
   */
  fun assume(properties: Int): Matrix4f {
    this._properties(properties)
    return this
  }

  /**
   * Compute and set the matrix properties returned by [.properties] based
   * on the current matrix element values.
   *
   * @return this
   */
  fun determineProperties(): Matrix4f {
    var properties = 0
    if (m03 == 0.0f && m13 == 0.0f) {
      if (m23 == 0.0f && m33 == 1.0f) {
        properties = properties or Matrix4fc.PROPERTY_AFFINE.toInt()
        if (m00 == 1.0f && m01 == 0.0f && m02 == 0.0f && m10 == 0.0f && m11 == 1.0f && m12 == 0.0f
            && m20 == 0.0f && m21 == 0.0f && m22 == 1.0f) {
          properties = properties or (Matrix4fc.PROPERTY_TRANSLATION or Matrix4fc.PROPERTY_ORTHONORMAL)
          if (m30 == 0.0f && m31 == 0.0f && m32 == 0.0f)
            properties = properties or Matrix4fc.PROPERTY_IDENTITY.toInt()
        }
        /*
                 * We do not determine orthogonality, since it would require arbitrary epsilons
                 * and is rather expensive (6 dot products) in the worst case.
                 */
      } else if (m01 == 0.0f && m02 == 0.0f && m10 == 0.0f && m12 == 0.0f && m20 == 0.0f && m21 == 0.0f
          && m30 == 0.0f && m31 == 0.0f && m33 == 0.0f) {
        properties = properties or Matrix4fc.PROPERTY_PERSPECTIVE.toInt()
      }
    }
    this.properties = properties
    return this
  }

  /* (non-Javadoc)
     * @see Matrix4fc#properties()
     */
  override fun properties(): Int {
    return properties
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m00()
     */
  override fun m00(): Float {
    return m00
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m01()
     */
  override fun m01(): Float {
    return m01
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m02()
     */
  override fun m02(): Float {
    return m02
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m03()
     */
  override fun m03(): Float {
    return m03
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m10()
     */
  override fun m10(): Float {
    return m10
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m11()
     */
  override fun m11(): Float {
    return m11
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m12()
     */
  override fun m12(): Float {
    return m12
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m13()
     */
  override fun m13(): Float {
    return m13
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m20()
     */
  override fun m20(): Float {
    return m20
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m21()
     */
  override fun m21(): Float {
    return m21
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m22()
     */
  override fun m22(): Float {
    return m22
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m23()
     */
  override fun m23(): Float {
    return m23
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m30()
     */
  override fun m30(): Float {
    return m30
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m31()
     */
  override fun m31(): Float {
    return m31
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m32()
     */
  override fun m32(): Float {
    return m32
  }

  /* (non-Javadoc)
     * @see Matrix4fc#m33()
     */
  override fun m33(): Float {
    return m33
  }

  /**
   * Set the value of the matrix element at column 0 and row 0.
   *
   * @param m00
   * the new value
   * @return this
   */
  fun m00(m00: Float): Matrix4f {
    this.m00 = m00
    properties = properties and Matrix4fc.PROPERTY_ORTHONORMAL.inv()
    if (m00 != 1.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_TRANSLATION).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 0 and row 1.
   *
   * @param m01
   * the new value
   * @return this
   */
  fun m01(m01: Float): Matrix4f {
    this.m01 = m01
    properties = properties and Matrix4fc.PROPERTY_ORTHONORMAL.inv()
    if (m01 != 0.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 0 and row 2.
   *
   * @param m02
   * the new value
   * @return this
   */
  fun m02(m02: Float): Matrix4f {
    this.m02 = m02
    properties = properties and Matrix4fc.PROPERTY_ORTHONORMAL.inv()
    if (m02 != 0.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 0 and row 3.
   *
   * @param m03
   * the new value
   * @return this
   */
  fun m03(m03: Float): Matrix4f {
    this.m03 = m03
    if (m03 != 0.0f)
      properties = 0
    return this
  }

  /**
   * Set the value of the matrix element at column 1 and row 0.
   *
   * @param m10
   * the new value
   * @return this
   */
  fun m10(m10: Float): Matrix4f {
    this.m10 = m10
    properties = properties and Matrix4fc.PROPERTY_ORTHONORMAL.inv()
    if (m10 != 0.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 1 and row 1.
   *
   * @param m11
   * the new value
   * @return this
   */
  fun m11(m11: Float): Matrix4f {
    this.m11 = m11
    properties = properties and Matrix4fc.PROPERTY_ORTHONORMAL.inv()
    if (m11 != 1.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_TRANSLATION).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 1 and row 2.
   *
   * @param m12
   * the new value
   * @return this
   */
  fun m12(m12: Float): Matrix4f {
    this.m12 = m12
    properties = properties and Matrix4fc.PROPERTY_ORTHONORMAL.inv()
    if (m12 != 0.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 1 and row 3.
   *
   * @param m13
   * the new value
   * @return this
   */
  fun m13(m13: Float): Matrix4f {
    this.m13 = m13
    if (m13 != 0.0f)
      properties = 0
    return this
  }

  /**
   * Set the value of the matrix element at column 2 and row 0.
   *
   * @param m20
   * the new value
   * @return this
   */
  fun m20(m20: Float): Matrix4f {
    this.m20 = m20
    properties = properties and Matrix4fc.PROPERTY_ORTHONORMAL.inv()
    if (m20 != 0.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 2 and row 1.
   *
   * @param m21
   * the new value
   * @return this
   */
  fun m21(m21: Float): Matrix4f {
    this.m21 = m21
    properties = properties and Matrix4fc.PROPERTY_ORTHONORMAL.inv()
    if (m21 != 0.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 2 and row 2.
   *
   * @param m22
   * the new value
   * @return this
   */
  fun m22(m22: Float): Matrix4f {
    this.m22 = m22
    properties = properties and Matrix4fc.PROPERTY_ORTHONORMAL.inv()
    if (m22 != 1.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_TRANSLATION).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 2 and row 3.
   *
   * @param m23
   * the new value
   * @return this
   */
  fun m23(m23: Float): Matrix4f {
    this.m23 = m23
    if (m23 != 0.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_AFFINE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 3 and row 0.
   *
   * @param m30
   * the new value
   * @return this
   */
  fun m30(m30: Float): Matrix4f {
    this.m30 = m30
    if (m30 != 0.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_PERSPECTIVE).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 3 and row 1.
   *
   * @param m31
   * the new value
   * @return this
   */
  fun m31(m31: Float): Matrix4f {
    this.m31 = m31
    if (m31 != 0.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_PERSPECTIVE).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 3 and row 2.
   *
   * @param m32
   * the new value
   * @return this
   */
  fun m32(m32: Float): Matrix4f {
    this.m32 = m32
    if (m32 != 0.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_PERSPECTIVE).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 3 and row 3.
   *
   * @param m33
   * the new value
   * @return this
   */
  fun m33(m33: Float): Matrix4f {
    this.m33 = m33
    if (m33 != 0.0f)
      properties = properties and Matrix4fc.PROPERTY_PERSPECTIVE.inv()
    if (m33 != 1.0f)
      properties = properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt() or Matrix4fc.PROPERTY_AFFINE.toInt()).inv()
    return this
  }

  /**
   * Set the value of the matrix element at column 0 and row 0 without updating the properties of the matrix.
   *
   * @param m00
   * the new value
   * @return this
   */
  fun _m00(m00: Float): Matrix4f {
    this.m00 = m00
    return this
  }

  /**
   * Set the value of the matrix element at column 0 and row 1 without updating the properties of the matrix.
   *
   * @param m01
   * the new value
   * @return this
   */
  fun _m01(m01: Float): Matrix4f {
    this.m01 = m01
    return this
  }

  /**
   * Set the value of the matrix element at column 0 and row 2 without updating the properties of the matrix.
   *
   * @param m02
   * the new value
   * @return this
   */
  fun _m02(m02: Float): Matrix4f {
    this.m02 = m02
    return this
  }

  /**
   * Set the value of the matrix element at column 0 and row 3 without updating the properties of the matrix.
   *
   * @param m03
   * the new value
   * @return this
   */
  fun _m03(m03: Float): Matrix4f {
    this.m03 = m03
    return this
  }

  /**
   * Set the value of the matrix element at column 1 and row 0 without updating the properties of the matrix.
   *
   * @param m10
   * the new value
   * @return this
   */
  fun _m10(m10: Float): Matrix4f {
    this.m10 = m10
    return this
  }

  /**
   * Set the value of the matrix element at column 1 and row 1 without updating the properties of the matrix.
   *
   * @param m11
   * the new value
   * @return this
   */
  fun _m11(m11: Float): Matrix4f {
    this.m11 = m11
    return this
  }

  /**
   * Set the value of the matrix element at column 1 and row 2 without updating the properties of the matrix.
   *
   * @param m12
   * the new value
   * @return this
   */
  fun _m12(m12: Float): Matrix4f {
    this.m12 = m12
    return this
  }

  /**
   * Set the value of the matrix element at column 1 and row 3 without updating the properties of the matrix.
   *
   * @param m13
   * the new value
   * @return this
   */
  fun _m13(m13: Float): Matrix4f {
    this.m13 = m13
    return this
  }

  /**
   * Set the value of the matrix element at column 2 and row 0 without updating the properties of the matrix.
   *
   * @param m20
   * the new value
   * @return this
   */
  fun _m20(m20: Float): Matrix4f {
    this.m20 = m20
    return this
  }

  /**
   * Set the value of the matrix element at column 2 and row 1 without updating the properties of the matrix.
   *
   * @param m21
   * the new value
   * @return this
   */
  fun _m21(m21: Float): Matrix4f {
    this.m21 = m21
    return this
  }

  /**
   * Set the value of the matrix element at column 2 and row 2 without updating the properties of the matrix.
   *
   * @param m22
   * the new value
   * @return this
   */
  fun _m22(m22: Float): Matrix4f {
    this.m22 = m22
    return this
  }

  /**
   * Set the value of the matrix element at column 2 and row 3 without updating the properties of the matrix.
   *
   * @param m23
   * the new value
   * @return this
   */
  fun _m23(m23: Float): Matrix4f {
    this.m23 = m23
    return this
  }

  /**
   * Set the value of the matrix element at column 3 and row 0 without updating the properties of the matrix.
   *
   * @param m30
   * the new value
   * @return this
   */
  fun _m30(m30: Float): Matrix4f {
    this.m30 = m30
    return this
  }

  /**
   * Set the value of the matrix element at column 3 and row 1 without updating the properties of the matrix.
   *
   * @param m31
   * the new value
   * @return this
   */
  fun _m31(m31: Float): Matrix4f {
    this.m31 = m31
    return this
  }

  /**
   * Set the value of the matrix element at column 3 and row 2 without updating the properties of the matrix.
   *
   * @param m32
   * the new value
   * @return this
   */
  fun _m32(m32: Float): Matrix4f {
    this.m32 = m32
    return this
  }

  /**
   * Set the value of the matrix element at column 3 and row 3 without updating the properties of the matrix.
   *
   * @param m33
   * the new value
   * @return this
   */
  fun _m33(m33: Float): Matrix4f {
    this.m33 = m33
    return this
  }

  /**
   * Reset this matrix to the identity.
   *
   *
   * Please note that if a call to [.identity] is immediately followed by a call to:
   * [translate][.translate],
   * [rotate][.rotate],
   * [scale][.scale],
   * [perspective][.perspective],
   * [frustum][.frustum],
   * [ortho][.ortho],
   * [ortho2D][.ortho2D],
   * [lookAt][.lookAt],
   * [lookAlong][.lookAlong],
   * or any of their overloads, then the call to [.identity] can be omitted and the subsequent call replaced with:
   * [translation][.translation],
   * [rotation][.rotation],
   * [scaling][.scaling],
   * [setPerspective][.setPerspective],
   * [setFrustum][.setFrustum],
   * [setOrtho][.setOrtho],
   * [setOrtho2D][.setOrtho2D],
   * [setLookAt][.setLookAt],
   * [setLookAlong][.setLookAlong],
   * or any of their overloads.
   *
   * @return this
   */
  fun identity(): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return this
    MemUtil.INSTANCE.identity(this)
    this._properties(Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_AFFINE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt())
    return this
  }

  /**
   * Store the values of the given matrix `m` into `this` matrix.
   *
   * @see .Matrix4f
   * @see .get
   * @param m
   * the matrix to copy the values from
   * @return this
   */
  fun set(m: Matrix4fc): Matrix4f {
    if (m is Matrix4f) {
      MemUtil.INSTANCE.copy(m, this)
    } else {
      setMatrix4fc(m)
    }
    this._properties(m.properties())
    return this
  }

  private fun setMatrix4fc(mat: Matrix4fc) {
    _m00(mat.m00())
    _m01(mat.m01())
    _m02(mat.m02())
    _m03(mat.m03())
    _m10(mat.m10())
    _m11(mat.m11())
    _m12(mat.m12())
    _m13(mat.m13())
    _m20(mat.m20())
    _m21(mat.m21())
    _m22(mat.m22())
    _m23(mat.m23())
    _m30(mat.m30())
    _m31(mat.m31())
    _m32(mat.m32())
    _m33(mat.m33())
  }

  /**
   * Store the values of the given matrix `m` into `this` matrix
   * and set the other matrix elements to identity.
   *
   * @see .Matrix4f
   * @param m
   * the matrix to copy the values from
   * @return this
   */
  fun set(m: Matrix4x3fc): Matrix4f {
    if (m is Matrix4x3f) {
      MemUtil.INSTANCE.copy(m, this)
    } else {
      setMatrix4x3fc(m)
    }
    this._properties(m.properties() or Matrix4fc.PROPERTY_AFFINE)
    return this
  }

  private fun setMatrix4x3fc(mat: Matrix4x3fc) {
    _m00(mat.m00())
    _m01(mat.m01())
    _m02(mat.m02())
    _m03(0.0f)
    _m10(mat.m10())
    _m11(mat.m11())
    _m12(mat.m12())
    _m13(0.0f)
    _m20(mat.m20())
    _m21(mat.m21())
    _m22(mat.m22())
    _m23(0.0f)
    _m30(mat.m30())
    _m31(mat.m31())
    _m32(mat.m32())
    _m33(1.0f)
  }

  /**
   * Store the values of the given matrix `m` into `this` matrix.
   *
   *
   * Note that due to the given matrix `m` storing values in double-precision and `this` matrix storing
   * them in single-precision, there is the possibility to lose precision.
   *
   * @see .Matrix4f
   * @see .get
   * @param m
   * the matrix to copy the values from
   * @return this
   */
  fun set(m: Matrix4dc): Matrix4f {
    this._m00(m.m00().toFloat())
    this._m01(m.m01().toFloat())
    this._m02(m.m02().toFloat())
    this._m03(m.m03().toFloat())
    this._m10(m.m10().toFloat())
    this._m11(m.m11().toFloat())
    this._m12(m.m12().toFloat())
    this._m13(m.m13().toFloat())
    this._m20(m.m20().toFloat())
    this._m21(m.m21().toFloat())
    this._m22(m.m22().toFloat())
    this._m23(m.m23().toFloat())
    this._m30(m.m30().toFloat())
    this._m31(m.m31().toFloat())
    this._m32(m.m32().toFloat())
    this._m33(m.m33().toFloat())
    this._properties(m.properties())
    return this
  }

  /**
   * Set the upper left 3x3 submatrix of this [Matrix4f] to the given [Matrix3fc]
   * and the rest to identity.
   *
   * @see .Matrix4f
   * @param mat
   * the [Matrix3fc]
   * @return this
   */
  fun set(mat: Matrix3fc): Matrix4f {
    if (mat is Matrix3f) {
      MemUtil.INSTANCE.copy(mat, this)
    } else {
      setMatrix3fc(mat)
    }
    this._properties(Matrix4fc.PROPERTY_AFFINE.toInt())
    return this
  }

  private fun setMatrix3fc(mat: Matrix3fc) {
    m00 = mat.m00()
    m01 = mat.m01()
    m02 = mat.m02()
    m03 = 0.0f
    m10 = mat.m10()
    m11 = mat.m11()
    m12 = mat.m12()
    m13 = 0.0f
    m20 = mat.m20()
    m21 = mat.m21()
    m22 = mat.m22()
    m23 = 0.0f
    m30 = 0.0f
    m31 = 0.0f
    m32 = 0.0f
    m33 = 1.0f
  }

  /**
   * Set this matrix to be equivalent to the rotation specified by the given [AxisAngle4f].
   *
   * @param axisAngle
   * the [AxisAngle4f]
   * @return this
   */
  fun set(axisAngle: AxisAngle4f): Matrix4f {
    var x = axisAngle.x
    var y = axisAngle.y
    var z = axisAngle.z
    val angle = axisAngle.angle.toDouble()
    var n = Math.sqrt((x * x + y * y + z * z).toDouble())
    n = 1 / n
    x *= n.toFloat()
    y *= n.toFloat()
    z *= n.toFloat()
    val s = Math.sin(angle)
    val c = Math.cosFromSin(s, angle)
    val omc = 1.0 - c
    this._m00((c + x.toDouble() * x.toDouble() * omc).toFloat())
    this._m11((c + y.toDouble() * y.toDouble() * omc).toFloat())
    this._m22((c + z.toDouble() * z.toDouble() * omc).toFloat())
    var tmp1 = x.toDouble() * y.toDouble() * omc
    var tmp2 = z * s
    this._m10((tmp1 - tmp2).toFloat())
    this._m01((tmp1 + tmp2).toFloat())
    tmp1 = x.toDouble() * z.toDouble() * omc
    tmp2 = y * s
    this._m20((tmp1 + tmp2).toFloat())
    this._m02((tmp1 - tmp2).toFloat())
    tmp1 = y.toDouble() * z.toDouble() * omc
    tmp2 = x * s
    this._m21((tmp1 - tmp2).toFloat())
    this._m12((tmp1 + tmp2).toFloat())
    this._m03(0.0f)
    this._m13(0.0f)
    this._m23(0.0f)
    this._m30(0.0f)
    this._m31(0.0f)
    this._m32(0.0f)
    this._m33(1.0f)
    this._properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to be equivalent to the rotation specified by the given [AxisAngle4d].
   *
   * @param axisAngle
   * the [AxisAngle4d]
   * @return this
   */
  fun set(axisAngle: AxisAngle4d): Matrix4f {
    var x = axisAngle.x
    var y = axisAngle.y
    var z = axisAngle.z
    val angle = axisAngle.angle
    var n = Math.sqrt(x * x + y * y + z * z)
    n = 1 / n
    x *= n
    y *= n
    z *= n
    val s = Math.sin(angle)
    val c = Math.cosFromSin(s, angle)
    val omc = 1.0 - c
    this._m00((c + x * x * omc).toFloat())
    this._m11((c + y * y * omc).toFloat())
    this._m22((c + z * z * omc).toFloat())
    var tmp1 = x * y * omc
    var tmp2 = z * s
    this._m10((tmp1 - tmp2).toFloat())
    this._m01((tmp1 + tmp2).toFloat())
    tmp1 = x * z * omc
    tmp2 = y * s
    this._m20((tmp1 + tmp2).toFloat())
    this._m02((tmp1 - tmp2).toFloat())
    tmp1 = y * z * omc
    tmp2 = x * s
    this._m21((tmp1 - tmp2).toFloat())
    this._m12((tmp1 + tmp2).toFloat())
    this._m03(0.0f)
    this._m13(0.0f)
    this._m23(0.0f)
    this._m30(0.0f)
    this._m31(0.0f)
    this._m32(0.0f)
    this._m33(1.0f)
    this._properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to be equivalent to the rotation specified by the given [Quaternionfc].
   *
   *
   * This method is equivalent to calling: <tt>rotation(q)</tt>
   *
   *
   * Reference: [http://www.euclideanspace.com/](http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/)
   *
   * @see .rotation
   * @param q
   * the [Quaternionfc]
   * @return this
   */
  fun set(q: Quaternionfc): Matrix4f {
    return rotation(q)
  }

  /**
   * Set this matrix to be equivalent to the rotation specified by the given [Quaterniondc].
   *
   *
   * Reference: [http://www.euclideanspace.com/](http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/)
   *
   * @param q
   * the [Quaterniondc]
   * @return this
   */
//  fun set(q: Quaterniondc): Matrix4f {
//    val w2 = q.w * q.w
//    val x2 = q.x * q.x
//    val y2 = q.y * q.y
//    val z2 = q.z * q.z
//    val zw = q.z * q.w
//    val xy = q.x * q.y
//    val xz = q.x * q.z
//    val yw = q.y * q.w
//    val yz = q.y * q.z
//    val xw = q.x * q.w
//    _m00((w2 + x2 - z2 - y2).toFloat())
//    _m01((xy + zw + zw + xy).toFloat())
//    _m02((xz - yw + xz - yw).toFloat())
//    _m03(0.0f)
//    _m10((-zw + xy - zw + xy).toFloat())
//    _m11((y2 - z2 + w2 - x2).toFloat())
//    _m12((yz + yz + xw + xw).toFloat())
//    _m13(0.0f)
//    _m20((yw + xz + xz + yw).toFloat())
//    _m21((yz + yz - xw - xw).toFloat())
//    _m22((z2 - y2 - x2 + w2).toFloat())
//    _m30(0.0f)
//    _m31(0.0f)
//    _m32(0.0f)
//    _m33(1.0f)
//    this._properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
//    return this
//  }

  /**
   * Set the upper left 3x3 submatrix of this [Matrix4f] to that of the given [Matrix4f]
   * and don't change the other elements.
   *
   * @param mat
   * the [Matrix4f]
   * @return this
   */
  fun set3x3(mat: Matrix4f): Matrix4f {
    MemUtil.INSTANCE.copy3x3(mat, this)
    properties = properties and (mat.properties and Matrix4fc.PROPERTY_PERSPECTIVE.inv())
    return this
  }


  /**
   * Set the upper 4x3 submatrix of this [Matrix4f] to the given [Matrix4x3fc]
   * and don't change the other elements.
   *
   * @see Matrix4x3f.get
   * @param mat
   * the [Matrix4x3fc]
   * @return this
   */
  fun set4x3(mat: Matrix4x3fc): Matrix4f {
    if (mat is Matrix4x3f) {
      MemUtil.INSTANCE.copy4x3(mat, this)
    } else {
      set4x3Matrix4x3fc(mat)
    }
    properties = properties and (mat.properties() and Matrix4fc.PROPERTY_PERSPECTIVE.inv())
    return this
  }

  private fun set4x3Matrix4x3fc(mat: Matrix4x3fc) {
    _m00(mat.m00())
    _m01(mat.m01())
    _m02(mat.m02())
    _m10(mat.m10())
    _m11(mat.m11())
    _m12(mat.m12())
    _m20(mat.m20())
    _m21(mat.m21())
    _m22(mat.m22())
    _m30(mat.m30())
    _m31(mat.m31())
    _m32(mat.m32())
  }

  /**
   * Set the upper 4x3 submatrix of this [Matrix4f] to the upper 4x3 submatrix of the given [Matrix4f]
   * and don't change the other elements.
   *
   * @param mat
   * the [Matrix4f]
   * @return this
   */
  fun set4x3(mat: Matrix4f): Matrix4f {
    MemUtil.INSTANCE.copy4x3(mat, this)
    properties = properties and (mat.properties and Matrix4fc.PROPERTY_PERSPECTIVE.inv())
    return this
  }

  /**
   * Multiply this matrix by the supplied `right` matrix and store the result in `this`.
   *
   *
   * If `M` is `this` matrix and `R` the `right` matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * transformation of the right matrix will be applied first!
   *
   * @param right
   * the right operand of the matrix multiplication
   * @return a matrix holding the result
   */
  fun mul(right: Matrix4fc): Matrix4f {
    return mul(right, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mul(Matrix4fc, Matrix4f)
     */
  override fun mul(right: Matrix4fc, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.set(right)
    else if (right.properties() and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.set(this)
    else if (properties and Matrix4fc.PROPERTY_TRANSLATION != 0 && right.properties() and Matrix4fc.PROPERTY_AFFINE != 0)
      return mulTranslationAffine(right, dest)
    else if (properties and Matrix4fc.PROPERTY_AFFINE != 0 && right.properties() and Matrix4fc.PROPERTY_AFFINE != 0)
      return mulAffine(right, dest)
    else if (properties and Matrix4fc.PROPERTY_PERSPECTIVE != 0 && right.properties() and Matrix4fc.PROPERTY_AFFINE != 0)
      return mulPerspectiveAffine(right, dest)
    else if (right.properties() and Matrix4fc.PROPERTY_AFFINE != 0)
      return mulAffineR(right, dest)
    return mulGeneric(right, dest)
  }

  private fun mulGeneric(right: Matrix4fc, dest: Matrix4f): Matrix4f {
    val nm00 = m00 * right.m00() + m10 * right.m01() + m20 * right.m02() + m30 * right.m03()
    val nm01 = m01 * right.m00() + m11 * right.m01() + m21 * right.m02() + m31 * right.m03()
    val nm02 = m02 * right.m00() + m12 * right.m01() + m22 * right.m02() + m32 * right.m03()
    val nm03 = m03 * right.m00() + m13 * right.m01() + m23 * right.m02() + m33 * right.m03()
    val nm10 = m00 * right.m10() + m10 * right.m11() + m20 * right.m12() + m30 * right.m13()
    val nm11 = m01 * right.m10() + m11 * right.m11() + m21 * right.m12() + m31 * right.m13()
    val nm12 = m02 * right.m10() + m12 * right.m11() + m22 * right.m12() + m32 * right.m13()
    val nm13 = m03 * right.m10() + m13 * right.m11() + m23 * right.m12() + m33 * right.m13()
    val nm20 = m00 * right.m20() + m10 * right.m21() + m20 * right.m22() + m30 * right.m23()
    val nm21 = m01 * right.m20() + m11 * right.m21() + m21 * right.m22() + m31 * right.m23()
    val nm22 = m02 * right.m20() + m12 * right.m21() + m22 * right.m22() + m32 * right.m23()
    val nm23 = m03 * right.m20() + m13 * right.m21() + m23 * right.m22() + m33 * right.m23()
    val nm30 = m00 * right.m30() + m10 * right.m31() + m20 * right.m32() + m30 * right.m33()
    val nm31 = m01 * right.m30() + m11 * right.m31() + m21 * right.m32() + m31 * right.m33()
    val nm32 = m02 * right.m30() + m12 * right.m31() + m22 * right.m32() + m32 * right.m33()
    val nm33 = m03 * right.m30() + m13 * right.m31() + m23 * right.m32() + m33 * right.m33()
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(0)
    return dest
  }

  /**
   * Pre-multiply this matrix by the supplied `left` matrix and store the result in `this`.
   *
   *
   * If `M` is `this` matrix and `L` the `left` matrix,
   * then the new matrix will be `L * M`. So when transforming a
   * vector `v` with the new matrix by using `L * M * v`, the
   * transformation of `this` matrix will be applied first!
   *
   * @param left
   * the left operand of the matrix multiplication
   * @return a matrix holding the result
   */
  fun mulLocal(left: Matrix4fc): Matrix4f {
    return mulLocal(left, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mulLocal(Matrix4fc, Matrix4f)
     */
  override fun mulLocal(left: Matrix4fc, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.set(left)
    else if (left.properties() and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.set(this)
    else if (properties and Matrix4fc.PROPERTY_AFFINE != 0 && left.properties() and Matrix4fc.PROPERTY_AFFINE != 0)
      return mulLocalAffine(left, dest)
    return mulLocalGeneric(left, dest)
  }

  private fun mulLocalGeneric(left: Matrix4fc, dest: Matrix4f): Matrix4f {
    val nm00 = left.m00() * m00 + left.m10() * m01 + left.m20() * m02 + left.m30() * m03
    val nm01 = left.m01() * m00 + left.m11() * m01 + left.m21() * m02 + left.m31() * m03
    val nm02 = left.m02() * m00 + left.m12() * m01 + left.m22() * m02 + left.m32() * m03
    val nm03 = left.m03() * m00 + left.m13() * m01 + left.m23() * m02 + left.m33() * m03
    val nm10 = left.m00() * m10 + left.m10() * m11 + left.m20() * m12 + left.m30() * m13
    val nm11 = left.m01() * m10 + left.m11() * m11 + left.m21() * m12 + left.m31() * m13
    val nm12 = left.m02() * m10 + left.m12() * m11 + left.m22() * m12 + left.m32() * m13
    val nm13 = left.m03() * m10 + left.m13() * m11 + left.m23() * m12 + left.m33() * m13
    val nm20 = left.m00() * m20 + left.m10() * m21 + left.m20() * m22 + left.m30() * m23
    val nm21 = left.m01() * m20 + left.m11() * m21 + left.m21() * m22 + left.m31() * m23
    val nm22 = left.m02() * m20 + left.m12() * m21 + left.m22() * m22 + left.m32() * m23
    val nm23 = left.m03() * m20 + left.m13() * m21 + left.m23() * m22 + left.m33() * m23
    val nm30 = left.m00() * m30 + left.m10() * m31 + left.m20() * m32 + left.m30() * m33
    val nm31 = left.m01() * m30 + left.m11() * m31 + left.m21() * m32 + left.m31() * m33
    val nm32 = left.m02() * m30 + left.m12() * m31 + left.m22() * m32 + left.m32() * m33
    val nm33 = left.m03() * m30 + left.m13() * m31 + left.m23() * m32 + left.m33() * m33
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(0)
    return dest
  }

  /**
   * Pre-multiply this matrix by the supplied `left` matrix, both of which are assumed to be [affine][.isAffine], and store the result in `this`.
   *
   *
   * This method assumes that `this` matrix and the given `left` matrix both represent an [affine][.isAffine] transformation
   * (i.e. their last rows are equal to <tt>(0, 0, 0, 1)</tt>)
   * and can be used to speed up matrix multiplication if the matrices only represent affine transformations, such as translation, rotation, scaling and shearing (in any combination).
   *
   *
   * This method will not modify either the last row of `this` or the last row of `left`.
   *
   *
   * If `M` is `this` matrix and `L` the `left` matrix,
   * then the new matrix will be `L * M`. So when transforming a
   * vector `v` with the new matrix by using `L * M * v`, the
   * transformation of `this` matrix will be applied first!
   *
   * @param left
   * the left operand of the matrix multiplication (the last row is assumed to be <tt>(0, 0, 0, 1)</tt>)
   * @return a matrix holding the result
   */
  fun mulLocalAffine(left: Matrix4fc): Matrix4f {
    return mulLocalAffine(left, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mulLocalAffine(Matrix4fc, Matrix4f)
     */
  override fun mulLocalAffine(left: Matrix4fc, dest: Matrix4f): Matrix4f {
    val nm00 = left.m00() * m00 + left.m10() * m01 + left.m20() * m02
    val nm01 = left.m01() * m00 + left.m11() * m01 + left.m21() * m02
    val nm02 = left.m02() * m00 + left.m12() * m01 + left.m22() * m02
    val nm03 = left.m03()
    val nm10 = left.m00() * m10 + left.m10() * m11 + left.m20() * m12
    val nm11 = left.m01() * m10 + left.m11() * m11 + left.m21() * m12
    val nm12 = left.m02() * m10 + left.m12() * m11 + left.m22() * m12
    val nm13 = left.m13()
    val nm20 = left.m00() * m20 + left.m10() * m21 + left.m20() * m22
    val nm21 = left.m01() * m20 + left.m11() * m21 + left.m21() * m22
    val nm22 = left.m02() * m20 + left.m12() * m21 + left.m22() * m22
    val nm23 = left.m23()
    val nm30 = left.m00() * m30 + left.m10() * m31 + left.m20() * m32 + left.m30()
    val nm31 = left.m01() * m30 + left.m11() * m31 + left.m21() * m32 + left.m31()
    val nm32 = left.m02() * m30 + left.m12() * m31 + left.m22() * m32 + left.m32()
    val nm33 = left.m33()
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(Matrix4fc.PROPERTY_AFFINE or (this.properties() and left.properties() and Matrix4fc.PROPERTY_ORTHONORMAL.toInt()))
    return dest
  }

  /**
   * Multiply this matrix by the supplied `right` matrix and store the result in `this`.
   *
   *
   * If `M` is `this` matrix and `R` the `right` matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * transformation of the right matrix will be applied first!
   *
   * @param right
   * the right operand of the matrix multiplication
   * @return a matrix holding the result
   */
  fun mul(right: Matrix4x3fc): Matrix4f {
    return mul(right, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mul(org.joml.Matrix4x3fc, Matrix4f)
     */
  override fun mul(right: Matrix4x3fc, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.set(right)
    else if (right.properties() and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.set(this)
    else if (properties and Matrix4fc.PROPERTY_PERSPECTIVE != 0 && right.properties() and Matrix4fc.PROPERTY_AFFINE != 0)
      return mulPerspectiveAffine(right, dest)
    return mulGeneric(right, dest)
  }

  private fun mulGeneric(right: Matrix4x3fc, dest: Matrix4f): Matrix4f {
    val nm00 = m00 * right.m00() + m10 * right.m01() + m20 * right.m02()
    val nm01 = m01 * right.m00() + m11 * right.m01() + m21 * right.m02()
    val nm02 = m02 * right.m00() + m12 * right.m01() + m22 * right.m02()
    val nm03 = m03 * right.m00() + m13 * right.m01() + m23 * right.m02()
    val nm10 = m00 * right.m10() + m10 * right.m11() + m20 * right.m12()
    val nm11 = m01 * right.m10() + m11 * right.m11() + m21 * right.m12()
    val nm12 = m02 * right.m10() + m12 * right.m11() + m22 * right.m12()
    val nm13 = m03 * right.m10() + m13 * right.m11() + m23 * right.m12()
    val nm20 = m00 * right.m20() + m10 * right.m21() + m20 * right.m22()
    val nm21 = m01 * right.m20() + m11 * right.m21() + m21 * right.m22()
    val nm22 = m02 * right.m20() + m12 * right.m21() + m22 * right.m22()
    val nm23 = m03 * right.m20() + m13 * right.m21() + m23 * right.m22()
    val nm30 = m00 * right.m30() + m10 * right.m31() + m20 * right.m32() + m30
    val nm31 = m01 * right.m30() + m11 * right.m31() + m21 * right.m32() + m31
    val nm32 = m02 * right.m30() + m12 * right.m31() + m22 * right.m32() + m32
    val nm33 = m03 * right.m30() + m13 * right.m31() + m23 * right.m32() + m33
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())
    return dest
  }

  /**
   * Multiply this matrix by the supplied `right` matrix and store the result in `this`.
   *
   *
   * If `M` is `this` matrix and `R` the `right` matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * transformation of the right matrix will be applied first!
   *
   * @param right
   * the right operand of the matrix multiplication
   * @return a matrix holding the result
   */
  fun mul(right: Matrix3x2fc): Matrix4f {
    return mul(right, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mul(org.joml.Matrix3x2fc, Matrix4f)
     */
  override fun mul(right: Matrix3x2fc, dest: Matrix4f): Matrix4f {
    val nm00 = m00 * right.m00() + m10 * right.m01()
    val nm01 = m01 * right.m00() + m11 * right.m01()
    val nm02 = m02 * right.m00() + m12 * right.m01()
    val nm03 = m03 * right.m00() + m13 * right.m01()
    val nm10 = m00 * right.m10() + m10 * right.m11()
    val nm11 = m01 * right.m10() + m11 * right.m11()
    val nm12 = m02 * right.m10() + m12 * right.m11()
    val nm13 = m03 * right.m10() + m13 * right.m11()
    val nm30 = m00 * right.m20() + m10 * right.m21() + m30
    val nm31 = m01 * right.m20() + m11 * right.m21() + m31
    val nm32 = m02 * right.m20() + m12 * right.m21() + m32
    val nm33 = m03 * right.m20() + m13 * right.m21() + m33
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(m20)
    dest._m21(m21)
    dest._m22(m22)
    dest._m23(m23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())
    return dest
  }

  /**
   * Multiply `this` symmetric perspective projection matrix by the supplied [affine][.isAffine] `view` matrix.
   *
   *
   * If `P` is `this` matrix and `V` the `view` matrix,
   * then the new matrix will be `P * V`. So when transforming a
   * vector `v` with the new matrix by using `P * V * v`, the
   * transformation of the `view` matrix will be applied first!
   *
   * @param view
   * the [affine][.isAffine] matrix to multiply `this` symmetric perspective projection matrix by
   * @return a matrix holding the result
   */
  fun mulPerspectiveAffine(view: Matrix4fc): Matrix4f {
    return mulPerspectiveAffine(view, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mulPerspectiveAffine(Matrix4fc, Matrix4f)
     */
  override fun mulPerspectiveAffine(view: Matrix4fc, dest: Matrix4f): Matrix4f {
    val nm00 = m00 * view.m00()
    val nm01 = m11 * view.m01()
    val nm02 = m22 * view.m02()
    val nm03 = m23 * view.m02()
    val nm10 = m00 * view.m10()
    val nm11 = m11 * view.m11()
    val nm12 = m22 * view.m12()
    val nm13 = m23 * view.m12()
    val nm20 = m00 * view.m20()
    val nm21 = m11 * view.m21()
    val nm22 = m22 * view.m22()
    val nm23 = m23 * view.m22()
    val nm30 = m00 * view.m30()
    val nm31 = m11 * view.m31()
    val nm32 = m22 * view.m32() + m32
    val nm33 = m23 * view.m32()
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(0)
    return dest
  }

  /**
   * Multiply `this` symmetric perspective projection matrix by the supplied `view` matrix.
   *
   *
   * If `P` is `this` matrix and `V` the `view` matrix,
   * then the new matrix will be `P * V`. So when transforming a
   * vector `v` with the new matrix by using `P * V * v`, the
   * transformation of the `view` matrix will be applied first!
   *
   * @param view
   * the matrix to multiply `this` symmetric perspective projection matrix by
   * @return a matrix holding the result
   */
  fun mulPerspectiveAffine(view: Matrix4x3fc): Matrix4f {
    return mulPerspectiveAffine(view, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mulPerspectiveAffine(org.joml.Matrix4x3fc, Matrix4f)
     */
  override fun mulPerspectiveAffine(view: Matrix4x3fc, dest: Matrix4f): Matrix4f {
    val nm00 = m00 * view.m00()
    val nm01 = m11 * view.m01()
    val nm02 = m22 * view.m02()
    val nm03 = m23 * view.m02()
    val nm10 = m00 * view.m10()
    val nm11 = m11 * view.m11()
    val nm12 = m22 * view.m12()
    val nm13 = m23 * view.m12()
    val nm20 = m00 * view.m20()
    val nm21 = m11 * view.m21()
    val nm22 = m22 * view.m22()
    val nm23 = m23 * view.m22()
    val nm30 = m00 * view.m30()
    val nm31 = m11 * view.m31()
    val nm32 = m22 * view.m32() + m32
    val nm33 = m23 * view.m32()
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(0)
    return dest
  }

  /**
   * Multiply this matrix by the supplied `right` matrix, which is assumed to be [affine][.isAffine], and store the result in `this`.
   *
   *
   * This method assumes that the given `right` matrix represents an [affine][.isAffine] transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
   * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
   *
   *
   * If `M` is `this` matrix and `R` the `right` matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * transformation of the right matrix will be applied first!
   *
   * @param right
   * the right operand of the matrix multiplication (the last row is assumed to be <tt>(0, 0, 0, 1)</tt>)
   * @return a matrix holding the result
   */
  fun mulAffineR(right: Matrix4fc): Matrix4f {
    return mulAffineR(right, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mulAffineR(Matrix4fc, Matrix4f)
     */
  override fun mulAffineR(right: Matrix4fc, dest: Matrix4f): Matrix4f {
    val nm00 = m00 * right.m00() + m10 * right.m01() + m20 * right.m02()
    val nm01 = m01 * right.m00() + m11 * right.m01() + m21 * right.m02()
    val nm02 = m02 * right.m00() + m12 * right.m01() + m22 * right.m02()
    val nm03 = m03 * right.m00() + m13 * right.m01() + m23 * right.m02()
    val nm10 = m00 * right.m10() + m10 * right.m11() + m20 * right.m12()
    val nm11 = m01 * right.m10() + m11 * right.m11() + m21 * right.m12()
    val nm12 = m02 * right.m10() + m12 * right.m11() + m22 * right.m12()
    val nm13 = m03 * right.m10() + m13 * right.m11() + m23 * right.m12()
    val nm20 = m00 * right.m20() + m10 * right.m21() + m20 * right.m22()
    val nm21 = m01 * right.m20() + m11 * right.m21() + m21 * right.m22()
    val nm22 = m02 * right.m20() + m12 * right.m21() + m22 * right.m22()
    val nm23 = m03 * right.m20() + m13 * right.m21() + m23 * right.m22()
    val nm30 = m00 * right.m30() + m10 * right.m31() + m20 * right.m32() + m30
    val nm31 = m01 * right.m30() + m11 * right.m31() + m21 * right.m32() + m31
    val nm32 = m02 * right.m30() + m12 * right.m31() + m22 * right.m32() + m32
    val nm33 = m03 * right.m30() + m13 * right.m31() + m23 * right.m32() + m33
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())
    return dest
  }

  /**
   * Multiply this matrix by the supplied `right` matrix, both of which are assumed to be [affine][.isAffine], and store the result in `this`.
   *
   *
   * This method assumes that `this` matrix and the given `right` matrix both represent an [affine][.isAffine] transformation
   * (i.e. their last rows are equal to <tt>(0, 0, 0, 1)</tt>)
   * and can be used to speed up matrix multiplication if the matrices only represent affine transformations, such as translation, rotation, scaling and shearing (in any combination).
   *
   *
   * This method will not modify either the last row of `this` or the last row of `right`.
   *
   *
   * If `M` is `this` matrix and `R` the `right` matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * transformation of the right matrix will be applied first!
   *
   * @param right
   * the right operand of the matrix multiplication (the last row is assumed to be <tt>(0, 0, 0, 1)</tt>)
   * @return a matrix holding the result
   */
  fun mulAffine(right: Matrix4fc): Matrix4f {
    return mulAffine(right, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mulAffine(Matrix4fc, Matrix4f)
     */
  override fun mulAffine(right: Matrix4fc, dest: Matrix4f): Matrix4f {
    val nm00 = m00 * right.m00() + m10 * right.m01() + m20 * right.m02()
    val nm01 = m01 * right.m00() + m11 * right.m01() + m21 * right.m02()
    val nm02 = m02 * right.m00() + m12 * right.m01() + m22 * right.m02()
    val nm03 = m03
    val nm10 = m00 * right.m10() + m10 * right.m11() + m20 * right.m12()
    val nm11 = m01 * right.m10() + m11 * right.m11() + m21 * right.m12()
    val nm12 = m02 * right.m10() + m12 * right.m11() + m22 * right.m12()
    val nm13 = m13
    val nm20 = m00 * right.m20() + m10 * right.m21() + m20 * right.m22()
    val nm21 = m01 * right.m20() + m11 * right.m21() + m21 * right.m22()
    val nm22 = m02 * right.m20() + m12 * right.m21() + m22 * right.m22()
    val nm23 = m23
    val nm30 = m00 * right.m30() + m10 * right.m31() + m20 * right.m32() + m30
    val nm31 = m01 * right.m30() + m11 * right.m31() + m21 * right.m32() + m31
    val nm32 = m02 * right.m30() + m12 * right.m31() + m22 * right.m32() + m32
    val nm33 = m33
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(Matrix4fc.PROPERTY_AFFINE or (this.properties and right.properties() and Matrix4fc.PROPERTY_ORTHONORMAL.toInt()))
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mulTranslationAffine(Matrix4fc, Matrix4f)
     */
  override fun mulTranslationAffine(right: Matrix4fc, dest: Matrix4f): Matrix4f {
    val nm00 = right.m00()
    val nm01 = right.m01()
    val nm02 = right.m02()
    val nm03 = m03
    val nm10 = right.m10()
    val nm11 = right.m11()
    val nm12 = right.m12()
    val nm13 = m13
    val nm20 = right.m20()
    val nm21 = right.m21()
    val nm22 = right.m22()
    val nm23 = m23
    val nm30 = right.m30() + m30
    val nm31 = right.m31() + m31
    val nm32 = right.m32() + m32
    val nm33 = m33
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(Matrix4fc.PROPERTY_AFFINE or (right.properties() and Matrix4fc.PROPERTY_ORTHONORMAL))
    return dest
  }

  /**
   * Multiply `this` orthographic projection matrix by the supplied [affine][.isAffine] `view` matrix.
   *
   *
   * If `M` is `this` matrix and `V` the `view` matrix,
   * then the new matrix will be `M * V`. So when transforming a
   * vector `v` with the new matrix by using `M * V * v`, the
   * transformation of the `view` matrix will be applied first!
   *
   * @param view
   * the affine matrix which to multiply `this` with
   * @return a matrix holding the result
   */
  fun mulOrthoAffine(view: Matrix4fc): Matrix4f {
    return mulOrthoAffine(view, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mulOrthoAffine(Matrix4fc, Matrix4f)
     */
  override fun mulOrthoAffine(view: Matrix4fc, dest: Matrix4f): Matrix4f {
    val nm00 = m00 * view.m00()
    val nm01 = m11 * view.m01()
    val nm02 = m22 * view.m02()
    val nm03 = 0.0f
    val nm10 = m00 * view.m10()
    val nm11 = m11 * view.m11()
    val nm12 = m22 * view.m12()
    val nm13 = 0.0f
    val nm20 = m00 * view.m20()
    val nm21 = m11 * view.m21()
    val nm22 = m22 * view.m22()
    val nm23 = 0.0f
    val nm30 = m00 * view.m30() + m30
    val nm31 = m11 * view.m31() + m31
    val nm32 = m22 * view.m32() + m32
    val nm33 = 1.0f
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(Matrix4fc.PROPERTY_AFFINE.toInt())
    return dest
  }

  /**
   * Component-wise add the upper 4x3 submatrices of `this` and `other`
   * by first multiplying each component of `other`'s 4x3 submatrix by `otherFactor` and
   * adding that result to `this`.
   *
   *
   * The matrix `other` will not be changed.
   *
   * @param other
   * the other matrix
   * @param otherFactor
   * the factor to multiply each of the other matrix's 4x3 components
   * @return a matrix holding the result
   */
  fun fma4x3(other: Matrix4fc, otherFactor: Float): Matrix4f {
    return fma4x3(other, otherFactor, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#fma4x3(Matrix4fc, float, Matrix4f)
     */
  override fun fma4x3(other: Matrix4fc, otherFactor: Float, dest: Matrix4f): Matrix4f {
    dest._m00(m00 + other.m00() * otherFactor)
    dest._m01(m01 + other.m01() * otherFactor)
    dest._m02(m02 + other.m02() * otherFactor)
    dest._m03(m03)
    dest._m10(m10 + other.m10() * otherFactor)
    dest._m11(m11 + other.m11() * otherFactor)
    dest._m12(m12 + other.m12() * otherFactor)
    dest._m13(m13)
    dest._m20(m20 + other.m20() * otherFactor)
    dest._m21(m21 + other.m21() * otherFactor)
    dest._m22(m22 + other.m22() * otherFactor)
    dest._m23(m23)
    dest._m30(m30 + other.m30() * otherFactor)
    dest._m31(m31 + other.m31() * otherFactor)
    dest._m32(m32 + other.m32() * otherFactor)
    dest._m33(m33)
    dest._properties(0)
    return dest
  }

  /**
   * Component-wise add `this` and `other`.
   *
   * @param other
   * the other addend
   * @return a matrix holding the result
   */
  fun add(other: Matrix4fc): Matrix4f {
    return add(other, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#add(Matrix4fc, Matrix4f)
     */
  override fun add(other: Matrix4fc, dest: Matrix4f): Matrix4f {
    dest._m00(m00 + other.m00())
    dest._m01(m01 + other.m01())
    dest._m02(m02 + other.m02())
    dest._m03(m03 + other.m03())
    dest._m10(m10 + other.m10())
    dest._m11(m11 + other.m11())
    dest._m12(m12 + other.m12())
    dest._m13(m13 + other.m13())
    dest._m20(m20 + other.m20())
    dest._m21(m21 + other.m21())
    dest._m22(m22 + other.m22())
    dest._m23(m23 + other.m23())
    dest._m30(m30 + other.m30())
    dest._m31(m31 + other.m31())
    dest._m32(m32 + other.m32())
    dest._m33(m33 + other.m33())
    dest._properties(0)
    return dest
  }

  /**
   * Component-wise subtract `subtrahend` from `this`.
   *
   * @param subtrahend
   * the subtrahend
   * @return a matrix holding the result
   */
  fun sub(subtrahend: Matrix4fc): Matrix4f {
    return sub(subtrahend, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#sub(Matrix4fc, Matrix4f)
     */
  override fun sub(subtrahend: Matrix4fc, dest: Matrix4f): Matrix4f {
    dest._m00(m00 - subtrahend.m00())
    dest._m01(m01 - subtrahend.m01())
    dest._m02(m02 - subtrahend.m02())
    dest._m03(m03 - subtrahend.m03())
    dest._m10(m10 - subtrahend.m10())
    dest._m11(m11 - subtrahend.m11())
    dest._m12(m12 - subtrahend.m12())
    dest._m13(m13 - subtrahend.m13())
    dest._m20(m20 - subtrahend.m20())
    dest._m21(m21 - subtrahend.m21())
    dest._m22(m22 - subtrahend.m22())
    dest._m23(m23 - subtrahend.m23())
    dest._m30(m30 - subtrahend.m30())
    dest._m31(m31 - subtrahend.m31())
    dest._m32(m32 - subtrahend.m32())
    dest._m33(m33 - subtrahend.m33())
    dest._properties(0)
    return dest
  }

  /**
   * Component-wise multiply `this` by `other`.
   *
   * @param other
   * the other matrix
   * @return a matrix holding the result
   */
  fun mulComponentWise(other: Matrix4fc): Matrix4f {
    return mulComponentWise(other, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mulComponentWise(Matrix4fc, Matrix4f)
     */
  override fun mulComponentWise(other: Matrix4fc, dest: Matrix4f): Matrix4f {
    dest._m00(m00 * other.m00())
    dest._m01(m01 * other.m01())
    dest._m02(m02 * other.m02())
    dest._m03(m03 * other.m03())
    dest._m10(m10 * other.m10())
    dest._m11(m11 * other.m11())
    dest._m12(m12 * other.m12())
    dest._m13(m13 * other.m13())
    dest._m20(m20 * other.m20())
    dest._m21(m21 * other.m21())
    dest._m22(m22 * other.m22())
    dest._m23(m23 * other.m23())
    dest._m30(m30 * other.m30())
    dest._m31(m31 * other.m31())
    dest._m32(m32 * other.m32())
    dest._m33(m33 * other.m33())
    dest._properties(0)
    return dest
  }

  /**
   * Component-wise add the upper 4x3 submatrices of `this` and `other`.
   *
   * @param other
   * the other addend
   * @return a matrix holding the result
   */
  fun add4x3(other: Matrix4fc): Matrix4f {
    return add4x3(other, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#add4x3(Matrix4fc, Matrix4f)
     */
  override fun add4x3(other: Matrix4fc, dest: Matrix4f): Matrix4f {
    dest._m00(m00 + other.m00())
    dest._m01(m01 + other.m01())
    dest._m02(m02 + other.m02())
    dest._m03(m03)
    dest._m10(m10 + other.m10())
    dest._m11(m11 + other.m11())
    dest._m12(m12 + other.m12())
    dest._m13(m13)
    dest._m20(m20 + other.m20())
    dest._m21(m21 + other.m21())
    dest._m22(m22 + other.m22())
    dest._m23(m23)
    dest._m30(m30 + other.m30())
    dest._m31(m31 + other.m31())
    dest._m32(m32 + other.m32())
    dest._m33(m33)
    dest._properties(0)
    return dest
  }

  /**
   * Component-wise subtract the upper 4x3 submatrices of `subtrahend` from `this`.
   *
   * @param subtrahend
   * the subtrahend
   * @return a matrix holding the result
   */
  fun sub4x3(subtrahend: Matrix4f): Matrix4f {
    return sub4x3(subtrahend, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#sub4x3(Matrix4fc, Matrix4f)
     */
  override fun sub4x3(subtrahend: Matrix4fc, dest: Matrix4f): Matrix4f {
    dest._m00(m00 - subtrahend.m00())
    dest._m01(m01 - subtrahend.m01())
    dest._m02(m02 - subtrahend.m02())
    dest._m03(m03)
    dest._m10(m10 - subtrahend.m10())
    dest._m11(m11 - subtrahend.m11())
    dest._m12(m12 - subtrahend.m12())
    dest._m13(m13)
    dest._m20(m20 - subtrahend.m20())
    dest._m21(m21 - subtrahend.m21())
    dest._m22(m22 - subtrahend.m22())
    dest._m23(m23)
    dest._m30(m30 - subtrahend.m30())
    dest._m31(m31 - subtrahend.m31())
    dest._m32(m32 - subtrahend.m32())
    dest._m33(m33)
    dest._properties(0)
    return dest
  }

  /**
   * Component-wise multiply the upper 4x3 submatrices of `this` by `other`.
   *
   * @param other
   * the other matrix
   * @return a matrix holding the result
   */
  fun mul4x3ComponentWise(other: Matrix4fc): Matrix4f {
    return mul4x3ComponentWise(other, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#mul4x3ComponentWise(Matrix4fc, Matrix4f)
     */
  override fun mul4x3ComponentWise(other: Matrix4fc, dest: Matrix4f): Matrix4f {
    dest._m00(m00 * other.m00())
    dest._m01(m01 * other.m01())
    dest._m02(m02 * other.m02())
    dest._m03(m03)
    dest._m10(m10 * other.m10())
    dest._m11(m11 * other.m11())
    dest._m12(m12 * other.m12())
    dest._m13(m13)
    dest._m20(m20 * other.m20())
    dest._m21(m21 * other.m21())
    dest._m22(m22 * other.m22())
    dest._m23(m23)
    dest._m30(m30 * other.m30())
    dest._m31(m31 * other.m31())
    dest._m32(m32 * other.m32())
    dest._m33(m33)
    dest._properties(0)
    return dest
  }

  /**
   * Set the values within this matrix to the supplied float values. The matrix will look like this:<br></br><br></br>
   *
   * m00, m10, m20, m30<br></br>
   * m01, m11, m21, m31<br></br>
   * m02, m12, m22, m32<br></br>
   * m03, m13, m23, m33
   *
   * @param m00
   * the new value of m00
   * @param m01
   * the new value of m01
   * @param m02
   * the new value of m02
   * @param m03
   * the new value of m03
   * @param m10
   * the new value of m10
   * @param m11
   * the new value of m11
   * @param m12
   * the new value of m12
   * @param m13
   * the new value of m13
   * @param m20
   * the new value of m20
   * @param m21
   * the new value of m21
   * @param m22
   * the new value of m22
   * @param m23
   * the new value of m23
   * @param m30
   * the new value of m30
   * @param m31
   * the new value of m31
   * @param m32
   * the new value of m32
   * @param m33
   * the new value of m33
   * @return this
   */
  operator fun set(m00: Float, m01: Float, m02: Float, m03: Float,
                   m10: Float, m11: Float, m12: Float, m13: Float,
                   m20: Float, m21: Float, m22: Float, m23: Float,
                   m30: Float, m31: Float, m32: Float, m33: Float): Matrix4f {
    this._m00(m00)
    this._m10(m10)
    this._m20(m20)
    this._m30(m30)
    this._m01(m01)
    this._m11(m11)
    this._m21(m21)
    this._m31(m31)
    this._m02(m02)
    this._m12(m12)
    this._m22(m22)
    this._m32(m32)
    this._m03(m03)
    this._m13(m13)
    this._m23(m23)
    this._m33(m33)
    _properties(0)
    return this
  }

  /**
   * Set the values in the matrix using a float array that contains the matrix elements in column-major order.
   *
   *
   * The results will look like this:<br></br><br></br>
   *
   * 0, 4, 8, 12<br></br>
   * 1, 5, 9, 13<br></br>
   * 2, 6, 10, 14<br></br>
   * 3, 7, 11, 15<br></br>
   *
   * @see .set
   * @param m
   * the array to read the matrix values from
   * @param off
   * the offset into the array
   * @return this
   */
  @JvmOverloads
  fun set(m: FloatArray, off: Int = 0): Matrix4f {
    MemUtil.INSTANCE.copy(m, off, this)
    _properties(0)
    return this
  }

  /**
   * Set the values of this matrix by reading 16 float values from the given [FloatBuffer] in column-major order,
   * starting at its current position.
   *
   *
   * The FloatBuffer is expected to contain the values in column-major order.
   *
   *
   * The position of the FloatBuffer will not be changed by this method.
   *
   * @param buffer
   * the FloatBuffer to read the matrix values from in column-major order
   * @return this
   */
  fun set(buffer: FloatBuffer): Matrix4f {
    MemUtil.INSTANCE.get(this, buffer.position(), buffer)
    _properties(0)
    return this
  }

  /**
   * Set the values of this matrix by reading 16 float values from the given [ByteBuffer] in column-major order,
   * starting at its current position.
   *
   *
   * The ByteBuffer is expected to contain the values in column-major order.
   *
   *
   * The position of the ByteBuffer will not be changed by this method.
   *
   * @param buffer
   * the ByteBuffer to read the matrix values from in column-major order
   * @return this
   */
  fun set(buffer: ByteBuffer): Matrix4f {
    MemUtil.INSTANCE.get(this, buffer.position(), buffer)
    _properties(0)
    return this
  }

  /**
   * Set the values of this matrix by reading 16 float values from off-heap memory in column-major order,
   * starting at the given address.
   *
   *
   * This method will throw an [UnsupportedOperationException] when JOML is used with `-Djoml.nounsafe`.
   *
   *
   * *This method is unsafe as it can result in a crash of the JVM process when the specified address range does not belong to this process.*
   *
   * @param address
   * the off-heap memory address to read the matrix values from in column-major order
   * @return this
   */
  fun setFromAddress(address: Long): Matrix4f {
    if (Options.NO_UNSAFE)
      throw UnsupportedOperationException("Not supported when using joml.nounsafe")
    val unsafe = MemUtil.INSTANCE as MemUtil.MemUtilUnsafe
    unsafe.get(this, address)
    _properties(0)
    return this
  }

  /**
   * Set the four columns of this matrix to the supplied vectors, respectively.
   *
   * @param col0
   * the first column
   * @param col1
   * the second column
   * @param col2
   * the third column
   * @param col3
   * the fourth column
   * @return this
   */
  operator fun set(col0: Vector4fc, col1: Vector4fc, col2: Vector4fc, col3: Vector4fc): Matrix4f {
    if (col0 is Vector4f &&
        col1 is Vector4f &&
        col2 is Vector4f &&
        col3 is Vector4f) {
      MemUtil.INSTANCE.set(this, col0, col1, col2, col3)
    } else {
      setVector4fc(col0, col1, col2, col3)
    }
    _properties(0)
    return this
  }

  private fun setVector4fc(col0: Vector4fc, col1: Vector4fc, col2: Vector4fc, col3: Vector4fc) {
    this.m00 = col0.x
    this.m01 = col0.y
    this.m02 = col0.z
    this.m03 = col0.w
    this.m10 = col1.x
    this.m11 = col1.y
    this.m12 = col1.z
    this.m13 = col1.w
    this.m20 = col2.x
    this.m21 = col2.y
    this.m22 = col2.z
    this.m23 = col2.w
    this.m30 = col3.x
    this.m31 = col3.y
    this.m32 = col3.z
    this.m33 = col3.w
  }

  /* (non-Javadoc)
     * @see Matrix4fc#determinant()
     */
  override fun determinant(): Float {
    return if (properties and Matrix4fc.PROPERTY_AFFINE != 0) determinantAffine() else (m00 * m11 - m01 * m10) * (m22 * m33 - m23 * m32)
    +(m02 * m10 - m00 * m12) * (m21 * m33 - m23 * m31)
    +(m00 * m13 - m03 * m10) * (m21 * m32 - m22 * m31)
    +(m01 * m12 - m02 * m11) * (m20 * m33 - m23 * m30)
    +(m03 * m11 - m01 * m13) * (m20 * m32 - m22 * m30)
    +(m02 * m13 - m03 * m12) * (m20 * m31 - m21 * m30)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#determinant3x3()
     */
  override fun determinant3x3(): Float {
    return ((m00 * m11 - m01 * m10) * m22
        + (m02 * m10 - m00 * m12) * m21
        + (m01 * m12 - m02 * m11) * m20)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#determinantAffine()
     */
  override fun determinantAffine(): Float {
    return ((m00 * m11 - m01 * m10) * m22
        + (m02 * m10 - m00 * m12) * m21
        + (m01 * m12 - m02 * m11) * m20)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#invert(Matrix4f)
     */
  override fun invert(dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) {
      return dest.identity()
    } else if (properties and Matrix4fc.PROPERTY_TRANSLATION != 0)
      return invertTranslation(dest)
    else if (properties and Matrix4fc.PROPERTY_ORTHONORMAL != 0)
      return invertOrthonormal(dest)
    else if (properties and Matrix4fc.PROPERTY_AFFINE != 0)
      return invertAffine(dest)
    else if (properties and Matrix4fc.PROPERTY_PERSPECTIVE != 0)
      return invertPerspective(dest)
    return invertGeneric(dest)
  }

  private fun invertTranslation(dest: Matrix4f): Matrix4f {
    if (dest !== this)
      dest.set(this)
    dest.m30 = -m30
    dest.m31 = -m31
    dest.m32 = -m32
    dest._properties(Matrix4fc.PROPERTY_AFFINE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt())
    return dest
  }

  private fun invertOrthonormal(dest: Matrix4f): Matrix4f {
    val nm30 = -(m00 * m30 + m01 * m31 + m02 * m32)
    val nm31 = -(m10 * m30 + m11 * m31 + m12 * m32)
    val nm32 = -(m20 * m30 + m21 * m31 + m22 * m32)
    val m01 = this.m01
    val m02 = this.m02
    val m12 = this.m12
    dest._m00(m00)
    dest._m01(m10)
    dest._m02(m20)
    dest._m03(0.0f)
    dest._m10(m01)
    dest._m11(m11)
    dest._m12(m21)
    dest._m13(0.0f)
    dest._m20(m02)
    dest._m21(m12)
    dest._m22(m22)
    dest._m23(0.0f)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(1.0f)
    dest._properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return dest
  }

  private fun invertGeneric(dest: Matrix4f): Matrix4f {
    val a = m00 * m11 - m01 * m10
    val b = m00 * m12 - m02 * m10
    val c = m00 * m13 - m03 * m10
    val d = m01 * m12 - m02 * m11
    val e = m01 * m13 - m03 * m11
    val f = m02 * m13 - m03 * m12
    val g = m20 * m31 - m21 * m30
    val h = m20 * m32 - m22 * m30
    val i = m20 * m33 - m23 * m30
    val j = m21 * m32 - m22 * m31
    val k = m21 * m33 - m23 * m31
    val l = m22 * m33 - m23 * m32
    var det = a * l - b * k + c * j + d * i - e * h + f * g
    val nm00: Float
    val nm01: Float
    val nm02: Float
    val nm03: Float
    val nm10: Float
    val nm11: Float
    val nm12: Float
    val nm13: Float
    val nm20: Float
    val nm21: Float
    val nm22: Float
    val nm23: Float
    val nm30: Float
    val nm31: Float
    val nm32: Float
    val nm33: Float
    det = 1.0f / det
    nm00 = (m11 * l - m12 * k + m13 * j) * det
    nm01 = (-m01 * l + m02 * k - m03 * j) * det
    nm02 = (m31 * f - m32 * e + m33 * d) * det
    nm03 = (-m21 * f + m22 * e - m23 * d) * det
    nm10 = (-m10 * l + m12 * i - m13 * h) * det
    nm11 = (m00 * l - m02 * i + m03 * h) * det
    nm12 = (-m30 * f + m32 * c - m33 * b) * det
    nm13 = (m20 * f - m22 * c + m23 * b) * det
    nm20 = (m10 * k - m11 * i + m13 * g) * det
    nm21 = (-m00 * k + m01 * i - m03 * g) * det
    nm22 = (m30 * e - m31 * c + m33 * a) * det
    nm23 = (-m20 * e + m21 * c - m23 * a) * det
    nm30 = (-m10 * j + m11 * h - m12 * g) * det
    nm31 = (m00 * j - m01 * h + m02 * g) * det
    nm32 = (-m30 * d + m31 * b - m32 * a) * det
    nm33 = (m20 * d - m21 * b + m22 * a) * det
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(0)
    return dest
  }

  /**
   * Invert this matrix.
   *
   *
   * If `this` matrix represents an [affine][.isAffine] transformation, such as translation, rotation, scaling and shearing,
   * and thus its last row is equal to <tt>(0, 0, 0, 1)</tt>, then [.invertAffine] can be used instead of this method.
   *
   * @see .invertAffine
   * @return a matrix holding the result
   */
  fun invert(): Matrix4f {
    return invert(thisOrNew())
  }

  /**
   * If `this` is a perspective projection matrix obtained via one of the [perspective()][.perspective] methods
   * or via [setPerspective()][.setPerspective], that is, if `this` is a symmetrical perspective frustum transformation,
   * then this method builds the inverse of `this` and stores it into the given `dest`.
   *
   *
   * This method can be used to quickly obtain the inverse of a perspective projection matrix when being obtained via [perspective()][.perspective].
   *
   * @see .perspective
   * @param dest
   * will hold the inverse of `this`
   * @return dest
   */
  override fun invertPerspective(dest: Matrix4f): Matrix4f {
    val a = 1.0f / (m00 * m11)
    val l = -1.0f / (m23 * m32)
    dest[m11 * a, 0f, 0f, 0f, 0f, m00 * a, 0f, 0f, 0f, 0f, 0f, -m23 * l, 0f, 0f, -m32 * l] = m22 * l
    dest._properties(0)
    return dest
  }

  /**
   * If `this` is a perspective projection matrix obtained via one of the [perspective()][.perspective] methods
   * or via [setPerspective()][.setPerspective], that is, if `this` is a symmetrical perspective frustum transformation,
   * then this method builds the inverse of `this`.
   *
   *
   * This method can be used to quickly obtain the inverse of a perspective projection matrix when being obtained via [perspective()][.perspective].
   *
   * @see .perspective
   * @return a matrix holding the result
   */
  fun invertPerspective(): Matrix4f {
    return invertPerspective(thisOrNew())
  }

  /**
   * If `this` is an arbitrary perspective projection matrix obtained via one of the [frustum()][.frustum]  methods
   * or via [setFrustum()][.setFrustum],
   * then this method builds the inverse of `this` and stores it into the given `dest`.
   *
   *
   * This method can be used to quickly obtain the inverse of a perspective projection matrix.
   *
   *
   * If this matrix represents a symmetric perspective frustum transformation, as obtained via [perspective()][.perspective], then
   * [.invertPerspective] should be used instead.
   *
   * @see .frustum
   * @see .invertPerspective
   * @param dest
   * will hold the inverse of `this`
   * @return dest
   */
  override fun invertFrustum(dest: Matrix4f): Matrix4f {
    val invM00 = 1.0f / m00
    val invM11 = 1.0f / m11
    val invM23 = 1.0f / m23
    val invM32 = 1.0f / m32
    dest[invM00, 0f, 0f, 0f, 0f, invM11, 0f, 0f, 0f, 0f, 0f, invM32, -m20 * invM00 * invM23, -m21 * invM11 * invM23, invM23] = -m22 * invM23 * invM32
    return dest
  }

  /**
   * If `this` is an arbitrary perspective projection matrix obtained via one of the [frustum()][.frustum]  methods
   * or via [setFrustum()][.setFrustum],
   * then this method builds the inverse of `this`.
   *
   *
   * This method can be used to quickly obtain the inverse of a perspective projection matrix.
   *
   *
   * If this matrix represents a symmetric perspective frustum transformation, as obtained via [perspective()][.perspective], then
   * [.invertPerspective] should be used instead.
   *
   * @see .frustum
   * @see .invertPerspective
   * @return a matrix holding the result
   */
  fun invertFrustum(): Matrix4f {
    return invertFrustum(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#invertOrtho(Matrix4f)
     */
  override fun invertOrtho(dest: Matrix4f): Matrix4f {
    val invM00 = 1.0f / m00
    val invM11 = 1.0f / m11
    val invM22 = 1.0f / m22
    dest[invM00, 0f, 0f, 0f, 0f, invM11, 0f, 0f, 0f, 0f, invM22, 0f, -m30 * invM00, -m31 * invM11, -m32 * invM22] = 1f
    dest._properties(Matrix4fc.PROPERTY_AFFINE or (this.properties and Matrix4fc.PROPERTY_ORTHONORMAL))
    return dest
  }

  /**
   * Invert `this` orthographic projection matrix.
   *
   *
   * This method can be used to quickly obtain the inverse of an orthographic projection matrix.
   *
   * @return a matrix holding the result
   */
  fun invertOrtho(): Matrix4f {
    return invertOrtho(thisOrNew())
  }

  /**
   * If `this` is a perspective projection matrix obtained via one of the [perspective()][.perspective] methods
   * or via [setPerspective()][.setPerspective], that is, if `this` is a symmetrical perspective frustum transformation
   * and the given `view` matrix is [affine][.isAffine] and has unit scaling (for example by being obtained via [lookAt()][.lookAt]),
   * then this method builds the inverse of <tt>this * view</tt> and stores it into the given `dest`.
   *
   *
   * This method can be used to quickly obtain the inverse of the combination of the view and projection matrices, when both were obtained
   * via the common methods [perspective()][.perspective] and [lookAt()][.lookAt] or
   * other methods, that build affine matrices, such as [translate][.translate] and [.rotate], except for [scale()][.scale].
   *
   *
   * For the special cases of the matrices `this` and `view` mentioned above, this method is equivalent to the following code:
   * <pre>
   * dest.set(this).mul(view).invert();
  </pre> *
   *
   * @param view
   * the view transformation (must be [affine][.isAffine] and have unit scaling)
   * @param dest
   * will hold the inverse of <tt>this * view</tt>
   * @return dest
   */
  override fun invertPerspectiveView(view: Matrix4fc, dest: Matrix4f): Matrix4f {
    val a = 1.0f / (m00 * m11)
    val l = -1.0f / (m23 * m32)
    val pm00 = m11 * a
    val pm11 = m00 * a
    val pm23 = -m23 * l
    val pm32 = -m32 * l
    val pm33 = m22 * l
    val vm30 = -view.m00() * view.m30() - view.m01() * view.m31() - view.m02() * view.m32()
    val vm31 = -view.m10() * view.m30() - view.m11() * view.m31() - view.m12() * view.m32()
    val vm32 = -view.m20() * view.m30() - view.m21() * view.m31() - view.m22() * view.m32()
    val nm00 = view.m00() * pm00
    val nm01 = view.m10() * pm00
    val nm02 = view.m20() * pm00
    val nm10 = view.m01() * pm11
    val nm11 = view.m11() * pm11
    val nm12 = view.m21() * pm11
    val nm20 = vm30 * pm23
    val nm21 = vm31 * pm23
    val nm22 = vm32 * pm23
    val nm30 = view.m02() * pm32 + vm30 * pm33
    val nm31 = view.m12() * pm32 + vm31 * pm33
    val nm32 = view.m22() * pm32 + vm32 * pm33
    dest.m00 = nm00
    dest.m01 = nm01
    dest.m02 = nm02
    dest.m03 = 0.0f
    dest.m10 = nm10
    dest.m11 = nm11
    dest.m12 = nm12
    dest.m13 = 0.0f
    dest.m20 = nm20
    dest.m21 = nm21
    dest.m22 = nm22
    dest.m23 = pm23
    dest.m30 = nm30
    dest.m31 = nm31
    dest.m32 = nm32
    dest.m33 = pm33
    dest._properties(0)
    return dest
  }

  /**
   * If `this` is a perspective projection matrix obtained via one of the [perspective()][.perspective] methods
   * or via [setPerspective()][.setPerspective], that is, if `this` is a symmetrical perspective frustum transformation
   * and the given `view` matrix has unit scaling,
   * then this method builds the inverse of <tt>this * view</tt> and stores it into the given `dest`.
   *
   *
   * This method can be used to quickly obtain the inverse of the combination of the view and projection matrices, when both were obtained
   * via the common methods [perspective()][.perspective] and [lookAt()][.lookAt] or
   * other methods, that build affine matrices, such as [translate][.translate] and [.rotate], except for [scale()][.scale].
   *
   *
   * For the special cases of the matrices `this` and `view` mentioned above, this method is equivalent to the following code:
   * <pre>
   * dest.set(this).mul(view).invert();
  </pre> *
   *
   * @param view
   * the view transformation (must have unit scaling)
   * @param dest
   * will hold the inverse of <tt>this * view</tt>
   * @return dest
   */
  override fun invertPerspectiveView(view: Matrix4x3fc, dest: Matrix4f): Matrix4f {
    val a = 1.0f / (m00 * m11)
    val l = -1.0f / (m23 * m32)
    val pm00 = m11 * a
    val pm11 = m00 * a
    val pm23 = -m23 * l
    val pm32 = -m32 * l
    val pm33 = m22 * l
    val vm30 = -view.m00() * view.m30() - view.m01() * view.m31() - view.m02() * view.m32()
    val vm31 = -view.m10() * view.m30() - view.m11() * view.m31() - view.m12() * view.m32()
    val vm32 = -view.m20() * view.m30() - view.m21() * view.m31() - view.m22() * view.m32()
    val nm00 = view.m00() * pm00
    val nm01 = view.m10() * pm00
    val nm02 = view.m20() * pm00
    val nm10 = view.m01() * pm11
    val nm11 = view.m11() * pm11
    val nm12 = view.m21() * pm11
    val nm20 = vm30 * pm23
    val nm21 = vm31 * pm23
    val nm22 = vm32 * pm23
    val nm30 = view.m02() * pm32 + vm30 * pm33
    val nm31 = view.m12() * pm32 + vm31 * pm33
    val nm32 = view.m22() * pm32 + vm32 * pm33
    dest.m00 = nm00
    dest.m01 = nm01
    dest.m02 = nm02
    dest.m03 = 0.0f
    dest.m10 = nm10
    dest.m11 = nm11
    dest.m12 = nm12
    dest.m13 = 0.0f
    dest.m20 = nm20
    dest.m21 = nm21
    dest.m22 = nm22
    dest.m23 = pm23
    dest.m30 = nm30
    dest.m31 = nm31
    dest.m32 = nm32
    dest.m33 = pm33
    dest._properties(0)
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#invertAffine(Matrix4f)
     */
  override fun invertAffine(dest: Matrix4f): Matrix4f {
    val m11m00 = m00 * m11
    val m10m01 = m01 * m10
    val m10m02 = m02 * m10
    val m12m00 = m00 * m12
    val m12m01 = m01 * m12
    val m11m02 = m02 * m11
    val det = (m11m00 - m10m01) * m22 + (m10m02 - m12m00) * m21 + (m12m01 - m11m02) * m20
    val s = 1.0f / det
    val nm00: Float
    val nm01: Float
    val nm02: Float
    val nm10: Float
    val nm11: Float
    val nm12: Float
    val nm20: Float
    val nm21: Float
    val nm22: Float
    val nm30: Float
    val nm31: Float
    val nm32: Float
    val m10m22 = m10 * m22
    val m10m21 = m10 * m21
    val m11m22 = m11 * m22
    val m11m20 = m11 * m20
    val m12m21 = m12 * m21
    val m12m20 = m12 * m20
    val m20m02 = m20 * m02
    val m20m01 = m20 * m01
    val m21m02 = m21 * m02
    val m21m00 = m21 * m00
    val m22m01 = m22 * m01
    val m22m00 = m22 * m00
    nm00 = (m11m22 - m12m21) * s
    nm01 = (m21m02 - m22m01) * s
    nm02 = (m12m01 - m11m02) * s
    nm10 = (m12m20 - m10m22) * s
    nm11 = (m22m00 - m20m02) * s
    nm12 = (m10m02 - m12m00) * s
    nm20 = (m10m21 - m11m20) * s
    nm21 = (m20m01 - m21m00) * s
    nm22 = (m11m00 - m10m01) * s
    nm30 = (m10m22 * m31 - m10m21 * m32 + m11m20 * m32 - m11m22 * m30 + m12m21 * m30 - m12m20 * m31) * s
    nm31 = (m20m02 * m31 - m20m01 * m32 + m21m00 * m32 - m21m02 * m30 + m22m01 * m30 - m22m00 * m31) * s
    nm32 = (m11m02 * m30 - m12m01 * m30 + m12m00 * m31 - m10m02 * m31 + m10m01 * m32 - m11m00 * m32) * s
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(0.0f)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(0.0f)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(0.0f)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(1.0f)
    dest._properties(Matrix4fc.PROPERTY_AFFINE.toInt())
    return dest
  }

  /**
   * Invert this matrix by assuming that it is an [affine][.isAffine] transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>).
   *
   * @return a matrix holding the result
   */
  fun invertAffine(): Matrix4f {
    return invertAffine(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transpose(Matrix4f)
     */
  override fun transpose(dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.identity() else transposeGeneric(dest)
  }

  private fun transposeGeneric(dest: Matrix4f): Matrix4f {
    val nm00 = m00
    val nm01 = m10
    val nm02 = m20
    val nm03 = m30
    val nm10 = m01
    val nm11 = m11
    val nm12 = m21
    val nm13 = m31
    val nm20 = m02
    val nm21 = m12
    val nm22 = m22
    val nm23 = m32
    val nm30 = m03
    val nm31 = m13
    val nm32 = m23
    val nm33 = m33
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(0)
    return dest
  }

  /**
   * Transpose only the upper left 3x3 submatrix of this matrix.
   *
   *
   * All other matrix elements are left unchanged.
   *
   * @return a matrix holding the result
   */
  fun transpose3x3(): Matrix4f {
    return transpose3x3(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transpose3x3(Matrix4f)
     */
  override fun transpose3x3(dest: Matrix4f): Matrix4f {
    val nm00 = m00
    val nm01 = m10
    val nm02 = m20
    val nm10 = m01
    val nm11 = m11
    val nm12 = m21
    val nm20 = m02
    val nm21 = m12
    val nm22 = m22
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._properties(this.properties and (Matrix4fc.PROPERTY_AFFINE.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()))
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transpose3x3(org.joml.Matrix3f)
     */
  override fun transpose3x3(dest: Matrix3f): Matrix3f {
    dest.m00(m00)
    dest.m01(m10)
    dest.m02(m20)
    dest.m10(m01)
    dest.m11(m11)
    dest.m12(m21)
    dest.m20(m02)
    dest.m21(m12)
    dest.m22(m22)
    return dest
  }

  /**
   * Transpose this matrix.
   *
   * @return a matrix holding the result
   */
  fun transpose(): Matrix4f {
    return transpose(thisOrNew())
  }

  /**
   * Set this matrix to be a simple translation matrix.
   *
   *
   * The resulting matrix can be multiplied against another transformation
   * matrix to obtain an additional translation.
   *
   *
   * In order to post-multiply a translation transformation directly to a
   * matrix, use [translate()][.translate] instead.
   *
   * @see .translate
   * @param x
   * the offset to translate in x
   * @param y
   * the offset to translate in y
   * @param z
   * the offset to translate in z
   * @return this
   */
  fun translation(x: Float, y: Float, z: Float): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m30(x)
    this._m31(y)
    this._m32(z)
    _properties(Matrix4fc.PROPERTY_AFFINE.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt())
    return this
  }

  /**
   * Set this matrix to be a simple translation matrix.
   *
   *
   * The resulting matrix can be multiplied against another transformation
   * matrix to obtain an additional translation.
   *
   *
   * In order to post-multiply a translation transformation directly to a
   * matrix, use [translate()][.translate] instead.
   *
   * @see .translate
   * @param offset
   * the offsets in x, y and z to translate
   * @return this
   */
  fun translation(offset: Vector3fc): Matrix4f {
    return translation(offset.x, offset.y, offset.z)
  }

  /**
   * Set only the translation components <tt>(m30, m31, m32)</tt> of this matrix to the given values <tt>(x, y, z)</tt>.
   *
   *
   * Note that this will only work properly for orthogonal matrices (without any perspective).
   *
   *
   * To build a translation matrix instead, use [.translation].
   * To apply a translation, use [.translate].
   *
   * @see .translation
   * @see .translate
   * @param x
   * the offset to translate in x
   * @param y
   * the offset to translate in y
   * @param z
   * the offset to translate in z
   * @return this
   */
  fun setTranslation(x: Float, y: Float, z: Float): Matrix4f {
    this._m30(x)
    this._m31(y)
    this._m32(z)
    properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE or Matrix4fc.PROPERTY_IDENTITY).inv()
    return this
  }

  /**
   * Set only the translation components <tt>(m30, m31, m32)</tt> of this matrix to the values <tt>(xyz.x, xyz.y, xyz.z)</tt>.
   *
   *
   * Note that this will only work properly for orthogonal matrices (without any perspective).
   *
   *
   * To build a translation matrix instead, use [.translation].
   * To apply a translation, use [.translate].
   *
   * @see .translation
   * @see .translate
   * @param xyz
   * the units to translate in <tt>(x, y, z)</tt>
   * @return this
   */
  fun setTranslation(xyz: Vector3fc): Matrix4f {
    return setTranslation(xyz.x, xyz.y, xyz.z)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getTranslation(Vector3m)
     */
  override fun getTranslation(dest: Vector3m): Vector3m {
    dest.x = m30
    dest.y = m31
    dest.z = m32
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getScale(Vector3m)
     */
  override fun getScale(dest: Vector3m): Vector3m {
    dest.x = Math.sqrt((m00 * m00 + m01 * m01 + m02 * m02).toDouble()).toFloat()
    dest.y = Math.sqrt((m10 * m10 + m11 * m11 + m12 * m12).toDouble()).toFloat()
    dest.z = Math.sqrt((m20 * m20 + m21 * m21 + m22 * m22).toDouble()).toFloat()
    return dest
  }

  /**
   * Return a string representation of this matrix.
   *
   *
   * This method creates a new [DecimalFormat] on every invocation with the format string "<tt>0.000E0;-</tt>".
   *
   * @return the string representation
   */
  override fun toString(): String {
    val formatter = DecimalFormat(" 0.000E0;-")
    val str = toString(formatter)
    val res = StringBuffer()
    var eIndex = Integer.MIN_VALUE
    for (i in 0 until str.length) {
      val c = str[i]
      if (c == 'E') {
        eIndex = i
      } else if (c == ' ' && eIndex == i - 1) {
        // workaround Java 1.4 DecimalFormat bug
        res.append('+')
        continue
      } else if (Character.isDigit(c) && eIndex == i - 1) {
        res.append('+')
      }
      res.append(c)
    }
    return res.toString()
  }

  /**
   * Return a string representation of this matrix by formatting the matrix elements with the given [NumberFormat].
   *
   * @param formatter
   * the [NumberFormat] used to format the matrix values with
   * @return the string representation
   */
  fun toString(formatter: NumberFormat): String {
    return (formatter.format(m00.toDouble()) + " " + formatter.format(m10.toDouble()) + " " + formatter.format(m20.toDouble()) + " " + formatter.format(m30.toDouble()) + "\n"
        + formatter.format(m01.toDouble()) + " " + formatter.format(m11.toDouble()) + " " + formatter.format(m21.toDouble()) + " " + formatter.format(m31.toDouble()) + "\n"
        + formatter.format(m02.toDouble()) + " " + formatter.format(m12.toDouble()) + " " + formatter.format(m22.toDouble()) + " " + formatter.format(m32.toDouble()) + "\n"
        + formatter.format(m03.toDouble()) + " " + formatter.format(m13.toDouble()) + " " + formatter.format(m23.toDouble()) + " " + formatter.format(m33.toDouble()) + "\n")
  }

  /**
   * Get the current values of `this` matrix and store them into
   * `dest`.
   *
   *
   * This is the reverse method of [.set] and allows to obtain
   * intermediate calculation results when chaining multiple transformations.
   *
   * @see .set
   * @param dest
   * the destination matrix
   * @return the passed in destination
   */
  override fun get(dest: Matrix4f): Matrix4f {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get4x3(org.joml.Matrix4x3f)
     */
  override fun get4x3(dest: Matrix4x3f): Matrix4x3f {
    return dest.set(this)
  }

  /**
   * Get the current values of `this` matrix and store them into
   * `dest`.
   *
   *
   * This is the reverse method of [.set] and allows to obtain
   * intermediate calculation results when chaining multiple transformations.
   *
   * @see .set
   * @param dest
   * the destination matrix
   * @return the passed in destination
   */
  override fun get(dest: Matrix4d): Matrix4d {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get3x3(org.joml.Matrix3f)
     */
  override fun get3x3(dest: Matrix3f): Matrix3f {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get3x3(org.joml.Matrix3d)
     */
  override fun get3x3(dest: Matrix3d): Matrix3d {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getRotation(org.joml.AxisAngle4f)
     */
  override fun getRotation(dest: AxisAngle4f): AxisAngle4f {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getRotation(org.joml.AxisAngle4d)
     */
  override fun getRotation(dest: AxisAngle4d): AxisAngle4d {
    return dest.set(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getUnnormalizedRotation(Quaternionf)
     */
  override fun getUnnormalizedRotation(dest: Quaternionf): Quaternionf {
    return dest.setFromUnnormalized(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getNormalizedRotation(Quaternionf)
     */
  override fun getNormalizedRotation(dest: Quaternionf): Quaternionf {
    return dest.setFromNormalized(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getUnnormalizedRotation(org.joml.Quaterniond)
     */
  override fun getUnnormalizedRotation(dest: Quaterniond): Quaterniond {
    return dest.setFromUnnormalized(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getNormalizedRotation(org.joml.Quaterniond)
     */
  override fun getNormalizedRotation(dest: Quaterniond): Quaterniond {
    return dest.setFromNormalized(this)
  }


  /* (non-Javadoc)
     * @see Matrix4fc#get(java.nio.FloatBuffer)
     */
  override fun get(buffer: FloatBuffer): FloatBuffer {
    return get(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get(int, java.nio.FloatBuffer)
     */
  override fun get(index: Int, buffer: FloatBuffer): FloatBuffer {
    MemUtil.INSTANCE.put(this, index, buffer)
    return buffer
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get(java.nio.ByteBuffer)
     */
  override fun get(buffer: ByteBuffer): ByteBuffer {
    return get(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get(int, java.nio.ByteBuffer)
     */
  override fun get(index: Int, buffer: ByteBuffer): ByteBuffer {
    MemUtil.INSTANCE.put(this, index, buffer)
    return buffer
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getTransposed(java.nio.FloatBuffer)
     */
  override fun getTransposed(buffer: FloatBuffer): FloatBuffer {
    return getTransposed(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getTransposed(int, java.nio.FloatBuffer)
     */
  override fun getTransposed(index: Int, buffer: FloatBuffer): FloatBuffer {
    MemUtil.INSTANCE.putTransposed(this, index, buffer)
    return buffer
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getTransposed(java.nio.ByteBuffer)
     */
  override fun getTransposed(buffer: ByteBuffer): ByteBuffer {
    return getTransposed(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getTransposed(int, java.nio.ByteBuffer)
     */
  override fun getTransposed(index: Int, buffer: ByteBuffer): ByteBuffer {
    MemUtil.INSTANCE.putTransposed(this, index, buffer)
    return buffer
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get4x3Transposed(java.nio.FloatBuffer)
     */
  override fun get4x3Transposed(buffer: FloatBuffer): FloatBuffer {
    return get4x3Transposed(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get4x3Transposed(int, java.nio.FloatBuffer)
     */
  override fun get4x3Transposed(index: Int, buffer: FloatBuffer): FloatBuffer {
    MemUtil.INSTANCE.put4x3Transposed(this, index, buffer)
    return buffer
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get4x3Transposed(java.nio.ByteBuffer)
     */
  override fun get4x3Transposed(buffer: ByteBuffer): ByteBuffer {
    return get4x3Transposed(buffer.position(), buffer)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get4x3Transposed(int, java.nio.ByteBuffer)
     */
  override fun get4x3Transposed(index: Int, buffer: ByteBuffer): ByteBuffer {
    MemUtil.INSTANCE.put4x3Transposed(this, index, buffer)
    return buffer
  }

  override fun getToAddress(address: Long): Matrix4fc {
    if (Options.NO_UNSAFE)
      throw UnsupportedOperationException("Not supported when using joml.nounsafe")
    val unsafe = MemUtil.INSTANCE as MemUtil.MemUtilUnsafe
    unsafe.put(this, address)
    return this
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get(float[], int)
     */
  override fun get(arr: FloatArray, offset: Int): FloatArray {
    MemUtil.INSTANCE.copy(this, arr, offset)
    return arr
  }

  /* (non-Javadoc)
     * @see Matrix4fc#get(float[])
     */
  override fun get(arr: FloatArray): FloatArray {
    return get(arr, 0)
  }

  /**
   * Set all the values within this matrix to `0`.
   *
   * @return a matrix holding the result
   */
  fun zero(): Matrix4f {
    val dest = thisOrNew()
    MemUtil.INSTANCE.zero(dest)
    _properties(0)
    return dest
  }

  /**
   * Set this matrix to be a simple scale matrix, which scales all axes uniformly by the given factor.
   *
   *
   * The resulting matrix can be multiplied against another transformation
   * matrix to obtain an additional scaling.
   *
   *
   * In order to post-multiply a scaling transformation directly to a
   * matrix, use [scale()][.scale] instead.
   *
   * @see .scale
   * @param factor
   * the scale factor in x, y and z
   * @return this
   */
  fun scaling(factor: Float): Matrix4f {
    return scaling(factor, factor, factor)
  }

  /**
   * Set this matrix to be a simple scale matrix.
   *
   *
   * The resulting matrix can be multiplied against another transformation
   * matrix to obtain an additional scaling.
   *
   *
   * In order to post-multiply a scaling transformation directly to a
   * matrix, use [scale()][.scale] instead.
   *
   * @see .scale
   * @param x
   * the scale in x
   * @param y
   * the scale in y
   * @param z
   * the scale in z
   * @return this
   */
  fun scaling(x: Float, y: Float, z: Float): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00(x)
    this._m11(y)
    this._m22(z)
    val one = Math.abs(x) == 1.0f && Math.abs(y) == 1.0f && Math.abs(z) == 1.0f
    _properties(Matrix4fc.PROPERTY_AFFINE or if (one) Matrix4fc.PROPERTY_ORTHONORMAL else 0)
    return this
  }

  /**
   * Set this matrix to be a simple scale matrix which scales the base axes by <tt>xyz.x</tt>, <tt>xyz.y</tt> and <tt>xyz.z</tt> respectively.
   *
   *
   * The resulting matrix can be multiplied against another transformation
   * matrix to obtain an additional scaling.
   *
   *
   * In order to post-multiply a scaling transformation directly to a
   * matrix use [scale()][.scale] instead.
   *
   * @see .scale
   * @param xyz
   * the scale in x, y and z respectively
   * @return this
   */
  fun scaling(xyz: Vector3fc): Matrix4f {
    return scaling(xyz.x, xyz.y, xyz.z)
  }

  /**
   * Set this matrix to a rotation matrix which rotates the given radians about a given axis.
   *
   *
   * The axis described by the `axis` vector needs to be a unit vector.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * The resulting matrix can be multiplied against another transformation
   * matrix to obtain an additional rotation.
   *
   *
   * In order to post-multiply a rotation transformation directly to a
   * matrix, use [rotate()][.rotate] instead.
   *
   * @see .rotate
   * @param angle
   * the angle in radians
   * @param axis
   * the axis to rotate about (needs to be [normalized][Vector3m.normalize])
   * @return this
   */
  fun rotation(angle: Float, axis: Vector3fc): Matrix4f {
    return rotation(angle, axis.x, axis.y, axis.z)
  }

  /**
   * Set this matrix to a rotation transformation using the given [AxisAngle4f].
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * The resulting matrix can be multiplied against another transformation
   * matrix to obtain an additional rotation.
   *
   *
   * In order to apply the rotation transformation to an existing transformation,
   * use [rotate()][.rotate] instead.
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle)
   *
   * @see .rotate
   * @param axisAngle
   * the [AxisAngle4f] (needs to be [normalized][AxisAngle4f.normalize])
   * @return this
   */
  fun rotation(axisAngle: AxisAngle4f): Matrix4f {
    return rotation(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z)
  }

  /**
   * Set this matrix to a rotation matrix which rotates the given radians about a given axis.
   *
   *
   * The axis described by the three components needs to be a unit vector.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * The resulting matrix can be multiplied against another transformation
   * matrix to obtain an additional rotation.
   *
   *
   * In order to apply the rotation transformation to an existing transformation,
   * use [rotate()][.rotate] instead.
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotate
   * @param angle
   * the angle in radians
   * @param x
   * the x-component of the rotation axis
   * @param y
   * the y-component of the rotation axis
   * @param z
   * the z-component of the rotation axis
   * @return this
   */
  fun rotation(angle: Float, x: Float, y: Float, z: Float): Matrix4f {
    val sin = Math.sin(angle.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val C = 1.0f - cos
    val xy = x * y
    val xz = x * z
    val yz = y * z
    this._m00(cos + x * x * C)
    this._m10(xy * C - z * sin)
    this._m20(xz * C + y * sin)
    this._m30(0.0f)
    this._m01(xy * C + z * sin)
    this._m11(cos + y * y * C)
    this._m21(yz * C - x * sin)
    this._m31(0.0f)
    this._m02(xz * C - y * sin)
    this._m12(yz * C + x * sin)
    this._m22(cos + z * z * C)
    this._m32(0.0f)
    this._m03(0.0f)
    this._m13(0.0f)
    this._m23(0.0f)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to a rotation transformation about the X axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations)
   *
   * @param ang
   * the angle in radians
   * @return this
   */
  fun rotationX(ang: Float): Matrix4f {
    val sin: Float
    val cos: Float
    sin = Math.sin(ang.toDouble()).toFloat()
    cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m11(cos)
    this._m12(sin)
    this._m21(-sin)
    this._m22(cos)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to a rotation transformation about the Y axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations)
   *
   * @param ang
   * the angle in radians
   * @return this
   */
  fun rotationY(ang: Float): Matrix4f {
    val sin: Float
    val cos: Float
    sin = Math.sin(ang.toDouble()).toFloat()
    cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00(cos)
    this._m02(-sin)
    this._m20(sin)
    this._m22(cos)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to a rotation transformation about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations)
   *
   * @param ang
   * the angle in radians
   * @return this
   */
  fun rotationZ(ang: Float): Matrix4f {
    val sin: Float
    val cos: Float
    sin = Math.sin(ang.toDouble()).toFloat()
    cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00(cos)
    this._m01(sin)
    this._m10(-sin)
    this._m11(cos)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to a rotation transformation about the Z axis to align the local <tt>+X</tt> towards <tt>(dirX, dirY)</tt>.
   *
   *
   * The vector <tt>(dirX, dirY)</tt> must be a unit vector.
   *
   * @param dirX
   * the x component of the normalized direction
   * @param dirY
   * the y component of the normalized direction
   * @return this
   */
  fun rotationTowardsXY(dirX: Float, dirY: Float): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00(dirY)
    this._m01(dirX)
    this._m10(-dirX)
    this._m11(dirY)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to a rotation of `angleX` radians about the X axis, followed by a rotation
   * of `angleY` radians about the Y axis and followed by a rotation of `angleZ` radians about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method is equivalent to calling: <tt>rotationX(angleX).rotateY(angleY).rotateZ(angleZ)</tt>
   *
   * @param angleX
   * the angle to rotate about X
   * @param angleY
   * the angle to rotate about Y
   * @param angleZ
   * the angle to rotate about Z
   * @return this
   */
  fun rotationXYZ(angleX: Float, angleY: Float, angleZ: Float): Matrix4f {
    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinX = -sinX
    val m_sinY = -sinY
    val m_sinZ = -sinZ

    // rotateX
    // rotateY
    val nm01 = m_sinX * m_sinY
    val nm02 = cosX * m_sinY
    this._m20(sinY)
    this._m21(m_sinX * cosY)
    this._m22(cosX * cosY)
    this._m23(0.0f)
    // rotateZ
    this._m00(cosY * cosZ)
    this._m01(nm01 * cosZ + cosX * sinZ)
    this._m02(nm02 * cosZ + sinX * sinZ)
    this._m03(0.0f)
    this._m10(cosY * m_sinZ)
    this._m11(nm01 * m_sinZ + cosX * cosZ)
    this._m12(nm02 * m_sinZ + sinX * cosZ)
    this._m13(0.0f)
    // set last column to identity
    this._m30(0.0f)
    this._m31(0.0f)
    this._m32(0.0f)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to a rotation of `angleZ` radians about the Z axis, followed by a rotation
   * of `angleY` radians about the Y axis and followed by a rotation of `angleX` radians about the X axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method is equivalent to calling: <tt>rotationZ(angleZ).rotateY(angleY).rotateX(angleX)</tt>
   *
   * @param angleZ
   * the angle to rotate about Z
   * @param angleY
   * the angle to rotate about Y
   * @param angleX
   * the angle to rotate about X
   * @return this
   */
  fun rotationZYX(angleZ: Float, angleY: Float, angleX: Float): Matrix4f {
    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinZ = -sinZ
    val m_sinY = -sinY
    val m_sinX = -sinX

    // rotateZ
    // rotateY
    val nm20 = cosZ * sinY
    val nm21 = sinZ * sinY
    this._m00(cosZ * cosY)
    this._m01(sinZ * cosY)
    this._m02(m_sinY)
    this._m03(0.0f)
    // rotateX
    this._m10(m_sinZ * cosX + nm20 * sinX)
    this._m11(cosZ * cosX + nm21 * sinX)
    this._m12(cosY * sinX)
    this._m13(0.0f)
    this._m20(m_sinZ * m_sinX + nm20 * cosX)
    this._m21(cosZ * m_sinX + nm21 * cosX)
    this._m22(cosY * cosX)
    this._m23(0.0f)
    // set last column to identity
    this._m30(0.0f)
    this._m31(0.0f)
    this._m32(0.0f)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to a rotation of `angleY` radians about the Y axis, followed by a rotation
   * of `angleX` radians about the X axis and followed by a rotation of `angleZ` radians about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method is equivalent to calling: <tt>rotationY(angleY).rotateX(angleX).rotateZ(angleZ)</tt>
   *
   * @param angleY
   * the angle to rotate about Y
   * @param angleX
   * the angle to rotate about X
   * @param angleZ
   * the angle to rotate about Z
   * @return this
   */
  fun rotationYXZ(angleY: Float, angleX: Float, angleZ: Float): Matrix4f {
    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinY = -sinY
    val m_sinX = -sinX
    val m_sinZ = -sinZ

    // rotateY
    // rotateX
    val nm10 = sinY * sinX
    val nm12 = cosY * sinX
    this._m20(sinY * cosX)
    this._m21(m_sinX)
    this._m22(cosY * cosX)
    this._m23(0.0f)
    // rotateZ
    this._m00(cosY * cosZ + nm10 * sinZ)
    this._m01(cosX * sinZ)
    this._m02(m_sinY * cosZ + nm12 * sinZ)
    this._m03(0.0f)
    this._m10(cosY * m_sinZ + nm10 * cosZ)
    this._m11(cosX * cosZ)
    this._m12(m_sinY * m_sinZ + nm12 * cosZ)
    this._m13(0.0f)
    // set last column to identity
    this._m30(0.0f)
    this._m31(0.0f)
    this._m32(0.0f)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set only the upper left 3x3 submatrix of this matrix to a rotation of `angleX` radians about the X axis, followed by a rotation
   * of `angleY` radians about the Y axis and followed by a rotation of `angleZ` radians about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   * @param angleX
   * the angle to rotate about X
   * @param angleY
   * the angle to rotate about Y
   * @param angleZ
   * the angle to rotate about Z
   * @return this
   */
  fun setRotationXYZ(angleX: Float, angleY: Float, angleZ: Float): Matrix4f {
    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinX = -sinX
    val m_sinY = -sinY
    val m_sinZ = -sinZ

    // rotateX
    // rotateY
    val nm01 = m_sinX * m_sinY
    val nm02 = cosX * m_sinY
    this._m20(sinY)
    this._m21(m_sinX * cosY)
    this._m22(cosX * cosY)
    // rotateZ
    this._m00(cosY * cosZ)
    this._m01(nm01 * cosZ + cosX * sinZ)
    this._m02(nm02 * cosZ + sinX * sinZ)
    this._m10(cosY * m_sinZ)
    this._m11(nm01 * m_sinZ + cosX * cosZ)
    this._m12(nm02 * m_sinZ + sinX * cosZ)
    properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv()
    return this
  }

  /**
   * Set only the upper left 3x3 submatrix of this matrix to a rotation of `angleZ` radians about the Z axis, followed by a rotation
   * of `angleY` radians about the Y axis and followed by a rotation of `angleX` radians about the X axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   * @param angleZ
   * the angle to rotate about Z
   * @param angleY
   * the angle to rotate about Y
   * @param angleX
   * the angle to rotate about X
   * @return this
   */
  fun setRotationZYX(angleZ: Float, angleY: Float, angleX: Float): Matrix4f {
    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinZ = -sinZ
    val m_sinY = -sinY
    val m_sinX = -sinX

    // rotateZ
    // rotateY
    val nm20 = cosZ * sinY
    val nm21 = sinZ * sinY
    this._m00(cosZ * cosY)
    this._m01(sinZ * cosY)
    this._m02(m_sinY)
    // rotateX
    this._m10(m_sinZ * cosX + nm20 * sinX)
    this._m11(cosZ * cosX + nm21 * sinX)
    this._m12(cosY * sinX)
    this._m20(m_sinZ * m_sinX + nm20 * cosX)
    this._m21(cosZ * m_sinX + nm21 * cosX)
    this._m22(cosY * cosX)
    properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv()
    return this
  }

  /**
   * Set only the upper left 3x3 submatrix of this matrix to a rotation of `angleY` radians about the Y axis, followed by a rotation
   * of `angleX` radians about the X axis and followed by a rotation of `angleZ` radians about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   * @param angleY
   * the angle to rotate about Y
   * @param angleX
   * the angle to rotate about X
   * @param angleZ
   * the angle to rotate about Z
   * @return this
   */
  fun setRotationYXZ(angleY: Float, angleX: Float, angleZ: Float): Matrix4f {
    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinY = -sinY
    val m_sinX = -sinX
    val m_sinZ = -sinZ

    // rotateY
    // rotateX
    val nm10 = sinY * sinX
    val nm12 = cosY * sinX
    this._m20(sinY * cosX)
    this._m21(m_sinX)
    this._m22(cosY * cosX)
    // rotateZ
    this._m00(cosY * cosZ + nm10 * sinZ)
    this._m01(cosX * sinZ)
    this._m02(m_sinY * cosZ + nm12 * sinZ)
    this._m10(cosY * m_sinZ + nm10 * cosZ)
    this._m11(cosX * cosZ)
    this._m12(m_sinY * m_sinZ + nm12 * cosZ)
    properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv()
    return this
  }

  /**
   * Set this matrix to the rotation transformation of the given [Quaternionfc].
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * The resulting matrix can be multiplied against another transformation
   * matrix to obtain an additional rotation.
   *
   *
   * In order to apply the rotation transformation to an existing transformation,
   * use [rotate()][.rotate] instead.
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
   *
   * @see .rotate
   * @param quat
   * the [Quaternionfc]
   * @return this
   */
  fun rotation(quat: Quaternionfc): Matrix4f {
    val w2 = quat.w * quat.w
    val x2 = quat.x * quat.x
    val y2 = quat.y * quat.y
    val z2 = quat.z * quat.z
    val zw = quat.z * quat.w
    val xy = quat.x * quat.y
    val xz = quat.x * quat.z
    val yw = quat.y * quat.w
    val yz = quat.y * quat.z
    val xw = quat.x * quat.w
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    _m00(w2 + x2 - z2 - y2)
    _m01(xy + zw + zw + xy)
    _m02(xz - yw + xz - yw)
    _m10(-zw + xy - zw + xy)
    _m11(y2 - z2 + w2 - x2)
    _m12(yz + yz + xw + xw)
    _m20(yw + xz + xz + yw)
    _m21(yz + yz - xw - xw)
    _m22(z2 - y2 - x2 + w2)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set `this` matrix to <tt>T * R * S</tt>, where <tt>T</tt> is a translation by the given <tt>(tx, ty, tz)</tt>,
   * <tt>R</tt> is a rotation transformation specified by the quaternion <tt>(qx, qy, qz, qw)</tt>, and <tt>S</tt> is a scaling transformation
   * which scales the three axes x, y and z by <tt>(sx, sy, sz)</tt>.
   *
   *
   * When transforming a vector by the resulting matrix the scaling transformation will be applied first, then the rotation and
   * at last the translation.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method is equivalent to calling: <tt>translation(tx, ty, tz).rotate(quat).scale(sx, sy, sz)</tt>
   *
   * @see .translation
   * @see .rotate
   * @see .scale
   * @param tx
   * the number of units by which to translate the x-component
   * @param ty
   * the number of units by which to translate the y-component
   * @param tz
   * the number of units by which to translate the z-component
   * @param qx
   * the x-coordinate of the vector part of the quaternion
   * @param qy
   * the y-coordinate of the vector part of the quaternion
   * @param qz
   * the z-coordinate of the vector part of the quaternion
   * @param qw
   * the scalar part of the quaternion
   * @param sx
   * the scaling factor for the x-axis
   * @param sy
   * the scaling factor for the y-axis
   * @param sz
   * the scaling factor for the z-axis
   * @return this
   */
  fun translationRotateScale(tx: Float, ty: Float, tz: Float,
                             qx: Float, qy: Float, qz: Float, qw: Float,
                             sx: Float, sy: Float, sz: Float): Matrix4f {
    val dqx = qx + qx
    val dqy = qy + qy
    val dqz = qz + qz
    val q00 = dqx * qx
    val q11 = dqy * qy
    val q22 = dqz * qz
    val q01 = dqx * qy
    val q02 = dqx * qz
    val q03 = dqx * qw
    val q12 = dqy * qz
    val q13 = dqy * qw
    val q23 = dqz * qw
    this._m00(sx - (q11 + q22) * sx)
    this._m01((q01 + q23) * sx)
    this._m02((q02 - q13) * sx)
    this._m03(0.0f)
    this._m10((q01 - q23) * sy)
    this._m11(sy - (q22 + q00) * sy)
    this._m12((q12 + q03) * sy)
    this._m13(0.0f)
    this._m20((q02 + q13) * sz)
    this._m21((q12 - q03) * sz)
    this._m22(sz - (q11 + q00) * sz)
    this._m23(0.0f)
    this._m30(tx)
    this._m31(ty)
    this._m32(tz)
    this._m33(1.0f)
    val one = Math.abs(sx) == 1.0f && Math.abs(sy) == 1.0f && Math.abs(sz) == 1.0f
    _properties(Matrix4fc.PROPERTY_AFFINE or if (one) Matrix4fc.PROPERTY_ORTHONORMAL else 0)
    return this
  }

  /**
   * Set `this` matrix to <tt>T * R * S</tt>, where <tt>T</tt> is the given `translation`,
   * <tt>R</tt> is a rotation transformation specified by the given quaternion, and <tt>S</tt> is a scaling transformation
   * which scales the axes by `scale`.
   *
   *
   * When transforming a vector by the resulting matrix the scaling transformation will be applied first, then the rotation and
   * at last the translation.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method is equivalent to calling: <tt>translation(translation).rotate(quat).scale(scale)</tt>
   *
   * @see .translation
   * @see .rotate
   * @see .scale
   * @param translation
   * the translation
   * @param quat
   * the quaternion representing a rotation
   * @param scale
   * the scaling factors
   * @return this
   */
  fun translationRotateScale(translation: Vector3fc,
                             quat: Quaternionfc,
                             scale: Vector3fc): Matrix4f {
    return translationRotateScale(translation.x, translation.y, translation.z, quat.x, quat.y, quat.z, quat.w, scale.x, scale.y, scale.z)
  }

  /**
   * Set `this` matrix to <tt>T * R * S</tt>, where <tt>T</tt> is a translation by the given <tt>(tx, ty, tz)</tt>,
   * <tt>R</tt> is a rotation transformation specified by the quaternion <tt>(qx, qy, qz, qw)</tt>, and <tt>S</tt> is a scaling transformation
   * which scales all three axes by `scale`.
   *
   *
   * When transforming a vector by the resulting matrix the scaling transformation will be applied first, then the rotation and
   * at last the translation.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method is equivalent to calling: <tt>translation(tx, ty, tz).rotate(quat).scale(scale)</tt>
   *
   * @see .translation
   * @see .rotate
   * @see .scale
   * @param tx
   * the number of units by which to translate the x-component
   * @param ty
   * the number of units by which to translate the y-component
   * @param tz
   * the number of units by which to translate the z-component
   * @param qx
   * the x-coordinate of the vector part of the quaternion
   * @param qy
   * the y-coordinate of the vector part of the quaternion
   * @param qz
   * the z-coordinate of the vector part of the quaternion
   * @param qw
   * the scalar part of the quaternion
   * @param scale
   * the scaling factor for all three axes
   * @return this
   */
  fun translationRotateScale(tx: Float, ty: Float, tz: Float,
                             qx: Float, qy: Float, qz: Float, qw: Float,
                             scale: Float): Matrix4f {
    return translationRotateScale(tx, ty, tz, qx, qy, qz, qw, scale, scale, scale)
  }

  /**
   * Set `this` matrix to <tt>T * R * S</tt>, where <tt>T</tt> is the given `translation`,
   * <tt>R</tt> is a rotation transformation specified by the given quaternion, and <tt>S</tt> is a scaling transformation
   * which scales all three axes by `scale`.
   *
   *
   * When transforming a vector by the resulting matrix the scaling transformation will be applied first, then the rotation and
   * at last the translation.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method is equivalent to calling: <tt>translation(translation).rotate(quat).scale(scale)</tt>
   *
   * @see .translation
   * @see .rotate
   * @see .scale
   * @param translation
   * the translation
   * @param quat
   * the quaternion representing a rotation
   * @param scale
   * the scaling factors
   * @return this
   */
  fun translationRotateScale(translation: Vector3fc,
                             quat: Quaternionfc,
                             scale: Float): Matrix4f {
    return translationRotateScale(translation.x, translation.y, translation.z, quat.x, quat.y, quat.z, quat.w, scale, scale, scale)
  }

  /**
   * Set `this` matrix to <tt>(T * R * S)<sup>-1</sup></tt>, where <tt>T</tt> is a translation by the given <tt>(tx, ty, tz)</tt>,
   * <tt>R</tt> is a rotation transformation specified by the quaternion <tt>(qx, qy, qz, qw)</tt>, and <tt>S</tt> is a scaling transformation
   * which scales the three axes x, y and z by <tt>(sx, sy, sz)</tt>.
   *
   *
   * This method is equivalent to calling: <tt>translationRotateScale(...).invert()</tt>
   *
   * @see .translationRotateScale
   * @see .invert
   * @param tx
   * the number of units by which to translate the x-component
   * @param ty
   * the number of units by which to translate the y-component
   * @param tz
   * the number of units by which to translate the z-component
   * @param qx
   * the x-coordinate of the vector part of the quaternion
   * @param qy
   * the y-coordinate of the vector part of the quaternion
   * @param qz
   * the z-coordinate of the vector part of the quaternion
   * @param qw
   * the scalar part of the quaternion
   * @param sx
   * the scaling factor for the x-axis
   * @param sy
   * the scaling factor for the y-axis
   * @param sz
   * the scaling factor for the z-axis
   * @return this
   */
  fun translationRotateScaleInvert(tx: Float, ty: Float, tz: Float,
                                   qx: Float, qy: Float, qz: Float, qw: Float,
                                   sx: Float, sy: Float, sz: Float): Matrix4f {
    val one = Math.abs(sx) == 1.0f && Math.abs(sy) == 1.0f && Math.abs(sz) == 1.0f
    if (one)
      return translationRotateScale(tx, ty, tz, qx, qy, qz, qw, sx, sy, sz).invertOrthonormal(this)
    val nqx = -qx
    val nqy = -qy
    val nqz = -qz
    val dqx = nqx + nqx
    val dqy = nqy + nqy
    val dqz = nqz + nqz
    val q00 = dqx * nqx
    val q11 = dqy * nqy
    val q22 = dqz * nqz
    val q01 = dqx * nqy
    val q02 = dqx * nqz
    val q03 = dqx * qw
    val q12 = dqy * nqz
    val q13 = dqy * qw
    val q23 = dqz * qw
    val isx = 1 / sx
    val isy = 1 / sy
    val isz = 1 / sz
    this._m00(isx * (1.0f - q11 - q22))
    this._m01(isy * (q01 + q23))
    this._m02(isz * (q02 - q13))
    this._m03(0.0f)
    this._m10(isx * (q01 - q23))
    this._m11(isy * (1.0f - q22 - q00))
    this._m12(isz * (q12 + q03))
    this._m13(0.0f)
    this._m20(isx * (q02 + q13))
    this._m21(isy * (q12 - q03))
    this._m22(isz * (1.0f - q11 - q00))
    this._m23(0.0f)
    this._m30(-m00 * tx - m10 * ty - m20 * tz)
    this._m31(-m01 * tx - m11 * ty - m21 * tz)
    this._m32(-m02 * tx - m12 * ty - m22 * tz)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE.toInt())
    return this
  }

  /**
   * Set `this` matrix to <tt>(T * R * S)<sup>-1</sup></tt>, where <tt>T</tt> is the given `translation`,
   * <tt>R</tt> is a rotation transformation specified by the given quaternion, and <tt>S</tt> is a scaling transformation
   * which scales the axes by `scale`.
   *
   *
   * This method is equivalent to calling: <tt>translationRotateScale(...).invert()</tt>
   *
   * @see .translationRotateScale
   * @see .invert
   * @param translation
   * the translation
   * @param quat
   * the quaternion representing a rotation
   * @param scale
   * the scaling factors
   * @return this
   */
  fun translationRotateScaleInvert(translation: Vector3fc,
                                   quat: Quaternionfc,
                                   scale: Vector3fc): Matrix4f {
    return translationRotateScaleInvert(translation.x, translation.y, translation.z, quat.x, quat.y, quat.z, quat.w, scale.x, scale.y, scale.z)
  }

  /**
   * Set `this` matrix to <tt>(T * R * S)<sup>-1</sup></tt>, where <tt>T</tt> is the given `translation`,
   * <tt>R</tt> is a rotation transformation specified by the given quaternion, and <tt>S</tt> is a scaling transformation
   * which scales all three axes by `scale`.
   *
   *
   * This method is equivalent to calling: <tt>translationRotateScale(...).invert()</tt>
   *
   * @see .translationRotateScale
   * @see .invert
   * @param translation
   * the translation
   * @param quat
   * the quaternion representing a rotation
   * @param scale
   * the scaling factors
   * @return this
   */
  fun translationRotateScaleInvert(translation: Vector3fc,
                                   quat: Quaternionfc,
                                   scale: Float): Matrix4f {
    return translationRotateScaleInvert(translation.x, translation.y, translation.z, quat.x, quat.y, quat.z, quat.w, scale, scale, scale)
  }

  /**
   * Set `this` matrix to <tt>T * R * S * M</tt>, where <tt>T</tt> is a translation by the given <tt>(tx, ty, tz)</tt>,
   * <tt>R</tt> is a rotation - and possibly scaling - transformation specified by the quaternion <tt>(qx, qy, qz, qw)</tt>, <tt>S</tt> is a scaling transformation
   * which scales the three axes x, y and z by <tt>(sx, sy, sz)</tt> and `M` is an [affine][.isAffine] matrix.
   *
   *
   * When transforming a vector by the resulting matrix the transformation described by `M` will be applied first, then the scaling, then rotation and
   * at last the translation.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method is equivalent to calling: <tt>translation(tx, ty, tz).rotate(quat).scale(sx, sy, sz).mulAffine(m)</tt>
   *
   * @see .translation
   * @see .rotate
   * @see .scale
   * @see .mulAffine
   * @param tx
   * the number of units by which to translate the x-component
   * @param ty
   * the number of units by which to translate the y-component
   * @param tz
   * the number of units by which to translate the z-component
   * @param qx
   * the x-coordinate of the vector part of the quaternion
   * @param qy
   * the y-coordinate of the vector part of the quaternion
   * @param qz
   * the z-coordinate of the vector part of the quaternion
   * @param qw
   * the scalar part of the quaternion
   * @param sx
   * the scaling factor for the x-axis
   * @param sy
   * the scaling factor for the y-axis
   * @param sz
   * the scaling factor for the z-axis
   * @param m
   * the [affine][.isAffine] matrix to multiply by
   * @return this
   */
  fun translationRotateScaleMulAffine(tx: Float, ty: Float, tz: Float,
                                      qx: Float, qy: Float, qz: Float, qw: Float,
                                      sx: Float, sy: Float, sz: Float,
                                      m: Matrix4f): Matrix4f {
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
    val nm00 = w2 + x2 - z2 - y2
    val nm01 = xy + zw + zw + xy
    val nm02 = xz - yw + xz - yw
    val nm10 = -zw + xy - zw + xy
    val nm11 = y2 - z2 + w2 - x2
    val nm12 = yz + yz + xw + xw
    val nm20 = yw + xz + xz + yw
    val nm21 = yz + yz - xw - xw
    val nm22 = z2 - y2 - x2 + w2
    val m00 = nm00 * m.m00 + nm10 * m.m01 + nm20 * m.m02
    val m01 = nm01 * m.m00 + nm11 * m.m01 + nm21 * m.m02
    this._m02(nm02 * m.m00 + nm12 * m.m01 + nm22 * m.m02)
    this._m00(m00)
    this._m01(m01)
    this._m03(0.0f)
    val m10 = nm00 * m.m10 + nm10 * m.m11 + nm20 * m.m12
    val m11 = nm01 * m.m10 + nm11 * m.m11 + nm21 * m.m12
    this._m12(nm02 * m.m10 + nm12 * m.m11 + nm22 * m.m12)
    this._m10(m10)
    this._m11(m11)
    this._m13(0.0f)
    val m20 = nm00 * m.m20 + nm10 * m.m21 + nm20 * m.m22
    val m21 = nm01 * m.m20 + nm11 * m.m21 + nm21 * m.m22
    this._m22(nm02 * m.m20 + nm12 * m.m21 + nm22 * m.m22)
    this._m20(m20)
    this._m21(m21)
    this._m23(0.0f)
    val m30 = nm00 * m.m30 + nm10 * m.m31 + nm20 * m.m32 + tx
    val m31 = nm01 * m.m30 + nm11 * m.m31 + nm21 * m.m32 + ty
    this._m32(nm02 * m.m30 + nm12 * m.m31 + nm22 * m.m32 + tz)
    this._m30(m30)
    this._m31(m31)
    this._m33(1.0f)
    val one = Math.abs(sx) == 1.0f && Math.abs(sy) == 1.0f && Math.abs(sz) == 1.0f
    _properties(Matrix4fc.PROPERTY_AFFINE or if (one && m.properties and Matrix4fc.PROPERTY_ORTHONORMAL != 0) Matrix4fc.PROPERTY_ORTHONORMAL else 0)
    return this
  }

  /**
   * Set `this` matrix to <tt>T * R * S * M</tt>, where <tt>T</tt> is the given `translation`,
   * <tt>R</tt> is a rotation - and possibly scaling - transformation specified by the given quaternion, <tt>S</tt> is a scaling transformation
   * which scales the axes by `scale` and `M` is an [affine][.isAffine] matrix.
   *
   *
   * When transforming a vector by the resulting matrix the transformation described by `M` will be applied first, then the scaling, then rotation and
   * at last the translation.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method is equivalent to calling: <tt>translation(translation).rotate(quat).scale(scale).mulAffine(m)</tt>
   *
   * @see .translation
   * @see .rotate
   * @see .mulAffine
   * @param translation
   * the translation
   * @param quat
   * the quaternion representing a rotation
   * @param scale
   * the scaling factors
   * @param m
   * the [affine][.isAffine] matrix to multiply by
   * @return this
   */
  fun translationRotateScaleMulAffine(translation: Vector3fc,
                                      quat: Quaternionfc,
                                      scale: Vector3fc,
                                      m: Matrix4f): Matrix4f {
    return translationRotateScaleMulAffine(translation.x, translation.y, translation.z, quat.x, quat.y, quat.z, quat.w, scale.x, scale.y, scale.z, m)
  }

  /**
   * Set `this` matrix to <tt>T * R</tt>, where <tt>T</tt> is a translation by the given <tt>(tx, ty, tz)</tt> and
   * <tt>R</tt> is a rotation - and possibly scaling - transformation specified by the quaternion <tt>(qx, qy, qz, qw)</tt>.
   *
   *
   * When transforming a vector by the resulting matrix the rotation - and possibly scaling - transformation will be applied first and then the translation.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method is equivalent to calling: <tt>translation(tx, ty, tz).rotate(quat)</tt>
   *
   * @see .translation
   * @see .rotate
   * @param tx
   * the number of units by which to translate the x-component
   * @param ty
   * the number of units by which to translate the y-component
   * @param tz
   * the number of units by which to translate the z-component
   * @param qx
   * the x-coordinate of the vector part of the quaternion
   * @param qy
   * the y-coordinate of the vector part of the quaternion
   * @param qz
   * the z-coordinate of the vector part of the quaternion
   * @param qw
   * the scalar part of the quaternion
   * @return this
   */
  fun translationRotate(tx: Float, ty: Float, tz: Float, qx: Float, qy: Float, qz: Float, qw: Float): Matrix4f {
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
    this._m00(w2 + x2 - z2 - y2)
    this._m01(xy + zw + zw + xy)
    this._m02(xz - yw + xz - yw)
    this._m10(-zw + xy - zw + xy)
    this._m11(y2 - z2 + w2 - x2)
    this._m12(yz + yz + xw + xw)
    this._m20(yw + xz + xz + yw)
    this._m21(yz + yz - xw - xw)
    this._m22(z2 - y2 - x2 + w2)
    this._m30(tx)
    this._m31(ty)
    this._m32(tz)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set `this` matrix to <tt>T * R</tt>, where <tt>T</tt> is a translation by the given <tt>(tx, ty, tz)</tt> and
   * <tt>R</tt> is a rotation - and possibly scaling - transformation specified by the given quaternion.
   *
   *
   * When transforming a vector by the resulting matrix the rotation - and possibly scaling - transformation will be applied first and then the translation.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method is equivalent to calling: <tt>translation(tx, ty, tz).rotate(quat)</tt>
   *
   * @see .translation
   * @see .rotate
   * @param tx
   * the number of units by which to translate the x-component
   * @param ty
   * the number of units by which to translate the y-component
   * @param tz
   * the number of units by which to translate the z-component
   * @param quat
   * the quaternion representing a rotation
   * @return this
   */
  fun translationRotate(tx: Float, ty: Float, tz: Float, quat: Quaternionfc): Matrix4f {
    return translationRotate(tx, ty, tz, quat.x, quat.y, quat.z, quat.w)
  }

  /**
   * Set the upper left 3x3 submatrix of this [Matrix4f] to the given [Matrix3fc] and don't change the other elements.
   *
   * @param mat
   * the 3x3 matrix
   * @return this
   */
  fun set3x3(mat: Matrix3fc): Matrix4f {
    if (mat is Matrix3f) {
      MemUtil.INSTANCE.copy3x3(mat, this)
    } else {
      set3x3Matrix3fc(mat)
    }
    properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv()
    return this
  }

  private fun set3x3Matrix3fc(mat: Matrix3fc) {
    m00 = mat.m00()
    m01 = mat.m01()
    m02 = mat.m02()
    m10 = mat.m10()
    m11 = mat.m11()
    m12 = mat.m12()
    m20 = mat.m20()
    m21 = mat.m21()
    m22 = mat.m22()
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transform(Vector4f)
     */
  override fun transform(v: Vector4f): Vector4f {
    return v.mul(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transform(Vector4fc, Vector4f)
     */
  override fun transform(v: Vector4fc, dest: Vector4f): Vector4f {
    return v.mul(this, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transform(float, float, float, float, Vector4f)
     */
  override fun transform(x: Float, y: Float, z: Float, w: Float, dest: Vector4f): Vector4f {
    dest.x = x
    dest.y = y
    dest.z = z
    dest.w = w
    return dest.mul(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformProject(Vector4f)
     */
  override fun transformProject(v: Vector4f): Vector4f {
    return v.mulProject(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformProject(Vector4fc, Vector4f)
     */
  override fun transformProject(v: Vector4fc, dest: Vector4f): Vector4f {
    return v.mulProject(this, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformProject(float, float, float, float, Vector4f)
     */
  override fun transformProject(x: Float, y: Float, z: Float, w: Float, dest: Vector4f): Vector4f {
    dest.x = x
    dest.y = y
    dest.z = z
    dest.w = w
    return dest.mulProject(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformProject(Vector3m)
     */
  override fun transformProject(v: Vector3m): Vector3m {
    return v.mulProject(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformProject(Vector3fc, Vector3m)
     */
  override fun transformProject(v: Vector3fc, dest: Vector3m): Vector3m {
    return v.mulProject(this, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformProject(float, float, float, Vector3m)
     */
  override fun transformProject(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m {
    dest.x = x
    dest.y = y
    dest.z = z
    return dest.mulProject(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformPosition(Vector3m)
     */
  override fun transformPosition(v: Vector3m): Vector3m {
    return v.mulPosition(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformPosition(Vector3fc, Vector3m)
     */
  override fun transformPosition(v: Vector3fc, dest: Vector3m): Vector3m {
    return transformPosition(v.x, v.y, v.z, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformPosition(float, float, float, Vector3m)
     */
  override fun transformPosition(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m {
    dest.x = x
    dest.y = y
    dest.z = z
    return dest.mulPosition(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformDirection(Vector3m)
     */
  override fun transformDirection(v: Vector3m): Vector3m {
    return transformDirection(v.x, v.y, v.z, v)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformDirection(Vector3fc, Vector3m)
     */
  override fun transformDirection(v: Vector3fc, dest: Vector3m): Vector3m {
    return transformDirection(v.x, v.y, v.z, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformDirection(float, float, float, Vector3m)
     */
  override fun transformDirection(x: Float, y: Float, z: Float, dest: Vector3m): Vector3m {
    dest.x = x
    dest.y = y
    dest.z = z
    return dest.mulDirection(this)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformAffine(Vector4f)
     */
  override fun transformAffine(v: Vector4f): Vector4f {
    return v.mulAffine(this, v)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformAffine(Vector4fc, Vector4f)
     */
  override fun transformAffine(v: Vector4fc, dest: Vector4f): Vector4f {
    return transformAffine(v.x, v.y, v.z, v.w, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformAffine(float, float, float, float, Vector4f)
     */
  override fun transformAffine(x: Float, y: Float, z: Float, w: Float, dest: Vector4f): Vector4f {
    dest.x = x
    dest.y = y
    dest.z = z
    dest.w = w
    return dest.mulAffine(this, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#scale(Vector3fc, Matrix4f)
     */
  override fun scale(xyz: Vector3fc, dest: Matrix4f): Matrix4f {
    return scale(xyz.x, xyz.y, xyz.z, dest)
  }

  /**
   * Apply scaling to this matrix by scaling the base axes by the given <tt>xyz.x</tt>,
   * <tt>xyz.y</tt> and <tt>xyz.z</tt> factors, respectively.
   *
   *
   * If `M` is `this` matrix and `S` the scaling matrix,
   * then the new matrix will be `M * S`. So when transforming a
   * vector `v` with the new matrix by using `M * S * v`, the
   * scaling will be applied first!
   *
   * @param xyz
   * the factors of the x, y and z component, respectively
   * @return a matrix holding the result
   */
  fun scale(xyz: Vector3fc): Matrix4f {
    return scale(xyz.x, xyz.y, xyz.z, thisOrNew())
  }

  fun scale(xyz: Vector3): Matrix4f {
    return scale(xyz.x, xyz.y, xyz.z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#scale(float, Matrix4f)
     */
  override fun scale(xyz: Float, dest: Matrix4f): Matrix4f {
    return scale(xyz, xyz, xyz, dest)
  }

  /**
   * Apply scaling to this matrix by uniformly scaling all base axes by the given `xyz` factor.
   *
   *
   * If `M` is `this` matrix and `S` the scaling matrix,
   * then the new matrix will be `M * S`. So when transforming a
   * vector `v` with the new matrix by using `M * S * v`, the
   * scaling will be applied first!
   *
   *
   * Individual scaling of all three axes can be applied using [.scale].
   *
   * @see .scale
   * @param xyz
   * the factor for all components
   * @return this
   */
  fun scale(xyz: Float): Matrix4f {
    return scale(xyz, xyz, xyz)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#scale(float, float, float, Matrix4f)
     */
  override fun scale(x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.scaling(x, y, z) else scaleGeneric(x, y, z, dest)
  }

  private fun scaleGeneric(x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    dest._m00(m00 * x)
    dest._m01(m01 * x)
    dest._m02(m02 * x)
    dest._m03(m03 * x)
    dest._m10(m10 * y)
    dest._m11(m11 * y)
    dest._m12(m12 * y)
    dest._m13(m13 * y)
    dest._m20(m20 * z)
    dest._m21(m21 * z)
    dest._m22(m22 * z)
    dest._m23(m23 * z)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    val one = Math.abs(x) == 1.0f && Math.abs(y) == 1.0f && Math.abs(z) == 1.0f
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()
        or (if (one) 0 else Matrix4fc.PROPERTY_ORTHONORMAL).toInt()).inv())
    return dest
  }

  /**
   * Apply scaling to this matrix by scaling the base axes by the given sx,
   * sy and sz factors.
   *
   *
   * If `M` is `this` matrix and `S` the scaling matrix,
   * then the new matrix will be `M * S`. So when transforming a
   * vector `v` with the new matrix by using `M * S * v`, the
   * scaling will be applied first!
   *
   * @param x
   * the factor of the x component
   * @param y
   * the factor of the y component
   * @param z
   * the factor of the z component
   * @return a matrix holding the result
   */
  fun scale(x: Float, y: Float, z: Float): Matrix4f {
    return scale(x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#scaleAround(float, float, float, float, float, float, Matrix4f)
     */
  override fun scaleAround(sx: Float, sy: Float, sz: Float, ox: Float, oy: Float, oz: Float, dest: Matrix4f): Matrix4f {
    val nm30 = m00 * ox + m10 * oy + m20 * oz + m30
    val nm31 = m01 * ox + m11 * oy + m21 * oz + m31
    val nm32 = m02 * ox + m12 * oy + m22 * oz + m32
    val nm33 = m03 * ox + m13 * oy + m23 * oz + m33
    dest._m00(m00 * sx)
    dest._m01(m01 * sx)
    dest._m02(m02 * sx)
    dest._m03(m03 * sx)
    dest._m10(m10 * sy)
    dest._m11(m11 * sy)
    dest._m12(m12 * sy)
    dest._m13(m13 * sy)
    dest._m20(m20 * sz)
    dest._m21(m21 * sz)
    dest._m22(m22 * sz)
    dest._m23(m23 * sz)
    dest._m30(-m00 * ox - m10 * oy - m20 * oz + nm30)
    dest._m31(-m01 * ox - m11 * oy - m21 * oz + nm31)
    dest._m32(-m02 * ox - m12 * oy - m22 * oz + nm32)
    dest._m33(-m03 * ox - m13 * oy - m23 * oz + nm33)
    val one = Math.abs(sx) == 1.0f && Math.abs(sy) == 1.0f && Math.abs(sz) == 1.0f
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()
        or (if (one) 0 else Matrix4fc.PROPERTY_ORTHONORMAL).toInt()).inv())
    return dest
  }

  /**
   * Apply scaling to this matrix by scaling the base axes by the given sx,
   * sy and sz factors while using <tt>(ox, oy, oz)</tt> as the scaling origin.
   *
   *
   * If `M` is `this` matrix and `S` the scaling matrix,
   * then the new matrix will be `M * S`. So when transforming a
   * vector `v` with the new matrix by using `M * S * v`, the
   * scaling will be applied first!
   *
   *
   * This method is equivalent to calling: <tt>translate(ox, oy, oz).scale(sx, sy, sz).translate(-ox, -oy, -oz)</tt>
   *
   * @param sx
   * the scaling factor of the x component
   * @param sy
   * the scaling factor of the y component
   * @param sz
   * the scaling factor of the z component
   * @param ox
   * the x coordinate of the scaling origin
   * @param oy
   * the y coordinate of the scaling origin
   * @param oz
   * the z coordinate of the scaling origin
   * @return a matrix holding the result
   */
  fun scaleAround(sx: Float, sy: Float, sz: Float, ox: Float, oy: Float, oz: Float): Matrix4f {
    return scaleAround(sx, sy, sz, ox, oy, oz, thisOrNew())
  }

  /**
   * Apply scaling to this matrix by scaling all three base axes by the given `factor`
   * while using <tt>(ox, oy, oz)</tt> as the scaling origin.
   *
   *
   * If `M` is `this` matrix and `S` the scaling matrix,
   * then the new matrix will be `M * S`. So when transforming a
   * vector `v` with the new matrix by using `M * S * v`, the
   * scaling will be applied first!
   *
   *
   * This method is equivalent to calling: <tt>translate(ox, oy, oz).scale(factor).translate(-ox, -oy, -oz)</tt>
   *
   * @param factor
   * the scaling factor for all three axes
   * @param ox
   * the x coordinate of the scaling origin
   * @param oy
   * the y coordinate of the scaling origin
   * @param oz
   * the z coordinate of the scaling origin
   * @return a matrix holding the result
   */
  fun scaleAround(factor: Float, ox: Float, oy: Float, oz: Float): Matrix4f {
    return scaleAround(factor, factor, factor, ox, oy, oz, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#scaleAround(float, float, float, float, Matrix4f)
     */
  override fun scaleAround(factor: Float, ox: Float, oy: Float, oz: Float, dest: Matrix4f): Matrix4f {
    return scaleAround(factor, factor, factor, ox, oy, oz, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#scaleLocal(float, float, float, Matrix4f)
     */
  override fun scaleLocal(x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.scaling(x, y, z) else scaleLocalGeneric(x, y, z, dest)
  }

  private fun scaleLocalGeneric(x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    val nm00 = x * m00
    val nm01 = y * m01
    val nm02 = z * m02
    val nm03 = m03
    val nm10 = x * m10
    val nm11 = y * m11
    val nm12 = z * m12
    val nm13 = m13
    val nm20 = x * m20
    val nm21 = y * m21
    val nm22 = z * m22
    val nm23 = m23
    val nm30 = x * m30
    val nm31 = y * m31
    val nm32 = z * m32
    val nm33 = m33
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    val one = Math.abs(x) == 1.0f && Math.abs(y) == 1.0f && Math.abs(z) == 1.0f
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()
        or (if (one) 0 else Matrix4fc.PROPERTY_ORTHONORMAL).toInt()).inv())
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#scaleLocal(float, Matrix4f)
     */
  override fun scaleLocal(xyz: Float, dest: Matrix4f): Matrix4f {
    return scaleLocal(xyz, xyz, xyz, dest)
  }

  /**
   * Pre-multiply scaling to this matrix by scaling the base axes by the given xyz factor.
   *
   *
   * If `M` is `this` matrix and `S` the scaling matrix,
   * then the new matrix will be `S * M`. So when transforming a
   * vector `v` with the new matrix by using `S * M * v`, the
   * scaling will be applied last!
   *
   * @param xyz
   * the factor of the x, y and z component
   * @return a matrix holding the result
   */
  fun scaleLocal(xyz: Float): Matrix4f {
    return scaleLocal(xyz, thisOrNew())
  }

  /**
   * Pre-multiply scaling to this matrix by scaling the base axes by the given x,
   * y and z factors.
   *
   *
   * If `M` is `this` matrix and `S` the scaling matrix,
   * then the new matrix will be `S * M`. So when transforming a
   * vector `v` with the new matrix by using `S * M * v`, the
   * scaling will be applied last!
   *
   * @param x
   * the factor of the x component
   * @param y
   * the factor of the y component
   * @param z
   * the factor of the z component
   * @return a matrix holding the result
   */
  fun scaleLocal(x: Float, y: Float, z: Float): Matrix4f {
    return scaleLocal(x, y, z, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#scaleAroundLocal(float, float, float, float, float, float, Matrix4f)
     */
  override fun scaleAroundLocal(sx: Float, sy: Float, sz: Float, ox: Float, oy: Float, oz: Float, dest: Matrix4f): Matrix4f {
    dest._m00(sx * (m00 - ox * m03) + ox * m03)
    dest._m01(sy * (m01 - oy * m03) + oy * m03)
    dest._m02(sz * (m02 - oz * m03) + oz * m03)
    dest._m03(m03)
    dest._m10(sx * (m10 - ox * m13) + ox * m13)
    dest._m11(sy * (m11 - oy * m13) + oy * m13)
    dest._m12(sz * (m12 - oz * m13) + oz * m13)
    dest._m13(m13)
    dest._m20(sx * (m20 - ox * m23) + ox * m23)
    dest._m21(sy * (m21 - oy * m23) + oy * m23)
    dest._m22(sz * (m22 - oz * m23) + oz * m23)
    dest._m23(m23)
    dest._m30(sx * (m30 - ox * m33) + ox * m33)
    dest._m31(sy * (m31 - oy * m33) + oy * m33)
    dest._m32(sz * (m32 - oz * m33) + oz * m33)
    dest._m33(m33)
    val one = Math.abs(sx) == 1.0f && Math.abs(sy) == 1.0f && Math.abs(sz) == 1.0f
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()
        or (if (one) 0 else Matrix4fc.PROPERTY_ORTHONORMAL).toInt()).inv())
    return dest
  }

  /**
   * Pre-multiply scaling to this matrix by scaling the base axes by the given sx,
   * sy and sz factors while using <tt>(ox, oy, oz)</tt> as the scaling origin.
   *
   *
   * If `M` is `this` matrix and `S` the scaling matrix,
   * then the new matrix will be `S * M`. So when transforming a
   * vector `v` with the new matrix by using `S * M * v`, the
   * scaling will be applied last!
   *
   *
   * This method is equivalent to calling: <tt>new Matrix4f().translate(ox, oy, oz).scale(sx, sy, sz).translate(-ox, -oy, -oz).mul(this, this)</tt>
   *
   * @param sx
   * the scaling factor of the x component
   * @param sy
   * the scaling factor of the y component
   * @param sz
   * the scaling factor of the z component
   * @param ox
   * the x coordinate of the scaling origin
   * @param oy
   * the y coordinate of the scaling origin
   * @param oz
   * the z coordinate of the scaling origin
   * @return a matrix holding the result
   */
  fun scaleAroundLocal(sx: Float, sy: Float, sz: Float, ox: Float, oy: Float, oz: Float): Matrix4f {
    return scaleAroundLocal(sx, sy, sz, ox, oy, oz, thisOrNew())
  }

  /**
   * Pre-multiply scaling to this matrix by scaling all three base axes by the given `factor`
   * while using <tt>(ox, oy, oz)</tt> as the scaling origin.
   *
   *
   * If `M` is `this` matrix and `S` the scaling matrix,
   * then the new matrix will be `S * M`. So when transforming a
   * vector `v` with the new matrix by using `S * M * v`, the
   * scaling will be applied last!
   *
   *
   * This method is equivalent to calling: <tt>new Matrix4f().translate(ox, oy, oz).scale(factor).translate(-ox, -oy, -oz).mul(this, this)</tt>
   *
   * @param factor
   * the scaling factor for all three axes
   * @param ox
   * the x coordinate of the scaling origin
   * @param oy
   * the y coordinate of the scaling origin
   * @param oz
   * the z coordinate of the scaling origin
   * @return a matrix holding the result
   */
  fun scaleAroundLocal(factor: Float, ox: Float, oy: Float, oz: Float): Matrix4f {
    return scaleAroundLocal(factor, factor, factor, ox, oy, oz, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#scaleAroundLocal(float, float, float, float, Matrix4f)
     */
  override fun scaleAroundLocal(factor: Float, ox: Float, oy: Float, oz: Float, dest: Matrix4f): Matrix4f {
    return scaleAroundLocal(factor, factor, factor, ox, oy, oz, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateX(float, Matrix4f)
     */
  override fun rotateX(ang: Float, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.rotationX(ang)
    val sin: Float
    val cos: Float
    sin = Math.sin(ang.toDouble()).toFloat()
    cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    val rm21 = -sin

    // add temporaries for dependent values
    val nm10 = m10 * cos + m20 * sin
    val nm11 = m11 * cos + m21 * sin
    val nm12 = m12 * cos + m22 * sin
    val nm13 = m13 * cos + m23 * sin
    // set non-dependent values directly
    dest._m20(m10 * rm21 + m20 * cos)
    dest._m21(m11 * rm21 + m21 * cos)
    dest._m22(m12 * rm21 + m22 * cos)
    dest._m23(m13 * rm21 + m23 * cos)
    // set other values
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m00(m00)
    dest._m01(m01)
    dest._m02(m02)
    dest._m03(m03)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation about the X axis to this matrix by rotating the given amount of radians.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations)
   *
   * @param ang
   * the angle in radians
   * @return a matrix holding the result
   */
  fun rotateX(ang: Float): Matrix4f {
    return rotateX(ang, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateY(float, Matrix4f)
     */
  override fun rotateY(ang: Float, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.rotationY(ang)
    val cos: Float
    val sin: Float
    sin = Math.sin(ang.toDouble()).toFloat()
    cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    val rm02 = -sin

    // add temporaries for dependent values
    val nm00 = m00 * cos + m20 * rm02
    val nm01 = m01 * cos + m21 * rm02
    val nm02 = m02 * cos + m22 * rm02
    val nm03 = m03 * cos + m23 * rm02
    // set non-dependent values directly
    dest._m20(m00 * sin + m20 * cos)
    dest._m21(m01 * sin + m21 * cos)
    dest._m22(m02 * sin + m22 * cos)
    dest._m23(m03 * sin + m23 * cos)
    // set other values
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(m10)
    dest._m11(m11)
    dest._m12(m12)
    dest._m13(m13)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation about the Y axis to this matrix by rotating the given amount of radians.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations)
   *
   * @param ang
   * the angle in radians
   * @return a matrix holding the result
   */
  fun rotateY(ang: Float): Matrix4f {
    return rotateY(ang, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateZ(float, Matrix4f)
     */
  override fun rotateZ(ang: Float, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.rotationZ(ang)
    val sin = Math.sin(ang.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    return rotateTowardsXY(sin, cos, dest)
  }

  /**
   * Apply rotation about the Z axis to this matrix by rotating the given amount of radians.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations)
   *
   * @param ang
   * the angle in radians
   * @return a matrix holding the result
   */
  fun rotateZ(ang: Float): Matrix4f {
    return rotateZ(ang, thisOrNew())
  }

  /**
   * Apply rotation about the Z axis to align the local <tt>+X</tt> towards <tt>(dirX, dirY)</tt>.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * The vector <tt>(dirX, dirY)</tt> must be a unit vector.
   *
   * @param dirX
   * the x component of the normalized direction
   * @param dirY
   * the y component of the normalized direction
   * @return a matrix holding the result
   */
  fun rotateTowardsXY(dirX: Float, dirY: Float): Matrix4f {
    return rotateTowardsXY(dirX, dirY, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateTowardsXY(float, float, Matrix4f)
     */
  override fun rotateTowardsXY(dirX: Float, dirY: Float, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.rotationTowardsXY(dirX, dirY)
    val rm10 = -dirX
    val nm00 = m00 * dirY + m10 * dirX
    val nm01 = m01 * dirY + m11 * dirX
    val nm02 = m02 * dirY + m12 * dirX
    val nm03 = m03 * dirY + m13 * dirX
    dest._m10(m00 * rm10 + m10 * dirY)
    dest._m11(m01 * rm10 + m11 * dirY)
    dest._m12(m02 * rm10 + m12 * dirY)
    dest._m13(m03 * rm10 + m13 * dirY)
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m20(m20)
    dest._m21(m21)
    dest._m22(m22)
    dest._m23(m23)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation of `angles.x` radians about the X axis, followed by a rotation of `angles.y` radians about the Y axis and
   * followed by a rotation of `angles.z` radians about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * This method is equivalent to calling: <tt>rotateX(angles.x).rotateY(angles.y).rotateZ(angles.z)</tt>
   *
   * @param angles
   * the Euler angles
   * @return this
   */
  fun rotateXYZ(angles: Vector3m): Matrix4f {
    return rotateXYZ(angles.x, angles.y, angles.z)
  }

  /**
   * Apply rotation of `angleX` radians about the X axis, followed by a rotation of `angleY` radians about the Y axis and
   * followed by a rotation of `angleZ` radians about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * This method is equivalent to calling: <tt>rotateX(angleX).rotateY(angleY).rotateZ(angleZ)</tt>
   *
   * @param angleX
   * the angle to rotate about X
   * @param angleY
   * the angle to rotate about Y
   * @param angleZ
   * the angle to rotate about Z
   * @return a matrix holding the result
   */
  fun rotateXYZ(angleX: Float, angleY: Float, angleZ: Float): Matrix4f {
    return rotateXYZ(angleX, angleY, angleZ, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateXYZ(float, float, float, Matrix4f)
     */
  override fun rotateXYZ(angleX: Float, angleY: Float, angleZ: Float, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.rotationXYZ(angleX, angleY, angleZ)
    else if (properties and Matrix4fc.PROPERTY_AFFINE != 0)
      return dest.rotateAffineXYZ(angleX, angleY, angleZ)

    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinX = -sinX
    val m_sinY = -sinY
    val m_sinZ = -sinZ

    // rotateX
    val nm10 = m10 * cosX + m20 * sinX
    val nm11 = m11 * cosX + m21 * sinX
    val nm12 = m12 * cosX + m22 * sinX
    val nm13 = m13 * cosX + m23 * sinX
    val nm20 = m10 * m_sinX + m20 * cosX
    val nm21 = m11 * m_sinX + m21 * cosX
    val nm22 = m12 * m_sinX + m22 * cosX
    val nm23 = m13 * m_sinX + m23 * cosX
    // rotateY
    val nm00 = m00 * cosY + nm20 * m_sinY
    val nm01 = m01 * cosY + nm21 * m_sinY
    val nm02 = m02 * cosY + nm22 * m_sinY
    val nm03 = m03 * cosY + nm23 * m_sinY
    dest._m20(m00 * sinY + nm20 * cosY)
    dest._m21(m01 * sinY + nm21 * cosY)
    dest._m22(m02 * sinY + nm22 * cosY)
    dest._m23(m03 * sinY + nm23 * cosY)
    // rotateZ
    dest._m00(nm00 * cosZ + nm10 * sinZ)
    dest._m01(nm01 * cosZ + nm11 * sinZ)
    dest._m02(nm02 * cosZ + nm12 * sinZ)
    dest._m03(nm03 * cosZ + nm13 * sinZ)
    dest._m10(nm00 * m_sinZ + nm10 * cosZ)
    dest._m11(nm01 * m_sinZ + nm11 * cosZ)
    dest._m12(nm02 * m_sinZ + nm12 * cosZ)
    dest._m13(nm03 * m_sinZ + nm13 * cosZ)
    // copy last column from 'this'
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation of `angleX` radians about the X axis, followed by a rotation of `angleY` radians about the Y axis and
   * followed by a rotation of `angleZ` radians about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method assumes that `this` matrix represents an [affine][.isAffine] transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
   * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * This method is equivalent to calling: <tt>rotateX(angleX).rotateY(angleY).rotateZ(angleZ)</tt>
   *
   * @param angleX
   * the angle to rotate about X
   * @param angleY
   * the angle to rotate about Y
   * @param angleZ
   * the angle to rotate about Z
   * @return a matrix holding the result
   */
  fun rotateAffineXYZ(angleX: Float, angleY: Float, angleZ: Float): Matrix4f {
    return rotateAffineXYZ(angleX, angleY, angleZ, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateAffineXYZ(float, float, float, Matrix4f)
     */
  override fun rotateAffineXYZ(angleX: Float, angleY: Float, angleZ: Float, dest: Matrix4f): Matrix4f {
    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinX = -sinX
    val m_sinY = -sinY
    val m_sinZ = -sinZ

    // rotateX
    val nm10 = m10 * cosX + m20 * sinX
    val nm11 = m11 * cosX + m21 * sinX
    val nm12 = m12 * cosX + m22 * sinX
    val nm20 = m10 * m_sinX + m20 * cosX
    val nm21 = m11 * m_sinX + m21 * cosX
    val nm22 = m12 * m_sinX + m22 * cosX
    // rotateY
    val nm00 = m00 * cosY + nm20 * m_sinY
    val nm01 = m01 * cosY + nm21 * m_sinY
    val nm02 = m02 * cosY + nm22 * m_sinY
    dest._m20(m00 * sinY + nm20 * cosY)
    dest._m21(m01 * sinY + nm21 * cosY)
    dest._m22(m02 * sinY + nm22 * cosY)
    dest._m23(0.0f)
    // rotateZ
    dest._m00(nm00 * cosZ + nm10 * sinZ)
    dest._m01(nm01 * cosZ + nm11 * sinZ)
    dest._m02(nm02 * cosZ + nm12 * sinZ)
    dest._m03(0.0f)
    dest._m10(nm00 * m_sinZ + nm10 * cosZ)
    dest._m11(nm01 * m_sinZ + nm11 * cosZ)
    dest._m12(nm02 * m_sinZ + nm12 * cosZ)
    dest._m13(0.0f)
    // copy last column from 'this'
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation of `angles.z` radians about the Z axis, followed by a rotation of `angles.y` radians about the Y axis and
   * followed by a rotation of `angles.x` radians about the X axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * This method is equivalent to calling: <tt>rotateZ(angles.z).rotateY(angles.y).rotateX(angles.x)</tt>
   *
   * @param angles
   * the Euler angles
   * @return this
   */
  fun rotateZYX(angles: Vector3m): Matrix4f {
    return rotateZYX(angles.z, angles.y, angles.x)
  }

  /**
   * Apply rotation of `angleZ` radians about the Z axis, followed by a rotation of `angleY` radians about the Y axis and
   * followed by a rotation of `angleX` radians about the X axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * This method is equivalent to calling: <tt>rotateZ(angleZ).rotateY(angleY).rotateX(angleX)</tt>
   *
   * @param angleZ
   * the angle to rotate about Z
   * @param angleY
   * the angle to rotate about Y
   * @param angleX
   * the angle to rotate about X
   * @return a matrix holding the result
   */
  fun rotateZYX(angleZ: Float, angleY: Float, angleX: Float): Matrix4f {
    return rotateZYX(angleZ, angleY, angleX, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateZYX(float, float, float, Matrix4f)
     */
  override fun rotateZYX(angleZ: Float, angleY: Float, angleX: Float, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.rotationZYX(angleZ, angleY, angleX)
    else if (properties and Matrix4fc.PROPERTY_AFFINE != 0)
      return dest.rotateAffineZYX(angleZ, angleY, angleX)

    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinZ = -sinZ
    val m_sinY = -sinY
    val m_sinX = -sinX

    // rotateZ
    val nm00 = m00 * cosZ + m10 * sinZ
    val nm01 = m01 * cosZ + m11 * sinZ
    val nm02 = m02 * cosZ + m12 * sinZ
    val nm03 = m03 * cosZ + m13 * sinZ
    val nm10 = m00 * m_sinZ + m10 * cosZ
    val nm11 = m01 * m_sinZ + m11 * cosZ
    val nm12 = m02 * m_sinZ + m12 * cosZ
    val nm13 = m03 * m_sinZ + m13 * cosZ
    // rotateY
    val nm20 = nm00 * sinY + m20 * cosY
    val nm21 = nm01 * sinY + m21 * cosY
    val nm22 = nm02 * sinY + m22 * cosY
    val nm23 = nm03 * sinY + m23 * cosY
    dest._m00(nm00 * cosY + m20 * m_sinY)
    dest._m01(nm01 * cosY + m21 * m_sinY)
    dest._m02(nm02 * cosY + m22 * m_sinY)
    dest._m03(nm03 * cosY + m23 * m_sinY)
    // rotateX
    dest._m10(nm10 * cosX + nm20 * sinX)
    dest._m11(nm11 * cosX + nm21 * sinX)
    dest._m12(nm12 * cosX + nm22 * sinX)
    dest._m13(nm13 * cosX + nm23 * sinX)
    dest._m20(nm10 * m_sinX + nm20 * cosX)
    dest._m21(nm11 * m_sinX + nm21 * cosX)
    dest._m22(nm12 * m_sinX + nm22 * cosX)
    dest._m23(nm13 * m_sinX + nm23 * cosX)
    // copy last column from 'this'
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation of `angleZ` radians about the Z axis, followed by a rotation of `angleY` radians about the Y axis and
   * followed by a rotation of `angleX` radians about the X axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method assumes that `this` matrix represents an [affine][.isAffine] transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
   * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   * @param angleZ
   * the angle to rotate about Z
   * @param angleY
   * the angle to rotate about Y
   * @param angleX
   * the angle to rotate about X
   * @return a matrix holding the result
   */
  fun rotateAffineZYX(angleZ: Float, angleY: Float, angleX: Float): Matrix4f {
    return rotateAffineZYX(angleZ, angleY, angleX, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateAffineZYX(float, float, float, Matrix4f)
     */
  override fun rotateAffineZYX(angleZ: Float, angleY: Float, angleX: Float, dest: Matrix4f): Matrix4f {
    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinZ = -sinZ
    val m_sinY = -sinY
    val m_sinX = -sinX

    // rotateZ
    val nm00 = m00 * cosZ + m10 * sinZ
    val nm01 = m01 * cosZ + m11 * sinZ
    val nm02 = m02 * cosZ + m12 * sinZ
    val nm10 = m00 * m_sinZ + m10 * cosZ
    val nm11 = m01 * m_sinZ + m11 * cosZ
    val nm12 = m02 * m_sinZ + m12 * cosZ
    // rotateY
    val nm20 = nm00 * sinY + m20 * cosY
    val nm21 = nm01 * sinY + m21 * cosY
    val nm22 = nm02 * sinY + m22 * cosY
    dest._m00(nm00 * cosY + m20 * m_sinY)
    dest._m01(nm01 * cosY + m21 * m_sinY)
    dest._m02(nm02 * cosY + m22 * m_sinY)
    dest._m03(0.0f)
    // rotateX
    dest._m10(nm10 * cosX + nm20 * sinX)
    dest._m11(nm11 * cosX + nm21 * sinX)
    dest._m12(nm12 * cosX + nm22 * sinX)
    dest._m13(0.0f)
    dest._m20(nm10 * m_sinX + nm20 * cosX)
    dest._m21(nm11 * m_sinX + nm21 * cosX)
    dest._m22(nm12 * m_sinX + nm22 * cosX)
    dest._m23(0.0f)
    // copy last column from 'this'
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation of `angles.y` radians about the Y axis, followed by a rotation of `angles.x` radians about the X axis and
   * followed by a rotation of `angles.z` radians about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * This method is equivalent to calling: <tt>rotateY(angles.y).rotateX(angles.x).rotateZ(angles.z)</tt>
   *
   * @param angles
   * the Euler angles
   * @return this
   */
  fun rotateYXZ(angles: Vector3m): Matrix4f {
    return rotateYXZ(angles.y, angles.x, angles.z)
  }

  /**
   * Apply rotation of `angleY` radians about the Y axis, followed by a rotation of `angleX` radians about the X axis and
   * followed by a rotation of `angleZ` radians about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * This method is equivalent to calling: <tt>rotateY(angleY).rotateX(angleX).rotateZ(angleZ)</tt>
   *
   * @param angleY
   * the angle to rotate about Y
   * @param angleX
   * the angle to rotate about X
   * @param angleZ
   * the angle to rotate about Z
   * @return a matrix holding the result
   */
  fun rotateYXZ(angleY: Float, angleX: Float, angleZ: Float): Matrix4f {
    return rotateYXZ(angleY, angleX, angleZ, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateYXZ(float, float, float, Matrix4f)
     */
  override fun rotateYXZ(angleY: Float, angleX: Float, angleZ: Float, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.rotationYXZ(angleY, angleX, angleZ)
    else if (properties and Matrix4fc.PROPERTY_AFFINE != 0)
      return dest.rotateAffineYXZ(angleY, angleX, angleZ)

    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinY = -sinY
    val m_sinX = -sinX
    val m_sinZ = -sinZ

    // rotateY
    val nm20 = m00 * sinY + m20 * cosY
    val nm21 = m01 * sinY + m21 * cosY
    val nm22 = m02 * sinY + m22 * cosY
    val nm23 = m03 * sinY + m23 * cosY
    val nm00 = m00 * cosY + m20 * m_sinY
    val nm01 = m01 * cosY + m21 * m_sinY
    val nm02 = m02 * cosY + m22 * m_sinY
    val nm03 = m03 * cosY + m23 * m_sinY
    // rotateX
    val nm10 = m10 * cosX + nm20 * sinX
    val nm11 = m11 * cosX + nm21 * sinX
    val nm12 = m12 * cosX + nm22 * sinX
    val nm13 = m13 * cosX + nm23 * sinX
    dest._m20(m10 * m_sinX + nm20 * cosX)
    dest._m21(m11 * m_sinX + nm21 * cosX)
    dest._m22(m12 * m_sinX + nm22 * cosX)
    dest._m23(m13 * m_sinX + nm23 * cosX)
    // rotateZ
    dest._m00(nm00 * cosZ + nm10 * sinZ)
    dest._m01(nm01 * cosZ + nm11 * sinZ)
    dest._m02(nm02 * cosZ + nm12 * sinZ)
    dest._m03(nm03 * cosZ + nm13 * sinZ)
    dest._m10(nm00 * m_sinZ + nm10 * cosZ)
    dest._m11(nm01 * m_sinZ + nm11 * cosZ)
    dest._m12(nm02 * m_sinZ + nm12 * cosZ)
    dest._m13(nm03 * m_sinZ + nm13 * cosZ)
    // copy last column from 'this'
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation of `angleY` radians about the Y axis, followed by a rotation of `angleX` radians about the X axis and
   * followed by a rotation of `angleZ` radians about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * This method assumes that `this` matrix represents an [affine][.isAffine] transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
   * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   * @param angleY
   * the angle to rotate about Y
   * @param angleX
   * the angle to rotate about X
   * @param angleZ
   * the angle to rotate about Z
   * @return a matrix holding the result
   */
  fun rotateAffineYXZ(angleY: Float, angleX: Float, angleZ: Float): Matrix4f {
    return rotateAffineYXZ(angleY, angleX, angleZ, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateAffineYXZ(float, float, float, Matrix4f)
     */
  override fun rotateAffineYXZ(angleY: Float, angleX: Float, angleZ: Float, dest: Matrix4f): Matrix4f {
    val sinX = Math.sin(angleX.toDouble()).toFloat()
    val cosX = Math.cosFromSin(sinX.toDouble(), angleX.toDouble()).toFloat()
    val sinY = Math.sin(angleY.toDouble()).toFloat()
    val cosY = Math.cosFromSin(sinY.toDouble(), angleY.toDouble()).toFloat()
    val sinZ = Math.sin(angleZ.toDouble()).toFloat()
    val cosZ = Math.cosFromSin(sinZ.toDouble(), angleZ.toDouble()).toFloat()
    val m_sinY = -sinY
    val m_sinX = -sinX
    val m_sinZ = -sinZ

    // rotateY
    val nm20 = m00 * sinY + m20 * cosY
    val nm21 = m01 * sinY + m21 * cosY
    val nm22 = m02 * sinY + m22 * cosY
    val nm00 = m00 * cosY + m20 * m_sinY
    val nm01 = m01 * cosY + m21 * m_sinY
    val nm02 = m02 * cosY + m22 * m_sinY
    // rotateX
    val nm10 = m10 * cosX + nm20 * sinX
    val nm11 = m11 * cosX + nm21 * sinX
    val nm12 = m12 * cosX + nm22 * sinX
    dest._m20(m10 * m_sinX + nm20 * cosX)
    dest._m21(m11 * m_sinX + nm21 * cosX)
    dest._m22(m12 * m_sinX + nm22 * cosX)
    dest._m23(0.0f)
    // rotateZ
    dest._m00(nm00 * cosZ + nm10 * sinZ)
    dest._m01(nm01 * cosZ + nm11 * sinZ)
    dest._m02(nm02 * cosZ + nm12 * sinZ)
    dest._m03(0.0f)
    dest._m10(nm00 * m_sinZ + nm10 * cosZ)
    dest._m11(nm01 * m_sinZ + nm11 * cosZ)
    dest._m12(nm02 * m_sinZ + nm12 * cosZ)
    dest._m13(0.0f)
    // copy last column from 'this'
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation to this matrix by rotating the given amount of radians
   * about the specified <tt>(x, y, z)</tt> axis and store the result in `dest`.
   *
   *
   * The axis described by the three components needs to be a unit vector.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation matrix without post-multiplying the rotation
   * transformation, use [rotation()][.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotation
   * @param ang
   * the angle in radians
   * @param x
   * the x component of the axis
   * @param y
   * the y component of the axis
   * @param z
   * the z component of the axis
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotate(ang: Float, x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.rotation(ang, x, y, z)
    else if (properties and Matrix4fc.PROPERTY_TRANSLATION != 0)
      return rotateTranslation(ang, x, y, z, dest)
    else if (properties and Matrix4fc.PROPERTY_AFFINE != 0)
      return rotateAffine(ang, x, y, z, dest)
    return rotateGeneric(ang, x, y, z, dest)
  }

  private fun rotateGeneric(ang: Float, x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    val s = Math.sin(ang.toDouble()).toFloat()
    val c = Math.cosFromSin(s.toDouble(), ang.toDouble()).toFloat()
    val C = 1.0f - c
    val xx = x * x
    val xy = x * y
    val xz = x * z
    val yy = y * y
    val yz = y * z
    val zz = z * z
    val rm00 = xx * C + c
    val rm01 = xy * C + z * s
    val rm02 = xz * C - y * s
    val rm10 = xy * C - z * s
    val rm11 = yy * C + c
    val rm12 = yz * C + x * s
    val rm20 = xz * C + y * s
    val rm21 = yz * C - x * s
    val rm22 = zz * C + c
    val nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02
    val nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02
    val nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02
    val nm03 = m03 * rm00 + m13 * rm01 + m23 * rm02
    val nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12
    val nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12
    val nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12
    val nm13 = m03 * rm10 + m13 * rm11 + m23 * rm12
    dest._m20(m00 * rm20 + m10 * rm21 + m20 * rm22)
    dest._m21(m01 * rm20 + m11 * rm21 + m21 * rm22)
    dest._m22(m02 * rm20 + m12 * rm21 + m22 * rm22)
    dest._m23(m03 * rm20 + m13 * rm21 + m23 * rm22)
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation to this matrix by rotating the given amount of radians
   * about the specified <tt>(x, y, z)</tt> axis.
   *
   *
   * The axis described by the three components needs to be a unit vector.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation matrix without post-multiplying the rotation
   * transformation, use [rotation()][.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotation
   * @param ang
   * the angle in radians
   * @param x
   * the x component of the axis
   * @param y
   * the y component of the axis
   * @param z
   * the z component of the axis
   * @return a matrix holding the result
   */
  fun rotate(ang: Float, x: Float, y: Float, z: Float): Matrix4f {
    return rotate(ang, x, y, z, thisOrNew())
  }

  /**
   * Apply rotation to this matrix, which is assumed to only contain a translation, by rotating the given amount of radians
   * about the specified <tt>(x, y, z)</tt> axis and store the result in `dest`.
   *
   *
   * This method assumes `this` to only contain a translation.
   *
   *
   * The axis described by the three components needs to be a unit vector.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation matrix without post-multiplying the rotation
   * transformation, use [rotation()][.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotation
   * @param ang
   * the angle in radians
   * @param x
   * the x component of the axis
   * @param y
   * the y component of the axis
   * @param z
   * the z component of the axis
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotateTranslation(ang: Float, x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    val s = Math.sin(ang.toDouble()).toFloat()
    val c = Math.cosFromSin(s.toDouble(), ang.toDouble()).toFloat()
    val C = 1.0f - c
    val xx = x * x
    val xy = x * y
    val xz = x * z
    val yy = y * y
    val yz = y * z
    val zz = z * z
    val rm00 = xx * C + c
    val rm01 = xy * C + z * s
    val rm02 = xz * C - y * s
    val rm10 = xy * C - z * s
    val rm11 = yy * C + c
    val rm12 = yz * C + x * s
    val rm20 = xz * C + y * s
    val rm21 = yz * C - x * s
    val rm22 = zz * C + c
// set non-dependent values directly
    dest._m20(rm20)
    dest._m21(rm21)
    dest._m22(rm22)
    // set other values
    dest._m00(rm00)
    dest._m01(rm01)
    dest._m02(rm02)
    dest._m03(0.0f)
    dest._m10(rm10)
    dest._m11(rm11)
    dest._m12(rm12)
    dest._m13(0.0f)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(1.0f)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation to this [affine][.isAffine] matrix by rotating the given amount of radians
   * about the specified <tt>(x, y, z)</tt> axis and store the result in `dest`.
   *
   *
   * This method assumes `this` to be [affine][.isAffine].
   *
   *
   * The axis described by the three components needs to be a unit vector.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation matrix without post-multiplying the rotation
   * transformation, use [rotation()][.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotation
   * @param ang
   * the angle in radians
   * @param x
   * the x component of the axis
   * @param y
   * the y component of the axis
   * @param z
   * the z component of the axis
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotateAffine(ang: Float, x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    val s = Math.sin(ang.toDouble()).toFloat()
    val c = Math.cosFromSin(s.toDouble(), ang.toDouble()).toFloat()
    val C = 1.0f - c
    val xx = x * x
    val xy = x * y
    val xz = x * z
    val yy = y * y
    val yz = y * z
    val zz = z * z
    val rm00 = xx * C + c
    val rm01 = xy * C + z * s
    val rm02 = xz * C - y * s
    val rm10 = xy * C - z * s
    val rm11 = yy * C + c
    val rm12 = yz * C + x * s
    val rm20 = xz * C + y * s
    val rm21 = yz * C - x * s
    val rm22 = zz * C + c
    // add temporaries for dependent values
    val nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02
    val nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02
    val nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02
    val nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12
    val nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12
    val nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12
    // set non-dependent values directly
    dest._m20(m00 * rm20 + m10 * rm21 + m20 * rm22)
    dest._m21(m01 * rm20 + m11 * rm21 + m21 * rm22)
    dest._m22(m02 * rm20 + m12 * rm21 + m22 * rm22)
    dest._m23(0.0f)
    // set other values
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(0.0f)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(0.0f)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(1.0f)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply rotation to this [affine][.isAffine] matrix by rotating the given amount of radians
   * about the specified <tt>(x, y, z)</tt> axis.
   *
   *
   * This method assumes `this` to be [affine][.isAffine].
   *
   *
   * The axis described by the three components needs to be a unit vector.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation matrix without post-multiplying the rotation
   * transformation, use [rotation()][.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotation
   * @param ang
   * the angle in radians
   * @param x
   * the x component of the axis
   * @param y
   * the y component of the axis
   * @param z
   * the z component of the axis
   * @return a matrix holding the result
   */
  fun rotateAffine(ang: Float, x: Float, y: Float, z: Float): Matrix4f {
    return rotateAffine(ang, x, y, z, thisOrNew())
  }

  /**
   * Pre-multiply a rotation to this matrix by rotating the given amount of radians
   * about the specified <tt>(x, y, z)</tt> axis and store the result in `dest`.
   *
   *
   * The axis described by the three components needs to be a unit vector.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `R * M`. So when transforming a
   * vector `v` with the new matrix by using `R * M * v`, the
   * rotation will be applied last!
   *
   *
   * In order to set the matrix to a rotation matrix without pre-multiplying the rotation
   * transformation, use [rotation()][.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotation
   * @param ang
   * the angle in radians
   * @param x
   * the x component of the axis
   * @param y
   * the y component of the axis
   * @param z
   * the z component of the axis
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotateLocal(ang: Float, x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.rotation(ang, x, y, z) else rotateLocalGeneric(ang, x, y, z, dest)
  }

  private fun rotateLocalGeneric(ang: Float, x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    val s = Math.sin(ang.toDouble()).toFloat()
    val c = Math.cosFromSin(s.toDouble(), ang.toDouble()).toFloat()
    val C = 1.0f - c
    val xx = x * x
    val xy = x * y
    val xz = x * z
    val yy = y * y
    val yz = y * z
    val zz = z * z
    val lm00 = xx * C + c
    val lm01 = xy * C + z * s
    val lm02 = xz * C - y * s
    val lm10 = xy * C - z * s
    val lm11 = yy * C + c
    val lm12 = yz * C + x * s
    val lm20 = xz * C + y * s
    val lm21 = yz * C - x * s
    val lm22 = zz * C + c
    val nm00 = lm00 * m00 + lm10 * m01 + lm20 * m02
    val nm01 = lm01 * m00 + lm11 * m01 + lm21 * m02
    val nm02 = lm02 * m00 + lm12 * m01 + lm22 * m02
    val nm03 = m03
    val nm10 = lm00 * m10 + lm10 * m11 + lm20 * m12
    val nm11 = lm01 * m10 + lm11 * m11 + lm21 * m12
    val nm12 = lm02 * m10 + lm12 * m11 + lm22 * m12
    val nm13 = m13
    val nm20 = lm00 * m20 + lm10 * m21 + lm20 * m22
    val nm21 = lm01 * m20 + lm11 * m21 + lm21 * m22
    val nm22 = lm02 * m20 + lm12 * m21 + lm22 * m22
    val nm23 = m23
    val nm30 = lm00 * m30 + lm10 * m31 + lm20 * m32
    val nm31 = lm01 * m30 + lm11 * m31 + lm21 * m32
    val nm32 = lm02 * m30 + lm12 * m31 + lm22 * m32
    val nm33 = m33
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Pre-multiply a rotation to this matrix by rotating the given amount of radians
   * about the specified <tt>(x, y, z)</tt> axis.
   *
   *
   * The axis described by the three components needs to be a unit vector.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `R * M`. So when transforming a
   * vector `v` with the new matrix by using `R * M * v`, the
   * rotation will be applied last!
   *
   *
   * In order to set the matrix to a rotation matrix without pre-multiplying the rotation
   * transformation, use [rotation()][.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotation
   * @param ang
   * the angle in radians
   * @param x
   * the x component of the axis
   * @param y
   * the y component of the axis
   * @param z
   * the z component of the axis
   * @return a matrix holding the result
   */
  fun rotateLocal(ang: Float, x: Float, y: Float, z: Float): Matrix4f {
    return rotateLocal(ang, x, y, z, thisOrNew())
  }

  /**
   * Pre-multiply a rotation around the X axis to this matrix by rotating the given amount of radians
   * about the X axis and store the result in `dest`.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `R * M`. So when transforming a
   * vector `v` with the new matrix by using `R * M * v`, the
   * rotation will be applied last!
   *
   *
   * In order to set the matrix to a rotation matrix without pre-multiplying the rotation
   * transformation, use [rotationX()][.rotationX].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotationX
   * @param ang
   * the angle in radians to rotate about the X axis
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotateLocalX(ang: Float, dest: Matrix4f): Matrix4f {
    val sin = Math.sin(ang.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    val nm01 = cos * m01 - sin * m02
    val nm02 = sin * m01 + cos * m02
    val nm11 = cos * m11 - sin * m12
    val nm12 = sin * m11 + cos * m12
    val nm21 = cos * m21 - sin * m22
    val nm22 = sin * m21 + cos * m22
    val nm31 = cos * m31 - sin * m32
    val nm32 = sin * m31 + cos * m32
    dest._m00(m00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(m03)
    dest._m10(m10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(m13)
    dest._m20(m20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(m23)
    dest._m30(m30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Pre-multiply a rotation to this matrix by rotating the given amount of radians about the X axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `R * M`. So when transforming a
   * vector `v` with the new matrix by using `R * M * v`, the
   * rotation will be applied last!
   *
   *
   * In order to set the matrix to a rotation matrix without pre-multiplying the rotation
   * transformation, use [rotationX()][.rotationX].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotationX
   * @param ang
   * the angle in radians to rotate about the X axis
   * @return a matrix holding the result
   */
  fun rotateLocalX(ang: Float): Matrix4f {
    return rotateLocalX(ang, thisOrNew())
  }

  /**
   * Pre-multiply a rotation around the Y axis to this matrix by rotating the given amount of radians
   * about the Y axis and store the result in `dest`.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `R * M`. So when transforming a
   * vector `v` with the new matrix by using `R * M * v`, the
   * rotation will be applied last!
   *
   *
   * In order to set the matrix to a rotation matrix without pre-multiplying the rotation
   * transformation, use [rotationY()][.rotationY].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotationY
   * @param ang
   * the angle in radians to rotate about the Y axis
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotateLocalY(ang: Float, dest: Matrix4f): Matrix4f {
    val sin = Math.sin(ang.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    val nm00 = cos * m00 + sin * m02
    val nm02 = -sin * m00 + cos * m02
    val nm10 = cos * m10 + sin * m12
    val nm12 = -sin * m10 + cos * m12
    val nm20 = cos * m20 + sin * m22
    val nm22 = -sin * m20 + cos * m22
    val nm30 = cos * m30 + sin * m32
    val nm32 = -sin * m30 + cos * m32
    dest._m00(nm00)
    dest._m01(m01)
    dest._m02(nm02)
    dest._m03(m03)
    dest._m10(nm10)
    dest._m11(m11)
    dest._m12(nm12)
    dest._m13(m13)
    dest._m20(nm20)
    dest._m21(m21)
    dest._m22(nm22)
    dest._m23(m23)
    dest._m30(nm30)
    dest._m31(m31)
    dest._m32(nm32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Pre-multiply a rotation to this matrix by rotating the given amount of radians about the Y axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `R * M`. So when transforming a
   * vector `v` with the new matrix by using `R * M * v`, the
   * rotation will be applied last!
   *
   *
   * In order to set the matrix to a rotation matrix without pre-multiplying the rotation
   * transformation, use [rotationY()][.rotationY].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotationY
   * @param ang
   * the angle in radians to rotate about the Y axis
   * @return a matrix holding the result
   */
  fun rotateLocalY(ang: Float): Matrix4f {
    return rotateLocalY(ang, thisOrNew())
  }

  /**
   * Pre-multiply a rotation around the Z axis to this matrix by rotating the given amount of radians
   * about the Z axis and store the result in `dest`.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `R * M`. So when transforming a
   * vector `v` with the new matrix by using `R * M * v`, the
   * rotation will be applied last!
   *
   *
   * In order to set the matrix to a rotation matrix without pre-multiplying the rotation
   * transformation, use [rotationZ()][.rotationZ].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotationZ
   * @param ang
   * the angle in radians to rotate about the Z axis
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotateLocalZ(ang: Float, dest: Matrix4f): Matrix4f {
    val sin = Math.sin(ang.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    val nm00 = cos * m00 - sin * m01
    val nm01 = sin * m00 + cos * m01
    val nm10 = cos * m10 - sin * m11
    val nm11 = sin * m10 + cos * m11
    val nm20 = cos * m20 - sin * m21
    val nm21 = sin * m20 + cos * m21
    val nm30 = cos * m30 - sin * m31
    val nm31 = sin * m30 + cos * m31
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(m02)
    dest._m03(m03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(m12)
    dest._m13(m13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(m22)
    dest._m23(m23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Pre-multiply a rotation to this matrix by rotating the given amount of radians about the Z axis.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `R` the rotation matrix,
   * then the new matrix will be `R * M`. So when transforming a
   * vector `v` with the new matrix by using `R * M * v`, the
   * rotation will be applied last!
   *
   *
   * In order to set the matrix to a rotation matrix without pre-multiplying the rotation
   * transformation, use [rotationY()][.rotationZ].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
   *
   * @see .rotationY
   * @param ang
   * the angle in radians to rotate about the Z axis
   * @return a matrix holding the result
   */
  fun rotateLocalZ(ang: Float): Matrix4f {
    return rotateLocalZ(ang, thisOrNew())
  }

  /**
   * Apply a translation to this matrix by translating by the given number of
   * units in x, y and z.
   *
   *
   * If `M` is `this` matrix and `T` the translation
   * matrix, then the new matrix will be `M * T`. So when
   * transforming a vector `v` with the new matrix by using
   * `M * T * v`, the translation will be applied first!
   *
   *
   * In order to set the matrix to a translation transformation without post-multiplying
   * it, use [.translation].
   *
   * @see .translation
   * @param offset
   * the number of units in x, y and z by which to translate
   * @return this
   */
  fun translate(offset: Vector3fc): Matrix4f {
    return translate(offset.x, offset.y, offset.z)
  }

  fun translateM(offset: Vector3): Matrix4f {
    return translate(offset.x, offset.y, offset.z)
  }

  fun translate(offset: Vector3): Matrix4f {
    return translate(offset.x, offset.y, offset.z)
  }

  /**
   * Apply a translation to this matrix by translating by the given number of
   * units in x, y and z and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `T` the translation
   * matrix, then the new matrix will be `M * T`. So when
   * transforming a vector `v` with the new matrix by using
   * `M * T * v`, the translation will be applied first!
   *
   *
   * In order to set the matrix to a translation transformation without post-multiplying
   * it, use [.translation].
   *
   * @see .translation
   * @param offset
   * the number of units in x, y and z by which to translate
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun translate(offset: Vector3fc, dest: Matrix4f): Matrix4f {
    return translate(offset.x, offset.y, offset.z, dest)
  }

  /**
   * Apply a translation to this matrix by translating by the given number of
   * units in x, y and z and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `T` the translation
   * matrix, then the new matrix will be `M * T`. So when
   * transforming a vector `v` with the new matrix by using
   * `M * T * v`, the translation will be applied first!
   *
   *
   * In order to set the matrix to a translation transformation without post-multiplying
   * it, use [.translation].
   *
   * @see .translation
   * @param x
   * the offset to translate in x
   * @param y
   * the offset to translate in y
   * @param z
   * the offset to translate in z
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun translate(x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.translation(x, y, z) else translateGeneric(x, y, z, dest)
  }

  private fun translateGeneric(x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    MemUtil.INSTANCE.copy(this, dest)
    dest._m30(m00 * x + m10 * y + m20 * z + m30)
    dest._m31(m01 * x + m11 * y + m21 * z + m31)
    dest._m32(m02 * x + m12 * y + m22 * z + m32)
    dest._m33(m03 * x + m13 * y + m23 * z + m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE or Matrix4fc.PROPERTY_IDENTITY).inv())
    return dest
  }

  /**
   * Apply a translation to this matrix by translating by the given number of
   * units in x, y and z.
   *
   *
   * If `M` is `this` matrix and `T` the translation
   * matrix, then the new matrix will be `M * T`. So when
   * transforming a vector `v` with the new matrix by using
   * `M * T * v`, the translation will be applied first!
   *
   *
   * In order to set the matrix to a translation transformation without post-multiplying
   * it, use [.translation].
   *
   * @see .translation
   * @param x
   * the offset to translate in x
   * @param y
   * the offset to translate in y
   * @param z
   * the offset to translate in z
   * @return this
   */
  fun translate(x: Float, y: Float, z: Float): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return translation(x, y, z)
    this._m30(m00 * x + m10 * y + m20 * z + m30)
    this._m31(m01 * x + m11 * y + m21 * z + m31)
    this._m32(m02 * x + m12 * y + m22 * z + m32)
    this._m33(m03 * x + m13 * y + m23 * z + m33)
    properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE or Matrix4fc.PROPERTY_IDENTITY).inv()
    return this
  }

  /**
   * Pre-multiply a translation to this matrix by translating by the given number of
   * units in x, y and z.
   *
   *
   * If `M` is `this` matrix and `T` the translation
   * matrix, then the new matrix will be `T * M`. So when
   * transforming a vector `v` with the new matrix by using
   * `T * M * v`, the translation will be applied last!
   *
   *
   * In order to set the matrix to a translation transformation without pre-multiplying
   * it, use [.translation].
   *
   * @see .translation
   * @param offset
   * the number of units in x, y and z by which to translate
   * @return this
   */
  fun translateLocal(offset: Vector3fc): Matrix4f {
    return translateLocal(offset.x, offset.y, offset.z)
  }

  /**
   * Pre-multiply a translation to this matrix by translating by the given number of
   * units in x, y and z and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `T` the translation
   * matrix, then the new matrix will be `T * M`. So when
   * transforming a vector `v` with the new matrix by using
   * `T * M * v`, the translation will be applied last!
   *
   *
   * In order to set the matrix to a translation transformation without pre-multiplying
   * it, use [.translation].
   *
   * @see .translation
   * @param offset
   * the number of units in x, y and z by which to translate
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun translateLocal(offset: Vector3fc, dest: Matrix4f): Matrix4f {
    return translateLocal(offset.x, offset.y, offset.z, dest)
  }

  /**
   * Pre-multiply a translation to this matrix by translating by the given number of
   * units in x, y and z and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `T` the translation
   * matrix, then the new matrix will be `T * M`. So when
   * transforming a vector `v` with the new matrix by using
   * `T * M * v`, the translation will be applied last!
   *
   *
   * In order to set the matrix to a translation transformation without pre-multiplying
   * it, use [.translation].
   *
   * @see .translation
   * @param x
   * the offset to translate in x
   * @param y
   * the offset to translate in y
   * @param z
   * the offset to translate in z
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun translateLocal(x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.translation(x, y, z) else translateLocalGeneric(x, y, z, dest)
  }

  private fun translateLocalGeneric(x: Float, y: Float, z: Float, dest: Matrix4f): Matrix4f {
    val nm00 = m00 + x * m03
    val nm01 = m01 + y * m03
    val nm02 = m02 + z * m03
    val nm03 = m03
    val nm10 = m10 + x * m13
    val nm11 = m11 + y * m13
    val nm12 = m12 + z * m13
    val nm13 = m13
    val nm20 = m20 + x * m23
    val nm21 = m21 + y * m23
    val nm22 = m22 + z * m23
    val nm23 = m23
    val nm30 = m30 + x * m33
    val nm31 = m31 + y * m33
    val nm32 = m32 + z * m33
    val nm33 = m33
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE or Matrix4fc.PROPERTY_IDENTITY).inv())
    return dest
  }

  /**
   * Pre-multiply a translation to this matrix by translating by the given number of
   * units in x, y and z.
   *
   *
   * If `M` is `this` matrix and `T` the translation
   * matrix, then the new matrix will be `T * M`. So when
   * transforming a vector `v` with the new matrix by using
   * `T * M * v`, the translation will be applied last!
   *
   *
   * In order to set the matrix to a translation transformation without pre-multiplying
   * it, use [.translation].
   *
   * @see .translation
   * @param x
   * the offset to translate in x
   * @param y
   * the offset to translate in y
   * @param z
   * the offset to translate in z
   * @return a matrix holding the result
   */
  fun translateLocal(x: Float, y: Float, z: Float): Matrix4f {
    return translateLocal(x, y, z, thisOrNew())
  }

  @Throws(IOException::class)
  override fun writeExternal(out: ObjectOutput) {
    out.writeFloat(m00)
    out.writeFloat(m01)
    out.writeFloat(m02)
    out.writeFloat(m03)
    out.writeFloat(m10)
    out.writeFloat(m11)
    out.writeFloat(m12)
    out.writeFloat(m13)
    out.writeFloat(m20)
    out.writeFloat(m21)
    out.writeFloat(m22)
    out.writeFloat(m23)
    out.writeFloat(m30)
    out.writeFloat(m31)
    out.writeFloat(m32)
    out.writeFloat(m33)
  }

  @Throws(IOException::class)
  override fun readExternal(`in`: ObjectInput) {
    this._m00(`in`.readFloat())
    this._m01(`in`.readFloat())
    this._m02(`in`.readFloat())
    this._m03(`in`.readFloat())
    this._m10(`in`.readFloat())
    this._m11(`in`.readFloat())
    this._m12(`in`.readFloat())
    this._m13(`in`.readFloat())
    this._m20(`in`.readFloat())
    this._m21(`in`.readFloat())
    this._m22(`in`.readFloat())
    this._m23(`in`.readFloat())
    this._m30(`in`.readFloat())
    this._m31(`in`.readFloat())
    this._m32(`in`.readFloat())
    this._m33(`in`.readFloat())
    _properties(0)
  }

  /**
   * Apply an orthographic projection transformation for a right-handed coordinate system
   * using the given NDC z range to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to an orthographic projection without post-multiplying it,
   * use [setOrtho()][.setOrtho].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrtho
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun ortho(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.setOrtho(left, right, bottom, top, zNear, zFar, zZeroToOne) else orthoGeneric(left, right, bottom, top, zNear, zFar, zZeroToOne, dest)
  }

  private fun orthoGeneric(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    // calculate right matrix elements
    val rm00 = 2.0f / (right - left)
    val rm11 = 2.0f / (top - bottom)
    val rm22 = (if (zZeroToOne) 1.0f else 2.0f) / (zNear - zFar)
    val rm30 = (left + right) / (left - right)
    val rm31 = (top + bottom) / (bottom - top)
    val rm32 = (if (zZeroToOne) zNear else zFar + zNear) / (zNear - zFar)
    // perform optimized multiplication
    // compute the last column first, because other columns do not depend on it
    dest._m30(m00 * rm30 + m10 * rm31 + m20 * rm32 + m30)
    dest._m31(m01 * rm30 + m11 * rm31 + m21 * rm32 + m31)
    dest._m32(m02 * rm30 + m12 * rm31 + m22 * rm32 + m32)
    dest._m33(m03 * rm30 + m13 * rm31 + m23 * rm32 + m33)
    dest._m00(m00 * rm00)
    dest._m01(m01 * rm00)
    dest._m02(m02 * rm00)
    dest._m03(m03 * rm00)
    dest._m10(m10 * rm11)
    dest._m11(m11 * rm11)
    dest._m12(m12 * rm11)
    dest._m13(m13 * rm11)
    dest._m20(m20 * rm22)
    dest._m21(m21 * rm22)
    dest._m22(m22 * rm22)
    dest._m23(m23 * rm22)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())
    return dest
  }

  /**
   * Apply an orthographic projection transformation for a right-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to an orthographic projection without post-multiplying it,
   * use [setOrtho()][.setOrtho].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrtho
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun ortho(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, dest: Matrix4f): Matrix4f {
    return ortho(left, right, bottom, top, zNear, zFar, false, dest)
  }

  /**
   * Apply an orthographic projection transformation for a right-handed coordinate system using the given NDC z range to this matrix.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to an orthographic projection without post-multiplying it,
   * use [setOrtho()][.setOrtho].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrtho
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return a matrix holding the result
   */
  @JvmOverloads
  fun ortho(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean = false): Matrix4f {
    return ortho(left, right, bottom, top, zNear, zFar, zZeroToOne, thisOrNew())
  }

  /**
   * Apply an orthographic projection transformation for a left-handed coordiante system
   * using the given NDC z range to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to an orthographic projection without post-multiplying it,
   * use [setOrthoLH()][.setOrthoLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoLH
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun orthoLH(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.setOrthoLH(left, right, bottom, top, zNear, zFar, zZeroToOne) else orthoLHGeneric(left, right, bottom, top, zNear, zFar, zZeroToOne, dest)
  }

  private fun orthoLHGeneric(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    // calculate right matrix elements
    val rm00 = 2.0f / (right - left)
    val rm11 = 2.0f / (top - bottom)
    val rm22 = (if (zZeroToOne) 1.0f else 2.0f) / (zFar - zNear)
    val rm30 = (left + right) / (left - right)
    val rm31 = (top + bottom) / (bottom - top)
    val rm32 = (if (zZeroToOne) zNear else zFar + zNear) / (zNear - zFar)

    // perform optimized multiplication
    // compute the last column first, because other columns do not depend on it
    dest._m30(m00 * rm30 + m10 * rm31 + m20 * rm32 + m30)
    dest._m31(m01 * rm30 + m11 * rm31 + m21 * rm32 + m31)
    dest._m32(m02 * rm30 + m12 * rm31 + m22 * rm32 + m32)
    dest._m33(m03 * rm30 + m13 * rm31 + m23 * rm32 + m33)
    dest._m00(m00 * rm00)
    dest._m01(m01 * rm00)
    dest._m02(m02 * rm00)
    dest._m03(m03 * rm00)
    dest._m10(m10 * rm11)
    dest._m11(m11 * rm11)
    dest._m12(m12 * rm11)
    dest._m13(m13 * rm11)
    dest._m20(m20 * rm22)
    dest._m21(m21 * rm22)
    dest._m22(m22 * rm22)
    dest._m23(m23 * rm22)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())

    return dest
  }

  /**
   * Apply an orthographic projection transformation for a left-handed coordiante system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to an orthographic projection without post-multiplying it,
   * use [setOrthoLH()][.setOrthoLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoLH
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun orthoLH(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, dest: Matrix4f): Matrix4f {
    return orthoLH(left, right, bottom, top, zNear, zFar, false, dest)
  }

  /**
   * Apply an orthographic projection transformation for a left-handed coordiante system
   * using the given NDC z range to this matrix.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to an orthographic projection without post-multiplying it,
   * use [setOrthoLH()][.setOrthoLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoLH
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return a matrix holding the result
   */
  @JvmOverloads
  fun orthoLH(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean = false): Matrix4f {
    return orthoLH(left, right, bottom, top, zNear, zFar, zZeroToOne, thisOrNew())
  }

  /**
   * Set this matrix to be an orthographic projection transformation for a right-handed coordinate system
   * using the given NDC z range.
   *
   *
   * In order to apply the orthographic projection to an already existing transformation,
   * use [ortho()][.ortho].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .ortho
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return this
   */
  @JvmOverloads
  fun setOrtho(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean = false): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00(2.0f / (right - left))
    this._m11(2.0f / (top - bottom))
    this._m22((if (zZeroToOne) 1.0f else 2.0f) / (zNear - zFar))
    this._m30((right + left) / (left - right))
    this._m31((top + bottom) / (bottom - top))
    this._m32((if (zZeroToOne) zNear else zFar + zNear) / (zNear - zFar))
    _properties(Matrix4fc.PROPERTY_AFFINE.toInt())
    return this
  }

  /**
   * Set this matrix to be an orthographic projection transformation for a left-handed coordinate system
   * using the given NDC z range.
   *
   *
   * In order to apply the orthographic projection to an already existing transformation,
   * use [orthoLH()][.orthoLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .orthoLH
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return this
   */
  @JvmOverloads
  fun setOrthoLH(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean = false): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00(2.0f / (right - left))
    this._m11(2.0f / (top - bottom))
    this._m22((if (zZeroToOne) 1.0f else 2.0f) / (zFar - zNear))
    this._m30((right + left) / (left - right))
    this._m31((top + bottom) / (bottom - top))
    this._m32((if (zZeroToOne) zNear else zFar + zNear) / (zNear - zFar))
    _properties(Matrix4fc.PROPERTY_AFFINE.toInt())
    return this
  }

  /**
   * Apply a symmetric orthographic projection transformation for a right-handed coordinate system
   * using the given NDC z range to this matrix and store the result in `dest`.
   *
   *
   * This method is equivalent to calling [ortho()][.ortho] with
   * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
   * use [setOrthoSymmetric()][.setOrthoSymmetric].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoSymmetric
   * @param width
   * the distance between the right and left frustum edges
   * @param height
   * the distance between the top and bottom frustum edges
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param dest
   * will hold the result
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return dest
   */
  override fun orthoSymmetric(width: Float, height: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.setOrthoSymmetric(width, height, zNear, zFar, zZeroToOne) else orthoSymmetricGeneric(width, height, zNear, zFar, zZeroToOne, dest)
  }

  private fun orthoSymmetricGeneric(width: Float, height: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    // calculate right matrix elements
    val rm00 = 2.0f / width
    val rm11 = 2.0f / height
    val rm22 = (if (zZeroToOne) 1.0f else 2.0f) / (zNear - zFar)
    val rm32 = (if (zZeroToOne) zNear else zFar + zNear) / (zNear - zFar)
    // perform optimized multiplication
    // compute the last column first, because other columns do not depend on it
    dest._m30(m20 * rm32 + m30)
    dest._m31(m21 * rm32 + m31)
    dest._m32(m22 * rm32 + m32)
    dest._m33(m23 * rm32 + m33)
    dest._m00(m00 * rm00)
    dest._m01(m01 * rm00)
    dest._m02(m02 * rm00)
    dest._m03(m03 * rm00)
    dest._m10(m10 * rm11)
    dest._m11(m11 * rm11)
    dest._m12(m12 * rm11)
    dest._m13(m13 * rm11)
    dest._m20(m20 * rm22)
    dest._m21(m21 * rm22)
    dest._m22(m22 * rm22)
    dest._m23(m23 * rm22)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())
    return dest
  }

  /**
   * Apply a symmetric orthographic projection transformation for a right-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix and store the result in `dest`.
   *
   *
   * This method is equivalent to calling [ortho()][.ortho] with
   * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
   * use [setOrthoSymmetric()][.setOrthoSymmetric].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoSymmetric
   * @param width
   * the distance between the right and left frustum edges
   * @param height
   * the distance between the top and bottom frustum edges
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun orthoSymmetric(width: Float, height: Float, zNear: Float, zFar: Float, dest: Matrix4f): Matrix4f {
    return orthoSymmetric(width, height, zNear, zFar, false, dest)
  }

  /**
   * Apply a symmetric orthographic projection transformation for a right-handed coordinate system
   * using the given NDC z range to this matrix.
   *
   *
   * This method is equivalent to calling [ortho()][.ortho] with
   * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
   * use [setOrthoSymmetric()][.setOrthoSymmetric].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoSymmetric
   * @param width
   * the distance between the right and left frustum edges
   * @param height
   * the distance between the top and bottom frustum edges
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return a matrix holding the result
   */
  fun orthoSymmetric(width: Float, height: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean): Matrix4f {
    return orthoSymmetric(width, height, zNear, zFar, zZeroToOne, thisOrNew())
  }

  /**
   * Apply a symmetric orthographic projection transformation for a right-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix.
   *
   *
   * This method is equivalent to calling [ortho()][.ortho] with
   * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
   * use [setOrthoSymmetric()][.setOrthoSymmetric].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoSymmetric
   * @param width
   * the distance between the right and left frustum edges
   * @param height
   * the distance between the top and bottom frustum edges
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @return a matrix holding the result
   */
  fun orthoSymmetric(width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f {
    return orthoSymmetric(width, height, zNear, zFar, false, thisOrNew())
  }

  /**
   * Apply a symmetric orthographic projection transformation for a left-handed coordinate system
   * using the given NDC z range to this matrix and store the result in `dest`.
   *
   *
   * This method is equivalent to calling [orthoLH()][.orthoLH] with
   * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
   * use [setOrthoSymmetricLH()][.setOrthoSymmetricLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoSymmetricLH
   * @param width
   * the distance between the right and left frustum edges
   * @param height
   * the distance between the top and bottom frustum edges
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param dest
   * will hold the result
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return dest
   */
  override fun orthoSymmetricLH(width: Float, height: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.setOrthoSymmetricLH(width, height, zNear, zFar, zZeroToOne) else orthoSymmetricLHGeneric(width, height, zNear, zFar, zZeroToOne, dest)
  }

  private fun orthoSymmetricLHGeneric(width: Float, height: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    // calculate right matrix elements
    val rm00 = 2.0f / width
    val rm11 = 2.0f / height
    val rm22 = (if (zZeroToOne) 1.0f else 2.0f) / (zFar - zNear)
    val rm32 = (if (zZeroToOne) zNear else zFar + zNear) / (zNear - zFar)
    // perform optimized multiplication
    // compute the last column first, because other columns do not depend on it
    dest._m30(m20 * rm32 + m30)
    dest._m31(m21 * rm32 + m31)
    dest._m32(m22 * rm32 + m32)
    dest._m33(m23 * rm32 + m33)
    dest._m00(m00 * rm00)
    dest._m01(m01 * rm00)
    dest._m02(m02 * rm00)
    dest._m03(m03 * rm00)
    dest._m10(m10 * rm11)
    dest._m11(m11 * rm11)
    dest._m12(m12 * rm11)
    dest._m13(m13 * rm11)
    dest._m20(m20 * rm22)
    dest._m21(m21 * rm22)
    dest._m22(m22 * rm22)
    dest._m23(m23 * rm22)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())
    return dest
  }

  /**
   * Apply a symmetric orthographic projection transformation for a left-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix and store the result in `dest`.
   *
   *
   * This method is equivalent to calling [orthoLH()][.orthoLH] with
   * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
   * use [setOrthoSymmetricLH()][.setOrthoSymmetricLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoSymmetricLH
   * @param width
   * the distance between the right and left frustum edges
   * @param height
   * the distance between the top and bottom frustum edges
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun orthoSymmetricLH(width: Float, height: Float, zNear: Float, zFar: Float, dest: Matrix4f): Matrix4f {
    return orthoSymmetricLH(width, height, zNear, zFar, false, dest)
  }

  /**
   * Apply a symmetric orthographic projection transformation for a left-handed coordinate system
   * using the given NDC z range to this matrix.
   *
   *
   * This method is equivalent to calling [orthoLH()][.orthoLH] with
   * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
   * use [setOrthoSymmetricLH()][.setOrthoSymmetricLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoSymmetricLH
   * @param width
   * the distance between the right and left frustum edges
   * @param height
   * the distance between the top and bottom frustum edges
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return a matrix holding the result
   */
  fun orthoSymmetricLH(width: Float, height: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean): Matrix4f {
    return orthoSymmetricLH(width, height, zNear, zFar, zZeroToOne, thisOrNew())
  }

  /**
   * Apply a symmetric orthographic projection transformation for a left-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix.
   *
   *
   * This method is equivalent to calling [orthoLH()][.orthoLH] with
   * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
   * use [setOrthoSymmetricLH()][.setOrthoSymmetricLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoSymmetricLH
   * @param width
   * the distance between the right and left frustum edges
   * @param height
   * the distance between the top and bottom frustum edges
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @return a matrix holding the result
   */
  fun orthoSymmetricLH(width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f {
    return orthoSymmetricLH(width, height, zNear, zFar, false, thisOrNew())
  }

  /**
   * Set this matrix to be a symmetric orthographic projection transformation for a right-handed coordinate system using the given NDC z range.
   *
   *
   * This method is equivalent to calling [setOrtho()][.setOrtho] with
   * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
   *
   *
   * In order to apply the symmetric orthographic projection to an already existing transformation,
   * use [orthoSymmetric()][.orthoSymmetric].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .orthoSymmetric
   * @param width
   * the distance between the right and left frustum edges
   * @param height
   * the distance between the top and bottom frustum edges
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return this
   */
  @JvmOverloads
  fun setOrthoSymmetric(width: Float, height: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean = false): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00(2.0f / width)
    this._m11(2.0f / height)
    this._m22((if (zZeroToOne) 1.0f else 2.0f) / (zNear - zFar))
    this._m32((if (zZeroToOne) zNear else zFar + zNear) / (zNear - zFar))
    _properties(Matrix4fc.PROPERTY_AFFINE.toInt())
    return this
  }

  /**
   * Set this matrix to be a symmetric orthographic projection transformation for a left-handed coordinate system using the given NDC z range.
   *
   *
   * This method is equivalent to calling [setOrtho()][.setOrtho] with
   * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
   *
   *
   * In order to apply the symmetric orthographic projection to an already existing transformation,
   * use [orthoSymmetricLH()][.orthoSymmetricLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .orthoSymmetricLH
   * @param width
   * the distance between the right and left frustum edges
   * @param height
   * the distance between the top and bottom frustum edges
   * @param zNear
   * near clipping plane distance
   * @param zFar
   * far clipping plane distance
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return this
   */
  @JvmOverloads
  fun setOrthoSymmetricLH(width: Float, height: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean = false): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00(2.0f / width)
    this._m11(2.0f / height)
    this._m22((if (zZeroToOne) 1.0f else 2.0f) / (zFar - zNear))
    this._m32((if (zZeroToOne) zNear else zFar + zNear) / (zNear - zFar))
    _properties(Matrix4fc.PROPERTY_AFFINE.toInt())
    return this
  }

  /**
   * Apply an orthographic projection transformation for a right-handed coordinate system to this matrix
   * and store the result in `dest`.
   *
   *
   * This method is equivalent to calling [ortho()][.ortho] with
   * `zNear=-1` and `zFar=+1`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to an orthographic projection without post-multiplying it,
   * use [setOrtho()][.setOrtho2D].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .ortho
   * @see .setOrtho2D
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun ortho2D(left: Float, right: Float, bottom: Float, top: Float, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.setOrtho2D(left, right, bottom, top) else ortho2DGeneric(left, right, bottom, top, dest)
  }

  private fun ortho2DGeneric(left: Float, right: Float, bottom: Float, top: Float, dest: Matrix4f): Matrix4f {
    // calculate right matrix elements
    val rm00 = 2.0f / (right - left)
    val rm11 = 2.0f / (top - bottom)
    val rm30 = -(right + left) / (right - left)
    val rm31 = -(top + bottom) / (top - bottom)
    // perform optimized multiplication
    // compute the last column first, because other columns do not depend on it
    dest._m30(m00 * rm30 + m10 * rm31 + m30)
    dest._m31(m01 * rm30 + m11 * rm31 + m31)
    dest._m32(m02 * rm30 + m12 * rm31 + m32)
    dest._m33(m03 * rm30 + m13 * rm31 + m33)
    dest._m00(m00 * rm00)
    dest._m01(m01 * rm00)
    dest._m02(m02 * rm00)
    dest._m03(m03 * rm00)
    dest._m10(m10 * rm11)
    dest._m11(m11 * rm11)
    dest._m12(m12 * rm11)
    dest._m13(m13 * rm11)
    dest._m20(-m20)
    dest._m21(-m21)
    dest._m22(-m22)
    dest._m23(-m23)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())
    return dest
  }

  /**
   * Apply an orthographic projection transformation for a right-handed coordinate system to this matrix.
   *
   *
   * This method is equivalent to calling [ortho()][.ortho] with
   * `zNear=-1` and `zFar=+1`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to an orthographic projection without post-multiplying it,
   * use [setOrtho2D()][.setOrtho2D].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .ortho
   * @see .setOrtho2D
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @return a matrix holding the result
   */
  fun ortho2D(left: Float, right: Float, bottom: Float, top: Float): Matrix4f {
    return ortho2D(left, right, bottom, top, thisOrNew())
  }

  /**
   * Apply an orthographic projection transformation for a left-handed coordinate system to this matrix and store the result in `dest`.
   *
   *
   * This method is equivalent to calling [orthoLH()][.orthoLH] with
   * `zNear=-1` and `zFar=+1`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to an orthographic projection without post-multiplying it,
   * use [setOrthoLH()][.setOrtho2DLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .orthoLH
   * @see .setOrtho2DLH
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun ortho2DLH(left: Float, right: Float, bottom: Float, top: Float, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.setOrtho2DLH(left, right, bottom, top) else ortho2DLHGeneric(left, right, bottom, top, dest)
  }

  private fun ortho2DLHGeneric(left: Float, right: Float, bottom: Float, top: Float, dest: Matrix4f): Matrix4f {
    // calculate right matrix elements
    val rm00 = 2.0f / (right - left)
    val rm11 = 2.0f / (top - bottom)
    val rm30 = -(right + left) / (right - left)
    val rm31 = -(top + bottom) / (top - bottom)

    // perform optimized multiplication
    // compute the last column first, because other columns do not depend on it
    dest._m30(m00 * rm30 + m10 * rm31 + m30)
    dest._m31(m01 * rm30 + m11 * rm31 + m31)
    dest._m32(m02 * rm30 + m12 * rm31 + m32)
    dest._m33(m03 * rm30 + m13 * rm31 + m33)
    dest._m00(m00 * rm00)
    dest._m01(m01 * rm00)
    dest._m02(m02 * rm00)
    dest._m03(m03 * rm00)
    dest._m10(m10 * rm11)
    dest._m11(m11 * rm11)
    dest._m12(m12 * rm11)
    dest._m13(m13 * rm11)
    dest._m20(m20)
    dest._m21(m21)
    dest._m22(m22)
    dest._m23(m23)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())

    return dest
  }

  /**
   * Apply an orthographic projection transformation for a left-handed coordinate system to this matrix.
   *
   *
   * This method is equivalent to calling [orthoLH()][.orthoLH] with
   * `zNear=-1` and `zFar=+1`.
   *
   *
   * If `M` is `this` matrix and `O` the orthographic projection matrix,
   * then the new matrix will be `M * O`. So when transforming a
   * vector `v` with the new matrix by using `M * O * v`, the
   * orthographic projection transformation will be applied first!
   *
   *
   * In order to set the matrix to an orthographic projection without post-multiplying it,
   * use [setOrtho2DLH()][.setOrtho2DLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .orthoLH
   * @see .setOrtho2DLH
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @return a matrix holding the result
   */
  fun ortho2DLH(left: Float, right: Float, bottom: Float, top: Float): Matrix4f {
    return ortho2DLH(left, right, bottom, top, thisOrNew())
  }

  /**
   * Set this matrix to be an orthographic projection transformation for a right-handed coordinate system.
   *
   *
   * This method is equivalent to calling [setOrtho()][.setOrtho] with
   * `zNear=-1` and `zFar=+1`.
   *
   *
   * In order to apply the orthographic projection to an already existing transformation,
   * use [ortho2D()][.ortho2D].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrtho
   * @see .ortho2D
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @return this
   */
  fun setOrtho2D(left: Float, right: Float, bottom: Float, top: Float): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00(2.0f / (right - left))
    this._m11(2.0f / (top - bottom))
    this._m22(-1.0f)
    this._m30(-(right + left) / (right - left))
    this._m31(-(top + bottom) / (top - bottom))
    _properties(Matrix4fc.PROPERTY_AFFINE.toInt())
    return this
  }

  /**
   * Set this matrix to be an orthographic projection transformation for a left-handed coordinate system.
   *
   *
   * This method is equivalent to calling [setOrthoLH()][.setOrtho] with
   * `zNear=-1` and `zFar=+1`.
   *
   *
   * In order to apply the orthographic projection to an already existing transformation,
   * use [ortho2DLH()][.ortho2DLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
   *
   * @see .setOrthoLH
   * @see .ortho2DLH
   * @param left
   * the distance from the center to the left frustum edge
   * @param right
   * the distance from the center to the right frustum edge
   * @param bottom
   * the distance from the center to the bottom frustum edge
   * @param top
   * the distance from the center to the top frustum edge
   * @return this
   */
  fun setOrtho2DLH(left: Float, right: Float, bottom: Float, top: Float): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00(2.0f / (right - left))
    this._m11(2.0f / (top - bottom))
    this._m30(-(right + left) / (right - left))
    this._m31(-(top + bottom) / (top - bottom))
    _properties(Matrix4fc.PROPERTY_AFFINE.toInt())
    return this
  }

  /**
   * Apply a rotation transformation to this matrix to make `-z` point along `dir`.
   *
   *
   * If `M` is `this` matrix and `L` the lookalong rotation matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`, the
   * lookalong rotation transformation will be applied first!
   *
   *
   * This is equivalent to calling
   * [lookAt][.lookAt]
   * with `eye = (0, 0, 0)` and `center = dir`.
   *
   *
   * In order to set the matrix to a lookalong transformation without post-multiplying it,
   * use [setLookAlong()][.setLookAlong].
   *
   * @see .lookAlong
   * @see .lookAt
   * @see .setLookAlong
   * @param dir
   * the direction in space to look along
   * @param up
   * the direction of 'up'
   * @return a matrix holding the result
   */
  fun lookAlong(dir: Vector3fc, up: Vector3fc): Matrix4f {
    return lookAlong(dir.x, dir.y, dir.z, up.x, up.y, up.z, thisOrNew())
  }

  /**
   * Apply a rotation transformation to this matrix to make `-z` point along `dir`
   * and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `L` the lookalong rotation matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`, the
   * lookalong rotation transformation will be applied first!
   *
   *
   * This is equivalent to calling
   * [lookAt][.lookAt]
   * with `eye = (0, 0, 0)` and `center = dir`.
   *
   *
   * In order to set the matrix to a lookalong transformation without post-multiplying it,
   * use [setLookAlong()][.setLookAlong].
   *
   * @see .lookAlong
   * @see .lookAt
   * @see .setLookAlong
   * @param dir
   * the direction in space to look along
   * @param up
   * the direction of 'up'
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun lookAlong(dir: Vector3fc, up: Vector3fc, dest: Matrix4f): Matrix4f {
    return lookAlong(dir.x, dir.y, dir.z, up.x, up.y, up.z, dest)
  }

  /**
   * Apply a rotation transformation to this matrix to make `-z` point along `dir`
   * and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `L` the lookalong rotation matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`, the
   * lookalong rotation transformation will be applied first!
   *
   *
   * This is equivalent to calling
   * [lookAt()][.lookAt]
   * with `eye = (0, 0, 0)` and `center = dir`.
   *
   *
   * In order to set the matrix to a lookalong transformation without post-multiplying it,
   * use [setLookAlong()][.setLookAlong]
   *
   * @see .lookAt
   * @see .setLookAlong
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
  override fun lookAlong(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.setLookAlong(dirX, dirY, dirZ, upX, upY, upZ) else lookAlongGeneric(dirX, dirY, dirZ, upX, upY, upZ, dest)
  }

  private fun lookAlongGeneric(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float, dest: Matrix4f): Matrix4f {
    var dirX = dirX
    var dirY = dirY
    var dirZ = dirZ
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    dirX *= -invDirLength
    dirY *= -invDirLength
    dirZ *= -invDirLength
    // left = up x direction
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * dirZ - upZ * dirY
    leftY = upZ * dirX - upX * dirZ
    leftZ = upX * dirY - upY * dirX
    // normalize left
    val invLeftLength = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = dirY * leftZ - dirZ * leftY
    val upnY = dirZ * leftX - dirX * leftZ
    val upnZ = dirX * leftY - dirY * leftX
    // calculate right matrix elements
    val rm02 = dirX
    val rm12 = dirY
    val rm22 = dirZ
    // perform optimized matrix multiplication
    // introduce temporaries for dependent results
    val nm00 = m00 * leftX + m10 * upnX + m20 * rm02
    val nm01 = m01 * leftX + m11 * upnX + m21 * rm02
    val nm02 = m02 * leftX + m12 * upnX + m22 * rm02
    val nm03 = m03 * leftX + m13 * upnX + m23 * rm02
    val nm10 = m00 * leftY + m10 * upnY + m20 * rm12
    val nm11 = m01 * leftY + m11 * upnY + m21 * rm12
    val nm12 = m02 * leftY + m12 * upnY + m22 * rm12
    val nm13 = m03 * leftY + m13 * upnY + m23 * rm12
    dest._m20(m00 * leftZ + m10 * upnZ + m20 * rm22)
    dest._m21(m01 * leftZ + m11 * upnZ + m21 * rm22)
    dest._m22(m02 * leftZ + m12 * upnZ + m22 * rm22)
    dest._m23(m03 * leftZ + m13 * upnZ + m23 * rm22)
    // set the rest of the matrix elements
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply a rotation transformation to this matrix to make `-z` point along `dir`.
   *
   *
   * If `M` is `this` matrix and `L` the lookalong rotation matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`, the
   * lookalong rotation transformation will be applied first!
   *
   *
   * This is equivalent to calling
   * [lookAt()][.lookAt]
   * with `eye = (0, 0, 0)` and `center = dir`.
   *
   *
   * In order to set the matrix to a lookalong transformation without post-multiplying it,
   * use [setLookAlong()][.setLookAlong]
   *
   * @see .lookAt
   * @see .setLookAlong
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
   * @return a matrix holding the result
   */
  fun lookAlong(dirX: Float, dirY: Float, dirZ: Float,
                upX: Float, upY: Float, upZ: Float): Matrix4f {
    return lookAlong(dirX, dirY, dirZ, upX, upY, upZ, thisOrNew())
  }

  /**
   * Set this matrix to a rotation transformation to make `-z`
   * point along `dir`.
   *
   *
   * This is equivalent to calling
   * [setLookAt()][.setLookAt]
   * with `eye = (0, 0, 0)` and `center = dir`.
   *
   *
   * In order to apply the lookalong transformation to any previous existing transformation,
   * use [.lookAlong].
   *
   * @see .setLookAlong
   * @see .lookAlong
   * @param dir
   * the direction in space to look along
   * @param up
   * the direction of 'up'
   * @return this
   */
  fun setLookAlong(dir: Vector3fc, up: Vector3fc): Matrix4f {
    return setLookAlong(dir.x, dir.y, dir.z, up.x, up.y, up.z)
  }

  /**
   * Set this matrix to a rotation transformation to make `-z`
   * point along `dir`.
   *
   *
   * This is equivalent to calling
   * [ setLookAt()][.setLookAt] with `eye = (0, 0, 0)` and `center = dir`.
   *
   *
   * In order to apply the lookalong transformation to any previous existing transformation,
   * use [lookAlong()][.lookAlong]
   *
   * @see .setLookAlong
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
  fun setLookAlong(dirX: Float, dirY: Float, dirZ: Float,
                   upX: Float, upY: Float, upZ: Float): Matrix4f {
    var dirX = dirX
    var dirY = dirY
    var dirZ = dirZ
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    dirX *= -invDirLength
    dirY *= -invDirLength
    dirZ *= -invDirLength
    // left = up x direction
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * dirZ - upZ * dirY
    leftY = upZ * dirX - upX * dirZ
    leftZ = upX * dirY - upY * dirX
    // normalize left
    val invLeftLength = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = dirY * leftZ - dirZ * leftY
    val upnY = dirZ * leftX - dirX * leftZ
    val upnZ = dirX * leftY - dirY * leftX

    this._m00(leftX)
    this._m01(upnX)
    this._m02(dirX)
    this._m03(0.0f)
    this._m10(leftY)
    this._m11(upnY)
    this._m12(dirY)
    this._m13(0.0f)
    this._m20(leftZ)
    this._m21(upnZ)
    this._m22(dirZ)
    this._m23(0.0f)
    this._m30(0.0f)
    this._m31(0.0f)
    this._m32(0.0f)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)

    return this
  }

  /**
   * Set this matrix to be a "lookat" transformation for a right-handed coordinate system, that aligns
   * `-z` with `center - eye`.
   *
   *
   * In order to not make use of vectors to specify `eye`, `center` and `up` but use primitives,
   * like in the GLU function, use [setLookAt()][.setLookAt]
   * instead.
   *
   *
   * In order to apply the lookat transformation to a previous existing transformation,
   * use [lookAt()][.lookAt].
   *
   * @see .setLookAt
   * @see .lookAt
   * @param eye
   * the position of the camera
   * @param center
   * the point in space to look at
   * @param up
   * the direction of 'up'
   * @return this
   */
  fun setLookAt(eye: Vector3fc, center: Vector3fc, up: Vector3fc): Matrix4f {
    return setLookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z)
  }

  fun setLookAt(eye: Vector3, center: Vector3, up: Vector3): Matrix4f {
    return setLookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z)
  }

  /**
   * Set this matrix to be a "lookat" transformation for a right-handed coordinate system,
   * that aligns `-z` with `center - eye`.
   *
   *
   * In order to apply the lookat transformation to a previous existing transformation,
   * use [lookAt][.lookAt].
   *
   * @see .setLookAt
   * @see .lookAt
   * @param eyeX
   * the x-coordinate of the eye/camera location
   * @param eyeY
   * the y-coordinate of the eye/camera location
   * @param eyeZ
   * the z-coordinate of the eye/camera location
   * @param centerX
   * the x-coordinate of the point to look at
   * @param centerY
   * the y-coordinate of the point to look at
   * @param centerZ
   * the z-coordinate of the point to look at
   * @param upX
   * the x-coordinate of the up vector
   * @param upY
   * the y-coordinate of the up vector
   * @param upZ
   * the z-coordinate of the up vector
   * @return this
   */
  fun setLookAt(eyeX: Float, eyeY: Float, eyeZ: Float,
                centerX: Float, centerY: Float, centerZ: Float,
                upX: Float, upY: Float, upZ: Float): Matrix4f {
    // Compute direction from position to lookAt
    var dirX: Float
    var dirY: Float
    var dirZ: Float
    dirX = eyeX - centerX
    dirY = eyeY - centerY
    dirZ = eyeZ - centerZ
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    dirX *= invDirLength
    dirY *= invDirLength
    dirZ *= invDirLength
    // left = up x direction
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * dirZ - upZ * dirY
    leftY = upZ * dirX - upX * dirZ
    leftZ = upX * dirY - upY * dirX
    // normalize left
    val invLeftLength = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = dirY * leftZ - dirZ * leftY
    val upnY = dirZ * leftX - dirX * leftZ
    val upnZ = dirX * leftY - dirY * leftX

    this._m00(leftX)
    this._m01(upnX)
    this._m02(dirX)
    this._m03(0.0f)
    this._m10(leftY)
    this._m11(upnY)
    this._m12(dirY)
    this._m13(0.0f)
    this._m20(leftZ)
    this._m21(upnZ)
    this._m22(dirZ)
    this._m23(0.0f)
    this._m30(-(leftX * eyeX + leftY * eyeY + leftZ * eyeZ))
    this._m31(-(upnX * eyeX + upnY * eyeY + upnZ * eyeZ))
    this._m32(-(dirX * eyeX + dirY * eyeY + dirZ * eyeZ))
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)

    return this
  }

  /**
   * Apply a "lookat" transformation to this matrix for a right-handed coordinate system,
   * that aligns `-z` with `center - eye` and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a lookat transformation without post-multiplying it,
   * use [.setLookAt].
   *
   * @see .lookAt
   * @see .setLookAlong
   * @param eye
   * the position of the camera
   * @param center
   * the point in space to look at
   * @param up
   * the direction of 'up'
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun lookAt(eye: Vector3fc, center: Vector3fc, up: Vector3fc, dest: Matrix4f): Matrix4f {
    return lookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z, dest)
  }

  /**
   * Apply a "lookat" transformation to this matrix for a right-handed coordinate system,
   * that aligns `-z` with `center - eye`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a lookat transformation without post-multiplying it,
   * use [.setLookAt].
   *
   * @see .lookAt
   * @see .setLookAlong
   * @param eye
   * the position of the camera
   * @param center
   * the point in space to look at
   * @param up
   * the direction of 'up'
   * @return a matrix holding the result
   */
  fun lookAt(eye: Vector3fc, center: Vector3fc, up: Vector3fc): Matrix4f {
    return lookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z, thisOrNew())
  }

  /**
   * Apply a "lookat" transformation to this matrix for a right-handed coordinate system,
   * that aligns `-z` with `center - eye` and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a lookat transformation without post-multiplying it,
   * use [setLookAt()][.setLookAt].
   *
   * @see .lookAt
   * @see .setLookAt
   * @param eyeX
   * the x-coordinate of the eye/camera location
   * @param eyeY
   * the y-coordinate of the eye/camera location
   * @param eyeZ
   * the z-coordinate of the eye/camera location
   * @param centerX
   * the x-coordinate of the point to look at
   * @param centerY
   * the y-coordinate of the point to look at
   * @param centerZ
   * the z-coordinate of the point to look at
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
  override fun lookAt(eyeX: Float, eyeY: Float, eyeZ: Float,
                      centerX: Float, centerY: Float, centerZ: Float,
                      upX: Float, upY: Float, upZ: Float, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.setLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    else if (properties and Matrix4fc.PROPERTY_PERSPECTIVE != 0)
      return lookAtPerspective(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ, dest)
    return lookAtGeneric(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ, dest)
  }

  private fun lookAtGeneric(eyeX: Float, eyeY: Float, eyeZ: Float,
                            centerX: Float, centerY: Float, centerZ: Float,
                            upX: Float, upY: Float, upZ: Float, dest: Matrix4f): Matrix4f {
    // Compute direction from position to lookAt
    var dirX: Float
    var dirY: Float
    var dirZ: Float
    dirX = eyeX - centerX
    dirY = eyeY - centerY
    dirZ = eyeZ - centerZ
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    dirX *= invDirLength
    dirY *= invDirLength
    dirZ *= invDirLength
    // left = up x direction
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * dirZ - upZ * dirY
    leftY = upZ * dirX - upX * dirZ
    leftZ = upX * dirY - upY * dirX
    // normalize left
    val invLeftLength = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = dirY * leftZ - dirZ * leftY
    val upnY = dirZ * leftX - dirX * leftZ
    val upnZ = dirX * leftY - dirY * leftX

    // calculate right matrix elements
    val rm30 = -(leftX * eyeX + leftY * eyeY + leftZ * eyeZ)
    val rm31 = -(upnX * eyeX + upnY * eyeY + upnZ * eyeZ)
    val rm32 = -(dirX * eyeX + dirY * eyeY + dirZ * eyeZ)

    // perform optimized matrix multiplication
    // compute last column first, because others do not depend on it
    dest._m30(m00 * rm30 + m10 * rm31 + m20 * rm32 + m30)
    dest._m31(m01 * rm30 + m11 * rm31 + m21 * rm32 + m31)
    dest._m32(m02 * rm30 + m12 * rm31 + m22 * rm32 + m32)
    dest._m33(m03 * rm30 + m13 * rm31 + m23 * rm32 + m33)
    // introduce temporaries for dependent results
    val nm00 = m00 * leftX + m10 * upnX + m20 * dirX
    val nm01 = m01 * leftX + m11 * upnX + m21 * dirX
    val nm02 = m02 * leftX + m12 * upnX + m22 * dirX
    val nm03 = m03 * leftX + m13 * upnX + m23 * dirX
    val nm10 = m00 * leftY + m10 * upnY + m20 * dirY
    val nm11 = m01 * leftY + m11 * upnY + m21 * dirY
    val nm12 = m02 * leftY + m12 * upnY + m22 * dirY
    val nm13 = m03 * leftY + m13 * upnY + m23 * dirY
    dest._m20(m00 * leftZ + m10 * upnZ + m20 * dirZ)
    dest._m21(m01 * leftZ + m11 * upnZ + m21 * dirZ)
    dest._m22(m02 * leftZ + m12 * upnZ + m22 * dirZ)
    dest._m23(m03 * leftZ + m13 * upnZ + m23 * dirZ)
    // set the rest of the matrix elements
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply a "lookat" transformation to this matrix for a right-handed coordinate system,
   * that aligns `-z` with `center - eye` and store the result in `dest`.
   *
   *
   * This method assumes `this` to be a perspective transformation, obtained via
   * [frustum()][.frustum] or [perspective()][.perspective] or
   * one of their overloads.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a lookat transformation without post-multiplying it,
   * use [setLookAt()][.setLookAt].
   *
   * @see .setLookAt
   * @param eyeX
   * the x-coordinate of the eye/camera location
   * @param eyeY
   * the y-coordinate of the eye/camera location
   * @param eyeZ
   * the z-coordinate of the eye/camera location
   * @param centerX
   * the x-coordinate of the point to look at
   * @param centerY
   * the y-coordinate of the point to look at
   * @param centerZ
   * the z-coordinate of the point to look at
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
  override fun lookAtPerspective(eyeX: Float, eyeY: Float, eyeZ: Float,
                                 centerX: Float, centerY: Float, centerZ: Float,
                                 upX: Float, upY: Float, upZ: Float, dest: Matrix4f): Matrix4f {
    // Compute direction from position to lookAt
    var dirX: Float
    var dirY: Float
    var dirZ: Float
    dirX = eyeX - centerX
    dirY = eyeY - centerY
    dirZ = eyeZ - centerZ
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    dirX *= invDirLength
    dirY *= invDirLength
    dirZ *= invDirLength
    // left = up x direction
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * dirZ - upZ * dirY
    leftY = upZ * dirX - upX * dirZ
    leftZ = upX * dirY - upY * dirX
    // normalize left
    val invLeftLength = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = dirY * leftZ - dirZ * leftY
    val upnY = dirZ * leftX - dirX * leftZ
    val upnZ = dirX * leftY - dirY * leftX

    // calculate right matrix elements
    val rm30 = -(leftX * eyeX + leftY * eyeY + leftZ * eyeZ)
    val rm31 = -(upnX * eyeX + upnY * eyeY + upnZ * eyeZ)
    val rm32 = -(dirX * eyeX + dirY * eyeY + dirZ * eyeZ)

    val nm00 = m00 * leftX
    val nm01 = m11 * upnX
    val nm02 = m22 * dirX
    val nm03 = m23 * dirX
    val nm10 = m00 * leftY
    val nm11 = m11 * upnY
    val nm12 = m22 * dirY
    val nm13 = m23 * dirY
    val nm20 = m00 * leftZ
    val nm21 = m11 * upnZ
    val nm22 = m22 * dirZ
    val nm23 = m23 * dirZ
    val nm30 = m00 * rm30
    val nm31 = m11 * rm31
    val nm32 = m22 * rm32 + m32
    val nm33 = m23 * rm32
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(0)

    return dest
  }

  /**
   * Apply a "lookat" transformation to this matrix for a right-handed coordinate system,
   * that aligns `-z` with `center - eye`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a lookat transformation without post-multiplying it,
   * use [setLookAt()][.setLookAt].
   *
   * @see .lookAt
   * @see .setLookAt
   * @param eyeX
   * the x-coordinate of the eye/camera location
   * @param eyeY
   * the y-coordinate of the eye/camera location
   * @param eyeZ
   * the z-coordinate of the eye/camera location
   * @param centerX
   * the x-coordinate of the point to look at
   * @param centerY
   * the y-coordinate of the point to look at
   * @param centerZ
   * the z-coordinate of the point to look at
   * @param upX
   * the x-coordinate of the up vector
   * @param upY
   * the y-coordinate of the up vector
   * @param upZ
   * the z-coordinate of the up vector
   * @return a matrix holding the result
   */
  fun lookAt(eyeX: Float, eyeY: Float, eyeZ: Float,
             centerX: Float, centerY: Float, centerZ: Float,
             upX: Float, upY: Float, upZ: Float): Matrix4f {
    return lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ, thisOrNew())
  }

  /**
   * Set this matrix to be a "lookat" transformation for a left-handed coordinate system, that aligns
   * `+z` with `center - eye`.
   *
   *
   * In order to not make use of vectors to specify `eye`, `center` and `up` but use primitives,
   * like in the GLU function, use [setLookAtLH()][.setLookAtLH]
   * instead.
   *
   *
   * In order to apply the lookat transformation to a previous existing transformation,
   * use [lookAt()][.lookAtLH].
   *
   * @see .setLookAtLH
   * @see .lookAtLH
   * @param eye
   * the position of the camera
   * @param center
   * the point in space to look at
   * @param up
   * the direction of 'up'
   * @return this
   */
  fun setLookAtLH(eye: Vector3fc, center: Vector3fc, up: Vector3fc): Matrix4f {
    return setLookAtLH(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z)
  }

  /**
   * Set this matrix to be a "lookat" transformation for a left-handed coordinate system,
   * that aligns `+z` with `center - eye`.
   *
   *
   * In order to apply the lookat transformation to a previous existing transformation,
   * use [lookAtLH][.lookAtLH].
   *
   * @see .setLookAtLH
   * @see .lookAtLH
   * @param eyeX
   * the x-coordinate of the eye/camera location
   * @param eyeY
   * the y-coordinate of the eye/camera location
   * @param eyeZ
   * the z-coordinate of the eye/camera location
   * @param centerX
   * the x-coordinate of the point to look at
   * @param centerY
   * the y-coordinate of the point to look at
   * @param centerZ
   * the z-coordinate of the point to look at
   * @param upX
   * the x-coordinate of the up vector
   * @param upY
   * the y-coordinate of the up vector
   * @param upZ
   * the z-coordinate of the up vector
   * @return this
   */
  fun setLookAtLH(eyeX: Float, eyeY: Float, eyeZ: Float,
                  centerX: Float, centerY: Float, centerZ: Float,
                  upX: Float, upY: Float, upZ: Float): Matrix4f {
    // Compute direction from position to lookAt
    var dirX: Float
    var dirY: Float
    var dirZ: Float
    dirX = centerX - eyeX
    dirY = centerY - eyeY
    dirZ = centerZ - eyeZ
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    dirX *= invDirLength
    dirY *= invDirLength
    dirZ *= invDirLength
    // left = up x direction
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * dirZ - upZ * dirY
    leftY = upZ * dirX - upX * dirZ
    leftZ = upX * dirY - upY * dirX
    // normalize left
    val invLeftLength = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = dirY * leftZ - dirZ * leftY
    val upnY = dirZ * leftX - dirX * leftZ
    val upnZ = dirX * leftY - dirY * leftX

    this._m00(leftX)
    this._m01(upnX)
    this._m02(dirX)
    this._m03(0.0f)
    this._m10(leftY)
    this._m11(upnY)
    this._m12(dirY)
    this._m13(0.0f)
    this._m20(leftZ)
    this._m21(upnZ)
    this._m22(dirZ)
    this._m23(0.0f)
    this._m30(-(leftX * eyeX + leftY * eyeY + leftZ * eyeZ))
    this._m31(-(upnX * eyeX + upnY * eyeY + upnZ * eyeZ))
    this._m32(-(dirX * eyeX + dirY * eyeY + dirZ * eyeZ))
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)

    return this
  }

  /**
   * Apply a "lookat" transformation to this matrix for a left-handed coordinate system,
   * that aligns `+z` with `center - eye` and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a lookat transformation without post-multiplying it,
   * use [.setLookAtLH].
   *
   * @see .lookAtLH
   * @param eye
   * the position of the camera
   * @param center
   * the point in space to look at
   * @param up
   * the direction of 'up'
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun lookAtLH(eye: Vector3fc, center: Vector3fc, up: Vector3fc, dest: Matrix4f): Matrix4f {
    return lookAtLH(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z, dest)
  }

  /**
   * Apply a "lookat" transformation to this matrix for a left-handed coordinate system,
   * that aligns `+z` with `center - eye`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a lookat transformation without post-multiplying it,
   * use [.setLookAtLH].
   *
   * @see .lookAtLH
   * @param eye
   * the position of the camera
   * @param center
   * the point in space to look at
   * @param up
   * the direction of 'up'
   * @return a matrix holding the result
   */
  fun lookAtLH(eye: Vector3fc, center: Vector3fc, up: Vector3fc): Matrix4f {
    return lookAtLH(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z, thisOrNew())
  }

  /**
   * Apply a "lookat" transformation to this matrix for a left-handed coordinate system,
   * that aligns `+z` with `center - eye` and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a lookat transformation without post-multiplying it,
   * use [setLookAtLH()][.setLookAtLH].
   *
   * @see .lookAtLH
   * @see .setLookAtLH
   * @param eyeX
   * the x-coordinate of the eye/camera location
   * @param eyeY
   * the y-coordinate of the eye/camera location
   * @param eyeZ
   * the z-coordinate of the eye/camera location
   * @param centerX
   * the x-coordinate of the point to look at
   * @param centerY
   * the y-coordinate of the point to look at
   * @param centerZ
   * the z-coordinate of the point to look at
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
  override fun lookAtLH(eyeX: Float, eyeY: Float, eyeZ: Float,
                        centerX: Float, centerY: Float, centerZ: Float,
                        upX: Float, upY: Float, upZ: Float, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.setLookAtLH(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    else if (properties and Matrix4fc.PROPERTY_PERSPECTIVE != 0)
      return lookAtPerspectiveLH(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ, dest)
    return lookAtLHGeneric(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ, dest)
  }

  private fun lookAtLHGeneric(eyeX: Float, eyeY: Float, eyeZ: Float,
                              centerX: Float, centerY: Float, centerZ: Float,
                              upX: Float, upY: Float, upZ: Float, dest: Matrix4f): Matrix4f {
    // Compute direction from position to lookAt
    var dirX: Float
    var dirY: Float
    var dirZ: Float
    dirX = centerX - eyeX
    dirY = centerY - eyeY
    dirZ = centerZ - eyeZ
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    dirX *= invDirLength
    dirY *= invDirLength
    dirZ *= invDirLength
    // left = up x direction
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * dirZ - upZ * dirY
    leftY = upZ * dirX - upX * dirZ
    leftZ = upX * dirY - upY * dirX
    // normalize left
    val invLeftLength = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = dirY * leftZ - dirZ * leftY
    val upnY = dirZ * leftX - dirX * leftZ
    val upnZ = dirX * leftY - dirY * leftX

    // calculate right matrix elements
    val rm30 = -(leftX * eyeX + leftY * eyeY + leftZ * eyeZ)
    val rm31 = -(upnX * eyeX + upnY * eyeY + upnZ * eyeZ)
    val rm32 = -(dirX * eyeX + dirY * eyeY + dirZ * eyeZ)

    // perform optimized matrix multiplication
    // compute last column first, because others do not depend on it
    dest._m30(m00 * rm30 + m10 * rm31 + m20 * rm32 + m30)
    dest._m31(m01 * rm30 + m11 * rm31 + m21 * rm32 + m31)
    dest._m32(m02 * rm30 + m12 * rm31 + m22 * rm32 + m32)
    dest._m33(m03 * rm30 + m13 * rm31 + m23 * rm32 + m33)
    // introduce temporaries for dependent results
    val nm00 = m00 * leftX + m10 * upnX + m20 * dirX
    val nm01 = m01 * leftX + m11 * upnX + m21 * dirX
    val nm02 = m02 * leftX + m12 * upnX + m22 * dirX
    val nm03 = m03 * leftX + m13 * upnX + m23 * dirX
    val nm10 = m00 * leftY + m10 * upnY + m20 * dirY
    val nm11 = m01 * leftY + m11 * upnY + m21 * dirY
    val nm12 = m02 * leftY + m12 * upnY + m22 * dirY
    val nm13 = m03 * leftY + m13 * upnY + m23 * dirY
    dest._m20(m00 * leftZ + m10 * upnZ + m20 * dirZ)
    dest._m21(m01 * leftZ + m11 * upnZ + m21 * dirZ)
    dest._m22(m02 * leftZ + m12 * upnZ + m22 * dirZ)
    dest._m23(m03 * leftZ + m13 * upnZ + m23 * dirZ)
    // set the rest of the matrix elements
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())

    return dest
  }

  /**
   * Apply a "lookat" transformation to this matrix for a left-handed coordinate system,
   * that aligns `+z` with `center - eye`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a lookat transformation without post-multiplying it,
   * use [setLookAtLH()][.setLookAtLH].
   *
   * @see .lookAtLH
   * @see .setLookAtLH
   * @param eyeX
   * the x-coordinate of the eye/camera location
   * @param eyeY
   * the y-coordinate of the eye/camera location
   * @param eyeZ
   * the z-coordinate of the eye/camera location
   * @param centerX
   * the x-coordinate of the point to look at
   * @param centerY
   * the y-coordinate of the point to look at
   * @param centerZ
   * the z-coordinate of the point to look at
   * @param upX
   * the x-coordinate of the up vector
   * @param upY
   * the y-coordinate of the up vector
   * @param upZ
   * the z-coordinate of the up vector
   * @return a matrix holding the result
   */
  fun lookAtLH(eyeX: Float, eyeY: Float, eyeZ: Float,
               centerX: Float, centerY: Float, centerZ: Float,
               upX: Float, upY: Float, upZ: Float): Matrix4f {
    return lookAtLH(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ, thisOrNew())
  }

  /**
   * Apply a "lookat" transformation to this matrix for a left-handed coordinate system,
   * that aligns `+z` with `center - eye` and store the result in `dest`.
   *
   *
   * This method assumes `this` to be a perspective transformation, obtained via
   * [frustumLH()][.frustumLH] or [perspectiveLH()][.perspectiveLH] or
   * one of their overloads.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a lookat transformation without post-multiplying it,
   * use [setLookAtLH()][.setLookAtLH].
   *
   * @see .setLookAtLH
   * @param eyeX
   * the x-coordinate of the eye/camera location
   * @param eyeY
   * the y-coordinate of the eye/camera location
   * @param eyeZ
   * the z-coordinate of the eye/camera location
   * @param centerX
   * the x-coordinate of the point to look at
   * @param centerY
   * the y-coordinate of the point to look at
   * @param centerZ
   * the z-coordinate of the point to look at
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
  override fun lookAtPerspectiveLH(eyeX: Float, eyeY: Float, eyeZ: Float,
                                   centerX: Float, centerY: Float, centerZ: Float,
                                   upX: Float, upY: Float, upZ: Float, dest: Matrix4f): Matrix4f {
    // Compute direction from position to lookAt
    var dirX: Float
    var dirY: Float
    var dirZ: Float
    dirX = centerX - eyeX
    dirY = centerY - eyeY
    dirZ = centerZ - eyeZ
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    dirX *= invDirLength
    dirY *= invDirLength
    dirZ *= invDirLength
    // left = up x direction
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * dirZ - upZ * dirY
    leftY = upZ * dirX - upX * dirZ
    leftZ = upX * dirY - upY * dirX
    // normalize left
    val invLeftLength = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = dirY * leftZ - dirZ * leftY
    val upnY = dirZ * leftX - dirX * leftZ
    val upnZ = dirX * leftY - dirY * leftX

    // calculate right matrix elements
    val rm30 = -(leftX * eyeX + leftY * eyeY + leftZ * eyeZ)
    val rm31 = -(upnX * eyeX + upnY * eyeY + upnZ * eyeZ)
    val rm32 = -(dirX * eyeX + dirY * eyeY + dirZ * eyeZ)

    val nm00 = m00 * leftX
    val nm01 = m11 * upnX
    val nm02 = m22 * dirX
    val nm03 = m23 * dirX
    val nm10 = m00 * leftY
    val nm11 = m11 * upnY
    val nm12 = m22 * dirY
    val nm13 = m23 * dirY
    val nm20 = m00 * leftZ
    val nm21 = m11 * upnZ
    val nm22 = m22 * dirZ
    val nm23 = m23 * dirZ
    val nm30 = m00 * rm30
    val nm31 = m11 * rm31
    val nm32 = m22 * rm32 + m32
    val nm33 = m23 * rm32
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(0)

    return dest
  }

  /**
   * Apply a symmetric perspective projection frustum transformation for a right-handed coordinate system
   * using the given NDC z range to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `P` the perspective projection matrix,
   * then the new matrix will be `M * P`. So when transforming a
   * vector `v` with the new matrix by using `M * P * v`,
   * the perspective projection will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setPerspective][.setPerspective].
   *
   * @see .setPerspective
   * @param fovy
   * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
   * @param aspect
   * the aspect ratio (i.e. width / height; must be greater than zero)
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param dest
   * will hold the result
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return dest
   */
  override fun perspective(fovy: Float, aspect: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.setPerspective(fovy, aspect, zNear, zFar, zZeroToOne) else perspectiveGeneric(fovy, aspect, zNear, zFar, zZeroToOne, dest)
  }

  private fun perspectiveGeneric(fovy: Float, aspect: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    val h = Math.tan((fovy * 0.5f).toDouble()).toFloat()
    // calculate right matrix elements
    val rm00 = 1.0f / (h * aspect)
    val rm11 = 1.0f / h
    val rm22: Float
    val rm32: Float
    val farInf = zFar > 0 && java.lang.Float.isInfinite(zFar)
    val nearInf = zNear > 0 && java.lang.Float.isInfinite(zNear)
    if (farInf) {
      // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
      val e = 1E-6f
      rm22 = e - 1.0f
      rm32 = (e - if (zZeroToOne) 1.0f else 2.0f) * zNear
    } else if (nearInf) {
      val e = 1E-6f
      rm22 = (if (zZeroToOne) 0.0f else 1.0f) - e
      rm32 = ((if (zZeroToOne) 1.0f else 2.0f) - e) * zFar
    } else {
      rm22 = (if (zZeroToOne) zFar else zFar + zNear) / (zNear - zFar)
      rm32 = (if (zZeroToOne) zFar else zFar + zFar) * zNear / (zNear - zFar)
    }
    // perform optimized matrix multiplication
    val nm20 = m20 * rm22 - m30
    val nm21 = m21 * rm22 - m31
    val nm22 = m22 * rm22 - m32
    val nm23 = m23 * rm22 - m33
    dest._m00(m00 * rm00)
    dest._m01(m01 * rm00)
    dest._m02(m02 * rm00)
    dest._m03(m03 * rm00)
    dest._m10(m10 * rm11)
    dest._m11(m11 * rm11)
    dest._m12(m12 * rm11)
    dest._m13(m13 * rm11)
    dest._m30(m20 * rm32)
    dest._m31(m21 * rm32)
    dest._m32(m22 * rm32)
    dest._m33(m23 * rm32)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._properties(properties and (Matrix4fc.PROPERTY_AFFINE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())
    return dest
  }

  /**
   * Apply a symmetric perspective projection frustum transformation for a right-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `P` the perspective projection matrix,
   * then the new matrix will be `M * P`. So when transforming a
   * vector `v` with the new matrix by using `M * P * v`,
   * the perspective projection will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setPerspective][.setPerspective].
   *
   * @see .setPerspective
   * @param fovy
   * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
   * @param aspect
   * the aspect ratio (i.e. width / height; must be greater than zero)
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun perspective(fovy: Float, aspect: Float, zNear: Float, zFar: Float, dest: Matrix4f): Matrix4f {
    return perspective(fovy, aspect, zNear, zFar, false, dest)
  }

  /**
   * Apply a symmetric perspective projection frustum transformation using for a right-handed coordinate system
   * the given NDC z range to this matrix.
   *
   *
   * If `M` is `this` matrix and `P` the perspective projection matrix,
   * then the new matrix will be `M * P`. So when transforming a
   * vector `v` with the new matrix by using `M * P * v`,
   * the perspective projection will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setPerspective][.setPerspective].
   *
   * @see .setPerspective
   * @param fovy
   * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
   * @param aspect
   * the aspect ratio (i.e. width / height; must be greater than zero)
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return a matrix holding the result
   */
  fun perspective(fovy: Float, aspect: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean): Matrix4f {
    return perspective(fovy, aspect, zNear, zFar, zZeroToOne, thisOrNew())
  }

  /**
   * Apply a symmetric perspective projection frustum transformation for a right-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix.
   *
   *
   * If `M` is `this` matrix and `P` the perspective projection matrix,
   * then the new matrix will be `M * P`. So when transforming a
   * vector `v` with the new matrix by using `M * P * v`,
   * the perspective projection will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setPerspective][.setPerspective].
   *
   * @see .setPerspective
   * @param fovy
   * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
   * @param aspect
   * the aspect ratio (i.e. width / height; must be greater than zero)
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @return a matrix holding the result
   */
  fun perspective(fovy: Float, aspect: Float, zNear: Float, zFar: Float): Matrix4f {
    return perspective(fovy, aspect, zNear, zFar, thisOrNew())
  }

  /**
   * Set this matrix to be a symmetric perspective projection frustum transformation for a right-handed coordinate system
   * using the given NDC z range.
   *
   *
   * In order to apply the perspective projection transformation to an existing transformation,
   * use [perspective()][.perspective].
   *
   * @see .perspective
   * @param fovy
   * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
   * @param aspect
   * the aspect ratio (i.e. width / height; must be greater than zero)
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return this
   */
  @JvmOverloads
  fun setPerspective(fovy: Float, aspect: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean = false): Matrix4f {
    MemUtil.INSTANCE.zero(this)
    val h = Math.tan((fovy * 0.5f).toDouble()).toFloat()
    this._m00(1.0f / (h * aspect))
    this._m11(1.0f / h)
    val farInf = zFar > 0 && java.lang.Float.isInfinite(zFar)
    val nearInf = zNear > 0 && java.lang.Float.isInfinite(zNear)
    if (farInf) {
      // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
      val e = 1E-6f
      this._m22(e - 1.0f)
      this._m32((e - if (zZeroToOne) 1.0f else 2.0f) * zNear)
    } else if (nearInf) {
      val e = 1E-6f
      this._m22((if (zZeroToOne) 0.0f else 1.0f) - e)
      this._m32(((if (zZeroToOne) 1.0f else 2.0f) - e) * zFar)
    } else {
      this._m22((if (zZeroToOne) zFar else zFar + zNear) / (zNear - zFar))
      this._m32((if (zZeroToOne) zFar else zFar + zFar) * zNear / (zNear - zFar))
    }
    this._m23(-1.0f)
    _properties(Matrix4fc.PROPERTY_PERSPECTIVE.toInt())
    return this
  }

  /**
   * Apply a symmetric perspective projection frustum transformation for a left-handed coordinate system
   * using the given NDC z range to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `P` the perspective projection matrix,
   * then the new matrix will be `M * P`. So when transforming a
   * vector `v` with the new matrix by using `M * P * v`,
   * the perspective projection will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setPerspectiveLH][.setPerspectiveLH].
   *
   * @see .setPerspectiveLH
   * @param fovy
   * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
   * @param aspect
   * the aspect ratio (i.e. width / height; must be greater than zero)
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun perspectiveLH(fovy: Float, aspect: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.setPerspectiveLH(fovy, aspect, zNear, zFar, zZeroToOne) else perspectiveLHGeneric(fovy, aspect, zNear, zFar, zZeroToOne, dest)
  }

  private fun perspectiveLHGeneric(fovy: Float, aspect: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    val h = Math.tan((fovy * 0.5f).toDouble()).toFloat()
    // calculate right matrix elements
    val rm00 = 1.0f / (h * aspect)
    val rm11 = 1.0f / h
    val rm22: Float
    val rm32: Float
    val farInf = zFar > 0 && java.lang.Float.isInfinite(zFar)
    val nearInf = zNear > 0 && java.lang.Float.isInfinite(zNear)
    if (farInf) {
      // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
      val e = 1E-6f
      rm22 = 1.0f - e
      rm32 = (e - if (zZeroToOne) 1.0f else 2.0f) * zNear
    } else if (nearInf) {
      val e = 1E-6f
      rm22 = (if (zZeroToOne) 0.0f else 1.0f) - e
      rm32 = ((if (zZeroToOne) 1.0f else 2.0f) - e) * zFar
    } else {
      rm22 = (if (zZeroToOne) zFar else zFar + zNear) / (zFar - zNear)
      rm32 = (if (zZeroToOne) zFar else zFar + zFar) * zNear / (zNear - zFar)
    }
    // perform optimized matrix multiplication
    val nm20 = m20 * rm22 + m30
    val nm21 = m21 * rm22 + m31
    val nm22 = m22 * rm22 + m32
    val nm23 = m23 * rm22 + m33
    dest._m00(m00 * rm00)
    dest._m01(m01 * rm00)
    dest._m02(m02 * rm00)
    dest._m03(m03 * rm00)
    dest._m10(m10 * rm11)
    dest._m11(m11 * rm11)
    dest._m12(m12 * rm11)
    dest._m13(m13 * rm11)
    dest._m30(m20 * rm32)
    dest._m31(m21 * rm32)
    dest._m32(m22 * rm32)
    dest._m33(m23 * rm32)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._properties(properties and (Matrix4fc.PROPERTY_AFFINE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())
    return dest
  }

  /**
   * Apply a symmetric perspective projection frustum transformation for a left-handed coordinate system
   * using the given NDC z range to this matrix.
   *
   *
   * If `M` is `this` matrix and `P` the perspective projection matrix,
   * then the new matrix will be `M * P`. So when transforming a
   * vector `v` with the new matrix by using `M * P * v`,
   * the perspective projection will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setPerspectiveLH][.setPerspectiveLH].
   *
   * @see .setPerspectiveLH
   * @param fovy
   * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
   * @param aspect
   * the aspect ratio (i.e. width / height; must be greater than zero)
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return a matrix holding the result
   */
  fun perspectiveLH(fovy: Float, aspect: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean): Matrix4f {
    return perspectiveLH(fovy, aspect, zNear, zFar, zZeroToOne, thisOrNew())
  }

  /**
   * Apply a symmetric perspective projection frustum transformation for a left-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `P` the perspective projection matrix,
   * then the new matrix will be `M * P`. So when transforming a
   * vector `v` with the new matrix by using `M * P * v`,
   * the perspective projection will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setPerspectiveLH][.setPerspectiveLH].
   *
   * @see .setPerspectiveLH
   * @param fovy
   * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
   * @param aspect
   * the aspect ratio (i.e. width / height; must be greater than zero)
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun perspectiveLH(fovy: Float, aspect: Float, zNear: Float, zFar: Float, dest: Matrix4f): Matrix4f {
    return perspectiveLH(fovy, aspect, zNear, zFar, false, dest)
  }

  /**
   * Apply a symmetric perspective projection frustum transformation for a left-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix.
   *
   *
   * If `M` is `this` matrix and `P` the perspective projection matrix,
   * then the new matrix will be `M * P`. So when transforming a
   * vector `v` with the new matrix by using `M * P * v`,
   * the perspective projection will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setPerspectiveLH][.setPerspectiveLH].
   *
   * @see .setPerspectiveLH
   * @param fovy
   * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
   * @param aspect
   * the aspect ratio (i.e. width / height; must be greater than zero)
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @return a matrix holding the result
   */
  fun perspectiveLH(fovy: Float, aspect: Float, zNear: Float, zFar: Float): Matrix4f {
    return perspectiveLH(fovy, aspect, zNear, zFar, thisOrNew())
  }

  /**
   * Set this matrix to be a symmetric perspective projection frustum transformation for a left-handed coordinate system
   * using the given NDC z range of <tt>[-1..+1]</tt>.
   *
   *
   * In order to apply the perspective projection transformation to an existing transformation,
   * use [perspectiveLH()][.perspectiveLH].
   *
   * @see .perspectiveLH
   * @param fovy
   * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
   * @param aspect
   * the aspect ratio (i.e. width / height; must be greater than zero)
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return this
   */
  @JvmOverloads
  fun setPerspectiveLH(fovy: Float, aspect: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean = false): Matrix4f {
    MemUtil.INSTANCE.zero(this)
    val h = Math.tan((fovy * 0.5f).toDouble()).toFloat()
    this._m00(1.0f / (h * aspect))
    this._m11(1.0f / h)
    val farInf = zFar > 0 && java.lang.Float.isInfinite(zFar)
    val nearInf = zNear > 0 && java.lang.Float.isInfinite(zNear)
    if (farInf) {
      // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
      val e = 1E-6f
      this._m22(1.0f - e)
      this._m32((e - if (zZeroToOne) 1.0f else 2.0f) * zNear)
    } else if (nearInf) {
      val e = 1E-6f
      this._m22((if (zZeroToOne) 0.0f else 1.0f) - e)
      this._m32(((if (zZeroToOne) 1.0f else 2.0f) - e) * zFar)
    } else {
      this._m22((if (zZeroToOne) zFar else zFar + zNear) / (zFar - zNear))
      this._m32((if (zZeroToOne) zFar else zFar + zFar) * zNear / (zNear - zFar))
    }
    this._m23(1.0f)
    _properties(Matrix4fc.PROPERTY_PERSPECTIVE.toInt())
    return this
  }

  /**
   * Apply an arbitrary perspective projection frustum transformation for a right-handed coordinate system
   * using the given NDC z range to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `F` the frustum matrix,
   * then the new matrix will be `M * F`. So when transforming a
   * vector `v` with the new matrix by using `M * F * v`,
   * the frustum transformation will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setFrustum()][.setFrustum].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
   *
   * @see .setFrustum
   * @param left
   * the distance along the x-axis to the left frustum edge
   * @param right
   * the distance along the x-axis to the right frustum edge
   * @param bottom
   * the distance along the y-axis to the bottom frustum edge
   * @param top
   * the distance along the y-axis to the top frustum edge
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun frustum(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.setFrustum(left, right, bottom, top, zNear, zFar, zZeroToOne) else frustumGeneric(left, right, bottom, top, zNear, zFar, zZeroToOne, dest)
  }

  private fun frustumGeneric(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    // calculate right matrix elements
    val rm00 = (zNear + zNear) / (right - left)
    val rm11 = (zNear + zNear) / (top - bottom)
    val rm20 = (right + left) / (right - left)
    val rm21 = (top + bottom) / (top - bottom)
    val rm22: Float
    val rm32: Float
    val farInf = zFar > 0 && java.lang.Float.isInfinite(zFar)
    val nearInf = zNear > 0 && java.lang.Float.isInfinite(zNear)
    if (farInf) {
      // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
      val e = 1E-6f
      rm22 = e - 1.0f
      rm32 = (e - if (zZeroToOne) 1.0f else 2.0f) * zNear
    } else if (nearInf) {
      val e = 1E-6f
      rm22 = (if (zZeroToOne) 0.0f else 1.0f) - e
      rm32 = ((if (zZeroToOne) 1.0f else 2.0f) - e) * zFar
    } else {
      rm22 = (if (zZeroToOne) zFar else zFar + zNear) / (zNear - zFar)
      rm32 = (if (zZeroToOne) zFar else zFar + zFar) * zNear / (zNear - zFar)
    }
    // perform optimized matrix multiplication
    val nm20 = m00 * rm20 + m10 * rm21 + m20 * rm22 - m30
    val nm21 = m01 * rm20 + m11 * rm21 + m21 * rm22 - m31
    val nm22 = m02 * rm20 + m12 * rm21 + m22 * rm22 - m32
    val nm23 = m03 * rm20 + m13 * rm21 + m23 * rm22 - m33
    dest._m00(m00 * rm00)
    dest._m01(m01 * rm00)
    dest._m02(m02 * rm00)
    dest._m03(m03 * rm00)
    dest._m10(m10 * rm11)
    dest._m11(m11 * rm11)
    dest._m12(m12 * rm11)
    dest._m13(m13 * rm11)
    dest._m30(m20 * rm32)
    dest._m31(m21 * rm32)
    dest._m32(m22 * rm32)
    dest._m33(m23 * rm32)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(0)
    return dest
  }

  /**
   * Apply an arbitrary perspective projection frustum transformation for a right-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `F` the frustum matrix,
   * then the new matrix will be `M * F`. So when transforming a
   * vector `v` with the new matrix by using `M * F * v`,
   * the frustum transformation will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setFrustum()][.setFrustum].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
   *
   * @see .setFrustum
   * @param left
   * the distance along the x-axis to the left frustum edge
   * @param right
   * the distance along the x-axis to the right frustum edge
   * @param bottom
   * the distance along the y-axis to the bottom frustum edge
   * @param top
   * the distance along the y-axis to the top frustum edge
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun frustum(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, dest: Matrix4f): Matrix4f {
    return frustum(left, right, bottom, top, zNear, zFar, false, dest)
  }

  /**
   * Apply an arbitrary perspective projection frustum transformation for a right-handed coordinate system
   * using the given NDC z range to this matrix.
   *
   *
   * If `M` is `this` matrix and `F` the frustum matrix,
   * then the new matrix will be `M * F`. So when transforming a
   * vector `v` with the new matrix by using `M * F * v`,
   * the frustum transformation will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setFrustum()][.setFrustum].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
   *
   * @see .setFrustum
   * @param left
   * the distance along the x-axis to the left frustum edge
   * @param right
   * the distance along the x-axis to the right frustum edge
   * @param bottom
   * the distance along the y-axis to the bottom frustum edge
   * @param top
   * the distance along the y-axis to the top frustum edge
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return a matrix holding the result
   */
  fun frustum(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean): Matrix4f {
    return frustum(left, right, bottom, top, zNear, zFar, zZeroToOne, thisOrNew())
  }

  /**
   * Apply an arbitrary perspective projection frustum transformation for a right-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix.
   *
   *
   * If `M` is `this` matrix and `F` the frustum matrix,
   * then the new matrix will be `M * F`. So when transforming a
   * vector `v` with the new matrix by using `M * F * v`,
   * the frustum transformation will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setFrustum()][.setFrustum].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
   *
   * @see .setFrustum
   * @param left
   * the distance along the x-axis to the left frustum edge
   * @param right
   * the distance along the x-axis to the right frustum edge
   * @param bottom
   * the distance along the y-axis to the bottom frustum edge
   * @param top
   * the distance along the y-axis to the top frustum edge
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @return a matrix holding the result
   */
  fun frustum(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float): Matrix4f {
    return frustum(left, right, bottom, top, zNear, zFar, thisOrNew())
  }

  /**
   * Set this matrix to be an arbitrary perspective projection frustum transformation for a right-handed coordinate system
   * using the given NDC z range.
   *
   *
   * In order to apply the perspective frustum transformation to an existing transformation,
   * use [frustum()][.frustum].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
   *
   * @see .frustum
   * @param left
   * the distance along the x-axis to the left frustum edge
   * @param right
   * the distance along the x-axis to the right frustum edge
   * @param bottom
   * the distance along the y-axis to the bottom frustum edge
   * @param top
   * the distance along the y-axis to the top frustum edge
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return this
   */
  @JvmOverloads
  fun setFrustum(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean = false): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00((zNear + zNear) / (right - left))
    this._m11((zNear + zNear) / (top - bottom))
    this._m20((right + left) / (right - left))
    this._m21((top + bottom) / (top - bottom))
    val farInf = zFar > 0 && java.lang.Float.isInfinite(zFar)
    val nearInf = zNear > 0 && java.lang.Float.isInfinite(zNear)
    if (farInf) {
      // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
      val e = 1E-6f
      this._m22(e - 1.0f)
      this._m32((e - if (zZeroToOne) 1.0f else 2.0f) * zNear)
    } else if (nearInf) {
      val e = 1E-6f
      this._m22((if (zZeroToOne) 0.0f else 1.0f) - e)
      this._m32(((if (zZeroToOne) 1.0f else 2.0f) - e) * zFar)
    } else {
      this._m22((if (zZeroToOne) zFar else zFar + zNear) / (zNear - zFar))
      this._m32((if (zZeroToOne) zFar else zFar + zFar) * zNear / (zNear - zFar))
    }
    this._m23(-1.0f)
    this._m33(0.0f)
    _properties((if (this.m20 == 0.0f && this.m21 == 0.0f) Matrix4fc.PROPERTY_PERSPECTIVE else 0).toInt())
    return this
  }

  /**
   * Apply an arbitrary perspective projection frustum transformation for a left-handed coordinate system
   * using the given NDC z range to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `F` the frustum matrix,
   * then the new matrix will be `M * F`. So when transforming a
   * vector `v` with the new matrix by using `M * F * v`,
   * the frustum transformation will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setFrustumLH()][.setFrustumLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
   *
   * @see .setFrustumLH
   * @param left
   * the distance along the x-axis to the left frustum edge
   * @param right
   * the distance along the x-axis to the right frustum edge
   * @param bottom
   * the distance along the y-axis to the bottom frustum edge
   * @param top
   * the distance along the y-axis to the top frustum edge
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun frustumLH(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0) dest.setFrustumLH(left, right, bottom, top, zNear, zFar, zZeroToOne) else frustumLHGeneric(left, right, bottom, top, zNear, zFar, zZeroToOne, dest)
  }

  private fun frustumLHGeneric(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean, dest: Matrix4f): Matrix4f {
    // calculate right matrix elements
    val rm00 = (zNear + zNear) / (right - left)
    val rm11 = (zNear + zNear) / (top - bottom)
    val rm20 = (right + left) / (right - left)
    val rm21 = (top + bottom) / (top - bottom)
    val rm22: Float
    val rm32: Float
    val farInf = zFar > 0 && java.lang.Float.isInfinite(zFar)
    val nearInf = zNear > 0 && java.lang.Float.isInfinite(zNear)
    if (farInf) {
      // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
      val e = 1E-6f
      rm22 = 1.0f - e
      rm32 = (e - if (zZeroToOne) 1.0f else 2.0f) * zNear
    } else if (nearInf) {
      val e = 1E-6f
      rm22 = (if (zZeroToOne) 0.0f else 1.0f) - e
      rm32 = ((if (zZeroToOne) 1.0f else 2.0f) - e) * zFar
    } else {
      rm22 = (if (zZeroToOne) zFar else zFar + zNear) / (zFar - zNear)
      rm32 = (if (zZeroToOne) zFar else zFar + zFar) * zNear / (zNear - zFar)
    }
    // perform optimized matrix multiplication
    val nm20 = m00 * rm20 + m10 * rm21 + m20 * rm22 + m30
    val nm21 = m01 * rm20 + m11 * rm21 + m21 * rm22 + m31
    val nm22 = m02 * rm20 + m12 * rm21 + m22 * rm22 + m32
    val nm23 = m03 * rm20 + m13 * rm21 + m23 * rm22 + m33
    dest._m00(m00 * rm00)
    dest._m01(m01 * rm00)
    dest._m02(m02 * rm00)
    dest._m03(m03 * rm00)
    dest._m10(m10 * rm11)
    dest._m11(m11 * rm11)
    dest._m12(m12 * rm11)
    dest._m13(m13 * rm11)
    dest._m30(m20 * rm32)
    dest._m31(m21 * rm32)
    dest._m32(m22 * rm32)
    dest._m33(m23 * rm32)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(0)
    return dest
  }

  /**
   * Apply an arbitrary perspective projection frustum transformation for a left-handed coordinate system
   * using the given NDC z range to this matrix.
   *
   *
   * If `M` is `this` matrix and `F` the frustum matrix,
   * then the new matrix will be `M * F`. So when transforming a
   * vector `v` with the new matrix by using `M * F * v`,
   * the frustum transformation will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setFrustumLH()][.setFrustumLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
   *
   * @see .setFrustumLH
   * @param left
   * the distance along the x-axis to the left frustum edge
   * @param right
   * the distance along the x-axis to the right frustum edge
   * @param bottom
   * the distance along the y-axis to the bottom frustum edge
   * @param top
   * the distance along the y-axis to the top frustum edge
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return a matrix holding the result
   */
  fun frustumLH(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean): Matrix4f {
    return frustumLH(left, right, bottom, top, zNear, zFar, zZeroToOne, thisOrNew())
  }

  /**
   * Apply an arbitrary perspective projection frustum transformation for a left-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `F` the frustum matrix,
   * then the new matrix will be `M * F`. So when transforming a
   * vector `v` with the new matrix by using `M * F * v`,
   * the frustum transformation will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setFrustumLH()][.setFrustumLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
   *
   * @see .setFrustumLH
   * @param left
   * the distance along the x-axis to the left frustum edge
   * @param right
   * the distance along the x-axis to the right frustum edge
   * @param bottom
   * the distance along the y-axis to the bottom frustum edge
   * @param top
   * the distance along the y-axis to the top frustum edge
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun frustumLH(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, dest: Matrix4f): Matrix4f {
    return frustumLH(left, right, bottom, top, zNear, zFar, false, dest)
  }

  /**
   * Apply an arbitrary perspective projection frustum transformation for a left-handed coordinate system
   * using the given NDC z range to this matrix.
   *
   *
   * If `M` is `this` matrix and `F` the frustum matrix,
   * then the new matrix will be `M * F`. So when transforming a
   * vector `v` with the new matrix by using `M * F * v`,
   * the frustum transformation will be applied first!
   *
   *
   * In order to set the matrix to a perspective frustum transformation without post-multiplying,
   * use [setFrustumLH()][.setFrustumLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
   *
   * @see .setFrustumLH
   * @param left
   * the distance along the x-axis to the left frustum edge
   * @param right
   * the distance along the x-axis to the right frustum edge
   * @param bottom
   * the distance along the y-axis to the bottom frustum edge
   * @param top
   * the distance along the y-axis to the top frustum edge
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @return a matrix holding the result
   */
  fun frustumLH(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float): Matrix4f {
    return frustumLH(left, right, bottom, top, zNear, zFar, thisOrNew())
  }

  /**
   * Set this matrix to be an arbitrary perspective projection frustum transformation for a left-handed coordinate system
   * using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
   *
   *
   * In order to apply the perspective frustum transformation to an existing transformation,
   * use [frustumLH()][.frustumLH].
   *
   *
   * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
   *
   * @see .frustumLH
   * @param left
   * the distance along the x-axis to the left frustum edge
   * @param right
   * the distance along the x-axis to the right frustum edge
   * @param bottom
   * the distance along the y-axis to the bottom frustum edge
   * @param top
   * the distance along the y-axis to the top frustum edge
   * @param zNear
   * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
   * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
   * @param zFar
   * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
   * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
   * @param zZeroToOne
   * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
   * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
   * @return this
   */
  @JvmOverloads
  fun setFrustumLH(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float, zZeroToOne: Boolean = false): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
      MemUtil.INSTANCE.identity(this)
    this._m00((zNear + zNear) / (right - left))
    this._m11((zNear + zNear) / (top - bottom))
    this._m20((right + left) / (right - left))
    this._m21((top + bottom) / (top - bottom))
    val farInf = zFar > 0 && java.lang.Float.isInfinite(zFar)
    val nearInf = zNear > 0 && java.lang.Float.isInfinite(zNear)
    if (farInf) {
      // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
      val e = 1E-6f
      this._m22(1.0f - e)
      this._m32((e - if (zZeroToOne) 1.0f else 2.0f) * zNear)
    } else if (nearInf) {
      val e = 1E-6f
      this._m22((if (zZeroToOne) 0.0f else 1.0f) - e)
      this._m32(((if (zZeroToOne) 1.0f else 2.0f) - e) * zFar)
    } else {
      this._m22((if (zZeroToOne) zFar else zFar + zNear) / (zFar - zNear))
      this._m32((if (zZeroToOne) zFar else zFar + zFar) * zNear / (zNear - zFar))
    }
    this._m23(1.0f)
    this._m33(0.0f)
    _properties(0)
    return this
  }

  /**
   * Set this matrix to represent a perspective projection equivalent to the given intrinsic camera calibration parameters.
   * The resulting matrix will be suited for a right-handed coordinate system using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
   *
   *
   * See: [https://en.wikipedia.org/](https://en.wikipedia.org/wiki/Camera_resectioning#Intrinsic_parameters)
   *
   *
   * Reference: [http://ksimek.github.io/](http://ksimek.github.io/2013/06/03/calibrated_cameras_in_opengl/)
   *
   * @param alphaX
   * specifies the focal length and scale along the X axis
   * @param alphaY
   * specifies the focal length and scale along the Y axis
   * @param gamma
   * the skew coefficient between the X and Y axis (may be <tt>0</tt>)
   * @param u0
   * the X coordinate of the principal point in image/sensor units
   * @param v0
   * the Y coordinate of the principal point in image/sensor units
   * @param imgWidth
   * the width of the sensor/image image/sensor units
   * @param imgHeight
   * the height of the sensor/image image/sensor units
   * @param near
   * the distance to the near plane
   * @param far
   * the distance to the far plane
   * @return this
   */
  fun setFromIntrinsic(alphaX: Float, alphaY: Float, gamma: Float, u0: Float, v0: Float, imgWidth: Int, imgHeight: Int, near: Float, far: Float): Matrix4f {
    val l00 = 2.0f / imgWidth
    val l11 = 2.0f / imgHeight
    val l22 = 2.0f / (near - far)
    this.m00 = l00 * alphaX
    this.m01 = 0.0f
    this.m02 = 0.0f
    this.m03 = 0.0f
    this.m10 = l00 * gamma
    this.m11 = l11 * alphaY
    this.m12 = 0.0f
    this.m13 = 0.0f
    this.m20 = l00 * u0 - 1.0f
    this.m21 = l11 * v0 - 1.0f
    this.m22 = l22 * -(near + far) + (far + near) / (near - far)
    this.m23 = -1.0f
    this.m30 = 0.0f
    this.m31 = 0.0f
    this.m32 = l22 * -near * far
    this.m33 = 0.0f
    this.properties = Matrix4fc.PROPERTY_PERSPECTIVE.toInt()
    return this
  }

  /**
   * Apply the rotation transformation of the given [Quaternionfc] to this matrix and store
   * the result in `dest`.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `Q` the rotation matrix obtained from the given quaternion,
   * then the new matrix will be `M * Q`. So when transforming a
   * vector `v` with the new matrix by using `M * Q * v`,
   * the quaternion rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying,
   * use [.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
   *
   * @see .rotation
   * @param quat
   * the [Quaternionfc]
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotate(quat: Quaternionfc, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.rotation(quat)
    else if (properties and Matrix4fc.PROPERTY_TRANSLATION != 0)
      return rotateTranslation(quat, dest)
    else if (properties and Matrix4fc.PROPERTY_AFFINE != 0)
      return rotateAffine(quat, dest)
    return rotateGeneric(quat, dest)
  }

  private fun rotateGeneric(quat: Quaternionfc, dest: Matrix4f): Matrix4f {
    val w2 = quat.w * quat.w
    val x2 = quat.x * quat.x
    val y2 = quat.y * quat.y
    val z2 = quat.z * quat.z
    val zw = quat.z * quat.w
    val xy = quat.x * quat.y
    val xz = quat.x * quat.z
    val yw = quat.y * quat.w
    val yz = quat.y * quat.z
    val xw = quat.x * quat.w
    val rm00 = w2 + x2 - z2 - y2
    val rm01 = xy + zw + zw + xy
    val rm02 = xz - yw + xz - yw
    val rm10 = -zw + xy - zw + xy
    val rm11 = y2 - z2 + w2 - x2
    val rm12 = yz + yz + xw + xw
    val rm20 = yw + xz + xz + yw
    val rm21 = yz + yz - xw - xw
    val rm22 = z2 - y2 - x2 + w2
    val nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02
    val nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02
    val nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02
    val nm03 = m03 * rm00 + m13 * rm01 + m23 * rm02
    val nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12
    val nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12
    val nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12
    val nm13 = m03 * rm10 + m13 * rm11 + m23 * rm12
    dest._m20(m00 * rm20 + m10 * rm21 + m20 * rm22)
    dest._m21(m01 * rm20 + m11 * rm21 + m21 * rm22)
    dest._m22(m02 * rm20 + m12 * rm21 + m22 * rm22)
    dest._m23(m03 * rm20 + m13 * rm21 + m23 * rm22)
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply the rotation transformation of the given [Quaternionfc] to this matrix.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `Q` the rotation matrix obtained from the given quaternion,
   * then the new matrix will be `M * Q`. So when transforming a
   * vector `v` with the new matrix by using `M * Q * v`,
   * the quaternion rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying,
   * use [.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
   *
   * @see .rotation
   * @param quat
   * the [Quaternionfc]
   * @return a matrix holding the result
   */
  fun rotate(quat: Quaternionfc): Matrix4f {
    return rotate(quat, thisOrNew())
  }

  /**
   * Apply the rotation transformation of the given [Quaternionfc] to this [affine][.isAffine] matrix and store
   * the result in `dest`.
   *
   *
   * This method assumes `this` to be [affine][.isAffine].
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `Q` the rotation matrix obtained from the given quaternion,
   * then the new matrix will be `M * Q`. So when transforming a
   * vector `v` with the new matrix by using `M * Q * v`,
   * the quaternion rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying,
   * use [.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
   *
   * @see .rotation
   * @param quat
   * the [Quaternionfc]
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotateAffine(quat: Quaternionfc, dest: Matrix4f): Matrix4f {
    val w2 = quat.w * quat.w
    val x2 = quat.x * quat.x
    val y2 = quat.y * quat.y
    val z2 = quat.z * quat.z
    val zw = quat.z * quat.w
    val xy = quat.x * quat.y
    val xz = quat.x * quat.z
    val yw = quat.y * quat.w
    val yz = quat.y * quat.z
    val xw = quat.x * quat.w
    val rm00 = w2 + x2 - z2 - y2
    val rm01 = xy + zw + zw + xy
    val rm02 = xz - yw + xz - yw
    val rm10 = -zw + xy - zw + xy
    val rm11 = y2 - z2 + w2 - x2
    val rm12 = yz + yz + xw + xw
    val rm20 = yw + xz + xz + yw
    val rm21 = yz + yz - xw - xw
    val rm22 = z2 - y2 - x2 + w2
    val nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02
    val nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02
    val nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02
    val nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12
    val nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12
    val nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12
    dest._m20(m00 * rm20 + m10 * rm21 + m20 * rm22)
    dest._m21(m01 * rm20 + m11 * rm21 + m21 * rm22)
    dest._m22(m02 * rm20 + m12 * rm21 + m22 * rm22)
    dest._m23(0.0f)
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(0.0f)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(0.0f)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())

    return dest
  }

  /**
   * Apply the rotation transformation of the given [Quaternionfc] to this matrix.
   *
   *
   * This method assumes `this` to be [affine][.isAffine].
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `Q` the rotation matrix obtained from the given quaternion,
   * then the new matrix will be `M * Q`. So when transforming a
   * vector `v` with the new matrix by using `M * Q * v`,
   * the quaternion rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying,
   * use [.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
   *
   * @see .rotation
   * @param quat
   * the [Quaternionfc]
   * @return a matrix holding the result
   */
  fun rotateAffine(quat: Quaternionfc): Matrix4f {
    return rotateAffine(quat, thisOrNew())
  }

  /**
   * Apply the rotation transformation of the given [Quaternionfc] to this matrix, which is assumed to only contain a translation, and store
   * the result in `dest`.
   *
   *
   * This method assumes `this` to only contain a translation.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `Q` the rotation matrix obtained from the given quaternion,
   * then the new matrix will be `M * Q`. So when transforming a
   * vector `v` with the new matrix by using `M * Q * v`,
   * the quaternion rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying,
   * use [.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
   *
   * @see .rotation
   * @param quat
   * the [Quaternionfc]
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotateTranslation(quat: Quaternionfc, dest: Matrix4f): Matrix4f {
    val w2 = quat.w * quat.w
    val x2 = quat.x * quat.x
    val y2 = quat.y * quat.y
    val z2 = quat.z * quat.z
    val zw = quat.z * quat.w
    val xy = quat.x * quat.y
    val xz = quat.x * quat.z
    val yw = quat.y * quat.w
    val yz = quat.y * quat.z
    val xw = quat.x * quat.w
    val rm00 = w2 + x2 - z2 - y2
    val rm01 = xy + zw + zw + xy
    val rm02 = xz - yw + xz - yw
    val rm10 = -zw + xy - zw + xy
    val rm11 = y2 - z2 + w2 - x2
    val rm12 = yz + yz + xw + xw
    val rm20 = yw + xz + xz + yw
    val rm21 = yz + yz - xw - xw
    val rm22 = z2 - y2 - x2 + w2
    dest._m20(rm20)
    dest._m21(rm21)
    dest._m22(rm22)
    dest._m23(0.0f)
    dest._m00(rm00)
    dest._m01(rm01)
    dest._m02(rm02)
    dest._m03(0.0f)
    dest._m10(rm10)
    dest._m11(rm11)
    dest._m12(rm12)
    dest._m13(0.0f)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())

    return dest
  }

  /**
   * Apply the rotation transformation of the given [Quaternionfc] to this matrix while using <tt>(ox, oy, oz)</tt> as the rotation origin.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `Q` the rotation matrix obtained from the given quaternion,
   * then the new matrix will be `M * Q`. So when transforming a
   * vector `v` with the new matrix by using `M * Q * v`,
   * the quaternion rotation will be applied first!
   *
   *
   * This method is equivalent to calling: <tt>translate(ox, oy, oz).rotate(quat).translate(-ox, -oy, -oz)</tt>
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
   *
   * @param quat
   * the [Quaternionfc]
   * @param ox
   * the x coordinate of the rotation origin
   * @param oy
   * the y coordinate of the rotation origin
   * @param oz
   * the z coordinate of the rotation origin
   * @return a matrix holding the result
   */
  fun rotateAround(quat: Quaternionfc, ox: Float, oy: Float, oz: Float): Matrix4f {
    return rotateAround(quat, ox, oy, oz, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateAround(Quaternionfc, float, float, float, Matrix4f)
     */
  override fun rotateAround(quat: Quaternionfc, ox: Float, oy: Float, oz: Float, dest: Matrix4f): Matrix4f {
    val w2 = quat.w * quat.w
    val x2 = quat.x * quat.x
    val y2 = quat.y * quat.y
    val z2 = quat.z * quat.z
    val zw = quat.z * quat.w
    val xy = quat.x * quat.y
    val xz = quat.x * quat.z
    val yw = quat.y * quat.w
    val yz = quat.y * quat.z
    val xw = quat.x * quat.w
    val rm00 = w2 + x2 - z2 - y2
    val rm01 = xy + zw + zw + xy
    val rm02 = xz - yw + xz - yw
    val rm10 = -zw + xy - zw + xy
    val rm11 = y2 - z2 + w2 - x2
    val rm12 = yz + yz + xw + xw
    val rm20 = yw + xz + xz + yw
    val rm21 = yz + yz - xw - xw
    val rm22 = z2 - y2 - x2 + w2
    val tm30 = m00 * ox + m10 * oy + m20 * oz + m30
    val tm31 = m01 * ox + m11 * oy + m21 * oz + m31
    val tm32 = m02 * ox + m12 * oy + m22 * oz + m32
    val nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02
    val nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02
    val nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02
    val nm03 = m03 * rm00 + m13 * rm01 + m23 * rm02
    val nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12
    val nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12
    val nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12
    val nm13 = m03 * rm10 + m13 * rm11 + m23 * rm12
    dest._m20(m00 * rm20 + m10 * rm21 + m20 * rm22)
    dest._m21(m01 * rm20 + m11 * rm21 + m21 * rm22)
    dest._m22(m02 * rm20 + m12 * rm21 + m22 * rm22)
    dest._m23(m03 * rm20 + m13 * rm21 + m23 * rm22)
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m30(-nm00 * ox - nm10 * oy - m20 * oz + tm30)
    dest._m31(-nm01 * ox - nm11 * oy - m21 * oz + tm31)
    dest._m32(-nm02 * ox - nm12 * oy - m22 * oz + tm32)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Pre-multiply the rotation transformation of the given [Quaternionfc] to this matrix and store
   * the result in `dest`.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `Q` the rotation matrix obtained from the given quaternion,
   * then the new matrix will be `Q * M`. So when transforming a
   * vector `v` with the new matrix by using `Q * M * v`,
   * the quaternion rotation will be applied last!
   *
   *
   * In order to set the matrix to a rotation transformation without pre-multiplying,
   * use [.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
   *
   * @see .rotation
   * @param quat
   * the [Quaternionfc]
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotateLocal(quat: Quaternionfc, dest: Matrix4f): Matrix4f {
    val w2 = quat.w * quat.w
    val x2 = quat.x * quat.x
    val y2 = quat.y * quat.y
    val z2 = quat.z * quat.z
    val zw = quat.z * quat.w
    val xy = quat.x * quat.y
    val xz = quat.x * quat.z
    val yw = quat.y * quat.w
    val yz = quat.y * quat.z
    val xw = quat.x * quat.w
    val lm00 = w2 + x2 - z2 - y2
    val lm01 = xy + zw + zw + xy
    val lm02 = xz - yw + xz - yw
    val lm10 = -zw + xy - zw + xy
    val lm11 = y2 - z2 + w2 - x2
    val lm12 = yz + yz + xw + xw
    val lm20 = yw + xz + xz + yw
    val lm21 = yz + yz - xw - xw
    val lm22 = z2 - y2 - x2 + w2
    val nm00 = lm00 * m00 + lm10 * m01 + lm20 * m02
    val nm01 = lm01 * m00 + lm11 * m01 + lm21 * m02
    val nm02 = lm02 * m00 + lm12 * m01 + lm22 * m02
    val nm03 = m03
    val nm10 = lm00 * m10 + lm10 * m11 + lm20 * m12
    val nm11 = lm01 * m10 + lm11 * m11 + lm21 * m12
    val nm12 = lm02 * m10 + lm12 * m11 + lm22 * m12
    val nm13 = m13
    val nm20 = lm00 * m20 + lm10 * m21 + lm20 * m22
    val nm21 = lm01 * m20 + lm11 * m21 + lm21 * m22
    val nm22 = lm02 * m20 + lm12 * m21 + lm22 * m22
    val nm23 = m23
    val nm30 = lm00 * m30 + lm10 * m31 + lm20 * m32
    val nm31 = lm01 * m30 + lm11 * m31 + lm21 * m32
    val nm32 = lm02 * m30 + lm12 * m31 + lm22 * m32
    val nm33 = m33
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m30(nm30)
    dest._m31(nm31)
    dest._m32(nm32)
    dest._m33(nm33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Pre-multiply the rotation transformation of the given [Quaternionfc] to this matrix.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `Q` the rotation matrix obtained from the given quaternion,
   * then the new matrix will be `Q * M`. So when transforming a
   * vector `v` with the new matrix by using `Q * M * v`,
   * the quaternion rotation will be applied last!
   *
   *
   * In order to set the matrix to a rotation transformation without pre-multiplying,
   * use [.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
   *
   * @see .rotation
   * @param quat
   * the [Quaternionfc]
   * @return a matrix holding the result
   */
  fun rotateLocal(quat: Quaternionfc): Matrix4f {
    return rotateLocal(quat, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#rotateAroundLocal(Quaternionfc, float, float, float, Matrix4f)
     */
  override fun rotateAroundLocal(quat: Quaternionfc, ox: Float, oy: Float, oz: Float, dest: Matrix4f): Matrix4f {
    val w2 = quat.w * quat.w
    val x2 = quat.x * quat.x
    val y2 = quat.y * quat.y
    val z2 = quat.z * quat.z
    val zw = quat.z * quat.w
    val xy = quat.x * quat.y
    val xz = quat.x * quat.z
    val yw = quat.y * quat.w
    val yz = quat.y * quat.z
    val xw = quat.x * quat.w
    val lm00 = w2 + x2 - z2 - y2
    val lm01 = xy + zw + zw + xy
    val lm02 = xz - yw + xz - yw
    val lm10 = -zw + xy - zw + xy
    val lm11 = y2 - z2 + w2 - x2
    val lm12 = yz + yz + xw + xw
    val lm20 = yw + xz + xz + yw
    val lm21 = yz + yz - xw - xw
    val lm22 = z2 - y2 - x2 + w2
    val tm00 = m00 - ox * m03
    val tm01 = m01 - oy * m03
    val tm02 = m02 - oz * m03
    val tm10 = m10 - ox * m13
    val tm11 = m11 - oy * m13
    val tm12 = m12 - oz * m13
    val tm20 = m20 - ox * m23
    val tm21 = m21 - oy * m23
    val tm22 = m22 - oz * m23
    val tm30 = m30 - ox * m33
    val tm31 = m31 - oy * m33
    val tm32 = m32 - oz * m33
    dest._m00(lm00 * tm00 + lm10 * tm01 + lm20 * tm02 + ox * m03)
    dest._m01(lm01 * tm00 + lm11 * tm01 + lm21 * tm02 + oy * m03)
    dest._m02(lm02 * tm00 + lm12 * tm01 + lm22 * tm02 + oz * m03)
    dest._m03(m03)
    dest._m10(lm00 * tm10 + lm10 * tm11 + lm20 * tm12 + ox * m13)
    dest._m11(lm01 * tm10 + lm11 * tm11 + lm21 * tm12 + oy * m13)
    dest._m12(lm02 * tm10 + lm12 * tm11 + lm22 * tm12 + oz * m13)
    dest._m13(m13)
    dest._m20(lm00 * tm20 + lm10 * tm21 + lm20 * tm22 + ox * m23)
    dest._m21(lm01 * tm20 + lm11 * tm21 + lm21 * tm22 + oy * m23)
    dest._m22(lm02 * tm20 + lm12 * tm21 + lm22 * tm22 + oz * m23)
    dest._m23(m23)
    dest._m30(lm00 * tm30 + lm10 * tm31 + lm20 * tm32 + ox * m33)
    dest._m31(lm01 * tm30 + lm11 * tm31 + lm21 * tm32 + oy * m33)
    dest._m32(lm02 * tm30 + lm12 * tm31 + lm22 * tm32 + oz * m33)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Pre-multiply the rotation transformation of the given [Quaternionfc] to this matrix while using <tt>(ox, oy, oz)</tt>
   * as the rotation origin.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `Q` the rotation matrix obtained from the given quaternion,
   * then the new matrix will be `Q * M`. So when transforming a
   * vector `v` with the new matrix by using `Q * M * v`,
   * the quaternion rotation will be applied last!
   *
   *
   * This method is equivalent to calling: <tt>translateLocal(-ox, -oy, -oz).rotateLocal(quat).translateLocal(ox, oy, oz)</tt>
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
   *
   * @param quat
   * the [Quaternionfc]
   * @param ox
   * the x coordinate of the rotation origin
   * @param oy
   * the y coordinate of the rotation origin
   * @param oz
   * the z coordinate of the rotation origin
   * @return a matrix holding the result
   */
  fun rotateAroundLocal(quat: Quaternionfc, ox: Float, oy: Float, oz: Float): Matrix4f {
    return rotateAroundLocal(quat, ox, oy, oz, thisOrNew())
  }

  /**
   * Apply a rotation transformation, rotating about the given [AxisAngle4f], to this matrix.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `A` the rotation matrix obtained from the given [AxisAngle4f],
   * then the new matrix will be `M * A`. So when transforming a
   * vector `v` with the new matrix by using `M * A * v`,
   * the [AxisAngle4f] rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying,
   * use [.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle)
   *
   * @see .rotate
   * @see .rotation
   * @param axisAngle
   * the [AxisAngle4f] (needs to be [normalized][AxisAngle4f.normalize])
   * @return this
   */
  fun rotate(axisAngle: AxisAngle4f): Matrix4f {
    return rotate(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z)
  }

  /**
   * Apply a rotation transformation, rotating about the given [AxisAngle4f] and store the result in `dest`.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `A` the rotation matrix obtained from the given [AxisAngle4f],
   * then the new matrix will be `M * A`. So when transforming a
   * vector `v` with the new matrix by using `M * A * v`,
   * the [AxisAngle4f] rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying,
   * use [.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle)
   *
   * @see .rotate
   * @see .rotation
   * @param axisAngle
   * the [AxisAngle4f] (needs to be [normalized][AxisAngle4f.normalize])
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotate(axisAngle: AxisAngle4f, dest: Matrix4f): Matrix4f {
    return rotate(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z, dest)
  }

  /**
   * Apply a rotation transformation, rotating the given radians about the specified axis, to this matrix.
   *
   *
   * The axis described by the `axis` vector needs to be a unit vector.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `A` the rotation matrix obtained from the given axis-angle,
   * then the new matrix will be `M * A`. So when transforming a
   * vector `v` with the new matrix by using `M * A * v`,
   * the axis-angle rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying,
   * use [.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle)
   *
   * @see .rotate
   * @see .rotation
   * @param angle
   * the angle in radians
   * @param axis
   * the rotation axis (needs to be [normalized][Vector3m.normalize])
   * @return this
   */
  fun rotate(angle: Float, axis: Vector3fc): Matrix4f {
    return rotate(angle, axis.x, axis.y, axis.z)
  }

  /**
   * Apply a rotation transformation, rotating the given radians about the specified axis and store the result in `dest`.
   *
   *
   * The axis described by the `axis` vector needs to be a unit vector.
   *
   *
   * When used with a right-handed coordinate system, the produced rotation will rotate a vector
   * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
   * When used with a left-handed coordinate system, the rotation is clockwise.
   *
   *
   * If `M` is `this` matrix and `A` the rotation matrix obtained from the given axis-angle,
   * then the new matrix will be `M * A`. So when transforming a
   * vector `v` with the new matrix by using `M * A * v`,
   * the axis-angle rotation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying,
   * use [.rotation].
   *
   *
   * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle)
   *
   * @see .rotate
   * @see .rotation
   * @param angle
   * the angle in radians
   * @param axis
   * the rotation axis (needs to be [normalized][Vector3m.normalize])
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotate(angle: Float, axis: Vector3fc, dest: Matrix4f): Matrix4f {
    return rotate(angle, axis.x, axis.y, axis.z, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unproject(float, float, float, int[], Vector4f)
     */
  override fun unproject(winX: Float, winY: Float, winZ: Float, viewport: IntArray, dest: Vector4f): Vector4f {
    val a = m00 * m11 - m01 * m10
    val b = m00 * m12 - m02 * m10
    val c = m00 * m13 - m03 * m10
    val d = m01 * m12 - m02 * m11
    val e = m01 * m13 - m03 * m11
    val f = m02 * m13 - m03 * m12
    val g = m20 * m31 - m21 * m30
    val h = m20 * m32 - m22 * m30
    val i = m20 * m33 - m23 * m30
    val j = m21 * m32 - m22 * m31
    val k = m21 * m33 - m23 * m31
    val l = m22 * m33 - m23 * m32
    var det = a * l - b * k + c * j + d * i - e * h + f * g
    det = 1.0f / det
    val im00 = (m11 * l - m12 * k + m13 * j) * det
    val im01 = (-m01 * l + m02 * k - m03 * j) * det
    val im02 = (m31 * f - m32 * e + m33 * d) * det
    val im03 = (-m21 * f + m22 * e - m23 * d) * det
    val im10 = (-m10 * l + m12 * i - m13 * h) * det
    val im11 = (m00 * l - m02 * i + m03 * h) * det
    val im12 = (-m30 * f + m32 * c - m33 * b) * det
    val im13 = (m20 * f - m22 * c + m23 * b) * det
    val im20 = (m10 * k - m11 * i + m13 * g) * det
    val im21 = (-m00 * k + m01 * i - m03 * g) * det
    val im22 = (m30 * e - m31 * c + m33 * a) * det
    val im23 = (-m20 * e + m21 * c - m23 * a) * det
    val im30 = (-m10 * j + m11 * h - m12 * g) * det
    val im31 = (m00 * j - m01 * h + m02 * g) * det
    val im32 = (-m30 * d + m31 * b - m32 * a) * det
    val im33 = (m20 * d - m21 * b + m22 * a) * det
    val ndcX = (winX - viewport[0]) / viewport[2] * 2.0f - 1.0f
    val ndcY = (winY - viewport[1]) / viewport[3] * 2.0f - 1.0f
    val ndcZ = winZ + winZ - 1.0f
    val invW = 1.0f / (im03 * ndcX + im13 * ndcY + im23 * ndcZ + im33)
    dest.x = (im00 * ndcX + im10 * ndcY + im20 * ndcZ + im30) * invW
    dest.y = (im01 * ndcX + im11 * ndcY + im21 * ndcZ + im31) * invW
    dest.z = (im02 * ndcX + im12 * ndcY + im22 * ndcZ + im32) * invW
    dest.w = 1.0f
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unproject(float, float, float, int[], Vector3m)
     */
  override fun unproject(winX: Float, winY: Float, winZ: Float, viewport: IntArray, dest: Vector3m): Vector3m {
    val a = m00 * m11 - m01 * m10
    val b = m00 * m12 - m02 * m10
    val c = m00 * m13 - m03 * m10
    val d = m01 * m12 - m02 * m11
    val e = m01 * m13 - m03 * m11
    val f = m02 * m13 - m03 * m12
    val g = m20 * m31 - m21 * m30
    val h = m20 * m32 - m22 * m30
    val i = m20 * m33 - m23 * m30
    val j = m21 * m32 - m22 * m31
    val k = m21 * m33 - m23 * m31
    val l = m22 * m33 - m23 * m32
    var det = a * l - b * k + c * j + d * i - e * h + f * g
    det = 1.0f / det
    val im00 = (m11 * l - m12 * k + m13 * j) * det
    val im01 = (-m01 * l + m02 * k - m03 * j) * det
    val im02 = (m31 * f - m32 * e + m33 * d) * det
    val im03 = (-m21 * f + m22 * e - m23 * d) * det
    val im10 = (-m10 * l + m12 * i - m13 * h) * det
    val im11 = (m00 * l - m02 * i + m03 * h) * det
    val im12 = (-m30 * f + m32 * c - m33 * b) * det
    val im13 = (m20 * f - m22 * c + m23 * b) * det
    val im20 = (m10 * k - m11 * i + m13 * g) * det
    val im21 = (-m00 * k + m01 * i - m03 * g) * det
    val im22 = (m30 * e - m31 * c + m33 * a) * det
    val im23 = (-m20 * e + m21 * c - m23 * a) * det
    val im30 = (-m10 * j + m11 * h - m12 * g) * det
    val im31 = (m00 * j - m01 * h + m02 * g) * det
    val im32 = (-m30 * d + m31 * b - m32 * a) * det
    val im33 = (m20 * d - m21 * b + m22 * a) * det
    val ndcX = (winX - viewport[0]) / viewport[2] * 2.0f - 1.0f
    val ndcY = (winY - viewport[1]) / viewport[3] * 2.0f - 1.0f
    val ndcZ = winZ + winZ - 1.0f
    val invW = 1.0f / (im03 * ndcX + im13 * ndcY + im23 * ndcZ + im33)
    dest.x = (im00 * ndcX + im10 * ndcY + im20 * ndcZ + im30) * invW
    dest.y = (im01 * ndcX + im11 * ndcY + im21 * ndcZ + im31) * invW
    dest.z = (im02 * ndcX + im12 * ndcY + im22 * ndcZ + im32) * invW
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unproject(Vector3fc, int[], Vector4f)
     */
  override fun unproject(winCoords: Vector3fc, viewport: IntArray, dest: Vector4f): Vector4f {
    return unproject(winCoords.x, winCoords.y, winCoords.z, viewport, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unproject(Vector3fc, int[], Vector3m)
     */
  override fun unproject(winCoords: Vector3fc, viewport: IntArray, dest: Vector3m): Vector3m {
    return unproject(winCoords.x, winCoords.y, winCoords.z, viewport, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unprojectRay(float, float, int[], Vector3m, Vector3m)
     */
  override fun unprojectRay(winX: Float, winY: Float, viewport: IntArray, originDest: Vector3m, dirDest: Vector3m): Matrix4f {
    val a = m00 * m11 - m01 * m10
    val b = m00 * m12 - m02 * m10
    val c = m00 * m13 - m03 * m10
    val d = m01 * m12 - m02 * m11
    val e = m01 * m13 - m03 * m11
    val f = m02 * m13 - m03 * m12
    val g = m20 * m31 - m21 * m30
    val h = m20 * m32 - m22 * m30
    val i = m20 * m33 - m23 * m30
    val j = m21 * m32 - m22 * m31
    val k = m21 * m33 - m23 * m31
    val l = m22 * m33 - m23 * m32
    var det = a * l - b * k + c * j + d * i - e * h + f * g
    det = 1.0f / det
    val im00 = (m11 * l - m12 * k + m13 * j) * det
    val im01 = (-m01 * l + m02 * k - m03 * j) * det
    val im02 = (m31 * f - m32 * e + m33 * d) * det
    val im03 = (-m21 * f + m22 * e - m23 * d) * det
    val im10 = (-m10 * l + m12 * i - m13 * h) * det
    val im11 = (m00 * l - m02 * i + m03 * h) * det
    val im12 = (-m30 * f + m32 * c - m33 * b) * det
    val im13 = (m20 * f - m22 * c + m23 * b) * det
    val im20 = (m10 * k - m11 * i + m13 * g) * det
    val im21 = (-m00 * k + m01 * i - m03 * g) * det
    val im22 = (m30 * e - m31 * c + m33 * a) * det
    val im23 = (-m20 * e + m21 * c - m23 * a) * det
    val im30 = (-m10 * j + m11 * h - m12 * g) * det
    val im31 = (m00 * j - m01 * h + m02 * g) * det
    val im32 = (-m30 * d + m31 * b - m32 * a) * det
    val im33 = (m20 * d - m21 * b + m22 * a) * det
    val ndcX = (winX - viewport[0]) / viewport[2] * 2.0f - 1.0f
    val ndcY = (winY - viewport[1]) / viewport[3] * 2.0f - 1.0f
    val px = im00 * ndcX + im10 * ndcY + im30
    val py = im01 * ndcX + im11 * ndcY + im31
    val pz = im02 * ndcX + im12 * ndcY + im32
    val invNearW = 1.0f / (im03 * ndcX + im13 * ndcY - im23 + im33)
    val nearX = (px - im20) * invNearW
    val nearY = (py - im21) * invNearW
    val nearZ = (pz - im22) * invNearW
    val invFarW = 1.0f / (im03 * ndcX + im13 * ndcY + im23 + im33)
    val farX = (px + im20) * invFarW
    val farY = (py + im21) * invFarW
    val farZ = (pz + im22) * invFarW
    originDest.x = nearX
    originDest.y = nearY
    originDest.z = nearZ
    dirDest.x = farX - nearX
    dirDest.y = farY - nearY
    dirDest.z = farZ - nearZ
    return this
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unprojectRay(Vector2fc, int[], Vector3m, Vector3m)
     */
  override fun unprojectRay(winCoords: Vector2fc, viewport: IntArray, originDest: Vector3m, dirDest: Vector3m): Matrix4f {
    return unprojectRay(winCoords.x, winCoords.y, viewport, originDest, dirDest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unprojectInv(Vector3fc, int[], Vector4f)
     */
  override fun unprojectInv(winCoords: Vector3fc, viewport: IntArray, dest: Vector4f): Vector4f {
    return unprojectInv(winCoords.x, winCoords.y, winCoords.z, viewport, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unprojectInv(float, float, float, int[], Vector4f)
     */
  override fun unprojectInv(winX: Float, winY: Float, winZ: Float, viewport: IntArray, dest: Vector4f): Vector4f {
    val ndcX = (winX - viewport[0]) / viewport[2] * 2.0f - 1.0f
    val ndcY = (winY - viewport[1]) / viewport[3] * 2.0f - 1.0f
    val ndcZ = winZ + winZ - 1.0f
    val invW = 1.0f / (m03 * ndcX + m13 * ndcY + m23 * ndcZ + m33)
    dest.x = (m00 * ndcX + m10 * ndcY + m20 * ndcZ + m30) * invW
    dest.y = (m01 * ndcX + m11 * ndcY + m21 * ndcZ + m31) * invW
    dest.z = (m02 * ndcX + m12 * ndcY + m22 * ndcZ + m32) * invW
    dest.w = 1.0f
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unprojectInvRay(Vector2fc, int[], Vector3m, Vector3m)
     */
  override fun unprojectInvRay(winCoords: Vector2fc, viewport: IntArray, originDest: Vector3m, dirDest: Vector3m): Matrix4f {
    return unprojectInvRay(winCoords.x, winCoords.y, viewport, originDest, dirDest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unprojectInvRay(float, float, int[], Vector3m, Vector3m)
     */
  override fun unprojectInvRay(winX: Float, winY: Float, viewport: IntArray, originDest: Vector3m, dirDest: Vector3m): Matrix4f {
    val ndcX = (winX - viewport[0]) / viewport[2] * 2.0f - 1.0f
    val ndcY = (winY - viewport[1]) / viewport[3] * 2.0f - 1.0f
    val px = m00 * ndcX + m10 * ndcY + m30
    val py = m01 * ndcX + m11 * ndcY + m31
    val pz = m02 * ndcX + m12 * ndcY + m32
    val invNearW = 1.0f / (m03 * ndcX + m13 * ndcY - m23 + m33)
    val nearX = (px - m20) * invNearW
    val nearY = (py - m21) * invNearW
    val nearZ = (pz - m22) * invNearW
    val invFarW = 1.0f / (m03 * ndcX + m13 * ndcY + m23 + m33)
    val farX = (px + m20) * invFarW
    val farY = (py + m21) * invFarW
    val farZ = (pz + m22) * invFarW
    originDest.x = nearX
    originDest.y = nearY
    originDest.z = nearZ
    dirDest.x = farX - nearX
    dirDest.y = farY - nearY
    dirDest.z = farZ - nearZ
    return this
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unprojectInv(Vector3fc, int[], Vector3m)
     */
  override fun unprojectInv(winCoords: Vector3fc, viewport: IntArray, dest: Vector3m): Vector3m {
    return unprojectInv(winCoords.x, winCoords.y, winCoords.z, viewport, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#unprojectInv(float, float, float, int[], Vector3m)
     */
  override fun unprojectInv(winX: Float, winY: Float, winZ: Float, viewport: IntArray, dest: Vector3m): Vector3m {
    val ndcX = (winX - viewport[0]) / viewport[2] * 2.0f - 1.0f
    val ndcY = (winY - viewport[1]) / viewport[3] * 2.0f - 1.0f
    val ndcZ = winZ + winZ - 1.0f
    val invW = 1.0f / (m03 * ndcX + m13 * ndcY + m23 * ndcZ + m33)
    dest.x = (m00 * ndcX + m10 * ndcY + m20 * ndcZ + m30) * invW
    dest.y = (m01 * ndcX + m11 * ndcY + m21 * ndcZ + m31) * invW
    dest.z = (m02 * ndcX + m12 * ndcY + m22 * ndcZ + m32) * invW
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#project(float, float, float, int[], Vector4f)
     */
  override fun project(x: Float, y: Float, z: Float, viewport: IntArray, winCoordsDest: Vector4f): Vector4f {
    val invW = 1.0f / (m03 * x + m13 * y + m23 * z + m33)
    val nx = (m00 * x + m10 * y + m20 * z + m30) * invW
    val ny = (m01 * x + m11 * y + m21 * z + m31) * invW
    val nz = (m02 * x + m12 * y + m22 * z + m32) * invW
    winCoordsDest.x = (nx * 0.5f + 0.5f) * viewport[2] + viewport[0]
    winCoordsDest.y = (ny * 0.5f + 0.5f) * viewport[3] + viewport[1]
    winCoordsDest.z = (1.0f + nz) * 0.5f
    winCoordsDest.w = 1.0f
    return winCoordsDest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#project(float, float, float, int[], Vector3m)
     */
  override fun project(x: Float, y: Float, z: Float, viewport: IntArray, winCoordsDest: Vector3m): Vector3m {
    val invW = 1.0f / (m03 * x + m13 * y + m23 * z + m33)
    val nx = (m00 * x + m10 * y + m20 * z + m30) * invW
    val ny = (m01 * x + m11 * y + m21 * z + m31) * invW
    val nz = (m02 * x + m12 * y + m22 * z + m32) * invW
    winCoordsDest.x = (nx * 0.5f + 0.5f) * viewport[2] + viewport[0]
    winCoordsDest.y = (ny * 0.5f + 0.5f) * viewport[3] + viewport[1]
    winCoordsDest.z = (1.0f + nz) * 0.5f
    return winCoordsDest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#project(Vector3fc, int[], Vector4f)
     */
  override fun project(position: Vector3fc, viewport: IntArray, winCoordsDest: Vector4f): Vector4f {
    return project(position.x, position.y, position.z, viewport, winCoordsDest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#project(Vector3fc, int[], Vector3m)
     */
  override fun project(position: Vector3fc, viewport: IntArray, winCoordsDest: Vector3m): Vector3m {
    return project(position.x, position.y, position.z, viewport, winCoordsDest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#reflect(float, float, float, float, Matrix4f)
     */
  override fun reflect(a: Float, b: Float, c: Float, d: Float, dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.reflection(a, b, c, d)
    else if (properties and Matrix4fc.PROPERTY_AFFINE != 0)
      return reflectAffine(a, b, c, d, dest)
    return reflectGeneric(a, b, c, d, dest)
  }

  private fun reflectAffine(a: Float, b: Float, c: Float, d: Float, dest: Matrix4f): Matrix4f {
    val da = a + a
    val db = b + b
    val dc = c + c
    val dd = d + d
    val rm00 = 1.0f - da * a
    val rm01 = -da * b
    val rm02 = -da * c
    val rm10 = -db * a
    val rm11 = 1.0f - db * b
    val rm12 = -db * c
    val rm20 = -dc * a
    val rm21 = -dc * b
    val rm22 = 1.0f - dc * c
    val rm30 = -dd * a
    val rm31 = -dd * b
    val rm32 = -dd * c
    // matrix multiplication
    dest._m30(m00 * rm30 + m10 * rm31 + m20 * rm32 + m30)
    dest._m31(m01 * rm30 + m11 * rm31 + m21 * rm32 + m31)
    dest._m32(m02 * rm30 + m12 * rm31 + m22 * rm32 + m32)
    dest._m33(m33)
    val nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02
    val nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02
    val nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02
    val nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12
    val nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12
    val nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12
    dest._m20(m00 * rm20 + m10 * rm21 + m20 * rm22)
    dest._m21(m01 * rm20 + m11 * rm21 + m21 * rm22)
    dest._m22(m02 * rm20 + m12 * rm21 + m22 * rm22)
    dest._m23(0.0f)
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(0.0f)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(0.0f)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  private fun reflectGeneric(a: Float, b: Float, c: Float, d: Float, dest: Matrix4f): Matrix4f {
    val da = a + a
    val db = b + b
    val dc = c + c
    val dd = d + d
    val rm00 = 1.0f - da * a
    val rm01 = -da * b
    val rm02 = -da * c
    val rm10 = -db * a
    val rm11 = 1.0f - db * b
    val rm12 = -db * c
    val rm20 = -dc * a
    val rm21 = -dc * b
    val rm22 = 1.0f - dc * c
    val rm30 = -dd * a
    val rm31 = -dd * b
    val rm32 = -dd * c
    // matrix multiplication
    dest._m30(m00 * rm30 + m10 * rm31 + m20 * rm32 + m30)
    dest._m31(m01 * rm30 + m11 * rm31 + m21 * rm32 + m31)
    dest._m32(m02 * rm30 + m12 * rm31 + m22 * rm32 + m32)
    dest._m33(m03 * rm30 + m13 * rm31 + m23 * rm32 + m33)
    val nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02
    val nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02
    val nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02
    val nm03 = m03 * rm00 + m13 * rm01 + m23 * rm02
    val nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12
    val nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12
    val nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12
    val nm13 = m03 * rm10 + m13 * rm11 + m23 * rm12
    dest._m20(m00 * rm20 + m10 * rm21 + m20 * rm22)
    dest._m21(m01 * rm20 + m11 * rm21 + m21 * rm22)
    dest._m22(m02 * rm20 + m12 * rm21 + m22 * rm22)
    dest._m23(m03 * rm20 + m13 * rm21 + m23 * rm22)
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
   * specified via the equation <tt>x*a + y*b + z*c + d = 0</tt>.
   *
   *
   * The vector <tt>(a, b, c)</tt> must be a unit vector.
   *
   *
   * If `M` is `this` matrix and `R` the reflection matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * reflection will be applied first!
   *
   *
   * Reference: [msdn.microsoft.com](https://msdn.microsoft.com/en-us/library/windows/desktop/bb281733(v=vs.85).aspx)
   *
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @return a matrix holding the result
   */
  fun reflect(a: Float, b: Float, c: Float, d: Float): Matrix4f {
    return reflect(a, b, c, d, thisOrNew())
  }

  /**
   * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
   * specified via the plane normal and a point on the plane.
   *
   *
   * If `M` is `this` matrix and `R` the reflection matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * reflection will be applied first!
   *
   * @param nx
   * the x-coordinate of the plane normal
   * @param ny
   * the y-coordinate of the plane normal
   * @param nz
   * the z-coordinate of the plane normal
   * @param px
   * the x-coordinate of a point on the plane
   * @param py
   * the y-coordinate of a point on the plane
   * @param pz
   * the z-coordinate of a point on the plane
   * @return a matrix holding the result
   */
  fun reflect(nx: Float, ny: Float, nz: Float, px: Float, py: Float, pz: Float): Matrix4f {
    return reflect(nx, ny, nz, px, py, pz, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#reflect(float, float, float, float, float, float, Matrix4f)
     */
  override fun reflect(nx: Float, ny: Float, nz: Float, px: Float, py: Float, pz: Float, dest: Matrix4f): Matrix4f {
    val invLength = 1.0f / Math.sqrt((nx * nx + ny * ny + nz * nz).toDouble()).toFloat()
    val nnx = nx * invLength
    val nny = ny * invLength
    val nnz = nz * invLength
    /* See: http://mathworld.wolfram.com/Plane.html */
    return reflect(nnx, nny, nnz, -nnx * px - nny * py - nnz * pz, dest)
  }

  /**
   * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
   * specified via the plane normal and a point on the plane.
   *
   *
   * If `M` is `this` matrix and `R` the reflection matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * reflection will be applied first!
   *
   * @param normal
   * the plane normal
   * @param point
   * a point on the plane
   * @return this
   */
  fun reflect(normal: Vector3fc, point: Vector3fc): Matrix4f {
    return reflect(normal.x, normal.y, normal.z, point.x, point.y, point.z)
  }

  /**
   * Apply a mirror/reflection transformation to this matrix that reflects about a plane
   * specified via the plane orientation and a point on the plane.
   *
   *
   * This method can be used to build a reflection transformation based on the orientation of a mirror object in the scene.
   * It is assumed that the default mirror plane's normal is <tt>(0, 0, 1)</tt>. So, if the given [Quaternionfc] is
   * the identity (does not apply any additional rotation), the reflection plane will be <tt>z=0</tt>, offset by the given `point`.
   *
   *
   * If `M` is `this` matrix and `R` the reflection matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * reflection will be applied first!
   *
   * @param orientation
   * the plane orientation
   * @param point
   * a point on the plane
   * @return a matrix holding the result
   */
  fun reflect(orientation: Quaternionfc, point: Vector3fc): Matrix4f {
    return reflect(orientation, point, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#reflect(Quaternionfc, Vector3fc, Matrix4f)
     */
  override fun reflect(orientation: Quaternionfc, point: Vector3fc, dest: Matrix4f): Matrix4f {
    val num1 = (orientation.x + orientation.x).toDouble()
    val num2 = (orientation.y + orientation.y).toDouble()
    val num3 = (orientation.z + orientation.z).toDouble()
    val normalX = (orientation.x * num3 + orientation.w * num2).toFloat()
    val normalY = (orientation.y * num3 - orientation.w * num1).toFloat()
    val normalZ = (1.0 - (orientation.x * num1 + orientation.y * num2)).toFloat()
    return reflect(normalX, normalY, normalZ, point.x, point.y, point.z, dest)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#reflect(Vector3fc, Vector3fc, Matrix4f)
     */
  override fun reflect(normal: Vector3fc, point: Vector3fc, dest: Matrix4f): Matrix4f {
    return reflect(normal.x, normal.y, normal.z, point.x, point.y, point.z, dest)
  }

  /**
   * Set this matrix to a mirror/reflection transformation that reflects about the given plane
   * specified via the equation <tt>x*a + y*b + z*c + d = 0</tt>.
   *
   *
   * The vector <tt>(a, b, c)</tt> must be a unit vector.
   *
   *
   * Reference: [msdn.microsoft.com](https://msdn.microsoft.com/en-us/library/windows/desktop/bb281733(v=vs.85).aspx)
   *
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @return this
   */
  fun reflection(a: Float, b: Float, c: Float, d: Float): Matrix4f {
    val da = a + a
    val db = b + b
    val dc = c + c
    val dd = d + d
    this._m00(1.0f - da * a)
    this._m01(-da * b)
    this._m02(-da * c)
    this._m03(0.0f)
    this._m10(-db * a)
    this._m11(1.0f - db * b)
    this._m12(-db * c)
    this._m13(0.0f)
    this._m20(-dc * a)
    this._m21(-dc * b)
    this._m22(1.0f - dc * c)
    this._m23(0.0f)
    this._m30(-dd * a)
    this._m31(-dd * b)
    this._m32(-dd * c)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to a mirror/reflection transformation that reflects about the given plane
   * specified via the plane normal and a point on the plane.
   *
   * @param nx
   * the x-coordinate of the plane normal
   * @param ny
   * the y-coordinate of the plane normal
   * @param nz
   * the z-coordinate of the plane normal
   * @param px
   * the x-coordinate of a point on the plane
   * @param py
   * the y-coordinate of a point on the plane
   * @param pz
   * the z-coordinate of a point on the plane
   * @return this
   */
  fun reflection(nx: Float, ny: Float, nz: Float, px: Float, py: Float, pz: Float): Matrix4f {
    val invLength = 1.0f / Math.sqrt((nx * nx + ny * ny + nz * nz).toDouble()).toFloat()
    val nnx = nx * invLength
    val nny = ny * invLength
    val nnz = nz * invLength
    /* See: http://mathworld.wolfram.com/Plane.html */
    return reflection(nnx, nny, nnz, -nnx * px - nny * py - nnz * pz)
  }

  /**
   * Set this matrix to a mirror/reflection transformation that reflects about the given plane
   * specified via the plane normal and a point on the plane.
   *
   * @param normal
   * the plane normal
   * @param point
   * a point on the plane
   * @return this
   */
  fun reflection(normal: Vector3fc, point: Vector3fc): Matrix4f {
    return reflection(normal.x, normal.y, normal.z, point.x, point.y, point.z)
  }

  /**
   * Set this matrix to a mirror/reflection transformation that reflects about a plane
   * specified via the plane orientation and a point on the plane.
   *
   *
   * This method can be used to build a reflection transformation based on the orientation of a mirror object in the scene.
   * It is assumed that the default mirror plane's normal is <tt>(0, 0, 1)</tt>. So, if the given [Quaternionfc] is
   * the identity (does not apply any additional rotation), the reflection plane will be <tt>z=0</tt>, offset by the given `point`.
   *
   * @param orientation
   * the plane orientation
   * @param point
   * a point on the plane
   * @return this
   */
  fun reflection(orientation: Quaternionfc, point: Vector3fc): Matrix4f {
    val num1 = (orientation.x + orientation.x).toDouble()
    val num2 = (orientation.y + orientation.y).toDouble()
    val num3 = (orientation.z + orientation.z).toDouble()
    val normalX = (orientation.x * num3 + orientation.w * num2).toFloat()
    val normalY = (orientation.y * num3 - orientation.w * num1).toFloat()
    val normalZ = (1.0 - (orientation.x * num1 + orientation.y * num2)).toFloat()
    return reflection(normalX, normalY, normalZ, point.x, point.y, point.z)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getRow(int, Vector4f)
     */
  @Throws(IndexOutOfBoundsException::class)
  override fun getRow(row: Int, dest: Vector4f): Vector4f {
    when (row) {
      0 -> {
        dest.x = m00
        dest.y = m10
        dest.z = m20
        dest.w = m30
      }
      1 -> {
        dest.x = m01
        dest.y = m11
        dest.z = m21
        dest.w = m31
      }
      2 -> {
        dest.x = m02
        dest.y = m12
        dest.z = m22
        dest.w = m32
      }
      3 -> {
        dest.x = m03
        dest.y = m13
        dest.z = m23
        dest.w = m33
      }
      else -> throw IndexOutOfBoundsException()
    }
    return dest
  }

  /**
   * Set the row at the given `row` index, starting with `0`.
   *
   * @param row
   * the row index in <tt>[0..3]</tt>
   * @param src
   * the row components to set
   * @return this
   * @throws IndexOutOfBoundsException if `row` is not in <tt>[0..3]</tt>
   */
  @Throws(IndexOutOfBoundsException::class)
  fun setRow(row: Int, src: Vector4fc): Matrix4f {
    when (row) {
      0 -> {
        this._m00(src.x)
        this._m10(src.y)
        this._m20(src.z)
        this._m30(src.w)
      }
      1 -> {
        this._m01(src.x)
        this._m11(src.y)
        this._m21(src.z)
        this._m31(src.w)
      }
      2 -> {
        this._m02(src.x)
        this._m12(src.y)
        this._m22(src.z)
        this._m32(src.w)
      }
      3 -> {
        this._m03(src.x)
        this._m13(src.y)
        this._m23(src.z)
        this._m33(src.w)
      }
      else -> throw IndexOutOfBoundsException()
    }
    _properties(0)
    return this
  }

  /* (non-Javadoc)
     * @see Matrix4fc#getColumn(int, Vector4f)
     */
  @Throws(IndexOutOfBoundsException::class)
  override fun getColumn(column: Int, dest: Vector4f): Vector4f {
    when (column) {
      0 -> MemUtil.INSTANCE.putColumn0(this, dest)
      1 -> MemUtil.INSTANCE.putColumn1(this, dest)
      2 -> MemUtil.INSTANCE.putColumn2(this, dest)
      3 -> MemUtil.INSTANCE.putColumn3(this, dest)
      else -> throw IndexOutOfBoundsException()
    }
    return dest
  }

  /**
   * Set the column at the given `column` index, starting with `0`.
   *
   * @param column
   * the column index in <tt>[0..3]</tt>
   * @param src
   * the column components to set
   * @return this
   * @throws IndexOutOfBoundsException if `column` is not in <tt>[0..3]</tt>
   */
  @Throws(IndexOutOfBoundsException::class)
  fun setColumn(column: Int, src: Vector4fc): Matrix4f {
    when (column) {
      0 -> if (src is Vector4f) {
        MemUtil.INSTANCE.getColumn0(this, src)
      } else {
        this._m00(src.x)
        this._m01(src.y)
        this._m02(src.z)
        this._m03(src.w)
      }
      1 -> if (src is Vector4f) {
        MemUtil.INSTANCE.getColumn1(this, src)
      } else {
        this._m10(src.x)
        this._m11(src.y)
        this._m12(src.z)
        this._m13(src.w)
      }
      2 -> if (src is Vector4f) {
        MemUtil.INSTANCE.getColumn2(this, src)
      } else {
        this._m20(src.x)
        this._m21(src.y)
        this._m22(src.z)
        this._m23(src.w)
      }
      3 -> if (src is Vector4f) {
        MemUtil.INSTANCE.getColumn3(this, src)
      } else {
        this._m30(src.x)
        this._m31(src.y)
        this._m32(src.z)
        this._m33(src.w)
      }
      else -> throw IndexOutOfBoundsException()
    }
    _properties(0)
    return this
  }

  /**
   * Compute a normal matrix from the upper left 3x3 submatrix of `this`
   * and store it into the upper left 3x3 submatrix of `this`.
   * All other values of `this` will be set to [identity][.identity].
   *
   *
   * The normal matrix of <tt>m</tt> is the transpose of the inverse of <tt>m</tt>.
   *
   *
   * Please note that, if `this` is an orthogonal matrix or a matrix whose columns are orthogonal vectors,
   * then this method *need not* be invoked, since in that case `this` itself is its normal matrix.
   * In that case, use [.set3x3] to set a given Matrix4f to only the upper left 3x3 submatrix
   * of this matrix.
   *
   * @see .set3x3
   * @return a matrix holding the result
   */
  fun normal(): Matrix4f {
    return normal(thisOrNew())
  }

  /**
   * Compute a normal matrix from the upper left 3x3 submatrix of `this`
   * and store it into the upper left 3x3 submatrix of `dest`.
   * All other values of `dest` will be set to [identity][.identity].
   *
   *
   * The normal matrix of <tt>m</tt> is the transpose of the inverse of <tt>m</tt>.
   *
   *
   * Please note that, if `this` is an orthogonal matrix or a matrix whose columns are orthogonal vectors,
   * then this method *need not* be invoked, since in that case `this` itself is its normal matrix.
   * In that case, use [.set3x3] to set a given Matrix4f to only the upper left 3x3 submatrix
   * of this matrix.
   *
   * @see .set3x3
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun normal(dest: Matrix4f): Matrix4f {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return dest.identity()
    else if (properties and Matrix4fc.PROPERTY_ORTHONORMAL != 0)
      return normalOrthonormal(dest)
    return normalGeneric(dest)
  }

  private fun normalOrthonormal(dest: Matrix4f): Matrix4f {
    if (dest !== this)
      dest.set(this)
    dest._properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return dest
  }

  private fun normalGeneric(dest: Matrix4f): Matrix4f {
    val m00m11 = m00 * m11
    val m01m10 = m01 * m10
    val m02m10 = m02 * m10
    val m00m12 = m00 * m12
    val m01m12 = m01 * m12
    val m02m11 = m02 * m11
    val det = (m00m11 - m01m10) * m22 + (m02m10 - m00m12) * m21 + (m01m12 - m02m11) * m20
    val s = 1.0f / det
    /* Invert and transpose in one go */
    val nm00 = (m11 * m22 - m21 * m12) * s
    val nm01 = (m20 * m12 - m10 * m22) * s
    val nm02 = (m10 * m21 - m20 * m11) * s
    val nm10 = (m21 * m02 - m01 * m22) * s
    val nm11 = (m00 * m22 - m20 * m02) * s
    val nm12 = (m20 * m01 - m00 * m21) * s
    val nm20 = (m01m12 - m02m11) * s
    val nm21 = (m02m10 - m00m12) * s
    val nm22 = (m00m11 - m01m10) * s
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(0.0f)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(0.0f)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(0.0f)
    dest._m30(0.0f)
    dest._m31(0.0f)
    dest._m32(0.0f)
    dest._m33(1.0f)
    dest._properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return dest
  }

  /**
   * Compute a normal matrix from the upper left 3x3 submatrix of `this`
   * and store it into `dest`.
   *
   *
   * The normal matrix of <tt>m</tt> is the transpose of the inverse of <tt>m</tt>.
   *
   *
   * Please note that, if `this` is an orthogonal matrix or a matrix whose columns are orthogonal vectors,
   * then this method *need not* be invoked, since in that case `this` itself is its normal matrix.
   * In that case, use [Matrix3f.set] to set a given Matrix3f to only the upper left 3x3 submatrix
   * of this matrix.
   *
   * @see Matrix3f.set
   * @see .get3x3
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun normal(dest: Matrix3f): Matrix3f {
    return if (properties and Matrix4fc.PROPERTY_ORTHONORMAL != 0) normalOrthonormal(dest) else normalGeneric(dest)
  }

  private fun normalOrthonormal(dest: Matrix3f): Matrix3f {
    dest.set(this)
    return dest
  }

  private fun normalGeneric(dest: Matrix3f): Matrix3f {
    val m00m11 = m00 * m11
    val m01m10 = m01 * m10
    val m02m10 = m02 * m10
    val m00m12 = m00 * m12
    val m01m12 = m01 * m12
    val m02m11 = m02 * m11
    val det = (m00m11 - m01m10) * m22 + (m02m10 - m00m12) * m21 + (m01m12 - m02m11) * m20
    val s = 1.0f / det
    /* Invert and transpose in one go */
    dest.m00((m11 * m22 - m21 * m12) * s)
    dest.m01((m20 * m12 - m10 * m22) * s)
    dest.m02((m10 * m21 - m20 * m11) * s)
    dest.m10((m21 * m02 - m01 * m22) * s)
    dest.m11((m00 * m22 - m20 * m02) * s)
    dest.m12((m20 * m01 - m00 * m21) * s)
    dest.m20((m01m12 - m02m11) * s)
    dest.m21((m02m10 - m00m12) * s)
    dest.m22((m00m11 - m01m10) * s)
    return dest
  }

  /**
   * Normalize the upper left 3x3 submatrix of this matrix.
   *
   *
   * The resulting matrix will map unit vectors to unit vectors, though a pair of orthogonal input unit
   * vectors need not be mapped to a pair of orthogonal output vectors if the original matrix was not orthogonal itself
   * (i.e. had *skewing*).
   *
   * @return a matrix holding the result
   */
  fun normalize3x3(): Matrix4f {
    return normalize3x3(thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#normalize3x3(Matrix4f)
     */
  override fun normalize3x3(dest: Matrix4f): Matrix4f {
    val invXlen = (1.0 / Math.sqrt((m00 * m00 + m01 * m01 + m02 * m02).toDouble())).toFloat()
    val invYlen = (1.0 / Math.sqrt((m10 * m10 + m11 * m11 + m12 * m12).toDouble())).toFloat()
    val invZlen = (1.0 / Math.sqrt((m20 * m20 + m21 * m21 + m22 * m22).toDouble())).toFloat()
    dest._m00(m00 * invXlen)
    dest._m01(m01 * invXlen)
    dest._m02(m02 * invXlen)
    dest._m10(m10 * invYlen)
    dest._m11(m11 * invYlen)
    dest._m12(m12 * invYlen)
    dest._m20(m20 * invZlen)
    dest._m21(m21 * invZlen)
    dest._m22(m22 * invZlen)
    dest._properties(properties)
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#normalize3x3(org.joml.Matrix3f)
     */
  override fun normalize3x3(dest: Matrix3f): Matrix3f {
    val invXlen = (1.0 / Math.sqrt((m00 * m00 + m01 * m01 + m02 * m02).toDouble())).toFloat()
    val invYlen = (1.0 / Math.sqrt((m10 * m10 + m11 * m11 + m12 * m12).toDouble())).toFloat()
    val invZlen = (1.0 / Math.sqrt((m20 * m20 + m21 * m21 + m22 * m22).toDouble())).toFloat()
    dest.m00(m00 * invXlen)
    dest.m01(m01 * invXlen)
    dest.m02(m02 * invXlen)
    dest.m10(m10 * invYlen)
    dest.m11(m11 * invYlen)
    dest.m12(m12 * invYlen)
    dest.m20(m20 * invZlen)
    dest.m21(m21 * invZlen)
    dest.m22(m22 * invZlen)
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#frustumPlane(int, Vector4f)
     */
  override fun frustumPlane(plane: Int, planeEquation: Vector4f): Vector4f {
    when (plane) {
      Matrix4fc.PLANE_NX -> planeEquation.set(m03 + m00, m13 + m10, m23 + m20, m33 + m30).normalize3(planeEquation)
      Matrix4fc.PLANE_PX -> planeEquation.set(m03 - m00, m13 - m10, m23 - m20, m33 - m30).normalize3(planeEquation)
      Matrix4fc.PLANE_NY -> planeEquation.set(m03 + m01, m13 + m11, m23 + m21, m33 + m31).normalize3(planeEquation)
      Matrix4fc.PLANE_PY -> planeEquation.set(m03 - m01, m13 - m11, m23 - m21, m33 - m31).normalize3(planeEquation)
      Matrix4fc.PLANE_NZ -> planeEquation.set(m03 + m02, m13 + m12, m23 + m22, m33 + m32).normalize3(planeEquation)
      Matrix4fc.PLANE_PZ -> planeEquation.set(m03 - m02, m13 - m12, m23 - m22, m33 - m32).normalize3(planeEquation)
      else -> throw IllegalArgumentException("plane") //$NON-NLS-1$
    }
    return planeEquation
  }

  /* (non-Javadoc)
     * @see Matrix4fc#frustumPlane(int, org.joml.Planef)
     */
  override fun frustumPlane(which: Int, plane: Planef): Planef {
    when (which) {
      Matrix4fc.PLANE_NX -> plane.set(m03 + m00, m13 + m10, m23 + m20, m33 + m30).normalize(plane)
      Matrix4fc.PLANE_PX -> plane.set(m03 - m00, m13 - m10, m23 - m20, m33 - m30).normalize(plane)
      Matrix4fc.PLANE_NY -> plane.set(m03 + m01, m13 + m11, m23 + m21, m33 + m31).normalize(plane)
      Matrix4fc.PLANE_PY -> plane.set(m03 - m01, m13 - m11, m23 - m21, m33 - m31).normalize(plane)
      Matrix4fc.PLANE_NZ -> plane.set(m03 + m02, m13 + m12, m23 + m22, m33 + m32).normalize(plane)
      Matrix4fc.PLANE_PZ -> plane.set(m03 - m02, m13 - m12, m23 - m22, m33 - m32).normalize(plane)
      else -> throw IllegalArgumentException("which") //$NON-NLS-1$
    }
    return plane
  }

  /* (non-Javadoc)
     * @see Matrix4fc#frustumCorner(int, Vector3m)
     */
  override fun frustumCorner(corner: Int, point: Vector3m): Vector3m {
    val d1: Float
    val d2: Float
    val d3: Float
    val n1x: Float
    val n1y: Float
    val n1z: Float
    val n2x: Float
    val n2y: Float
    val n2z: Float
    val n3x: Float
    val n3y: Float
    val n3z: Float
    when (corner) {
      Matrix4fc.CORNER_NXNYNZ // left, bottom, near
      -> {
        n1x = m03 + m00
        n1y = m13 + m10
        n1z = m23 + m20
        d1 = m33 + m30 // left
        n2x = m03 + m01
        n2y = m13 + m11
        n2z = m23 + m21
        d2 = m33 + m31 // bottom
        n3x = m03 + m02
        n3y = m13 + m12
        n3z = m23 + m22
        d3 = m33 + m32 // near
      }
      Matrix4fc.CORNER_PXNYNZ // right, bottom, near
      -> {
        n1x = m03 - m00
        n1y = m13 - m10
        n1z = m23 - m20
        d1 = m33 - m30 // right
        n2x = m03 + m01
        n2y = m13 + m11
        n2z = m23 + m21
        d2 = m33 + m31 // bottom
        n3x = m03 + m02
        n3y = m13 + m12
        n3z = m23 + m22
        d3 = m33 + m32 // near
      }
      Matrix4fc.CORNER_PXPYNZ // right, top, near
      -> {
        n1x = m03 - m00
        n1y = m13 - m10
        n1z = m23 - m20
        d1 = m33 - m30 // right
        n2x = m03 - m01
        n2y = m13 - m11
        n2z = m23 - m21
        d2 = m33 - m31 // top
        n3x = m03 + m02
        n3y = m13 + m12
        n3z = m23 + m22
        d3 = m33 + m32 // near
      }
      Matrix4fc.CORNER_NXPYNZ // left, top, near
      -> {
        n1x = m03 + m00
        n1y = m13 + m10
        n1z = m23 + m20
        d1 = m33 + m30 // left
        n2x = m03 - m01
        n2y = m13 - m11
        n2z = m23 - m21
        d2 = m33 - m31 // top
        n3x = m03 + m02
        n3y = m13 + m12
        n3z = m23 + m22
        d3 = m33 + m32 // near
      }
      Matrix4fc.CORNER_PXNYPZ // right, bottom, far
      -> {
        n1x = m03 - m00
        n1y = m13 - m10
        n1z = m23 - m20
        d1 = m33 - m30 // right
        n2x = m03 + m01
        n2y = m13 + m11
        n2z = m23 + m21
        d2 = m33 + m31 // bottom
        n3x = m03 - m02
        n3y = m13 - m12
        n3z = m23 - m22
        d3 = m33 - m32 // far
      }
      Matrix4fc.CORNER_NXNYPZ // left, bottom, far
      -> {
        n1x = m03 + m00
        n1y = m13 + m10
        n1z = m23 + m20
        d1 = m33 + m30 // left
        n2x = m03 + m01
        n2y = m13 + m11
        n2z = m23 + m21
        d2 = m33 + m31 // bottom
        n3x = m03 - m02
        n3y = m13 - m12
        n3z = m23 - m22
        d3 = m33 - m32 // far
      }
      Matrix4fc.CORNER_NXPYPZ // left, top, far
      -> {
        n1x = m03 + m00
        n1y = m13 + m10
        n1z = m23 + m20
        d1 = m33 + m30 // left
        n2x = m03 - m01
        n2y = m13 - m11
        n2z = m23 - m21
        d2 = m33 - m31 // top
        n3x = m03 - m02
        n3y = m13 - m12
        n3z = m23 - m22
        d3 = m33 - m32 // far
      }
      Matrix4fc.CORNER_PXPYPZ // right, top, far
      -> {
        n1x = m03 - m00
        n1y = m13 - m10
        n1z = m23 - m20
        d1 = m33 - m30 // right
        n2x = m03 - m01
        n2y = m13 - m11
        n2z = m23 - m21
        d2 = m33 - m31 // top
        n3x = m03 - m02
        n3y = m13 - m12
        n3z = m23 - m22
        d3 = m33 - m32 // far
      }
      else -> throw IllegalArgumentException("corner") //$NON-NLS-1$
    }
    val c23x: Float
    val c23y: Float
    val c23z: Float
    c23x = n2y * n3z - n2z * n3y
    c23y = n2z * n3x - n2x * n3z
    c23z = n2x * n3y - n2y * n3x
    val c31x: Float
    val c31y: Float
    val c31z: Float
    c31x = n3y * n1z - n3z * n1y
    c31y = n3z * n1x - n3x * n1z
    c31z = n3x * n1y - n3y * n1x
    val c12x: Float
    val c12y: Float
    val c12z: Float
    c12x = n1y * n2z - n1z * n2y
    c12y = n1z * n2x - n1x * n2z
    c12z = n1x * n2y - n1y * n2x
    val invDot = 1.0f / (n1x * c23x + n1y * c23y + n1z * c23z)
    point.x = (-c23x * d1 - c31x * d2 - c12x * d3) * invDot
    point.y = (-c23y * d1 - c31y * d2 - c12y * d3) * invDot
    point.z = (-c23z * d1 - c31z * d2 - c12z * d3) * invDot
    return point
  }

  /**
   * Compute the eye/origin of the perspective frustum transformation defined by `this` matrix,
   * which can be a projection matrix or a combined modelview-projection matrix, and store the result
   * in the given `origin`.
   *
   *
   * Note that this method will only work using perspective projections obtained via one of the
   * perspective methods, such as [perspective()][.perspective]
   * or [frustum()][.frustum].
   *
   *
   * Generally, this method computes the origin in the local frame of
   * any coordinate system that existed before `this`
   * transformation was applied to it in order to yield homogeneous clipping space.
   *
   *
   * Reference: [http://geomalgorithms.com](http://geomalgorithms.com/a05-_intersect-1.html)
   *
   *
   * Reference: [
 * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix](http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf)
   *
   * @param origin
   * will hold the origin of the coordinate system before applying `this`
   * perspective projection transformation
   * @return origin
   */
  override fun perspectiveOrigin(origin: Vector3m): Vector3m {
    /*
         * Simply compute the intersection point of the left, right and top frustum plane.
         */
    val d1: Float
    val d2: Float
    val d3: Float
    val n1x: Float
    val n1y: Float
    val n1z: Float
    val n2x: Float
    val n2y: Float
    val n2z: Float
    val n3x: Float
    val n3y: Float
    val n3z: Float
    n1x = m03 + m00
    n1y = m13 + m10
    n1z = m23 + m20
    d1 = m33 + m30 // left
    n2x = m03 - m00
    n2y = m13 - m10
    n2z = m23 - m20
    d2 = m33 - m30 // right
    n3x = m03 - m01
    n3y = m13 - m11
    n3z = m23 - m21
    d3 = m33 - m31 // top
    val c23x: Float
    val c23y: Float
    val c23z: Float
    c23x = n2y * n3z - n2z * n3y
    c23y = n2z * n3x - n2x * n3z
    c23z = n2x * n3y - n2y * n3x
    val c31x: Float
    val c31y: Float
    val c31z: Float
    c31x = n3y * n1z - n3z * n1y
    c31y = n3z * n1x - n3x * n1z
    c31z = n3x * n1y - n3y * n1x
    val c12x: Float
    val c12y: Float
    val c12z: Float
    c12x = n1y * n2z - n1z * n2y
    c12y = n1z * n2x - n1x * n2z
    c12z = n1x * n2y - n1y * n2x
    val invDot = 1.0f / (n1x * c23x + n1y * c23y + n1z * c23z)
    origin.x = (-c23x * d1 - c31x * d2 - c12x * d3) * invDot
    origin.y = (-c23y * d1 - c31y * d2 - c12y * d3) * invDot
    origin.z = (-c23z * d1 - c31z * d2 - c12z * d3) * invDot
    return origin
  }

  /**
   * Return the vertical field-of-view angle in radians of this perspective transformation matrix.
   *
   *
   * Note that this method will only work using perspective projections obtained via one of the
   * perspective methods, such as [perspective()][.perspective]
   * or [frustum()][.frustum].
   *
   *
   * For orthogonal transformations this method will return <tt>0.0</tt>.
   *
   *
   * Reference: [
 * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix](http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf)
   *
   * @return the vertical field-of-view angle in radians
   */
  override fun perspectiveFov(): Float {
    /*
         * Compute the angle between the bottom and top frustum plane normals.
         */
    val n1x: Float
    val n1y: Float
    val n1z: Float
    val n2x: Float
    val n2y: Float
    val n2z: Float
    n1x = m03 + m01
    n1y = m13 + m11
    n1z = m23 + m21 // bottom
    n2x = m01 - m03
    n2y = m11 - m13
    n2z = m21 - m23 // top
    val n1len = Math.sqrt((n1x * n1x + n1y * n1y + n1z * n1z).toDouble()).toFloat()
    val n2len = Math.sqrt((n2x * n2x + n2y * n2y + n2z * n2z).toDouble()).toFloat()
    return Math.acos(((n1x * n2x + n1y * n2y + n1z * n2z) / (n1len * n2len)).toDouble()).toFloat()
  }

  /**
   * Extract the near clip plane distance from `this` perspective projection matrix.
   *
   *
   * This method only works if `this` is a perspective projection matrix, for example obtained via [.perspective].
   *
   * @return the near clip plane distance
   */
  override fun perspectiveNear(): Float {
    return m32 / (m23 + m22)
  }

  /**
   * Extract the far clip plane distance from `this` perspective projection matrix.
   *
   *
   * This method only works if `this` is a perspective projection matrix, for example obtained via [.perspective].
   *
   * @return the far clip plane distance
   */
  override fun perspectiveFar(): Float {
    return m32 / (m22 - m23)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#frustumRayDir(float, float, Vector3m)
     */
  override fun frustumRayDir(x: Float, y: Float, dir: Vector3m): Vector3m {
    /*
         * This method works by first obtaining the frustum plane normals,
         * then building the cross product to obtain the corner rays,
         * and finally bilinearly interpolating to obtain the desired direction.
         * The code below uses a condense form of doing all this making use
         * of some mathematical identities to simplify the overall expression.
         */
    val a = m10 * m23
    val b = m13 * m21
    val c = m10 * m21
    val d = m11 * m23
    val e = m13 * m20
    val f = m11 * m20
    val g = m03 * m20
    val h = m01 * m23
    val i = m01 * m20
    val j = m03 * m21
    val k = m00 * m23
    val l = m00 * m21
    val m = m00 * m13
    val n = m03 * m11
    val o = m00 * m11
    val p = m01 * m13
    val q = m03 * m10
    val r = m01 * m10
    val m1x: Float
    val m1y: Float
    val m1z: Float
    m1x = (d + e + f - a - b - c) * (1.0f - y) + (a - b - c + d - e + f) * y
    m1y = (j + k + l - g - h - i) * (1.0f - y) + (g - h - i + j - k + l) * y
    m1z = (p + q + r - m - n - o) * (1.0f - y) + (m - n - o + p - q + r) * y
    val m2x: Float
    val m2y: Float
    val m2z: Float
    m2x = (b - c - d + e + f - a) * (1.0f - y) + (a + b - c - d - e + f) * y
    m2y = (h - i - j + k + l - g) * (1.0f - y) + (g + h - i - j - k + l) * y
    m2z = (n - o - p + q + r - m) * (1.0f - y) + (m + n - o - p - q + r) * y
    dir.x = m1x + (m2x - m1x) * x
    dir.y = m1y + (m2y - m1y) * x
    dir.z = m1z + (m2z - m1z) * x
    return dir.normalize(dir)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#positiveZ(Vector3m)
     */
  override fun positiveZ(dir: Vector3m): Vector3m {
    dir.x = m10 * m21 - m11 * m20
    dir.y = m20 * m01 - m21 * m00
    dir.z = m00 * m11 - m01 * m10
    return dir.normalize(dir)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#normalizedPositiveZ(Vector3m)
     */
  override fun normalizedPositiveZ(dir: Vector3m): Vector3m {
    dir.x = m02
    dir.y = m12
    dir.z = m22
    return dir
  }

  /* (non-Javadoc)
     * @see Matrix4fc#positiveX(Vector3m)
     */
  override fun positiveX(dir: Vector3m): Vector3m {
    dir.x = m11 * m22 - m12 * m21
    dir.y = m02 * m21 - m01 * m22
    dir.z = m01 * m12 - m02 * m11
    return dir.normalize(dir)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#normalizedPositiveX(Vector3m)
     */
  override fun normalizedPositiveX(dir: Vector3m): Vector3m {
    dir.x = m00
    dir.y = m10
    dir.z = m20
    return dir
  }

  /* (non-Javadoc)
     * @see Matrix4fc#positiveY(Vector3m)
     */
  override fun positiveY(dir: Vector3m): Vector3m {
    dir.x = m12 * m20 - m10 * m22
    dir.y = m00 * m22 - m02 * m20
    dir.z = m02 * m10 - m00 * m12
    return dir.normalize(dir)
  }

  /* (non-Javadoc)
     * @see Matrix4fc#normalizedPositiveY(Vector3m)
     */
  override fun normalizedPositiveY(dir: Vector3m): Vector3m {
    dir.x = m01
    dir.y = m11
    dir.z = m21
    return dir
  }

  /* (non-Javadoc)
     * @see Matrix4fc#originAffine(Vector3m)
     */
  override fun originAffine(origin: Vector3m): Vector3m {
    val a = m00 * m11 - m01 * m10
    val b = m00 * m12 - m02 * m10
    val d = m01 * m12 - m02 * m11
    val g = m20 * m31 - m21 * m30
    val h = m20 * m32 - m22 * m30
    val j = m21 * m32 - m22 * m31
    origin.x = -m10 * j + m11 * h - m12 * g
    origin.y = m00 * j - m01 * h + m02 * g
    origin.z = -m30 * d + m31 * b - m32 * a
    return origin
  }

  /* (non-Javadoc)
     * @see Matrix4fc#origin(Vector3m)
     */
  override fun origin(dest: Vector3m): Vector3m {
    return if (properties and Matrix4fc.PROPERTY_AFFINE != 0) originAffine(dest) else originGeneric(dest)
  }

  private fun originGeneric(dest: Vector3m): Vector3m {
    val a = m00 * m11 - m01 * m10
    val b = m00 * m12 - m02 * m10
    val c = m00 * m13 - m03 * m10
    val d = m01 * m12 - m02 * m11
    val e = m01 * m13 - m03 * m11
    val f = m02 * m13 - m03 * m12
    val g = m20 * m31 - m21 * m30
    val h = m20 * m32 - m22 * m30
    val i = m20 * m33 - m23 * m30
    val j = m21 * m32 - m22 * m31
    val k = m21 * m33 - m23 * m31
    val l = m22 * m33 - m23 * m32
    val det = a * l - b * k + c * j + d * i - e * h + f * g
    val invDet = 1.0f / det
    val nm30 = (-m10 * j + m11 * h - m12 * g) * invDet
    val nm31 = (m00 * j - m01 * h + m02 * g) * invDet
    val nm32 = (-m30 * d + m31 * b - m32 * a) * invDet
    val nm33 = det / (m20 * d - m21 * b + m22 * a)
    val x = nm30 * nm33
    val y = nm31 * nm33
    val z = nm32 * nm33
    return dest.set(x, y, z)
  }

  /**
   * Apply a projection transformation to this matrix that projects onto the plane specified via the general plane equation
   * <tt>x*a + y*b + z*c + d = 0</tt> as if casting a shadow from a given light position/direction `light`.
   *
   *
   * If <tt>light.w</tt> is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
   *
   *
   * If `M` is `this` matrix and `S` the shadow matrix,
   * then the new matrix will be `M * S`. So when transforming a
   * vector `v` with the new matrix by using `M * S * v`, the
   * reflection will be applied first!
   *
   *
   * Reference: [ftp.sgi.com](ftp://ftp.sgi.com/opengl/contrib/blythe/advanced99/notes/node192.html)
   *
   * @param light
   * the light's vector
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @return a matrix holding the result
   */
  fun shadow(light: Vector4f, a: Float, b: Float, c: Float, d: Float): Matrix4f {
    return shadow(light.x, light.y, light.z, light.w, a, b, c, d, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#shadow(Vector4f, float, float, float, float, Matrix4f)
     */
  override fun shadow(light: Vector4f, a: Float, b: Float, c: Float, d: Float, dest: Matrix4f): Matrix4f {
    return shadow(light.x, light.y, light.z, light.w, a, b, c, d, dest)
  }

  /**
   * Apply a projection transformation to this matrix that projects onto the plane specified via the general plane equation
   * <tt>x*a + y*b + z*c + d = 0</tt> as if casting a shadow from a given light position/direction <tt>(lightX, lightY, lightZ, lightW)</tt>.
   *
   *
   * If `lightW` is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
   *
   *
   * If `M` is `this` matrix and `S` the shadow matrix,
   * then the new matrix will be `M * S`. So when transforming a
   * vector `v` with the new matrix by using `M * S * v`, the
   * reflection will be applied first!
   *
   *
   * Reference: [ftp.sgi.com](ftp://ftp.sgi.com/opengl/contrib/blythe/advanced99/notes/node192.html)
   *
   * @param lightX
   * the x-component of the light's vector
   * @param lightY
   * the y-component of the light's vector
   * @param lightZ
   * the z-component of the light's vector
   * @param lightW
   * the w-component of the light's vector
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @return a matrix holding the result
   */
  fun shadow(lightX: Float, lightY: Float, lightZ: Float, lightW: Float, a: Float, b: Float, c: Float, d: Float): Matrix4f {
    return shadow(lightX, lightY, lightZ, lightW, a, b, c, d, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#shadow(float, float, float, float, float, float, float, float, Matrix4f)
     */
  override fun shadow(lightX: Float, lightY: Float, lightZ: Float, lightW: Float, a: Float, b: Float, c: Float, d: Float, dest: Matrix4f): Matrix4f {
    // normalize plane
    val invPlaneLen = (1.0 / Math.sqrt((a * a + b * b + c * c).toDouble())).toFloat()
    val an = a * invPlaneLen
    val bn = b * invPlaneLen
    val cn = c * invPlaneLen
    val dn = d * invPlaneLen

    val dot = an * lightX + bn * lightY + cn * lightZ + dn * lightW

    // compute right matrix elements
    val rm00 = dot - an * lightX
    val rm01 = -an * lightY
    val rm02 = -an * lightZ
    val rm03 = -an * lightW
    val rm10 = -bn * lightX
    val rm11 = dot - bn * lightY
    val rm12 = -bn * lightZ
    val rm13 = -bn * lightW
    val rm20 = -cn * lightX
    val rm21 = -cn * lightY
    val rm22 = dot - cn * lightZ
    val rm23 = -cn * lightW
    val rm30 = -dn * lightX
    val rm31 = -dn * lightY
    val rm32 = -dn * lightZ
    val rm33 = dot - dn * lightW

    // matrix multiplication
    val nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02 + m30 * rm03
    val nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02 + m31 * rm03
    val nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02 + m32 * rm03
    val nm03 = m03 * rm00 + m13 * rm01 + m23 * rm02 + m33 * rm03
    val nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12 + m30 * rm13
    val nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12 + m31 * rm13
    val nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12 + m32 * rm13
    val nm13 = m03 * rm10 + m13 * rm11 + m23 * rm12 + m33 * rm13
    val nm20 = m00 * rm20 + m10 * rm21 + m20 * rm22 + m30 * rm23
    val nm21 = m01 * rm20 + m11 * rm21 + m21 * rm22 + m31 * rm23
    val nm22 = m02 * rm20 + m12 * rm21 + m22 * rm22 + m32 * rm23
    val nm23 = m03 * rm20 + m13 * rm21 + m23 * rm22 + m33 * rm23
    dest._m30(m00 * rm30 + m10 * rm31 + m20 * rm32 + m30 * rm33)
    dest._m31(m01 * rm30 + m11 * rm31 + m21 * rm32 + m31 * rm33)
    dest._m32(m02 * rm30 + m12 * rm31 + m22 * rm32 + m32 * rm33)
    dest._m33(m03 * rm30 + m13 * rm31 + m23 * rm32 + m33 * rm33)
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())

    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#shadow(Vector4f, Matrix4fc, Matrix4f)
     */
  override fun shadow(light: Vector4f, planeTransform: Matrix4fc, dest: Matrix4f): Matrix4f {
    // compute plane equation by transforming (y = 0)
    val a = planeTransform.m10()
    val b = planeTransform.m11()
    val c = planeTransform.m12()
    val d = -a * planeTransform.m30() - b * planeTransform.m31() - c * planeTransform.m32()
    return shadow(light.x, light.y, light.z, light.w, a, b, c, d, dest)
  }

  /**
   * Apply a projection transformation to this matrix that projects onto the plane with the general plane equation
   * <tt>y = 0</tt> as if casting a shadow from a given light position/direction `light`.
   *
   *
   * Before the shadow projection is applied, the plane is transformed via the specified `planeTransformation`.
   *
   *
   * If <tt>light.w</tt> is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
   *
   *
   * If `M` is `this` matrix and `S` the shadow matrix,
   * then the new matrix will be `M * S`. So when transforming a
   * vector `v` with the new matrix by using `M * S * v`, the
   * reflection will be applied first!
   *
   * @param light
   * the light's vector
   * @param planeTransform
   * the transformation to transform the implied plane <tt>y = 0</tt> before applying the projection
   * @return a matrix holding the result
   */
  fun shadow(light: Vector4f, planeTransform: Matrix4f): Matrix4f {
    return shadow(light, planeTransform, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#shadow(float, float, float, float, Matrix4fc, Matrix4f)
     */
  override fun shadow(lightX: Float, lightY: Float, lightZ: Float, lightW: Float, planeTransform: Matrix4fc, dest: Matrix4f): Matrix4f {
    // compute plane equation by transforming (y = 0)
    val a = planeTransform.m10()
    val b = planeTransform.m11()
    val c = planeTransform.m12()
    val d = -a * planeTransform.m30() - b * planeTransform.m31() - c * planeTransform.m32()
    return shadow(lightX, lightY, lightZ, lightW, a, b, c, d, dest)
  }

  /**
   * Apply a projection transformation to this matrix that projects onto the plane with the general plane equation
   * <tt>y = 0</tt> as if casting a shadow from a given light position/direction <tt>(lightX, lightY, lightZ, lightW)</tt>.
   *
   *
   * Before the shadow projection is applied, the plane is transformed via the specified `planeTransformation`.
   *
   *
   * If `lightW` is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
   *
   *
   * If `M` is `this` matrix and `S` the shadow matrix,
   * then the new matrix will be `M * S`. So when transforming a
   * vector `v` with the new matrix by using `M * S * v`, the
   * reflection will be applied first!
   *
   * @param lightX
   * the x-component of the light vector
   * @param lightY
   * the y-component of the light vector
   * @param lightZ
   * the z-component of the light vector
   * @param lightW
   * the w-component of the light vector
   * @param planeTransform
   * the transformation to transform the implied plane <tt>y = 0</tt> before applying the projection
   * @return a matrix holding the result
   */
  fun shadow(lightX: Float, lightY: Float, lightZ: Float, lightW: Float, planeTransform: Matrix4f): Matrix4f {
    return shadow(lightX, lightY, lightZ, lightW, planeTransform, thisOrNew())
  }

  /**
   * Set this matrix to a cylindrical billboard transformation that rotates the local +Z axis of a given object with position `objPos` towards
   * a target position at `targetPos` while constraining a cylindrical rotation around the given `up` vector.
   *
   *
   * This method can be used to create the complete model transformation for a given object, including the translation of the object to
   * its position `objPos`.
   *
   * @param objPos
   * the position of the object to rotate towards `targetPos`
   * @param targetPos
   * the position of the target (for example the camera) towards which to rotate the object
   * @param up
   * the rotation axis (must be [normalized][Vector3m.normalize])
   * @return this
   */
  fun billboardCylindrical(objPos: Vector3, targetPos: Vector3, up: Vector3): Matrix4f {
    var dirX = targetPos.x - objPos.x
    var dirY = targetPos.y - objPos.y
    var dirZ = targetPos.z - objPos.z
    // left = up x dir
    var leftX = up.y * dirZ - up.z * dirY
    var leftY = up.z * dirX - up.x * dirZ
    var leftZ = up.x * dirY - up.y * dirX
    // normalize left
    val invLeftLen = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLen
    leftY *= invLeftLen
    leftZ *= invLeftLen
    // recompute dir by constraining rotation around 'up'
    // dir = left x up
    dirX = leftY * up.z - leftZ * up.y
    dirY = leftZ * up.x - leftX * up.z
    dirZ = leftX * up.y - leftY * up.x
    // normalize dir
    val invDirLen = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    dirX *= invDirLen
    dirY *= invDirLen
    dirZ *= invDirLen
    // set matrix elements
    this._m00(leftX)
    this._m01(leftY)
    this._m02(leftZ)
    this._m03(0.0f)
    this._m10(up.x)
    this._m11(up.y)
    this._m12(up.z)
    this._m13(0.0f)
    this._m20(dirX)
    this._m21(dirY)
    this._m22(dirZ)
    this._m23(0.0f)
    this._m30(objPos.x)
    this._m31(objPos.y)
    this._m32(objPos.z)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to a spherical billboard transformation that rotates the local +Z axis of a given object with position `objPos` towards
   * a target position at `targetPos`.
   *
   *
   * This method can be used to create the complete model transformation for a given object, including the translation of the object to
   * its position `objPos`.
   *
   *
   * If preserving an *up* vector is not necessary when rotating the +Z axis, then a shortest arc rotation can be obtained
   * using [.billboardSpherical].
   *
   * @see .billboardSpherical
   * @param objPos
   * the position of the object to rotate towards `targetPos`
   * @param targetPos
   * the position of the target (for example the camera) towards which to rotate the object
   * @param up
   * the up axis used to orient the object
   * @return this
   */
  fun billboardSpherical(objPos: Vector3, targetPos: Vector3, up: Vector3): Matrix4f {
    var dirX = targetPos.x - objPos.x
    var dirY = targetPos.y - objPos.y
    var dirZ = targetPos.z - objPos.z
    // normalize dir
    val invDirLen = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    dirX *= invDirLen
    dirY *= invDirLen
    dirZ *= invDirLen
    // left = up x dir
    var leftX = up.y * dirZ - up.z * dirY
    var leftY = up.z * dirX - up.x * dirZ
    var leftZ = up.x * dirY - up.y * dirX
    // normalize left
    val invLeftLen = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLen
    leftY *= invLeftLen
    leftZ *= invLeftLen
    // up = dir x left
    val upX = dirY * leftZ - dirZ * leftY
    val upY = dirZ * leftX - dirX * leftZ
    val upZ = dirX * leftY - dirY * leftX
    // set matrix elements
    this._m00(leftX)
    this._m01(leftY)
    this._m02(leftZ)
    this._m03(0.0f)
    this._m10(upX)
    this._m11(upY)
    this._m12(upZ)
    this._m13(0.0f)
    this._m20(dirX)
    this._m21(dirY)
    this._m22(dirZ)
    this._m23(0.0f)
    this._m30(objPos.x)
    this._m31(objPos.y)
    this._m32(objPos.z)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to a spherical billboard transformation that rotates the local +Z axis of a given object with position `objPos` towards
   * a target position at `targetPos` using a shortest arc rotation by not preserving any *up* vector of the object.
   *
   *
   * This method can be used to create the complete model transformation for a given object, including the translation of the object to
   * its position `objPos`.
   *
   *
   * In order to specify an *up* vector which needs to be maintained when rotating the +Z axis of the object,
   * use [.billboardSpherical].
   *
   * @see .billboardSpherical
   * @param objPos
   * the position of the object to rotate towards `targetPos`
   * @param targetPos
   * the position of the target (for example the camera) towards which to rotate the object
   * @return this
   */
  fun billboardSpherical(objPos: Vector3, targetPos: Vector3): Matrix4f {
    val toDirX = targetPos.x - objPos.x
    val toDirY = targetPos.y - objPos.y
    val toDirZ = targetPos.z - objPos.z
    var x = -toDirY
    var y = toDirX
    var w = Math.sqrt((toDirX * toDirX + toDirY * toDirY + toDirZ * toDirZ).toDouble()).toFloat() + toDirZ
    val invNorm = (1.0 / Math.sqrt((x * x + y * y + w * w).toDouble())).toFloat()
    x *= invNorm
    y *= invNorm
    w *= invNorm
    val q00 = (x + x) * x
    val q11 = (y + y) * y
    val q01 = (x + x) * y
    val q03 = (x + x) * w
    val q13 = (y + y) * w
    this._m00(1.0f - q11)
    this._m01(q01)
    this._m02(-q13)
    this._m03(0.0f)
    this._m10(q01)
    this._m11(1.0f - q00)
    this._m12(q03)
    this._m13(0.0f)
    this._m20(q13)
    this._m21(-q03)
    this._m22(1.0f - q11 - q00)
    this._m23(0.0f)
    this._m30(objPos.x)
    this._m31(objPos.y)
    this._m32(objPos.z)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  override fun hashCode(): Int {
    val prime = 31
    var result = 1
    result = prime * result + java.lang.Float.floatToIntBits(m00)
    result = prime * result + java.lang.Float.floatToIntBits(m01)
    result = prime * result + java.lang.Float.floatToIntBits(m02)
    result = prime * result + java.lang.Float.floatToIntBits(m03)
    result = prime * result + java.lang.Float.floatToIntBits(m10)
    result = prime * result + java.lang.Float.floatToIntBits(m11)
    result = prime * result + java.lang.Float.floatToIntBits(m12)
    result = prime * result + java.lang.Float.floatToIntBits(m13)
    result = prime * result + java.lang.Float.floatToIntBits(m20)
    result = prime * result + java.lang.Float.floatToIntBits(m21)
    result = prime * result + java.lang.Float.floatToIntBits(m22)
    result = prime * result + java.lang.Float.floatToIntBits(m23)
    result = prime * result + java.lang.Float.floatToIntBits(m30)
    result = prime * result + java.lang.Float.floatToIntBits(m31)
    result = prime * result + java.lang.Float.floatToIntBits(m32)
    result = prime * result + java.lang.Float.floatToIntBits(m33)
    return result
  }

  override fun equals(obj: Any?): Boolean {
    if (this === obj)
      return true
    if (obj == null)
      return false
    if (obj !is Matrix4f)
      return false
    val other = obj as Matrix4fc?
    if (java.lang.Float.floatToIntBits(m00) != java.lang.Float.floatToIntBits(other!!.m00()))
      return false
    if (java.lang.Float.floatToIntBits(m01) != java.lang.Float.floatToIntBits(other.m01()))
      return false
    if (java.lang.Float.floatToIntBits(m02) != java.lang.Float.floatToIntBits(other.m02()))
      return false
    if (java.lang.Float.floatToIntBits(m03) != java.lang.Float.floatToIntBits(other.m03()))
      return false
    if (java.lang.Float.floatToIntBits(m10) != java.lang.Float.floatToIntBits(other.m10()))
      return false
    if (java.lang.Float.floatToIntBits(m11) != java.lang.Float.floatToIntBits(other.m11()))
      return false
    if (java.lang.Float.floatToIntBits(m12) != java.lang.Float.floatToIntBits(other.m12()))
      return false
    if (java.lang.Float.floatToIntBits(m13) != java.lang.Float.floatToIntBits(other.m13()))
      return false
    if (java.lang.Float.floatToIntBits(m20) != java.lang.Float.floatToIntBits(other.m20()))
      return false
    if (java.lang.Float.floatToIntBits(m21) != java.lang.Float.floatToIntBits(other.m21()))
      return false
    if (java.lang.Float.floatToIntBits(m22) != java.lang.Float.floatToIntBits(other.m22()))
      return false
    if (java.lang.Float.floatToIntBits(m23) != java.lang.Float.floatToIntBits(other.m23()))
      return false
    if (java.lang.Float.floatToIntBits(m30) != java.lang.Float.floatToIntBits(other.m30()))
      return false
    if (java.lang.Float.floatToIntBits(m31) != java.lang.Float.floatToIntBits(other.m31()))
      return false
    if (java.lang.Float.floatToIntBits(m32) != java.lang.Float.floatToIntBits(other.m32()))
      return false
    return if (java.lang.Float.floatToIntBits(m33) != java.lang.Float.floatToIntBits(other.m33())) false else true
  }

  /* (non-Javadoc)
     * @see Matrix4fc#pick(float, float, float, float, int[], Matrix4f)
     */
  override fun pick(x: Float, y: Float, width: Float, height: Float, viewport: IntArray, dest: Matrix4f): Matrix4f {
    val sx = viewport[2] / width
    val sy = viewport[3] / height
    val tx = (viewport[2] + 2.0f * (viewport[0] - x)) / width
    val ty = (viewport[3] + 2.0f * (viewport[1] - y)) / height
    dest._m30(m00 * tx + m10 * ty + m30)
    dest._m31(m01 * tx + m11 * ty + m31)
    dest._m32(m02 * tx + m12 * ty + m32)
    dest._m33(m03 * tx + m13 * ty + m33)
    dest._m00(m00 * sx)
    dest._m01(m01 * sx)
    dest._m02(m02 * sx)
    dest._m03(m03 * sx)
    dest._m10(m10 * sy)
    dest._m11(m11 * sy)
    dest._m12(m12 * sy)
    dest._m13(m13 * sy)
    dest._properties(0)
    return dest
  }

  /**
   * Apply a picking transformation to this matrix using the given window coordinates <tt>(x, y)</tt> as the pick center
   * and the given <tt>(width, height)</tt> as the size of the picking region in window coordinates.
   *
   * @param x
   * the x coordinate of the picking region center in window coordinates
   * @param y
   * the y coordinate of the picking region center in window coordinates
   * @param width
   * the width of the picking region in window coordinates
   * @param height
   * the height of the picking region in window coordinates
   * @param viewport
   * the viewport described by <tt>[x, y, width, height]</tt>
   * @return a matrix holding the result
   */
  fun pick(x: Float, y: Float, width: Float, height: Float, viewport: IntArray): Matrix4f {
    return pick(x, y, width, height, viewport, thisOrNew())
  }

  /**
   * Exchange the values of `this` matrix with the given `other` matrix.
   *
   * @param other
   * the other matrix to exchange the values with
   * @return this
   */
  fun swap(other: Matrix4f): Matrix4f {
    MemUtil.INSTANCE.swap(this, other)
    val props = properties
    this.properties = other.properties()
    other.properties = props
    return this
  }

  /* (non-Javadoc)
     * @see Matrix4fc#arcball(float, float, float, float, float, float, Matrix4f)
     */
  override fun arcball(radius: Float, centerX: Float, centerY: Float, centerZ: Float, angleX: Float, angleY: Float, dest: Matrix4f): Matrix4f {
    val m30 = m20 * -radius + this.m30
    val m31 = m21 * -radius + this.m31
    val m32 = m22 * -radius + this.m32
    val m33 = m23 * -radius + this.m33
    var sin = Math.sin(angleX.toDouble()).toFloat()
    var cos = Math.cosFromSin(sin.toDouble(), angleX.toDouble()).toFloat()
    val nm10 = m10 * cos + m20 * sin
    val nm11 = m11 * cos + m21 * sin
    val nm12 = m12 * cos + m22 * sin
    val nm13 = m13 * cos + m23 * sin
    val m20 = this.m20 * cos - m10 * sin
    val m21 = this.m21 * cos - m11 * sin
    val m22 = this.m22 * cos - m12 * sin
    val m23 = this.m23 * cos - m13 * sin
    sin = Math.sin(angleY.toDouble()).toFloat()
    cos = Math.cosFromSin(sin.toDouble(), angleY.toDouble()).toFloat()
    val nm00 = m00 * cos - m20 * sin
    val nm01 = m01 * cos - m21 * sin
    val nm02 = m02 * cos - m22 * sin
    val nm03 = m03 * cos - m23 * sin
    val nm20 = m00 * sin + m20 * cos
    val nm21 = m01 * sin + m21 * cos
    val nm22 = m02 * sin + m22 * cos
    val nm23 = m03 * sin + m23 * cos
    dest._m30(-nm00 * centerX - nm10 * centerY - nm20 * centerZ + m30)
    dest._m31(-nm01 * centerX - nm11 * centerY - nm21 * centerZ + m31)
    dest._m32(-nm02 * centerX - nm12 * centerY - nm22 * centerZ + m32)
    dest._m33(-nm03 * centerX - nm13 * centerY - nm23 * centerZ + m33)
    dest._m20(nm20)
    dest._m21(nm21)
    dest._m22(nm22)
    dest._m23(nm23)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /* (non-Javadoc)
     * @see Matrix4fc#arcball(float, Vector3fc, float, float, Matrix4f)
     */
  override fun arcball(radius: Float, center: Vector3fc, angleX: Float, angleY: Float, dest: Matrix4f): Matrix4f {
    return arcball(radius, center.x, center.y, center.z, angleX, angleY, dest)
  }

  /**
   * Apply an arcball view transformation to this matrix with the given `radius` and center <tt>(centerX, centerY, centerZ)</tt>
   * position of the arcball and the specified X and Y rotation angles.
   *
   *
   * This method is equivalent to calling: <tt>translate(0, 0, -radius).rotateX(angleX).rotateY(angleY).translate(-centerX, -centerY, -centerZ)</tt>
   *
   * @param radius
   * the arcball radius
   * @param centerX
   * the x coordinate of the center position of the arcball
   * @param centerY
   * the y coordinate of the center position of the arcball
   * @param centerZ
   * the z coordinate of the center position of the arcball
   * @param angleX
   * the rotation angle around the X axis in radians
   * @param angleY
   * the rotation angle around the Y axis in radians
   * @return a matrix holding the result
   */
  fun arcball(radius: Float, centerX: Float, centerY: Float, centerZ: Float, angleX: Float, angleY: Float): Matrix4f {
    return arcball(radius, centerX, centerY, centerZ, angleX, angleY, thisOrNew())
  }

  /**
   * Apply an arcball view transformation to this matrix with the given `radius` and `center`
   * position of the arcball and the specified X and Y rotation angles.
   *
   *
   * This method is equivalent to calling: <tt>translate(0, 0, -radius).rotateX(angleX).rotateY(angleY).translate(-center.x, -center.y, -center.z)</tt>
   *
   * @param radius
   * the arcball radius
   * @param center
   * the center position of the arcball
   * @param angleX
   * the rotation angle around the X axis in radians
   * @param angleY
   * the rotation angle around the Y axis in radians
   * @return a matrix holding the result
   */
  fun arcball(radius: Float, center: Vector3fc, angleX: Float, angleY: Float): Matrix4f {
    return arcball(radius, center.x, center.y, center.z, angleX, angleY, thisOrNew())
  }

  /**
   * Compute the axis-aligned bounding box of the frustum described by `this` matrix and store the minimum corner
   * coordinates in the given `min` and the maximum corner coordinates in the given `max` vector.
   *
   *
   * The matrix `this` is assumed to be the [inverse][.invert] of the origial view-projection matrix
   * for which to compute the axis-aligned bounding box in world-space.
   *
   *
   * The axis-aligned bounding box of the unit frustum is <tt>(-1, -1, -1)</tt>, <tt>(1, 1, 1)</tt>.
   *
   * @param min
   * will hold the minimum corner coordinates of the axis-aligned bounding box
   * @param max
   * will hold the maximum corner coordinates of the axis-aligned bounding box
   * @return this
   */
  override fun frustumAabb(min: Vector3m, max: Vector3m): Matrix4f {
    var minX = java.lang.Float.POSITIVE_INFINITY
    var minY = java.lang.Float.POSITIVE_INFINITY
    var minZ = java.lang.Float.POSITIVE_INFINITY
    var maxX = java.lang.Float.NEGATIVE_INFINITY
    var maxY = java.lang.Float.NEGATIVE_INFINITY
    var maxZ = java.lang.Float.NEGATIVE_INFINITY
    for (t in 0..7) {
      val x = (t and 1 shl 1) - 1.0f
      val y = (t.ushr(1) and 1 shl 1) - 1.0f
      val z = (t.ushr(2) and 1 shl 1) - 1.0f
      val invW = 1.0f / (m03 * x + m13 * y + m23 * z + m33)
      val nx = (m00 * x + m10 * y + m20 * z + m30) * invW
      val ny = (m01 * x + m11 * y + m21 * z + m31) * invW
      val nz = (m02 * x + m12 * y + m22 * z + m32) * invW
      minX = if (minX < nx) minX else nx
      minY = if (minY < ny) minY else ny
      minZ = if (minZ < nz) minZ else nz
      maxX = if (maxX > nx) maxX else nx
      maxY = if (maxY > ny) maxY else ny
      maxZ = if (maxZ > nz) maxZ else nz
    }
    min.x = minX
    min.y = minY
    min.z = minZ
    max.x = maxX
    max.y = maxY
    max.z = maxZ
    return this
  }

  /* (non-Javadoc)
     * @see Matrix4fc#projectedGridRange(Matrix4fc, float, float, Matrix4f)
     */
  override fun projectedGridRange(projector: Matrix4fc, sLower: Float, sUpper: Float, dest: Matrix4f): Matrix4f? {
    // Compute intersection with frustum edges and plane
    var minX = java.lang.Float.POSITIVE_INFINITY
    var minY = java.lang.Float.POSITIVE_INFINITY
    var maxX = java.lang.Float.NEGATIVE_INFINITY
    var maxY = java.lang.Float.NEGATIVE_INFINITY
    var intersection = false
    for (t in 0 until 3 * 4) {
      val c0X: Float
      val c0Y: Float
      val c0Z: Float
      val c1X: Float
      val c1Y: Float
      val c1Z: Float
      if (t < 4) {
        // all x edges
        c0X = -1f
        c1X = +1f
        c1Y = (t and 1 shl 1) - 1.0f
        c0Y = c1Y
        c1Z = (t.ushr(1) and 1 shl 1) - 1.0f
        c0Z = c1Z
      } else if (t < 8) {
        // all y edges
        c0Y = -1f
        c1Y = +1f
        c1X = (t and 1 shl 1) - 1.0f
        c0X = c1X
        c1Z = (t.ushr(1) and 1 shl 1) - 1.0f
        c0Z = c1Z
      } else {
        // all z edges
        c0Z = -1f
        c1Z = +1f
        c1X = (t and 1 shl 1) - 1.0f
        c0X = c1X
        c1Y = (t.ushr(1) and 1 shl 1) - 1.0f
        c0Y = c1Y
      }
      // unproject corners
      var invW = 1.0f / (m03 * c0X + m13 * c0Y + m23 * c0Z + m33)
      val p0x = (m00 * c0X + m10 * c0Y + m20 * c0Z + m30) * invW
      val p0y = (m01 * c0X + m11 * c0Y + m21 * c0Z + m31) * invW
      val p0z = (m02 * c0X + m12 * c0Y + m22 * c0Z + m32) * invW
      invW = 1.0f / (m03 * c1X + m13 * c1Y + m23 * c1Z + m33)
      val p1x = (m00 * c1X + m10 * c1Y + m20 * c1Z + m30) * invW
      val p1y = (m01 * c1X + m11 * c1Y + m21 * c1Z + m31) * invW
      val p1z = (m02 * c1X + m12 * c1Y + m22 * c1Z + m32) * invW
      val dirX = p1x - p0x
      val dirY = p1y - p0y
      val dirZ = p1z - p0z
      val invDenom = 1.0f / dirY
      // test for intersection
      for (s in 0..1) {
        val isectT = -(p0y + if (s == 0) sLower else sUpper) * invDenom
        if (isectT >= 0.0f && isectT <= 1.0f) {
          intersection = true
          // project with projector matrix
          val ix = p0x + isectT * dirX
          val iz = p0z + isectT * dirZ
          invW = 1.0f / (projector.m03() * ix + projector.m23() * iz + projector.m33())
          val px = (projector.m00() * ix + projector.m20() * iz + projector.m30()) * invW
          val py = (projector.m01() * ix + projector.m21() * iz + projector.m31()) * invW
          minX = if (minX < px) minX else px
          minY = if (minY < py) minY else py
          maxX = if (maxX > px) maxX else px
          maxY = if (maxY > py) maxY else py
        }
      }
    }
    if (!intersection)
      return null // <- projected grid is not visible
    dest[maxX - minX, 0f, 0f, 0f, 0f, maxY - minY, 0f, 0f, 0f, 0f, 1f, 0f, minX, minY, 0f] = 1f
    dest._properties(Matrix4fc.PROPERTY_AFFINE.toInt())
    return dest
  }

  /**
   * Change the near and far clip plane distances of `this` perspective frustum transformation matrix
   * and store the result in `dest`.
   *
   *
   * This method only works if `this` is a perspective projection frustum transformation, for example obtained
   * via [perspective()][.perspective] or [frustum()][.frustum].
   *
   * @see .perspective
   * @see .frustum
   * @param near
   * the new near clip plane distance
   * @param far
   * the new far clip plane distance
   * @param dest
   * will hold the resulting matrix
   * @return dest
   */
  override fun perspectiveFrustumSlice(near: Float, far: Float, dest: Matrix4f): Matrix4f {
    val invOldNear = (m23 + m22) / m32
    val invNearFar = 1.0f / (near - far)
    dest._m00(m00 * invOldNear * near)
    dest._m01(m01)
    dest._m02(m02)
    dest._m03(m03)
    dest._m10(m10)
    dest._m11(m11 * invOldNear * near)
    dest._m12(m12)
    dest._m13(m13)
    dest._m20(m20)
    dest._m21(m21)
    dest._m22((far + near) * invNearFar)
    dest._m23(m23)
    dest._m30(m30)
    dest._m31(m31)
    dest._m32((far + far) * near * invNearFar)
    dest._m33(m33)
    dest._properties(properties and (Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt() or Matrix4fc.PROPERTY_ORTHONORMAL.toInt()).inv())
    return dest
  }

  /**
   * Build an ortographic projection transformation that fits the view-projection transformation represented by `this`
   * into the given affine `view` transformation.
   *
   *
   * The transformation represented by `this` must be given as the [inverse][.invert] of a typical combined camera view-projection
   * transformation, whose projection can be either orthographic or perspective.
   *
   *
   * The `view` must be an [affine][.isAffine] transformation which in the application of Cascaded Shadow Maps is usually the light view transformation.
   * It be obtained via any affine transformation or for example via [lookAt()][.lookAt].
   *
   *
   * Reference: [OpenGL SDK - Cascaded Shadow Maps](http://developer.download.nvidia.com/SDK/10.5/opengl/screenshots/samples/cascaded_shadow_maps.html)
   *
   * @param view
   * the view transformation to build a corresponding orthographic projection to fit the frustum of `this`
   * @param dest
   * will hold the crop projection transformation
   * @return dest
   */
  override fun orthoCrop(view: Matrix4fc, dest: Matrix4f): Matrix4f {
    // determine min/max world z and min/max orthographically view-projected x/y
    var minX = java.lang.Float.POSITIVE_INFINITY
    var maxX = java.lang.Float.NEGATIVE_INFINITY
    var minY = java.lang.Float.POSITIVE_INFINITY
    var maxY = java.lang.Float.NEGATIVE_INFINITY
    var minZ = java.lang.Float.POSITIVE_INFINITY
    var maxZ = java.lang.Float.NEGATIVE_INFINITY
    for (t in 0..7) {
      val x = (t and 1 shl 1) - 1.0f
      val y = (t.ushr(1) and 1 shl 1) - 1.0f
      val z = (t.ushr(2) and 1 shl 1) - 1.0f
      var invW = 1.0f / (m03 * x + m13 * y + m23 * z + m33)
      val wx = (m00 * x + m10 * y + m20 * z + m30) * invW
      val wy = (m01 * x + m11 * y + m21 * z + m31) * invW
      val wz = (m02 * x + m12 * y + m22 * z + m32) * invW
      invW = 1.0f / (view.m03() * wx + view.m13() * wy + view.m23() * wz + view.m33())
      val vx = view.m00() * wx + view.m10() * wy + view.m20() * wz + view.m30()
      val vy = view.m01() * wx + view.m11() * wy + view.m21() * wz + view.m31()
      val vz = (view.m02() * wx + view.m12() * wy + view.m22() * wz + view.m32()) * invW
      minX = if (minX < vx) minX else vx
      maxX = if (maxX > vx) maxX else vx
      minY = if (minY < vy) minY else vy
      maxY = if (maxY > vy) maxY else vy
      minZ = if (minZ < vz) minZ else vz
      maxZ = if (maxZ > vz) maxZ else vz
    }
    // build crop projection matrix to fit 'this' frustum into view
    return dest.setOrtho(minX, maxX, minY, maxY, -maxZ, -minZ)
  }

  /**
   * Set `this` matrix to a perspective transformation that maps the trapezoid spanned by the four corner coordinates
   * `(p0x, p0y)`, `(p1x, p1y)`, `(p2x, p2y)` and `(p3x, p3y)` to the unit square <tt>[(-1, -1)..(+1, +1)]</tt>.
   *
   *
   * The corner coordinates are given in counter-clockwise order starting from the *left* corner on the smaller parallel side of the trapezoid
   * seen when looking at the trapezoid oriented with its shorter parallel edge at the bottom and its longer parallel edge at the top.
   *
   *
   * Reference: [Trapezoidal Shadow Maps (TSM) - Recipe](http://www.comp.nus.edu.sg/~tants/tsm/TSM_recipe.html)
   *
   * @param p0x
   * the x coordinate of the left corner at the shorter edge of the trapezoid
   * @param p0y
   * the y coordinate of the left corner at the shorter edge of the trapezoid
   * @param p1x
   * the x coordinate of the right corner at the shorter edge of the trapezoid
   * @param p1y
   * the y coordinate of the right corner at the shorter edge of the trapezoid
   * @param p2x
   * the x coordinate of the right corner at the longer edge of the trapezoid
   * @param p2y
   * the y coordinate of the right corner at the longer edge of the trapezoid
   * @param p3x
   * the x coordinate of the left corner at the longer edge of the trapezoid
   * @param p3y
   * the y coordinate of the left corner at the longer edge of the trapezoid
   * @return this
   */
  fun trapezoidCrop(p0x: Float, p0y: Float, p1x: Float, p1y: Float, p2x: Float, p2y: Float, p3x: Float, p3y: Float): Matrix4f {
    val aX = p1y - p0y
    val aY = p0x - p1x
    var m00 = aY
    var m10 = -aX
    var m30 = aX * p0y - aY * p0x
    var m01 = aX
    var m11 = aY
    var m31 = -(aX * p0x + aY * p0y)
    val c3x = m00 * p3x + m10 * p3y + m30
    val c3y = m01 * p3x + m11 * p3y + m31
    val s = -c3x / c3y
    m00 += s * m01
    m10 += s * m11
    m30 += s * m31
    val d1x = m00 * p1x + m10 * p1y + m30
    val d2x = m00 * p2x + m10 * p2y + m30
    val d = d1x * c3y / (d2x - d1x)
    m31 += d
    val sx = 2.0f / d2x
    val sy = 1.0f / (c3y + d)
    val u = (sy + sy) * d / (1.0f - sy * d)
    val m03 = m01 * sy
    val m13 = m11 * sy
    val m33 = m31 * sy
    m01 = (u + 1.0f) * m03
    m11 = (u + 1.0f) * m13
    m31 = (u + 1.0f) * m33 - u
    m00 = sx * m00 - m03
    m10 = sx * m10 - m13
    m30 = sx * m30 - m33
    set(m00, m01, 0f, m03,
        m10, m11, 0f, m13,
        0f, 0f, 1f, 0f,
        m30, m31, 0f, m33)
    _properties(0)
    return this
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformAab(float, float, float, float, float, float, Vector3m, Vector3m)
     */
  override fun transformAab(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float, outMin: Vector3m, outMax: Vector3m): Matrix4f {
    val xax = m00 * minX
    val xay = m01 * minX
    val xaz = m02 * minX
    val xbx = m00 * maxX
    val xby = m01 * maxX
    val xbz = m02 * maxX
    val yax = m10 * minY
    val yay = m11 * minY
    val yaz = m12 * minY
    val ybx = m10 * maxY
    val yby = m11 * maxY
    val ybz = m12 * maxY
    val zax = m20 * minZ
    val zay = m21 * minZ
    val zaz = m22 * minZ
    val zbx = m20 * maxZ
    val zby = m21 * maxZ
    val zbz = m22 * maxZ
    val xminx: Float
    val xminy: Float
    val xminz: Float
    val yminx: Float
    val yminy: Float
    val yminz: Float
    val zminx: Float
    val zminy: Float
    val zminz: Float
    val xmaxx: Float
    val xmaxy: Float
    val xmaxz: Float
    val ymaxx: Float
    val ymaxy: Float
    val ymaxz: Float
    val zmaxx: Float
    val zmaxy: Float
    val zmaxz: Float
    if (xax < xbx) {
      xminx = xax
      xmaxx = xbx
    } else {
      xminx = xbx
      xmaxx = xax
    }
    if (xay < xby) {
      xminy = xay
      xmaxy = xby
    } else {
      xminy = xby
      xmaxy = xay
    }
    if (xaz < xbz) {
      xminz = xaz
      xmaxz = xbz
    } else {
      xminz = xbz
      xmaxz = xaz
    }
    if (yax < ybx) {
      yminx = yax
      ymaxx = ybx
    } else {
      yminx = ybx
      ymaxx = yax
    }
    if (yay < yby) {
      yminy = yay
      ymaxy = yby
    } else {
      yminy = yby
      ymaxy = yay
    }
    if (yaz < ybz) {
      yminz = yaz
      ymaxz = ybz
    } else {
      yminz = ybz
      ymaxz = yaz
    }
    if (zax < zbx) {
      zminx = zax
      zmaxx = zbx
    } else {
      zminx = zbx
      zmaxx = zax
    }
    if (zay < zby) {
      zminy = zay
      zmaxy = zby
    } else {
      zminy = zby
      zmaxy = zay
    }
    if (zaz < zbz) {
      zminz = zaz
      zmaxz = zbz
    } else {
      zminz = zbz
      zmaxz = zaz
    }
    outMin.x = xminx + yminx + zminx + m30
    outMin.y = xminy + yminy + zminy + m31
    outMin.z = xminz + yminz + zminz + m32
    outMax.x = xmaxx + ymaxx + zmaxx + m30
    outMax.y = xmaxy + ymaxy + zmaxy + m31
    outMax.z = xmaxz + ymaxz + zmaxz + m32
    return this
  }

  /* (non-Javadoc)
     * @see Matrix4fc#transformAab(Vector3fc, Vector3fc, Vector3m, Vector3m)
     */
  override fun transformAab(min: Vector3fc, max: Vector3fc, outMin: Vector3m, outMax: Vector3m): Matrix4f {
    return transformAab(min.x, min.y, min.z, max.x, max.y, max.z, outMin, outMax)
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
   * the other matrix
   * @param t
   * the interpolation factor between 0.0 and 1.0
   * @return a matrix holding the result
   */
  fun lerp(other: Matrix4fc, t: Float): Matrix4f {
    return lerp(other, t, thisOrNew())
  }

  /* (non-Javadoc)
     * @see Matrix4fc#lerp(Matrix4fc, float, Matrix4f)
     */
  override fun lerp(other: Matrix4fc, t: Float, dest: Matrix4f): Matrix4f {
    dest._m00(m00 + (other.m00() - m00) * t)
    dest._m01(m01 + (other.m01() - m01) * t)
    dest._m02(m02 + (other.m02() - m02) * t)
    dest._m03(m03 + (other.m03() - m03) * t)
    dest._m10(m10 + (other.m10() - m10) * t)
    dest._m11(m11 + (other.m11() - m11) * t)
    dest._m12(m12 + (other.m12() - m12) * t)
    dest._m13(m13 + (other.m13() - m13) * t)
    dest._m20(m20 + (other.m20() - m20) * t)
    dest._m21(m21 + (other.m21() - m21) * t)
    dest._m22(m22 + (other.m22() - m22) * t)
    dest._m23(m23 + (other.m23() - m23) * t)
    dest._m30(m30 + (other.m30() - m30) * t)
    dest._m31(m31 + (other.m31() - m31) * t)
    dest._m32(m32 + (other.m32() - m32) * t)
    dest._m33(m33 + (other.m33() - m33) * t)
    return dest
  }

  /**
   * Apply a model transformation to this matrix for a right-handed coordinate system,
   * that aligns the local `+Z` axis with `dir`
   * and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying it,
   * use [rotationTowards()][.rotationTowards].
   *
   *
   * This method is equivalent to calling: <tt>mulAffine(new Matrix4f().lookAt(new Vector3m(), new Vector3m(dir).negate(), up).invertAffine(), dest)</tt>
   *
   * @see .rotateTowards
   * @see .rotationTowards
   * @param dir
   * the direction to rotate towards
   * @param up
   * the up vector
   * @param dest
   * will hold the result
   * @return dest
   */
  override fun rotateTowards(dir: Vector3fc, up: Vector3fc, dest: Matrix4f): Matrix4f {
    return rotateTowards(dir.x, dir.y, dir.z, up.x, up.y, up.z, dest)
  }

  /**
   * Apply a model transformation to this matrix for a right-handed coordinate system,
   * that aligns the local `+Z` axis with `dir`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying it,
   * use [rotationTowards()][.rotationTowards].
   *
   *
   * This method is equivalent to calling: <tt>mulAffine(new Matrix4f().lookAt(new Vector3m(), new Vector3m(dir).negate(), up).invertAffine())</tt>
   *
   * @see .rotateTowards
   * @see .rotationTowards
   * @param dir
   * the direction to orient towards
   * @param up
   * the up vector
   * @return a matrix holding the result
   */
  fun rotateTowards(dir: Vector3fc, up: Vector3fc): Matrix4f {
    return rotateTowards(dir.x, dir.y, dir.z, up.x, up.y, up.z, thisOrNew())
  }

  /**
   * Apply a model transformation to this matrix for a right-handed coordinate system,
   * that aligns the local `+Z` axis with `(dirX, dirY, dirZ)`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying it,
   * use [rotationTowards()][.rotationTowards].
   *
   *
   * This method is equivalent to calling: <tt>mulAffine(new Matrix4f().lookAt(0, 0, 0, -dirX, -dirY, -dirZ, upX, upY, upZ).invertAffine())</tt>
   *
   * @see .rotateTowards
   * @see .rotationTowards
   * @param dirX
   * the x-coordinate of the direction to rotate towards
   * @param dirY
   * the y-coordinate of the direction to rotate towards
   * @param dirZ
   * the z-coordinate of the direction to rotate towards
   * @param upX
   * the x-coordinate of the up vector
   * @param upY
   * the y-coordinate of the up vector
   * @param upZ
   * the z-coordinate of the up vector
   * @return a matrix holding the result
   */
  fun rotateTowards(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float): Matrix4f {
    return rotateTowards(dirX, dirY, dirZ, upX, upY, upZ, thisOrNew())
  }

  /**
   * Apply a model transformation to this matrix for a right-handed coordinate system,
   * that aligns the local `+Z` axis with `(dirX, dirY, dirZ)`
   * and store the result in `dest`.
   *
   *
   * If `M` is `this` matrix and `L` the lookat matrix,
   * then the new matrix will be `M * L`. So when transforming a
   * vector `v` with the new matrix by using `M * L * v`,
   * the lookat transformation will be applied first!
   *
   *
   * In order to set the matrix to a rotation transformation without post-multiplying it,
   * use [rotationTowards()][.rotationTowards].
   *
   *
   * This method is equivalent to calling: <tt>mulAffine(new Matrix4f().lookAt(0, 0, 0, -dirX, -dirY, -dirZ, upX, upY, upZ).invertAffine(), dest)</tt>
   *
   * @see .rotateTowards
   * @see .rotationTowards
   * @param dirX
   * the x-coordinate of the direction to rotate towards
   * @param dirY
   * the y-coordinate of the direction to rotate towards
   * @param dirZ
   * the z-coordinate of the direction to rotate towards
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
  override fun rotateTowards(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float, dest: Matrix4f): Matrix4f {
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    val ndirX = dirX * invDirLength
    val ndirY = dirY * invDirLength
    val ndirZ = dirZ * invDirLength
    // left = up x direction
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * ndirZ - upZ * ndirY
    leftY = upZ * ndirX - upX * ndirZ
    leftZ = upX * ndirY - upY * ndirX
    // normalize left
    val invLeftLength = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = ndirY * leftZ - ndirZ * leftY
    val upnY = ndirZ * leftX - ndirX * leftZ
    val upnZ = ndirX * leftY - ndirY * leftX
    dest._m30(m30)
    dest._m31(m31)
    dest._m32(m32)
    dest._m33(m33)
    val nm00 = m00 * leftX + m10 * leftY + m20 * leftZ
    val nm01 = m01 * leftX + m11 * leftY + m21 * leftZ
    val nm02 = m02 * leftX + m12 * leftY + m22 * leftZ
    val nm03 = m03 * leftX + m13 * leftY + m23 * leftZ
    val nm10 = m00 * upnX + m10 * upnY + m20 * upnZ
    val nm11 = m01 * upnX + m11 * upnY + m21 * upnZ
    val nm12 = m02 * upnX + m12 * upnY + m22 * upnZ
    val nm13 = m03 * upnX + m13 * upnY + m23 * upnZ
    dest._m20(m00 * ndirX + m10 * ndirY + m20 * ndirZ)
    dest._m21(m01 * ndirX + m11 * ndirY + m21 * ndirZ)
    dest._m22(m02 * ndirX + m12 * ndirY + m22 * ndirZ)
    dest._m23(m03 * ndirX + m13 * ndirY + m23 * ndirZ)
    dest._m00(nm00)
    dest._m01(nm01)
    dest._m02(nm02)
    dest._m03(nm03)
    dest._m10(nm10)
    dest._m11(nm11)
    dest._m12(nm12)
    dest._m13(nm13)
    dest._properties(properties and (Matrix4fc.PROPERTY_PERSPECTIVE.toInt() or Matrix4fc.PROPERTY_IDENTITY.toInt() or Matrix4fc.PROPERTY_TRANSLATION.toInt()).inv())
    return dest
  }

  /**
   * Set this matrix to a model transformation for a right-handed coordinate system,
   * that aligns the local `-z` axis with `dir`.
   *
   *
   * In order to apply the rotation transformation to a previous existing transformation,
   * use [rotateTowards][.rotateTowards].
   *
   *
   * This method is equivalent to calling: <tt>setLookAt(new Vector3m(), new Vector3m(dir).negate(), up).invertAffine()</tt>
   *
   * @see .rotationTowards
   * @see .rotateTowards
   * @param dir
   * the direction to orient the local -z axis towards
   * @param up
   * the up vector
   * @return this
   */
  fun rotationTowards(dir: Vector3fc, up: Vector3fc): Matrix4f {
    return rotationTowards(dir.x, dir.y, dir.z, up.x, up.y, up.z)
  }

  /**
   * Set this matrix to a model transformation for a right-handed coordinate system,
   * that aligns the local `-z` axis with `(dirX, dirY, dirZ)`.
   *
   *
   * In order to apply the rotation transformation to a previous existing transformation,
   * use [rotateTowards][.rotateTowards].
   *
   *
   * This method is equivalent to calling: <tt>setLookAt(0, 0, 0, -dirX, -dirY, -dirZ, upX, upY, upZ).invertAffine()</tt>
   *
   * @see .rotateTowards
   * @see .rotationTowards
   * @param dirX
   * the x-coordinate of the direction to rotate towards
   * @param dirY
   * the y-coordinate of the direction to rotate towards
   * @param dirZ
   * the z-coordinate of the direction to rotate towards
   * @param upX
   * the x-coordinate of the up vector
   * @param upY
   * the y-coordinate of the up vector
   * @param upZ
   * the z-coordinate of the up vector
   * @return this
   */
  fun rotationTowards(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float): Matrix4f {
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    val ndirX = dirX * invDirLength
    val ndirY = dirY * invDirLength
    val ndirZ = dirZ * invDirLength
    // left = up x direction
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * ndirZ - upZ * ndirY
    leftY = upZ * ndirX - upX * ndirZ
    leftZ = upX * ndirY - upY * ndirX
    // normalize left
    val invLeftLength = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = ndirY * leftZ - ndirZ * leftY
    val upnY = ndirZ * leftX - ndirX * leftZ
    val upnZ = ndirX * leftY - ndirY * leftX
    this._m00(leftX)
    this._m01(leftY)
    this._m02(leftZ)
    this._m03(0.0f)
    this._m10(upnX)
    this._m11(upnY)
    this._m12(upnZ)
    this._m13(0.0f)
    this._m20(ndirX)
    this._m21(ndirY)
    this._m22(ndirZ)
    this._m23(0.0f)
    this._m30(0.0f)
    this._m31(0.0f)
    this._m32(0.0f)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Set this matrix to a model transformation for a right-handed coordinate system,
   * that translates to the given `pos` and aligns the local `-z`
   * axis with `dir`.
   *
   *
   * This method is equivalent to calling: <tt>translation(pos).rotateTowards(dir, up)</tt>
   *
   * @see .translation
   * @see .rotateTowards
   * @param pos
   * the position to translate to
   * @param dir
   * the direction to rotate towards
   * @param up
   * the up vector
   * @return this
   */
  fun translationRotateTowards(pos: Vector3fc, dir: Vector3fc, up: Vector3fc): Matrix4f {
    return translationRotateTowards(pos.x, pos.y, pos.z, dir.x, dir.y, dir.z, up.x, up.y, up.z)
  }

  /**
   * Set this matrix to a model transformation for a right-handed coordinate system,
   * that translates to the given `(posX, posY, posZ)` and aligns the local `-z`
   * axis with `(dirX, dirY, dirZ)`.
   *
   *
   * This method is equivalent to calling: <tt>translation(posX, posY, posZ).rotateTowards(dirX, dirY, dirZ, upX, upY, upZ)</tt>
   *
   * @see .translation
   * @see .rotateTowards
   * @param posX
   * the x-coordinate of the position to translate to
   * @param posY
   * the y-coordinate of the position to translate to
   * @param posZ
   * the z-coordinate of the position to translate to
   * @param dirX
   * the x-coordinate of the direction to rotate towards
   * @param dirY
   * the y-coordinate of the direction to rotate towards
   * @param dirZ
   * the z-coordinate of the direction to rotate towards
   * @param upX
   * the x-coordinate of the up vector
   * @param upY
   * the y-coordinate of the up vector
   * @param upZ
   * the z-coordinate of the up vector
   * @return this
   */
  fun translationRotateTowards(posX: Float, posY: Float, posZ: Float, dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float): Matrix4f {
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt((dirX * dirX + dirY * dirY + dirZ * dirZ).toDouble()).toFloat()
    val ndirX = dirX * invDirLength
    val ndirY = dirY * invDirLength
    val ndirZ = dirZ * invDirLength
    // left = up x direction
    var leftX: Float
    var leftY: Float
    var leftZ: Float
    leftX = upY * ndirZ - upZ * ndirY
    leftY = upZ * ndirX - upX * ndirZ
    leftZ = upX * ndirY - upY * ndirX
    // normalize left
    val invLeftLength = 1.0f / Math.sqrt((leftX * leftX + leftY * leftY + leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = ndirY * leftZ - ndirZ * leftY
    val upnY = ndirZ * leftX - ndirX * leftZ
    val upnZ = ndirX * leftY - ndirY * leftX
    this._m00(leftX)
    this._m01(leftY)
    this._m02(leftZ)
    this._m03(0.0f)
    this._m10(upnX)
    this._m11(upnY)
    this._m12(upnZ)
    this._m13(0.0f)
    this._m20(ndirX)
    this._m21(ndirY)
    this._m22(ndirZ)
    this._m23(0.0f)
    this._m30(posX)
    this._m31(posY)
    this._m32(posZ)
    this._m33(1.0f)
    _properties(Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL)
    return this
  }

  /**
   * Extract the Euler angles from the rotation represented by the upper left 3x3 submatrix of `this`
   * and store the extracted Euler angles in `dest`.
   *
   *
   * This method assumes that the upper left of `this` only represents a rotation without scaling.
   *
   *
   * Note that the returned Euler angles must be applied in the order <tt>Z * Y * X</tt> to obtain the identical matrix.
   * This means that calling [Matrix4f.rotateZYX] using the obtained Euler angles will yield
   * the same rotation as the original matrix from which the Euler angles were obtained, so in the below code the matrix
   * <tt>m2</tt> should be identical to <tt>m</tt> (disregarding possible floating-point inaccuracies).
   * <pre>
   * Matrix4f m = ...; // &lt;- matrix only representing rotation
   * Matrix4f n = new Matrix4f();
   * n.rotateZYX(m.getEulerAnglesZYX(new Vector3m()));
  </pre> *
   *
   *
   * Reference: [http://nghiaho.com/](http://nghiaho.com/?page_id=846)
   *
   * @param dest
   * will hold the extracted Euler angles
   * @return dest
   */
  override fun getEulerAnglesZYX(dest: Vector3m): Vector3m {
    dest.x = Math.atan2(m12.toDouble(), m22.toDouble()).toFloat()
    dest.y = Math.atan2((-m02).toDouble(), Math.sqrt((m12 * m12 + m22 * m22).toDouble()).toFloat().toDouble()).toFloat()
    dest.z = Math.atan2(m01.toDouble(), m00.toDouble()).toFloat()
    return dest
  }

  /**
   * Compute the extents of the coordinate system before this [affine][.isAffine] transformation was applied
   * and store the resulting corner coordinates in `corner` and the span vectors in
   * `xDir`, `yDir` and `zDir`.
   *
   *
   * That means, given the maximum extents of the coordinate system between <tt>[-1..+1]</tt> in all dimensions,
   * this method returns one corner and the length and direction of the three base axis vectors in the coordinate
   * system before this transformation is applied, which transforms into the corner coordinates <tt>[-1, +1]</tt>.
   *
   *
   * This method is equivalent to computing at least three adjacent corners using [.frustumCorner]
   * and subtracting them to obtain the length and direction of the span vectors.
   *
   * @param corner
   * will hold one corner of the span (usually the corner [Matrix4fc.CORNER_NXNYNZ])
   * @param xDir
   * will hold the direction and length of the span along the positive X axis
   * @param yDir
   * will hold the direction and length of the span along the positive Y axis
   * @param zDir
   * will hold the direction and length of the span along the positive z axis
   * @return this
   */
  fun affineSpan(corner: Vector3m, xDir: Vector3m, yDir: Vector3m, zDir: Vector3m): Matrix4f {
    val a = m10 * m22
    val b = m10 * m21
    val c = m10 * m02
    val d = m10 * m01
    val e = m11 * m22
    val f = m11 * m20
    val g = m11 * m02
    val h = m11 * m00
    val i = m12 * m21
    val j = m12 * m20
    val k = m12 * m01
    val l = m12 * m00
    val m = m20 * m02
    val n = m20 * m01
    val o = m21 * m02
    val p = m21 * m00
    val q = m22 * m01
    val r = m22 * m00
    val s = 1.0f / (m00 * m11 - m01 * m10) * m22 + (m02 * m10 - m00 * m12) * m21 + (m01 * m12 - m02 * m11) * m20
    val nm00 = (e - i) * s
    val nm01 = (o - q) * s
    val nm02 = (k - g) * s
    val nm10 = (j - a) * s
    val nm11 = (r - m) * s
    val nm12 = (c - l) * s
    val nm20 = (b - f) * s
    val nm21 = (n - p) * s
    val nm22 = (h - d) * s
    corner.x = -nm00 - nm10 - nm20 + (a * m31 - b * m32 + f * m32 - e * m30 + i * m30 - j * m31) * s
    corner.y = -nm01 - nm11 - nm21 + (m * m31 - n * m32 + p * m32 - o * m30 + q * m30 - r * m31) * s
    corner.z = -nm02 - nm12 - nm22 + (g * m30 - k * m30 + l * m31 - c * m31 + d * m32 - h * m32) * s
    xDir.x = 2.0f * nm00
    xDir.y = 2.0f * nm01
    xDir.z = 2.0f * nm02
    yDir.x = 2.0f * nm10
    yDir.y = 2.0f * nm11
    yDir.z = 2.0f * nm12
    zDir.x = 2.0f * nm20
    zDir.y = 2.0f * nm21
    zDir.z = 2.0f * nm22
    return this
  }

  /*
     * (non-Javadoc)
     * @see Matrix4fc#testPoint(float, float, float)
     */
  override fun testPoint(x: Float, y: Float, z: Float): Boolean {
    val nxX = m03 + m00
    val nxY = m13 + m10
    val nxZ = m23 + m20
    val nxW = m33 + m30
    val pxX = m03 - m00
    val pxY = m13 - m10
    val pxZ = m23 - m20
    val pxW = m33 - m30
    val nyX = m03 + m01
    val nyY = m13 + m11
    val nyZ = m23 + m21
    val nyW = m33 + m31
    val pyX = m03 - m01
    val pyY = m13 - m11
    val pyZ = m23 - m21
    val pyW = m33 - m31
    val nzX = m03 + m02
    val nzY = m13 + m12
    val nzZ = m23 + m22
    val nzW = m33 + m32
    val pzX = m03 - m02
    val pzY = m13 - m12
    val pzZ = m23 - m22
    val pzW = m33 - m32
    return nxX * x + nxY * y + nxZ * z + nxW >= 0 && pxX * x + pxY * y + pxZ * z + pxW >= 0 &&
        nyX * x + nyY * y + nyZ * z + nyW >= 0 && pyX * x + pyY * y + pyZ * z + pyW >= 0 &&
        nzX * x + nzY * y + nzZ * z + nzW >= 0 && pzX * x + pzY * y + pzZ * z + pzW >= 0
  }

  /*
     * (non-Javadoc)
     * @see Matrix4fc#testSphere(float, float, float, float)
     */
  override fun testSphere(x: Float, y: Float, z: Float, r: Float): Boolean {
    var invl: Float
    var nxX = m03 + m00
    var nxY = m13 + m10
    var nxZ = m23 + m20
    var nxW = m33 + m30
    invl = (1.0 / Math.sqrt((nxX * nxX + nxY * nxY + nxZ * nxZ).toDouble())).toFloat()
    nxX *= invl
    nxY *= invl
    nxZ *= invl
    nxW *= invl
    var pxX = m03 - m00
    var pxY = m13 - m10
    var pxZ = m23 - m20
    var pxW = m33 - m30
    invl = (1.0 / Math.sqrt((pxX * pxX + pxY * pxY + pxZ * pxZ).toDouble())).toFloat()
    pxX *= invl
    pxY *= invl
    pxZ *= invl
    pxW *= invl
    var nyX = m03 + m01
    var nyY = m13 + m11
    var nyZ = m23 + m21
    var nyW = m33 + m31
    invl = (1.0 / Math.sqrt((nyX * nyX + nyY * nyY + nyZ * nyZ).toDouble())).toFloat()
    nyX *= invl
    nyY *= invl
    nyZ *= invl
    nyW *= invl
    var pyX = m03 - m01
    var pyY = m13 - m11
    var pyZ = m23 - m21
    var pyW = m33 - m31
    invl = (1.0 / Math.sqrt((pyX * pyX + pyY * pyY + pyZ * pyZ).toDouble())).toFloat()
    pyX *= invl
    pyY *= invl
    pyZ *= invl
    pyW *= invl
    var nzX = m03 + m02
    var nzY = m13 + m12
    var nzZ = m23 + m22
    var nzW = m33 + m32
    invl = (1.0 / Math.sqrt((nzX * nzX + nzY * nzY + nzZ * nzZ).toDouble())).toFloat()
    nzX *= invl
    nzY *= invl
    nzZ *= invl
    nzW *= invl
    var pzX = m03 - m02
    var pzY = m13 - m12
    var pzZ = m23 - m22
    var pzW = m33 - m32
    invl = (1.0 / Math.sqrt((pzX * pzX + pzY * pzY + pzZ * pzZ).toDouble())).toFloat()
    pzX *= invl
    pzY *= invl
    pzZ *= invl
    pzW *= invl
    return nxX * x + nxY * y + nxZ * z + nxW >= -r && pxX * x + pxY * y + pxZ * z + pxW >= -r &&
        nyX * x + nyY * y + nyZ * z + nyW >= -r && pyX * x + pyY * y + pyZ * z + pyW >= -r &&
        nzX * x + nzY * y + nzZ * z + nzW >= -r && pzX * x + pzY * y + pzZ * z + pzW >= -r
  }

  /*
     * (non-Javadoc)
     * @see Matrix4fc#testAab(float, float, float, float, float, float)
     */
  override fun testAab(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float): Boolean {
    val nxX = m03 + m00
    val nxY = m13 + m10
    val nxZ = m23 + m20
    val nxW = m33 + m30
    val pxX = m03 - m00
    val pxY = m13 - m10
    val pxZ = m23 - m20
    val pxW = m33 - m30
    val nyX = m03 + m01
    val nyY = m13 + m11
    val nyZ = m23 + m21
    val nyW = m33 + m31
    val pyX = m03 - m01
    val pyY = m13 - m11
    val pyZ = m23 - m21
    val pyW = m33 - m31
    val nzX = m03 + m02
    val nzY = m13 + m12
    val nzZ = m23 + m22
    val nzW = m33 + m32
    val pzX = m03 - m02
    val pzY = m13 - m12
    val pzZ = m23 - m22
    val pzW = m33 - m32
    /*
         * This is an implementation of the "2.4 Basic intersection test" of the mentioned site.
         * It does not distinguish between partially inside and fully inside, though, so the test with the 'p' vertex is omitted.
         */
    return nxX * (if (nxX < 0) minX else maxX) + nxY * (if (nxY < 0) minY else maxY) + nxZ * (if (nxZ < 0) minZ else maxZ) >= -nxW &&
        pxX * (if (pxX < 0) minX else maxX) + pxY * (if (pxY < 0) minY else maxY) + pxZ * (if (pxZ < 0) minZ else maxZ) >= -pxW &&
        nyX * (if (nyX < 0) minX else maxX) + nyY * (if (nyY < 0) minY else maxY) + nyZ * (if (nyZ < 0) minZ else maxZ) >= -nyW &&
        pyX * (if (pyX < 0) minX else maxX) + pyY * (if (pyY < 0) minY else maxY) + pyZ * (if (pyZ < 0) minZ else maxZ) >= -pyW &&
        nzX * (if (nzX < 0) minX else maxX) + nzY * (if (nzY < 0) minY else maxY) + nzZ * (if (nzZ < 0) minZ else maxZ) >= -nzW &&
        pzX * (if (pzX < 0) minX else maxX) + pzY * (if (pzY < 0) minY else maxY) + pzZ * (if (pzZ < 0) minZ else maxZ) >= -pzW
  }

  companion object {

    private val serialVersionUID = 1L

    /**
     * Create a view and projection matrix from a given `eye` position, a given bottom left corner position `p` of the near plane rectangle
     * and the extents of the near plane rectangle along its local `x` and `y` axes, and store the resulting matrices
     * in `projDest` and `viewDest`.
     *
     *
     * This method creates a view and perspective projection matrix assuming that there is a pinhole camera at position `eye`
     * projecting the scene onto the near plane defined by the rectangle.
     *
     *
     * All positions and lengths are in the same (world) unit.
     *
     * @param eye
     * the position of the camera
     * @param p
     * the bottom left corner of the near plane rectangle (will map to the bottom left corner in window coordinates)
     * @param x
     * the direction and length of the local "bottom/top" X axis/side of the near plane rectangle
     * @param y
     * the direction and length of the local "left/right" Y axis/side of the near plane rectangle
     * @param nearFarDist
     * the distance between the far and near plane (the near plane will be calculated by this method).
     * If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
     * If the special value [Float.NEGATIVE_INFINITY] is used, the near and far planes will be swapped and
     * the near clipping plane will be at positive infinity.
     * If a negative value is used (except for [Float.NEGATIVE_INFINITY]) the near and far planes will be swapped
     * @param zeroToOne
     * whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when `true`
     * or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when `false`
     * @param projDest
     * will hold the resulting projection matrix
     * @param viewDest
     * will hold the resulting view matrix
     */
    fun projViewFromRectangle(
        eye: Vector3m, p: Vector3m, x: Vector3m, y: Vector3m, nearFarDist: Float, zeroToOne: Boolean,
        projDest: Matrix4f, viewDest: Matrix4f) {
      var zx = y.y * x.z - y.z * x.y
      var zy = y.z * x.x - y.x * x.z
      var zz = y.x * x.y - y.y * x.x
      var zd = zx * (p.x - eye.x) + zy * (p.y - eye.y) + zz * (p.z - eye.z)
      val zs = (if (zd >= 0) 1 else -1).toFloat()
      zx *= zs
      zy *= zs
      zz *= zs
      zd *= zs
      viewDest.setLookAt(eye.x, eye.y, eye.z, eye.x + zx, eye.y + zy, eye.z + zz, y.x, y.y, y.z)
      val px = viewDest.m00 * p.x + viewDest.m10 * p.y + viewDest.m20 * p.z + viewDest.m30
      val py = viewDest.m01 * p.x + viewDest.m11 * p.y + viewDest.m21 * p.z + viewDest.m31
      val tx = viewDest.m00 * x.x + viewDest.m10 * x.y + viewDest.m20 * x.z
      val ty = viewDest.m01 * y.x + viewDest.m11 * y.y + viewDest.m21 * y.z
      val len = Math.sqrt((zx * zx + zy * zy + zz * zz).toDouble()).toFloat()
      var near = zd / len
      val far: Float
      if (java.lang.Float.isInfinite(nearFarDist) && nearFarDist < 0.0f) {
        far = near
        near = java.lang.Float.POSITIVE_INFINITY
      } else if (java.lang.Float.isInfinite(nearFarDist) && nearFarDist > 0.0f) {
        far = java.lang.Float.POSITIVE_INFINITY
      } else if (nearFarDist < 0.0f) {
        far = near
        near = near + nearFarDist
      } else {
        far = near + nearFarDist
      }
      projDest.setFrustum(px, px + tx, py, py + ty, near, far, zeroToOne)
    }
  }

}
/**
 * Set the values in the matrix using a float array that contains the matrix elements in column-major order.
 *
 *
 * The results will look like this:<br></br><br></br>
 *
 * 0, 4, 8, 12<br></br>
 * 1, 5, 9, 13<br></br>
 * 2, 6, 10, 14<br></br>
 * 3, 7, 11, 15<br></br>
 *
 * @see .set
 * @param m
 * the array to read the matrix values from
 * @return this
 */
/**
 * Apply an orthographic projection transformation for a right-handed coordinate system
 * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix.
 *
 *
 * If `M` is `this` matrix and `O` the orthographic projection matrix,
 * then the new matrix will be `M * O`. So when transforming a
 * vector `v` with the new matrix by using `M * O * v`, the
 * orthographic projection transformation will be applied first!
 *
 *
 * In order to set the matrix to an orthographic projection without post-multiplying it,
 * use [setOrtho()][.setOrtho].
 *
 *
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
 * @see .setOrtho
 * @param left
 * the distance from the center to the left frustum edge
 * @param right
 * the distance from the center to the right frustum edge
 * @param bottom
 * the distance from the center to the bottom frustum edge
 * @param top
 * the distance from the center to the top frustum edge
 * @param zNear
 * near clipping plane distance
 * @param zFar
 * far clipping plane distance
 * @return this
 */
/**
 * Apply an orthographic projection transformation for a left-handed coordiante system
 * using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix.
 *
 *
 * If `M` is `this` matrix and `O` the orthographic projection matrix,
 * then the new matrix will be `M * O`. So when transforming a
 * vector `v` with the new matrix by using `M * O * v`, the
 * orthographic projection transformation will be applied first!
 *
 *
 * In order to set the matrix to an orthographic projection without post-multiplying it,
 * use [setOrthoLH()][.setOrthoLH].
 *
 *
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
 * @see .setOrthoLH
 * @param left
 * the distance from the center to the left frustum edge
 * @param right
 * the distance from the center to the right frustum edge
 * @param bottom
 * the distance from the center to the bottom frustum edge
 * @param top
 * the distance from the center to the top frustum edge
 * @param zNear
 * near clipping plane distance
 * @param zFar
 * far clipping plane distance
 * @return this
 */
/**
 * Set this matrix to be an orthographic projection transformation for a right-handed coordinate system
 * using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
 *
 *
 * In order to apply the orthographic projection to an already existing transformation,
 * use [ortho()][.ortho].
 *
 *
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
 * @see .ortho
 * @param left
 * the distance from the center to the left frustum edge
 * @param right
 * the distance from the center to the right frustum edge
 * @param bottom
 * the distance from the center to the bottom frustum edge
 * @param top
 * the distance from the center to the top frustum edge
 * @param zNear
 * near clipping plane distance
 * @param zFar
 * far clipping plane distance
 * @return this
 */
/**
 * Set this matrix to be an orthographic projection transformation for a left-handed coordinate system
 * using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
 *
 *
 * In order to apply the orthographic projection to an already existing transformation,
 * use [orthoLH()][.orthoLH].
 *
 *
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
 * @see .orthoLH
 * @param left
 * the distance from the center to the left frustum edge
 * @param right
 * the distance from the center to the right frustum edge
 * @param bottom
 * the distance from the center to the bottom frustum edge
 * @param top
 * the distance from the center to the top frustum edge
 * @param zNear
 * near clipping plane distance
 * @param zFar
 * far clipping plane distance
 * @return this
 */
/**
 * Set this matrix to be a symmetric orthographic projection transformation for a right-handed coordinate system
 * using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
 *
 *
 * This method is equivalent to calling [setOrtho()][.setOrtho] with
 * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
 *
 *
 * In order to apply the symmetric orthographic projection to an already existing transformation,
 * use [orthoSymmetric()][.orthoSymmetric].
 *
 *
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
 * @see .orthoSymmetric
 * @param width
 * the distance between the right and left frustum edges
 * @param height
 * the distance between the top and bottom frustum edges
 * @param zNear
 * near clipping plane distance
 * @param zFar
 * far clipping plane distance
 * @return this
 */
/**
 * Set this matrix to be a symmetric orthographic projection transformation for a left-handed coordinate system
 * using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
 *
 *
 * This method is equivalent to calling [setOrthoLH()][.setOrthoLH] with
 * `left=-width/2`, `right=+width/2`, `bottom=-height/2` and `top=+height/2`.
 *
 *
 * In order to apply the symmetric orthographic projection to an already existing transformation,
 * use [orthoSymmetricLH()][.orthoSymmetricLH].
 *
 *
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
 * @see .orthoSymmetricLH
 * @param width
 * the distance between the right and left frustum edges
 * @param height
 * the distance between the top and bottom frustum edges
 * @param zNear
 * near clipping plane distance
 * @param zFar
 * far clipping plane distance
 * @return this
 */
/**
 * Set this matrix to be a symmetric perspective projection frustum transformation for a right-handed coordinate system
 * using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
 *
 *
 * In order to apply the perspective projection transformation to an existing transformation,
 * use [perspective()][.perspective].
 *
 * @see .perspective
 * @param fovy
 * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
 * @param aspect
 * the aspect ratio (i.e. width / height; must be greater than zero)
 * @param zNear
 * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
 * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
 * @param zFar
 * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
 * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
 * @return this
 */
/**
 * Set this matrix to be a symmetric perspective projection frustum transformation for a left-handed coordinate system
 * using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
 *
 *
 * In order to apply the perspective projection transformation to an existing transformation,
 * use [perspectiveLH()][.perspectiveLH].
 *
 * @see .perspectiveLH
 * @param fovy
 * the vertical field of view in radians (must be greater than zero and less than [PI][Math.PI])
 * @param aspect
 * the aspect ratio (i.e. width / height; must be greater than zero)
 * @param zNear
 * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
 * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
 * @param zFar
 * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
 * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
 * @return this
 */
/**
 * Set this matrix to be an arbitrary perspective projection frustum transformation for a right-handed coordinate system
 * using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
 *
 *
 * In order to apply the perspective frustum transformation to an existing transformation,
 * use [frustum()][.frustum].
 *
 *
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
 *
 * @see .frustum
 * @param left
 * the distance along the x-axis to the left frustum edge
 * @param right
 * the distance along the x-axis to the right frustum edge
 * @param bottom
 * the distance along the y-axis to the bottom frustum edge
 * @param top
 * the distance along the y-axis to the top frustum edge
 * @param zNear
 * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
 * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
 * @param zFar
 * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
 * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
 * @return this
 */
/**
 * Set this matrix to be an arbitrary perspective projection frustum transformation for a left-handed coordinate system
 * using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
 *
 *
 * In order to apply the perspective frustum transformation to an existing transformation,
 * use [frustumLH()][.frustumLH].
 *
 *
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
 *
 * @see .frustumLH
 * @param left
 * the distance along the x-axis to the left frustum edge
 * @param right
 * the distance along the x-axis to the right frustum edge
 * @param bottom
 * the distance along the y-axis to the bottom frustum edge
 * @param top
 * the distance along the y-axis to the top frustum edge
 * @param zNear
 * near clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the near clipping plane will be at positive infinity.
 * In that case, `zFar` may not also be [Float.POSITIVE_INFINITY].
 * @param zFar
 * far clipping plane distance. If the special value [Float.POSITIVE_INFINITY] is used, the far clipping plane will be at positive infinity.
 * In that case, `zNear` may not also be [Float.POSITIVE_INFINITY].
 * @return this
 */
