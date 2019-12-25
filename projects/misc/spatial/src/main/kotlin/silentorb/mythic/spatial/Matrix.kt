package silentorb.mythic.spatial

import org.joml.Math
import org.joml.Matrix4fc
import org.joml.Vector3fc
import org.joml.Vector4f
import java.nio.ByteBuffer
import java.nio.FloatBuffer

fun matrixIdentityProperties() =
    Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_TRANSLATION or Matrix4fc.PROPERTY_ORTHONORMAL

fun mulAffine(matrix: Matrix, vector: Vector4): Vector4 {
  val rx = matrix.m00 * vector.x + matrix.m10 * vector.y + matrix.m20 * vector.z + matrix.m30 * vector.w
  val ry = matrix.m01 * vector.x + matrix.m11 * vector.y + matrix.m21 * vector.z + matrix.m31 * vector.w
  val rz = matrix.m02 * vector.x + matrix.m12 * vector.y + matrix.m22 * vector.z + matrix.m32 * vector.w
  return Vector4(rx, ry, rz, vector.w)
}

private fun mulGeneric(matrix: Matrix, vector: Vector4): Vector4f {
  val rx = matrix.m00 * vector.x + matrix.m10 * vector.y + matrix.m20 * vector.z + matrix.m30 * vector.w
  val ry = matrix.m01 * vector.x + matrix.m11 * vector.y + matrix.m21 * vector.z + matrix.m31 * vector.w
  val rz = matrix.m02 * vector.x + matrix.m12 * vector.y + matrix.m22 * vector.z + matrix.m32 * vector.w
  val rw = matrix.m03 * vector.x + matrix.m13 * vector.y + matrix.m23 * vector.z + matrix.m33 * vector.w
  return Vector4(rx, ry, rz, rw)
}

fun scaling(matrix: Matrix, x: Float, y: Float, z: Float): Matrix {
  val source = if (matrix.properties and Matrix4fc.PROPERTY_IDENTITY == 0)
    Matrix.identity
  else
    matrix

  val one = Math.abs(x) == 1.0f && Math.abs(y) == 1.0f && Math.abs(z) == 1.0f
  val properties = Matrix4fc.PROPERTY_AFFINE or if (one) Matrix4fc.PROPERTY_ORTHONORMAL else 0

  return matrix.copy(
      m00 = x,
      m11 = y,
      m22 = z,
      properties = properties
  )
}

fun scaleGeneric(matrix: Matrix, x: Float, y: Float, z: Float): Matrix {
  val one = Math.abs(x) == 1.0f && Math.abs(y) == 1.0f && Math.abs(z) == 1.0f
  val properties = matrix.properties and (Matrix4fc.PROPERTY_PERSPECTIVE
      or Matrix4fc.PROPERTY_IDENTITY
      or Matrix4fc.PROPERTY_TRANSLATION
      or (if (one) 0 else Matrix4fc.PROPERTY_ORTHONORMAL).toInt()).inv()

  return Matrix(
      matrix.m00 * x,
      matrix.m01 * x,
      matrix.m02 * x,
      matrix.m03 * x,
      matrix.m10 * y,
      matrix.m11 * y,
      matrix.m12 * y,
      matrix.m13 * y,
      matrix.m20 * z,
      matrix.m21 * z,
      matrix.m22 * z,
      matrix.m23 * z,
      matrix.m30,
      matrix.m31,
      matrix.m32,
      matrix.m33,
      properties = properties
  )
}

fun zeroMatrix(): Matrix =
    Matrix(
        m00 = 0f,
        m01 = 0f,
        m02 = 0f,
        m03 = 0f,
        m10 = 0f,
        m11 = 0f,
        m12 = 0f,
        m13 = 0f,
        m20 = 0f,
        m21 = 0f,
        m22 = 0f,
        m23 = 0f,
        m30 = 0f,
        m31 = 0f,
        m32 = 0f,
        m33 = 0f,
        properties = 0
    )

data class Matrix(
    val m00: Float,
    val m01: Float,
    val m02: Float,
    val m03: Float,
    val m10: Float,
    val m11: Float,
    val m12: Float,
    val m13: Float,
    val m20: Float,
    val m21: Float,
    val m22: Float,
    val m23: Float,
    val m30: Float,
    val m31: Float,
    val m32: Float,
    val m33: Float,
    val properties: Int
) {
  companion object {
    val identity: Matrix = zeroMatrix()
        .copy(
            m00 = 1.0f,
            m11 = 1.0f,
            m22 = 1.0f,
            m33 = 1.0f,
            properties = matrixIdentityProperties()
        )
  }

  fun identityOrThis() =
      if (properties and Matrix4fc.PROPERTY_IDENTITY == 0)
        identity
      else
        this

  // TODO: Migrate additional mul functions to remove dependency and avoid the triple conversion
  fun mul(right: Matrix): Matrix {
    return toMatrix(toMutableMatrix(this).mul(toMutableMatrix(right)))
  }

  fun rotation(angle: Float, x: Float, y: Float, z: Float): Matrix {
    val sin = Math.sin(angle.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    val C = 1.0f - cos
    val xy = x * y
    val xz = x * z
    val yz = y * z
    return Matrix(
        m00 = cos + x * x * C,
        m10 = xy * C - z * sin,
        m20 = xz * C + y * sin,
        m30 = 0.0f,
        m01 = xy * C + z * sin,
        m11 = cos + y * y * C,
        m21 = yz * C - x * sin,
        m31 = 0.0f,
        m02 = xz * C - y * sin,
        m12 = yz * C + x * sin,
        m22 = cos + z * z * C,
        m32 = 0.0f,
        m03 = 0.0f,
        m13 = 0.0f,
        m23 = 0.0f,
        m33 = 1.0f,
        properties = Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL
    )
  }

  fun rotation(quaternion: Quaternion): Matrix =
      rotation(quaternion.x, quaternion.y, quaternion.z, quaternion.w)

  fun rotation(angle: Float, axis: Vector3): Matrix =
      rotation(angle, axis.x, axis.y, axis.z)

  fun rotate(quat: Quaternion): Matrix {
    return if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      rotation(quat)
    else if (properties and Matrix4fc.PROPERTY_TRANSLATION != 0)
      rotateTranslation(quat)
    else if (properties and Matrix4fc.PROPERTY_AFFINE != 0)
      rotateAffine(quat)
    else
      rotateGeneric(quat)
  }

  fun rotateAffine(ang: Float, x: Float, y: Float, z: Float): Matrix {
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
    return Matrix(
        m20 = m00 * rm20 + m10 * rm21 + m20 * rm22,
        m21 = m01 * rm20 + m11 * rm21 + m21 * rm22,
        m22 = m02 * rm20 + m12 * rm21 + m22 * rm22,
        m23 = 0.0f,
        // set other values
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m03 = 0.0f,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12,
        m13 = 0.0f,
        m30 = m30,
        m31 = m31,
        m32 = m32,
        m33 = 1.0f,
        properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE or Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_TRANSLATION).inv()
    )
  }

  fun rotateAffine(quaternion: Quaternion): Matrix =
      rotateAffine(quaternion.x, quaternion.y, quaternion.z, quaternion.w)

  fun rotateGeneric(quaternion: Quaternion): Matrix {
    val w2 = quaternion.w * quaternion.w
    val x2 = quaternion.x * quaternion.x
    val y2 = quaternion.y * quaternion.y
    val z2 = quaternion.z * quaternion.z
    val zw = quaternion.z * quaternion.w
    val xy = quaternion.x * quaternion.y
    val xz = quaternion.x * quaternion.z
    val yw = quaternion.y * quaternion.w
    val yz = quaternion.y * quaternion.z
    val xw = quaternion.x * quaternion.w
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
    return Matrix(
        m20 = m00 * rm20 + m10 * rm21 + m20 * rm22,
        m21 = m01 * rm20 + m11 * rm21 + m21 * rm22,
        m22 = m02 * rm20 + m12 * rm21 + m22 * rm22,
        m23 = m03 * rm20 + m13 * rm21 + m23 * rm22,
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m03 = nm03,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12,
        m13 = nm13,
        m30 = m30,
        m31 = m31,
        m32 = m32,
        m33 = m33,
        properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE or Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_TRANSLATION).inv()
    )
  }

  fun rotateTowards(dirX: Float, dirY: Float, dirZ: Float, upX: Float, upY: Float, upZ: Float): Matrix {
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
    val nm00 = m00 * leftX + m10 * leftY + m20 * leftZ
    val nm01 = m01 * leftX + m11 * leftY + m21 * leftZ
    val nm02 = m02 * leftX + m12 * leftY + m22 * leftZ
    val nm03 = m03 * leftX + m13 * leftY + m23 * leftZ
    val nm10 = m00 * upnX + m10 * upnY + m20 * upnZ
    val nm11 = m01 * upnX + m11 * upnY + m21 * upnZ
    val nm12 = m02 * upnX + m12 * upnY + m22 * upnZ
    val nm13 = m03 * upnX + m13 * upnY + m23 * upnZ
    return Matrix(
        m30 = m30,
        m31 = m31,
        m32 = m32,
        m33 = m33,
        m20 = m00 * ndirX + m10 * ndirY + m20 * ndirZ,
        m21 = m01 * ndirX + m11 * ndirY + m21 * ndirZ,
        m22 = m02 * ndirX + m12 * ndirY + m22 * ndirZ,
        m23 = m03 * ndirX + m13 * ndirY + m23 * ndirZ,
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m03 = nm03,
        m10 = nm10,
        m11 = nm11,
        m12 = nm12,
        m13 = nm13,
        properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE or Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_TRANSLATION).inv()
    )
  }

  fun rotateTowards(dir: Vector3fc, up: Vector3fc): Matrix {
    return rotateTowards(dir.x, dir.y, dir.z, up.x, up.y, up.z)
  }

  fun rotateTowardsXY(dirX: Float, dirY: Float): Matrix {
    return identityOrThis().copy(
        m00 = dirY,
        m01 = dirX,
        m10 = -dirX,
        m11 = dirY,
        properties = Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL
    )
  }

  fun rotateTranslation(quaternion: Quaternion): Matrix {
    val w2 = quaternion.w * quaternion.w
    val x2 = quaternion.x * quaternion.x
    val y2 = quaternion.y * quaternion.y
    val z2 = quaternion.z * quaternion.z
    val zw = quaternion.z * quaternion.w
    val xy = quaternion.x * quaternion.y
    val xz = quaternion.x * quaternion.z
    val yw = quaternion.y * quaternion.w
    val yz = quaternion.y * quaternion.z
    val xw = quaternion.x * quaternion.w
    val rm00 = w2 + x2 - z2 - y2
    val rm01 = xy + zw + zw + xy
    val rm02 = xz - yw + xz - yw
    val rm10 = -zw + xy - zw + xy
    val rm11 = y2 - z2 + w2 - x2
    val rm12 = yz + yz + xw + xw
    val rm20 = yw + xz + xz + yw
    val rm21 = yz + yz - xw - xw
    val rm22 = z2 - y2 - x2 + w2
    return Matrix(
        m20 = rm20,
        m21 = rm21,
        m22 = rm22,
        m23 = 0.0f,
        m00 = rm00,
        m01 = rm01,
        m02 = rm02,
        m03 = 0.0f,
        m10 = rm10,
        m11 = rm11,
        m12 = rm12,
        m13 = 0.0f,
        m30 = m30,
        m31 = m31,
        m32 = m32,
        m33 = m33,
        properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE or Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_TRANSLATION).inv()
    )
  }

  fun rotationX(ang: Float): Matrix {
    val sin = Math.sin(ang.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    return identityOrThis().copy(
        m11 = cos,
        m12 = sin,
        m21 = -sin,
        m22 = cos,
        properties = Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL
    )
  }

  fun rotateX(ang: Float): Matrix {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return rotationX(ang)

    val sin = Math.sin(ang.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    val rm21 = -sin

    // add temporaries for dependent values
    val nm10 = m10 * cos + m20 * sin
    val nm11 = m11 * cos + m21 * sin
    val nm12 = m12 * cos + m22 * sin
    val nm13 = m13 * cos + m23 * sin
    // set non-dependent values directly
    return Matrix(
        m20 = m10 * rm21 + m20 * cos,
        m21 = m11 * rm21 + m21 * cos,
        m22 = m12 * rm21 + m22 * cos,
        m23 = m13 * rm21 + m23 * cos,
        // set other values
        m10 = nm10,
        m11 = nm11,
        m12 = nm12,
        m13 = nm13,
        m00 = m00,
        m01 = m01,
        m02 = m02,
        m03 = m03,
        m30 = m30,
        m31 = m31,
        m32 = m32,
        m33 = m33,
        properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE or Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_TRANSLATION).inv()
    )
  }

  fun rotationY(angle: Float): Matrix {
    val sin: Float
    val cos: Float
    sin = Math.sin(angle.toDouble()).toFloat()
    cos = Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    return identityOrThis().copy(
        m00 = cos,
        m02 = -sin,
        m20 = sin,
        m22 = cos,
        properties = Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL
    )
  }

  fun rotateY(ang: Float): Matrix {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return rotationY(ang)
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
    return Matrix(
        m20 = m00 * sin + m20 * cos,
        m21 = m01 * sin + m21 * cos,
        m22 = m02 * sin + m22 * cos,
        m23 = m03 * sin + m23 * cos,
        // set other values
        m00 = nm00,
        m01 = nm01,
        m02 = nm02,
        m03 = nm03,
        m10 = m10,
        m11 = m11,
        m12 = m12,
        m13 = m13,
        m30 = m30,
        m31 = m31,
        m32 = m32,
        m33 = m33,
        properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE or Matrix4fc.PROPERTY_IDENTITY or Matrix4fc.PROPERTY_TRANSLATION).inv()
    )
  }

  fun rotationZ(ang: Float): Matrix {
    val sin = Math.sin(ang.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
    return identityOrThis().copy(
        m00 = cos,
        m01 = sin,
        m10 = -sin,
        m11 = cos,
        properties = Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_ORTHONORMAL
    )
  }

  fun rotateZ(angle: Float): Matrix {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return rotationZ(angle)
    val sin = Math.sin(angle.toDouble()).toFloat()
    val cos = Math.cosFromSin(sin.toDouble(), angle.toDouble()).toFloat()
    return rotateTowardsXY(sin, cos)
  }

//  fun rotateZ(ang: Float): Matrix {
//    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
//      return dest.rotationZ(ang)
//    val sin = Math.sin(ang.toDouble()).toFloat()
//    val cos = Math.cosFromSin(sin.toDouble(), ang.toDouble()).toFloat()
//    return rotateTowardsXY(sin, cos, dest)
//  }

  fun scale(x: Float, y: Float, z: Float): Matrix =
      if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
        scaling(this, x, y, z)
      else
        scaleGeneric(this, x, y, z)

  fun scale(vector: Vector3): Matrix =
      scale(vector.x, vector.y, vector.z)

  fun scale(value: Float): Matrix =
      scale(value, value, value)

  fun transform(vector: Vector4): Vector4 {
    return if (properties and Matrix4fc.PROPERTY_AFFINE != 0)
      mulAffine(this, vector)
    else
      mulGeneric(this, vector)
  }

  fun translation(x: Float, y: Float, z: Float): Matrix {
    return identityOrThis().copy(
        m30 = x,
        m31 = y,
        m32 = z,
        properties = Matrix4fc.PROPERTY_AFFINE or Matrix4fc.PROPERTY_TRANSLATION or Matrix4fc.PROPERTY_ORTHONORMAL
    )
  }

  fun translate(x: Float, y: Float, z: Float): Matrix {
    if (properties and Matrix4fc.PROPERTY_IDENTITY != 0)
      return translation(x, y, z)

    return this.copy(
        m30 = m00 * x + m10 * y + m20 * z + m30,
        m31 = m01 * x + m11 * y + m21 * z + m31,
        m32 = m02 * x + m12 * y + m22 * z + m32,
        m33 = m03 * x + m13 * y + m23 * z + m33,
        properties = properties and (Matrix4fc.PROPERTY_PERSPECTIVE or Matrix4fc.PROPERTY_IDENTITY).inv()
    )
  }

  fun translate(offset: Vector3): Matrix {
    return translate(offset.x, offset.y, offset.z)
  }

  operator fun times(m: Matrix) = mul(m)
  operator fun times(v: Vector4f) = transform(v)
}

fun writeMatrixToBuffer(buffer: ByteBuffer, matrix: Matrix) {
  buffer.putFloat(matrix.m00)
  buffer.putFloat(matrix.m01)
  buffer.putFloat(matrix.m02)
  buffer.putFloat(matrix.m03)
  buffer.putFloat(matrix.m10)
  buffer.putFloat(matrix.m11)
  buffer.putFloat(matrix.m12)
  buffer.putFloat(matrix.m13)
  buffer.putFloat(matrix.m20)
  buffer.putFloat(matrix.m21)
  buffer.putFloat(matrix.m22)
  buffer.putFloat(matrix.m23)
  buffer.putFloat(matrix.m30)
  buffer.putFloat(matrix.m31)
  buffer.putFloat(matrix.m32)
  buffer.putFloat(matrix.m33)
}

fun writeMatrixToBuffer(buffer: FloatBuffer, matrix: Matrix) {
  buffer.put(matrix.m00)
  buffer.put(matrix.m01)
  buffer.put(matrix.m02)
  buffer.put(matrix.m03)
  buffer.put(matrix.m10)
  buffer.put(matrix.m11)
  buffer.put(matrix.m12)
  buffer.put(matrix.m13)
  buffer.put(matrix.m20)
  buffer.put(matrix.m21)
  buffer.put(matrix.m22)
  buffer.put(matrix.m23)
  buffer.put(matrix.m30)
  buffer.put(matrix.m31)
  buffer.put(matrix.m32)
  buffer.put(matrix.m33)
}

fun toMutableMatrix(matrix: Matrix): MutableMatrix {
  val result = MutableMatrix(
      matrix.m00,
      matrix.m01,
      matrix.m02,
      matrix.m03,
      matrix.m10,
      matrix.m11,
      matrix.m12,
      matrix.m13,
      matrix.m20,
      matrix.m21,
      matrix.m22,
      matrix.m23,
      matrix.m30,
      matrix.m31,
      matrix.m32,
      matrix.m33
  )
  result.properties = matrix.properties
  return result
}

fun toMatrix(matrix: MutableMatrix): Matrix =
    Matrix(
        matrix.m00,
        matrix.m01,
        matrix.m02,
        matrix.m03,
        matrix.m10,
        matrix.m11,
        matrix.m12,
        matrix.m13,
        matrix.m20,
        matrix.m21,
        matrix.m22,
        matrix.m23,
        matrix.m30,
        matrix.m31,
        matrix.m32,
        matrix.m33,
        properties = matrix.properties
    )
