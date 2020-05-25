package silentorb.mythic.aura.generation.imp

import silentorb.imp.core.*
import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.TypeAlias
import silentorb.mythic.aura.generation.*
import kotlin.math.max
import kotlin.math.min

fun signalGenerators() = mapOf(
    "sineOsc" to oscillateInfinite(::sine)
)
    .map { signalGeneratorFunction(it.key, it.value) }
    .plus(
        CompleteFunction(
            path = PathKey(auraPath, "random"),
            signature = CompleteSignature(
                parameters = listOf(),
                output = monoSignalType
            ),
            implementation = { randomSampler }
        )
    )

fun auraFunctions() = signalGenerators() +
    listOf(
        CompleteFunction(
            path = PathKey(auraPath, "period"),
            signature = CompleteSignature(
                parameters = listOf(
                    CompleteParameter("signal", monoSignalType),
                    CompleteParameter("start", absoluteTimeType),
                    CompleteParameter("end", absoluteTimeType)
                ),
                output = audioOutputType
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
        ),
        CompleteFunction(
            path = PathKey(auraPath, "+"),
            signature = CompleteSignature(
                parameters = listOf(
                    CompleteParameter("first", audioOutputType),
                    CompleteParameter("second", audioOutputType)
                ),
                output = audioOutputType
            ),
            implementation = { arguments ->
              val first = arguments["first"] as AudioOutput
              val second = arguments["second"] as AudioOutput
              AudioOutput(
                  samplers = listOf { sampleRate ->
                    val a = renderPeriod(first.samplers.first(), first.start, first.end)(sampleRate)
                    val b = renderPeriod(second.samplers.first(), second.start, second.end)(sampleRate)
                    val lineBreak = 0
                    { position ->
                      a(position) + b(position)
                    }
                  },
                  start = min(first.start, second.start),
                  end = max(first.end, second.end)
              )
            }
        )
    )

fun auraAliases() =
    listOf(
        TypeAlias(
            path = frequencyType.hash,
            alias = floatType.hash,
            numericConstraint = newNumericConstraint(1f, 33000f)
        ),
        TypeAlias(
            path = absoluteTimeType.hash,
            alias = floatType.hash,
            numericConstraint = newNumericConstraint(0f, 1000f)
        )
    )
