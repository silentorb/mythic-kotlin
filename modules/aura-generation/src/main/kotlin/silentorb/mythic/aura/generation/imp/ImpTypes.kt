package silentorb.mythic.aura.generation.imp

import silentorb.imp.core.PathKey
import silentorb.imp.core.newTypePair

const val auraPath = "silentorb.mythic.aura.generation"
val monoSignalType = newTypePair(PathKey(auraPath, "MonoSignal"))
val audioOutputType = newTypePair(PathKey(auraPath, "AudioOutput"))
val frequencyType = newTypePair(PathKey(auraPath, "Frequency"))
val absoluteTimeType = newTypePair(PathKey(auraPath, "Time"))

typealias AbsoluteTime = Float
