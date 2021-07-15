import org.junit.jupiter.api.Test
import silentorb.imp.execution.executeToSingleValue
import silentorb.imp.testing.errored

class AudioTest {

    @Test()
    fun canPlaySineWave() {
        val code = """
import silentorb.mythic.aura.generation.*

let osc1 = oscillator sine 8000.0

let output = period osc1 (Time 0 0) (Time 1 1)
    """.trimIndent()
//        handleRoot(errored, parseTextBranchingDeprecated(context)(code)) { result ->
//            val graph = result.graph
////            val value = executeToSingleValue(library.implementation, graph)
//        }
    }
}
