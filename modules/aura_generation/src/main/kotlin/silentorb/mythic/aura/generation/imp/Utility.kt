package silentorb.mythic.aura.generation.imp

import silentorb.imp.core.Parameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.Signature
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.aura.generation.FrequencySignalGenerator

fun signalGeneratorFunction(name: String, generator: FrequencySignalGenerator) =
    CompleteFunction(
        path = PathKey(auraPath, name),
        signature = Signature(
            parameters = listOf(
                Parameter("frequency", frequencyType)
            ),
            output = monoSignalType
        ),
        implementation = { arguments ->
          val frequency = arguments["frequency"] as Float
          generator(frequency)
        }
    )
