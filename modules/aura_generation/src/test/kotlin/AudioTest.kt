import org.junit.jupiter.api.Test
import silentorb.mythic.aura.generation.*

class AudioTest {

    @Test()
    fun canPlaySineWave() {
        val synthDefinition = SynthDefinition(
            name = "test",
            constants = listOf(440f, 0f, 0.2f, 0f),
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
                ),
                UnitGenerator(
                    className = "Out",
                    calculationRate = CalculationRate.audio,
                    inputs = listOf(
                        Input(-1, 3),
                        Input(0, 0)
                    ),
                    outputs = listOf(
                        CalculationRate.audio
                    )
                )
            )
        )
        val buffer = serializeSynthDefinitions(listOf(synthDefinition))
        val synthId = 1
        val instantiateSynth = newSynthMessage(synthDefinition.name, synthId, PlacementMethod.append, 0)

        sendMessages(
            listOf(
//                Message(Commands.status, listOf())
                Message(Commands.freeAll, listOf(0)),
//                Message(Commands.dumpOsc, listOf(2)),
                Message(Commands.receiveSynth, listOf(buffer)),
                newSynthMessage(synthDefinition.name, synthId, PlacementMethod.append, 0),
                Message(Commands.runNode, listOf(synthId, 1))
            )
        )
    }
}
