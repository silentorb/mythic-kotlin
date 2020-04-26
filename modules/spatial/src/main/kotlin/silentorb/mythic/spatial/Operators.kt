package silentorb.mythic.spatial

import org.joml.*


/* Matrix3d */

//operator fun Matrix3d.get(c: Int, r: Int): Double = get(c, r)
//operator fun Matrix3d.minus(m: Matrix3d) = sub(m)
//operator fun Matrix3d.plus(m: Matrix3dc) = add(m)
//operator fun Matrix3d.times(m: Matrix3dc) = mul(m)
//operator fun Matrix3d.times(m: Matrix3fc) = mul(m)
//operator fun Matrix3d.times(v: Vector3d) = transform(v)
//operator fun Matrix3d.times(v: Vector3m) = transform(v)
//operator fun Matrix3d.times(q: Quaternionfc) = rotate(q)
//operator fun Matrix3d.times(q: Quaterniondc) = rotate(q)
//infix fun Matrix3d.rotate(q: Quaternionfc) = rotate(q)
//infix fun Matrix3d.rotate(q: Quaterniondc) = rotate(q)
//infix fun Matrix3d.transform(v: Vector3m) = transform(v)
//infix fun Matrix3d.transform(v: Vector3d) = transform(v)


/* Matrix4x3d */

//operator fun Matrix4x3d.get(c: Int, r: Int): Double = get(c, r)
//operator fun Matrix4x3d.minus(m: Matrix4x3dc) = sub(m)
//operator fun Matrix4x3d.plus(m: Matrix4x3dc) = add(m)
//operator fun Matrix4x3d.times(m: Matrix4x3fc) = mul(m)
//operator fun Matrix4x3d.times(m: Matrix4x3dc) = mul(m)
//operator fun Matrix4x3d.times(v: Vector4d) = transform(v)
//operator fun Matrix4x3d.times(q: Quaternionfc) = rotate(q)
//operator fun Matrix4x3d.times(q: Quaterniondc) = rotate(q)
//infix fun Matrix4x3d.rotate(q: Quaternionfc) = rotate(q)
//infix fun Matrix4x3d.rotate(q: Quaterniondc) = rotate(q)
//infix fun Matrix4x3d.transform(v: Vector4d) = transform(v)
//infix fun Matrix4x3d.transformPosition(v: Vector3d) = transformPosition(v)
//infix fun Matrix4x3d.transformDirection(v: Vector3d) = transformDirection(v)

/* Matrix4f */

operator fun Matrix4f.get(c: Int, r: Int): Float = get(c, r)
operator fun Matrix4f.minus(m: Matrix4f) = sub(m)
operator fun Matrix4f.plus(m: Matrix4f) = add(m)
operator fun Matrix4f.times(v: Vector4) = transform(v)
infix fun Matrix4f.mulAffine(m: Matrix4f) = this.mulAffine(m)
infix fun Matrix4f.mulAffineR(m: Matrix4f) = this.mulAffineR(m)
infix fun Matrix4f.transform(v: Vector4) = transform(v)
infix fun Matrix4f.transformPosition(v: Vector3m) = transformPosition(v)
infix fun Matrix4f.transformDirection(v: Vector3m) = transformDirection(v)

/* Vector2f */

operator fun Vector2f.plus(v: Float) = Vector2f(x + v, y + v)
operator fun Vector2f.unaryMinus() = negate()
operator fun Vector2f.div(v: Float) = Vector2f(x / v, y / v)
operator fun Vector2f.div(v: Vector2f) = Vector2f(x / v.x, y / v.y)

//operator fun Vector2i.get(e: Int): Int = get(e)
operator fun Vector2i.minus(v: Vector2i) = Vector2i(x - v.x, y - v.y)
operator fun Vector2i.plus(v: Int) = Vector2i(x + v, y + v)
operator fun Vector2i.unaryMinus() = negate()
operator fun Vector2i.div(v: Int) = Vector2i(x / v, y / v)

/* Vector3m */

operator fun Vector3m.minus(v: Float) = Vector3m(x - v, y - v, z - v)


operator fun Vector3m.plus(v: Vector3f): Vector3m = Vector3m(x + v.x, y + v.y, z + v.z)
operator fun Vector3m.plus(v: Float): Vector3m = Vector3m(x + v, y + v, z + v)
operator fun Vector3m.unaryMinus() = Vector3m(this).negate()

operator fun Vector4.plus(v: Float): Vector4 = Vector4(x + v, y + v, z + v, w + v)
operator fun Vector4i.plus(v: Int): Vector4i = Vector4i(x + v, y + v, z + v, w + v)

fun Vector4.toVector4i() = Vector4i(x.toInt(), y.toInt(), z.toInt(), w.toInt())

/* Vector4 */

//operator fun Vector4.get(e: Int): Float = get(e)
operator fun Vector4.minus(v: Vector4) = sub(v)

operator fun Vector4.plus(v: Vector4) = add(v)
operator fun Vector4.unaryMinus() = negate()

/* Quaternion */

//operator fun Quaternion.get(e: Int): Float = get(e)
//operator fun Quaternion.unaryMinus() = conjugate()
//operator fun Quaternion.times(v: Vector3m) = transform(v, Vector3m())
//operator fun Quaternion.times(v: Vector3) = transform(v)
//operator fun Quaternion.times(q: Quaternion) = mul(q, Quaternion())
//operator fun Quaternion.times(v: Vector4) = transform(v)

//val Vector3m.xy(): Vector2f
//  get() = Vector2f(x, y)

val Vector4i.zw: Vector2i
  get() = Vector2i(z, w)

