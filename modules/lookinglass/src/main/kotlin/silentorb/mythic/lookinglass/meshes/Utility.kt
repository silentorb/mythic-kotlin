package silentorb.mythic.lookinglass.meshes

import silentorb.mythic.glowing.GeneralMesh
import silentorb.mythic.glowing.PrimitiveType
import silentorb.mythic.glowing.VertexSchema
import silentorb.mythic.glowing.newGeneralMesh
import silentorb.mythic.spatial.*
import silentorb.mythic.lookinglass.Material
import silentorb.mythic.lookinglass.WeightMap

object AttributeName {
  const val position = "position"
  const val normal = "normal"
  const val color = "color"
  const val uv = "uv"
  const val joints = "joints"
  const val weights = "weights"
  const val pointSize = "pointSize"
  const val level = "level"
  const val glow = "glow"
  const val specular = "specular"
}

fun createLineMesh(vertexSchema: VertexSchema) =
    newGeneralMesh(vertexSchema, PrimitiveType.lineSegments, listOf(
        0f, 0f, 0f,
        1f, 0f, 0f
    ))

fun createBillboardMesh(vertexSchema: VertexSchema) =
    newGeneralMesh(vertexSchema, PrimitiveType.loops, listOf(
        0f, 1f, 0f, 1f, 0f, 0f, 0f, 1f,
        0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f,
        1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f,
        1f, 1f, 0f, 1f, 0f, 0f, 1f, 1f
    ))

typealias Lod = Map<Float, GeneralMesh>

data class Primitive(
    val mesh: GeneralMesh,
    val material: Material,
    val transform: Matrix? = null,
    val parentBone: Int? = null,
    val name: String = "",
    val isAnimated: Boolean = mesh.vertexSchema.attributes.any { it.name == "weights" }
)

typealias Primitives = List<Primitive>

//typealias ModelMap = Map<MeshType, AdvancedModel>

//fun modelToMeshes(vertexSchemas: VertexSchemas, model: Model): Primitives {
//  val sections = partitionModelMeshes(model)
//  return sections.map {
//    Primitive(createSimpleMesh(it.faces, vertexSchemas.shaded), it.material)
//  }
//}

//fun modelToMeshes(vertexSchemas: VertexSchemas, model: Model, weightMap: WeightMap): Primitives {
//  val sections = partitionModelMeshes(model)
//  return sections.map {
//    Primitive(createAnimatedMesh(it.faces, vertexSchemas.animated, weightMap), it.material)
//  }
//}

fun createModelElements(simpleMesh: GeneralMesh, color: Vector4 = Vector4(1f)) =
    listOf(Primitive(simpleMesh, Material(shading = false, color = color)))
