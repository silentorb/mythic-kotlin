import org.junit.Test
import silentorb.imp.execution.executeToSingleValue
import silentorb.imp.parsing.general.handleRoot
import silentorb.imp.parsing.parser.parseText
import silentorb.imp.testing.errored

// These are intended to run indefinitely until manually stopped
class ProfilingTest {

  @Test
  fun seamlessNoise() {
    val code = """
import silentorb.mythic.generation.texturing.*

let dimensions = Dimensions 512 512

let output = coloredNoise
    dimensions = dimensions
    scale = 0.2
    octaves = 1
    roughness = 0.5
    firstColor = (SolidColor 0.5 1.0 0.4)
    secondColor = (SolidColor 0.0 0.0 0.0)
    """.trimIndent()
    handleRoot(errored, parseText(context)(code)) { result ->
      val graph = result.graph
      while(true) {
        executeToSingleValue(library.implementation, graph)
      }
    }
  }
}
