import org.junit.jupiter.api.Test
import silentorb.mythic.aura.generation.*

class AudioTest {

    @Test()
    fun canPlaySineWave() {
//        sendSound(listOf(Message(Commands.version, listOf())))
        val synthDefinition = SynthDefinition(
            name = "test",
            constants = listOf(440f, 0f, 0.2f),
            parameters = listOf(),
            parameterNames = listOf(),
            unitGenerators = listOf(
                UnitGenerator(
                    className = "SinOsc.ar",
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
        val synthMessage = Message(Commands.receiveSynth, listOf(Argument(FieldType.bytes, buffer)))
        sendSound(listOf(
//            Message(Commands.dumpOsc, listOf(Argument(FieldType.int, byteArrayOf(0x0, 0x0, 0x0, 0x3)))),
            synthMessage
        ))
    }
}
