package silentorb.mythic.aura.generation.imp

import silentorb.imp.core.*
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.aura.generation.FrequencySignalGenerator

fun signalGeneratorFunction(name: String, generator: FrequencySignalGenerator) =
    CompleteFunction(
        path = PathKey(auraPath, name),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("frequency", frequencyType)
            ),
            output = monoSignalType
        ),
        implementation = { arguments ->
          val frequency = arguments["frequency"] as Float
          generator(frequency)
        }
    )
