package silentorb.mythic.aura.generation

typealias AudioTime = Int
typealias AudioSample = Float
typealias SampleRate = Int
typealias SamplerAudioTime = Double
typealias SamplerSampleRate = Int
typealias AudioSampler = (SamplerSampleRate, SamplerAudioTime) -> AudioSample
typealias SignalGenerator = (Float) -> Float

data class TimeRange(
    val start: AudioTime,
    val end: AudioTime
)

data class AudioOutput(
    val samplers: List<AudioSampler>,
    val start: AudioTime,
    val end: AudioTime
)

data class AudioConfig(
    val sampleRate: Int
)
