/*
 * (C) Copyright 2016-2018 Kai Burjack

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
package org.joml.internal

import mythic.spatial.Vector3m
import org.joml.*

import java.io.IOException
import java.lang.reflect.Field
import java.nio.*

/**
 * Helper class to do efficient memory operations on all JOML objects, NIO buffers and primitive arrays.
 * This class is used internally throughout JOML, is undocumented and is subject to change.
 * Use with extreme caution!
 *
 * @author The LWJGL authors
 * @author Kai Burjack
 */
abstract class MemUtil {

  abstract fun put(m: Matrix4f, offset: Int, dest: FloatBuffer)
  abstract fun put(m: Matrix4f, offset: Int, dest: ByteBuffer)
  abstract fun put(m: Matrix4x3f, offset: Int, dest: FloatBuffer)
  abstract fun put(m: Matrix4x3f, offset: Int, dest: ByteBuffer)
  abstract fun put4x4(m: Matrix4x3f, offset: Int, dest: FloatBuffer)
  abstract fun put4x4(m: Matrix4x3f, offset: Int, dest: ByteBuffer)
  abstract fun put4x4(m: Matrix4x3d, offset: Int, dest: DoubleBuffer)
  abstract fun put4x4(m: Matrix4x3d, offset: Int, dest: ByteBuffer)
  abstract fun put4x4(m: Matrix3x2f, offset: Int, dest: FloatBuffer)
  abstract fun put4x4(m: Matrix3x2f, offset: Int, dest: ByteBuffer)
  abstract fun put4x4(m: Matrix3x2d, offset: Int, dest: DoubleBuffer)
  abstract fun put4x4(m: Matrix3x2d, offset: Int, dest: ByteBuffer)
  abstract fun put3x3(m: Matrix3x2f, offset: Int, dest: FloatBuffer)
  abstract fun put3x3(m: Matrix3x2f, offset: Int, dest: ByteBuffer)
  abstract fun put3x3(m: Matrix3x2d, offset: Int, dest: DoubleBuffer)
  abstract fun put3x3(m: Matrix3x2d, offset: Int, dest: ByteBuffer)
  abstract fun putTransposed(m: Matrix4f, offset: Int, dest: FloatBuffer)
  abstract fun putTransposed(m: Matrix4f, offset: Int, dest: ByteBuffer)
  abstract fun put4x3Transposed(m: Matrix4f, offset: Int, dest: FloatBuffer)
  abstract fun put4x3Transposed(m: Matrix4f, offset: Int, dest: ByteBuffer)
  abstract fun putTransposed(m: Matrix4x3f, offset: Int, dest: FloatBuffer)
  abstract fun putTransposed(m: Matrix4x3f, offset: Int, dest: ByteBuffer)
  abstract fun putTransposed(m: Matrix3f, offset: Int, dest: FloatBuffer)
  abstract fun putTransposed(m: Matrix3f, offset: Int, dest: ByteBuffer)
  abstract fun put(m: Matrix4d, offset: Int, dest: DoubleBuffer)
  abstract fun put(m: Matrix4d, offset: Int, dest: ByteBuffer)
  abstract fun put(m: Matrix4x3d, offset: Int, dest: DoubleBuffer)
  abstract fun put(m: Matrix4x3d, offset: Int, dest: ByteBuffer)
  abstract fun putf(m: Matrix4d, offset: Int, dest: FloatBuffer)
  abstract fun putf(m: Matrix4d, offset: Int, dest: ByteBuffer)
  abstract fun putf(m: Matrix4x3d, offset: Int, dest: FloatBuffer)
  abstract fun putf(m: Matrix4x3d, offset: Int, dest: ByteBuffer)
  abstract fun putTransposed(m: Matrix4d, offset: Int, dest: DoubleBuffer)
  abstract fun putTransposed(m: Matrix4d, offset: Int, dest: ByteBuffer)
  abstract fun put4x3Transposed(m: Matrix4d, offset: Int, dest: DoubleBuffer)
  abstract fun put4x3Transposed(m: Matrix4d, offset: Int, dest: ByteBuffer)
  abstract fun putTransposed(m: Matrix4x3d, offset: Int, dest: DoubleBuffer)
  abstract fun putTransposed(m: Matrix4x3d, offset: Int, dest: ByteBuffer)
  abstract fun putfTransposed(m: Matrix4d, offset: Int, dest: FloatBuffer)
  abstract fun putfTransposed(m: Matrix4d, offset: Int, dest: ByteBuffer)
  abstract fun putfTransposed(m: Matrix4x3d, offset: Int, dest: FloatBuffer)
  abstract fun putfTransposed(m: Matrix4x3d, offset: Int, dest: ByteBuffer)
  abstract fun put(m: Matrix3f, offset: Int, dest: FloatBuffer)
  abstract fun put(m: Matrix3f, offset: Int, dest: ByteBuffer)
  abstract fun put(m: Matrix3d, offset: Int, dest: DoubleBuffer)
  abstract fun put(m: Matrix3d, offset: Int, dest: ByteBuffer)
  abstract fun putf(m: Matrix3d, offset: Int, dest: FloatBuffer)
  abstract fun putf(m: Matrix3d, offset: Int, dest: ByteBuffer)
  abstract fun put(m: Matrix3x2f, offset: Int, dest: FloatBuffer)
  abstract fun put(m: Matrix3x2f, offset: Int, dest: ByteBuffer)
  abstract fun put(m: Matrix3x2d, offset: Int, dest: DoubleBuffer)
  abstract fun put(m: Matrix3x2d, offset: Int, dest: ByteBuffer)
  abstract fun put(src: Vector4d, offset: Int, dest: DoubleBuffer)
  abstract fun put(src: Vector4d, offset: Int, dest: ByteBuffer)
  abstract fun put(src: Vector4f, offset: Int, dest: FloatBuffer)
  abstract fun put(src: Vector4f, offset: Int, dest: ByteBuffer)
  abstract fun put(src: Vector4i, offset: Int, dest: IntBuffer)
  abstract fun put(src: Vector4i, offset: Int, dest: ByteBuffer)
  abstract fun put(src: Vector3m, offset: Int, dest: FloatBuffer)
  abstract fun put(src: Vector3m, offset: Int, dest: ByteBuffer)
  abstract fun put(src: Vector3d, offset: Int, dest: DoubleBuffer)
  abstract fun put(src: Vector3d, offset: Int, dest: ByteBuffer)
  abstract fun put(src: Vector3i, offset: Int, dest: IntBuffer)
  abstract fun put(src: Vector3i, offset: Int, dest: ByteBuffer)
  abstract fun put(src: Vector2f, offset: Int, dest: FloatBuffer)
  abstract fun put(src: Vector2f, offset: Int, dest: ByteBuffer)
  abstract fun put(src: Vector2d, offset: Int, dest: DoubleBuffer)
  abstract fun put(src: Vector2d, offset: Int, dest: ByteBuffer)
  abstract fun put(src: Vector2i, offset: Int, dest: IntBuffer)
  abstract fun put(src: Vector2i, offset: Int, dest: ByteBuffer)
  abstract operator fun get(m: Matrix4f, offset: Int, src: FloatBuffer)
  abstract operator fun get(m: Matrix4f, offset: Int, src: ByteBuffer)
  abstract operator fun get(m: Matrix4x3f, offset: Int, src: FloatBuffer)
  abstract operator fun get(m: Matrix4x3f, offset: Int, src: ByteBuffer)
  abstract operator fun get(m: Matrix4d, offset: Int, src: DoubleBuffer)
  abstract operator fun get(m: Matrix4d, offset: Int, src: ByteBuffer)
  abstract operator fun get(m: Matrix4x3d, offset: Int, src: DoubleBuffer)
  abstract operator fun get(m: Matrix4x3d, offset: Int, src: ByteBuffer)
  abstract fun getf(m: Matrix4d, offset: Int, src: FloatBuffer)
  abstract fun getf(m: Matrix4d, offset: Int, src: ByteBuffer)
  abstract fun getf(m: Matrix4x3d, offset: Int, src: FloatBuffer)
  abstract fun getf(m: Matrix4x3d, offset: Int, src: ByteBuffer)
  abstract operator fun get(m: Matrix3f, offset: Int, src: FloatBuffer)
  abstract operator fun get(m: Matrix3f, offset: Int, src: ByteBuffer)
  abstract operator fun get(m: Matrix3d, offset: Int, src: DoubleBuffer)
  abstract operator fun get(m: Matrix3d, offset: Int, src: ByteBuffer)
  abstract operator fun get(m: Matrix3x2f, offset: Int, src: FloatBuffer)
  abstract operator fun get(m: Matrix3x2f, offset: Int, src: ByteBuffer)
  abstract operator fun get(m: Matrix3x2d, offset: Int, src: DoubleBuffer)
  abstract operator fun get(m: Matrix3x2d, offset: Int, src: ByteBuffer)
  abstract fun getf(m: Matrix3d, offset: Int, src: FloatBuffer)
  abstract fun getf(m: Matrix3d, offset: Int, src: ByteBuffer)
  abstract operator fun get(dst: Vector4d, offset: Int, src: DoubleBuffer)
  abstract operator fun get(dst: Vector4d, offset: Int, src: ByteBuffer)
  abstract operator fun get(dst: Vector4f, offset: Int, src: FloatBuffer)
  abstract operator fun get(dst: Vector4f, offset: Int, src: ByteBuffer)
  abstract operator fun get(dst: Vector4i, offset: Int, src: IntBuffer)
  abstract operator fun get(dst: Vector4i, offset: Int, src: ByteBuffer)
  abstract operator fun get(dst: Vector3m, offset: Int, src: FloatBuffer)
  abstract operator fun get(dst: Vector3m, offset: Int, src: ByteBuffer)
  abstract operator fun get(dst: Vector3d, offset: Int, src: DoubleBuffer)
  abstract operator fun get(dst: Vector3d, offset: Int, src: ByteBuffer)
  abstract operator fun get(dst: Vector3i, offset: Int, src: IntBuffer)
  abstract operator fun get(dst: Vector3i, offset: Int, src: ByteBuffer)
  abstract operator fun get(dst: Vector2f, offset: Int, src: FloatBuffer)
  abstract operator fun get(dst: Vector2f, offset: Int, src: ByteBuffer)
  abstract operator fun get(dst: Vector2d, offset: Int, src: DoubleBuffer)
  abstract operator fun get(dst: Vector2d, offset: Int, src: ByteBuffer)
  abstract operator fun get(dst: Vector2i, offset: Int, src: IntBuffer)
  abstract operator fun get(dst: Vector2i, offset: Int, src: ByteBuffer)

  abstract fun copy(src: Matrix4f, dest: Matrix4f)
  abstract fun copy(src: Matrix4x3f, dest: Matrix4x3f)
  abstract fun copy(src: Matrix4f, dest: Matrix4x3f)
  abstract fun copy(src: Matrix4x3f, dest: Matrix4f)
  abstract fun copy(src: Matrix3f, dest: Matrix3f)
  abstract fun copy(src: Matrix3f, dest: Matrix4f)
  abstract fun copy(src: Matrix4f, dest: Matrix3f)
  abstract fun copy(src: Matrix3f, dest: Matrix4x3f)
  abstract fun copy(src: Matrix3x2f, dest: Matrix3x2f)
  abstract fun copy(src: Matrix3x2d, dest: Matrix3x2d)
  abstract fun copy3x3(src: Matrix4f, dest: Matrix4f)
  abstract fun copy3x3(src: Matrix4x3f, dest: Matrix4x3f)
  abstract fun copy3x3(src: Matrix3f, dest: Matrix4x3f)
  abstract fun copy3x3(src: Matrix3f, dest: Matrix4f)
  abstract fun copy4x3(src: Matrix4f, dest: Matrix4f)
  abstract fun copy4x3(src: Matrix4x3f, dest: Matrix4f)
  abstract fun copy(src: Vector4f, dst: Vector4f)
  abstract fun copy(src: Vector4i, dst: Vector4i)
  abstract fun copy(src: Quaternionf, dst: Quaternionf)
  abstract fun copy(arr: FloatArray, off: Int, dest: Matrix4f)
  abstract fun copy(arr: FloatArray, off: Int, dest: Matrix3f)
  abstract fun copy(arr: FloatArray, off: Int, dest: Matrix4x3f)
  abstract fun copy(arr: FloatArray, off: Int, dest: Matrix3x2f)
  abstract fun copy(arr: DoubleArray, off: Int, dest: Matrix3x2d)
  abstract fun copy(src: Matrix4f, dest: FloatArray, off: Int)
  abstract fun copy(src: Matrix3f, dest: FloatArray, off: Int)
  abstract fun copy(src: Matrix4x3f, dest: FloatArray, off: Int)
  abstract fun copy(src: Matrix3x2f, dest: FloatArray, off: Int)
  abstract fun copy(src: Matrix3x2d, dest: DoubleArray, off: Int)
  abstract fun copy4x4(src: Matrix4x3f, dest: FloatArray, off: Int)
  abstract fun copy4x4(src: Matrix4x3d, dest: FloatArray, off: Int)
  abstract fun copy4x4(src: Matrix4x3d, dest: DoubleArray, off: Int)
  abstract fun copy4x4(src: Matrix3x2f, dest: FloatArray, off: Int)
  abstract fun copy4x4(src: Matrix3x2d, dest: DoubleArray, off: Int)
  abstract fun copy3x3(src: Matrix3x2f, dest: FloatArray, off: Int)
  abstract fun copy3x3(src: Matrix3x2d, dest: DoubleArray, off: Int)
  abstract fun identity(dest: Matrix4f)
  abstract fun identity(dest: Matrix4x3f)
  abstract fun identity(dest: Matrix3f)
  abstract fun identity(dest: Matrix3x2f)
  abstract fun identity(dest: Matrix3x2d)
  abstract fun identity(dest: Quaternionf)
  abstract fun swap(m1: Matrix4f, m2: Matrix4f)
  abstract fun swap(m1: Matrix4x3f, m2: Matrix4x3f)
  abstract fun swap(m1: Matrix3f, m2: Matrix3f)
  abstract fun zero(dest: Matrix4f)
  abstract fun zero(dest: Matrix4x3f)
  abstract fun zero(dest: Matrix3f)
  abstract fun zero(dest: Matrix3x2f)
  abstract fun zero(dest: Matrix3x2d)
  abstract fun zero(dest: Vector4f)
  abstract fun zero(dest: Vector4i)

  abstract fun putMatrix3f(q: Quaternionf, position: Int, dest: ByteBuffer)
  abstract fun putMatrix3f(q: Quaternionf, position: Int, dest: FloatBuffer)
  abstract fun putMatrix4f(q: Quaternionf, position: Int, dest: ByteBuffer)
  abstract fun putMatrix4f(q: Quaternionf, position: Int, dest: FloatBuffer)
  abstract fun putMatrix4x3f(q: Quaternionf, position: Int, dest: ByteBuffer)
  abstract fun putMatrix4x3f(q: Quaternionf, position: Int, dest: FloatBuffer)

  abstract operator fun set(dest: Matrix4f, col0: Vector4f, col1: Vector4f, col2: Vector4f, col3: Vector4f)
  abstract operator fun set(dest: Matrix4x3f, col0: Vector3m, col1: Vector3m, col2: Vector3m, col3: Vector3m)
  abstract operator fun set(dest: Matrix3f, col0: Vector3m, col1: Vector3m, col2: Vector3m)

  abstract fun putColumn0(src: Matrix4f, dest: Vector4f)
  abstract fun putColumn1(src: Matrix4f, dest: Vector4f)
  abstract fun putColumn2(src: Matrix4f, dest: Vector4f)
  abstract fun putColumn3(src: Matrix4f, dest: Vector4f)

  abstract fun getColumn0(dest: Matrix4f, src: Vector4f)
  abstract fun getColumn1(dest: Matrix4f, src: Vector4f)
  abstract fun getColumn2(dest: Matrix4f, src: Vector4f)
  abstract fun getColumn3(dest: Matrix4f, src: Vector4f)

  abstract fun broadcast(c: Float, dest: Vector4f)
  abstract fun broadcast(c: Int, dest: Vector4i)

  open class MemUtilNIO : MemUtil() {
    fun put0(m: Matrix4f, dest: FloatBuffer) {
      dest.put(0, m.m00())
      dest.put(1, m.m01())
      dest.put(2, m.m02())
      dest.put(3, m.m03())
      dest.put(4, m.m10())
      dest.put(5, m.m11())
      dest.put(6, m.m12())
      dest.put(7, m.m13())
      dest.put(8, m.m20())
      dest.put(9, m.m21())
      dest.put(10, m.m22())
      dest.put(11, m.m23())
      dest.put(12, m.m30())
      dest.put(13, m.m31())
      dest.put(14, m.m32())
      dest.put(15, m.m33())
    }

    fun putN(m: Matrix4f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, m.m02())
      dest.put(offset + 3, m.m03())
      dest.put(offset + 4, m.m10())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, m.m12())
      dest.put(offset + 7, m.m13())
      dest.put(offset + 8, m.m20())
      dest.put(offset + 9, m.m21())
      dest.put(offset + 10, m.m22())
      dest.put(offset + 11, m.m23())
      dest.put(offset + 12, m.m30())
      dest.put(offset + 13, m.m31())
      dest.put(offset + 14, m.m32())
      dest.put(offset + 15, m.m33())
    }

    override fun put(m: Matrix4f, offset: Int, dest: FloatBuffer) {
      if (offset == 0)
        put0(m, dest)
      else
        putN(m, offset, dest)
    }

    fun put0(m: Matrix4f, dest: ByteBuffer) {
      dest.putFloat(0, m.m00())
      dest.putFloat(4, m.m01())
      dest.putFloat(8, m.m02())
      dest.putFloat(12, m.m03())
      dest.putFloat(16, m.m10())
      dest.putFloat(20, m.m11())
      dest.putFloat(24, m.m12())
      dest.putFloat(28, m.m13())
      dest.putFloat(32, m.m20())
      dest.putFloat(36, m.m21())
      dest.putFloat(40, m.m22())
      dest.putFloat(44, m.m23())
      dest.putFloat(48, m.m30())
      dest.putFloat(52, m.m31())
      dest.putFloat(56, m.m32())
      dest.putFloat(60, m.m33())
    }

    private fun putN(m: Matrix4f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00())
      dest.putFloat(offset + 4, m.m01())
      dest.putFloat(offset + 8, m.m02())
      dest.putFloat(offset + 12, m.m03())
      dest.putFloat(offset + 16, m.m10())
      dest.putFloat(offset + 20, m.m11())
      dest.putFloat(offset + 24, m.m12())
      dest.putFloat(offset + 28, m.m13())
      dest.putFloat(offset + 32, m.m20())
      dest.putFloat(offset + 36, m.m21())
      dest.putFloat(offset + 40, m.m22())
      dest.putFloat(offset + 44, m.m23())
      dest.putFloat(offset + 48, m.m30())
      dest.putFloat(offset + 52, m.m31())
      dest.putFloat(offset + 56, m.m32())
      dest.putFloat(offset + 60, m.m33())
    }

    override fun put(m: Matrix4f, offset: Int, dest: ByteBuffer) {
      if (offset == 0)
        put0(m, dest)
      else
        putN(m, offset, dest)
    }

    fun put0(m: Matrix4x3f, dest: FloatBuffer) {
      dest.put(0, m.m00())
      dest.put(1, m.m01())
      dest.put(2, m.m02())
      dest.put(3, m.m10())
      dest.put(4, m.m11())
      dest.put(5, m.m12())
      dest.put(6, m.m20())
      dest.put(7, m.m21())
      dest.put(8, m.m22())
      dest.put(9, m.m30())
      dest.put(10, m.m31())
      dest.put(11, m.m32())
    }

    fun putN(m: Matrix4x3f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, m.m02())
      dest.put(offset + 3, m.m10())
      dest.put(offset + 4, m.m11())
      dest.put(offset + 5, m.m12())
      dest.put(offset + 6, m.m20())
      dest.put(offset + 7, m.m21())
      dest.put(offset + 8, m.m22())
      dest.put(offset + 9, m.m30())
      dest.put(offset + 10, m.m31())
      dest.put(offset + 11, m.m32())
    }

    override fun put(m: Matrix4x3f, offset: Int, dest: FloatBuffer) {
      if (offset == 0)
        put0(m, dest)
      else
        putN(m, offset, dest)
    }

    fun put0(m: Matrix4x3f, dest: ByteBuffer) {
      dest.putFloat(0, m.m00())
      dest.putFloat(4, m.m01())
      dest.putFloat(8, m.m02())
      dest.putFloat(12, m.m10())
      dest.putFloat(16, m.m11())
      dest.putFloat(20, m.m12())
      dest.putFloat(24, m.m20())
      dest.putFloat(28, m.m21())
      dest.putFloat(32, m.m22())
      dest.putFloat(36, m.m30())
      dest.putFloat(40, m.m31())
      dest.putFloat(44, m.m32())
    }

    fun putN(m: Matrix4x3f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00())
      dest.putFloat(offset + 4, m.m01())
      dest.putFloat(offset + 8, m.m02())
      dest.putFloat(offset + 12, m.m10())
      dest.putFloat(offset + 16, m.m11())
      dest.putFloat(offset + 20, m.m12())
      dest.putFloat(offset + 24, m.m20())
      dest.putFloat(offset + 28, m.m21())
      dest.putFloat(offset + 32, m.m22())
      dest.putFloat(offset + 36, m.m30())
      dest.putFloat(offset + 40, m.m31())
      dest.putFloat(offset + 44, m.m32())
    }

    override fun put(m: Matrix4x3f, offset: Int, dest: ByteBuffer) {
      if (offset == 0)
        put0(m, dest)
      else
        putN(m, offset, dest)
    }

    override fun put4x4(m: Matrix4x3f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, m.m02())
      dest.put(offset + 3, 0.0f)
      dest.put(offset + 4, m.m10())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, m.m12())
      dest.put(offset + 7, 0.0f)
      dest.put(offset + 8, m.m20())
      dest.put(offset + 9, m.m21())
      dest.put(offset + 10, m.m22())
      dest.put(offset + 11, 0.0f)
      dest.put(offset + 12, m.m30())
      dest.put(offset + 13, m.m31())
      dest.put(offset + 14, m.m32())
      dest.put(offset + 15, 1.0f)
    }

    override fun put4x4(m: Matrix4x3f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00())
      dest.putFloat(offset + 4, m.m01())
      dest.putFloat(offset + 8, m.m02())
      dest.putFloat(offset + 12, 0.0f)
      dest.putFloat(offset + 16, m.m10())
      dest.putFloat(offset + 20, m.m11())
      dest.putFloat(offset + 24, m.m12())
      dest.putFloat(offset + 28, 0.0f)
      dest.putFloat(offset + 32, m.m20())
      dest.putFloat(offset + 36, m.m21())
      dest.putFloat(offset + 40, m.m22())
      dest.putFloat(offset + 44, 0.0f)
      dest.putFloat(offset + 48, m.m30())
      dest.putFloat(offset + 52, m.m31())
      dest.putFloat(offset + 56, m.m32())
      dest.putFloat(offset + 60, 1.0f)
    }

    override fun put4x4(m: Matrix4x3d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, m.m02())
      dest.put(offset + 3, 0.0)
      dest.put(offset + 4, m.m10())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, m.m12())
      dest.put(offset + 7, 0.0)
      dest.put(offset + 8, m.m20())
      dest.put(offset + 9, m.m21())
      dest.put(offset + 10, m.m22())
      dest.put(offset + 11, 0.0)
      dest.put(offset + 12, m.m30())
      dest.put(offset + 13, m.m31())
      dest.put(offset + 14, m.m32())
      dest.put(offset + 15, 1.0)
    }

    override fun put4x4(m: Matrix4x3d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, m.m00())
      dest.putDouble(offset + 4, m.m01())
      dest.putDouble(offset + 8, m.m02())
      dest.putDouble(offset + 12, 0.0)
      dest.putDouble(offset + 16, m.m10())
      dest.putDouble(offset + 20, m.m11())
      dest.putDouble(offset + 24, m.m12())
      dest.putDouble(offset + 28, 0.0)
      dest.putDouble(offset + 32, m.m20())
      dest.putDouble(offset + 36, m.m21())
      dest.putDouble(offset + 40, m.m22())
      dest.putDouble(offset + 44, 0.0)
      dest.putDouble(offset + 48, m.m30())
      dest.putDouble(offset + 52, m.m31())
      dest.putDouble(offset + 56, m.m32())
      dest.putDouble(offset + 60, 1.0)
    }

    override fun put4x4(m: Matrix3x2f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, 0.0f)
      dest.put(offset + 3, 0.0f)
      dest.put(offset + 4, m.m10())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, 0.0f)
      dest.put(offset + 7, 0.0f)
      dest.put(offset + 8, 0.0f)
      dest.put(offset + 9, 0.0f)
      dest.put(offset + 10, 1.0f)
      dest.put(offset + 11, 0.0f)
      dest.put(offset + 12, m.m20())
      dest.put(offset + 13, m.m21())
      dest.put(offset + 14, 0.0f)
      dest.put(offset + 15, 1.0f)
    }

    override fun put4x4(m: Matrix3x2f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00())
      dest.putFloat(offset + 4, m.m01())
      dest.putFloat(offset + 8, 0.0f)
      dest.putFloat(offset + 12, 0.0f)
      dest.putFloat(offset + 16, m.m10())
      dest.putFloat(offset + 20, m.m11())
      dest.putFloat(offset + 24, 0.0f)
      dest.putFloat(offset + 28, 0.0f)
      dest.putFloat(offset + 32, 0.0f)
      dest.putFloat(offset + 36, 0.0f)
      dest.putFloat(offset + 40, 1.0f)
      dest.putFloat(offset + 44, 0.0f)
      dest.putFloat(offset + 48, m.m20())
      dest.putFloat(offset + 52, m.m21())
      dest.putFloat(offset + 56, 0.0f)
      dest.putFloat(offset + 60, 1.0f)
    }

    override fun put4x4(m: Matrix3x2d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, 0.0)
      dest.put(offset + 3, 0.0)
      dest.put(offset + 4, m.m10())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, 0.0)
      dest.put(offset + 7, 0.0)
      dest.put(offset + 8, 0.0)
      dest.put(offset + 9, 0.0)
      dest.put(offset + 10, 1.0)
      dest.put(offset + 11, 0.0)
      dest.put(offset + 12, m.m20())
      dest.put(offset + 13, m.m21())
      dest.put(offset + 14, 0.0)
      dest.put(offset + 15, 1.0)
    }

    override fun put4x4(m: Matrix3x2d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, m.m00())
      dest.putDouble(offset + 8, m.m01())
      dest.putDouble(offset + 16, 0.0)
      dest.putDouble(offset + 24, 0.0)
      dest.putDouble(offset + 32, m.m10())
      dest.putDouble(offset + 40, m.m11())
      dest.putDouble(offset + 48, 0.0)
      dest.putDouble(offset + 56, 0.0)
      dest.putDouble(offset + 64, 0.0)
      dest.putDouble(offset + 72, 0.0)
      dest.putDouble(offset + 80, 1.0)
      dest.putDouble(offset + 88, 0.0)
      dest.putDouble(offset + 96, m.m20())
      dest.putDouble(offset + 104, m.m21())
      dest.putDouble(offset + 112, 0.0)
      dest.putDouble(offset + 120, 1.0)
    }

    override fun put3x3(m: Matrix3x2f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, 0.0f)
      dest.put(offset + 3, m.m10())
      dest.put(offset + 4, m.m11())
      dest.put(offset + 5, 0.0f)
      dest.put(offset + 6, m.m20())
      dest.put(offset + 7, m.m21())
      dest.put(offset + 8, 1.0f)
    }

    override fun put3x3(m: Matrix3x2f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00())
      dest.putFloat(offset + 4, m.m01())
      dest.putFloat(offset + 8, 0.0f)
      dest.putFloat(offset + 12, m.m10())
      dest.putFloat(offset + 16, m.m11())
      dest.putFloat(offset + 20, 0.0f)
      dest.putFloat(offset + 24, m.m20())
      dest.putFloat(offset + 28, m.m21())
      dest.putFloat(offset + 32, 1.0f)
    }

    override fun put3x3(m: Matrix3x2d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, 0.0)
      dest.put(offset + 3, m.m10())
      dest.put(offset + 4, m.m11())
      dest.put(offset + 5, 0.0)
      dest.put(offset + 6, m.m20())
      dest.put(offset + 7, m.m21())
      dest.put(offset + 8, 1.0)
    }

    override fun put3x3(m: Matrix3x2d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, m.m00())
      dest.putDouble(offset + 8, m.m01())
      dest.putDouble(offset + 16, 0.0)
      dest.putDouble(offset + 24, m.m10())
      dest.putDouble(offset + 32, m.m11())
      dest.putDouble(offset + 40, 0.0)
      dest.putDouble(offset + 48, m.m20())
      dest.putDouble(offset + 56, m.m21())
      dest.putDouble(offset + 64, 1.0)
    }

    override fun putTransposed(m: Matrix4f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m10())
      dest.put(offset + 2, m.m20())
      dest.put(offset + 3, m.m30())
      dest.put(offset + 4, m.m01())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, m.m21())
      dest.put(offset + 7, m.m31())
      dest.put(offset + 8, m.m02())
      dest.put(offset + 9, m.m12())
      dest.put(offset + 10, m.m22())
      dest.put(offset + 11, m.m32())
      dest.put(offset + 12, m.m03())
      dest.put(offset + 13, m.m13())
      dest.put(offset + 14, m.m23())
      dest.put(offset + 15, m.m33())
    }

    override fun putTransposed(m: Matrix4f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00())
      dest.putFloat(offset + 4, m.m10())
      dest.putFloat(offset + 8, m.m20())
      dest.putFloat(offset + 12, m.m30())
      dest.putFloat(offset + 16, m.m01())
      dest.putFloat(offset + 20, m.m11())
      dest.putFloat(offset + 24, m.m21())
      dest.putFloat(offset + 28, m.m31())
      dest.putFloat(offset + 32, m.m02())
      dest.putFloat(offset + 36, m.m12())
      dest.putFloat(offset + 40, m.m22())
      dest.putFloat(offset + 44, m.m32())
      dest.putFloat(offset + 48, m.m03())
      dest.putFloat(offset + 52, m.m13())
      dest.putFloat(offset + 56, m.m23())
      dest.putFloat(offset + 60, m.m33())
    }

    override fun put4x3Transposed(m: Matrix4f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m10())
      dest.put(offset + 2, m.m20())
      dest.put(offset + 3, m.m30())
      dest.put(offset + 4, m.m01())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, m.m21())
      dest.put(offset + 7, m.m31())
      dest.put(offset + 8, m.m02())
      dest.put(offset + 9, m.m12())
      dest.put(offset + 10, m.m22())
      dest.put(offset + 11, m.m32())
    }

    override fun put4x3Transposed(m: Matrix4f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00())
      dest.putFloat(offset + 4, m.m10())
      dest.putFloat(offset + 8, m.m20())
      dest.putFloat(offset + 12, m.m30())
      dest.putFloat(offset + 16, m.m01())
      dest.putFloat(offset + 20, m.m11())
      dest.putFloat(offset + 24, m.m21())
      dest.putFloat(offset + 28, m.m31())
      dest.putFloat(offset + 32, m.m02())
      dest.putFloat(offset + 36, m.m12())
      dest.putFloat(offset + 40, m.m22())
      dest.putFloat(offset + 44, m.m32())
    }

    override fun putTransposed(m: Matrix4x3f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m10())
      dest.put(offset + 2, m.m20())
      dest.put(offset + 3, m.m30())
      dest.put(offset + 4, m.m01())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, m.m21())
      dest.put(offset + 7, m.m31())
      dest.put(offset + 8, m.m02())
      dest.put(offset + 9, m.m12())
      dest.put(offset + 10, m.m22())
      dest.put(offset + 11, m.m32())
    }

    override fun putTransposed(m: Matrix4x3f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00())
      dest.putFloat(offset + 4, m.m10())
      dest.putFloat(offset + 8, m.m20())
      dest.putFloat(offset + 12, m.m30())
      dest.putFloat(offset + 16, m.m01())
      dest.putFloat(offset + 20, m.m11())
      dest.putFloat(offset + 24, m.m21())
      dest.putFloat(offset + 28, m.m31())
      dest.putFloat(offset + 32, m.m02())
      dest.putFloat(offset + 36, m.m12())
      dest.putFloat(offset + 40, m.m22())
      dest.putFloat(offset + 44, m.m32())
    }

    override fun putTransposed(m: Matrix3f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m10())
      dest.put(offset + 2, m.m20())
      dest.put(offset + 3, m.m01())
      dest.put(offset + 4, m.m11())
      dest.put(offset + 5, m.m21())
      dest.put(offset + 6, m.m02())
      dest.put(offset + 7, m.m12())
      dest.put(offset + 8, m.m22())
    }

    override fun putTransposed(m: Matrix3f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00())
      dest.putFloat(offset + 4, m.m10())
      dest.putFloat(offset + 8, m.m20())
      dest.putFloat(offset + 12, m.m01())
      dest.putFloat(offset + 16, m.m11())
      dest.putFloat(offset + 20, m.m21())
      dest.putFloat(offset + 24, m.m02())
      dest.putFloat(offset + 28, m.m12())
      dest.putFloat(offset + 32, m.m22())
    }

    override fun put(m: Matrix4d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, m.m02())
      dest.put(offset + 3, m.m03())
      dest.put(offset + 4, m.m10())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, m.m12())
      dest.put(offset + 7, m.m13())
      dest.put(offset + 8, m.m20())
      dest.put(offset + 9, m.m21())
      dest.put(offset + 10, m.m22())
      dest.put(offset + 11, m.m23())
      dest.put(offset + 12, m.m30())
      dest.put(offset + 13, m.m31())
      dest.put(offset + 14, m.m32())
      dest.put(offset + 15, m.m33())
    }

    override fun put(m: Matrix4d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, m.m00())
      dest.putDouble(offset + 4, m.m01())
      dest.putDouble(offset + 8, m.m02())
      dest.putDouble(offset + 12, m.m03())
      dest.putDouble(offset + 16, m.m10())
      dest.putDouble(offset + 20, m.m11())
      dest.putDouble(offset + 24, m.m12())
      dest.putDouble(offset + 28, m.m13())
      dest.putDouble(offset + 32, m.m20())
      dest.putDouble(offset + 36, m.m21())
      dest.putDouble(offset + 40, m.m22())
      dest.putDouble(offset + 44, m.m23())
      dest.putDouble(offset + 48, m.m30())
      dest.putDouble(offset + 52, m.m31())
      dest.putDouble(offset + 56, m.m32())
      dest.putDouble(offset + 60, m.m33())
    }

    override fun put(m: Matrix4x3d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, m.m02())
      dest.put(offset + 3, m.m10())
      dest.put(offset + 4, m.m11())
      dest.put(offset + 5, m.m12())
      dest.put(offset + 6, m.m20())
      dest.put(offset + 7, m.m21())
      dest.put(offset + 8, m.m22())
      dest.put(offset + 9, m.m30())
      dest.put(offset + 10, m.m31())
      dest.put(offset + 11, m.m32())
    }

    override fun put(m: Matrix4x3d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, m.m00())
      dest.putDouble(offset + 4, m.m01())
      dest.putDouble(offset + 8, m.m02())
      dest.putDouble(offset + 12, m.m10())
      dest.putDouble(offset + 16, m.m11())
      dest.putDouble(offset + 20, m.m12())
      dest.putDouble(offset + 24, m.m20())
      dest.putDouble(offset + 28, m.m21())
      dest.putDouble(offset + 32, m.m22())
      dest.putDouble(offset + 36, m.m30())
      dest.putDouble(offset + 40, m.m31())
      dest.putDouble(offset + 44, m.m32())
    }

    override fun putf(m: Matrix4d, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00().toFloat())
      dest.put(offset + 1, m.m01().toFloat())
      dest.put(offset + 2, m.m02().toFloat())
      dest.put(offset + 3, m.m03().toFloat())
      dest.put(offset + 4, m.m10().toFloat())
      dest.put(offset + 5, m.m11().toFloat())
      dest.put(offset + 6, m.m12().toFloat())
      dest.put(offset + 7, m.m13().toFloat())
      dest.put(offset + 8, m.m20().toFloat())
      dest.put(offset + 9, m.m21().toFloat())
      dest.put(offset + 10, m.m22().toFloat())
      dest.put(offset + 11, m.m23().toFloat())
      dest.put(offset + 12, m.m30().toFloat())
      dest.put(offset + 13, m.m31().toFloat())
      dest.put(offset + 14, m.m32().toFloat())
      dest.put(offset + 15, m.m33().toFloat())
    }

    override fun putf(m: Matrix4d, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00().toFloat())
      dest.putFloat(offset + 4, m.m01().toFloat())
      dest.putFloat(offset + 8, m.m02().toFloat())
      dest.putFloat(offset + 12, m.m03().toFloat())
      dest.putFloat(offset + 16, m.m10().toFloat())
      dest.putFloat(offset + 20, m.m11().toFloat())
      dest.putFloat(offset + 24, m.m12().toFloat())
      dest.putFloat(offset + 28, m.m13().toFloat())
      dest.putFloat(offset + 32, m.m20().toFloat())
      dest.putFloat(offset + 36, m.m21().toFloat())
      dest.putFloat(offset + 40, m.m22().toFloat())
      dest.putFloat(offset + 44, m.m23().toFloat())
      dest.putFloat(offset + 48, m.m30().toFloat())
      dest.putFloat(offset + 52, m.m31().toFloat())
      dest.putFloat(offset + 56, m.m32().toFloat())
      dest.putFloat(offset + 60, m.m33().toFloat())
    }

    override fun putf(m: Matrix4x3d, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00().toFloat())
      dest.put(offset + 1, m.m01().toFloat())
      dest.put(offset + 2, m.m02().toFloat())
      dest.put(offset + 3, m.m10().toFloat())
      dest.put(offset + 4, m.m11().toFloat())
      dest.put(offset + 5, m.m12().toFloat())
      dest.put(offset + 6, m.m20().toFloat())
      dest.put(offset + 7, m.m21().toFloat())
      dest.put(offset + 8, m.m22().toFloat())
      dest.put(offset + 9, m.m30().toFloat())
      dest.put(offset + 10, m.m31().toFloat())
      dest.put(offset + 11, m.m32().toFloat())
    }

    override fun putf(m: Matrix4x3d, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00().toFloat())
      dest.putFloat(offset + 4, m.m01().toFloat())
      dest.putFloat(offset + 8, m.m02().toFloat())
      dest.putFloat(offset + 12, m.m10().toFloat())
      dest.putFloat(offset + 16, m.m11().toFloat())
      dest.putFloat(offset + 20, m.m12().toFloat())
      dest.putFloat(offset + 24, m.m20().toFloat())
      dest.putFloat(offset + 28, m.m21().toFloat())
      dest.putFloat(offset + 32, m.m22().toFloat())
      dest.putFloat(offset + 36, m.m30().toFloat())
      dest.putFloat(offset + 40, m.m31().toFloat())
      dest.putFloat(offset + 44, m.m32().toFloat())
    }

    override fun putTransposed(m: Matrix4d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m10())
      dest.put(offset + 2, m.m20())
      dest.put(offset + 3, m.m30())
      dest.put(offset + 4, m.m01())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, m.m21())
      dest.put(offset + 7, m.m31())
      dest.put(offset + 8, m.m02())
      dest.put(offset + 9, m.m12())
      dest.put(offset + 10, m.m22())
      dest.put(offset + 11, m.m32())
      dest.put(offset + 12, m.m03())
      dest.put(offset + 13, m.m13())
      dest.put(offset + 14, m.m23())
      dest.put(offset + 15, m.m33())
    }

    override fun putTransposed(m: Matrix4d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, m.m00())
      dest.putDouble(offset + 8, m.m10())
      dest.putDouble(offset + 16, m.m20())
      dest.putDouble(offset + 24, m.m30())
      dest.putDouble(offset + 32, m.m01())
      dest.putDouble(offset + 40, m.m11())
      dest.putDouble(offset + 48, m.m21())
      dest.putDouble(offset + 56, m.m31())
      dest.putDouble(offset + 64, m.m02())
      dest.putDouble(offset + 72, m.m12())
      dest.putDouble(offset + 80, m.m22())
      dest.putDouble(offset + 88, m.m32())
      dest.putDouble(offset + 96, m.m03())
      dest.putDouble(offset + 104, m.m13())
      dest.putDouble(offset + 112, m.m23())
      dest.putDouble(offset + 120, m.m33())
    }

    override fun put4x3Transposed(m: Matrix4d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m10())
      dest.put(offset + 2, m.m20())
      dest.put(offset + 3, m.m30())
      dest.put(offset + 4, m.m01())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, m.m21())
      dest.put(offset + 7, m.m31())
      dest.put(offset + 8, m.m02())
      dest.put(offset + 9, m.m12())
      dest.put(offset + 10, m.m22())
      dest.put(offset + 11, m.m32())
    }

    override fun put4x3Transposed(m: Matrix4d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, m.m00())
      dest.putDouble(offset + 8, m.m10())
      dest.putDouble(offset + 16, m.m20())
      dest.putDouble(offset + 24, m.m30())
      dest.putDouble(offset + 32, m.m01())
      dest.putDouble(offset + 40, m.m11())
      dest.putDouble(offset + 48, m.m21())
      dest.putDouble(offset + 56, m.m31())
      dest.putDouble(offset + 64, m.m02())
      dest.putDouble(offset + 72, m.m12())
      dest.putDouble(offset + 80, m.m22())
      dest.putDouble(offset + 88, m.m32())
    }

    override fun putTransposed(m: Matrix4x3d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m10())
      dest.put(offset + 2, m.m20())
      dest.put(offset + 3, m.m30())
      dest.put(offset + 4, m.m01())
      dest.put(offset + 5, m.m11())
      dest.put(offset + 6, m.m21())
      dest.put(offset + 7, m.m31())
      dest.put(offset + 8, m.m02())
      dest.put(offset + 9, m.m12())
      dest.put(offset + 10, m.m22())
      dest.put(offset + 11, m.m32())
    }

    override fun putTransposed(m: Matrix4x3d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, m.m00())
      dest.putDouble(offset + 4, m.m10())
      dest.putDouble(offset + 8, m.m20())
      dest.putDouble(offset + 12, m.m30())
      dest.putDouble(offset + 16, m.m01())
      dest.putDouble(offset + 20, m.m11())
      dest.putDouble(offset + 24, m.m21())
      dest.putDouble(offset + 28, m.m31())
      dest.putDouble(offset + 32, m.m02())
      dest.putDouble(offset + 36, m.m12())
      dest.putDouble(offset + 40, m.m22())
      dest.putDouble(offset + 44, m.m32())
    }

    override fun putfTransposed(m: Matrix4x3d, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00().toFloat())
      dest.put(offset + 1, m.m10().toFloat())
      dest.put(offset + 2, m.m20().toFloat())
      dest.put(offset + 3, m.m30().toFloat())
      dest.put(offset + 4, m.m01().toFloat())
      dest.put(offset + 5, m.m11().toFloat())
      dest.put(offset + 6, m.m21().toFloat())
      dest.put(offset + 7, m.m31().toFloat())
      dest.put(offset + 8, m.m02().toFloat())
      dest.put(offset + 9, m.m12().toFloat())
      dest.put(offset + 10, m.m22().toFloat())
      dest.put(offset + 11, m.m32().toFloat())
    }

    override fun putfTransposed(m: Matrix4x3d, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00().toFloat())
      dest.putFloat(offset + 4, m.m10().toFloat())
      dest.putFloat(offset + 8, m.m20().toFloat())
      dest.putFloat(offset + 12, m.m30().toFloat())
      dest.putFloat(offset + 16, m.m01().toFloat())
      dest.putFloat(offset + 20, m.m11().toFloat())
      dest.putFloat(offset + 24, m.m21().toFloat())
      dest.putFloat(offset + 28, m.m31().toFloat())
      dest.putFloat(offset + 32, m.m02().toFloat())
      dest.putFloat(offset + 36, m.m12().toFloat())
      dest.putFloat(offset + 40, m.m22().toFloat())
      dest.putFloat(offset + 44, m.m32().toFloat())
    }

    override fun putfTransposed(m: Matrix4d, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00().toFloat())
      dest.put(offset + 1, m.m10().toFloat())
      dest.put(offset + 2, m.m20().toFloat())
      dest.put(offset + 3, m.m30().toFloat())
      dest.put(offset + 4, m.m01().toFloat())
      dest.put(offset + 5, m.m11().toFloat())
      dest.put(offset + 6, m.m21().toFloat())
      dest.put(offset + 7, m.m31().toFloat())
      dest.put(offset + 8, m.m02().toFloat())
      dest.put(offset + 9, m.m12().toFloat())
      dest.put(offset + 10, m.m22().toFloat())
      dest.put(offset + 11, m.m32().toFloat())
      dest.put(offset + 12, m.m03().toFloat())
      dest.put(offset + 13, m.m13().toFloat())
      dest.put(offset + 14, m.m23().toFloat())
      dest.put(offset + 15, m.m33().toFloat())
    }

    override fun putfTransposed(m: Matrix4d, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00().toFloat())
      dest.putFloat(offset + 4, m.m10().toFloat())
      dest.putFloat(offset + 8, m.m20().toFloat())
      dest.putFloat(offset + 12, m.m30().toFloat())
      dest.putFloat(offset + 16, m.m01().toFloat())
      dest.putFloat(offset + 20, m.m11().toFloat())
      dest.putFloat(offset + 24, m.m21().toFloat())
      dest.putFloat(offset + 28, m.m31().toFloat())
      dest.putFloat(offset + 32, m.m02().toFloat())
      dest.putFloat(offset + 36, m.m12().toFloat())
      dest.putFloat(offset + 40, m.m22().toFloat())
      dest.putFloat(offset + 44, m.m32().toFloat())
      dest.putFloat(offset + 48, m.m03().toFloat())
      dest.putFloat(offset + 52, m.m13().toFloat())
      dest.putFloat(offset + 56, m.m23().toFloat())
      dest.putFloat(offset + 60, m.m33().toFloat())
    }

    override fun put(m: Matrix3f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, m.m02())
      dest.put(offset + 3, m.m10())
      dest.put(offset + 4, m.m11())
      dest.put(offset + 5, m.m12())
      dest.put(offset + 6, m.m20())
      dest.put(offset + 7, m.m21())
      dest.put(offset + 8, m.m22())
    }

    override fun put(m: Matrix3f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00())
      dest.putFloat(offset + 4, m.m01())
      dest.putFloat(offset + 8, m.m02())
      dest.putFloat(offset + 12, m.m10())
      dest.putFloat(offset + 16, m.m11())
      dest.putFloat(offset + 20, m.m12())
      dest.putFloat(offset + 24, m.m20())
      dest.putFloat(offset + 28, m.m21())
      dest.putFloat(offset + 32, m.m22())
    }

    override fun put(m: Matrix3d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, m.m02())
      dest.put(offset + 3, m.m10())
      dest.put(offset + 4, m.m11())
      dest.put(offset + 5, m.m12())
      dest.put(offset + 6, m.m20())
      dest.put(offset + 7, m.m21())
      dest.put(offset + 8, m.m22())
    }

    override fun put(m: Matrix3d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, m.m00())
      dest.putDouble(offset + 8, m.m01())
      dest.putDouble(offset + 16, m.m02())
      dest.putDouble(offset + 24, m.m10())
      dest.putDouble(offset + 32, m.m11())
      dest.putDouble(offset + 40, m.m12())
      dest.putDouble(offset + 48, m.m20())
      dest.putDouble(offset + 56, m.m21())
      dest.putDouble(offset + 64, m.m22())
    }

    override fun put(m: Matrix3x2f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, m.m10())
      dest.put(offset + 3, m.m11())
      dest.put(offset + 4, m.m20())
      dest.put(offset + 5, m.m21())
    }

    override fun put(m: Matrix3x2f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00())
      dest.putFloat(offset + 4, m.m01())
      dest.putFloat(offset + 8, m.m10())
      dest.putFloat(offset + 12, m.m11())
      dest.putFloat(offset + 16, m.m20())
      dest.putFloat(offset + 20, m.m21())
    }

    override fun put(m: Matrix3x2d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, m.m00())
      dest.put(offset + 1, m.m01())
      dest.put(offset + 2, m.m10())
      dest.put(offset + 3, m.m11())
      dest.put(offset + 4, m.m20())
      dest.put(offset + 5, m.m21())
    }

    override fun put(m: Matrix3x2d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, m.m00())
      dest.putDouble(offset + 8, m.m01())
      dest.putDouble(offset + 16, m.m10())
      dest.putDouble(offset + 24, m.m11())
      dest.putDouble(offset + 32, m.m20())
      dest.putDouble(offset + 40, m.m21())
    }

    override fun putf(m: Matrix3d, offset: Int, dest: FloatBuffer) {
      dest.put(offset, m.m00().toFloat())
      dest.put(offset + 1, m.m01().toFloat())
      dest.put(offset + 2, m.m02().toFloat())
      dest.put(offset + 3, m.m10().toFloat())
      dest.put(offset + 4, m.m11().toFloat())
      dest.put(offset + 5, m.m12().toFloat())
      dest.put(offset + 6, m.m20().toFloat())
      dest.put(offset + 7, m.m21().toFloat())
      dest.put(offset + 8, m.m22().toFloat())
    }

    override fun putf(m: Matrix3d, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, m.m00().toFloat())
      dest.putFloat(offset + 4, m.m01().toFloat())
      dest.putFloat(offset + 8, m.m02().toFloat())
      dest.putFloat(offset + 12, m.m10().toFloat())
      dest.putFloat(offset + 16, m.m11().toFloat())
      dest.putFloat(offset + 20, m.m12().toFloat())
      dest.putFloat(offset + 24, m.m20().toFloat())
      dest.putFloat(offset + 28, m.m21().toFloat())
      dest.putFloat(offset + 32, m.m22().toFloat())
    }

    override fun put(src: Vector4d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, src.x)
      dest.put(offset + 1, src.y)
      dest.put(offset + 2, src.z)
      dest.put(offset + 3, src.w)
    }

    override fun put(src: Vector4d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, src.x)
      dest.putDouble(offset + 8, src.y)
      dest.putDouble(offset + 16, src.z)
      dest.putDouble(offset + 24, src.w)
    }

    override fun put(src: Vector4f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, src.x)
      dest.put(offset + 1, src.y)
      dest.put(offset + 2, src.z)
      dest.put(offset + 3, src.w)
    }

    override fun put(src: Vector4f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, src.x)
      dest.putFloat(offset + 4, src.y)
      dest.putFloat(offset + 8, src.z)
      dest.putFloat(offset + 12, src.w)
    }

    override fun put(src: Vector4i, offset: Int, dest: IntBuffer) {
      dest.put(offset, src.x)
      dest.put(offset + 1, src.y)
      dest.put(offset + 2, src.z)
      dest.put(offset + 3, src.w)
    }

    override fun put(src: Vector4i, offset: Int, dest: ByteBuffer) {
      dest.putInt(offset, src.x)
      dest.putInt(offset + 4, src.y)
      dest.putInt(offset + 8, src.z)
      dest.putInt(offset + 12, src.w)
    }

    override fun put(src: Vector3m, offset: Int, dest: FloatBuffer) {
      dest.put(offset, src.x)
      dest.put(offset + 1, src.y)
      dest.put(offset + 2, src.z)
    }

    override fun put(src: Vector3m, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, src.x)
      dest.putFloat(offset + 4, src.y)
      dest.putFloat(offset + 8, src.z)
    }

    override fun put(src: Vector3d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, src.x)
      dest.put(offset + 1, src.y)
      dest.put(offset + 2, src.z)
    }

    override fun put(src: Vector3d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, src.x)
      dest.putDouble(offset + 8, src.y)
      dest.putDouble(offset + 16, src.z)
    }

    override fun put(src: Vector3i, offset: Int, dest: IntBuffer) {
      dest.put(offset, src.x)
      dest.put(offset + 1, src.y)
      dest.put(offset + 2, src.z)
    }

    override fun put(src: Vector3i, offset: Int, dest: ByteBuffer) {
      dest.putInt(offset, src.x)
      dest.putInt(offset + 4, src.y)
      dest.putInt(offset + 8, src.z)
    }

    override fun put(src: Vector2f, offset: Int, dest: FloatBuffer) {
      dest.put(offset, src.x)
      dest.put(offset + 1, src.y)
    }

    override fun put(src: Vector2f, offset: Int, dest: ByteBuffer) {
      dest.putFloat(offset, src.x)
      dest.putFloat(offset + 4, src.y)
    }

    override fun put(src: Vector2d, offset: Int, dest: DoubleBuffer) {
      dest.put(offset, src.x)
      dest.put(offset + 1, src.y)
    }

    override fun put(src: Vector2d, offset: Int, dest: ByteBuffer) {
      dest.putDouble(offset, src.x)
      dest.putDouble(offset + 8, src.y)
    }

    override fun put(src: Vector2i, offset: Int, dest: IntBuffer) {
      dest.put(offset, src.x)
      dest.put(offset + 1, src.y)
    }

    override fun put(src: Vector2i, offset: Int, dest: ByteBuffer) {
      dest.putInt(offset, src.x)
      dest.putInt(offset + 4, src.y)
    }

    override fun get(m: Matrix4f, offset: Int, src: FloatBuffer) {
      m._m00(src.get(offset))
      m._m01(src.get(offset + 1))
      m._m02(src.get(offset + 2))
      m._m03(src.get(offset + 3))
      m._m10(src.get(offset + 4))
      m._m11(src.get(offset + 5))
      m._m12(src.get(offset + 6))
      m._m13(src.get(offset + 7))
      m._m20(src.get(offset + 8))
      m._m21(src.get(offset + 9))
      m._m22(src.get(offset + 10))
      m._m23(src.get(offset + 11))
      m._m30(src.get(offset + 12))
      m._m31(src.get(offset + 13))
      m._m32(src.get(offset + 14))
      m._m33(src.get(offset + 15))
    }

    override fun get(m: Matrix4f, offset: Int, src: ByteBuffer) {
      m._m00(src.getFloat(offset))
      m._m01(src.getFloat(offset + 4))
      m._m02(src.getFloat(offset + 8))
      m._m03(src.getFloat(offset + 12))
      m._m10(src.getFloat(offset + 16))
      m._m11(src.getFloat(offset + 20))
      m._m12(src.getFloat(offset + 24))
      m._m13(src.getFloat(offset + 28))
      m._m20(src.getFloat(offset + 32))
      m._m21(src.getFloat(offset + 36))
      m._m22(src.getFloat(offset + 40))
      m._m23(src.getFloat(offset + 44))
      m._m30(src.getFloat(offset + 48))
      m._m31(src.getFloat(offset + 52))
      m._m32(src.getFloat(offset + 56))
      m._m33(src.getFloat(offset + 60))
    }

    override fun get(m: Matrix4x3f, offset: Int, src: FloatBuffer) {
      m._m00(src.get(offset))
      m._m01(src.get(offset + 1))
      m._m02(src.get(offset + 2))
      m._m10(src.get(offset + 3))
      m._m11(src.get(offset + 4))
      m._m12(src.get(offset + 5))
      m._m20(src.get(offset + 6))
      m._m21(src.get(offset + 7))
      m._m22(src.get(offset + 8))
      m._m30(src.get(offset + 9))
      m._m31(src.get(offset + 10))
      m._m32(src.get(offset + 11))
    }

    override fun get(m: Matrix4x3f, offset: Int, src: ByteBuffer) {
      m._m00(src.getFloat(offset))
      m._m01(src.getFloat(offset + 4))
      m._m02(src.getFloat(offset + 8))
      m._m10(src.getFloat(offset + 12))
      m._m11(src.getFloat(offset + 16))
      m._m12(src.getFloat(offset + 20))
      m._m20(src.getFloat(offset + 24))
      m._m21(src.getFloat(offset + 28))
      m._m22(src.getFloat(offset + 32))
      m._m30(src.getFloat(offset + 36))
      m._m31(src.getFloat(offset + 40))
      m._m32(src.getFloat(offset + 44))
    }

    override fun get(m: Matrix4d, offset: Int, src: DoubleBuffer) {
      m._m00(src.get(offset))
      m._m01(src.get(offset + 1))
      m._m02(src.get(offset + 2))
      m._m03(src.get(offset + 3))
      m._m10(src.get(offset + 4))
      m._m11(src.get(offset + 5))
      m._m12(src.get(offset + 6))
      m._m13(src.get(offset + 7))
      m._m20(src.get(offset + 8))
      m._m21(src.get(offset + 9))
      m._m22(src.get(offset + 10))
      m._m23(src.get(offset + 11))
      m._m30(src.get(offset + 12))
      m._m31(src.get(offset + 13))
      m._m32(src.get(offset + 14))
      m._m33(src.get(offset + 15))
    }

    override fun get(m: Matrix4d, offset: Int, src: ByteBuffer) {
      m._m00(src.getDouble(offset))
      m._m01(src.getDouble(offset + 8))
      m._m02(src.getDouble(offset + 16))
      m._m03(src.getDouble(offset + 24))
      m._m10(src.getDouble(offset + 32))
      m._m11(src.getDouble(offset + 40))
      m._m12(src.getDouble(offset + 48))
      m._m13(src.getDouble(offset + 56))
      m._m20(src.getDouble(offset + 64))
      m._m21(src.getDouble(offset + 72))
      m._m22(src.getDouble(offset + 80))
      m._m23(src.getDouble(offset + 88))
      m._m30(src.getDouble(offset + 96))
      m._m31(src.getDouble(offset + 104))
      m._m32(src.getDouble(offset + 112))
      m._m33(src.getDouble(offset + 120))
    }

    override fun get(m: Matrix4x3d, offset: Int, src: DoubleBuffer) {
      m._m00(src.get(offset))
      m._m01(src.get(offset + 1))
      m._m02(src.get(offset + 2))
      m._m10(src.get(offset + 3))
      m._m11(src.get(offset + 4))
      m._m12(src.get(offset + 5))
      m._m20(src.get(offset + 6))
      m._m21(src.get(offset + 7))
      m._m22(src.get(offset + 8))
      m._m30(src.get(offset + 9))
      m._m31(src.get(offset + 10))
      m._m32(src.get(offset + 11))
    }

    override fun get(m: Matrix4x3d, offset: Int, src: ByteBuffer) {
      m._m00(src.getDouble(offset))
      m._m01(src.getDouble(offset + 8))
      m._m02(src.getDouble(offset + 16))
      m._m10(src.getDouble(offset + 24))
      m._m11(src.getDouble(offset + 32))
      m._m12(src.getDouble(offset + 40))
      m._m20(src.getDouble(offset + 48))
      m._m21(src.getDouble(offset + 56))
      m._m22(src.getDouble(offset + 64))
      m._m30(src.getDouble(offset + 72))
      m._m31(src.getDouble(offset + 80))
      m._m32(src.getDouble(offset + 88))
    }

    override fun getf(m: Matrix4d, offset: Int, src: FloatBuffer) {
      m._m00(src.get(offset).toDouble())
      m._m01(src.get(offset + 1).toDouble())
      m._m02(src.get(offset + 2).toDouble())
      m._m03(src.get(offset + 3).toDouble())
      m._m10(src.get(offset + 4).toDouble())
      m._m11(src.get(offset + 5).toDouble())
      m._m12(src.get(offset + 6).toDouble())
      m._m13(src.get(offset + 7).toDouble())
      m._m20(src.get(offset + 8).toDouble())
      m._m21(src.get(offset + 9).toDouble())
      m._m22(src.get(offset + 10).toDouble())
      m._m23(src.get(offset + 11).toDouble())
      m._m30(src.get(offset + 12).toDouble())
      m._m31(src.get(offset + 13).toDouble())
      m._m32(src.get(offset + 14).toDouble())
      m._m33(src.get(offset + 15).toDouble())
    }

    override fun getf(m: Matrix4d, offset: Int, src: ByteBuffer) {
      m._m00(src.getFloat(offset).toDouble())
      m._m01(src.getFloat(offset + 4).toDouble())
      m._m02(src.getFloat(offset + 8).toDouble())
      m._m03(src.getFloat(offset + 12).toDouble())
      m._m10(src.getFloat(offset + 16).toDouble())
      m._m11(src.getFloat(offset + 20).toDouble())
      m._m12(src.getFloat(offset + 24).toDouble())
      m._m13(src.getFloat(offset + 28).toDouble())
      m._m20(src.getFloat(offset + 32).toDouble())
      m._m21(src.getFloat(offset + 36).toDouble())
      m._m22(src.getFloat(offset + 40).toDouble())
      m._m23(src.getFloat(offset + 44).toDouble())
      m._m30(src.getFloat(offset + 48).toDouble())
      m._m31(src.getFloat(offset + 52).toDouble())
      m._m32(src.getFloat(offset + 56).toDouble())
      m._m33(src.getFloat(offset + 60).toDouble())
    }

    override fun getf(m: Matrix4x3d, offset: Int, src: FloatBuffer) {
      m._m00(src.get(offset).toDouble())
      m._m01(src.get(offset + 1).toDouble())
      m._m02(src.get(offset + 2).toDouble())
      m._m10(src.get(offset + 3).toDouble())
      m._m11(src.get(offset + 4).toDouble())
      m._m12(src.get(offset + 5).toDouble())
      m._m20(src.get(offset + 6).toDouble())
      m._m21(src.get(offset + 7).toDouble())
      m._m22(src.get(offset + 8).toDouble())
      m._m30(src.get(offset + 9).toDouble())
      m._m31(src.get(offset + 10).toDouble())
      m._m32(src.get(offset + 11).toDouble())
    }

    override fun getf(m: Matrix4x3d, offset: Int, src: ByteBuffer) {
      m._m00(src.getFloat(offset).toDouble())
      m._m01(src.getFloat(offset + 4).toDouble())
      m._m02(src.getFloat(offset + 8).toDouble())
      m._m10(src.getFloat(offset + 12).toDouble())
      m._m11(src.getFloat(offset + 16).toDouble())
      m._m12(src.getFloat(offset + 20).toDouble())
      m._m20(src.getFloat(offset + 24).toDouble())
      m._m21(src.getFloat(offset + 28).toDouble())
      m._m22(src.getFloat(offset + 32).toDouble())
      m._m30(src.getFloat(offset + 36).toDouble())
      m._m31(src.getFloat(offset + 40).toDouble())
      m._m32(src.getFloat(offset + 44).toDouble())
    }

    override fun get(m: Matrix3f, offset: Int, src: FloatBuffer) {
      m._m00(src.get(offset))
      m._m01(src.get(offset + 1))
      m._m02(src.get(offset + 2))
      m._m10(src.get(offset + 3))
      m._m11(src.get(offset + 4))
      m._m12(src.get(offset + 5))
      m._m20(src.get(offset + 6))
      m._m21(src.get(offset + 7))
      m._m22(src.get(offset + 8))
    }

    override fun get(m: Matrix3f, offset: Int, src: ByteBuffer) {
      m._m00(src.getFloat(offset))
      m._m01(src.getFloat(offset + 4))
      m._m02(src.getFloat(offset + 8))
      m._m10(src.getFloat(offset + 12))
      m._m11(src.getFloat(offset + 16))
      m._m12(src.getFloat(offset + 20))
      m._m20(src.getFloat(offset + 24))
      m._m21(src.getFloat(offset + 28))
      m._m22(src.getFloat(offset + 32))
    }

    override fun get(m: Matrix3d, offset: Int, src: DoubleBuffer) {
      m._m00(src.get(offset))
      m._m01(src.get(offset + 1))
      m._m02(src.get(offset + 2))
      m._m10(src.get(offset + 3))
      m._m11(src.get(offset + 4))
      m._m12(src.get(offset + 5))
      m._m20(src.get(offset + 6))
      m._m21(src.get(offset + 7))
      m._m22(src.get(offset + 8))
    }

    override fun get(m: Matrix3d, offset: Int, src: ByteBuffer) {
      m._m00(src.getDouble(offset))
      m._m01(src.getDouble(offset + 8))
      m._m02(src.getDouble(offset + 16))
      m._m10(src.getDouble(offset + 24))
      m._m11(src.getDouble(offset + 32))
      m._m12(src.getDouble(offset + 40))
      m._m20(src.getDouble(offset + 48))
      m._m21(src.getDouble(offset + 56))
      m._m22(src.getDouble(offset + 64))
    }

    override fun get(m: Matrix3x2f, offset: Int, src: FloatBuffer) {
      m._m00(src.get(offset))
      m._m01(src.get(offset + 1))
      m._m10(src.get(offset + 2))
      m._m11(src.get(offset + 3))
      m._m20(src.get(offset + 4))
      m._m21(src.get(offset + 5))
    }

    override fun get(m: Matrix3x2f, offset: Int, src: ByteBuffer) {
      m._m00(src.getFloat(offset))
      m._m01(src.getFloat(offset + 4))
      m._m10(src.getFloat(offset + 8))
      m._m11(src.getFloat(offset + 12))
      m._m20(src.getFloat(offset + 16))
      m._m21(src.getFloat(offset + 20))
    }

    override fun get(m: Matrix3x2d, offset: Int, src: DoubleBuffer) {
      m._m00(src.get(offset))
      m._m01(src.get(offset + 1))
      m._m10(src.get(offset + 2))
      m._m11(src.get(offset + 3))
      m._m20(src.get(offset + 4))
      m._m21(src.get(offset + 5))
    }

    override fun get(m: Matrix3x2d, offset: Int, src: ByteBuffer) {
      m._m00(src.getDouble(offset))
      m._m01(src.getDouble(offset + 8))
      m._m10(src.getDouble(offset + 16))
      m._m11(src.getDouble(offset + 24))
      m._m20(src.getDouble(offset + 32))
      m._m21(src.getDouble(offset + 40))
    }

    override fun getf(m: Matrix3d, offset: Int, src: FloatBuffer) {
      m._m00(src.get(offset).toDouble())
      m._m01(src.get(offset + 1).toDouble())
      m._m02(src.get(offset + 2).toDouble())
      m._m10(src.get(offset + 3).toDouble())
      m._m11(src.get(offset + 4).toDouble())
      m._m12(src.get(offset + 5).toDouble())
      m._m20(src.get(offset + 6).toDouble())
      m._m21(src.get(offset + 7).toDouble())
      m._m22(src.get(offset + 8).toDouble())
    }

    override fun getf(m: Matrix3d, offset: Int, src: ByteBuffer) {
      m._m00(src.getFloat(offset).toDouble())
      m._m01(src.getFloat(offset + 4).toDouble())
      m._m02(src.getFloat(offset + 8).toDouble())
      m._m10(src.getFloat(offset + 12).toDouble())
      m._m11(src.getFloat(offset + 16).toDouble())
      m._m12(src.getFloat(offset + 20).toDouble())
      m._m20(src.getFloat(offset + 24).toDouble())
      m._m21(src.getFloat(offset + 28).toDouble())
      m._m22(src.getFloat(offset + 32).toDouble())
    }

    override fun get(dst: Vector4d, offset: Int, src: DoubleBuffer) {
      dst.x = src.get(offset)
      dst.y = src.get(offset + 1)
      dst.z = src.get(offset + 2)
      dst.w = src.get(offset + 3)
    }

    override fun get(dst: Vector4d, offset: Int, src: ByteBuffer) {
      dst.x = src.getDouble(offset)
      dst.y = src.getDouble(offset + 8)
      dst.z = src.getDouble(offset + 16)
      dst.w = src.getDouble(offset + 24)
    }

    override fun get(dst: Vector4f, offset: Int, src: FloatBuffer) {
      dst.x = src.get(offset)
      dst.y = src.get(offset + 1)
      dst.z = src.get(offset + 2)
      dst.w = src.get(offset + 3)
    }

    override fun get(dst: Vector4f, offset: Int, src: ByteBuffer) {
      dst.x = src.getFloat(offset)
      dst.y = src.getFloat(offset + 4)
      dst.z = src.getFloat(offset + 8)
      dst.w = src.getFloat(offset + 12)
    }

    override fun get(dst: Vector4i, offset: Int, src: IntBuffer) {
      dst.x = src.get(offset)
      dst.y = src.get(offset + 1)
      dst.z = src.get(offset + 2)
      dst.w = src.get(offset + 3)
    }

    override fun get(dst: Vector4i, offset: Int, src: ByteBuffer) {
      dst.x = src.getInt(offset)
      dst.y = src.getInt(offset + 4)
      dst.z = src.getInt(offset + 8)
      dst.w = src.getInt(offset + 12)
    }

    override fun get(dst: Vector3m, offset: Int, src: FloatBuffer) {
      dst.x = src.get(offset)
      dst.y = src.get(offset + 1)
      dst.z = src.get(offset + 2)
    }

    override fun get(dst: Vector3m, offset: Int, src: ByteBuffer) {
      dst.x = src.getFloat(offset)
      dst.y = src.getFloat(offset + 4)
      dst.z = src.getFloat(offset + 8)
    }

    override fun get(dst: Vector3d, offset: Int, src: DoubleBuffer) {
      dst.x = src.get(offset)
      dst.y = src.get(offset + 1)
      dst.z = src.get(offset + 2)
    }

    override fun get(dst: Vector3d, offset: Int, src: ByteBuffer) {
      dst.x = src.getDouble(offset)
      dst.y = src.getDouble(offset + 8)
      dst.z = src.getDouble(offset + 16)
    }

    override fun get(dst: Vector3i, offset: Int, src: IntBuffer) {
      dst.x = src.get(offset)
      dst.y = src.get(offset + 1)
      dst.z = src.get(offset + 2)
    }

    override fun get(dst: Vector3i, offset: Int, src: ByteBuffer) {
      dst.x = src.getInt(offset)
      dst.y = src.getInt(offset + 4)
      dst.z = src.getInt(offset + 8)
    }

    override fun get(dst: Vector2f, offset: Int, src: FloatBuffer) {
      dst.x = src.get(offset)
      dst.y = src.get(offset + 1)
    }

    override fun get(dst: Vector2f, offset: Int, src: ByteBuffer) {
      dst.x = src.getFloat(offset)
      dst.y = src.getFloat(offset + 4)
    }

    override fun get(dst: Vector2d, offset: Int, src: DoubleBuffer) {
      dst.x = src.get(offset)
      dst.y = src.get(offset + 1)
    }

    override fun get(dst: Vector2d, offset: Int, src: ByteBuffer) {
      dst.x = src.getDouble(offset)
      dst.y = src.getDouble(offset + 8)
    }

    override fun get(dst: Vector2i, offset: Int, src: IntBuffer) {
      dst.x = src.get(offset)
      dst.y = src.get(offset + 1)
    }

    override fun get(dst: Vector2i, offset: Int, src: ByteBuffer) {
      dst.x = src.getInt(offset)
      dst.y = src.getInt(offset + 4)
    }

    override fun copy(src: Matrix4f, dest: Matrix4f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m03(src.m03())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m13(src.m13())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
      dest._m23(src.m23())
      dest._m30(src.m30())
      dest._m31(src.m31())
      dest._m32(src.m32())
      dest._m33(src.m33())
    }

    override fun copy(src: Matrix3f, dest: Matrix4f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m03(0.0f)
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m13(0.0f)
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
      dest._m23(0.0f)
      dest._m30(0.0f)
      dest._m31(0.0f)
      dest._m32(0.0f)
      dest._m33(1.0f)
    }

    override fun copy(src: Matrix4f, dest: Matrix3f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
    }

    override fun copy(src: Matrix3f, dest: Matrix4x3f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
      dest._m30(0.0f)
      dest._m31(0.0f)
      dest._m32(0.0f)
    }

    override fun copy(src: Matrix3x2f, dest: Matrix3x2f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m20(src.m20())
      dest._m21(src.m21())
    }

    override fun copy(src: Matrix3x2d, dest: Matrix3x2d) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m20(src.m20())
      dest._m21(src.m21())
    }

    override fun copy3x3(src: Matrix4f, dest: Matrix4f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
    }

    override fun copy3x3(src: Matrix4x3f, dest: Matrix4x3f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
    }

    override fun copy3x3(src: Matrix3f, dest: Matrix4x3f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
    }

    override fun copy3x3(src: Matrix3f, dest: Matrix4f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
    }

    override fun copy4x3(src: Matrix4x3f, dest: Matrix4f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
      dest._m30(src.m30())
      dest._m31(src.m31())
      dest._m32(src.m32())
    }

    override fun copy(src: Vector4f, dst: Vector4f) {
      dst.x = src.x
      dst.y = src.y
      dst.z = src.z
      dst.w = src.w
    }

    override fun copy(src: Vector4i, dst: Vector4i) {
      dst.x = src.x
      dst.y = src.y
      dst.z = src.z
      dst.w = src.w
    }

    override fun copy(src: Quaternionf, dst: Quaternionf) {
      dst.x = src.x
      dst.y = src.y
      dst.z = src.z
      dst.w = src.w
    }

    override fun copy4x3(src: Matrix4f, dest: Matrix4f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
      dest._m30(src.m30())
      dest._m31(src.m31())
      dest._m32(src.m32())
    }

    override fun copy(src: Matrix4f, dest: Matrix4x3f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
      dest._m30(src.m30())
      dest._m31(src.m31())
      dest._m32(src.m32())
    }

    override fun copy(src: Matrix4x3f, dest: Matrix4f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m03(0.0f)
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m13(0.0f)
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
      dest._m23(0.0f)
      dest._m30(src.m30())
      dest._m31(src.m31())
      dest._m32(src.m32())
      dest._m33(1.0f)
    }

    override fun copy(src: Matrix4x3f, dest: Matrix4x3f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
      dest._m30(src.m30())
      dest._m31(src.m31())
      dest._m32(src.m32())
    }

    override fun copy(src: Matrix3f, dest: Matrix3f) {
      dest._m00(src.m00())
      dest._m01(src.m01())
      dest._m02(src.m02())
      dest._m10(src.m10())
      dest._m11(src.m11())
      dest._m12(src.m12())
      dest._m20(src.m20())
      dest._m21(src.m21())
      dest._m22(src.m22())
    }

    override fun copy(arr: FloatArray, off: Int, dest: Matrix4f) {
      dest._m00(arr[off + 0])
      dest._m01(arr[off + 1])
      dest._m02(arr[off + 2])
      dest._m03(arr[off + 3])
      dest._m10(arr[off + 4])
      dest._m11(arr[off + 5])
      dest._m12(arr[off + 6])
      dest._m13(arr[off + 7])
      dest._m20(arr[off + 8])
      dest._m21(arr[off + 9])
      dest._m22(arr[off + 10])
      dest._m23(arr[off + 11])
      dest._m30(arr[off + 12])
      dest._m31(arr[off + 13])
      dest._m32(arr[off + 14])
      dest._m33(arr[off + 15])
    }

    override fun copy(arr: FloatArray, off: Int, dest: Matrix3f) {
      dest._m00(arr[off + 0])
      dest._m01(arr[off + 1])
      dest._m02(arr[off + 2])
      dest._m10(arr[off + 3])
      dest._m11(arr[off + 4])
      dest._m12(arr[off + 5])
      dest._m20(arr[off + 6])
      dest._m21(arr[off + 7])
      dest._m22(arr[off + 8])
    }

    override fun copy(arr: FloatArray, off: Int, dest: Matrix4x3f) {
      dest._m00(arr[off + 0])
      dest._m01(arr[off + 1])
      dest._m02(arr[off + 2])
      dest._m10(arr[off + 3])
      dest._m11(arr[off + 4])
      dest._m12(arr[off + 5])
      dest._m20(arr[off + 6])
      dest._m21(arr[off + 7])
      dest._m22(arr[off + 8])
      dest._m30(arr[off + 9])
      dest._m31(arr[off + 10])
      dest._m32(arr[off + 11])
    }

    override fun copy(arr: FloatArray, off: Int, dest: Matrix3x2f) {
      dest._m00(arr[off + 0])
      dest._m01(arr[off + 1])
      dest._m10(arr[off + 2])
      dest._m11(arr[off + 3])
      dest._m20(arr[off + 4])
      dest._m21(arr[off + 5])
    }

    override fun copy(arr: DoubleArray, off: Int, dest: Matrix3x2d) {
      dest._m00(arr[off + 0])
      dest._m01(arr[off + 1])
      dest._m10(arr[off + 2])
      dest._m11(arr[off + 3])
      dest._m20(arr[off + 4])
      dest._m21(arr[off + 5])
    }

    override fun copy(src: Matrix4f, dest: FloatArray, off: Int) {
      dest[off + 0] = src.m00()
      dest[off + 1] = src.m01()
      dest[off + 2] = src.m02()
      dest[off + 3] = src.m03()
      dest[off + 4] = src.m10()
      dest[off + 5] = src.m11()
      dest[off + 6] = src.m12()
      dest[off + 7] = src.m13()
      dest[off + 8] = src.m20()
      dest[off + 9] = src.m21()
      dest[off + 10] = src.m22()
      dest[off + 11] = src.m23()
      dest[off + 12] = src.m30()
      dest[off + 13] = src.m31()
      dest[off + 14] = src.m32()
      dest[off + 15] = src.m33()
    }

    override fun copy(src: Matrix3f, dest: FloatArray, off: Int) {
      dest[off + 0] = src.m00()
      dest[off + 1] = src.m01()
      dest[off + 2] = src.m02()
      dest[off + 3] = src.m10()
      dest[off + 4] = src.m11()
      dest[off + 5] = src.m12()
      dest[off + 6] = src.m20()
      dest[off + 7] = src.m21()
      dest[off + 8] = src.m22()
    }

    override fun copy(src: Matrix4x3f, dest: FloatArray, off: Int) {
      dest[off + 0] = src.m00()
      dest[off + 1] = src.m01()
      dest[off + 2] = src.m02()
      dest[off + 3] = src.m10()
      dest[off + 4] = src.m11()
      dest[off + 5] = src.m12()
      dest[off + 6] = src.m20()
      dest[off + 7] = src.m21()
      dest[off + 8] = src.m22()
      dest[off + 9] = src.m30()
      dest[off + 10] = src.m31()
      dest[off + 11] = src.m32()
    }

    override fun copy(src: Matrix3x2f, dest: FloatArray, off: Int) {
      dest[off + 0] = src.m00()
      dest[off + 1] = src.m01()
      dest[off + 2] = src.m10()
      dest[off + 3] = src.m11()
      dest[off + 4] = src.m20()
      dest[off + 5] = src.m21()
    }

    override fun copy(src: Matrix3x2d, dest: DoubleArray, off: Int) {
      dest[off + 0] = src.m00()
      dest[off + 1] = src.m01()
      dest[off + 2] = src.m10()
      dest[off + 3] = src.m11()
      dest[off + 4] = src.m20()
      dest[off + 5] = src.m21()
    }

    override fun copy4x4(src: Matrix4x3f, dest: FloatArray, off: Int) {
      dest[off + 0] = src.m00()
      dest[off + 1] = src.m01()
      dest[off + 2] = src.m02()
      dest[off + 3] = 0.0f
      dest[off + 4] = src.m10()
      dest[off + 5] = src.m11()
      dest[off + 6] = src.m12()
      dest[off + 7] = 0.0f
      dest[off + 8] = src.m20()
      dest[off + 9] = src.m21()
      dest[off + 10] = src.m22()
      dest[off + 11] = 0.0f
      dest[off + 12] = src.m30()
      dest[off + 13] = src.m31()
      dest[off + 14] = src.m32()
      dest[off + 15] = 1.0f
    }

    override fun copy4x4(src: Matrix4x3d, dest: FloatArray, off: Int) {
      dest[off + 0] = src.m00().toFloat()
      dest[off + 1] = src.m01().toFloat()
      dest[off + 2] = src.m02().toFloat()
      dest[off + 3] = 0.0f
      dest[off + 4] = src.m10().toFloat()
      dest[off + 5] = src.m11().toFloat()
      dest[off + 6] = src.m12().toFloat()
      dest[off + 7] = 0.0f
      dest[off + 8] = src.m20().toFloat()
      dest[off + 9] = src.m21().toFloat()
      dest[off + 10] = src.m22().toFloat()
      dest[off + 11] = 0.0f
      dest[off + 12] = src.m30().toFloat()
      dest[off + 13] = src.m31().toFloat()
      dest[off + 14] = src.m32().toFloat()
      dest[off + 15] = 1.0f
    }

    override fun copy4x4(src: Matrix4x3d, dest: DoubleArray, off: Int) {
      dest[off + 0] = src.m00()
      dest[off + 1] = src.m01()
      dest[off + 2] = src.m02()
      dest[off + 3] = 0.0
      dest[off + 4] = src.m10()
      dest[off + 5] = src.m11()
      dest[off + 6] = src.m12()
      dest[off + 7] = 0.0
      dest[off + 8] = src.m20()
      dest[off + 9] = src.m21()
      dest[off + 10] = src.m22()
      dest[off + 11] = 0.0
      dest[off + 12] = src.m30()
      dest[off + 13] = src.m31()
      dest[off + 14] = src.m32()
      dest[off + 15] = 1.0
    }

    override fun copy3x3(src: Matrix3x2f, dest: FloatArray, off: Int) {
      dest[off + 0] = src.m00()
      dest[off + 1] = src.m01()
      dest[off + 2] = 0.0f
      dest[off + 3] = src.m10()
      dest[off + 4] = src.m11()
      dest[off + 5] = 0.0f
      dest[off + 6] = src.m20()
      dest[off + 7] = src.m21()
      dest[off + 8] = 1.0f
    }

    override fun copy3x3(src: Matrix3x2d, dest: DoubleArray, off: Int) {
      dest[off + 0] = src.m00()
      dest[off + 1] = src.m01()
      dest[off + 2] = 0.0
      dest[off + 3] = src.m10()
      dest[off + 4] = src.m11()
      dest[off + 5] = 0.0
      dest[off + 6] = src.m20()
      dest[off + 7] = src.m21()
      dest[off + 8] = 1.0
    }

    override fun copy4x4(src: Matrix3x2f, dest: FloatArray, off: Int) {
      dest[off + 0] = src.m00()
      dest[off + 1] = src.m01()
      dest[off + 2] = 0.0f
      dest[off + 3] = 0.0f
      dest[off + 4] = src.m10()
      dest[off + 5] = src.m11()
      dest[off + 6] = 0.0f
      dest[off + 7] = 0.0f
      dest[off + 8] = 0.0f
      dest[off + 9] = 0.0f
      dest[off + 10] = 1.0f
      dest[off + 11] = 0.0f
      dest[off + 12] = src.m20()
      dest[off + 13] = src.m21()
      dest[off + 14] = 0.0f
      dest[off + 15] = 1.0f
    }

    override fun copy4x4(src: Matrix3x2d, dest: DoubleArray, off: Int) {
      dest[off + 0] = src.m00()
      dest[off + 1] = src.m01()
      dest[off + 2] = 0.0
      dest[off + 3] = 0.0
      dest[off + 4] = src.m10()
      dest[off + 5] = src.m11()
      dest[off + 6] = 0.0
      dest[off + 7] = 0.0
      dest[off + 8] = 0.0
      dest[off + 9] = 0.0
      dest[off + 10] = 1.0
      dest[off + 11] = 0.0
      dest[off + 12] = src.m20()
      dest[off + 13] = src.m21()
      dest[off + 14] = 0.0
      dest[off + 15] = 1.0
    }

    override fun identity(dest: Matrix4f) {
      dest._m00(1.0f)
      dest._m01(0.0f)
      dest._m02(0.0f)
      dest._m03(0.0f)
      dest._m10(0.0f)
      dest._m11(1.0f)
      dest._m12(0.0f)
      dest._m13(0.0f)
      dest._m20(0.0f)
      dest._m21(0.0f)
      dest._m22(1.0f)
      dest._m23(0.0f)
      dest._m30(0.0f)
      dest._m31(0.0f)
      dest._m32(0.0f)
      dest._m33(1.0f)
    }

    override fun identity(dest: Matrix4x3f) {
      dest._m00(1.0f)
      dest._m01(0.0f)
      dest._m02(0.0f)
      dest._m10(0.0f)
      dest._m11(1.0f)
      dest._m12(0.0f)
      dest._m20(0.0f)
      dest._m21(0.0f)
      dest._m22(1.0f)
      dest._m30(0.0f)
      dest._m31(0.0f)
      dest._m32(0.0f)
    }

    override fun identity(dest: Matrix3f) {
      dest._m00(1.0f)
      dest._m01(0.0f)
      dest._m02(0.0f)
      dest._m10(0.0f)
      dest._m11(1.0f)
      dest._m12(0.0f)
      dest._m20(0.0f)
      dest._m21(0.0f)
      dest._m22(1.0f)
    }

    override fun identity(dest: Matrix3x2f) {
      dest._m00(1.0f)
      dest._m01(0.0f)
      dest._m10(0.0f)
      dest._m11(1.0f)
      dest._m20(0.0f)
      dest._m21(0.0f)
    }

    override fun identity(dest: Matrix3x2d) {
      dest._m00(1.0)
      dest._m01(0.0)
      dest._m10(0.0)
      dest._m11(1.0)
      dest._m20(0.0)
      dest._m21(0.0)
    }

    override fun identity(dest: Quaternionf) {
      dest.x = 0.0f
      dest.y = 0.0f
      dest.z = 0.0f
      dest.w = 1.0f
    }

    override fun swap(m1: Matrix4f, m2: Matrix4f) {
      var tmp: Float
      tmp = m1.m00()
      m1._m00(m2.m00())
      m2._m00(tmp)
      tmp = m1.m01()
      m1._m01(m2.m01())
      m2._m01(tmp)
      tmp = m1.m02()
      m1._m02(m2.m02())
      m2._m02(tmp)
      tmp = m1.m03()
      m1._m03(m2.m03())
      m2._m03(tmp)
      tmp = m1.m10()
      m1._m10(m2.m10())
      m2._m10(tmp)
      tmp = m1.m11()
      m1._m11(m2.m11())
      m2._m11(tmp)
      tmp = m1.m12()
      m1._m12(m2.m12())
      m2._m12(tmp)
      tmp = m1.m13()
      m1._m13(m2.m13())
      m2._m13(tmp)
      tmp = m1.m20()
      m1._m20(m2.m20())
      m2._m20(tmp)
      tmp = m1.m21()
      m1._m21(m2.m21())
      m2._m21(tmp)
      tmp = m1.m22()
      m1._m22(m2.m22())
      m2._m22(tmp)
      tmp = m1.m23()
      m1._m23(m2.m23())
      m2._m23(tmp)
      tmp = m1.m30()
      m1._m30(m2.m30())
      m2._m30(tmp)
      tmp = m1.m31()
      m1._m31(m2.m31())
      m2._m31(tmp)
      tmp = m1.m32()
      m1._m32(m2.m32())
      m2._m32(tmp)
      tmp = m1.m33()
      m1._m33(m2.m33())
      m2._m33(tmp)
    }

    override fun swap(m1: Matrix4x3f, m2: Matrix4x3f) {
      var tmp: Float
      tmp = m1.m00()
      m1._m00(m2.m00())
      m2._m00(tmp)
      tmp = m1.m01()
      m1._m01(m2.m01())
      m2._m01(tmp)
      tmp = m1.m02()
      m1._m02(m2.m02())
      m2._m02(tmp)
      tmp = m1.m10()
      m1._m10(m2.m10())
      m2._m10(tmp)
      tmp = m1.m11()
      m1._m11(m2.m11())
      m2._m11(tmp)
      tmp = m1.m12()
      m1._m12(m2.m12())
      m2._m12(tmp)
      tmp = m1.m20()
      m1._m20(m2.m20())
      m2._m20(tmp)
      tmp = m1.m21()
      m1._m21(m2.m21())
      m2._m21(tmp)
      tmp = m1.m22()
      m1._m22(m2.m22())
      m2._m22(tmp)
      tmp = m1.m30()
      m1._m30(m2.m30())
      m2._m30(tmp)
      tmp = m1.m31()
      m1._m31(m2.m31())
      m2._m31(tmp)
      tmp = m1.m32()
      m1._m32(m2.m32())
      m2._m32(tmp)
    }

    override fun swap(m1: Matrix3f, m2: Matrix3f) {
      var tmp: Float
      tmp = m1.m00()
      m1._m00(m2.m00())
      m2._m00(tmp)
      tmp = m1.m01()
      m1._m01(m2.m01())
      m2._m01(tmp)
      tmp = m1.m02()
      m1._m02(m2.m02())
      m2._m02(tmp)
      tmp = m1.m10()
      m1._m10(m2.m10())
      m2._m10(tmp)
      tmp = m1.m11()
      m1._m11(m2.m11())
      m2._m11(tmp)
      tmp = m1.m12()
      m1._m12(m2.m12())
      m2._m12(tmp)
      tmp = m1.m20()
      m1._m20(m2.m20())
      m2._m20(tmp)
      tmp = m1.m21()
      m1._m21(m2.m21())
      m2._m21(tmp)
      tmp = m1.m22()
      m1._m22(m2.m22())
      m2._m22(tmp)
    }

    override fun zero(dest: Matrix4f) {
      dest._m00(0.0f)
      dest._m01(0.0f)
      dest._m02(0.0f)
      dest._m03(0.0f)
      dest._m10(0.0f)
      dest._m11(0.0f)
      dest._m12(0.0f)
      dest._m13(0.0f)
      dest._m20(0.0f)
      dest._m21(0.0f)
      dest._m22(0.0f)
      dest._m23(0.0f)
      dest._m30(0.0f)
      dest._m31(0.0f)
      dest._m32(0.0f)
      dest._m33(0.0f)
    }

    override fun zero(dest: Matrix4x3f) {
      dest._m00(0.0f)
      dest._m01(0.0f)
      dest._m02(0.0f)
      dest._m10(0.0f)
      dest._m11(0.0f)
      dest._m12(0.0f)
      dest._m20(0.0f)
      dest._m21(0.0f)
      dest._m22(0.0f)
      dest._m30(0.0f)
      dest._m31(0.0f)
      dest._m32(0.0f)
    }

    override fun zero(dest: Matrix3f) {
      dest._m00(0.0f)
      dest._m01(0.0f)
      dest._m02(0.0f)
      dest._m10(0.0f)
      dest._m11(0.0f)
      dest._m12(0.0f)
      dest._m20(0.0f)
      dest._m21(0.0f)
      dest._m22(0.0f)
    }

    override fun zero(dest: Matrix3x2f) {
      dest._m00(0.0f)
      dest._m01(0.0f)
      dest._m10(0.0f)
      dest._m11(0.0f)
      dest._m20(0.0f)
      dest._m21(0.0f)
    }

    override fun zero(dest: Matrix3x2d) {
      dest._m00(0.0)
      dest._m01(0.0)
      dest._m10(0.0)
      dest._m11(0.0)
      dest._m20(0.0)
      dest._m21(0.0)
    }

    override fun zero(dest: Vector4f) {
      dest.x = 0.0f
      dest.y = 0.0f
      dest.z = 0.0f
      dest.w = 0.0f
    }

    override fun zero(dest: Vector4i) {
      dest.x = 0
      dest.y = 0
      dest.z = 0
      dest.w = 0
    }

    override fun putMatrix3f(q: Quaternionf, position: Int, dest: ByteBuffer) {
      val w2 = q.w * q.w
      val x2 = q.x * q.x
      val y2 = q.y * q.y
      val z2 = q.z * q.z
      val zw = q.z * q.w
      val xy = q.x * q.y
      val xz = q.x * q.z
      val yw = q.y * q.w
      val yz = q.y * q.z
      val xw = q.x * q.w
      dest.putFloat(position, w2 + x2 - z2 - y2)
      dest.putFloat(position + 4, xy + zw + zw + xy)
      dest.putFloat(position + 8, xz - yw + xz - yw)
      dest.putFloat(position + 12, -zw + xy - zw + xy)
      dest.putFloat(position + 16, y2 - z2 + w2 - x2)
      dest.putFloat(position + 20, yz + yz + xw + xw)
      dest.putFloat(position + 24, yw + xz + xz + yw)
      dest.putFloat(position + 28, yz + yz - xw - xw)
      dest.putFloat(position + 32, z2 - y2 - x2 + w2)
    }

    override fun putMatrix3f(q: Quaternionf, position: Int, dest: FloatBuffer) {
      val w2 = q.w * q.w
      val x2 = q.x * q.x
      val y2 = q.y * q.y
      val z2 = q.z * q.z
      val zw = q.z * q.w
      val xy = q.x * q.y
      val xz = q.x * q.z
      val yw = q.y * q.w
      val yz = q.y * q.z
      val xw = q.x * q.w
      dest.put(position, w2 + x2 - z2 - y2)
      dest.put(position + 1, xy + zw + zw + xy)
      dest.put(position + 2, xz - yw + xz - yw)
      dest.put(position + 3, -zw + xy - zw + xy)
      dest.put(position + 4, y2 - z2 + w2 - x2)
      dest.put(position + 5, yz + yz + xw + xw)
      dest.put(position + 6, yw + xz + xz + yw)
      dest.put(position + 7, yz + yz - xw - xw)
      dest.put(position + 8, z2 - y2 - x2 + w2)
    }

    override fun putMatrix4f(q: Quaternionf, position: Int, dest: ByteBuffer) {
      val w2 = q.w * q.w
      val x2 = q.x * q.x
      val y2 = q.y * q.y
      val z2 = q.z * q.z
      val zw = q.z * q.w
      val xy = q.x * q.y
      val xz = q.x * q.z
      val yw = q.y * q.w
      val yz = q.y * q.z
      val xw = q.x * q.w
      dest.putFloat(position, w2 + x2 - z2 - y2)
      dest.putFloat(position + 4, xy + zw + zw + xy)
      dest.putFloat(position + 8, xz - yw + xz - yw)
      dest.putFloat(position + 12, 0.0f)
      dest.putFloat(position + 16, -zw + xy - zw + xy)
      dest.putFloat(position + 20, y2 - z2 + w2 - x2)
      dest.putFloat(position + 24, yz + yz + xw + xw)
      dest.putFloat(position + 28, 0.0f)
      dest.putFloat(position + 32, yw + xz + xz + yw)
      dest.putFloat(position + 36, yz + yz - xw - xw)
      dest.putFloat(position + 40, z2 - y2 - x2 + w2)
      dest.putFloat(position + 44, 0.0f)
      dest.putLong(position + 48, 0L)
      dest.putLong(position + 56, 0x3F80000000000000L)
    }

    override fun putMatrix4f(q: Quaternionf, position: Int, dest: FloatBuffer) {
      val w2 = q.w * q.w
      val x2 = q.x * q.x
      val y2 = q.y * q.y
      val z2 = q.z * q.z
      val zw = q.z * q.w
      val xy = q.x * q.y
      val xz = q.x * q.z
      val yw = q.y * q.w
      val yz = q.y * q.z
      val xw = q.x * q.w
      dest.put(position, w2 + x2 - z2 - y2)
      dest.put(position + 1, xy + zw + zw + xy)
      dest.put(position + 2, xz - yw + xz - yw)
      dest.put(position + 3, 0.0f)
      dest.put(position + 4, -zw + xy - zw + xy)
      dest.put(position + 5, y2 - z2 + w2 - x2)
      dest.put(position + 6, yz + yz + xw + xw)
      dest.put(position + 7, 0.0f)
      dest.put(position + 8, yw + xz + xz + yw)
      dest.put(position + 9, yz + yz - xw - xw)
      dest.put(position + 10, z2 - y2 - x2 + w2)
      dest.put(position + 11, 0.0f)
      dest.put(position + 12, 0.0f)
      dest.put(position + 13, 0.0f)
      dest.put(position + 14, 0.0f)
      dest.put(position + 15, 1.0f)
    }

    override fun putMatrix4x3f(q: Quaternionf, position: Int, dest: ByteBuffer) {
      val w2 = q.w * q.w
      val x2 = q.x * q.x
      val y2 = q.y * q.y
      val z2 = q.z * q.z
      val zw = q.z * q.w
      val xy = q.x * q.y
      val xz = q.x * q.z
      val yw = q.y * q.w
      val yz = q.y * q.z
      val xw = q.x * q.w
      dest.putFloat(position, w2 + x2 - z2 - y2)
      dest.putFloat(position + 4, xy + zw + zw + xy)
      dest.putFloat(position + 8, xz - yw + xz - yw)
      dest.putFloat(position + 12, -zw + xy - zw + xy)
      dest.putFloat(position + 16, y2 - z2 + w2 - x2)
      dest.putFloat(position + 20, yz + yz + xw + xw)
      dest.putFloat(position + 24, yw + xz + xz + yw)
      dest.putFloat(position + 28, yz + yz - xw - xw)
      dest.putFloat(position + 32, z2 - y2 - x2 + w2)
      dest.putLong(position + 36, 0L)
      dest.putFloat(position + 44, 0.0f)
    }

    override fun putMatrix4x3f(q: Quaternionf, position: Int, dest: FloatBuffer) {
      val w2 = q.w * q.w
      val x2 = q.x * q.x
      val y2 = q.y * q.y
      val z2 = q.z * q.z
      val zw = q.z * q.w
      val xy = q.x * q.y
      val xz = q.x * q.z
      val yw = q.y * q.w
      val yz = q.y * q.z
      val xw = q.x * q.w
      dest.put(position, w2 + x2 - z2 - y2)
      dest.put(position + 1, xy + zw + zw + xy)
      dest.put(position + 2, xz - yw + xz - yw)
      dest.put(position + 3, -zw + xy - zw + xy)
      dest.put(position + 4, y2 - z2 + w2 - x2)
      dest.put(position + 5, yz + yz + xw + xw)
      dest.put(position + 6, yw + xz + xz + yw)
      dest.put(position + 7, yz + yz - xw - xw)
      dest.put(position + 8, z2 - y2 - x2 + w2)
      dest.put(position + 9, 0.0f)
      dest.put(position + 10, 0.0f)
      dest.put(position + 11, 0.0f)
    }

    override fun set(m: Matrix4f, col0: Vector4f, col1: Vector4f, col2: Vector4f, col3: Vector4f) {
      m._m00(col0.x)
      m._m01(col0.y)
      m._m02(col0.z)
      m._m03(col0.w)
      m._m10(col1.x)
      m._m11(col1.y)
      m._m12(col1.z)
      m._m13(col1.w)
      m._m20(col2.x)
      m._m21(col2.y)
      m._m22(col2.z)
      m._m23(col2.w)
      m._m30(col3.x)
      m._m31(col3.y)
      m._m32(col3.z)
      m._m33(col3.w)
    }

    override fun set(m: Matrix4x3f, col0: Vector3m, col1: Vector3m, col2: Vector3m, col3: Vector3m) {
      m._m00(col0.x)
      m._m01(col0.y)
      m._m02(col0.z)
      m._m10(col1.x)
      m._m11(col1.y)
      m._m12(col1.z)
      m._m20(col2.x)
      m._m21(col2.y)
      m._m22(col2.z)
      m._m30(col3.x)
      m._m31(col3.y)
      m._m32(col3.z)
    }

    override fun set(m: Matrix3f, col0: Vector3m, col1: Vector3m, col2: Vector3m) {
      m._m00(col0.x)
      m._m01(col0.y)
      m._m02(col0.z)
      m._m10(col1.x)
      m._m11(col1.y)
      m._m12(col1.z)
      m._m20(col2.x)
      m._m21(col2.y)
      m._m22(col2.z)
    }

    override fun putColumn0(src: Matrix4f, dest: Vector4f) {
      dest.x = src.m00()
      dest.y = src.m01()
      dest.z = src.m02()
      dest.w = src.m03()
    }

    override fun putColumn1(src: Matrix4f, dest: Vector4f) {
      dest.x = src.m10()
      dest.y = src.m11()
      dest.z = src.m12()
      dest.w = src.m13()
    }

    override fun putColumn2(src: Matrix4f, dest: Vector4f) {
      dest.x = src.m20()
      dest.y = src.m21()
      dest.z = src.m22()
      dest.w = src.m23()
    }

    override fun putColumn3(src: Matrix4f, dest: Vector4f) {
      dest.x = src.m30()
      dest.y = src.m31()
      dest.z = src.m32()
      dest.w = src.m33()
    }

    override fun getColumn0(dest: Matrix4f, src: Vector4f) {
      dest._m00(src.x)
      dest._m01(src.y)
      dest._m02(src.z)
      dest._m03(src.w)
    }

    override fun getColumn1(dest: Matrix4f, src: Vector4f) {
      dest._m10(src.x)
      dest._m11(src.y)
      dest._m12(src.z)
      dest._m13(src.w)
    }

    override fun getColumn2(dest: Matrix4f, src: Vector4f) {
      dest._m20(src.x)
      dest._m21(src.y)
      dest._m22(src.z)
      dest._m23(src.w)
    }

    override fun getColumn3(dest: Matrix4f, src: Vector4f) {
      dest._m30(src.x)
      dest._m31(src.y)
      dest._m32(src.z)
      dest._m33(src.w)
    }

    override fun broadcast(c: Float, dest: Vector4f) {
      dest.x = c
      dest.y = c
      dest.z = c
      dest.w = c
    }

    override fun broadcast(c: Int, dest: Vector4i) {
      dest.x = c
      dest.y = c
      dest.z = c
      dest.w = c
    }
  }

  class MemUtilUnsafe : MemUtilNIO() {

    fun addressOf(buffer: Buffer): Long {
      return UNSAFE.getLong(buffer, ADDRESS)
    }

    fun put(m: Matrix4f, destAddr: Long) {
      for (i in 0..7) {
        putLong(null, destAddr + (i shl 3), UNSAFE.getLong(m, Matrix4f_m00 + (i shl 3)))
      }
    }

    fun put(m: Matrix4x3f, destAddr: Long) {
      for (i in 0..5) {
        putLong(null, destAddr + (i shl 3), UNSAFE.getLong(m, Matrix4x3f_m00 + (i shl 3)))
      }
    }

    fun put4x4(m: Matrix4x3f, destAddr: Long) {
      for (i in 0..3) {
        putLong(null, destAddr + (i shl 4), UNSAFE.getLong(m, Matrix4x3f_m00 + 12 * i))
        val lng = (UNSAFE.getInt(m, Matrix4x3f_m00 + 8 + (12 * i).toLong()) and 0xFFFFFFFFL.toInt()).toLong()
        putLong(null, destAddr + 8 + (i shl 4).toLong(), lng)
      }
      UNSAFE.putFloat(null, destAddr + 60, 1.0f)
    }

    fun put4x4(m: Matrix4x3d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, m.m00())
      UNSAFE.putDouble(null, destAddr + 8, m.m01())
      UNSAFE.putDouble(null, destAddr + 16, m.m02())
      UNSAFE.putDouble(null, destAddr + 24, 0.0)
      UNSAFE.putDouble(null, destAddr + 32, m.m10())
      UNSAFE.putDouble(null, destAddr + 40, m.m11())
      UNSAFE.putDouble(null, destAddr + 48, m.m12())
      UNSAFE.putDouble(null, destAddr + 56, 0.0)
      UNSAFE.putDouble(null, destAddr + 64, m.m20())
      UNSAFE.putDouble(null, destAddr + 72, m.m21())
      UNSAFE.putDouble(null, destAddr + 80, m.m22())
      UNSAFE.putDouble(null, destAddr + 88, 0.0)
      UNSAFE.putDouble(null, destAddr + 96, m.m30())
      UNSAFE.putDouble(null, destAddr + 104, m.m31())
      UNSAFE.putDouble(null, destAddr + 112, m.m32())
      UNSAFE.putDouble(null, destAddr + 120, 1.0)
    }

    fun put4x4(m: Matrix3x2f, destAddr: Long) {
      UNSAFE.putLong(null, destAddr, UNSAFE.getLong(m, Matrix3x2f_m00))
      UNSAFE.putLong(null, destAddr + 8, 0L)
      UNSAFE.putLong(null, destAddr + 16, UNSAFE.getLong(m, Matrix3x2f_m00 + 8))
      UNSAFE.putLong(null, destAddr + 24, 0L)
      UNSAFE.putLong(null, destAddr + 32, 0L)
      UNSAFE.putLong(null, destAddr + 40, 0x3F800000L)
      UNSAFE.putLong(null, destAddr + 48, UNSAFE.getLong(m, Matrix3x2f_m00 + 16))
      UNSAFE.putLong(null, destAddr + 56, 0x3F80000000000000L)
    }

    fun put4x4(m: Matrix3x2d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, m.m00())
      UNSAFE.putDouble(null, destAddr + 8, m.m01())
      UNSAFE.putDouble(null, destAddr + 16, 0.0)
      UNSAFE.putDouble(null, destAddr + 24, 0.0)
      UNSAFE.putDouble(null, destAddr + 32, m.m10())
      UNSAFE.putDouble(null, destAddr + 40, m.m11())
      UNSAFE.putDouble(null, destAddr + 48, 0.0)
      UNSAFE.putDouble(null, destAddr + 56, 0.0)
      UNSAFE.putDouble(null, destAddr + 64, 0.0)
      UNSAFE.putDouble(null, destAddr + 72, 0.0)
      UNSAFE.putDouble(null, destAddr + 80, 1.0)
      UNSAFE.putDouble(null, destAddr + 88, 0.0)
      UNSAFE.putDouble(null, destAddr + 96, m.m20())
      UNSAFE.putDouble(null, destAddr + 104, m.m21())
      UNSAFE.putDouble(null, destAddr + 112, 0.0)
      UNSAFE.putDouble(null, destAddr + 120, 1.0)
    }

    fun put3x3(m: Matrix3x2f, destAddr: Long) {
      UNSAFE.putLong(null, destAddr, UNSAFE.getLong(m, Matrix3x2f_m00))
      UNSAFE.putInt(null, destAddr + 8, 0)
      UNSAFE.putLong(null, destAddr + 12, UNSAFE.getLong(m, Matrix3x2f_m00 + 8))
      UNSAFE.putInt(null, destAddr + 20, 0)
      UNSAFE.putLong(null, destAddr + 24, UNSAFE.getLong(m, Matrix3x2f_m00 + 16))
      UNSAFE.putFloat(null, destAddr + 32, 0.0f)
    }

    fun put3x3(m: Matrix3x2d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, m.m00())
      UNSAFE.putDouble(null, destAddr + 8, m.m01())
      UNSAFE.putDouble(null, destAddr + 16, 0.0)
      UNSAFE.putDouble(null, destAddr + 24, m.m10())
      UNSAFE.putDouble(null, destAddr + 32, m.m11())
      UNSAFE.putDouble(null, destAddr + 40, 0.0)
      UNSAFE.putDouble(null, destAddr + 48, m.m20())
      UNSAFE.putDouble(null, destAddr + 56, m.m21())
      UNSAFE.putDouble(null, destAddr + 64, 1.0)
    }

    fun putTransposed(m: Matrix4f, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, m.m00())
      UNSAFE.putFloat(null, destAddr + 4, m.m10())
      UNSAFE.putFloat(null, destAddr + 8, m.m20())
      UNSAFE.putFloat(null, destAddr + 12, m.m30())
      UNSAFE.putFloat(null, destAddr + 16, m.m01())
      UNSAFE.putFloat(null, destAddr + 20, m.m11())
      UNSAFE.putFloat(null, destAddr + 24, m.m21())
      UNSAFE.putFloat(null, destAddr + 28, m.m31())
      UNSAFE.putFloat(null, destAddr + 32, m.m02())
      UNSAFE.putFloat(null, destAddr + 36, m.m12())
      UNSAFE.putFloat(null, destAddr + 40, m.m22())
      UNSAFE.putFloat(null, destAddr + 44, m.m32())
      UNSAFE.putFloat(null, destAddr + 48, m.m03())
      UNSAFE.putFloat(null, destAddr + 52, m.m13())
      UNSAFE.putFloat(null, destAddr + 56, m.m23())
      UNSAFE.putFloat(null, destAddr + 60, m.m33())
    }

    fun put4x3Transposed(m: Matrix4f, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, m.m00())
      UNSAFE.putFloat(null, destAddr + 4, m.m10())
      UNSAFE.putFloat(null, destAddr + 8, m.m20())
      UNSAFE.putFloat(null, destAddr + 12, m.m30())
      UNSAFE.putFloat(null, destAddr + 16, m.m01())
      UNSAFE.putFloat(null, destAddr + 20, m.m11())
      UNSAFE.putFloat(null, destAddr + 24, m.m21())
      UNSAFE.putFloat(null, destAddr + 28, m.m31())
      UNSAFE.putFloat(null, destAddr + 32, m.m02())
      UNSAFE.putFloat(null, destAddr + 36, m.m12())
      UNSAFE.putFloat(null, destAddr + 40, m.m22())
      UNSAFE.putFloat(null, destAddr + 44, m.m32())
    }

    fun putTransposed(m: Matrix4x3f, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, m.m00())
      UNSAFE.putFloat(null, destAddr + 4, m.m10())
      UNSAFE.putFloat(null, destAddr + 8, m.m20())
      UNSAFE.putFloat(null, destAddr + 12, m.m30())
      UNSAFE.putFloat(null, destAddr + 16, m.m01())
      UNSAFE.putFloat(null, destAddr + 20, m.m11())
      UNSAFE.putFloat(null, destAddr + 24, m.m21())
      UNSAFE.putFloat(null, destAddr + 28, m.m31())
      UNSAFE.putFloat(null, destAddr + 32, m.m02())
      UNSAFE.putFloat(null, destAddr + 36, m.m12())
      UNSAFE.putFloat(null, destAddr + 40, m.m22())
      UNSAFE.putFloat(null, destAddr + 44, m.m32())
    }

    fun putTransposed(m: Matrix3f, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, m.m00())
      UNSAFE.putFloat(null, destAddr + 4, m.m10())
      UNSAFE.putFloat(null, destAddr + 8, m.m20())
      UNSAFE.putFloat(null, destAddr + 12, m.m01())
      UNSAFE.putFloat(null, destAddr + 16, m.m11())
      UNSAFE.putFloat(null, destAddr + 20, m.m21())
      UNSAFE.putFloat(null, destAddr + 24, m.m02())
      UNSAFE.putFloat(null, destAddr + 28, m.m12())
      UNSAFE.putFloat(null, destAddr + 32, m.m22())
    }

    fun put(m: Matrix4d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, m.m00())
      UNSAFE.putDouble(null, destAddr + 8, m.m01())
      UNSAFE.putDouble(null, destAddr + 16, m.m02())
      UNSAFE.putDouble(null, destAddr + 24, m.m03())
      UNSAFE.putDouble(null, destAddr + 32, m.m10())
      UNSAFE.putDouble(null, destAddr + 40, m.m11())
      UNSAFE.putDouble(null, destAddr + 48, m.m12())
      UNSAFE.putDouble(null, destAddr + 56, m.m13())
      UNSAFE.putDouble(null, destAddr + 64, m.m20())
      UNSAFE.putDouble(null, destAddr + 72, m.m21())
      UNSAFE.putDouble(null, destAddr + 80, m.m22())
      UNSAFE.putDouble(null, destAddr + 88, m.m23())
      UNSAFE.putDouble(null, destAddr + 96, m.m30())
      UNSAFE.putDouble(null, destAddr + 104, m.m31())
      UNSAFE.putDouble(null, destAddr + 112, m.m32())
      UNSAFE.putDouble(null, destAddr + 120, m.m33())
    }

    fun put(m: Matrix4x3d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, m.m00())
      UNSAFE.putDouble(null, destAddr + 8, m.m01())
      UNSAFE.putDouble(null, destAddr + 16, m.m02())
      UNSAFE.putDouble(null, destAddr + 24, m.m10())
      UNSAFE.putDouble(null, destAddr + 32, m.m11())
      UNSAFE.putDouble(null, destAddr + 40, m.m12())
      UNSAFE.putDouble(null, destAddr + 48, m.m20())
      UNSAFE.putDouble(null, destAddr + 56, m.m21())
      UNSAFE.putDouble(null, destAddr + 64, m.m22())
      UNSAFE.putDouble(null, destAddr + 72, m.m30())
      UNSAFE.putDouble(null, destAddr + 80, m.m31())
      UNSAFE.putDouble(null, destAddr + 88, m.m32())
    }

    fun putTransposed(m: Matrix4d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, m.m00())
      UNSAFE.putDouble(null, destAddr + 8, m.m10())
      UNSAFE.putDouble(null, destAddr + 16, m.m20())
      UNSAFE.putDouble(null, destAddr + 24, m.m30())
      UNSAFE.putDouble(null, destAddr + 32, m.m01())
      UNSAFE.putDouble(null, destAddr + 40, m.m11())
      UNSAFE.putDouble(null, destAddr + 48, m.m21())
      UNSAFE.putDouble(null, destAddr + 56, m.m31())
      UNSAFE.putDouble(null, destAddr + 64, m.m02())
      UNSAFE.putDouble(null, destAddr + 72, m.m12())
      UNSAFE.putDouble(null, destAddr + 80, m.m22())
      UNSAFE.putDouble(null, destAddr + 88, m.m32())
      UNSAFE.putDouble(null, destAddr + 96, m.m03())
      UNSAFE.putDouble(null, destAddr + 104, m.m13())
      UNSAFE.putDouble(null, destAddr + 112, m.m23())
      UNSAFE.putDouble(null, destAddr + 120, m.m33())
    }

    fun putfTransposed(m: Matrix4d, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, m.m00().toFloat())
      UNSAFE.putFloat(null, destAddr + 4, m.m10().toFloat())
      UNSAFE.putFloat(null, destAddr + 8, m.m20().toFloat())
      UNSAFE.putFloat(null, destAddr + 12, m.m30().toFloat())
      UNSAFE.putFloat(null, destAddr + 16, m.m01().toFloat())
      UNSAFE.putFloat(null, destAddr + 20, m.m11().toFloat())
      UNSAFE.putFloat(null, destAddr + 24, m.m21().toFloat())
      UNSAFE.putFloat(null, destAddr + 28, m.m31().toFloat())
      UNSAFE.putFloat(null, destAddr + 32, m.m02().toFloat())
      UNSAFE.putFloat(null, destAddr + 36, m.m12().toFloat())
      UNSAFE.putFloat(null, destAddr + 40, m.m22().toFloat())
      UNSAFE.putFloat(null, destAddr + 44, m.m32().toFloat())
      UNSAFE.putFloat(null, destAddr + 48, m.m03().toFloat())
      UNSAFE.putFloat(null, destAddr + 52, m.m13().toFloat())
      UNSAFE.putFloat(null, destAddr + 56, m.m23().toFloat())
      UNSAFE.putFloat(null, destAddr + 60, m.m33().toFloat())
    }

    fun put4x3Transposed(m: Matrix4d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, m.m00())
      UNSAFE.putDouble(null, destAddr + 8, m.m10())
      UNSAFE.putDouble(null, destAddr + 16, m.m20())
      UNSAFE.putDouble(null, destAddr + 24, m.m30())
      UNSAFE.putDouble(null, destAddr + 32, m.m01())
      UNSAFE.putDouble(null, destAddr + 40, m.m11())
      UNSAFE.putDouble(null, destAddr + 48, m.m21())
      UNSAFE.putDouble(null, destAddr + 56, m.m31())
      UNSAFE.putDouble(null, destAddr + 64, m.m02())
      UNSAFE.putDouble(null, destAddr + 72, m.m12())
      UNSAFE.putDouble(null, destAddr + 80, m.m22())
      UNSAFE.putDouble(null, destAddr + 88, m.m32())
    }

    fun putTransposed(m: Matrix4x3d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, m.m00())
      UNSAFE.putDouble(null, destAddr + 8, m.m10())
      UNSAFE.putDouble(null, destAddr + 16, m.m20())
      UNSAFE.putDouble(null, destAddr + 24, m.m30())
      UNSAFE.putDouble(null, destAddr + 32, m.m01())
      UNSAFE.putDouble(null, destAddr + 40, m.m11())
      UNSAFE.putDouble(null, destAddr + 48, m.m21())
      UNSAFE.putDouble(null, destAddr + 56, m.m31())
      UNSAFE.putDouble(null, destAddr + 64, m.m02())
      UNSAFE.putDouble(null, destAddr + 72, m.m12())
      UNSAFE.putDouble(null, destAddr + 80, m.m22())
      UNSAFE.putDouble(null, destAddr + 88, m.m32())
    }

    fun putfTransposed(m: Matrix4x3d, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, m.m00().toFloat())
      UNSAFE.putFloat(null, destAddr + 4, m.m10().toFloat())
      UNSAFE.putFloat(null, destAddr + 8, m.m20().toFloat())
      UNSAFE.putFloat(null, destAddr + 12, m.m30().toFloat())
      UNSAFE.putFloat(null, destAddr + 16, m.m01().toFloat())
      UNSAFE.putFloat(null, destAddr + 20, m.m11().toFloat())
      UNSAFE.putFloat(null, destAddr + 24, m.m21().toFloat())
      UNSAFE.putFloat(null, destAddr + 28, m.m31().toFloat())
      UNSAFE.putFloat(null, destAddr + 32, m.m02().toFloat())
      UNSAFE.putFloat(null, destAddr + 36, m.m12().toFloat())
      UNSAFE.putFloat(null, destAddr + 40, m.m22().toFloat())
      UNSAFE.putFloat(null, destAddr + 44, m.m32().toFloat())
    }

    fun putf(m: Matrix4d, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, m.m00().toFloat())
      UNSAFE.putFloat(null, destAddr + 4, m.m01().toFloat())
      UNSAFE.putFloat(null, destAddr + 8, m.m02().toFloat())
      UNSAFE.putFloat(null, destAddr + 12, m.m03().toFloat())
      UNSAFE.putFloat(null, destAddr + 16, m.m10().toFloat())
      UNSAFE.putFloat(null, destAddr + 20, m.m11().toFloat())
      UNSAFE.putFloat(null, destAddr + 24, m.m12().toFloat())
      UNSAFE.putFloat(null, destAddr + 28, m.m13().toFloat())
      UNSAFE.putFloat(null, destAddr + 32, m.m20().toFloat())
      UNSAFE.putFloat(null, destAddr + 36, m.m21().toFloat())
      UNSAFE.putFloat(null, destAddr + 40, m.m22().toFloat())
      UNSAFE.putFloat(null, destAddr + 44, m.m23().toFloat())
      UNSAFE.putFloat(null, destAddr + 48, m.m30().toFloat())
      UNSAFE.putFloat(null, destAddr + 52, m.m31().toFloat())
      UNSAFE.putFloat(null, destAddr + 56, m.m32().toFloat())
      UNSAFE.putFloat(null, destAddr + 60, m.m33().toFloat())
    }

    fun putf(m: Matrix4x3d, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, m.m00().toFloat())
      UNSAFE.putFloat(null, destAddr + 4, m.m01().toFloat())
      UNSAFE.putFloat(null, destAddr + 8, m.m02().toFloat())
      UNSAFE.putFloat(null, destAddr + 12, m.m10().toFloat())
      UNSAFE.putFloat(null, destAddr + 16, m.m11().toFloat())
      UNSAFE.putFloat(null, destAddr + 20, m.m12().toFloat())
      UNSAFE.putFloat(null, destAddr + 24, m.m20().toFloat())
      UNSAFE.putFloat(null, destAddr + 28, m.m21().toFloat())
      UNSAFE.putFloat(null, destAddr + 32, m.m22().toFloat())
      UNSAFE.putFloat(null, destAddr + 36, m.m30().toFloat())
      UNSAFE.putFloat(null, destAddr + 40, m.m31().toFloat())
      UNSAFE.putFloat(null, destAddr + 44, m.m32().toFloat())
    }

    fun put(m: Matrix3f, destAddr: Long) {
      for (i in 0..3) {
        putLong(null, destAddr + (i shl 3), UNSAFE.getLong(m, Matrix3f_m00 + (i shl 3)))
      }
      UNSAFE.putFloat(null, destAddr + 32, m.m22())
    }

    fun put(m: Matrix3d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, m.m00())
      UNSAFE.putDouble(null, destAddr + 8, m.m01())
      UNSAFE.putDouble(null, destAddr + 16, m.m02())
      UNSAFE.putDouble(null, destAddr + 24, m.m10())
      UNSAFE.putDouble(null, destAddr + 32, m.m11())
      UNSAFE.putDouble(null, destAddr + 40, m.m12())
      UNSAFE.putDouble(null, destAddr + 48, m.m20())
      UNSAFE.putDouble(null, destAddr + 56, m.m21())
      UNSAFE.putDouble(null, destAddr + 64, m.m22())
    }

    fun put(m: Matrix3x2f, destAddr: Long) {
      for (i in 0..2) {
        putLong(null, destAddr + (i shl 3), UNSAFE.getLong(m, Matrix3x2f_m00 + (i shl 3)))
      }
    }

    fun put(m: Matrix3x2d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, m.m00())
      UNSAFE.putDouble(null, destAddr + 8, m.m01())
      UNSAFE.putDouble(null, destAddr + 16, m.m10())
      UNSAFE.putDouble(null, destAddr + 24, m.m11())
      UNSAFE.putDouble(null, destAddr + 32, m.m20())
      UNSAFE.putDouble(null, destAddr + 40, m.m21())
    }

    fun putf(m: Matrix3d, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, m.m00().toFloat())
      UNSAFE.putFloat(null, destAddr + 4, m.m01().toFloat())
      UNSAFE.putFloat(null, destAddr + 8, m.m02().toFloat())
      UNSAFE.putFloat(null, destAddr + 12, m.m10().toFloat())
      UNSAFE.putFloat(null, destAddr + 16, m.m11().toFloat())
      UNSAFE.putFloat(null, destAddr + 20, m.m12().toFloat())
      UNSAFE.putFloat(null, destAddr + 24, m.m20().toFloat())
      UNSAFE.putFloat(null, destAddr + 28, m.m21().toFloat())
      UNSAFE.putFloat(null, destAddr + 32, m.m22().toFloat())
    }

    fun put(src: Vector4d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, src.x)
      UNSAFE.putDouble(null, destAddr + 8, src.y)
      UNSAFE.putDouble(null, destAddr + 16, src.z)
      UNSAFE.putDouble(null, destAddr + 24, src.w)
    }

    fun put(src: Vector4f, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, src.x)
      UNSAFE.putFloat(null, destAddr + 4, src.y)
      UNSAFE.putFloat(null, destAddr + 8, src.z)
      UNSAFE.putFloat(null, destAddr + 12, src.w)
    }

    fun put(src: Vector4i, destAddr: Long) {
      UNSAFE.putInt(null, destAddr, src.x)
      UNSAFE.putInt(null, destAddr + 4, src.y)
      UNSAFE.putInt(null, destAddr + 8, src.z)
      UNSAFE.putInt(null, destAddr + 12, src.w)
    }

    fun put(src: Vector3m, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, src.x)
      UNSAFE.putFloat(null, destAddr + 4, src.y)
      UNSAFE.putFloat(null, destAddr + 8, src.z)
    }

    fun put(src: Vector3d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, src.x)
      UNSAFE.putDouble(null, destAddr + 8, src.y)
      UNSAFE.putDouble(null, destAddr + 16, src.z)
    }

    fun put(src: Vector3i, destAddr: Long) {
      UNSAFE.putInt(null, destAddr, src.x)
      UNSAFE.putInt(null, destAddr + 4, src.y)
      UNSAFE.putInt(null, destAddr + 8, src.z)
    }

    fun put(src: Vector2f, destAddr: Long) {
      UNSAFE.putFloat(null, destAddr, src.x)
      UNSAFE.putFloat(null, destAddr + 4, src.y)
    }

    fun put(src: Vector2d, destAddr: Long) {
      UNSAFE.putDouble(null, destAddr, UNSAFE.getDouble(src, Vector2d_x))
      UNSAFE.putDouble(null, destAddr + 8, UNSAFE.getDouble(src, Vector2d_x + 8))
    }

    fun put(src: Vector2i, destAddr: Long) {
      UNSAFE.putInt(null, destAddr, src.x)
      UNSAFE.putInt(null, destAddr + 4, src.y)
    }

    operator fun get(m: Matrix4f, srcAddr: Long) {
      for (i in 0..7) {
        putLong(m, Matrix4f_m00 + (i shl 3), UNSAFE.getLong(srcAddr + (i shl 3)))
      }
    }

    operator fun get(m: Matrix4x3f, srcAddr: Long) {
      for (i in 0..5) {
        putLong(m, Matrix4x3f_m00 + (i shl 3), UNSAFE.getLong(srcAddr + (i shl 3)))
      }
    }

    operator fun get(m: Matrix4d, srcAddr: Long) {
      m._m00(UNSAFE.getDouble(null, srcAddr))
      m._m01(UNSAFE.getDouble(null, srcAddr + 8))
      m._m02(UNSAFE.getDouble(null, srcAddr + 16))
      m._m03(UNSAFE.getDouble(null, srcAddr + 24))
      m._m10(UNSAFE.getDouble(null, srcAddr + 32))
      m._m11(UNSAFE.getDouble(null, srcAddr + 40))
      m._m12(UNSAFE.getDouble(null, srcAddr + 48))
      m._m13(UNSAFE.getDouble(null, srcAddr + 56))
      m._m20(UNSAFE.getDouble(null, srcAddr + 64))
      m._m21(UNSAFE.getDouble(null, srcAddr + 72))
      m._m22(UNSAFE.getDouble(null, srcAddr + 80))
      m._m23(UNSAFE.getDouble(null, srcAddr + 88))
      m._m30(UNSAFE.getDouble(null, srcAddr + 96))
      m._m31(UNSAFE.getDouble(null, srcAddr + 104))
      m._m32(UNSAFE.getDouble(null, srcAddr + 112))
      m._m33(UNSAFE.getDouble(null, srcAddr + 120))
    }

    operator fun get(m: Matrix4x3d, srcAddr: Long) {
      m._m00(UNSAFE.getDouble(null, srcAddr))
      m._m01(UNSAFE.getDouble(null, srcAddr + 8))
      m._m02(UNSAFE.getDouble(null, srcAddr + 16))
      m._m10(UNSAFE.getDouble(null, srcAddr + 24))
      m._m11(UNSAFE.getDouble(null, srcAddr + 32))
      m._m12(UNSAFE.getDouble(null, srcAddr + 40))
      m._m20(UNSAFE.getDouble(null, srcAddr + 48))
      m._m21(UNSAFE.getDouble(null, srcAddr + 56))
      m._m22(UNSAFE.getDouble(null, srcAddr + 64))
      m._m30(UNSAFE.getDouble(null, srcAddr + 72))
      m._m31(UNSAFE.getDouble(null, srcAddr + 80))
      m._m32(UNSAFE.getDouble(null, srcAddr + 88))
    }

    fun getf(m: Matrix4d, srcAddr: Long) {
      m._m00(UNSAFE.getFloat(null, srcAddr).toDouble())
      m._m01(UNSAFE.getFloat(null, srcAddr + 4).toDouble())
      m._m02(UNSAFE.getFloat(null, srcAddr + 8).toDouble())
      m._m03(UNSAFE.getFloat(null, srcAddr + 12).toDouble())
      m._m10(UNSAFE.getFloat(null, srcAddr + 16).toDouble())
      m._m11(UNSAFE.getFloat(null, srcAddr + 20).toDouble())
      m._m12(UNSAFE.getFloat(null, srcAddr + 24).toDouble())
      m._m13(UNSAFE.getFloat(null, srcAddr + 28).toDouble())
      m._m20(UNSAFE.getFloat(null, srcAddr + 32).toDouble())
      m._m21(UNSAFE.getFloat(null, srcAddr + 36).toDouble())
      m._m22(UNSAFE.getFloat(null, srcAddr + 40).toDouble())
      m._m23(UNSAFE.getFloat(null, srcAddr + 44).toDouble())
      m._m30(UNSAFE.getFloat(null, srcAddr + 48).toDouble())
      m._m31(UNSAFE.getFloat(null, srcAddr + 52).toDouble())
      m._m32(UNSAFE.getFloat(null, srcAddr + 56).toDouble())
      m._m33(UNSAFE.getFloat(null, srcAddr + 60).toDouble())
    }

    fun getf(m: Matrix4x3d, srcAddr: Long) {
      m._m00(UNSAFE.getFloat(null, srcAddr).toDouble())
      m._m01(UNSAFE.getFloat(null, srcAddr + 4).toDouble())
      m._m02(UNSAFE.getFloat(null, srcAddr + 8).toDouble())
      m._m10(UNSAFE.getFloat(null, srcAddr + 12).toDouble())
      m._m11(UNSAFE.getFloat(null, srcAddr + 16).toDouble())
      m._m12(UNSAFE.getFloat(null, srcAddr + 20).toDouble())
      m._m20(UNSAFE.getFloat(null, srcAddr + 24).toDouble())
      m._m21(UNSAFE.getFloat(null, srcAddr + 28).toDouble())
      m._m22(UNSAFE.getFloat(null, srcAddr + 32).toDouble())
      m._m30(UNSAFE.getFloat(null, srcAddr + 36).toDouble())
      m._m31(UNSAFE.getFloat(null, srcAddr + 40).toDouble())
      m._m32(UNSAFE.getFloat(null, srcAddr + 44).toDouble())
    }

    operator fun get(m: Matrix3f, srcAddr: Long) {
      for (i in 0..3) {
        putLong(m, Matrix3f_m00 + (i shl 3), UNSAFE.getLong(null, srcAddr + (i shl 3)))
      }
      m._m22(UNSAFE.getFloat(null, srcAddr + 32))
    }

    operator fun get(m: Matrix3d, srcAddr: Long) {
      m._m00(UNSAFE.getDouble(null, srcAddr))
      m._m01(UNSAFE.getDouble(null, srcAddr + 8))
      m._m02(UNSAFE.getDouble(null, srcAddr + 16))
      m._m10(UNSAFE.getDouble(null, srcAddr + 24))
      m._m11(UNSAFE.getDouble(null, srcAddr + 32))
      m._m12(UNSAFE.getDouble(null, srcAddr + 40))
      m._m20(UNSAFE.getDouble(null, srcAddr + 48))
      m._m21(UNSAFE.getDouble(null, srcAddr + 56))
      m._m22(UNSAFE.getDouble(null, srcAddr + 64))
    }

    operator fun get(m: Matrix3x2f, srcAddr: Long) {
      for (i in 0..2) {
        putLong(m, Matrix3x2f_m00 + (i shl 3), UNSAFE.getLong(null, srcAddr + (i shl 3)))
      }
    }

    operator fun get(m: Matrix3x2d, srcAddr: Long) {
      m._m00(UNSAFE.getDouble(null, srcAddr))
      m._m01(UNSAFE.getDouble(null, srcAddr + 8))
      m._m10(UNSAFE.getDouble(null, srcAddr + 16))
      m._m11(UNSAFE.getDouble(null, srcAddr + 24))
      m._m20(UNSAFE.getDouble(null, srcAddr + 32))
      m._m21(UNSAFE.getDouble(null, srcAddr + 40))
    }

    fun getf(m: Matrix3d, srcAddr: Long) {
      m._m00(UNSAFE.getFloat(null, srcAddr).toDouble())
      m._m01(UNSAFE.getFloat(null, srcAddr + 4).toDouble())
      m._m02(UNSAFE.getFloat(null, srcAddr + 8).toDouble())
      m._m10(UNSAFE.getFloat(null, srcAddr + 12).toDouble())
      m._m11(UNSAFE.getFloat(null, srcAddr + 16).toDouble())
      m._m12(UNSAFE.getFloat(null, srcAddr + 20).toDouble())
      m._m20(UNSAFE.getFloat(null, srcAddr + 24).toDouble())
      m._m21(UNSAFE.getFloat(null, srcAddr + 28).toDouble())
      m._m22(UNSAFE.getFloat(null, srcAddr + 32).toDouble())
    }

    operator fun get(dst: Vector4d, srcAddr: Long) {
      dst.x = UNSAFE.getLong(null, srcAddr).toDouble()
      dst.y = UNSAFE.getLong(null, srcAddr + 8).toDouble()
      dst.z = UNSAFE.getLong(null, srcAddr + 16).toDouble()
      dst.w = UNSAFE.getLong(null, srcAddr + 24).toDouble()
    }

    operator fun get(dst: Vector4f, srcAddr: Long) {
      dst.x = UNSAFE.getFloat(null, srcAddr)
      dst.y = UNSAFE.getFloat(null, srcAddr + 4)
      dst.z = UNSAFE.getFloat(null, srcAddr + 8)
      dst.w = UNSAFE.getFloat(null, srcAddr + 12)
    }

    operator fun get(dst: Vector4i, srcAddr: Long) {
      dst.x = UNSAFE.getInt(null, srcAddr)
      dst.y = UNSAFE.getInt(null, srcAddr + 4)
      dst.z = UNSAFE.getInt(null, srcAddr + 8)
      dst.w = UNSAFE.getInt(null, srcAddr + 12)
    }

    operator fun get(dst: Vector3m, srcAddr: Long) {
      dst.x = UNSAFE.getFloat(null, srcAddr)
      dst.y = UNSAFE.getFloat(null, srcAddr + 4)
      dst.z = UNSAFE.getFloat(null, srcAddr + 8)
    }

    operator fun get(dst: Vector3d, srcAddr: Long) {
      dst.x = UNSAFE.getDouble(null, srcAddr)
      dst.y = UNSAFE.getDouble(null, srcAddr + 8)
      dst.z = UNSAFE.getDouble(null, srcAddr + 16)
    }

    operator fun get(dst: Vector3i, srcAddr: Long) {
      dst.x = UNSAFE.getInt(null, srcAddr)
      dst.y = UNSAFE.getInt(null, srcAddr + 4)
      dst.z = UNSAFE.getInt(null, srcAddr + 8)
    }

    operator fun get(dst: Vector2f, srcAddr: Long) {
      dst.x = UNSAFE.getFloat(null, srcAddr)
      dst.y = UNSAFE.getFloat(null, srcAddr + 4)
    }

    operator fun get(dst: Vector2d, srcAddr: Long) {
      dst.x = UNSAFE.getDouble(null, srcAddr)
      dst.y = UNSAFE.getDouble(null, srcAddr + 8)
    }

    operator fun get(dst: Vector2i, srcAddr: Long) {
      dst.x = UNSAFE.getInt(null, srcAddr)
      dst.y = UNSAFE.getInt(null, srcAddr + 4)
    }

    fun putMatrix3f(q: Quaternionf, addr: Long) {
      val dx = q.x + q.x
      val dy = q.y + q.y
      val dz = q.z + q.z
      val q00 = dx * q.x
      val q11 = dy * q.y
      val q22 = dz * q.z
      val q01 = dx * q.y
      val q02 = dx * q.z
      val q03 = dx * q.w
      val q12 = dy * q.z
      val q13 = dy * q.w
      val q23 = dz * q.w
      UNSAFE.putFloat(null, addr, 1.0f - q11 - q22)
      UNSAFE.putFloat(null, addr + 4, q01 + q23)
      UNSAFE.putFloat(null, addr + 8, q02 - q13)
      UNSAFE.putFloat(null, addr + 12, q01 - q23)
      UNSAFE.putFloat(null, addr + 16, 1.0f - q22 - q00)
      UNSAFE.putFloat(null, addr + 20, q12 + q03)
      UNSAFE.putFloat(null, addr + 24, q02 + q13)
      UNSAFE.putFloat(null, addr + 28, q12 - q03)
      UNSAFE.putFloat(null, addr + 32, 1.0f - q11 - q00)
    }

    fun putMatrix4f(q: Quaternionf, addr: Long) {
      val dx = q.x + q.x
      val dy = q.y + q.y
      val dz = q.z + q.z
      val q00 = dx * q.x
      val q11 = dy * q.y
      val q22 = dz * q.z
      val q01 = dx * q.y
      val q02 = dx * q.z
      val q03 = dx * q.w
      val q12 = dy * q.z
      val q13 = dy * q.w
      val q23 = dz * q.w
      UNSAFE.putFloat(null, addr, 1.0f - q11 - q22)
      UNSAFE.putFloat(null, addr + 4, q01 + q23)
      putLong(null, addr + 8, (java.lang.Float.floatToRawIntBits(q02 - q13) and 0xFFFFFFFFL.toInt()).toLong())
      UNSAFE.putFloat(null, addr + 16, q01 - q23)
      UNSAFE.putFloat(null, addr + 20, 1.0f - q22 - q00)
      putLong(null, addr + 24, (java.lang.Float.floatToRawIntBits(q12 + q03) and 0xFFFFFFFFL.toInt()).toLong())
      UNSAFE.putFloat(null, addr + 32, q02 + q13)
      UNSAFE.putFloat(null, addr + 36, q12 - q03)
      putLong(null, addr + 40, (java.lang.Float.floatToRawIntBits(1.0f - q11 - q00) and 0xFFFFFFFFL.toInt()).toLong())
      putLong(null, addr + 48, 0L)
      putLong(null, addr + 56, 0x3F80000000000000L)
    }

    fun putMatrix4x3f(q: Quaternionf, addr: Long) {
      val dx = q.x + q.x
      val dy = q.y + q.y
      val dz = q.z + q.z
      val q00 = dx * q.x
      val q11 = dy * q.y
      val q22 = dz * q.z
      val q01 = dx * q.y
      val q02 = dx * q.z
      val q03 = dx * q.w
      val q12 = dy * q.z
      val q13 = dy * q.w
      val q23 = dz * q.w
      UNSAFE.putFloat(null, addr, 1.0f - q11 - q22)
      UNSAFE.putFloat(null, addr + 4, q01 + q23)
      UNSAFE.putFloat(null, addr + 8, q02 - q13)
      UNSAFE.putFloat(null, addr + 12, q01 - q23)
      UNSAFE.putFloat(null, addr + 16, 1.0f - q22 - q00)
      UNSAFE.putFloat(null, addr + 20, q12 + q03)
      UNSAFE.putFloat(null, addr + 24, q02 + q13)
      UNSAFE.putFloat(null, addr + 28, q12 - q03)
      UNSAFE.putFloat(null, addr + 32, 1.0f - q11 - q00)
      putLong(null, addr + 36, 0L)
      UNSAFE.putFloat(null, addr + 44, 0.0f)
    }

    override fun putMatrix3f(q: Quaternionf, position: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      val addr = addressOf(dest) + position
      putMatrix3f(q, addr)
    }

    override fun putMatrix3f(q: Quaternionf, position: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      val addr = addressOf(dest) + (position shl 2)
      putMatrix3f(q, addr)
    }

    override fun putMatrix4f(q: Quaternionf, position: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      val addr = addressOf(dest) + position
      putMatrix4f(q, addr)
    }

    override fun putMatrix4f(q: Quaternionf, position: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      val addr = addressOf(dest) + (position shl 2)
      putMatrix4f(q, addr)
    }

    override fun putMatrix4x3f(q: Quaternionf, position: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      val addr = addressOf(dest) + position
      putMatrix4x3f(q, addr)
    }

    override fun putMatrix4x3f(q: Quaternionf, position: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      val addr = addressOf(dest) + (position shl 2)
      putMatrix4x3f(q, addr)
    }

    override fun put(m: Matrix4f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + (offset shl 2))
    }

    override fun put(m: Matrix4f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + offset)
    }

    override fun put(m: Matrix4x3f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + (offset shl 2))
    }

    override fun put(m: Matrix4x3f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + offset)
    }

    override fun put4x4(m: Matrix4x3f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x4(m, addressOf(dest) + (offset shl 2))
    }

    override fun put4x4(m: Matrix4x3f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x4(m, addressOf(dest) + offset)
    }

    override fun put4x4(m: Matrix4x3d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x4(m, addressOf(dest) + (offset shl 3))
    }

    override fun put4x4(m: Matrix4x3d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x4(m, addressOf(dest) + offset)
    }

    override fun put4x4(m: Matrix3x2f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x4(m, addressOf(dest) + (offset shl 2))
    }

    override fun put4x4(m: Matrix3x2f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x4(m, addressOf(dest) + offset)
    }

    override fun put4x4(m: Matrix3x2d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x4(m, addressOf(dest) + (offset shl 2))
    }

    override fun put4x4(m: Matrix3x2d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x4(m, addressOf(dest) + offset)
    }

    override fun put3x3(m: Matrix3x2f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put3x3(m, addressOf(dest) + (offset shl 2))
    }

    override fun put3x3(m: Matrix3x2f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put3x3(m, addressOf(dest) + offset)
    }

    override fun put3x3(m: Matrix3x2d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put3x3(m, addressOf(dest) + (offset shl 2))
    }

    override fun put3x3(m: Matrix3x2d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put3x3(m, addressOf(dest) + offset)
    }

    override fun putTransposed(m: Matrix4f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putTransposed(m, addressOf(dest) + (offset shl 2))
    }

    override fun putTransposed(m: Matrix4f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putTransposed(m, addressOf(dest) + offset)
    }

    override fun put4x3Transposed(m: Matrix4f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x3Transposed(m, addressOf(dest) + (offset shl 2))
    }

    override fun put4x3Transposed(m: Matrix4f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x3Transposed(m, addressOf(dest) + offset)
    }

    override fun putTransposed(m: Matrix4x3f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putTransposed(m, addressOf(dest) + (offset shl 2))
    }

    override fun putTransposed(m: Matrix4x3f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putTransposed(m, addressOf(dest) + offset)
    }

    override fun putTransposed(m: Matrix3f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putTransposed(m, addressOf(dest) + (offset shl 2))
    }

    override fun putTransposed(m: Matrix3f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putTransposed(m, addressOf(dest) + offset)
    }

    override fun put(m: Matrix4d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + (offset shl 3))
    }

    override fun put(m: Matrix4d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + offset)
    }

    override fun put(m: Matrix4x3d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + (offset shl 3))
    }

    override fun put(m: Matrix4x3d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + offset)
    }

    override fun putf(m: Matrix4d, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putf(m, addressOf(dest) + (offset shl 2))
    }

    override fun putf(m: Matrix4d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putf(m, addressOf(dest) + offset)
    }

    override fun putf(m: Matrix4x3d, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putf(m, addressOf(dest) + (offset shl 2))
    }

    override fun putf(m: Matrix4x3d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putf(m, addressOf(dest) + offset)
    }

    override fun putTransposed(m: Matrix4d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putTransposed(m, addressOf(dest) + (offset shl 3))
    }

    override fun putTransposed(m: Matrix4d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putTransposed(m, addressOf(dest) + offset)
    }

    override fun put4x3Transposed(m: Matrix4d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x3Transposed(m, addressOf(dest) + (offset shl 3))
    }

    override fun put4x3Transposed(m: Matrix4d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put4x3Transposed(m, addressOf(dest) + offset)
    }

    override fun putTransposed(m: Matrix4x3d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putTransposed(m, addressOf(dest) + (offset shl 3))
    }

    override fun putTransposed(m: Matrix4x3d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putTransposed(m, addressOf(dest) + offset)
    }

    override fun putfTransposed(m: Matrix4d, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putfTransposed(m, addressOf(dest) + (offset shl 2))
    }

    override fun putfTransposed(m: Matrix4d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putfTransposed(m, addressOf(dest) + offset)
    }

    override fun putfTransposed(m: Matrix4x3d, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putfTransposed(m, addressOf(dest) + (offset shl 2))
    }

    override fun putfTransposed(m: Matrix4x3d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putfTransposed(m, addressOf(dest) + offset)
    }

    override fun put(m: Matrix3f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + (offset shl 2))
    }

    override fun put(m: Matrix3f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + offset)
    }

    override fun put(m: Matrix3d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + (offset shl 3))
    }

    override fun put(m: Matrix3d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + offset)
    }

    override fun put(m: Matrix3x2f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + (offset shl 2))
    }

    override fun put(m: Matrix3x2f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + offset)
    }

    override fun put(m: Matrix3x2d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + (offset shl 2))
    }

    override fun put(m: Matrix3x2d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(m, addressOf(dest) + offset)
    }

    override fun putf(m: Matrix3d, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putf(m, addressOf(dest) + (offset shl 2))
    }

    override fun putf(m: Matrix3d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      putf(m, addressOf(dest) + offset)
    }

    override fun put(src: Vector4d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + (offset shl 3))
    }

    override fun put(src: Vector4d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + offset)
    }

    override fun put(src: Vector4f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + (offset shl 2))
    }

    override fun put(src: Vector4f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + offset)
    }

    override fun put(src: Vector4i, offset: Int, dest: IntBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + (offset shl 2))
    }

    override fun put(src: Vector4i, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + offset)
    }

    override fun put(src: Vector3m, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + (offset shl 2))
    }

    override fun put(src: Vector3m, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + offset)
    }

    override fun put(src: Vector3d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + (offset shl 3))
    }

    override fun put(src: Vector3d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + offset)
    }

    override fun put(src: Vector3i, offset: Int, dest: IntBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + (offset shl 2))
    }

    override fun put(src: Vector3i, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + offset)
    }

    override fun put(src: Vector2f, offset: Int, dest: FloatBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + (offset shl 2))
    }

    override fun put(src: Vector2f, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + offset)
    }

    override fun put(src: Vector2d, offset: Int, dest: DoubleBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + (offset shl 3))
    }

    override fun put(src: Vector2d, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + offset)
    }

    override fun put(src: Vector2i, offset: Int, dest: IntBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + (offset shl 2))
    }

    override fun put(src: Vector2i, offset: Int, dest: ByteBuffer) {
      if (Options.DEBUG && !dest.isDirect) {
        throwNoDirectBufferException()
      }
      put(src, addressOf(dest) + offset)
    }

    override fun get(m: Matrix4f, offset: Int, src: FloatBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + (offset shl 2))
    }

    override fun get(m: Matrix4f, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + offset)
    }

    override fun get(m: Matrix4x3f, offset: Int, src: FloatBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + (offset shl 2))
    }

    override fun get(m: Matrix4x3f, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + offset)
    }

    override fun get(m: Matrix4d, offset: Int, src: DoubleBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + (offset shl 3))
    }

    override fun get(m: Matrix4d, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + offset)
    }

    override fun get(m: Matrix4x3d, offset: Int, src: DoubleBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + (offset shl 3))
    }

    override fun get(m: Matrix4x3d, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + offset)
    }

    override fun getf(m: Matrix4d, offset: Int, src: FloatBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      getf(m, addressOf(src) + (offset shl 2))
    }

    override fun getf(m: Matrix4d, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      getf(m, addressOf(src) + offset)
    }

    override fun getf(m: Matrix4x3d, offset: Int, src: FloatBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      getf(m, addressOf(src) + (offset shl 2))
    }

    override fun getf(m: Matrix4x3d, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      getf(m, addressOf(src) + offset)
    }

    override fun get(m: Matrix3f, offset: Int, src: FloatBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + (offset shl 2))
    }

    override fun get(m: Matrix3f, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + offset)
    }

    override fun get(m: Matrix3d, offset: Int, src: DoubleBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + (offset shl 3))
    }

    override fun get(m: Matrix3d, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + offset)
    }

    override fun get(m: Matrix3x2f, offset: Int, src: FloatBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + (offset shl 2))
    }

    override fun get(m: Matrix3x2f, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + offset)
    }

    override fun get(m: Matrix3x2d, offset: Int, src: DoubleBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + (offset shl 2))
    }

    override fun get(m: Matrix3x2d, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(m, addressOf(src) + offset)
    }

    override fun getf(m: Matrix3d, offset: Int, src: FloatBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      getf(m, addressOf(src) + (offset shl 2))
    }

    override fun getf(m: Matrix3d, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      getf(m, addressOf(src) + offset)
    }

    override fun get(dst: Vector4d, offset: Int, src: DoubleBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + (offset shl 3))
    }

    override fun get(dst: Vector4d, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + offset)
    }

    override fun get(dst: Vector4f, offset: Int, src: FloatBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + (offset shl 2))
    }

    override fun get(dst: Vector4f, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + offset)
    }

    override fun get(dst: Vector4i, offset: Int, src: IntBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + (offset shl 2))
    }

    override fun get(dst: Vector4i, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + offset)
    }

    override fun get(dst: Vector3m, offset: Int, src: FloatBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + (offset shl 2))
    }

    override fun get(dst: Vector3m, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + offset)
    }

    override fun get(dst: Vector3d, offset: Int, src: DoubleBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + (offset shl 3))
    }

    override fun get(dst: Vector3d, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + offset)
    }

    override fun get(dst: Vector3i, offset: Int, src: IntBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + (offset shl 2))
    }

    override fun get(dst: Vector3i, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + offset)
    }

    override fun get(dst: Vector2f, offset: Int, src: FloatBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + (offset shl 2))
    }

    override fun get(dst: Vector2f, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + offset)
    }

    override fun get(dst: Vector2d, offset: Int, src: DoubleBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + (offset shl 3))
    }

    override fun get(dst: Vector2d, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + offset)
    }

    override fun get(dst: Vector2i, offset: Int, src: IntBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + (offset shl 2))
    }

    override fun get(dst: Vector2i, offset: Int, src: ByteBuffer) {
      if (Options.DEBUG && !src.isDirect) {
        throwNoDirectBufferException()
      }
      get(dst, addressOf(src) + offset)
    }

    companion object {
      val UNSAFE: sun.misc.Unsafe

      val ADDRESS: Long
      val Matrix3f_m00: Long
      val Matrix4f_m00: Long
      val Matrix4x3f_m00: Long
      val Matrix3x2f_m00: Long
      val Vector4f_x: Long
      val Vector4d_x: Long
      val Vector4i_x: Long
      val Vector3f_x: Long
      val Vector3d_x: Long
      val Vector3i_x: Long
      val Vector2f_x: Long
      val Vector2d_x: Long
      val Vector2i_x: Long
      val Quaternionf_x: Long
      val floatArrayOffset: Long

      /**
       * Used to create a direct ByteBuffer for a known address.
       */
      private external fun newTestBuffer(): ByteBuffer

      /**
       * Return the pointer size (4 = 32-bit, 8 = 64-bit).
       */
      private val pointerSize: Int
        external get

      init {
        UNSAFE = unsafeInstance
        try {
          ADDRESS = findBufferAddress()
          Matrix4f_m00 = checkMatrix4f()
          Matrix4x3f_m00 = checkMatrix4x3f()
          Matrix3f_m00 = checkMatrix3f()
          Matrix3x2f_m00 = checkMatrix3x2f()
          Vector4f_x = checkVector4f()
          Vector4d_x = checkVector4d()
          Vector4i_x = checkVector4i()
          Vector3f_x = checkVector3f()
          Vector3d_x = checkVector3d()
          Vector3i_x = checkVector3i()
          Vector2f_x = checkVector2f()
          Vector2d_x = checkVector2d()
          Vector2i_x = checkVector2i()
          Quaternionf_x = checkQuaternionf()
          floatArrayOffset = UNSAFE.arrayBaseOffset(FloatArray::class.java).toLong()
          // Check if we can use object field offset/address put/get methods
          sun.misc.Unsafe::class.java.getDeclaredMethod("getLong", *arrayOf<Class<*>>(Any::class.java, Long::class.javaPrimitiveType!!))
          sun.misc.Unsafe::class.java.getDeclaredMethod("putLong", *arrayOf<Class<*>>(Any::class.java, Long::class.javaPrimitiveType!!, Long::class.javaPrimitiveType!!))
        } catch (e: NoSuchFieldException) {
          throw UnsupportedOperationException(e)
        } catch (e: NoSuchMethodException) {
          throw UnsupportedOperationException(e)
        }

      }

      private fun atLeastJava9(classVersion: String): Boolean {
        try {
          val value = java.lang.Double.parseDouble(classVersion)
          return value >= 53.0
        } catch (e: NumberFormatException) {
          return false
        }
      }

      private fun findBufferAddress(): Long {
        val javaVersion = System.getProperty("java.class.version")
        return if (atLeastJava9(javaVersion))
          findBufferAddressJDK9(null)
        else
          findBufferAddressJDK1()
      }

      private fun findBufferAddressJDK1(): Long {
        try {
          return UNSAFE.objectFieldOffset(getDeclaredField(Buffer::class.java, "address")) //$NON-NLS-1$
        } catch (e: Exception) {
          /* Still try with shared library via address offset testing */
          return findBufferAddressJDK9(e)
        }

      }

      private fun findBufferAddressJDK9(e: Exception?): Long {
        /* Maybe because of JDK9 AwkwardStrongEncapsulation. */
        /* Try detecting the address from a known value. */
        try {
          SharedLibraryLoader.load()
        } catch (e1: IOException) {
          throw UnsupportedOperationException("Failed to load joml shared library", e1)
        }

        val bb = newTestBuffer()
        var magicAddress = -0x112454121524111L
        if (pointerSize == 4)
          magicAddress = magicAddress and 0xFFFFFFFFL
        var offset = 8L
        while (offset <= 32L) { // <- don't expect offset to be too large
          if (UNSAFE.getLong(bb, offset) == magicAddress)
            return offset
          offset += 8L
        }
        throw UnsupportedOperationException("Could not detect ByteBuffer.address offset", e)
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkMatrix4f(): Long {
        var f = Matrix4f::class.java.getDeclaredField("m00")
        val Matrix4f_m00 = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        for (i in 1..15) {
          val c = i.ushr(2)
          val r = i and 3
          f = Matrix4f::class.java.getDeclaredField("m$c$r")
          val offset = UNSAFE.objectFieldOffset(f)
          if (offset != Matrix4f_m00 + (i shl 2))
            throw UnsupportedOperationException("Unexpected Matrix4f element offset")
        }
        return Matrix4f_m00
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkMatrix4x3f(): Long {
        var f = Matrix4x3f::class.java.getDeclaredField("m00")
        val Matrix4x3f_m00 = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        for (i in 1..11) {
          val c = i / 3
          val r = i % 3
          f = Matrix4x3f::class.java.getDeclaredField("m$c$r")
          val offset = UNSAFE.objectFieldOffset(f)
          if (offset != Matrix4x3f_m00 + (i shl 2))
            throw UnsupportedOperationException("Unexpected Matrix4x3f element offset")
        }
        return Matrix4x3f_m00
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkMatrix3f(): Long {
        var f = Matrix3f::class.java.getDeclaredField("m00")
        val Matrix3f_m00 = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        for (i in 1..8) {
          val c = i / 3
          val r = i % 3
          f = Matrix3f::class.java.getDeclaredField("m$c$r")
          val offset = UNSAFE.objectFieldOffset(f)
          if (offset != Matrix3f_m00 + (i shl 2))
            throw UnsupportedOperationException("Unexpected Matrix3f element offset")
        }
        return Matrix3f_m00
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkMatrix3x2f(): Long {
        var f = Matrix3x2f::class.java.getDeclaredField("m00")
        val Matrix3x2f_m00 = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        for (i in 1..5) {
          val c = i / 2
          val r = i % 2
          f = Matrix3x2f::class.java.getDeclaredField("m$c$r")
          val offset = UNSAFE.objectFieldOffset(f)
          if (offset != Matrix3x2f_m00 + (i shl 2))
            throw UnsupportedOperationException("Unexpected Matrix3x2f element offset")
        }
        return Matrix3x2f_m00
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkVector4f(): Long {
        var f = Vector4f::class.java.getDeclaredField("x")
        val Vector4f_x = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        val names = arrayOf("y", "z", "w")
        for (i in 1..3) {
          f = Vector4f::class.java.getDeclaredField(names[i - 1])
          val offset = UNSAFE.objectFieldOffset(f)
          if (offset != Vector4f_x + (i shl 2))
            throw UnsupportedOperationException("Unexpected Vector4f element offset")
        }
        return Vector4f_x
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkVector4d(): Long {
        var f = Vector4d::class.java.getDeclaredField("x")
        val Vector4d_x = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        val names = arrayOf("y", "z", "w")
        for (i in 1..3) {
          f = Vector4d::class.java.getDeclaredField(names[i - 1])
          val offset = UNSAFE.objectFieldOffset(f)
          if (offset != Vector4d_x + (i shl 3))
            throw UnsupportedOperationException("Unexpected Vector4d element offset")
        }
        return Vector4d_x
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkVector4i(): Long {
        var f = Vector4i::class.java.getDeclaredField("x")
        val Vector4i_x = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        val names = arrayOf("y", "z", "w")
        for (i in 1..3) {
          f = Vector4i::class.java.getDeclaredField(names[i - 1])
          val offset = UNSAFE.objectFieldOffset(f)
          if (offset != Vector4i_x + (i shl 2))
            throw UnsupportedOperationException("Unexpected Vector4i element offset")
        }
        return Vector4i_x
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkVector3f(): Long {
        var f = Vector3m::class.java.getDeclaredField("x")
        val Vector3f_x = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        val names = arrayOf("y", "z")
        for (i in 1..2) {
          f = Vector3m::class.java.getDeclaredField(names[i - 1])
          val offset = UNSAFE.objectFieldOffset(f)
          if (offset != Vector3f_x + (i shl 2))
            throw UnsupportedOperationException("Unexpected Vector3m element offset")
        }
        return Vector3f_x
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkVector3d(): Long {
        var f = Vector3d::class.java.getDeclaredField("x")
        val Vector3d_x = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        val names = arrayOf("y", "z")
        for (i in 1..2) {
          f = Vector3d::class.java.getDeclaredField(names[i - 1])
          val offset = UNSAFE.objectFieldOffset(f)
          if (offset != Vector3d_x + (i shl 3))
            throw UnsupportedOperationException("Unexpected Vector3d element offset")
        }
        return Vector3d_x
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkVector3i(): Long {
        var f = Vector3i::class.java.getDeclaredField("x")
        val Vector3i_x = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        val names = arrayOf("y", "z")
        for (i in 1..2) {
          f = Vector3i::class.java.getDeclaredField(names[i - 1])
          val offset = UNSAFE.objectFieldOffset(f)
          if (offset != Vector3i_x + (i shl 2))
            throw UnsupportedOperationException("Unexpected Vector3i element offset")
        }
        return Vector3i_x
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkVector2f(): Long {
        var f = Vector2f::class.java.getDeclaredField("x")
        val Vector2f_x = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        f = Vector2f::class.java.getDeclaredField("y")
        val offset = UNSAFE.objectFieldOffset(f)
        if (offset != Vector2f_x + (1 shl 2))
          throw UnsupportedOperationException("Unexpected Vector2f element offset")
        return Vector2f_x
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkVector2d(): Long {
        var f = Vector2d::class.java.getDeclaredField("x")
        val Vector2d_x = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        f = Vector2d::class.java.getDeclaredField("y")
        val offset = UNSAFE.objectFieldOffset(f)
        if (offset != Vector2d_x + (1 shl 3))
          throw UnsupportedOperationException("Unexpected Vector2d element offset")
        return Vector2d_x
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkVector2i(): Long {
        var f = Vector2i::class.java.getDeclaredField("x")
        val Vector2i_x = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        f = Vector2i::class.java.getDeclaredField("y")
        val offset = UNSAFE.objectFieldOffset(f)
        if (offset != Vector2i_x + (1 shl 2))
          throw UnsupportedOperationException("Unexpected Vector2i element offset")
        return Vector2i_x
      }

      @Throws(NoSuchFieldException::class, SecurityException::class)
      private fun checkQuaternionf(): Long {
        var f = Quaternionf::class.java.getDeclaredField("x")
        val Quaternionf_x = UNSAFE.objectFieldOffset(f)
        // Validate expected field offsets
        val names = arrayOf("y", "z", "w")
        for (i in 1..3) {
          f = Quaternionf::class.java.getDeclaredField(names[i - 1])
          val offset = UNSAFE.objectFieldOffset(f)
          if (offset != Quaternionf_x + (i shl 2))
            throw UnsupportedOperationException("Unexpected Quaternionf element offset")
        }
        return Quaternionf_x
      }

      @Throws(NoSuchFieldException::class)
      private fun getDeclaredField(root: Class<*>, fieldName: String): Field {
        var type: Class<*>? = root
        do {
          try {
            val field = type!!.getDeclaredField(fieldName)
            field.isAccessible = true
            return field
          } catch (e: NoSuchFieldException) {
            type = type!!.superclass
          } catch (e: SecurityException) {
            type = type!!.superclass
          }

        } while (type != null)
        throw NoSuchFieldException(fieldName + " does not exist in " + root.name + " or any of its superclasses.") //$NON-NLS-1$ //$NON-NLS-2$
      }

      /* Ignore */ val unsafeInstance: sun.misc.Unsafe
        @Throws(SecurityException::class)
        get() {
          val fields = sun.misc.Unsafe::class.java.declaredFields
          for (i in fields.indices) {
            val field = fields[i]
            if (field.type != sun.misc.Unsafe::class.java)
              continue
            val modifiers = field.modifiers
            if (!(java.lang.reflect.Modifier.isStatic(modifiers) && java.lang.reflect.Modifier.isFinal(modifiers)))
              continue
            field.isAccessible = true
            try {
              return field.get(null) as sun.misc.Unsafe
            } catch (e: IllegalAccessException) {
            }

            break
          }
          throw UnsupportedOperationException()
        }

      fun putLong(obj: Any?, off: Long, `val`: Long) {
        UNSAFE.putLong(obj, off, `val`)
      }

      private fun throwNoDirectBufferException() {
        throw IllegalArgumentException("Must use a direct buffer")
      }
    }
  }

  companion object {
    val INSTANCE = createInstance()
    private fun createInstance(): MemUtil {
      var accessor: MemUtil
      try {
        if (Options.NO_UNSAFE)
          accessor = MemUtilNIO()
        else
          accessor = MemUtilUnsafe()
      } catch (e: Throwable) {
        accessor = MemUtilNIO()
      }

      return accessor
    }
  }
}
