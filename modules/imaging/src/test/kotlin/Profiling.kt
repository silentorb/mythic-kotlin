import org.junit.Test
import silentorb.imp.execution.executeToSingleValue
import silentorb.imp.parsing.general.handleRoot
import silentorb.imp.parsing.parser.parseTextBranching
import silentorb.imp.testing.errored
import silentorb.mythic.debugging.globalProfiler
import silentorb.mythic.debugging.printProfiler

// These are intended to run indefinitely until manually stopped
class ProfilingTest {

  @Test
  fun seamlessNoise() {
    val code = """
import silentorb.mythic.generation.texturing.*
import silentorb.mythic.generation.drawing.*
import silentorb.mythic.math.*

let length = 512
let dimensions = Dimensions length length

let shapes = rectangle (RelativeDimensions 40.0 30.0)
    . translate (Vector2 -10.0 -10.0)

let background = coloredNoise
    dimensions = dimensions
    scale = 0.5
    octaves = 5
    roughness = 0.75
    firstColor = (RgbColor 1.0 0.5 0.4)
    secondColor = (RgbColor 0.0 0.0 0.0)

let foreground = coloredNoise
    dimensions = dimensions
    scale = 0.2
    octaves = 10
    roughness = 0.5
    firstColor = (RgbColor 0.4 1.0 0.4)
    secondColor = (RgbColor 0.0 0.0 0.0)

let shapeMask = rasterizeShapes (Bitmap 0.0 dimensions) (grayscaleFill 1.0 shapes)

let output = mask foreground background shapeMask
    . rasterizeShapes (rgbStroke (RgbColor 0.0 0.0 0.0) 5.0 shapes)

    """.trimIndent()
    handleRoot(errored, parseTextBranching(context)(code)) { result ->
      val graph = result.graph
      var i = 0
      while (++i < 20) {
        globalProfiler().wrapBlock("all") {
          executeToSingleValue(library.implementation, graph)
        }
      }
      printProfiler(globalProfiler())
    }
  }
}
