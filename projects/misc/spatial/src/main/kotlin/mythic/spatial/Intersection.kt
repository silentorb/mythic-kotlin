package mythic.spatial

import org.joml.Math
import org.joml.Vector2fMinimal

fun lineIntersectsCircle(lineStart: Vector2, lineEnd: Vector2, circleCenter: Vector2, radius: Float): Boolean {
  val d = lineEnd - lineStart
  val f = lineStart - circleCenter
  val a = d.dot(d)
  val b = 2f * f.dot(d)
  val c = f.dot(f) - radius * radius

  var discriminant = b * b - 4f * a * c
  if (discriminant < 0) {
    return false
  } else {
    // ray didn't totally miss sphere,
    // so there is a solution to
    // the equation.

    discriminant = Math.sqrt(discriminant.toDouble()).toFloat()

    // either solution may be on or off the ray so need to test both
    // t1 is always the smaller value, because BOTH discriminant and
    // a are nonnegative.
    val t1 = (-b - discriminant) / (2 * a)
    val t2 = (-b + discriminant) / (2 * a)

    // 3x HIT cases:
    //          -o->             --|-->  |            |  --|->
    // Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit),

    // 3x MISS cases:
    //       ->  o                     o ->              | -> |
    // FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)

    if (t1 >= 0 && t1 <= 1) {
      // t1 is the intersection, and it's closer than t2
      // (since t1 uses -b - discriminant)
      // Impale, Poke
      return true
    }

    // here t1 didn't intersect so we are either started
    // inside the sphere or completely past it
    return if (t2 >= 0 && t2 <= 1) {
      // ExitWound
      true
    } else false

    // no intn: FallShort, Past, CompletelyInside
  }
}

fun lineIntersectsSphere(lineStart: Vector3m, lineEnd: Vector3m, circleCenter: Vector3m, radius: Float): Boolean {
  val d = lineEnd - lineStart
  val f = lineStart - circleCenter
  val a = d.dot(d)
  val b = 2f * f.dot(d)
  val c = f.dot(f) - radius * radius

  var discriminant = b * b - 4f * a * c
  if (discriminant < 0) {
    return false
  } else {
    // ray didn't totally miss sphere,
    // so there is a solution to
    // the equation.

    discriminant = Math.sqrt(discriminant.toDouble()).toFloat()

    // either solution may be on or off the ray so need to test both
    // t1 is always the smaller value, because BOTH discriminant and
    // a are nonnegative.
    val t1 = (-b - discriminant) / (2 * a)
    val t2 = (-b + discriminant) / (2 * a)

    // 3x HIT cases:
    //          -o->             --|-->  |            |  --|->
    // Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit),

    // 3x MISS cases:
    //       ->  o                     o ->              | -> |
    // FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)

    if (t1 >= 0 && t1 <= 1) {
      // t1 is the intersection, and it's closer than t2
      // (since t1 uses -b - discriminant)
      // Impale, Poke
      return true
    }

    // here t1 didn't intersect so we are either started
    // inside the sphere or completely past it
    return if (t2 >= 0 && t2 <= 1) {
      // ExitWound
      true
    } else false

    // no intn: FallShort, Past, CompletelyInside
  }
}

fun rayIntersectsSphere(lineStart: Vector3, lineEnd: Vector3, circleCenter: Vector3, radius: Float): Boolean {
  val d = lineEnd - lineStart
  val f = lineStart - circleCenter
  val a = d.dot(d)
  val b = 2f * f.dot(d)
  val c = f.dot(f) - radius * radius

  val discriminant = b * b - 4f * a * c
  if (discriminant < 0) {
    return false
  } else {
    return true
  }
}

// If successful returns the closest point of intersection on the line.
fun rayIntersectsLine3D(p1: Vector3, p2: Vector3, p3: Vector3, p4: Vector3, maxGap: Float): Vector3? {
  val p13 = p1 - p3
  val p43 = p4 - p3

  if (p43.lengthSquared() < epsilon) {
    return null
  }
  val p21 = p2 - p1
  if (p21.lengthSquared() < epsilon) {
    return null
  }

  val d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z
  val d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z
  val d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z
  val d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z
  val d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z

  val denom = d2121 * d4343 - d4321 * d4321
  if (Math.abs(denom) < epsilon) {
    return null
  }
  val numer = d1343 * d4321 - d1321 * d4343

  val mua = numer / denom
  val mub = (d1343 + d4321 * mua) / d4343

  val result1 = p1 + p21 * mua
  val result2 = p3 + p43 * mub

  if (result1.distance(result2) > maxGap)
    return null

  if (!isBetween(result2, p3, p4))
    return null

  return result2
}

fun simpleRayIntersectsLineSegment(rayStart: Vector2fMinimal, segmentStart: Vector2fMinimal, segmentEnd: Vector2fMinimal): Vector2? {
  if (segmentStart.y == segmentEnd.y)
    return null

  if (!isBetween(rayStart.y, segmentStart.y, segmentEnd.y))
    return null

  val x = if (segmentStart.x == segmentEnd.x) {
    segmentStart.x
  } else {
    val slope = getSlope(segmentStart, segmentEnd)
    (rayStart.y - segmentStart.y) / slope + segmentStart.x
  }

  return if (x >= rayStart.x && isBetween(x, segmentStart.x, segmentEnd.x))
    Vector2(x, rayStart.y)
  else
    null
}

//fun lineSegmentIntersectsLineSegment(f1: Vector2fMinimal, f2: Vector2fMinimal, s1: Vector2fMinimal, s2: Vector2fMinimal): Vector2? {
//  val point = if (f1.x == f2.x || f1.y == f2.y || s1.x == s2.x || s1.y == s2.y) {
//    throw Error("Not yet implemented.")
//  } else {
//    val fSlope = getSlope(s1, s2)
//    val sSlope = getSlope(f1, f2)
//    val fOffset = f1.y - f1.x * fSlope
//    val sOffset = s1.y - s1.x * sSlope
//    val x = (sOffset - fOffset) / (fSlope - sSlope)
//    val y = s1.x * sSlope + sOffset
//    Vector2(x, y)
//  }
//
//  return if (isBetween(point, f1, f2) && isBetween(point, s1, s2))
//    point
//  else
//    null
//}

private infix fun Vector2.cross(b: Vector2): Float =
    x * b.y - y * b.x

class LineF(val s: Vector2fMinimal, val e: Vector2fMinimal)

fun findIntersection(l1: LineF, l2: LineF): Vector2? {
  val a1 = l1.e.y - l1.s.y
  val b1 = l1.s.x - l1.e.x
  val c1 = a1 * l1.s.x + b1 * l1.s.y

  val a2 = l2.e.y - l2.s.y
  val b2 = l2.s.x - l2.e.x
  val c2 = a2 * l2.s.x + b2 * l2.s.y

  val delta = a1 * b2 - a2 * b1
  if (delta == 0f)
    return null

  // If lines are parallel, intersection point will contain infinite values
  return Vector2((b2 * c1 - b1 * c2) / delta, (a1 * c2 - a2 * c1) / delta)
}

fun getLineAndLineIntersection(f1: Vector2fMinimal, f2: Vector2fMinimal, s1: Vector2fMinimal, s2: Vector2fMinimal): Vector2? {
  return findIntersection(LineF(f1, f2), LineF(s1, s2))
}

//  fun getLineAndLineIntersection(f1: Vector2fMinimal, f2: Vector2fMinimal, s1: Vector2fMinimal, s2: Vector2fMinimal): Vector2? {
//  val a1 = f2.y - f1.y
//  val b1 = f2.x - f1.x
//
//  val a2 = s2.y - s1.y
//  val b2 = s2.x - s1.x
//
//  val delta = a1 * b2 - a2 * b1
//
//  if (delta == 0f)
//    return null
//
//  val c1 = a1 * f1.x + b1 * f1.y
//  val c2 = a2 * s1.x + b2 * s1.y
//
//  val x = (b2 * c1 - b1 * c2) / delta
//  val y = (a1 * c2 - a2 * c1) / delta
//  return Vector2(x, y)
//}

fun lineSegmentIntersectsLineSegment(f1: Vector2fMinimal, f2: Vector2fMinimal, s1: Vector2fMinimal, s2: Vector2fMinimal): Pair<Boolean, Vector2?> {
  val p = f1.xy()
  val q = s1.xy()
  val r = (f2 - f1).xy()
  val s = (s2 - s1).xy()
  val startGap = q - p
  val vectorCross = (r cross s)

  val m = vectorCross
  val n = startGap cross r

  if (m == 0f && n != 0f) {
    return Pair(false, null)
  }

  if (m == 0f && n == 0f) {
    val k = r / r.dot(r)
    val t0 = startGap.dot(k)
    val t1 = t0 + s.dot(k)
    return if ((t0 < 1f && t1 > 0f) || (t1 < 1f && t0 > 0f))
      Pair(true, null)
    else
      Pair(false, null)
  }

  val t = startGap cross (s / vectorCross)
  val u = startGap cross (r / vectorCross)
  if (r cross s != 0f && 0f <= t && t <= 1f && 0f <= u && u <= 1f) {
    val point = p + r * t
    return Pair(true, point)
  }
  return Pair(false, null)
}

fun simpleRayIntersectsLineSegmentAsNumber(rayStart: Vector2fMinimal, segmentStart: Vector2fMinimal, segmentEnd: Vector2fMinimal): Int {
  val result = simpleRayIntersectsLineSegment(rayStart, segmentStart, segmentEnd)
  return if (result != null) 1 else 0
}

fun transformPoints(vertices: List<Vector3>, polygonNormal: Vector3,
                    planeIntersection: Vector3): Pair<List<Vector2>, Vector2> {
  val u = polygonNormal.cross((vertices[1] - vertices[0].normalize()))
  val v = polygonNormal.cross(u)
  val transformedPoints = vertices.map { Vector2(u.dot(it), v.dot(it)) }
  val transformedIntersection = Vector2(u.dot(planeIntersection), v.dot(planeIntersection))
  return Pair(transformedPoints, transformedIntersection)
}

fun rayIntersectsPolygon3D(rayStart: Vector3, rayDirection: Vector3, vertices: List<Vector3>, polygonNormal: Vector3): Vector3? {
  val distance = rayPolygonDistance(rayStart, rayDirection, vertices.first(), polygonNormal)
      ?: rayPolygonDistance(rayStart, rayDirection, vertices.first(), -polygonNormal)
  if (distance == null)
    return null

  val planeIntersection = rayStart + rayDirection * distance
  val (transformedPoints, transformedIntersection) = transformPoints(vertices, polygonNormal, planeIntersection)

  return if (isInsidePolygon(transformedIntersection, transformedPoints))
    planeIntersection
  else
    null
}
