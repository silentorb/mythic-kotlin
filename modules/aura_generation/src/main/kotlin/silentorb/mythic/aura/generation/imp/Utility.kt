package silentorb.mythic.aura.generation.imp

import silentorb.imp.core.PathKey
import silentorb.imp.core.Signature
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.aura.generation.SignalGenerator

fun signalGeneratorFunction(name: String, method: SignalGenerator) =
    CompleteFunction(
        path = PathKey(auraPath, name),
        signature = Signature(
            parameters = listOf(),
            output = signalGeneratorKey
        ),
        implementation = { method }
    )
