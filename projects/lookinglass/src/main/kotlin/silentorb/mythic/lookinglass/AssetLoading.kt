package silentorb.mythic.lookinglass

import silentorb.mythic.resource_loading.getResourceUrl
import silentorb.mythic.resource_loading.getUrlPath
import java.nio.file.*

fun scanResources(rootPath: String, extensions: List<String>): List<Path> {
  val modelRootUrl = getResourceUrl(rootPath)
  val rootWalkPath = getUrlPath(rootPath)
  val modelRootPath = Paths.get(modelRootUrl!!.toURI())
  println(modelRootUrl)
  println(modelRootPath)
  println(rootPath)
  val subPathNameCount = modelRootPath.nameCount - Paths.get(rootPath).nameCount
  val resourceSubPath = if (subPathNameCount > 0)
    modelRootPath.subpath(0, subPathNameCount)
  else
    null
//  val modelRootPath = Paths.get(modelRootUrl!!.toURI())
  val walk = Files.walk(rootWalkPath, 10)
  val it = walk.iterator()
  val files = mutableListOf<Path>()
  while (it.hasNext()) {
    val path = it.next()
    val stringPath = path.toString()
    if (extensions.any { stringPath.endsWith(it) }) {
      val newPath = if (resourceSubPath != null)
        resourceSubPath.relativize(path.subpath(0, path.nameCount))
      else
        path
      files.add(newPath)
//      files.add(stringPath.substring(pathPrefix).replace("\\", "/"))
//      files.add(path)
    }
  }

  return files.toList()
}

fun scanTextureResources(rootPath: String): List<Path> =
    scanResources(rootPath, listOf(".jpg", ".png"))
