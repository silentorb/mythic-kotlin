import org.junit.jupiter.api.Test
import silentorb.mythic.aura.generation.*

class AudioTest {

    @Test()
    fun canPlaySineWave() {
        val synthDefinition = SynthDefinition(
            name = "test",
            constants = listOf(440f, 0f, 0.2f),
            parameters = listOf(),
            parameterNames = listOf(),
            unitGenerators = listOf(
                UnitGenerator(
                    className = "SinOsc",
                    calculationRate = CalculationRate.audio,
                    inputs = listOf(
                        Input(-1, 0),
                        Input(-1, 1),
                        Input(-1, 2)
                    ),
                    outputs = listOf(
                        CalculationRate.audio
                    )
                )
            )
        )
        val buffer = serializeSynthDefinitions(listOf(synthDefinition))
        sendMessages(listOf(
//            Message(Commands.dumpOsc, listOf(3)),
            Message(Commands.receiveSynth, listOf(buffer))
        ))
    }
}
