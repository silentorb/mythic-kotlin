import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import silentorb.imp.execution.executeToSingleValue
import silentorb.imp.parsing.general.handleRoot
import silentorb.imp.parsing.parser.parseText
import silentorb.imp.testing.errored
import silentorb.mythic.imaging.Bitmap

class TexturingTest {

  @Test
  fun canGenerateASeamlessNoiseColorBitmap() {
    val code = """
import silentorb.mythic.generation.texturing.*

let dimensions = Dimensions 512 512

let output = seamlessColoredNoise
    dimensions = dimensions
    scale = 0.2
    octaves = 10
    roughness = 0.5
    firstColor = (SolidColor 0.5 1.0 0.4)
    secondColor = (SolidColor 0.0 0.0 0.0)
    """.trimIndent()
    handleRoot(errored, parseText(context)(code)) { result ->
      val graph = result.graph
      val value = executeToSingleValue(library.implementation, graph)
      assertTrue(value is Bitmap)
      val bitmap = value as Bitmap
      assertEquals(3, bitmap.channels)
      assertEquals(512, bitmap.dimensions.x)
      assertEquals(512, bitmap.dimensions.y)
    }
  }
}
