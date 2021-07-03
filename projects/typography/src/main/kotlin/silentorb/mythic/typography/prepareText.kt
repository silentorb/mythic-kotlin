package silentorb.mythic.typography

import silentorb.mythic.glowing.Drawable
import silentorb.mythic.glowing.SimpleMesh
import silentorb.mythic.glowing.VertexSchema
import silentorb.mythic.spatial.Vector4
import org.lwjgl.BufferUtils
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.put

val unitConversion = 24f

data class TextStyle(
    val font: Font,
    val color: Vector4
)

data class TextConfiguration(
    val content: String,
    val style: TextStyle,
    val maxWidth: Int = 0,
    val lineHeight: Int = 2,
)

data class TextPackage(
    val mesh: Drawable
)

data class ArrangedCharacter(
    val glyph: Glyph,
    var x: Int,
    var y: Int
)

data class TypeArrangement(
    val characters: List<ArrangedCharacter>,
    val width: Int,
    val height: Int,
)

fun arrangeType(config: TextConfiguration): TypeArrangement? {
  val content = config.content
  val font = config.style.font
  val characters = font.characters
  var block_dimensionsX = 0
  val maxWidth = config.maxWidth

  val characterCount = content.count { it != ' ' }
  if (characterCount == 0)
    return null

  val arrangedCharacters = ArrayList<ArrangedCharacter>(characterCount)

  val letterSpace = font.additionalKerning
  val lineStep = font.height * config.lineHeight
  var x = 0
  var y = font.height
  var following_visible_character = false
  block_dimensionsX = 0
  var last_space_index = 0
  var lastSpaceX = 0

  for (i in 0 until content.length) {
    val c = content[i]
    if (c == ' ') {
      lastSpaceX = x
      last_space_index = arrangedCharacters.size
      x += font.spaceWidth
      following_visible_character = false
      continue
    }

    if (c == '\n') {
      if (x > block_dimensionsX) {
        block_dimensionsX = x
      }

      y += lineStep
      x = 0
      following_visible_character = false
      last_space_index = 0
      lastSpaceX = 0
      continue
    }

    if (following_visible_character) {
      x += letterSpace
    }

    val character = characters[c]!!

    arrangedCharacters.add(ArrangedCharacter(
        character,
        x + character.info.bearingX,
        y + character.info.sizeY - character.info.bearingY
    ))
    x += character.info.advanceX

    if (maxWidth != 0 && x > maxWidth && last_space_index > 0) {
      if (lastSpaceX > block_dimensionsX) {
        block_dimensionsX = lastSpaceX
      }

      for (c2 in last_space_index until arrangedCharacters.size) {
        val char = arrangedCharacters[c2]
        char.x -= lastSpaceX
        char.y += lineStep
      }

      y += lineStep
      x -= lastSpaceX
      last_space_index = 0
      lastSpaceX = 0
    }

    following_visible_character = true
  }

  return TypeArrangement(
      arrangedCharacters,
      x, y
  )
}

fun calculateTextDimensions(config: TextConfiguration): Vector2i {
  val arrangement = arrangeType(config)
  return if (arrangement != null)
    Vector2i(arrangement.width, arrangement.height)
  else
    Vector2i.zero
}

private val maxCharacters = 128
private val vertexSchemaFloatSize = 4
private val vertexBuffer = BufferUtils.createFloatBuffer(4 * maxCharacters * vertexSchemaFloatSize)
private val offsetBuffer = BufferUtils.createIntBuffer(maxCharacters)
private val countBuffer = BufferUtils.createIntBuffer(maxCharacters)

fun prepareText(config: TextConfiguration, vertexSchema: VertexSchema): TextPackage? {
  val arrangement = arrangeType(config)
  if (arrangement == null)
    return null

  val characters = arrangement.characters
  var index = 0

  vertexBuffer.limit(4 * characters.size * vertexSchemaFloatSize)
  offsetBuffer.limit(characters.size)
  countBuffer.limit(characters.size)

  for (arrangedCharacter in arrangement.characters) {
    val glyph = arrangedCharacter.glyph
    val x = arrangedCharacter.x.toFloat()
    val y = arrangedCharacter.y.toFloat()
    val width = glyph.info.sizeX.toFloat()
    val height = glyph.info.sizeY.toFloat()
    val textureWidth = width / config.style.font.dimensions.x.toFloat()

    vertexBuffer.put(x + width, y - height, textureWidth, glyph.offset)
    vertexBuffer.put(x + width, y, textureWidth, glyph.offset + glyph.height)
    vertexBuffer.put(x, y, 0f, glyph.offset + glyph.height)
    vertexBuffer.put(x, y - height, 0f, glyph.offset)

    offsetBuffer.put(index)
    index += 4
    countBuffer.put(4)
  }

  vertexBuffer.flip()
  offsetBuffer.flip()
  countBuffer.flip()

  return TextPackage(
      SimpleMesh(vertexSchema, vertexBuffer, offsetBuffer, countBuffer)
  )
}

data class IndexedTextStyle(
    val font: Int,
    val size: Int,
    val color: Vector4
)

fun resolveTextStyle(fonts: List<FontSet>, style: IndexedTextStyle) =
    TextStyle(
        font = fonts[style.font][style.size]!!,
        color = style.color
    )
