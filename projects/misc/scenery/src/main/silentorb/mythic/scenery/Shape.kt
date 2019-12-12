package silentorb.mythic.scenery

import mythic.spatial.Matrix
import mythic.spatial.Vector3
import kotlin.math.max

interface Shape {
  val radius: Float
  val x: Float
  val y: Float
  val height: Float
}

data class CompositeShape(
    val shapes: List<Shape>,
    override val height: Float = shapes.map { it.height }.max() ?: 0f,
    override val radius: Float = shapes.map { it.radius }.max() ?: 0f,
    override val x: Float = shapes.map { it.x }.max() ?: 0f,
    override val y: Float = shapes.map { it.y }.max() ?: 0f
) : Shape

data class ShapeTransform(
    val transform: Matrix,
    val shape: Shape,
    override val height: Float = shape.height,
    override val radius: Float = shape.radius,
    override val x: Float = shape.x,
    override val y: Float = shape.y
) : Shape

data class Cylinder(
    override val radius: Float,
    override val height: Float,
    override val x: Float = radius * 2f,
    override val y: Float = radius * 2f
) : Shape

data class Capsule(
    override val radius: Float,
    override val height: Float,
    override val x: Float = radius * 2f,
    override val y: Float = radius * 2f
) : Shape

data class Sphere(
    override val radius: Float,
    override val height: Float = radius * 2f,
    override val x: Float = radius * 2f,
    override val y: Float = radius * 2f
) : Shape

data class Box(
    val halfExtents: Vector3,
    override val x: Float = halfExtents.x * 2f,
    override val y: Float = halfExtents.y * 2f,
    override val height: Float = halfExtents.z * 2f,
    override val radius: Float = max(max(halfExtents.x, halfExtents.y), halfExtents.z)
) : Shape

data class MeshShape(
    val triangles: List<Vector3>,
    override val radius: Float,
    override val height: Float = radius * 2f,
    override val x: Float = radius * 2f,
    override val y: Float = radius * 2f
) : Shape
