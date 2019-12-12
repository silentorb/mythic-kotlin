package mythic.typography

import org.lwjgl.BufferUtils
import org.lwjgl.system.jni.JNINativeInterface.GetDirectBufferAddress
import kotlin.reflect.full.memberProperties

fun loadCharacters(face: Long, dimensions: IntegerVector2, fontInfo: FontLoadInfo): CharacterMap {
  val characters: MutableMap<Char, Glyph> = mutableMapOf()
  var verticalOffset = 0f
  for (value in CharRange('!', '~')) {
    val glyphInfo = FaceLoader.loadCharacterInfo(face, value, fontInfo.loadFlags, fontInfo.renderMode.ordinal)
    characters[value] = Glyph(glyphInfo,
        verticalOffset / dimensions.y,
        glyphInfo.sizeY.toFloat() / dimensions.y
    )
    verticalOffset += glyphInfo.sizeY + 2
  }

  return characters
}

fun loadFont(freetype: Long, info: FontLoadInfo): Font {
  val face = FaceLoader.loadFace(freetype, info.filename, info.pixelWidth, info.pixelHeight)
  val dimensions = FaceLoader.getTextureDimensions(face, info.loadFlags, info.renderMode.ordinal)
  val buffer = BufferUtils.createByteBuffer(dimensions.x * dimensions.y)
  val characters = loadCharacters(face, dimensions, info)
  val spaceWidth = characters['i']!!.info.sizeX
  FaceLoader.renderFaces(freetype, face, GetDirectBufferAddress(buffer), dimensions.x,
      info.loadFlags, info.renderMode.ordinal)
  FaceLoader.releaseFace(face)

  val texture = generateFontTexture(buffer, dimensions.x, dimensions.y)
  val font = Font(
      characters = characters,
      texture = texture,
      dimensions = dimensions,
      additionalKerning = info.additionalKerning,
      spaceWidth = spaceWidth
  )

  return font
}

/*
Freetype header definitions

#define FT_LOAD_DEFAULT                      0x0
#define FT_LOAD_NO_SCALE                     ( 1L << 0 )
#define FT_LOAD_NO_HINTING                   ( 1L << 1 )
#define FT_LOAD_RENDER                       ( 1L << 2 )
#define FT_LOAD_NO_BITMAP                    ( 1L << 3 )
#define FT_LOAD_VERTICAL_LAYOUT              ( 1L << 4 )
#define FT_LOAD_FORCE_AUTOHINT               ( 1L << 5 )
#define FT_LOAD_CROP_BITMAP                  ( 1L << 6 )
#define FT_LOAD_PEDANTIC                     ( 1L << 7 )
#define FT_LOAD_IGNORE_GLOBAL_ADVANCE_WIDTH  ( 1L << 9 )
#define FT_LOAD_NO_RECURSE                   ( 1L << 10 )
#define FT_LOAD_IGNORE_TRANSFORM             ( 1L << 11 )
#define FT_LOAD_MONOCHROME                   ( 1L << 12 )
#define FT_LOAD_LINEAR_DESIGN                ( 1L << 13 )
#define FT_LOAD_NO_AUTOHINT                  ( 1L << 15 )
  /* Bits 16-19 are used by `FT_LOAD_TARGET_' */
#define FT_LOAD_COLOR                        ( 1L << 20 )
#define FT_LOAD_COMPUTE_METRICS              ( 1L << 21 )
#define FT_LOAD_BITMAP_METRICS_ONLY          ( 1L << 22 )

#define FT_LOAD_TARGET_( x )   ( (FT_Int32)( (x) & 15 ) << 16 )

#define FT_LOAD_TARGET_NORMAL  FT_LOAD_TARGET_( FT_RENDER_MODE_NORMAL )
#define FT_LOAD_TARGET_LIGHT   FT_LOAD_TARGET_( FT_RENDER_MODE_LIGHT  )
#define FT_LOAD_TARGET_MONO    FT_LOAD_TARGET_( FT_RENDER_MODE_MONO   )
#define FT_LOAD_TARGET_LCD     FT_LOAD_TARGET_( FT_RENDER_MODE_LCD    )
#define FT_LOAD_TARGET_LCD_V   FT_LOAD_TARGET_( FT_RENDER_MODE_LCD_V  )
 */

fun FT_LOAD_TARGET(x: Int) = (x and 15) shl 16
val FT_LOAD_RENDER = 1 shl 2
val FT_LOAD_MONOCHROME = 1 shl 12

val FT_LOAD_TARGET_MONO = FT_LOAD_TARGET(RenderMode.FT_RENDER_MODE_MONO.ordinal)

enum class RenderMode {
  FT_RENDER_MODE_NORMAL,
  FT_RENDER_MODE_LIGHT,
  FT_RENDER_MODE_MONO,
  FT_RENDER_MODE_LCD,
  FT_RENDER_MODE_LCD_V,

  FT_RENDER_MODE_MAX
}

data class FontLoadInfo(
    val filename: String,
    val pixelWidth: Int = 0,
    val pixelHeight: Int,
    val additionalKerning: Float = 0f,
    val loadFlags: Int = FT_LOAD_RENDER,
    val renderMode: RenderMode = RenderMode.FT_RENDER_MODE_NORMAL,
    val monospace: Int? = null
)

fun loadFonts(files: List<FontLoadInfo>): List<Font> {
  val freetype = FaceLoader.initializeFreetype()
  try {
    return files.map { loadFont(freetype, it) }
  } finally {
    FaceLoader.releaseFreetype(freetype)
  }
}

data class RangedFontLoadInfo(
    val info: FontLoadInfo,
    val pixelHeights: List<Int>
)

typealias FontSet = Map<Int, Font>

fun loadFontSets(files: List<RangedFontLoadInfo>): List<Map<Int, Font>> {
  return files.map { (info, pixelHeights) ->
    val typesets = pixelHeights.map { pixelHeight ->
      info.copy(
          pixelHeight = pixelHeight
      )
    }
    pixelHeights.zip(loadFonts(typesets)) { pixelHeight, font ->
      Pair(pixelHeight, font)
    }
        .associate { it }
  }
}

fun extractFontSets(fonts: List<FontLoadInfo>, styles: List<IndexedTextStyle>): List<RangedFontLoadInfo> =
    styles.groupBy { it.font }
        .mapValues { it.value.map { it.size }.distinct() }
        .map {
          RangedFontLoadInfo(
              info = fonts[it.key],
              pixelHeights = it.value
          )
        }

fun enumerateTextStyles(styles: Any): List<IndexedTextStyle> {
  return styles.javaClass.kotlin.memberProperties.map { member ->
    member.get(styles) as IndexedTextStyle
  }
}

fun loadFontSets(fonts: List<FontLoadInfo>, styles: Any): List<Map<Int, Font>> =
    loadFontSets(extractFontSets(fonts, enumerateTextStyles(styles)))
