package silentorb.mythic.typography

import silentorb.mythic.glowing.Drawable
import silentorb.mythic.glowing.SimpleMesh
import silentorb.mythic.glowing.VertexSchema
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.put
import org.lwjgl.BufferUtils

val unitConversion = 24f
private val line_height = 2f

data class TextStyle(
    val font: Font,
    val color: Vector4
)

data class TextConfiguration(
    val content: String,
    val position: Vector2,
    val style: TextStyle,
    val maxWidth: Float = 0f,
)

data class TextPackage(
    val mesh: Drawable
)

data class ArrangedCharacter(
    val glyph: Glyph,
    var x: Float,
    var y: Float
)

data class TypeArrangement(
    val characters: List<ArrangedCharacter>,
    val width: Float,
    val height: Float
)

fun arrangeType(config: TextConfiguration): TypeArrangement? {
  val content = config.content
  val font = config.style.font
  val characters = font.characters
  var block_dimensionsX = 0f
  val maxWidth = config.maxWidth

  val characterCount = content.count { it != ' ' }
  if (characterCount == 0)
    return null

  val arrangedCharacters = ArrayList<ArrangedCharacter>(characterCount)

  val letter_space = font.additionalKerning
  val line_step = font.height * line_height
  var x = 0f
  var y = font.height
  var following_visible_character = false
  block_dimensionsX = 0f
  var last_space_index = 0
  var last_space_x = 0f

  for (i in 0 until content.length) {
    val c = content[i]
    if (c == ' ') {
      last_space_x = x
      last_space_index = arrangedCharacters.size
      x += font.spaceWidth
      following_visible_character = false
      continue
    }

    if (c == '\n') {
      if (x > block_dimensionsX) {
        block_dimensionsX = x
      }

      y += line_step
      x = 0f
      following_visible_character = false
      last_space_index = 0
      last_space_x = 0f
      continue
    }

    if (following_visible_character) {
      x += letter_space
    }

    val character = characters[c]!!

    arrangedCharacters.add(ArrangedCharacter(
        character,
        x + character.info.bearingX,
        y + character.info.sizeY - character.info.bearingY
    ))
    x += character.info.advanceX

    if (maxWidth != 0f && x > maxWidth && last_space_index > 0) {
      if (last_space_x > block_dimensionsX) {
        block_dimensionsX = last_space_x
      }

      for (c2 in last_space_index until arrangedCharacters.size) {
        val char = arrangedCharacters[c2]
        char.x -= last_space_x
        char.y += line_step
      }

      y += line_step
      x -= last_space_x
      last_space_index = 0
      last_space_x = 0f
    }

    following_visible_character = true
  }

  return TypeArrangement(
      arrangedCharacters,
      x, y
  )
}

fun calculateTextDimensions(config: TextConfiguration): Vector2 {
  val arrangement = arrangeType(config)
  return if (arrangement != null)
    Vector2(arrangement.width, arrangement.height)
  else
    Vector2()
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
    val x = arrangedCharacter.x
    val y = arrangedCharacter.y
    val width = glyph.info.sizeX
    val height = glyph.info.sizeY.toFloat()
    val texture_width = (width + 0).toFloat() / config.style.font.dimensions.x

    vertexBuffer.put(x + width, y - height, texture_width, glyph.offset)
    vertexBuffer.put(x + width, y, texture_width, glyph.offset + glyph.height)
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
