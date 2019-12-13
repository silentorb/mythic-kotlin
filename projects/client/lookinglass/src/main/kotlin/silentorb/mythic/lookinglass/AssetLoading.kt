package silentorb.mythic.lookinglass

import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

fun getResourceUrl(path: String): URL {
  val classloader = Thread.currentThread().contextClassLoader
  return classloader.getResource(path)
}

fun getResourceStream(name: String): InputStream {
  val classloader = Thread.currentThread().contextClassLoader
  return classloader.getResourceAsStream(name)
}

fun loadTextResource(name: String): String {
  val inputStream = getResourceStream(name)
  val s = Scanner(inputStream).useDelimiter("\\A")
  val result = if (s.hasNext()) s.next() else ""
  return result
}

fun scanResources(rootPath: String, extensions: List<String>): List<String> {
  val modelRoot = getResourceUrl(rootPath)
  val modelRootPath = Paths.get(modelRoot.toURI())
  val pathPrefix = modelRootPath.toString().length - rootPath.length
  val walk = Files.walk(modelRootPath, 10)
  val it = walk.iterator()
  val files = mutableListOf<String>()
  while (it.hasNext()) {
    val path = it.next()
    val stringPath = path.toString()
    if (extensions.any { stringPath.endsWith(it) }) {
      files.add(stringPath.substring(pathPrefix).replace("\\", "/"))
    }
  }

  return files.toList()
}

fun scanTextureResources(rootPath: String): List<String> =
    scanResources(rootPath, listOf(".jpg", ".png"))
