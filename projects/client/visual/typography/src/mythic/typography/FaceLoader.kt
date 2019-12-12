package mythic.typography

data class IntegerVector2(val x: Int, val y: Int)

data class GlyphInfo(
    val sizeX: Int,
    val sizeY: Int,
    val bearingX: Int,
    val bearingY: Int,
    val advanceX: Int
)

data class Glyph(
    val info: GlyphInfo,
    val offset: Float,
    val height: Float
)

class FaceLoader {

  companion object {
    init {
      System.loadLibrary("libjava_freetype")
    }

    @JvmStatic
    external fun getTextureDimensions(face: Long, loadFlags: Int, renderMode: Int): IntegerVector2

    @JvmStatic
    external fun loadFace(freetype: Long, filename: String, pixelWidth: Int, pixelHeight: Int): Long

    @JvmStatic
    external fun loadCharacterInfo(face: Long, value: Char, loadFlags: Int, renderMode: Int): GlyphInfo

    @JvmStatic
    external fun renderFaces(freetype: Long, face: Long, buffer: Long, width: Int, loadFlags: Int, renderMode: Int)

    @JvmStatic
    external fun initializeFreetype(): Long

    @JvmStatic
    external fun releaseFace(face: Long)

    @JvmStatic
    external fun releaseFreetype(freetype: Long)
  }

}