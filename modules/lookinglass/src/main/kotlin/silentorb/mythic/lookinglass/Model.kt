package silentorb.mythic.lookinglass

import silentorb.mythic.glowing.GeneralMesh
import silentorb.mythic.lookinglass.meshes.Primitives
import silentorb.mythic.scenery.Light
import silentorb.mythic.scenery.MeshName
import silentorb.mythic.scenery.Shape
import silentorb.mythic.spatial.Vector3

data class VertexWeight(
    val index: Int,
    val strength: Float
)

typealias VertexWeights = Pair<VertexWeight, VertexWeight>

typealias WeightMap = Map<Vector3, VertexWeights>

typealias SamplePartitioning = List<List<Int>>

typealias LodRanges = List<Float>

data class SampledModel(
    val mesh: GeneralMesh,
    val partitioning: SamplePartitioning,
    val baseSize: Int,
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

typealias IndexedPolygons = List<List<Int>>

data class IndexedGeometry(
    val vertices: FloatArray,
    val triangles: IndexedPolygons
)
