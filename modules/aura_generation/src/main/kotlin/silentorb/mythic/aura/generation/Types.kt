package silentorb.mythic.aura.generation

import silentorb.mythic.aura.generation.imp.AbsoluteTime

typealias AudioTime = Int
typealias AudioSample = Float
typealias SampleRate = Int
typealias SamplerAudioTime = Int
typealias SamplerSampleRate = Int
typealias Frequency = Float
typealias AudioSampler = (SamplerSampleRate, SamplerAudioTime) -> AudioSample
typealias FrequencySignalGenerator = (Frequency) -> AudioSampler

data class TimeRange(
    val start: AudioTime,
    val end: AudioTime
)

data class AudioOutput(
    val samplers: List<AudioSampler>,
    val start: AbsoluteTime,
    val end: AbsoluteTime
)

data class AudioConfig(
    val sampleRate: Int
)
