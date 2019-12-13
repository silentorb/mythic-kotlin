package silentorb.mythic.lookinglass.meshes.loading

import silentorb.mythic.spatial.Matrix
import silentorb.mythic.scenery.*

fun loadBoundingShape(getTriangles: GetTriangles): (Map<String, Any>) -> Shape? = { source ->
  val type = source["type"] as String?
  val shape = when (type) {

    "composite" -> {
      @Suppress("UNCHECKED_CAST")
      val shapes = source.getValue("children") as List<Map<String, Any>>
      val shapes2 = shapes.mapNotNull(loadBoundingShape(getTriangles))
      CompositeShape(
          shapes = shapes2
      )
    }

    "cylinder" -> Cylinder(
        radius = parseFloat(source["radius"]),
        height = parseFloat(source["height"])
    )

    "mesh" -> MeshShape(
        triangles = getTriangles(),
        radius = parseFloat(source["radius"]),
        height = parseFloat(source["height"])
    )

    "box" -> {
      Box(
          halfExtents = parseVector3(source["dimensions"]) * 0.5f
      )
    }

    else -> null
  }
  val offset = if (source.containsKey("offset"))
    parseVector3(source["offset"])
  else
    null

  if (shape != null && offset != null)
    ShapeTransform(transform = Matrix().translate(offset), shape = shape)
  else
    shape
}

fun loadBoundingShapeFromNode(node: Node, getTriangles: GetTriangles): Shape? {
  val shapeProperty = node.extras?.get("bounds")
  return if (shapeProperty == null)
    null
  else {
    @Suppress("UNCHECKED_CAST")
    val source = shapeProperty as Map<String, Any>
    return loadBoundingShape(getTriangles)(source)
  }
}
