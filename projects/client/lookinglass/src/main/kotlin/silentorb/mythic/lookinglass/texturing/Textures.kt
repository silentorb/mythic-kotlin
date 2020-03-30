package silentorb.mythic.lookinglass.texturing

import silentorb.mythic.lookinglass.getResourceUrl
import silentorb.mythic.lookinglass.scanTextureResources
import silentorb.mythic.glowing.Texture
import silentorb.mythic.glowing.TextureAttributes
import silentorb.mythic.glowing.TextureFormat
import silentorb.mythic.platforming.ImageLoader
import silentorb.mythic.platforming.RawImage
import silentorb.mythic.lookinglass.toCamelCase
import java.nio.file.Paths
import kotlin.concurrent.thread

typealias ImageSource = () -> RawImage?

data class DeferredTexture(
    val name: String,
    val attributes: TextureAttributes,
    val load: ImageSource
)

data class LoadedTextureData(
    val name: String,
    val attributes: TextureAttributes,
    val image: RawImage
)

typealias DynamicTextureLibrary = MutableMap<String, Texture>

fun rawImageToTexture(image: RawImage, attributes: TextureAttributes): Texture {
  val modifiedAttributes = if (image.channels == 4)
    attributes.copy(
        format = TextureFormat.rgba
    )
  else
    attributes
  return Texture(image.width, image.height, modifiedAttributes, image.buffer)
}

fun loadTextureFromFile(loadImage: ImageLoader, path: String, attributes: TextureAttributes): Texture {
  val fullPath = getResourceUrl(path)!!.path.substring(1)
  val image = loadImage(fullPath)!!

  return rawImageToTexture(image, attributes)
}

fun deferImageFile(loadImage: ImageLoader, path: String, attributes: TextureAttributes): DeferredTexture {
  val fullPath = getResourceUrl(path)!!.path.substring(1)
  val shortName = getFileShortName(path)
  val (truncated, newAttributes) = if (shortName.contains('.')) {
    val tokens = shortName.split('.')
    Pair(tokens[0], attributes.copy(frames = tokens[1].toInt()))
  } else
    Pair(shortName, attributes)

  return DeferredTexture(
      name = truncated,
      attributes = newAttributes,
      load = { loadImage(fullPath) }
  )
}

fun getFileShortName(path: String): String =
    toCamelCase(Paths.get(path).fileName.toString().substringBeforeLast("."))

typealias TextureAttributeMapper = (String) -> TextureAttributes

fun gatherTextures(loadImage: ImageLoader, attributes: TextureAttributeMapper): List<DeferredTexture> =
    scanTextureResources("models")
        .plus(scanTextureResources("textures"))
        .map { deferImageFile(loadImage, it, attributes(it)) }

fun loadDeferredTextures(list: List<DeferredTexture>): List<LoadedTextureData> {
  return list.mapNotNull { deferred ->
    val image = deferred.load()
    if (image != null)
      LoadedTextureData(
          name = deferred.name,
          attributes = deferred.attributes,
          image = image
      )
    else
      null
  }
}

fun texturesToGpu(list: List<LoadedTextureData>): Map<String, Texture> {
  return list.map {
    Pair(it.name, Texture(it.image.width, it.image.height, it.attributes, it.image.buffer))
    Pair(it.name, rawImageToTexture(it.image, it.attributes))
  }.associate { it }
}

data class AsyncTextureLoader(
    var remainingTextures: List<DeferredTexture>,
    var batchSize: Int = 8,
    var loadedTextureBuffer: List<LoadedTextureData>? = null,
    var isLoading: Boolean = false
)

fun updateAsyncTextureLoading(loader: AsyncTextureLoader, destination: DynamicTextureLibrary) {
  if (loader.isLoading)
    return

  loader.isLoading = true
  val loadedTextures = loader.loadedTextureBuffer
  if (loadedTextures != null) {
    destination += texturesToGpu(loadedTextures)
    loader.loadedTextureBuffer = null
  }
  if (loader.remainingTextures.none())
    return

  val next = loader.remainingTextures.take(loader.batchSize)
  loader.remainingTextures = loader.remainingTextures.drop(loader.batchSize)

  thread(start = true) {
    loader.loadedTextureBuffer = loadDeferredTextures(next)
    loader.isLoading = false
  }
}
