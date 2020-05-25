package silentorb.mythic.imaging.texturing

import silentorb.mythic.spatial.Vector3
import org.lwjgl.BufferUtils
import silentorb.imp.execution.newLibrary
import silentorb.mythic.imaging.texturing.filters.completeTexturingAliases
import silentorb.mythic.imaging.texturing.filters.completeTexturingFunctions
import java.nio.ByteBuffer

typealias OpaqueColor = silentorb.mythic.spatial.Vector3
typealias TransparentColor = silentorb.mythic.spatial.Vector4

typealias TextureAlgorithm<T> = (x: Float, y: Float) -> T

typealias ScalarTextureAlgorithm = TextureAlgorithm<Float>
typealias OpaqueTextureAlgorithm = TextureAlgorithm<OpaqueColor>
typealias TransparentTextureAlgorithm = TextureAlgorithm<TransparentColor>

fun flip(a: Boolean, b: Boolean) = if (b) a else !a

fun solidColor(color: OpaqueColor): OpaqueTextureAlgorithm = { x, y -> color }

fun <T> checkerOp(first: T, second: T) = { x: Float, y: Float ->
  if (flip(x < 0.5f, y < 0.5f))
    first
  else
    second
}

val coloredCheckerPattern = { first: OpaqueColor, second: OpaqueColor ->
  { x: Float, y: Float ->
    if (flip(x < 0.5f, y < 0.5f))
      first
    else
      second
  }
}

//fun simpleNoise(scales: List<Float>): ScalarTextureAlgorithm =
//    { x, y ->
//      scales.map { simpleNoise(it)(x, y) / scales.size }
//          .sum()
//    }

fun colorize(first: OpaqueColor, second: OpaqueColor, mod: Float): Vector3 =
    first * (1 - mod) + second * mod

fun colorize(a: OpaqueColor, b: OpaqueColor, algorithm: ScalarTextureAlgorithm): OpaqueTextureAlgorithm = { x, y ->
  colorize(a, b, algorithm(x, y))
}

fun createTextureBuffer(algorithm: OpaqueTextureAlgorithm, width: Int, height: Int = width): ByteBuffer {
  val buffer = BufferUtils.createByteBuffer(width * height * 3)
  for (y in 0 until width) {
    for (x in 0 until height) {
      val value = algorithm(x / width.toFloat(), y / height.toFloat())
      buffer.put((value.x * 255).toByte())
      buffer.put((value.y * 255).toByte())
      buffer.put((value.z * 255).toByte())
    }
  }
  buffer.flip()
  return buffer
}

fun texturingLibrary() =
  newLibrary(completeTexturingFunctions(), typeAliases = completeTexturingAliases())
