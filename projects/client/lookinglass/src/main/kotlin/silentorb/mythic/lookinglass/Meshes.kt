package silentorb.mythic.lookinglass

import silentorb.mythic.lookinglass.meshes.*
import silentorb.mythic.lookinglass.meshes.loading.loadGltf
import silentorb.mythic.scenery.MeshName
import java.io.File

fun getMeshFilenames(): Array<File> {
  val modelRoot = getResourceUrl("models")
  val files = File(modelRoot!!.toURI()).listFiles()
  return files
}

fun importedMeshes(vertexSchemas: VertexSchemas) =
    getMeshFilenames()
        .map { it.name }
        .map { loadGltf(vertexSchemas, it, "models/" + it + "/" + it) }
//    }

fun createMeshes(vertexSchemas: VertexSchemas): Pair<Map<MeshName, ModelMesh>, List<Armature>> {
  val imports = importedMeshes(vertexSchemas)
  val meshes = mapOf(
      "line" to createLineMesh(vertexSchemas.flat),
      "billboard" to createBillboardMesh(vertexSchemas.billboard)
  )
      .mapValues { createModelElements(it.value) }
      .mapValues { ModelMesh(it.key, it.value) }
      .plus(imports.flatMap { it.meshes }.associate { Pair(it.id, it) })

  val armatures = imports.flatMap { it.armatures }
  return Pair(meshes, armatures)

}
