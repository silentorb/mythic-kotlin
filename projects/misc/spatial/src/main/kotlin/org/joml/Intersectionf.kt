/*
 * (C) Copyright 2015-2018 Kai Burjack

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

/**
 * Contains intersection and distance tests for some 2D and 3D geometric primitives.
 *
 * @author Kai Burjack
 */
object Intersectionf {

  /**
   * Return value of
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle] and
   * [.findClosestPointOnTriangle] or
   * [.intersectSweptSphereTriangle]
   * to signal that the closest point is the first vertex of the triangle.
   */
  val POINT_ON_TRIANGLE_VERTEX_0 = 1
  /**
   * Return value of
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle] and
   * [.findClosestPointOnTriangle] or
   * [.intersectSweptSphereTriangle]
   * to signal that the closest point is the second vertex of the triangle.
   */
  val POINT_ON_TRIANGLE_VERTEX_1 = 2
  /**
   * Return value of
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle] and
   * [.findClosestPointOnTriangle] or
   * [.intersectSweptSphereTriangle]
   * to signal that the closest point is the third vertex of the triangle.
   */
  val POINT_ON_TRIANGLE_VERTEX_2 = 3

  /**
   * Return value of
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle] and
   * [.findClosestPointOnTriangle] or
   * [.intersectSweptSphereTriangle]
   * to signal that the closest point lies on the edge between the first and second vertex of the triangle.
   */
  val POINT_ON_TRIANGLE_EDGE_01 = 4
  /**
   * Return value of
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle] and
   * [.findClosestPointOnTriangle] or
   * [.intersectSweptSphereTriangle]
   * to signal that the closest point lies on the edge between the second and third vertex of the triangle.
   */
  val POINT_ON_TRIANGLE_EDGE_12 = 5
  /**
   * Return value of
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle] and
   * [.findClosestPointOnTriangle] or
   * [.intersectSweptSphereTriangle]
   * to signal that the closest point lies on the edge between the third and first vertex of the triangle.
   */
  val POINT_ON_TRIANGLE_EDGE_20 = 6

  /**
   * Return value of
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle],
   * [.findClosestPointOnTriangle] and
   * [.findClosestPointOnTriangle] or
   * [.intersectSweptSphereTriangle]
   * to signal that the closest point lies on the face of the triangle.
   */
  val POINT_ON_TRIANGLE_FACE = 2

  /**
   * Return value of [.intersectRayAar] and
   * [.intersectRayAar]
   * to indicate that the ray intersects the side of the axis-aligned rectangle with the minimum x coordinate.
   */
  val AAR_SIDE_MINX = 0
  /**
   * Return value of [.intersectRayAar] and
   * [.intersectRayAar]
   * to indicate that the ray intersects the side of the axis-aligned rectangle with the minimum y coordinate.
   */
  val AAR_SIDE_MINY = 1
  /**
   * Return value of [.intersectRayAar] and
   * [.intersectRayAar]
   * to indicate that the ray intersects the side of the axis-aligned rectangle with the maximum x coordinate.
   */
  val AAR_SIDE_MAXX = 2
  /**
   * Return value of [.intersectRayAar] and
   * [.intersectRayAar]
   * to indicate that the ray intersects the side of the axis-aligned rectangle with the maximum y coordinate.
   */
  val AAR_SIDE_MAXY = 3

  /**
   * Return value of [.intersectLineSegmentAab] and
   * [.intersectLineSegmentAab] to indicate that the line segment does not intersect the axis-aligned box;
   * or return value of [.intersectLineSegmentAar] and
   * [.intersectLineSegmentAar] to indicate that the line segment does not intersect the axis-aligned rectangle.
   */
  val OUTSIDE = -1
  /**
   * Return value of [.intersectLineSegmentAab] and
   * [.intersectLineSegmentAab] to indicate that one end point of the line segment lies inside of the axis-aligned box;
   * or return value of [.intersectLineSegmentAar] and
   * [.intersectLineSegmentAar] to indicate that one end point of the line segment lies inside of the axis-aligned rectangle.
   */
  val ONE_INTERSECTION = 1
  /**
   * Return value of [.intersectLineSegmentAab] and
   * [.intersectLineSegmentAab] to indicate that the line segment intersects two sides of the axis-aligned box
   * or lies on an edge or a side of the box;
   * or return value of [.intersectLineSegmentAar] and
   * [.intersectLineSegmentAar] to indicate that the line segment intersects two edges of the axis-aligned rectangle
   * or lies on an edge of the rectangle.
   */
  val TWO_INTERSECTION = 2
  /**
   * Return value of [.intersectLineSegmentAab] and
   * [.intersectLineSegmentAab] to indicate that the line segment lies completely inside of the axis-aligned box;
   * or return value of [.intersectLineSegmentAar] and
   * [.intersectLineSegmentAar] to indicate that the line segment lies completely inside of the axis-aligned rectangle.
   */
  val INSIDE = 3

  /**
   * Test whether the plane with the general plane equation *a*x + b*y + c*z + d = 0* intersects the sphere with center
   * <tt>(centerX, centerY, centerZ)</tt> and `radius`.
   *
   *
   * Reference: [http://math.stackexchange.com](http://math.stackexchange.com/questions/943383/determine-circle-of-intersection-of-plane-and-sphere)
   *
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @param centerX
   * the x coordinate of the sphere's center
   * @param centerY
   * the y coordinate of the sphere's center
   * @param centerZ
   * the z coordinate of the sphere's center
   * @param radius
   * the radius of the sphere
   * @return `true` iff the plane intersects the sphere; `false` otherwise
   */
  fun testPlaneSphere(
      a: Float, b: Float, c: Float, d: Float,
      centerX: Float, centerY: Float, centerZ: Float, radius: Float): Boolean {
    val denom = Math.sqrt((a * a + b * b + c * c).toDouble()).toFloat()
    val dist = (a * centerX + b * centerY + c * centerZ + d) / denom
    return -radius <= dist && dist <= radius
  }

  /**
   * Test whether the given plane intersects the given sphere with center.
   *
   *
   * Reference: [http://math.stackexchange.com](http://math.stackexchange.com/questions/943383/determine-circle-of-intersection-of-plane-and-sphere)
   *
   * @param plane
   * the plane
   * @param sphere
   * the sphere
   * @return `true` iff the plane intersects the sphere; `false` otherwise
   */
  fun testPlaneSphere(plane: Planef, sphere: Spheref): Boolean {
    return testPlaneSphere(plane.a, plane.b, plane.c, plane.d, sphere.x, sphere.y, sphere.z, sphere.r)
  }

  /**
   * Test whether the plane with the general plane equation *a*x + b*y + c*z + d = 0* intersects the sphere with center
   * <tt>(centerX, centerY, centerZ)</tt> and `radius`, and store the center of the circle of
   * intersection in the <tt>(x, y, z)</tt> components of the supplied vector and the radius of that circle in the w component.
   *
   *
   * Reference: [http://math.stackexchange.com](http://math.stackexchange.com/questions/943383/determine-circle-of-intersection-of-plane-and-sphere)
   *
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @param centerX
   * the x coordinate of the sphere's center
   * @param centerY
   * the y coordinate of the sphere's center
   * @param centerZ
   * the z coordinate of the sphere's center
   * @param radius
   * the radius of the sphere
   * @param intersectionCenterAndRadius
   * will hold the center of the circle of intersection in the <tt>(x, y, z)</tt> components and the radius in the w component
   * @return `true` iff the plane intersects the sphere; `false` otherwise
   */
  fun intersectPlaneSphere(
      a: Float, b: Float, c: Float, d: Float,
      centerX: Float, centerY: Float, centerZ: Float, radius: Float,
      intersectionCenterAndRadius: Vector4f): Boolean {
    val invDenom = 1.0f / Math.sqrt((a * a + b * b + c * c).toDouble()).toFloat()
    val dist = (a * centerX + b * centerY + c * centerZ + d) * invDenom
    if (-radius <= dist && dist <= radius) {
      intersectionCenterAndRadius.x = centerX + dist * a * invDenom
      intersectionCenterAndRadius.y = centerY + dist * b * invDenom
      intersectionCenterAndRadius.z = centerZ + dist * c * invDenom
      intersectionCenterAndRadius.w = Math.sqrt((radius * radius - dist * dist).toDouble()).toFloat()
      return true
    }
    return false
  }

  /**
   * Test whether the plane with the general plane equation *a*x + b*y + c*z + d = 0* intersects the moving sphere with center
   * <tt>(cX, cY, cZ)</tt>, `radius` and velocity <tt>(vX, vY, vZ)</tt>, and store the point of intersection
   * in the <tt>(x, y, z)</tt> components of the supplied vector and the time of intersection in the w component.
   *
   *
   * The normal vector <tt>(a, b, c)</tt> of the plane equation needs to be normalized.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 5.5.3 "Intersecting Moving Sphere Against Plane"
   *
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @param cX
   * the x coordinate of the center position of the sphere at t=0
   * @param cY
   * the y coordinate of the center position of the sphere at t=0
   * @param cZ
   * the z coordinate of the center position of the sphere at t=0
   * @param radius
   * the sphere's radius
   * @param vX
   * the x component of the velocity of the sphere
   * @param vY
   * the y component of the velocity of the sphere
   * @param vZ
   * the z component of the velocity of the sphere
   * @param pointAndTime
   * will hold the point and time of intersection (if any)
   * @return `true` iff the sphere intersects the plane; `false` otherwise
   */
  fun intersectPlaneSweptSphere(
      a: Float, b: Float, c: Float, d: Float,
      cX: Float, cY: Float, cZ: Float, radius: Float,
      vX: Float, vY: Float, vZ: Float,
      pointAndTime: Vector4f): Boolean {
    // Compute distance of sphere center to plane
    val dist = a * cX + b * cY + c * cZ - d
    if (Math.abs(dist) <= radius) {
      // The sphere is already overlapping the plane. Set time of
      // intersection to zero and q to sphere center
      pointAndTime[cX, cY, cZ] = 0.0f
      return true
    }
    val denom = a * vX + b * vY + c * vZ
    if (denom * dist >= 0.0f) {
      // No intersection as sphere moving parallel to or away from plane
      return false
    }
    // Sphere is moving towards the plane
    // Use +r in computations if sphere in front of plane, else -r
    val r = if (dist > 0.0f) radius else -radius
    val t = (r - dist) / denom
    pointAndTime[cX + t * vX - r * a, cY + t * vY - r * b, cZ + t * vZ - r * c] = t
    return true
  }

  /**
   * Test whether the plane with the general plane equation *a*x + b*y + c*z + d = 0* intersects the sphere moving from center
   * position <tt>(t0X, t0Y, t0Z)</tt> to <tt>(t1X, t1Y, t1Z)</tt> and having the given `radius`.
   *
   *
   * The normal vector <tt>(a, b, c)</tt> of the plane equation needs to be normalized.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 5.5.3 "Intersecting Moving Sphere Against Plane"
   *
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @param t0X
   * the x coordinate of the start position of the sphere
   * @param t0Y
   * the y coordinate of the start position of the sphere
   * @param t0Z
   * the z coordinate of the start position of the sphere
   * @param r
   * the sphere's radius
   * @param t1X
   * the x coordinate of the end position of the sphere
   * @param t1Y
   * the y coordinate of the end position of the sphere
   * @param t1Z
   * the z coordinate of the end position of the sphere
   * @return `true` if the sphere intersects the plane; `false` otherwise
   */
  fun testPlaneSweptSphere(
      a: Float, b: Float, c: Float, d: Float,
      t0X: Float, t0Y: Float, t0Z: Float, r: Float,
      t1X: Float, t1Y: Float, t1Z: Float): Boolean {
    // Get the distance for both a and b from plane p
    val adist = t0X * a + t0Y * b + t0Z * c - d
    val bdist = t1X * a + t1Y * b + t1Z * c - d
    // Intersects if on different sides of plane (distances have different signs)
    if (adist * bdist < 0.0f) return true
    // Intersects if start or end position within radius from plane
    return if (Math.abs(adist) <= r || Math.abs(bdist) <= r) true else false
    // No intersection
  }

  /**
   * Test whether the axis-aligned box with minimum corner <tt>(minX, minY, minZ)</tt> and maximum corner <tt>(maxX, maxY, maxZ)</tt>
   * intersects the plane with the general equation *a*x + b*y + c*z + d = 0*.
   *
   *
   * Reference: [http://www.lighthouse3d.com](http://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-testing-boxes-ii/) ("Geometric Approach - Testing Boxes II")
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
   * the z coordinate of the maximum corner of the axis-aligned box
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @return `true` iff the axis-aligned box intersects the plane; `false` otherwise
   */
  fun testAabPlane(
      minX: Float, minY: Float, minZ: Float,
      maxX: Float, maxY: Float, maxZ: Float,
      a: Float, b: Float, c: Float, d: Float): Boolean {
    val pX: Float
    val pY: Float
    val pZ: Float
    val nX: Float
    val nY: Float
    val nZ: Float
    if (a > 0.0f) {
      pX = maxX
      nX = minX
    } else {
      pX = minX
      nX = maxX
    }
    if (b > 0.0f) {
      pY = maxY
      nY = minY
    } else {
      pY = minY
      nY = maxY
    }
    if (c > 0.0f) {
      pZ = maxZ
      nZ = minZ
    } else {
      pZ = minZ
      nZ = maxZ
    }
    val distN = d + a * nX + b * nY + c * nZ
    val distP = d + a * pX + b * pY + c * pZ
    return distN <= 0.0f && distP >= 0.0f
  }

  /**
   * Test whether the axis-aligned box intersects the plane.
   *
   *
   * Reference: [http://www.lighthouse3d.com](http://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-testing-boxes-ii/) ("Geometric Approach - Testing Boxes II")
   *
   * @param aabb
   * the AABB
   * @param plane
   * the plane
   * @return `true` iff the axis-aligned box intersects the plane; `false` otherwise
   */
  fun testAabPlane(aabb: AABBf, plane: Planef): Boolean {
    return testAabPlane(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, plane.a, plane.b, plane.c, plane.d)
  }

  /**
   * Test whether the axis-aligned box with minimum corner `min` and maximum corner `max`
   * intersects the plane with the general equation *a*x + b*y + c*z + d = 0*.
   *
   *
   * Reference: [http://www.lighthouse3d.com](http://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-testing-boxes-ii/) ("Geometric Approach - Testing Boxes II")
   *
   * @param min
   * the minimum corner of the axis-aligned box
   * @param max
   * the maximum corner of the axis-aligned box
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @return `true` iff the axis-aligned box intersects the plane; `false` otherwise
   */
//     fun testAabPlane(min:Vector3fc, max:Vector3fc, a:Float, b:Float, c:Float, d:Float):Boolean {
//return testAabPlane(min.x, min.y, min.z, max.x, max.y, max.z, a, b, c, d)
//}

  /**
   * Test whether the axis-aligned box with minimum corner <tt>(minXA, minYA, minZA)</tt> and maximum corner <tt>(maxXA, maxYA, maxZA)</tt>
   * intersects the axis-aligned box with minimum corner <tt>(minXB, minYB, minZB)</tt> and maximum corner <tt>(maxXB, maxYB, maxZB)</tt>.
   *
   * @param minXA
   * the x coordinate of the minimum corner of the first axis-aligned box
   * @param minYA
   * the y coordinate of the minimum corner of the first axis-aligned box
   * @param minZA
   * the z coordinate of the minimum corner of the first axis-aligned box
   * @param maxXA
   * the x coordinate of the maximum corner of the first axis-aligned box
   * @param maxYA
   * the y coordinate of the maximum corner of the first axis-aligned box
   * @param maxZA
   * the z coordinate of the maximum corner of the first axis-aligned box
   * @param minXB
   * the x coordinate of the minimum corner of the second axis-aligned box
   * @param minYB
   * the y coordinate of the minimum corner of the second axis-aligned box
   * @param minZB
   * the z coordinate of the minimum corner of the second axis-aligned box
   * @param maxXB
   * the x coordinate of the maximum corner of the second axis-aligned box
   * @param maxYB
   * the y coordinate of the maximum corner of the second axis-aligned box
   * @param maxZB
   * the z coordinate of the maximum corner of the second axis-aligned box
   * @return `true` iff both axis-aligned boxes intersect; `false` otherwise
   */
  fun testAabAab(
      minXA: Float, minYA: Float, minZA: Float,
      maxXA: Float, maxYA: Float, maxZA: Float,
      minXB: Float, minYB: Float, minZB: Float,
      maxXB: Float, maxYB: Float, maxZB: Float): Boolean {
    return maxXA >= minXB && maxYA >= minYB && maxZA >= minZB &&
        minXA <= maxXB && minYA <= maxYB && minZA <= maxZB
  }

  /**
   * Test whether the axis-aligned box with minimum corner `minA` and maximum corner `maxA`
   * intersects the axis-aligned box with minimum corner `minB` and maximum corner `maxB`.
   *
   * @param minA
   * the minimum corner of the first axis-aligned box
   * @param maxA
   * the maximum corner of the first axis-aligned box
   * @param minB
   * the minimum corner of the second axis-aligned box
   * @param maxB
   * the maximum corner of the second axis-aligned box
   * @return `true` iff both axis-aligned boxes intersect; `false` otherwise
   */
  fun testAabAab(minA: Vector3fc, maxA: Vector3fc, minB: Vector3fc, maxB: Vector3fc): Boolean {
    return testAabAab(minA.x, minA.y, minA.z, maxA.x, maxA.y, maxA.z, minB.x, minB.y, minB.z, maxB.x, maxB.y, maxB.z)
  }

  /**
   * Test whether the two axis-aligned boxes intersect.
   *
   * @param aabb1
   * the first AABB
   * @param aabb2
   * the second AABB
   * @return `true` iff both axis-aligned boxes intersect; `false` otherwise
   */
  fun testAabAab(aabb1: AABBf, aabb2: AABBf): Boolean {
    return testAabAab(aabb1.minX, aabb1.minY, aabb1.minZ, aabb1.maxX, aabb1.maxY, aabb1.maxZ, aabb2.minX, aabb2.minY, aabb2.minZ, aabb2.maxX, aabb2.maxY, aabb2.maxZ)
  }

  /**
   * Test whether two oriented boxes given via their center position, orientation and half-size, intersect.
   *
   *
   * The orientation of a box is given as three unit vectors spanning the local orthonormal basis of the box.
   *
   *
   * The size is given as the half-size along each of the unit vectors defining the orthonormal basis.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 4.4.1 "OBB-OBB Intersection"
   *
   * @param b0c
   * the center of the first box
   * @param b0uX
   * the local X unit vector of the first box
   * @param b0uY
   * the local Y unit vector of the first box
   * @param b0uZ
   * the local Z unit vector of the first box
   * @param b0hs
   * the half-size of the first box
   * @param b1c
   * the center of the second box
   * @param b1uX
   * the local X unit vector of the second box
   * @param b1uY
   * the local Y unit vector of the second box
   * @param b1uZ
   * the local Z unit vector of the second box
   * @param b1hs
   * the half-size of the second box
   * @return `true` if both boxes intersect; `false` otherwise
   */
  fun testObOb(
      b0c: Vector3m, b0uX: Vector3m, b0uY: Vector3m, b0uZ: Vector3m, b0hs: Vector3m,
      b1c: Vector3m, b1uX: Vector3m, b1uY: Vector3m, b1uZ: Vector3m, b1hs: Vector3m): Boolean {
    return testObOb(
        b0c.x, b0c.y, b0c.z, b0uX.x, b0uX.y, b0uX.z, b0uY.x, b0uY.y, b0uY.z, b0uZ.x, b0uZ.y, b0uZ.z, b0hs.x, b0hs.y, b0hs.z,
        b1c.x, b1c.y, b1c.z, b1uX.x, b1uX.y, b1uX.z, b1uY.x, b1uY.y, b1uY.z, b1uZ.x, b1uZ.y, b1uZ.z, b1hs.x, b1hs.y, b1hs.z)
  }

  /**
   * Test whether two oriented boxes given via their center position, orientation and half-size, intersect.
   *
   *
   * The orientation of a box is given as three unit vectors spanning the local orthonormal basis of the box.
   *
   *
   * The size is given as the half-size along each of the unit vectors defining the orthonormal basis.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 4.4.1 "OBB-OBB Intersection"
   *
   * @param b0cX
   * the x coordinate of the center of the first box
   * @param b0cY
   * the y coordinate of the center of the first box
   * @param b0cZ
   * the z coordinate of the center of the first box
   * @param b0uXx
   * the x coordinate of the local X unit vector of the first box
   * @param b0uXy
   * the y coordinate of the local X unit vector of the first box
   * @param b0uXz
   * the z coordinate of the local X unit vector of the first box
   * @param b0uYx
   * the x coordinate of the local Y unit vector of the first box
   * @param b0uYy
   * the y coordinate of the local Y unit vector of the first box
   * @param b0uYz
   * the z coordinate of the local Y unit vector of the first box
   * @param b0uZx
   * the x coordinate of the local Z unit vector of the first box
   * @param b0uZy
   * the y coordinate of the local Z unit vector of the first box
   * @param b0uZz
   * the z coordinate of the local Z unit vector of the first box
   * @param b0hsX
   * the half-size of the first box along its local X axis
   * @param b0hsY
   * the half-size of the first box along its local Y axis
   * @param b0hsZ
   * the half-size of the first box along its local Z axis
   * @param b1cX
   * the x coordinate of the center of the second box
   * @param b1cY
   * the y coordinate of the center of the second box
   * @param b1cZ
   * the z coordinate of the center of the second box
   * @param b1uXx
   * the x coordinate of the local X unit vector of the second box
   * @param b1uXy
   * the y coordinate of the local X unit vector of the second box
   * @param b1uXz
   * the z coordinate of the local X unit vector of the second box
   * @param b1uYx
   * the x coordinate of the local Y unit vector of the second box
   * @param b1uYy
   * the y coordinate of the local Y unit vector of the second box
   * @param b1uYz
   * the z coordinate of the local Y unit vector of the second box
   * @param b1uZx
   * the x coordinate of the local Z unit vector of the second box
   * @param b1uZy
   * the y coordinate of the local Z unit vector of the second box
   * @param b1uZz
   * the z coordinate of the local Z unit vector of the second box
   * @param b1hsX
   * the half-size of the second box along its local X axis
   * @param b1hsY
   * the half-size of the second box along its local Y axis
   * @param b1hsZ
   * the half-size of the second box along its local Z axis
   * @return `true` if both boxes intersect; `false` otherwise
   */
  fun testObOb(
      b0cX: Float, b0cY: Float, b0cZ: Float, b0uXx: Float, b0uXy: Float, b0uXz: Float, b0uYx: Float, b0uYy: Float, b0uYz: Float, b0uZx: Float, b0uZy: Float, b0uZz: Float, b0hsX: Float, b0hsY: Float, b0hsZ: Float,
      b1cX: Float, b1cY: Float, b1cZ: Float, b1uXx: Float, b1uXy: Float, b1uXz: Float, b1uYx: Float, b1uYy: Float, b1uYz: Float, b1uZx: Float, b1uZy: Float, b1uZz: Float, b1hsX: Float, b1hsY: Float, b1hsZ: Float): Boolean {
    var ra: Float
    var rb: Float
    // Compute rotation matrix expressing b in a's coordinate frame
    val rm00 = b0uXx * b1uXx + b0uYx * b1uYx + b0uZx * b1uZx
    val rm10 = b0uXx * b1uXy + b0uYx * b1uYy + b0uZx * b1uZy
    val rm20 = b0uXx * b1uXz + b0uYx * b1uYz + b0uZx * b1uZz
    val rm01 = b0uXy * b1uXx + b0uYy * b1uYx + b0uZy * b1uZx
    val rm11 = b0uXy * b1uXy + b0uYy * b1uYy + b0uZy * b1uZy
    val rm21 = b0uXy * b1uXz + b0uYy * b1uYz + b0uZy * b1uZz
    val rm02 = b0uXz * b1uXx + b0uYz * b1uYx + b0uZz * b1uZx
    val rm12 = b0uXz * b1uXy + b0uYz * b1uYy + b0uZz * b1uZy
    val rm22 = b0uXz * b1uXz + b0uYz * b1uYz + b0uZz * b1uZz
    // Compute common subexpressions. Add in an epsilon term to
    // counteract arithmetic errors when two edges are parallel and
    // their cross product is (near) null (see text for details)
    val EPSILON = 1E-5f
    val arm00 = Math.abs(rm00) + EPSILON
    val arm01 = Math.abs(rm01) + EPSILON
    val arm02 = Math.abs(rm02) + EPSILON
    val arm10 = Math.abs(rm10) + EPSILON
    val arm11 = Math.abs(rm11) + EPSILON
    val arm12 = Math.abs(rm12) + EPSILON
    val arm20 = Math.abs(rm20) + EPSILON
    val arm21 = Math.abs(rm21) + EPSILON
    val arm22 = Math.abs(rm22) + EPSILON
    // Compute translation vector t
    val tx = b1cX - b0cX
    val ty = b1cY - b0cY
    val tz = b1cZ - b0cZ
    // Bring translation into a's coordinate frame
    val tax = tx * b0uXx + ty * b0uXy + tz * b0uXz
    val tay = tx * b0uYx + ty * b0uYy + tz * b0uYz
    val taz = tx * b0uZx + ty * b0uZy + tz * b0uZz
    // Test axes L = A0, L = A1, L = A2
    ra = b0hsX
    rb = b1hsX * arm00 + b1hsY * arm01 + b1hsZ * arm02
    if (Math.abs(tax) > ra + rb) return false
    ra = b0hsY
    rb = b1hsX * arm10 + b1hsY * arm11 + b1hsZ * arm12
    if (Math.abs(tay) > ra + rb) return false
    ra = b0hsZ
    rb = b1hsX * arm20 + b1hsY * arm21 + b1hsZ * arm22
    if (Math.abs(taz) > ra + rb) return false
    // Test axes L = B0, L = B1, L = B2
    ra = b0hsX * arm00 + b0hsY * arm10 + b0hsZ * arm20
    rb = b1hsX
    if (Math.abs(tax * rm00 + tay * rm10 + taz * rm20) > ra + rb) return false
    ra = b0hsX * arm01 + b0hsY * arm11 + b0hsZ * arm21
    rb = b1hsY
    if (Math.abs(tax * rm01 + tay * rm11 + taz * rm21) > ra + rb) return false
    ra = b0hsX * arm02 + b0hsY * arm12 + b0hsZ * arm22
    rb = b1hsZ
    if (Math.abs(tax * rm02 + tay * rm12 + taz * rm22) > ra + rb) return false
    // Test axis L = A0 x B0
    ra = b0hsY * arm20 + b0hsZ * arm10
    rb = b1hsY * arm02 + b1hsZ * arm01
    if (Math.abs(taz * rm10 - tay * rm20) > ra + rb) return false
    // Test axis L = A0 x B1
    ra = b0hsY * arm21 + b0hsZ * arm11
    rb = b1hsX * arm02 + b1hsZ * arm00
    if (Math.abs(taz * rm11 - tay * rm21) > ra + rb) return false
    // Test axis L = A0 x B2
    ra = b0hsY * arm22 + b0hsZ * arm12
    rb = b1hsX * arm01 + b1hsY * arm00
    if (Math.abs(taz * rm12 - tay * rm22) > ra + rb) return false
    // Test axis L = A1 x B0
    ra = b0hsX * arm20 + b0hsZ * arm00
    rb = b1hsY * arm12 + b1hsZ * arm11
    if (Math.abs(tax * rm20 - taz * rm00) > ra + rb) return false
    // Test axis L = A1 x B1
    ra = b0hsX * arm21 + b0hsZ * arm01
    rb = b1hsX * arm12 + b1hsZ * arm10
    if (Math.abs(tax * rm21 - taz * rm01) > ra + rb) return false
    // Test axis L = A1 x B2
    ra = b0hsX * arm22 + b0hsZ * arm02
    rb = b1hsX * arm11 + b1hsY * arm10
    if (Math.abs(tax * rm22 - taz * rm02) > ra + rb) return false
    // Test axis L = A2 x B0
    ra = b0hsX * arm10 + b0hsY * arm00
    rb = b1hsY * arm22 + b1hsZ * arm21
    if (Math.abs(tay * rm00 - tax * rm10) > ra + rb) return false
    // Test axis L = A2 x B1
    ra = b0hsX * arm11 + b0hsY * arm01
    rb = b1hsX * arm22 + b1hsZ * arm20
    if (Math.abs(tay * rm01 - tax * rm11) > ra + rb) return false
    // Test axis L = A2 x B2
    ra = b0hsX * arm12 + b0hsY * arm02
    rb = b1hsX * arm21 + b1hsY * arm20
    return if (Math.abs(tay * rm02 - tax * rm12) > ra + rb) false else true
    // Since no separating axis is found, the OBBs must be intersecting
  }

  /**
   * Test whether the one sphere with center <tt>(aX, aY, aZ)</tt> and square radius `radiusSquaredA` intersects the other
   * sphere with center <tt>(bX, bY, bZ)</tt> and square radius `radiusSquaredB`, and store the center of the circle of
   * intersection in the <tt>(x, y, z)</tt> components of the supplied vector and the radius of that circle in the w component.
   *
   *
   * The normal vector of the circle of intersection can simply be obtained by subtracting the center of either sphere from the other.
   *
   *
   * Reference: [http://gamedev.stackexchange.com](http://gamedev.stackexchange.com/questions/75756/sphere-sphere-intersection-and-circle-sphere-intersection)
   *
   * @param aX
   * the x coordinate of the first sphere's center
   * @param aY
   * the y coordinate of the first sphere's center
   * @param aZ
   * the z coordinate of the first sphere's center
   * @param radiusSquaredA
   * the square of the first sphere's radius
   * @param bX
   * the x coordinate of the second sphere's center
   * @param bY
   * the y coordinate of the second sphere's center
   * @param bZ
   * the z coordinate of the second sphere's center
   * @param radiusSquaredB
   * the square of the second sphere's radius
   * @param centerAndRadiusOfIntersectionCircle
   * will hold the center of the circle of intersection in the <tt>(x, y, z)</tt> components and the radius in the w component
   * @return `true` iff both spheres intersect; `false` otherwise
   */
  fun intersectSphereSphere(
      aX: Float, aY: Float, aZ: Float, radiusSquaredA: Float,
      bX: Float, bY: Float, bZ: Float, radiusSquaredB: Float,
      centerAndRadiusOfIntersectionCircle: Vector4f): Boolean {
    val dX = bX - aX
    val dY = bY - aY
    val dZ = bZ - aZ
    val distSquared = dX * dX + dY * dY + dZ * dZ
    val h = 0.5f + (radiusSquaredA - radiusSquaredB) / distSquared
    val r_i = radiusSquaredA - h * h * distSquared
    if (r_i >= 0.0f) {
      centerAndRadiusOfIntersectionCircle.x = aX + h * dX
      centerAndRadiusOfIntersectionCircle.y = aY + h * dY
      centerAndRadiusOfIntersectionCircle.z = aZ + h * dZ
      centerAndRadiusOfIntersectionCircle.w = Math.sqrt(r_i.toDouble()).toFloat()
      return true
    }
    return false
  }

  /**
   * Test whether the one sphere with center `centerA` and square radius `radiusSquaredA` intersects the other
   * sphere with center `centerB` and square radius `radiusSquaredB`, and store the center of the circle of
   * intersection in the <tt>(x, y, z)</tt> components of the supplied vector and the radius of that circle in the w component.
   *
   *
   * The normal vector of the circle of intersection can simply be obtained by subtracting the center of either sphere from the other.
   *
   *
   * Reference: [http://gamedev.stackexchange.com](http://gamedev.stackexchange.com/questions/75756/sphere-sphere-intersection-and-circle-sphere-intersection)
   *
   * @param centerA
   * the first sphere's center
   * @param radiusSquaredA
   * the square of the first sphere's radius
   * @param centerB
   * the second sphere's center
   * @param radiusSquaredB
   * the square of the second sphere's radius
   * @param centerAndRadiusOfIntersectionCircle
   * will hold the center of the circle of intersection in the <tt>(x, y, z)</tt> components and the radius in the w component
   * @return `true` iff both spheres intersect; `false` otherwise
   */
  fun intersectSphereSphere(centerA: Vector3fc, radiusSquaredA: Float, centerB: Vector3fc, radiusSquaredB: Float, centerAndRadiusOfIntersectionCircle: Vector4f): Boolean {
    return intersectSphereSphere(centerA.x, centerA.y, centerA.z, radiusSquaredA, centerB.x, centerB.y, centerB.z, radiusSquaredB, centerAndRadiusOfIntersectionCircle)
  }

  /**
   * Test whether the one sphere with intersects the other sphere, and store the center of the circle of
   * intersection in the <tt>(x, y, z)</tt> components of the supplied vector and the radius of that circle in the w component.
   *
   *
   * The normal vector of the circle of intersection can simply be obtained by subtracting the center of either sphere from the other.
   *
   *
   * Reference: [http://gamedev.stackexchange.com](http://gamedev.stackexchange.com/questions/75756/sphere-sphere-intersection-and-circle-sphere-intersection)
   *
   * @param sphereA
   * the first sphere
   * @param sphereB
   * the second sphere
   * @param centerAndRadiusOfIntersectionCircle
   * will hold the center of the circle of intersection in the <tt>(x, y, z)</tt> components and the radius in the w component
   * @return `true` iff both spheres intersect; `false` otherwise
   */
  fun intersectSphereSphere(sphereA: Spheref, sphereB: Spheref, centerAndRadiusOfIntersectionCircle: Vector4f): Boolean {
    return intersectSphereSphere(sphereA.x, sphereA.y, sphereA.z, sphereA.r * sphereA.r, sphereB.x, sphereB.y, sphereB.z, sphereB.r * sphereB.r, centerAndRadiusOfIntersectionCircle)
  }

  /**
   * Test whether the given sphere with center <tt>(sX, sY, sZ)</tt> intersects the triangle given by its three vertices, and if they intersect
   * store the point of intersection into `result`.
   *
   *
   * This method also returns whether the point of intersection is on one of the triangle's vertices, edges or on the face.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 5.2.7 "Testing Sphere Against Triangle"
   *
   * @param sX
   * the x coordinate of the sphere's center
   * @param sY
   * the y coordinate of the sphere's center
   * @param sZ
   * the z coordinate of the sphere's center
   * @param sR
   * the sphere's radius
   * @param v0X
   * the x coordinate of the first vertex of the triangle
   * @param v0Y
   * the y coordinate of the first vertex of the triangle
   * @param v0Z
   * the z coordinate of the first vertex of the triangle
   * @param v1X
   * the x coordinate of the second vertex of the triangle
   * @param v1Y
   * the y coordinate of the second vertex of the triangle
   * @param v1Z
   * the z coordinate of the second vertex of the triangle
   * @param v2X
   * the x coordinate of the third vertex of the triangle
   * @param v2Y
   * the y coordinate of the third vertex of the triangle
   * @param v2Z
   * the z coordinate of the third vertex of the triangle
   * @param result
   * will hold the point of intersection
   * @return one of [.POINT_ON_TRIANGLE_VERTEX_0], [.POINT_ON_TRIANGLE_VERTEX_1], [.POINT_ON_TRIANGLE_VERTEX_2],
   * [.POINT_ON_TRIANGLE_EDGE_01], [.POINT_ON_TRIANGLE_EDGE_12], [.POINT_ON_TRIANGLE_EDGE_20] or
   * [.POINT_ON_TRIANGLE_FACE] or <tt>0</tt>
   */
  fun intersectSphereTriangle(
      sX: Float, sY: Float, sZ: Float, sR: Float,
      v0X: Float, v0Y: Float, v0Z: Float,
      v1X: Float, v1Y: Float, v1Z: Float,
      v2X: Float, v2Y: Float, v2Z: Float,
      result: Vector3m): Int {
    val closest = findClosestPointOnTriangle(v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z, sX, sY, sZ, result)
    val vX = result.x - sX
    val vY = result.y - sY
    val vZ = result.z - sZ
    val dot = vX * vX + vY * vY + vZ * vZ
    return if (dot <= sR * sR) {
      closest
    } else 0
  }

  /**
   * Test whether the one sphere with center <tt>(aX, aY, aZ)</tt> and square radius `radiusSquaredA` intersects the other
   * sphere with center <tt>(bX, bY, bZ)</tt> and square radius `radiusSquaredB`.
   *
   *
   * Reference: [http://gamedev.stackexchange.com](http://gamedev.stackexchange.com/questions/75756/sphere-sphere-intersection-and-circle-sphere-intersection)
   *
   * @param aX
   * the x coordinate of the first sphere's center
   * @param aY
   * the y coordinate of the first sphere's center
   * @param aZ
   * the z coordinate of the first sphere's center
   * @param radiusSquaredA
   * the square of the first sphere's radius
   * @param bX
   * the x coordinate of the second sphere's center
   * @param bY
   * the y coordinate of the second sphere's center
   * @param bZ
   * the z coordinate of the second sphere's center
   * @param radiusSquaredB
   * the square of the second sphere's radius
   * @return `true` iff both spheres intersect; `false` otherwise
   */
  fun testSphereSphere(
      aX: Float, aY: Float, aZ: Float, radiusSquaredA: Float,
      bX: Float, bY: Float, bZ: Float, radiusSquaredB: Float): Boolean {
    val dX = bX - aX
    val dY = bY - aY
    val dZ = bZ - aZ
    val distSquared = dX * dX + dY * dY + dZ * dZ
    val h = 0.5f + (radiusSquaredA - radiusSquaredB) / distSquared
    val r_i = radiusSquaredA - h * h * distSquared
    return r_i >= 0.0f
  }

  /**
   * Test whether the one sphere with center `centerA` and square radius `radiusSquaredA` intersects the other
   * sphere with center `centerB` and square radius `radiusSquaredB`.
   *
   *
   * Reference: [http://gamedev.stackexchange.com](http://gamedev.stackexchange.com/questions/75756/sphere-sphere-intersection-and-circle-sphere-intersection)
   *
   * @param centerA
   * the first sphere's center
   * @param radiusSquaredA
   * the square of the first sphere's radius
   * @param centerB
   * the second sphere's center
   * @param radiusSquaredB
   * the square of the second sphere's radius
   * @return `true` iff both spheres intersect; `false` otherwise
   */
  fun testSphereSphere(centerA: Vector3fc, radiusSquaredA: Float, centerB: Vector3fc, radiusSquaredB: Float): Boolean {
    return testSphereSphere(centerA.x, centerA.y, centerA.z, radiusSquaredA, centerB.x, centerB.y, centerB.z, radiusSquaredB)
  }

  /**
   * Determine the signed distance of the given point <tt>(pointX, pointY, pointZ)</tt> to the plane specified via its general plane equation
   * *a*x + b*y + c*z + d = 0*.
   *
   * @param pointX
   * the x coordinate of the point
   * @param pointY
   * the y coordinate of the point
   * @param pointZ
   * the z coordinate of the point
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @return the distance between the point and the plane
   */
  fun distancePointPlane(pointX: Float, pointY: Float, pointZ: Float, a: Float, b: Float, c: Float, d: Float): Float {
    val denom = Math.sqrt((a * a + b * b + c * c).toDouble()).toFloat()
    return (a * pointX + b * pointY + c * pointZ + d) / denom
  }

  /**
   * Determine the signed distance of the given point <tt>(pointX, pointY, pointZ)</tt> to the plane of the triangle specified by its three points
   * <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt> and <tt>(v2X, v2Y, v2Z)</tt>.
   *
   *
   * If the point lies on the front-facing side of the triangle's plane, that is, if the triangle has counter-clockwise winding order
   * as seen from the point, then this method returns a positive number.
   *
   * @param pointX
   * the x coordinate of the point
   * @param pointY
   * the y coordinate of the point
   * @param pointZ
   * the z coordinate of the point
   * @param v0X
   * the x coordinate of the first vertex of the triangle
   * @param v0Y
   * the y coordinate of the first vertex of the triangle
   * @param v0Z
   * the z coordinate of the first vertex of the triangle
   * @param v1X
   * the x coordinate of the second vertex of the triangle
   * @param v1Y
   * the y coordinate of the second vertex of the triangle
   * @param v1Z
   * the z coordinate of the second vertex of the triangle
   * @param v2X
   * the x coordinate of the third vertex of the triangle
   * @param v2Y
   * the y coordinate of the third vertex of the triangle
   * @param v2Z
   * the z coordinate of the third vertex of the triangle
   * @return the signed distance between the point and the plane of the triangle
   */
  fun distancePointPlane(pointX: Float, pointY: Float, pointZ: Float,
                         v0X: Float, v0Y: Float, v0Z: Float, v1X: Float, v1Y: Float, v1Z: Float, v2X: Float, v2Y: Float, v2Z: Float): Float {
    val v1Y0Y = v1Y - v0Y
    val v2Z0Z = v2Z - v0Z
    val v2Y0Y = v2Y - v0Y
    val v1Z0Z = v1Z - v0Z
    val v2X0X = v2X - v0X
    val v1X0X = v1X - v0X
    val a = v1Y0Y * v2Z0Z - v2Y0Y * v1Z0Z
    val b = v1Z0Z * v2X0X - v2Z0Z * v1X0X
    val c = v1X0X * v2Y0Y - v2X0X * v1Y0Y
    val d = -(a * v0X + b * v0Y + c * v0Z)
    return distancePointPlane(pointX, pointY, pointZ, a, b, c, d)
  }

  /**
   * Test whether the ray with given origin <tt>(originX, originY, originZ)</tt> and direction <tt>(dirX, dirY, dirZ)</tt> intersects the plane
   * containing the given point <tt>(pointX, pointY, pointZ)</tt> and having the normal <tt>(normalX, normalY, normalZ)</tt>, and return the
   * value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point.
   *
   *
   * This method returns <tt>-1.0</tt> if the ray does not intersect the plane, because it is either parallel to the plane or its direction points
   * away from the plane or the ray's origin is on the *negative* side of the plane (i.e. the plane's normal points away from the ray's origin).
   *
   *
   * Reference: [https://www.siggraph.org/](https://www.siggraph.org/education/materials/HyperGraph/raytrace/rayplane_intersection.htm)
   *
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param originZ
   * the z coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param dirZ
   * the z coordinate of the ray's direction
   * @param pointX
   * the x coordinate of a point on the plane
   * @param pointY
   * the y coordinate of a point on the plane
   * @param pointZ
   * the z coordinate of a point on the plane
   * @param normalX
   * the x coordinate of the plane's normal
   * @param normalY
   * the y coordinate of the plane's normal
   * @param normalZ
   * the z coordinate of the plane's normal
   * @param epsilon
   * some small epsilon for when the ray is parallel to the plane
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point, if the ray
   * intersects the plane; <tt>-1.0</tt> otherwise
   */
  fun intersectRayPlane(originX: Float, originY: Float, originZ: Float, dirX: Float, dirY: Float, dirZ: Float,
                        pointX: Float, pointY: Float, pointZ: Float, normalX: Float, normalY: Float, normalZ: Float, epsilon: Float): Float {
    val denom = normalX * dirX + normalY * dirY + normalZ * dirZ
    if (denom < epsilon) {
      val t = ((pointX - originX) * normalX + (pointY - originY) * normalY + (pointZ - originZ) * normalZ) / denom
      if (t >= 0.0f)
        return t
    }
    return -1.0f
  }

  /**
   * Test whether the ray with given `origin` and direction `dir` intersects the plane
   * containing the given `point` and having the given `normal`, and return the
   * value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point.
   *
   *
   * This method returns <tt>-1.0</tt> if the ray does not intersect the plane, because it is either parallel to the plane or its direction points
   * away from the plane or the ray's origin is on the *negative* side of the plane (i.e. the plane's normal points away from the ray's origin).
   *
   *
   * Reference: [https://www.siggraph.org/](https://www.siggraph.org/education/materials/HyperGraph/raytrace/rayplane_intersection.htm)
   *
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param point
   * a point on the plane
   * @param normal
   * the plane's normal
   * @param epsilon
   * some small epsilon for when the ray is parallel to the plane
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point, if the ray
   * intersects the plane; <tt>-1.0</tt> otherwise
   */
  fun intersectRayPlane(origin: Vector3fc, dir: Vector3fc, point: Vector3fc, normal: Vector3fc, epsilon: Float): Float {
    return intersectRayPlane(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, point.x, point.y, point.z, normal.x, normal.y, normal.z, epsilon)
  }

  /**
   * Test whether the given ray intersects the given plane, and return the
   * value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point.
   *
   *
   * This method returns <tt>-1.0</tt> if the ray does not intersect the plane, because it is either parallel to the plane or its direction points
   * away from the plane or the ray's origin is on the *negative* side of the plane (i.e. the plane's normal points away from the ray's origin).
   *
   *
   * Reference: [https://www.siggraph.org/](https://www.siggraph.org/education/materials/HyperGraph/raytrace/rayplane_intersection.htm)
   *
   * @param ray
   * the ray
   * @param plane
   * the plane
   * @param epsilon
   * some small epsilon for when the ray is parallel to the plane
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point, if the ray
   * intersects the plane; <tt>-1.0</tt> otherwise
   */
  fun intersectRayPlane(ray: Rayf, plane: Planef, epsilon: Float): Float {
    return intersectRayPlane(ray.oX, ray.oY, ray.oZ, ray.dX, ray.dY, ray.dZ, plane.a, plane.b, plane.c, plane.d, epsilon)
  }

  /**
   * Test whether the ray with given origin <tt>(originX, originY, originZ)</tt> and direction <tt>(dirX, dirY, dirZ)</tt> intersects the plane
   * given as the general plane equation *a*x + b*y + c*z + d = 0*, and return the
   * value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point.
   *
   *
   * This method returns <tt>-1.0</tt> if the ray does not intersect the plane, because it is either parallel to the plane or its direction points
   * away from the plane or the ray's origin is on the *negative* side of the plane (i.e. the plane's normal points away from the ray's origin).
   *
   *
   * Reference: [https://www.siggraph.org/](https://www.siggraph.org/education/materials/HyperGraph/raytrace/rayplane_intersection.htm)
   *
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param originZ
   * the z coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param dirZ
   * the z coordinate of the ray's direction
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @param epsilon
   * some small epsilon for when the ray is parallel to the plane
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point, if the ray
   * intersects the plane; <tt>-1.0</tt> otherwise
   */
  fun intersectRayPlane(originX: Float, originY: Float, originZ: Float, dirX: Float, dirY: Float, dirZ: Float,
                        a: Float, b: Float, c: Float, d: Float, epsilon: Float): Float {
    val denom = a * dirX + b * dirY + c * dirZ
    if (denom < 0.0f) {
      val t = -(a * originX + b * originY + c * originZ + d) / denom
      if (t >= 0.0f)
        return t
    }
    return -1.0f
  }

  /**
   * Test whether the axis-aligned box with minimum corner <tt>(minX, minY, minZ)</tt> and maximum corner <tt>(maxX, maxY, maxZ)</tt>
   * intersects the sphere with the given center <tt>(centerX, centerY, centerZ)</tt> and square radius `radiusSquared`.
   *
   *
   * Reference: [http://stackoverflow.com](http://stackoverflow.com/questions/4578967/cube-sphere-intersection-test#answer-4579069)
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
   * the z coordinate of the maximum corner of the axis-aligned box
   * @param centerX
   * the x coordinate of the sphere's center
   * @param centerY
   * the y coordinate of the sphere's center
   * @param centerZ
   * the z coordinate of the sphere's center
   * @param radiusSquared
   * the square of the sphere's radius
   * @return `true` iff the axis-aligned box intersects the sphere; `false` otherwise
   */
  fun testAabSphere(
      minX: Float, minY: Float, minZ: Float,
      maxX: Float, maxY: Float, maxZ: Float,
      centerX: Float, centerY: Float, centerZ: Float, radiusSquared: Float): Boolean {
    var radius2 = radiusSquared
    if (centerX < minX) {
      val d = centerX - minX
      radius2 -= d * d
    } else if (centerX > maxX) {
      val d = centerX - maxX
      radius2 -= d * d
    }
    if (centerY < minY) {
      val d = centerY - minY
      radius2 -= d * d
    } else if (centerY > maxY) {
      val d = centerY - maxY
      radius2 -= d * d
    }
    if (centerZ < minZ) {
      val d = centerZ - minZ
      radius2 -= d * d
    } else if (centerZ > maxZ) {
      val d = centerZ - maxZ
      radius2 -= d * d
    }
    return radius2 >= 0.0f
  }

  /**
   * Test whether the axis-aligned box with minimum corner `min` and maximum corner `max`
   * intersects the sphere with the given `center` and square radius `radiusSquared`.
   *
   *
   * Reference: [http://stackoverflow.com](http://stackoverflow.com/questions/4578967/cube-sphere-intersection-test#answer-4579069)
   *
   * @param min
   * the minimum corner of the axis-aligned box
   * @param max
   * the maximum corner of the axis-aligned box
   * @param center
   * the sphere's center
   * @param radiusSquared
   * the squared of the sphere's radius
   * @return `true` iff the axis-aligned box intersects the sphere; `false` otherwise
   */
  fun testAabSphere(min: Vector3fc, max: Vector3fc, center: Vector3fc, radiusSquared: Float): Boolean {
    return testAabSphere(min.x, min.y, min.z, max.x, max.y, max.z, center.x, center.y, center.z, radiusSquared)
  }

  /**
   * Test whether the given axis-aligned box intersects the given sphere.
   *
   *
   * Reference: [http://stackoverflow.com](http://stackoverflow.com/questions/4578967/cube-sphere-intersection-test#answer-4579069)
   *
   * @param aabb
   * the AABB
   * @param sphere
   * the sphere
   * @return `true` iff the axis-aligned box intersects the sphere; `false` otherwise
   */
  fun testAabSphere(aabb: AABBf, sphere: Spheref): Boolean {
    return testAabSphere(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, sphere.x, sphere.y, sphere.z, sphere.r * sphere.r)
  }

  /**
   * Find the point on the given plane which is closest to the specified point <tt>(pX, pY, pZ)</tt> and store the result in `result`.
   *
   * @param aX
   * the x coordinate of one point on the plane
   * @param aY
   * the y coordinate of one point on the plane
   * @param aZ
   * the z coordinate of one point on the plane
   * @param nX
   * the x coordinate of the unit normal of the plane
   * @param nY
   * the y coordinate of the unit normal of the plane
   * @param nZ
   * the z coordinate of the unit normal of the plane
   * @param pX
   * the x coordinate of the point
   * @param pY
   * the y coordinate of the point
   * @param pZ
   * the z coordinate of the point
   * @param result
   * will hold the result
   * @return result
   */
  fun findClosestPointOnPlane(aX: Float, aY: Float, aZ: Float, nX: Float, nY: Float, nZ: Float, pX: Float, pY: Float, pZ: Float, result: Vector3m): Vector3m {
    val d = -(nX * aX + nY * aY + nZ * aZ)
    val t = nX * pX + nY * pY + nZ * pZ - d
    result.x = pX - t * nX
    result.y = pY - t * nY
    result.z = pZ - t * nZ
    return result
  }

  /**
   * Find the point on the given line segment which is closest to the specified point <tt>(pX, pY, pZ)</tt>, and store the result in `result`.
   *
   * @param aX
   * the x coordinate of the first end point of the line segment
   * @param aY
   * the y coordinate of the first end point of the line segment
   * @param aZ
   * the z coordinate of the first end point of the line segment
   * @param bX
   * the x coordinate of the second end point of the line segment
   * @param bY
   * the y coordinate of the second end point of the line segment
   * @param bZ
   * the z coordinate of the second end point of the line segment
   * @param pX
   * the x coordinate of the point
   * @param pY
   * the y coordinate of the point
   * @param pZ
   * the z coordinate of the point
   * @param result
   * will hold the result
   * @return result
   */
  fun findClosestPointOnLineSegment(aX: Float, aY: Float, aZ: Float, bX: Float, bY: Float, bZ: Float, pX: Float, pY: Float, pZ: Float, result: Vector3m): Vector3m {
    val abX = bX - aX
    val abY = bY - aY
    val abZ = bZ - aZ
    var t = ((pX - aX) * abX + (pY - aY) * abY + (pZ - aZ) * abZ) / (abX * abX + abY * abY + abZ * abZ)
    if (t < 0.0f) t = 0.0f
    if (t > 1.0f) t = 1.0f
    result.x = aX + t * abX
    result.y = aY + t * abY
    result.z = aZ + t * abZ
    return result
  }

  /**
   * Find the closest points on the two line segments, store the point on the first line segment in `resultA` and
   * the point on the second line segment in `resultB`, and return the square distance between both points.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 5.1.9 "Closest Points of Two Line Segments"
   *
   * @param a0X
   * the x coordinate of the first line segment's first end point
   * @param a0Y
   * the y coordinate of the first line segment's first end point
   * @param a0Z
   * the z coordinate of the first line segment's first end point
   * @param a1X
   * the x coordinate of the first line segment's second end point
   * @param a1Y
   * the y coordinate of the first line segment's second end point
   * @param a1Z
   * the z coordinate of the first line segment's second end point
   * @param b0X
   * the x coordinate of the second line segment's first end point
   * @param b0Y
   * the y coordinate of the second line segment's first end point
   * @param b0Z
   * the z coordinate of the second line segment's first end point
   * @param b1X
   * the x coordinate of the second line segment's second end point
   * @param b1Y
   * the y coordinate of the second line segment's second end point
   * @param b1Z
   * the z coordinate of the second line segment's second end point
   * @param resultA
   * will hold the point on the first line segment
   * @param resultB
   * will hold the point on the second line segment
   * @return the square distance between the two closest points
   */
  fun findClosestPointsLineSegments(
      a0X: Float, a0Y: Float, a0Z: Float, a1X: Float, a1Y: Float, a1Z: Float,
      b0X: Float, b0Y: Float, b0Z: Float, b1X: Float, b1Y: Float, b1Z: Float,
      resultA: Vector3m, resultB: Vector3m): Float {
    val d1x = a1X - a0X
    val d1y = a1Y - a0Y
    val d1z = a1Z - a0Z
    val d2x = b1X - b0X
    val d2y = b1Y - b0Y
    val d2z = b1Z - b0Z
    val rX = a0X - b0X
    val rY = a0Y - b0Y
    val rZ = a0Z - b0Z
    val a = d1x * d1x + d1y * d1y + d1z * d1z
    val e = d2x * d2x + d2y * d2y + d2z * d2z
    val f = d2x * rX + d2y * rY + d2z * rZ
    val EPSILON = 1E-5f
    var s: Float
    var t: Float
    if (a <= EPSILON && e <= EPSILON) {
      // Both segments degenerate into points
      resultA.set(a0X, a0Y, a0Z)
      resultB.set(b0X, b0Y, b0Z)
      return resultA.dot(resultB)
    }
    if (a <= EPSILON) {
      // First segment degenerates into a point
      s = 0.0f
      t = f / e
      t = Math.min(Math.max(t, 0.0f), 1.0f)
    } else {
      val c = d1x * rX + d1y * rY + d1z * rZ
      if (e <= EPSILON) {
        // Second segment degenerates into a point
        t = 0.0f
        s = Math.min(Math.max(-c / a, 0.0f), 1.0f)
      } else {
        // The general nondegenerate case starts here
        val b = d1x * d2x + d1y * d2y + d1z * d2z
        val denom = a * e - b * b
        // If segments not parallel, compute closest point on L1 to L2 and
        // clamp to segment S1. Else pick arbitrary s (here 0)
        if (denom.toDouble() != 0.0)
          s = Math.min(Math.max((b * f - c * e) / denom, 0.0f), 1.0f)
        else
          s = 0.0f
        // Compute point on L2 closest to S1(s) using
        // t = Dot((P1 + D1*s) - P2,D2) / Dot(D2,D2) = (b*s + f) / e
        t = (b * s + f) / e
        // If t in [0,1] done. Else clamp t, recompute s for the new value
        // of t using s = Dot((P2 + D2*t) - P1,D1) / Dot(D1,D1)= (t*b - c) / a
        // and clamp s to [0, 1]
        if (t < 0.0) {
          t = 0.0f
          s = Math.min(Math.max(-c / a, 0.0f), 1.0f)
        } else if (t > 1.0) {
          t = 1.0f
          s = Math.min(Math.max((b - c) / a, 0.0f), 1.0f)
        }
      }
    }
    resultA.set(a0X + d1x * s, a0Y + d1y * s, a0Z + d1z * s)
    resultB.set(b0X + d2x * t, b0Y + d2y * t, b0Z + d2z * t)
    val dX = resultA.x - resultB.x
    val dY = resultA.y - resultB.y
    val dZ = resultA.z - resultB.z
    return dX * dX + dY * dY + dZ * dZ
  }

  /**
   * Find the closest points on a line segment and a triangle.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 5.1.10 "Closest Points of a Line Segment and a Triangle"
   *
   * @param aX
   * the x coordinate of the line segment's first end point
   * @param aY
   * the y coordinate of the line segment's first end point
   * @param aZ
   * the z coordinate of the line segment's first end point
   * @param bX
   * the x coordinate of the line segment's second end point
   * @param bY
   * the y coordinate of the line segment's second end point
   * @param bZ
   * the z coordinate of the line segment's second end point
   * @param v0X
   * the x coordinate of the triangle's first vertex
   * @param v0Y
   * the y coordinate of the triangle's first vertex
   * @param v0Z
   * the z coordinate of the triangle's first vertex
   * @param v1X
   * the x coordinate of the triangle's second vertex
   * @param v1Y
   * the y coordinate of the triangle's second vertex
   * @param v1Z
   * the z coordinate of the triangle's second vertex
   * @param v2X
   * the x coordinate of the triangle's third vertex
   * @param v2Y
   * the y coordinate of the triangle's third vertex
   * @param v2Z
   * the z coordinate of the triangle's third vertex
   * @param lineSegmentResult
   * will hold the closest point on the line segment
   * @param triangleResult
   * will hold the closest point on the triangle
   * @return the square distance of the closest points
   */
//  fun findClosestPointsLineSegmentTriangle(
//      aX: Float, aY: Float, aZ: Float, bX: Float, bY: Float, bZ: Float,
//      v0X: Float, v0Y: Float, v0Z: Float, v1X: Float, v1Y: Float, v1Z: Float, v2X: Float, v2Y: Float, v2Z: Float,
//      lineSegmentResult: Vector3m, triangleResult: Vector3m): Float {
//    var min: Float
//    var d: Float
//    var minlsX: Float
//    var minlsY: Float
//    var minlsZ: Float
//    var mintX: Float
//    var mintY: Float
//    var mintZ: Float
//    // AB -> V0V1
//    d = findClosestPointsLineSegments(aX, aY, aZ, bX, bY, bZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, lineSegmentResult, triangleResult)
//    min = d
//    minlsX = lineSegmentResult.x
//    minlsY = lineSegmentResult.y
//    minlsZ = lineSegmentResult.z
//    mintX = triangleResult.x
//    mintY = triangleResult.y
//    mintZ = triangleResult.z
//    // AB -> V1V2
//    d = findClosestPointsLineSegments(aX, aY, aZ, bX, bY, bZ, v1X, v1Y, v1Z, v2X, v2Y, v2Z, lineSegmentResult, triangleResult)
//    if (d < min) {
//      min = d
//      minlsX = lineSegmentResult.x
//      minlsY = lineSegmentResult.y
//      minlsZ = lineSegmentResult.z
//      mintX = triangleResult.x
//      mintY = triangleResult.y
//      mintZ = triangleResult.z
//    }
//    // AB -> V2V0
//    d = findClosestPointsLineSegments(aX, aY, aZ, bX, bY, bZ, v2X, v2Y, v2Z, v0X, v0Y, v0Z, lineSegmentResult, triangleResult)
//    if (d < min) {
//      min = d
//      minlsX = lineSegmentResult.x
//      minlsY = lineSegmentResult.y
//      minlsZ = lineSegmentResult.z
//      mintX = triangleResult.x
//      mintY = triangleResult.y
//      mintZ = triangleResult.z
//    }
//    // segment endpoint A and plane of triangle (when A projects inside V0V1V2)
//    var computed = false
//    var a = java.lang.Float.NaN
//    var b = java.lang.Float.NaN
//    var c = java.lang.Float.NaN
//    var nd = java.lang.Float.NaN
//    if (testPointInTriangle(aX, aY, aZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z)) {
//      val v1Y0Y = v1Y - v0Y
//      val v2Z0Z = v2Z - v0Z
//      val v2Y0Y = v2Y - v0Y
//      val v1Z0Z = v1Z - v0Z
//      val v2X0X = v2X - v0X
//      val v1X0X = v1X - v0X
//      a = v1Y0Y * v2Z0Z - v2Y0Y * v1Z0Z
//      b = v1Z0Z * v2X0X - v2Z0Z * v1X0X
//      c = v1X0X * v2Y0Y - v2X0X * v1Y0Y
//      computed = true
//      val invLen = 1.0f / Math.sqrt((a * a + b * b + c * c).toDouble()).toFloat()
//      a *= invLen
//      b *= invLen
//      c *= invLen
//      nd = -(a * v0X + b * v0Y + c * v0Z)
//      d = a * aX + b * aY + c * aZ + nd
//      d *= d
//      if (d < min) {
//        min = d
//        minlsX = aX
//        minlsY = aY
//        minlsZ = aZ
//        mintX = aX - a * d
//        mintY = aY - b * d
//        mintZ = aZ - c * d
//      }
//    }
//    // segment endpoint B and plane of triangle (when B projects inside V0V1V2)
//    if (testPointInTriangle(bX, bY, bZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z)) {
//      if (!computed) {
//        val v1Y0Y = v1Y - v0Y
//        val v2Z0Z = v2Z - v0Z
//        val v2Y0Y = v2Y - v0Y
//        val v1Z0Z = v1Z - v0Z
//        val v2X0X = v2X - v0X
//        val v1X0X = v1X - v0X
//        a = v1Y0Y * v2Z0Z - v2Y0Y * v1Z0Z
//        b = v1Z0Z * v2X0X - v2Z0Z * v1X0X
//        c = v1X0X * v2Y0Y - v2X0X * v1Y0Y
//        val invLen = 1.0f / Math.sqrt((a * a + b * b + c * c).toDouble()).toFloat()
//        a *= invLen
//        b *= invLen
//        c *= invLen
//        nd = -(a * v0X + b * v0Y + c * v0Z)
//      }
//      d = a * bX + b * bY + c * bZ + nd
//      d *= d
//      if (d < min) {
//        min = d
//        minlsX = bX
//        minlsY = bY
//        minlsZ = bZ
//        mintX = bX - a * d
//        mintY = bY - b * d
//        mintZ = bZ - c * d
//      }
//    }
//    lineSegmentResult.set(minlsX, minlsY, minlsZ)
//    triangleResult.set(mintX, mintY, mintZ)
//    return min
//  }

  /**
   * Determine the closest point on the triangle with the given vertices <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt>, <tt>(v2X, v2Y, v2Z)</tt>
   * between that triangle and the given point <tt>(pX, pY, pZ)</tt> and store that point into the given `result`.
   *
   *
   * Additionally, this method returns whether the closest point is a vertex ([.POINT_ON_TRIANGLE_VERTEX_0], [.POINT_ON_TRIANGLE_VERTEX_1], [.POINT_ON_TRIANGLE_VERTEX_2])
   * of the triangle, lies on an edge ([.POINT_ON_TRIANGLE_EDGE_01], [.POINT_ON_TRIANGLE_EDGE_12], [.POINT_ON_TRIANGLE_EDGE_20])
   * or on the [face][.POINT_ON_TRIANGLE_FACE] of the triangle.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 5.1.5 "Closest Point on Triangle to Point"
   *
   * @param v0X
   * the x coordinate of the first vertex of the triangle
   * @param v0Y
   * the y coordinate of the first vertex of the triangle
   * @param v0Z
   * the z coordinate of the first vertex of the triangle
   * @param v1X
   * the x coordinate of the second vertex of the triangle
   * @param v1Y
   * the y coordinate of the second vertex of the triangle
   * @param v1Z
   * the z coordinate of the second vertex of the triangle
   * @param v2X
   * the x coordinate of the third vertex of the triangle
   * @param v2Y
   * the y coordinate of the third vertex of the triangle
   * @param v2Z
   * the z coordinate of the third vertex of the triangle
   * @param pX
   * the x coordinate of the point
   * @param pY
   * the y coordinate of the point
   * @param pZ
   * the y coordinate of the point
   * @param result
   * will hold the closest point
   * @return one of [.POINT_ON_TRIANGLE_VERTEX_0], [.POINT_ON_TRIANGLE_VERTEX_1], [.POINT_ON_TRIANGLE_VERTEX_2],
   * [.POINT_ON_TRIANGLE_EDGE_01], [.POINT_ON_TRIANGLE_EDGE_12], [.POINT_ON_TRIANGLE_EDGE_20] or
   * [.POINT_ON_TRIANGLE_FACE]
   */
  fun findClosestPointOnTriangle(
      v0X: Float, v0Y: Float, v0Z: Float,
      v1X: Float, v1Y: Float, v1Z: Float,
      v2X: Float, v2Y: Float, v2Z: Float,
      pX: Float, pY: Float, pZ: Float,
      result: Vector3m): Int {
    val abX = v1X - v0X
    val abY = v1Y - v0Y
    val abZ = v1Z - v0Z
    val acX = v2X - v0X
    val acY = v2Y - v0Y
    val acZ = v2Z - v0Z
    val apX = pX - v0X
    val apY = pY - v0Y
    val apZ = pZ - v0Z
    val d1 = abX * apX + abY * apY + abZ * apZ
    val d2 = acX * apX + acY * apY + acZ * apZ
    if (d1 <= 0.0f && d2 <= 0.0f) {
      result.x = v0X
      result.y = v0Y
      result.z = v0Z
      return POINT_ON_TRIANGLE_VERTEX_0
    }
    val bpX = pX - v1X
    val bpY = pY - v1Y
    val bpZ = pZ - v1Z
    val d3 = abX * bpX + abY * bpY + abZ * bpZ
    val d4 = acX * bpX + acY * bpY + acZ * bpZ
    if (d3 >= 0.0f && d4 <= d3) {
      result.x = v1X
      result.y = v1Y
      result.z = v1Z
      return POINT_ON_TRIANGLE_VERTEX_1
    }
    val vc = d1 * d4 - d3 * d2
    if (vc <= 0.0f && d1 >= 0.0f && d3 <= 0.0f) {
      val v = d1 / (d1 - d3)
      result.x = v0X + v * abX
      result.y = v0Y + v * abY
      result.z = v0Z + v * abZ
      return POINT_ON_TRIANGLE_EDGE_01
    }
    val cpX = pX - v2X
    val cpY = pY - v2Y
    val cpZ = pZ - v2Z
    val d5 = abX * cpX + abY * cpY + abZ * cpZ
    val d6 = acX * cpX + acY * cpY + acZ * cpZ
    if (d6 >= 0.0f && d5 <= d6) {
      result.x = v2X
      result.y = v2Y
      result.z = v2Z
      return POINT_ON_TRIANGLE_VERTEX_2
    }
    val vb = d5 * d2 - d1 * d6
    if (vb <= 0.0f && d2 >= 0.0f && d6 <= 0.0f) {
      val w = d2 / (d2 - d6)
      result.x = v0X + w * acX
      result.y = v0Y + w * acY
      result.z = v0Z + w * acZ
      return POINT_ON_TRIANGLE_EDGE_20
    }
    val va = d3 * d6 - d5 * d4
    if (va <= 0.0f && d4 - d3 >= 0.0f && d5 - d6 >= 0.0f) {
      val w = (d4 - d3) / (d4 - d3 + d5 - d6)
      result.x = v1X + w * (v2X - v1X)
      result.y = v1Y + w * (v2Y - v1Y)
      result.z = v1Z + w * (v2Z - v1Z)
      return POINT_ON_TRIANGLE_EDGE_12
    }
    val denom = 1.0f / (va + vb + vc)
    val v = vb * denom
    val w = vc * denom
    result.x = v0X + abX * v + acX * w
    result.y = v0Y + abY * v + acY * w
    result.z = v0Z + abZ * v + acZ * w
    return POINT_ON_TRIANGLE_FACE
  }

  /**
   * Determine the closest point on the triangle with the vertices `v0`, `v1`, `v2`
   * between that triangle and the given point `p` and store that point into the given `result`.
   *
   *
   * Additionally, this method returns whether the closest point is a vertex ([.POINT_ON_TRIANGLE_VERTEX_0], [.POINT_ON_TRIANGLE_VERTEX_1], [.POINT_ON_TRIANGLE_VERTEX_2])
   * of the triangle, lies on an edge ([.POINT_ON_TRIANGLE_EDGE_01], [.POINT_ON_TRIANGLE_EDGE_12], [.POINT_ON_TRIANGLE_EDGE_20])
   * or on the [face][.POINT_ON_TRIANGLE_FACE] of the triangle.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 5.1.5 "Closest Point on Triangle to Point"
   *
   * @param v0
   * the first vertex of the triangle
   * @param v1
   * the second vertex of the triangle
   * @param v2
   * the third vertex of the triangle
   * @param p
   * the point
   * @param result
   * will hold the closest point
   * @return one of [.POINT_ON_TRIANGLE_VERTEX_0], [.POINT_ON_TRIANGLE_VERTEX_1], [.POINT_ON_TRIANGLE_VERTEX_2],
   * [.POINT_ON_TRIANGLE_EDGE_01], [.POINT_ON_TRIANGLE_EDGE_12], [.POINT_ON_TRIANGLE_EDGE_20] or
   * [.POINT_ON_TRIANGLE_FACE]
   */
  fun findClosestPointOnTriangle(v0: Vector3fc, v1: Vector3fc, v2: Vector3fc, p: Vector3fc, result: Vector3m): Int {
    return findClosestPointOnTriangle(v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, p.x, p.y, p.z, result)
  }

  /**
   * Find the point on a given rectangle, specified via three of its corners, which is closest to the specified point
   * <tt>(pX, pY, pZ)</tt> and store the result into `res`.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 5.1.4.2 "Closest Point on 3D Rectangle to Point"
   *
   * @param aX
   * the x coordinate of the first corner point of the rectangle
   * @param aY
   * the y coordinate of the first corner point of the rectangle
   * @param aZ
   * the z coordinate of the first corner point of the rectangle
   * @param bX
   * the x coordinate of the second corner point of the rectangle
   * @param bY
   * the y coordinate of the second corner point of the rectangle
   * @param bZ
   * the z coordinate of the second corner point of the rectangle
   * @param cX
   * the x coordinate of the third corner point of the rectangle
   * @param cY
   * the y coordinate of the third corner point of the rectangle
   * @param cZ
   * the z coordinate of the third corner point of the rectangle
   * @param pX
   * the x coordinate of the point
   * @param pY
   * the y coordinate of the point
   * @param pZ
   * the z coordinate of the point
   * @param res
   * will hold the result
   * @return res
   */
  fun findClosestPointOnRectangle(
      aX: Float, aY: Float, aZ: Float,
      bX: Float, bY: Float, bZ: Float,
      cX: Float, cY: Float, cZ: Float,
      pX: Float, pY: Float, pZ: Float, res: Vector3m): Vector3m {
    val abX = bX - aX
    val abY = bY - aY
    val abZ = bZ - aZ
    val acX = cX - aX
    val acY = cY - aY
    val acZ = cZ - aZ
    val dX = pX - aX
    val dY = pY - aY
    val dZ = pZ - aZ
    var qX = aX
    var qY = aY
    var qZ = aZ
    var dist = dX * abX + dY + abY + dZ * abZ
    var maxdist = abX * abX + abY * abY + abZ * abZ
    if (dist >= maxdist) {
      qX += abX
      qY += abY
      qZ += abZ
    } else if (dist > 0.0f) {
      qX += dist / maxdist * abX
      qY += dist / maxdist * abY
      qZ += dist / maxdist * abZ
    }
    dist = dX * acX + dY * acY + dZ * acZ
    maxdist = acX * acX + acY * acY + acZ * acZ
    if (dist >= maxdist) {
      qX += acX
      qY += acY
      qZ += acZ
    } else if (dist > 0.0f) {
      qX += dist / maxdist * acX
      qY += dist / maxdist * acY
      qZ += dist / maxdist * acZ
    }
    res.x = qX
    res.y = qY
    res.z = qZ
    return res
  }

  /**
   * Determine the point of intersection between a sphere with the given center <tt>(centerX, centerY, centerZ)</tt> and `radius` moving
   * with the given velocity <tt>(velX, velY, velZ)</tt> and the triangle specified via its three vertices <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt>, <tt>(v2X, v2Y, v2Z)</tt>.
   *
   *
   * The vertices of the triangle must be specified in counter-clockwise winding order.
   *
   *
   * An intersection is only considered if the time of intersection is smaller than the given `maxT` value.
   *
   *
   * Reference: [Improved Collision detection and Response](http://www.peroxide.dk/papers/collision/collision.pdf)
   *
   * @param centerX
   * the x coordinate of the sphere's center
   * @param centerY
   * the y coordinate of the sphere's center
   * @param centerZ
   * the z coordinate of the sphere's center
   * @param radius
   * the radius of the sphere
   * @param velX
   * the x component of the velocity of the sphere
   * @param velY
   * the y component of the velocity of the sphere
   * @param velZ
   * the z component of the velocity of the sphere
   * @param v0X
   * the x coordinate of the first triangle vertex
   * @param v0Y
   * the y coordinate of the first triangle vertex
   * @param v0Z
   * the z coordinate of the first triangle vertex
   * @param v1X
   * the x coordinate of the second triangle vertex
   * @param v1Y
   * the y coordinate of the second triangle vertex
   * @param v1Z
   * the z coordinate of the second triangle vertex
   * @param v2X
   * the x coordinate of the third triangle vertex
   * @param v2Y
   * the y coordinate of the third triangle vertex
   * @param v2Z
   * the z coordinate of the third triangle vertex
   * @param epsilon
   * a small epsilon when testing spheres that move almost parallel to the triangle
   * @param maxT
   * the maximum intersection time
   * @param pointAndTime
   * iff the moving sphere and the triangle intersect, this will hold the point of intersection in the <tt>(x, y, z)</tt> components
   * and the time of intersection in the <tt>w</tt> component
   * @return [.POINT_ON_TRIANGLE_FACE] if the intersection point lies on the triangle's face,
   * or [.POINT_ON_TRIANGLE_VERTEX_0], [.POINT_ON_TRIANGLE_VERTEX_1] or [.POINT_ON_TRIANGLE_VERTEX_2] if the intersection point is a vertex,
   * or [.POINT_ON_TRIANGLE_EDGE_01], [.POINT_ON_TRIANGLE_EDGE_12] or [.POINT_ON_TRIANGLE_EDGE_20] if the intersection point lies on an edge;
   * or <tt>0</tt> if no intersection
   */
//  fun intersectSweptSphereTriangle(
//      centerX: Float, centerY: Float, centerZ: Float, radius: Float, velX: Float, velY: Float, velZ: Float,
//      v0X: Float, v0Y: Float, v0Z: Float, v1X: Float, v1Y: Float, v1Z: Float, v2X: Float, v2Y: Float, v2Z: Float,
//      epsilon: Float, maxT: Float, pointAndTime: Vector4f): Int {
//    val v10X = v1X - v0X
//    val v10Y = v1Y - v0Y
//    val v10Z = v1Z - v0Z
//    val v20X = v2X - v0X
//    val v20Y = v2Y - v0Y
//    val v20Z = v2Z - v0Z
//    // build triangle plane
//    val a = v10Y * v20Z - v20Y * v10Z
//    val b = v10Z * v20X - v20Z * v10X
//    val c = v10X * v20Y - v20X * v10Y
//    val d = -(a * v0X + b * v0Y + c * v0Z)
//    val invLen = (1.0 / Math.sqrt((a * a + b * b + c * c).toDouble())).toFloat()
//    val signedDist = (a * centerX + b * centerY + c * centerZ + d) * invLen
//    val dot = (a * velX + b * velY + c * velZ) * invLen
//    if (dot < epsilon && dot > -epsilon)
//      return 0
//    val pt0 = (radius - signedDist) / dot
//    if (pt0 > maxT)
//      return 0
//    val pt1 = (-radius - signedDist) / dot
//    val p0X = centerX - radius * a * invLen + velX * pt0
//    val p0Y = centerY - radius * b * invLen + velY * pt0
//    val p0Z = centerZ - radius * c * invLen + velZ * pt0
//    val insideTriangle = testPointInTriangle(p0X, p0Y, p0Z, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z)
//    if (insideTriangle) {
//      pointAndTime.x = p0X
//      pointAndTime.y = p0Y
//      pointAndTime.z = p0Z
//      pointAndTime.w = pt0
//      return POINT_ON_TRIANGLE_FACE
//    }
//    var isect = 0
//    var t0 = maxT
//    val A = velX * velX + velY * velY + velZ * velZ
//    val radius2 = radius * radius
//    // test against v0
//    val centerV0X = centerX - v0X
//    val centerV0Y = centerY - v0Y
//    val centerV0Z = centerZ - v0Z
//    val B0 = 2.0f * (velX * centerV0X + velY * centerV0Y + velZ * centerV0Z)
//    val C0 = centerV0X * centerV0X + centerV0Y * centerV0Y + centerV0Z * centerV0Z - radius2
//    val root0 = computeLowestRoot(A, B0, C0, t0)
//    if (root0 < t0) {
//      pointAndTime.x = v0X
//      pointAndTime.y = v0Y
//      pointAndTime.z = v0Z
//      pointAndTime.w = root0
//      t0 = root0
//      isect = POINT_ON_TRIANGLE_VERTEX_0
//    }
//    // test against v1
//    val centerV1X = centerX - v1X
//    val centerV1Y = centerY - v1Y
//    val centerV1Z = centerZ - v1Z
//    val centerV1Len = centerV1X * centerV1X + centerV1Y * centerV1Y + centerV1Z * centerV1Z
//    val B1 = 2.0f * (velX * centerV1X + velY * centerV1Y + velZ * centerV1Z)
//    val C1 = centerV1Len - radius2
//    val root1 = computeLowestRoot(A, B1, C1, t0)
//    if (root1 < t0) {
//      pointAndTime.x = v1X
//      pointAndTime.y = v1Y
//      pointAndTime.z = v1Z
//      pointAndTime.w = root1
//      t0 = root1
//      isect = POINT_ON_TRIANGLE_VERTEX_1
//    }
//    // test against v2
//    val centerV2X = centerX - v2X
//    val centerV2Y = centerY - v2Y
//    val centerV2Z = centerZ - v2Z
//    val B2 = 2.0f * (velX * centerV2X + velY * centerV2Y + velZ * centerV2Z)
//    val C2 = centerV2X * centerV2X + centerV2Y * centerV2Y + centerV2Z * centerV2Z - radius2
//    val root2 = computeLowestRoot(A, B2, C2, t0)
//    if (root2 < t0) {
//      pointAndTime.x = v2X
//      pointAndTime.y = v2Y
//      pointAndTime.z = v2Z
//      pointAndTime.w = root2
//      t0 = root2
//      isect = POINT_ON_TRIANGLE_VERTEX_2
//    }
//    val velLen = velX * velX + velY * velY + velZ * velZ
//    // test against edge10
//    val len10 = v10X * v10X + v10Y * v10Y + v10Z * v10Z
//    val baseTo0Len = centerV0X * centerV0X + centerV0Y * centerV0Y + centerV0Z * centerV0Z
//    val v10Vel = v10X * velX + v10Y * velY + v10Z * velZ
//    val A10 = len10 * -velLen + v10Vel * v10Vel
//    val v10BaseTo0 = v10X * -centerV0X + v10Y * -centerV0Y + v10Z * -centerV0Z
//    val velBaseTo0 = velX * -centerV0X + velY * -centerV0Y + velZ * -centerV0Z
//    val B10 = len10 * 2f * velBaseTo0 - 2f * v10Vel * v10BaseTo0
//    val C10 = len10 * (radius2 - baseTo0Len) + v10BaseTo0 * v10BaseTo0
//    val root10 = computeLowestRoot(A10, B10, C10, t0)
//    val f10 = (v10Vel * root10 - v10BaseTo0) / len10
//    if (f10 >= 0.0f && f10 <= 1.0f && root10 < t0) {
//      pointAndTime.x = v0X + f10 * v10X
//      pointAndTime.y = v0Y + f10 * v10Y
//      pointAndTime.z = v0Z + f10 * v10Z
//      pointAndTime.w = root10
//      t0 = root10
//      isect = POINT_ON_TRIANGLE_EDGE_01
//    }
//    // test against edge20
//    val len20 = v20X * v20X + v20Y * v20Y + v20Z * v20Z
//    val v20Vel = v20X * velX + v20Y * velY + v20Z * velZ
//    val A20 = len20 * -velLen + v20Vel * v20Vel
//    val v20BaseTo0 = v20X * -centerV0X + v20Y * -centerV0Y + v20Z * -centerV0Z
//    val B20 = len20 * 2f * velBaseTo0 - 2f * v20Vel * v20BaseTo0
//    val C20 = len20 * (radius2 - baseTo0Len) + v20BaseTo0 * v20BaseTo0
//    val root20 = computeLowestRoot(A20, B20, C20, t0)
//    val f20 = (v20Vel * root20 - v20BaseTo0) / len20
//    if (f20 >= 0.0f && f20 <= 1.0f && root20 < pt1) {
//      pointAndTime.x = v0X + f20 * v20X
//      pointAndTime.y = v0Y + f20 * v20Y
//      pointAndTime.z = v0Z + f20 * v20Z
//      pointAndTime.w = root20
//      t0 = root20
//      isect = POINT_ON_TRIANGLE_EDGE_20
//    }
//    // test against edge21
//    val v21X = v2X - v1X
//    val v21Y = v2Y - v1Y
//    val v21Z = v2Z - v1Z
//    val len21 = v21X * v21X + v21Y * v21Y + v21Z * v21Z
//    val v21Vel = v21X * velX + v21Y * velY + v21Z * velZ
//    val A21 = len21 * -velLen + v21Vel * v21Vel
//    val v21BaseTo1 = v21X * -centerV1X + v21Y * -centerV1Y + v21Z * -centerV1Z
//    val velBaseTo1 = velX * -centerV1X + velY * -centerV1Y + velZ * -centerV1Z
//    val B21 = len21 * 2f * velBaseTo1 - 2f * v21Vel * v21BaseTo1
//    val C21 = len21 * (radius2 - centerV1Len) + v21BaseTo1 * v21BaseTo1
//    val root21 = computeLowestRoot(A21, B21, C21, t0)
//    val f21 = (v21Vel * root21 - v21BaseTo1) / len21
//    if (f21 >= 0.0f && f21 <= 1.0f && root21 < t0) {
//      pointAndTime.x = v1X + f21 * v21X
//      pointAndTime.y = v1Y + f21 * v21Y
//      pointAndTime.z = v1Z + f21 * v21Z
//      pointAndTime.w = root21
//      t0 = root21
//      isect = POINT_ON_TRIANGLE_EDGE_12
//    }
//    return isect
//  }

  /**
   * Compute the lowest root for <tt>t</tt> in the quadratic equation <tt>a*t*t + b*t + c = 0</tt>.
   *
   *
   * This is a helper method for [.intersectSweptSphereTriangle]
   *
   * @param a
   * the quadratic factor
   * @param b
   * the linear factor
   * @param c
   * the constant
   * @param maxR
   * the maximum expected root
   * @return the lowest of the two roots of the quadratic equation; or [Float.POSITIVE_INFINITY]
   */
  private fun computeLowestRoot(a: Float, b: Float, c: Float, maxR: Float): Float {
    val determinant = b * b - 4.0f * a * c
    if (determinant < 0.0f)
      return java.lang.Float.POSITIVE_INFINITY
    val sqrtD = Math.sqrt(determinant.toDouble()).toFloat()
    var r1 = (-b - sqrtD) / (2.0f * a)
    var r2 = (-b + sqrtD) / (2.0f * a)
    if (r1 > r2) {
      val temp = r2
      r2 = r1
      r1 = temp
    }
    if (r1 > 0.0f && r1 < maxR) {
      return r1
    }
    return if (r2 > 0.0f && r2 < maxR) {
      r2
    } else java.lang.Float.POSITIVE_INFINITY
  }

  /**
   * Test whether the projection of the given point <tt>(pX, pY, pZ)</tt> lies inside of the triangle defined by the three vertices
   * <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt> and <tt>(v2X, v2Y, v2Z)</tt>.
   *
   *
   * Reference: [Improved Collision detection and Response](http://www.peroxide.dk/papers/collision/collision.pdf)
   *
   * @param pX
   * the x coordinate of the point to test
   * @param pY
   * the y coordinate of the point to test
   * @param pZ
   * the z coordinate of the point to test
   * @param v0X
   * the x coordinate of the first vertex
   * @param v0Y
   * the y coordinate of the first vertex
   * @param v0Z
   * the z coordinate of the first vertex
   * @param v1X
   * the x coordinate of the second vertex
   * @param v1Y
   * the y coordinate of the second vertex
   * @param v1Z
   * the z coordinate of the second vertex
   * @param v2X
   * the x coordinate of the third vertex
   * @param v2Y
   * the y coordinate of the third vertex
   * @param v2Z
   * the z coordinate of the third vertex
   * @return `true` if the projection of the given point lies inside of the given triangle; `false` otherwise
   */
//     fun testPointInTriangle(pX:Float, pY:Float, pZ:Float, v0X:Float, v0Y:Float, v0Z:Float, v1X:Float, v1Y:Float, v1Z:Float, v2X:Float, v2Y:Float, v2Z:Float):Boolean {
//val e10X = v1X - v0X
//val e10Y = v1Y - v0Y
//val e10Z = v1Z - v0Z
//val e20X = v2X - v0X
//val e20Y = v2Y - v0Y
//val e20Z = v2Z - v0Z
//val a = e10X * e10X + e10Y * e10Y + e10Z * e10Z
//val b = e10X * e20X + e10Y * e20Y + e10Z * e20Z
//val c = e20X * e20X + e20Y * e20Y + e20Z * e20Z
//val ac_bb = a * c - b * b
//val vpX = pX - v0X
//val vpY = pY - v0Y
//val vpZ = pZ - v0Z
//val d = vpX * e10X + vpY * e10Y + vpZ * e10Z
//val e = vpX * e20X + vpY * e20Y + vpZ * e20Z
//val x = d * c - e * b
//val y = e * a - d * b
//val z = x + y - ac_bb
//return (Runtime.floatToIntBits(z) and (Runtime.floatToIntBits(x) or Runtime.floatToIntBits(y)).inv() and -0x8000000000000000L).toLong() != 0L
//}

  /**
   * Test whether the given ray with the origin <tt>(originX, originY, originZ)</tt> and normalized direction <tt>(dirX, dirY, dirZ)</tt>
   * intersects the given sphere with center <tt>(centerX, centerY, centerZ)</tt> and square radius `radiusSquared`,
   * and store the values of the parameter *t* in the ray equation *p(t) = origin + t * dir* for both points (near
   * and far) of intersections into the given `result` vector.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the sphere.
   *
   *
   * Reference: [http://www.scratchapixel.com/](http://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection)
   *
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param originZ
   * the z coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's normalized direction
   * @param dirY
   * the y coordinate of the ray's normalized direction
   * @param dirZ
   * the z coordinate of the ray's normalized direction
   * @param centerX
   * the x coordinate of the sphere's center
   * @param centerY
   * the y coordinate of the sphere's center
   * @param centerZ
   * the z coordinate of the sphere's center
   * @param radiusSquared
   * the sphere radius squared
   * @param result
   * a vector that will contain the values of the parameter *t* in the ray equation
   * *p(t) = origin + t * dir* for both points (near, far) of intersections with the sphere
   * @return `true` if the ray intersects the sphere; `false` otherwise
   */
  fun intersectRaySphere(originX: Float, originY: Float, originZ: Float, dirX: Float, dirY: Float, dirZ: Float,
                         centerX: Float, centerY: Float, centerZ: Float, radiusSquared: Float, result: Vector2f): Boolean {
    val Lx = centerX - originX
    val Ly = centerY - originY
    val Lz = centerZ - originZ
    val tca = Lx * dirX + Ly * dirY + Lz * dirZ
    val d2 = Lx * Lx + Ly * Ly + Lz * Lz - tca * tca
    if (d2 > radiusSquared)
      return false
    val thc = Math.sqrt((radiusSquared - d2).toDouble()).toFloat()
    val t0 = tca - thc
    val t1 = tca + thc
    if (t0 < t1 && t1 >= 0.0f) {
      result.x = t0
      result.y = t1
      return true
    }
    return false
  }

  /**
   * Test whether the ray with the given `origin` and normalized direction `dir`
   * intersects the sphere with the given `center` and square radius `radiusSquared`,
   * and store the values of the parameter *t* in the ray equation *p(t) = origin + t * dir* for both points (near
   * and far) of intersections into the given `result` vector.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the sphere.
   *
   *
   * Reference: [http://www.scratchapixel.com/](http://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection)
   *
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's normalized direction
   * @param center
   * the sphere's center
   * @param radiusSquared
   * the sphere radius squared
   * @param result
   * a vector that will contain the values of the parameter *t* in the ray equation
   * *p(t) = origin + t * dir* for both points (near, far) of intersections with the sphere
   * @return `true` if the ray intersects the sphere; `false` otherwise
   */
  fun intersectRaySphere(origin: Vector3fc, dir: Vector3fc, center: Vector3fc, radiusSquared: Float, result: Vector2f): Boolean {
    return intersectRaySphere(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, center.x, center.y, center.z, radiusSquared, result)
  }

  /**
   * Test whether the given ray intersects the given sphere,
   * and store the values of the parameter *t* in the ray equation *p(t) = origin + t * dir* for both points (near
   * and far) of intersections into the given `result` vector.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the sphere.
   *
   *
   * Reference: [http://www.scratchapixel.com/](http://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection)
   *
   * @param ray
   * the ray
   * @param sphere
   * the sphere
   * @param result
   * a vector that will contain the values of the parameter *t* in the ray equation
   * *p(t) = origin + t * dir* for both points (near, far) of intersections with the sphere
   * @return `true` if the ray intersects the sphere; `false` otherwise
   */
  fun intersectRaySphere(ray: Rayf, sphere: Spheref, result: Vector2f): Boolean {
    return intersectRaySphere(ray.oX, ray.oY, ray.oZ, ray.dX, ray.dY, ray.dZ, sphere.x, sphere.y, sphere.z, sphere.r * sphere.r, result)
  }

  /**
   * Test whether the given ray with the origin <tt>(originX, originY, originZ)</tt> and normalized direction <tt>(dirX, dirY, dirZ)</tt>
   * intersects the given sphere with center <tt>(centerX, centerY, centerZ)</tt> and square radius `radiusSquared`.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the sphere.
   *
   *
   * Reference: [http://www.scratchapixel.com/](http://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection)
   *
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param originZ
   * the z coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's normalized direction
   * @param dirY
   * the y coordinate of the ray's normalized direction
   * @param dirZ
   * the z coordinate of the ray's normalized direction
   * @param centerX
   * the x coordinate of the sphere's center
   * @param centerY
   * the y coordinate of the sphere's center
   * @param centerZ
   * the z coordinate of the sphere's center
   * @param radiusSquared
   * the sphere radius squared
   * @return `true` if the ray intersects the sphere; `false` otherwise
   */
  fun testRaySphere(originX: Float, originY: Float, originZ: Float, dirX: Float, dirY: Float, dirZ: Float,
                    centerX: Float, centerY: Float, centerZ: Float, radiusSquared: Float): Boolean {
    val Lx = centerX - originX
    val Ly = centerY - originY
    val Lz = centerZ - originZ
    val tca = Lx * dirX + Ly * dirY + Lz * dirZ
    val d2 = Lx * Lx + Ly * Ly + Lz * Lz - tca * tca
    if (d2 > radiusSquared)
      return false
    val thc = Math.sqrt((radiusSquared - d2).toDouble()).toFloat()
    val t0 = tca - thc
    val t1 = tca + thc
    return t0 < t1 && t1 >= 0.0f
  }

  /**
   * Test whether the ray with the given `origin` and normalized direction `dir`
   * intersects the sphere with the given `center` and square radius.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the sphere.
   *
   *
   * Reference: [http://www.scratchapixel.com/](http://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection)
   *
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's normalized direction
   * @param center
   * the sphere's center
   * @param radiusSquared
   * the sphere radius squared
   * @return `true` if the ray intersects the sphere; `false` otherwise
   */
  fun testRaySphere(origin: Vector3fc, dir: Vector3fc, center: Vector3fc, radiusSquared: Float): Boolean {
    return testRaySphere(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, center.x, center.y, center.z, radiusSquared)
  }

  /**
   * Test whether the given ray intersects the given sphere.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the sphere.
   *
   *
   * Reference: [http://www.scratchapixel.com/](http://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection)
   *
   * @param ray
   * the ray
   * @param sphere
   * the sphere
   * @return `true` if the ray intersects the sphere; `false` otherwise
   */
  fun testRaySphere(ray: Rayf, sphere: Spheref): Boolean {
    return testRaySphere(ray.oX, ray.oY, ray.oZ, ray.dX, ray.dY, ray.dZ, sphere.x, sphere.y, sphere.z, sphere.r * sphere.r)
  }

  /**
   * Test whether the line segment with the end points <tt>(p0X, p0Y, p0Z)</tt> and <tt>(p1X, p1Y, p1Z)</tt>
   * intersects the given sphere with center <tt>(centerX, centerY, centerZ)</tt> and square radius `radiusSquared`.
   *
   *
   * Reference: [http://paulbourke.net/](http://paulbourke.net/geometry/circlesphere/index.html#linesphere)
   *
   * @param p0X
   * the x coordinate of the line segment's first end point
   * @param p0Y
   * the y coordinate of the line segment's first end point
   * @param p0Z
   * the z coordinate of the line segment's first end point
   * @param p1X
   * the x coordinate of the line segment's second end point
   * @param p1Y
   * the y coordinate of the line segment's second end point
   * @param p1Z
   * the z coordinate of the line segment's second end point
   * @param centerX
   * the x coordinate of the sphere's center
   * @param centerY
   * the y coordinate of the sphere's center
   * @param centerZ
   * the z coordinate of the sphere's center
   * @param radiusSquared
   * the sphere radius squared
   * @return `true` if the line segment intersects the sphere; `false` otherwise
   */
  fun testLineSegmentSphere(p0X: Float, p0Y: Float, p0Z: Float, p1X: Float, p1Y: Float, p1Z: Float,
                            centerX: Float, centerY: Float, centerZ: Float, radiusSquared: Float): Boolean {
    var dX = p1X - p0X
    var dY = p1Y - p0Y
    var dZ = p1Z - p0Z
    val nom = (centerX - p0X) * dX + (centerY - p0Y) * dY + (centerZ - p0Z) * dZ
    val den = dX * dX + dY * dY + dZ * dZ
    val u = nom / den
    if (u < 0.0f) {
      dX = p0X - centerX
      dY = p0Y - centerY
      dZ = p0Z - centerZ
    } else if (u > 1.0f) {
      dX = p1X - centerX
      dY = p1Y - centerY
      dZ = p1Z - centerZ
    } else { // has to be >= 0 and <= 1
      val pX = p0X + u * dX
      val pY = p0Y + u * dY
      val pZ = p0Z + u * dZ
      dX = pX - centerX
      dY = pY - centerY
      dZ = pZ - centerZ
    }
    val dist = dX * dX + dY * dY + dZ * dZ
    return dist <= radiusSquared
  }

  /**
   * Test whether the line segment with the end points `p0` and `p1`
   * intersects the given sphere with center `center` and square radius `radiusSquared`.
   *
   *
   * Reference: [http://paulbourke.net/](http://paulbourke.net/geometry/circlesphere/index.html#linesphere)
   *
   * @param p0
   * the line segment's first end point
   * @param p1
   * the line segment's second end point
   * @param center
   * the sphere's center
   * @param radiusSquared
   * the sphere radius squared
   * @return `true` if the line segment intersects the sphere; `false` otherwise
   */
  fun testLineSegmentSphere(p0: Vector3fc, p1: Vector3fc, center: Vector3fc, radiusSquared: Float): Boolean {
    return testLineSegmentSphere(p0.x, p0.y, p0.z, p1.x, p1.y, p1.z, center.x, center.y, center.z, radiusSquared)
  }

  /**
   * Test whether the given ray with the origin <tt>(originX, originY, originZ)</tt> and direction <tt>(dirX, dirY, dirZ)</tt>
   * intersects the axis-aligned box given as its minimum corner <tt>(minX, minY, minZ)</tt> and maximum corner <tt>(maxX, maxY, maxZ)</tt>,
   * and return the values of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the near and far point of intersection.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the axis-aligned box.
   *
   *
   * If many boxes need to be tested against the same ray, then the [RayAabIntersection] class is likely more efficient.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .intersectRayAab
   * @see RayAabIntersection
   *
   *
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param originZ
   * the z coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param dirZ
   * the z coordinate of the ray's direction
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
   * @param result
   * a vector which will hold the resulting values of the parameter
   * *t* in the ray equation *p(t) = origin + t * dir* of the near and far point of intersection
   * iff the ray intersects the axis-aligned box
   * @return `true` if the given ray intersects the axis-aligned box; `false` otherwise
   */
  fun intersectRayAab(originX: Float, originY: Float, originZ: Float, dirX: Float, dirY: Float, dirZ: Float,
                      minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float, result: Vector2f): Boolean {
    val invDirX = 1.0f / dirX
    val invDirY = 1.0f / dirY
    val invDirZ = 1.0f / dirZ
    var tNear: Float
    var tFar: Float
    val tymin: Float
    val tymax: Float
    val tzmin: Float
    val tzmax: Float
    if (invDirX >= 0.0f) {
      tNear = (minX - originX) * invDirX
      tFar = (maxX - originX) * invDirX
    } else {
      tNear = (maxX - originX) * invDirX
      tFar = (minX - originX) * invDirX
    }
    if (invDirY >= 0.0f) {
      tymin = (minY - originY) * invDirY
      tymax = (maxY - originY) * invDirY
    } else {
      tymin = (maxY - originY) * invDirY
      tymax = (minY - originY) * invDirY
    }
    if (tNear > tymax || tymin > tFar)
      return false
    if (invDirZ >= 0.0f) {
      tzmin = (minZ - originZ) * invDirZ
      tzmax = (maxZ - originZ) * invDirZ
    } else {
      tzmin = (maxZ - originZ) * invDirZ
      tzmax = (minZ - originZ) * invDirZ
    }
    if (tNear > tzmax || tzmin > tFar)
      return false
    tNear = if (tymin > tNear || java.lang.Float.isNaN(tNear)) tymin else tNear
    tFar = if (tymax < tFar || java.lang.Float.isNaN(tFar)) tymax else tFar
    tNear = if (tzmin > tNear) tzmin else tNear
    tFar = if (tzmax < tFar) tzmax else tFar
    if (tNear < tFar && tFar >= 0.0f) {
      result.x = tNear
      result.y = tFar
      return true
    }
    return false
  }

  /**
   * Test whether the ray with the given `origin` and direction `dir`
   * intersects the axis-aligned box specified as its minimum corner `min` and maximum corner `max`,
   * and return the values of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the near and far point of intersection..
   *
   *
   * This method returns `true` for a ray whose origin lies inside the axis-aligned box.
   *
   *
   * If many boxes need to be tested against the same ray, then the [RayAabIntersection] class is likely more efficient.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .intersectRayAab
   * @see RayAabIntersection
   *
   *
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param min
   * the minimum corner of the axis-aligned box
   * @param max
   * the maximum corner of the axis-aligned box
   * @param result
   * a vector which will hold the resulting values of the parameter
   * *t* in the ray equation *p(t) = origin + t * dir* of the near and far point of intersection
   * iff the ray intersects the axis-aligned box
   * @return `true` if the given ray intersects the axis-aligned box; `false` otherwise
   */
  fun intersectRayAab(origin: Vector3fc, dir: Vector3fc, min: Vector3fc, max: Vector3fc, result: Vector2f): Boolean {
    return intersectRayAab(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, min.x, min.y, min.z, max.x, max.y, max.z, result)
  }

  /**
   * Test whether the given ray intersects given the axis-aligned box
   * and return the values of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the near and far point of intersection..
   *
   *
   * This method returns `true` for a ray whose origin lies inside the axis-aligned box.
   *
   *
   * If many boxes need to be tested against the same ray, then the [RayAabIntersection] class is likely more efficient.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .intersectRayAab
   * @see RayAabIntersection
   *
   *
   * @param ray
   * the ray
   * @param aabb
   * the AABB
   * @param result
   * a vector which will hold the resulting values of the parameter
   * *t* in the ray equation *p(t) = origin + t * dir* of the near and far point of intersection
   * iff the ray intersects the axis-aligned box
   * @return `true` if the given ray intersects the axis-aligned box; `false` otherwise
   */
  fun intersectRayAab(ray: Rayf, aabb: AABBf, result: Vector2f): Boolean {
    return intersectRayAab(ray.oX, ray.oY, ray.oZ, ray.dX, ray.dY, ray.dZ, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, result)
  }

  /**
   * Determine whether the undirected line segment with the end points <tt>(p0X, p0Y, p0Z)</tt> and <tt>(p1X, p1Y, p1Z)</tt>
   * intersects the axis-aligned box given as its minimum corner <tt>(minX, minY, minZ)</tt> and maximum corner <tt>(maxX, maxY, maxZ)</tt>,
   * and return the values of the parameter *t* in the ray equation *p(t) = origin + p0 * (p1 - p0)* of the near and far point of intersection.
   *
   *
   * This method returns `true` for a line segment whose either end point lies inside the axis-aligned box.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .intersectLineSegmentAab
   * @param p0X
   * the x coordinate of the line segment's first end point
   * @param p0Y
   * the y coordinate of the line segment's first end point
   * @param p0Z
   * the z coordinate of the line segment's first end point
   * @param p1X
   * the x coordinate of the line segment's second end point
   * @param p1Y
   * the y coordinate of the line segment's second end point
   * @param p1Z
   * the z coordinate of the line segment's second end point
   * @param minX
   * the x coordinate of one corner of the axis-aligned box
   * @param minY
   * the y coordinate of one corner of the axis-aligned box
   * @param minZ
   * the z coordinate of one corner of the axis-aligned box
   * @param maxX
   * the x coordinate of the opposite corner of the axis-aligned box
   * @param maxY
   * the y coordinate of the opposite corner of the axis-aligned box
   * @param maxZ
   * the y coordinate of the opposite corner of the axis-aligned box
   * @param result
   * a vector which will hold the resulting values of the parameter
   * *t* in the ray equation *p(t) = p0 + t * (p1 - p0)* of the near and far point of intersection
   * iff the line segment intersects the axis-aligned box
   * @return [.INSIDE] if the line segment lies completely inside of the axis-aligned box; or
   * [.OUTSIDE] if the line segment lies completely outside of the axis-aligned box; or
   * [.ONE_INTERSECTION] if one of the end points of the line segment lies inside of the axis-aligned box; or
   * [.TWO_INTERSECTION] if the line segment intersects two sides of the axis-aligned box
   * or lies on an edge or a side of the box
   */
  fun intersectLineSegmentAab(p0X: Float, p0Y: Float, p0Z: Float, p1X: Float, p1Y: Float, p1Z: Float,
                              minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float, result: Vector2f): Int {
    val dirX = p1X - p0X
    val dirY = p1Y - p0Y
    val dirZ = p1Z - p0Z
    val invDirX = 1.0f / dirX
    val invDirY = 1.0f / dirY
    val invDirZ = 1.0f / dirZ
    var tNear: Float
    var tFar: Float
    val tymin: Float
    val tymax: Float
    val tzmin: Float
    val tzmax: Float
    if (invDirX >= 0.0f) {
      tNear = (minX - p0X) * invDirX
      tFar = (maxX - p0X) * invDirX
    } else {
      tNear = (maxX - p0X) * invDirX
      tFar = (minX - p0X) * invDirX
    }
    if (invDirY >= 0.0f) {
      tymin = (minY - p0Y) * invDirY
      tymax = (maxY - p0Y) * invDirY
    } else {
      tymin = (maxY - p0Y) * invDirY
      tymax = (minY - p0Y) * invDirY
    }
    if (tNear > tymax || tymin > tFar)
      return OUTSIDE
    if (invDirZ >= 0.0f) {
      tzmin = (minZ - p0Z) * invDirZ
      tzmax = (maxZ - p0Z) * invDirZ
    } else {
      tzmin = (maxZ - p0Z) * invDirZ
      tzmax = (minZ - p0Z) * invDirZ
    }
    if (tNear > tzmax || tzmin > tFar)
      return OUTSIDE
    tNear = if (tymin > tNear || java.lang.Float.isNaN(tNear)) tymin else tNear
    tFar = if (tymax < tFar || java.lang.Float.isNaN(tFar)) tymax else tFar
    tNear = if (tzmin > tNear) tzmin else tNear
    tFar = if (tzmax < tFar) tzmax else tFar
    var type = OUTSIDE
    if (tNear < tFar && tNear <= 1.0f && tFar >= 0.0f) {
      if (tNear > 0.0f && tFar > 1.0f) {
        tFar = tNear
        type = ONE_INTERSECTION
      } else if (tNear < 0.0f && tFar < 1.0f) {
        tNear = tFar
        type = ONE_INTERSECTION
      } else if (tNear < 0.0f && tFar > 1.0f) {
        type = INSIDE
      } else {
        type = TWO_INTERSECTION
      }
      result.x = tNear
      result.y = tFar
    }
    return type
  }

  /**
   * Determine whether the undirected line segment with the end points `p0` and `p1`
   * intersects the axis-aligned box given as its minimum corner `min` and maximum corner `max`,
   * and return the values of the parameter *t* in the ray equation *p(t) = origin + p0 * (p1 - p0)* of the near and far point of intersection.
   *
   *
   * This method returns `true` for a line segment whose either end point lies inside the axis-aligned box.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .intersectLineSegmentAab
   * @param p0
   * the line segment's first end point
   * @param p1
   * the line segment's second end point
   * @param min
   * the minimum corner of the axis-aligned box
   * @param max
   * the maximum corner of the axis-aligned box
   * @param result
   * a vector which will hold the resulting values of the parameter
   * *t* in the ray equation *p(t) = p0 + t * (p1 - p0)* of the near and far point of intersection
   * iff the line segment intersects the axis-aligned box
   * @return [.INSIDE] if the line segment lies completely inside of the axis-aligned box; or
   * [.OUTSIDE] if the line segment lies completely outside of the axis-aligned box; or
   * [.ONE_INTERSECTION] if one of the end points of the line segment lies inside of the axis-aligned box; or
   * [.TWO_INTERSECTION] if the line segment intersects two sides of the axis-aligned box
   * or lies on an edge or a side of the box
   */
  fun intersectLineSegmentAab(p0: Vector3fc, p1: Vector3fc, min: Vector3fc, max: Vector3fc, result: Vector2f): Int {
    return intersectLineSegmentAab(p0.x, p0.y, p0.z, p1.x, p1.y, p1.z, min.x, min.y, min.z, max.x, max.y, max.z, result)
  }

  /**
   * Determine whether the given undirected line segment intersects the given axis-aligned box,
   * and return the values of the parameter *t* in the ray equation *p(t) = origin + p0 * (p1 - p0)* of the near and far point of intersection.
   *
   *
   * This method returns `true` for a line segment whose either end point lies inside the axis-aligned box.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .intersectLineSegmentAab
   * @param lineSegment
   * the line segment
   * @param aabb
   * the AABB
   * @param result
   * a vector which will hold the resulting values of the parameter
   * *t* in the ray equation *p(t) = p0 + t * (p1 - p0)* of the near and far point of intersection
   * iff the line segment intersects the axis-aligned box
   * @return [.INSIDE] if the line segment lies completely inside of the axis-aligned box; or
   * [.OUTSIDE] if the line segment lies completely outside of the axis-aligned box; or
   * [.ONE_INTERSECTION] if one of the end points of the line segment lies inside of the axis-aligned box; or
   * [.TWO_INTERSECTION] if the line segment intersects two sides of the axis-aligned box
   * or lies on an edge or a side of the box
   */
  fun intersectLineSegmentAab(lineSegment: LineSegmentf, aabb: AABBf, result: Vector2f): Int {
    return intersectLineSegmentAab(lineSegment.aX, lineSegment.aY, lineSegment.aZ, lineSegment.bX, lineSegment.bY, lineSegment.bZ, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, result)
  }

  /**
   * Test whether the given ray with the origin <tt>(originX, originY, originZ)</tt> and direction <tt>(dirX, dirY, dirZ)</tt>
   * intersects the axis-aligned box given as its minimum corner <tt>(minX, minY, minZ)</tt> and maximum corner <tt>(maxX, maxY, maxZ)</tt>.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the axis-aligned box.
   *
   *
   * If many boxes need to be tested against the same ray, then the [RayAabIntersection] class is likely more efficient.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .testRayAab
   * @see RayAabIntersection
   *
   *
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param originZ
   * the z coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param dirZ
   * the z coordinate of the ray's direction
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
   * @return `true` if the given ray intersects the axis-aligned box; `false` otherwise
   */
  fun testRayAab(originX: Float, originY: Float, originZ: Float, dirX: Float, dirY: Float, dirZ: Float,
                 minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float): Boolean {
    val invDirX = 1.0f / dirX
    val invDirY = 1.0f / dirY
    val invDirZ = 1.0f / dirZ
    var tNear: Float
    var tFar: Float
    val tymin: Float
    val tymax: Float
    val tzmin: Float
    val tzmax: Float
    if (invDirX >= 0.0f) {
      tNear = (minX - originX) * invDirX
      tFar = (maxX - originX) * invDirX
    } else {
      tNear = (maxX - originX) * invDirX
      tFar = (minX - originX) * invDirX
    }
    if (invDirY >= 0.0f) {
      tymin = (minY - originY) * invDirY
      tymax = (maxY - originY) * invDirY
    } else {
      tymin = (maxY - originY) * invDirY
      tymax = (minY - originY) * invDirY
    }
    if (tNear > tymax || tymin > tFar)
      return false
    if (invDirZ >= 0.0f) {
      tzmin = (minZ - originZ) * invDirZ
      tzmax = (maxZ - originZ) * invDirZ
    } else {
      tzmin = (maxZ - originZ) * invDirZ
      tzmax = (minZ - originZ) * invDirZ
    }
    if (tNear > tzmax || tzmin > tFar)
      return false
    tNear = if (tymin > tNear || java.lang.Float.isNaN(tNear)) tymin else tNear
    tFar = if (tymax < tFar || java.lang.Float.isNaN(tFar)) tymax else tFar
    tNear = if (tzmin > tNear) tzmin else tNear
    tFar = if (tzmax < tFar) tzmax else tFar
    return tNear < tFar && tFar >= 0.0f
  }

  /**
   * Test whether the ray with the given `origin` and direction `dir`
   * intersects the axis-aligned box specified as its minimum corner `min` and maximum corner `max`.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the axis-aligned box.
   *
   *
   * If many boxes need to be tested against the same ray, then the [RayAabIntersection] class is likely more efficient.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .testRayAab
   * @see RayAabIntersection
   *
   *
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param min
   * the minimum corner of the axis-aligned box
   * @param max
   * the maximum corner of the axis-aligned box
   * @return `true` if the given ray intersects the axis-aligned box; `false` otherwise
   */
  fun testRayAab(origin: Vector3fc, dir: Vector3fc, min: Vector3fc, max: Vector3fc): Boolean {
    return testRayAab(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, min.x, min.y, min.z, max.x, max.y, max.z)
  }

  /**
   * Test whether the given ray intersects the given axis-aligned box.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the axis-aligned box.
   *
   *
   * If many boxes need to be tested against the same ray, then the [RayAabIntersection] class is likely more efficient.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .testRayAab
   * @see RayAabIntersection
   *
   *
   * @param ray
   * the ray
   * @param aabb
   * the AABB
   * @return `true` if the given ray intersects the axis-aligned box; `false` otherwise
   */
  fun testRayAab(ray: Rayf, aabb: AABBf): Boolean {
    return testRayAab(ray.oX, ray.oY, ray.oZ, ray.dX, ray.dY, ray.dZ, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)
  }

  /**
   * Test whether the given ray with the origin <tt>(originX, originY, originZ)</tt> and direction <tt>(dirX, dirY, dirZ)</tt>
   * intersects the frontface of the triangle consisting of the three vertices <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt> and <tt>(v2X, v2Y, v2Z)</tt>.
   *
   *
   * This is an implementation of the [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf) method.
   *
   *
   * This test implements backface culling, that is, it will return `false` when the triangle is in clockwise
   * winding order assuming a *right-handed* coordinate system when seen along the ray's direction, even if the ray intersects the triangle.
   * This is in compliance with how OpenGL handles backface culling with default frontface/backface settings.
   *
   * @see .testRayTriangleFront
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param originZ
   * the z coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param dirZ
   * the z coordinate of the ray's direction
   * @param v0X
   * the x coordinate of the first vertex
   * @param v0Y
   * the y coordinate of the first vertex
   * @param v0Z
   * the z coordinate of the first vertex
   * @param v1X
   * the x coordinate of the second vertex
   * @param v1Y
   * the y coordinate of the second vertex
   * @param v1Z
   * the z coordinate of the second vertex
   * @param v2X
   * the x coordinate of the third vertex
   * @param v2Y
   * the y coordinate of the third vertex
   * @param v2Z
   * the z coordinate of the third vertex
   * @param epsilon
   * a small epsilon when testing rays that are almost parallel to the triangle
   * @return `true` if the given ray intersects the frontface of the triangle; `false` otherwise
   */
  fun testRayTriangleFront(originX: Float, originY: Float, originZ: Float, dirX: Float, dirY: Float, dirZ: Float,
                           v0X: Float, v0Y: Float, v0Z: Float, v1X: Float, v1Y: Float, v1Z: Float, v2X: Float, v2Y: Float, v2Z: Float,
                           epsilon: Float): Boolean {
    val edge1X = v1X - v0X
    val edge1Y = v1Y - v0Y
    val edge1Z = v1Z - v0Z
    val edge2X = v2X - v0X
    val edge2Y = v2Y - v0Y
    val edge2Z = v2Z - v0Z
    val pvecX = dirY * edge2Z - dirZ * edge2Y
    val pvecY = dirZ * edge2X - dirX * edge2Z
    val pvecZ = dirX * edge2Y - dirY * edge2X
    val det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ
    if (det < epsilon)
      return false
    val tvecX = originX - v0X
    val tvecY = originY - v0Y
    val tvecZ = originZ - v0Z
    val u = tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ
    if (u < 0.0f || u > det)
      return false
    val qvecX = tvecY * edge1Z - tvecZ * edge1Y
    val qvecY = tvecZ * edge1X - tvecX * edge1Z
    val qvecZ = tvecX * edge1Y - tvecY * edge1X
    val v = dirX * qvecX + dirY * qvecY + dirZ * qvecZ
    if (v < 0.0f || u + v > det)
      return false
    val invDet = 1.0f / det
    val t = (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) * invDet
    return t >= epsilon
  }

  /**
   * Test whether the ray with the given `origin` and the given `dir` intersects the frontface of the triangle consisting of the three vertices
   * `v0`, `v1` and `v2`.
   *
   *
   * This is an implementation of the [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf) method.
   *
   *
   * This test implements backface culling, that is, it will return `false` when the triangle is in clockwise
   * winding order assuming a *right-handed* coordinate system when seen along the ray's direction, even if the ray intersects the triangle.
   * This is in compliance with how OpenGL handles backface culling with default frontface/backface settings.
   *
   * @see .testRayTriangleFront
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param v0
   * the position of the first vertex
   * @param v1
   * the position of the second vertex
   * @param v2
   * the position of the third vertex
   * @param epsilon
   * a small epsilon when testing rays that are almost parallel to the triangle
   * @return `true` if the given ray intersects the frontface of the triangle; `false` otherwise
   */
  fun testRayTriangleFront(origin: Vector3fc, dir: Vector3fc, v0: Vector3fc, v1: Vector3fc, v2: Vector3fc, epsilon: Float): Boolean {
    return testRayTriangleFront(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, epsilon)
  }

  /**
   * Test whether the given ray with the origin <tt>(originX, originY, originZ)</tt> and direction <tt>(dirX, dirY, dirZ)</tt>
   * intersects the triangle consisting of the three vertices <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt> and <tt>(v2X, v2Y, v2Z)</tt>.
   *
   *
   * This is an implementation of the [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf) method.
   *
   *
   * This test does not take into account the winding order of the triangle, so a ray will intersect a front-facing triangle as well as a back-facing triangle.
   *
   * @see .testRayTriangle
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param originZ
   * the z coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param dirZ
   * the z coordinate of the ray's direction
   * @param v0X
   * the x coordinate of the first vertex
   * @param v0Y
   * the y coordinate of the first vertex
   * @param v0Z
   * the z coordinate of the first vertex
   * @param v1X
   * the x coordinate of the second vertex
   * @param v1Y
   * the y coordinate of the second vertex
   * @param v1Z
   * the z coordinate of the second vertex
   * @param v2X
   * the x coordinate of the third vertex
   * @param v2Y
   * the y coordinate of the third vertex
   * @param v2Z
   * the z coordinate of the third vertex
   * @param epsilon
   * a small epsilon when testing rays that are almost parallel to the triangle
   * @return `true` if the given ray intersects the frontface of the triangle; `false` otherwise
   */
  fun testRayTriangle(originX: Float, originY: Float, originZ: Float, dirX: Float, dirY: Float, dirZ: Float,
                      v0X: Float, v0Y: Float, v0Z: Float, v1X: Float, v1Y: Float, v1Z: Float, v2X: Float, v2Y: Float, v2Z: Float,
                      epsilon: Float): Boolean {
    val edge1X = v1X - v0X
    val edge1Y = v1Y - v0Y
    val edge1Z = v1Z - v0Z
    val edge2X = v2X - v0X
    val edge2Y = v2Y - v0Y
    val edge2Z = v2Z - v0Z
    val pvecX = dirY * edge2Z - dirZ * edge2Y
    val pvecY = dirZ * edge2X - dirX * edge2Z
    val pvecZ = dirX * edge2Y - dirY * edge2X
    val det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ
    if (det > -epsilon && det < epsilon)
      return false
    val tvecX = originX - v0X
    val tvecY = originY - v0Y
    val tvecZ = originZ - v0Z
    val invDet = 1.0f / det
    val u = (tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ) * invDet
    if (u < 0.0f || u > 1.0f)
      return false
    val qvecX = tvecY * edge1Z - tvecZ * edge1Y
    val qvecY = tvecZ * edge1X - tvecX * edge1Z
    val qvecZ = tvecX * edge1Y - tvecY * edge1X
    val v = (dirX * qvecX + dirY * qvecY + dirZ * qvecZ) * invDet
    if (v < 0.0f || u + v > 1.0f)
      return false
    val t = (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) * invDet
    return t >= epsilon
  }

  /**
   * Test whether the ray with the given `origin` and the given `dir` intersects the frontface of the triangle consisting of the three vertices
   * `v0`, `v1` and `v2`.
   *
   *
   * This is an implementation of the [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf) method.
   *
   *
   * This test does not take into account the winding order of the triangle, so a ray will intersect a front-facing triangle as well as a back-facing triangle.
   *
   * @see .testRayTriangle
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param v0
   * the position of the first vertex
   * @param v1
   * the position of the second vertex
   * @param v2
   * the position of the third vertex
   * @param epsilon
   * a small epsilon when testing rays that are almost parallel to the triangle
   * @return `true` if the given ray intersects the frontface of the triangle; `false` otherwise
   */
  fun testRayTriangle(origin: Vector3fc, dir: Vector3fc, v0: Vector3fc, v1: Vector3fc, v2: Vector3fc, epsilon: Float): Boolean {
    return testRayTriangle(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, epsilon)
  }

  /**
   * Determine whether the given ray with the origin <tt>(originX, originY, originZ)</tt> and direction <tt>(dirX, dirY, dirZ)</tt>
   * intersects the frontface of the triangle consisting of the three vertices <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt> and <tt>(v2X, v2Y, v2Z)</tt>
   * and return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the point of intersection.
   *
   *
   * This is an implementation of the [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf) method.
   *
   *
   * This test implements backface culling, that is, it will return `false` when the triangle is in clockwise
   * winding order assuming a *right-handed* coordinate system when seen along the ray's direction, even if the ray intersects the triangle.
   * This is in compliance with how OpenGL handles backface culling with default frontface/backface settings.
   *
   * @see .testRayTriangleFront
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param originZ
   * the z coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param dirZ
   * the z coordinate of the ray's direction
   * @param v0X
   * the x coordinate of the first vertex
   * @param v0Y
   * the y coordinate of the first vertex
   * @param v0Z
   * the z coordinate of the first vertex
   * @param v1X
   * the x coordinate of the second vertex
   * @param v1Y
   * the y coordinate of the second vertex
   * @param v1Z
   * the z coordinate of the second vertex
   * @param v2X
   * the x coordinate of the third vertex
   * @param v2Y
   * the y coordinate of the third vertex
   * @param v2Z
   * the z coordinate of the third vertex
   * @param epsilon
   * a small epsilon when testing rays that are almost parallel to the triangle
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the point of intersection
   * if the ray intersects the frontface of the triangle; <tt>-1.0</tt> otherwise
   */
  fun intersectRayTriangleFront(originX: Float, originY: Float, originZ: Float, dirX: Float, dirY: Float, dirZ: Float,
                                v0X: Float, v0Y: Float, v0Z: Float, v1X: Float, v1Y: Float, v1Z: Float, v2X: Float, v2Y: Float, v2Z: Float,
                                epsilon: Float): Float {
    val edge1X = v1X - v0X
    val edge1Y = v1Y - v0Y
    val edge1Z = v1Z - v0Z
    val edge2X = v2X - v0X
    val edge2Y = v2Y - v0Y
    val edge2Z = v2Z - v0Z
    val pvecX = dirY * edge2Z - dirZ * edge2Y
    val pvecY = dirZ * edge2X - dirX * edge2Z
    val pvecZ = dirX * edge2Y - dirY * edge2X
    val det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ
    if (det <= epsilon)
      return -1.0f
    val tvecX = originX - v0X
    val tvecY = originY - v0Y
    val tvecZ = originZ - v0Z
    val u = tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ
    if (u < 0.0f || u > det)
      return -1.0f
    val qvecX = tvecY * edge1Z - tvecZ * edge1Y
    val qvecY = tvecZ * edge1X - tvecX * edge1Z
    val qvecZ = tvecX * edge1Y - tvecY * edge1X
    val v = dirX * qvecX + dirY * qvecY + dirZ * qvecZ
    if (v < 0.0f || u + v > det)
      return -1.0f
    val invDet = 1.0f / det
    return (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) * invDet
  }

  /**
   * Determine whether the ray with the given `origin` and the given `dir` intersects the frontface of the triangle consisting of the three vertices
   * `v0`, `v1` and `v2` and return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the point of intersection.
   *
   *
   * This is an implementation of the [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf) method.
   *
   *
   * This test implements backface culling, that is, it will return `false` when the triangle is in clockwise
   * winding order assuming a *right-handed* coordinate system when seen along the ray's direction, even if the ray intersects the triangle.
   * This is in compliance with how OpenGL handles backface culling with default frontface/backface settings.
   *
   * @see .intersectRayTriangleFront
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param v0
   * the position of the first vertex
   * @param v1
   * the position of the second vertex
   * @param v2
   * the position of the third vertex
   * @param epsilon
   * a small epsilon when testing rays that are almost parallel to the triangle
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the point of intersection
   * if the ray intersects the frontface of the triangle; <tt>-1.0</tt> otherwise
   */
  fun intersectRayTriangleFront(origin: Vector3fc, dir: Vector3fc, v0: Vector3fc, v1: Vector3fc, v2: Vector3fc, epsilon: Float): Float {
    return intersectRayTriangleFront(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, epsilon)
  }

  /**
   * Determine whether the given ray with the origin <tt>(originX, originY, originZ)</tt> and direction <tt>(dirX, dirY, dirZ)</tt>
   * intersects the triangle consisting of the three vertices <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt> and <tt>(v2X, v2Y, v2Z)</tt>
   * and return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the point of intersection.
   *
   *
   * This is an implementation of the [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf) method.
   *
   *
   * This test does not take into account the winding order of the triangle, so a ray will intersect a front-facing triangle as well as a back-facing triangle.
   *
   * @see .testRayTriangle
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param originZ
   * the z coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param dirZ
   * the z coordinate of the ray's direction
   * @param v0X
   * the x coordinate of the first vertex
   * @param v0Y
   * the y coordinate of the first vertex
   * @param v0Z
   * the z coordinate of the first vertex
   * @param v1X
   * the x coordinate of the second vertex
   * @param v1Y
   * the y coordinate of the second vertex
   * @param v1Z
   * the z coordinate of the second vertex
   * @param v2X
   * the x coordinate of the third vertex
   * @param v2Y
   * the y coordinate of the third vertex
   * @param v2Z
   * the z coordinate of the third vertex
   * @param epsilon
   * a small epsilon when testing rays that are almost parallel to the triangle
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the point of intersection
   * if the ray intersects the triangle; <tt>-1.0</tt> otherwise
   */
  fun intersectRayTriangle(originX: Float, originY: Float, originZ: Float, dirX: Float, dirY: Float, dirZ: Float,
                           v0X: Float, v0Y: Float, v0Z: Float, v1X: Float, v1Y: Float, v1Z: Float, v2X: Float, v2Y: Float, v2Z: Float,
                           epsilon: Float): Float {
    val edge1X = v1X - v0X
    val edge1Y = v1Y - v0Y
    val edge1Z = v1Z - v0Z
    val edge2X = v2X - v0X
    val edge2Y = v2Y - v0Y
    val edge2Z = v2Z - v0Z
    val pvecX = dirY * edge2Z - dirZ * edge2Y
    val pvecY = dirZ * edge2X - dirX * edge2Z
    val pvecZ = dirX * edge2Y - dirY * edge2X
    val det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ
    if (det > -epsilon && det < epsilon)
      return -1.0f
    val tvecX = originX - v0X
    val tvecY = originY - v0Y
    val tvecZ = originZ - v0Z
    val invDet = 1.0f / det
    val u = (tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ) * invDet
    if (u < 0.0f || u > 1.0f)
      return -1.0f
    val qvecX = tvecY * edge1Z - tvecZ * edge1Y
    val qvecY = tvecZ * edge1X - tvecX * edge1Z
    val qvecZ = tvecX * edge1Y - tvecY * edge1X
    val v = (dirX * qvecX + dirY * qvecY + dirZ * qvecZ) * invDet
    return if (v < 0.0f || u + v > 1.0f) -1.0f else (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) * invDet
  }

  /**
   * Determine whether the ray with the given `origin` and the given `dir` intersects the triangle consisting of the three vertices
   * `v0`, `v1` and `v2` and return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the point of intersection.
   *
   *
   * This is an implementation of the [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf) method.
   *
   *
   * This test does not take into account the winding order of the triangle, so a ray will intersect a front-facing triangle as well as a back-facing triangle.
   *
   * @see .intersectRayTriangle
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param v0
   * the position of the first vertex
   * @param v1
   * the position of the second vertex
   * @param v2
   * the position of the third vertex
   * @param epsilon
   * a small epsilon when testing rays that are almost parallel to the triangle
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the point of intersection
   * if the ray intersects the triangle; <tt>-1.0</tt> otherwise
   */
  fun intersectRayTriangle(origin: Vector3fc, dir: Vector3fc, v0: Vector3fc, v1: Vector3fc, v2: Vector3fc, epsilon: Float): Float {
    return intersectRayTriangle(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, epsilon)
  }

  /**
   * Test whether the line segment with the end points <tt>(p0X, p0Y, p0Z)</tt> and <tt>(p1X, p1Y, p1Z)</tt>
   * intersects the triangle consisting of the three vertices <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt> and <tt>(v2X, v2Y, v2Z)</tt>,
   * regardless of the winding order of the triangle or the direction of the line segment between its two end points.
   *
   *
   * Reference: [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf)
   *
   * @see .testLineSegmentTriangle
   * @param p0X
   * the x coordinate of the line segment's first end point
   * @param p0Y
   * the y coordinate of the line segment's first end point
   * @param p0Z
   * the z coordinate of the line segment's first end point
   * @param p1X
   * the x coordinate of the line segment's second end point
   * @param p1Y
   * the y coordinate of the line segment's second end point
   * @param p1Z
   * the z coordinate of the line segment's second end point
   * @param v0X
   * the x coordinate of the first vertex
   * @param v0Y
   * the y coordinate of the first vertex
   * @param v0Z
   * the z coordinate of the first vertex
   * @param v1X
   * the x coordinate of the second vertex
   * @param v1Y
   * the y coordinate of the second vertex
   * @param v1Z
   * the z coordinate of the second vertex
   * @param v2X
   * the x coordinate of the third vertex
   * @param v2Y
   * the y coordinate of the third vertex
   * @param v2Z
   * the z coordinate of the third vertex
   * @param epsilon
   * a small epsilon when testing line segments that are almost parallel to the triangle
   * @return `true` if the given line segment intersects the triangle; `false` otherwise
   */
  fun testLineSegmentTriangle(p0X: Float, p0Y: Float, p0Z: Float, p1X: Float, p1Y: Float, p1Z: Float,
                              v0X: Float, v0Y: Float, v0Z: Float, v1X: Float, v1Y: Float, v1Z: Float, v2X: Float, v2Y: Float, v2Z: Float,
                              epsilon: Float): Boolean {
    val dirX = p1X - p0X
    val dirY = p1Y - p0Y
    val dirZ = p1Z - p0Z
    val t = intersectRayTriangle(p0X, p0Y, p0Z, dirX, dirY, dirZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z, epsilon)
    return t >= 0.0f && t <= 1.0f
  }

  /**
   * Test whether the line segment with the end points `p0` and `p1`
   * intersects the triangle consisting of the three vertices <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt> and <tt>(v2X, v2Y, v2Z)</tt>,
   * regardless of the winding order of the triangle or the direction of the line segment between its two end points.
   *
   *
   * Reference: [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf)
   *
   * @see .testLineSegmentTriangle
   * @param p0
   * the line segment's first end point
   * @param p1
   * the line segment's second end point
   * @param v0
   * the position of the first vertex
   * @param v1
   * the position of the second vertex
   * @param v2
   * the position of the third vertex
   * @param epsilon
   * a small epsilon when testing line segments that are almost parallel to the triangle
   * @return `true` if the given line segment intersects the triangle; `false` otherwise
   */
  fun testLineSegmentTriangle(p0: Vector3fc, p1: Vector3fc, v0: Vector3fc, v1: Vector3fc, v2: Vector3fc, epsilon: Float): Boolean {
    return testLineSegmentTriangle(p0.x, p0.y, p0.z, p1.x, p1.y, p1.z, v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, epsilon)
  }

  /**
   * Determine whether the line segment with the end points <tt>(p0X, p0Y, p0Z)</tt> and <tt>(p1X, p1Y, p1Z)</tt>
   * intersects the triangle consisting of the three vertices <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt> and <tt>(v2X, v2Y, v2Z)</tt>,
   * regardless of the winding order of the triangle or the direction of the line segment between its two end points,
   * and return the point of intersection.
   *
   *
   * Reference: [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf)
   *
   * @see .intersectLineSegmentTriangle
   * @param p0X
   * the x coordinate of the line segment's first end point
   * @param p0Y
   * the y coordinate of the line segment's first end point
   * @param p0Z
   * the z coordinate of the line segment's first end point
   * @param p1X
   * the x coordinate of the line segment's second end point
   * @param p1Y
   * the y coordinate of the line segment's second end point
   * @param p1Z
   * the z coordinate of the line segment's second end point
   * @param v0X
   * the x coordinate of the first vertex
   * @param v0Y
   * the y coordinate of the first vertex
   * @param v0Z
   * the z coordinate of the first vertex
   * @param v1X
   * the x coordinate of the second vertex
   * @param v1Y
   * the y coordinate of the second vertex
   * @param v1Z
   * the z coordinate of the second vertex
   * @param v2X
   * the x coordinate of the third vertex
   * @param v2Y
   * the y coordinate of the third vertex
   * @param v2Z
   * the z coordinate of the third vertex
   * @param epsilon
   * a small epsilon when testing line segments that are almost parallel to the triangle
   * @param intersectionPoint
   * the point of intersection
   * @return `true` if the given line segment intersects the triangle; `false` otherwise
   */
  fun intersectLineSegmentTriangle(p0X: Float, p0Y: Float, p0Z: Float, p1X: Float, p1Y: Float, p1Z: Float,
                                   v0X: Float, v0Y: Float, v0Z: Float, v1X: Float, v1Y: Float, v1Z: Float, v2X: Float, v2Y: Float, v2Z: Float,
                                   epsilon: Float, intersectionPoint: Vector3m): Boolean {
    val dirX = p1X - p0X
    val dirY = p1Y - p0Y
    val dirZ = p1Z - p0Z
    val t = intersectRayTriangle(p0X, p0Y, p0Z, dirX, dirY, dirZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z, epsilon)
    if (t >= 0.0f && t <= 1.0f) {
      intersectionPoint.x = p0X + dirX * t
      intersectionPoint.y = p0Y + dirY * t
      intersectionPoint.z = p0Z + dirZ * t
      return true
    }
    return false
  }

  /**
   * Determine whether the line segment with the end points `p0` and `p1`
   * intersects the triangle consisting of the three vertices <tt>(v0X, v0Y, v0Z)</tt>, <tt>(v1X, v1Y, v1Z)</tt> and <tt>(v2X, v2Y, v2Z)</tt>,
   * regardless of the winding order of the triangle or the direction of the line segment between its two end points,
   * and return the point of intersection.
   *
   *
   * Reference: [
 * Fast, Minimum Storage Ray/Triangle Intersection](http://www.graphics.cornell.edu/pubs/1997/MT97.pdf)
   *
   * @see .intersectLineSegmentTriangle
   * @param p0
   * the line segment's first end point
   * @param p1
   * the line segment's second end point
   * @param v0
   * the position of the first vertex
   * @param v1
   * the position of the second vertex
   * @param v2
   * the position of the third vertex
   * @param epsilon
   * a small epsilon when testing line segments that are almost parallel to the triangle
   * @param intersectionPoint
   * the point of intersection
   * @return `true` if the given line segment intersects the triangle; `false` otherwise
   */
  fun intersectLineSegmentTriangle(p0: Vector3fc, p1: Vector3fc, v0: Vector3fc, v1: Vector3fc, v2: Vector3fc, epsilon: Float, intersectionPoint: Vector3m): Boolean {
    return intersectLineSegmentTriangle(p0.x, p0.y, p0.z, p1.x, p1.y, p1.z, v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, epsilon, intersectionPoint)
  }

  /**
   * Determine whether the line segment with the end points <tt>(p0X, p0Y, p0Z)</tt> and <tt>(p1X, p1Y, p1Z)</tt>
   * intersects the plane given as the general plane equation *a*x + b*y + c*z + d = 0*,
   * and return the point of intersection.
   *
   * @param p0X
   * the x coordinate of the line segment's first end point
   * @param p0Y
   * the y coordinate of the line segment's first end point
   * @param p0Z
   * the z coordinate of the line segment's first end point
   * @param p1X
   * the x coordinate of the line segment's second end point
   * @param p1Y
   * the y coordinate of the line segment's second end point
   * @param p1Z
   * the z coordinate of the line segment's second end point
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the z factor in the plane equation
   * @param d
   * the constant in the plane equation
   * @param intersectionPoint
   * the point of intersection
   * @return `true` if the given line segment intersects the plane; `false` otherwise
   */
  fun intersectLineSegmentPlane(p0X: Float, p0Y: Float, p0Z: Float, p1X: Float, p1Y: Float, p1Z: Float,
                                a: Float, b: Float, c: Float, d: Float, intersectionPoint: Vector3m): Boolean {
    val dirX = p1X - p0X
    val dirY = p1Y - p0Y
    val dirZ = p1Z - p0Z
    val denom = a * dirX + b * dirY + c * dirZ
    val t = -(a * p0X + b * p0Y + c * p0Z + d) / denom
    if (t >= 0.0f && t <= 1.0f) {
      intersectionPoint.x = p0X + t * dirX
      intersectionPoint.y = p0Y + t * dirY
      intersectionPoint.z = p0Z + t * dirZ
      return true
    }
    return false
  }

  /**
   * Test whether the line with the general line equation *a*x + b*y + c = 0* intersects the circle with center
   * <tt>(centerX, centerY)</tt> and `radius`.
   *
   *
   * Reference: [http://math.stackexchange.com](http://math.stackexchange.com/questions/943383/determine-circle-of-intersection-of-plane-and-sphere)
   *
   * @param a
   * the x factor in the line equation
   * @param b
   * the y factor in the line equation
   * @param c
   * the constant in the line equation
   * @param centerX
   * the x coordinate of the circle's center
   * @param centerY
   * the y coordinate of the circle's center
   * @param radius
   * the radius of the circle
   * @return `true` iff the line intersects the circle; `false` otherwise
   */
  fun testLineCircle(a: Float, b: Float, c: Float, centerX: Float, centerY: Float, radius: Float): Boolean {
    val denom = Math.sqrt((a * a + b * b).toDouble()).toFloat()
    val dist = (a * centerX + b * centerY + c) / denom
    return -radius <= dist && dist <= radius
  }

  /**
   * Test whether the line with the general line equation *a*x + b*y + c = 0* intersects the circle with center
   * <tt>(centerX, centerY)</tt> and `radius`, and store the center of the line segment of
   * intersection in the <tt>(x, y)</tt> components of the supplied vector and the half-length of that line segment in the z component.
   *
   *
   * Reference: [http://math.stackexchange.com](http://math.stackexchange.com/questions/943383/determine-circle-of-intersection-of-plane-and-sphere)
   *
   * @param a
   * the x factor in the line equation
   * @param b
   * the y factor in the line equation
   * @param c
   * the constant in the line equation
   * @param centerX
   * the x coordinate of the circle's center
   * @param centerY
   * the y coordinate of the circle's center
   * @param radius
   * the radius of the circle
   * @param intersectionCenterAndHL
   * will hold the center of the line segment of intersection in the <tt>(x, y)</tt> components and the half-length in the z component
   * @return `true` iff the line intersects the circle; `false` otherwise
   */
  fun intersectLineCircle(a: Float, b: Float, c: Float, centerX: Float, centerY: Float, radius: Float, intersectionCenterAndHL: Vector3m): Boolean {
    val invDenom = 1.0f / Math.sqrt((a * a + b * b).toDouble()).toFloat()
    val dist = (a * centerX + b * centerY + c) * invDenom
    if (-radius <= dist && dist <= radius) {
      intersectionCenterAndHL.x = centerX + dist * a * invDenom
      intersectionCenterAndHL.y = centerY + dist * b * invDenom
      intersectionCenterAndHL.z = Math.sqrt((radius * radius - dist * dist).toDouble()).toFloat()
      return true
    }
    return false
  }

  /**
   * Test whether the line defined by the two points <tt>(x0, y0)</tt> and <tt>(x1, y1)</tt> intersects the circle with center
   * <tt>(centerX, centerY)</tt> and `radius`, and store the center of the line segment of
   * intersection in the <tt>(x, y)</tt> components of the supplied vector and the half-length of that line segment in the z component.
   *
   *
   * Reference: [http://math.stackexchange.com](http://math.stackexchange.com/questions/943383/determine-circle-of-intersection-of-plane-and-sphere)
   *
   * @param x0
   * the x coordinate of the first point on the line
   * @param y0
   * the y coordinate of the first point on the line
   * @param x1
   * the x coordinate of the second point on the line
   * @param y1
   * the y coordinate of the second point on the line
   * @param centerX
   * the x coordinate of the circle's center
   * @param centerY
   * the y coordinate of the circle's center
   * @param radius
   * the radius of the circle
   * @param intersectionCenterAndHL
   * will hold the center of the line segment of intersection in the <tt>(x, y)</tt> components and the half-length in the z component
   * @return `true` iff the line intersects the circle; `false` otherwise
   */
  fun intersectLineCircle(x0: Float, y0: Float, x1: Float, y1: Float, centerX: Float, centerY: Float, radius: Float, intersectionCenterAndHL: Vector3m): Boolean {
    // Build general line equation from two points and use the other method
    return intersectLineCircle(y0 - y1, x1 - x0, (x0 - x1) * y0 + (y1 - y0) * x0, centerX, centerY, radius, intersectionCenterAndHL)
  }

  /**
   * Test whether the axis-aligned rectangle with minimum corner <tt>(minX, minY)</tt> and maximum corner <tt>(maxX, maxY)</tt>
   * intersects the line with the general equation *a*x + b*y + c = 0*.
   *
   *
   * Reference: [http://www.lighthouse3d.com](http://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-testing-boxes-ii/) ("Geometric Approach - Testing Boxes II")
   *
   * @param minX
   * the x coordinate of the minimum corner of the axis-aligned rectangle
   * @param minY
   * the y coordinate of the minimum corner of the axis-aligned rectangle
   * @param maxX
   * the x coordinate of the maximum corner of the axis-aligned rectangle
   * @param maxY
   * the y coordinate of the maximum corner of the axis-aligned rectangle
   * @param a
   * the x factor in the line equation
   * @param b
   * the y factor in the line equation
   * @param c
   * the constant in the plane equation
   * @return `true` iff the axis-aligned rectangle intersects the line; `false` otherwise
   */
  fun testAarLine(minX: Float, minY: Float, maxX: Float, maxY: Float, a: Float, b: Float, c: Float): Boolean {
    val pX: Float
    val pY: Float
    val nX: Float
    val nY: Float
    if (a > 0.0f) {
      pX = maxX
      nX = minX
    } else {
      pX = minX
      nX = maxX
    }
    if (b > 0.0f) {
      pY = maxY
      nY = minY
    } else {
      pY = minY
      nY = maxY
    }
    val distN = c + a * nX + b * nY
    val distP = c + a * pX + b * pY
    return distN <= 0.0f && distP >= 0.0f
  }

  /**
   * Test whether the axis-aligned rectangle with minimum corner `min` and maximum corner `max`
   * intersects the line with the general equation *a*x + b*y + c = 0*.
   *
   *
   * Reference: [http://www.lighthouse3d.com](http://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-testing-boxes-ii/) ("Geometric Approach - Testing Boxes II")
   *
   * @param min
   * the minimum corner of the axis-aligned rectangle
   * @param max
   * the maximum corner of the axis-aligned rectangle
   * @param a
   * the x factor in the line equation
   * @param b
   * the y factor in the line equation
   * @param c
   * the constant in the line equation
   * @return `true` iff the axis-aligned rectangle intersects the line; `false` otherwise
   */
  fun testAarLine(min: Vector2fc, max: Vector2fc, a: Float, b: Float, c: Float): Boolean {
    return testAarLine(min.x, min.y, max.x, max.y, a, b, c)
  }

  /**
   * Test whether the axis-aligned rectangle with minimum corner <tt>(minX, minY)</tt> and maximum corner <tt>(maxX, maxY)</tt>
   * intersects the line defined by the two points <tt>(x0, y0)</tt> and <tt>(x1, y1)</tt>.
   *
   *
   * Reference: [http://www.lighthouse3d.com](http://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-testing-boxes-ii/) ("Geometric Approach - Testing Boxes II")
   *
   * @param minX
   * the x coordinate of the minimum corner of the axis-aligned rectangle
   * @param minY
   * the y coordinate of the minimum corner of the axis-aligned rectangle
   * @param maxX
   * the x coordinate of the maximum corner of the axis-aligned rectangle
   * @param maxY
   * the y coordinate of the maximum corner of the axis-aligned rectangle
   * @param x0
   * the x coordinate of the first point on the line
   * @param y0
   * the y coordinate of the first point on the line
   * @param x1
   * the x coordinate of the second point on the line
   * @param y1
   * the y coordinate of the second point on the line
   * @return `true` iff the axis-aligned rectangle intersects the line; `false` otherwise
   */
  fun testAarLine(minX: Float, minY: Float, maxX: Float, maxY: Float, x0: Float, y0: Float, x1: Float, y1: Float): Boolean {
    val a = y0 - y1
    val b = x1 - x0
    val c = -b * y0 - a * x0
    return testAarLine(minX, minY, maxX, maxY, a, b, c)
  }

  /**
   * Test whether the axis-aligned rectangle with minimum corner <tt>(minXA, minYA)</tt> and maximum corner <tt>(maxXA, maxYA)</tt>
   * intersects the axis-aligned rectangle with minimum corner <tt>(minXB, minYB)</tt> and maximum corner <tt>(maxXB, maxYB)</tt>.
   *
   * @param minXA
   * the x coordinate of the minimum corner of the first axis-aligned rectangle
   * @param minYA
   * the y coordinate of the minimum corner of the first axis-aligned rectangle
   * @param maxXA
   * the x coordinate of the maximum corner of the first axis-aligned rectangle
   * @param maxYA
   * the y coordinate of the maximum corner of the first axis-aligned rectangle
   * @param minXB
   * the x coordinate of the minimum corner of the second axis-aligned rectangle
   * @param minYB
   * the y coordinate of the minimum corner of the second axis-aligned rectangle
   * @param maxXB
   * the x coordinate of the maximum corner of the second axis-aligned rectangle
   * @param maxYB
   * the y coordinate of the maximum corner of the second axis-aligned rectangle
   * @return `true` iff both axis-aligned rectangles intersect; `false` otherwise
   */
  fun testAarAar(minXA: Float, minYA: Float, maxXA: Float, maxYA: Float, minXB: Float, minYB: Float, maxXB: Float, maxYB: Float): Boolean {
    return maxXA >= minXB && maxYA >= minYB && minXA <= maxXB && minYA <= maxYB
  }

  /**
   * Test whether the axis-aligned rectangle with minimum corner `minA` and maximum corner `maxA`
   * intersects the axis-aligned rectangle with minimum corner `minB` and maximum corner `maxB`.
   *
   * @param minA
   * the minimum corner of the first axis-aligned rectangle
   * @param maxA
   * the maximum corner of the first axis-aligned rectangle
   * @param minB
   * the minimum corner of the second axis-aligned rectangle
   * @param maxB
   * the maximum corner of the second axis-aligned rectangle
   * @return `true` iff both axis-aligned rectangles intersect; `false` otherwise
   */
  fun testAarAar(minA: Vector2fc, maxA: Vector2fc, minB: Vector2fc, maxB: Vector2fc): Boolean {
    return testAarAar(minA.x, minA.y, maxA.x, maxA.y, minB.x, minB.y, maxB.x, maxB.y)
  }

  /**
   * Test whether a given circle with center <tt>(aX, aY)</tt> and radius `aR` and travelled distance vector <tt>(maX, maY)</tt>
   * intersects a given static circle with center <tt>(bX, bY)</tt> and radius `bR`.
   *
   *
   * Note that the case of two moving circles can always be reduced to this case by expressing the moved distance of one of the circles relative
   * to the other.
   *
   *
   * Reference: [https://www.gamasutra.com](https://www.gamasutra.com/view/feature/131424/pool_hall_lessons_fast_accurate_.php?page=2)
   *
   * @param aX
   * the x coordinate of the first circle's center
   * @param aY
   * the y coordinate of the first circle's center
   * @param maX
   * the x coordinate of the first circle's travelled distance vector
   * @param maY
   * the y coordinate of the first circle's travelled distance vector
   * @param aR
   * the radius of the first circle
   * @param bX
   * the x coordinate of the second circle's center
   * @param bY
   * the y coordinate of the second circle's center
   * @param bR
   * the radius of the second circle
   * @return `true` if both circle intersect; `false` otherwise
   */
  fun testMovingCircleCircle(aX: Float, aY: Float, maX: Float, maY: Float, aR: Float, bX: Float, bY: Float, bR: Float): Boolean {
    val aRbR = aR + bR
    val dist = Math.sqrt(((aX - bX) * (aX - bX) + (aY - bY) * (aY - bY)).toDouble()).toFloat() - aRbR
    val mLen = Math.sqrt((maX * maX + maY * maY).toDouble()).toFloat()
    if (mLen < dist)
      return false
    val invMLen = 1.0f / mLen
    val nX = maX * invMLen
    val nY = maY * invMLen
    val cX = bX - aX
    val cY = bY - aY
    val nDotC = nX * cX + nY * cY
    if (nDotC <= 0.0f)
      return false
    val cLen = Math.sqrt((cX * cX + cY * cY).toDouble()).toFloat()
    val cLenNdotC = cLen * cLen - nDotC * nDotC
    val aRbR2 = aRbR * aRbR
    if (cLenNdotC >= aRbR2)
      return false
    val t = aRbR2 - cLenNdotC
    if (t < 0.0f)
      return false
    val distance = nDotC - Math.sqrt(t.toDouble()).toFloat()
    return if (mLen < distance) false else true
  }

  /**
   * Test whether a given circle with center `centerA` and radius `aR` and travelled distance vector `moveA`
   * intersects a given static circle with center `centerB` and radius `bR`.
   *
   *
   * Note that the case of two moving circles can always be reduced to this case by expressing the moved distance of one of the circles relative
   * to the other.
   *
   *
   * Reference: [https://www.gamasutra.com](https://www.gamasutra.com/view/feature/131424/pool_hall_lessons_fast_accurate_.php?page=2)
   *
   * @param centerA
   * the coordinates of the first circle's center
   * @param moveA
   * the coordinates of the first circle's travelled distance vector
   * @param aR
   * the radius of the first circle
   * @param centerB
   * the coordinates of the second circle's center
   * @param bR
   * the radius of the second circle
   * @return `true` if both circle intersect; `false` otherwise
   */
  fun testMovingCircleCircle(centerA: Vector2f, moveA: Vector2f, aR: Float, centerB: Vector2f, bR: Float): Boolean {
    return testMovingCircleCircle(centerA.x, centerA.y, moveA.x, moveA.y, aR, centerB.x, centerB.y, bR)
  }

  /**
   * Test whether the one circle with center <tt>(aX, aY)</tt> and square radius `radiusSquaredA` intersects the other
   * circle with center <tt>(bX, bY)</tt> and square radius `radiusSquaredB`, and store the center of the line segment of
   * intersection in the <tt>(x, y)</tt> components of the supplied vector and the half-length of that line segment in the z component.
   *
   *
   * This method returns `false` when one circle contains the other circle.
   *
   *
   * Reference: [http://gamedev.stackexchange.com](http://gamedev.stackexchange.com/questions/75756/sphere-sphere-intersection-and-circle-sphere-intersection)
   *
   * @param aX
   * the x coordinate of the first circle's center
   * @param aY
   * the y coordinate of the first circle's center
   * @param radiusSquaredA
   * the square of the first circle's radius
   * @param bX
   * the x coordinate of the second circle's center
   * @param bY
   * the y coordinate of the second circle's center
   * @param radiusSquaredB
   * the square of the second circle's radius
   * @param intersectionCenterAndHL
   * will hold the center of the circle of intersection in the <tt>(x, y, z)</tt> components and the radius in the w component
   * @return `true` iff both circles intersect; `false` otherwise
   */
  fun intersectCircleCircle(aX: Float, aY: Float, radiusSquaredA: Float, bX: Float, bY: Float, radiusSquaredB: Float, intersectionCenterAndHL: Vector3m): Boolean {
    val dX = bX - aX
    val dY = bY - aY
    val distSquared = dX * dX + dY * dY
    val h = 0.5f + (radiusSquaredA - radiusSquaredB) / distSquared
    val r_i = Math.sqrt((radiusSquaredA - h * h * distSquared).toDouble()).toFloat()
    if (r_i >= 0.0f) {
      intersectionCenterAndHL.x = aX + h * dX
      intersectionCenterAndHL.y = aY + h * dY
      intersectionCenterAndHL.z = r_i
      return true
    }
    return false
  }

  /**
   * Test whether the one circle with center `centerA` and square radius `radiusSquaredA` intersects the other
   * circle with center `centerB` and square radius `radiusSquaredB`, and store the center of the line segment of
   * intersection in the <tt>(x, y)</tt> components of the supplied vector and the half-length of that line segment in the z component.
   *
   *
   * This method returns `false` when one circle contains the other circle.
   *
   *
   * Reference: [http://gamedev.stackexchange.com](http://gamedev.stackexchange.com/questions/75756/sphere-sphere-intersection-and-circle-sphere-intersection)
   *
   * @param centerA
   * the first circle's center
   * @param radiusSquaredA
   * the square of the first circle's radius
   * @param centerB
   * the second circle's center
   * @param radiusSquaredB
   * the square of the second circle's radius
   * @param intersectionCenterAndHL
   * will hold the center of the line segment of intersection in the <tt>(x, y)</tt> components and the half-length in the z component
   * @return `true` iff both circles intersect; `false` otherwise
   */
  fun intersectCircleCircle(centerA: Vector2fc, radiusSquaredA: Float, centerB: Vector2fc, radiusSquaredB: Float, intersectionCenterAndHL: Vector3m): Boolean {
    return intersectCircleCircle(centerA.x, centerA.y, radiusSquaredA, centerB.x, centerB.y, radiusSquaredB, intersectionCenterAndHL)
  }

  /**
   * Test whether the one circle with center <tt>(aX, aY)</tt> and radius `rA` intersects the other circle with center <tt>(bX, bY)</tt> and radius `rB`.
   *
   *
   * This method returns `true` when one circle contains the other circle.
   *
   *
   * Reference: [http://math.stackexchange.com/](http://math.stackexchange.com/questions/275514/two-circles-overlap)
   *
   * @param aX
   * the x coordinate of the first circle's center
   * @param aY
   * the y coordinate of the first circle's center
   * @param rA
   * the square of the first circle's radius
   * @param bX
   * the x coordinate of the second circle's center
   * @param bY
   * the y coordinate of the second circle's center
   * @param rB
   * the square of the second circle's radius
   * @return `true` iff both circles intersect; `false` otherwise
   */
  fun testCircleCircle(aX: Float, aY: Float, rA: Float, bX: Float, bY: Float, rB: Float): Boolean {
    val d = (aX - bX) * (aX - bX) + (aY - bY) * (aY - bY)
    return d <= (rA + rB) * (rA + rB)
  }

  /**
   * Test whether the one circle with center `centerA` and square radius `radiusSquaredA` intersects the other
   * circle with center `centerB` and square radius `radiusSquaredB`.
   *
   *
   * This method returns `true` when one circle contains the other circle.
   *
   *
   * Reference: [http://gamedev.stackexchange.com](http://gamedev.stackexchange.com/questions/75756/sphere-sphere-intersection-and-circle-sphere-intersection)
   *
   * @param centerA
   * the first circle's center
   * @param radiusSquaredA
   * the square of the first circle's radius
   * @param centerB
   * the second circle's center
   * @param radiusSquaredB
   * the square of the second circle's radius
   * @return `true` iff both circles intersect; `false` otherwise
   */
  fun testCircleCircle(centerA: Vector2fc, radiusSquaredA: Float, centerB: Vector2fc, radiusSquaredB: Float): Boolean {
    return testCircleCircle(centerA.x, centerA.y, radiusSquaredA, centerB.x, centerB.y, radiusSquaredB)
  }

  /**
   * Determine the signed distance of the given point <tt>(pointX, pointY)</tt> to the line specified via its general plane equation
   * *a*x + b*y + c = 0*.
   *
   *
   * Reference: [http://mathworld.wolfram.com](http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html)
   *
   * @param pointX
   * the x coordinate of the point
   * @param pointY
   * the y coordinate of the point
   * @param a
   * the x factor in the plane equation
   * @param b
   * the y factor in the plane equation
   * @param c
   * the constant in the plane equation
   * @return the distance between the point and the line
   */
  fun distancePointLine(pointX: Float, pointY: Float, a: Float, b: Float, c: Float): Float {
    val denom = Math.sqrt((a * a + b * b).toDouble()).toFloat()
    return (a * pointX + b * pointY + c) / denom
  }

  /**
   * Determine the signed distance of the given point <tt>(pointX, pointY)</tt> to the line defined by the two points <tt>(x0, y0)</tt> and <tt>(x1, y1)</tt>.
   *
   *
   * Reference: [http://mathworld.wolfram.com](http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html)
   *
   * @param pointX
   * the x coordinate of the point
   * @param pointY
   * the y coordinate of the point
   * @param x0
   * the x coordinate of the first point on the line
   * @param y0
   * the y coordinate of the first point on the line
   * @param x1
   * the x coordinate of the second point on the line
   * @param y1
   * the y coordinate of the second point on the line
   * @return the distance between the point and the line
   */
  fun distancePointLine(pointX: Float, pointY: Float, x0: Float, y0: Float, x1: Float, y1: Float): Float {
    val dx = x1 - x0
    val dy = y1 - y0
    val denom = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    return (dx * (y0 - pointY) - (x0 - pointX) * dy) / denom
  }

  /**
   * Compute the distance of the given point <tt>(pX, pY, pZ)</tt> to the line defined by the two points <tt>(x0, y0, z0)</tt> and <tt>(x1, y1, z1)</tt>.
   *
   *
   * Reference: [http://mathworld.wolfram.com](http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html)
   *
   * @param pX
   * the x coordinate of the point
   * @param pY
   * the y coordinate of the point
   * @param pZ
   * the z coordinate of the point
   * @param x0
   * the x coordinate of the first point on the line
   * @param y0
   * the y coordinate of the first point on the line
   * @param z0
   * the z coordinate of the first point on the line
   * @param x1
   * the x coordinate of the second point on the line
   * @param y1
   * the y coordinate of the second point on the line
   * @param z1
   * the z coordinate of the second point on the line
   * @return the distance between the point and the line
   */
  fun distancePointLine(pX: Float, pY: Float, pZ: Float,
                        x0: Float, y0: Float, z0: Float, x1: Float, y1: Float, z1: Float): Float {
    val d21x = x1 - x0
    val d21y = y1 - y0
    val d21z = z1 - z0
    val d10x = x0 - pX
    val d10y = y0 - pY
    val d10z = z0 - pZ
    val cx = d21y * d10z - d21z * d10y
    val cy = d21z * d10x - d21x * d10z
    val cz = d21x * d10y - d21y * d10x
    return Math.sqrt(((cx * cx + cy * cy + cz * cz) / (d21x * d21x + d21y * d21y + d21z * d21z)).toDouble()).toFloat()
  }

  /**
   * Test whether the ray with given origin <tt>(originX, originY)</tt> and direction <tt>(dirX, dirY)</tt> intersects the line
   * containing the given point <tt>(pointX, pointY)</tt> and having the normal <tt>(normalX, normalY)</tt>, and return the
   * value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point.
   *
   *
   * This method returns <tt>-1.0</tt> if the ray does not intersect the line, because it is either parallel to the line or its direction points
   * away from the line or the ray's origin is on the *negative* side of the line (i.e. the line's normal points away from the ray's origin).
   *
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param pointX
   * the x coordinate of a point on the line
   * @param pointY
   * the y coordinate of a point on the line
   * @param normalX
   * the x coordinate of the line's normal
   * @param normalY
   * the y coordinate of the line's normal
   * @param epsilon
   * some small epsilon for when the ray is parallel to the line
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point, if the ray
   * intersects the line; <tt>-1.0</tt> otherwise
   */
  fun intersectRayLine(originX: Float, originY: Float, dirX: Float, dirY: Float, pointX: Float, pointY: Float, normalX: Float, normalY: Float, epsilon: Float): Float {
    val denom = normalX * dirX + normalY * dirY
    if (denom < epsilon) {
      val t = ((pointX - originX) * normalX + (pointY - originY) * normalY) / denom
      if (t >= 0.0f)
        return t
    }
    return -1.0f
  }

  /**
   * Test whether the ray with given `origin` and direction `dir` intersects the line
   * containing the given `point` and having the given `normal`, and return the
   * value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point.
   *
   *
   * This method returns <tt>-1.0</tt> if the ray does not intersect the line, because it is either parallel to the line or its direction points
   * away from the line or the ray's origin is on the *negative* side of the line (i.e. the line's normal points away from the ray's origin).
   *
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param point
   * a point on the line
   * @param normal
   * the line's normal
   * @param epsilon
   * some small epsilon for when the ray is parallel to the line
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point, if the ray
   * intersects the line; <tt>-1.0</tt> otherwise
   */
  fun intersectRayLine(origin: Vector2fc, dir: Vector2fc, point: Vector2fc, normal: Vector2fc, epsilon: Float): Float {
    return intersectRayLine(origin.x, origin.y, dir.x, dir.y, point.x, point.y, normal.x, normal.y, epsilon)
  }

  /**
   * Determine whether the ray with given origin <tt>(originX, originY)</tt> and direction <tt>(dirX, dirY)</tt> intersects the undirected line segment
   * given by the two end points <tt>(aX, bY)</tt> and <tt>(bX, bY)</tt>, and return the value of the parameter *t* in the ray equation
   * *p(t) = origin + t * dir* of the intersection point, if any.
   *
   *
   * This method returns <tt>-1.0</tt> if the ray does not intersect the line segment.
   *
   * @see .intersectRayLineSegment
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param aX
   * the x coordinate of the line segment's first end point
   * @param aY
   * the y coordinate of the line segment's first end point
   * @param bX
   * the x coordinate of the line segment's second end point
   * @param bY
   * the y coordinate of the line segment's second end point
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point, if the ray
   * intersects the line segment; <tt>-1.0</tt> otherwise
   */
  fun intersectRayLineSegment(originX: Float, originY: Float, dirX: Float, dirY: Float, aX: Float, aY: Float, bX: Float, bY: Float): Float {
    val v1X = originX - aX
    val v1Y = originY - aY
    val v2X = bX - aX
    val v2Y = bY - aY
    val invV23 = 1.0f / (v2Y * dirX - v2X * dirY)
    val t1 = (v2X * v1Y - v2Y * v1X) * invV23
    val t2 = (v1Y * dirX - v1X * dirY) * invV23
    return if (t1 >= 0.0f && t2 >= 0.0f && t2 <= 1.0f) t1 else -1.0f
  }

  /**
   * Determine whether the ray with given `origin` and direction `dir` intersects the undirected line segment
   * given by the two end points `a` and `b`, and return the value of the parameter *t* in the ray equation
   * *p(t) = origin + t * dir* of the intersection point, if any.
   *
   *
   * This method returns <tt>-1.0</tt> if the ray does not intersect the line segment.
   *
   * @see .intersectRayLineSegment
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param a
   * the line segment's first end point
   * @param b
   * the line segment's second end point
   * @return the value of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the intersection point, if the ray
   * intersects the line segment; <tt>-1.0</tt> otherwise
   */
  fun intersectRayLineSegment(origin: Vector2fc, dir: Vector2fc, a: Vector2fc, b: Vector2fc): Float {
    return intersectRayLineSegment(origin.x, origin.y, dir.x, dir.y, a.x, a.y, b.x, b.y)
  }

  /**
   * Test whether the axis-aligned rectangle with minimum corner <tt>(minX, minY)</tt> and maximum corner <tt>(maxX, maxY)</tt>
   * intersects the circle with the given center <tt>(centerX, centerY)</tt> and square radius `radiusSquared`.
   *
   *
   * Reference: [http://stackoverflow.com](http://stackoverflow.com/questions/4578967/cube-sphere-intersection-test#answer-4579069)
   *
   * @param minX
   * the x coordinate of the minimum corner of the axis-aligned rectangle
   * @param minY
   * the y coordinate of the minimum corner of the axis-aligned rectangle
   * @param maxX
   * the x coordinate of the maximum corner of the axis-aligned rectangle
   * @param maxY
   * the y coordinate of the maximum corner of the axis-aligned rectangle
   * @param centerX
   * the x coordinate of the circle's center
   * @param centerY
   * the y coordinate of the circle's center
   * @param radiusSquared
   * the square of the circle's radius
   * @return `true` iff the axis-aligned rectangle intersects the circle; `false` otherwise
   */
  fun testAarCircle(minX: Float, minY: Float, maxX: Float, maxY: Float, centerX: Float, centerY: Float, radiusSquared: Float): Boolean {
    var radius2 = radiusSquared
    if (centerX < minX) {
      val d = centerX - minX
      radius2 -= d * d
    } else if (centerX > maxX) {
      val d = centerX - maxX
      radius2 -= d * d
    }
    if (centerY < minY) {
      val d = centerY - minY
      radius2 -= d * d
    } else if (centerY > maxY) {
      val d = centerY - maxY
      radius2 -= d * d
    }
    return radius2 >= 0.0f
  }

  /**
   * Test whether the axis-aligned rectangle with minimum corner `min` and maximum corner `max`
   * intersects the circle with the given `center` and square radius `radiusSquared`.
   *
   *
   * Reference: [http://stackoverflow.com](http://stackoverflow.com/questions/4578967/cube-sphere-intersection-test#answer-4579069)
   *
   * @param min
   * the minimum corner of the axis-aligned rectangle
   * @param max
   * the maximum corner of the axis-aligned rectangle
   * @param center
   * the circle's center
   * @param radiusSquared
   * the squared of the circle's radius
   * @return `true` iff the axis-aligned rectangle intersects the circle; `false` otherwise
   */
  fun testAarCircle(min: Vector2fc, max: Vector2fc, center: Vector2fc, radiusSquared: Float): Boolean {
    return testAarCircle(min.x, min.y, max.x, max.y, center.x, center.y, radiusSquared)
  }

  /**
   * Determine the closest point on the triangle with the given vertices <tt>(v0X, v0Y)</tt>, <tt>(v1X, v1Y)</tt>, <tt>(v2X, v2Y)</tt>
   * between that triangle and the given point <tt>(pX, pY)</tt> and store that point into the given `result`.
   *
   *
   * Additionally, this method returns whether the closest point is a vertex ([.POINT_ON_TRIANGLE_VERTEX_0], [.POINT_ON_TRIANGLE_VERTEX_1], [.POINT_ON_TRIANGLE_VERTEX_2])
   * of the triangle, lies on an edge ([.POINT_ON_TRIANGLE_EDGE_01], [.POINT_ON_TRIANGLE_EDGE_12], [.POINT_ON_TRIANGLE_EDGE_20])
   * or on the [face][.POINT_ON_TRIANGLE_FACE] of the triangle.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 5.1.5 "Closest Point on Triangle to Point"
   *
   * @param v0X
   * the x coordinate of the first vertex of the triangle
   * @param v0Y
   * the y coordinate of the first vertex of the triangle
   * @param v1X
   * the x coordinate of the second vertex of the triangle
   * @param v1Y
   * the y coordinate of the second vertex of the triangle
   * @param v2X
   * the x coordinate of the third vertex of the triangle
   * @param v2Y
   * the y coordinate of the third vertex of the triangle
   * @param pX
   * the x coordinate of the point
   * @param pY
   * the y coordinate of the point
   * @param result
   * will hold the closest point
   * @return one of [.POINT_ON_TRIANGLE_VERTEX_0], [.POINT_ON_TRIANGLE_VERTEX_1], [.POINT_ON_TRIANGLE_VERTEX_2],
   * [.POINT_ON_TRIANGLE_EDGE_01], [.POINT_ON_TRIANGLE_EDGE_12], [.POINT_ON_TRIANGLE_EDGE_20] or
   * [.POINT_ON_TRIANGLE_FACE]
   */
  fun findClosestPointOnTriangle(v0X: Float, v0Y: Float, v1X: Float, v1Y: Float, v2X: Float, v2Y: Float, pX: Float, pY: Float, result: Vector2f): Int {
    val abX = v1X - v0X
    val abY = v1Y - v0Y
    val acX = v2X - v0X
    val acY = v2Y - v0Y
    val apX = pX - v0X
    val apY = pY - v0Y
    val d1 = abX * apX + abY * apY
    val d2 = acX * apX + acY * apY
    if (d1 <= 0.0f && d2 <= 0.0f) {
      result.x = v0X
      result.y = v0Y
      return POINT_ON_TRIANGLE_VERTEX_0
    }
    val bpX = pX - v1X
    val bpY = pY - v1Y
    val d3 = abX * bpX + abY * bpY
    val d4 = acX * bpX + acY * bpY
    if (d3 >= 0.0f && d4 <= d3) {
      result.x = v1X
      result.y = v1Y
      return POINT_ON_TRIANGLE_VERTEX_1
    }
    val vc = d1 * d4 - d3 * d2
    if (vc <= 0.0f && d1 >= 0.0f && d3 <= 0.0f) {
      val v = d1 / (d1 - d3)
      result.x = v0X + v * abX
      result.y = v0Y + v * abY
      return POINT_ON_TRIANGLE_EDGE_01
    }
    val cpX = pX - v2X
    val cpY = pY - v2Y
    val d5 = abX * cpX + abY * cpY
    val d6 = acX * cpX + acY * cpY
    if (d6 >= 0.0f && d5 <= d6) {
      result.x = v2X
      result.y = v2Y
      return POINT_ON_TRIANGLE_VERTEX_2
    }
    val vb = d5 * d2 - d1 * d6
    if (vb <= 0.0f && d2 >= 0.0f && d6 <= 0.0f) {
      val w = d2 / (d2 - d6)
      result.x = v0X + w * acX
      result.y = v0Y + w * acY
      return POINT_ON_TRIANGLE_EDGE_20
    }
    val va = d3 * d6 - d5 * d4
    if (va <= 0.0f && d4 - d3 >= 0.0f && d5 - d6 >= 0.0f) {
      val w = (d4 - d3) / (d4 - d3 + d5 - d6)
      result.x = v1X + w * (v2X - v1X)
      result.y = v1Y + w * (v2Y - v1Y)
      return POINT_ON_TRIANGLE_EDGE_12
    }
    val denom = 1.0f / (va + vb + vc)
    val v = vb * denom
    val w = vc * denom
    result.x = v0X + abX * v + acX * w
    result.y = v0Y + abY * v + acY * w
    return POINT_ON_TRIANGLE_FACE
  }

  /**
   * Determine the closest point on the triangle with the vertices `v0`, `v1`, `v2`
   * between that triangle and the given point `p` and store that point into the given `result`.
   *
   *
   * Additionally, this method returns whether the closest point is a vertex ([.POINT_ON_TRIANGLE_VERTEX_0], [.POINT_ON_TRIANGLE_VERTEX_1], [.POINT_ON_TRIANGLE_VERTEX_2])
   * of the triangle, lies on an edge ([.POINT_ON_TRIANGLE_EDGE_01], [.POINT_ON_TRIANGLE_EDGE_12], [.POINT_ON_TRIANGLE_EDGE_20])
   * or on the [face][.POINT_ON_TRIANGLE_FACE] of the triangle.
   *
   *
   * Reference: Book "Real-Time Collision Detection" chapter 5.1.5 "Closest Point on Triangle to Point"
   *
   * @param v0
   * the first vertex of the triangle
   * @param v1
   * the second vertex of the triangle
   * @param v2
   * the third vertex of the triangle
   * @param p
   * the point
   * @param result
   * will hold the closest point
   * @return one of [.POINT_ON_TRIANGLE_VERTEX_0], [.POINT_ON_TRIANGLE_VERTEX_1], [.POINT_ON_TRIANGLE_VERTEX_2],
   * [.POINT_ON_TRIANGLE_EDGE_01], [.POINT_ON_TRIANGLE_EDGE_12], [.POINT_ON_TRIANGLE_EDGE_20] or
   * [.POINT_ON_TRIANGLE_FACE]
   */
  fun findClosestPointOnTriangle(v0: Vector2fc, v1: Vector2fc, v2: Vector2fc, p: Vector2fc, result: Vector2f): Int {
    return findClosestPointOnTriangle(v0.x, v0.y, v1.x, v1.y, v2.x, v2.y, p.x, p.y, result)
  }

  /**
   * Test whether the given ray with the origin <tt>(originX, originY)</tt> and direction <tt>(dirX, dirY)</tt>
   * intersects the given circle with center <tt>(centerX, centerY)</tt> and square radius `radiusSquared`,
   * and store the values of the parameter *t* in the ray equation *p(t) = origin + t * dir* for both points (near
   * and far) of intersections into the given `result` vector.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the circle.
   *
   *
   * Reference: [http://www.scratchapixel.com/](http://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection)
   *
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param centerX
   * the x coordinate of the circle's center
   * @param centerY
   * the y coordinate of the circle's center
   * @param radiusSquared
   * the circle radius squared
   * @param result
   * a vector that will contain the values of the parameter *t* in the ray equation
   * *p(t) = origin + t * dir* for both points (near, far) of intersections with the circle
   * @return `true` if the ray intersects the circle; `false` otherwise
   */
  fun intersectRayCircle(originX: Float, originY: Float, dirX: Float, dirY: Float,
                         centerX: Float, centerY: Float, radiusSquared: Float, result: Vector2f): Boolean {
    val Lx = centerX - originX
    val Ly = centerY - originY
    val tca = Lx * dirX + Ly * dirY
    val d2 = Lx * Lx + Ly * Ly - tca * tca
    if (d2 > radiusSquared)
      return false
    val thc = Math.sqrt((radiusSquared - d2).toDouble()).toFloat()
    val t0 = tca - thc
    val t1 = tca + thc
    if (t0 < t1 && t1 >= 0.0f) {
      result.x = t0
      result.y = t1
      return true
    }
    return false
  }

  /**
   * Test whether the ray with the given `origin` and direction `dir`
   * intersects the circle with the given `center` and square radius `radiusSquared`,
   * and store the values of the parameter *t* in the ray equation *p(t) = origin + t * dir* for both points (near
   * and far) of intersections into the given `result` vector.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the circle.
   *
   *
   * Reference: [http://www.scratchapixel.com/](http://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection)
   *
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param center
   * the circle's center
   * @param radiusSquared
   * the circle radius squared
   * @param result
   * a vector that will contain the values of the parameter *t* in the ray equation
   * *p(t) = origin + t * dir* for both points (near, far) of intersections with the circle
   * @return `true` if the ray intersects the circle; `false` otherwise
   */
  fun intersectRayCircle(origin: Vector2fc, dir: Vector2fc, center: Vector2fc, radiusSquared: Float, result: Vector2f): Boolean {
    return intersectRayCircle(origin.x, origin.y, dir.x, dir.y, center.x, center.y, radiusSquared, result)
  }

  /**
   * Test whether the given ray with the origin <tt>(originX, originY)</tt> and direction <tt>(dirX, dirY)</tt>
   * intersects the given circle with center <tt>(centerX, centerY)</tt> and square radius `radiusSquared`.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the circle.
   *
   *
   * Reference: [http://www.scratchapixel.com/](http://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection)
   *
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param centerX
   * the x coordinate of the circle's center
   * @param centerY
   * the y coordinate of the circle's center
   * @param radiusSquared
   * the circle radius squared
   * @return `true` if the ray intersects the circle; `false` otherwise
   */
  fun testRayCircle(originX: Float, originY: Float, dirX: Float, dirY: Float,
                    centerX: Float, centerY: Float, radiusSquared: Float): Boolean {
    val Lx = centerX - originX
    val Ly = centerY - originY
    val tca = Lx * dirX + Ly * dirY
    val d2 = Lx * Lx + Ly * Ly - tca * tca
    if (d2 > radiusSquared)
      return false
    val thc = Math.sqrt((radiusSquared - d2).toDouble()).toFloat()
    val t0 = tca - thc
    val t1 = tca + thc
    return t0 < t1 && t1 >= 0.0f
  }

  /**
   * Test whether the ray with the given `origin` and direction `dir`
   * intersects the circle with the given `center` and square radius.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the circle.
   *
   *
   * Reference: [http://www.scratchapixel.com/](http://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection)
   *
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param center
   * the circle's center
   * @param radiusSquared
   * the circle radius squared
   * @return `true` if the ray intersects the circle; `false` otherwise
   */
  fun testRayCircle(origin: Vector2fc, dir: Vector2fc, center: Vector2fc, radiusSquared: Float): Boolean {
    return testRayCircle(origin.x, origin.y, dir.x, dir.y, center.x, center.y, radiusSquared)
  }

  /**
   * Determine whether the given ray with the origin <tt>(originX, originY)</tt> and direction <tt>(dirX, dirY)</tt>
   * intersects the axis-aligned rectangle given as its minimum corner <tt>(minX, minY)</tt> and maximum corner <tt>(maxX, maxY)</tt>,
   * and return the values of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the near and far point of intersection
   * as well as the side of the axis-aligned rectangle the ray intersects.
   *
   *
   * This method also detects an intersection for a ray whose origin lies inside the axis-aligned rectangle.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .intersectRayAar
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param minX
   * the x coordinate of the minimum corner of the axis-aligned rectangle
   * @param minY
   * the y coordinate of the minimum corner of the axis-aligned rectangle
   * @param maxX
   * the x coordinate of the maximum corner of the axis-aligned rectangle
   * @param maxY
   * the y coordinate of the maximum corner of the axis-aligned rectangle
   * @param result
   * a vector which will hold the values of the parameter *t* in the ray equation
   * *p(t) = origin + t * dir* of the near and far point of intersection
   * @return the side on which the near intersection occurred as one of
   * [.AAR_SIDE_MINX], [.AAR_SIDE_MINY], [.AAR_SIDE_MAXX] or [.AAR_SIDE_MAXY];
   * or <tt>-1</tt> if the ray does not intersect the axis-aligned rectangle;
   */
  fun intersectRayAar(originX: Float, originY: Float, dirX: Float, dirY: Float,
                      minX: Float, minY: Float, maxX: Float, maxY: Float, result: Vector2f): Int {
    val invDirX = 1.0f / dirX
    val invDirY = 1.0f / dirY
    var tNear: Float
    var tFar: Float
    val tymin: Float
    val tymax: Float
    if (invDirX >= 0.0f) {
      tNear = (minX - originX) * invDirX
      tFar = (maxX - originX) * invDirX
    } else {
      tNear = (maxX - originX) * invDirX
      tFar = (minX - originX) * invDirX
    }
    if (invDirY >= 0.0f) {
      tymin = (minY - originY) * invDirY
      tymax = (maxY - originY) * invDirY
    } else {
      tymin = (maxY - originY) * invDirY
      tymax = (minY - originY) * invDirY
    }
    if (tNear > tymax || tymin > tFar)
      return OUTSIDE
    tNear = if (tymin > tNear || java.lang.Float.isNaN(tNear)) tymin else tNear
    tFar = if (tymax < tFar || java.lang.Float.isNaN(tFar)) tymax else tFar
    var side = -1 // no intersection side
    if (tNear < tFar && tFar >= 0.0f) {
      val px = originX + tNear * dirX
      val py = originY + tNear * dirY
      result.x = tNear
      result.y = tFar
      val daX = Math.abs(px - minX)
      val daY = Math.abs(py - minY)
      val dbX = Math.abs(px - maxX)
      val dbY = Math.abs(py - maxY)
      side = 0 // min x coordinate
      var min = daX
      if (daY < min) {
        min = daY
        side = 1 // min y coordinate
      }
      if (dbX < min) {
        min = dbX
        side = 2 // max xcoordinate
      }
      if (dbY < min)
        side = 3 // max y coordinate
    }
    return side
  }

  /**
   * Determine whether the given ray with the given `origin` and direction `dir`
   * intersects the axis-aligned rectangle given as its minimum corner `min` and maximum corner `max`,
   * and return the values of the parameter *t* in the ray equation *p(t) = origin + t * dir* of the near and far point of intersection
   * as well as the side of the axis-aligned rectangle the ray intersects.
   *
   *
   * This method also detects an intersection for a ray whose origin lies inside the axis-aligned rectangle.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .intersectRayAar
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param min
   * the minimum corner of the axis-aligned rectangle
   * @param max
   * the maximum corner of the axis-aligned rectangle
   * @param result
   * a vector which will hold the values of the parameter *t* in the ray equation
   * *p(t) = origin + t * dir* of the near and far point of intersection
   * @return the side on which the near intersection occurred as one of
   * [.AAR_SIDE_MINX], [.AAR_SIDE_MINY], [.AAR_SIDE_MAXX] or [.AAR_SIDE_MAXY];
   * or <tt>-1</tt> if the ray does not intersect the axis-aligned rectangle;
   */
  fun intersectRayAar(origin: Vector2fc, dir: Vector2fc, min: Vector2fc, max: Vector2fc, result: Vector2f): Int {
    return intersectRayAar(origin.x, origin.y, dir.x, dir.y, min.x, min.y, max.x, max.y, result)
  }

  /**
   * Determine whether the undirected line segment with the end points <tt>(p0X, p0Y)</tt> and <tt>(p1X, p1Y)</tt>
   * intersects the axis-aligned rectangle given as its minimum corner <tt>(minX, minY)</tt> and maximum corner <tt>(maxX, maxY)</tt>,
   * and store the values of the parameter *t* in the ray equation *p(t) = p0 + t * (p1 - p0)* of the near and far point of intersection
   * into `result`.
   *
   *
   * This method also detects an intersection of a line segment whose either end point lies inside the axis-aligned rectangle.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .intersectLineSegmentAar
   * @param p0X
   * the x coordinate of the line segment's first end point
   * @param p0Y
   * the y coordinate of the line segment's first end point
   * @param p1X
   * the x coordinate of the line segment's second end point
   * @param p1Y
   * the y coordinate of the line segment's second end point
   * @param minX
   * the x coordinate of the minimum corner of the axis-aligned rectangle
   * @param minY
   * the y coordinate of the minimum corner of the axis-aligned rectangle
   * @param maxX
   * the x coordinate of the maximum corner of the axis-aligned rectangle
   * @param maxY
   * the y coordinate of the maximum corner of the axis-aligned rectangle
   * @param result
   * a vector which will hold the values of the parameter *t* in the ray equation
   * *p(t) = p0 + t * (p1 - p0)* of the near and far point of intersection
   * @return [.INSIDE] if the line segment lies completely inside of the axis-aligned rectangle; or
   * [.OUTSIDE] if the line segment lies completely outside of the axis-aligned rectangle; or
   * [.ONE_INTERSECTION] if one of the end points of the line segment lies inside of the axis-aligned rectangle; or
   * [.TWO_INTERSECTION] if the line segment intersects two edges of the axis-aligned rectangle or lies on one edge of the rectangle
   */
  fun intersectLineSegmentAar(p0X: Float, p0Y: Float, p1X: Float, p1Y: Float,
                              minX: Float, minY: Float, maxX: Float, maxY: Float, result: Vector2f): Int {
    val dirX = p1X - p0X
    val dirY = p1Y - p0Y
    val invDirX = 1.0f / dirX
    val invDirY = 1.0f / dirY
    var tNear: Float
    var tFar: Float
    val tymin: Float
    val tymax: Float
    if (invDirX >= 0.0f) {
      tNear = (minX - p0X) * invDirX
      tFar = (maxX - p0X) * invDirX
    } else {
      tNear = (maxX - p0X) * invDirX
      tFar = (minX - p0X) * invDirX
    }
    if (invDirY >= 0.0f) {
      tymin = (minY - p0Y) * invDirY
      tymax = (maxY - p0Y) * invDirY
    } else {
      tymin = (maxY - p0Y) * invDirY
      tymax = (minY - p0Y) * invDirY
    }
    if (tNear > tymax || tymin > tFar)
      return OUTSIDE
    tNear = if (tymin > tNear || java.lang.Float.isNaN(tNear)) tymin else tNear
    tFar = if (tymax < tFar || java.lang.Float.isNaN(tFar)) tymax else tFar
    var type = OUTSIDE
    if (tNear < tFar && tNear <= 1.0f && tFar >= 0.0f) {
      if (tNear > 0.0f && tFar > 1.0f) {
        tFar = tNear
        type = ONE_INTERSECTION
      } else if (tNear < 0.0f && tFar < 1.0f) {
        tNear = tFar
        type = ONE_INTERSECTION
      } else if (tNear < 0.0f && tFar > 1.0f) {
        type = INSIDE
      } else {
        type = TWO_INTERSECTION
      }
      result.x = tNear
      result.y = tFar
    }
    return type
  }

  /**
   * Determine whether the undirected line segment with the end points `p0` and `p1`
   * intersects the axis-aligned rectangle given as its minimum corner `min` and maximum corner `max`,
   * and store the values of the parameter *t* in the ray equation *p(t) = p0 + t * (p1 - p0)* of the near and far point of intersection
   * into `result`.
   *
   *
   * This method also detects an intersection of a line segment whose either end point lies inside the axis-aligned rectangle.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * #see [.intersectLineSegmentAar]
   *
   * @param p0
   * the line segment's first end point
   * @param p1
   * the line segment's second end point
   * @param min
   * the minimum corner of the axis-aligned rectangle
   * @param max
   * the maximum corner of the axis-aligned rectangle
   * @param result
   * a vector which will hold the values of the parameter *t* in the ray equation
   * *p(t) = p0 + t * (p1 - p0)* of the near and far point of intersection
   * @return [.INSIDE] if the line segment lies completely inside of the axis-aligned rectangle; or
   * [.OUTSIDE] if the line segment lies completely outside of the axis-aligned rectangle; or
   * [.ONE_INTERSECTION] if one of the end points of the line segment lies inside of the axis-aligned rectangle; or
   * [.TWO_INTERSECTION] if the line segment intersects two edges of the axis-aligned rectangle
   */
  fun intersectLineSegmentAar(p0: Vector2fc, p1: Vector2fc, min: Vector2fc, max: Vector2fc, result: Vector2f): Int {
    return intersectLineSegmentAar(p0.x, p0.y, p1.x, p1.y, min.x, min.y, max.x, max.y, result)
  }

  /**
   * Test whether the given ray with the origin <tt>(originX, originY)</tt> and direction <tt>(dirX, dirY)</tt>
   * intersects the given axis-aligned rectangle given as its minimum corner <tt>(minX, minY)</tt> and maximum corner <tt>(maxX, maxY)</tt>.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the axis-aligned rectangle.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .testRayAar
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param minX
   * the x coordinate of the minimum corner of the axis-aligned rectangle
   * @param minY
   * the y coordinate of the minimum corner of the axis-aligned rectangle
   * @param maxX
   * the x coordinate of the maximum corner of the axis-aligned rectangle
   * @param maxY
   * the y coordinate of the maximum corner of the axis-aligned rectangle
   * @return `true` if the given ray intersects the axis-aligned rectangle; `false` otherwise
   */
  fun testRayAar(originX: Float, originY: Float, dirX: Float, dirY: Float, minX: Float, minY: Float, maxX: Float, maxY: Float): Boolean {
    val invDirX = 1.0f / dirX
    val invDirY = 1.0f / dirY
    var tNear: Float
    var tFar: Float
    val tymin: Float
    val tymax: Float
    if (invDirX >= 0.0f) {
      tNear = (minX - originX) * invDirX
      tFar = (maxX - originX) * invDirX
    } else {
      tNear = (maxX - originX) * invDirX
      tFar = (minX - originX) * invDirX
    }
    if (invDirY >= 0.0f) {
      tymin = (minY - originY) * invDirY
      tymax = (maxY - originY) * invDirY
    } else {
      tymin = (maxY - originY) * invDirY
      tymax = (minY - originY) * invDirY
    }
    if (tNear > tymax || tymin > tFar)
      return false
    tNear = if (tymin > tNear || java.lang.Float.isNaN(tNear)) tymin else tNear
    tFar = if (tymax < tFar || java.lang.Float.isNaN(tFar)) tymax else tFar
    return tNear < tFar && tFar >= 0.0f
  }

  /**
   * Test whether the ray with the given `origin` and direction `dir`
   * intersects the given axis-aligned rectangle specified as its minimum corner `min` and maximum corner `max`.
   *
   *
   * This method returns `true` for a ray whose origin lies inside the axis-aligned rectangle.
   *
   *
   * Reference: [An Efficient and Robust Ray–Box Intersection](https://dl.acm.org/citation.cfm?id=1198748)
   *
   * @see .testRayAar
   * @param origin
   * the ray's origin
   * @param dir
   * the ray's direction
   * @param min
   * the minimum corner of the axis-aligned rectangle
   * @param max
   * the maximum corner of the axis-aligned rectangle
   * @return `true` if the given ray intersects the axis-aligned rectangle; `false` otherwise
   */
  fun testRayAar(origin: Vector2fc, dir: Vector2fc, min: Vector2fc, max: Vector2fc): Boolean {
    return testRayAar(origin.x, origin.y, dir.x, dir.y, min.x, min.y, max.x, max.y)
  }

  /**
   * Test whether the given point <tt>(pX, pY)</tt> lies inside the triangle with the vertices <tt>(v0X, v0Y)</tt>, <tt>(v1X, v1Y)</tt>, <tt>(v2X, v2Y)</tt>.
   *
   * @param pX
   * the x coordinate of the point
   * @param pY
   * the y coordinate of the point
   * @param v0X
   * the x coordinate of the first vertex of the triangle
   * @param v0Y
   * the y coordinate of the first vertex of the triangle
   * @param v1X
   * the x coordinate of the second vertex of the triangle
   * @param v1Y
   * the y coordinate of the second vertex of the triangle
   * @param v2X
   * the x coordinate of the third vertex of the triangle
   * @param v2Y
   * the y coordinate of the third vertex of the triangle
   * @return `true` iff the point lies inside the triangle; `false` otherwise
   */
  fun testPointTriangle(pX: Float, pY: Float, v0X: Float, v0Y: Float, v1X: Float, v1Y: Float, v2X: Float, v2Y: Float): Boolean {
    val b1 = (pX - v1X) * (v0Y - v1Y) - (v0X - v1X) * (pY - v1Y) < 0.0f
    val b2 = (pX - v2X) * (v1Y - v2Y) - (v1X - v2X) * (pY - v2Y) < 0.0f
    if (b1 != b2)
      return false
    val b3 = (pX - v0X) * (v2Y - v0Y) - (v2X - v0X) * (pY - v0Y) < 0.0f
    return b2 == b3
  }

  /**
   * Test whether the given `point` lies inside the triangle with the vertices `v0`, `v1`, `v2`.
   *
   * @param v0
   * the first vertex of the triangle
   * @param v1
   * the second vertex of the triangle
   * @param v2
   * the third vertex of the triangle
   * @param point
   * the point
   * @return `true` iff the point lies inside the triangle; `false` otherwise
   */
  fun testPointTriangle(point: Vector2fc, v0: Vector2fc, v1: Vector2fc, v2: Vector2fc): Boolean {
    return testPointTriangle(point.x, point.y, v0.x, v0.y, v1.x, v1.y, v2.x, v2.y)
  }

  /**
   * Test whether the given point <tt>(pX, pY)</tt> lies inside the axis-aligned rectangle with the minimum corner <tt>(minX, minY)</tt>
   * and maximum corner <tt>(maxX, maxY)</tt>.
   *
   * @param pX
   * the x coordinate of the point
   * @param pY
   * the y coordinate of the point
   * @param minX
   * the x coordinate of the minimum corner of the axis-aligned rectangle
   * @param minY
   * the y coordinate of the minimum corner of the axis-aligned rectangle
   * @param maxX
   * the x coordinate of the maximum corner of the axis-aligned rectangle
   * @param maxY
   * the y coordinate of the maximum corner of the axis-aligned rectangle
   * @return `true` iff the point lies inside the axis-aligned rectangle; `false` otherwise
   */
  fun testPointAar(pX: Float, pY: Float, minX: Float, minY: Float, maxX: Float, maxY: Float): Boolean {
    return pX >= minX && pY >= minY && pX <= maxX && pY <= maxY
  }

  /**
   * Test whether the point <tt>(pX, pY)</tt> lies inside the circle with center <tt>(centerX, centerY)</tt> and square radius `radiusSquared`.
   *
   * @param pX
   * the x coordinate of the point
   * @param pY
   * the y coordinate of the point
   * @param centerX
   * the x coordinate of the circle's center
   * @param centerY
   * the y coordinate of the circle's center
   * @param radiusSquared
   * the square radius of the circle
   * @return `true` iff the point lies inside the circle; `false` otherwise
   */
  fun testPointCircle(pX: Float, pY: Float, centerX: Float, centerY: Float, radiusSquared: Float): Boolean {
    val dx = pX - centerX
    val dy = pY - centerY
    val dx2 = dx * dx
    val dy2 = dy * dy
    return dx2 + dy2 <= radiusSquared
  }

  /**
   * Test whether the circle with center <tt>(centerX, centerY)</tt> and square radius `radiusSquared` intersects the triangle with counter-clockwise vertices
   * <tt>(v0X, v0Y)</tt>, <tt>(v1X, v1Y)</tt>, <tt>(v2X, v2Y)</tt>.
   *
   *
   * The vertices of the triangle must be specified in counter-clockwise order.
   *
   *
   * Reference: [http://www.phatcode.net/](http://www.phatcode.net/articles.php?id=459)
   *
   * @param centerX
   * the x coordinate of the circle's center
   * @param centerY
   * the y coordinate of the circle's center
   * @param radiusSquared
   * the square radius of the circle
   * @param v0X
   * the x coordinate of the first vertex of the triangle
   * @param v0Y
   * the y coordinate of the first vertex of the triangle
   * @param v1X
   * the x coordinate of the second vertex of the triangle
   * @param v1Y
   * the y coordinate of the second vertex of the triangle
   * @param v2X
   * the x coordinate of the third vertex of the triangle
   * @param v2Y
   * the y coordinate of the third vertex of the triangle
   * @return `true` iff the circle intersects the triangle; `false` otherwise
   */
  fun testCircleTriangle(centerX: Float, centerY: Float, radiusSquared: Float, v0X: Float, v0Y: Float, v1X: Float, v1Y: Float, v2X: Float, v2Y: Float): Boolean {
    val c1x = centerX - v0X
    val c1y = centerY - v0Y
    val c1sqr = c1x * c1x + c1y * c1y - radiusSquared
    if (c1sqr <= 0.0f)
      return true
    val c2x = centerX - v1X
    val c2y = centerY - v1Y
    val c2sqr = c2x * c2x + c2y * c2y - radiusSquared
    if (c2sqr <= 0.0f)
      return true
    val c3x = centerX - v2X
    val c3y = centerY - v2Y
    val c3sqr = c3x * c3x + c3y * c3y - radiusSquared
    if (c3sqr <= 0.0f)
      return true
    val e1x = v1X - v0X
    val e1y = v1Y - v0Y
    val e2x = v2X - v1X
    val e2y = v2Y - v1Y
    val e3x = v0X - v2X
    val e3y = v0Y - v2Y
    if (e1x * c1y - e1y * c1x >= 0.0f && e2x * c2y - e2y * c2x >= 0.0f && e3x * c3y - e3y * c3x >= 0.0f)
      return true
    var k = c1x * e1x + c1y * e1y
    if (k >= 0.0f) {
      val len = e1x * e1x + e1y * e1y
      if (k <= len) {
        if (c1sqr * len <= k * k)
          return true
      }
    }
    k = c2x * e2x + c2y * e2y
    if (k > 0.0f) {
      val len = e2x * e2x + e2y * e2y
      if (k <= len) {
        if (c2sqr * len <= k * k)
          return true
      }
    }
    k = c3x * e3x + c3y * e3y
    if (k >= 0.0f) {
      val len = e3x * e3x + e3y * e3y
      if (k < len) {
        if (c3sqr * len <= k * k)
          return true
      }
    }
    return false
  }

  /**
   * Test whether the circle with given `center` and square radius `radiusSquared` intersects the triangle with counter-clockwise vertices
   * `v0`, `v1`, `v2`.
   *
   *
   * The vertices of the triangle must be specified in counter-clockwise order.
   *
   *
   * Reference: [http://www.phatcode.net/](http://www.phatcode.net/articles.php?id=459)
   *
   * @param center
   * the circle's center
   * @param radiusSquared
   * the square radius of the circle
   * @param v0
   * the first vertex of the triangle
   * @param v1
   * the second vertex of the triangle
   * @param v2
   * the third vertex of the triangle
   * @return `true` iff the circle intersects the triangle; `false` otherwise
   */
  fun testCircleTriangle(center: Vector2fc, radiusSquared: Float, v0: Vector2fc, v1: Vector2fc, v2: Vector2fc): Boolean {
    return testCircleTriangle(center.x, center.y, radiusSquared, v0.x, v0.y, v1.x, v1.y, v2.x, v2.y)
  }

  /**
   * Determine whether the polygon specified by the given sequence of <tt>(x, y)</tt> coordinate pairs intersects with the ray
   * with given origin <tt>(originX, originY, originZ)</tt> and direction <tt>(dirX, dirY, dirZ)</tt>, and store the point of intersection
   * into the given vector `p`.
   *
   *
   * If the polygon intersects the ray, this method returns the index of the polygon edge intersecting the ray, that is, the index of the
   * first vertex of the directed line segment. The second vertex is always that index + 1, modulus the number of polygon vertices.
   *
   * @param verticesXY
   * the sequence of <tt>(x, y)</tt> coordinate pairs of all vertices of the polygon
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param p
   * will hold the point of intersection
   * @return the index of the first vertex of the polygon edge that intersects the ray; or <tt>-1</tt> if the ray does not intersect the polygon
   */
  fun intersectPolygonRay(verticesXY: FloatArray, originX: Float, originY: Float, dirX: Float, dirY: Float, p: Vector2f): Int {
    var nearestT = java.lang.Float.POSITIVE_INFINITY
    val count = verticesXY.size shr 1
    var edgeIndex = -1
    var aX = verticesXY[count - 1 shl 1]
    var aY = verticesXY[(count - 1 shl 1) + 1]
    for (i in 0 until count) {
      val bX = verticesXY[i shl 1]
      val bY = verticesXY[(i shl 1) + 1]
      val doaX = originX - aX
      val doaY = originY - aY
      val dbaX = bX - aX
      val dbaY = bY - aY
      val invDbaDir = 1.0f / (dbaY * dirX - dbaX * dirY)
      val t = (dbaX * doaY - dbaY * doaX) * invDbaDir
      if (t >= 0.0f && t < nearestT) {
        val t2 = (doaY * dirX - doaX * dirY) * invDbaDir
        if (t2 >= 0.0f && t2 <= 1.0f) {
          edgeIndex = (i - 1 + count) % count
          nearestT = t
          p.x = originX + t * dirX
          p.y = originY + t * dirY
        }
      }
      aX = bX
      aY = bY
    }
    return edgeIndex
  }

  /**
   * Determine whether the polygon specified by the given sequence of `vertices` intersects with the ray
   * with given origin <tt>(originX, originY, originZ)</tt> and direction <tt>(dirX, dirY, dirZ)</tt>, and store the point of intersection
   * into the given vector `p`.
   *
   *
   * If the polygon intersects the ray, this method returns the index of the polygon edge intersecting the ray, that is, the index of the
   * first vertex of the directed line segment. The second vertex is always that index + 1, modulus the number of polygon vertices.
   *
   * @param vertices
   * the sequence of <tt>(x, y)</tt> coordinate pairs of all vertices of the polygon
   * @param originX
   * the x coordinate of the ray's origin
   * @param originY
   * the y coordinate of the ray's origin
   * @param dirX
   * the x coordinate of the ray's direction
   * @param dirY
   * the y coordinate of the ray's direction
   * @param p
   * will hold the point of intersection
   * @return the index of the first vertex of the polygon edge that intersects the ray; or <tt>-1</tt> if the ray does not intersect the polygon
   */
  fun intersectPolygonRay(vertices: Array<Vector2fc>, originX: Float, originY: Float, dirX: Float, dirY: Float, p: Vector2f): Int {
    var nearestT = java.lang.Float.POSITIVE_INFINITY
    val count = vertices.size
    var edgeIndex = -1
    var aX = vertices[count - 1].x
    var aY = vertices[count - 1].y
    for (i in 0 until count) {
      val b = vertices[i]
      val bX = b.x
      val bY = b.y
      val doaX = originX - aX
      val doaY = originY - aY
      val dbaX = bX - aX
      val dbaY = bY - aY
      val invDbaDir = 1.0f / (dbaY * dirX - dbaX * dirY)
      val t = (dbaX * doaY - dbaY * doaX) * invDbaDir
      if (t >= 0.0f && t < nearestT) {
        val t2 = (doaY * dirX - doaX * dirY) * invDbaDir
        if (t2 >= 0.0f && t2 <= 1.0f) {
          edgeIndex = (i - 1 + count) % count
          nearestT = t
          p.x = originX + t * dirX
          p.y = originY + t * dirY
        }
      }
      aX = bX
      aY = bY
    }
    return edgeIndex
  }

  /**
   * Determine whether the two lines, specified via two points lying on each line, intersect each other, and store the point of intersection
   * into the given vector `p`.
   *
   * @param ps1x
   * the x coordinate of the first point on the first line
   * @param ps1y
   * the y coordinate of the first point on the first line
   * @param pe1x
   * the x coordinate of the second point on the first line
   * @param pe1y
   * the y coordinate of the second point on the first line
   * @param ps2x
   * the x coordinate of the first point on the second line
   * @param ps2y
   * the y coordinate of the first point on the second line
   * @param pe2x
   * the x coordinate of the second point on the second line
   * @param pe2y
   * the y coordinate of the second point on the second line
   * @param p
   * will hold the point of intersection
   * @return `true` iff the two lines intersect; `false` otherwise
   */
  fun intersectLineLine(ps1x: Float, ps1y: Float, pe1x: Float, pe1y: Float, ps2x: Float, ps2y: Float, pe2x: Float, pe2y: Float, p: Vector2f): Boolean {
    val d1x = ps1x - pe1x
    val d1y = pe1y - ps1y
    val d1ps1 = d1y * ps1x + d1x * ps1y
    val d2x = ps2x - pe2x
    val d2y = pe2y - ps2y
    val d2ps2 = d2y * ps2x + d2x * ps2y
    val det = d1y * d2x - d2y * d1x
    if (det == 0.0f)
      return false
    p.x = (d2x * d1ps1 - d1x * d2ps2) / det
    p.y = (d1y * d2ps2 - d2y * d1ps1) / det
    return true
  }

  private fun separatingAxis(v1s: Array<Vector2f>, v2s: Array<Vector2f>, aX: Float, aY: Float): Boolean {
    var minA = java.lang.Float.POSITIVE_INFINITY
    var maxA = java.lang.Float.NEGATIVE_INFINITY
    var minB = java.lang.Float.POSITIVE_INFINITY
    var maxB = java.lang.Float.NEGATIVE_INFINITY
    val maxLen = Math.max(v1s.size, v2s.size)
    /* Project both polygons on axis */
    for (k in 0 until maxLen) {
      if (k < v1s.size) {
        val v1 = v1s[k]
        val d = v1.x * aX + v1.y * aY
        if (d < minA) minA = d
        if (d > maxA) maxA = d
      }
      if (k < v2s.size) {
        val v2 = v2s[k]
        val d = v2.x * aX + v2.y * aY
        if (d < minB) minB = d
        if (d > maxB) maxB = d
      }
      /* Early-out if overlap found */
      if (minA <= maxB && minB <= maxA) {
        return false
      }
    }
    return true
  }

  /**
   * Test if the two polygons, given via their vertices, intersect.
   *
   * @param v1s
   * the vertices of the first polygon
   * @param v2s
   * the vertices of the second polygon
   * @return `true` if the polygons intersect; `false` otherwise
   */
  fun testPolygonPolygon(v1s: Array<Vector2f>, v2s: Array<Vector2f>): Boolean {
    /* Try to find a separating axis using the first polygon's edges */
    run {
      var i = 0
      var j = v1s.size - 1
      while (i < v1s.size) {
        val s = v1s[i]
        val t = v1s[j]
        if (separatingAxis(v1s, v2s, s.y - t.y, t.x - s.x))
          return false
        j = i
        i++
      }
    }
    /* Try to find a separating axis using the second polygon's edges */
    var i = 0
    var j = v2s.size - 1
    while (i < v2s.size) {
      val s = v2s[i]
      val t = v2s[j]
      if (separatingAxis(v1s, v2s, s.y - t.y, t.x - s.x))
        return false
      j = i
      i++
    }
    return true
  }

}
