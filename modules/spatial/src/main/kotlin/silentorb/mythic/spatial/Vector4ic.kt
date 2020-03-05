package silentorb.mythic.spatial

interface Vector4ic {
  /**
   * @return the value of the x component
   */
  fun x(): Int

  /**
   * @return the value of the y component
   */
  fun y(): Int

  /**
   * @return the value of the z component
   */
  fun z(): Int

  /**
   * @return the value of the w component
   */
  fun w(): Int

  /**
   * Subtract the supplied vector from this one and store the result in
   * `dest`.
   *
   * @param v
   * the vector to subtract from `this`
   * @param dest
   * will hold the result
   * @return dest
   */
  fun sub(v: Vector4ic?, dest: Vector4i?): Vector4i?

  /**
   * Subtract <tt>(x, y, z, w)</tt> from this and store the result in
   * `dest`.
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
  fun sub(x: Int, y: Int, z: Int, w: Int, dest: Vector4i?): Vector4i?

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
  fun add(v: Vector4ic?, dest: Vector4i?): Vector4i?

  /**
   * Increment the components of this vector by the given values and store the
   * result in `dest`.
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
  fun add(x: Int, y: Int, z: Int, w: Int, dest: Vector4i?): Vector4i?

  /**
   * Multiply this Vector4i component-wise by another Vector4ic and store the
   * result in `dest`.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(v: Vector4ic?, dest: Vector4i?): Vector4i?

  /**
   * Divide this Vector4i component-wise by another Vector4ic and store the
   * result in `dest`.
   *
   * @param v
   * the vector to divide by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun div(v: Vector4ic?, dest: Vector4i?): Vector4i?

  /**
   * Multiply all components of this [Vector4i] by the given scalar
   * value and store the result in `dest`.
   *
   * @param scalar
   * the scalar to multiply by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun mul(scalar: Float, dest: Vector4i?): Vector4i?

  /**
   * Divide all components of this [Vector4i] by the given scalar value
   * and store the result in `dest`.
   *
   * @param scalar
   * the scalar to divide by
   * @param dest
   * will hold the result
   * @return dest
   */
  fun div(scalar: Float, dest: Vector4i?): Vector4i?

  /**
   * Return the length squared of this vector.
   *
   * @return the length squared
   */
  fun lengthSquared(): Long

  /**
   * Return the length of this vector.
   *
   * @return the length
   */
  fun length(): Double

  /**
   * Return the distance between this Vector and `v`.
   *
   * @param v
   * the other vector
   * @return the distance
   */
  fun distance(v: Vector4ic?): Double

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
  fun distance(x: Int, y: Int, z: Int, w: Int): Double

  /**
   * Return the square of the distance between this vector and `v`.
   *
   * @param v
   * the other vector
   * @return the squared of the distance
   */
  fun distanceSquared(v: Vector4ic?): Int

  /**
   * Return the square of the distance between `this` vector and
   * <tt>(x, y, z, w)</tt>.
   *
   * @param x
   * the x component of the other vector
   * @param y
   * the y component of the other vector
   * @param z
   * the z component of the other vector
   * @param w
   * the w component of the other vector
   * @return the square of the distance
   */
  fun distanceSquared(x: Int, y: Int, z: Int, w: Int): Int

  /**
   * Compute the dot product (inner product) of this vector and `v`.
   *
   * @param v
   * the other vector
   * @return the dot product
   */
  fun dot(v: Vector4ic?): Int

  /**
   * Negate this vector and store the result in `dest`.
   *
   * @param dest
   * will hold the result
   * @return dest
   */
  fun negate(dest: Vector4i?): Vector4i?

  /**
   * Set the components of `dest` to be the component-wise minimum of this and the other vector.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun min(v: Vector4ic?, dest: Vector4i?): Vector4i?

  /**
   * Set the components of `dest` to be the component-wise maximum of this and the other vector.
   *
   * @param v
   * the other vector
   * @param dest
   * will hold the result
   * @return dest
   */
  fun max(v: Vector4ic?, dest: Vector4i?): Vector4i?

  /**
   * Get the value of the specified component of this vector.
   *
   * @param component
   * the component, within <tt>[0..3]</tt>
   * @return the value
   * @throws IllegalArgumentException if `component` is not within <tt>[0..3]</tt>
   */
  @Throws(IllegalArgumentException::class)
  operator fun get(component: Int): Int
}
