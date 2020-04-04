package silentorb.mythic.lookinglass.meshes.loading

import silentorb.mythic.spatial.Vector3
import org.lwjgl.BufferUtils
import silentorb.mythic.glowing.*
import silentorb.mythic.lookinglass.ModelMesh
import silentorb.mythic.lookinglass.meshes.AttributeName
import silentorb.mythic.lookinglass.meshes.VertexSchemas
import silentorb.mythic.lookinglass.toCamelCase
import silentorb.mythic.scenery.MeshName
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

typealias GetTriangles = () -> List<Vector3>

fun getIndexIterator(buffer: ByteBuffer, info: GltfInfo, primitive: Primitive): BufferIterator {
  val indexAccessor = info.accessors[primitive.indices]
  val bufferView = info.bufferViews[indexAccessor.bufferView]
  buffer.position(bufferView.byteOffset)
  return selectBufferIterator(indexAccessor.componentType)
}

fun loadIndices(buffer: ByteBuffer, info: GltfInfo, primitive: Primitive): IntBuffer {
  val indexAccessor = info.accessors[primitive.indices]
  val iterator = getIndexIterator(buffer, info, primitive)
  val triangleCount = indexAccessor.count / 3
  val indexCount = triangleCount * 3
  val indices = BufferUtils.createIntBuffer(indexCount)
  iterator(buffer, indexAccessor.count, { indices.put(it) })
  return indices
}

typealias VertexConverter = (ByteBuffer, FloatBuffer, VertexAttributeDetail, Int, Int) -> Unit

fun createVertexConverter(info: GltfInfo, transformBuffer: ByteBuffer, boneMap: BoneMap, meshIndex: Int): VertexConverter {
  val meshNode = info.nodes.first { it.mesh == meshIndex }
  val skin = if (meshNode.skin != null) info.skins?.get(meshNode.skin) else null

  return if (skin != null) {
    if (skin.joints.none())
      throw Error("Invalid mesh skin.")

    val names = boneMap.map { it.value.name }
    val jointMap = skin.joints.mapIndexed { index, jointIndex ->
      Pair(index, jointIndex)
    }.associate { it }

//    val transforms = getMatrices(transformBuffer, getOffset(info, skin.inverseBindMatrices), skin.joints.size)
    var lastJoints: List<Int> = listOf()
    var lastWeights: List<Float> = listOf()
    return { buffer, vertices, attribute, componentType, vertexIndex ->
      if (attribute.name == AttributeName.weights.name) {
        lastWeights = (0 until attribute.size).map {
          val value = buffer.float
          vertices.put(value)
          value
        }
      } else if (attribute.name == AttributeName.joints.name) {
        lastJoints = (0 until attribute.size).map {
          val value = getComponentIntValue(buffer, componentType)
          val jointIndex = jointMap[value]!!
          val converted = boneMap[jointIndex]!!.index
          vertices.put(converted.toFloat())
          value
        }
      } else {
        assert(componentType == ComponentType.Float.value)
        for (x in 0 until attribute.size) {
          val value = buffer.float
          vertices.put(value)
        }
      }
    }
  } else {
    { buffer, vertices, attribute, _, _ ->
      for (x in 0 until attribute.size) {
        val value = buffer.float
        vertices.put(value)
      }
    }
  }
}

enum class VertexPacking {
  interleaved,
  noninterleaved
}

fun loadVertices(buffer: ByteBuffer, info: GltfInfo, vertexSchema: VertexSchema, primitive: Primitive,
                 converter: VertexConverter): Pair<FloatBuffer, VertexPacking> {
  val vertexAccessor = info.accessors[primitive.attributes[AttributeType.POSITION]!!]
  val vertexCount = vertexAccessor.count
  val vertices = BufferUtils.createFloatBuffer(vertexSchema.floatSize * vertexCount)

  val attributes = vertexSchema.attributes.map { attribute ->
    val mappedAttribute = attributeMap2[attribute.name]
    if (mappedAttribute == null)
      throw Error("Missing attribute map for " + attribute.name)

    val attributeAccessorIndex = primitive.attributes[mappedAttribute]
    if (attributeAccessorIndex != null) {
      val attributeAccessor = info.accessors[attributeAccessorIndex]
      val bufferView = info.bufferViews[attributeAccessor.bufferView]
      Triple(attributeAccessor, bufferView, attribute)
    } else
      Triple(null, null, attribute)
  }

  for (i in 0 until vertexCount) {
    for ((attributeAccessor, bufferView, attribute) in attributes) {
      if (attributeAccessor != null && bufferView != null) {
        val stride = if (bufferView.byteStride != 0)
          bufferView.byteStride
        else
          attribute.size * getComponentByteSize(attributeAccessor.componentType)

        buffer.position(bufferView.byteOffset + attributeAccessor.byteOffset + i * stride)
        converter(buffer, vertices, attribute, attributeAccessor.componentType, i)
      } else {
        for (x in 0 until attribute.size) {
          vertices.put(0f)
        }
      }
    }
  }
  val packing = if (attributes.first().second?.byteStride == 0)
    VertexPacking.noninterleaved
  else
    VertexPacking.interleaved

  return Pair(vertices, packing)
}

fun loadPositionVertices(buffer: ByteBuffer, info: GltfInfo, primitive: Primitive): List<Vector3> {
  val vertexAccessor = info.accessors[primitive.attributes.getValue(AttributeType.POSITION)]
  val vertexCount = vertexAccessor.count
  val mappedAttribute = attributeMap2.getValue(AttributeName.position.name)
  val attributeAccessorIndex = primitive.attributes.getValue(mappedAttribute)
  val attributeAccessor = info.accessors[attributeAccessorIndex]
  val bufferView = info.bufferViews[attributeAccessor.bufferView]
  val stride = if (bufferView.byteStride != 0)
    bufferView.byteStride
  else
    3 * 4

  return (0 until vertexCount).map { i ->
    buffer.position(bufferView.byteOffset + attributeAccessor.byteOffset + i * stride)
    getVector3(buffer)
  }
}

fun getMeshName(info: GltfInfo, nodeIndex: Int): MeshName? {
  val parent = info.nodes.firstOrNull { it.children != null && it.children.contains(nodeIndex) }
  val node = info.nodes[nodeIndex]
  val name = if (parent != null && parent.name != "rig")
    parent.name
  else
    node.name

  return toCamelCase(name)
}

fun getTrianglesFromPrimitive(buffer: ByteBuffer, info: GltfInfo, primitive: Primitive): GetTriangles = {
  val indexAccessor = info.accessors[primitive.indices]
  val vertices = loadPositionVertices(buffer, info, primitive)
  val iterator = getIndexIterator(buffer, info, primitive)
  val result = mutableListOf<Vector3>()
  iterator(buffer, indexAccessor.count) { result.add(vertices[it]) }
  result.toList()
}

fun getVertexSchema(vertexSchemas: VertexSchemas, attributes: Map<AttributeType, Int>): VertexSchema =
    if (attributes.containsKey(AttributeType.JOINTS_0))
      vertexSchemas.animated
    else if (attributes.containsKey(AttributeType.TEXCOORD_0))
      vertexSchemas.textured
    else
      vertexSchemas.imported

fun loadPrimitiveMesh(buffer: ByteBuffer, info: GltfInfo, vertexSchema: VertexSchema, primitive: Primitive,
                      converter: VertexConverter): GeneralMesh {
  val (vertices, packing) = loadVertices(buffer, info, vertexSchema, primitive, converter)
  val indices = loadIndices(buffer, info, primitive)
  vertices.position(0)
  indices.position(0)

  return GeneralMesh(
      vertexSchema = vertexSchema,
      vertexBuffer = newVertexBuffer(vertexSchema, packing == VertexPacking.interleaved).load(vertices),
      indices = indices,
      primitiveType = PrimitiveType.loops
  )
}

fun loadMeshes(info: GltfInfo, buffer: ByteBuffer, vertexSchemas: VertexSchemas, boneMap: BoneMap): List<ModelMesh> {
  return info.meshes
      .mapIndexedNotNull { meshIndex, mesh ->
        val nodeIndex = info.nodes.indexOfFirst { it.mesh == meshIndex }
        val id = getMeshName(info, nodeIndex)
        if (id == null)
          null
        else {
          val name2 = mesh.name.replace(".001", "")
          val parentBone = getParentBone(info, nodeIndex, boneMap)
          val primitives = mesh.primitives.map { primitiveSource ->
            val material = loadMaterial(info, primitiveSource.material)
            val converter = createVertexConverter(info, buffer, boneMap, meshIndex)
            val vertexSchema = getVertexSchema(vertexSchemas, primitiveSource.attributes)
            silentorb.mythic.lookinglass.meshes.Primitive(
                mesh = loadPrimitiveMesh(buffer, info, vertexSchema, primitiveSource, converter),
                transform = null,
                material = material,
                name = name2,
                parentBone = parentBone
            )
          }
          val node = info.nodes[nodeIndex]
          val getTriangles = getTrianglesFromPrimitive(buffer, info, mesh.primitives.first())

          ModelMesh(
              id = id,
              primitives = primitives,
              lights = gatherChildLights(info, node),
              bounds = loadBoundingShapeFromNode(node, getTriangles)
          )
        }
      }
}
