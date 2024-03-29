package silentorb.mythic.editing.lookinglass

import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.lookinglass.Material
import silentorb.mythic.lookinglass.MeshElement
import silentorb.mythic.scenery.*
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector4

val collisionMaterial = Material(
    color = Vector4(0.8f, 0f, 0.8f),
    shading = false,
    drawMethod = DrawMethod.lineLoop,
)

fun shapeToMeshes(meshShapes: Map<String, Shape>, shape: Shape, transform: Matrix): List<MeshElement> {
  return when (shape) {
    is Box -> listOf(
        MeshElement(
            mesh = "cube",
            material = collisionMaterial,
            transform = transform,
        )
    )
    is Cylinder -> listOf(
        MeshElement(
            mesh = "cylinder",
            material = collisionMaterial,
            transform = transform.scale(shape.radius, shape.radius, shape.height / 2f),
        )
    )
    is Sphere -> listOf(
        MeshElement(
            mesh = "sphere",
            material = collisionMaterial,
            transform = transform.scale(shape.radius),
        )
    )
    is MeshShape ->
      listOf(
          MeshElement(
              mesh = meshShapes.filter { it.value == shape }.keys.first(),
              material = collisionMaterial,
              transform = transform,
          )
      )
    else -> listOf()
  }
}
