package silentorb.mythic.lookinglass.meshes.loading

import silentorb.mythic.spatial.serialization.loadSpatialJsonResource
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.lookinglass.Armature
import silentorb.mythic.lookinglass.Material
import silentorb.mythic.lookinglass.ModelMesh
import silentorb.mythic.lookinglass.meshes.VertexSchemas
import silentorb.mythic.lookinglass.toCamelCase
import silentorb.mythic.scenery.Light
import silentorb.mythic.scenery.LightType
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.newVector4

// considerFaceCulling exists to workaround how Blender (and probably other 3D content creation tools) default
// to double sided, while Mythic needs to default to not default sided.
// Instead of trying to remember to manually set 99% of the materials in blender to have backface culling enabled,
// the loader is ignoring glTF's doubleSided field unless the considerFaceCulling is added to the object which
// contains the material
fun loadMaterial(info: GltfInfo, materialIndex: Int, considerFaceCulling: Boolean): Material {
  val materialSource = info.materials?.getOrNull(materialIndex)
  return if (materialSource == null)
    Material(
        color = Vector4(1f),
        glow = 1f,
        shading = false,
    )
  else {
    val details = materialSource.pbrMetallicRoughness
    val color = details.baseColorFactor
    val glow = if (materialSource.emissiveFactor != null && materialSource.emissiveFactor.first() != 0f)
      materialSource.emissiveFactor.first()
    else
      0f

    val texture = if (details.baseColorTexture == null) {
      null
    } else {
      val gltfTexture = info.textures!![details.baseColorTexture.index]
      val gltfImage = info.images!![gltfTexture.source]
      toCamelCase(gltfImage.uri.substringBeforeLast(".").substringAfterLast("/").substringAfterLast("\\"))
    }

    val doubleSided = considerFaceCulling && materialSource.doubleSided

    Material(
        color = color,
        glow = glow,
        texture = texture,
        shading = true,
        doubleSided = doubleSided,
    )
  }
}

data class ModelImport(
    val meshes: List<ModelMesh>,
    val armatures: List<Armature>
)

fun gatherChildLights(info: GltfInfo, node: Node): List<Light> {
  if (info.extensions != null) {
    val k = 0
  }
  val lights = info.extensions?.KHR_lights_punctual?.lights
  return if (node.children == null || lights == null)
    listOf()
  else
    info.nodes.mapNotNull { childNode ->
      val lightIndex = childNode.extensions?.KHR_lights_punctual?.light
      if (lightIndex == null)
        null
      else {
        val light = lights[lightIndex]
        Light(
            type = LightType.values().first { it.name == light.type.name },
            color = newVector4(light.color, light.intensity / 100f),
            offset = childNode.translation ?: Vector3.zero,
            direction = null,
            range = light.range
        )
      }
    }
}

fun loadGltf(vertexSchemas: VertexSchemas, filename: String, resourcePath: String): ModelImport {
  val info = loadSpatialJsonResource<GltfInfo>(resourcePath + ".gltf")
  val directoryPath = resourcePath.split("/").dropLast(1).joinToString("/")
  val buffer = loadGltfByteBuffer(directoryPath, info)

  val originalSocketMap = if (info.skins != null)
    getSockets(info.nodes)
  else
    mapOf()

  val boneMap = if (info.skins != null)
    getBoneMap(info, originalSocketMap.values)
  else
    mapOf()

  val newSocketMap = originalSocketMap.mapValues { (_, index) ->
    boneMap[index]!!.index
  }

  val armatures = if (info.animations == null || info.animations.none())
    listOf()
  else
    listOf(loadArmature(buffer, info, filename, boneMap, newSocketMap)).mapNotNull { it }

  val meshes = loadMeshes(info, buffer, vertexSchemas, boneMap)

  return ModelImport(meshes = meshes, armatures = armatures)
}
