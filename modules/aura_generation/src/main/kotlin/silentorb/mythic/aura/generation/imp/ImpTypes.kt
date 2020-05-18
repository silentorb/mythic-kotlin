package silentorb.mythic.aura.generation.imp

import silentorb.imp.core.PathKey

const val auraPath = "silentorb.mythic.aura.generation"
val monoSignalKey = PathKey(auraPath, "MonoSignal")
val monoSignalType = monoSignalKey.hashCode()
val audioOutputKey = PathKey(auraPath, "AudioOutput")
val audioOutputType = audioOutputKey.hashCode()
val frequencyKey = PathKey(auraPath, "Frequency")
val frequencyType = frequencyKey.hashCode()
val absoluteTimeKey = PathKey(auraPath, "Time")
val absoluteTimeType = absoluteTimeKey.hashCode()

typealias AbsoluteTime = Float
