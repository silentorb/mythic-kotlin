package silentorb.mythic.lookinglass.texturing

import silentorb.mythic.sculpting.VertexNormalTexture
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.put
import silentorb.mythic.lookinglass.meshes.ImmutableVertexSerializer
import silentorb.mythic.scenery.TextureName

typealias VertexMap = Map<Vector3, VertexNormalTexture>
typealias FaceTextureMap = Map<Long, VertexMap>

data class TextureFace(
    val face: Long,
    val vertexMap: VertexMap,
    val texture: TextureName
)

fun texturedVertexSerializer(vertexInfo: FaceTextureMap): ImmutableVertexSerializer = { vertex, face, vertices ->
  val info = vertexInfo[face.id]!![vertex]!!
  vertices.put(info.normal)
  vertices.put(info.uv.x)
  vertices.put(info.uv.y)
}
