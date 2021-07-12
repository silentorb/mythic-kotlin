package silentorb.mythic.lookinglass

import silentorb.mythic.glowing.Texture
import silentorb.mythic.glowing.TextureAttributes
import silentorb.mythic.glowing.TextureFormat
import silentorb.mythic.glowing.newTexture
import silentorb.mythic.platforming.ImageLoader
import silentorb.mythic.platforming.RawImage
import silentorb.mythic.resource_loading.LoadingState
import silentorb.mythic.resource_loading.updateAsyncLoading
import java.nio.file.Path
import java.nio.file.Paths

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
  val modifiedAttributes = if (image.info.channels == 4)
    attributes.copy(
        format = TextureFormat.rgba
    )
  else
    attributes
  return newTexture(image.info.width, image.info.height, modifiedAttributes, image.buffer)
}

fun loadTextureFromFile(loadImage: ImageLoader, path: Path, attributes: TextureAttributes): Texture {
  val image = loadImage(path.toString())
  return rawImageToTexture(image!!, attributes)
}

fun deferImageFile(loadImage: ImageLoader, path: Path, attributes: TextureAttributes): DeferredTexture {
  val shortName = getFileShortName(path)
  val (truncated, newAttributes) = if (shortName.contains('.')) {
    val tokens = shortName.split('.')
    Pair(tokens[0], attributes.copy(frames = tokens[1].toInt()))
  } else
    Pair(shortName, attributes)

  return DeferredTexture(
      name = truncated,
      attributes = newAttributes,
      load = { loadImage(path.toString()) }
  )
}

fun getFileShortName(path: String): String =
    toCamelCase(Paths.get(path).fileName.toString().substringBeforeLast("."))

fun getFileShortName(path: Path): String =
    toCamelCase(path.fileName.toString().substringBeforeLast("."))

typealias TextureAttributeMapper = (Path) -> TextureAttributes

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
//    Pair(it.name, Texture(it.image.width, it.image.height, it.attributes, it.image.buffer))
    Pair(it.name, rawImageToTexture(it.image, it.attributes))
  }.associate { it }
}

typealias TextureLoadingState = LoadingState<DeferredTexture, LoadedTextureData>

val updateAsyncTextureLoading = updateAsyncLoading(::loadDeferredTextures, ::texturesToGpu)
