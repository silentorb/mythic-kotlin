package silentorb.mythic.resource_loading

import org.lwjgl.BufferUtils.createByteBuffer
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.file.*
import java.util.*
import kotlin.streams.toList

class Loading

fun sanitizeResourcePath(path: String): String =
    if (path.first() == '/')
      path.drop(1)
    else
      path

fun getResourceUrl(path: String): URL? =
    Loading::class.java.classLoader.getResource(sanitizeResourcePath(path))

fun getResourceStream(path: String): InputStream? =
    Loading::class.java.classLoader.getResourceAsStream(sanitizeResourcePath(path))

fun loadTextResource(name: String): String =
    getResourceStream(name).use { inputStream ->
      val s = Scanner(inputStream!!).useDelimiter("\\A")
      val result = if (s.hasNext()) s.next() else ""
      result
    }

fun bufferStream(input: InputStream): ByteBuffer {
  val array = ByteArray(input.available())
  input.read(array)
  val buffer: ByteBuffer = createByteBuffer(array.size)
  buffer.put(array)
  buffer.flip()
  return buffer
}

private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
  val newBuffer = createByteBuffer(newCapacity)
  buffer.flip()
  newBuffer.put(buffer)
  return newBuffer
}

fun getResourceRootUri(uri: URI): URI {
  return URI.create(uri.toString().split("!").first())
}

fun getResourceRootUri(path: String): URI =
    getResourceRootUri(getResourceUrl(path)!!.toURI())

val fileSystemCache = mutableMapOf<URI, FileSystem>()

fun getUrlPath(path: String): Path {
  val url = getResourceUrl(path)
  if (url == null)
    throw Error("Could not load $path")

  val uri = url.toURI()
  return if (uri.scheme == "jar") {
    val root = getResourceRootUri(path)
    println("Loading jar $root")
    val existing = fileSystemCache[root]
    val fileSystem = if (existing != null)
      existing
    else {
      val newFileSystem = FileSystems.newFileSystem(root, mutableMapOf<String, Any>())
      fileSystemCache[root] = newFileSystem
      newFileSystem
    }
    fileSystem.getPath(path)
  } else
    Paths.get(uri)
}

fun ioResourceToByteBuffer(resource: String, bufferSize: Int = 8 * 1024): ByteBuffer {
  val path = getUrlPath(resource)
  val buffer = if (Files.isReadable(path)) {
    Files.newByteChannel(path)
        .use { channel ->
          val buffer = createByteBuffer(channel.size().toInt() + 1)
          while (channel.read(buffer) != -1) {
          }
          buffer
        }
  } else {
    getResourceStream(resource)
        .use { source ->
          Channels.newChannel(source!!)
              .use { channel ->
                var buffer = createByteBuffer(bufferSize)
                while (true) {
                  val bytes: Int = channel.read(buffer)
                  if (bytes == -1) {
                    break
                  }
                  if (buffer.remaining() == 0) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2) // 50%
                  }
                }
                buffer
              }
        }
  }
  buffer.flip()
  return buffer
}

fun listFiles(path: Path): List<Path> =
    Files.list(path)
        .use { paths ->
          paths
              .toList()
              .filterIsInstance<Path>()
        }

fun listFilesRecursive(path: Path): List<Path> =
    if (Files.isDirectory(path))
      Files.list(path)
          .use { paths ->
            paths
                .toList()
                .filterIsInstance<Path>()
                .flatMap { listFilesRecursive(path.resolve(it)) }
          }
    else
      listOf(path)

fun listFilesAndFoldersRecursive(path: Path): List<Path> =
    if (Files.isDirectory(path))
      listOf(path) +
          Files.list(path)
              .use { paths ->
                paths
                    .toList()
                    .filterIsInstance<Path>()
                    .flatMap {child ->
                      listFilesAndFoldersRecursive(child)
                    }
              }
    else
      listOf(path)
