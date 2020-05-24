package silentorb.mythic.lookinglass.meshes

import silentorb.mythic.glowing.GeneralMesh
import silentorb.mythic.glowing.PrimitiveType
import silentorb.mythic.glowing.newGeneralMesh
import silentorb.mythic.sculpting.ImmutableFace
import silentorb.mythic.sculpting.ImmutableMesh
import silentorb.mythic.spatial.*
import silentorb.mythic.lookinglass.Material
import silentorb.mythic.lookinglass.Model
import silentorb.mythic.lookinglass.WeightMap

object AttributeName {
  const val position = "position"
  const val normal = "normal"
  const val color = "color"
  const val uv = "uv"
  const val joints = "joints"
  const val weights = "weights"
  const val pointSize = "pointSize"
}

fun animatedVertexSerializer(weightMap: WeightMap): ImmutableVertexSerializer {
  return { vertex, face, vertices ->
    vertices.put(vertex)
    vertices.put(0f)
    vertices.put(0f)

    val weights = weightMap[vertex]!!
    vertices.put(weights.first.index.toFloat())
    vertices.put(weights.first.strength)
    vertices.put(weights.second.index.toFloat())
    vertices.put(weights.second.strength)
  }
}

fun simpleVertexSerializer(): ImmutableVertexSerializer {
  return { vertex, face, vertices ->
    vertices.put(vertex)
  }
}

fun createSimpleMesh(faces: List<ImmutableFace>, vertexSchema: VertexSchema) =
    convertMesh(faces, vertexSchema, simpleVertexSerializer())

fun createAnimatedMesh(faces: List<ImmutableFace>, vertexSchema: VertexSchema, weightMap: WeightMap) =
    convertMesh(faces, vertexSchema, animatedVertexSerializer(weightMap))

fun createSimpleMesh(mesh: ImmutableMesh, vertexSchema: VertexSchema) =
    convertMesh(mesh, vertexSchema, simpleVertexSerializer())

fun createLineMesh(vertexSchema: VertexSchema) =
    newGeneralMesh(vertexSchema, PrimitiveType.lineSegments, listOf(
        0f, 0f, 0f,
        1f, 0f, 0f
    ))

fun createBillboardMesh(vertexSchema: VertexSchema) =
    newGeneralMesh(vertexSchema, PrimitiveType.loops, listOf(
        0f, 1f, 0f, 0f, 1f,
        0f, 0f, 0f, 0f, 0f,
        1f, 0f, 0f, 1f, 0f,
        1f, 1f, 0f, 1f, 1f
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

data class TransientModelElement(
    val faces: List<ImmutableFace>,
    val material: Material
)

fun partitionModelMeshes(model: Model): List<TransientModelElement> {
  if (model.groups.size == 0)
    throw Error("Missing materials")

  return model.groups.map {
    TransientModelElement(it.faces.toList(), it.material)
  }
}

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
    listOf(Primitive(simpleMesh, Material(color, shading = false)))
