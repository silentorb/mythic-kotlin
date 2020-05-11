package silentorb.mythic.aura.generation

import java.nio.ByteBuffer

fun renderAudio(config: AudioConfig, output: AudioOutput): ByteArray {
  val channelCount = output.samplers.size
  val sampleRate = config.sampleRate
  assert(channelCount == 1 || channelCount == 2)
  val timeLength = output.end - output.start
  val bufferLength = timeLength * config.sampleRate * channelCount
  val result = ByteArray(bufferLength)
  val buffer = ByteBuffer.wrap(result)
  for (step in (output.start until output.end)) {
    for (sampler in output.samplers) {
      buffer.putFloat(sampler(sampleRate, step.toDouble()))
    }
  }
  return result
}
