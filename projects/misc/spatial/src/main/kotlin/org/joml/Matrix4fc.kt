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
 * Interface to a read-only view of a 4x4 matrix of single-precision floats.
 *
 * @author Kai Burjack
 */
 interface Matrix4fc {

/**
 * Determine whether this matrix describes an affine transformation. This is the case iff its last row is equal to <tt>(0, 0, 0, 1)</tt>.
 *
 * @return `true` iff this matrix is affine; `false` otherwise
 */
     val isAffine:Boolean

/**
 * Return the assumed properties of this matrix. This is a bit-combination of
 * [.PROPERTY_IDENTITY], [.PROPERTY_AFFINE],
 * [.PROPERTY_TRANSLATION] and [.PROPERTY_PERSPECTIVE].
 *
 * @return the properties of the matrix
 */
     fun properties():Int

/**
 * Return the value of the matrix element at column 0 and row 0.
 *
 * @return the value of the matrix element
 */
     fun m00():Float

/**
 * Return the value of the matrix element at column 0 and row 1.
 *
 * @return the value of the matrix element
 */
     fun m01():Float

/**
 * Return the value of the matrix element at column 0 and row 2.
 *
 * @return the value of the matrix element
 */
     fun m02():Float

/**
 * Return the value of the matrix element at column 0 and row 3.
 *
 * @return the value of the matrix element
 */
     fun m03():Float

/**
 * Return the value of the matrix element at column 1 and row 0.
 *
 * @return the value of the matrix element
 */
     fun m10():Float

/**
 * Return the value of the matrix element at column 1 and row 1.
 *
 * @return the value of the matrix element
 */
     fun m11():Float

/**
 * Return the value of the matrix element at column 1 and row 2.
 *
 * @return the value of the matrix element
 */
     fun m12():Float

/**
 * Return the value of the matrix element at column 1 and row 3.
 *
 * @return the value of the matrix element
 */
     fun m13():Float

/**
 * Return the value of the matrix element at column 2 and row 0.
 *
 * @return the value of the matrix element
 */
     fun m20():Float

/**
 * Return the value of the matrix element at column 2 and row 1.
 *
 * @return the value of the matrix element
 */
     fun m21():Float

/**
 * Return the value of the matrix element at column 2 and row 2.
 *
 * @return the value of the matrix element
 */
     fun m22():Float

/**
 * Return the value of the matrix element at column 2 and row 3.
 *
 * @return the value of the matrix element
 */
     fun m23():Float

/**
 * Return the value of the matrix element at column 3 and row 0.
 *
 * @return the value of the matrix element
 */
     fun m30():Float

/**
 * Return the value of the matrix element at column 3 and row 1.
 *
 * @return the value of the matrix element
 */
     fun m31():Float

/**
 * Return the value of the matrix element at column 3 and row 2.
 *
 * @return the value of the matrix element
 */
     fun m32():Float

/**
 * Return the value of the matrix element at column 3 and row 3.
 *
 * @return the value of the matrix element
 */
     fun m33():Float

/**
 * Multiply this matrix by the supplied `right` matrix and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `R` the `right` matrix,
 * then the new matrix will be `M * R`. So when transforming a
 * vector `v` with the new matrix by using `M * R * v`, the
 * transformation of the right matrix will be applied first!
 *
 * @param right
 * the right operand of the matrix multiplication
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun mul(right:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Pre-multiply this matrix by the supplied `left` matrix and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `L` the `left` matrix,
 * then the new matrix will be `L * M`. So when transforming a
 * vector `v` with the new matrix by using `L * M * v`, the
 * transformation of `this` matrix will be applied first!
 *
 * @param left
 * the left operand of the matrix multiplication
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun mulLocal(left:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Pre-multiply this matrix by the supplied `left` matrix, both of which are assumed to be [affine][.isAffine], and store the result in `dest`.
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
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun mulLocalAffine(left:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Multiply this matrix by the supplied `right` matrix and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `R` the `right` matrix,
 * then the new matrix will be `M * R`. So when transforming a
 * vector `v` with the new matrix by using `M * R * v`, the
 * transformation of the right matrix will be applied first!
 *
 * @param right
 * the right operand of the matrix multiplication
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun mul(right:Matrix3x2fc, dest:Matrix4f):Matrix4f

/**
 * Multiply this matrix by the supplied `right` matrix and store the result in `dest`.
 *
 *
 * The last row of the `right` matrix is assumed to be <tt>(0, 0, 0, 1)</tt>.
 *
 *
 * If `M` is `this` matrix and `R` the `right` matrix,
 * then the new matrix will be `M * R`. So when transforming a
 * vector `v` with the new matrix by using `M * R * v`, the
 * transformation of the right matrix will be applied first!
 *
 * @param right
 * the right operand of the matrix multiplication
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun mul(right:Matrix4x3fc, dest:Matrix4f):Matrix4f

/**
 * Multiply `this` symmetric perspective projection matrix by the supplied [affine][.isAffine] `view` matrix and store the result in `dest`.
 *
 *
 * If `P` is `this` matrix and `V` the `view` matrix,
 * then the new matrix will be `P * V`. So when transforming a
 * vector `v` with the new matrix by using `P * V * v`, the
 * transformation of the `view` matrix will be applied first!
 *
 * @param view
 * the [affine][.isAffine] matrix to multiply `this` symmetric perspective projection matrix by
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun mulPerspectiveAffine(view:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Multiply `this` symmetric perspective projection matrix by the supplied `view` matrix and store the result in `dest`.
 *
 *
 * If `P` is `this` matrix and `V` the `view` matrix,
 * then the new matrix will be `P * V`. So when transforming a
 * vector `v` with the new matrix by using `P * V * v`, the
 * transformation of the `view` matrix will be applied first!
 *
 * @param view
 * the matrix to multiply `this` symmetric perspective projection matrix by
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun mulPerspectiveAffine(view:Matrix4x3fc, dest:Matrix4f):Matrix4f

/**
 * Multiply this matrix by the supplied `right` matrix, which is assumed to be [affine][.isAffine], and store the result in `dest`.
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
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun mulAffineR(right:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Multiply this matrix by the supplied `right` matrix, both of which are assumed to be [affine][.isAffine], and store the result in `dest`.
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
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun mulAffine(right:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Multiply this matrix, which is assumed to only contain a translation, by the supplied `right` matrix, which is assumed to be [affine][.isAffine], and store the result in `dest`.
 *
 *
 * This method assumes that `this` matrix only contains a translation, and that the given `right` matrix represents an [affine][.isAffine] transformation
 * (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>).
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
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun mulTranslationAffine(right:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Multiply `this` orthographic projection matrix by the supplied [affine][.isAffine] `view` matrix
 * and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `V` the `view` matrix,
 * then the new matrix will be `M * V`. So when transforming a
 * vector `v` with the new matrix by using `M * V * v`, the
 * transformation of the `view` matrix will be applied first!
 *
 * @param view
 * the affine matrix which to multiply `this` with
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun mulOrthoAffine(view:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Component-wise add the upper 4x3 submatrices of `this` and `other`
 * by first multiplying each component of `other`'s 4x3 submatrix by `otherFactor`,
 * adding that to `this` and storing the final result in `dest`.
 *
 *
 * The other components of `dest` will be set to the ones of `this`.
 *
 *
 * The matrices `this` and `other` will not be changed.
 *
 * @param other
 * the other matrix
 * @param otherFactor
 * the factor to multiply each of the other matrix's 4x3 components
 * @param dest
 * will hold the result
 * @return dest
 */
     fun fma4x3(other:Matrix4fc, otherFactor:Float, dest:Matrix4f):Matrix4f

/**
 * Component-wise add `this` and `other` and store the result in `dest`.
 *
 * @param other
 * the other addend
 * @param dest
 * will hold the result
 * @return dest
 */
     fun add(other:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Component-wise subtract `subtrahend` from `this` and store the result in `dest`.
 *
 * @param subtrahend
 * the subtrahend
 * @param dest
 * will hold the result
 * @return dest
 */
     fun sub(subtrahend:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Component-wise multiply `this` by `other` and store the result in `dest`.
 *
 * @param other
 * the other matrix
 * @param dest
 * will hold the result
 * @return dest
 */
     fun mulComponentWise(other:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Component-wise add the upper 4x3 submatrices of `this` and `other`
 * and store the result in `dest`.
 *
 *
 * The other components of `dest` will be set to the ones of `this`.
 *
 * @param other
 * the other addend
 * @param dest
 * will hold the result
 * @return dest
 */
     fun add4x3(other:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Component-wise subtract the upper 4x3 submatrices of `subtrahend` from `this`
 * and store the result in `dest`.
 *
 *
 * The other components of `dest` will be set to the ones of `this`.
 *
 * @param subtrahend
 * the subtrahend
 * @param dest
 * will hold the result
 * @return dest
 */
     fun sub4x3(subtrahend:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Component-wise multiply the upper 4x3 submatrices of `this` by `other`
 * and store the result in `dest`.
 *
 *
 * The other components of `dest` will be set to the ones of `this`.
 *
 * @param other
 * the other matrix
 * @param dest
 * will hold the result
 * @return dest
 */
     fun mul4x3ComponentWise(other:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Return the determinant of this matrix.
 *
 *
 * If `this` matrix represents an [affine][.isAffine] transformation, such as translation, rotation, scaling and shearing,
 * and thus its last row is equal to <tt>(0, 0, 0, 1)</tt>, then [.determinantAffine] can be used instead of this method.
 *
 * @see .determinantAffine
 * @return the determinant
 */
     fun determinant():Float

/**
 * Return the determinant of the upper left 3x3 submatrix of this matrix.
 *
 * @return the determinant
 */
     fun determinant3x3():Float

/**
 * Return the determinant of this matrix by assuming that it represents an [affine][.isAffine] transformation and thus
 * its last row is equal to <tt>(0, 0, 0, 1)</tt>.
 *
 * @return the determinant
 */
     fun determinantAffine():Float

/**
 * Invert this matrix and write the result into `dest`.
 *
 *
 * If `this` matrix represents an [affine][.isAffine] transformation, such as translation, rotation, scaling and shearing,
 * and thus its last row is equal to <tt>(0, 0, 0, 1)</tt>, then [.invertAffine] can be used instead of this method.
 *
 * @see .invertAffine
 * @param dest
 * will hold the result
 * @return dest
 */
     fun invert(dest:Matrix4f):Matrix4f

/**
 * If `this` is a perspective projection matrix obtained via one of the [perspective()][.perspective] methods,
 * that is, if `this` is a symmetrical perspective frustum transformation,
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
     fun invertPerspective(dest:Matrix4f):Matrix4f

/**
 * If `this` is an arbitrary perspective projection matrix obtained via one of the [frustum()][.frustum] methods,
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
     fun invertFrustum(dest:Matrix4f):Matrix4f

/**
 * Invert `this` orthographic projection matrix and store the result into the given `dest`.
 *
 *
 * This method can be used to quickly obtain the inverse of an orthographic projection matrix.
 *
 * @param dest
 * will hold the inverse of `this`
 * @return dest
 */
     fun invertOrtho(dest:Matrix4f):Matrix4f

/**
 * If `this` is a perspective projection matrix obtained via one of the [perspective()][.perspective] methods,
 * that is, if `this` is a symmetrical perspective frustum transformation
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
     fun invertPerspectiveView(view:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * If `this` is a perspective projection matrix obtained via one of the [perspective()][.perspective] methods,
 * that is, if `this` is a symmetrical perspective frustum transformation
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
     fun invertPerspectiveView(view:Matrix4x3fc, dest:Matrix4f):Matrix4f

/**
 * Invert this matrix by assuming that it is an [affine][.isAffine] transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
 * and write the result into `dest`.
 *
 * @param dest
 * will hold the result
 * @return dest
 */
     fun invertAffine(dest:Matrix4f):Matrix4f

/**
 * Transpose this matrix and store the result in `dest`.
 *
 * @param dest
 * will hold the result
 * @return dest
 */
     fun transpose(dest:Matrix4f):Matrix4f

/**
 * Transpose only the upper left 3x3 submatrix of this matrix and store the result in `dest`.
 *
 *
 * All other matrix elements are left unchanged.
 *
 * @param dest
 * will hold the result
 * @return dest
 */
     fun transpose3x3(dest:Matrix4f):Matrix4f

/**
 * Transpose only the upper left 3x3 submatrix of this matrix and store the result in `dest`.
 *
 * @param dest
 * will hold the result
 * @return dest
 */
     fun transpose3x3(dest:Matrix3f):Matrix3f

/**
 * Get only the translation components <tt>(m30, m31, m32)</tt> of this matrix and store them in the given vector `xyz`.
 *
 * @param dest
 * will hold the translation components of this matrix
 * @return dest
 */
     fun getTranslation(dest: Vector3m): Vector3m

/**
 * Get the scaling factors of `this` matrix for the three base axes.
 *
 * @param dest
 * will hold the scaling factors for <tt>x</tt>, <tt>y</tt> and <tt>z</tt>
 * @return dest
 */
     fun getScale(dest: Vector3m): Vector3m

/**
 * Get the current values of `this` matrix and store them into
 * `dest`.
 *
 * @param dest
 * the destination matrix
 * @return the passed in destination
 */
     operator fun get(dest:Matrix4f):Matrix4f

/**
 * Get the current values of the upper 4x3 submatrix of `this` matrix and store them into
 * `dest`.
 *
 * @see Matrix4x3f.set
 * @param dest
 * the destination matrix
 * @return the passed in destination
 */
     fun get4x3(dest:Matrix4x3f):Matrix4x3f

/**
 * Get the current values of `this` matrix and store them into
 * `dest`.
 *
 * @param dest
 * the destination matrix
 * @return the passed in destination
 */
     operator fun get(dest:Matrix4d):Matrix4d

/**
 * Get the current values of the upper left 3x3 submatrix of `this` matrix and store them into
 * `dest`.
 *
 * @see Matrix3f.set
 * @param dest
 * the destination matrix
 * @return the passed in destination
 */
     fun get3x3(dest:Matrix3f):Matrix3f

/**
 * Get the current values of the upper left 3x3 submatrix of `this` matrix and store them into
 * `dest`.
 *
 * @see Matrix3d.set
 * @param dest
 * the destination matrix
 * @return the passed in destination
 */
     fun get3x3(dest:Matrix3d):Matrix3d

/**
 * Get the rotational component of `this` matrix and store the represented rotation
 * into the given [AxisAngle4f].
 *
 * @see AxisAngle4f.set
 * @param dest
 * the destination [AxisAngle4f]
 * @return the passed in destination
 */
     fun getRotation(dest:AxisAngle4f):AxisAngle4f

/**
 * Get the rotational component of `this` matrix and store the represented rotation
 * into the given [AxisAngle4d].
 *
 * @see AxisAngle4f.set
 * @param dest
 * the destination [AxisAngle4d]
 * @return the passed in destination
 */
     fun getRotation(dest:AxisAngle4d):AxisAngle4d

/**
 * Get the current values of `this` matrix and store the represented rotation
 * into the given [Quaternionf].
 *
 *
 * This method assumes that the first three column vectors of the upper left 3x3 submatrix are not normalized and
 * thus allows to ignore any additional scaling factor that is applied to the matrix.
 *
 * @see Quaternionf.setFromUnnormalized
 * @param dest
 * the destination [Quaternionf]
 * @return the passed in destination
 */
     fun getUnnormalizedRotation(dest:Quaternionf):Quaternionf

/**
 * Get the current values of `this` matrix and store the represented rotation
 * into the given [Quaternionf].
 *
 *
 * This method assumes that the first three column vectors of the upper left 3x3 submatrix are normalized.
 *
 * @see Quaternionf.setFromNormalized
 * @param dest
 * the destination [Quaternionf]
 * @return the passed in destination
 */
     fun getNormalizedRotation(dest:Quaternionf):Quaternionf

/**
 * Get the current values of `this` matrix and store the represented rotation
 * into the given [Quaterniond].
 *
 *
 * This method assumes that the first three column vectors of the upper left 3x3 submatrix are not normalized and
 * thus allows to ignore any additional scaling factor that is applied to the matrix.
 *
 * @see Quaterniond.setFromUnnormalized
 * @param dest
 * the destination [Quaterniond]
 * @return the passed in destination
 */
     fun getUnnormalizedRotation(dest:Quaterniond):Quaterniond

/**
 * Get the current values of `this` matrix and store the represented rotation
 * into the given [Quaterniond].
 *
 *
 * This method assumes that the first three column vectors of the upper left 3x3 submatrix are normalized.
 *
 * @see Quaterniond.setFromNormalized
 * @param dest
 * the destination [Quaterniond]
 * @return the passed in destination
 */
     fun getNormalizedRotation(dest:Quaterniond):Quaterniond


/**
 * Store this matrix in column-major order into the supplied [FloatBuffer] at the current
 * buffer [position][FloatBuffer.position].
 *
 *
 * This method will not increment the position of the given FloatBuffer.
 *
 *
 * In order to specify the offset into the FloatBuffer at which
 * the matrix is stored, use [.get], taking
 * the absolute position as parameter.
 *
 * @see .get
 * @param buffer
 * will receive the values of this matrix in column-major order at its current position
 * @return the passed in buffer
 */
     operator fun get(buffer:FloatBuffer):FloatBuffer

/**
 * Store this matrix in column-major order into the supplied [FloatBuffer] starting at the specified
 * absolute buffer position/index.
 *
 *
 * This method will not increment the position of the given FloatBuffer.
 *
 * @param index
 * the absolute position into the FloatBuffer
 * @param buffer
 * will receive the values of this matrix in column-major order
 * @return the passed in buffer
 */
     operator fun get(index:Int, buffer:FloatBuffer):FloatBuffer

/**
 * Store this matrix in column-major order into the supplied [ByteBuffer] at the current
 * buffer [position][ByteBuffer.position].
 *
 *
 * This method will not increment the position of the given ByteBuffer.
 *
 *
 * In order to specify the offset into the ByteBuffer at which
 * the matrix is stored, use [.get], taking
 * the absolute position as parameter.
 *
 * @see .get
 * @param buffer
 * will receive the values of this matrix in column-major order at its current position
 * @return the passed in buffer
 */
     operator fun get(buffer:ByteBuffer):ByteBuffer

/**
 * Store this matrix in column-major order into the supplied [ByteBuffer] starting at the specified
 * absolute buffer position/index.
 *
 *
 * This method will not increment the position of the given ByteBuffer.
 *
 * @param index
 * the absolute position into the ByteBuffer
 * @param buffer
 * will receive the values of this matrix in column-major order
 * @return the passed in buffer
 */
     operator fun get(index:Int, buffer:ByteBuffer):ByteBuffer

/**
 * Store the transpose of this matrix in column-major order into the supplied [FloatBuffer] at the current
 * buffer [position][FloatBuffer.position].
 *
 *
 * This method will not increment the position of the given FloatBuffer.
 *
 *
 * In order to specify the offset into the FloatBuffer at which
 * the matrix is stored, use [.getTransposed], taking
 * the absolute position as parameter.
 *
 * @see .getTransposed
 * @param buffer
 * will receive the values of this matrix in column-major order at its current position
 * @return the passed in buffer
 */
     fun getTransposed(buffer:FloatBuffer):FloatBuffer

/**
 * Store the transpose of this matrix in column-major order into the supplied [FloatBuffer] starting at the specified
 * absolute buffer position/index.
 *
 *
 * This method will not increment the position of the given FloatBuffer.
 *
 * @param index
 * the absolute position into the FloatBuffer
 * @param buffer
 * will receive the values of this matrix in column-major order
 * @return the passed in buffer
 */
     fun getTransposed(index:Int, buffer:FloatBuffer):FloatBuffer

/**
 * Store the transpose of this matrix in column-major order into the supplied [ByteBuffer] at the current
 * buffer [position][ByteBuffer.position].
 *
 *
 * This method will not increment the position of the given ByteBuffer.
 *
 *
 * In order to specify the offset into the ByteBuffer at which
 * the matrix is stored, use [.getTransposed], taking
 * the absolute position as parameter.
 *
 * @see .getTransposed
 * @param buffer
 * will receive the values of this matrix in column-major order at its current position
 * @return the passed in buffer
 */
     fun getTransposed(buffer:ByteBuffer):ByteBuffer

/**
 * Store the transpose of this matrix in column-major order into the supplied [ByteBuffer] starting at the specified
 * absolute buffer position/index.
 *
 *
 * This method will not increment the position of the given ByteBuffer.
 *
 * @param index
 * the absolute position into the ByteBuffer
 * @param buffer
 * will receive the values of this matrix in column-major order
 * @return the passed in buffer
 */
     fun getTransposed(index:Int, buffer:ByteBuffer):ByteBuffer

/**
 * Store the upper 4x3 submatrix of `this` matrix in row-major order into the supplied [FloatBuffer] at the current
 * buffer [position][FloatBuffer.position].
 *
 *
 * This method will not increment the position of the given FloatBuffer.
 *
 *
 * In order to specify the offset into the FloatBuffer at which
 * the matrix is stored, use [.get4x3Transposed], taking
 * the absolute position as parameter.
 *
 * @see .get4x3Transposed
 * @param buffer
 * will receive the values of the upper 4x3 submatrix in row-major order at its current position
 * @return the passed in buffer
 */
     fun get4x3Transposed(buffer:FloatBuffer):FloatBuffer

/**
 * Store the upper 4x3 submatrix of `this` matrix in row-major order into the supplied [FloatBuffer] starting at the specified
 * absolute buffer position/index.
 *
 *
 * This method will not increment the position of the given FloatBuffer.
 *
 * @param index
 * the absolute position into the FloatBuffer
 * @param buffer
 * will receive the values of the upper 4x3 submatrix in row-major order
 * @return the passed in buffer
 */
     fun get4x3Transposed(index:Int, buffer:FloatBuffer):FloatBuffer

/**
 * Store the upper 4x3 submatrix of `this` matrix in row-major order into the supplied [ByteBuffer] at the current
 * buffer [position][ByteBuffer.position].
 *
 *
 * This method will not increment the position of the given ByteBuffer.
 *
 *
 * In order to specify the offset into the ByteBuffer at which
 * the matrix is stored, use [.get4x3Transposed], taking
 * the absolute position as parameter.
 *
 * @see .get4x3Transposed
 * @param buffer
 * will receive the values of the upper 4x3 submatrix in row-major order at its current position
 * @return the passed in buffer
 */
     fun get4x3Transposed(buffer:ByteBuffer):ByteBuffer

/**
 * Store the upper 4x3 submatrix of `this` matrix in row-major order into the supplied [ByteBuffer] starting at the specified
 * absolute buffer position/index.
 *
 *
 * This method will not increment the position of the given ByteBuffer.
 *
 * @param index
 * the absolute position into the ByteBuffer
 * @param buffer
 * will receive the values of the upper 4x3 submatrix in row-major order
 * @return the passed in buffer
 */
     fun get4x3Transposed(index:Int, buffer:ByteBuffer):ByteBuffer

/**
 * Store this matrix in column-major order at the given off-heap address.
 *
 *
 * This method will throw an [UnsupportedOperationException] when JOML is used with `-Djoml.nounsafe`.
 *
 *
 * *This method is unsafe as it can result in a crash of the JVM process when the specified address range does not belong to this process.*
 *
 * @param address
 * the off-heap address where to store this matrix
 * @return this
 */
     fun getToAddress(address:Long):Matrix4fc

/**
 * Store this matrix into the supplied float array in column-major order at the given offset.
 *
 * @param arr
 * the array to write the matrix values into
 * @param offset
 * the offset into the array
 * @return the passed in array
 */
     operator fun get(arr:FloatArray, offset:Int):FloatArray

/**
 * Store this matrix into the supplied float array in column-major order.
 *
 *
 * In order to specify an explicit offset into the array, use the method [.get].
 *
 * @see .get
 * @param arr
 * the array to write the matrix values into
 * @return the passed in array
 */
     operator fun get(arr:FloatArray):FloatArray

/**
 * Transform/multiply the given vector by this matrix and store the result in that vector.
 *
 * @see Vector4f.mul
 * @param v
 * the vector to transform and to hold the final result
 * @return v
 */
     fun transform(v:Vector4f):Vector4f

/**
 * Transform/multiply the given vector by this matrix and store the result in `dest`.
 *
 * @see Vector4f.mul
 * @param v
 * the vector to transform
 * @param dest
 * will contain the result
 * @return dest
 */
     fun transform(v:Vector4fc, dest:Vector4f):Vector4f

/**
 * Transform/multiply the vector <tt>(x, y, z, w)</tt> by this matrix and store the result in `dest`.
 *
 * @param x
 * the x coordinate of the vector to transform
 * @param y
 * the y coordinate of the vector to transform
 * @param z
 * the z coordinate of the vector to transform
 * @param w
 * the w coordinate of the vector to transform
 * @param dest
 * will contain the result
 * @return dest
 */
     fun transform(x:Float, y:Float, z:Float, w:Float, dest:Vector4f):Vector4f

/**
 * Transform/multiply the given vector by this matrix, perform perspective divide and store the result in that vector.
 *
 * @see Vector4f.mulProject
 * @param v
 * the vector to transform and to hold the final result
 * @return v
 */
     fun transformProject(v:Vector4f):Vector4f

/**
 * Transform/multiply the given vector by this matrix, perform perspective divide and store the result in `dest`.
 *
 * @see Vector4f.mulProject
 * @param v
 * the vector to transform
 * @param dest
 * will contain the result
 * @return dest
 */
     fun transformProject(v:Vector4fc, dest:Vector4f):Vector4f

/**
 * Transform/multiply the vector <tt>(x, y, z, w)</tt> by this matrix, perform perspective divide and store the result in `dest`.
 *
 * @param x
 * the x coordinate of the vector to transform
 * @param y
 * the y coordinate of the vector to transform
 * @param z
 * the z coordinate of the vector to transform
 * @param w
 * the w coordinate of the vector to transform
 * @param dest
 * will contain the result
 * @return dest
 */
     fun transformProject(x:Float, y:Float, z:Float, w:Float, dest:Vector4f):Vector4f

/**
 * Transform/multiply the given vector by this matrix, perform perspective divide and store the result in that vector.
 *
 *
 * This method uses <tt>w=1.0</tt> as the fourth vector component.
 *
 * @see Vector3m.mulProject
 * @param v
 * the vector to transform and to hold the final result
 * @return v
 */
     fun transformProject(v: Vector3m): Vector3m

/**
 * Transform/multiply the given vector by this matrix, perform perspective divide and store the result in `dest`.
 *
 *
 * This method uses <tt>w=1.0</tt> as the fourth vector component.
 *
 * @see Vector3m.mulProject
 * @param v
 * the vector to transform
 * @param dest
 * will contain the result
 * @return dest
 */
     fun transformProject(v:Vector3fc, dest: Vector3m): Vector3m

/**
 * Transform/multiply the vector <tt>(x, y, z)</tt> by this matrix, perform perspective divide and store the result in `dest`.
 *
 *
 * This method uses <tt>w=1.0</tt> as the fourth vector component.
 *
 * @param x
 * the x coordinate of the vector to transform
 * @param y
 * the y coordinate of the vector to transform
 * @param z
 * the z coordinate of the vector to transform
 * @param dest
 * will contain the result
 * @return dest
 */
     fun transformProject(x:Float, y:Float, z:Float, dest: Vector3m): Vector3m

/**
 * Transform/multiply the given 3D-vector, as if it was a 4D-vector with w=1, by
 * this matrix and store the result in that vector.
 *
 *
 * The given 3D-vector is treated as a 4D-vector with its w-component being 1.0, so it
 * will represent a position/location in 3D-space rather than a direction. This method is therefore
 * not suited for perspective projection transformations as it will not save the
 * <tt>w</tt> component of the transformed vector.
 * For perspective projection use [.transform] or [.transformProject]
 * when perspective divide should be applied, too.
 *
 *
 * In order to store the result in another vector, use [.transformPosition].
 *
 * @see .transformPosition
 * @see .transform
 * @see .transformProject
 * @param v
 * the vector to transform and to hold the final result
 * @return v
 */
     fun transformPosition(v: Vector3m): Vector3m

/**
 * Transform/multiply the given 3D-vector, as if it was a 4D-vector with w=1, by
 * this matrix and store the result in `dest`.
 *
 *
 * The given 3D-vector is treated as a 4D-vector with its w-component being 1.0, so it
 * will represent a position/location in 3D-space rather than a direction. This method is therefore
 * not suited for perspective projection transformations as it will not save the
 * <tt>w</tt> component of the transformed vector.
 * For perspective projection use [.transform] or
 * [.transformProject] when perspective divide should be applied, too.
 *
 *
 * In order to store the result in the same vector, use [.transformPosition].
 *
 * @see .transformPosition
 * @see .transform
 * @see .transformProject
 * @param v
 * the vector to transform
 * @param dest
 * will hold the result
 * @return dest
 */
     fun transformPosition(v:Vector3fc, dest: Vector3m): Vector3m

/**
 * Transform/multiply the 3D-vector <tt>(x, y, z)</tt>, as if it was a 4D-vector with w=1, by
 * this matrix and store the result in `dest`.
 *
 *
 * The given 3D-vector is treated as a 4D-vector with its w-component being 1.0, so it
 * will represent a position/location in 3D-space rather than a direction. This method is therefore
 * not suited for perspective projection transformations as it will not save the
 * <tt>w</tt> component of the transformed vector.
 * For perspective projection use [.transform] or
 * [.transformProject] when perspective divide should be applied, too.
 *
 * @see .transform
 * @see .transformProject
 * @param x
 * the x coordinate of the position
 * @param y
 * the y coordinate of the position
 * @param z
 * the z coordinate of the position
 * @param dest
 * will hold the result
 * @return dest
 */
     fun transformPosition(x:Float, y:Float, z:Float, dest: Vector3m): Vector3m

/**
 * Transform/multiply the given 3D-vector, as if it was a 4D-vector with w=0, by
 * this matrix and store the result in that vector.
 *
 *
 * The given 3D-vector is treated as a 4D-vector with its w-component being <tt>0.0</tt>, so it
 * will represent a direction in 3D-space rather than a position. This method will therefore
 * not take the translation part of the matrix into account.
 *
 *
 * In order to store the result in another vector, use [.transformDirection].
 *
 * @see .transformDirection
 * @param v
 * the vector to transform and to hold the final result
 * @return v
 */
     fun transformDirection(v: Vector3m): Vector3m

/**
 * Transform/multiply the given 3D-vector, as if it was a 4D-vector with w=0, by
 * this matrix and store the result in `dest`.
 *
 *
 * The given 3D-vector is treated as a 4D-vector with its w-component being <tt>0.0</tt>, so it
 * will represent a direction in 3D-space rather than a position. This method will therefore
 * not take the translation part of the matrix into account.
 *
 *
 * In order to store the result in the same vector, use [.transformDirection].
 *
 * @see .transformDirection
 * @param v
 * the vector to transform and to hold the final result
 * @param dest
 * will hold the result
 * @return dest
 */
     fun transformDirection(v:Vector3fc, dest: Vector3m): Vector3m

/**
 * Transform/multiply the given 3D-vector <tt>(x, y, z)</tt>, as if it was a 4D-vector with w=0, by
 * this matrix and store the result in `dest`.
 *
 *
 * The given 3D-vector is treated as a 4D-vector with its w-component being <tt>0.0</tt>, so it
 * will represent a direction in 3D-space rather than a position. This method will therefore
 * not take the translation part of the matrix into account.
 *
 * @param x
 * the x coordinate of the direction to transform
 * @param y
 * the y coordinate of the direction to transform
 * @param z
 * the z coordinate of the direction to transform
 * @param dest
 * will hold the result
 * @return dest
 */
     fun transformDirection(x:Float, y:Float, z:Float, dest: Vector3m): Vector3m

/**
 * Transform/multiply the given 4D-vector by assuming that `this` matrix represents an [affine][.isAffine] transformation
 * (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>).
 *
 *
 * In order to store the result in another vector, use [.transformAffine].
 *
 * @see .transformAffine
 * @param v
 * the vector to transform and to hold the final result
 * @return v
 */
     fun transformAffine(v:Vector4f):Vector4f

/**
 * Transform/multiply the given 4D-vector by assuming that `this` matrix represents an [affine][.isAffine] transformation
 * (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>) and store the result in `dest`.
 *
 *
 * In order to store the result in the same vector, use [.transformAffine].
 *
 * @see .transformAffine
 * @param v
 * the vector to transform and to hold the final result
 * @param dest
 * will hold the result
 * @return dest
 */
     fun transformAffine(v:Vector4fc, dest:Vector4f):Vector4f

/**
 * Transform/multiply the 4D-vector <tt>(x, y, z, w)</tt> by assuming that `this` matrix represents an [affine][.isAffine] transformation
 * (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>) and store the result in `dest`.
 *
 * @param x
 * the x coordinate of the direction to transform
 * @param y
 * the y coordinate of the direction to transform
 * @param z
 * the z coordinate of the direction to transform
 * @param w
 * the w coordinate of the direction to transform
 * @param dest
 * will hold the result
 * @return dest
 */
     fun transformAffine(x:Float, y:Float, z:Float, w:Float, dest:Vector4f):Vector4f

/**
 * Apply scaling to `this` matrix by scaling the base axes by the given <tt>xyz.x</tt>,
 * <tt>xyz.y</tt> and <tt>xyz.z</tt> factors, respectively and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `S` the scaling matrix,
 * then the new matrix will be `M * S`. So when transforming a
 * vector `v` with the new matrix by using `M * S * v`
 * , the scaling will be applied first!
 *
 * @param xyz
 * the factors of the x, y and z component, respectively
 * @param dest
 * will hold the result
 * @return dest
 */
     fun scale(xyz:Vector3fc, dest:Matrix4f):Matrix4f

/**
 * Apply scaling to this matrix by uniformly scaling all base axes by the given `xyz` factor
 * and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun scale(xyz:Float, dest:Matrix4f):Matrix4f

/**
 * Apply scaling to `this` matrix by scaling the base axes by the given x,
 * y and z factors and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `S` the scaling matrix,
 * then the new matrix will be `M * S`. So when transforming a
 * vector `v` with the new matrix by using `M * S * v`
 * , the scaling will be applied first!
 *
 * @param x
 * the factor of the x component
 * @param y
 * the factor of the y component
 * @param z
 * the factor of the z component
 * @param dest
 * will hold the result
 * @return dest
 */
     fun scale(x:Float, y:Float, z:Float, dest:Matrix4f):Matrix4f

/**
 * Apply scaling to `this` matrix by scaling the base axes by the given sx,
 * sy and sz factors while using <tt>(ox, oy, oz)</tt> as the scaling origin,
 * and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `S` the scaling matrix,
 * then the new matrix will be `M * S`. So when transforming a
 * vector `v` with the new matrix by using `M * S * v`
 * , the scaling will be applied first!
 *
 *
 * This method is equivalent to calling: <tt>translate(ox, oy, oz, dest).scale(sx, sy, sz).translate(-ox, -oy, -oz)</tt>
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun scaleAround(sx:Float, sy:Float, sz:Float, ox:Float, oy:Float, oz:Float, dest:Matrix4f):Matrix4f

/**
 * Apply scaling to this matrix by scaling all three base axes by the given `factor`
 * while using <tt>(ox, oy, oz)</tt> as the scaling origin,
 * and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `S` the scaling matrix,
 * then the new matrix will be `M * S`. So when transforming a
 * vector `v` with the new matrix by using `M * S * v`, the
 * scaling will be applied first!
 *
 *
 * This method is equivalent to calling: <tt>translate(ox, oy, oz, dest).scale(factor).translate(-ox, -oy, -oz)</tt>
 *
 * @param factor
 * the scaling factor for all three axes
 * @param ox
 * the x coordinate of the scaling origin
 * @param oy
 * the y coordinate of the scaling origin
 * @param oz
 * the z coordinate of the scaling origin
 * @param dest
 * will hold the result
 * @return this
 */
     fun scaleAround(factor:Float, ox:Float, oy:Float, oz:Float, dest:Matrix4f):Matrix4f

/**
 * Pre-multiply scaling to `this` matrix by scaling all base axes by the given `xyz` factor,
 * and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `S` the scaling matrix,
 * then the new matrix will be `S * M`. So when transforming a
 * vector `v` with the new matrix by using `S * M * v`
 * , the scaling will be applied last!
 *
 * @param xyz
 * the factor to scale all three base axes by
 * @param dest
 * will hold the result
 * @return dest
 */
     fun scaleLocal(xyz:Float, dest:Matrix4f):Matrix4f

/**
 * Pre-multiply scaling to `this` matrix by scaling the base axes by the given x,
 * y and z factors and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `S` the scaling matrix,
 * then the new matrix will be `S * M`. So when transforming a
 * vector `v` with the new matrix by using `S * M * v`
 * , the scaling will be applied last!
 *
 * @param x
 * the factor of the x component
 * @param y
 * the factor of the y component
 * @param z
 * the factor of the z component
 * @param dest
 * will hold the result
 * @return dest
 */
     fun scaleLocal(x:Float, y:Float, z:Float, dest:Matrix4f):Matrix4f

/**
 * Pre-multiply scaling to `this` matrix by scaling the base axes by the given sx,
 * sy and sz factors while using the given <tt>(ox, oy, oz)</tt> as the scaling origin,
 * and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `S` the scaling matrix,
 * then the new matrix will be `S * M`. So when transforming a
 * vector `v` with the new matrix by using `S * M * v`
 * , the scaling will be applied last!
 *
 *
 * This method is equivalent to calling: <tt>new Matrix4f().translate(ox, oy, oz).scale(sx, sy, sz).translate(-ox, -oy, -oz).mul(this, dest)</tt>
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun scaleAroundLocal(sx:Float, sy:Float, sz:Float, ox:Float, oy:Float, oz:Float, dest:Matrix4f):Matrix4f

/**
 * Pre-multiply scaling to this matrix by scaling all three base axes by the given `factor`
 * while using <tt>(ox, oy, oz)</tt> as the scaling origin,
 * and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `S` the scaling matrix,
 * then the new matrix will be `S * M`. So when transforming a
 * vector `v` with the new matrix by using `S * M * v`, the
 * scaling will be applied last!
 *
 *
 * This method is equivalent to calling: <tt>new Matrix4f().translate(ox, oy, oz).scale(factor).translate(-ox, -oy, -oz).mul(this, dest)</tt>
 *
 * @param factor
 * the scaling factor for all three axes
 * @param ox
 * the x coordinate of the scaling origin
 * @param oy
 * the y coordinate of the scaling origin
 * @param oz
 * the z coordinate of the scaling origin
 * @param dest
 * will hold the result
 * @return this
 */
     fun scaleAroundLocal(factor:Float, ox:Float, oy:Float, oz:Float, dest:Matrix4f):Matrix4f

/**
 * Apply rotation about the X axis to this matrix by rotating the given amount of radians
 * and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateX(ang:Float, dest:Matrix4f):Matrix4f

/**
 * Apply rotation about the Y axis to this matrix by rotating the given amount of radians
 * and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateY(ang:Float, dest:Matrix4f):Matrix4f

/**
 * Apply rotation about the Z axis to this matrix by rotating the given amount of radians
 * and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateZ(ang:Float, dest:Matrix4f):Matrix4f

/**
 * Apply rotation about the Z axis to align the local <tt>+X</tt> towards <tt>(dirX, dirY)</tt> and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return this
 */
     fun rotateTowardsXY(dirX:Float, dirY:Float, dest:Matrix4f):Matrix4f

/**
 * Apply rotation of `angleX` radians about the X axis, followed by a rotation of `angleY` radians about the Y axis and
 * followed by a rotation of `angleZ` radians about the Z axis and store the result in `dest`.
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
 * This method is equivalent to calling: <tt>rotateX(angleX, dest).rotateY(angleY).rotateZ(angleZ)</tt>
 *
 * @param angleX
 * the angle to rotate about X
 * @param angleY
 * the angle to rotate about Y
 * @param angleZ
 * the angle to rotate about Z
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateXYZ(angleX:Float, angleY:Float, angleZ:Float, dest:Matrix4f):Matrix4f

/**
 * Apply rotation of `angleX` radians about the X axis, followed by a rotation of `angleY` radians about the Y axis and
 * followed by a rotation of `angleZ` radians about the Z axis and store the result in `dest`.
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
 * @param angleX
 * the angle to rotate about X
 * @param angleY
 * the angle to rotate about Y
 * @param angleZ
 * the angle to rotate about Z
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateAffineXYZ(angleX:Float, angleY:Float, angleZ:Float, dest:Matrix4f):Matrix4f

/**
 * Apply rotation of `angleZ` radians about the Z axis, followed by a rotation of `angleY` radians about the Y axis and
 * followed by a rotation of `angleX` radians about the X axis and store the result in `dest`.
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
 * This method is equivalent to calling: <tt>rotateZ(angleZ, dest).rotateY(angleY).rotateX(angleX)</tt>
 *
 * @param angleZ
 * the angle to rotate about Z
 * @param angleY
 * the angle to rotate about Y
 * @param angleX
 * the angle to rotate about X
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateZYX(angleZ:Float, angleY:Float, angleX:Float, dest:Matrix4f):Matrix4f

/**
 * Apply rotation of `angleZ` radians about the Z axis, followed by a rotation of `angleY` radians about the Y axis and
 * followed by a rotation of `angleX` radians about the X axis and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateAffineZYX(angleZ:Float, angleY:Float, angleX:Float, dest:Matrix4f):Matrix4f

/**
 * Apply rotation of `angleY` radians about the Y axis, followed by a rotation of `angleX` radians about the X axis and
 * followed by a rotation of `angleZ` radians about the Z axis and store the result in `dest`.
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
 * This method is equivalent to calling: <tt>rotateY(angleY, dest).rotateX(angleX).rotateZ(angleZ)</tt>
 *
 * @param angleY
 * the angle to rotate about Y
 * @param angleX
 * the angle to rotate about X
 * @param angleZ
 * the angle to rotate about Z
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateYXZ(angleY:Float, angleX:Float, angleZ:Float, dest:Matrix4f):Matrix4f

/**
 * Apply rotation of `angleY` radians about the Y axis, followed by a rotation of `angleX` radians about the X axis and
 * followed by a rotation of `angleZ` radians about the Z axis and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateAffineYXZ(angleY:Float, angleX:Float, angleZ:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
 *
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
     fun rotate(ang:Float, x:Float, y:Float, z:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
 *
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
     fun rotateTranslation(ang:Float, x:Float, y:Float, z:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
 *
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
     fun rotateAffine(ang:Float, x:Float, y:Float, z:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
 *
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
     fun rotateLocal(ang:Float, x:Float, y:Float, z:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
 *
 * @param ang
 * the angle in radians to rotate about the X axis
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateLocalX(ang:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
 *
 * @param ang
 * the angle in radians to rotate about the Y axis
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateLocalY(ang:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle)
 *
 * @param ang
 * the angle in radians to rotate about the Z axis
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateLocalZ(ang:Float, dest:Matrix4f):Matrix4f

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
 * @param offset
 * the number of units in x, y and z by which to translate
 * @param dest
 * will hold the result
 * @return dest
 */
     fun translate(offset:Vector3fc, dest:Matrix4f):Matrix4f

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
     fun translate(x:Float, y:Float, z:Float, dest:Matrix4f):Matrix4f

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
 * @param offset
 * the number of units in x, y and z by which to translate
 * @param dest
 * will hold the result
 * @return dest
 */
     fun translateLocal(offset:Vector3fc, dest:Matrix4f):Matrix4f

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
     fun translateLocal(x:Float, y:Float, z:Float, dest:Matrix4f):Matrix4f

/**
 * Apply an orthographic projection transformation for a right-handed coordinate system
 * using the given NDC z range to this matrix and store the result in `dest`.
 *
 *
 * If `M` is `this` matrix and `O` the orthographic projection matrix,
 * then the new matrix will be `M * O`. So when transforming a
 * vector `v` with the new matrix by using `M * O * v`, the
 * orthographic projection transformation will be applied first!
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
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
     fun ortho(left:Float, right:Float, bottom:Float, top:Float, zNear:Float, zFar:Float, zZeroToOne:Boolean, dest:Matrix4f):Matrix4f

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
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
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
     fun ortho(left:Float, right:Float, bottom:Float, top:Float, zNear:Float, zFar:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
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
     fun orthoLH(left:Float, right:Float, bottom:Float, top:Float, zNear:Float, zFar:Float, zZeroToOne:Boolean, dest:Matrix4f):Matrix4f

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
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
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
     fun orthoLH(left:Float, right:Float, bottom:Float, top:Float, zNear:Float, zFar:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
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
     fun orthoSymmetric(width:Float, height:Float, zNear:Float, zFar:Float, zZeroToOne:Boolean, dest:Matrix4f):Matrix4f

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
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
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
     fun orthoSymmetric(width:Float, height:Float, zNear:Float, zFar:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
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
     fun orthoSymmetricLH(width:Float, height:Float, zNear:Float, zFar:Float, zZeroToOne:Boolean, dest:Matrix4f):Matrix4f

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
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho)
 *
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
     fun orthoSymmetricLH(width:Float, height:Float, zNear:Float, zFar:Float, dest:Matrix4f):Matrix4f

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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun ortho2D(left:Float, right:Float, bottom:Float, top:Float, dest:Matrix4f):Matrix4f

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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun ortho2DLH(left:Float, right:Float, bottom:Float, top:Float, dest:Matrix4f):Matrix4f

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
 * @see .lookAlong
 * @see .lookAt
 * @param dir
 * the direction in space to look along
 * @param up
 * the direction of 'up'
 * @param dest
 * will hold the result
 * @return dest
 */
     fun lookAlong(dir:Vector3fc, up:Vector3fc, dest:Matrix4f):Matrix4f

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
 * @see .lookAt
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
     fun lookAlong(dirX:Float, dirY:Float, dirZ:Float, upX:Float, upY:Float, upZ:Float, dest:Matrix4f):Matrix4f

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
 * @see .lookAt
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
     fun lookAt(eye:Vector3fc, center:Vector3fc, up:Vector3fc, dest:Matrix4f):Matrix4f

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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun lookAt(eyeX:Float, eyeY:Float, eyeZ:Float, centerX:Float, centerY:Float, centerZ:Float, upX:Float, upY:Float, upZ:Float, dest:Matrix4f):Matrix4f

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
     fun lookAtPerspective(eyeX:Float, eyeY:Float, eyeZ:Float, centerX:Float, centerY:Float, centerZ:Float, upX:Float, upY:Float, upZ:Float, dest:Matrix4f):Matrix4f

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
     fun lookAtLH(eye:Vector3fc, center:Vector3fc, up:Vector3fc, dest:Matrix4f):Matrix4f

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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun lookAtLH(eyeX:Float, eyeY:Float, eyeZ:Float, centerX:Float, centerY:Float, centerZ:Float, upX:Float, upY:Float, upZ:Float, dest:Matrix4f):Matrix4f

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
     fun lookAtPerspectiveLH(eyeX:Float, eyeY:Float, eyeZ:Float, centerX:Float, centerY:Float, centerZ:Float, upX:Float, upY:Float, upZ:Float, dest:Matrix4f):Matrix4f

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
     fun perspective(fovy:Float, aspect:Float, zNear:Float, zFar:Float, zZeroToOne:Boolean, dest:Matrix4f):Matrix4f

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
     fun perspective(fovy:Float, aspect:Float, zNear:Float, zFar:Float, dest:Matrix4f):Matrix4f

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
     fun perspectiveLH(fovy:Float, aspect:Float, zNear:Float, zFar:Float, zZeroToOne:Boolean, dest:Matrix4f):Matrix4f

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
     fun perspectiveLH(fovy:Float, aspect:Float, zNear:Float, zFar:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
 *
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
     fun frustum(left:Float, right:Float, bottom:Float, top:Float, zNear:Float, zFar:Float, zZeroToOne:Boolean, dest:Matrix4f):Matrix4f

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
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
 *
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
     fun frustum(left:Float, right:Float, bottom:Float, top:Float, zNear:Float, zFar:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
 *
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
     fun frustumLH(left:Float, right:Float, bottom:Float, top:Float, zNear:Float, zFar:Float, zZeroToOne:Boolean, dest:Matrix4f):Matrix4f

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
 * Reference: [http://www.songho.ca](http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective)
 *
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
     fun frustumLH(left:Float, right:Float, bottom:Float, top:Float, zNear:Float, zFar:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
 *
 * @param quat
 * the [Quaternionfc]
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotate(quat:Quaternionfc, dest:Matrix4f):Matrix4f

/**
 * Apply the rotation - and possibly scaling - transformation of the given [Quaternionfc] to this [affine][.isAffine] matrix and store
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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
 *
 * @param quat
 * the [Quaternionfc]
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateAffine(quat:Quaternionfc, dest:Matrix4f):Matrix4f

/**
 * Apply the rotation - and possibly scaling - ransformation of the given [Quaternionfc] to this matrix, which is assumed to only contain a translation, and store
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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
 *
 * @param quat
 * the [Quaternionfc]
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateTranslation(quat:Quaternionfc, dest:Matrix4f):Matrix4f

/**
 * Apply the rotation - and possibly scaling - transformation of the given [Quaternionfc] to this matrix while using <tt>(ox, oy, oz)</tt> as the rotation origin,
 * and store the result in `dest`.
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
 * This method is equivalent to calling: <tt>translate(ox, oy, oz, dest).rotate(quat).translate(-ox, -oy, -oz)</tt>
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateAround(quat:Quaternionfc, ox:Float, oy:Float, oz:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion)
 *
 * @param quat
 * the [Quaternionfc]
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateLocal(quat:Quaternionfc, dest:Matrix4f):Matrix4f

/**
 * Pre-multiply the rotation - and possibly scaling - transformation of the given [Quaternionfc] to this matrix while using <tt>(ox, oy, oz)</tt>
 * as the rotation origin, and store the result in `dest`.
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
 * This method is equivalent to calling: <tt>translateLocal(-ox, -oy, -oz, dest).rotateLocal(quat).translateLocal(ox, oy, oz)</tt>
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateAroundLocal(quat:Quaternionfc, ox:Float, oy:Float, oz:Float, dest:Matrix4f):Matrix4f

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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle)
 *
 * @see .rotate
 * @param axisAngle
 * the [AxisAngle4f] (needs to be [normalized][AxisAngle4f.normalize])
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotate(axisAngle:AxisAngle4f, dest:Matrix4f):Matrix4f

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
 * Reference: [http://en.wikipedia.org](http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle)
 *
 * @see .rotate
 * @param angle
 * the angle in radians
 * @param axis
 * the rotation axis (needs to be [normalized][Vector3fc.normalize])
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotate(angle:Float, axis:Vector3fc, dest:Matrix4f):Matrix4f

/**
 * Unproject the given window coordinates <tt>(winX, winY, winZ)</tt> by `this` matrix using the specified viewport.
 *
 *
 * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
 * and then transforms those NDC coordinates by the inverse of `this` matrix.
 *
 *
 * The depth range of <tt>winZ</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 *
 * As a necessary computation step for unprojecting, this method computes the inverse of `this` matrix.
 * In order to avoid computing the matrix inverse with every invocation, the inverse of `this` matrix can be built
 * once outside using [.invert] and then the method [unprojectInv()][.unprojectInv] can be invoked on it.
 *
 * @see .unprojectInv
 * @see .invert
 * @param winX
 * the x-coordinate in window coordinates (pixels)
 * @param winY
 * the y-coordinate in window coordinates (pixels)
 * @param winZ
 * the z-coordinate, which is the depth value in <tt>[0..1]</tt>
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param dest
 * will hold the unprojected position
 * @return dest
 */
     fun unproject(winX:Float, winY:Float, winZ:Float, viewport:IntArray, dest:Vector4f):Vector4f

/**
 * Unproject the given window coordinates <tt>(winX, winY, winZ)</tt> by `this` matrix using the specified viewport.
 *
 *
 * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
 * and then transforms those NDC coordinates by the inverse of `this` matrix.
 *
 *
 * The depth range of <tt>winZ</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 *
 * As a necessary computation step for unprojecting, this method computes the inverse of `this` matrix.
 * In order to avoid computing the matrix inverse with every invocation, the inverse of `this` matrix can be built
 * once outside using [.invert] and then the method [unprojectInv()][.unprojectInv] can be invoked on it.
 *
 * @see .unprojectInv
 * @see .invert
 * @param winX
 * the x-coordinate in window coordinates (pixels)
 * @param winY
 * the y-coordinate in window coordinates (pixels)
 * @param winZ
 * the z-coordinate, which is the depth value in <tt>[0..1]</tt>
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param dest
 * will hold the unprojected position
 * @return dest
 */
     fun unproject(winX:Float, winY:Float, winZ:Float, viewport:IntArray, dest: Vector3m): Vector3m

/**
 * Unproject the given window coordinates `winCoords` by `this` matrix using the specified viewport.
 *
 *
 * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
 * and then transforms those NDC coordinates by the inverse of `this` matrix.
 *
 *
 * The depth range of <tt>winCoords.z</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 *
 * As a necessary computation step for unprojecting, this method computes the inverse of `this` matrix.
 * In order to avoid computing the matrix inverse with every invocation, the inverse of `this` matrix can be built
 * once outside using [.invert] and then the method [unprojectInv()][.unprojectInv] can be invoked on it.
 *
 * @see .unprojectInv
 * @see .unproject
 * @see .invert
 * @param winCoords
 * the window coordinates to unproject
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param dest
 * will hold the unprojected position
 * @return dest
 */
     fun unproject(winCoords:Vector3fc, viewport:IntArray, dest:Vector4f):Vector4f

/**
 * Unproject the given window coordinates `winCoords` by `this` matrix using the specified viewport.
 *
 *
 * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
 * and then transforms those NDC coordinates by the inverse of `this` matrix.
 *
 *
 * The depth range of <tt>winCoords.z</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 *
 * As a necessary computation step for unprojecting, this method computes the inverse of `this` matrix.
 * In order to avoid computing the matrix inverse with every invocation, the inverse of `this` matrix can be built
 * once outside using [.invert] and then the method [unprojectInv()][.unprojectInv] can be invoked on it.
 *
 * @see .unprojectInv
 * @see .unproject
 * @see .invert
 * @param winCoords
 * the window coordinates to unproject
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param dest
 * will hold the unprojected position
 * @return dest
 */
     fun unproject(winCoords:Vector3fc, viewport:IntArray, dest: Vector3m): Vector3m

/**
 * Unproject the given 2D window coordinates <tt>(winX, winY)</tt> by `this` matrix using the specified viewport
 * and compute the origin and the direction of the resulting ray which starts at NDC <tt>z = -1.0</tt> and goes through NDC <tt>z = +1.0</tt>.
 *
 *
 * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
 * and then transforms those NDC coordinates by the inverse of `this` matrix.
 *
 *
 * As a necessary computation step for unprojecting, this method computes the inverse of `this` matrix.
 * In order to avoid computing the matrix inverse with every invocation, the inverse of `this` matrix can be built
 * once outside using [.invert] and then the method [unprojectInvRay()][.unprojectInvRay] can be invoked on it.
 *
 * @see .unprojectInvRay
 * @see .invert
 * @param winX
 * the x-coordinate in window coordinates (pixels)
 * @param winY
 * the y-coordinate in window coordinates (pixels)
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param originDest
 * will hold the ray origin
 * @param dirDest
 * will hold the (unnormalized) ray direction
 * @return this
 */
     fun unprojectRay(winX:Float, winY:Float, viewport:IntArray, originDest: Vector3m, dirDest: Vector3m):Matrix4f

/**
 * Unproject the given 2D window coordinates `winCoords` by `this` matrix using the specified viewport
 * and compute the origin and the direction of the resulting ray which starts at NDC <tt>z = -1.0</tt> and goes through NDC <tt>z = +1.0</tt>.
 *
 *
 * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
 * and then transforms those NDC coordinates by the inverse of `this` matrix.
 *
 *
 * As a necessary computation step for unprojecting, this method computes the inverse of `this` matrix.
 * In order to avoid computing the matrix inverse with every invocation, the inverse of `this` matrix can be built
 * once outside using [.invert] and then the method [unprojectInvRay()][.unprojectInvRay] can be invoked on it.
 *
 * @see .unprojectInvRay
 * @see .unprojectRay
 * @see .invert
 * @param winCoords
 * the window coordinates to unproject
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param originDest
 * will hold the ray origin
 * @param dirDest
 * will hold the (unnormalized) ray direction
 * @return this
 */
     fun unprojectRay(winCoords:Vector2fc, viewport:IntArray, originDest: Vector3m, dirDest: Vector3m):Matrix4f

/**
 * Unproject the given window coordinates `winCoords` by `this` matrix using the specified viewport.
 *
 *
 * This method differs from [unproject()][.unproject]
 * in that it assumes that `this` is already the inverse matrix of the original projection matrix.
 * It exists to avoid recomputing the matrix inverse with every invocation.
 *
 *
 * The depth range of <tt>winCoords.z</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 *
 * This method reads the four viewport parameters from the given int[].
 *
 * @see .unproject
 * @param winCoords
 * the window coordinates to unproject
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param dest
 * will hold the unprojected position
 * @return dest
 */
     fun unprojectInv(winCoords:Vector3fc, viewport:IntArray, dest:Vector4f):Vector4f

/**
 * Unproject the given window coordinates <tt>(winX, winY, winZ)</tt> by `this` matrix using the specified viewport.
 *
 *
 * This method differs from [unproject()][.unproject]
 * in that it assumes that `this` is already the inverse matrix of the original projection matrix.
 * It exists to avoid recomputing the matrix inverse with every invocation.
 *
 *
 * The depth range of <tt>winZ</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 * @see .unproject
 * @param winX
 * the x-coordinate in window coordinates (pixels)
 * @param winY
 * the y-coordinate in window coordinates (pixels)
 * @param winZ
 * the z-coordinate, which is the depth value in <tt>[0..1]</tt>
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param dest
 * will hold the unprojected position
 * @return dest
 */
     fun unprojectInv(winX:Float, winY:Float, winZ:Float, viewport:IntArray, dest:Vector4f):Vector4f

/**
 * Unproject the given window coordinates `winCoords` by `this` matrix using the specified viewport
 * and compute the origin and the direction of the resulting ray which starts at NDC <tt>z = -1.0</tt> and goes through NDC <tt>z = +1.0</tt>.
 *
 *
 * This method differs from [unprojectRay()][.unprojectRay]
 * in that it assumes that `this` is already the inverse matrix of the original projection matrix.
 * It exists to avoid recomputing the matrix inverse with every invocation.
 *
 * @see .unprojectRay
 * @param winCoords
 * the window coordinates to unproject
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param originDest
 * will hold the ray origin
 * @param dirDest
 * will hold the (unnormalized) ray direction
 * @return this
 */
     fun unprojectInvRay(winCoords:Vector2fc, viewport:IntArray, originDest: Vector3m, dirDest: Vector3m):Matrix4f

/**
 * Unproject the given 2D window coordinates <tt>(winX, winY)</tt> by `this` matrix using the specified viewport
 * and compute the origin and the direction of the resulting ray which starts at NDC <tt>z = -1.0</tt> and goes through NDC <tt>z = +1.0</tt>.
 *
 *
 * This method differs from [unprojectRay()][.unprojectRay]
 * in that it assumes that `this` is already the inverse matrix of the original projection matrix.
 * It exists to avoid recomputing the matrix inverse with every invocation.
 *
 * @see .unprojectRay
 * @param winX
 * the x-coordinate in window coordinates (pixels)
 * @param winY
 * the y-coordinate in window coordinates (pixels)
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param originDest
 * will hold the ray origin
 * @param dirDest
 * will hold the (unnormalized) ray direction
 * @return this
 */
     fun unprojectInvRay(winX:Float, winY:Float, viewport:IntArray, originDest: Vector3m, dirDest: Vector3m):Matrix4f

/**
 * Unproject the given window coordinates `winCoords` by `this` matrix using the specified viewport.
 *
 *
 * This method differs from [unproject()][.unproject]
 * in that it assumes that `this` is already the inverse matrix of the original projection matrix.
 * It exists to avoid recomputing the matrix inverse with every invocation.
 *
 *
 * The depth range of <tt>winCoords.z</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 * @see .unproject
 * @param winCoords
 * the window coordinates to unproject
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param dest
 * will hold the unprojected position
 * @return dest
 */
     fun unprojectInv(winCoords:Vector3fc, viewport:IntArray, dest: Vector3m): Vector3m

/**
 * Unproject the given window coordinates <tt>(winX, winY, winZ)</tt> by `this` matrix using the specified viewport.
 *
 *
 * This method differs from [unproject()][.unproject]
 * in that it assumes that `this` is already the inverse matrix of the original projection matrix.
 * It exists to avoid recomputing the matrix inverse with every invocation.
 *
 *
 * The depth range of <tt>winZ</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 * @see .unproject
 * @param winX
 * the x-coordinate in window coordinates (pixels)
 * @param winY
 * the y-coordinate in window coordinates (pixels)
 * @param winZ
 * the z-coordinate, which is the depth value in <tt>[0..1]</tt>
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param dest
 * will hold the unprojected position
 * @return dest
 */
     fun unprojectInv(winX:Float, winY:Float, winZ:Float, viewport:IntArray, dest: Vector3m): Vector3m

/**
 * Project the given <tt>(x, y, z)</tt> position via `this` matrix using the specified viewport
 * and store the resulting window coordinates in `winCoordsDest`.
 *
 *
 * This method transforms the given coordinates by `this` matrix including perspective division to
 * obtain normalized device coordinates, and then translates these into window coordinates by using the
 * given `viewport` settings <tt>[x, y, width, height]</tt>.
 *
 *
 * The depth range of the returned `winCoordsDest.z` will be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 * @param x
 * the x-coordinate of the position to project
 * @param y
 * the y-coordinate of the position to project
 * @param z
 * the z-coordinate of the position to project
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param winCoordsDest
 * will hold the projected window coordinates
 * @return winCoordsDest
 */
     fun project(x:Float, y:Float, z:Float, viewport:IntArray, winCoordsDest:Vector4f):Vector4f

/**
 * Project the given <tt>(x, y, z)</tt> position via `this` matrix using the specified viewport
 * and store the resulting window coordinates in `winCoordsDest`.
 *
 *
 * This method transforms the given coordinates by `this` matrix including perspective division to
 * obtain normalized device coordinates, and then translates these into window coordinates by using the
 * given `viewport` settings <tt>[x, y, width, height]</tt>.
 *
 *
 * The depth range of the returned `winCoordsDest.z` will be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 * @param x
 * the x-coordinate of the position to project
 * @param y
 * the y-coordinate of the position to project
 * @param z
 * the z-coordinate of the position to project
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param winCoordsDest
 * will hold the projected window coordinates
 * @return winCoordsDest
 */
     fun project(x:Float, y:Float, z:Float, viewport:IntArray, winCoordsDest: Vector3m): Vector3m

/**
 * Project the given `position` via `this` matrix using the specified viewport
 * and store the resulting window coordinates in `winCoordsDest`.
 *
 *
 * This method transforms the given coordinates by `this` matrix including perspective division to
 * obtain normalized device coordinates, and then translates these into window coordinates by using the
 * given `viewport` settings <tt>[x, y, width, height]</tt>.
 *
 *
 * The depth range of the returned `winCoordsDest.z` will be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 * @see .project
 * @param position
 * the position to project into window coordinates
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param winCoordsDest
 * will hold the projected window coordinates
 * @return winCoordsDest
 */
     fun project(position:Vector3fc, viewport:IntArray, winCoordsDest:Vector4f):Vector4f

/**
 * Project the given `position` via `this` matrix using the specified viewport
 * and store the resulting window coordinates in `winCoordsDest`.
 *
 *
 * This method transforms the given coordinates by `this` matrix including perspective division to
 * obtain normalized device coordinates, and then translates these into window coordinates by using the
 * given `viewport` settings <tt>[x, y, width, height]</tt>.
 *
 *
 * The depth range of the returned `winCoordsDest.z` will be <tt>[0..1]</tt>, which is also the OpenGL default.
 *
 * @see .project
 * @param position
 * the position to project into window coordinates
 * @param viewport
 * the viewport described by <tt>[x, y, width, height]</tt>
 * @param winCoordsDest
 * will hold the projected window coordinates
 * @return winCoordsDest
 */
     fun project(position:Vector3fc, viewport:IntArray, winCoordsDest: Vector3m): Vector3m

/**
 * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
 * specified via the equation <tt>x*a + y*b + z*c + d = 0</tt> and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun reflect(a:Float, b:Float, c:Float, d:Float, dest:Matrix4f):Matrix4f

/**
 * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
 * specified via the plane normal and a point on the plane, and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun reflect(nx:Float, ny:Float, nz:Float, px:Float, py:Float, pz:Float, dest:Matrix4f):Matrix4f

/**
 * Apply a mirror/reflection transformation to this matrix that reflects about a plane
 * specified via the plane orientation and a point on the plane, and store the result in `dest`.
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
 * the plane orientation relative to an implied normal vector of <tt>(0, 0, 1)</tt>
 * @param point
 * a point on the plane
 * @param dest
 * will hold the result
 * @return dest
 */
     fun reflect(orientation:Quaternionfc, point:Vector3fc, dest:Matrix4f):Matrix4f

/**
 * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
 * specified via the plane normal and a point on the plane, and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun reflect(normal:Vector3fc, point:Vector3fc, dest:Matrix4f):Matrix4f

/**
 * Get the row at the given `row` index, starting with `0`.
 *
 * @param row
 * the row index in <tt>[0..3]</tt>
 * @param dest
 * will hold the row components
 * @return the passed in destination
 * @throws IndexOutOfBoundsException if `row` is not in <tt>[0..3]</tt>
 */
    @Throws(IndexOutOfBoundsException::class)
 fun getRow(row:Int, dest:Vector4f):Vector4f

/**
 * Get the column at the given `column` index, starting with `0`.
 *
 * @param column
 * the column index in <tt>[0..3]</tt>
 * @param dest
 * will hold the column components
 * @return the passed in destination
 * @throws IndexOutOfBoundsException if `column` is not in <tt>[0..3]</tt>
 */
    @Throws(IndexOutOfBoundsException::class)
 fun getColumn(column:Int, dest:Vector4f):Vector4f

/**
 * Compute a normal matrix from the upper left 3x3 submatrix of `this`
 * and store it into the upper left 3x3 submatrix of `dest`.
 * All other values of `dest` will be set to identity.
 *
 *
 * The normal matrix of <tt>m</tt> is the transpose of the inverse of <tt>m</tt>.
 *
 * @param dest
 * will hold the result
 * @return dest
 */
     fun normal(dest:Matrix4f):Matrix4f

/**
 * Compute a normal matrix from the upper left 3x3 submatrix of `this`
 * and store it into `dest`.
 *
 *
 * The normal matrix of <tt>m</tt> is the transpose of the inverse of <tt>m</tt>.
 *
 * @see Matrix3f.set
 * @see .get3x3
 * @param dest
 * will hold the result
 * @return dest
 */
     fun normal(dest:Matrix3f):Matrix3f

/**
 * Normalize the upper left 3x3 submatrix of this matrix and store the result in `dest`.
 *
 *
 * The resulting matrix will map unit vectors to unit vectors, though a pair of orthogonal input unit
 * vectors need not be mapped to a pair of orthogonal output vectors if the original matrix was not orthogonal itself
 * (i.e. had *skewing*).
 *
 * @param dest
 * will hold the result
 * @return dest
 */
     fun normalize3x3(dest:Matrix4f):Matrix4f

/**
 * Normalize the upper left 3x3 submatrix of this matrix and store the result in `dest`.
 *
 *
 * The resulting matrix will map unit vectors to unit vectors, though a pair of orthogonal input unit
 * vectors need not be mapped to a pair of orthogonal output vectors if the original matrix was not orthogonal itself
 * (i.e. had *skewing*).
 *
 * @param dest
 * will hold the result
 * @return dest
 */
     fun normalize3x3(dest:Matrix3f):Matrix3f

/**
 * Calculate a frustum plane of `this` matrix, which
 * can be a projection matrix or a combined modelview-projection matrix, and store the result
 * in the given `planeEquation`.
 *
 *
 * Generally, this method computes the frustum plane in the local frame of
 * any coordinate system that existed before `this`
 * transformation was applied to it in order to yield homogeneous clipping space.
 *
 *
 * The frustum plane will be given in the form of a general plane equation:
 * <tt>a*x + b*y + c*z + d = 0</tt>, where the given [Vector4f] components will
 * hold the <tt>(a, b, c, d)</tt> values of the equation.
 *
 *
 * The plane normal, which is <tt>(a, b, c)</tt>, is directed "inwards" of the frustum.
 * Any plane/point test using <tt>a*x + b*y + c*z + d</tt> therefore will yield a result greater than zero
 * if the point is within the frustum (i.e. at the *positive* side of the frustum plane).
 *
 *
 * For performing frustum culling, the class [FrustumIntersection] should be used instead of
 * manually obtaining the frustum planes and testing them against points, spheres or axis-aligned boxes.
 *
 *
 * Reference: [
 * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix](http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf)
 *
 * @param plane
 * one of the six possible planes, given as numeric constants
 * [.PLANE_NX], [.PLANE_PX],
 * [.PLANE_NY], [.PLANE_PY],
 * [.PLANE_NZ] and [.PLANE_PZ]
 * @param planeEquation
 * will hold the computed plane equation.
 * The plane equation will be normalized, meaning that <tt>(a, b, c)</tt> will be a unit vector
 * @return planeEquation
 */
     fun frustumPlane(plane:Int, planeEquation:Vector4f):Vector4f

/**
 * Calculate a frustum plane of `this` matrix, which
 * can be a projection matrix or a combined modelview-projection matrix, and store the result
 * in the given `plane`.
 *
 *
 * Generally, this method computes the frustum plane in the local frame of
 * any coordinate system that existed before `this`
 * transformation was applied to it in order to yield homogeneous clipping space.
 *
 *
 * The plane normal, which is <tt>(a, b, c)</tt>, is directed "inwards" of the frustum.
 * Any plane/point test using <tt>a*x + b*y + c*z + d</tt> therefore will yield a result greater than zero
 * if the point is within the frustum (i.e. at the *positive* side of the frustum plane).
 *
 *
 * For performing frustum culling, the class [FrustumIntersection] should be used instead of
 * manually obtaining the frustum planes and testing them against points, spheres or axis-aligned boxes.
 *
 *
 * Reference: [
 * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix](http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf)
 *
 * @param which
 * one of the six possible planes, given as numeric constants
 * [.PLANE_NX], [.PLANE_PX],
 * [.PLANE_NY], [.PLANE_PY],
 * [.PLANE_NZ] and [.PLANE_PZ]
 * @param plane
 * will hold the computed plane equation.
 * The plane equation will be normalized, meaning that <tt>(a, b, c)</tt> will be a unit vector
 * @return planeEquation
 */
     fun frustumPlane(which:Int, plane:Planef):Planef

/**
 * Compute the corner coordinates of the frustum defined by `this` matrix, which
 * can be a projection matrix or a combined modelview-projection matrix, and store the result
 * in the given `point`.
 *
 *
 * Generally, this method computes the frustum corners in the local frame of
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
 * @param corner
 * one of the eight possible corners, given as numeric constants
 * [.CORNER_NXNYNZ], [.CORNER_PXNYNZ], [.CORNER_PXPYNZ], [.CORNER_NXPYNZ],
 * [.CORNER_PXNYPZ], [.CORNER_NXNYPZ], [.CORNER_NXPYPZ], [.CORNER_PXPYPZ]
 * @param point
 * will hold the resulting corner point coordinates
 * @return point
 */
     fun frustumCorner(corner:Int, point: Vector3m): Vector3m

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
     fun perspectiveOrigin(origin: Vector3m): Vector3m

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
     fun perspectiveFov():Float

/**
 * Extract the near clip plane distance from `this` perspective projection matrix.
 *
 *
 * This method only works if `this` is a perspective projection matrix, for example obtained via [.perspective].
 *
 * @return the near clip plane distance
 */
     fun perspectiveNear():Float

/**
 * Extract the far clip plane distance from `this` perspective projection matrix.
 *
 *
 * This method only works if `this` is a perspective projection matrix, for example obtained via [.perspective].
 *
 * @return the far clip plane distance
 */
     fun perspectiveFar():Float

/**
 * Obtain the direction of a ray starting at the center of the coordinate system and going
 * through the near frustum plane.
 *
 *
 * This method computes the `dir` vector in the local frame of
 * any coordinate system that existed before `this`
 * transformation was applied to it in order to yield homogeneous clipping space.
 *
 *
 * The parameters `x` and `y` are used to interpolate the generated ray direction
 * from the bottom-left to the top-right frustum corners.
 *
 *
 * For optimal efficiency when building many ray directions over the whole frustum,
 * it is recommended to use this method only in order to compute the four corner rays at
 * <tt>(0, 0)</tt>, <tt>(1, 0)</tt>, <tt>(0, 1)</tt> and <tt>(1, 1)</tt>
 * and then bilinearly interpolating between them; or to use the [FrustumRayBuilder].
 *
 *
 * Reference: [
 * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix](http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf)
 *
 * @param x
 * the interpolation factor along the left-to-right frustum planes, within <tt>[0..1]</tt>
 * @param y
 * the interpolation factor along the bottom-to-top frustum planes, within <tt>[0..1]</tt>
 * @param dir
 * will hold the normalized ray direction in the local frame of the coordinate system before
 * transforming to homogeneous clipping space using `this` matrix
 * @return dir
 */
     fun frustumRayDir(x:Float, y:Float, dir: Vector3m): Vector3m

/**
 * Obtain the direction of <tt>+Z</tt> before the transformation represented by `this` matrix is applied.
 *
 *
 * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction
 * that is transformed to <tt>+Z</tt> by `this` matrix.
 *
 *
 * This method is equivalent to the following code:
 * <pre>
 * Matrix4f inv = new Matrix4f(this).invert();
 * inv.transformDirection(dir.set(0, 0, 1)).normalize();
</pre> *
 * If `this` is already an orthogonal matrix, then consider using [.normalizedPositiveZ] instead.
 *
 *
 * Reference: [http://www.euclideanspace.com](http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/)
 *
 * @param dir
 * will hold the direction of <tt>+Z</tt>
 * @return dir
 */
     fun positiveZ(dir: Vector3m): Vector3m

/**
 * Obtain the direction of <tt>+Z</tt> before the transformation represented by `this` *orthogonal* matrix is applied.
 * This method only produces correct results if `this` is an *orthogonal* matrix.
 *
 *
 * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction
 * that is transformed to <tt>+Z</tt> by `this` matrix.
 *
 *
 * This method is equivalent to the following code:
 * <pre>
 * Matrix4f inv = new Matrix4f(this).transpose();
 * inv.transformDirection(dir.set(0, 0, 1));
</pre> *
 *
 *
 * Reference: [http://www.euclideanspace.com](http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/)
 *
 * @param dir
 * will hold the direction of <tt>+Z</tt>
 * @return dir
 */
     fun normalizedPositiveZ(dir: Vector3m): Vector3m

/**
 * Obtain the direction of <tt>+X</tt> before the transformation represented by `this` matrix is applied.
 *
 *
 * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction
 * that is transformed to <tt>+X</tt> by `this` matrix.
 *
 *
 * This method is equivalent to the following code:
 * <pre>
 * Matrix4f inv = new Matrix4f(this).invert();
 * inv.transformDirection(dir.set(1, 0, 0)).normalize();
</pre> *
 * If `this` is already an orthogonal matrix, then consider using [.normalizedPositiveX] instead.
 *
 *
 * Reference: [http://www.euclideanspace.com](http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/)
 *
 * @param dir
 * will hold the direction of <tt>+X</tt>
 * @return dir
 */
     fun positiveX(dir: Vector3m): Vector3m

/**
 * Obtain the direction of <tt>+X</tt> before the transformation represented by `this` *orthogonal* matrix is applied.
 * This method only produces correct results if `this` is an *orthogonal* matrix.
 *
 *
 * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction
 * that is transformed to <tt>+X</tt> by `this` matrix.
 *
 *
 * This method is equivalent to the following code:
 * <pre>
 * Matrix4f inv = new Matrix4f(this).transpose();
 * inv.transformDirection(dir.set(1, 0, 0));
</pre> *
 *
 *
 * Reference: [http://www.euclideanspace.com](http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/)
 *
 * @param dir
 * will hold the direction of <tt>+X</tt>
 * @return dir
 */
     fun normalizedPositiveX(dir: Vector3m): Vector3m

/**
 * Obtain the direction of <tt>+Y</tt> before the transformation represented by `this` matrix is applied.
 *
 *
 * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction
 * that is transformed to <tt>+Y</tt> by `this` matrix.
 *
 *
 * This method is equivalent to the following code:
 * <pre>
 * Matrix4f inv = new Matrix4f(this).invert();
 * inv.transformDirection(dir.set(0, 1, 0)).normalize();
</pre> *
 * If `this` is already an orthogonal matrix, then consider using [.normalizedPositiveY] instead.
 *
 *
 * Reference: [http://www.euclideanspace.com](http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/)
 *
 * @param dir
 * will hold the direction of <tt>+Y</tt>
 * @return dir
 */
     fun positiveY(dir: Vector3m): Vector3m

/**
 * Obtain the direction of <tt>+Y</tt> before the transformation represented by `this` *orthogonal* matrix is applied.
 * This method only produces correct results if `this` is an *orthogonal* matrix.
 *
 *
 * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction
 * that is transformed to <tt>+Y</tt> by `this` matrix.
 *
 *
 * This method is equivalent to the following code:
 * <pre>
 * Matrix4f inv = new Matrix4f(this).transpose();
 * inv.transformDirection(dir.set(0, 1, 0));
</pre> *
 *
 *
 * Reference: [http://www.euclideanspace.com](http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/)
 *
 * @param dir
 * will hold the direction of <tt>+Y</tt>
 * @return dir
 */
     fun normalizedPositiveY(dir: Vector3m): Vector3m

/**
 * Obtain the position that gets transformed to the origin by `this` [affine][.isAffine] matrix.
 * This can be used to get the position of the "camera" from a given *view* transformation matrix.
 *
 *
 * This method only works with [affine][.isAffine] matrices.
 *
 *
 * This method is equivalent to the following code:
 * <pre>
 * Matrix4f inv = new Matrix4f(this).invertAffine();
 * inv.transformPosition(origin.set(0, 0, 0));
</pre> *
 *
 * @param origin
 * will hold the position transformed to the origin
 * @return origin
 */
     fun originAffine(origin: Vector3m): Vector3m

/**
 * Obtain the position that gets transformed to the origin by `this` matrix.
 * This can be used to get the position of the "camera" from a given *view/projection* transformation matrix.
 *
 *
 * This method is equivalent to the following code:
 * <pre>
 * Matrix4f inv = new Matrix4f(this).invert();
 * inv.transformPosition(origin.set(0, 0, 0));
</pre> *
 *
 * @param origin
 * will hold the position transformed to the origin
 * @return origin
 */
     fun origin(origin: Vector3m): Vector3m

/**
 * Apply a projection transformation to this matrix that projects onto the plane specified via the general plane equation
 * <tt>x*a + y*b + z*c + d = 0</tt> as if casting a shadow from a given light position/direction `light`
 * and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun shadow(light:Vector4f, a:Float, b:Float, c:Float, d:Float, dest:Matrix4f):Matrix4f

/**
 * Apply a projection transformation to this matrix that projects onto the plane specified via the general plane equation
 * <tt>x*a + y*b + z*c + d = 0</tt> as if casting a shadow from a given light position/direction <tt>(lightX, lightY, lightZ, lightW)</tt>
 * and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun shadow(lightX:Float, lightY:Float, lightZ:Float, lightW:Float, a:Float, b:Float, c:Float, d:Float, dest:Matrix4f):Matrix4f

/**
 * Apply a projection transformation to this matrix that projects onto the plane with the general plane equation
 * <tt>y = 0</tt> as if casting a shadow from a given light position/direction `light`
 * and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun shadow(light:Vector4f, planeTransform:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Apply a projection transformation to this matrix that projects onto the plane with the general plane equation
 * <tt>y = 0</tt> as if casting a shadow from a given light position/direction <tt>(lightX, lightY, lightZ, lightW)</tt>
 * and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun shadow(lightX:Float, lightY:Float, lightZ:Float, lightW:Float, planeTransform:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Apply a picking transformation to this matrix using the given window coordinates <tt>(x, y)</tt> as the pick center
 * and the given <tt>(width, height)</tt> as the size of the picking region in window coordinates, and store the result
 * in `dest`.
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
 * @param dest
 * the destination matrix, which will hold the result
 * @return dest
 */
     fun pick(x:Float, y:Float, width:Float, height:Float, viewport:IntArray, dest:Matrix4f):Matrix4f

/**
 * Apply an arcball view transformation to this matrix with the given `radius` and center <tt>(centerX, centerY, centerZ)</tt>
 * position of the arcball and the specified X and Y rotation angles, and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun arcball(radius:Float, centerX:Float, centerY:Float, centerZ:Float, angleX:Float, angleY:Float, dest:Matrix4f):Matrix4f

/**
 * Apply an arcball view transformation to this matrix with the given `radius` and `center`
 * position of the arcball and the specified X and Y rotation angles, and store the result in `dest`.
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
 * @param dest
 * will hold the result
 * @return dest
 */
     fun arcball(radius:Float, center:Vector3fc, angleX:Float, angleY:Float, dest:Matrix4f):Matrix4f

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
     fun frustumAabb(min: Vector3m, max: Vector3m):Matrix4f

/**
 * Compute the *range matrix* for the Projected Grid transformation as described in chapter "2.4.2 Creating the range conversion matrix"
 * of the paper [Real-time water rendering - Introducing the projected grid concept](http://fileadmin.cs.lth.se/graphics/theses/projects/projgrid/projgrid-lq.pdf)
 * based on the *inverse* of the view-projection matrix which is assumed to be `this`, and store that range matrix into `dest`.
 *
 *
 * If the projected grid will not be visible then this method returns `null`.
 *
 *
 * This method uses the <tt>y = 0</tt> plane for the projection.
 *
 * @param projector
 * the projector view-projection transformation
 * @param sLower
 * the lower (smallest) Y-coordinate which any transformed vertex might have while still being visible on the projected grid
 * @param sUpper
 * the upper (highest) Y-coordinate which any transformed vertex might have while still being visible on the projected grid
 * @param dest
 * will hold the resulting range matrix
 * @return the computed range matrix; or `null` if the projected grid will not be visible
 */
     fun projectedGridRange(projector:Matrix4fc, sLower:Float, sUpper:Float, dest:Matrix4f):Matrix4f?

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
     fun perspectiveFrustumSlice(near:Float, far:Float, dest:Matrix4f):Matrix4f

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
     fun orthoCrop(view:Matrix4fc, dest:Matrix4f):Matrix4f

/**
 * Transform the axis-aligned box given as the minimum corner <tt>(minX, minY, minZ)</tt> and maximum corner <tt>(maxX, maxY, maxZ)</tt>
 * by `this` [affine][.isAffine] matrix and compute the axis-aligned box of the result whose minimum corner is stored in `outMin`
 * and maximum corner stored in `outMax`.
 *
 *
 * Reference: [http://dev.theomader.com](http://dev.theomader.com/transform-bounding-boxes/)
 *
 * @param minX
 * the x coordinate of the minimum corner of the axis-aligned box
 * @param minY
 * the y coordinate of the minimum corner of the axis-aligned box
 * @param minZ
 * the z coordinate of the minimum corner of the axis-aligned box
 * @param maxX
 * the x coordinate of the maximum corner of the axis-aligned box
 * @param maxY
 * the y coordinate of the maximum corner of the axis-aligned box
 * @param maxZ
 * the y coordinate of the maximum corner of the axis-aligned box
 * @param outMin
 * will hold the minimum corner of the resulting axis-aligned box
 * @param outMax
 * will hold the maximum corner of the resulting axis-aligned box
 * @return this
 */
     fun transformAab(minX:Float, minY:Float, minZ:Float, maxX:Float, maxY:Float, maxZ:Float, outMin: Vector3m, outMax: Vector3m):Matrix4f

/**
 * Transform the axis-aligned box given as the minimum corner `min` and maximum corner `max`
 * by `this` [affine][.isAffine] matrix and compute the axis-aligned box of the result whose minimum corner is stored in `outMin`
 * and maximum corner stored in `outMax`.
 *
 * @param min
 * the minimum corner of the axis-aligned box
 * @param max
 * the maximum corner of the axis-aligned box
 * @param outMin
 * will hold the minimum corner of the resulting axis-aligned box
 * @param outMax
 * will hold the maximum corner of the resulting axis-aligned box
 * @return this
 */
     fun transformAab(min:Vector3fc, max:Vector3fc, outMin: Vector3m, outMax: Vector3m):Matrix4f

/**
 * Linearly interpolate `this` and `other` using the given interpolation factor `t`
 * and store the result in `dest`.
 *
 *
 * If `t` is <tt>0.0</tt> then the result is `this`. If the interpolation factor is `1.0`
 * then the result is `other`.
 *
 * @param other
 * the other matrix
 * @param t
 * the interpolation factor between 0.0 and 1.0
 * @param dest
 * will hold the result
 * @return dest
 */
     fun lerp(other:Matrix4fc, t:Float, dest:Matrix4f):Matrix4f

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
 * This method is equivalent to calling: <tt>mulAffine(new Matrix4f().lookAt(new Vector3m(), new Vector3m(dir).negate(), up).invertAffine(), dest)</tt>
 *
 * @see .rotateTowards
 * @param dir
 * the direction to rotate towards
 * @param up
 * the up vector
 * @param dest
 * will hold the result
 * @return dest
 */
     fun rotateTowards(dir:Vector3fc, up:Vector3fc, dest:Matrix4f):Matrix4f

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
 * This method is equivalent to calling: <tt>mulAffine(new Matrix4f().lookAt(0, 0, 0, -dirX, -dirY, -dirZ, upX, upY, upZ).invertAffine(), dest)</tt>
 *
 * @see .rotateTowards
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
     fun rotateTowards(dirX:Float, dirY:Float, dirZ:Float, upX:Float, upY:Float, upZ:Float, dest:Matrix4f):Matrix4f

/**
 * Extract the Euler angles from the rotation represented by the upper left 3x3 submatrix of `this`
 * and store the extracted Euler angles in `dest`.
 *
 *
 * This method assumes that the upper left of `this` only represents a rotation without scaling.
 *
 *
 * Note that the returned Euler angles must be applied in the order <tt>Z * Y * X</tt> to obtain the identical matrix.
 * This means that calling [Matrix4fc.rotateZYX] using the obtained Euler angles will yield
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
     fun getEulerAnglesZYX(dest: Vector3m): Vector3m

/**
 * Test whether the given point <tt>(x, y, z)</tt> is within the frustum defined by `this` matrix.
 *
 *
 * This method assumes `this` matrix to be a transformation from any arbitrary coordinate system/space <tt>M</tt>
 * into standard OpenGL clip space and tests whether the given point with the coordinates <tt>(x, y, z)</tt> given
 * in space <tt>M</tt> is within the clip space.
 *
 *
 * When testing multiple points using the same transformation matrix, [FrustumIntersection] should be used instead.
 *
 *
 * Reference: [
 * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix](http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf)
 *
 * @param x
 * the x-coordinate of the point
 * @param y
 * the y-coordinate of the point
 * @param z
 * the z-coordinate of the point
 * @return `true` if the given point is inside the frustum; `false` otherwise
 */
     fun testPoint(x:Float, y:Float, z:Float):Boolean

/**
 * Test whether the given sphere is partly or completely within or outside of the frustum defined by `this` matrix.
 *
 *
 * This method assumes `this` matrix to be a transformation from any arbitrary coordinate system/space <tt>M</tt>
 * into standard OpenGL clip space and tests whether the given sphere with the coordinates <tt>(x, y, z)</tt> given
 * in space <tt>M</tt> is within the clip space.
 *
 *
 * When testing multiple spheres using the same transformation matrix, or more sophisticated/optimized intersection algorithms are required,
 * [FrustumIntersection] should be used instead.
 *
 *
 * The algorithm implemented by this method is conservative. This means that in certain circumstances a *false positive*
 * can occur, when the method returns <tt>true</tt> for spheres that are actually not visible.
 * See [iquilezles.org](http://iquilezles.org/www/articles/frustumcorrect/frustumcorrect.htm) for an examination of this problem.
 *
 *
 * Reference: [
 * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix](http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf)
 *
 * @param x
 * the x-coordinate of the sphere's center
 * @param y
 * the y-coordinate of the sphere's center
 * @param z
 * the z-coordinate of the sphere's center
 * @param r
 * the sphere's radius
 * @return `true` if the given sphere is partly or completely inside the frustum; `false` otherwise
 */
     fun testSphere(x:Float, y:Float, z:Float, r:Float):Boolean

/**
 * Test whether the given axis-aligned box is partly or completely within or outside of the frustum defined by `this` matrix.
 * The box is specified via its min and max corner coordinates.
 *
 *
 * This method assumes `this` matrix to be a transformation from any arbitrary coordinate system/space <tt>M</tt>
 * into standard OpenGL clip space and tests whether the given axis-aligned box with its minimum corner coordinates <tt>(minX, minY, minZ)</tt>
 * and maximum corner coordinates <tt>(maxX, maxY, maxZ)</tt> given in space <tt>M</tt> is within the clip space.
 *
 *
 * When testing multiple axis-aligned boxes using the same transformation matrix, or more sophisticated/optimized intersection algorithms are required,
 * [FrustumIntersection] should be used instead.
 *
 *
 * The algorithm implemented by this method is conservative. This means that in certain circumstances a *false positive*
 * can occur, when the method returns <tt>-1</tt> for boxes that are actually not visible/do not intersect the frustum.
 * See [iquilezles.org](http://iquilezles.org/www/articles/frustumcorrect/frustumcorrect.htm) for an examination of this problem.
 *
 *
 * Reference: [Efficient View Frustum Culling](http://old.cescg.org/CESCG-2002/DSykoraJJelinek/)
 * <br></br>
 * Reference: [
 * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix](http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf)
 *
 * @param minX
 * the x-coordinate of the minimum corner
 * @param minY
 * the y-coordinate of the minimum corner
 * @param minZ
 * the z-coordinate of the minimum corner
 * @param maxX
 * the x-coordinate of the maximum corner
 * @param maxY
 * the y-coordinate of the maximum corner
 * @param maxZ
 * the z-coordinate of the maximum corner
 * @return `true` if the axis-aligned box is completely or partly inside of the frustum; `false` otherwise
 */
     fun testAab(minX:Float, minY:Float, minZ:Float, maxX:Float, maxY:Float, maxZ:Float):Boolean

companion object {

/**
 * Argument to the first parameter of [.frustumPlane] and
 * [.frustumPlane]
 * identifying the plane with equation <tt>x=-1</tt> when using the identity matrix.
 */
     val PLANE_NX = 0
/**
 * Argument to the first parameter of [.frustumPlane] and
 * [.frustumPlane]
 * identifying the plane with equation <tt>x=1</tt> when using the identity matrix.
 */
     val PLANE_PX = 1
/**
 * Argument to the first parameter of [.frustumPlane] and
 * [.frustumPlane]
 * identifying the plane with equation <tt>y=-1</tt> when using the identity matrix.
 */
     val PLANE_NY = 2
/**
 * Argument to the first parameter of [.frustumPlane] and
 * [.frustumPlane]
 * identifying the plane with equation <tt>y=1</tt> when using the identity matrix.
 */
     val PLANE_PY = 3
/**
 * Argument to the first parameter of [.frustumPlane] and
 * [.frustumPlane]
 * identifying the plane with equation <tt>z=-1</tt> when using the identity matrix.
 */
     val PLANE_NZ = 4
/**
 * Argument to the first parameter of [.frustumPlane] and
 * [.frustumPlane]
 * identifying the plane with equation <tt>z=1</tt> when using the identity matrix.
 */
     val PLANE_PZ = 5
/**
 * Argument to the first parameter of [.frustumCorner]
 * identifying the corner <tt>(-1, -1, -1)</tt> when using the identity matrix.
 */
     val CORNER_NXNYNZ = 0
/**
 * Argument to the first parameter of [.frustumCorner]
 * identifying the corner <tt>(1, -1, -1)</tt> when using the identity matrix.
 */
     val CORNER_PXNYNZ = 1
/**
 * Argument to the first parameter of [.frustumCorner]
 * identifying the corner <tt>(1, 1, -1)</tt> when using the identity matrix.
 */
     val CORNER_PXPYNZ = 2
/**
 * Argument to the first parameter of [.frustumCorner]
 * identifying the corner <tt>(-1, 1, -1)</tt> when using the identity matrix.
 */
     val CORNER_NXPYNZ = 3
/**
 * Argument to the first parameter of [.frustumCorner]
 * identifying the corner <tt>(1, -1, 1)</tt> when using the identity matrix.
 */
     val CORNER_PXNYPZ = 4
/**
 * Argument to the first parameter of [.frustumCorner]
 * identifying the corner <tt>(-1, -1, 1)</tt> when using the identity matrix.
 */
     val CORNER_NXNYPZ = 5
/**
 * Argument to the first parameter of [.frustumCorner]
 * identifying the corner <tt>(-1, 1, 1)</tt> when using the identity matrix.
 */
     val CORNER_NXPYPZ = 6
/**
 * Argument to the first parameter of [.frustumCorner]
 * identifying the corner <tt>(1, 1, 1)</tt> when using the identity matrix.
 */
     val CORNER_PXPYPZ = 7

/**
 * Bit returned by [.properties] to indicate that the matrix represents a perspective transformation.
 */
     val PROPERTY_PERSPECTIVE = (1 shl 0)
/**
 * Bit returned by [.properties] to indicate that the matrix represents an affine transformation.
 */
     val PROPERTY_AFFINE = (1 shl 1)
/**
 * Bit returned by [.properties] to indicate that the matrix represents the identity transformation.
 */
     val PROPERTY_IDENTITY = (1 shl 2)
/**
 * Bit returned by [.properties] to indicate that the matrix represents a pure translation transformation.
 */
     val PROPERTY_TRANSLATION = (1 shl 3)
/**
 * Bit returned by [.properties] to indicate that the upper-left 3x3 submatrix represents an orthogonal
 * matrix (i.e. orthonormal basis). For practical reasons, this property also always implies
 * [.PROPERTY_AFFINE] in this implementation.
 */
     val PROPERTY_ORTHONORMAL = (1 shl 4)
}

}
