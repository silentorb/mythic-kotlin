package silentorb.mythic.lookinglass

import silentorb.mythic.glowing.GeneralMesh
import silentorb.mythic.sculpting.ImmutableEdge
import silentorb.mythic.sculpting.ImmutableFace
import silentorb.mythic.sculpting.ImmutableMesh
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.lookinglass.meshes.MeshInfo
import silentorb.mythic.lookinglass.meshes.Primitives
import silentorb.mythic.lookinglass.texturing.FaceTextureMap
import silentorb.mythic.scenery.Light
import silentorb.mythic.scenery.MeshName
import silentorb.mythic.scenery.Shape

data class MeshGroup(
    val material: Material,
    val faces: Collection<ImmutableFace>,
    val name: String = "Unnamed"
)

fun mapMaterialToMesh(material: Material, mesh: ImmutableMesh): MeshGroup {
  return MeshGroup(material, mesh.faces.values)
}

fun mapMaterialToManyMeshes(material: Material, meshes: List<ImmutableMesh>): MeshGroup {
  return MeshGroup(material, meshes.flatMap { it.faces.values })
}

data class Model(
    val mesh: ImmutableMesh,
    val groups: List<MeshGroup> = listOf(),
    val info: MeshInfo = MeshInfo(),
    val textureMap: FaceTextureMap? = null
) {
  val vertices: List<Vector3> get() = mesh.distinctVertices
  val edges: List<ImmutableEdge> get() = mesh.edges.values.toList()
}

data class VertexWeight(
    val index: Int,
    val strength: Float
)

typealias VertexWeights = Pair<VertexWeight, VertexWeight>

typealias WeightMap = Map<Vector3, VertexWeights>

data class AdvancedModel(
    val primitives: Primitives,
    val model: Model? = null,
    val armature: Armature? = null
)

typealias SamplePartitioning = List<List<Int>>

typealias LodRanges = List<Float>

data class SampledModel(
    val mesh: GeneralMesh,
    val partitioning: SamplePartitioning,
    val offsets: List<Int>,
    val levels: Int,
    val lodRanges: LodRanges
) {
  init {
    assert(lodRanges.size == levels)
  }
}

data class ModelMesh(
    val id: MeshName,
    val primitives: Primitives = listOf(),
    val sampledModel: SampledModel? = null,
    val lights: List<Light> = listOf(),
    val bounds: Shape? = null
)

typealias ModelMeshMap = Map<MeshName, ModelMesh>
