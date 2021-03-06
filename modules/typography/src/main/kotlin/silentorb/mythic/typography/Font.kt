package silentorb.mythic.typography

import silentorb.mythic.glowing.Texture
import silentorb.mythic.glowing.TextureInitializer
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import silentorb.mythic.glowing.newTexture
import java.nio.ByteBuffer
import java.nio.FloatBuffer

typealias CharacterMap = Map<Char, Glyph>

val fontTextureInitializer: TextureInitializer = { width: Int, height: Int, buffer: FloatBuffer? ->
  glPixelStorei(GL_UNPACK_ALIGNMENT, 1) // Disable byte-alignment restriction
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

  glTexImage2D(
      GL_TEXTURE_2D, 0, GL_RED,
      width,
      height,
      0, GL_RED, GL_UNSIGNED_BYTE, buffer)
}

fun generateFontTexture(buffer: ByteBuffer, width: Int, height: Int): Texture {
  return newTexture(width, height, buffer.asFloatBuffer(), fontTextureInitializer)
}

data class Font(
    val characters: CharacterMap,
    val texture: Texture,
    val dimensions: IntegerVector2,
    val additionalKerning: Int = 0,
    val spaceWidth: Int
) {
  val height: Int = characters.values.maxBy { it.info.bearingY }!!.info.bearingY
  val v = characters.maxBy { it.value.info.sizeY - it.value.info.bearingY }!!

}
