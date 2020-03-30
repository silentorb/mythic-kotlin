package silentorb.mythic.spatial

import org.joml.*
import org.joml.Quaternionfc
import org.joml.Vector3f
import java.text.DecimalFormat
import java.text.NumberFormat

data class Matrix3(
    val m00: Float = 1f,
    val m01: Float = 0f,
    val m02: Float = 0f,
    val m10: Float = 0f,
    val m11: Float = 1f,
    val m12: Float = 0f,
    val m20: Float = 0f,
    val m21: Float = 0f,
    val m22: Float = 1f
) {

  fun translate(x: Float, y: Float): Matrix3 {
    return this.copy(
        m20 = m00 * x + m10 * y + m20,
        m21 = m01 * x + m11 * y + m21,
        m22 = m02 * x + m12 * y + m22
    )
  }

  fun translate(vector: Vector2): Matrix3 =
      translate(vector.x, vector.y)

  fun transform(x: Float, y: Float): Vector2 {
   return Vector2(
        x * m00 + y * m10 + m20,
        x * m01 + y * m11 + m21
    )
  }

  fun transform(vector: Vector2): Vector2 =
      transform(vector.x, vector.y)

  /**
   * Multiply this matrix by the supplied `right` matrix.
   *
   *
   * If `M` is `this` matrix and `R` the `right` matrix,
   * then the new matrix will be `M * R`. So when transforming a
   * vector `v` with the new matrix by using `M * R * v`, the
   * transformation of the right matrix will be applied first!
   *
   * @param right
   * the right operand of the matrix multiplication
   * @return this
   */
  fun mul(right: Matrix3): Matrix3 {
    val nm00 = m00 * right.m00 + m10 * right.m01 + m20 * right.m02
    val nm01 = m01 * right.m00 + m11 * right.m01 + m21 * right.m02
    val nm02 = m02 * right.m00 + m12 * right.m01 + m22 * right.m02
    val nm10 = m00 * right.m10 + m10 * right.m11 + m20 * right.m12
    val nm11 = m01 * right.m10 + m11 * right.m11 + m21 * right.m12
    val nm12 = m02 * right.m10 + m12 * right.m11 + m22 * right.m12
    val nm20 = m00 * right.m20 + m10 * right.m21 + m20 * right.m22
    val nm21 = m01 * right.m20 + m11 * right.m21 + m21 * right.m22
    val nm22 = m02 * right.m20 + m12 * right.m21 + m22 * right.m22
    return Matrix3(
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12,
        m20 = nm20,
        m21 = nm21,
        m22 = nm22
    )
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#mulLocal(silentorb.mythic.spatial.Matrix3, org.joml.Matrix3f)
     */
  fun mulLocal(left: Matrix3): Matrix3 {
    val nm00 = left.m00 * m00 + left.m10 * m01 + left.m20 * m02
    val nm01 = left.m01 * m00 + left.m11 * m01 + left.m20 * m02
    val nm10 = left.m00 * m10 + left.m10 * m11 + left.m20 * m12
    val nm11 = left.m01 * m10 + left.m11 * m11 + left.m20 * m12
    val nm20 = left.m00 * m20 + left.m10 * m21 + left.m20 * m22
    val nm21 = left.m01 * m20 + left.m11 * m21 + left.m21 * m22
    return Matrix3(
        m00 = nm00,
        m01 = nm01,
        m10 = nm10,
        m11 = nm11,
        m20 = nm20,
        m21 = nm21
    )
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#determinant()
     */
  fun determinant(): Float {
    return (m00 * m11 - m01 * m10) * m22 + (m02 * m10 - m00 * m12) * m21 + (m01 * m12 - m02 * m11) * m20
  }

  /**
   * Invert this matrix.
   *
   * @return this
   */
  fun invert(): Matrix3 {
    return invert(this)
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#invert(org.joml.Matrix3f)
     */
  fun invert(dest: Matrix3): Matrix3 {
    val s = 1.0f / determinant()
    val nm00 = (m11 * m22 - m21 * m12) * s
    val nm01 = (m21 * m02 - m01 * m22) * s
    val nm02 = (m01 * m12 - m11 * m02) * s
    val nm10 = (m20 * m12 - m10 * m22) * s
    val nm11 = (m00 * m22 - m20 * m02) * s
    val nm12 = (m10 * m02 - m00 * m12) * s
    val nm20 = (m10 * m21 - m20 * m11) * s
    val nm21 = (m20 * m01 - m00 * m21) * s
    val nm22 = (m00 * m11 - m10 * m01) * s
    return Matrix3(
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12,
        m20 = nm20,
        m21 = nm21,
        m22 = nm22
    )
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
    var eIndex = Int.MIN_VALUE
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
    return """${formatter.format(m00.toDouble())} ${formatter.format(m10.toDouble())} ${formatter.format(m20.toDouble())}
${formatter.format(m01.toDouble())} ${formatter.format(m11.toDouble())} ${formatter.format(m21.toDouble())}
${formatter.format(m02.toDouble())} ${formatter.format(m12.toDouble())} ${formatter.format(m22.toDouble())}
"""
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
   * @return this
   */
  fun scale(xyz: Vector3f): Matrix3 {
    return scale(xyz.x(), xyz.y(), xyz.z())
  }

  fun scale(x: Float, y: Float, z: Float): Matrix3 {
    // scale matrix elements:
    // m00 = x, m11 = y, m22 = z
    // all others = 0
    return Matrix3(
        m00 = m00 * x,
        m01 = m01 * x,
        m02 = m02 * x,
        m10 = m10 * y,
        m11 = m11 * y,
        m12 = m12 * y,
        m20 = m20 * z,
        m21 = m21 * z,
        m22 = m22 * z
    )
  }

  /**
   * Apply scaling to this matrix by uniformly scaling all base axes by the given `xyz` factor.
   *
   *
   * If `M` is `this` matrix and `S` the scaling matrix,
   * then the new matrix will be `M * S`. So when transforming a
   * vector `v` with the new matrix by using `M * S * v`
   * , the scaling will be applied first!
   *
   * @see .scale
   * @param xyz
   * the factor for all components
   * @return this
   */
  fun scale(xyz: Float): Matrix3 {
    return scale(xyz, xyz, xyz)
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#scaleLocal(float, float, float, org.joml.Matrix3f)
     */
  fun scaleLocal(x: Float, y: Float, z: Float): Matrix3 {
    val nm00 = x * m00
    val nm01 = y * m01
    val nm02 = z * m02
    val nm10 = x * m10
    val nm11 = y * m11
    val nm12 = z * m12
    val nm20 = x * m20
    val nm21 = y * m21
    val nm22 = z * m22
    return Matrix3(
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12,
        m20 = nm20,
        m21 = nm21,
        m22 = nm22
    )
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
   * the axis to rotate about (needs to be [normalized][Vector3f.normalize])
   * @return this
   */
  fun rotation(angle: Float, axis: Vector3f): Matrix3 {
    return rotation(angle, axis.x(), axis.y(), axis.z())
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
  fun rotation(axisAngle: AxisAngle4f): Matrix3 {
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
  fun rotation(angle: Float, x: Float, y: Float, z: Float): Matrix3 {
    val sin = Math.sin(angle.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val C = 1.0f - cos
    val xy = x * y
    val xz = x * z
    val yz = y * z
    return Matrix3(
        m00 = cos + x * x * C,
        m10 = xy * C - z * sin,
        m20 = xz * C + y * sin,
        m01 = xy * C + z * sin,
        m11 = cos + y * y * C,
        m21 = yz * C - x * sin,
        m02 = xz * C - y * sin,
        m12 = yz * C + x * sin,
        m22 = cos + z * z * C
    )
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
  fun rotationX(ang: Float): Matrix3 {
    val sin: Float
    val cos: Float
    sin = Math.sin(ang.toDouble()).toFloat()
    cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    return Matrix3(
        m00 = 1.0f,
        m01 = 0.0f,
        m02 = 0.0f,
        m10 = 0.0f,
        m11 = cos,
        m12 = sin,
        m20 = 0.0f,
        m21 = -sin,
        m22 = cos
    )
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
  fun rotationY(ang: Float): Matrix3 {
    val sin: Float
    val cos: Float
    sin = Math.sin(ang.toDouble()).toFloat()
    cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    return Matrix3(
        m00 = cos,
        m01 = 0.0f,
        m02 = -sin,
        m10 = 0.0f,
        m11 = 1.0f,
        m12 = 0.0f,
        m20 = sin,
        m21 = 0.0f,
        m22 = cos
    )
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
  fun rotationZ(ang: Float): Matrix3 {
    val sin: Float
    val cos: Float
    sin = Math.sin(ang.toDouble()).toFloat()
    cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    return Matrix3(
        m00 = cos,
        m01 = sin,
        m02 = 0.0f,
        m10 = -sin,
        m11 = cos,
        m12 = 0.0f,
        m20 = 0.0f,
        m21 = 0.0f,
        m22 = 1.0f
    )
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
  fun rotationXYZ(angleX: Float, angleY: Float, angleZ: Float): Matrix3 {
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
    return Matrix3(
        m20 = sinY,
        m21 = m_sinX * cosY,
        m22 = cosX * cosY,
        // rotateZ
        m00 = cosY * cosZ,
        m01 = nm01 * cosZ + cosX * sinZ,
        m02 = nm02 * cosZ + sinX * sinZ,
        m10 = cosY * m_sinZ,
        m11 = nm01 * m_sinZ + cosX * cosZ,
        m12 = nm02 * m_sinZ + sinX * cosZ
    )
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
  fun rotationZYX(angleZ: Float, angleY: Float, angleX: Float): Matrix3 {
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
    return Matrix3(
        m00 = cosZ * cosY,
        m01 = sinZ * cosY,
        m02 = m_sinY,
        // rotateX
        m10 = m_sinZ * cosX + nm20 * sinX,
        m11 = cosZ * cosX + nm21 * sinX,
        m12 = cosY * sinX,
        m20 = m_sinZ * m_sinX + nm20 * cosX,
        m21 = cosZ * m_sinX + nm21 * cosX,
        m22 = cosY * cosX
    )
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
  fun rotationYXZ(angleY: Float, angleX: Float, angleZ: Float): Matrix3 {
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
    return Matrix3(
        m20 = sinY * cosX,
        m21 = m_sinX,
        m22 = cosY * cosX,
        // rotateZ
        m00 = cosY * cosZ + nm10 * sinZ,
        m01 = cosX * sinZ,
        m02 = m_sinY * cosZ + nm12 * sinZ,
        m10 = cosY * m_sinZ + nm10 * cosZ,
        m11 = cosX * cosZ,
        m12 = m_sinY * m_sinZ + nm12 * cosZ
    )
  }

  /**
   * Set this matrix to the rotation - and possibly scaling - transformation of the given [Quaternionfc].
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
  fun rotation(quat: Quaternionfc): Matrix3 {
    val w2 = quat.w() * quat.w()
    val x2 = quat.x() * quat.x()
    val y2 = quat.y() * quat.y()
    val z2 = quat.z() * quat.z()
    val zw = quat.z() * quat.w()
    val xy = quat.x() * quat.y()
    val xz = quat.x() * quat.z()
    val yw = quat.y() * quat.w()
    val yz = quat.y() * quat.z()
    val xw = quat.x() * quat.w()
    return Matrix3(
        m00 = w2 + x2 - z2 - y2,
        m01 = xy + zw + zw + xy,
        m02 = xz - yw + xz - yw,
        m10 = -zw + xy - zw + xy,
        m11 = y2 - z2 + w2 - x2,
        m12 = yz + yz + xw + xw,
        m20 = yw + xz + xz + yw,
        m21 = yz + yz - xw - xw,
        m22 = z2 - y2 - x2 + w2
    )
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#rotateX(float, org.joml.Matrix3f)
     */
  fun rotateX(ang: Float): Matrix3 {
    val sin: Float
    val cos: Float
    if (ang == Math.PI.toFloat() || ang == (-Math.PI).toFloat()) {
      cos = -1.0f
      sin = 0.0f
    } else if (ang == Math.PI.toFloat() * 0.5f || ang == (-Math.PI).toFloat() * 1.5f) {
      cos = 0.0f
      sin = 1.0f
    } else if (ang == (-Math.PI).toFloat() * 0.5f || ang == Math.PI.toFloat() * 1.5f) {
      cos = 0.0f
      sin = -1.0f
    } else {
      sin = Math.sin(ang.toDouble()).toFloat()
      cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    }
    val rm21 = -sin

    // add temporaries for dependent values
    val nm10 = m10 * cos + m20 * sin
    val nm11 = m11 * cos + m21 * sin
    val nm12 = m12 * cos + m22 * sin
    // set non-dependent values directly
    return Matrix3(
        m20 = m10 * rm21 + m20 * cos,
        m21 = m11 * rm21 + m21 * cos,
        m22 = m12 * rm21 + m22 * cos,
        // set other values
        m10 = nm10,
        m11 = nm11,
        m12 = nm12,
        m00 = m00,
        m01 = m01,
        m02 = m02
    )
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#rotateY(float, org.joml.Matrix3f)
     */
  fun rotateY(ang: Float): Matrix3 {
    val sin: Float
    val cos: Float
    if (ang == Math.PI.toFloat() || ang == (-Math.PI).toFloat()) {
      cos = -1.0f
      sin = 0.0f
    } else if (ang == Math.PI.toFloat() * 0.5f || ang == (-Math.PI).toFloat() * 1.5f) {
      cos = 0.0f
      sin = 1.0f
    } else if (ang == (-Math.PI).toFloat() * 0.5f || ang == Math.PI.toFloat() * 1.5f) {
      cos = 0.0f
      sin = -1.0f
    } else {
      sin = Math.sin(ang.toDouble()).toFloat()
      cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    }
    val rm02 = -sin

    // add temporaries for dependent values
    val nm00 = m00 * cos + m20 * rm02
    val nm01 = m01 * cos + m21 * rm02
    val nm02 = m02 * cos + m22 * rm02
    // set non-dependent values directly
    return Matrix3(
        m20 = m00 * sin + m20 * cos,
        m21 = m01 * sin + m21 * cos,
        m22 = m02 * sin + m22 * cos,
        // set other values
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m10 = m10,
        m11 = m11,
        m12 = m12
    )
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#rotateZ(float, org.joml.Matrix3f)
     */
  fun rotateZ(ang: Float): Matrix3 {
    val sin: Float
    val cos: Float
    if (ang == Math.PI.toFloat() || ang == (-Math.PI).toFloat()) {
      cos = -1.0f
      sin = 0.0f
    } else if (ang == Math.PI.toFloat() * 0.5f || ang == (-Math.PI).toFloat() * 1.5f) {
      cos = 0.0f
      sin = 1.0f
    } else if (ang == (-Math.PI).toFloat() * 0.5f || ang == Math.PI.toFloat() * 1.5f) {
      cos = 0.0f
      sin = -1.0f
    } else {
      sin = Math.sin(ang.toDouble()).toFloat()
      cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    }
    val rm10 = -sin

    // add temporaries for dependent values
    val nm00 = m00 * cos + m10 * sin
    val nm01 = m01 * cos + m11 * sin
    val nm02 = m02 * cos + m12 * sin
    // set non-dependent values directly
    return Matrix3(
        m10 = m00 * rm10 + m10 * cos,
        m11 = m01 * rm10 + m11 * cos,
        m12 = m02 * rm10 + m12 * cos,
        // set other values
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m20 = m20,
        m21 = m21,
        m22 = m22
    )
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
  fun rotateXYZ(angles: Vector3f): Matrix3 {
    return rotateXYZ(angles.x, angles.y, angles.z)
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#rotateXYZ(float, float, float, org.joml.Matrix3f)
     */
  fun rotateXYZ(angleX: Float, angleY: Float, angleZ: Float): Matrix3 {
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
    return Matrix3(
        m20 = m00 * sinY + nm20 * cosY,
        m21 = m01 * sinY + nm21 * cosY,
        m22 = m02 * sinY + nm22 * cosY,
        // rotateZ
        m00 = nm00 * cosZ + nm10 * sinZ,
        m01 = nm01 * cosZ + nm11 * sinZ,
        m02 = nm02 * cosZ + nm12 * sinZ,
        m10 = nm00 * m_sinZ + nm10 * cosZ,
        m11 = nm01 * m_sinZ + nm11 * cosZ,
        m12 = nm02 * m_sinZ + nm12 * cosZ
    )
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
  fun rotateZYX(angles: Vector3f): Matrix3 {
    return rotateZYX(angles.z, angles.y, angles.x)
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#rotateZYX(float, float, float, org.joml.Matrix3f)
     */
  fun rotateZYX(angleZ: Float, angleY: Float, angleX: Float): Matrix3 {
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
    return Matrix3(
        m00 = nm00 * cosY + m20 * m_sinY,
        m01 = nm01 * cosY + m21 * m_sinY,
        m02 = nm02 * cosY + m22 * m_sinY,
        // rotateX
        m10 = nm10 * cosX + nm20 * sinX,
        m11 = nm11 * cosX + nm21 * sinX,
        m12 = nm12 * cosX + nm22 * sinX,
        m20 = nm10 * m_sinX + nm20 * cosX,
        m21 = nm11 * m_sinX + nm21 * cosX,
        m22 = nm12 * m_sinX + nm22 * cosX
    )
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
  fun rotateYXZ(angles: Vector3f): Matrix3 {
    return rotateYXZ(angles.y, angles.x, angles.z)
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#rotateYXZ(float, float, float, org.joml.Matrix3f)
     */
  fun rotateYXZ(angleY: Float, angleX: Float, angleZ: Float): Matrix3 {
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
    return Matrix3(
        m20 = m10 * m_sinX + nm20 * cosX,
        m21 = m11 * m_sinX + nm21 * cosX,
        m22 = m12 * m_sinX + nm22 * cosX,
        // rotateZ
        m00 = nm00 * cosZ + nm10 * sinZ,
        m01 = nm01 * cosZ + nm11 * sinZ,
        m02 = nm02 * cosZ + nm12 * sinZ,
        m10 = nm00 * m_sinZ + nm10 * cosZ,
        m11 = nm01 * m_sinZ + nm11 * cosZ,
        m12 = nm02 * m_sinZ + nm12 * cosZ
    )
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#rotate(float, float, float, float, org.joml.Matrix3f)
     */
  fun rotate(ang: Float, x: Float, y: Float, z: Float): Matrix3 {
    val s = Math.sin(ang.toDouble()).toFloat()
    val c = Math.cosFromSin(s.toDouble(), ang.toDouble()).toFloat()
    val C = 1.0f - c

    // rotation matrix elements:
    // m30, m31, m32, m03, m13, m23 = 0
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
    return Matrix3(
        m20 = m00 * rm20 + m10 * rm21 + m20 * rm22,
        m21 = m01 * rm20 + m11 * rm21 + m21 * rm22,
        m22 = m02 * rm20 + m12 * rm21 + m22 * rm22,
        // set other values
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12
    )
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
   * @)
   */
  fun rotateLocal(ang: Float, x: Float, y: Float, z: Float): Matrix3 {
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
    val nm10 = lm00 * m10 + lm10 * m11 + lm20 * m12
    val nm11 = lm01 * m10 + lm11 * m11 + lm21 * m12
    val nm12 = lm02 * m10 + lm12 * m11 + lm22 * m12
    val nm20 = lm00 * m20 + lm10 * m21 + lm20 * m22
    val nm21 = lm01 * m20 + lm11 * m21 + lm21 * m22
    val nm22 = lm02 * m20 + lm12 * m21 + lm22 * m22
    return Matrix3(
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12,
        m20 = nm20,
        m21 = nm21,
        m22 = nm22
    )
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
   * @)
   */
  fun rotateLocalX(ang: Float): Matrix3 {
    val sin = Math.sin(ang.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    val nm01 = cos * m01 - sin * m02
    val nm02 = sin * m01 + cos * m02
    val nm11 = cos * m11 - sin * m12
    val nm12 = sin * m11 + cos * m12
    val nm21 = cos * m21 - sin * m22
    val nm22 = sin * m21 + cos * m22
    return Matrix3(
        m00 = m00,
        m01 = nm01,
        m02 = nm02,
        m10 = m10,
        m11 = nm11,
        m12 = nm12,
        m20 = m20,
        m21 = nm21,
        m22 = nm22
    )
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
   * @)
   */
  fun rotateLocalY(ang: Float): Matrix3 {
    val sin = Math.sin(ang.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    val nm00 = cos * m00 + sin * m02
    val nm02 = -sin * m00 + cos * m02
    val nm10 = cos * m10 + sin * m12
    val nm12 = -sin * m10 + cos * m12
    val nm20 = cos * m20 + sin * m22
    val nm22 = -sin * m20 + cos * m22
    return Matrix3(
        m00 = nm00,
        m01 = m01,
        m02 = nm02,
        m10 = nm10,
        m11 = m11,
        m12 = nm12,
        m20 = nm20,
        m21 = m21,
        m22 = nm22
    )
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
   * @)
   */
  fun rotateLocalZ(ang: Float): Matrix3 {
    val sin = Math.sin(ang.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    val nm00 = cos * m00 - sin * m01
    val nm01 = sin * m00 + cos * m01
    val nm10 = cos * m10 - sin * m11
    val nm11 = sin * m10 + cos * m11
    val nm20 = cos * m20 - sin * m21
    val nm21 = sin * m20 + cos * m21
    return Matrix3(
        m00 = nm00,
        m01 = nm01,
        m02 = m02,
        m10 = nm10,
        m11 = nm11,
        m12 = m12,
        m20 = nm20,
        m21 = nm21,
        m22 = m22
    )
  }

  /**
   * Apply the rotation - and possibly scaling - transformation of the given [Quaternionfc] to this matrix and store
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
   * @)
   */
  fun rotate(quat: Quaternionfc?): Matrix3 {
    val w2 = quat!!.w() * quat.w()
    val x2 = quat.x() * quat.x()
    val y2 = quat.y() * quat.y()
    val z2 = quat.z() * quat.z()
    val zw = quat.z() * quat.w()
    val xy = quat.x() * quat.y()
    val xz = quat.x() * quat.z()
    val yw = quat.y() * quat.w()
    val yz = quat.y() * quat.z()
    val xw = quat.x() * quat.w()
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
    return Matrix3(
        m20 = m00 * rm20 + m10 * rm21 + m20 * rm22,
        m21 = m01 * rm20 + m11 * rm21 + m21 * rm22,
        m22 = m02 * rm20 + m12 * rm21 + m22 * rm22,
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12
    )
  }

  /**
   * Pre-multiply the rotation - and possibly scaling - transformation of the given [Quaternionfc] to this matrix and store
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
   * @)
   */
  fun rotateLocal(quat: Quaternionfc?): Matrix3 {
    val w2 = quat!!.w() * quat.w()
    val x2 = quat.x() * quat.x()
    val y2 = quat.y() * quat.y()
    val z2 = quat.z() * quat.z()
    val zw = quat.z() * quat.w()
    val xy = quat.x() * quat.y()
    val xz = quat.x() * quat.z()
    val yw = quat.y() * quat.w()
    val yz = quat.y() * quat.z()
    val xw = quat.x() * quat.w()
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
    val nm10 = lm00 * m10 + lm10 * m11 + lm20 * m12
    val nm11 = lm01 * m10 + lm11 * m11 + lm21 * m12
    val nm12 = lm02 * m10 + lm12 * m11 + lm22 * m12
    val nm20 = lm00 * m20 + lm10 * m21 + lm20 * m22
    val nm21 = lm01 * m20 + lm11 * m21 + lm21 * m22
    val nm22 = lm02 * m20 + lm12 * m21 + lm22 * m22
    return Matrix3(
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12,
        m20 = nm20,
        m21 = nm21,
        m22 = nm22
    )
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
   * In order to set the matrix to a lookalong transformation without post-multiplying it,
   * use [setLookAlong()][.setLookAlong]
   *
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
   * @)
   */
  fun lookAlong(dirX: Float, dirY: Float, dirZ: Float,
                upX: Float, upY: Float, upZ: Float): Matrix3 {
    // Normalize direction
    var dirX = dirX
    var dirY = dirY
    var dirZ = dirZ
    val invDirLength = 1.0f / Math.sqrt(dirX * dirX + dirY * dirY + (dirZ * dirZ).toDouble()).toFloat()
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
    val invLeftLength = 1.0f / Math.sqrt(leftX * leftX + leftY * leftY + (leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = dirY * leftZ - dirZ * leftY
    val upnY = dirZ * leftX - dirX * leftZ
    val upnZ = dirX * leftY - dirY * leftX

    // calculate right matrix elements
    val rm00 = leftX
    val rm02 = dirX
    val rm10 = leftY
    val rm12 = dirY
    val rm20 = leftZ
    val rm22 = dirZ

    // perform optimized matrix multiplication
    // introduce temporaries for dependent results
    val nm00 = m00 * rm00 + m10 * upnX + m20 * rm02
    val nm01 = m01 * rm00 + m11 * upnX + m21 * rm02
    val nm02 = m02 * rm00 + m12 * upnX + m22 * rm02
    val nm10 = m00 * rm10 + m10 * upnY + m20 * rm12
    val nm11 = m01 * rm10 + m11 * upnY + m21 * rm12
    val nm12 = m02 * rm10 + m12 * upnY + m22 * rm12
    return Matrix3(
        m20 = m00 * rm20 + m10 * upnZ + m20 * rm22,
        m21 = m01 * rm20 + m11 * upnZ + m21 * rm22,
        m22 = m02 * rm20 + m12 * upnZ + m22 * rm22,
        // set the rest of the matrix elements
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12
    )
  }

  /**
   * Set `this` matrix to its own normal matrix.
   *
   *
   * Please note that, if `this` is an orthogonal matrix or a matrix whose columns are orthogonal vectors,
   * then this method *need not* be invoked, since in that case `this` itself is its normal matrix.
   * In this case, use [.set] to set a given Matrix3f to this matrix.
   *
   * @see .set
   * @return this
   */
  fun normal(): Matrix3 {
    return normal(this)
  }

  /**
   * Compute a normal matrix from `this` matrix and store it into `dest`.
   *
   *
   * Please note that, if `this` is an orthogonal matrix or a matrix whose columns are orthogonal vectors,
   * then this method *need not* be invoked, since in that case `this` itself is its normal matrix.
   * In this case, use [.set] to set a given Matrix3f to this matrix.
   *
   * @see .set
   * @param dest
   * will hold the result
   * @)
   */
  fun normal(dest: Matrix3): Matrix3 {
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
    return Matrix3(
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12,
        m20 = nm20,
        m21 = nm21,
        m22 = nm22
    )
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#positiveZ(org.joml.Vector3f)
     */
  fun positiveZ(dir: Vector3f?): Vector3f? {
    dir!!.x = m10 * m21 - m11 * m20
    dir.y = m20 * m01 - m21 * m00
    dir.z = m00 * m11 - m01 * m10
    return dir.normalize(dir)
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#normalizedPositiveZ(org.joml.Vector3f)
     */
  fun normalizedPositiveZ(dir: Vector3f?): Vector3f? {
    dir!!.x = m02
    dir.y = m12
    dir.z = m22
    return dir
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#positiveX(org.joml.Vector3f)
     */
  fun positiveX(dir: Vector3f?): Vector3f? {
    dir!!.x = m11 * m22 - m12 * m21
    dir.y = m02 * m21 - m01 * m22
    dir.z = m01 * m12 - m02 * m11
    return dir.normalize(dir)
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#normalizedPositiveX(org.joml.Vector3f)
     */
  fun normalizedPositiveX(dir: Vector3f?): Vector3f? {
    dir!!.x = m00
    dir.y = m10
    dir.z = m20
    return dir
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#positiveY(org.joml.Vector3f)
     */
  fun positiveY(dir: Vector3f?): Vector3f? {
    dir!!.x = m12 * m20 - m10 * m22
    dir.y = m00 * m22 - m02 * m20
    dir.z = m02 * m10 - m00 * m12
    return dir.normalize(dir)
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#normalizedPositiveY(org.joml.Vector3f)
     */
  fun normalizedPositiveY(dir: Vector3f?): Vector3f? {
    dir!!.x = m01
    dir.y = m11
    dir.z = m21
    return dir
  }

  override fun hashCode(): Int {
    val prime = 31
    var result = 1
    result = prime * result + java.lang.Float.floatToIntBits(m00)
    result = prime * result + java.lang.Float.floatToIntBits(m01)
    result = prime * result + java.lang.Float.floatToIntBits(m02)
    result = prime * result + java.lang.Float.floatToIntBits(m10)
    result = prime * result + java.lang.Float.floatToIntBits(m11)
    result = prime * result + java.lang.Float.floatToIntBits(m12)
    result = prime * result + java.lang.Float.floatToIntBits(m20)
    result = prime * result + java.lang.Float.floatToIntBits(m21)
    result = prime * result + java.lang.Float.floatToIntBits(m22)
    return result
  }

  override fun equals(obj: Any?): Boolean {
    if (this === obj) return true
    if (obj == null) return false
    if (javaClass != obj.javaClass) return false
    val other = obj as Matrix3
    if (java.lang.Float.floatToIntBits(m00) != java.lang.Float.floatToIntBits(other.m00)) return false
    if (java.lang.Float.floatToIntBits(m01) != java.lang.Float.floatToIntBits(other.m01)) return false
    if (java.lang.Float.floatToIntBits(m02) != java.lang.Float.floatToIntBits(other.m02)) return false
    if (java.lang.Float.floatToIntBits(m10) != java.lang.Float.floatToIntBits(other.m10)) return false
    if (java.lang.Float.floatToIntBits(m11) != java.lang.Float.floatToIntBits(other.m11)) return false
    if (java.lang.Float.floatToIntBits(m12) != java.lang.Float.floatToIntBits(other.m12)) return false
    if (java.lang.Float.floatToIntBits(m20) != java.lang.Float.floatToIntBits(other.m20)) return false
    if (java.lang.Float.floatToIntBits(m21) != java.lang.Float.floatToIntBits(other.m21)) return false
    return if (java.lang.Float.floatToIntBits(m22) != java.lang.Float.floatToIntBits(other.m22)) false else true
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#add(silentorb.mythic.spatial.Matrix3, org.joml.Matrix3f)
     */
  fun add(other: Matrix3): Matrix3 {
    return Matrix3(
        m00 = m00 + other.m00,
        m01 = m01 + other.m01,
        m02 = m02 + other.m02,
        m10 = m10 + other.m10,
        m11 = m11 + other.m11,
        m12 = m12 + other.m12,
        m20 = m20 + other.m20,
        m21 = m21 + other.m21,
        m22 = m22 + other.m22
    )
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#sub(silentorb.mythic.spatial.Matrix3, org.joml.Matrix3f)
     */
  fun sub(subtrahend: Matrix3): Matrix3 {
    return Matrix3(
        m00 = m00 - subtrahend.m00,
        m01 = m01 - subtrahend.m01,
        m02 = m02 - subtrahend.m02,
        m10 = m10 - subtrahend.m10,
        m11 = m11 - subtrahend.m11,
        m12 = m12 - subtrahend.m12,
        m20 = m20 - subtrahend.m20,
        m21 = m21 - subtrahend.m21,
        m22 = m22 - subtrahend.m22
    )
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#mulComponentWise(silentorb.mythic.spatial.Matrix3, org.joml.Matrix3f)
     */
  fun mulComponentWise(other: Matrix3): Matrix3 {
    return Matrix3(
        m00 = m00 * other.m00,
        m01 = m01 * other.m01,
        m02 = m02 * other.m02,
        m10 = m10 * other.m10,
        m11 = m11 * other.m11,
        m12 = m12 * other.m12,
        m20 = m20 * other.m20,
        m21 = m21 * other.m21,
        m22 = m22 * other.m22
    )
  }

  /* (non-Javadoc)
     * @see silentorb.mythic.spatial.Matrix3#lerp(silentorb.mythic.spatial.Matrix3, float, org.joml.Matrix3f)
     */
  fun lerp(other: Matrix3, t: Float): Matrix3 {
    return Matrix3(
        m00 = m00 + (other.m00 - m00) * t,
        m01 = m01 + (other.m01 - m01) * t,
        m02 = m02 + (other.m02 - m02) * t,
        m10 = m10 + (other.m10 - m10) * t,
        m11 = m11 + (other.m11 - m11) * t,
        m12 = m12 + (other.m12 - m12) * t,
        m20 = m20 + (other.m20 - m20) * t,
        m21 = m21 + (other.m21 - m21) * t,
        m22 = m22 + (other.m22 - m22) * t
    )
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
   * This method is equivalent to calling: <tt>mul(new Matrix3f().lookAlong(-dirX, -dirY, -dirZ, upX, upY, upZ).invert(), dest)</tt>
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
   * @)
   */
  fun rotateTowards(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float): Matrix3 {
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt(dirX * dirX + dirY * dirY + (dirZ * dirZ).toDouble()).toFloat()
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
    val invLeftLength = 1.0f / Math.sqrt(leftX * leftX + leftY * leftY + (leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = ndirY * leftZ - ndirZ * leftY
    val upnY = ndirZ * leftX - ndirX * leftZ
    val upnZ = ndirX * leftY - ndirY * leftX
    val rm00 = leftX
    val rm01 = leftY
    val rm02 = leftZ
    val nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02
    val nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02
    val nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02
    val nm10 = m00 * upnX + m10 * upnY + m20 * upnZ
    val nm11 = m01 * upnX + m11 * upnY + m21 * upnZ
    val nm12 = m02 * upnX + m12 * upnY + m22 * upnZ
    return Matrix3(
        m20 = m00 * ndirX + m10 * ndirY + m20 * ndirZ,
        m21 = m01 * ndirX + m11 * ndirY + m21 * ndirZ,
        m22 = m02 * ndirX + m12 * ndirY + m22 * ndirZ,
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12
    )
  }

  /**
   * Set this matrix to a model transformation for a right-handed coordinate system,
   * that aligns the local `-z` axis with `center - eye`.
   *
   *
   * In order to apply the rotation transformation to a previous existing transformation,
   * use [rotateTowards][.rotateTowards].
   *
   *
   * This method is equivalent to calling: <tt>setLookAlong(new Vector3f(dir).negate(), up).invert()</tt>
   *
   * @see .rotationTowards
   * @see .rotateTowards
   * @param dir
   * the direction to orient the local -z axis towards
   * @param up
   * the up vector
   * @return this
   */
  fun rotationTowards(dir: Vector3f, up: Vector3f): Matrix3 {
    return rotationTowards(dir.x(), dir.y(), dir.z(), up.x(), up.y(), up.z())
  }

  /**
   * Set this matrix to a model transformation for a right-handed coordinate system,
   * that aligns the local `-z` axis with `center - eye`.
   *
   *
   * In order to apply the rotation transformation to a previous existing transformation,
   * use [rotateTowards][.rotateTowards].
   *
   *
   * This method is equivalent to calling: <tt>setLookAlong(-dirX, -dirY, -dirZ, upX, upY, upZ).invert()</tt>
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
  fun rotationTowards(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float): Matrix3 {
    // Normalize direction
    val invDirLength = 1.0f / Math.sqrt(dirX * dirX + dirY * dirY + (dirZ * dirZ).toDouble()).toFloat()
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
    val invLeftLength = 1.0f / Math.sqrt(leftX * leftX + leftY * leftY + (leftZ * leftZ).toDouble()).toFloat()
    leftX *= invLeftLength
    leftY *= invLeftLength
    leftZ *= invLeftLength
    // up = direction x left
    val upnX = ndirY * leftZ - ndirZ * leftY
    val upnY = ndirZ * leftX - ndirX * leftZ
    val upnZ = ndirX * leftY - ndirY * leftX
    return Matrix3(
        m00 = leftX,
        m01 = leftY,
        m02 = leftZ,
        m10 = upnX,
        m11 = upnY,
        m12 = upnZ,
        m20 = ndirX,
        m21 = ndirY,
        m22 = ndirZ
    )
  }

}
