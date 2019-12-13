package silentorb.mythic.lookinglass.meshes

import silentorb.mythic.glowing.SimpleMesh
import silentorb.mythic.glowing.VertexSchema
import silentorb.mythic.sculpting.ImmutableMesh
import silentorb.mythic.sculpting.ImmutableFace
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.put
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer

typealias ImmutableVertexSerializer = (vertex: Vector3, face: ImmutableFace, vertices: FloatBuffer) -> Unit

fun temporaryVertexSerializer(color: Vector4): ImmutableVertexSerializer {
  return { vertex, face, vertices ->
    vertices.put(vertex)

    // Temporary color code
    vertices.put(color)
  }
}

fun convertMesh(faces: Collection<ImmutableFace>, vertexSchema: VertexSchema,
                vertexSerializer: ImmutableVertexSerializer): SimpleMesh {
  val vertex_count = faces.flatMap { it.vertices }.size
  val vertices = BufferUtils.createFloatBuffer(vertex_count * vertexSchema.floatSize)
  val offsets = BufferUtils.createIntBuffer(faces.size)
  val counts = BufferUtils.createIntBuffer(faces.size)
  var offset = 0

  for (polygon in faces) {
    polygon.vertices.forEach { v ->
      vertices.put(v)
      vertexSerializer(v, polygon, vertices)
    }

    val face_vertex_count = polygon.vertices.size
    offsets.put(offset)
    counts.put(face_vertex_count)
    offset += face_vertex_count
  }

  vertices.flip()
  offsets.flip()
  counts.flip()
  return SimpleMesh(vertexSchema, vertices, offsets, counts)
}

fun convertMesh(mesh: ImmutableMesh, vertexSchema: VertexSchema, vertexSerializer: ImmutableVertexSerializer) =
    convertMesh(mesh.faces.values, vertexSchema, vertexSerializer)
