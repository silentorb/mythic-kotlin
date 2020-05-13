package silentorb.mythic.aura.generation.imp

import silentorb.imp.core.PathKey

const val auraPath = "silentorb.mythic.aura.generation"
val monoSignalKey = PathKey(auraPath, "MonoSignal")
val audioOutputKey = PathKey(auraPath, "AudioOutput")
val frequencySignalGeneratorKey = PathKey(auraPath, "FrequencySignalGenerator")
val frequencyKey = PathKey(auraPath, "Frequency")
val absoluteTimeKey = PathKey(auraPath, "Time")

data class AbsoluteTime(
    val seconds: Int,
    val milliseconds: Int
)
