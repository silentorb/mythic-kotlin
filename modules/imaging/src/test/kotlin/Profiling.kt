import org.junit.Test
import silentorb.imp.execution.executeToSingleValue
import silentorb.imp.parsing.general.handleRoot
import silentorb.imp.parsing.parser.parseTextBranching
import silentorb.imp.testing.errored
import silentorb.mythic.debugging.globalProfiler
import silentorb.mythic.debugging.printProfiler
import java.lang.management.ManagementFactory

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

let background = noise
    dimensions = dimensions
    scale = 0.55
    octaves = 4
    roughness = 0.75
    . colorize (RgbColor 1.0 0.5 0.4) (RgbColor 0.0 0.0 0.0)

let foreground = noise
    dimensions = dimensions
    scale = 0.2
    octaves = 5
    roughness = 0.5
    . colorize (RgbColor 0.4 1.0 0.4) (RgbColor 0.0 0.0 0.0)

let shapeMask = rasterizeShapes (Bitmap 0.0 dimensions) (grayscaleFill 1.0 shapes)

let output = mask foreground background shapeMask
    . rasterizeShapes (rgbStroke (RgbColor 0.0 0.0 0.0) 5.0 shapes)
    """.trimIndent()
    val pid = ManagementFactory.getRuntimeMXBean().getName()
    println("pid: " + pid)
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
