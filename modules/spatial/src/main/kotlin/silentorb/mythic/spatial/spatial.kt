package silentorb.mythic.spatial

import org.joml.Math.PI
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

typealias MutableMatrix = Matrix4f
typealias Vector4New = Vector4

private val initialQuaternion = Quaternion()

//operator fun Vector3m.times(other: Matrix): Vector3m = mulDirection(other)
operator fun Vector3m.times(other: Float): Vector3m = mul(other, Vector3m())

operator fun Vector3m.times(other: Vector3m): Vector3m = mul(other, Vector3m())

fun FloatBuffer.put(value: Vector3m) {
  put(value.x)
  put(value.y)
  put(value.z)
}

fun FloatBuffer.put(value: Vector3) {
  put(value.x)
  put(value.y)
  put(value.z)
}

fun FloatBuffer.put(value: Vector4) {
  put(value.x)
  put(value.y)
  put(value.z)
  put(value.w)
}

fun FloatBuffer.put(x: Float, y: Float, z: Float, w: Float) {
  put(x)
  put(y)
  put(z)
  put(w)
}

fun ByteBuffer.putVector3(value: Vector3) {
  putFloat(value.x)
  putFloat(value.y)
  putFloat(value.z)
}

fun ByteBuffer.putVector3m(value: Vector3m) {
  putFloat(value.x)
  putFloat(value.y)
  putFloat(value.z)
}

fun ByteBuffer.putVector4(value: Vector4) {
  putFloat(value.x)
  putFloat(value.y)
  putFloat(value.z)
  putFloat(value.w)
}

const val Pi = PI.toFloat()
const val Pi2 = Pi * 2f
const val quarterAngle = Pi * 0.5f

fun Vector2.toVector3m() = Vector3m(x, y, 0f)
fun Vector2.toVector3() = Vector3(x, y, 0f)

fun isBetween(middle: Float, first: Float, second: Float) =
    if (middle == first || middle == second)
      true
    else if (middle > first)
      middle < second
    else
      middle > second

fun isBetween(middle: Vector3m, first: Vector3m, second: Vector3m) =
    isBetween(middle.x, first.x, second.x)
        && isBetween(middle.y, first.y, second.y)
        && isBetween(middle.z, first.z, second.z)

fun isBetween(middle: Vector2fMinimal, first: Vector2fMinimal, second: Vector2fMinimal) =
    isBetween(middle.x, first.x, second.x)
        && isBetween(middle.y, first.y, second.y)

const val epsilon = 0.00000001f
const val lowResEpsilon = 0.000001f

fun rayPolygonDistance(rayStart: Vector3, rayDirection: Vector3, polygonPoint: Vector3, polygonNormal: Vector3): Float? {
  val denominator = -polygonNormal.dot(rayDirection)
  if (denominator < 1e-6)
    return null

  val foo = polygonPoint - rayStart
  val T = foo.dot(polygonNormal) / denominator
  return if (T <= 0f)
    -T
  else
    null
}

private val flattenedPlaneNormal = Vector3m(0f, 0f, 1f)

fun getSlope(start: Vector2fMinimal, end: Vector2fMinimal): Float {
  val normal = end - start
  return normal.y / normal.x
}

fun isEven(value: Int) = (value and 1) == 0

fun isOdd(value: Int) = (value and 1) != 0

fun isInsidePolygon(point: Vector2fMinimal, vertices: List<Vector2fMinimal>): Boolean {
  var count = simpleRayIntersectsLineSegmentAsNumber(point, vertices.last(), vertices.first())
  for (i in 0 until vertices.size - 1) {
    count += simpleRayIntersectsLineSegmentAsNumber(point, vertices[i], vertices[i + 1])
  }

  return count > 0 && isOdd(count)
}

fun flattenPoints(normal: Vector3m, points: List<Vector3m>): Map<Vector2, Vector3m> {
  val u = Vector3m(normal).cross((points[1] - points[0]).normalize())
  val v = Vector3m(normal).cross(u)
  return points.associate { Pair(Vector2(u.dot(it), v.dot(it)), it) }
}

fun flattenPoints(normal: Vector3, points: List<Vector3>): Map<Vector2, Vector3> {
  val u = normal.cross((points[1] - points[0]).normalize())
  val v = normal.cross(u)
  return points.associate { Pair(Vector2(u.dot(it), v.dot(it)), it) }
}

fun flattenPoints(points: List<Vector3>): Map<Vector2, Vector3> {
  assert(points.size >= 3)
  val normal = (points[0] - points[1]).cross(points[2] - points[1])
  return flattenPoints(normal, points)
}

fun projectPointOntoRay(v: Vector2, u1: Vector2, u2: Vector2): Vector2 {
  val u = u2 - u1
  val relative = u * u.dot(v - u1) / u.dot(u)
  return relative + u1
}

fun projectPointOntoLine(v: Vector2, u1: Vector2, u2: Vector2): Vector2 {
  val u = u2 - u1
  val relative = u * u.dot(v - u1) / u.dot(u)
  return relative + u1
}

fun projectPointFromNormal(normal: Vector3, point: Vector3): Vector3 {
  val rotation = Quaternion().rotationTo(Vector3(0f, 0f, 1f), normal)
  return rotation.transform(point)
}

fun projectPointFromNormal(normal: Vector3, point: Vector2) =
    projectPointFromNormal(normal, Vector3(point.x, point.y, 0f))

fun projectPointOntoLine(v: Vector3, u1: Vector3, u2: Vector3): Vector3 {
  val u = u2 - u1
  val relative = u * u.dot(v - u1) / u.dot(u)
  return relative + u1
}

fun getPointToLineDistance(v: Vector2, u1: Vector2, u2: Vector2): Float {
  val projectedPoint = projectPointOntoLine(v, u1, u2)
  return v.distance(projectedPoint)
}

fun getPointToLineDistance(v: Vector3, u1: Vector3, u2: Vector3): Float {
  val projectedPoint = projectPointOntoLine(v, u1, u2)
  return v.distance(projectedPoint)
}

fun projectPointOnLine(u1: Vector3, u2: Vector3): (Vector3) -> Vector3 {
  val u = u2 - u1
  val mod = u / u.dot(u)
  return { v ->
    val relative = mod * u.dot(v - u1)
    relative + u1
  }
}

fun projectPointOntoLineSegment(v: Vector2, u1: Vector2, u2: Vector2): Vector2? {
  val result = projectPointOntoLine(v, u1, u2)
  if (isBetween(result, u1, u2))
    return result
  else
    return null
}

fun atan(v: Vector2) = //if (v.x < 0)
//  Math.atan2(v.y.toDouble(), v.x.toDouble()).toFloat() - Pi
//else
    Math.atan2(v.y.toDouble(), v.x.toDouble()).toFloat()

fun getAngle(a: Vector2, b: Vector2): Float {
  val ad = atan(a)
  val bd = atan(b)
  assert(!(bd - ad).isNaN())
  return bd - ad
}

//fun getAngle(a: Vector2, b: Vector2, c: Vector2): Float {
//  return getAngle(a - b, c - b)
//}

val Vector4.xyz: Vector3
  get() = Vector3(x, y, z)

fun Vector4.xy(): Vector2 = Vector2(x, y)

val Vector4.zw: Vector2
  get() = Vector2(z, w)

//val Vector4.xy(): Vector2
//  get() = Vector2(x, y)

fun Vector3m.copy() = Vector3m(this)

fun Vector3m.transform(m: Matrix) = m.transform(Vector4(this.x, this.y, this.z, 1f)).xyz

private val tempVector = Vector4()

//fun Vector2.transform(m: Matrix) = m.transform(Vector4(x, y, 0f, 1f)).xy()

fun getCenter(first: Vector3, second: Vector3) =
    (first + second) * 0.5f

//fun getRotationMatrix(matrix: MutableMatrix): MutableMatrix =
//    MutableMatrix().rotation(matrix.getUnnormalizedRotation(initialQuaternion))

fun getRotationMatrix(matrix: Matrix): Matrix =
    toMatrix(MutableMatrix().rotation(toMutableMatrix(matrix).getUnnormalizedRotation(initialQuaternion)))

fun Vector2.toVector2i() = Vector2i(x.toInt(), y.toInt())
fun Vector2i.toVector2() = Vector2(x.toFloat(), y.toFloat())

fun Vector4i.toVector4() = Vector4(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())

fun arrangePointsCounterClockwise2D(center: Vector2, vertices: Collection<Vector2>): List<Vector2> =
    vertices.sortedBy { atan(it - center) }

//fun getCenter(points: Collection<Vector3m>): Vector3m =
//    points.reduce { a, b -> a + b } / points.size.toFloat()

fun getCenter(points: Collection<Vector3>): Vector3 =
    points.reduce { a, b -> a + b } / points.size.toFloat()

fun getCenter2D(points: Collection<Vector2>): Vector2 =
    points.reduce { a, b -> a + b } / points.size.toFloat()

fun arrangePointsCounterClockwise2D(vertices: List<Vector2>): List<Vector2> =
    arrangePointsCounterClockwise2D(getCenter2D(vertices), vertices)

fun arrangePointsCounterClockwise(vertices: List<Vector3>): List<Vector3> {
  val flatMap = flattenPoints(vertices)
  val flatVertices = flatMap.keys
  val sorted = arrangePointsCounterClockwise2D(getCenter2D(flatVertices), flatVertices)
  val result = sorted.map { flatMap[it]!! }
  return result
}

val decimalFormat = DecimalFormat("#.#####")

fun toString(vector: Vector3m) =
    decimalFormat.format(vector.x) + ", " + decimalFormat.format(vector.y) + ", " + decimalFormat.format(vector.z)

fun toString(vector: Vector3) =
    decimalFormat.format(vector.x) + ", " + decimalFormat.format(vector.y) + ", " + decimalFormat.format(vector.z)

fun toString(vectors: List<Vector3m>) =
    vectors.map { toString(it) }.joinToString("\n")

fun toString2(vector: Vector2) =
    decimalFormat.format(vector.x) + ", " + decimalFormat.format(vector.y)

fun toString2(vectors: List<Vector2>) =
    vectors.map { toString2(it) }.joinToString("\n")

fun isZero(vector: Vector3m) =
    vector.x == 0f && vector.y == 0f && vector.z == 0f

//fun rotateToward(matrix: Matrix, dir: Vector3m): Matrix =
//    if (dir.x == 0f && dir.y == 0f)
//      matrix.rotateTowards(dir, Vector3m(0f, 1f, 0f))
//    else
//      matrix.rotateTowards(dir, Vector3m(0f, 0f, 1f))

//fun rotateToward(dir: Vector3m): Quaternion =
////    if (dir.x == 0f && dir.y == 0f)
//    Quaternion().rotateTo(Vector3m(1f, 0f, 0f), dir)
////    else
////      Quaternion().rotateTo(dir, Vector3m(0f, 0f, 1f))

//fun sum(vertices: Collection<Vector3m>): Vector3m {
//  var result = Vector3m()
//  for (vertex in vertices) {
//    result += vertex
//  }
//
//  return result / vertices.size.toFloat()
//}

fun minMax(min: Int, max: Int, value: Int) =
    when {
      value < min -> min
      value > max -> max
      else -> value
    }

fun minMax(value: Float, min: Float, max: Float): Float =
    when {
      value < min -> min
      value > max -> max
      else -> value
    }

fun clipByRange(range: Float, value: Float): Float =
    when {
      value > 0 && value > range -> range
      value < 0 && value < range -> -range
      else -> value
    }

operator fun Vector3i.plus(other: Vector3i): Vector3i = Vector3i(x + other.x, y + other.y, z + other.z)
operator fun Vector3i.plus(other: Int): Vector3i = Vector3i(x + other, y + other, z + other)
//operator fun Vector2f.minus(other: Float): Vector2f = Vector2f(x - other, y - other)

fun Vector3i.toVector3(): Vector3 = Vector3(x.toFloat(), y.toFloat(), z.toFloat())

fun manhattanDistance(a: Vector3, b: Vector3): Float =
    java.lang.Math.abs(a.x - b.x) + java.lang.Math.abs(a.y - b.y)

//private val mk = mutableListOf(0, 0, 0, 0, 0)

fun withinRange(a: Vector3, b: Vector3, range: Float): Boolean =
    a.distance(b) <= range

// Faster than a regular distance check
fun withinRangeFast(a: Vector3, b: Vector3, range: Float): Boolean {
//  assert(a.z == b.z) // This function only works along two dimensions.  What was I thinking?
  val c = abs(a.x - b.x)
  if (c > range) {
    return false
  }

  val d = abs(a.y - b.y)
  if (d > range) {
    return false
  }

  val m = c + d

  return if (m <= range) {
    true
  } else if (m * 0.70710677 <= range) {
    a.distance(b) <= range
  } else {
    false
  }
}

// Faster than a regular distance check
fun withinRangeFast(a: Vector2, b: Vector2, range: Float): Boolean {
//  assert(a.z == b.z) // This function only works along two dimensions.  What was I thinking?
  val c = abs(a.x - b.x)
  if (c > range) {
    return false
  }

  val d = abs(a.y - b.y)
  if (d > range) {
    return false
  }

  val m = c + d

  return if (m <= range) {
    true
  } else if (m * 0.70710677 <= range) {
    a.distance(b) <= range
  } else {
    false
  }
}

fun <T> nearest(items: List<Pair<Vector3, T>>): (Vector3) -> T = { anchor ->
  var result = items.first()
  var distance = result.first.distance(anchor)
  for (item in items.asSequence().drop(1)) {
    if (withinRange(anchor, item.first, distance)) {
      result = item
      distance = item.first.distance(anchor)
    }
  }
  result.second
}

fun <T> nearestFast(items: List<Pair<Vector3, T>>): (Vector3) -> T = { anchor ->
  var result = items.first()
  var distance = result.first.distance(anchor)
  for (item in items.asSequence().drop(1)) {
    if (withinRangeFast(anchor, item.first, distance)) {
      result = item
      distance = item.first.distance(anchor)
    }
  }
  result.second
}

fun <T> nearestFast2d(items: List<Pair<Vector2, T>>): (Vector2) -> T = { anchor ->
  var result = items.first()
  var distance = result.first.distance(anchor)
  for (item in items.asSequence().drop(1)) {
    if (withinRangeFast(anchor, item.first, distance)) {
      result = item
      distance = item.first.distance(anchor)
    }
  }
  result.second
}

fun projectVector3(angle: Float, radius: Float, z: Float): Vector3 {
  return Vector3(cos(angle) * radius, sin(angle) * radius, 0f)
}

fun createArcZ(radius: Float, count: Int, sweep: Float = Pi * 2, offset: Float = 0f): List<Vector3> {
  val increment = sweep / count
  return (0 until count)
      .map { i ->
        val theta = increment * i + offset
        val x = if (sweep == Pi && i == count - 1)
          0f
        else
          cos(theta) * radius

        val y = sin(theta) * radius

        Vector3(x, y, 0f)
      }
}

fun radiansToDegrees(radians: Float): Float =
    radians * 180f / Pi

fun degreesToRadians(degrees: Float): Float =
    degrees * Pi / 180f

fun radiansToDegrees(value: Vector3): Vector3 =
    Vector3(
        radiansToDegrees(value.x),
        radiansToDegrees(value.y),
        radiansToDegrees(value.z)
    )

fun degreesToRadians(value: Vector3): Vector3 =
    Vector3(
        degreesToRadians(value.x),
        degreesToRadians(value.y),
        degreesToRadians(value.z)
    )

fun getAngleCourse(source: Float, destination: Float): Float {
  val full = Pi * 2
  val a = normalizeRadialAngle(source)
  val b = normalizeRadialAngle(destination)
  if (a == b)
    return 0f

  val plus = normalizeRadialAngle(full + destination - source)
  val minus = normalizeRadialAngle(full + source - destination)
  return if (plus < minus)
    plus
  else
    -minus
}

fun getAngleGap(a: Float, b: Float): Float =
    abs(getAngleCourse(a, b))

fun getYawAngle(lookAt: Vector2fMinimal): Float =
    getAngle(Vector2(1f, 0f), lookAt.xy())

fun getPitchAngle(lookAt: Vector3) =
    getAngle(Vector2(1f, 0f), Vector2(lookAt.xy().length(), lookAt.z))

fun getYawAndPitch(lookAt: Vector3): Vector2 =
    Vector2(
        getYawAngle(lookAt),
        getPitchAngle(lookAt)
    )

fun horizontalFacingDistance(angle: Float, lookAt: Vector3): Float {
  val secondAngle = getYawAngle(lookAt)
  return getAngleCourse(angle, secondAngle)
}

fun verticalFacingDistance(angle: Float, lookAt: Vector3): Float {
  val secondAngle = getPitchAngle(lookAt)
  return getAngleCourse(angle, secondAngle)
}

tailrec fun normalizeRadialAngle(angle: Float): Float {
  val cycle = Pi * 2f
  return when {
    angle > cycle -> normalizeRadialAngle(angle - cycle)
    angle < 0f -> normalizeRadialAngle(angle + cycle)
    else -> angle
  }
}

fun normalizeRadialAngles(angles: Vector3): Vector3 =
    Vector3(
        normalizeRadialAngle(angles.x),
        normalizeRadialAngle(angles.y),
        normalizeRadialAngle(angles.z),
    )

fun transformToScreenRaw(transform: Matrix, target: Vector3): Vector4 =
    transform * Vector4(target.x, target.y, target.z, 1f)

fun transformToScreenIncludingBehind(transform: Matrix, target: Vector3): Vector2 {
  val coordinate = transformToScreenRaw(transform, target)
  return coordinate.xy() / coordinate.w
}

fun transformToScreen(transform: Matrix, target: Vector3): Vector2? {
  val coordinate = transformToScreenRaw(transform, target)

  // The w condition filters out targets behind the camera
  return if (coordinate.w > 0f)
    coordinate.xy() / coordinate.w
  else
    null
}
