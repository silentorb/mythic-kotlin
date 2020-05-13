package silentorb.mythic.aura.generation.imp

import silentorb.imp.core.*
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.TypeAlias
import silentorb.mythic.aura.generation.*

fun signalGenerators() = mapOf(
    "sine" to ::sine
)
    .map { signalGeneratorFunction(it.key, it.value) }

fun auraFunctions() = signalGenerators() +
    listOf(
        CompleteFunction(
            path = absoluteTimeKey,
            signature = Signature(
                parameters = listOf(
                    Parameter("seconds", intKey),
                    Parameter("milliseconds", intKey)
                ),
                output = absoluteTimeKey
            ),
            implementation = { arguments ->
              AbsoluteTime(
                  seconds = arguments["seconds"] as Int,
                  milliseconds = arguments["milliseconds"] as Int
              )
            }
        ),
        CompleteFunction(
            path = PathKey(auraPath, "oscillator"),
            signature = Signature(
                parameters = listOf(
                    Parameter("generator", signalGeneratorKey),
                    Parameter("frequency", frequencyKey)
                ),
                output = monoSignalKey
            ),
            implementation = { arguments ->
              val generator = arguments["generator"] as SignalGenerator
              val frequency = arguments["frequency"] as Float
              oscillate(generator, frequency)
            }
        ),
        CompleteFunction(
            path = PathKey(auraPath, "period"),
            signature = Signature(
                parameters = listOf(
                    Parameter("signal", monoSignalKey),
                    Parameter("start", absoluteTimeKey),
                    Parameter("end", absoluteTimeKey)
                ),
                output = audioOutputKey
            ),
            implementation = { arguments ->
              val signal = arguments["signal"] as AudioSampler
              val start = arguments["start"] as AbsoluteTime
              val end = arguments["end"] as AbsoluteTime
              AudioOutput(
                  samplers = listOf(signal),
                  start = start,
                  end = end
              )
            }
        )
    )

fun auraAliases() =
    listOf(
        TypeAlias(
            path = frequencyKey,
            alias = floatKey,
            numericConstraint = newNumericConstraint(1f, 33000f)
        )
    )
